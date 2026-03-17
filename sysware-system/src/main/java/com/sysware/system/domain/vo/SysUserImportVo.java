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

/**
 * 用户对象导入VO
 *
 * @author
 */

@Data
@NoArgsConstructor
// @Accessors(chain = true) // 导入不允许使用 会找不到set方法
public class SysUserImportVo implements Serializable {
    private static final long serialVersionUID = 1L;



    /**
     * 部门ID
     */
    @ExcelProperty(value = "用户ID")
    private String userId;
    /**
     * 部门ID
     */
    @ExcelProperty(value = "部门", converter = ExcelDeptConvert.class)
    private String deptId;

    /**
     * 用户姓名
     */
    @ExcelProperty(value = "姓名")
    private String userName;

    /**
     * 用户昵称
     */
    @ExcelProperty(value = "工号")
    private String loginName;

    /**
     * 用户电话
     */
    @ExcelProperty(value = "电话")
    private String phonenumber;


    /**
     * 用户性别
     */
    @ExcelProperty(value = "性别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_user_sex")
    private String sex;

    /**
     * 用户密级
     */
    @ExcelProperty(value = "密级", converter = ExcelSecurityConvert.class)
    @ExcelSecurityFormat(securityType = "user")
    private String securityId;

}
