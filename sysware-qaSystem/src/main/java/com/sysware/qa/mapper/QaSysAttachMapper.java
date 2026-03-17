package com.sysware.qa.mapper;

import com.sysware.qa.domain.QaSysAttach;
import com.sysware.qa.domain.vo.QaSysAttachVo;
import com.sysware.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问题回答附件Mapper接口
 *
 * @author aa
 * @date 2025-07-20
 */
public interface QaSysAttachMapper extends BaseMapperPlus<QaSysAttachMapper, QaSysAttach, QaSysAttachVo> {
    List<QaSysAttachVo> selectByRecordIdAndType(@Param("recordId") String recordId, @Param("attachType") String attachType);
    List<QaSysAttachVo> selectByOssId(@Param("ossId") Long ossId);
    List<QaSysAttachVo> selectByRecordId(@Param("recordId") String recordId);
}
