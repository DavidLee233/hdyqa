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
 * 操作日志记录视图对象 sys_oper_log
 *
 * @author aa
 * @date 2026-01-23
 */
@Data
@ExcelIgnoreUnannotated
public class SysOperLogVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 日志主键
     */
    @ExcelProperty(value = "日志主键")
    private String operId;

    /**
     * 模块标题
     */
    @ExcelProperty(value = "模块标题")
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @ExcelProperty(value = "业务类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=其它,1=新增,2=修改,3=删除")
    private Integer businessType;

    /**
     * 方法名称
     */
    @ExcelProperty(value = "方法名称")
    private String method;

    /**
     * 请求方式
     */
    @ExcelProperty(value = "请求方式")
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @ExcelProperty(value = "操作类别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=其它,1=后台用户,2=手机端用户")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @ExcelProperty(value = "操作人员")
    private String operName;

    /**
     * 部门名称
     */
    @ExcelProperty(value = "部门名称")
    private String deptName;

    /**
     * 请求URL
     */
    @ExcelProperty(value = "请求URL")
    private String operUrl;

    /**
     * 主机地址
     */
    @ExcelProperty(value = "主机地址")
    private String operIp;

    /**
     * 操作地点
     */
    @ExcelProperty(value = "操作地点")
    private String operLocation;

    /**
     * 请求参数
     */
    @ExcelProperty(value = "请求参数")
    private String operParam;

    /**
     * 返回参数
     */
    @ExcelProperty(value = "返回参数")
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    @ExcelProperty(value = "操作状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=正常,1=异常")
    private Integer status;

    /**
     * 错误消息
     */
    @ExcelProperty(value = "错误消息")
    private String errorMsg;

    /**
     * 操作时间
     */
    @ExcelProperty(value = "操作时间")
    private Date operTime;

    /**
     * 操作人ID
     */
    @ExcelProperty(value = "操作人ID")
    private String operUserId;

    /**
     * 操作人员密级ID
     */
    @ExcelProperty(value = "操作人员密级ID")
    private String operUserSecurityId;

    /**
     * 操作人员密级名称
     */
    @ExcelProperty(value = "操作人员密级名称")
    private String operUserSecurityName;

    /**
     * 操作对象密级名称
     */
    @ExcelProperty(value = "操作对象密级名称")
    private String operObjectSecurityName;

    /**
     * 操作对象密级ID
     */
    @ExcelProperty(value = "操作对象密级ID")
    private String operObjectSecurityId;

    /**
     * 操作对象ID
     */
    @ExcelProperty(value = "操作对象ID")
    private String operObjectId;

    /**
     * 操作对象名称
     */
    @ExcelProperty(value = "操作对象名称")
    private String operObjectName;

    /**
     * 操作人员密级值
     */
    @ExcelProperty(value = "操作人员密级值")
    private Long operUserSecurityValue;

    /**
     * 操作对象密级值
     */
    @ExcelProperty(value = "操作对象密级值")
    private Long operObjectSecurityValue;


}
