package com.sysware.mainData.service;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.mainData.domain.HdlMainDataSyncBatch;
import com.sysware.mainData.domain.bo.HdlMainDataSyncBatchBo;
import com.sysware.mainData.domain.vo.HdlMainDataSyncBatchVo;

import java.util.List;
/**
 * @project npic
 * @description IHdlMainDataSyncBatchService服务接口，定义主数据同步批次记录能力与调用契约。
 * @author DavidLee233
 * @date 2026/3/20
 */
public interface IHdlMainDataSyncBatchService {
/**
 * @description 按筛选条件分页查询主数据同步批次记录并封装为表格数据返回。
 * @params bo 主数据同步批次记录分页查询条件业务对象
 * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
 *
  * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
 * @author DavidLee233
 * @date 2026/3/20
 */
TableDataInfo queryPageList(HdlMainDataSyncBatchBo bo, PageQuery pageQuery);
/**
 * @description 按业务条件查询主数据同步批次记录数据并返回处理结果。
 * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
 * @params limit 返回记录数量上限
 *
  * @return List<HdlMainDataSyncBatchVo> 主数据同步批次列表结果，用于批量处理或前端展示。
 * @author DavidLee233
 * @date 2026/3/20
 */
List<HdlMainDataSyncBatchVo> queryLatestByType(String dataType, Integer limit);
/**
 * @description 保存主数据同步批次记录到数据库。
 * @params batch 同步批次记录实体
 *
  * @return void 无返回值，方法执行后通过副作用更新系统状态。
 * @author DavidLee233
 * @date 2026/3/20
 */
void saveBatch(HdlMainDataSyncBatch batch);
/**
 * @description 按业务条件查询主数据同步批次记录数据并返回处理结果。
 * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
 *
  * @return HdlMainDataSyncBatch 主数据同步批次相关业务结果。
 * @author DavidLee233
 * @date 2026/3/20
 */
HdlMainDataSyncBatch queryLatestSuccessByType(String dataType);
/**
 * @description 查询最近一次成功的主数据同步批次记录记录。
 * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
 *
  * @return HdlMainDataSyncBatch 主数据同步批次相关业务结果。
 * @author DavidLee233
 * @date 2026/3/20
 */
HdlMainDataSyncBatch queryLatestSuccess(String triggerMode);
}