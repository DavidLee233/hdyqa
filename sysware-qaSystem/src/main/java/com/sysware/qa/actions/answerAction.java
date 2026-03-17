package com.sysware.qa.actions;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.StringUtils;

import java.time.LocalDateTime;

public class answerAction {
    /**
     * @author lxd
     * @description: 进行回答角色左侧表格页面查询
     * @param bo
     * @param pageQuery
     * @return LambdaQueryWrapper<com.sysware.archives.domain.QaSys>
     * @date 2025/7/19
     **/
    public static LambdaQueryWrapper<QaSys> buildAnswerQueryWrapper(QaSysBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<QaSys> lqw = Wrappers.lambdaQuery();
        // 对于回答者来说只查询自己相关的内容
        LoginUser user = LoginHelper.getLoginUser();
        lqw.and(wrapper -> wrapper
                .eq(StringUtils.isNotBlank(user.getUsername()), QaSys::getUpdateBy, user.getUsername())
                .eq(StringUtils.isNotBlank(user.getLoginName()), QaSys::getUpdateId, user.getLoginName())
        );
        // 回答者在左边只需要看到“待处理”和“"已处理"状态的问题（采纳完后即可消失）
        lqw.and(wrapper -> wrapper
                .eq(QaSys::getStatus, "待处理")
                .or()
                .eq(QaSys::getStatus, "已处理")
        );
        return lqw;
    }

    /**
     * @author lxd
     * @description: 检查当前用户是否为系统管理员或当前问题状态是否为已采纳，检查完毕后对相应字段进行更新
     * @param update
     * @return boolean
     * @date 2025/7/19
     **/
    public static boolean checkAndUpdate(QaSys update) {
        if (!"系统管理员".equals(LoginHelper.getUsername()) && update.getStatus().equals("已采纳")){
            return false;
        }
        update.setStatus("已处理");
        // 如果在提出问题已指定回答人，则以下步骤不需要
        update.setUpdateTime(LocalDateTime.now());
        // 消息提醒位置为1，提醒提问者及时采纳问题
        update.setNotify(1L);
        return true;
    }
}
