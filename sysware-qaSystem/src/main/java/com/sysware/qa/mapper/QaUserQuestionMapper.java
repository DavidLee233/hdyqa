package com.sysware.qa.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.qa.domain.QaUserQuestion;
import com.sysware.qa.domain.vo.QaUserQuestionVo;
import com.sysware.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

/**
 * 问题回答类型角色关联Mapper接口
 *
 * @author aa
 * @date 2025-08-18
 */
public interface QaUserQuestionMapper extends BaseMapperPlus<QaUserQuestionMapper, QaUserQuestion, QaUserQuestionVo> {
    /**
     * 从qa_user_question表中获取user_id从而从sys_user表中获取到userName和loginName，
     * 从qa_user_question表中获取type_id从而从qa_question_type中获取到type
     */
    IPage<QaUserQuestionVo> selectNewVoPage(Page<QaUserQuestion> page, @Param("ew") QueryWrapper ew);
    /**
     * 三表联查获取用户和问题类型信息
     */
    QaUserQuestionVo selectNewVoById(@Param("id") String id);

    QaUserQuestionVo selectNewVoByTypeId(@Param("typeId") String typeId);

    QaUserQuestionVo selectNewVoByType(@Param("type") String type);
}
