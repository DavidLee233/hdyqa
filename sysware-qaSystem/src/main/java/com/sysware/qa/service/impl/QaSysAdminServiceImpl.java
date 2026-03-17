package com.sysware.qa.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.domain.vo.QaSysVo;
import com.sysware.qa.mapper.QaSysMapper;
import com.sysware.qa.service.IQaSysAdminService;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.sysware.qa.actions.adminAction.buildAdminQueryWrapper;
import static com.sysware.qa.actions.adminAction.buildQueryWrapper;
import static com.sysware.qa.actions.adminAction.exportRes;

/**
 * @FileName QaSysServiceImpl
 * @Author lxd
 * @Description
 * @date 2025/7/19
 **/
@RequiredArgsConstructor
@Service
public class QaSysAdminServiceImpl implements IQaSysAdminService {

    private final QaSysMapper baseMapper;

    /**
     * @author lxd
     * @description: 查询管理员左侧页面数据
     * @param bo
     * @param pageQuery
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/19
     **/
    @Override
    public TableDataInfo queryAdminPageList(QaSysBo bo, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<QaSys> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<QaSysVo> result = baseMapper.selectVoPage(page, buildAdminQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }

    /**
     * @author lxd
     * @description: 查询需提醒的问题回答列表
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/19
     **/
    @Override
    public R<List<QaSys>> queryAdminNotifyList() {
        List<QaSys> notifyList = baseMapper.selectByAdminNotify();
        return R.ok(notifyList);
    }

    /**
     * @author lxd
     * @description: 查询管理员右侧页面数据
     * @param bo
     * @param pageQuery
     * @return com.sysware.common.core.page.TableDataInfo
     * @date 2025/7/19
     **/
    @Override
    public TableDataInfo queryPageList(QaSysBo bo, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<QaSys> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<QaSysVo> result = baseMapper.selectVoPage(page, buildQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }


    /**
     * @author lxd
     * @description: 导出Excel
     * @param exportForm
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/19
     **/
    @Override
    public R<Void> exportExcel(Map<String, String> exportForm) {
        try {
            return exportRes(exportForm, baseMapper);
        }catch (Exception e) {
            return R.fail("导出失败");
        }
    }
}
