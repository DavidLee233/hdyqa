package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.sysware.common.constant.CacheNames;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.entity.SysDictData;
import com.sysware.common.core.domain.entity.SysDictType;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.utils.SyswareUtil;
import com.sysware.common.utils.redis.CacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.system.domain.bo.SysTableFieldAddBo;
import com.sysware.system.domain.bo.SysTableFieldQueryBo;
import com.sysware.system.domain.bo.SysTableFieldEditBo;
import com.sysware.system.domain.SysTableField;
import com.sysware.system.mapper.SysTableFieldMapper;
import com.sysware.system.domain.vo.SysTableFieldVo;
import com.sysware.system.service.ISysTableFieldService;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 自定义页面显示Service业务层处理
 *
 * @author aa
 * @date 2022-08-03
 */
@RequiredArgsConstructor
@Service
public class SysTableFieldServiceImpl  implements ISysTableFieldService {

    private final SysTableFieldMapper baseMapper;

    public  SysTableFieldVo queryById(String id){
        SysTableFieldVo sysTableFieldVo = baseMapper.selectVoById(id, SysTableFieldVo.class);
        return sysTableFieldVo;
    }

    @Override
    public TableDataInfo<SysTableFieldVo> queryPageList(SysTableFieldQueryBo bo, PageQuery pageQuery) {

        //IPage<Map> result1 = getResult(BeanUtil.beanToMap(bo), pageQuery, baseMapper);

        IPage<SysTableFieldVo> result = baseMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo),SysTableFieldVo.class);

        return TableDataInfo.build(result);
    }

    @Override
    public List<SysTableFieldVo> queryList(SysTableFieldQueryBo bo) {

        List<SysTableFieldVo> list = baseMapper.selectVoList(buildQueryWrapper(bo),SysTableFieldVo.class);
        return list;
    }

    private LambdaQueryWrapper<SysTableField> buildQueryWrapper(SysTableFieldQueryBo bo) {
        Map params = bo.getParams();
        LambdaQueryWrapper<SysTableField> lqw = Wrappers.lambdaQuery();
        //lqw.eq(StrUtil.isNotBlank(bo.getUserId()), SysTableField::getUserid, bo.getUserid());
        //lqw.eq(StrUtil.isNotBlank(bo.getMenuid()), SysTableField::getMenuid, bo.getMenuid());
        //lqw.like(StrUtil.isNotBlank(bo.getPagename()), SysTableField::getPagename, bo.getPagename());
        lqw.eq(StrUtil.isNotBlank(bo.getType()), SysTableField::getType, bo.getType());
        lqw.eq(bo.getWidth() != null, SysTableField::getWidth, bo.getWidth());
        lqw.eq(StrUtil.isNotBlank(bo.getAlign()), SysTableField::getAlign, bo.getAlign());
        lqw.eq(StrUtil.isNotBlank(bo.getLabel()), SysTableField::getLabel, bo.getLabel());
        lqw.eq(StrUtil.isNotBlank(bo.getProp()), SysTableField::getProp, bo.getProp());
        lqw.eq(bo.getVisible() != null, SysTableField::getVisible, bo.getVisible());
        lqw.eq(StrUtil.isNotBlank(bo.getSortable()), SysTableField::getSortable, bo.getSortable());
        //lqw.eq(bo.getDateselect() != null, SysTableField::getDateselect, bo.getDateselect());
        lqw.eq(bo.getSearchable() != null,  SysTableField::getSearchable , bo.getSearchable());
        lqw.eq(bo.getPath()!= null,  SysTableField::getPath , bo.getPath());
        lqw.eq(bo.getHighLight()!= null,  SysTableField::getHighLight , bo.getHighLight());
        lqw.eq(bo.getOnlyShow() != null,SysTableField::getOnlyShow,bo.getOnlyShow());
        lqw.like(StrUtil.isNotBlank(bo.getProjectType()), SysTableField::getProjectType, bo.getProjectType());
        lqw.orderBy(true,true,SysTableField::getSort);
        return lqw;
    }

    @Override
    public Boolean insertByAddBo(SysTableFieldAddBo bo) {
        SysTableField add = BeanUtil.toBean(bo, SysTableField.class);

        validEntityBeforeSave(add);
        afterSave(add);
        return baseMapper.insert(add) > 0l;


    }

    @Override
    public Boolean updateByEditBo(SysTableFieldEditBo bo) {
        SysTableField update = BeanUtil.toBean(bo, SysTableField.class);
        validEntityBeforeSave(update);
        afterSave(update);
        return baseMapper.updateById(update) > 0l;
    }

    /**
     * 更新后操作
     * @param tableField
     */
    private void afterSave(SysTableField tableField) {


        /**
         * 先删除该字段的高亮显示字段
         */
        baseMapper.delete(new LambdaQueryWrapper<SysTableField>()
                .eq(SysTableField::getProp,tableField.getProp()+"Show")
                .eq(SysTableField::getLabel,tableField.getLabel())
                .eq(SysTableField::getPageName,tableField.getPageName())
                .eq(SysTableField::getOnlyShow,"1"));

        /**
         * 如果字段被设置了高亮显示并且不是只显示的字段，则自动添加高亮显示字段
         */
        if("1".equals(tableField.getHighLight())&&!"1".equals(tableField.getOnlyShow())){
            SysTableField onlyShow = BeanUtil.toBean(tableField, SysTableField.class);
            onlyShow.setId(UUID.randomUUID().toString());
            onlyShow.setOnlyShow("1");//设置为仅显示
            onlyShow.setSearchable("1");//设置为能搜索
            onlyShow.setVisible("0");
            onlyShow.setHighLight("0");
            onlyShow.setProp(tableField.getProp()+"Show");
            baseMapper.insert(onlyShow);

            tableField.setVisible("1");//将自己设置为隐藏
            tableField.setSearchable("1");//将自己设置为可搜索
            tableField.setOnlyShow("0");//将自己去掉仅显示
        }
    }

    /**
     * 保存前的数据校验
     *
     * @param entity 实体类数据
     */
    private void validEntityBeforeSave(SysTableField entity){




        //TODO 做一些数据校验,如唯一约束
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public void getSearchFields(String path, List<String> searchFieldList, List<SysTableFieldVo> dictList) {

        SysTableFieldQueryBo tableBo = new SysTableFieldQueryBo();
        tableBo.setPath(path);
        //tableBo.setSearchable("1");
        //tableBo.setHighLight("1");
        //tableBo.setOnlyShow("0");
        List<SysTableFieldVo> fields = queryList(tableBo);

        List<String>  searchFieldsArr = new ArrayList<String>();
        fields.forEach(f ->{
            if("1".equals(f.getSearchable()) && "0".equals(f.getOnlyShow())){
                searchFieldList.add(f.getProp());
            }
            if("dict".equals(f.getType())){
                dictList.add(f);
            }

        });
    }

    @Override
    public QueryWrapper<Object> buildQuery(Map<String, Object> params,List<String> searchField) {
        String path = String.valueOf(params.get("path"));
        QueryWrapper query = Wrappers.query();
        query.eq("path",path);
        query.eq("searchable","1");
        query.eq("only_show","0");

        List<Map> fields = baseMapper.selectMaps(query);

        SyswareUtil.listToCamelCase(fields);
        String searchValue = String.valueOf(params.get("searchValue"));
        StringBuffer sb = new StringBuffer("");

        QueryWrapper<Object> qw = Wrappers.query();

        Map<String,Object> likes = new HashMap<String,Object>();
        Map<String,Object> eqs = new HashMap<String,Object>();

        if(StringUtils.isNotBlank(searchValue)){
            searchValue = searchValue.trim();

            String[] searchArr = searchValue.split(" ");
            for (int j = 0; j < searchArr.length; j++){
                String search = searchArr[j];
                for (int i = 0; i < fields.size(); i++){
                    Map<String, Object> stf = fields.get(i);
                    String prop = StringUtils.toUnderScoreCase(String.valueOf(stf.get("prop")));
                    String type = String.valueOf(stf.get("type"));
                    if("num".equals(type) &&  NumberUtil.isInteger(search)){
                        eqs.put(prop,search);
                    }else if("date".equals(type)){
                        likes.put(prop,search);
                    }else{
                        if(prop.endsWith("id") || prop.endsWith("Id")){
                            eqs.put(prop,search);
                        }else{
                            likes.put(prop,search);
                        }

                    }

                }

                if(likes.size()+eqs.size()>0){
                    qw.and(i ->{
                        likes.forEach((k,v) ->{
                            i.like(k,v).or();
                        });
                        eqs.forEach((k,v) ->{
                            i.eq(k, v).or();
                        });
                    });
                }
            }

        }



        Map<String,Object> likes1 = new HashMap<String,Object>();
        Map<String,Object> eqs1 = new HashMap<String,Object>();
        Map<String,Object> bt1 = new HashMap<String,Object>();
        Map<String,Object> in1 = new HashMap<String,Object>();

        if(searchField != null && searchField.size()>0){
            searchField.forEach(field ->{
                eqs1.put(StringUtils.toUnderScoreCase(String.valueOf(field)),String.valueOf(params.get(field)));
               /* if(String.valueOf(field).endsWith("id") || String.valueOf(field).endsWith("Id")){
                    eqs1.put(StringUtils.toUnderScoreCase(String.valueOf(field)),String.valueOf(params.get(field)));
                }else{
                    likes1.put(StringUtils.toUnderScoreCase(String.valueOf(field)),String.valueOf(params.get(field)));
                }*/

            });
        }


        for (int i = 0; i < fields.size(); i++){
            Map<String, Object> stf = fields.get(i);
            String prop =  String.valueOf(stf.get("prop"));
            Object obj = params.get(prop+"Show")==null?params.get(prop):params.get(prop+"Show");
            if(obj instanceof ArrayList ){
                if(((ArrayList<?>) obj).size()>0){
                    in1.put(StringUtils.toUnderScoreCase(prop),obj);
                }
            }else{
                String value = String.valueOf(params.get(prop+"Show")==null?params.get(prop):params.get(prop+"Show"));
                prop = StringUtils.toUnderScoreCase(prop);
                String type = String.valueOf(stf.get("type"));

                if(StringUtils.isNotBlank(value)){
                    if("num".equals(type) &&  NumberUtil.isInteger(value)){
                        eqs1.put(prop,value);
                    }else if("date".equals(type) && value.startsWith("[")&&value.endsWith("]")&&value.contains(",")){
                        bt1.put(prop,params.get(prop));
                    } else{
                        if(prop.endsWith("id") || prop.endsWith("Id")){
                            eqs1.put(prop,value);
                        }else{
                            likes1.put(prop,value);
                        }

                    }

                }
            }


        }



        if(likes1.size()+eqs1.size()+bt1.size()+in1.size()>0){
            qw.and(i->{
                likes1.forEach((k,v) ->{
                    i.like(k, v);
                });
                eqs1.forEach((k,v) ->{
                    i.eq(k, v);
                });
                bt1.forEach((k,v) ->{
                    i.between(k, ((ArrayList)v).get(0),((ArrayList)v).get(1));
                });
                in1.forEach((k,v) ->{
                    i.in(k, (ArrayList)v);
                });
            });
        }

        return qw;
    }

    @Override
    public TableDataInfo<SysTableFieldVo> queryFieldlistByPath(SysTableFieldQueryBo bo,PageQuery pageQuery) {
        LambdaQueryWrapper<SysTableField> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPath()!= null,SysTableField::getPath, bo.getPath());
        lqw.orderBy(true,true,SysTableField::getSort);

        IPage<SysTableFieldVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw,SysTableFieldVo.class);

        return TableDataInfo.build(result);
    }



    @Override
    public IPage<Map> getResult(Map params, PageQuery pageQuery, BaseMapper mapper,Boolean highLight,List<String> searchField) {

        QueryWrapper tQueryWrapper =  buildQuery(params,searchField);
        String sqlSegment = tQueryWrapper.getSqlSegment();
        List<String> searchFields = new ArrayList<>();
        List<SysTableFieldVo> dictList = new ArrayList<>();
        getSearchFields(String.valueOf(params.get("path")),searchFields,dictList);
        if(pageQuery == null){
            pageQuery = new PageQuery();
            pageQuery.setPageSize(Integer.MAX_VALUE);
            pageQuery.setPageNum(1);
        }

        IPage<Map> result = mapper.selectMapsPage(pageQuery.build(), tQueryWrapper);
        List<Map> newRecords = SyswareUtil.listToCamelCase(result.getRecords());

        if(highLight){
            SyswareUtil.highLight(newRecords,searchFields,params);
        }

        //字典格式化
        dictFormat(newRecords,dictList);

        result.setRecords(newRecords);

        return result;
    }

    private void dictFormat(List<Map> newRecords, List<SysTableFieldVo> dictList) {

        for (SysTableFieldVo dict : dictList) {
            String dictType = dict.getDictType();
            String prop = dict.getProp();
            newRecords.forEach(record -> {
                Object value = record.get(prop);
                if(value!= null){
                    ArrayList<SysDictData> list = (ArrayList<SysDictData>)CacheUtils.get(CacheNames.SYS_DICT, dictType);
                    if(list!=null){
                        list.forEach(dictItem -> {
                            if(dictItem.getDictValue().equals(value)) {
                                record.put(prop+"Label", dictItem.getDictLabel());
                            }
                        });
                    }
                }
            });

        }
    }


    @Override
    public IPage<Map> getResult(Map params, PageQuery pageQuery, BaseMapper mapper,List<String> searchField) {
        return getResult(params,pageQuery,mapper,true,searchField);
    }

    @Override
    public IPage<Map> getResult(Map params, PageQuery pageQuery, BaseMapper mapper) {
        return getResult(params,pageQuery,mapper,true,null);
    }
}
