package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.sysware.common.core.domain.entity.SysDictData;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.common.utils.SyswareUtil;
import com.sysware.system.service.ISysDictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sysware.system.service.ISysTableFieldService;
import com.sysware.system.domain.bo.SysFormFieldRulesBo;
import com.sysware.system.domain.vo.SysFormFieldRulesVo;
import com.sysware.system.domain.SysFormFieldRules;
import com.sysware.system.mapper.SysFormFieldRulesMapper;
import com.sysware.system.service.ISysFormFieldRulesService;

import java.util.*;

/**
 * 校验规则Service业务层处理
 *
 * @author aa
 * @date 2023-06-03
 */
@RequiredArgsConstructor
@Service
public class SysFormFieldRulesServiceImpl implements ISysFormFieldRulesService {

    private final SysFormFieldRulesMapper baseMapper;
    private final ISysTableFieldService tableFieldService;
    private final ISysDictDataService dictDataService;

    /**
     * 查询校验规则
     */
    @Override
    public SysFormFieldRulesVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询校验规则列表
     */
    @Override
    public TableDataInfo queryPageList(SysFormFieldRulesBo bo, PageQuery pageQuery) {

        pageQuery.setOrderByColumn("sort");
        pageQuery.setIsAsc("asc");

        List<String> fields = new ArrayList<>();
        fields.add("fieldId");


        return TableDataInfo.build(tableFieldService.getResult(BeanUtil.beanToMap(bo), pageQuery,baseMapper,fields));


    }

    /**
     * 查询校验规则列表
     */
    @Override
    public List<SysFormFieldRulesVo> queryList(SysFormFieldRulesBo bo) {
        LambdaQueryWrapper<SysFormFieldRules> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysFormFieldRules> buildQueryWrapper(SysFormFieldRulesBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysFormFieldRules> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getFieldId()), SysFormFieldRules::getFieldId, bo.getFieldId());
        lqw.eq(StringUtils.isNotBlank(bo.getVerifyType()), SysFormFieldRules::getVerifyType, bo.getVerifyType());
        lqw.eq(StringUtils.isNotBlank(bo.getMessage()), SysFormFieldRules::getMessage, bo.getMessage());
        lqw.like(StringUtils.isNotBlank(bo.getRuleName()), SysFormFieldRules::getRuleName, bo.getRuleName());
        lqw.like(StringUtils.isNotBlank(bo.getCheckName()), SysFormFieldRules::getCheckName, bo.getCheckName());
        lqw.eq(StringUtils.isNotBlank(bo.getRegText()), SysFormFieldRules::getRegText, bo.getRegText());
        lqw.like(StringUtils.isNotBlank(bo.getTableName()), SysFormFieldRules::getTableName, bo.getTableName());
        lqw.like(StringUtils.isNotBlank(bo.getValidateIdName()), SysFormFieldRules::getValidateIdName, bo.getValidateIdName());
        lqw.eq(bo.getMax() != null, SysFormFieldRules::getMax, bo.getMax());
        lqw.eq(bo.getMin() != null, SysFormFieldRules::getMin, bo.getMin());
        lqw.eq(bo.getSort() != null, SysFormFieldRules::getSort, bo.getSort());
        return lqw;
    }

    /**
     * 新增校验规则
     */
    @Override
    public Boolean insertByBo(SysFormFieldRulesBo bo) {
        SysFormFieldRules add = BeanUtil.toBean(bo, SysFormFieldRules.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insertOrUpdate(add);
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改校验规则
     */
    @Override
    public Boolean updateByBo(SysFormFieldRulesBo bo) {
        SysFormFieldRules update = BeanUtil.toBean(bo, SysFormFieldRules.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysFormFieldRules entity){

        if(StringUtils.isNotBlank(entity.getTableName())){
            entity.setValidateIdName(StringUtils.toCamelCase(TableInfoHelper.getTableInfo(entity.getTableName()).getKeyColumn()));
        }

        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除校验规则
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Boolean addRequired(SysFormFieldRules sfr) {
        SysFormFieldRules rule = getRequirdRules(sfr);
        if(rule ==  null ){
            return baseMapper.insert(sfr) > 0;
        }
        return false;
    }

    @Override
    public Boolean removeRequired(SysFormFieldRules sfr) {
        SysFormFieldRules rule = getRequirdRules(sfr);
        if(rule !=  null ){
            return baseMapper.deleteById(rule) > 0;
        }
        return false;
    }

    /**
     * 获取必填规则
     * @return
     */
    private SysFormFieldRules getRequirdRules(SysFormFieldRules sfr){
        SysDictData sd =  dictDataService.selectDictData("sys_form_rule_type","required");
        sfr.setVerifyType(sd.getDictCode());
        sfr.setVerifyTypeLabel(sd.getDictLabel());
        SysFormFieldRules rule = baseMapper.selectOne(new LambdaQueryWrapper<SysFormFieldRules>()
                .eq(SysFormFieldRules::getFieldId, sfr.getFieldId())
                .eq(SysFormFieldRules::getVerifyType, sfr.getVerifyType()));
        return rule;
    }
}
