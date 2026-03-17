package com.sysware.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.system.domain.SysTableField;
import com.sysware.system.domain.vo.SysTableFieldVo;
import com.sysware.system.domain.bo.SysTableFieldQueryBo;
import com.sysware.system.domain.bo.SysTableFieldAddBo;
import com.sysware.system.domain.bo.SysTableFieldEditBo;
import com.sysware.common.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 自定义页面显示Service接口
 *
 * @author aa
 * @date 2022-08-03
 */
public interface ISysTableFieldService {
	/**
	 * 查询单个
	 * @return
	 */
	SysTableFieldVo queryById(String id);

	/**
	 * 查询列表
	 */
	TableDataInfo<SysTableFieldVo> queryPageList(SysTableFieldQueryBo bo, PageQuery pageQuery);

	/**
	 * 查询列表
	 */
	List<SysTableFieldVo> queryList(SysTableFieldQueryBo bo);

	/**
	 * 根据新增业务对象插入自定义页面显示
	 * @param bo 自定义页面显示新增业务对象
	 * @return
	 */
	Boolean insertByAddBo(SysTableFieldAddBo bo);

	/**
	 * 根据编辑业务对象修改自定义页面显示
	 * @param bo 自定义页面显示编辑业务对象
	 * @return
	 */
	Boolean updateByEditBo(SysTableFieldEditBo bo);

	/**
	 * 校验并删除数据
	 * @param ids 主键集合
	 * @param isValid 是否校验,true-删除前校验,false-不校验
	 * @return
	 */
	Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

	/**
	 *	根据组件获取
	 * @param component  组件全路径
	 * @param searchFieldList  搜索的字段
	 * @param dictList  数据字典字段
	 * @return
	 */
	void getSearchFields(String component, List<String> searchFieldList, List<SysTableFieldVo> dictList);

	/**
	 *
	 * 根据查询条件组装sql
	 *
	 * @param params 查询条件
	 * @return  where 后的sql
	 */
	QueryWrapper<Object> buildQuery(Map<String, Object> params,List<String> searchField);

	/**
	 * 根据 path查询页面字段
	 * @param bo
	 * @return
	 */
	TableDataInfo<SysTableFieldVo> queryFieldlistByPath(SysTableFieldQueryBo bo,PageQuery pageQuery);

	/**
	 * 根据查询参数和分页 查询结果
	 * @param params 查询参数
	 * @param pageQuery 分页参数
	 * @param mapper 查询的mapper
	 * @param searchField 需要搜索的字段
	 * @return
	 */
	IPage<Map> getResult(Map params, PageQuery pageQuery, BaseMapper mapper,List<String> searchField);

	/**
	 * 根据查询参数和分页 查询结果
	 * @param params 查询参数
	 * @param pageQuery 分页参数
	 * @param mapper 查询的mapper
	 * @return
	 */
	IPage<Map> getResult(Map params, PageQuery pageQuery, BaseMapper mapper);

	/**
	 * 根据查询参数和分页 查询结果
	 * @param params 查询参数
	 * @param pageQuery 分页参数
	 * @param mapper 查询的mapper
	 * @param highLight  是否高亮显示查询结果
	 * @return
	 */
	IPage<Map> getResult(Map params, PageQuery pageQuery, BaseMapper mapper,Boolean highLight,List<String> searchField);
}
