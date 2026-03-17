package com.sysware.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.convert.ExcelDictConvert;
import com.sysware.common.core.domain.BaseEntity;
import lombok.Data;
import java.util.Date;



/**
 * 系统访问记录视图对象 sys_logininfor
 *
 * @author aa
 * @date 2026-01-25
 */
@Data
@ExcelIgnoreUnannotated
public class SysLogininforVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 访问ID
     */
    @ExcelProperty(value = "访问ID")
    private String infoId;

    /**
     * 用户账号
     */
    @ExcelProperty(value = "用户账号")
    private String userName;

    /**
     * 登录IP地址
     */
    @ExcelProperty(value = "登录IP地址")
    private String ipaddr;

    /**
     * 登录地点
     */
    @ExcelProperty(value = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @ExcelProperty(value = "浏览器类型")
    private String browser;

    /**
     * 操作系统
     */
    @ExcelProperty(value = "操作系统")
    private String os;

    /**
     * 登录状态（0成功 1失败）
     */
    @ExcelProperty(value = "登录状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=成功,1=失败")
    private String status;

    /**
     * 提示消息
     */
    @ExcelProperty(value = "提示消息")
    private String msg;

    /**
     * 访问时间
     */
    @ExcelProperty(value = "访问时间")
    private Date loginTime;


}
