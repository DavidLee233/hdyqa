package com.sysware.system.service;

import com.sysware.system.domain.SysPageBtn;
import com.sysware.system.domain.vo.SysPageBtnVo;
import com.sysware.system.domain.bo.SysPageBtnBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 页面按钮Service接口
 *
 * @author aa
 * @date 2023-05-22
 */
public interface ISysPageBtnService {

    /**
     * 查询页面按钮
     */
    SysPageBtnVo queryById(String id);

    /**
     * 查询页面按钮列表
     */
    TableDataInfo queryPageList(SysPageBtnBo bo, PageQuery pageQuery);

    /**
     * 查询页面按钮列表
     */
    List<SysPageBtnVo> queryList(SysPageBtnBo bo);

    /**
     * 新增页面按钮
     */
    Boolean insertByBo(SysPageBtnBo bo);

    /**
     * 修改页面按钮
     */
    Boolean updateByBo(SysPageBtnBo bo);

    /**
     * 校验并批量删除页面按钮信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 初始化页面按钮
     * @return
     */
    Boolean initBtn();
}
