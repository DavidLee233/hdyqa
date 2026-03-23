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
 * @project npic
 * @description HdlMainDataMappingServiceImpl服务实现类，负责主数据映射业务规则执行、数据处理与流程编排。
 * @author DavidLee233
 * @date 2026/3/20
 */
@RequiredArgsConstructor
@Service
public class HdlMainDataMappingServiceImpl implements IHdlMainDataMappingService {

    private final HdlMainDataMappingMapper baseMapper;
    private final ISysTableFieldService tableFieldService;
    /**
     * @description 根据主键查询单条主数据映射详情信息。
     * @params mapId 主数据映射主键ID
     *
      * @return HdlMainDataMappingVo 主数据映射展示对象，用于前端详情或列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public HdlMainDataMappingVo queryById(Long mapId){
        return baseMapper.selectVoById(mapId);
    }
    /**
     * @description 按筛选条件分页查询主数据映射并封装为表格数据返回。
     * @params bo 主数据映射分页查询条件业务对象
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public TableDataInfo queryPageList(HdlMainDataMappingBo bo, PageQuery pageQuery) {
        if (bo != null && StringUtils.isNotBlank(bo.getType())) {
            bo.setType(resolveStorageType(bo.getType()));
        }
        // 使用传入的pageQuery构建分页参数
        Page<HdlMainDataMapping> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<HdlMainDataMappingVo> result = baseMapper.selectVoPage(page, buildMappingQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }
    /**
     * @description 将业务对象转换为实体并新增主数据映射记录。
     * @params bo 主数据映射新增业务请求对象
     *
      * @return Boolean 业务执行结果标记（true成功，false失败）。
     * @author DavidLee233
     * @date 2026/3/20
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
     * @description 按业务对象更新主数据映射记录并校验关键字段。
     * @params bo 主数据映射编辑业务请求对象
     *
      * @return Boolean 业务执行结果标记（true成功，false失败）。
     * @author DavidLee233
     * @date 2026/3/20
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
     * @description 执行validEntityBeforeSave方法，完成主数据映射相关业务处理。
     * @params entity 待持久化的实体对象
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private void validEntityBeforeSave(HdlMainDataMapping entity){
        //TODO 做一些数据校验,如唯一约束
    }
    /**
     * @description 按业务校验规则批量删除主数据映射记录。
     * @params ids 主键ID集合
     * @params isValid 数据有效标记（0无效、1有效）
     *
      * @return Boolean 业务执行结果标记（true成功，false失败）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
    /**
     * @description 按类型加载主数据字段映射列表供远端转换使用。
     * @params type 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     *
      * @return List<HdlMainDataMappingVo> 主数据映射列表结果，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public List<HdlMainDataMappingVo> selectMainDataMappingByType(String type) {
        return baseMapper.selectMainDataMappingByType(resolveStorageType(type));
    }
    /**
     * @description 解析并转换主数据映射相关输入，生成可直接使用的数据结构。
     * @params requestedType 请求指定的主数据类型编码
     *
      * @return String 解析后的业务字符串结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private String resolveStorageType(String requestedType) {
        if (StringUtils.isBlank(requestedType)) {
            return requestedType;
        }
        if (!isLegacyTypeSchema()) {
            return requestedType;
        }
        if ("1".equals(requestedType)) {
            return "0";
        }
        if ("2".equals(requestedType)) {
            return "1";
        }
        if ("3".equals(requestedType)) {
            return "2";
        }
        return requestedType;
    }

    /**
     * @description 判断主数据映射业务状态是否满足预设条件。
     * @params 无
     *
      * @return boolean 状态判定结果（true表示满足条件，false表示不满足）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private boolean isLegacyTypeSchema() {
        Long type0Count = baseMapper.selectCount(
            Wrappers.<HdlMainDataMapping>lambdaQuery().eq(HdlMainDataMapping::getType, "0"));
        Long type3Count = baseMapper.selectCount(
            Wrappers.<HdlMainDataMapping>lambdaQuery().eq(HdlMainDataMapping::getType, "3"));
        return type0Count != null && type0Count > 0 && (type3Count == null || type3Count == 0);
    }
}