package com.sysware.mainData.controller;

import com.sysware.common.annotation.Log;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.RemoteTableDataInfo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.enums.BusinessType;
import com.sysware.mainData.domain.ApiTokenResponse;
import com.sysware.mainData.service.IRemoteDataService;
import com.sysware.mainData.service.IRemoteTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 远程数据控制器
 */
@RestController
@RequestMapping("/mainData/remote")
public class RemoteDataController {
    private static final Logger logger = LoggerFactory.getLogger(RemoteDataController.class);

    @Autowired
    private IRemoteTokenService remoteTokenService;

    @Autowired
    private IRemoteDataService remoteDataService;

    /**
     * 获取当前Token信息（仅用于调试）
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
            result.put("tokenInfo", tokenInfo);
            result.put("tokenAge", tokenAge);
            result.put("remainingTime", remainingTime);
            result.put("isValid", remoteTokenService.isTokenValid());

            return R.ok(result);
        }
        return R.fail("Token信息为空");
    }

    /**
     * 刷新Token（仅用于调试）
     */
    @PostMapping("/token/refresh")
    @Log(title = "远程数据", businessType = BusinessType.OTHER)
    public R refreshToken() {
        try {
            ApiTokenResponse newToken = remoteTokenService.refreshToken();
            return R.ok("Token刷新成功", newToken);
        } catch (Exception e) {
            logger.error("刷新Token失败", e);
            return R.fail("刷新Token失败: " + e.getMessage());
        }
    }

    /**
     * 查询远程组织部门列表
     * 适配您当前的分页插件版本
     */
    @PostMapping("/remote/list")
    public RemoteTableDataInfo listRemote(@RequestBody Map<String, Object> params) {
        try {
            // 1. 处理分页参数 - 适配PageQuery格式
            PageQuery pageQuery = new PageQuery();

            // 从请求参数中获取分页信息
            Integer pageNum = params.get("pageNum") != null ?
                    Integer.parseInt(params.get("pageNum").toString()) : 1;
            Integer pageSize = params.get("pageSize") != null ?
                    Integer.parseInt(params.get("pageSize").toString()) : 10;

            pageQuery.setPageNum(pageNum);
            pageQuery.setPageSize(pageSize);

            // 2. 处理搜索条件
            Map<String, Object> queryParams = new HashMap<>();

            // 复制基础查询参数
            if (params.get("name") != null) queryParams.put("name", params.get("name"));
            if (params.get("code") != null) queryParams.put("code", params.get("code"));
            if (params.get("shortName") != null) queryParams.put("shortName", params.get("shortName"));
            if (params.get("internetName") != null) queryParams.put("internetName", params.get("internetName"));
            if (params.get("sign") != null) queryParams.put("sign", params.get("sign"));

            // 处理全局搜索
            if (params.get("searchValue") != null && !params.get("searchValue").toString().isEmpty()) {
                queryParams.put("searchValue", params.get("searchValue"));
            }

            // 3. 添加分页参数（远程接口页码从0开始）
            queryParams.put("pageNum", pageNum - 1); // 转换为远程分页（从0开始）
            queryParams.put("pageSize", pageSize);

            // 4. 调用远程服务查询数据
            Map<String, Object> result = remoteDataService.queryRemoteDepartments(queryParams);

            // 5. 提取数据并构建TableDataInfo
            List<?> rows = (List<?>) result.get("rows");
            Long total = result.get("total") != null ?
                    Long.parseLong(result.get("total").toString()) : 0L;

            // 6. 使用您系统标准的TableDataInfo.build方法
            return RemoteTableDataInfo.build(rows, total, pageQuery);

        } catch (Exception e) {
            logger.error("查询远程组织部门失败", e);
            // 返回空数据而不是抛出异常
            return RemoteTableDataInfo.build(null, 0L, new PageQuery());
        }
    }

    /**
     * 导出远程组织部门数据
     */
    @PostMapping("/remote/export")
    @Log(title = "远程组织部门导出", businessType = BusinessType.EXPORT)
    public void exportRemote(@RequestBody Map<String, Object> params, HttpServletResponse response) {
        try {
            // 处理分页参数（导出时不分页）
            params.put("pageNum", 0);
            params.put("pageSize", 10000); // 导出一万条数据

            // 调用远程服务获取导出数据
            byte[] data = remoteDataService.exportRemoteDepartments(params);

            if (data == null || data.length == 0) {
                // 如果没有数据，导出Excel模板
                exportEmptyExcel(response);
                return;
            }

            // 设置响应头
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition",
                    "attachment;filename=remote_departments_" + System.currentTimeMillis() + ".xlsx");

            // 写入响应流
            try (OutputStream os = response.getOutputStream()) {
                os.write(data);
                os.flush();
            }

        } catch (Exception e) {
            logger.error("导出远程组织部门数据失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 导出空Excel
     */
    private void exportEmptyExcel(HttpServletResponse response) throws Exception {
        // 这里可以根据您的导出工具类创建空的Excel模板
        // 例如使用EasyExcel或POI创建带有表头的空Excel
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition",
                "attachment;filename=remote_departments_empty_" + System.currentTimeMillis() + ".xlsx");
    }
}
