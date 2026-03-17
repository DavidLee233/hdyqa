package com.sysware.system.service;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.system.domain.SysLogininfor;
import com.sysware.system.domain.bo.SysLogininforBo;
import com.sysware.system.domain.vo.SysLogininforVo;

import java.util.List;
import java.util.Map;

/**
 * 系统访问日志情况信息 服务层
 *
 * @author
 */
public interface ISysLogininforService{

    /**
     * 查询系统访问记录列表
     */
    public TableDataInfo<SysLogininforVo> selectPageLogininforList(SysLogininforBo logininfor, PageQuery pageQuery);

    /**
     * 查询系统访问记录
     */
    public SysLogininforVo queryById(String infoId);

    /**
     * 新增系统访问记录
     */
    public Boolean insertByBo(SysLogininforBo bo);

    /**
     * 修改系统访问记录
     */
    public Boolean updateByBo(SysLogininforBo bo);

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(SysLogininfor logininfor);

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    public List<SysLogininfor> selectLogininforList(SysLogininforBo logininfor);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return
     */
    public int deleteLogininforByIds(String[] infoIds);

    /**
     * 清空系统登录日志
     */
    public void cleanLogininfor();
}
