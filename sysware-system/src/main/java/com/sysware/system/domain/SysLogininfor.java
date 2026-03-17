package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author
 */

@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_logininfor")
public class SysLogininfor implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value= "登录序号")
    @TableId(value = "info_id", type = IdType.ASSIGN_UUID)
    private String infoId;

    /**
     * 用户账号
     */
    @ExcelProperty(value = "用户账号")
    private String userName;

    /**
     * 登录状态 0成功 1失败
     */
    @ExcelProperty(value = "登录状态")
    @ExcelDictFormat(readConverterExp = "0=成功,1=失败")
    private String status;

    /**
     * 登录IP地址
     */
    @ExcelProperty(value = "登录地址")
    private String ipaddr;

    /**
     * 登录地点
     */
    @ExcelProperty(value = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @ExcelProperty(value = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @ExcelProperty(value = "操作系统")
    private String os;

    /**
     * 提示消息
     */
    @ExcelProperty(value = "提示消息")
    private String msg;

    /**
     * 访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "访问时间")
    private Date loginTime;

    /**
     * 请求参数
     */
//    @TableField(exist = false)
//    private Map<String, Object> params = new HashMap<>();

}
