package com.sysware.qa.service;

import com.sysware.common.core.domain.R;
import com.sysware.qa.domain.QaUserQuestion;
import com.sysware.qa.domain.vo.QaUserQuestionVo;
import com.sysware.qa.domain.bo.QaUserQuestionBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 问题回答类型角色关联Service接口
 *
 * @author aa
 * @date 2025-08-18
 */
public interface IQaUserQuestionService {

    /**
     * 查询问题回答类型角色关联
     */
    QaUserQuestionVo queryById(String id);

    /**
     * 查询问题回答类型角色关联列表
     */
    TableDataInfo queryPageList(QaUserQuestionBo bo, PageQuery pageQuery);

    /**
     * 新增问题回答类型角色关联
     */
    R<Void> insertByBo(QaUserQuestionBo bo);

    /**
     * 修改问题回答类型角色关联
     */
    R<Void> updateByBo(QaUserQuestionBo bo);

    /**
     * 校验并批量删除问题回答类型角色关联信息
     */
    R<Void> deleteWithValidByIds(Collection<String> ids);
}
