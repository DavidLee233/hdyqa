package com.sysware.system.service;

import com.sysware.system.domain.SysFormFieldVisible;
import com.sysware.system.domain.vo.SysFormFieldVisibleVo;
import com.sysware.system.domain.bo.SysFormFieldVisibleBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 单项显示控制Service接口
 *
 * @author aa
 * @date 2023-06-04
 */
public interface ISysFormFieldVisibleService {

    /**
     * 查询单项显示控制
     */
    SysFormFieldVisibleVo queryById(String id);

    /**
     * 查询单项显示控制列表
     */
    TableDataInfo queryPageList(SysFormFieldVisibleBo bo, PageQuery pageQuery);

    /**
     * 查询单项显示控制列表
     */
    List<SysFormFieldVisibleVo> queryList(SysFormFieldVisibleBo bo);

    /**
     * 新增单项显示控制
     */
    Boolean insertByBo(SysFormFieldVisibleBo bo);

    /**
     * 修改单项显示控制
     */
    Boolean updateByBo(SysFormFieldVisibleBo bo);

    /**
     * 校验并批量删除单项显示控制信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
