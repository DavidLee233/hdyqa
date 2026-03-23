package com.sysware.mainData.service;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.mainData.domain.HdlMainDataBackupRecord;
import com.sysware.mainData.domain.bo.HdlMainDataBackupRecordBo;
import com.sysware.mainData.domain.vo.HdlMainDataBackupRecordVo;

import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * @project npic
 * @description IHdlMainDataBackupRecordService服务接口，定义主数据备份记录能力与调用契约。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface IHdlMainDataBackupRecordService {
/**
 * @description 按筛选条件分页查询主数据备份记录并封装为表格数据返回。
 * @params bo 主数据备份记录分页查询条件业务对象
 * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
 *
  * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
 * @author DavidLee233
 * @date 2026/3/20
 */
TableDataInfo queryPageList(HdlMainDataBackupRecordBo bo, PageQuery pageQuery);
/**
 * @description 查询最近的主数据备份记录记录列表。
 * @params limit 返回记录数量上限
 *
  * @return List<HdlMainDataBackupRecordVo> 主数据备份记录列表结果，用于批量处理或前端展示。
 * @author DavidLee233
 * @date 2026/3/20
 */
List<HdlMainDataBackupRecordVo> queryLatest(Integer limit);
/**
 * @description 查询最近一次成功的主数据备份记录记录。
 * @params 无
 *
  * @return HdlMainDataBackupRecord 主数据备份记录相关业务结果。
 * @author DavidLee233
 * @date 2026/3/20
 */
HdlMainDataBackupRecord queryLatestSuccess();
/**
 * @description 保存主数据备份记录到数据库。
 * @params record 备份记录实体
 *
  * @return void 无返回值，方法执行后通过副作用更新系统状态。
 * @author DavidLee233
 * @date 2026/3/20
 */
void saveRecord(HdlMainDataBackupRecord record);
/**
 * @description 执行主数据备份流程并返回批次统计信息。
 * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
 *
  * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Map<String, Object> executeBackup(String triggerMode);
/**
 * @description 执行主数据备份流程并返回批次统计信息。
 * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
 * @params handlerName 本地任务处理器名称
 *
  * @return Map<String, Object> 键值结构业务结果（包含统计信息、数据内容或错误详情）。
 * @author DavidLee233
 * @date 2026/3/20
 */
Map<String, Object> executeBackup(String triggerMode, String handlerName);
/**
 * @description 按指定可参与定时备份的数据类型集合执行主数据备份流程，并返回本次备份的文件与统计结果。
 * @params triggerMode 触发方式（manual手动触发或handler本地处理器触发）。
 * @params handlerName 本地处理器名称。
 * @params enabledDataTypes 允许参与本次备份的数据类型编码集合，用于按页面开关筛选需备份的业务表。
 *
 * @return Map<String, Object> 备份执行结果（包含文件名、文件路径、备份表数、备份行数与提示信息），用于处理器日志与前端结果展示。
 * @author DavidLee233
 * @date 2026/3/21
 */
Map<String, Object> executeBackup(String triggerMode, String handlerName, Set<String> enabledDataTypes);
}
