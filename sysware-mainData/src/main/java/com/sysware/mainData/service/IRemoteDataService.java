package com.sysware.mainData.service;

import java.util.Map;
/**
 * @project npic
 * @description IRemoteDataService服务接口，定义远端主数据能力与调用契约。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface IRemoteDataService {
/**
 * @description 查询远端组织部门数据并转换为统一返回结构。
 * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
 *
  * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Map<String, Object> queryRemoteDepartments(Map<String, Object> params);
/**
 * @description 查询远端员工基本信息并转换为统一返回结构。
 * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
 *
  * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Map<String, Object> queryRemotePersonBasicInfos(Map<String, Object> params);
/**
 * @description 查询远端员工工作信息并转换为统一返回结构。
 * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
 *
  * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Map<String, Object> queryRemotePersonJobInfos(Map<String, Object> params);
/**
 * @description 导出远端主数据数据并输出为文件流。
 * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
 *
  * @return byte[] 导出文件的二进制内容，可用于直接下载或写入响应流。
 * @author DavidLee233
 * @date 2026/3/20
 */
byte[] exportRemoteDepartments(Map<String, Object> params);
/**
 * @description 导出远端主数据数据并输出为文件流。
 * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
 *
  * @return byte[] 导出文件的二进制内容，可用于直接下载或写入响应流。
 * @author DavidLee233
 * @date 2026/3/20
 */
byte[] exportRemotePersonBasicInfos(Map<String, Object> params);
/**
 * @description 导出远端主数据数据并输出为文件流。
 * @params params 动态参数集合（承载远端查询条件、分页与同步模式）
 *
  * @return byte[] 导出文件的二进制内容，可用于直接下载或写入响应流。
 * @author DavidLee233
 * @date 2026/3/20
 */
byte[] exportRemotePersonJobInfos(Map<String, Object> params);
/**
 * @description 执行组织部门远端到本地同步并记录批次统计。
 * @params 无
 *
  * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Map<String, Object> forceSyncDepartments();
/**
 * @description 执行组织部门远端到本地同步并记录批次统计。
 * @params syncMode 同步模式（FULL全量或INCREMENTAL增量）
 * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
 *
  * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Map<String, Object> forceSyncDepartments(String syncMode, String triggerMode);
/**
 * @description 执行员工基本信息远端到本地同步并记录批次统计。
 * @params 无
 *
  * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Map<String, Object> forceSyncPersonBasicInfos();
/**
 * @description 执行员工基本信息远端到本地同步并记录批次统计。
 * @params syncMode 同步模式（FULL全量或INCREMENTAL增量）
 * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
 *
  * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Map<String, Object> forceSyncPersonBasicInfos(String syncMode, String triggerMode);
/**
 * @description 执行员工工作信息远端到本地同步并记录批次统计。
 * @params 无
 *
  * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Map<String, Object> forceSyncPersonJobInfos();
/**
 * @description 执行员工工作信息远端到本地同步并记录批次统计。
 * @params syncMode 同步模式（FULL全量或INCREMENTAL增量）
 * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
 *
  * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Map<String, Object> forceSyncPersonJobInfos(String syncMode, String triggerMode);
}