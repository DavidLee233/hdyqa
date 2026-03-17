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
 * 员工工作信息数据视图对象 hdl_person_job_info
 *
 * @author aa
 * @date 2026-01-15
 */
@Data
@ExcelIgnoreUnannotated
public class HdlPersonJobInfoVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 员工工作记录主键
     */
    @ExcelProperty(value = "员工工作记录主键")
    private String pkPsnjob;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名")
    private String name;

    /**
     * 证件号码
     */
    @ExcelProperty(value = "证件号码")
    private String idNumber;

    /**
     * 出入证号码
     */
    @ExcelProperty(value = "出入证号码")
    private String inOutNumber;

    /**
     * 实际部门
     */
    @ExcelProperty(value = "实际部门")
    private String realPkDept;

    /**
     * key号码
     */
    @ExcelProperty(value = "key号码")
    private String keyNumber;

    /**
     * 员工号
     */
    @ExcelProperty(value = "员工号")
    private String code;

    /**
     * 部门主键
     */
    @ExcelProperty(value = "部门主键")
    private String pkDept;

    /**
     * 职务
     */
    @ExcelProperty(value = "职务")
    private String pkJob;

    /**
     * 开始日期
     */
    @ExcelProperty(value = "开始日期")
    private Date beginDate;

    /**
     * 结束日期
     */
    @ExcelProperty(value = "结束日期")
    private Date endDate;

    /**
     * 人员类别（详见BIP参照数据）
     */
    @ExcelProperty(value = "人员类别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String pkPsncl;

    /**
     * 员工基本信息主键
     */
    @ExcelProperty(value = "员工基本信息主键")
    private String pkPsndoc;

    /**
     * 涉密级别（详见BIP参照数据）
     */
    @ExcelProperty(value = "涉密级别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "详=见BIP参照数据")
    private String secretLevel;

    /**
     * 最新记录（N=否，Y=是）
     */
    @ExcelProperty(value = "最新记录", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "N==否，Y=是")
    private String lastFlag;

    /**
     * 是否结束（N=否，Y=是）
     */
    @ExcelProperty(value = "是否结束", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "N==否，Y=是")
    private String endFlag;

    /**
     * 组织
     */
    @ExcelProperty(value = "组织")
    private String pkOrg;

    /**
     * 是否主职（N=否，Y=是）
     */
    @ExcelProperty(value = "是否主职", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "N==否，Y=是")
    private String isMainJob;

    /**
     * 旧NC人员基本信息主键
     */
    @ExcelProperty(value = "旧NC人员基本信息主键")
    private String oldPkPsnjob;

    /**
     * 其他职务名称
     */
    @ExcelProperty(value = "其他职务名称")
    private String otherJobTitle;

    /**
     * 职级
     */
    @ExcelProperty(value = "职级")
    private String jobLevel;

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
