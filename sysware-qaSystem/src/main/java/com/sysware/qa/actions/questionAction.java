package com.sysware.qa.actions;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.domain.vo.QaSysAttachVo;
import com.sysware.qa.domain.vo.QaSysReplyVo;
import com.sysware.qa.mapper.QaSysAttachMapper;
import com.sysware.qa.mapper.QaSysReplyMapper;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class questionAction {

    /**
     * @author lxd
     * @description: 进行提问角色左侧表格页面查询
     * @param bo
     * @param pageQuery
     * @return LambdaQueryWrapper<com.sysware.archives.domain.QaSys>
     * @date 2025/7/19
     **/
    public static LambdaQueryWrapper<QaSys> buildQuestionQueryWrapper(QaSysBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<QaSys> lqw = Wrappers.lambdaQuery();
        // 对于提问者来说只查询自己相关的内容
        LoginUser user = LoginHelper.getLoginUser();
        lqw.and(wrapper -> wrapper
                .eq(StringUtils.isNotBlank(user.getUsername()), QaSys::getCreateBy, user.getUsername())
                .eq(StringUtils.isNotBlank(user.getLoginName()), QaSys::getCreateId, user.getLoginName())
        );
        // 提问者在左边只需要看到“待处理”和“"已处理"状态的问题
        lqw.and(wrapper -> wrapper
                .eq(QaSys::getStatus, "待处理")
                .or()
                .eq(QaSys::getStatus, "已处理")
        );
        return lqw;
    }

    /**
     * @author lxd
     * @description: 新增问题更新字段
     * @param bo
     * @return QaSys
     * @date 2025/7/25
     **/
    public static QaSys qaSysInsert(QaSysBo bo) {
        QaSys add = BeanUtil.toBean(bo, QaSys.class);
        add.setCreateTime(LocalDateTime.now());
        LoginUser user = LoginHelper.getLoginUser();
        add.setCreateBy(user.getUsername());
        add.setCreateId(user.getLoginName());
        add.setDepartment(user.getDeptName());
        add.setStatus("待处理");
        add.setVisits(0L);
        return add;
    }

    /**
     * @author lxd
     * @description: 通过唯一ID获取附件组
     * @param updates
     * @param attachMapper
     * @return Collection<Long>
     * @date 2025/7/25
     **/
    public static Collection<Long> getAttachIds(List<QaSys> updates, QaSysAttachMapper attachMapper) {
        Collection<Long> attachIds = new ArrayList<>();
        for (QaSys update : updates) {
            List<QaSysAttachVo> attachVos = attachMapper.selectByRecordId(update.getRecordId());
            if (attachVos != null && !attachVos.isEmpty()) {
                attachVos.forEach(vo -> {
                    attachIds.add(vo.getId());
                });
            }
        }
        return attachIds;
    }

    /**
     * @author lxd
     * @description: 通过唯一ID获取回复组
     * @param updates
     * @param replyMapper
     * @return Collection<Long>
     * @date 2025/7/25
     **/
    public static Collection<Long> getReplyIds(List<QaSys> updates, QaSysReplyMapper replyMapper) {
        Collection<Long> replyIds = new ArrayList<>();
        for (QaSys update : updates) {
            List<QaSysReplyVo> replyVos = replyMapper.selectByRecordId(update.getRecordId());
            if (replyVos != null && !replyVos.isEmpty()){
                replyVos.forEach(vo -> {
                    replyIds.add(vo.getReplyId());
                });
            }
        }
        return replyIds;
    }
}
