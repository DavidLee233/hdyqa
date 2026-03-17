package com.sysware.system.service;

import com.sysware.system.domain.SysFormFieldRules;
import com.sysware.system.domain.vo.SysFormFieldRulesVo;
import com.sysware.system.domain.bo.SysFormFieldRulesBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 校验规则Service接口
 *
 * @author aa
 * @date 2023-06-03
 */
public interface ISysFormFieldRulesService {

    /**
     * 查询校验规则
     */
    SysFormFieldRulesVo queryById(String id);

    /**
     * 查询校验规则列表
     */
    TableDataInfo queryPageList(SysFormFieldRulesBo bo, PageQuery pageQuery);

    /**
     * 查询校验规则列表
     */
    List<SysFormFieldRulesVo> queryList(SysFormFieldRulesBo bo);

    /**
     * 新增校验规则
     */
    Boolean insertByBo(SysFormFieldRulesBo bo);

    /**
     * 修改校验规则
     */
    Boolean updateByBo(SysFormFieldRulesBo bo);

    /**
     * 校验并批量删除校验规则信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 添加必填规则
     * @param sfr  实体
     * @return
     */
    Boolean addRequired(SysFormFieldRules sfr);


    /**
     * 删除必填规则
     * @param sfr  实体
     * @return
     */
    Boolean removeRequired(SysFormFieldRules sfr);
}
