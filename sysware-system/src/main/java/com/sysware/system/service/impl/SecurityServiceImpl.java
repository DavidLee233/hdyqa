package com.sysware.system.service.impl;

import cn.dev33.satoken.context.SaHolder;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.constant.CacheConstants;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.entity.SysDictData;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.service.SecurityService;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.StreamUtils;
import com.sysware.common.utils.spring.SpringUtils;
import com.sysware.system.service.ISysTableFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.system.domain.bo.SecurityAddBo;
import com.sysware.system.domain.bo.SecurityQueryBo;
import com.sysware.system.domain.bo.SecurityEditBo;
import com.sysware.common.core.domain.entity.SysSecurity;
import com.sysware.system.mapper.SecurityMapper;
import com.sysware.system.domain.vo.SecurityVo;
import com.sysware.system.service.ISecurityService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 密级Service业务层处理
 *
 * @author zzr
 * @date 2022-01-05
 */
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl  implements ISecurityService, SecurityService {

    private final SecurityMapper baseMapper;
    private final ISysTableFieldService tableFieldService;



    @Override
    public SecurityVo queryById(String id){
        return baseMapper.selectVoById(id, SecurityVo.class);
    }

    @Override
    public TableDataInfo queryPageList(SecurityQueryBo bo, PageQuery pageQuery) {

        List<String> searchFields = new ArrayList<>();

        if(StringUtils.isBlank(pageQuery.getOrderByColumn())){
            pageQuery.setOrderByColumn("securityValue");
            pageQuery.setIsAsc("asc");
        }

        return TableDataInfo.build(tableFieldService.getResult(BeanUtil.beanToMap(bo), pageQuery, baseMapper,searchFields));
    }

    @Override
    public List<SecurityVo> queryList(SecurityQueryBo bo) {

        List<SecurityVo> list = baseMapper.selectVoList(buildQueryWrapper(bo));
        return list;
    }

    private LambdaQueryWrapper<SysSecurity> buildQueryWrapper(SecurityQueryBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysSecurity> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getSecurityName()), SysSecurity::getSecurityName, bo.getSecurityName());
        lqw.eq(bo.getSecurityValue() != null, SysSecurity::getSecurityValue, bo.getSecurityValue());
        lqw.eq(StrUtil.isNotBlank(bo.getSecurityType()), SysSecurity::getSecurityType, bo.getSecurityType());
        lqw.eq(SysSecurity::getSecurityFlag, "1");
        lqw.le(bo.getMaxSecurityValue()!=null, SysSecurity::getSecurityValue, bo.getMaxSecurityValue());
        lqw.ge(bo.getMinSecurityValue()!=null, SysSecurity::getSecurityValue, bo.getMinSecurityValue());
        lqw.orderBy(true,true, SysSecurity::getSecurityValue, SysSecurity::getSort);
        return lqw;
    }

    /**
     * 获取所有的密级信息
     */
    @Override
    public List<SysSecurity> selectSecurityList(String securityType) {
        List<SysSecurity> list = baseMapper.selectAllSecurity(securityType);
        return list;
    }

    @Override
    public Boolean insertByAddBo(SecurityAddBo bo) {
        SysSecurity add = BeanUtil.toBean(bo, SysSecurity.class);

        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        return flag;
    }

    @Override
    public Boolean updateByEditBo(SecurityEditBo bo) {
        SysSecurity update = BeanUtil.toBean(bo, SysSecurity.class);

        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     *
     * @param entity 实体类数据
     */
    private void validEntityBeforeSave(SysSecurity entity){
        //TODO 做一些数据校验,如唯一约束
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }


    @Override
    public String selectSecurityNameById(String securityId,String type) {

        List<SecurityVo> datas = getSecurityCacheData(type);

        Map<String, String> map = StreamUtils.toMap(datas, SecurityVo::getSecurityId, SecurityVo::getSecurityName);

        /*if (com.sysware.common.utils.StringUtils.containsAny(securityId, ",")) {
            return Arrays.stream(securityId.split(","))
                    .map(v -> map.getOrDefault(v, com.sysware.common.utils.StringUtils.EMPTY))
                    .collect(Collectors.joining(","));
        } else {

        }*/
        return map.getOrDefault(securityId, com.sysware.common.utils.StringUtils.EMPTY);

    }

    @Override
    public Long selectSecurityValueById(String securityId,String type) {

        // 优先从本地缓存获取
        List<SecurityVo> datas = getSecurityCacheData(type);

        Map<String, Long> map = StreamUtils.toMap(datas, SecurityVo::getSecurityId, SecurityVo::getSecurityValue);

        return map.getOrDefault(securityId,0l);
    }

    /**
     * 根据密级类型从缓存中取数据，如果没有就添加到缓存
     * @param type
     * @return
     */
    private List<SecurityVo> getSecurityCacheData(String type){
        List<SecurityVo> datas = (List<SecurityVo>) SaHolder.getStorage().get(CacheConstants.SYS_SECURITY_KEY + type);
        if (ObjectUtil.isNull(datas)) {
            SecurityQueryBo bo = new SecurityQueryBo();
            bo.setSecurityType(type);
            datas = queryList(bo);
            SaHolder.getStorage().set(CacheConstants.SYS_SECURITY_KEY + type, datas);
        }
        return datas;
    }

    @Override
    public String selectSecurityIdByNameAndType(String securityName, String type) {
        // 优先从本地缓存获取
        List<SecurityVo> datas = getSecurityCacheData(type);

        Map<String, String> map = StreamUtils.toMap(datas, SecurityVo::getSecurityName, SecurityVo::getSecurityId);

        return map.getOrDefault(securityName, com.sysware.common.utils.StringUtils.EMPTY);
    }
}
