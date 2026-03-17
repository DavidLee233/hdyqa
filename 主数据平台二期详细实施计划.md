# 主数据平台二期详细实施计划

## Summary

- 以现有 sysware-mainData 和主数据三张本地表为基础继续扩展，不推翻已完成的字段映射与本地 CRUD。
- 本期统一按以下策略落地：
  - 远端同步：全量同步主流程 + 结构预留增量
  - 数据归并：远端主导覆盖，纯本地人工记录保留
  - 下游提供方式：三类独立接口
  - 下游鉴权：复用现有 Bearer Token
  - 调度方式：复用现有 xxl-job 配置
- 先补齐三类数据的“远端浏览、强制同步、定时同步、备份、下游接口”，再补同步日志与运维可见性。

## Key Changes

### 1. 统一主数据模型与映射规则

- 定义统一枚举 MainDataType，固定为：
  - ORG_DEPT = 1
  - PERSON_BASIC = 2
  - PERSON_JOB = 3
- 修正当前“字段映射 type 值不一致”问题：
  - 现状里代码已使用部门类型 0，但 SQL 元数据说明是 1/2/3
  - 本期统一迁移到 1/2/3，并在后端兼容读取旧值 0 -> 1 一次过渡，避免已有数据立即失效
- 三张本地业务表直接作为最终主数据表，不新增影子业务表；每张表补充同步元数据字段：
  - data_source：REMOTE / LOCAL
  - sync_batch_no
  - last_sync_time
  - sync_deleted_flag
  - sync_hash 或 remote_update_mark
- 业务主键直接沿用现有字段：
  - 部门：pkDept
  - 员工基本：pkPsndoc
  - 员工工作：pkPsnjob

### 2. 远端接入与字段映射引擎

- 保留现有 Token 获取服务，补齐为统一远端访问入口：
  - Token 获取、刷新、有效期判断
  - 远端可用性检查
  - 三类数据统一查询模板
- 将 RemoteDataService 从“仅支持部门查询”扩展为三类通用能力：
  - queryRemoteOrgDept
  - queryRemotePersonBasic
  - queryRemotePersonJob
  - sync<Type>ToLocal
- 字段映射统一由一个映射组件处理，不在各 controller/page 手工写死字段名。
- 同步时按“映射表 -> 远端字段 -> 本地字段”转换，缺失映射时直接记录错误并拒绝该类型同步启动。
- 当前部门远端浏览逻辑保留路径风格，员工两类补齐同等能力；/mainData/remote/** 只保留公共 token、同步状态、手工触发入口，不再承载具体列表查询。

### 3. 本地与远端数据归并规则

- 本地人工新增记录：
  - data_source = LOCAL
  - 不受远端全量同步删除影响
- 远端同步记录：
  - data_source = REMOTE
  - 按业务主键执行 upsert
  - 同步时远端字段覆盖本地同主键的 REMOTE 记录
- 同一主键若本地已存在 LOCAL 记录：
  - 不直接覆盖
  - 记为冲突日志并跳过
  - 本期不做冲突处理页面，只在同步日志中展示
- 全量同步完成后，对“本批未再出现的旧 REMOTE 记录”：
  - 不硬删除
  - 标记 sync_deleted_flag = 1
  - 默认不参与下游接口返回
  - 管理页增加“显示已失效远端记录”筛选

### 4. 页面与功能补齐

- 主数据菜单下统一为三类页面补齐相同交互：
  - 本地数据
  - 院主数据
- 三类页面都支持：
  - 本地：浏览、新增、修改、删除、导出
  - 远端：浏览、查询、强制同步
- 部门页面保留现有切换思路，但重构成可复用模式；员工基本、员工工作复用同一套切换框架。
- 新增公共运维页面：
  - 同步日志页：按批次、类型、结果、时间筛选
  - Token 状态卡片：当前 token 是否有效、上次刷新时间
  - 备份记录页：展示文件名、生成时间、保留状态

### 5. 强制同步与定时同步

- 新增三类手工同步接口：
  - /mainData/organizationDepartment/remote/sync
  - /mainData/personBasicInfo/remote/sync
  - /mainData/personJobInfo/remote/sync
- 新增一键全量同步入口：
  - /mainData/remote/sync/all
  - 执行顺序固定：组织部门 -> 员工基本 -> 员工工作
- 固定依赖顺序：
  - 先同步组织部门，再同步员工基本，再同步员工工作
  - 任一前置类型失败，后续类型不执行，本批记为失败
- 使用 xxl-job 新增两个任务处理器：
  - mainDataFullSyncJob
  - mainDataWeeklyBackupJob
- 默认调度建议：
  - 全量同步：每天 01:00
  - 周备份：每周日 02:00
- 调度时间不写死在代码，最终由 xxl-job 控制台维护。

### 6. 备份与保留策略

- 备份与远端同步允许并行执行，不共用互斥锁。
- 备份文件命名固定：yyyyMMdd_backup.sql，例如 20260317_backup.sql
- 备份目录固定为独立主数据备份目录，不与普通上传目录混放。
- 每次周备份完成后执行清理：
  - 仅保留最近 10 周文件
  - 超出部分按文件日期从旧到新删除
- 备份结果写入备份记录表，至少包含：
  - 文件名
  - 文件路径
  - 开始/结束时间
  - 文件大小
  - 执行结果
  - 错误信息

### 7. 下属系统开放接口

- 提供三类独立只读接口：
  - /open-api/main-data/organization-departments
  - /open-api/main-data/person-basic-infos
  - /open-api/main-data/person-job-infos
- 鉴权统一复用现有 Bearer Token。
- 接口统一支持参数：
  - pageNum
  - pageSize
  - updatedAfter
  - includeDeleted
- 接口默认仅返回：
  - data_source in (REMOTE, LOCAL)
  - sync_deleted_flag = 0
- 返回结构统一包含：
  - rows
  - total
  - type
  - syncBatchNo
  - syncTime
- updatedAfter 本期即可生效，基于 last_sync_time / update_time 过滤，为后续增量消费预留。

### 8. 日志、审计与失败处理

- 新增同步批次日志表，记录：
  - 批次号
  - 类型
  - 触发方式（manual/job）
  - 开始/结束时间
  - 拉取条数
  - 新增/更新/失效/冲突/失败数量
  - 执行人或任务名
- 同步失败不中断系统运行，但必须落日志。
- Token 失效时先自动刷新一次，再重试当前远端请求一次；重试仍失败则记失败。
- 每个同步任务加分布式锁，避免同一类型并发同步。
- 备份任务单独加锁，避免重复备份。

## Public APIs / Interfaces

- 新增或调整后端接口：
  - /mainData/organizationDepartment/remote/list
  - /mainData/personBasicInfo/remote/list
  - /mainData/personJobInfo/remote/list
  - /mainData/organizationDepartment/remote/sync
  - /mainData/personBasicInfo/remote/sync
  - /mainData/personJobInfo/remote/sync
  - /mainData/remote/sync/all
  - /mainData/remote/token/info
  - /mainData/remote/token/refresh
  - /open-api/main-data/organization-departments
  - /open-api/main-data/person-basic-infos
  - /open-api/main-data/person-job-infos
- 数据库变更：
  - 三张主数据表补充同步元数据字段
  - 新增同步日志表
  - 新增备份记录表
- 前端变更：
  - 三个主数据页面统一成本地/院主数据双视图
  - 新增同步日志页、备份记录页、Token 状态展示

## Test Plan

- 字段映射
  - 三类数据分别验证映射正确入库
  - 缺失映射时禁止同步并给出明确错误
  - 兼容旧部门类型 0 的映射读取
- 远端浏览
  - 三类远端分页、筛选、空结果、远端异常
  - Token 失效后自动刷新并重试
- 同步入库
  - 远端新增、本地无记录
  - 远端更新已有 REMOTE 记录
  - 远端缺失旧 REMOTE 记录并标记失效
  - 本地 LOCAL 记录与远端同主键冲突时跳过并记日志
  - 三类顺序同步依赖正确
- 页面能力
  - 三类页面本地/远端切换一致
  - 远端页只有浏览和强制同步，无增删改
  - 本地导出不受远端页逻辑影响
- 开放接口
  - Bearer Token 鉴权通过/失败
  - 分页、updatedAfter、includeDeleted
  - 默认仅返回有效数据
- 备份
  - 生成文件名符合 20260317_backup.sql
  - 周备份执行成功
  - 第 11 周自动清理最旧备份
  - 与同步任务并行执行时互不阻塞

## Assumptions

- 远端三类接口均可按现有 RemoteDataConfig 的三个 path 访问，且能返回可分页数据。
- 本期不同步设计独立 AppKey/AppSecret 鉴权，开放接口直接复用现有 Bearer Token。
- 本期不做“本地优先冲突处理页面”，冲突仅记录日志并人工处理。
- 本期以全量同步为准，增量只做接口与字段预留，不交付完整增量算法。
- xxl-job 配置已存在但当前未发现现成主数据 JobHandler，本期直接新增两个 handler。