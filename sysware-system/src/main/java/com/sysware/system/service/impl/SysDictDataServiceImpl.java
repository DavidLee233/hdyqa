package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.constant.CacheNames;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.entity.SysDictData;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.exception.ServiceException;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.utils.SyswareUtil;
import com.sysware.common.utils.redis.CacheUtils;
import com.sysware.system.mapper.SysDictDataMapper;
import com.sysware.system.service.ISysDictDataService;
import com.sysware.system.service.ISysTableFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 *
 * @author
 */
@RequiredArgsConstructor
@Service
public class SysDictDataServiceImpl  implements ISysDictDataService {

	private final SysDictDataMapper baseMapper;
	private final ISysTableFieldService tableFieldService;

	@Override
	public TableDataInfo<Map> selectPageDictDataList(SysDictData dictData, PageQuery pageQuery) {
		Map params = BeanUtil.beanToMap(dictData);
		pageQuery.setOrderByColumn("sort");
		pageQuery.setIsAsc("asc");

		List<String> fields = new ArrayList<>();
		fields.add("dictType");

		IPage<Map> result = tableFieldService.getResult(params,pageQuery,baseMapper,fields);


		List<Map> parentId = SyswareUtil.getTreeMap(result.getRecords(),"dictCode","parentId","0",false);
		result.setRecords(parentId);

		return TableDataInfo.build(result);

	}

	/**
	 * 根据条件分页查询字典数据
	 *
	 * @param dictData 字典数据信息
	 * @return 字典数据集合信息
	 */
	@Override
	public List<SysDictData> selectDictDataList(SysDictData dictData) {

		return baseMapper.selectList(new LambdaQueryWrapper<SysDictData>()
				.eq(StrUtil.isNotBlank(dictData.getDictType()), SysDictData::getDictType, dictData.getDictType())
				.like(StrUtil.isNotBlank(dictData.getDictLabel()), SysDictData::getDictLabel, dictData.getDictLabel())
				.eq(StrUtil.isNotBlank(dictData.getStatus()), SysDictData::getStatus, dictData.getStatus())
				.orderByAsc(SysDictData::getSort));
	}

	/**
	 * 根据字典类型和字典键值查询字典数据信息
	 *
	 * @param dictType  字典类型
	 * @param dictValue 字典键值
	 * @return 字典标签
	 */
	@Override
	public String selectDictLabel(String dictType, String dictValue) {

		return Optional.ofNullable(selectDictData(dictType,dictValue)).map(i -> i.getDictLabel()).get();
	}

	/**
	 * 根据字典类型和字典键值查询字典数据信息
	 *
	 * @param dictType  字典类型
	 * @param dictValue 字典键值
	 * @return 字典标签
	 */
	@Override
	public SysDictData selectDictData(String dictType, String dictValue) {

		return baseMapper.selectVoOne(new LambdaQueryWrapper<SysDictData>()
				.eq(SysDictData::getDictType, dictType)
				.eq(SysDictData::getDictValue, dictValue),SysDictData.class);
	}

	@Override
	public List<Map> selectSysTableInfo() {
		return baseMapper.selectSysTableInfo();
	}


	@Override
	public List<Map> getSysTableField(String tableName) {

		List<Map> sysTableField = baseMapper.getSysTableField(tableName);
		List<Map> newResult = new ArrayList<>();
		//将字段名称中的下划线转换为驼峰
		sysTableField.forEach(map -> {
			Map newMap = new HashMap();
			map.forEach((key,value) -> {
				newMap.put(key, StringUtils.toCamelCase(String.valueOf(value)));
			});
			newResult.add(newMap);
		});

		return  newResult;
	}

	/**
	 * 根据字典数据ID查询信息
	 *
	 * @param dictCode 字典数据ID
	 * @return 字典数据
	 */
	@Override
	public SysDictData selectDictDataById(String dictCode) {
		return baseMapper.selectById(dictCode);
	}

	/**
	 * 批量删除字典数据信息
	 *
	 * @param dictCodes 需要删除的字典数据ID
	 * @return 结果
	 */
	@Override
	public void deleteDictDataByIds(String[] dictCodes) {
		for (String dictCode : dictCodes) {
			SysDictData data = selectDictDataById(dictCode);
			baseMapper.deleteById(dictCode);
			CacheUtils.evict(CacheNames.SYS_DICT, data.getDictType());
		}
	}

	/**
	 * 新增保存字典数据信息
	 *
	 * @param data 字典数据信息
	 * @return 结果
	 */
	@CachePut(cacheNames = CacheNames.SYS_DICT, key = "#data.dictType")
	@Override
	public List<SysDictData> insertDictData(SysDictData data) {
		int row = baseMapper.insert(data);
		if (row > 0) {



			return baseMapper.selectDictDataByType(data.getDictType());
		}
		throw new ServiceException("操作失败");
	}

	/**
	 * 修改保存字典数据信息
	 *
	 * @param data 字典数据信息
	 * @return 结果
	 */
	@CachePut(cacheNames = CacheNames.SYS_DICT, key = "#data.dictType")
	@Override
	public List<SysDictData> updateDictData(SysDictData data) {
		int row = baseMapper.updateById(data);
		if (row > 0) {
			return baseMapper.selectDictDataByType(data.getDictType());
		}
		throw new ServiceException("操作失败");
	}
}
