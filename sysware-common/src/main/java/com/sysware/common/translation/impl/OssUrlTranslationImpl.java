package com.sysware.common.translation.impl;

import com.sysware.common.annotation.TranslationType;
import com.sysware.common.constant.TransConstant;
import com.sysware.common.core.service.OssService;
import com.sysware.common.translation.TranslationInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * OSS翻译实现
 *
 * @author Lion Li
 */
@Component
@AllArgsConstructor
@TranslationType(type = TransConstant.OSS_ID_TO_URL)
public class OssUrlTranslationImpl implements TranslationInterface<String> {

   // private final OssService ossService;

    public String translation(Object key, String other) {

        return "";//ossService.selectUrlByIds(key.toString());
    }
}
