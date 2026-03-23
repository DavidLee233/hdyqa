package com.sysware.mainData.domain.bo;

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
 * @project npic
 * @description HdlPersonBasicInfoBo业务入参对象，封装员工基本信息主数据查询与变更请求字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HdlPersonBasicInfoBo extends BaseEntity {
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