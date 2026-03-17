package com.sysware.system.mapper;

import com.sysware.common.core.mapper.BaseMapperPlus;
import com.sysware.system.domain.SysOperLog;
import com.sysware.system.domain.bo.SysOperLogBo;
import com.sysware.system.domain.vo.SysOperLogVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 操作日志 数据层
 *
 * @author
 */
public interface SysOperLogMapper extends BaseMapperPlus<SysOperLogMapper, SysOperLog, SysOperLogVo> {

    /**
     *
     * @param tableName
     * @return
     */
    List<Map> getTableColumn(@Param("tableName") String tableName);

    /**
     *
     * @param operId
     * @return
     */
    List<Map> getOperDataInfo(@Param("operId") String operId);

    /**
     * 根据时间选择需要的实体类集合（导出）
     * @param  operLog
     * @return 操作结果
     */
    List<SysOperLog> selectOperLogList(SysOperLogBo operLog);
}
