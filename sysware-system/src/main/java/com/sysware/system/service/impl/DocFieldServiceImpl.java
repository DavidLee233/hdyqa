package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sysware.common.annotation.Log;
import com.sysware.common.constant.Constants;
import com.sysware.common.core.domain.OperationObject;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysDictData;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.system.domain.DocType;
import com.sysware.system.domain.SysFormField;
import com.sysware.system.domain.SysTableField;
import com.sysware.system.domain.bo.SysFormFieldAddBo;
import com.sysware.system.mapper.DocTypeMapper;
import com.sysware.system.mapper.SysFormFieldMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sysware.system.service.ISysTableFieldService;
import com.sysware.system.domain.bo.DocFieldBo;
import com.sysware.system.domain.vo.DocFieldVo;
import com.sysware.system.domain.DocField;
import com.sysware.system.mapper.DocFieldMapper;
import com.sysware.system.service.IDocFieldService;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.SyswareUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.*;

/**
 * 文档著录字段Service业务层处理
 *
 * @author aa
 * @date 2023-09-02
 */
@RequiredArgsConstructor
@Service
public class DocFieldServiceImpl implements IDocFieldService {

    private final DocFieldMapper baseMapper;
    private final ISysTableFieldService tableFieldService;
    private final SysFormFieldMapper formFieldMapper;
    private final DocTypeMapper docTypeMapper;



    /**
     * 查询文档著录字段
     */
    @Override
    public DocFieldVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询文档著录字段列表
     */
    @Override
    public TableDataInfo queryPageList(DocFieldBo bo, PageQuery pageQuery) {


        IPage<Map> result =  tableFieldService.getResult(BeanUtil.beanToMap(bo),pageQuery, baseMapper,SyswareUtil.getSearchFieldMap(bo.getSearchField()));


        return TableDataInfo.build(result);

    }


    /**
     * 新增文档著录字段
     */
    @Override
    @Log(title = "文档著录字段", businessType = BusinessType.INSERT)
    public R insertByBo(List<DocFieldBo> list) {
        List<DocField> docs = new ArrayList<>();
        list.forEach((bo) ->{
            DocField add = BeanUtil.toBean(bo, DocField.class);
            add.setCode(StringUtils.toCamelCase(add.getCode()));

            if(validEntityBeforeSave(add)){
                docs.add(add);
                saveFormField(add);
            }
        });
        boolean flag = baseMapper.insertBatch(docs);
        String docName = "批量导入";
        if(list.size() > 0 ){
            docName = list.get(0).getDocName();
        }
        OperationObject object = setOperationObject(docName);

        return flag ? R.ok(list,object) : R.fail(list,object);
    }

    private OperationObject setOperationObject(String operName) {


        OperationObject object = new OperationObject();
        object.setOperObjectTableName("hdy_doc_field");
        object.setOperObjectName(operName);
        object.setOperObjectSecurityName("/");



        return  object;
    }

    /**
     *
     * @param field
     * @return
     */
    private void saveFormField(DocField field){
        SysFormField sysFormField = formFieldMapper.selectOne(new LambdaQueryWrapper<SysFormField>().
                eq(SysFormField::getFormId, Constants.FIELD_TYPE).eq(SysFormField::getProp,field.getCode()));

        SysFormField add = new SysFormField();

        BeanUtil.copyProperties(sysFormField,add);
        add.setFieldId(field.getId());
        add.setFormId(field.getDocId());

        formFieldMapper.insertOrUpdate(add);

        field.setId(add.getFieldId());
        field.setType(sysFormField.getFieldId());
    }

    /**
     * 修改文档著录字段
     */
    @Override
    @Log(title = "文档著录字段", businessType = BusinessType.UPDATE)
    public R updateByBo(DocFieldBo bo) {

        DocField preData = baseMapper.selectById(bo.getId());
        DocField update = BeanUtil.toBean(bo, DocField.class);
        //validEntityBeforeSave(update);

        OperationObject object = setOperationObject(bo.getDocName());
        int rows = baseMapper.updateById(update);


        return rows > 0 ? R.ok(preData,object) : R.fail(preData,object);
    }

    /**
     * 保存前的数据校验
     */
    private Boolean validEntityBeforeSave(DocField field){
        //TODO 做一些数据校验,如唯一约束
        LambdaQueryWrapper<DocField> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocField::getDocId, field.getDocId()).eq(DocField::getCode,field.getCode());

        return  baseMapper.selectVoList(wrapper).size() == 0;

    }

    /**
     * 批量删除文档著录字段
     */
    @Override
    @Log(title = "文档著录字段", businessType = BusinessType.DELETE)
    public R deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        formFieldMapper.deleteBatchIds(ids);
        List<DocField> list = baseMapper.selectList(new LambdaQueryWrapper<DocField>().in(DocField::getId,ids));
        int rows = baseMapper.deleteBatchIds(ids);

        String docName = "批量删除";
        if(list.size() > 0 ){
            docName = list.get(0).getDocName();
        }

        OperationObject object = setOperationObject(docName);

        return rows > 0 ? R.ok(list,object) : R.fail(list,object);
    }

    @Override
    public List<Map> queryImportFieldList(DocFieldBo bo) {
        Map<String, Object> params = BeanUtil.beanToMap(bo);
        List<String> searchFields = new ArrayList<>();
        tableFieldService.getSearchFields(bo.getPath(),searchFields,null);
        QueryWrapper<Object> qw = tableFieldService.buildQuery(params, null);
        String sqlSegment = qw.getSqlSegment();
        List<Map> resultList = baseMapper.selectImportFieldList(qw,bo.getDocId());

        SyswareUtil.highLight(resultList,searchFields,BeanUtil.beanToMap(bo));
        return resultList;
    }

    @Override
    public List<DocFieldVo> selectVoListByType(Collection<String> fieldId) {

        return baseMapper.selectVoList(new LambdaQueryWrapper<DocField>().in(DocField::getType,fieldId));
    }
}
