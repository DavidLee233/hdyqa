package com.sysware.mainData.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.utils.StringUtils;
import com.sysware.mainData.domain.HdlMainDataSyncBatch;
import com.sysware.mainData.domain.bo.HdlMainDataSyncBatchBo;
import com.sysware.mainData.domain.vo.HdlMainDataSyncBatchVo;
import com.sysware.mainData.mapper.HdlMainDataSyncBatchMapper;
import com.sysware.mainData.service.IHdlMainDataSyncBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
/**
 * @project npic
 * @description HdlMainDataSyncBatchServiceImpl服务实现类，负责主数据同步批次记录业务规则执行、数据处理与流程编排。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Service
@RequiredArgsConstructor
public class HdlMainDataSyncBatchServiceImpl implements IHdlMainDataSyncBatchService {

    private final HdlMainDataSyncBatchMapper baseMapper;

    /**
     * @description 按筛选条件分页查询主数据同步批次记录并封装为表格数据返回。
     * @params bo 主数据同步批次记录分页查询条件业务对象
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return TableDataInfo 表格分页结果（包含记录列表与总条数），用于前端列表展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public TableDataInfo queryPageList(HdlMainDataSyncBatchBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<HdlMainDataSyncBatch> lqw = buildQueryWrapper(bo);
        Page<HdlMainDataSyncBatch> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<HdlMainDataSyncBatch> result = baseMapper.selectPage(page, lqw);
        List<HdlMainDataSyncBatchVo> voList = BeanUtil.copyToList(result.getRecords(), HdlMainDataSyncBatchVo.class);
        Page<HdlMainDataSyncBatchVo> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return TableDataInfo.build(voPage);
    }

    /**
     * @description 按业务条件查询主数据同步批次记录数据并返回处理结果。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     * @params limit 返回记录数量上限
     *
      * @return List<HdlMainDataSyncBatchVo> 主数据同步批次列表结果，用于批量处理或前端展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public List<HdlMainDataSyncBatchVo> queryLatestByType(String dataType, Integer limit) {
        if (StringUtils.isBlank(dataType)) {
            return Collections.emptyList();
        }
        int effectiveLimit = (limit == null || limit <= 0) ? 5 : Math.min(limit, 20);
        LambdaQueryWrapper<HdlMainDataSyncBatch> lqw = Wrappers.lambdaQuery(HdlMainDataSyncBatch.class)
            .eq(HdlMainDataSyncBatch::getDataType, dataType)
            .orderByDesc(HdlMainDataSyncBatch::getStartTime)
            .last("limit " + effectiveLimit);
        List<HdlMainDataSyncBatch> list = baseMapper.selectList(lqw);
        return BeanUtil.copyToList(list, HdlMainDataSyncBatchVo.class);
    }

    /**
     * @description 保存主数据同步批次记录到数据库。
     * @params batch 同步批次记录实体
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public void saveBatch(HdlMainDataSyncBatch batch) {
        baseMapper.insert(batch);
    }

    /**
     * @description 按业务条件查询主数据同步批次记录数据并返回处理结果。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）
     *
      * @return HdlMainDataSyncBatch 主数据同步批次相关业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public HdlMainDataSyncBatch queryLatestSuccessByType(String dataType) {
        if (StringUtils.isBlank(dataType)) {
            return null;
        }
        return baseMapper.selectOne(Wrappers.lambdaQuery(HdlMainDataSyncBatch.class)
            .eq(HdlMainDataSyncBatch::getDataType, dataType)
            .eq(HdlMainDataSyncBatch::getSuccess, "1")
            .orderByDesc(HdlMainDataSyncBatch::getEndTime)
            .last("limit 1"));
    }

    /**
     * @description 查询最近一次成功的主数据同步批次记录记录。
     * @params triggerMode 触发方式（MANUAL手动触发或SCHEDULE定时触发）
     *
      * @return HdlMainDataSyncBatch 主数据同步批次相关业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Override
    public HdlMainDataSyncBatch queryLatestSuccess(String triggerMode) {
        LambdaQueryWrapper<HdlMainDataSyncBatch> lqw = Wrappers.lambdaQuery(HdlMainDataSyncBatch.class)
            .eq(HdlMainDataSyncBatch::getSuccess, "1");
        if (StringUtils.isNotBlank(triggerMode)) {
            lqw.eq(HdlMainDataSyncBatch::getTriggerMode, triggerMode);
        }
        lqw.orderByDesc(HdlMainDataSyncBatch::getEndTime).last("limit 1");
        return baseMapper.selectOne(lqw);
    }

    /**
     * @description 构建主数据同步批次记录处理所需的中间对象或条件。
     * @params bo 主数据同步批次记录业务请求对象（包含查询与变更字段）
     *
      * @return LambdaQueryWrapper<HdlMainDataSyncBatch> 主数据同步批次记录查询条件构造器，用于后续数据库过滤与排序。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private LambdaQueryWrapper<HdlMainDataSyncBatch> buildQueryWrapper(HdlMainDataSyncBatchBo bo) {
        LambdaQueryWrapper<HdlMainDataSyncBatch> lqw = Wrappers.lambdaQuery(HdlMainDataSyncBatch.class);
        if (bo == null) {
            return lqw.orderByDesc(HdlMainDataSyncBatch::getStartTime);
        }
        if (StringUtils.isNotBlank(bo.getBatchNo())) {
            lqw.like(HdlMainDataSyncBatch::getBatchNo, bo.getBatchNo());
        }
        if (StringUtils.isNotBlank(bo.getDataType())) {
            lqw.eq(HdlMainDataSyncBatch::getDataType, bo.getDataType());
        }
        if (StringUtils.isNotBlank(bo.getTriggerMode())) {
            lqw.eq(HdlMainDataSyncBatch::getTriggerMode, bo.getTriggerMode());
        }
        if (StringUtils.isNotBlank(bo.getSyncMode())) {
            lqw.eq(HdlMainDataSyncBatch::getSyncMode, bo.getSyncMode());
        }
        if (StringUtils.isNotBlank(bo.getSuccess())) {
            lqw.eq(HdlMainDataSyncBatch::getSuccess, bo.getSuccess());
        }
        if (bo.getStartTimeBegin() != null) {
            lqw.ge(HdlMainDataSyncBatch::getStartTime, bo.getStartTimeBegin());
        }
        if (bo.getStartTimeEnd() != null) {
            lqw.le(HdlMainDataSyncBatch::getStartTime, bo.getStartTimeEnd());
        }
        lqw.orderByDesc(HdlMainDataSyncBatch::getStartTime);
        return lqw;
    }
}