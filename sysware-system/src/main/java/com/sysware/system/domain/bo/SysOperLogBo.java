package com.sysware.system.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sysware.common.core.validate.AddGroup;
import com.sysware.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sysware.common.core.domain.BaseEntity;

/**
 * 操作日志记录业务对象 sys_oper_log
 *
 * @author aa
 * @date 2023-07-05
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SysOperLogBo extends BaseEntity {


    /**
     * 日志主键
     */
    private String operId;
    /**
     * 模块标题
     */
    @NotBlank(message = "模块标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @NotNull(message = "业务类型（0其它 1新增 2修改 3删除）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long businessType;

    /**
     * 业务类型数组
     */
//    private Integer[] businessTypes;
    /**
     * 方法名称
     */
    @NotBlank(message = "方法名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String method;

    /**
     * 请求方式
     */
    @NotBlank(message = "请求方式不能为空", groups = { AddGroup.class, EditGroup.class })
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    //@NotNull(message = "操作类别（0其它 1后台用户 2手机端用户）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long operatorType;

    /**
     * 操作人员
     */
    @NotBlank(message = "操作人员不能为空", groups = { AddGroup.class, EditGroup.class })
    private String operName;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deptName;

    /**
     * 请求URL
     */
    @NotBlank(message = "请求URL不能为空", groups = { AddGroup.class, EditGroup.class })
    private String operUrl;

    /**
     * 主机地址
     */
    @NotBlank(message = "主机地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String operIp;

    /**
     * 操作地点
     */
    @NotBlank(message = "操作地点不能为空", groups = { AddGroup.class, EditGroup.class })
    private String operLocation;

    /**
     * 请求参数
     */
    @NotBlank(message = "请求参数不能为空", groups = { AddGroup.class, EditGroup.class })
    private String operParam;

    /**
     * 返回参数
     */
    //@NotBlank(message = "返回参数不能为空", groups = { AddGroup.class, EditGroup.class })
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    @NotNull(message = "操作状态（0正常 1异常）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;

    /**
     * 错误消息
     */
    //@NotBlank(message = "错误消息不能为空", groups = { AddGroup.class, EditGroup.class })
    private String errorMsg;

    /**
     * 操作时间
     */
    @NotNull(message = "操作时间不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

    // 添加时间范围查询字段（不需要在数据库表中存在）
    private String beginTime;

    private String endTime;

}