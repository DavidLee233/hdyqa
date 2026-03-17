package com.sysware.framework.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sysware.common.core.domain.BaseEntity;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.exception.ServiceException;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * MP注入处理器
 *
 * @author
 * @date
 */
@Slf4j
public class CreateAndUpdateMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) metaObject.getOriginalObject();
                LocalDateTime current = ObjectUtil.isNotNull(baseEntity.getCreateTime())
                    ? baseEntity.getCreateTime() : LocalDateTime.now();
                baseEntity.setCreateTime(current);
//                baseEntity.setUpdateTime(current);
                String userId = StringUtils.isNotBlank(baseEntity.getCreateBy())
                    ? baseEntity.getCreateBy() : LoginHelper.getUserId();
                // 当前已登录 且 创建人为空 则填充
                baseEntity.setCreateBy(userId);
                // 当前已登录 且 更新人为空 则填充
//                baseEntity.setUpdateBy(userId);
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) metaObject.getOriginalObject();
                LocalDateTime current = LocalDateTime.now();
                // 更新时间填充(不管为不为空)
                //baseEntity.setUpdateTime(current);
                String userId = LoginHelper.getUserId();
                // 当前已登录 更新人填充(不管为不为空)
                if (StringUtils.isNotBlank(userId)) {
                    //baseEntity.setUpdateBy(userId);
                }
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    /**
     * 获取登录用户名
     */
    private String getLoginUsername() {
        LoginUser loginUser;
        try {
            loginUser = LoginHelper.getLoginUser();
        } catch (Exception e) {
            log.warn("自动注入警告 => 用户未登录");
            return null;
        }
        return loginUser.getUsername();
    }

}
