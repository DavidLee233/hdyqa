package com.sysware.web.controller.system;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import com.sysware.common.annotation.Log;
import com.sysware.common.constant.CacheConstants;
import com.sysware.common.constant.UserConstants;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysRole;
import com.sysware.common.core.domain.entity.SysUser;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.system.domain.SysUserRole;
import com.sysware.system.service.ISysRoleService;
import com.sysware.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 角色信息
 *
 * @author
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController
{
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysUserService userService;

    @SaCheckPermission("system:role:list")
    @GetMapping("/list")
    public TableDataInfo list(SysRole role, PageQuery pageQuery)
    {
        return roleService.selectPageRoleList(role,pageQuery);
    }

    /**
     * 导出角色信息列表
     */
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:role:export")
    @PostMapping("/export")
    public void export(SysRole role, HttpServletResponse response) {
        List<SysRole> list = roleService.selectRoleList(role);
        ExcelUtil.exportExcel(list, "角色数据", SysRole.class, response);
    }

    /**
     * 根据角色编号获取详细信息
     */
    @SaCheckPermission("system:role:query")
    @GetMapping(value = "/{roleId}")
    public  R<SysRole> getInfo(@PathVariable String roleId)
    {
        return R.ok(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @SaCheckPermission("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public  R<Void> add(@Validated @RequestBody SysRole role)
    {
        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        return toAjax(roleService.insertRole(role));

    }

    /**
     * 修改保存角色
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public  R<Void> edit(@Validated @RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }

        if (roleService.updateRole(role)) {
            List<String> keys = StpUtil.searchTokenValue("", 0, -1, false);
            if (CollUtil.isEmpty(keys)) {
                return R.ok();
            }
            // 角色关联的在线用户量过大会导致redis阻塞卡顿 谨慎操作
            keys.parallelStream().forEach(key -> {
                String token = key.replace(CacheConstants.LOGIN_TOKEN_KEY, "");
                // 如果已经过期则跳过
                if (StpUtil.stpLogic.getTokenActivityTimeoutByToken(token) < -1) {
                    return;
                }
                LoginUser loginUser = LoginHelper.getLoginUser(token);
                if (loginUser.getRoles().stream().anyMatch(r -> r.getRoleId().equals(role.getRoleId()))) {
                    try {
                       //StpUtil.logoutByTokenValue(token);
                    } catch (NotLoginException ignored) {
                    }
                }
            });
            return R.ok();
        }
        return R.fail("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 修改保存数据权限
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public R<Void> dataScope(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        return toAjax(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        //role.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
    @SaCheckPermission("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public R<Void> remove(@PathVariable String[] roleIds)
    {
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    @SaCheckPermission("system:role:query")
    @GetMapping("/optionselect")
    public R<List<SysRole>> optionselect()
    {
        return R.ok(roleService.selectRoleAll());
    }

    /**
     * 查询已分配用户角色列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo allocatedList(SysUser user, PageQuery pageQuery)
    {
		return userService.selectAllocatedList(user,pageQuery);
    }

    /**
     * 查询未分配用户角色列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo unallocatedList(SysUser user, PageQuery pageQuery)
    {
        return userService.selectUnallocatedList(user,pageQuery);
    }

    /**
     * 取消授权用户
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public R<Void> cancelAuthUser(@RequestBody SysUserRole userRole)
    {
        return toAjax(roleService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public R<Void> cancelAuthUserAll(String roleId, String userIds)
    {
        return toAjax(roleService.deleteAuthUsers(roleId, userIds.split(",")));
    }

    /**
     * 批量选择用户授权
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public R<Void> selectAuthUserAll(String roleId, String[] userIds)
    {
        return toAjax(roleService.insertAuthUsers(roleId, userIds));
    }

    /**
     * 获取角色权限信息
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @GetMapping("/permission/{roleId}")
    public R<List<String>> getRolePermissionIds(@PathVariable String roleId)
    {
        List<String> rolePermission = roleService.getRolePermission(roleId);
        return R.ok(rolePermission);
    }
}
