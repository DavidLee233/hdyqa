package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.annotation.Log;
import com.sysware.common.core.domain.OperationObject;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.exception.ServiceException;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.utils.SyswareUtil;
import com.sysware.system.domain.DocField;
import com.sysware.system.domain.SysFormFieldRules;
import com.sysware.system.domain.SysTableField;
import com.sysware.system.domain.vo.DocFieldVo;
import com.sysware.system.domain.vo.SysFormFieldVo;
import com.sysware.system.mapper.DocFieldMapper;
import com.sysware.system.service.IDocFieldService;
import com.sysware.system.service.ISysTableFieldService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.system.domain.bo.SysFormFieldAddBo;
import com.sysware.system.domain.bo.SysFormFieldQueryBo;
import com.sysware.system.domain.bo.SysFormFieldEditBo;
import com.sysware.system.domain.SysFormField;
import com.sysware.system.mapper.SysFormFieldMapper;
import com.sysware.system.service.ISysFormFieldService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 表单配置Service业务层处理
 *
 * @author zzr
 * @date 2023-04-14
 */
@RequiredArgsConstructor
@Service
public class SysFormFieldServiceImpl implements ISysFormFieldService {

    private final ISysTableFieldService tableFieldService;
    private final SysFormFieldMapper baseMapper;
    private final SysFormFieldRulesServiceImpl fieldRulesService;

    private final IDocFieldService docFieldService;


    @Override
    public SysFormFieldVo queryById(String fieldId){
        return baseMapper.selectVoById(fieldId, SysFormFieldVo.class);
    }

    @Override
    public TableDataInfo queryPageList(SysFormFieldQueryBo bo, PageQuery pageQuery) {

        return TableDataInfo.build(tableFieldService.getResult(BeanUtil.beanToMap(bo),pageQuery,baseMapper,SyswareUtil.getSearchFieldMap(bo.getSearchField())));
    }

    @Override
    public List queryList(SysFormFieldQueryBo bo) {

        List list = baseMapper.selectFormFieldList(buildLambdaQueryWrapper(bo));
        //replaceSearchWords(list,bo);
        return list;
    }

    private QueryWrapper<SysFormField> buildQueryWrapper(SysFormFieldQueryBo bo) {
        QueryWrapper<SysFormField> lqw = Wrappers.query();
        lqw.eq("form_id", bo.getFormId());
        return lqw;
    }

    private LambdaQueryWrapper<SysFormField> buildLambdaQueryWrapper(SysFormFieldQueryBo bo) {
        LambdaQueryWrapper<SysFormField> lqw = Wrappers.lambdaQuery();
        lqw.eq(StrUtil.isNotBlank(bo.getFormId()), SysFormField::getFormId, bo.getFormId());
        return lqw;
    }

    @Override
    @Log(title = "表单字段", businessType = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public R insertByAddBo(SysFormFieldAddBo bo) {
        SysFormField add = BeanUtil.toBean(bo, SysFormField.class);

        beforeSave(add);

        Boolean flag =  baseMapper.insertOrUpdate(add);

        saveRules(add);

        return flag ? R.ok(bo,afterSave(add,null,null)) :R.fail(add);
    }

    private void saveRules(SysFormField entity) {
        SysFormFieldRules sfr = new SysFormFieldRules();
        sfr.setFieldId(entity.getFieldId());
        if("1".equals(entity.getRequired())){
            fieldRulesService.addRequired(sfr);
        }else {
            fieldRulesService.removeRequired(sfr);
        }


        Collection<String> ids = new ArrayList<>();
        ids.add(entity.getFieldId());

        List<DocFieldVo> docFields = docFieldService.selectVoListByType(ids);
        docFields.forEach(docField ->{
            SysFormField target = baseMapper.selectById(docField.getId());
            Long sort = target.getSort();
            BeanUtil.copyProperties(entity,target);
            target.setFieldId(docField.getId());
            target.setFormId(docField.getDocId());
            target.setSort(sort);
            baseMapper.updateById(target);
        });
    }

    @Override
    public Boolean updateByEditBo(SysFormFieldEditBo bo) {
        SysFormField update = BeanUtil.toBean(bo, SysFormField.class);
        beforeSave(update);
        return baseMapper.updateById(update)>0;
    }

    /**
     * 保存前的数据校验
     *
     * @param field 实体类数据
     */
    private void beforeSave(SysFormField field){








        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 保存之后添加一些数据
     *
     * @param entity 实体类数据
     */
    private OperationObject afterSave(SysFormField entity,String objectId,String objectName){


        OperationObject operObject = new OperationObject();
        if(entity != null){
            operObject.setOperObjectId(entity.getFieldId());
            operObject.setOperObjectName(entity.getLabel());
        }else{
            operObject.setOperObjectId(objectId);
            operObject.setOperObjectName(objectName);
        }
        operObject.setOperObjectSecurityValue(0);
        operObject.setOperObjectSecurityName("/");
        operObject.setOperObjectTableName("sys_form_field");

        return operObject;



    }
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid ){
            //TODO 做一些业务上的校验,判断是否需要校验
            List<DocFieldVo> docFields =  docFieldService.selectVoListByType(ids);
            if(docFields.size() > 0){
                throw new ServiceException("【"+docFields.get(0).getLabel()+"】字段至少在类型【"+docFields.get(0).getDocName()+"】中已经被引用,请删除后再试");
            }

        }
        return baseMapper.deleteBatchIds(ids)>0;
    }

    @Override
    public Map<String, Object> getDefaultValue(SysFormFieldQueryBo bo) {

        List<SysFormFieldVo> list = baseMapper.selectVoList(buildLambdaQueryWrapper(bo),SysFormFieldVo.class);
        Map<String,Object> map = new HashMap<String,Object>();
        for (SysFormFieldVo vo : list){
            map.put(vo.getProp(),vo.getDefaultValue());
        }
        return map;
    }

    @Override
    public TableDataInfo<Map> selectFormFieldList(SysFormFieldQueryBo bo,PageQuery pageQuery) {

        Map  params = BeanUtil.beanToMap(bo);

        //List<String> searchFields = tableFieldService.getSearchFields(String.valueOf(params.get("pagePath")));

       /* QueryWrapper tQueryWrapper =  tableFieldService.buildQuery(params);
        Page page = baseMapper.selectFormFieldList(pageQuery.build(), tQueryWrapper);*/
        pageQuery.setOrderByColumn("sort");
        pageQuery.setIsAsc("asc");
        IPage<Map> result = tableFieldService.getResult(params, pageQuery, baseMapper, false,null);

        //StringUtils.highLight(result.getRecords(),searchFields,params);


        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo selectFormFieldByFieldId(String fieldId) {

        List list =  baseMapper.selectFormFieldByFieldId(fieldId);

        return TableDataInfo.build(list);
    }

    @Override
    public boolean copyFormField(SysFormFieldAddBo bo) {
        if(StringUtils.isEmpty(bo.getTargetFormId())){
            throw new ServiceException("请输入目标表单");
        }

        List<SysFormField> targetList = baseMapper.selectFormFieldList(new LambdaQueryWrapper<SysFormField>().eq(SysFormField::getFormId, bo.getTargetFormId()));

        Map<String, String> targetMap = targetList.stream().collect(Collectors.toMap(SysFormField::getProp, SysFormField::getFieldId, (k1, k2) -> k1));
        List<SysFormField> list = new ArrayList<SysFormField>();

        if(StringUtils.isNotEmpty(bo.getFormId())){
            list = baseMapper.selectFormFieldList(new LambdaQueryWrapper<SysFormField>().eq(SysFormField::getFormId, bo.getFormId()));
        }else if(bo.getFieldIds()!=null && bo.getFieldIds().length>0){
            list = baseMapper.selectFormFieldList(new LambdaQueryWrapper<SysFormField>().in(SysFormField::getFieldId, bo.getFieldIds()));
        }

        List<SysFormField> copyList = new ArrayList<>();
        list.forEach(field ->{
            if(StringUtils.isEmpty(targetMap.get(field.getProp()))){
                SysFormField copy = BeanUtil.toBean(field, SysFormField.class);
                copy.setFieldId(UUID.randomUUID().toString());
                copy.setFormId(bo.getTargetFormId());

                SyswareUtil.setCreateAndUpdate(copy);

                copyList.add(copy);
            }
        });
        if(copyList.size()==0){
            return true;
        }

        return  baseMapper.insertBatch(copyList);

    }
}
