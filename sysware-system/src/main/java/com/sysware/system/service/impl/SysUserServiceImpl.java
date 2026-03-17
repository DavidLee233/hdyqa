package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.annotation.Log;
import com.sysware.common.constant.CacheNames;
import com.sysware.common.constant.UserConstants;
import com.sysware.common.core.bo.SysUserQueryBo;
import com.sysware.common.core.domain.OperationObject;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysDept;
import com.sysware.common.core.domain.entity.SysRole;
import com.sysware.common.core.domain.entity.SysUser;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.service.UserService;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.exception.ServiceException;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.SecurityUtils;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.utils.SyswareUtil;
import com.sysware.common.utils.redis.CacheUtils;
import com.sysware.system.domain.SysPost;
import com.sysware.system.domain.SysUserConfig;
import com.sysware.system.domain.SysUserPost;
import com.sysware.system.domain.SysUserRole;
import com.sysware.system.mapper.*;
import com.sysware.system.service.ISysConfigService;
import com.sysware.system.service.ISysDeptService;
import com.sysware.system.service.ISysTableFieldService;
import com.sysware.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 *
 * @author
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SysUserServiceImpl implements ISysUserService, UserService {


    private final SysUserMapper baseMapper;
    private final SysRoleMapper roleMapper;
    private final SysPostMapper postMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysUserConfigMapper userConfigMapper;
    private final SysUserDeptMapper userDeptMapper;
    private final SysUserPostMapper userPostMapper;
    private final ISysConfigService configService;
    private final ISysTableFieldService tableFieldService;
    private final ISysDeptService deptService;





    @Override
    public TableDataInfo<Map> selectPageUserList(SysUserQueryBo bo, PageQuery pageQuery) {

        Map  params = BeanUtil.beanToMap(bo);

        List<String> fields = new ArrayList<>();
        if(StringUtils.isNotBlank(bo.getSearchField())){
            for (String prop : bo.getSearchField().split(",")) {
                fields.add(prop);
            }
        }
        IPage<Map> result = tableFieldService.getResult(params, pageQuery, baseMapper,fields);

        return TableDataInfo.build(result);
    }

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return baseMapper.selectUserList(user);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public TableDataInfo<SysUser> selectAllocatedList(SysUser user,PageQuery pageQuery) {


        TableDataInfo<SysUser> ingf =  TableDataInfo.build(baseMapper.selectAllocatedList(pageQuery.build(), user));
		return ingf;
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public TableDataInfo<SysUser> selectUnallocatedList(SysUser user,PageQuery pageQuery) {
		return TableDataInfo.build(baseMapper.selectUnallocatedList(pageQuery.build(), user));
    }

    /**
     * 通过用户名查询用户
     *
     * @param loginName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByLoginName(String loginName) {
        return baseMapper.selectUserByLoginName(loginName);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(String userId) {
        return baseMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        StringBuilder idsStr = new StringBuilder();
        for (SysRole role : list) {
            idsStr.append(role.getRoleName()).append(",");
        }
        if (Validator.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        StringBuilder idsStr = new StringBuilder();
        for (SysPost post : list) {
            idsStr.append(post.getPostName()).append(",");
        }
        if (Validator.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 校验用户工号是否唯一
     *
     * @param user 用户名称
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(SysUser user) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getLoginName, user.getLoginName())
                .ne(ObjectUtil.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId()));
        return !exist;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        return "";
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        return "";
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (Validator.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    public R insertUser(SysUser user) {
        // 新增用户信息
        user.setDelFlag("0");
        user.setUserType("0");
        int rows = baseMapper.insert(user);
        // 插入配置表内容
        SysUserConfig userConfig = new SysUserConfig();
        userConfig.setUserId(baseMapper.selectById(user).getUserId());
        // 默认提醒消息
        userConfig.setOpenNotify(1);
        userConfigMapper.insert(userConfig);

        if (user.getUserName().equals("1")){
            throw new ServiceException("部门停用，不允许新增");
        }

        CacheUtils.clear(CacheNames.SYS_USER_LIST);


        return rows > 0 ? R.ok(new SysUser(), saveAfter(user,null,null)) : R.fail(user);
    }

    private OperationObject saveAfter(SysUser user,String objectId,String objectName) {
        OperationObject operObject = new OperationObject();
        if(user != null){
            operObject.setOperObjectId(user.getUserId());
            operObject.setOperObjectName(user.getUserName());
        }else{
            operObject.setOperObjectId(objectId);
            operObject.setOperObjectName(objectName);
        }
        operObject.setOperObjectSecurityValue(0);
        operObject.setOperObjectSecurityName("/");
        operObject.setOperObjectTableName("sys_user");


        return operObject;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    public R updateUser(SysUser user) {
        CacheUtils.clear(CacheNames.SYS_USER_LIST);
        SysUser sysUser = baseMapper.selectUserById(user.getUserId());
        int rows = baseMapper.updateById(user);
        return rows > 0 ? R.ok(sysUser, saveAfter(user,null,null)) : R.fail(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(String userId, String[] roleIds)
    {
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
			.eq(SysUserRole::getUserId, userId));
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        return baseMapper.updateById(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return baseMapper.updateById(user);
    }

    /**
     * 修改用户头像
     *
     * @param loginName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String loginName, String avatar) {
        return true;
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user) {
        SysUser sysUser = baseMapper.selectUserById(user.getUserId());
        sysUser.setPassword(user.getPassword());
        return baseMapper.updateById(sysUser);
    }

    /**
     * 重置用户密码
     *
     * @param loginName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String loginName, String password) {
        return baseMapper.update(null,
                new LambdaUpdateWrapper<SysUser>()
                        .set(SysUser::getPassword,password)
                        .eq(SysUser::getLoginName,loginName));
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        String[] roles = user.getRoleIds();
        if (Validator.isNotNull(roles)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (String roleId : roles) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
				userRoleMapper.insertBatch(list);
            }
        }
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {
        String[] posts = user.getPostIds();
        if (Validator.isNotNull(posts)) {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>();
            for (String postId : posts) {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            if (list.size() > 0) {
				userPostMapper.insertBatch(list);
            }
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(String userId, String[] roleIds) {
        if (Validator.isNotNull(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (String roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                userRoleMapper.insertBatch(list);
            }
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    public R deleteUserById(String userId) {
        // 删除用户与角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId,userId));
       SysUser delUser =  baseMapper.selectUserById(userId);
        int rows = baseMapper.deleteById(userId);

        return rows > 0 ? R.ok(delUser, saveAfter(delUser,null,null)) : R.fail();
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    public R deleteUserByIds(String[] userIds) {
        for (String userId : userIds) {
            checkUserAllowed(new SysUser(userId));
        }
        List<String> ids = Arrays.asList(userIds);
        List<SysUser> sysUsers = baseMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, ids));

        // 删除用户与角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId,ids));
        int rows = baseMapper.deleteBatchIds(ids);

        return rows > 0 ? R.ok(sysUsers, saveAfter(null,null,"批量用户")) : R.fail();
    }

    /**
     * 导入用户数据
     *
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (Validator.isNull(userList) || userList.size() == 0) {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList) {
            try {
                // 验证是否存在这个用户
                SysUser u = baseMapper.selectUserByLoginName(user.getLoginName());
                if (Validator.isNull(u)) {
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    this.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、工号 " + user.getLoginName() + " 导入成功");
                } else if (isUpdateSupport) {
                    user.setUpdateBy(operName);
                    this.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、工号 " + user.getLoginName() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、工号 " + user.getLoginName() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、工号 " + user.getLoginName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    @Override
    public int setSecurity(SysUser user) {
       return baseMapper.updateById(user);
    }

    @Override
    public List<Map> selectUserForTag(Map query) {
        List<Map> maps = baseMapper.selectUserForTag(query);

        return maps;
    }
    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(String userId) {
        if (!LoginHelper.isAdmin()) {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = this.selectUserList(user);
            if (CollUtil.isEmpty(users)) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    @Override
    public boolean registerUser(SysUser user) {
        user.setCreateBy(user.getUserName());
        user.setUpdateBy(user.getUserName());
        return baseMapper.insert(user) > 0;
    }


    @Override
    public List<SysUser> selectUserForCheck(SysUserQueryBo bo) {

        return filterList(selectUserList(), bo);
    }

    private List<SysUser> filterList(List<SysUser> list, SysUserQueryBo bo) {

        List<SysUser> returnList = list;
        //筛选包含当前角色的用户列表
        if(StringUtils.isNotBlank(bo.getRoleId())){
            //list =  list.stream().filter(i -> StringUtils.isNotBlank(i.getRoleId()) && i.getRoleId().contains(bo.getRoleId())).collect(Collectors.toList());
            list =  list.stream().filter(i -> i.getRoles().stream().filter(r -> r.getRoleId().equals(bo.getRoleId())).collect(Collectors.toList()).size()>0).collect(Collectors.toList());

        }
        if(StringUtils.isNotBlank(bo.getRoleKey())){
            list =  list.stream().filter(i -> i.getRoles().stream().filter(r -> r.getRoleKey().equals(bo.getRoleKey())).collect(Collectors.toList()).size()>0).collect(Collectors.toList());

        }
        //筛选是当前部门的用户列表
        if(StringUtils.isNotBlank(bo.getDeptId())){
            list =  list.stream().filter(i -> bo.getDeptId().equals(i.getDeptId())).collect(Collectors.toList());
        }

        //筛选是当前密级的用户列表
        if(StringUtils.isNotBlank(bo.getSecurityId())){
            list =  list.stream().filter(i -> bo.getSecurityId().equals(i.getSecurityId())).collect(Collectors.toList());
        }

        //筛选高于当前密级的用户列表
        if(bo.getSecurityValue() != 0){
            list =  list.stream().filter(i -> i.getSecurityValue() >= bo.getSecurityValue()).collect(Collectors.toList());
        }

        if("eq".equals(bo.getConnector())){
            returnList = list;
        }else if("ne".equals(bo.getConnector())){
            returnList.removeAll(list);
        }

        return returnList;
    }


    @Override
    public boolean insertDeptUsers(String deptId, String[] userIds) {

        List<SysUser> list = new ArrayList<SysUser>();
        for (String userId : userIds) {
            SysUser ur = selectUserById(userId);
            ur.setUserId(userId);
            ur.setDeptId(deptId);
            ur.setLeaderType("0");
            list.add(ur);
        }

        return baseMapper.updateBatchById(list);
    }

    @Override
    public boolean cancelUserDept(String[] userIds) {
        List<SysUser> list = new ArrayList<SysUser>();
        for (String userId : userIds) {
            SysUser ur = new SysUser();
            ur.setUserId(userId);
            ur.setDeptId("");
            ur.setLeaderType("0");
            list.add(ur);
        }
        return baseMapper.updateBatchById(list);
    }

    @Override
    public boolean updateLeaderType(String userId, String leaderType) {
        SysUser ur = new SysUser();
        ur.setUserId(userId);
        ur.setLeaderType(leaderType);
        return baseMapper.updateById(ur) > 0;
    }

    @Override
    public TableDataInfo selectPageRoleUserList(SysUserQueryBo bo, PageQuery pageQuery) {

        IPage<Map> result = tableFieldService.getResult(BeanUtil.beanToMap(bo), pageQuery, userRoleMapper, SyswareUtil.getSearchFieldMap(bo.getSearchField()));

        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo selectPageDeptUserList(SysUserQueryBo bo, PageQuery pageQuery) {


        IPage<Map> result = tableFieldService.getResult(BeanUtil.beanToMap(bo), pageQuery, userDeptMapper, SyswareUtil.getSearchFieldMap(bo.getSearchField()));

        return TableDataInfo.build(result);
    }

    @Override
    public List<SysUser> selectUserForTeam(SysUserQueryBo bo) {
        return filterList(baseMapper.selectProjectUserList(bo.getProjectId()),bo);
    }

    @Override
    public R<SysUser> getCurrentUser() {
        SysUser sysUser = new SysUser();
        LoginUser user = LoginHelper.getLoginUser();
        sysUser.setUserName(user.getUsername());
        sysUser.setUserId(user.getUserId());
        sysUser.setLoginName(user.getLoginName());
        sysUser.setDeptId(user.getDeptId());
        String deptName = deptService.selectDeptById(user.getDeptId()).getDeptName();
        sysUser.setDeptName(deptName);
        sysUser.setSecurityId(user.getSecurityId());
        sysUser.setSecurityName(user.getSecurityName());
        sysUser.setSecurityValue(user.getSecurityValue());
        return R.ok(sysUser);
    }

    @Cacheable(cacheNames = CacheNames.SYS_USER_LIST)
    @Override
    public List<SysUser> selectUserList() {
        List<SysUser> list = baseMapper.selectAllUser();
        return list;
    }

    @Cacheable(cacheNames = CacheNames.SYS_USER_PART)
    @Override
    public List<SysUser> selectUserPartList() {
        List<SysUser> list = baseMapper.selectAllUserPart();
        return list;
    }


    @Cacheable(cacheNames = CacheNames.SYS_USER_LIST, key = "#projectId")
    @Override
    public List<SysUser> selectProjectUserList(String projectId) {
        List<SysUser> list = baseMapper.selectProjectUserList(projectId);
        return list;
    }




    @Cacheable(cacheNames = CacheNames.SYS_USER_NAME, key = "#userId")
    @Override
    public String selectUserNameById(String userId) {
        SysUser sysUser = baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getUserName).eq(SysUser::getUserId, userId));
        return ObjectUtil.isNull(sysUser) ? null : sysUser.getUserName();
    }
}
