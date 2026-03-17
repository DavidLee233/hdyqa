package com.sysware.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.core.domain.entity.SysUser;
import com.sysware.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户表 数据层
 *
 * @author
 */
public interface SysUserMapper extends BaseMapperPlus<SysUserMapper,SysUser,Map> {

    /**
     * 根据条件分页查询用户
     * @param page
     * @param ew
     * @return
     */
    Page<List<Map>> selectPageUserList(IPage<Map> page, @Param(Constants.WRAPPER) Wrapper<Object> ew);


    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserList(SysUser sysUser);

    /**
     * 根据条件分页查询未已配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public Page<SysUser> selectAllocatedList(@Param("page") Page<SysUser> page, @Param("user") SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public Page<SysUser> selectUnallocatedList(@Param("page") Page<SysUser> page, @Param("user") SysUser user);

    /**
     * 通过用户名查询用户
     *
     * @param loginName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByLoginName(String loginName);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(String userId);

    /**
     * 根据条件查询用户（用户选择时使用）
      * @param query  查询条件
     * @return
     */
    public List<Map> selectUserForTag(Map query);


    /**
     * 查询所有的用户
     * @return
     */
    List<SysUser> selectAllUser();

    /**
     * 查询所有的用户（部分数据）
     * @return
     */
    List<SysUser> selectAllUserPart();

    /**
     * 根据项目ID获取项目团队成员
     * @param projectId
     * @return
     */
    List<SysUser> selectProjectUserList(@Param("projectId") String projectId);



}
