package com.sysware.qa.service;

import cn.hutool.core.lang.Opt;
import com.sysware.common.core.domain.R;
import com.sysware.qa.domain.QaQuestionType;
import com.sysware.qa.domain.vo.QaQuestionTypeVo;
import com.sysware.qa.domain.bo.QaQuestionTypeBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 问题类型表Service接口
 *
 * @author aa
 * @date 2025-08-19
 */
public interface IQaQuestionTypeService {

    /**
     * 查询问题类型表
     */
    QaQuestionTypeVo queryById(String questionId);

    /**
     * 查询问题类型表列表
     */
    TableDataInfo queryPageList(QaQuestionTypeBo bo, PageQuery pageQuery);

    /**
     * 新增问题类型表
     */
    R<Void> insertByBo(QaQuestionTypeBo bo);

    /**
     * 修改问题类型表
     */
    R<Void> updateByBo(QaQuestionTypeBo bo);

    /**
     * 校验并批量删除问题类型表信息
     */
    R<Void> deleteWithValidByIds(Collection<String> ids);

    /**
     * 获取所有的问题类型信息
     */
    List<QaQuestionType> selectTypeList();
    /**
     * 获取问题类型顺序
     */
    R<Long> getSeqenceNum();
    /**
     * 获取占有位为0的问题类型信息
     */
    List<QaQuestionType> selectPartTypeList();
}
