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
 * @project npic
 * @description HdlPersonJobInfo领域实体，描述员工工作信息主数据核心数据结构与属性。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_person_job_info")
public class HdlPersonJobInfo extends BaseEntity {

    private static final long serialVersionUID=1L;
    @TableId(value = "pk_psnjob")
    private String pkPsnjob;
    private String name;
    private String idNumber;
    private String inOutNumber;
    private String realPkDept;
    private String keyNumber;
    private String code;
    private String pkDept;
    private String pkJob;
    private Date beginDate;
    private Date endDate;
    private String pkPsncl;
    private String pkPsndoc;
    private String secretLevel;
    private String lastFlag;
    private String endFlag;
    private String pkOrg;
    private String isMainJob;
    private String oldPkPsnjob;
    private String otherJobTitle;
    private String jobLevel;
    private String createId;
    private String updateId;
}