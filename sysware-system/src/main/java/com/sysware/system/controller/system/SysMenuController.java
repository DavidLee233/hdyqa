package com.sysware.web.controller.system;

import cn.hutool.core.lang.Validator;
import com.sysware.common.annotation.Log;
import com.sysware.common.constant.UserConstants;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.TreeSelect;
import com.sysware.common.core.domain.entity.SysMenu;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.helper.LoginHelper;
import com.sysware.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单信息
 *
 * @author
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController
{
    @Autowired
    private ISysMenuService menuService;


    /**
     * 获取菜单列表
     */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/list")
    public R<List<SysMenu>> list(SysMenu menu)
    {
        LoginUser loginUser =  LoginHelper.getLoginUser();
        String userId = loginUser.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return R.ok(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = "/{menuId}")
    public R<SysMenu> getInfo(@PathVariable String menuId)
    {
        R<SysMenu> ok = R.ok(menuService.selectMenuById(menuId));
        return ok;
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public R<List<TreeSelect>> treeselect(SysMenu menu)
    {
        LoginUser loginUser = LoginHelper.getLoginUser();
        String userId = loginUser.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return R.ok(menuService.buildMenuTreeSelect(menus));
    }
    /**
     * 获取页面下拉树列表
     */
    @GetMapping("/pageselect")
    public R<List<TreeSelect>> pageselect(SysMenu menu)
    {
        LoginUser loginUser = LoginHelper.getLoginUser();
        String userId = loginUser.getUserId();
        List<SysMenu> menus = menuService.selectPageList(menu, userId);
        return R.ok(menuService.buildMenuTreeSelect(menus));
    }
    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public R<Map<String,Object>> roleMenuTreeselect(@PathVariable("roleId") String roleId)
    {
        LoginUser loginUser = LoginHelper.getLoginUser();
        List<SysMenu> menus = menuService.selectMenuList(loginUser.getUserId());
		Map<String,Object> ajax = new HashMap<>();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return R.ok(ajax);
    }

    /**
     * 新增菜单
     */
    @SaCheckPermission("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysMenu menu)
    {
        if (menuService.checkMenuNameUnique(menu))
        {
            return R.warn("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !Validator.isUrl(menu.getPath()))
        {
            return R.warn("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @SaCheckPermission("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysMenu menu)
    {
        if (menuService.checkMenuNameUnique(menu))
        {
            return R.warn("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !Validator.isUrl(menu.getPath()))
        {
            return R.warn("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            return R.warn("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @SaCheckPermission("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable("menuId") String menuId)
    {
        if (menuService.hasChildByMenuId(menuId))
        {
            return R.warn("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId))
        {
            return R.warn("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }
}
