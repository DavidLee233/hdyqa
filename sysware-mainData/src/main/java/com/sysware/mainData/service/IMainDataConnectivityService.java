package com.sysware.mainData.service;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.mainData.domain.HdlMainDataDownstreamLog;
import com.sysware.mainData.domain.HdlMainDataDownstreamSystem;
import com.sysware.mainData.domain.HdlMainDataRemoteApiLog;

import java.util.List;
import java.util.Map;

public interface IMainDataConnectivityService {

    List<HdlMainDataDownstreamSystem> listDownstreamSystems();

    HdlMainDataDownstreamSystem saveDownstreamSystem(HdlMainDataDownstreamSystem system);

    void deleteDownstreamSystems(List<Long> ids);

    List<HdlMainDataDownstreamSystem> checkDownstreamSystems(List<Long> ids);

    Map<String, Object> checkRemoteConnectivity();

    void recordDownstreamPull(String requestUrl,
                              String dataType,
                              boolean success,
                              long recordCount,
                              long durationMs,
                              String message,
                              String clientIp);

    void recordRemoteCall(String requestUrl,
                          String dataType,
                          String callSource,
                          boolean success,
                          int recordCount,
                          long durationMs,
                          String message);

    TableDataInfo<HdlMainDataDownstreamLog> listDownstreamLogs(Long systemId, String dataType, PageQuery pageQuery);

    void deleteDownstreamLogs(List<Long> ids);

    void clearDownstreamLogs(Long systemId);

    TableDataInfo<HdlMainDataRemoteApiLog> listRemoteLogs(String dataType, String callSource, PageQuery pageQuery);

    void deleteRemoteLogs(List<Long> ids);

    void clearRemoteLogs(String dataType, String callSource);
}
