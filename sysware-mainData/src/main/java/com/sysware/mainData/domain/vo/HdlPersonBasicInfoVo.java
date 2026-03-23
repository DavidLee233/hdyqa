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
 * @project npic
 * @description HdlPersonBasicInfoVo业务出参对象，封装员工基本信息主数据返回与展示字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@ExcelIgnoreUnannotated
public class HdlPersonBasicInfoVo extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @ExcelProperty(value = "员工基本信息主键")
    private String pkPsndoc;
    @ExcelProperty(value = "员工号")
    private String code;
    @ExcelProperty(value = "户籍")
    private String registeredResidence;
    @ExcelProperty(value = "家庭地址")
    private String familyAddress;
    @ExcelProperty(value = "办公地点")
    private String office;
    @ExcelProperty(value = "出入证件号")
    private String inOutNumber;
    @ExcelProperty(value = "涉密级别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String secretLevel;
    @ExcelProperty(value = "姓名")
    private String name;
    @ExcelProperty(value = "证件类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String idType;
    @ExcelProperty(value = "性别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0==未知、1=男、2=女")
    private String sex;
    @ExcelProperty(value = "证件号码")
    private String idNumber;
    @ExcelProperty(value = "出生日期")
    private Date birthdate;
    @ExcelProperty(value = "籍贯")
    private String nativePlace;
    @ExcelProperty(value = "民族", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String nationality;
    @ExcelProperty(value = "政治面貌", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String polity;
    @ExcelProperty(value = "学历", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String pkEdu;
    @ExcelProperty(value = "学位", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String pkDegree;
    @ExcelProperty(value = "参加工作时间")
    private Date joinWorkDate;
    @ExcelProperty(value = "手机号")
    private String mobile;
    @ExcelProperty(value = "办公室电话")
    private String officePhone;
    @ExcelProperty(value = "专业")
    private String postalCode;
    @ExcelProperty(value = "所属部门")
    private String pkOrg;
    @ExcelProperty(value = "入党", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "团=")
    private Date joinPolityDate;
    @ExcelProperty(value = "专业技术级别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String professionalLevel;
    @ExcelProperty(value = "职业技术级别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String technicalLevel;
    @ExcelProperty(value = "毕业学校")
    private String graduationSchool;
    @ExcelProperty(value = "进入集团时间")
    private Date joinCompanyDate;
    @ExcelProperty(value = "专业技术职务", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String professionalQualification;
    @ExcelProperty(value = "毕业日期")
    private Date graduationDate;
    @ExcelProperty(value = "启用状态")
    private String enableState;
    @ExcelProperty(value = "旧NC人员基本信息主键")
    private String oldPkPsndoc;
    @ExcelProperty(value = "创建者工号")
    private String createId;
    @ExcelProperty(value = "更新者工号")
    private String updateId;


}