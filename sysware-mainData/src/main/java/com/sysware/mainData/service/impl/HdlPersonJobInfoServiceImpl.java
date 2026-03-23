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
import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sysware.system.service.ISysTableFieldService;
import com.sysware.mainData.domain.bo.HdlPersonJobInfoBo;
import com.sysware.mainData.domain.vo.HdlPersonJobInfoVo;
import com.sysware.mainData.domain.HdlPersonJobInfo;
import com.sysware.mainData.mapper.HdlPersonJobInfoMapper;
import com.sysware.mainData.service.IHdlPersonJobInfoService;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.SyswareUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.*;

import static com.sysware.mainData.action.personJobInfoAction.buildPersonJobQueryWrapper;
/**
 * @project npic
 * @description HdlPersonJobInfoServiceImpl服务实现类，负责员工工作信息主数据业务规则执行、数据处理与流程编排。
 * @author DavidLee233
 * @date 2026/3/20
 */
@RequiredArgsConstructor
@Service
public class HdlPersonJobInfoServiceImpl implements IHdlPersonJobInfoService {

    private final HdlPersonJobInfoMapper baseMapper;
    private final ISysTableFieldService tableFieldService;
    /**
     * @description 根据主键查询单条员工工作信息主数据详情信息。
     * @params pkPsnjob 员工工作信息主键编码
     *
      * @return HdlPersonJobInfoVo 员工工作信息展示对象，用于前端详情或列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public HdlPersonJobInfoVo queryById(String pkPsnjob){
        return baseMapper.selectVoById(pkPsnjob);
    }
    /**
     * @description 按筛选条件分页查询员工工作信息主数据并封装为表格数据返回。
     * @params bo 员工工作信息主数据分页查询条件业务对象
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public TableDataInfo queryPageList(HdlPersonJobInfoBo bo, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<HdlPersonJobInfo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<HdlPersonJobInfoVo> result = baseMapper.selectVoPage(page, buildPersonJobQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }
    /**
     * @description 将业务对象转换为实体并新增员工工作信息主数据记录。
     * @params bo 员工工作信息主数据新增业务请求对象
     *
      * @return Boolean 业务执行结果标记（true成功，false失败）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Boolean insertByBo(HdlPersonJobInfoBo bo) {
        HdlPersonJobInfo add = BeanUtil.toBean(bo, HdlPersonJobInfo.class);
        LoginUser user = LoginHelper.getLoginUser();
        add.setCreateBy(user.getUsername());
        add.setCreateId(user.getLoginName());
        add.setCreateTime(LocalDateTime.now());
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setPkPsnjob(add.getPkPsnjob());
        }
        return flag;
    }
    /**
     * @description 按业务对象更新员工工作信息主数据记录并校验关键字段。
     * @params bo 员工工作信息主数据编辑业务请求对象
     *
      * @return Boolean 业务执行结果标记（true成功，false失败）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Boolean updateByBo(HdlPersonJobInfoBo bo) {
        HdlPersonJobInfo update = BeanUtil.toBean(bo, HdlPersonJobInfo.class);
        LoginUser user = LoginHelper.getLoginUser();
        update.setUpdateBy(user.getUsername());
        update.setUpdateId(user.getLoginName());
        update.setUpdateTime(LocalDateTime.now());
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }
    /**
     * @description 执行validEntityBeforeSave方法，完成员工工作信息主数据相关业务处理。
     * @params entity 待持久化的实体对象
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private void validEntityBeforeSave(HdlPersonJobInfo entity){
        //TODO 做一些数据校验,如唯一约束
    }
    /**
     * @description 按业务校验规则批量删除员工工作信息主数据记录。
     * @params ids 主键ID集合
     * @params isValid 数据有效标记（0无效、1有效）
     *
      * @return Boolean 业务执行结果标记（true成功，false失败）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
    /**
     * @description 执行selectPJIList方法，完成员工工作信息主数据相关业务处理。
     * @params pji 员工工作信息实体对象
     *
      * @return List<HdlPersonJobInfo> 员工工作信息列表结果，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public List<HdlPersonJobInfo> selectPJIList(HdlPersonJobInfo pji){
        return baseMapper.selectPJIList(pji);
    }
}