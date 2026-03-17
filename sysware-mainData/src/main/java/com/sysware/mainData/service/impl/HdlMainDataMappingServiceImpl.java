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
        return baseMapper.selectMainDataMappingByType(type);
    }
}
