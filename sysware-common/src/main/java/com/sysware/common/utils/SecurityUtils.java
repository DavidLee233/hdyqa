package com.sysware.common.utils;

import cn.hutool.http.HttpStatus;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.exception.ServiceException;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;

/**
 * 安全服务工具类
 * 
 * @author
 */
public class SecurityUtils
{
    /**
     * 获取用户账户
     **/
    public static String getUsername()
    {
        try
        {
            return getLoginUser().getUsername();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户账户异常", HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser()
    {
        try
        {
            return null;//(LoginUser) getAuthentication().getPrincipal();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户信息异常", HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    /**
     * 获取Authentication
     */
    public static Neo4jProperties.Authentication getAuthentication()
    {
        return null;
        //return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password)
    {
        /*BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);*/
        return "";
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
       /* BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);*/
        return false;
    }

    /**
     * 是否为管理员
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(String userId)
    {
        boolean equals = "1".equals(userId);

        return userId != null && equals;
    }
}
