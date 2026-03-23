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
 * @description HdlPersonJobInfoBo业务入参对象，封装员工工作信息主数据查询与变更请求字段。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HdlPersonJobInfoBo extends BaseEntity {
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