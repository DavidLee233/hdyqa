package com.sysware.system.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;
import java.util.Date;



/**
 * 密级视图对象 sys_security
 *
 * @author zzr
 * @date 2022-01-05
 */
@Data
public class SecurityVo {

	private static final long serialVersionUID = 1L;

	/**
     *  主键
     */
	private String securityId;

    /**
     * 密级名称
     */
	@ExcelProperty(value= "密级名称")
	private String securityName;

    /**
     * 密级值
     */
	@ExcelProperty(value= "密级值")
	private Long securityValue;

    /**
     * 密级类型
     */
	@ExcelProperty(value= "密级类型")
	private String securityType;

    /**
     * 起用状态
     */
	@ExcelProperty(value= "起用状态")
	private String securityFlag;

    /**
     * 排序
     */
	@ExcelProperty(value= "排序")
	private Long sort;


}
