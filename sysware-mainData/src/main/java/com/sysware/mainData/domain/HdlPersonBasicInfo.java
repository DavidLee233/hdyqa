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
 * 员工基本信息数据对象 hdl_person_basic_info
 *
 * @author aa
 * @date 2026-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hdl_person_basic_info")
public class HdlPersonBasicInfo extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 员工基本信息主键
     */
    @TableId(value = "pk_psndoc")
    private String pkPsndoc;
    /**
     * 员工号
     */
    private String code;
    /**
     * 户籍
     */
    private String registeredResidence;
    /**
     * 家庭地址
     */
    private String familyAddress;
    /**
     * 办公地点
     */
    private String office;
    /**
     * 出入证件号
     */
    private String inOutNumber;
    /**
     * 涉密级别（详见BIP参照数据）
     */
    private String secretLevel;
    /**
     * 姓名
     */
    private String name;
    /**
     * 证件类型（详见BIP参照数据）
     */
    private String idType;
    /**
     * 性别（0=未知、1=男、2=女）
     */
    private String sex;
    /**
     * 证件号码
     */
    private String idNumber;
    /**
     * 出生日期
     */
    private Date birthdate;
    /**
     * 籍贯
     */
    private String nativePlace;
    /**
     * 民族（详见BIP参照数据）
     */
    private String nationality;
    /**
     * 政治面貌（详见BIP参照数据）
     */
    private String polity;
    /**
     * 学历（详见BIP参照数据）
     */
    private String pkEdu;
    /**
     * 学位（详见BIP参照数据）
     */
    private String pkDegree;
    /**
     * 参加工作时间
     */
    private Date joinWorkDate;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 办公室电话
     */
    private String officePhone;
    /**
     * 专业
     */
    private String postalCode;
    /**
     * 所属部门
     */
    private String pkOrg;
    /**
     * 入党（团）时间
     */
    private Date joinPolityDate;
    /**
     * 专业技术级别（详见BIP参照数据）
     */
    private String professionalLevel;
    /**
     * 职业技术级别（详见BIP参照数据）
     */
    private String technicalLevel;
    /**
     * 毕业学校
     */
    private String graduationSchool;
    /**
     * 进入集团时间
     */
    private Date joinCompanyDate;
    /**
     * 专业技术职务（详见BIP参照数据）
     */
    private String professionalQualification;
    /**
     * 毕业日期
     */
    private Date graduationDate;
    /**
     * 启用状态
     */
    private String enableState;
    /**
     * 旧NC人员基本信息主键
     */
    private String oldPkPsndoc;
    /**
     * 创建者工号
     */
    private String createId;
    /**
     * 更新者工号
     */
    private String updateId;
}
