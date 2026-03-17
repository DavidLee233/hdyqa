package com.sysware.qa.service;

import com.sysware.qa.domain.QaSysReply;
import com.sysware.qa.domain.bo.QaSysReplyBo;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;

/**
 * 问答论坛回复Service接口
 *
 * @author aa
 * @date 2025-07-26
 */
public interface IQaSysReplyService {
    /**
     * 查询问答论坛回复列表
     */
    TableDataInfo queryPageList(QaSysReplyBo bo, PageQuery pageQuery);
    /**
     * 校验并批量删除问答论坛回复信息
     */
    R<Void> deleteWithValidByIds(Collection<String> ids);
    /**
     * 点赞问答论坛回复
     */
    int thumbsUp(String replyId);
    /**
     * 新增问答论坛回复
     */
    R<QaSysReply> insertByBo(QaSysReplyBo bo);
    /**
     * 获取全局回复楼层号
     */
    R<Integer> getFloorNum(String replyId);
    /**
     * 获取全局回复楼层号的页码数
     */
    R<Integer> getPageNum(String replyId, int pageSize);

}
