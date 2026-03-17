package com.sysware.web.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import com.sysware.common.annotation.Log;
import com.sysware.common.core.bo.SysUserQueryBo;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysRole;
import com.sysware.common.core.domain.entity.SysUser;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.excel.ExcelResult;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.BeanMapUtil;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.system.domain.vo.SysUserExportVo;
import com.sysware.system.domain.vo.SysUserImportVo;
import com.sysware.system.listener.SysUserImportListener;
import com.sysware.system.service.ISysPostService;
import com.sysware.system.service.ISysRoleService;
import com.sysware.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    /**
     * 获取用户列表
     * @SaCheckPermission("system:user:list")
     * @param bo 查询条件
     * @param pageQuery 分页信息
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(SysUserQueryBo bo, PageQuery pageQuery)
    {
        return userService.selectPageUserList(bo,pageQuery);
    }

    /**
     * 获取当前角色下的用户列表
     *
     */
    @GetMapping("/listByRole")
    public TableDataInfo listByRoleId(SysUserQueryBo bo, PageQuery pageQuery)
    {
        return userService.selectPageRoleUserList(bo,pageQuery);
    }

    /**
     * 获取当前部门下的用户列表
     *
     */
    @GetMapping("/listByDept")
    public TableDataInfo listByDeptId(SysUserQueryBo bo, PageQuery pageQuery)
    {
        return userService.selectPageDeptUserList(bo,pageQuery);
    }


    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:user:export")
    @PostMapping("/export")
    public void  export(SysUser user, HttpServletResponse response)
    {
        List<SysUser> list = userService.selectUserList(user);
        List<SysUserExportVo> listVo = BeanUtil.copyToList(list, SysUserExportVo.class);
        ExcelUtil.exportExcel(listVo, "用户列表", SysUserExportVo.class, response);
    }

    /**
     * 导入数据
     *
     * @param file          导入文件
     * @param updateSupport 是否更新已存在数据
     */
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @SaCheckPermission("system:user:import")
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestPart("file") MultipartFile file, boolean updateSupport) throws Exception {
        ExcelResult<SysUserImportVo> result = ExcelUtil.importExcel(file.getInputStream(), SysUserImportVo.class, new SysUserImportListener(updateSupport));
        return R.ok(result.getAnalysis());
    }

    /**
     * 获取导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "用户数据", SysUserImportVo.class, response);
    }

    /**
     * 导入数据
     *
     * @param file          导入文件
     */
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @SaCheckPermission("system:user:signUpload")
    @PostMapping(value = "/signUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> signUpload(@RequestPart("file") MultipartFile file, String userId) throws Exception {

        return R.ok();
    }

    /**
     * 根据用户编号获取详细信息
     *
     * @param userId 用户ID
     */
    @SaCheckPermission("system:user:query")
    @GetMapping(value = {"/", "/{userId}"})
    public R getInfo(@PathVariable(value = "userId", required = false) String userId) {
        //userService.checkUserDataScope(userId);
        //Map<String, Object> ajax = new HashMap<>();
        //List<SysRole> roles = roleService.selectRoleAll();
        //ajax.put("roles", LoginHelper.isAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isAdmin()));
        //ajax.put("posts", postService.selectPostAll());
        /*if (ObjectUtil.isNotNull(userId)) {
            SysUser sysUser = userService.selectUserById(userId);
            ajax.put("user", sysUser);
            //ajax.put("postIds", postService.selectPostListByUserId(userId));
           // ajax.put("roleIds", StreamUtils.toList(sysUser.getRoles(), SysRole::getRoleId));
        }*/
        return R.ok(userService.selectUserById(userId));
    }

    /**
     * 新增用户
     */
    @SaCheckPermission("system:user:add")
    @PostMapping
    public R add(@Validated @RequestBody SysUser user) {
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        return userService.insertUser(user);
    }

    /**
     * 修改用户
     */
    @SaCheckPermission("system:user:edit")
    @PutMapping
    public R edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        return userService.updateUser(user);
    }

    /**
     * 删除用户
     *
     * @param userIds 角色ID串
     */
    @SaCheckPermission("system:user:remove")
    @DeleteMapping("/{userIds}")
    public R remove(@PathVariable String[] userIds) {
        if (ArrayUtil.contains(userIds, getUserId())) {
            return R.fail("当前用户不能删除");
        }
        return userService.deleteUserByIds(userIds);
    }

    /**
     * 重置密码
     */
    @SaCheckPermission("system:user:resetPwd")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 设置密级
     */
    @SaCheckPermission("system:user:setSecurity")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/setSecurity")
    public R<Void> setSecurity(@RequestBody SysUser user)
    {
        SysUser u =  userService.selectUserById(user.getUserId());
        userService.checkUserAllowed(user);
        u.setSecurityId(user.getSecurityId());
        u.setUpdateBy(LoginHelper.getUsername());
        return toAjax(userService.setSecurity(u));
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        user.setUpdateBy(LoginHelper.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/authRole/{userId}")
    public R<Map<String, Object>> authRole(@PathVariable("userId") String userId)
    {
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
		Map<String, Object> ajax = new HashMap<>();
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return R.ok(ajax);
    }

    /**
     * 用户授权角色
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public R<Void> insertAuthRole(String userId, String[] roleIds)
    {
        userService.insertUserAuth(userId, roleIds);
        return R.ok();
    }


    /**
     * 为用户下拉标签获取用户
     * @param bo
     * @return
     * @throws Exception
     */
    @GetMapping("/selectUserForTag")
    public R<List<Map>> selectUserForTag(SysUserQueryBo bo) throws Exception {

        return R.ok(userService.selectUserForTag(BeanMapUtil.beanToMap(bo)));
    }

    /**
     * 多条件获取系统用户（下拉框）
     *
     */
    @GetMapping("/check")
    public R selectUserForSelect(SysUserQueryBo bo)
    {
        List<SysUser> sysUsers = userService.selectUserForCheck(bo);
        return  R.ok(sysUsers);
    }

    /**
     *
     * 多条件获取团队用户（下拉框）
     *
     */
    @GetMapping("/team")
    public R selectUserForTeam(SysUserQueryBo bo)
    {
        List<SysUser> sysUsers = userService.selectUserForTeam(bo);
        return  R.ok(sysUsers);
    }


    /**
     * 设置用户部门
     *
     */
    @PutMapping("/updateUserDept")
    public R<Void> updateUserDept(String deptId,String[] userIds)
    {



        return toAjax(userService.insertDeptUsers(deptId, userIds));
    }

    /**
     * 取消用户部门
     *
     */
    @PutMapping("/cancelUserDept")
    public R<Void> cancelUserDept(String[] userIds)
    {
        return toAjax(userService.cancelUserDept(userIds));
    }

    /**
     * 更新用户职位 0 普通部门人员 1 部门副主任 2 部门主任
     *
     */
    @PutMapping("/updateLeaderType")
    public R<Void> updateLeaderType(String userId,String leaderType)
    {
        return toAjax(userService.updateLeaderType(userId, leaderType));
    }

}
