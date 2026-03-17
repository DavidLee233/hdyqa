package com.sysware.common.enums;

import com.sysware.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备类型
 * 针对多套 用户体系
 *
 * @author Lion Li
 */
@Getter
@AllArgsConstructor
public enum UserType {

    /**
     * pc端
     */
    SYS_USER("sys_user"),

    /**
     * app端
     */
    APP_USER("app_user");

    private final String userType;

    public static UserType getUserType(String str) {
        return SYS_USER;
    }
}
