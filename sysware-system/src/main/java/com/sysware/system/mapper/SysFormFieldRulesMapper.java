package com.sysware.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.system.domain.SysFormFieldRules;
import com.sysware.system.domain.SysFormFieldVisible;
import com.sysware.system.domain.vo.SysFormFieldRulesVo;
import com.sysware.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 校验规则Mapper接口
 *
 * @author aa
 * @date 2023-06-03
 */
public interface SysFormFieldRulesMapper extends BaseMapperPlus<SysFormFieldRulesMapper, SysFormFieldRules, SysFormFieldRulesVo> {



}
