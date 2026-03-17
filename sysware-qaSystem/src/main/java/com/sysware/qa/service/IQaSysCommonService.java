package com.sysware.qa.service;

import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.domain.vo.QaSysVo;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 问题回答通用Service接口
 *
 * @author aa
 * @date 2025-07-11
 */
public interface IQaSysCommonService {
    /**
     * 查询问题回答
     */
    QaSysVo queryById(String recordId, String mode);

    /**
     * 查询右侧表单
     */
    TableDataInfo queryPageList(QaSysBo bo, PageQuery pageQuery);

    /**
     * 刷新缓存
     */
    void refreshCache();

    /**
     * 更新访问量
     */
    R<Void> updateVisits(String recordId);

    /**
     * 获取用户页面用户数据
     */
    R<QaSysVo> getUser();

    /**
     * 获取用户下拉框信息（全部）
     */
    R<List<Map<String, Object>>> getUserOption();

    /**
     * 获取问题类型下拉框信息（全部）
     */
    R<List<Map<String, Object>>> getTypeOption();

    /**
     * 获取问题类型下拉框信息（筛选占有位为0的）
     */
    R<List<Map<String, Object>>> getPartTypeOption();

    /**
     * 获取密级信息下拉框信息（全部）
     */
    R<List<Map<String, Object>>> getSecurityOption(String securityType);

    /**
     * 开启消息提醒
     */
    R<Void> openNotify();

    /**
     * 关闭消息提醒
     */
    R<Void> closeNotify();

    /**
     * 将消息提醒位置1（提醒）
     */
    R<Void> setNotify(String recordId);

    /**
     * 将消息提醒位置0（不提醒）
     */
    R<Void> markNotified(Collection<String> ids);
}
