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
 * 主数据同步批次记录
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mainData/syncBatch")
public class HdlMainDataSyncBatchController extends BaseController {

    private final IHdlMainDataSyncBatchService syncBatchService;

    /**
     * 分页查询同步批次记录
     */
    @GetMapping("/list")
    public TableDataInfo list(HdlMainDataSyncBatchBo bo, PageQuery pageQuery) {
        return syncBatchService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询指定类型最近同步批次
     */
    @GetMapping("/latest")
    public R<List<HdlMainDataSyncBatchVo>> latest(@RequestParam("dataType") String dataType,
                                                   @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(syncBatchService.queryLatestByType(dataType, limit));
    }
}
