package com.sysware.common.core.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户对象 sys_user
 *
 * @author
 */

@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser extends BaseEntity
{

    /** 用户ID */
    @TableId(value = "user_id",type = IdType.ASSIGN_UUID)
    private String userId;

    /** 部门ID */
    @ExcelProperty(value = "部门编号")
    private String deptId;

    /** 工号 */
    @NotBlank(message = "用户工号不能为空")
    @Size(min = 0, max = 20, message = "工号长度不能超过20个字符")
    @ExcelProperty(value = "登录名称")
    private String loginName;

    /** 姓名 */
    @Size(min = 0, max = 20, message = "姓名长度不能超过20个字符")
    @ExcelProperty(value = "用户名称")
    private String userName;

    /** 用户邮箱 */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    @ExcelProperty(value = "用户邮箱")
    private String email;

    /** 手机号码 */
    @ExcelProperty(value = "电话")
    private String phoneNumber;

    /** 用户性别 */
    @ExcelProperty(value = "用户性别")
    private String sex;

    /** 用户签名文件ID */
    private String signFileId;
    /**
     * 用户签名文件名称
     */
    @TableField(exist = false)
    private String signFileName;
    /**
     * 用户类型 0代表普通用户  1代表系统管理员 2代表安全管理员 3代表审计管理员
     */
    private String userType;

    /** 密码 */
    private String password;

    @JsonIgnore
    @JsonProperty
    public String getPassword() {
        return password;
    }



    /** 帐号状态（0正常 1停用） */
    @ExcelProperty(value = "帐号状态")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    //@TableLogic
    private String delFlag;

    /** 最后登录IP */
    @ExcelProperty(value = "最后登录IP")
    private String loginIp;

    /** 最后登录时间 */
    @ExcelProperty(value = "最后登录时间")
    private Date loginDate;

    /** 备注 */
    private String remark;

    /** 用户IP地址 */
    private String userIpAddress;

    /** 备注 */
    @TableField(exist = false)
    private String projectId;

    /** 部门对象 */
    @TableField(exist = false)
    private SysDept dept;

    /** 角色对象 */
    @TableField(exist = false)
    private List<SysRole> roles;

    /** 角色组 */
    @TableField(exist = false)
    private String[] roleIds;

    /** 岗位组 */
    @TableField(exist = false)
    private String[] postIds;

	/** 角色ID */
	@TableField(exist = false)
	private String roleId;

	/** 角色名称 */
	@TableField(exist = false)
	private String roleName;

    /** 部门名称 */
    @TableField(exist = false)
    private String deptName;

    /** 密级 */
    @ExcelProperty(value = "密级")
    @TableField(exist = false)
    private String securityName;
    
	private String securityId;

    @TableField(exist = false)
    private int securityValue;

    @ExcelProperty(value = "职位")
    private String leaderType;

    public SysUser(String userId)
    {
        this.userId = userId;
    }

    public boolean isAdmin()
    {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(String userId)
    {
        return userId != null && "123".contains(userId);
    }
    public boolean isSecurityAdmin()
    {
        return isSecurityAdmin(this.userId);
    }

    public static boolean isSecurityAdmin(String userId)
    {
        return userId != null && "456".contains(userId);
    }
    public boolean isAuditAdmin()
    {
        return isAuditAdmin(this.userId);
    }

    public static boolean isAuditAdmin(String userId)
    {
        return userId != null && "789".contains(userId);
    }
}
