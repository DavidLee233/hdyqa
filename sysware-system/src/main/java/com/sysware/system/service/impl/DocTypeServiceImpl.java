package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sysware.system.service.ISysTableFieldService;
import com.sysware.system.domain.bo.DocTypeBo;
import com.sysware.system.domain.vo.DocTypeVo;
import com.sysware.system.domain.DocType;
import com.sysware.system.mapper.DocTypeMapper;
import com.sysware.system.service.IDocTypeService;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.SyswareUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.*;

/**
 * 文件类型Service业务层处理
 *
 * @author aa
 * @date 2023-09-02
 */
@RequiredArgsConstructor
@Service
public class DocTypeServiceImpl implements IDocTypeService {

    private final DocTypeMapper baseMapper;
    private final ISysTableFieldService tableFieldService;

    /**
     * 查询文件类型
     */
    @Override
    public DocTypeVo queryById(String createBy){
        return baseMapper.selectVoById(createBy);
    }

    /**
     * 查询文件类型列表
     */
    @Override
    public TableDataInfo queryPageList(DocTypeBo bo, PageQuery pageQuery) {

        beforeQuery(bo);

        return TableDataInfo.build(tableFieldService.getResult(BeanUtil.beanToMap(bo), pageQuery, baseMapper,SyswareUtil.getSearchFieldMap(bo.getSearchField())));

    }

    /**
     * 查询前做条件组装
     * @param bo
     */
    private void beforeQuery(DocTypeBo bo) {


    }

    @Override
    public List<DocTypeVo> queryList(DocTypeBo bo) {
        return null;
    }

    /**
     * 新增文件类型
     */
    @Override
    public Boolean insertByBo(DocTypeBo bo) {
        DocType add = BeanUtil.toBean(bo, DocType.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setCreateBy(add.getCreateBy());
        }
        return flag;
    }

    /**
     * 修改文件类型
     */
    @Override
    public Boolean updateByBo(DocTypeBo bo) {
        DocType update = BeanUtil.toBean(bo, DocType.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DocType entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除文件类型
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }


}
