package com.sysware.qa.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.qa.domain.QaQuestionType;
import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysAttachBo;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.domain.vo.QaSysVo;
import com.sysware.qa.domain.vo.QaUserQuestionVo;
import com.sysware.qa.mapper.*;
import com.sysware.qa.service.IQaSysQuestionService;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysRole;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.helper.LoginHelper;
import com.sysware.system.service.ISysRoleService;
import com.sysware.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.sysware.qa.actions.commonAction.updateAttach;
import static com.sysware.qa.actions.commonAction.validEntityBeforeSave;
import static com.sysware.qa.actions.questionAction.*;

/**
 * @FileName QaSysServiceImpl
 * @Author lxd
 * @Description
 * @date 2025/7/19
 **/
@RequiredArgsConstructor
@Service
public class QaSysQuestionServiceImpl implements IQaSysQuestionService {

    private final QaSysMapper baseMapper;
    private final ISysRoleService roleService;
    private final QaSysAttachMapper attachMapper;
    private final QaSysReplyMapper replyMapper;
    private final QaQuestionTypeMapper questionTypeMapper;
    private final QaUserQuestionMapper userQuestionMapper;
    private final ISysUserService userService;
    /**
     * @author lxd
     * @description: 查询问题回答分页列表（左侧提问者列表）
     * @param bo
     * @param pageQuery
     * @return com.sysware.common.core.page.TableDataInfo
     * @date 2025/7/31
     **/
    @Override
    public TableDataInfo queryQPageList(QaSysBo bo, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<QaSys> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<QaSysVo> result = baseMapper.selectVoPage(page, buildQuestionQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }

    /**
     * @author lxd
     * @description: 查询需提醒的问题回答列表
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/19
     **/
    @Override
    public R<List<QaSys>> queryQuestionNotifyList() {
        List<QaSys> notifyList = baseMapper.selectByQuestionNotify(LoginHelper.getLoginUser().getLoginName());
        return R.ok(notifyList);
    }

    /**
     * @author lxd
     * @description: 新增问题回答
     * @param bo
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/19
     **/
    @Override
    public R<Void> insertByBo(QaSysBo bo) {
        QaSys add = qaSysInsert(bo);
        validEntityBeforeSave(add, baseMapper);
        List<QaSysAttachBo> attachBoList = bo.getQuestionAttachList();
        // 问题类型对应回答人员和消息提醒
        insertUserQuestionNotify(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setRecordId(add.getRecordId());
            // 附件逻辑
            updateAttach(attachBoList, add, attachMapper);
        }
        return flag ? R.ok() : R.fail("新增失败，请稍后再试");
    }

    /**
     * @author lxd
     * @description: 插入问题对应人员以及消息提醒状态位
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/28
     **/
    public void insertUserQuestionNotify(QaSys qaSys){
        // 将问题类型对应到相应的处理人员中
        QaQuestionType questionType = questionTypeMapper.selectByType(qaSys.getType());
        // 只有被占用的问题才有对应的回答人
        if (questionType.getOccupied() == 1){
            QaUserQuestionVo userQuestion = userQuestionMapper.selectNewVoByTypeId(questionType.getTypeId());
            qaSys.setUpdateId(userQuestion.getLoginName());
            qaSys.setUpdateBy(userQuestion.getUserName());
            // 加了对应的回答人后（默认会提醒回答者一次）
            qaSys.setNotify(1L);
        }
    }

    /**
     * @author lxd
     * @description: 修改问题回答
     * @param bo
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/19
     **/
    @Override
    public R<Void> updateByBo(QaSysBo bo) {
        // 先查询数据库中的原始记录
        QaSys update = baseMapper.selectById(bo.getRecordId());
        if (update == null) {
            return R.fail("记录不存在");
        }

        // 更新问题类型对应回答人员和消息提醒
        updateUserQuestionNotify(update, bo);

        // 将bo中非null的属性复制到update中
        BeanUtil.copyProperties(bo, update, CopyOptions.create().setIgnoreNullValue(true));
        // 普通成员不可对已处理、不予处理和已采纳QA组进行修改，管理员可以
        List<SysRole> roles = roleService.selectRolesByUserId(LoginHelper.getLoginUser().getUserId());
        boolean isAdmin = roles.stream().anyMatch(role -> "问答管理员".equals(role.getRoleName()));
        if (!isAdmin && !update.getStatus().equals("待处理")){
            return R.fail("问题为非待处理状态，请联系管理员修改");
        }
        validEntityBeforeSave(update, baseMapper);

        boolean flag = baseMapper.updateById(update) > 0;

        List<QaSysAttachBo> question_attachBoList = bo.getQuestionAttachList();
        if(flag){
            // 附件逻辑
            updateAttach(question_attachBoList, update, attachMapper);
            if (isAdmin) {
                List<QaSysAttachBo> answer_attachBoList = bo.getAnswerAttachList();
                updateAttach(answer_attachBoList, update, attachMapper);
            }
        }
        return flag ? R.ok() : R.fail("修改失败，请稍后再试");
    }

    /**
     * @author lxd
     * @description: 更新问题对应人员以及消息提醒状态位
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/28
     **/
    public void updateUserQuestionNotify(QaSys beforeQa, QaSysBo afterQa){
        // 如果更新前后的问题类型相同则不更新对应处理人，再进行一次消息提醒即可
        if (beforeQa.getType().equals(afterQa.getType())){
            beforeQa.setNotify(1L);
            return;
        }
        // 将问题类型对应到相应的处理人员中
        QaQuestionType questionType = questionTypeMapper.selectByType(afterQa.getType());
        // 只有被占用的问题才有对应的回答人
        if (questionType.getOccupied() == 1){
            QaUserQuestionVo userQuestion = userQuestionMapper.selectNewVoByTypeId(questionType.getTypeId());
            beforeQa.setUpdateId(userQuestion.getLoginName());
            beforeQa.setUpdateBy(userQuestion.getUserName());
            // 加了对应的回答人后（默认会提醒回答者一次）
            beforeQa.setNotify(1L);
        }
    }

    /**
     * @author lxd
     * @description: 批量删除问题回答
     * @param ids
     * @param isValid
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/19
     **/
    @Override
    public R<Void> deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        // 先检查从前端传过来的list
        if (CollUtil.isEmpty(ids)){
            return R.fail("采纳的问题为空，请重新选择");
        }
        List<QaSys> updates = baseMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(updates)){
            return R.fail("未找到对应问题");
        }
        for (QaSys update : updates) {
            // 非管理员角色无法删除处于已采纳和已处理状态的问题组
            Set<String> restrictedStatus = new HashSet<>(Arrays.asList("已采纳", "已处理"));
            if (!"系统管理员".equals(LoginHelper.getUsername()) && restrictedStatus.contains(update.getStatus())) {
                return R.fail("问题处于" + update.getStatus() + "状态，请联系管理员删除");
            }
        }
        // 获取删除附件组的唯一ID
        Collection<Long> attachIds = getAttachIds(updates, attachMapper);
        // 获取删除回复组的唯一ID
        Collection<Long> replyIds = getReplyIds(updates, replyMapper);
        // 依次检查QA组和附件组是否在数据库中删除
        if(!(baseMapper.deleteBatchIds(ids) > 0)){
            return R.fail("问题删除失败，请稍后再试");
        }
        if(!(attachMapper.deleteBatchIds(attachIds) > 0)){
            return R.fail("问题附件删除失败，请稍后再试");
        }
        if(!(replyMapper.deleteBatchIds(replyIds) > 0)){
            return R.fail("问题回复删除失败，请稍后再试");
        }
        return R.ok();
    }

    /**
     * @author lxd
     * @description: 批量采纳问题回答
     * @param ids
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/19
     **/
    @Override
    public R<Void> acceptByIds(Collection<String> ids) {
        // 先检查从前端传过来的list
        if (CollUtil.isEmpty(ids)){
            return R.fail("请选择要采纳的问题");
        }
        List<QaSys> updates = baseMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(updates)){
            return R.fail("未找到对应问题");
        }
        // 分为四种情况不可更新已采纳状态
        for (QaSys update : updates) {
            // 情况一：有问答已被采纳
            if(update.getStatus().equals("已采纳")){
                return R.fail("问题已是采纳状态");
            }
            // 情况二：问题为待处理状态
            if(update.getStatus().equals("待处理")){
                return R.fail("问题是待处理状态");
            }
            // 情况三：回答者觉得问题无法解决
            if(update.getCanSolve().equals("无法解决")){
                return R.fail("问题无法解决");
            }
            // 情况四：回答内容为空
            if(update.getCanSolve().equals("可解决") && StrUtil.isBlank(update.getAnswerContent())) {
                return R.fail("问题回答内容为空");
            }
        }
        LocalDateTime now = LocalDateTime.now();
        updates.forEach(q -> {
            q.setSolveTime(now);
            q.setStatus("已采纳");
        });
        return baseMapper.updateBatchById(updates) ? R.ok() : R.fail("采纳失败，请稍后再试");
    }


}
