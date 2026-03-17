package com.sysware.system.domain.bo;


import lombok.Data;
import java.util.Date;
import javax.validation.constraints.*;


/**
 * 密级编辑对象 sys_security
 *
 * @author zzr
 * @date 2022-01-05
 */
@Data
public class SecurityEditBo {


    /**
     * 主键
     */
    private String securityId;

    /**
     * 密级名称
     */
    private String securityName;

    /**
     * 密级值
     */
    private Long securityValue;

    /**
     * 密级类型
     */
    private String securityType;

    /**
     * 起用状态
     */
    private String securityFlag;

    /**
     * 排序
     */
    private Long sort;
}
