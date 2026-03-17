package com.sysware.qa.service.impl;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.helper.LoginHelper;
import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.domain.vo.QaSysVo;
import com.sysware.qa.actions.commonAction;
import com.sysware.qa.domain.vo.QaUserQuestionVo;
import com.sysware.qa.mapper.QaSysAttachMapper;
import com.sysware.qa.mapper.QaSysMapper;
import com.sysware.qa.mapper.QaUserQuestionMapper;
import com.sysware.qa.service.IQaQuestionTypeService;
import com.sysware.qa.service.IQaSysCommonService;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.system.domain.SysUserConfig;
import com.sysware.system.mapper.SysUserConfigMapper;
import com.sysware.system.service.ISecurityService;
import com.sysware.system.service.ISysRoleService;
import com.sysware.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.sysware.qa.actions.commonAction.*;

/**
 * @FileName QaSysServiceImpl
 * @Author lxd
 * @Description
 * @date 2025/7/19
 **/
@RequiredArgsConstructor
@Service
public class QaSysCommonServiceImpl implements IQaSysCommonService {

    private final QaSysMapper baseMapper;
    private final CacheManager cacheManager;
    private final QaUserQuestionMapper userQuestionMapper;
    private final ISysRoleService roleService;
    private final QaSysAttachMapper attachMapper;
    private final SysUserConfigMapper userConfigMapper;
    private final ISysUserService sysUserService;
    private final ISecurityService securityService;
    private final IQaQuestionTypeService questionTypeService;
    private static final String LOGIN_USER_KEY = "loginUser";

    /**
     * @author lxd
     * @description: 查询问题回答
     * @param recordId
     * @return com.sysware.archives.domain.vo.QaSysVo
     * @date 2025/7/19
     **/
    @Override
    public QaSysVo queryById(String recordId, String mode){
        QaSysVo qaSysVo = baseMapper.selectVoById(recordId);
        commonAction.setAttachByMode(recordId, mode, qaSysVo, attachMapper);
        return qaSysVo;
    }

    /**
     * @author lxd
     * @description: 查询管理员右侧页面数据
     * @param bo
     * @param pageQuery
     * @return com.sysware.common.core.page.TableDataInfo
     * @date 2025/7/19
     **/
    @Override
    public TableDataInfo queryPageList(QaSysBo bo, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<QaSys> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<QaSysVo> result = baseMapper.selectVoPage(page, buildQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }

    /**
     * @author lxd
     * @description: 刷新缓存
     * @param
     * @return void
     * @date 2025/7/19
     **/
    @Override
    public void refreshCache() {
        cacheManager.getCacheNames().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    /**
     * @author lxd
     * @description: 根据点击数据行更新访问量
     * @param recordId
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/20
     **/
    @Override
    public R<Void> updateVisits(String recordId) {
        return baseMapper.updateVisits(recordId) > 0 ? R.ok() : R.fail("数据库无更新");
    }

    /**
     * @author lxd
     * @description: 获取当前用户信息
     * @return com.sysware.common.core.domain.R<QaSysVo>
     * @date 2025/7/20
     **/
    @Override
    public R<QaSysVo> getUser() {
        return R.ok(getLoginUser());
    }

    /**
     * @author lxd
     * @description: 获取用户下拉框信息（全部）
     * @return com.sysware.common.core.domain.R<QaSysVo>
     * @date 2025/7/20
     **/
    @Override
    public R<List<Map<String, Object>>> getUserOption() {
        List<Map<String, Object>> list = sysUserService.selectUserPartList().stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("userId", u.getUserId());
            m.put("userName", u.getUserName());
            m.put("loginName", u.getLoginName());
            return m;
        }).collect(Collectors.toList());
        if (list.isEmpty()) return R.fail("用户姓名获取为空，请稍后再试");
        else return R.ok(list);
    }

    /**
     * @author lxd
     * @description: 获取问题类型下拉框信息（全部）
     * @return com.sysware.common.core.domain.R<QaSysVo>
     * @date 2025/7/20
     **/
    @Override
    public R<List<Map<String, Object>>> getTypeOption() {
        List<Map<String, Object>> list = questionTypeService.selectTypeList().stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("typeId", u.getTypeId());
            m.put("type", u.getType());
            m.put("occupied", u.getOccupied());
            m.put("sequence", u.getSequence());
            return m;
        }).collect(Collectors.toList());
        return R.ok(list);
    }

    /**
     * @author lxd
     * @description: 获取问题类型下拉框信息（筛选占有位为0的）
     * @return com.sysware.common.core.domain.R<QaSysVo>
     * @date 2025/7/20
     **/
    @Override
    public R<List<Map<String, Object>>> getPartTypeOption() {
        List<Map<String, Object>> list = questionTypeService.selectPartTypeList().stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("typeId", u.getTypeId());
            m.put("type", u.getType());
            m.put("occupied", u.getOccupied());
            m.put("sequence", u.getSequence());
            return m;
        }).collect(Collectors.toList());
        return R.ok(list);
    }

    /**
     * @author lxd
     * @description: 获取密级信息下拉框信息（全部）
     * @return com.sysware.common.core.domain.R<QaSysVo>
     * @date 2025/7/20
     **/
    @Override
    public R<List<Map<String, Object>>> getSecurityOption(String securityType) {
        List<Map<String, Object>> list = securityService.selectSecurityList(securityType).stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("securityId", u.getSecurityId());
            m.put("securityName", u.getSecurityName());
            m.put("sort", u.getSort());
            return m;
        }).collect(Collectors.toList());
        return R.ok(list);
    }

    /**
     * @author lxd
     * @description: 开启消息开关
     * @return com.sysware.common.core.domain.R<QaSysVo>
     * @date 2025/9/1
     **/
    @Override
    public R<Void> openNotify() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        SysUserConfig config = userConfigMapper.selectByUserId(loginUser.getUserId());
        if (config != null){
            // 缓存和数据库层面都要更新
            loginUser.setOpenNotify(1);
            config.setOpenNotify(1);
        }else{
            R.fail("对应人员无配置文件存在，请联系管理员");
        }
        boolean flag = userConfigMapper.updateByUserId(config) > 0;
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
        StpUtil.getTokenSession().set(LOGIN_USER_KEY,loginUser);
        return flag ? R.ok("打开消息提醒功能成功！") : R.fail("消息功能开启失败");
    }

    /**
     * @author lxd
     * @description: 关闭消息开关
     * @return com.sysware.common.core.domain.R<QaSysVo>
     * @date 2025/9/1
     **/
    @Override
    public R<Void> closeNotify() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        SysUserConfig config = userConfigMapper.selectByUserId(loginUser.getUserId());
        if (config != null){
            // 缓存和数据库层面都要更新
            loginUser.setOpenNotify(0);
            config.setOpenNotify(0);
        }else{
            R.fail("对应人员无配置文件存在，请联系管理员");
        }
        boolean flag = userConfigMapper.updateByUserId(config) > 0;
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
        StpUtil.getTokenSession().set(LOGIN_USER_KEY,loginUser);
        return flag ? R.ok("关闭消息提醒功能成功") : R.fail("消息功能关闭失败");
    }

    /**
     * @author lxd
     * @description: 将消息提醒位置1（提醒）
     * @return com.sysware.common.core.domain.R<QaSysVo>
     * @date 2025/8/28
     **/
    @Override
    public R<Void> setNotify(String recordId) {
        QaSys qaSys = baseMapper.selectById(recordId);

        LoginUser user = LoginHelper.getLoginUser();

        // 相应人员提醒对应的话语
        if("待处理".equals(qaSys.getStatus())) {
            if (user.getLoginName().equals(qaSys.getCreateId())){
                if(qaSys.getNotify() == null || qaSys.getNotify() != 1L){
                    if(validQaTypeMember(qaSys)){
                        qaSys.setNotify(1L);
                        boolean flag = baseMapper.updateById(qaSys) > 0;
                        if (!flag){
                            return R.fail("提醒失败，请稍后再试");
                        }
                        return R.ok("已提醒运维人员处理，请耐心等待！");
                    }else{
                        return R.fail("问题类型不存在或尚未分配处理人员");
                    }
                }else{
                    return R.ok("已提醒运维人员，请勿重复提醒");
                }
            } else{
                return R.fail("问题尚未处理，无法提醒");
            }
        }
        else if("已处理".equals(qaSys.getStatus())) {
            if (user.getLoginName().equals(qaSys.getUpdateId())){
                if(qaSys.getNotify() != 1L){
                    qaSys.setNotify(1L);
                    boolean flag = baseMapper.updateById(qaSys) > 0;
                    if (!flag){
                        return R.fail("提醒失败，请稍后再试");
                    }
                    return R.ok("已提醒提问人员采纳，请耐心等待！");
                }else{
                    return R.ok("已提醒提问人员，请勿重复提醒");
                }
            } else{
                return R.fail("问题已被处理，无法提醒");
            }
        }
        else return R.fail("问题已处理完毕，无法提醒");
    }

    public boolean validQaTypeMember(QaSys qaSys) {
        QaUserQuestionVo vo = userQuestionMapper.selectNewVoByType(qaSys.getType());
        if(vo != null){
            String loginName = vo.getLoginName();
            String userName = vo.getUserName();
            if(qaSys.getUpdateId() == null || qaSys.getUpdateBy() == null || !qaSys.getUpdateId().equals(loginName) || !qaSys.getUpdateBy().equals(userName)){
                qaSys.setUpdateId(loginName);
                qaSys.setUpdateBy(userName);
                return true;
            }
        }
        return false;
    }

    /**
     * @author lxd
     * @description: 将消息提醒位置0（不提醒）
     * @return com.sysware.common.core.domain.R<QaSysVo>
     * @date 2025/8/28
     **/
    @Override
    public R<Void> markNotified(Collection<String> ids) {
        if (CollUtil.isEmpty(ids)){
            return R.fail("暂无无消息提醒");
        }
        List<QaSys> notifyQa = baseMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(notifyQa)){
            return R.fail("未找到对应问题");
        }
        // 将消息位置为0，后续不再重复提示
        boolean allUpdated = notifyQa.stream()
                .peek(q -> q.setNotify(0L)) // 设置通知状态
                .map(baseMapper:: updateById)
                .allMatch(result -> result > 0); // 判断是否全部更新成功
        return allUpdated ? R.ok() : R.fail("消息位更新失败，请重试");
    }
}
