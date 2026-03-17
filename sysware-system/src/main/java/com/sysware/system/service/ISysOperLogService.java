package com.sysware.system.service;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.system.domain.SysOperLog;
import com.sysware.system.domain.bo.SysOperLogBo;
import com.sysware.system.domain.vo.SysOperLogVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 操作日志 服务层
 *
 * @author
 */
public interface ISysOperLogService{

    TableDataInfo<SysOperLogVo> selectPageOperLogList(SysOperLogBo operLog, PageQuery pageQuery);

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    public void insertOperlog(SysOperLog operLog);

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    public List<SysOperLog> selectOperLogList(SysOperLogBo operLog);

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    public int deleteOperLogByIds(Collection<String> operIds);

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    public SysOperLog selectOperLogById(Long operId);

    /**
     * 清空操作日志
     */
    public void cleanOperLog();

    /**
     * 获取操作对象详情
     * @param operLog
     * @return
     */
    R getOperData(SysOperLogBo operLog);

    /**
     * 查询操作日志记录
     */
    public SysOperLogVo queryById(String operId);

    /**
     * 新增操作日志记录
     */
    public Boolean insertByBo(SysOperLogBo bo);

    /**
     * 修改操作日志记录
     */
    public Boolean updateByBo(SysOperLogBo bo);

}
