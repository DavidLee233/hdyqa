package com.sysware.qa.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysRole;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sysware.qa.domain.bo.QaSysReplyBo;
import com.sysware.qa.domain.vo.QaSysReplyVo;
import com.sysware.qa.domain.QaSysReply;
import com.sysware.qa.mapper.QaSysReplyMapper;
import com.sysware.qa.service.IQaSysReplyService;
import com.sysware.common.helper.LoginHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collection;

/**
 * 问答论坛回复Service业务层处理
 *
 * @author aa
 * @date 2025-07-26
 */
@RequiredArgsConstructor
@Service
public class QaSysReplyServiceImpl implements IQaSysReplyService {

    private final QaSysReplyMapper baseMapper;
    private final ISysRoleService roleService;
    /**
     * @author lxd
     * @description: 查询问答论坛回复列表
     * @param bo
     * @param pageQuery
     * @return TableDataInfo
     * @date 2025/7/19
     **/
    @Override
    public TableDataInfo queryPageList(QaSysReplyBo bo, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<QaSysReply> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<QaSysReplyVo> result = baseMapper.selectVoPage(page, buildQueryWrapper(bo));
        return TableDataInfo.build(result);
    }

    private LambdaQueryWrapper<QaSysReply> buildQueryWrapper(QaSysReplyBo bo) {
        LambdaQueryWrapper<QaSysReply> lqw = Wrappers.lambdaQuery();
        //lqw.eq(StringUtils.isNotBlank(bo.getReplyId()), QaSysReply::getReplyId, bo.getReplyId());
        lqw.eq(StringUtils.isNotBlank(bo.getSearchValue()), QaSysReply::getRecordId, bo.getSearchValue());
        return lqw;
    }
    /**
     * @author lxd
     * @description: 新增问答论坛回复
     * @param bo
     * @return R<QaSysReply>
     * @date 2025/7/19
     **/
    @Override
    public R<QaSysReply> insertByBo(QaSysReplyBo bo) {
        QaSysReply add = BeanUtil.toBean(bo, QaSysReply.class);
        add.setCreateTime(LocalDateTime.now());
        add.setCreateBy(LoginHelper.getUsername());
        add.setCreateId(LoginHelper.getLoginUser().getLoginName());
        add.setRemind(0L);
        add.setThumbsUp(0L);
        // 检查回复是否是回复了别的楼层
        String replyToId = bo.getReplyToId();
        if (replyToId != null){
            add.setReplyToId(replyToId);
            add.setReplyToUser(baseMapper.selectVoById(replyToId).getCreateBy());
        }
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setReplyId(add.getReplyId());
        }
        return flag ? R.ok("发送成功", add) : R.fail("发送失败，请稍后再试");
    }
    /**
     * @author lxd
     * @description: 获取全局回复楼层号
     * @param replyId
     * @return R<Integer>
     * @date 2025/7/19
     **/
    @Override
    public R<Integer> getFloorNum(String replyId) {
        QaSysReplyVo replyVo = baseMapper.selectVoById(replyId);
        if (replyVo == null) {
            // 解决删除评论后加载持续报错的问题
            //return R.fail("回复不存在，请重试");
            return R.ok();
        }
        int floorNumber = baseMapper.getFloorById(replyVo.getRecordId(), replyId);
        return R.ok(floorNumber);
    }
    /**
     * @author lxd
     * @description: 获取全局回复楼层号的页码数
     * @param replyId
     * @param pageSize
     * @return R<Integer>
     * @date 2025/7/19
     **/
    @Override
    public R<Integer> getPageNum(String replyId, int pageSize) {
        QaSysReplyVo replyVo = baseMapper.selectVoById(replyId);
        if (replyVo == null) {
            // 解决删除评论后加载持续报错的问题
            //return R.fail("回复不存在，请重试");
            return R.ok();
        }
        int floorNumber = baseMapper.getFloorById(replyVo.getRecordId(), replyId);
        return R.ok((floorNumber / pageSize) + 1);
    }

    /**
     * @author lxd
     * @description: 点赞论坛
     * @param replyId
     * @return int
     * @date 2025/7/19
     **/
    @Override
    public int thumbsUp(String replyId) {
        return baseMapper.thumbsUp(replyId);
    }
    /**
     * @author lxd
     * @description: 保存前的数据校验
     * @param entity
     * @return void
     * @date 2025/7/19
     **/
    private void validEntityBeforeSave(QaSysReply entity){
        //TODO 做一些数据校验,如唯一约束
    }
    /**
     * @author lxd
     * @description: 批量删除问答论坛回复
     * @param ids
     * @return Boolean
     * @date 2025/7/19
     **/
    @Override
    public R<Void> deleteWithValidByIds(Collection<String> ids) {
        LoginUser user = LoginHelper.getLoginUser();
        if(ids.isEmpty()){
            return R.fail("无回复可删除");
        }
        // 因为每次删除只需要删除选择的那一个即可
        QaSysReplyVo replyVo = baseMapper.selectVoById(ids.iterator().next());
        // 判断角色是否为管理员（这里的service方法最好再加一个新方法）
        List<SysRole> roles = roleService.selectRolesByUserIdQA(LoginHelper.getLoginUser().getUserId());
        boolean isAdmin = roles.stream().anyMatch(role -> "问答管理员".equals(role.getRoleName()));
        // 只有两个条件满足才可删除：1.是管理员角色 2.普通角色只能删除自己创建的评论
        if(isAdmin || (replyVo.getCreateBy().equals(user.getUsername()) && replyVo.getCreateId().equals(user.getLoginName()))){
            boolean flag = baseMapper.deleteBatchIds(ids) > 0;
            if (flag) return R.ok();
            else return R.fail("删除失败");
        }else{
            return R.fail("禁止删除他人回复，如有疑问请联系管理员删除");
        }
    }
}
