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
 * 主数据同步批次记录Service业务层处理
 */
@Service
@RequiredArgsConstructor
public class HdlMainDataSyncBatchServiceImpl implements IHdlMainDataSyncBatchService {

    private final HdlMainDataSyncBatchMapper baseMapper;

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

    @Override
    public void saveBatch(HdlMainDataSyncBatch batch) {
        baseMapper.insert(batch);
    }

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
