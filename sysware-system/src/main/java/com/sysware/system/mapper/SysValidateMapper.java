package com.sysware.system.mapper;

import com.sysware.common.core.mapper.BaseMapperPlus;
import com.sysware.system.domain.vo.SysValidateRepetitionVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface SysValidateMapper extends BaseMapperPlus<SysValidateMapper, SysValidateRepetitionVo, Map> {

    @Select("SELECT count(0) FROM ${tableName} WHERE " +
            " ${field} = '${value}'   ${otherCondition} " +
            " and ${validateIdName} != '${validateIdValue}'")
    Long verifyRepetitionByUpdate(@Param("tableName") String tableName,
                          @Param("field") String field,
                          @Param("value") String value,
                          @Param("otherCondition") String otherCondition,
                          @Param("validateIdName") String validateIdName,
                          @Param("validateIdValue") String validateIdValue);

    @Select("SELECT count(0) FROM ${tableName} WHERE ${field} = '${value}'  ${otherCondition} ")
    Long verifyRepetition(@Param("tableName") String tableName,
                          @Param("field") String field,
                          @Param("value") String value,
                          @Param("otherCondition") String otherCondition);
}
