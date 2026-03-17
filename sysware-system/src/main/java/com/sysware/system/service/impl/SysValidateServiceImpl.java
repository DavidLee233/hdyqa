package com.sysware.system.service.impl;


import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.sysware.common.enums.RoleType;
import com.sysware.common.utils.StringUtils;
import com.sysware.system.domain.vo.SysValidateRepetitionVo;
import com.sysware.system.mapper.SysValidateMapper;
import com.sysware.system.service.ISysValidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SysValidateServiceImpl implements ISysValidateService {

    private final SysValidateMapper baseMapper;
    private final SysRoleServiceImpl roleService;


    @Override
    public Boolean verifyRepetition(SysValidateRepetitionVo vo) {

        StringBuffer otherCondition = new StringBuffer("");

        if(vo.getOtherFieldValidate() != null) {
            vo.getOtherFieldValidate().forEach((key,value) ->{
                otherCondition.append(" and ").append(StringUtils.toUnderScoreCase(key)).append(" ='").append(value).append("'");
            });
        }

        System.out.println(otherCondition);

        if(StringUtils.isEmpty(vo.getValidateIdValue())){
            System.out.println(baseMapper.verifyRepetition(vo.getTableName(), StringUtils.toUnderScoreCase(vo.getField()), vo.getValue(), otherCondition.toString()));
            return   baseMapper.verifyRepetition(vo.getTableName(),StringUtils.toUnderScoreCase(vo.getField()),vo.getValue(),otherCondition.toString()) == 0;
        }else{
            System.out.println(baseMapper.verifyRepetitionByUpdate(vo.getTableName(), StringUtils.toUnderScoreCase(vo.getField()), vo.getValue(), otherCondition.toString(),
                    StringUtils.toUnderScoreCase(vo.getValidateIdName()), vo.getValidateIdValue()));
            return   baseMapper.verifyRepetitionByUpdate(vo.getTableName(),StringUtils.toUnderScoreCase(vo.getField()),vo.getValue(),otherCondition.toString(),
                 StringUtils.toUnderScoreCase(vo.getValidateIdName()),vo.getValidateIdValue()) == 0;
        }

    }

    @Override
    public Object verifyRoleKey(String userId, String roleKey,String roleType) {

        if(StringUtils.isEmpty(roleType)){
            roleType = RoleType.SYSTEM.getCode();
        }

        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(roleKey)){
            return true;
        }
        return roleService.checkUserRoleByKey(userId,roleKey,roleType);
    }
}
