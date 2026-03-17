package com.sysware.system.service;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.system.domain.SysFormField;
import com.sysware.system.domain.bo.SysFormFieldQueryBo;
import com.sysware.system.domain.bo.SysFormFieldAddBo;
import com.sysware.system.domain.bo.SysFormFieldEditBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.system.domain.vo.SysFormFieldVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 表单配置Service接口
 *
 * @author zzr
 * @date 2023-04-14
 */
public interface ISysFormFieldService{
	/**
	 * 查询单个
	 * @return
	 */
	SysFormFieldVo queryById(String fieldId);

	/**
	 * 查询列表
	 */
    TableDataInfo<Map> queryPageList(SysFormFieldQueryBo bo, PageQuery pageQuery);

	/**
	 * 查询列表
	 */
	List queryList(SysFormFieldQueryBo bo);

	/**
	 * 根据新增业务对象插入表单配置
	 * @param bo 表单配置新增业务对象
	 * @return
	 */
	R insertByAddBo(SysFormFieldAddBo bo);

	/**
	 * 根据编辑业务对象修改表单配置
	 * @param bo 表单配置编辑业务对象
	 * @return
	 */
	Boolean updateByEditBo(SysFormFieldEditBo bo);

	/**
	 * 校验并删除数据
	 * @param ids 主键集合
	 * @param isValid 是否校验,true-删除前校验,false-不校验
	 * @return
	 */
	Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

	/**
	 * 获取表单的默认值
	 * @param bo
	 * @return
	 */
    Map<String, Object> getDefaultValue(SysFormFieldQueryBo bo);

	/**
	 * 查询表单字段
	 * @param bo
	 * @return
	 */
	TableDataInfo<Map> selectFormFieldList(SysFormFieldQueryBo bo, PageQuery pageQuery);

	/**
	 * 根据字段ID查询当前字段所属表单所有字段，排除当前字段
	 * @param fieldId
	 * @return
	 */
    TableDataInfo selectFormFieldByFieldId(String fieldId);

	/**
	 * 复制表单字段
	 * @param bo
	 * @return
	 */
	boolean copyFormField(SysFormFieldAddBo bo);
}
