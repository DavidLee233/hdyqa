package com.sysware.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * 角色类型
 *
 * @author
 */
@Getter
public enum RoleType {


    SYSTEM("system","系统级"), PROJECT("project","项目级");

    private final String code;
    private final String info;

    RoleType(String code,String info) {
        this.code = code;
        this.info = info;
    }


}
