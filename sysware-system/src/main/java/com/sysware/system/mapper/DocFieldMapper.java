package com.sysware.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sysware.system.domain.DocField;
import com.sysware.system.domain.bo.DocFieldBo;
import com.sysware.system.domain.vo.DocFieldVo;
import com.sysware.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 文档著录字段Mapper接口
 *
 * @author aa
 * @date 2023-09-02
 */
@InterceptorIgnore(dataPermission = "true")
public interface DocFieldMapper extends BaseMapperPlus<DocFieldMapper, DocField, DocFieldVo> {


    List<Map> selectImportFieldList(@Param(Constants.WRAPPER) Wrapper<Object> ew, @Param("docId")  String docId);
}
