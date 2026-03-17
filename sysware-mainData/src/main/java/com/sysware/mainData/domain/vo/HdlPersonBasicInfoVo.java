package com.sysware.mainData.domain.vo;

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
 * 员工基本信息数据视图对象 hdl_person_basic_info
 *
 * @author aa
 * @date 2026-01-15
 */
@Data
@ExcelIgnoreUnannotated
public class HdlPersonBasicInfoVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 员工基本信息主键
     */
    @ExcelProperty(value = "员工基本信息主键")
    private String pkPsndoc;

    /**
     * 员工号
     */
    @ExcelProperty(value = "员工号")
    private String code;

    /**
     * 户籍
     */
    @ExcelProperty(value = "户籍")
    private String registeredResidence;

    /**
     * 家庭地址
     */
    @ExcelProperty(value = "家庭地址")
    private String familyAddress;

    /**
     * 办公地点
     */
    @ExcelProperty(value = "办公地点")
    private String office;

    /**
     * 出入证件号
     */
    @ExcelProperty(value = "出入证件号")
    private String inOutNumber;

    /**
     * 涉密级别（详见BIP参照数据）
     */
    @ExcelProperty(value = "涉密级别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String secretLevel;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名")
    private String name;

    /**
     * 证件类型（详见BIP参照数据）
     */
    @ExcelProperty(value = "证件类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String idType;

    /**
     * 性别（0=未知、1=男、2=女）
     */
    @ExcelProperty(value = "性别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0==未知、1=男、2=女")
    private String sex;

    /**
     * 证件号码
     */
    @ExcelProperty(value = "证件号码")
    private String idNumber;

    /**
     * 出生日期
     */
    @ExcelProperty(value = "出生日期")
    private Date birthdate;

    /**
     * 籍贯
     */
    @ExcelProperty(value = "籍贯")
    private String nativePlace;

    /**
     * 民族（详见BIP参照数据）
     */
    @ExcelProperty(value = "民族", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String nationality;

    /**
     * 政治面貌（详见BIP参照数据）
     */
    @ExcelProperty(value = "政治面貌", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String polity;

    /**
     * 学历（详见BIP参照数据）
     */
    @ExcelProperty(value = "学历", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String pkEdu;

    /**
     * 学位（详见BIP参照数据）
     */
    @ExcelProperty(value = "学位", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String pkDegree;

    /**
     * 参加工作时间
     */
    @ExcelProperty(value = "参加工作时间")
    private Date joinWorkDate;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String mobile;

    /**
     * 办公室电话
     */
    @ExcelProperty(value = "办公室电话")
    private String officePhone;

    /**
     * 专业
     */
    @ExcelProperty(value = "专业")
    private String postalCode;

    /**
     * 所属部门
     */
    @ExcelProperty(value = "所属部门")
    private String pkOrg;

    /**
     * 入党（团）时间
     */
    @ExcelProperty(value = "入党", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "团=")
    private Date joinPolityDate;

    /**
     * 专业技术级别（详见BIP参照数据）
     */
    @ExcelProperty(value = "专业技术级别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String professionalLevel;

    /**
     * 职业技术级别（详见BIP参照数据）
     */
    @ExcelProperty(value = "职业技术级别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String technicalLevel;

    /**
     * 毕业学校
     */
    @ExcelProperty(value = "毕业学校")
    private String graduationSchool;

    /**
     * 进入集团时间
     */
    @ExcelProperty(value = "进入集团时间")
    private Date joinCompanyDate;

    /**
     * 专业技术职务（详见BIP参照数据）
     */
    @ExcelProperty(value = "专业技术职务", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String professionalQualification;

    /**
     * 毕业日期
     */
    @ExcelProperty(value = "毕业日期")
    private Date graduationDate;

    /**
     * 启用状态
     */
    @ExcelProperty(value = "启用状态")
    private String enableState;

    /**
     * 旧NC人员基本信息主键
     */
    @ExcelProperty(value = "旧NC人员基本信息主键")
    private String oldPkPsndoc;

    /**
     * 创建者工号
     */
    @ExcelProperty(value = "创建者工号")
    private String createId;

    /**
     * 更新者工号
     */
    @ExcelProperty(value = "更新者工号")
    private String updateId;


}
