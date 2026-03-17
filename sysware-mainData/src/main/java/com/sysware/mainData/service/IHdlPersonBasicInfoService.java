package com.sysware.mainData.service;

import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.vo.HdlPersonBasicInfoVo;
import com.sysware.mainData.domain.bo.HdlPersonBasicInfoBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 员工基本信息数据Service接口
 *
 * @author aa
 * @date 2026-01-15
 */
public interface IHdlPersonBasicInfoService {

    /**
     * 查询员工基本信息数据
     */
    HdlPersonBasicInfoVo queryById(String pkPsndoc);

    /**
     * 查询员工基本信息数据列表
     */
    TableDataInfo queryPageList(HdlPersonBasicInfoBo bo, PageQuery pageQuery);

    /**
     * 新增员工基本信息数据
     */
    Boolean insertByBo(HdlPersonBasicInfoBo bo);

    /**
     * 修改员工基本信息数据
     */
    Boolean updateByBo(HdlPersonBasicInfoBo bo);

    /**
     * 校验并批量删除员工基本信息数据信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 查询导出数据
     */
    List<HdlPersonBasicInfo> selectPBIList(HdlPersonBasicInfo pbi);
}
