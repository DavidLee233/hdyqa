package com.sysware.mainData.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.StringUtils;
import com.sysware.mainData.domain.HdlMainDataBackupRecord;
import com.sysware.mainData.domain.HdlMainDataSyncBatch;
import com.sysware.mainData.domain.bo.HdlMainDataBackupRecordBo;
import com.sysware.mainData.domain.vo.HdlMainDataBackupRecordVo;
import com.sysware.mainData.mapper.HdlMainDataBackupRecordMapper;
import com.sysware.mainData.service.IHdlMainDataBackupRecordService;
import com.sysware.mainData.service.IHdlMainDataSyncBatchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * @project npic
 * @description HdlMainDataBackupRecordServiceImpl服务实现类，负责主数据备份记录业务规则执行、数据处理与流程编排。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Service
@RequiredArgsConstructor
public class HdlMainDataBackupRecordServiceImpl implements IHdlMainDataBackupRecordService {

    private static final Logger logger = LoggerFactory.getLogger(HdlMainDataBackupRecordServiceImpl.class);
    private static final String TRIGGER_MODE_MANUAL = "manual";
    private static final String TRIGGER_MODE_HANDLER = "handler";
    private static final String TYPE_ORG_DEPT = "1";
    private static final String TYPE_PERSON_BASIC = "2";
    private static final String TYPE_PERSON_JOB = "3";
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<String> SHARED_BACKUP_TABLES = Arrays.asList(
        "hdl_main_data_mapping",
        "hdl_main_data_sync_batch",
        "hdl_main_data_backup_record"
    );
    private static final Map<String, String> DATA_TYPE_TABLE_MAPPING = new LinkedHashMap<>();

    static {
        DATA_TYPE_TABLE_MAPPING.put(TYPE_ORG_DEPT, "hdl_organization_department");
        DATA_TYPE_TABLE_MAPPING.put(TYPE_PERSON_BASIC, "hdl_person_basic_info");
        DATA_TYPE_TABLE_MAPPING.put(TYPE_PERSON_JOB, "hdl_person_job_info");
    }

    private final HdlMainDataBackupRecordMapper baseMapper;
    private final IHdlMainDataSyncBatchService syncBatchService;
    private final JdbcTemplate jdbcTemplate;

    @Value("${main-data.backup-dir:${sysware.profile}/main-data-backup}")
    private String backupDir;

    @Value("${main-data.backup-retention-weeks:10}")
    private int backupRetentionWeeks;

    @Value("${main-data.sync-backup-min-interval-minutes:60}")
    private long syncBackupMinIntervalMinutes;

    /**
     * @description 按筛选条件分页查询主数据备份记录并封装为表格数据返回。
     * @params bo 主数据备份记录分页查询条件业务对象
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public TableDataInfo queryPageList(HdlMainDataBackupRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<HdlMainDataBackupRecord> lqw = buildQueryWrapper(bo);
        Page<HdlMainDataBackupRecord> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<HdlMainDataBackupRecord> result = baseMapper.selectPage(page, lqw);
        List<HdlMainDataBackupRecordVo> voList = BeanUtil.copyToList(result.getRecords(), HdlMainDataBackupRecordVo.class);
        Page<HdlMainDataBackupRecordVo> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return TableDataInfo.build(voPage);
    }

    /**
     * @description 查询最近的主数据备份记录记录列表。
     * @params limit 返回记录数量上限
     *
      * @return List<HdlMainDataBackupRecordVo> 主数据备份记录列表结果，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public List<HdlMainDataBackupRecordVo> queryLatest(Integer limit) {
        int effectiveLimit = (limit == null || limit <= 0) ? 5 : Math.min(limit, 20);
        List<HdlMainDataBackupRecord> list = baseMapper.selectList(Wrappers.lambdaQuery(HdlMainDataBackupRecord.class)
            .orderByDesc(HdlMainDataBackupRecord::getStartTime)
            .last("limit " + effectiveLimit));
        return BeanUtil.copyToList(list, HdlMainDataBackupRecordVo.class);
    }

    /**
     * @description 查询最近一次成功的主数据备份记录记录。
     * @params 无
     *
      * @return HdlMainDataBackupRecord 主数据备份记录相关业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public HdlMainDataBackupRecord queryLatestSuccess() {
        return baseMapper.selectOne(Wrappers.lambdaQuery(HdlMainDataBackupRecord.class)
            .eq(HdlMainDataBackupRecord::getSuccess, "1")
            .orderByDesc(HdlMainDataBackupRecord::getEndTime)
            .last("limit 1"));
    }

    /**
     * @description 保存主数据备份记录到数据库。
     * @params record 备份记录实体
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public void saveRecord(HdlMainDataBackupRecord record) {
        baseMapper.insert(record);
    }

    /**
     * @description 执行主数据备份流程并返回批次统计信息。
     * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Map<String, Object> executeBackup(String triggerMode) {
        return executeBackup(triggerMode, null, null);
    }

    /**
     * @description 执行主数据备份流程并返回批次统计信息。
     * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
     * @params handlerName 本地任务处理器名称
     *
      * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Map<String, Object> executeBackup(String triggerMode, String handlerName) {
        return executeBackup(triggerMode, handlerName, null);
    }

    /**
     * @description 按指定可参与定时备份的数据类型集合执行主数据备份流程，并返回本次备份的文件与统计结果。
     * @params triggerMode 触发方式（manual手动触发或handler本地处理器触发）。
     * @params handlerName 本地处理器名称。
     * @params enabledDataTypes 允许参与本次备份的数据类型编码集合，用于按页面开关筛选需备份的业务表。
     *
     * @return Map<String, Object> 备份执行结果（包含文件名、文件路径、备份表数、备份行数与提示信息），用于处理器日志与前端结果展示。
     * @author DavidLee233
     * @date 2026/3/21
     */
    @Override
    public Map<String, Object> executeBackup(String triggerMode, String handlerName, Set<String> enabledDataTypes) {
        BackupStats stats = BackupStats.start(
            normalizeTriggerMode(triggerMode),
            resolveHandlerName(handlerName, triggerMode),
            resolveOperatorName(),
            resolveOperatorId()
        );
        try {
            String intervalError = validateBackupInterval();
            if (StrUtil.isNotBlank(intervalError)) {
                stats.fail(intervalError);
                return finishAndPersist(stats);
            }

            Path backupDirectory = ensureBackupDirectory();
            String fileName = LocalDate.now().format(FILE_DATE_FORMATTER) + "_backup.sql";
            Path filePath = backupDirectory.resolve(fileName);

            Set<String> effectiveEnabledTypes = resolveEnabledBackupTypes(enabledDataTypes);
            stats.enabledDataTypes = String.join(",", effectiveEnabledTypes);
            stats.disabledDataTypes = buildDisabledTypeSummary(effectiveEnabledTypes);
            if (StrUtil.isNotBlank(stats.disabledDataTypes)) {
                stats.note("已按开关跳过以下数据类型：" + stats.disabledDataTypes);
            }

            BackupSnapshot snapshot = buildBackupSnapshot(effectiveEnabledTypes, stats);
            Files.write(
                filePath,
                snapshot.sqlContent.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            stats.fileName = fileName;
            stats.filePath = filePath.toAbsolutePath().toString();
            stats.fileSizeBytes = Files.size(filePath);
            stats.tableCount = snapshot.tableCount;
            stats.rowCount = snapshot.rowCount;

            int deletedCount = cleanupOldBackupFiles(backupDirectory, backupRetentionWeeks);
            if (deletedCount > 0) {
                stats.note("已清理历史备份文件 " + deletedCount + " 个");
            }
        } catch (Exception e) {
            logger.error("执行主数据备份失败", e);
            stats.fail("执行主数据备份失败：" + e.getMessage());
        }
        return finishAndPersist(stats);
    }


    /**
     * @description 构建主数据备份记录处理所需的中间对象或条件。
     * @params bo 主数据备份记录业务请求对象（包含查询与变更字段）
     *
      * @return LambdaQueryWrapper<HdlMainDataBackupRecord> 主数据备份记录查询条件构造器，用于后续数据库过滤与排序。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private LambdaQueryWrapper<HdlMainDataBackupRecord> buildQueryWrapper(HdlMainDataBackupRecordBo bo) {
        LambdaQueryWrapper<HdlMainDataBackupRecord> lqw = Wrappers.lambdaQuery(HdlMainDataBackupRecord.class);
        if (bo == null) {
            return lqw.orderByDesc(HdlMainDataBackupRecord::getStartTime);
        }
        if (StringUtils.isNotBlank(bo.getBatchNo())) {
            lqw.like(HdlMainDataBackupRecord::getBatchNo, bo.getBatchNo());
        }
        if (StringUtils.isNotBlank(bo.getTriggerMode())) {
            lqw.eq(HdlMainDataBackupRecord::getTriggerMode, bo.getTriggerMode());
        }
        if (StringUtils.isNotBlank(bo.getHandlerName())) {
            lqw.like(HdlMainDataBackupRecord::getHandlerName, bo.getHandlerName());
        }
        if (StringUtils.isNotBlank(bo.getSuccess())) {
            lqw.eq(HdlMainDataBackupRecord::getSuccess, bo.getSuccess());
        }
        if (StringUtils.isNotBlank(bo.getFileName())) {
            lqw.like(HdlMainDataBackupRecord::getFileName, bo.getFileName());
        }
        if (StringUtils.isNotBlank(bo.getOperatorName())) {
            lqw.like(HdlMainDataBackupRecord::getOperatorName, bo.getOperatorName());
        }
        if (bo.getStartTimeBegin() != null) {
            lqw.ge(HdlMainDataBackupRecord::getStartTime, bo.getStartTimeBegin());
        }
        if (bo.getStartTimeEnd() != null) {
            lqw.le(HdlMainDataBackupRecord::getStartTime, bo.getStartTimeEnd());
        }
        lqw.orderByDesc(HdlMainDataBackupRecord::getStartTime);
        return lqw;
    }

    /**
     * @description 结束备份批次并落库执行结果后返回统计。
     * @params stats 统计结果对象（同步或备份执行统计）
     *
     * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Map<String, Object> finishAndPersist(BackupStats stats) {
        Map<String, Object> result = stats.finishAndBuild();
        try {
            saveRecord(buildBackupRecordEntity(stats));
            result.put("persisted", true);
        } catch (Exception e) {
            logger.error("备份记录落库失败，批次号={}", stats.batchNo, e);
            result.put("persisted", false);
            result.put("persistError", e.getMessage());
        }
        return result;
    }

    /**
     * @description 将备份统计对象转换为备份记录实体。
     * @params stats 统计结果对象（同步或备份执行统计）
     *
      * @return HdlMainDataBackupRecord 主数据备份记录相关业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private HdlMainDataBackupRecord buildBackupRecordEntity(BackupStats stats) {
        HdlMainDataBackupRecord record = new HdlMainDataBackupRecord();
        record.setBatchNo(stats.batchNo);
        record.setTriggerMode(stats.triggerMode);
        record.setHandlerName(stats.handlerName);
        record.setSuccess(stats.success ? "1" : "0");
        record.setMessage(stats.message);
        record.setFileName(stats.fileName);
        record.setFilePath(stats.filePath);
        record.setFileSizeBytes(stats.fileSizeBytes);
        record.setTableCount(stats.tableCount);
        record.setRowCount(stats.rowCount);
        record.setStartTime(toLocalDateTime(stats.startAt));
        record.setEndTime(toLocalDateTime(stats.endAt));
        record.setDurationMs(stats.endAt - stats.startAt);
        record.setOperatorName(stats.operatorName);
        record.setOperatorId(stats.operatorId);
        record.setCreateBy(stats.operatorName);
        record.setCreateTime(LocalDateTime.now());
        return record;
    }

    /**
     * @description 执行toLocalDateTime方法，完成主数据备份记录相关业务处理。
     * @params epochMillis 毫秒级时间戳（用于转换为本地日期时间）
     *
      * @return LocalDateTime 本地日期时间结果，用于同步水位或时间过滤。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private LocalDateTime toLocalDateTime(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }

    /**
     * @description 解析并转换主数据备份记录相关输入，生成可直接使用的数据结构。
     * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
     *
      * @return String 字符串类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String normalizeTriggerMode(String triggerMode) {
        if (StrUtil.isBlank(triggerMode)) {
            return TRIGGER_MODE_MANUAL;
        }
        String normalized = triggerMode.trim().toLowerCase(Locale.ROOT);
        return TRIGGER_MODE_HANDLER.equals(normalized) ? TRIGGER_MODE_HANDLER : TRIGGER_MODE_MANUAL;
    }

    /**
     * @description 解析并转换主数据备份记录相关输入，生成可直接使用的数据结构。
     * @params handlerName 本地任务处理器名称
     * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
     *
      * @return String 解析后的业务字符串结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String resolveHandlerName(String handlerName, String triggerMode) {
        if (StrUtil.isNotBlank(handlerName)) {
            return handlerName;
        }
        String normalizedTriggerMode = normalizeTriggerMode(triggerMode);
        if (TRIGGER_MODE_HANDLER.equals(normalizedTriggerMode)) {
            return "mainDataWeeklyBackupHandler";
        }
        return "manualTrigger";
    }

    /**
     * @description 执行validateBackupInterval方法，完成主数据备份记录相关业务处理。
     * @params 无
     *
      * @return String 字符串类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String validateBackupInterval() {
        HdlMainDataSyncBatch latestSync = syncBatchService.queryLatestSuccess(null);
        if (latestSync == null || latestSync.getEndTime() == null) {
            return null;
        }
        long minutes = Duration.between(latestSync.getEndTime(), LocalDateTime.now()).toMinutes();
        if (minutes >= syncBackupMinIntervalMinutes) {
            return null;
        }
        return String.format(
            "最近一次同步完成时间为 %s，备份任务需至少间隔 %d 分钟后再执行",
            latestSync.getEndTime().format(TIME_FORMATTER),
            syncBackupMinIntervalMinutes
        );
    }

    /**
     * @description 检查并创建备份目录，确保备份文件可写入。
     * @params param1 动态参数值
     *
      * @return Path 文件路径对象，用于定位备份目录或文件。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private Path ensureBackupDirectory() throws IOException {
        Path path = Paths.get(backupDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    /**
     * @description 构建主数据备份记录处理所需的中间对象或条件。
     * @params 无
     *
      * @return BackupSnapshot 类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private BackupSnapshot buildBackupSnapshot(Set<String> enabledDataTypes, BackupStats stats) {
        StringBuilder sql = new StringBuilder(32 * 1024);
        sql.append("-- Main data backup generated at ")
            .append(LocalDateTime.now().format(TIME_FORMATTER))
            .append('\n')
            .append("SET NAMES utf8mb4;\n")
            .append("SET FOREIGN_KEY_CHECKS = 0;\n\n");

        int rowCount = 0;
        int tableCount = 0;
        List<String> backupTables = resolveBackupTables(enabledDataTypes);
        if (backupTables.size() == SHARED_BACKUP_TABLES.size() && stats != null && StrUtil.isNotBlank(stats.disabledDataTypes)) {
            stats.note("当前仅备份共享元数据表，业务数据表均因开关关闭被跳过");
        }
        for (String table : backupTables) {
            List<String> columns = queryTableColumns(table);
            if (columns.isEmpty()) {
                continue;
            }
            tableCount++;
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM `" + table + "`");
            rowCount += rows.size();

            sql.append("-- ----------------------------\n")
                .append("-- Data of ").append(table).append('\n')
                .append("-- ----------------------------\n")
                .append("DELETE FROM `").append(table).append("`;\n");

            if (rows.isEmpty()) {
                sql.append('\n');
                continue;
            }
            for (Map<String, Object> row : rows) {
                sql.append("INSERT INTO `").append(table).append("` (");
                sql.append(columns.stream().map(col -> "`" + col + "`").collect(Collectors.joining(", ")));
                sql.append(") VALUES (");
                List<String> values = new ArrayList<>(columns.size());
                for (String column : columns) {
                    values.add(toSqlValue(row.get(column)));
                }
                sql.append(String.join(", ", values));
                sql.append(");\n");
            }
            sql.append('\n');
        }

        sql.append("SET FOREIGN_KEY_CHECKS = 1;\n");
        return new BackupSnapshot(sql.toString(), tableCount, rowCount);
    }




    /**
     * @description 根据定时备份开关汇总本次允许进入备份文件的业务表列表，确保关闭开关的数据类型不会被处理器备份。
     * @params enabledDataTypes 已开启定时备份的数据类型编码集合。
     *
     * @return List<String> 本次备份应执行的表名列表（包含共享表及开启开关的数据类型业务表），用于生成SQL备份文件。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private List<String> resolveBackupTables(Set<String> enabledDataTypes) {
        List<String> tables = new ArrayList<>(SHARED_BACKUP_TABLES);
        Set<String> normalizedTypes = resolveEnabledBackupTypes(enabledDataTypes);
        for (Map.Entry<String, String> entry : DATA_TYPE_TABLE_MAPPING.entrySet()) {
            if (normalizedTypes.contains(entry.getKey())) {
                tables.add(entry.getValue());
            }
        }
        return tables;
    }

    /**
     * @description 统一规范化本次参与定时备份的数据类型集合，在未显式传入时回退为当前配置中已开启的备份类型集合。
     * @params enabledDataTypes 外部传入的已开启定时备份数据类型集合。
     *
     * @return Set<String> 规范化后的数据类型编码集合，用于后续备份表筛选与结果说明。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private Set<String> resolveEnabledBackupTypes(Set<String> enabledDataTypes) {
        Set<String> resolved = new LinkedHashSet<>();
        Set<String> source = enabledDataTypes;
        if (source == null) {
            source = new LinkedHashSet<>(DATA_TYPE_TABLE_MAPPING.keySet());
        }
        for (String item : source) {
            if (DATA_TYPE_TABLE_MAPPING.containsKey(item)) {
                resolved.add(item);
            }
        }
        if (resolved.isEmpty() && enabledDataTypes == null) {
            resolved.addAll(DATA_TYPE_TABLE_MAPPING.keySet());
        }
        return resolved;
    }

    /**
     * @description 根据已开启的定时备份类型集合计算被关闭的类型说明文本，供批次记录与处理器日志展示跳过信息。
     * @params enabledDataTypes 已开启定时备份的数据类型编码集合。
     *
     * @return String 已关闭备份的数据类型中文摘要，用于备份结果提示与日志说明。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private String buildDisabledTypeSummary(Set<String> enabledDataTypes) {
        List<String> disabledNames = new ArrayList<>();
        for (String dataType : DATA_TYPE_TABLE_MAPPING.keySet()) {
            if (!enabledDataTypes.contains(dataType)) {
                disabledNames.add(resolveDataTypeName(dataType));
            }
        }
        return String.join("、", disabledNames);
    }

    /**
     * @description 将主数据类型编码转换为中文类型名称，供备份日志、备注与统计提示统一使用。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     *
     * @return String 主数据类型中文名称，用于备份结果提示与日志展示。
     * @author DavidLee233
     * @date 2026/3/21
     */
    private String resolveDataTypeName(String dataType) {
        if (TYPE_PERSON_BASIC.equals(dataType)) {
            return "员工基本信息";
        }
        if (TYPE_PERSON_JOB.equals(dataType)) {
            return "员工工作信息";
        }
        return "组织部门";
    }

    /**
     * @description 按业务条件查询主数据备份记录数据并返回处理结果。
     * @params table 数据库表名
     *
      * @return List<String> 列表结果集合，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private List<String> queryTableColumns(String table) {
        try {
            List<Map<String, Object>> columnRows = jdbcTemplate.queryForList("SHOW COLUMNS FROM `" + table + "`");
            if (columnRows == null || columnRows.isEmpty()) {
                return Collections.emptyList();
            }
            List<String> columns = new ArrayList<>(columnRows.size());
            for (Map<String, Object> columnRow : columnRows) {
                Object field = columnRow.get("Field");
                if (field != null) {
                    columns.add(String.valueOf(field));
                }
            }
            return columns;
        } catch (Exception e) {
            logger.warn("查询表字段失败，跳过该表备份，表名={}", table, e);
            return Collections.emptyList();
        }
    }

    /**
     * @description 执行toSqlValue方法，完成主数据备份记录相关业务处理。
     * @params value 待转换字段值
     *
      * @return String 字符串类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String toSqlValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }
        if (value instanceof LocalDateTime) {
            return "'" + ((LocalDateTime) value).format(TIME_FORMATTER) + "'";
        }
        if (value instanceof Timestamp) {
            return "'" + ((Timestamp) value).toLocalDateTime().format(TIME_FORMATTER) + "'";
        }
        if (value instanceof Date) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault());
            return "'" + localDateTime.format(TIME_FORMATTER) + "'";
        }
        String text = String.valueOf(value)
            .replace("\\", "\\\\")
            .replace("'", "''");
        return "'" + text + "'";
    }

    /**
     * @description 清理超出保留周期的历史备份文件。
     * @params backupDirectory 备份目录路径
     * @params keepCount 备份文件最大保留数量
     *
      * @return int 数值型业务处理结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private int cleanupOldBackupFiles(Path backupDirectory, int keepCount) {
        int effectiveKeepCount = keepCount <= 0 ? 10 : keepCount;
        List<Path> files;
        try (Stream<Path> stream = Files.list(backupDirectory)) {
            files = stream
                .filter(path -> Files.isRegularFile(path))
                .filter(path -> {
                    String name = path.getFileName().toString();
                    return name.matches("\\d{8}_backup\\.sql");
                })
                .sorted((a, b) -> b.getFileName().toString().compareTo(a.getFileName().toString()))
                .collect(Collectors.toList());
        } catch (IOException e) {
            logger.warn("读取备份文件列表失败，目录={}", backupDirectory, e);
            return 0;
        }

        if (files.size() <= effectiveKeepCount) {
            return 0;
        }

        int deleted = 0;
        for (int i = effectiveKeepCount; i < files.size(); i++) {
            try {
                Files.deleteIfExists(files.get(i));
                deleted++;
            } catch (IOException e) {
                logger.warn("删除历史备份文件失败，文件={}", files.get(i), e);
            }
        }
        return deleted;
    }

    /**
     * @description 解析当前操作人名称，未登录时回退默认值。
     * @params 无
     *
      * @return String 解析后的业务字符串结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String resolveOperatorName() {
        try {
            LoginUser user = LoginHelper.getLoginUser();
            if (user != null && StrUtil.isNotBlank(user.getUsername())) {
                return user.getUsername();
            }
        } catch (Exception ignored) {
            // ignore
        }
        return "MAIN_DATA_BACKUP";
    }

    /**
     * @description 解析当前操作人标识，未登录时回退默认值。
     * @params 无
     *
      * @return String 解析后的业务字符串结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String resolveOperatorId() {
        try {
            LoginUser user = LoginHelper.getLoginUser();
            if (user != null && StrUtil.isNotBlank(user.getLoginName())) {
                return user.getLoginName();
            }
        } catch (Exception ignored) {
            // ignore
        }
        return "main_data_backup";
    }

    private static class BackupSnapshot {
        private final String sqlContent;
        private final int tableCount;
        private final int rowCount;

        private BackupSnapshot(String sqlContent, int tableCount, int rowCount) {
            this.sqlContent = sqlContent;
            this.tableCount = tableCount;
            this.rowCount = rowCount;
        }
    }

    private static class BackupStats {
        private final String batchNo;
        private final String triggerMode;
        private final String handlerName;
        private final String operatorName;
        private final String operatorId;
        private final long startAt;
        private long endAt;
        private boolean success = true;
        private String message = "success";
        private String fileName;
        private String filePath;
        private Long fileSizeBytes = 0L;
        private Integer tableCount = 0;
        private Integer rowCount = 0;
        private String enabledDataTypes = "";
        private String disabledDataTypes = "";

        private BackupStats(String triggerMode, String handlerName, String operatorName, String operatorId) {
            this.triggerMode = triggerMode;
            this.handlerName = handlerName;
            this.operatorName = operatorName;
            this.operatorId = operatorId;
            this.startAt = System.currentTimeMillis();
            this.batchNo = "backup_" + this.startAt;
        }

        static BackupStats start(String triggerMode, String handlerName, String operatorName, String operatorId) {
            return new BackupStats(triggerMode, handlerName, operatorName, operatorId);
        }

        void fail(String msg) {
            this.success = false;
            this.message = msg;
        }

        void note(String msg) {
            if (StrUtil.isBlank(msg)) {
                return;
            }
            if ("success".equals(this.message)) {
                this.message = msg;
                return;
            }
            if (!this.message.contains(msg)) {
                this.message = this.message + " | " + msg;
            }
        }

        Map<String, Object> finishAndBuild() {
            this.endAt = System.currentTimeMillis();
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("batchNo", batchNo);
            result.put("triggerMode", triggerMode);
            result.put("handlerName", handlerName);
            result.put("operatorName", operatorName);
            result.put("operatorId", operatorId);
            result.put("success", success);
            result.put("message", message);
            result.put("fileName", fileName);
            result.put("filePath", filePath);
            result.put("fileSizeBytes", fileSizeBytes);
            result.put("tableCount", tableCount);
            result.put("rowCount", rowCount);
            result.put("enabledDataTypes", enabledDataTypes);
            result.put("disabledDataTypes", disabledDataTypes);
            result.put("startAt", startAt);
            result.put("endAt", endAt);
            result.put("durationMs", endAt - startAt);
            return result;
        }
    }
}
