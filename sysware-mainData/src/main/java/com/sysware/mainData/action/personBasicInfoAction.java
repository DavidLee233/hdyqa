package com.sysware.mainData.action;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.bo.HdlOrganizationDepartmentBo;
import com.sysware.mainData.domain.bo.HdlPersonBasicInfoBo;

/**
 * @project npic
 * @description personBasicInfoAction查询构建工具类，负责组装员工基本信息主数据过滤条件表达式。
 * @author DavidLee233
 * @date 2026/3/20
 */
public class personBasicInfoAction {
    /**
     * @description 构建员工基本信息主数据处理所需的中间对象或条件。
     * @params bo 员工基本信息主数据业务请求对象（包含查询与变更字段）
     * @params pageQuery 分页查询参数（页码、每页条数与排序规则）
     *
      * @return LambdaQueryWrapper<HdlPersonBasicInfo> 员工基本信息主数据查询条件构造器，用于后续数据库过滤与排序。
     * @author DavidLee233
     * @date 2026/3/20
     */
    public static LambdaQueryWrapper<HdlPersonBasicInfo> buildPersonBasicQueryWrapper(HdlPersonBasicInfoBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<HdlPersonBasicInfo> lqw = Wrappers.lambdaQuery();
        // 首先 进行局部精确匹配查询
        if (bo != null) {
            // 模糊匹配所有搜索字段
            if (StrUtil.isNotBlank(bo.getName())) lqw.like(HdlPersonBasicInfo::getName, bo.getName());
            if (StrUtil.isNotBlank(bo.getCode())) lqw.like(HdlPersonBasicInfo::getCode, bo.getCode());
            if (StrUtil.isNotBlank(bo.getMobile())) lqw.like(HdlPersonBasicInfo::getMobile, bo.getMobile());
        }
        // 其次 进行全局模糊查询逻辑
        if (pageQuery != null && StrUtil.isNotBlank(bo.getSearchValue())) {
            lqw.and(wrapper -> {
                wrapper.like(HdlPersonBasicInfo::getCode, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getRegisteredResidence, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getFamilyAddress, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getOffice, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getInOutNumber, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getSecretLevel, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getName, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getIdType, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getSex, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getIdNumber, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getBirthdate, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getNativePlace, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getNationality, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getPolity, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getPkEdu, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getPkDegree, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getJoinWorkDate, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getMobile, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getOfficePhone, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getPostalCode, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getPkOrg, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getJoinPolityDate, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getProfessionalLevel, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getTechnicalLevel, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getGraduationSchool, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getJoinCompanyDate, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getProfessionalQualification, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getGraduationDate, bo.getSearchValue())
                        .or()
                        .like(HdlPersonBasicInfo::getEnableState, bo.getSearchValue());
            });
        }
        return lqw;
    }
}