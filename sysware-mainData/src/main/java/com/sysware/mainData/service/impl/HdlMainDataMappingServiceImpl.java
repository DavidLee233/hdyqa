package com.sysware.mainData.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sysware.system.service.ISysTableFieldService;
import com.sysware.mainData.domain.bo.HdlMainDataMappingBo;
import com.sysware.mainData.domain.vo.HdlMainDataMappingVo;
import com.sysware.mainData.domain.HdlMainDataMapping;
import com.sysware.mainData.mapper.HdlMainDataMappingMapper;
import com.sysware.mainData.service.IHdlMainDataMappingService;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.SyswareUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.*;

import static com.sysware.mainData.action.mainDataMappingAction.buildMappingQueryWrapper;

/**
 * 主数据字段映射Service业务层处理
 *
 * @author aa
 * @date 2026-01-14
 */
@RequiredArgsConstructor
@Service
public class HdlMainDataMappingServiceImpl implements IHdlMainDataMappingService {

    private final HdlMainDataMappingMapper baseMapper;
    private final ISysTableFieldService tableFieldService;

    /**
     * 查询主数据字段映射
     */
    @Override
    public HdlMainDataMappingVo queryById(Long mapId){
        return baseMapper.selectVoById(mapId);
    }

    /**
     * 查询主数据字段映射列表
     */
    @Override
    public TableDataInfo queryPageList(HdlMainDataMappingBo bo, PageQuery pageQuery) {
        if (bo != null && StringUtils.isNotBlank(bo.getType())) {
            bo.setType(resolveStorageType(bo.getType()));
        }
        // 使用传入的pageQuery构建分页参数
        Page<HdlMainDataMapping> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<HdlMainDataMappingVo> result = baseMapper.selectVoPage(page, buildMappingQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }


    /**
     * 新增主数据字段映射
     */
    @Override
    public Boolean insertByBo(HdlMainDataMappingBo bo) {
        HdlMainDataMapping add = BeanUtil.toBean(bo, HdlMainDataMapping.class);
        LoginUser user = LoginHelper.getLoginUser();
        add.setCreateBy(user.getUsername());
        add.setCreateId(user.getLoginName());
        add.setCreateTime(LocalDateTime.now());
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setMapId(add.getMapId());
        }
        return flag;
    }

    /**
     * 修改主数据字段映射
     */
    @Override
    public Boolean updateByBo(HdlMainDataMappingBo bo) {
        HdlMainDataMapping update = BeanUtil.toBean(bo, HdlMainDataMapping.class);
        LoginUser user = LoginHelper.getLoginUser();
        update.setUpdateBy(user.getUsername());
        update.setUpdateId(user.getLoginName());
        update.setUpdateTime(LocalDateTime.now());
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(HdlMainDataMapping entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除主数据字段映射
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 根据类型查询字段映射列表
     *
     * @param type 字段类型
     * @return 字段映射列表
     */
    @Override
    public List<HdlMainDataMappingVo> selectMainDataMappingByType(String type) {
        return baseMapper.selectMainDataMappingByType(resolveStorageType(type));
    }

    /**
     * 自动兼容字段类型编码：
     * - 新编码：1/2/3
     * - 旧编码：0/1/2
     *
     * 规则：当库中存在 type=0 且不存在 type=3 时，认为使用旧编码。
     */
    private String resolveStorageType(String requestedType) {
        if (StringUtils.isBlank(requestedType)) {
            return requestedType;
        }
        if (!isLegacyTypeSchema()) {
            return requestedType;
        }
        if ("1".equals(requestedType)) {
            return "0";
        }
        if ("2".equals(requestedType)) {
            return "1";
        }
        if ("3".equals(requestedType)) {
            return "2";
        }
        return requestedType;
    }

    private boolean isLegacyTypeSchema() {
        Long type0Count = baseMapper.selectCount(
            Wrappers.<HdlMainDataMapping>lambdaQuery().eq(HdlMainDataMapping::getType, "0"));
        Long type3Count = baseMapper.selectCount(
            Wrappers.<HdlMainDataMapping>lambdaQuery().eq(HdlMainDataMapping::getType, "3"));
        return type0Count != null && type0Count > 0 && (type3Count == null || type3Count == 0);
    }
}
