package com.sysware.mainData.action;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.mainData.domain.HdlMainDataMapping;
import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.bo.HdlMainDataMappingBo;
import com.sysware.mainData.domain.bo.HdlOrganizationDepartmentBo;

public class mainDataMappingAction {
    /**
     * @author lxd
     * @description: 进行表格页面查询
     * @param bo
     * @param pageQuery
     **/
    public static LambdaQueryWrapper<HdlMainDataMapping> buildMappingQueryWrapper(HdlMainDataMappingBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<HdlMainDataMapping> lqw = Wrappers.lambdaQuery();
        // 首先 进行局部精确匹配查询
        if (bo != null) {
            // 精确匹配搜索字段类型
            if (StrUtil.isNotBlank(bo.getType())) lqw.eq(HdlMainDataMapping::getType, bo.getType());
            // 模糊匹配其他搜索字段
            if (StrUtil.isNotBlank(bo.getSourceField())) lqw.like(HdlMainDataMapping::getSourceField, bo.getSourceField());
            if (StrUtil.isNotBlank(bo.getTargetField())) lqw.like(HdlMainDataMapping::getTargetField, bo.getTargetField());
            if (StrUtil.isNotBlank(bo.getFieldMeaning())) lqw.like(HdlMainDataMapping::getFieldMeaning, bo.getFieldMeaning());
        }
        // 其次 进行全局模糊查询逻辑
        if (pageQuery != null && StrUtil.isNotBlank(bo.getSearchValue())) {
            lqw.and(wrapper -> {
                wrapper.like(HdlMainDataMapping::getType, bo.getSearchValue())
                        .or()
                        .like(HdlMainDataMapping::getSourceField, bo.getSearchValue())
                        .or()
                        .like(HdlMainDataMapping::getTargetField, bo.getSearchValue())
                        .or()
                        .like(HdlMainDataMapping::getFieldMeaning, bo.getSearchValue());
            });
        }
        return lqw;
    }
}
