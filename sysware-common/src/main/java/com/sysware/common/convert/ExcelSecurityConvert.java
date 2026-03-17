package com.sysware.common.convert;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.sysware.common.annotation.ExcelDictFormat;
import com.sysware.common.annotation.ExcelSecurityFormat;
import com.sysware.common.core.service.DictService;
import com.sysware.common.core.service.SecurityService;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.common.utils.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 密级格式化转换处理
 *
 * @author Lion Li
 */
@Slf4j
public class ExcelSecurityConvert implements Converter<Object> {

    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        ExcelSecurityFormat anno = getAnnotation(contentProperty.getField());
        String type = anno.securityType();
        String label = cellData.getStringValue();
        String value;
        if (StringUtils.isBlank(type)) {
            value = ExcelUtil.reverseByExp(label, anno.readConverterExp(), anno.separator());
        } else {
            value = SpringUtils.getBean(SecurityService.class).selectSecurityIdByNameAndType(label,type);
        }
        return Convert.convert(contentProperty.getField().getType(), value);
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(object)) {
            return new WriteCellData<>("");
        }
        ExcelSecurityFormat anno = getAnnotation(contentProperty.getField());
        String type = anno.securityType();
        String value = Convert.toStr(object);
        String label;
        if (StringUtils.isBlank(type)) {
            label = ExcelUtil.convertByExp(value, anno.readConverterExp(), anno.separator());
        } else {
            label = SpringUtils.getBean(SecurityService.class).selectSecurityNameById(value,type);
        }
        return new WriteCellData<>(label);
    }

    private ExcelSecurityFormat getAnnotation(Field field) {
        return AnnotationUtil.getAnnotation(field, ExcelSecurityFormat.class);
    }
}
