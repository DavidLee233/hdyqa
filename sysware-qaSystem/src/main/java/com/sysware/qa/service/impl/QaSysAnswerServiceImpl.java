package com.sysware.qa.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.helper.LoginHelper;
import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysAttachBo;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.domain.vo.QaSysVo;
import com.sysware.qa.mapper.QaSysAttachMapper;
import com.sysware.qa.mapper.QaSysMapper;
import com.sysware.qa.service.IQaSysAnswerService;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.sysware.qa.actions.answerAction.buildAnswerQueryWrapper;
import static com.sysware.qa.actions.answerAction.checkAndUpdate;
import static com.sysware.qa.actions.commonAction.updateAttach;
import static com.sysware.qa.actions.commonAction.validEntityBeforeSave;

/**
 * @FileName QaSysServiceImpl
 * @Author lxd
 * @Description
 * @date 2025/7/19
 **/
@RequiredArgsConstructor
@Service
public class QaSysAnswerServiceImpl implements IQaSysAnswerService {

    private final QaSysMapper baseMapper;
    private final QaSysAttachMapper attachMapper;
    /**
     * @author lxd
     * @description: 查询问题回答分页列表（左侧回答者列表）
     * @param bo
     * @param pageQuery
     * @return com.sysware.common.core.page.TableDataInfo
     * @date 2025/7/31
     **/
    @Override
    public TableDataInfo queryAPageList(QaSysBo bo, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<QaSys> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<QaSysVo> result = baseMapper.selectVoPage(page, buildAnswerQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }

    /**
     * @author lxd
     * @description: 查询需提醒的问题回答列表
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/19
     **/
    @Override
    public R<List<QaSys>> queryAnswerNotifyList() {
        List<QaSys> notifyList = baseMapper.selectByAnswerNotify(LoginHelper.getLoginUser().getLoginName());
        return R.ok(notifyList);
    }

    /**
     * @author lxd
     * @description: 问题回答
     * @param bo
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/19
     **/
    @Override
    public R<Void> answerByBo(QaSysBo bo) {
        // 先查询数据库中的原始记录
        QaSys update = baseMapper.selectById(bo.getRecordId());
        if (update == null) {
            return R.fail("记录不存在");
        }
        // 将bo中非null的属性复制到update中
        BeanUtil.copyProperties(bo, update, CopyOptions.create().setIgnoreNullValue(true));
        // 已采纳QA组不可回答（仅管理员可回答）
        if (!checkAndUpdate(update)) {
            return R.fail("问题已采纳，请联系管理员修改");
        }
        validEntityBeforeSave(update, baseMapper);
        LocalDateTime now = LocalDateTime.now();
        update.setUpdateTime(now);
        boolean flag = baseMapper.updateById(update) > 0;
        List<QaSysAttachBo> attachBoList = bo.getAnswerAttachList();
        if(flag){
            // 附件逻辑
            updateAttach(attachBoList, update, attachMapper);
        }
        return flag ? R.ok() : R.fail("回答失败，请稍后再试");
    }


}
