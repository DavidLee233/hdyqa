package com.sysware.mainData.service;

import java.util.Map;

/**
 * 远程数据服务接口
 */
public interface IRemoteDataService {
    /**
     * 查询远程组织部门列表
     * @param params 查询参数
     * @return 远程数据响应
     */
    Map<String, Object> queryRemoteDepartments(Map<String, Object> params);

    /**
     * 查询远程员工基本信息列表
     * @param params 查询参数
     * @return 远程数据响应
     */
    Map<String, Object> queryRemotePersonBasicInfos(Map<String, Object> params);

    /**
     * 查询远程员工工作信息列表
     * @param params 查询参数
     * @return 远程数据响应
     */
    Map<String, Object> queryRemotePersonJobInfos(Map<String, Object> params);

    /**
     * 导出远程组织部门数据
     * @param params 查询参数
     * @return 导出文件字节数组
     */
    byte[] exportRemoteDepartments(Map<String, Object> params);

    /**
     * 导出远程员工基本信息数据
     * @param params 查询参数
     * @return 导出文件字节数组
     */
    byte[] exportRemotePersonBasicInfos(Map<String, Object> params);

    /**
     * 导出远程员工工作信息数据
     * @param params 查询参数
     * @return 导出文件字节数组
     */
    byte[] exportRemotePersonJobInfos(Map<String, Object> params);

    /**
     * 强制同步远程组织部门到本地
     * @return 同步统计结果
     */
    Map<String, Object> forceSyncDepartments();

    /**
     * 强制同步远程员工基本信息到本地
     * @return 同步统计结果
     */
    Map<String, Object> forceSyncPersonBasicInfos();

    /**
     * 强制同步远程员工工作信息到本地
     * @return 同步统计结果
     */
    Map<String, Object> forceSyncPersonJobInfos();
}
