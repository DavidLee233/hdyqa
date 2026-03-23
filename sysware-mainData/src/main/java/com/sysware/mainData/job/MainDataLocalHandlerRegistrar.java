package com.sysware.mainData.job;

import cn.hutool.core.util.StrUtil;
import com.sysware.mainData.service.IHdlMainDataBackupRecordService;
import com.sysware.mainData.service.IMainDataScheduleSwitchService;
import com.sysware.mainData.service.IRemoteDataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @project npic
 * @description MainDataLocalHandlerRegistrar任务处理类，负责在系统启动后注册主数据定时同步与定时备份处理器，并按Cron表达式执行本地调度。
 * @author DavidLee233
 * @date 2026/3/21
 */
@Component
@RequiredArgsConstructor
public class MainDataLocalHandlerRegistrar implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MainDataLocalHandlerRegistrar.class);
    private static final String TRIGGER_MODE_HANDLER = "handler";
    private static final String SYNC_HANDLER_NAME = "mainDataSyncHandler";
    private static final String BACKUP_HANDLER_NAME = "mainDataWeeklyBackupHandler";
    private static final String TYPE_ORG_DEPT = "1";
    private static final String TYPE_PERSON_BASIC = "2";
    private static final String TYPE_PERSON_JOB = "3";

    private final IRemoteDataService remoteDataService;
    private final IHdlMainDataBackupRecordService backupRecordService;
    private final IMainDataScheduleSwitchService mainDataScheduleSwitchService;
    private final ThreadPoolTaskScheduler scheduler;

    @Value("${main-data.handler.enabled:true}")
    private boolean enabled;

    @Value("${main-data.handler.zone-id:Asia/Shanghai}")
    private String zoneId;

    @Value("${main-data.handler.sync-cron:0 0 1 * * ?}")
    private String syncCron;

    @Value("${main-data.handler.backup-cron:0 30 2 ? * SUN}")
    private String backupCron;

    private ScheduledFuture<?> syncFuture;
    private ScheduledFuture<?> backupFuture;

    /**
     * @description 应用启动完成后注册主数据本地处理器，并根据配置的Cron表达式启动定时同步与定时备份任务。
     * @params args 应用启动参数对象。
     *
     * @return void 无返回结果，方法执行后会将本地处理器注册到任务调度器中。
     * @author DavidLee233
     * @date 2026/3/21
     */
    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            logger.info("主数据本地处理器已禁用，不再注册定时同步与定时备份任务");
            return;
        }
        ZoneId zone = parseZoneId(zoneId);
        this.syncFuture = scheduleWithCron(SYNC_HANDLER_NAME, syncCron, zone, this::runSyncHandler);
        this.backupFuture = scheduleWithCron(BACKUP_HANDLER_NAME, backupCron, zone, this::runBackupHandler);
    }

    /**
     * @description 应用停止前取消已注册的本地处理器任务，避免应用关闭后仍保留无效调度引用。
     * @params 无
     *
     * @return void 无返回结果，方法执行后会释放同步与备份任务的调度句柄。
     * @author DavidLee233
     * @date 2026/3/21
     */
    @PreDestroy
    public void destroy() {
        cancelFuture(syncFuture);
        cancelFuture(backupFuture);
    }

    /**
     * @description 根据Cron表达式注册本地处理器任务，并返回调度句柄供系统后续取消或销毁。
     * @params handlerName 本地处理器名称。
     * @params cron 定时任务Cron表达式。
     * @params zone 定时任务执行时区对象。
     * @params task 需要由调度器周期触发执行的任务逻辑。
     *
     * @return ScheduledFuture<?> 已注册定时任务的调度句柄，用于任务销毁与生命周期管理。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private ScheduledFuture<?> scheduleWithCron(String handlerName, String cron, ZoneId zone, Runnable task) {
        if (StrUtil.isBlank(cron)) {
            logger.warn("定时任务Cron表达式为空，已跳过本地处理器注册，处理器={}", handlerName);
            return null;
        }
        try {
            CronExpression.parse(cron);
            ScheduledFuture<?> future = scheduler.schedule(task, new CronTrigger(cron, zone));
            logger.info("本地处理器注册成功：{}，Cron={}，时区={}", handlerName, cron, zone);
            return future;
        } catch (Exception e) {
            logger.error("本地处理器注册失败，处理器={}，Cron={}", handlerName, cron, e);
            return null;
        }
    }

    /**
     * @description 按既定顺序触发三类主数据的定时同步任务，并根据页面开关状态决定是否跳过对应类型的同步。
     * @params 无
     *
     * @return void 无返回结果，方法执行后会将本次同步摘要写入本地处理器日志。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private void runSyncHandler() {
        List<String> details = new ArrayList<>();
        try {
            boolean success = true;

            if (mainDataScheduleSwitchService.isSyncEnabled(TYPE_ORG_DEPT)) {
                Map<String, Object> deptResult = remoteDataService.forceSyncDepartments("auto", TRIGGER_MODE_HANDLER);
                appendSyncSummary(details, "组织部门", deptResult);
                success = success && isSuccess(deptResult);
            } else {
                details.add("组织部门：定时同步已关闭，已跳过");
            }

            if (mainDataScheduleSwitchService.isSyncEnabled(TYPE_PERSON_BASIC)) {
                Map<String, Object> basicResult = remoteDataService.forceSyncPersonBasicInfos("auto", TRIGGER_MODE_HANDLER);
                appendSyncSummary(details, "员工基本信息", basicResult);
                success = success && isSuccess(basicResult);
            } else {
                details.add("员工基本信息：定时同步已关闭，已跳过");
            }

            if (mainDataScheduleSwitchService.isSyncEnabled(TYPE_PERSON_JOB)) {
                Map<String, Object> jobResult = remoteDataService.forceSyncPersonJobInfos("auto", TRIGGER_MODE_HANDLER);
                appendSyncSummary(details, "员工工作信息", jobResult);
                success = success && isSuccess(jobResult);
            } else {
                details.add("员工工作信息：定时同步已关闭，已跳过");
            }

            if (success) {
                logger.info("[{}] 执行完成：{}", SYNC_HANDLER_NAME, String.join(" | ", details));
            } else {
                logger.warn("[{}] 执行失败：{}", SYNC_HANDLER_NAME, String.join(" | ", details));
            }
        } catch (Exception e) {
            logger.error("[{}] 执行异常", SYNC_HANDLER_NAME, e);
        }
    }

    /**
     * @description 触发主数据定时备份任务，并根据三类页面的备份开关筛选本次需要纳入备份的业务表。
     * @params 无
     *
     * @return void 无返回结果，方法执行后会将本次备份摘要写入本地处理器日志。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private void runBackupHandler() {
        try {
            Map<String, Object> result = backupRecordService.executeBackup(
                TRIGGER_MODE_HANDLER,
                BACKUP_HANDLER_NAME,
                mainDataScheduleSwitchService.listEnabledBackupTypes()
            );
            if (isSuccess(result)) {
                logger.info("[{}] 执行完成：{}", BACKUP_HANDLER_NAME, buildBackupSummary(result));
            } else {
                logger.warn("[{}] 执行失败：{}", BACKUP_HANDLER_NAME, buildBackupSummary(result));
            }
        } catch (Exception e) {
            logger.error("[{}] 执行异常", BACKUP_HANDLER_NAME, e);
        }
    }

    /**
     * @description 解析时区配置并在配置非法时回退为上海时区，确保处理器调度时间可正常计算。
     * @params zoneId 时区配置编码。
     *
     * @return ZoneId 时区对象，用于CronTrigger构造与任务调度时间计算。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private ZoneId parseZoneId(String zoneId) {
        try {
            return ZoneId.of(zoneId);
        } catch (Exception e) {
            logger.warn("非法时区配置={}，已回退为默认时区Asia/Shanghai", zoneId);
            return ZoneId.of("Asia/Shanghai");
        }
    }

    /**
     * @description 将单类主数据的同步执行结果整理为日志摘要条目，便于处理器输出统一的批次统计信息。
     * @params details 同步处理明细列表，用于收集各数据类型的摘要信息。
     * @params label 当前数据类型中文标签。
     * @params result 单类主数据同步返回结果对象。
     *
     * @return void 无返回结果，方法执行后会将当前数据类型的同步摘要追加到明细列表中。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private void appendSyncSummary(List<String> details, String label, Map<String, Object> result) {
        if (result == null) {
            details.add(label + "：结果为空");
            return;
        }
        String syncMode = result.get("syncMode") == null ? "-" : String.valueOf(result.get("syncMode"));
        String fetched = result.get("fetched") == null ? "0" : String.valueOf(result.get("fetched"));
        String inserted = result.get("inserted") == null ? "0" : String.valueOf(result.get("inserted"));
        String updated = result.get("updated") == null ? "0" : String.valueOf(result.get("updated"));
        String invalidated = result.get("invalidated") == null ? "0" : String.valueOf(result.get("invalidated"));
        String failed = result.get("failed") == null ? "0" : String.valueOf(result.get("failed"));
        String status = isSuccess(result) ? "成功" : "失败";
        details.add(label + "：" + status + "，模式=" + syncMode + "，拉取=" + fetched
            + "，新增=" + inserted + "，更新=" + updated
            + "，失效=" + invalidated + "，失败=" + failed);
    }

    /**
     * @description 构建主数据备份处理器的日志摘要文本，便于快速确认文件名、表数、行数及跳过说明。
     * @params result 备份执行结果对象。
     *
     * @return String 备份处理器日志摘要，用于本地处理器日志输出。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private String buildBackupSummary(Map<String, Object> result) {
        if (result == null) {
            return "结果为空";
        }
        String status = isSuccess(result) ? "成功" : "失败";
        String fileName = result.get("fileName") == null ? "-" : String.valueOf(result.get("fileName"));
        String tableCount = result.get("tableCount") == null ? "0" : String.valueOf(result.get("tableCount"));
        String rowCount = result.get("rowCount") == null ? "0" : String.valueOf(result.get("rowCount"));
        String message = result.get("message") == null ? "" : String.valueOf(result.get("message"));
        return "状态=" + status + "，文件=" + fileName + "，表数=" + tableCount
            + "，行数=" + rowCount + (message.isEmpty() ? "" : "，提示=" + message);
    }

    /**
     * @description 统一判定返回结果中的success字段是否表示执行成功，兼容布尔值与字符串两种格式。
     * @params result 任务执行结果对象。
     *
     * @return boolean 执行结果判定值（true表示成功，false表示失败）。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private boolean isSuccess(Map<String, Object> result) {
        if (result == null || result.get("success") == null) {
            return false;
        }
        Object success = result.get("success");
        if (success instanceof Boolean) {
            return (Boolean) success;
        }
        String value = String.valueOf(success).trim().toLowerCase();
        return "1".equals(value) || "true".equals(value);
    }

    /**
     * @description 取消已注册的本地任务句柄，防止应用退出时残留无效调度引用。
     * @params future 已注册定时任务的调度句柄。
     *
     * @return void 无返回结果，方法执行后会取消指定的任务调度。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private void cancelFuture(ScheduledFuture<?> future) {
        if (future != null) {
            future.cancel(false);
        }
    }
}
