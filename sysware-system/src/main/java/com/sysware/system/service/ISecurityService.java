package com.sysware.system.service;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.entity.SysSecurity;
import com.sysware.system.domain.vo.SecurityVo;
import com.sysware.system.domain.bo.SecurityQueryBo;
import com.sysware.system.domain.bo.SecurityAddBo;
import com.sysware.system.domain.bo.SecurityEditBo;
import com.sysware.common.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 密级Service接口
 *
 * @author zzr
 * @date 2022-01-05
 */
public interface ISecurityService{
	/**
	 * 查询单个
	 * @return
	 */
	SecurityVo queryById(String id);

	/**
	 * 查询列表
	 */
    TableDataInfo queryPageList(SecurityQueryBo bo, PageQuery query);

	/**
	 * 查询列表
	 */
	List<SecurityVo> queryList(SecurityQueryBo bo);

	/**
	 * 根据新增业务对象插入密级
	 * @param bo 密级新增业务对象
	 * @return
	 */
	Boolean insertByAddBo(SecurityAddBo bo);

	/**
	 * 根据编辑业务对象修改密级
	 * @param bo 密级编辑业务对象
	 * @return
	 */
	Boolean updateByEditBo(SecurityEditBo bo);

	/**
	 * 校验并删除数据
	 * @param ids 主键集合
	 * @param isValid 是否校验,true-删除前校验,false-不校验
	 * @return
	 */
	Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
	/**
	 * 获取所有的密级信息
	 */
	List<SysSecurity> selectSecurityList(String securityType);
}
