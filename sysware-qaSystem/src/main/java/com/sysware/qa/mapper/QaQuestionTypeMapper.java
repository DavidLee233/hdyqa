package com.sysware.qa.mapper;

import com.sysware.qa.domain.QaQuestionType;
import com.sysware.qa.domain.vo.QaQuestionTypeVo;
import com.sysware.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问题类型表Mapper接口
 *
 * @author aa
 * @date 2025-08-18
 */
public interface QaQuestionTypeMapper extends BaseMapperPlus<QaQuestionTypeMapper, QaQuestionType, QaQuestionTypeVo> {

    /**
     * 查询所有的问题类型
     * @return
     */
    List<QaQuestionType> selectAllType();
    /**
     * 查询占有位为0的问题类型
     * @return
     */
    List<QaQuestionType> selectPartType();

    QaQuestionType selectByType(@Param("type") String type);
}
