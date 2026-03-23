package com.sysware.mainData.controller;

import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.mainData.domain.bo.HdlMainDataSyncBatchBo;
import com.sysware.mainData.domain.vo.HdlMainDataSyncBatchVo;
import com.sysware.mainData.service.IHdlMainDataSyncBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * @project npic
 * @description HdlMainDataSyncBatchController控制器，负责主数据同步批次记录相关接口请求接收、参数处理与结果响应。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mainData/syncBatch")
public class HdlMainDataSyncBatchController extends BaseController {

    private final IHdlMainDataSyncBatchService syncBatchService;
    /**
     * @description 查询主数据同步批次记录数据并处理为表格分页结构后返回前端展示。
     * @params bo 主数据同步批次记录分页查询条件业务对象
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/list")
    public TableDataInfo list(HdlMainDataSyncBatchBo bo, PageQuery pageQuery) {
        return syncBatchService.queryPageList(bo, pageQuery);
    }
    /**
     * @description 执行latest方法，完成主数据同步批次记录相关业务处理。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     * @params limit 返回记录数量上限
     *
      * @return R<List<HdlMainDataSyncBatchVo>> 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/latest")
    public R<List<HdlMainDataSyncBatchVo>> latest(@RequestParam("dataType") String dataType,
                                                  @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(syncBatchService.queryLatestByType(dataType, limit));
    }
}