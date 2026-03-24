package com.sysware.mainData.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.sysware.common.annotation.Log;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.RemoteTableDataInfo;
import com.sysware.common.enums.BusinessType;
import com.sysware.mainData.controller.support.MainDataRemoteControllerSupport;
import com.sysware.mainData.domain.ApiTokenResponse;
import com.sysware.mainData.service.IRemoteDataService;
import com.sysware.mainData.service.IRemoteTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
/**
 * @project npic
 * @description RemoteDataController控制器，负责远端主数据相关接口请求接收、参数处理与结果响应。
 * @author DavidLee233
 * @date 2026/3/20
 */
@RestController
@SaCheckLogin
@RequestMapping("/mainData/remote")
public class RemoteDataController {
    private static final Logger logger = LoggerFactory.getLogger(RemoteDataController.class);

    @Autowired
    private IRemoteTokenService remoteTokenService;

    @Autowired
    private IRemoteDataService remoteDataService;
    /**
     * @description 查询当前缓存的远端令牌信息并返回可视化结果。
     * @params 无
     *
      * @return R 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @GetMapping("/token/info")
    public R getTokenInfo() {
        ApiTokenResponse tokenInfo = remoteTokenService.getCurrentTokenInfo();
        if (tokenInfo != null) {
            // 计算Token剩余有效期
            long currentTime = System.currentTimeMillis();
            long tokenAge = currentTime - tokenInfo.getTimestamp();
            long expireTime = 3600000; // 1小时
            long remainingTime = expireTime - tokenAge;

            Map<String, Object> result = new java.util.HashMap<>();
            Map<String, Object> tokenView = new HashMap<>();
            tokenView.put("id", tokenInfo.getId());
            tokenView.put("name", tokenInfo.getName());
            tokenView.put("tenantId", tokenInfo.getTenantId());
            tokenView.put("timestamp", tokenInfo.getTimestamp());
            tokenView.put("tokenMasked", maskToken(tokenInfo.getToken()));
            result.put("tokenInfo", tokenView);
            result.put("tokenAge", tokenAge);
            result.put("remainingTime", remainingTime);
            result.put("isValid", remoteTokenService.isTokenValid());

            return R.ok(result);
        }
        return R.fail("Token信息为空");
    }
    /**
     * @description 主动刷新远端访问令牌并返回最新令牌信息。
     * @params 无
     *
      * @return R 统一响应结果对象（包含状态码、消息与业务数据）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostMapping("/token/refresh")
    @Log(title = "远程数据", businessType = BusinessType.OTHER)
    public R refreshToken() {
        try {
            ApiTokenResponse newToken = remoteTokenService.refreshToken();
            return R.ok("Token刷新成功", newToken);
        } catch (Exception e) {
            logger.error("刷新令牌失败", e);
            return R.fail("刷新Token失败: " + e.getMessage());
        }
    }
    /**
     * @description 调用远端接口分页查询远端主数据并适配前端分页结构。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     *
      * @return RemoteTableDataInfo 远端表格分页结果（包含远端数据列表与总条数）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostMapping("/remote/list")
    public RemoteTableDataInfo listRemote(@RequestBody Map<String, Object> params) {
        PageQuery pageQuery = MainDataRemoteControllerSupport.resolvePageQuery(params, 10);
        Map<String, Object> safeParams = params == null ? new HashMap<>() : params;
        try {
            Map<String, Object> queryParams = new HashMap<>();
            if (safeParams.get("name") != null) queryParams.put("name", safeParams.get("name"));
            if (safeParams.get("code") != null) queryParams.put("code", safeParams.get("code"));
            if (safeParams.get("shortName") != null) queryParams.put("shortName", safeParams.get("shortName"));
            if (safeParams.get("internetName") != null) queryParams.put("internetName", safeParams.get("internetName"));
            if (safeParams.get("sign") != null) queryParams.put("sign", safeParams.get("sign"));
            if (safeParams.get("searchValue") != null && !safeParams.get("searchValue").toString().isEmpty()) {
                queryParams.put("searchValue", safeParams.get("searchValue"));
            }
            queryParams.put("pageNum", pageQuery.getPageNum() - 1);
            queryParams.put("pageSize", pageQuery.getPageSize());
            Map<String, Object> result = remoteDataService.queryRemoteDepartments(queryParams);
            return MainDataRemoteControllerSupport.buildRemoteSuccess(result, pageQuery);
        } catch (Exception e) {
            logger.error("查询远端组织部门失败，参数={}", safeParams, e);
            return MainDataRemoteControllerSupport.buildRemoteError(pageQuery, "查询远端组织部门失败", e);
        }
    }
    /**
     * @description 导出远端远端主数据查询结果并写入响应流。
     * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
     * @params response HTTP响应对象，用于写出导出文件或接口返回内容
     *
      * @return void 无返回值，导出结果已写入HTTP响应输出流供前端下载。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostMapping("/remote/export")
    @Log(title = "远程组织部门导出", businessType = BusinessType.EXPORT)
    public void exportRemote(@RequestBody(required = false) Map<String, Object> params, HttpServletResponse response) {
        try {
            Map<String, Object> safeParams = params == null ? new HashMap<>() : new HashMap<>(params);
            safeParams.put("pageNum", 0);
            safeParams.put("pageSize", 10000);
            byte[] data = remoteDataService.exportRemoteDepartments(safeParams);
            MainDataRemoteControllerSupport.writeExcelResponse(response, data, "remote_departments");
        } catch (Exception e) {
            logger.error("导出远程组织部门数据失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description 对远端令牌进行脱敏处理后返回日志展示值，避免敏感信息在接口返回中明文暴露。
     * @params token 远端令牌原文。
     *
     * @return String 脱敏后的令牌摘要（保留前后少量字符），用于页面或日志展示。
     * @author DavidLee233
     * @date 2026/3/23
     */
    private String maskToken(String token) {
        if (token == null || token.isEmpty()) {
            return "null";
        }
        if (token.length() <= 10) {
            return token.substring(0, 1) + "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
