package com.sysware.mainData.domain;

import lombok.Data;

@Data
public class MdmParseRefParam {
    private Object parseRefDictField;
    private Object parseRefOrdinaryField;
    private String parseRefWithSuffix;
    private String parseRefOrdinaryValue;
    private String parseRefDictValue;
}
