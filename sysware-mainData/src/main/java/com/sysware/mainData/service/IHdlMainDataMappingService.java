package com.sysware.mainData.service;

import com.sysware.mainData.domain.HdlMainDataMapping;
import com.sysware.mainData.domain.vo.HdlMainDataMappingVo;
import com.sysware.mainData.domain.bo.HdlMainDataMappingBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 主数据字段映射Service接口
 *
 * @author aa
 * @date 2026-01-14
 */
public interface IHdlMainDataMappingService {

    /**
     * 查询主数据字段映射
     */
    public HdlMainDataMappingVo queryById(Long mapId);

    /**
     * 查询主数据字段映射列表
     */
    public TableDataInfo queryPageList(HdlMainDataMappingBo bo, PageQuery pageQuery);

    /**
     * 新增主数据字段映射
     */
    public Boolean insertByBo(HdlMainDataMappingBo bo);

    /**
     * 修改主数据字段映射
     */
    public Boolean updateByBo(HdlMainDataMappingBo bo);

    /**
     * 校验并批量删除主数据字段映射信息
     */
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据类型查询字段映射列表
     *
     * @param type 字段类型
     * @return 字段映射列表
     */
    public List<HdlMainDataMappingVo> selectMainDataMappingByType(String type);
}
