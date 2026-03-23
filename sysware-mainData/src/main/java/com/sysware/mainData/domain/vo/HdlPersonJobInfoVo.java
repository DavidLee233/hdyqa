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
 * @description HdlPersonJobInfoVo业务出参对象，封装员工工作信息主数据返回与展示字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@ExcelIgnoreUnannotated
public class HdlPersonJobInfoVo extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @ExcelProperty(value = "员工工作记录主键")
    private String pkPsnjob;
    @ExcelProperty(value = "姓名")
    private String name;
    @ExcelProperty(value = "证件号码")
    private String idNumber;
    @ExcelProperty(value = "出入证号码")
    private String inOutNumber;
    @ExcelProperty(value = "实际部门")
    private String realPkDept;
    @ExcelProperty(value = "key号码")
    private String keyNumber;
    @ExcelProperty(value = "员工号")
    private String code;
    @ExcelProperty(value = "部门主键")
    private String pkDept;
    @ExcelProperty(value = "职务")
    private String pkJob;
    @ExcelProperty(value = "开始日期")
    private Date beginDate;
    @ExcelProperty(value = "结束日期")
    private Date endDate;
    @ExcelProperty(value = "人员类别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String pkPsncl;
    @ExcelProperty(value = "员工基本信息主键")
    private String pkPsndoc;
    @ExcelProperty(value = "涉密级别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String secretLevel;
    @ExcelProperty(value = "最新记录", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "N==否，Y=是")
    private String lastFlag;
    @ExcelProperty(value = "是否结束", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "N==否，Y=是")
    private String endFlag;
    @ExcelProperty(value = "组织")
    private String pkOrg;
    @ExcelProperty(value = "是否主职", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "N==否，Y=是")
    private String isMainJob;
    @ExcelProperty(value = "旧NC人员基本信息主键")
    private String oldPkPsnjob;
    @ExcelProperty(value = "其他职务名称")
    private String otherJobTitle;
    @ExcelProperty(value = "职级")
    private String jobLevel;
    @ExcelProperty(value = "创建者工号")
    private String createId;
    @ExcelProperty(value = "更新者工号")
    private String updateId;


}