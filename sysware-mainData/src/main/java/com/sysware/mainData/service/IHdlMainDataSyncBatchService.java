package com.sysware.mainData.service;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.mainData.domain.HdlMainDataSyncBatch;
import com.sysware.mainData.domain.bo.HdlMainDataSyncBatchBo;
import com.sysware.mainData.domain.vo.HdlMainDataSyncBatchVo;

import java.util.List;

/**
 * 主数据同步批次记录Service接口
 */
public interface IHdlMainDataSyncBatchService {

    /**
     * 分页查询同步批次记录
     */
    TableDataInfo queryPageList(HdlMainDataSyncBatchBo bo, PageQuery pageQuery);

    /**
     * 查询指定类型最近的同步批次记录
     */
    List<HdlMainDataSyncBatchVo> queryLatestByType(String dataType, Integer limit);

    /**
     * 保存同步批次记录
     */
    void saveBatch(HdlMainDataSyncBatch batch);
}
