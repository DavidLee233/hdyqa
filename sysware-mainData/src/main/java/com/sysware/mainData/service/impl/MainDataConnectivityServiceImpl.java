package com.sysware.mainData.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.helper.LoginHelper;
import com.sysware.mainData.config.RemoteDataConfig;
import com.sysware.mainData.domain.HdlMainDataDownstreamLog;
import com.sysware.mainData.domain.HdlMainDataDownstreamSystem;
import com.sysware.mainData.domain.HdlMainDataRemoteApiLog;
import com.sysware.mainData.mapper.HdlMainDataDownstreamLogMapper;
import com.sysware.mainData.mapper.HdlMainDataDownstreamSystemMapper;
import com.sysware.mainData.mapper.HdlMainDataRemoteApiLogMapper;
import com.sysware.mainData.service.IMainDataConnectivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MainDataConnectivityServiceImpl implements IMainDataConnectivityService {

    private static final String TYPE_ORG_DEPT = "1";
    private static final String TYPE_PERSON_BASIC = "2";
    private static final String TYPE_PERSON_JOB = "3";

    private static final String STATUS_CONNECTED = "1";
    private static final String STATUS_DISCONNECTED = "0";
    private static final int DEFAULT_CHECK_TIMEOUT_MS = 3000;
    private static final List<Integer> ALLOWED_PORTS = Arrays.asList(80, 8080, 8081, 443);
    private static final Path PING_LOG_PATH = Paths.get("E:\\hdyqa\\logs\\ping.log");
    private static final DateTimeFormatter PING_LOG_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Object PING_LOG_LOCK = new Object();

    private final HdlMainDataDownstreamSystemMapper downstreamSystemMapper;
    private final HdlMainDataDownstreamLogMapper downstreamLogMapper;
    private final HdlMainDataRemoteApiLogMapper remoteApiLogMapper;
    private final RemoteDataConfig remoteDataConfig;

    @Override
    public List<HdlMainDataDownstreamSystem> listDownstreamSystems() {
        LambdaQueryWrapper<HdlMainDataDownstreamSystem> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(HdlMainDataDownstreamSystem::getUpdateTime)
            .orderByDesc(HdlMainDataDownstreamSystem::getCreateTime)
            .orderByDesc(HdlMainDataDownstreamSystem::getId);
        List<HdlMainDataDownstreamSystem> rows = downstreamSystemMapper.selectList(lqw);
        for (HdlMainDataDownstreamSystem row : rows) {
            fillDisplayStatus(row);
        }
        return rows;
    }

    @Override
    public HdlMainDataDownstreamSystem saveDownstreamSystem(HdlMainDataDownstreamSystem system) {
        if (system == null) {
            throw new IllegalArgumentException("下游系统配置不能为空");
        }
        if (StrUtil.isBlank(system.getSystemName())) {
            throw new IllegalArgumentException("下游系统名称不能为空");
        }
        if (StrUtil.isBlank(system.getSystemIp())) {
            throw new IllegalArgumentException("下游系统IP不能为空");
        }
        if (system.getSystemPort() == null || system.getSystemPort() <= 0) {
            system.setSystemPort(80);
        }
        if (!ALLOWED_PORTS.contains(system.getSystemPort())) {
            throw new IllegalArgumentException("端口仅支持 80/8080/8081/443");
        }

        String operator = resolveOperatorName();
        LocalDateTime now = LocalDateTime.now();
        if (system.getId() == null) {
            system.setCreateBy(operator);
            system.setCreateTime(now);
            if (StrUtil.isBlank(system.getLastConnectStatus())) {
                system.setLastConnectStatus(STATUS_DISCONNECTED);
            }
            downstreamSystemMapper.insert(system);
        } else {
            system.setUpdateBy(operator);
            system.setUpdateTime(now);
            downstreamSystemMapper.updateById(system);
        }
        HdlMainDataDownstreamSystem saved = downstreamSystemMapper.selectById(system.getId());
        fillDisplayStatus(saved);
        return saved;
    }

    @Override
    public void deleteDownstreamSystems(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        downstreamSystemMapper.deleteBatchIds(ids);
    }

    @Override
    public List<HdlMainDataDownstreamSystem> checkDownstreamSystems(List<Long> ids) {
        List<HdlMainDataDownstreamSystem> targets;
        if (ids == null || ids.isEmpty()) {
            targets = listDownstreamSystems();
        } else {
            targets = downstreamSystemMapper.selectBatchIds(ids);
        }
        if (targets == null || targets.isEmpty()) {
            return Collections.emptyList();
        }

        String operator = resolveOperatorName();
        LocalDateTime now = LocalDateTime.now();
        List<HdlMainDataDownstreamSystem> updated = new ArrayList<>();
        for (HdlMainDataDownstreamSystem item : targets) {
            ConnectivityResult result = checkDownstreamEndpoint(item);
            item.setLastConnectStatus(result.connected ? STATUS_CONNECTED : STATUS_DISCONNECTED);
            item.setLastCheckTime(now);
            item.setLastCheckMessage(result.message);
            item.setUpdateBy(operator);
            item.setUpdateTime(now);
            downstreamSystemMapper.updateById(item);
            fillDisplayStatus(item);
            updated.add(item);
        }
        return updated;
    }

    @Override
    public Map<String, Object> checkRemoteConnectivity() {
        Map<String, Object> result = new HashMap<>(8);
        HostPort hostPort = resolveRemoteHostPort();
        ConnectivityResult checkResult = checkRemoteEndpoint(hostPort);
        result.put("remoteHost", hostPort.host);
        result.put("remotePort", hostPort.port);
        result.put("connected", checkResult.connected);
        result.put("status", checkResult.connected ? STATUS_CONNECTED : STATUS_DISCONNECTED);
        result.put("message", checkResult.message);
        result.put("ipStatus", checkResult.ipStatus);
        result.put("ipMessage", checkResult.ipMessage);
        result.put("port80Status", checkResult.port80Status);
        result.put("port80Message", checkResult.port80Message);
        result.put("port443Status", checkResult.port443Status);
        result.put("port443Message", checkResult.port443Message);
        result.put("checkTime", LocalDateTime.now());
        return result;
    }

    @Override
    public void recordDownstreamPull(String requestUrl,
                                     String dataType,
                                     boolean success,
                                     long recordCount,
                                     long durationMs,
                                     String message,
                                     String clientIp) {
        try {
            HdlMainDataDownstreamLog log = new HdlMainDataDownstreamLog();
            String normalizedIp = normalizeClientIp(clientIp);
            HdlMainDataDownstreamSystem system = findDownstreamSystemByIp(normalizedIp);
            if (system != null) {
                log.setDownstreamSystemId(system.getId());
                log.setDownstreamSystemName(system.getSystemName());
                log.setDownstreamSystemIp(system.getSystemIp());
            } else {
                log.setDownstreamSystemName("未登记系统");
                log.setDownstreamSystemIp(StrUtil.blankToDefault(normalizedIp, "-"));
            }
            log.setRequestUrl(truncate(requestUrl, 500));
            log.setDataType(normalizeDataType(dataType));
            log.setDataTypeName(resolveDataTypeName(dataType));
            log.setSyncTime(LocalDateTime.now());
            log.setSuccess(success ? STATUS_CONNECTED : STATUS_DISCONNECTED);
            log.setRecordCount((int) Math.max(0, Math.min(recordCount, Integer.MAX_VALUE)));
            log.setDurationMs(Math.max(durationMs, 0L));
            log.setMessage(truncate(StrUtil.blankToDefault(message, success ? "success" : "failed"), 500));
            log.setCreateBy(resolveOperatorName());
            log.setCreateTime(LocalDateTime.now());
            downstreamLogMapper.insert(log);
        } catch (Exception ignored) {
            // keep primary flow safe
        }
    }

    @Override
    public void recordRemoteCall(String requestUrl,
                                 String dataType,
                                 String callSource,
                                 boolean success,
                                 int recordCount,
                                 long durationMs,
                                 String message) {
        try {
            HdlMainDataRemoteApiLog log = new HdlMainDataRemoteApiLog();
            log.setRequestUrl(truncate(requestUrl, 500));
            log.setDataType(normalizeDataType(dataType));
            log.setDataTypeName(resolveDataTypeName(dataType));
            log.setCallSource(normalizeCallSource(callSource));
            log.setSyncTime(LocalDateTime.now());
            log.setSuccess(success ? STATUS_CONNECTED : STATUS_DISCONNECTED);
            log.setRecordCount(Math.max(recordCount, 0));
            log.setDurationMs(Math.max(durationMs, 0L));
            log.setMessage(truncate(StrUtil.blankToDefault(message, success ? "success" : "failed"), 500));
            log.setCreateBy(resolveOperatorName());
            log.setCreateTime(LocalDateTime.now());
            remoteApiLogMapper.insert(log);
        } catch (Exception ignored) {
            // keep primary flow safe
        }
    }

    @Override
    public TableDataInfo<HdlMainDataDownstreamLog> listDownstreamLogs(Long systemId, String dataType, PageQuery pageQuery) {
        Page<HdlMainDataDownstreamLog> page = (pageQuery == null ? new PageQuery() : pageQuery).build();
        LambdaQueryWrapper<HdlMainDataDownstreamLog> lqw = new LambdaQueryWrapper<>();
        if (systemId != null) {
            lqw.eq(HdlMainDataDownstreamLog::getDownstreamSystemId, systemId);
        }
        if (StrUtil.isNotBlank(dataType)) {
            lqw.eq(HdlMainDataDownstreamLog::getDataType, normalizeDataType(dataType));
        }
        lqw.orderByDesc(HdlMainDataDownstreamLog::getSyncTime)
            .orderByDesc(HdlMainDataDownstreamLog::getId);
        return TableDataInfo.build(downstreamLogMapper.selectPage(page, lqw));
    }

    @Override
    public void deleteDownstreamLogs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        downstreamLogMapper.deleteBatchIds(ids);
    }

    @Override
    public void clearDownstreamLogs(Long systemId) {
        LambdaQueryWrapper<HdlMainDataDownstreamLog> lqw = new LambdaQueryWrapper<>();
        if (systemId != null) {
            lqw.eq(HdlMainDataDownstreamLog::getDownstreamSystemId, systemId);
        }
        downstreamLogMapper.delete(lqw);
    }

    @Override
    public TableDataInfo<HdlMainDataRemoteApiLog> listRemoteLogs(String dataType, String callSource, PageQuery pageQuery) {
        Page<HdlMainDataRemoteApiLog> page = (pageQuery == null ? new PageQuery() : pageQuery).build();
        LambdaQueryWrapper<HdlMainDataRemoteApiLog> lqw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(dataType)) {
            lqw.eq(HdlMainDataRemoteApiLog::getDataType, normalizeDataType(dataType));
        }
        if (StrUtil.isNotBlank(callSource)) {
            lqw.eq(HdlMainDataRemoteApiLog::getCallSource, normalizeCallSource(callSource));
        }
        lqw.orderByDesc(HdlMainDataRemoteApiLog::getSyncTime)
            .orderByDesc(HdlMainDataRemoteApiLog::getId);
        return TableDataInfo.build(remoteApiLogMapper.selectPage(page, lqw));
    }

    @Override
    public void deleteRemoteLogs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        remoteApiLogMapper.deleteBatchIds(ids);
    }

    @Override
    public void clearRemoteLogs(String dataType, String callSource) {
        LambdaQueryWrapper<HdlMainDataRemoteApiLog> lqw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(dataType)) {
            lqw.eq(HdlMainDataRemoteApiLog::getDataType, normalizeDataType(dataType));
        }
        if (StrUtil.isNotBlank(callSource)) {
            lqw.eq(HdlMainDataRemoteApiLog::getCallSource, normalizeCallSource(callSource));
        }
        remoteApiLogMapper.delete(lqw);
    }

    private HdlMainDataDownstreamSystem findDownstreamSystemByIp(String clientIp) {
        if (StrUtil.isBlank(clientIp)) {
            return null;
        }
        LambdaQueryWrapper<HdlMainDataDownstreamSystem> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HdlMainDataDownstreamSystem::getSystemIp, clientIp).last("limit 1");
        return downstreamSystemMapper.selectOne(lqw);
    }

    private ConnectivityResult checkHostPort(String host, Integer port, int timeoutMs) {
        String targetHost = StrUtil.blankToDefault(host, "");
        int targetPort = (port == null || port <= 0) ? 80 : port;
        if (StrUtil.isBlank(targetHost)) {
            return ConnectivityResult.fail("主机地址为空");
        }
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(targetHost, targetPort), timeoutMs);
            return ConnectivityResult.ok("连接成功");
        } catch (Exception e) {
            return ConnectivityResult.fail(truncate(StrUtil.blankToDefault(e.getMessage(), "连接失败"), 500));
        }
    }

    private ConnectivityResult checkDownstreamEndpoint(HdlMainDataDownstreamSystem system) {
        String host = resolveHost(system == null ? null : system.getSystemIp());
        int port = resolvePort(system == null ? null : system.getSystemIp(), system == null ? null : system.getSystemPort());

        IpConnectivityResult ipResult = checkIpConnectivity(host, DEFAULT_CHECK_TIMEOUT_MS);
        PortConnectivityResult portResult;
        if (!ipResult.connected) {
            portResult = PortConnectivityResult.fail("IP未连通，端口检测未执行");
        } else {
            portResult = checkPortAvailability(host, port, DEFAULT_CHECK_TIMEOUT_MS);
        }

        if (system != null) {
            system.setIpConnectStatus(ipResult.connected ? STATUS_CONNECTED : STATUS_DISCONNECTED);
            system.setIpCheckMessage(ipResult.message);
            system.setPortConnectStatus(portResult.available ? STATUS_CONNECTED : STATUS_DISCONNECTED);
            system.setPortCheckMessage(portResult.message);
        }

        boolean success = ipResult.connected && portResult.available;
        String message = success ? "连接成功" : (ipResult.connected ? portResult.message : ipResult.message);
        writePingLog(
            "下游连接检测",
            host,
            String.valueOf(port),
            ipResult.connected,
            portResult.available,
            "系统名称=" + StrUtil.blankToDefault(system == null ? null : system.getSystemName(), "-")
                + "; IP消息=" + StrUtil.blankToDefault(ipResult.message, "-")
                + "; Ping输出=" + StrUtil.blankToDefault(truncate(ipResult.rawOutput, 300), "-")
                + "; 端口消息=" + StrUtil.blankToDefault(portResult.message, "-")
                + "; 总体结果=" + message
        );
        return ConnectivityResult.of(success, message,
            ipResult.connected ? STATUS_CONNECTED : STATUS_DISCONNECTED,
            ipResult.message,
            portResult.available ? STATUS_CONNECTED : STATUS_DISCONNECTED,
            portResult.message,
            null,
            null);
    }

    private ConnectivityResult checkRemoteEndpoint(HostPort hostPort) {
        IpConnectivityResult ipResult = checkIpConnectivity(hostPort.host, DEFAULT_CHECK_TIMEOUT_MS);
        PortConnectivityResult port80Result;
        PortConnectivityResult port443Result;
        if (!ipResult.connected) {
            port80Result = PortConnectivityResult.fail("IP未连通，端口检测未执行");
            port443Result = PortConnectivityResult.fail("IP未连通，端口检测未执行");
        } else {
            port80Result = checkPortAvailability(hostPort.host, 80, DEFAULT_CHECK_TIMEOUT_MS);
            port443Result = checkPortAvailability(hostPort.host, 443, DEFAULT_CHECK_TIMEOUT_MS);
        }

        boolean anyPortAvailable = port80Result.available || port443Result.available;
        if (!ipResult.connected) {
            writePingLog(
                "院主数据连接检测",
                hostPort.host,
                "80,443",
                false,
                false,
                "IP消息=" + StrUtil.blankToDefault(ipResult.message, "-")
                    + "; Ping输出=" + StrUtil.blankToDefault(truncate(ipResult.rawOutput, 300), "-")
                    + "; 80端口消息=" + StrUtil.blankToDefault(port80Result.message, "-")
                    + "; 443端口消息=" + StrUtil.blankToDefault(port443Result.message, "-")
                    + "; 总体结果=服务器IP连接超时"
            );
            return ConnectivityResult.of(false, "服务器IP连接超时",
                STATUS_DISCONNECTED, "服务器IP连接超时",
                STATUS_DISCONNECTED, port80Result.message,
                STATUS_DISCONNECTED, port443Result.message);
        }
        if (!anyPortAvailable) {
            writePingLog(
                "院主数据连接检测",
                hostPort.host,
                "80,443",
                true,
                false,
                "IP消息=" + StrUtil.blankToDefault(ipResult.message, "-")
                    + "; Ping输出=" + StrUtil.blankToDefault(truncate(ipResult.rawOutput, 300), "-")
                    + "; 80端口消息=" + StrUtil.blankToDefault(port80Result.message, "-")
                    + "; 443端口消息=" + StrUtil.blankToDefault(port443Result.message, "-")
                    + "; 总体结果=服务器IP连接成功但无可用端口连接"
            );
            return ConnectivityResult.of(false, "服务器IP连接成功但无可用端口连接",
                STATUS_CONNECTED, "服务器IP连接成功",
                STATUS_DISCONNECTED, port80Result.message,
                STATUS_DISCONNECTED, port443Result.message);
        }
        writePingLog(
            "院主数据连接检测",
            hostPort.host,
            "80,443",
            true,
            true,
            "IP消息=" + StrUtil.blankToDefault(ipResult.message, "-")
                + "; Ping输出=" + StrUtil.blankToDefault(truncate(ipResult.rawOutput, 300), "-")
                + "; 80端口消息=" + StrUtil.blankToDefault(port80Result.message, "-")
                + "; 443端口消息=" + StrUtil.blankToDefault(port443Result.message, "-")
                + "; 总体结果=院主数据服务器连接成功"
        );
        return ConnectivityResult.of(true, "院主数据服务器连接成功",
            STATUS_CONNECTED, "服务器IP连接成功",
            port80Result.available ? STATUS_CONNECTED : STATUS_DISCONNECTED, port80Result.message,
            port443Result.available ? STATUS_CONNECTED : STATUS_DISCONNECTED, port443Result.message);
    }

    private IpConnectivityResult checkIpConnectivity(String host, int timeoutMs) {
        if (StrUtil.isBlank(host)) {
            return IpConnectivityResult.fail("服务器IP连接超时", "主机地址为空");
        }
        PingCommandResult pingResult = pingHost(host, timeoutMs);
        if (pingResult.success) {
            return IpConnectivityResult.ok("服务器IP连接成功", pingResult.output);
        }
        return IpConnectivityResult.fail("服务器IP连接超时", pingResult.output);
    }

    private PortConnectivityResult checkPortAvailability(String host, int port, int timeoutMs) {
        PortProbeResult probeResult = probePort(host, port, timeoutMs);
        if (probeResult.connected) {
            return PortConnectivityResult.ok("端口" + port + "可用");
        }
        String detail = StrUtil.blankToDefault(probeResult.message, "连接失败");
        return PortConnectivityResult.fail(truncate("端口" + port + "不可用：" + detail, 500));
    }

    private PortProbeResult probePort(String host, int port, int timeoutMs) {
        if (StrUtil.isBlank(host)) {
            return PortProbeResult.fail("主机地址为空");
        }
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return PortProbeResult.connected();
        } catch (ConnectException e) {
            if (isConnectionRefused(e)) {
                return PortProbeResult.refused();
            }
            return PortProbeResult.fail(e.getMessage());
        } catch (Exception e) {
            return PortProbeResult.fail(e.getMessage());
        }
    }

    private PingCommandResult pingHost(String host, int timeoutMs) {
        try {
            String osName = StrUtil.blankToDefault(System.getProperty("os.name"), "").toLowerCase();
            List<String> command;
            if (osName.contains("win")) {
                command = Arrays.asList("ping", "-n", "1", "-w", String.valueOf(timeoutMs), host);
            } else {
                int waitSeconds = Math.max(timeoutMs / 1000, 1);
                command = Arrays.asList("ping", "-c", "1", "-W", String.valueOf(waitSeconds), host);
            }
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            boolean finished = process.waitFor(Math.max(timeoutMs + 1000, 2000), TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                return PingCommandResult.fail("ping命令执行超时");
            }
            String output = readProcessOutput(process);
            if (StrUtil.isBlank(output)) {
                return PingCommandResult.fail("ping命令无输出");
            }
            String lowerOutput = output.toLowerCase();
            boolean success = lowerOutput.contains("ttl=")
                || lowerOutput.contains("bytes from");
            return success ? PingCommandResult.ok(output) : PingCommandResult.fail(output);
        } catch (Exception e) {
            return PingCommandResult.fail(e.getMessage());
        }
    }

    private String readProcessOutput(Process process) {
        try (java.io.InputStream inputStream = process.getInputStream()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            byte[] bytes = outputStream.toByteArray();
            if (bytes.length == 0) {
                return "";
            }
            return new String(bytes, Charset.defaultCharset());
        } catch (Exception e) {
            return "";
        }
    }

    private boolean isConnectionRefused(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        String msg = StrUtil.blankToDefault(throwable.getMessage(), "").toLowerCase();
        return msg.contains("refused")
            || msg.contains("actively refused")
            || msg.contains("拒绝")
            || msg.contains("actively")
            || msg.contains("10061");
    }

    private String resolveHost(String hostOrUrl) {
        if (StrUtil.isBlank(hostOrUrl)) {
            return "";
        }
        String raw = hostOrUrl.trim();
        try {
            if (raw.startsWith("http://") || raw.startsWith("https://")) {
                URI uri = URI.create(raw);
                return StrUtil.blankToDefault(uri.getHost(), "");
            }
            int slashIndex = raw.indexOf('/');
            if (slashIndex > -1) {
                raw = raw.substring(0, slashIndex);
            }
            int colonIndex = raw.indexOf(':');
            if (colonIndex > -1) {
                return raw.substring(0, colonIndex);
            }
            return raw;
        } catch (Exception e) {
            return raw;
        }
    }

    private int resolvePort(String hostOrUrl, Integer defaultPort) {
        int fallback = (defaultPort == null || defaultPort <= 0) ? 80 : defaultPort;
        if (StrUtil.isBlank(hostOrUrl)) {
            return fallback;
        }
        String raw = hostOrUrl.trim();
        try {
            if (raw.startsWith("http://") || raw.startsWith("https://")) {
                URI uri = URI.create(raw);
                if (uri.getPort() > 0) {
                    return uri.getPort();
                }
                if ("https".equalsIgnoreCase(uri.getScheme())) {
                    return defaultPort == null || defaultPort <= 0 ? 443 : defaultPort;
                }
                return fallback;
            }
            int slashIndex = raw.indexOf('/');
            if (slashIndex > -1) {
                raw = raw.substring(0, slashIndex);
            }
            int colonIndex = raw.indexOf(':');
            if (colonIndex > -1 && colonIndex < raw.length() - 1) {
                return Integer.parseInt(raw.substring(colonIndex + 1));
            }
        } catch (Exception ignored) {
            // use fallback
        }
        return fallback;
    }

    private void fillDisplayStatus(HdlMainDataDownstreamSystem row) {
        if (row == null) {
            return;
        }
        if (StrUtil.isBlank(row.getIpConnectStatus())) {
            row.setIpConnectStatus(StrUtil.blankToDefault(row.getLastConnectStatus(), STATUS_DISCONNECTED));
        }
        if (StrUtil.isBlank(row.getPortConnectStatus())) {
            row.setPortConnectStatus(StrUtil.blankToDefault(row.getLastConnectStatus(), STATUS_DISCONNECTED));
        }
        if (StrUtil.isBlank(row.getIpCheckMessage())) {
            row.setIpCheckMessage(StrUtil.blankToDefault(row.getLastCheckMessage(), ""));
        }
        if (StrUtil.isBlank(row.getPortCheckMessage())) {
            row.setPortCheckMessage(StrUtil.blankToDefault(row.getLastCheckMessage(), ""));
        }
    }

    private HostPort resolveRemoteHostPort() {
        String apiUrl = StrUtil.blankToDefault(remoteDataConfig.getApiUrl(), "");
        try {
            URI uri = URI.create(apiUrl);
            String host = uri.getHost();
            int port = uri.getPort();
            String scheme = uri.getScheme();
            if (StrUtil.isBlank(host)) {
                host = apiUrl;
            }
            if (port <= 0) {
                port = "https".equalsIgnoreCase(scheme) ? 443 : 80;
            }
            return new HostPort(host, port);
        } catch (Exception ignored) {
            return new HostPort(apiUrl, 80);
        }
    }

    private String normalizeClientIp(String clientIp) {
        if (StrUtil.isBlank(clientIp)) {
            return "";
        }
        String value = clientIp;
        if (value.contains(",")) {
            value = value.split(",")[0];
        }
        value = value.trim();
        if ("0:0:0:0:0:0:0:1".equals(value) || "::1".equals(value)) {
            return "127.0.0.1";
        }
        return value;
    }

    private String normalizeDataType(String dataType) {
        if (TYPE_PERSON_BASIC.equals(dataType)) {
            return TYPE_PERSON_BASIC;
        }
        if (TYPE_PERSON_JOB.equals(dataType)) {
            return TYPE_PERSON_JOB;
        }
        return TYPE_ORG_DEPT;
    }

    private String resolveDataTypeName(String dataType) {
        String value = normalizeDataType(dataType);
        if (TYPE_PERSON_BASIC.equals(value)) {
            return "员工基本信息";
        }
        if (TYPE_PERSON_JOB.equals(value)) {
            return "员工工作信息";
        }
        return "组织部门";
    }

    private String normalizeCallSource(String callSource) {
        String value = StrUtil.blankToDefault(callSource, "sync").trim().toLowerCase();
        if (!"sync".equals(value) && !"query".equals(value) && !"export".equals(value)) {
            return "sync";
        }
        return value;
    }

    private String resolveOperatorName() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (loginUser != null && StrUtil.isNotBlank(loginUser.getUsername())) {
                return loginUser.getUsername();
            }
        } catch (Exception ignored) {
            // ignore
        }
        return "MAIN_DATA_CONNECTIVITY";
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private void writePingLog(String operation,
                              String targetIp,
                              String targetPort,
                              boolean ipConnected,
                              boolean portConnected,
                              String detail) {
        String timeText = LocalDateTime.now().format(PING_LOG_TIME_FORMATTER);
        String line = timeText
            + " | 操作记录=" + sanitizeForLog(operation)
            + " | 目标IP=" + sanitizeForLog(targetIp)
            + " | 目标端口=" + sanitizeForLog(targetPort)
            + " | IP连接状况=" + (ipConnected ? "成功" : "失败")
            + " | 端口可用状况=" + (portConnected ? "成功" : "失败")
            + " | 详情=" + sanitizeForLog(detail)
            + System.lineSeparator();
        try {
            synchronized (PING_LOG_LOCK) {
                Path parent = PING_LOG_PATH.getParent();
                if (parent != null && Files.notExists(parent)) {
                    Files.createDirectories(parent);
                }
                Files.write(PING_LOG_PATH,
                    line.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
            }
        } catch (Exception ignored) {
            // keep connectivity check flow safe
        }
    }

    private String sanitizeForLog(String value) {
        if (value == null) {
            return "-";
        }
        return value.replace("\r", " ").replace("\n", " ").trim();
    }

    private static class HostPort {
        private final String host;
        private final int port;

        private HostPort(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    private static class ConnectivityResult {
        private final boolean connected;
        private final String message;
        private final String ipStatus;
        private final String ipMessage;
        private final String port80Status;
        private final String port80Message;
        private final String port443Status;
        private final String port443Message;

        private ConnectivityResult(boolean connected,
                                   String message,
                                   String ipStatus,
                                   String ipMessage,
                                   String port80Status,
                                   String port80Message,
                                   String port443Status,
                                   String port443Message) {
            this.connected = connected;
            this.message = message;
            this.ipStatus = ipStatus;
            this.ipMessage = ipMessage;
            this.port80Status = port80Status;
            this.port80Message = port80Message;
            this.port443Status = port443Status;
            this.port443Message = port443Message;
        }

        private static ConnectivityResult ok(String message) {
            return new ConnectivityResult(true, message, null, null, null, null, null, null);
        }

        private static ConnectivityResult fail(String message) {
            return new ConnectivityResult(false, message, null, null, null, null, null, null);
        }

        private static ConnectivityResult of(boolean connected,
                                             String message,
                                             String ipStatus,
                                             String ipMessage,
                                             String port80Status,
                                             String port80Message,
                                             String port443Status,
                                             String port443Message) {
            return new ConnectivityResult(connected, message, ipStatus, ipMessage, port80Status, port80Message, port443Status, port443Message);
        }
    }

    private static class IpConnectivityResult {
        private final boolean connected;
        private final String message;
        private final String rawOutput;

        private IpConnectivityResult(boolean connected, String message, String rawOutput) {
            this.connected = connected;
            this.message = message;
            this.rawOutput = rawOutput;
        }

        private static IpConnectivityResult ok(String message, String rawOutput) {
            return new IpConnectivityResult(true, message, rawOutput);
        }

        private static IpConnectivityResult fail(String message, String rawOutput) {
            return new IpConnectivityResult(false, message, rawOutput);
        }
    }

    private static class PingCommandResult {
        private final boolean success;
        private final String output;

        private PingCommandResult(boolean success, String output) {
            this.success = success;
            this.output = output;
        }

        private static PingCommandResult ok(String output) {
            return new PingCommandResult(true, output);
        }

        private static PingCommandResult fail(String output) {
            return new PingCommandResult(false, output);
        }
    }

    private static class PortConnectivityResult {
        private final boolean available;
        private final String message;

        private PortConnectivityResult(boolean available, String message) {
            this.available = available;
            this.message = message;
        }

        private static PortConnectivityResult ok(String message) {
            return new PortConnectivityResult(true, message);
        }

        private static PortConnectivityResult fail(String message) {
            return new PortConnectivityResult(false, message);
        }
    }

    private static class PortProbeResult {
        private final boolean connected;
        private final boolean refused;
        private final String message;

        private PortProbeResult(boolean connected, boolean refused, String message) {
            this.connected = connected;
            this.refused = refused;
            this.message = message;
        }

        private static PortProbeResult connected() {
            return new PortProbeResult(true, false, "连接成功");
        }

        private static PortProbeResult refused() {
            return new PortProbeResult(false, true, "连接被拒绝");
        }

        private static PortProbeResult fail(String message) {
            return new PortProbeResult(false, false, message);
        }
    }
}
