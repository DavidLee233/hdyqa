package com.sysware.common.core.domain.event;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志事件
 *
 * @author
 */

@Data
public class OperLogEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志主键
     */
    private String operId;

    /**
     * 操作模块
     */
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    private Integer businessType;

    /**
     * 业务类型数组
     */
    private Integer[] businessTypes;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    private Integer operatorType;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 请求url
     */
    private String operUrl;

    /**
     * 操作地址
     */
    private String operIp;

    /**
     * 操作地点
     */
    private String operLocation;

    /**
     * 请求参数
     */
    private String operParam;

    /**
     * 返回参数
     */
    private Object jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    private Date operTime;

    /**
     * 操作人员ID
     */
    private String operUserId;

    /**
     * 操作人员密级ID
     */
    private String operUserSecurityId;
    /**
     * 操作人员密级值
     */
    private Integer operUserSecurityValue;

    /**
     * 操作人员密级名称
     */
    private String operUserSecurityName;

    /**
     * 操作对象ID
     */
    private String operObjectId;

    /**
     * 操作对象名称
     */
    private String operObjectName;

    /**
     * 操作对象密级名称
     */
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
    private String operObjectTableName;

}
