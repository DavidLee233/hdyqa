package com.sysware.mainData.mapper;

import com.sysware.common.core.mapper.BaseMapperPlus;
import com.sysware.mainData.domain.HdlMainDataBackupRecord;
import com.sysware.mainData.domain.vo.HdlMainDataBackupRecordVo;
/**
 * @project npic
 * @description HdlMainDataBackupRecordMapper数据访问接口，负责主数据备份记录持久化查询与写入操作。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface HdlMainDataBackupRecordMapper
    extends BaseMapperPlus<HdlMainDataBackupRecordMapper, HdlMainDataBackupRecord, HdlMainDataBackupRecordVo> {
}