package com.sysware.system.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.annotation.ExcelSecurityFormat;
import com.sysware.common.convert.ExcelDeptConvert;
import com.sysware.common.convert.ExcelDictConvert;
import com.sysware.common.convert.ExcelSecurityConvert;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户对象导出VO
 *
 * @author
 */

@Data
@NoArgsConstructor
public class SysUserExportVo implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 用户账号
     */
    @ExcelProperty(value = "姓名")
    private String userName;

    /**
     * 用户昵称
     */
    @ExcelProperty(value = "工号")
    private String loginName;

    /**
     * 手机号码
     */
    @ExcelProperty(value = "电话")
    private String phonenumber;

    /**
     * 用户性别
     */
    @ExcelProperty(value = "用户性别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_user_sex")
    private String sex;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ExcelProperty(value = "帐号状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;


    /**
     * 部门名称
     */
    @ExcelProperty(value = "部门")
    private String deptName;

    /**
     * 密级
     */
    @ExcelProperty(value = "密级")
    private String securityName;


    /**
     * 密级
     */
    @ExcelProperty(value = "角色")
    private String roleName;


}
