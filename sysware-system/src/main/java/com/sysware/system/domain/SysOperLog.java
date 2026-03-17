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
 * 操作日志记录表 oper_log
 *
 * @author
 */

@Data
@Accessors(chain = true)
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 日志主键
     */
    @ExcelProperty(value= "操作序号")
    @TableId(value = "oper_id", type = IdType.ASSIGN_UUID)
    private String operId;

    /**
     * 操作模块
     */
    @ExcelProperty(value= "操作模块")
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @ExcelProperty(value= "业务类型")
    @ExcelDictFormat(readConverterExp = "0=其它,1=新增,2=修改,3=删除")
    private Integer businessType;

    /**
     * 业务类型数组
     */
//    @TableField(exist = false)
//    private Integer[] businessTypes;

    /**
     * 请求方法
     */
    @ExcelProperty(value= "请求方法")
    private String method;

    /**
     * 请求方式
     */
    @ExcelProperty(value= "请求方式")
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @ExcelProperty(value= "操作类别")
    @ExcelDictFormat(readConverterExp = "0=其它,1=后台用户,2=手机端用户")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @ExcelProperty(value= "操作人员")
    private String operName;

    /**
     * 部门名称
     */
    @ExcelProperty(value= "部门名称")
    private String deptName;

    /**
     * 请求url
     */
    @ExcelProperty(value= "请求地址")
    private String operUrl;

    /**
     * 操作地址
     */
    @ExcelProperty(value= "操作地址")
    private String operIp;

    /**
     * 操作地点
     */
    @ExcelProperty(value= "操作地点")
    private String operLocation;

    /**
     * 请求参数
     */
    @ExcelProperty(value= "请求参数")
    private String operParam;

    /**
     * 返回参数
     */
    @ExcelProperty(value= "返回参数")
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    @ExcelProperty(value= "状态")
    @ExcelDictFormat(readConverterExp = "0=正常,1=异常")
    private Integer status;

    /**
     * 错误消息
     */
    @ExcelProperty(value= "错误消息")
    private String errorMsg;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value= "操作时间")
    private Date operTime;

    /**
     * 请求参数
     */
//    @TableField(exist = false)
//    private Map<String, Object> params = new HashMap<>();

    /**
     * 操作人员ID
     */
    private String operUserId;

    /**
     * 操作人员密级ID
     */
    private String operUserSecurityId;

    /**
     * 操作人员密级名称
     */
    @ExcelProperty(value= "操作人员密级名称")
    private String operUserSecurityName;

    /**
     * 操作人员密级值
     */
    private Integer operUserSecurityValue;

    /**
     * 操作对象ID
     */
    private String operObjectId;

    /**
     * 操作对象名称
     */
    @ExcelProperty(value= "操作对象名称")
    private String operObjectName;
    /**
     * 操作对象密级名称
     */
    @ExcelProperty(value= "操作对象密级名称")
    private String operObjectSecurityName;

    /**
     * 操作对象密级ID
     */
    private String operObjectSecurityId;

    /**
     * 操作对象密级值
     */
    private Integer operObjectSecurityValue;

    /**
     * 操作对象表名称
     */
    @TableField(exist = false)
    private String operObjectTableName;

}
