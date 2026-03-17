package com.sysware.qa.service;

import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.qa.domain.vo.QaSysVo;

import java.util.List;
import java.util.Map;

/**
 * 问题回答Service接口
 *
 * @author aa
 * @date 2025-07-11
 */
public interface IQaSysAdminService {
    /**
     * 导出excel
     */
    R<Void> exportExcel(Map<String, String> exportForm);
    /**
     * 查询左侧表单
     */
    TableDataInfo queryAdminPageList(QaSysBo bo, PageQuery pageQuery);
    /**
     * 查询右侧表单
     */
    TableDataInfo queryPageList(QaSysBo bo, PageQuery pageQuery);
    /**
     * 查询需提醒的问题回答列表
     */
    R<List<QaSys>> queryAdminNotifyList();
}
