package com.sysware.qa.service;

import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;

import java.util.List;

/**
 * 问题回答Service接口
 *
 * @author aa
 * @date 2025-07-11
 */
public interface IQaSysAnswerService {
    /**
     * 回答问题
     */
    R<Void> answerByBo(QaSysBo bo);
    /**
     * 查询左侧表单
     */
    TableDataInfo queryAPageList(QaSysBo bo, PageQuery pageQuery);
    /**
     * 查询需提醒的问题回答列表
     */
    R<List<QaSys>> queryAnswerNotifyList();
}
