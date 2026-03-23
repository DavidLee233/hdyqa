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
 * @description HdlPersonBasicInfo领域实体，描述员工基本信息主数据核心数据结构与属性。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_person_basic_info")
public class HdlPersonBasicInfo extends BaseEntity {

    private static final long serialVersionUID=1L;
    @TableId(value = "pk_psndoc")
    private String pkPsndoc;
    private String code;
    private String registeredResidence;
    private String familyAddress;
    private String office;
    private String inOutNumber;
    private String secretLevel;
    private String name;
    private String idType;
    private String sex;
    private String idNumber;
    private Date birthdate;
    private String nativePlace;
    private String nationality;
    private String polity;
    private String pkEdu;
    private String pkDegree;
    private Date joinWorkDate;
    private String mobile;
    private String officePhone;
    private String postalCode;
    private String pkOrg;
    private Date joinPolityDate;
    private String professionalLevel;
    private String technicalLevel;
    private String graduationSchool;
    private Date joinCompanyDate;
    private String professionalQualification;
    private Date graduationDate;
    private String enableState;
    private String oldPkPsndoc;
    private String createId;
    private String updateId;
}