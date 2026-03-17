package com.sysware.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.annotation.DataColumn;
import com.sysware.common.annotation.DataPermission;
import com.sysware.common.core.domain.entity.SysRole;
import com.sysware.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 角色表 数据层
 *
 * @author
 */
public interface SysRoleMapper extends BaseMapperPlus<SysRoleMapper,SysRole, Map> {

    @DataPermission({ @DataColumn(key = "deptName", value = "d.dept_id")})
    Page<SysRole> selectPageRoleList(@Param("page") Page<SysRole> page, @Param(Constants.WRAPPER) Wrapper<SysRole> queryWrapper);

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<SysRole> selectRolePermissionByUserId(String userId);


    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    public List<Integer> selectRoleListByUserId(String userId);

    /**
     * 根据用户ID获取角色列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    public List<SysRole> selectRolesListByUserId(String userId);

    /**
     * 根据用户ID查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     */
    public List<SysRole> selectRolesByUserName(String userName);

}
