package com.sysware.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sysware.common.core.domain.entity.SysDictData;
import com.sysware.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 字典表 数据层
 *
 * @author
 */
@InterceptorIgnore(dataPermission = "true")
public interface SysDictDataMapper extends BaseMapperPlus<SysDictDataMapper,SysDictData, Map> {

	/**
	 * 根据数据字典类型获取当前类型下的字典值
	 * @param dictType
	 * @return
	 */
	default List<SysDictData> selectDictDataByType(String dictType) {
		return selectList(
			new LambdaQueryWrapper<SysDictData>()
				//.eq(SysDictData::getStatus, "0")
				.eq(SysDictData::getDictType, dictType)
				.orderByAsc(SysDictData::getSort));
	}

	/**
	 * 获取系统表的信息
	 * @return
	 */
	List<Map> selectSysTableInfo();

	/**
	 * 通过表名称获取系统中表的字段
	 * @param tableName
	 * @return
	 */
	List<Map> getSysTableField(@Param("tableName") String tableName);
}
