package com.sysware.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpStatus;
import com.sysware.common.constant.Constants;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端工具类
 *
 * @author sysware
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServletUtils extends ServletUtil {

    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue) {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name) {
        return Convert.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Boolean参数
     */
    public static Boolean getParameterToBool(String name) {
        return Convert.toBool(getRequest().getParameter(name));
    }

    /**
     * 获取Boolean参数
     */
    public static Boolean getParameterToBool(String name, Boolean defaultValue) {
        return Convert.toBool(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String[]> getParams(ServletRequest request) {
        final Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map);
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String> getParamMap(ServletRequest request) {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : getParams(request).entrySet()) {
            params.put(entry.getKey(), StringUtils.join(entry.getValue(), StringUtils.SEPARATOR));
        }
        return params;
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(HttpStatus.HTTP_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否是Ajax异步请求
     *
     * @param request
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {

        String accept = request.getHeader("accept");
        if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest")) {
            return true;
        }

        String uri = request.getRequestURI();
        if (StringUtils.equalsAnyIgnoreCase(uri, ".json", ".xml")) {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        return StringUtils.equalsAnyIgnoreCase(ajax, "json", "xml");
    }

    /**
     * 获取客户端IP地址
     */
    public static String getClientIP(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.length() > 15 && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 获取客户端IP地址（无参数版本）
     */
    public static String getClientIP() {
        return getClientIP(getRequest());
    }

    /**
     * 获取浏览器类型
     */
    public static String getBrowser(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String userAgent = request.getHeader("User-Agent");
        if (StrUtil.isBlank(userAgent)) {
            return "unknown";
        }

        try {
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            Browser browser = ua.getBrowser();
            String browserName = browser.getName();
            String browserVersion = getBrowserVersion(userAgent, browser);

            if (StrUtil.isNotBlank(browserVersion)) {
                return browserName + " " + browserVersion;
            }
            return browserName;
        } catch (Exception e) {
            return parseBrowserManually(userAgent);
        }
    }


    /**
     * 获取浏览器版本
     */
    private static String getBrowserVersion(String userAgent, Browser browser) {
        try {
            return browser.getVersion(userAgent).getVersion();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 手动解析浏览器类型
     */
    private static String parseBrowserManually(String userAgent) {
        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("msie") || userAgent.contains("trident")) {
            return "Internet Explorer";
        } else if (userAgent.contains("edge")) {
            return "Microsoft Edge";
        } else if (userAgent.contains("chrome")) {
            return "Chrome";
        } else if (userAgent.contains("firefox")) {
            return "Firefox";
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            return "Safari";
        } else if (userAgent.contains("opera")) {
            return "Opera";
        } else {
            return "unknown";
        }
    }

    /**
     * 获取浏览器类型（无参数版本）
     */
    public static String getBrowser() {
        return getBrowser(getRequest());
    }

    /**
     * 获取操作系统类型
     */
    public static String getOs(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String userAgent = request.getHeader("User-Agent");
        if (StrUtil.isBlank(userAgent)) {
            return "unknown";
        }

        try {
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            OperatingSystem os = ua.getOperatingSystem();
            return os.getName();
        } catch (Exception e) {
            return parseOsManually(userAgent);
        }
    }

    /**
     * 获取操作系统类型（无参数版本）
     */
    public static String getOs() {
        return getOs(getRequest());
    }

    /**
     * 手动解析操作系统
     */
    private static String parseOsManually(String userAgent) {
        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("windows")) {
            return "Windows";
        } else if (userAgent.contains("mac os")) {
            return "Mac OS";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad") || userAgent.contains("ipod")) {
            return "iOS";
        } else if (userAgent.contains("unix")) {
            return "Unix";
        } else {
            return "unknown";
        }
    }

    /**
     * 获取完整的User-Agent
     */
    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        return request.getHeader("User-Agent");
    }

    /**
     * 获取完整的User-Agent（无参数版本）
     */
    public static String getUserAgent() {
        return getUserAgent(getRequest());
    }

    /**
     * 判断是否为内网IP
     */
    public static boolean isInternalIP(String ip) {
        return NetUtil.isInnerIP(ip);
    }

    /**
     * 获取请求的完整URL
     */
    public static String getFullUrl(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        StringBuffer url = request.getRequestURL();
        String queryString = request.getQueryString();
        if (StrUtil.isNotBlank(queryString)) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }

    /**
     * 获取请求的完整URL（无参数版本）
     */
    public static String getFullUrl() {
        return getFullUrl(getRequest());
    }

    /**
     * 获取请求方法
     */
    public static String getMethod(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        return request.getMethod();
    }

    /**
     * 获取请求方法（无参数版本）
     */
    public static String getMethod() {
        return getMethod(getRequest());
    }

    /**
     * 内容编码
     *
     * @param str 内容
     * @return 编码后的内容
     */
    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, Constants.UTF8);
        } catch (UnsupportedEncodingException e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 内容解码
     *
     * @param str 内容
     * @return 解码后的内容
     */
    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, Constants.UTF8);
        } catch (UnsupportedEncodingException e) {
            return StringUtils.EMPTY;
        }
    }

}
