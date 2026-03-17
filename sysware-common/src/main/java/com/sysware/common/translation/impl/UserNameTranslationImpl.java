package com.sysware.common.translation.impl;

import com.sysware.common.annotation.TranslationType;
import com.sysware.common.constant.TransConstant;
import com.sysware.common.core.service.UserService;
import com.sysware.common.translation.TranslationInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用户名翻译实现
 *
 * @author
 */
@Component
@AllArgsConstructor
@TranslationType(type = TransConstant.USER_ID_TO_NAME)
public class UserNameTranslationImpl implements TranslationInterface<String> {

    private final UserService userService;

    public String translation(Object key, String other) {
        if (key instanceof String) {
            return userService.selectUserNameById((String) key);
        }
        return null;
    }
}
