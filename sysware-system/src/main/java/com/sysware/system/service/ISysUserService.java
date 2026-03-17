package com.sysware.system.service;

import com.sysware.common.core.bo.SysUserQueryBo;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysUser;
import com.sysware.common.core.page.TableDataInfo;

import java.util.List;
import java.util.Map;

/**
 * 用户 业务层
 *
 * @author
 */
public interface ISysUserService {


    /**
     * 根据条件分页查询用户
     * @param bo 用户查询对象
     * @return
     */
    TableDataInfo<Map> selectPageUserList(SysUserQueryBo bo, PageQuery pageQuery);

    /**
     * 根据条件查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserList(SysUser user);

    /**
     * 查询所有用户列表
     *
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserList();

    /**
     * 查询所有用户列表（部分字段）
     *
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserPartList();
    /**
     * 根据项目ID查询所有项目团队用户
     *
     *  @param projectId 项目团队ID
     * @return 用户信息集合信息
     */
    public List<SysUser> selectProjectUserList(String projectId);



    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public TableDataInfo<SysUser> selectAllocatedList(SysUser user, PageQuery pageQuery);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public TableDataInfo<SysUser> selectUnallocatedList(SysUser user, PageQuery pageQuery);

    /**
     * 通过用户名查询用户
     *
     * @param loginName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByLoginName(String loginName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(String userId);

    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserRoleGroup(String userName);

    /**
     * 根据用户ID查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserPostGroup(String userName);

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户名称
     * @return 结果
     */
    public boolean checkUserNameUnique(SysUser user);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    public String checkPhoneUnique(SysUser user);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    public String checkEmailUnique(SysUser user);

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    public void checkUserAllowed(SysUser user);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public R insertUser(SysUser user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public R updateUser(SysUser user);

    /**
     * 用户授权角色
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserAuth(String userId, String[] roleIds);

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserStatus(SysUser user);

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserProfile(SysUser user);

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    public boolean updateUserAvatar(String userName, String avatar);

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    public int resetPwd(SysUser user);

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(String userName, String password);

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    public R deleteUserById(String userId);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    public R deleteUserByIds(String[] userIds);

    /**
     * 导入用户数据
     *
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName);

    /**
     * 设置用户密级
     * @param user
     * @return
     */
    int setSecurity(SysUser user);

    /**
     * 根据条件查询用户（用户选择时使用）
     * @param query
     * @return
     */
    List<Map> selectUserForTag(Map query);

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    void checkUserDataScope(String userId);

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean registerUser(SysUser user);

    /**
     *
     * @param bo
     * @return
     */
    List<SysUser> selectUserForCheck(SysUserQueryBo bo);


    /**
     * 为用户设置部门
     * @param deptId
     * @param userIds
     * @return
     */
    boolean insertDeptUsers(String deptId, String[] userIds);

    /**
     * 删除用户部门信息
     * @param userIds
     * @return
     */
    boolean cancelUserDept(String[] userIds);

    /**
     * 设置部门人员职位
     * @param userId
     * @param leaderType
     * @return
     */
    boolean updateLeaderType(String userId, String leaderType);

    /**
     * 获取角色人员列表
     * @param bo
     * @param pageQuery
     * @return
     */
    TableDataInfo selectPageRoleUserList(SysUserQueryBo bo, PageQuery pageQuery);

    /**
     * 获取部门用户列表
     * @param bo
     * @param pageQuery
     * @return
     */
    TableDataInfo selectPageDeptUserList(SysUserQueryBo bo, PageQuery pageQuery);

    /**
     * 多条件获取团队人员列表
     * @param bo
     * @return
     */
    List<SysUser> selectUserForTeam(SysUserQueryBo bo);

    R<SysUser> getCurrentUser();
}
