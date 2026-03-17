package com.sysware.common.translation.impl;

import com.sysware.common.annotation.TranslationType;
import com.sysware.common.constant.TransConstant;
import com.sysware.common.core.service.DictService;
import com.sysware.common.translation.TranslationInterface;
import com.sysware.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 字典翻译实现
 *
 * @author Lion Li
 */
@Component
@AllArgsConstructor
@TranslationType(type = TransConstant.DICT_TYPE_TO_LABEL)
public class DictTypeTranslationImpl implements TranslationInterface<String> {

    private final DictService dictService;

    public String translation(Object key, String other) {
        if (key instanceof String && StringUtils.isNotBlank(other)) {
            return dictService.getDictLabel(other, key.toString());
        }
        return null;
    }
}
