package com.sysware.system.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import com.sysware.common.core.domain.BaseEntity;

/**
 * 系统访问记录业务对象 sys_logininfor
 *
 * @author aa
 * @date 2026-01-25
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SysLogininforBo extends BaseEntity {

    /**
     * 访问ID
     */
    private String infoId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录状态（0成功 1失败）
     */
    private String status;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    private Date loginTime;

    // 添加时间范围查询字段（不需要在数据库表中存在）
    private String beginTime;

    private String endTime;
}
