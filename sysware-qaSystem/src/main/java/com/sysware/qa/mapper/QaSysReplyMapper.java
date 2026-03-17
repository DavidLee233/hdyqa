package com.sysware.qa.mapper;

import com.sysware.qa.domain.QaSysReply;
import com.sysware.qa.domain.vo.QaSysReplyVo;
import com.sysware.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 问答论坛回复Mapper接口
 *
 * @author aa
 * @date 2025-07-26
 */
public interface QaSysReplyMapper extends BaseMapperPlus<QaSysReplyMapper, QaSysReply, QaSysReplyVo> {
    /**
     * @author lxd
     * @description: 根据反馈记录id查询回复列表
     * @param recordId
     * @return java.util.List<com.sysware.archives.domain.vo.QaSysReplyVo>
     * @date 2025/7/26
     **/
    List<QaSysReplyVo> selectByRecordId(@Param("recordId") String recordId);
    /**
     * @author lxd
     * @description: 点赞加一
     * @param replyId
     * @return int
     * @date 2025/7/26
     **/
    int thumbsUp(@Param("replyId") String replyId);
    /**
     * 查询指定记录的回复总数
     */
    @Select("SELECT COUNT(*) FROM qa_sys_reply WHERE record_id = #{recordId}")
    int countByRecordId(@Param("recordId") String recordId);
    /**
     * 查询某个问题的回复对应楼层数
     */
    @Select("SELECT COUNT(*) + 1 FROM qa_sys_reply " +
            "WHERE record_id = #{recordId} AND " +
            "(create_time < (SELECT create_time FROM qa_sys_reply WHERE reply_id = #{replyId}) " +
            "OR (create_time = (SELECT create_time FROM qa_sys_reply WHERE reply_id = #{replyId}) " +
            "AND reply_id < #{replyId}))")
    int getFloorById(@Param("recordId") String recordId, @Param("replyId") String replyId);
}
