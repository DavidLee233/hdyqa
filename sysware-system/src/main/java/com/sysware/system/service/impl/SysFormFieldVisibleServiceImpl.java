package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.common.utils.SyswareUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sysware.system.service.ISysTableFieldService;
import com.sysware.system.domain.bo.SysFormFieldVisibleBo;
import com.sysware.system.domain.vo.SysFormFieldVisibleVo;
import com.sysware.system.domain.SysFormFieldVisible;
import com.sysware.system.mapper.SysFormFieldVisibleMapper;
import com.sysware.system.service.ISysFormFieldVisibleService;
import com.sysware.common.helper.LoginHelper;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.*;

/**
 * 单项显示控制Service业务层处理
 *
 * @author aa
 * @date 2023-06-04
 */
@RequiredArgsConstructor
@Service
public class SysFormFieldVisibleServiceImpl implements ISysFormFieldVisibleService {

    private final SysFormFieldVisibleMapper baseMapper;
    private final ISysTableFieldService tableFieldService;

    /**
     * 查询单项显示控制
     */
    @Override
    public SysFormFieldVisibleVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询单项显示控制列表
     */
    @Override
    public TableDataInfo queryPageList(SysFormFieldVisibleBo bo, PageQuery pageQuery) {

        List<String> fields = new ArrayList<>();
        fields.add("fieldId");

        IPage<Map> result = tableFieldService.getResult(BeanUtil.beanToMap(bo), pageQuery, baseMapper, fields);

        return TableDataInfo.build(result);

    }

    /**
     * 查询单项显示控制列表
     */
    @Override
    public List<SysFormFieldVisibleVo> queryList(SysFormFieldVisibleBo bo) {
        LambdaQueryWrapper<SysFormFieldVisible> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysFormFieldVisible> buildQueryWrapper(SysFormFieldVisibleBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysFormFieldVisible> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getFieldId()), SysFormFieldVisible::getFieldId, bo.getFieldId());
        lqw.eq(StringUtils.isNotBlank(bo.getDefaultValue()), SysFormFieldVisible::getDefaultValue, bo.getDefaultValue());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SysFormFieldVisible::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增单项显示控制
     */
    @Override
    public Boolean insertByBo(SysFormFieldVisibleBo bo) {
        SysFormFieldVisible add = BeanUtil.toBean(bo, SysFormFieldVisible.class);

        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改单项显示控制
     */
    @Override
    public Boolean updateByBo(SysFormFieldVisibleBo bo) {
        SysFormFieldVisible update = BeanUtil.toBean(bo, SysFormFieldVisible.class);
        /*update.setUpdateBy(LoginHelper.getUserId());
        update.setUpdateTime(new Date());*/
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysFormFieldVisible entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除单项显示控制
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
