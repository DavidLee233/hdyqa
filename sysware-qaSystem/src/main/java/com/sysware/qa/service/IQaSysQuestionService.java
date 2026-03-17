package com.sysware.qa.service;

import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 问题回答Service接口
 *
 * @author aa
 * @date 2025-07-11
 */
public interface IQaSysQuestionService {
    /**
     * 新增问题回答
     */
    R<Void> insertByBo(QaSysBo bo);

    /**
     * 修改问题回答（提问者与管理员）
     */
    R<Void> updateByBo(QaSysBo bo);
    /**
     * 校验并批量删除问题回答信息
     */
    R<Void> deleteWithValidByIds(Collection<String> ids, Boolean isValid);
    /**
     * 采纳回答
     */
    R<Void> acceptByIds(Collection<String> ids);
    /**
     * 查询左侧表单
     */
    TableDataInfo queryQPageList(QaSysBo bo, PageQuery pageQuery);
    /**
     * 查询需提醒的问题回答列表
     */
    R<List<QaSys>> queryQuestionNotifyList();
}
