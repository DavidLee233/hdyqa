package com.sysware.common.enums;

/**
 * 用户状态
 *
 * @author sysware
 */
public enum DocumentStatus {
    PLANNING("planning", "编制中"),
    PLANNING_BACK("planningback", "退回编制中"),
    AUDITING("auditing", "审批中"),
    WITE_COMPLETED("witecompleted", "待归档"),
    COMPLET_AUDITING("completauditing", "归档审批中"),
    COMPLETING("completing", "归档中"),
    COMPLETED("completed", "已归档"),
    ONRUNNING("onrunning", "入库中"),
    RUNNING("running", "已入库"),
    ONCANCELING("oncanceling", "作废审批中"),
    WITEONCANCELINGDA("witeoncancelingda", "档案室作废中"),
    WITEONCANCELINGKF("witeoncancelingkf", "库房作废中"),
    CANCELING("canceling", "已作废"),
    DESTROY("destroy", "已销毁"),
    UPING("uping", "升版中"),
    UPDATEAUDITING("updateauditing", "更改审批中"),
    UPDATEING("updateing", "更改中");



    private final String code;
    private final String info;

    DocumentStatus(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
