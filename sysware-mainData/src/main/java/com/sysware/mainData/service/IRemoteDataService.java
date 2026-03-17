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
     * 导出远程组织部门数据
     * @param params 查询参数
     * @return 导出文件字节数组
     */
    byte[] exportRemoteDepartments(Map<String, Object> params);
}
