package com.sysware.mainData.action;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.mainData.domain.HdlMainDataMapping;
import com.sysware.mainData.domain.bo.HdlMainDataMappingBo;

/**
 * @project npic
 * @description mainDataMappingAction查询构建工具类，负责组装主数据映射过滤条件表达式。
 * @author DavidLee233
 * @date 2026/3/20
 */
public class mainDataMappingAction {
    /**
     * @description 构建主数据映射处理所需的中间对象或条件。
     * @params bo 主数据映射业务请求对象（包含查询与变更字段）
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return LambdaQueryWrapper<HdlMainDataMapping> 主数据映射查询条件构造器，用于后续数据库过滤与排序。
     * @author DavidLee233
     * @date 2026/3/20
     */
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