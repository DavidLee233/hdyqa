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
import com.sysware.mainData.domain.bo.HdlPersonBasicInfoBo;
import com.sysware.mainData.domain.vo.HdlPersonBasicInfoVo;
import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.mapper.HdlPersonBasicInfoMapper;
import com.sysware.mainData.service.IHdlPersonBasicInfoService;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.SyswareUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.*;

import static com.sysware.mainData.action.personBasicInfoAction.buildPersonBasicQueryWrapper;

/**
 * 员工基本信息数据Service业务层处理
 *
 * @author aa
 * @date 2026-01-15
 */
@RequiredArgsConstructor
@Service
public class HdlPersonBasicInfoServiceImpl implements IHdlPersonBasicInfoService {

    private final HdlPersonBasicInfoMapper baseMapper;
    private final ISysTableFieldService tableFieldService;

    /**
     * 查询员工基本信息数据
     */
    @Override
    public HdlPersonBasicInfoVo queryById(String pkPsndoc){
        return baseMapper.selectVoById(pkPsndoc);
    }

    /**
     * 查询员工基本信息数据列表
     */
    @Override
    public TableDataInfo queryPageList(HdlPersonBasicInfoBo bo, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<HdlPersonBasicInfo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<HdlPersonBasicInfoVo> result = baseMapper.selectVoPage(page, buildPersonBasicQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }


    /**
     * 新增员工基本信息数据
     */
    @Override
    public Boolean insertByBo(HdlPersonBasicInfoBo bo) {
        HdlPersonBasicInfo add = BeanUtil.toBean(bo, HdlPersonBasicInfo.class);
        LoginUser user = LoginHelper.getLoginUser();
        add.setCreateBy(user.getUsername());
        add.setCreateId(user.getLoginName());
        add.setCreateTime(LocalDateTime.now());
        add.setEnableState("2");
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setPkPsndoc(add.getPkPsndoc());
        }
        return flag;
    }

    /**
     * 修改员工基本信息数据
     */
    @Override
    public Boolean updateByBo(HdlPersonBasicInfoBo bo) {
        HdlPersonBasicInfo update = BeanUtil.toBean(bo, HdlPersonBasicInfo.class);
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
    private void validEntityBeforeSave(HdlPersonBasicInfo entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除员工基本信息数据
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 查询导出数据
     */
    public List<HdlPersonBasicInfo> selectPBIList(HdlPersonBasicInfo pbi){
        return baseMapper.selectPBIList(pbi);
    }
}
