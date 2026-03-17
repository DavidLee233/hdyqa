package com.sysware.qa.mapper;

import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.vo.QaSysVo;
import com.sysware.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 问题回答Mapper接口
 *
 * @author aa
 * @date 2025-07-16
 */
public interface QaSysMapper extends BaseMapperPlus<QaSysMapper, QaSys, QaSysVo> {
    /**
     * @description: 根据日期范围查询QA数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return QA数据列表
     */
    List<QaSysVo> selectQaDataByRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);
    /**
     * @author lxd
     * @description: 根据recordId更新访问量
     * @param recordId
     * @return boolean
     * @date 2025/7/20
     **/
    int updateVisits(@Param("recordId") String recordId);

    /**
     * @description: 筛选出需要提醒的数据（管理员）
     * @return QA数据列表
     */
    List<QaSys> selectByAdminNotify();
    /**
     * @description: 筛选出需要提醒的数据（提问者）
     * @return QA数据列表
     */
    List<QaSys> selectByQuestionNotify(@Param("createId") String createId);
    /**
     * @description: 筛选出需要提醒的数据（回答者）
     * @return QA数据列表
     */
    List<QaSys> selectByAnswerNotify(@Param("updateId") String updateId);
}

