package com.sysware.mainData.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sysware.common.core.domain.BaseEntity;

/**
 * 员工工作信息数据对象 hdl_person_job_info
 *
 * @author aa
 * @date 2026-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_person_job_info")
public class HdlPersonJobInfo extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 员工工作记录主键
     */
    @TableId(value = "pk_psnjob")
    private String pkPsnjob;
    /**
     * 姓名
     */
    private String name;
    /**
     * 证件号码
     */
    private String idNumber;
    /**
     * 出入证号码
     */
    private String inOutNumber;
    /**
     * 实际部门
     */
    private String realPkDept;
    /**
     * key号码
     */
    private String keyNumber;
    /**
     * 员工号
     */
    private String code;
    /**
     * 部门主键
     */
    private String pkDept;
    /**
     * 职务
     */
    private String pkJob;
    /**
     * 开始日期
     */
    private Date beginDate;
    /**
     * 结束日期
     */
    private Date endDate;
    /**
     * 人员类别（详见BIP参照数据）
     */
    private String pkPsncl;
    /**
     * 员工基本信息主键
     */
    private String pkPsndoc;
    /**
     * 涉密级别（详见BIP参照数据）
     */
    private String secretLevel;
    /**
     * 最新记录（N=否，Y=是）
     */
    private String lastFlag;
    /**
     * 是否结束（N=否，Y=是）
     */
    private String endFlag;
    /**
     * 组织
     */
    private String pkOrg;
    /**
     * 是否主职（N=否，Y=是）
     */
    private String isMainJob;
    /**
     * 旧NC人员基本信息主键
     */
    private String oldPkPsnjob;
    /**
     * 其他职务名称
     */
    private String otherJobTitle;
    /**
     * 职级
     */
    private String jobLevel;
    /**
     * 创建者工号
     */
    private String createId;
    /**
     * 更新者工号
     */
    private String updateId;
}
