package com.sysware.qa.utils;

import com.sysware.common.core.domain.R;
import com.sysware.common.utils.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @FileName excelUtils
 * @Author lxd
 * @Description
 * @date 2025/7/19
 **/
public class excelUtils {
    /**
     * @author lxd
     * @description: 通用Excel导出方法
     * @param list 数据列表
     * @param filePath 文件保存路径（包含文件名）
     * @param fields 要导出的字段数组（对应实体类属性名）
     * @return RVoid 导出文件的绝对路径
     * @date 2025/7/19
     */
    public static R<Void> exportToExcel(List<?> list, String filePath, String[] fields) throws IOException {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("数据列表不能为空");
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        if (fields == null || fields.length == 0) {
            throw new IllegalArgumentException("导出字段不能为空");
        }

        // 1. 创建工作簿和工作表
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("问题收集导出");

        // 2. 创建表头行
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < fields.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields[i]);
        }

        // 3. 获取第一个对象的Class用于反射
        Class<?> clazz = list.get(0).getClass();

        // 4. 填充数据行
        int rowNum = 1;
        for (Object obj : list) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < fields.length; i++) {
                try {
                    // 获取字段值
                    Object value = getFieldValue(obj, fields[i]);
                    // 设置单元格值
                    setCellValue(row.createCell(i), value);
                } catch (Exception e) {
                    R.fail("导出失败，请检查字段参数");
                }
            }
        }

        // 5. 自动调整列宽
        for (int i = 0; i < fields.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 6. 确保目录存在
        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = "QA_" + DateUtils.datePath2() + ".xlsx";
        File file = new File(directory, fileName);

        // 7. 写入文件
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }

        return R.ok(file.getAbsolutePath());
    }

    /**
     * @author lxd
     * @description: 通过反射获取对象属性值
     * @param obj
	 * @param fieldName
     * @return java.lang.Object
     * @date 2025/7/19
     **/
    private static Object getFieldValue(Object obj, String fieldName) throws Exception {
        // 优先尝试getter方法
        String getterMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            Method method = obj.getClass().getMethod(getterMethodName);
            return method.invoke(obj);
        } catch (NoSuchMethodException e) {
            // 如果getter方法不存在，尝试直接访问字段
            try {
                Field field = obj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException ex) {
                throw new NoSuchFieldException("找不到字段: " + fieldName);
            }
        }
    }

    /**
     * @author lxd
     * @description: 设置单元格值
     * @param cell
	 * @param value
     * @return void
     * @date 2025/7/19
     **/
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof java.util.Date) {
            cell.setCellValue((java.util.Date) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * @author lxd
     * @description: 根据前端传入的时间范围参数确定查询的时间范围
     * @param dataRange
     * @return java.util.List<java.time.LocalDateTime>
     * @date 2025/7/19
     **/
    public static List<LocalDateTime> getDateRange(String dataRange) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;
        // 根据前端传入的时间范围参数确定查询的时间范围
        switch (dataRange) {
            case "最近一周":
                startDate = endDate.minusWeeks(1);
                break;
            case "最近一月":
                startDate = endDate.minusMonths(1);
                break;
            case "最近一年":
                startDate = endDate.minusYears(1);
                break;
            case "全部":
            default:
                startDate = LocalDateTime.of(1970, 1, 1, 0, 0); // 设置一个很早的日期表示查询全部
                break;
        }
        return Arrays.asList(startDate, endDate);
    }

    /**
     * @author lxd
     * @description: 根据saveMode获取对应的字段数组
     * @param saveMode
     * @return java.lang.String[]
     * @date 2025/7/19
     **/
    public static String[] getFieldsBySaveMode(String saveMode) {
        // 定义不同模式对应的字段
        Map<String, String[]> fieldsMap = new HashMap<>();
        fieldsMap.put("精简模式", new String[]{"recordId", "type", "title", "createBy", "createId", "questionContent", "updateBy", "updateId", "answerContent"});
        fieldsMap.put("标准模式", new String[]{"recordId", "type", "title", "severity", "createBy", "createId", "department", "room_number",
                "telephone", "questionContent", "updateBy", "updateId", "answerContent", "solve_time"});
        fieldsMap.put("全量模式", new String[]{"recordId", "type", "status", "can_solve", "track", "title", "severity", "createBy", "createId",
                "createTime", "department", "room_number", "telephone", "classification", "questionContent",
                "updateBy", "updateId", "updateTime", "answerContent", "solve_time"});

        // 默认返回标准模式
        return fieldsMap.getOrDefault(saveMode, fieldsMap.get("标准模式"));
    }
}
