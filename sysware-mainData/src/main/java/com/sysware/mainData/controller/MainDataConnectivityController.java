package com.sysware.mainData.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.convert.Convert;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.mainData.domain.HdlMainDataDownstreamLog;
import com.sysware.mainData.domain.HdlMainDataDownstreamSystem;
import com.sysware.mainData.domain.HdlMainDataRemoteApiLog;
import com.sysware.mainData.service.IMainDataConnectivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@RestController
@SaCheckLogin
@RequiredArgsConstructor
@RequestMapping("/mainData/connectivity")
public class MainDataConnectivityController {

    private final IMainDataConnectivityService connectivityService;

    @GetMapping("/downstream/list")
    public R<List<HdlMainDataDownstreamSystem>> listDownstreamSystems() {
        return R.ok(connectivityService.listDownstreamSystems());
    }

    @PostMapping("/downstream/save")
    public R<HdlMainDataDownstreamSystem> saveDownstreamSystem(@RequestBody HdlMainDataDownstreamSystem system) {
        return R.ok(connectivityService.saveDownstreamSystem(system));
    }

    @PostMapping("/downstream/delete")
    public R<Void> deleteDownstreamSystems(@RequestBody(required = false) Map<String, Object> body) {
        connectivityService.deleteDownstreamSystems(resolveIds(body));
        return R.ok();
    }

    @PostMapping("/downstream/check")
    public R<List<HdlMainDataDownstreamSystem>> checkDownstreamSystems(@RequestBody(required = false) Map<String, Object> body) {
        return R.ok(connectivityService.checkDownstreamSystems(resolveIds(body)));
    }

    @GetMapping("/downstream/log/list")
    public TableDataInfo<HdlMainDataDownstreamLog> listDownstreamLogs(@RequestParam(value = "systemId", required = false) Long systemId,
                                                                      @RequestParam(value = "dataType", required = false) String dataType,
                                                                      PageQuery pageQuery) {
        return connectivityService.listDownstreamLogs(systemId, dataType, pageQuery);
    }

    @PostMapping("/downstream/log/delete")
    public R<Void> deleteDownstreamLogs(@RequestBody(required = false) Map<String, Object> body) {
        connectivityService.deleteDownstreamLogs(resolveIds(body));
        return R.ok();
    }

    @PostMapping("/downstream/log/clear")
    public R<Void> clearDownstreamLogs(@RequestBody(required = false) Map<String, Object> body) {
        Long systemId = body == null ? null : Convert.toLong(body.get("systemId"), null);
        connectivityService.clearDownstreamLogs(systemId);
        return R.ok();
    }

    @GetMapping("/remote/check")
    public R<Map<String, Object>> checkRemoteConnectivity() {
        return R.ok(connectivityService.checkRemoteConnectivity());
    }

    @GetMapping("/remote/log/list")
    public TableDataInfo<HdlMainDataRemoteApiLog> listRemoteLogs(@RequestParam(value = "dataType", required = false) String dataType,
                                                                 @RequestParam(value = "callSource", required = false, defaultValue = "sync") String callSource,
                                                                 PageQuery pageQuery) {
        return connectivityService.listRemoteLogs(dataType, callSource, pageQuery);
    }

    @PostMapping("/remote/log/delete")
    public R<Void> deleteRemoteLogs(@RequestBody(required = false) Map<String, Object> body) {
        connectivityService.deleteRemoteLogs(resolveIds(body));
        return R.ok();
    }

    @PostMapping("/remote/log/clear")
    public R<Void> clearRemoteLogs(@RequestBody(required = false) Map<String, Object> body) {
        String dataType = body == null ? null : Convert.toStr(body.get("dataType"), null);
        String callSource = body == null ? "sync" : Convert.toStr(body.get("callSource"), "sync");
        connectivityService.clearRemoteLogs(dataType, callSource);
        return R.ok();
    }

    private List<Long> resolveIds(Map<String, Object> body) {
        if (body == null || body.get("ids") == null) {
            return Collections.emptyList();
        }
        Object raw = body.get("ids");
        if (!(raw instanceof List)) {
            return Collections.emptyList();
        }
        List<?> values = (List<?>) raw;
        return values.stream()
            .map(item -> Convert.toLong(item, null))
            .filter(id -> id != null && id > 0)
            .collect(Collectors.toList());
    }
}
