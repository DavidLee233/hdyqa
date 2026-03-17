package com.sysware.qa.actions;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.domain.vo.QaSysVo;
import com.sysware.qa.mapper.QaSysMapper;
import com.sysware.qa.utils.excelUtils;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.sysware.qa.actions.commonAction.queryWithCondition;

public class adminAction {
    /**
     * @author lxd
     * @description: 进行管理员左侧表格页面查询
     * @param bo
     * @param pageQuery
     * @return LambdaQueryWrapper<com.sysware.archives.domain.QaSys>
     * @date 2025/7/19
     **/
    public static LambdaQueryWrapper<QaSys> buildAdminQueryWrapper(QaSysBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<QaSys> lqw = Wrappers.lambdaQuery();
        // 查询所有人的问题回答组，不需要筛选人
        // 管理者在左边只需要看到“待处理”和“"已处理"状态的问题
        lqw.and(wrapper -> wrapper
                .eq(QaSys::getStatus, "待处理")
                .or()
                .eq(QaSys::getStatus, "已处理")
        );
        return lqw;
    }

    /**
     * @author lxd
     * @description: 进行模糊（可选）和排序字段搜索（可选）查询
     * @param bo
     * @return LambdaQueryWrapper<com.sysware.archives.domain.QaSys>
     * @date 2025/7/19
     **/
    public static LambdaQueryWrapper<QaSys> buildQueryWrapper(QaSysBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<QaSys> lqw = Wrappers.lambdaQuery();
        // 进行人员查询->模糊查询->排序查询的三级搜索
        queryWithCondition(bo, pageQuery, lqw);
        return lqw;
    }

    /**
     * @author lxd
     * @description: 导出表格数据并返回表格绝对路径
     * @param exportForm
     * @param baseMapper
     * @return R<Void>
     * @date 2025/7/19
     **/
    public static R<Void> exportRes(Map<String, String> exportForm, QaSysMapper baseMapper) throws IOException {
        // 1. 验证参数
        String dataRange = exportForm.get("dataRange");
        String savePath = exportForm.get("savePath");
        String saveMode = exportForm.get("saveMode");
        if (dataRange == null || dataRange.isEmpty()) {
            return R.fail("时间范围不能为空");
        }
        if (savePath == null || savePath.isEmpty()) {
            return R.fail("保存路径不能为空");
        }
        // 2. 获取日期范围
        List<LocalDateTime> dateRange = excelUtils.getDateRange(dataRange);
        if (dateRange == null || dateRange.size() < 2) {
            return R.fail("获取日期范围失败");
        }
        LocalDateTime startDate = dateRange.get(0);
        LocalDateTime endDate = dateRange.get(1);
        // 3. 查询数据
        List<QaSysVo> list = baseMapper.selectQaDataByRange(startDate, endDate);
        if (CollectionUtils.isEmpty(list)) {
            return R.fail("该时间范围内无数据可导出");
        }
        // 4. 选择导出字段
        String[] fields = excelUtils.getFieldsBySaveMode(saveMode);
        // 5. 导出Excel
        return excelUtils.exportToExcel(list, savePath, fields);
    }
}
