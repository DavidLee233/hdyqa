package com.sysware.mainData.mapper;

import com.sysware.common.core.mapper.BaseMapperPlus;
import com.sysware.mainData.domain.HdlMainDataSyncBatch;
import com.sysware.mainData.domain.vo.HdlMainDataSyncBatchVo;
/**
 * @project npic
 * @description HdlMainDataSyncBatchMapper数据访问接口，负责主数据同步批次记录持久化查询与写入操作。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface HdlMainDataSyncBatchMapper extends BaseMapperPlus<HdlMainDataSyncBatchMapper, HdlMainDataSyncBatch, HdlMainDataSyncBatchVo> {
}