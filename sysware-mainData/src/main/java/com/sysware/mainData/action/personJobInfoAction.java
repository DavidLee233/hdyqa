package com.sysware.mainData.action;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.HdlPersonJobInfo;
import com.sysware.mainData.domain.bo.HdlPersonBasicInfoBo;
import com.sysware.mainData.domain.bo.HdlPersonJobInfoBo;

public class personJobInfoAction {
    /**
     * @author lxd
     * @description: 进行表格页面查询
     * @param bo
     * @param pageQuery
     **/
    public static LambdaQueryWrapper<HdlPersonJobInfo> buildPersonJobQueryWrapper(HdlPersonJobInfoBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<HdlPersonJobInfo> lqw = Wrappers.lambdaQuery();
        // 首先 进行局部精确匹配查询
        if (bo != null) {
            // 模糊匹配所有搜索字段
            if (StrUtil.isNotBlank(bo.getName())) lqw.like(HdlPersonJobInfo::getName, bo.getName());
            if (StrUtil.isNotBlank(bo.getCode())) lqw.like(HdlPersonJobInfo::getCode, bo.getCode());
            if (StrUtil.isNotBlank(bo.getKeyNumber())) lqw.like(HdlPersonJobInfo::getKeyNumber, bo.getKeyNumber());
        }
        // 其次 进行全局模糊查询逻辑
        if (pageQuery != null && StrUtil.isNotBlank(bo.getSearchValue())) {
            lqw.and(wrapper -> {
                wrapper.like(HdlPersonJobInfo::getName, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getIdNumber, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getInOutNumber, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getRealPkDept, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getKeyNumber, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getCode, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getPkDept, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getBeginDate, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getEndDate, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getPkPsncl, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getSecretLevel, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getLastFlag, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getEndFlag, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getIsMainJob, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getOtherJobTitle, bo.getSearchValue())
                        .or()
                        .like(HdlPersonJobInfo::getJobLevel, bo.getSearchValue());
            });
        }
        return lqw;
    }
}
