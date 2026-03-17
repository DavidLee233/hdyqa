package com.sysware.common.translation.impl;

import com.sysware.common.annotation.TranslationType;
import com.sysware.common.constant.TransConstant;
import com.sysware.common.core.service.DeptService;
import com.sysware.common.translation.TranslationInterface;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 部门翻译实现
 *
 * @author Lion Li
 */
@Component
@AllArgsConstructor
@TranslationType(type = TransConstant.DEPT_ID_TO_NAME)
public class DeptNameTranslationImpl implements TranslationInterface<String> {

    private final DeptService deptService;

    public String translation(Object key, String other) {
        return deptService.selectDeptNameByIds(key.toString());
    }
}
