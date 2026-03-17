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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sysware.system.service.ISysTableFieldService;
import com.sysware.mainData.domain.bo.HdlOrganizationDepartmentBo;
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.mapper.HdlOrganizationDepartmentMapper;
import com.sysware.mainData.service.IHdlOrganizationDepartmentService;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.SyswareUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.*;

import static com.sysware.mainData.action.organizationDepartmentAction.buildOrgQueryWrapper;

/**
 * 主数据系统Service业务层处理
 *
 * @author aa
 * @date 2026-01-14
 */
@RequiredArgsConstructor
@Service
public class HdlOrganizationDepartmentServiceImpl implements IHdlOrganizationDepartmentService {

    private final HdlOrganizationDepartmentMapper baseMapper;
    private final ISysTableFieldService tableFieldService;

    /**
     * 查询主数据系统
     */
    @Override
    public HdlOrganizationDepartmentVo queryById(String pkDept){
        return baseMapper.selectVoById(pkDept);
    }

    /**
     * 查询主数据系统列表
     */
    @Override
    public TableDataInfo queryPageList(HdlOrganizationDepartmentBo bo, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<HdlOrganizationDepartment> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<HdlOrganizationDepartmentVo> result = baseMapper.selectVoPage(page, buildOrgQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }

    /**
     * 新增主数据系统
     */
    @Override
    public Boolean insertByBo(HdlOrganizationDepartmentBo bo) {
        HdlOrganizationDepartment add = BeanUtil.toBean(bo, HdlOrganizationDepartment.class);
        LoginUser user = LoginHelper.getLoginUser();
        add.setCreateBy(user.getUsername());
        add.setCreateId(user.getLoginName());
        add.setCreateTime(LocalDateTime.now());
        add.setEnableState("2");
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setPkDept(add.getPkDept());
        }
        return flag;
    }

    /**
     * 修改主数据系统
     */
    @Override
    public Boolean updateByBo(HdlOrganizationDepartmentBo bo) {
        HdlOrganizationDepartment update = BeanUtil.toBean(bo, HdlOrganizationDepartment.class);
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
    private void validEntityBeforeSave(HdlOrganizationDepartment entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除主数据系统
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
    public List<HdlOrganizationDepartment> selectOrgDeptList(HdlOrganizationDepartment orgDept){
        return baseMapper.selectOrgDeptList(orgDept);
    }
}
