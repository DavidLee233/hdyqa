package com.sysware.common.core.domain.model;

import com.sysware.common.core.domain.dto.RoleDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 登录用户身份权限
 *
 * @author
 */

@Data
@NoArgsConstructor
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     *
     */
    private String loginName;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 部门名
     */
    private String deptName;
    /**
     * 密级名
     */
    private String securityName;
    /**
     * 密级Id
     */
    private String securityId;
    /**
     * 密级值
     */
    private int securityValue;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 菜单权限
     */
    private Set<String> menuPermission;

    /**
     * 角色权限
     */
    private Set<String> rolePermission;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色对象
     */
    private List<RoleDTO> roles;

    /**
     * 数据权限 当前角色ID
     */
    private String roleId;

    /**
     * 是否消息提醒
     */
    private Integer openNotify;

    /**
     * 获取登录id
     */
    public String getLoginId() {
        /*if (userType == null) {
            throw new IllegalArgumentException("用户类型不能为空");
        }*/
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userId;
    }

}
