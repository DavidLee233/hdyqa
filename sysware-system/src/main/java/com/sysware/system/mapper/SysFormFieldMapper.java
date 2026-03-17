package com.sysware.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.core.mapper.BaseMapperPlus;
import com.sysware.system.domain.SysFormField;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 表单配置Mapper接口
 *
 * @author zzr
 * @date 2023-04-14
 */
public interface SysFormFieldMapper extends BaseMapperPlus<SysFormFieldMapper,SysFormField,Map> {

    /**
     * 根据条件查询所有表单字段
     * @param ew
     * @return
     */
    public List selectFormFieldList(@Param(Constants.WRAPPER) Wrapper<SysFormField> ew);

    /**
     * 根据字段ID查询当前字段所属表单所有字段，排除当前字段
     * @param fieldId
     * @return
     */
    List selectFormFieldByFieldId(@Param("fieldId") String fieldId);

    long selectFormFieldListCount(@Param(Constants.WRAPPER) Wrapper<SysFormField> buildQueryWrapper);
}
