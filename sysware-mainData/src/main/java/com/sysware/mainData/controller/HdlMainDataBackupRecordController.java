package com.sysware.mainData.controller;

import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.mainData.domain.bo.HdlMainDataBackupRecordBo;
import com.sysware.mainData.domain.vo.HdlMainDataBackupRecordVo;
import com.sysware.mainData.service.IHdlMainDataBackupRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
/**
 * @project npic
 * @description HdlMainDataBackupRecordController控制器，负责主数据备份记录相关接口请求接收、参数处理与结果响应。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mainData/backupRecord")
public class HdlMainDataBackupRecordController extends BaseController {

    private final IHdlMainDataBackupRecordService backupRecordService;
    /**
     * @description 查询主数据备份记录数据并处理为表格分页结构后返回前端展示。
     * @params bo 主数据备份记录分页查询条件业务对象
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/list")
    public TableDataInfo list(HdlMainDataBackupRecordBo bo, PageQuery pageQuery) {
        return backupRecordService.queryPageList(bo, pageQuery);
    }
    /**
     * @description 执行latest方法，完成主数据备份记录相关业务处理。
     * @params limit 返回记录数量上限
     *
      * @return R<List<HdlMainDataBackupRecordVo>> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/latest")
    public R<List<HdlMainDataBackupRecordVo>> latest(
                                                     @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(backupRecordService.queryLatest(limit));
    }
    /**
     * @description 执行execute方法，完成主数据备份记录相关业务处理。
     * @params 无
     *
      * @return R<Map<String, Object>> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostMapping("/execute")
    public R<Map<String, Object>> execute() {
        Map<String, Object> result = backupRecordService.executeBackup("manual", "manualTrigger");
        Object success = result.get("success");
        if (success instanceof Boolean && (Boolean) success) {
            return R.ok(result);
        }
        if (success != null && "1".equals(String.valueOf(success))) {
            return R.ok(result);
        }
        String message = result.get("message") == null ? "执行备份失败" : String.valueOf(result.get("message"));
        return R.fail(message, result);
    }
}