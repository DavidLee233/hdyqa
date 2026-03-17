package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.OS;
import cn.hutool.http.useragent.UserAgent;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.event.LogininforEvent;
import com.sysware.common.core.domain.event.OperLogEvent;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.utils.ServletUtils;
import com.sysware.common.utils.ip.AddressUtils;
import com.sysware.system.domain.SysLogininfor;
import com.sysware.system.domain.SysOperLog;
import com.sysware.system.domain.bo.SysLogininforBo;
import com.sysware.system.domain.bo.SysOperLogBo;
import com.sysware.system.domain.vo.SysLogininforVo;
import com.sysware.system.domain.vo.SysOperLogVo;
import com.sysware.system.mapper.SysLogininforMapper;
import com.sysware.system.service.ISysLogininforService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SysLogininforServiceImpl  implements ISysLogininforService {

    private final SysLogininforMapper baseMapper;

    /**
     * 登录日志记录
     *
     * @param logininforEvent 登录日志事件
     */
    @Async
    @EventListener
    public void onLogininforEvent(LogininforEvent logininforEvent) {
        log.info("=== 事件监听器被触发 ===");
        try {
            SysLogininfor logininfor = new SysLogininfor();
            logininfor.setUserName(logininforEvent.getUsername());
            if("Success".equals(logininforEvent.getStatus())) logininfor.setStatus("0");
            else logininfor.setStatus("1");
            logininfor.setMsg(logininforEvent.getMessage());
            logininfor.setIpaddr(ServletUtils.getClientIP(logininforEvent.getRequest()));
            logininfor.setLoginLocation(AddressUtils.getRealAddressByIP(logininfor.getIpaddr()));
            logininfor.setBrowser(ServletUtils.getBrowser(logininforEvent.getRequest()));
            logininfor.setOs(ServletUtils.getOs(logininforEvent.getRequest()));
            logininfor.setLoginTime(new Date());
            baseMapper.insert(logininfor);
            log.info("登录信息保存成功");
        } catch (Exception e) {
            log.error("保存登录信息异常", e);
        }
    }

    @Override
    public TableDataInfo<SysLogininforVo> selectPageLogininforList(SysLogininforBo logininfor, PageQuery pageQuery) {
/*        Map<String, Object> params = logininfor.getParams();
        LambdaQueryWrapper<SysLogininfor> lqw = new LambdaQueryWrapper<SysLogininfor>()
                .like(StrUtil.isNotBlank(logininfor.getIpaddr()), SysLogininfor::getIpaddr, logininfor.getIpaddr())
                .eq(StrUtil.isNotBlank(logininfor.getStatus()), SysLogininfor::getStatus, logininfor.getStatus())
                .like(StrUtil.isNotBlank(logininfor.getUserName()), SysLogininfor::getUserName, logininfor.getUserName())
                .apply(Validator.isNotEmpty(params.get("beginTime")),
                        "date_format(login_time,'%y%m%d') >= date_format({0},'%y%m%d')",
                        params.get("beginTime"))
                .apply(Validator.isNotEmpty(params.get("endTime")),
                        "date_format(login_time,'%y%m%d') <= date_format({0},'%y%m%d')",
                        params.get("endTime"));

        Page<SysLogininforVo> resilt = baseMapper.selectVoPage(pageQuery.build(),lqw);*/
        // 使用传入的pageQuery构建分页参数
        Page<SysLogininfor> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<SysLogininforVo> result = baseMapper.selectVoPage(page, buildQueryWrapper(logininfor, pageQuery));
        return TableDataInfo.build(result);
    }

    private LambdaQueryWrapper<SysLogininfor> buildQueryWrapper(SysLogininforBo logininfor, PageQuery pageQuery) {
        LambdaQueryWrapper<SysLogininfor> lqw = Wrappers.lambdaQuery();

        // 添加查询条件
        if (logininfor != null) {
            // 多个条件之间是 AND 关系
            lqw.like(StrUtil.isNotBlank(logininfor.getUserName()), SysLogininfor::getUserName, logininfor.getUserName())
                    .like(StrUtil.isNotBlank(logininfor.getIpaddr()), SysLogininfor::getIpaddr, logininfor.getIpaddr())
                    .eq(logininfor.getStatus() != null, SysLogininfor::getStatus, logininfor.getStatus());
        }

        // 添加日期范围查询逻辑
        if (logininfor != null) {
            // 均不为空时 beginTime =< operTime <= endTime
            if (logininfor.getBeginTime() != null &&  !"".equals(logininfor.getBeginTime()) && (logininfor.getEndTime() != null && !"".equals(logininfor.getEndTime()))) {
                lqw.between(SysLogininfor::getLoginTime, logininfor.getBeginTime(), logininfor.getEndTime());
            }
            // 开始时间不为空时，查询 operTime >= beginTime
            if(logininfor.getBeginTime() != null &&  !"".equals(logininfor.getBeginTime()) && (logininfor.getEndTime() == null || "".equals(logininfor.getEndTime()))){
                lqw.ge(SysLogininfor::getLoginTime, logininfor.getBeginTime());
            }
            // 结束时间不为空时，查询 operTime <= endTime
            if(logininfor.getEndTime() != null && !"".equals(logininfor.getEndTime()) && (logininfor.getBeginTime() == null || "".equals(logininfor.getBeginTime()))){
                lqw.le(SysLogininfor::getLoginTime, logininfor.getEndTime());
            }
        }

        // 添加排序查询逻辑
        if (pageQuery != null && StrUtil.isNotBlank(pageQuery.getOrderByColumn()) && isValidSortField(pageQuery.getOrderByColumn())) {
            String sortField = pageQuery.getOrderByColumn();
            boolean isAsc = "asc".equalsIgnoreCase(pageQuery.getIsAsc());

            // 将前端字段名映射到实体属性
            switch (sortField) {
                case "userName":
                    lqw.orderBy(true, isAsc, SysLogininfor::getUserName);
                    break;
                case "loginTime":
                    lqw.orderBy(true, isAsc, SysLogininfor::getLoginTime);
                    break;
                // 添加其他可排序字段...
                default:
                    // 默认按操作时间降序
                    lqw.orderBy(true, false, SysLogininfor::getLoginTime);
                    break;
            }
        } else {
            // 默认按操作时间降序排序
            lqw.orderBy(true, false, SysLogininfor::getLoginTime);
        }
        return lqw;
    }

    /**
     * @author lxd
     * @description: 排序搜索查询前字段校验
     * @param field
     * @return boolean
     * @date 2025/7/20
     **/
    public static boolean isValidSortField(String field) {
        if (StrUtil.isBlank(field)) {
            return false;
        }
        // 定义允许排序的字段
        Set<String> allowedFields = new HashSet<>(Arrays.asList(
                "userName", "loginTime"
        ));
        return allowedFields.contains(field);
    }

    /**
     * 查询系统访问记录
     */
    @Override
    public SysLogininforVo queryById(String infoId){
        return baseMapper.selectVoById(infoId);
    }

    /**
     * 新增系统访问记录
     */
    @Override
    public Boolean insertByBo(SysLogininforBo bo) {
        SysLogininfor add = BeanUtil.toBean(bo, SysLogininfor.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setInfoId(add.getInfoId());
        }
        return flag;
    }

    /**
     * 修改系统访问记录
     */
    @Override
    public Boolean updateByBo(SysLogininforBo bo) {
        SysLogininfor update = BeanUtil.toBean(bo, SysLogininfor.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysLogininfor entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininfor logininfor) {
        logininfor.setLoginTime(new Date());
        baseMapper.insert(logininfor);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininforBo logininfor) {
        return baseMapper.selectLogininforList(logininfor);
        /*Map<String, Object> params = logininfor.getParams();
        return baseMapper.selectList(new LambdaQueryWrapper<SysLogininfor>()
                .like(StrUtil.isNotBlank(logininfor.getIpaddr()),SysLogininfor::getIpaddr,logininfor.getIpaddr())
                .eq(StrUtil.isNotBlank(logininfor.getStatus()),SysLogininfor::getStatus,logininfor.getStatus())
                .like(StrUtil.isNotBlank(logininfor.getUserName()),SysLogininfor::getUserName,logininfor.getUserName())
                .apply(Validator.isNotEmpty(params.get("beginTime")),
                        "date_format(login_time,'%y%m%d') >= date_format({0},'%y%m%d')",
                        params.get("beginTime"))
                .apply(Validator.isNotEmpty(params.get("endTime")),
                        "date_format(login_time,'%y%m%d') <= date_format({0},'%y%m%d')",
                        params.get("endTime"))
                .orderByDesc(SysLogininfor::getInfoId));*/
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return
     */
    @Override
    public int deleteLogininforByIds(String[] infoIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor() {
       baseMapper.delete(new LambdaQueryWrapper<>());
    }
}
