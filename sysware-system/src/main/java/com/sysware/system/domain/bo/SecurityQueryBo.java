package com.sysware.system.domain.bo;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 密级分页查询对象 sys_security
 *
 * @author zzr
 * @date 2022-01-05
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityQueryBo extends BaseEntity {

	/**
	 * 分页大小
	 */
	private Integer pageSize;
	/**
	 * 当前页数
	 */
	private Integer pageNum;
	/**
	 * 排序列
	 */
	private String orderByColumn;
	/**
	 * 排序的方向desc或者asc
	 */
	private String isAsc;


    /**
     * 密级名称
     */
	private String securityName;
    /**
     * 密级最大值
     */
	private Long maxSecurityValue;
	/**
	 * 密级最小值
	 */
	private Long minSecurityValue;
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
