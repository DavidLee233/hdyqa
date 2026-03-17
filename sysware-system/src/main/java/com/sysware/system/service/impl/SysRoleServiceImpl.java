package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.constant.UserConstants;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.entity.SysRole;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.enums.RoleKey;
import com.sysware.common.enums.RoleType;
import com.sysware.common.exception.ServiceException;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.utils.SyswareUtil;
import com.sysware.common.utils.spring.SpringUtils;
import com.sysware.system.domain.SysRoleDept;
import com.sysware.system.domain.SysRoleMenu;
import com.sysware.system.domain.SysRolePermission;
import com.sysware.system.domain.SysUserRole;
import com.sysware.system.mapper.*;
import com.sysware.system.service.ISysRoleService;
import com.sysware.system.service.ISysTableFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色 业务层处理
 *
 * @author
 */
@RequiredArgsConstructor
@Service
public class SysRoleServiceImpl  implements ISysRoleService {

    private final SysRoleMenuMapper roleMenuMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysRoleMapper baseMapper;
    private final ISysTableFieldService tableFieldService;


    @Override
    public TableDataInfo<Map> selectPageRoleList(SysRole role, PageQuery pageQuery) {
        IPage<Map> result = tableFieldService.getResult(BeanUtil.beanToMap(role),pageQuery,baseMapper, SyswareUtil.getSearchFieldMap(role.getSearchField()));
        return TableDataInfo.build(result);
    }


    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        return baseMapper.selectList();
    }

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRolesByUserId(String userId) {
        List<SysRole> userRoles = baseMapper.selectRolePermissionByUserId(userId);
        List<SysRole> roles = selectRoleAll();
        for (SysRole role : roles) {
            for (SysRole userRole : userRoles) {
                if (role.getRoleId() == userRole.getRoleId()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * 根据用户ID查询角色（用在问答系统中）
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRolesByUserIdQA(String userId) {
        List<SysRole> userRoles = baseMapper.selectRolesListByUserId(userId);
        return userRoles;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(String userId) {
        List<SysRole> perms = baseMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (Validator.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleAll() {

        return baseMapper.selectList();
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Integer> selectRoleListByUserId(String userId) {
        return baseMapper.selectRoleListByUserId(userId);
    }
    public List<SysRole> selectRolesListByUserId(String userId) {
        return baseMapper.selectRolesListByUserId(userId);
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRole selectRoleById(String roleId) {
        SysRole role =   baseMapper.selectById(roleId);
        return role;
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, role.getRoleName())
                .eq(SysRole::getRoleType, role.getRoleType())
                .ne(ObjectUtil.isNotNull(role.getRoleId()), SysRole::getRoleId, role.getRoleId()));
        return !exist;
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleKey, role.getRoleKey())
                .eq(SysRole::getRoleType, role.getRoleType())
                .ne(ObjectUtil.isNotNull(role.getRoleId()), SysRole::getRoleId, role.getRoleId()));
        return !exist;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRole role) {
        if (ObjectUtil.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(String roleId) {
        return userRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId)).intValue();
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean insertRole(SysRole role) {
        // 新增角色信息
        role.setDelFlag("0");
        //role.setDataScope("2");
        role.setRoleStatus("0");
        role.setMenuCheckStrictly(true);

        int insert = baseMapper.insert(role);

        insertRoleMenu(role);

        insertRolePermission(role);

        return insert > 0;
    }

    /**
     * 插入角色权限信息
     * @param role
     */
    private boolean insertRolePermission(SysRole role) {

        if(StringUtils.isEmpty(role.getPermissionIds())){
            return true;
        }
        List<SysRolePermission> list = new ArrayList<SysRolePermission>();
        for (String menuId : role.getPermissionIds().split(",")) {
            SysRolePermission rm = new SysRolePermission();
            rm.setRoleId(role.getRoleId());
            rm.setPermissionId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            return rolePermissionMapper.insertBatch(list);
        }
        return false;

    }

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean updateRole(SysRole role) {

        // 删除角色与菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, role.getRoleId()));
        insertRoleMenu(role);

        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, role.getRoleId()));
        insertRolePermission(role);


        return baseMapper.updateById(role) > 0;
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public int updateRoleStatus(SysRole role) {
        return baseMapper.updateById(role);
    }

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean authDataScope(SysRole role) {
        // 修改角色信息

        // 删除角色与部门关联
       // roleDeptMapper.delete(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, role.getRoleId()));
        // 新增角色和部门信息（数据权限）
        return  baseMapper.updateById(role) > 0;
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public Boolean insertRoleMenu(SysRole role) {
        if(role.getMenuIds() == null || role.getMenuIds().length == 0){
            return true;
        }
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (String menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            return roleMenuMapper.insertBatch(list);
        }
        return false;
    }


    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteRoleById(String roleId) {
        // 删除角色与菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        // 删除角色与权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
        return baseMapper.deleteById(roleId);
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteRoleByIds(String[] roleIds) {
        for (String roleId : roleIds) {
            checkRoleAllowed(new SysRole(roleId));
            SysRole role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        List<String> ids = Arrays.asList(roleIds);
        // 删除角色与菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, ids));
        // 删除角色与权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().in(SysRolePermission::getRoleId, ids));

        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    @Override
    public int deleteAuthUser(SysUserRole userRole) {
        return userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
			.eq(SysUserRole::getRoleId, userRole.getRoleId())
			.eq(SysUserRole::getUserId, userRole.getUserId()));
    }

    /**
     * 批量取消授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    @Override
    public int deleteAuthUsers(String roleId, String[] userIds) {
		return userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
			.eq(SysUserRole::getRoleId, roleId)
			.in(SysUserRole::getUserId, Arrays.asList(userIds)));
    }

    /**
     * 批量选择授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    @Override
    public boolean insertAuthUsers(String roleId, String[] userIds) {
        // 新增用户与角色管理
        List<SysUserRole> list = new ArrayList<SysUserRole>();
        for (String userId : userIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        return userRoleMapper.insertBatch(list);
    }

    @Override
    public void checkRoleDataScope(String roleId) {
        if (!LoginHelper.isAdmin()) {
            SysRole role = new SysRole();
            role.setRoleId(roleId);
            List<SysRole> roles = this.selectRoleList(role);
            if (CollUtil.isEmpty(roles)) {
                throw new ServiceException("没有权限访问角色数据！");
            }
        }
    }


    @Override
    public List<String> getRolePermission(String roleId) {
        return rolePermissionMapper.selectList(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId)).stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
    }

    @Override
    public Boolean checkUserRoleByKey(String userId, String roleKey, String roleType) {
        List<SysRole> userRoles = baseMapper.selectRolePermissionByUserId(userId);
        for (SysRole userRole : userRoles) {
            if (roleKey.equals(userRole.getRoleKey()) && roleType.equals(userRole.getRoleType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean checkUserRoleById(String userId, String roleId) {
        List<SysRole> userRoles = baseMapper.selectRolePermissionByUserId(userId);
        for (SysRole userRole : userRoles) {
            if (roleId.equals(userRole.getRoleId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isShowAllProject(String userId) {
        return checkUserRoleByKey(userId, RoleKey.SHOW_ALL_PROJECT.getCode(), RoleType.SYSTEM.getCode());
    }

}
