package com.sysware.mainData.controller.support;

import cn.hutool.core.util.StrUtil;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.RemoteTableDataInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @project npic
 * @description MainDataRemoteControllerSupport工具类，负责统一远端分页结果封装、同步结果判定与导出响应处理逻辑。
 * @author DavidLee233
 * @date 2026/3/23
 */
public final class MainDataRemoteControllerSupport {

    private MainDataRemoteControllerSupport() {
    }

    /**
     * @description 解析请求中的分页参数并构建分页对象，统一三类主数据远端浏览接口的分页处理行为。
     * @params params 请求参数集合（包含页码与每页条数字段）
     * @params defaultPageSize 默认每页条数
     *
     * @return PageQuery 分页查询参数对象（包含页码、页大小与默认排序信息）。
     * @author DavidLee233
     * @date 2026/3/23
     */
    public static PageQuery resolvePageQuery(Map<String, Object> params, int defaultPageSize) {
        PageQuery pageQuery = new PageQuery();
        int pageNum = parseInt(params == null ? null : params.get("pageNum"), 1);
        int pageSize = parseInt(params == null ? null : params.get("pageSize"), defaultPageSize);
        pageQuery.setPageNum(Math.max(pageNum, 1));
        pageQuery.setPageSize(pageSize <= 0 ? defaultPageSize : pageSize);
        return pageQuery;
    }

    /**
     * @description 将远端查询结果统一封装为分页结构，保证三类主数据远端浏览接口返回格式一致。
     * @params remoteResult 远端服务返回结果
     * @params pageQuery 当前请求的分页对象
     *
     * @return RemoteTableDataInfo 远端表格分页结果（包含数据列表、总条数、页码与提示信息）。
     * @author DavidLee233
     * @date 2026/3/23
     */
    public static RemoteTableDataInfo buildRemoteSuccess(Map<String, Object> remoteResult, PageQuery pageQuery) {
        Map<String, Object> result = remoteResult == null ? new HashMap<>() : remoteResult;
        Object rowsObj = result.get("rows");
        java.util.List<?> rows = rowsObj instanceof java.util.List ? (java.util.List<?>) rowsObj : new java.util.ArrayList<>();
        long total = parseLong(result.get("total"), 0L);
        RemoteTableDataInfo tableDataInfo = RemoteTableDataInfo.build(rows, total, pageQuery);
        tableDataInfo.setCode(200);
        tableDataInfo.setMsg("查询成功");
        return tableDataInfo;
    }

    /**
     * @description 在远端查询异常时统一返回失败分页结构，便于前端识别错误并展示明确提示。
     * @params pageQuery 当前请求分页对象
     * @params fallbackMessage 失败兜底提示
     * @params throwable 异常对象
     *
     * @return RemoteTableDataInfo 远端表格分页失败结果（包含空数据与错误提示）。
     * @author DavidLee233
     * @date 2026/3/23
     */
    public static RemoteTableDataInfo buildRemoteError(PageQuery pageQuery, String fallbackMessage, Throwable throwable) {
        String errorText = throwable == null ? null : throwable.getMessage();
        String message = StrUtil.isNotBlank(errorText)
            ? fallbackMessage + "：" + errorText
            : fallbackMessage;
        if (message.length() > 240) {
            message = message.substring(0, 240);
        }
        RemoteTableDataInfo tableDataInfo = RemoteTableDataInfo.build(new java.util.ArrayList<>(), 0L, pageQuery);
        tableDataInfo.setCode(500);
        tableDataInfo.setMsg(message);
        return tableDataInfo;
    }

    /**
     * @description 解析请求体中的同步模式参数并标准化为受支持的模式值，统一三类主数据强制同步行为。
     * @params body 请求体参数集合（承载同步模式）
     *
     * @return String 同步模式值（full、incremental或auto）。
     * @author DavidLee233
     * @date 2026/3/23
     */
    public static String resolveSyncMode(Map<String, Object> body) {
        if (body == null || body.get("syncMode") == null) {
            return "full";
        }
        String value = String.valueOf(body.get("syncMode")).trim().toLowerCase();
        if ("incremental".equals(value) || "auto".equals(value)) {
            return value;
        }
        return "full";
    }

    /**
     * @description 统一判定同步执行结果是否成功，兼容布尔值与字符串格式。
     * @params result 同步执行结果对象
     *
     * @return boolean 同步成功判定值（true表示成功，false表示失败）。
     * @author DavidLee233
     * @date 2026/3/23
     */
    public static boolean isSyncSuccess(Map<String, Object> result) {
        if (result == null) {
            return false;
        }
        Object success = result.get("success");
        if (success == null) {
            return false;
        }
        if (success instanceof Boolean) {
            return (Boolean) success;
        }
        String normalized = String.valueOf(success).trim().toLowerCase();
        return "1".equals(normalized) || "true".equals(normalized);
    }

    /**
     * @description 统一解析同步失败提示，优先返回后端业务失败信息，避免前端只展示笼统错误。
     * @params result 同步执行结果对象
     * @params fallbackMessage 失败兜底提示
     *
     * @return String 面向页面展示的失败提示文本。
     * @author DavidLee233
     * @date 2026/3/23
     */
    public static String resolveSyncFailureMessage(Map<String, Object> result, String fallbackMessage) {
        if (result == null) {
            return fallbackMessage;
        }
        Object message = result.get("message");
        if (message == null) {
            return fallbackMessage;
        }
        String text = String.valueOf(message).trim();
        return text.isEmpty() ? fallbackMessage : text;
    }

    /**
     * @description 合并远端导出接口的URL参数与请求体参数，并补齐默认分页参数，保证导出查询条件在不同调用方式下行为一致。
     * @params requestParams URL参数集合
     * @params requestBody 请求体参数集合
     * @params defaultPageNum 默认页码
     * @params defaultPageSize 默认每页条数
     *
     * @return Map<String, Object> 导出查询参数集合（包含分页与筛选参数）。
     * @author DavidLee233
     * @date 2026/3/23
     */
    public static Map<String, Object> mergeExportParams(Map<String, String> requestParams,
                                                        Map<String, Object> requestBody,
                                                        int defaultPageNum,
                                                        int defaultPageSize) {
        Map<String, Object> queryParams = new HashMap<>();
        if (requestBody != null) {
            queryParams.putAll(requestBody);
        }
        if (requestParams != null) {
            queryParams.putAll(requestParams);
        }
        queryParams.put("pageNum", defaultPageNum);
        queryParams.put("pageSize", defaultPageSize);
        return queryParams;
    }

    /**
     * @description 统一写出远端导出Excel响应头与二进制内容，确保三类主数据远端导出下载行为一致。
     * @params response HTTP响应对象
     * @params data Excel二进制内容
     * @params filePrefix 文件名前缀
     *
     * @return void 无返回值，导出结果写入HTTP响应输出流。
     * @author DavidLee233
     * @date 2026/3/23
     */
    public static void writeExcelResponse(HttpServletResponse response, byte[] data, String filePrefix) throws IOException {
        String fileName = filePrefix + "_" + System.currentTimeMillis() + ".xlsx";
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + encoded);
        response.getOutputStream().write(data == null ? new byte[0] : data);
        response.getOutputStream().flush();
    }

    private static int parseInt(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static long parseLong(Object value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
