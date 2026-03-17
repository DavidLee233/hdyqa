package com.sysware.common.annotation;

import com.sysware.common.utils.StringUtils;

import java.lang.annotation.*;

/**
 * 密级格式化
 *
 * @author
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelSecurityFormat {

    /**
     * 密级类型  data = 数据  user = 用户
     */
    String securityType() default "";

    /**
     * 读取内容转表达式 (如: 0=男,1=女,2=未知)
     */
    String readConverterExp() default "";

    /**
     * 分隔符，读取字符串组内容
     */
    String separator() default StringUtils.SEPARATOR;

}
