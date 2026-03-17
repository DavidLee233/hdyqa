package com.sysware.common.enums;

import lombok.Getter;

/**
 * 角色编号
 *
 * @author sysware
 */
@Getter
public enum RoleKey {
    SHOW_ALL_PROJECT("QXMKJRY", "全项目可见人员"),
    DOC_USER("DARY", "档案人员"),
    CREATE_PROJECT_USER("XMCJR", "项目创建人"),
    PROJECT_MANAGE_USER("XMFZR", "项目负责人");

    private final String code;
    private final String info;

    RoleKey(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
