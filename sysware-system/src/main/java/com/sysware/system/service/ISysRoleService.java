package com.sysware.system.service;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.entity.SysRole;
import com.sysware.system.domain.SysUserRole;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色业务层
 *
 * @author
 */
public interface ISysRoleService{


    TableDataInfo<Map> selectPageRoleList(SysRole role, PageQuery pageQuery);

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<SysRole> selectRolesByUserId(String userId);

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectRolePermissionByUserId(String userId);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    public List<SysRole> selectRoleAll();

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
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    public SysRole selectRoleById(String roleId);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    public boolean checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    public boolean checkRoleKeyUnique(SysRole role);

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    public void checkRoleAllowed(SysRole role);

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public int countUserRoleByRoleId(String roleId);

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public boolean insertRole(SysRole role);

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public boolean updateRole(SysRole role);

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    public int updateRoleStatus(SysRole role);

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public boolean authDataScope(SysRole role);

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public int deleteRoleById(String roleId);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    public int deleteRoleByIds(String[] roleIds);

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    public int deleteAuthUser(SysUserRole userRole);

    /**
     * 批量取消授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    public int deleteAuthUsers(String roleId, String[] userIds);

    /**
     * 批量选择授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    public boolean insertAuthUsers(String roleId, String[] userIds);

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    void checkRoleDataScope(String roleId);

    public List<String> getRolePermission(String roleId);

    /**
     * 校验用户是否拥有角色
     * @param userId 用户ID
     * @param roleKey 角色编号
     * @param roleType 角色类型
     * @return
     */
    public Boolean checkUserRoleByKey(String userId, String roleKey, String roleType);

    /**
     * 校验用户是否拥有角色
     * @param userId 用户ID
     * @param roleId 角色编号
     * @return
     */
    public Boolean checkUserRoleById(String userId, String roleId);

    /**
     * 是否能查看所有项目
     * @param userId 用户ID
     * @return
     */
    public Boolean isShowAllProject(String userId);

    List<SysRole> selectRolesByUserIdQA(String userId);
}
