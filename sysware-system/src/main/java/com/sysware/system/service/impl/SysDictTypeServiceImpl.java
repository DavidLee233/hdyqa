package com.sysware.system.service.impl;

import cn.dev33.satoken.context.SaHolder;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.constant.CacheConstants;
import com.sysware.common.constant.CacheNames;
import com.sysware.common.constant.UserConstants;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.entity.SysDictData;
import com.sysware.common.core.domain.entity.SysDictType;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.service.DictService;
import com.sysware.common.exception.ServiceException;
import com.sysware.common.utils.StreamUtils;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.utils.SyswareUtil;
import com.sysware.common.utils.redis.CacheUtils;
import com.sysware.common.utils.spring.SpringUtils;
import com.sysware.system.mapper.SysDictDataMapper;
import com.sysware.system.mapper.SysDictTypeMapper;
import com.sysware.system.service.ISysDictTypeService;
import com.sysware.system.service.ISysTableFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 *
 * @author
 */
@RequiredArgsConstructor
@Service
public class SysDictTypeServiceImpl  implements ISysDictTypeService, DictService {

	private final SysDictDataMapper dictDataMapper;
	private final SysDictTypeMapper baseMapper;
	private final ISysTableFieldService tableFieldService;

	/**
	 * 项目启动时，初始化字典到缓存
	 */
	@PostConstruct
	public void init() {
		loadingDictCache();
	}

	@Override
	public TableDataInfo<Map> selectPageDictTypeList(SysDictType dictType, PageQuery pageQuery) {

		Map params = BeanUtil.beanToMap(dictType);

		IPage<Map> result = tableFieldService.getResult(params, pageQuery,baseMapper,null);
		return TableDataInfo.build(result);
	}

	/**
	 * 根据条件分页查询字典类型
	 *
	 * @param dictType 字典类型信息
	 * @return 字典类型集合信息
	 */
	@Override
	public List<SysDictType> selectDictTypeList(SysDictType dictType) {
		Map<String, Object> params = dictType.getParams();
		return baseMapper.selectList(new LambdaQueryWrapper<SysDictType>()
			.like(StrUtil.isNotBlank(dictType.getDictName()), SysDictType::getDictName, dictType.getDictName())
			.eq(StrUtil.isNotBlank(dictType.getStatus()), SysDictType::getStatus, dictType.getStatus())
			.like(StrUtil.isNotBlank(dictType.getDictType()), SysDictType::getDictType, dictType.getDictType())
			.apply(Validator.isNotEmpty(params.get("beginTime")),
				"date_format(create_time,'%y%m%d') >= date_format({0},'%y%m%d')",
				params.get("beginTime"))
			.apply(Validator.isNotEmpty(params.get("endTime")),
				"date_format(create_time,'%y%m%d') <= date_format({0},'%y%m%d')",
				params.get("endTime")));
	}

	/**
	 * 根据所有字典类型
	 *
	 * @return 字典类型集合信息
	 */
	@Override
	public List<SysDictType> selectDictTypeAll() {
		return baseMapper.selectList();
	}

	/**
	 * 根据字典类型查询字典数据
	 *
	 * @param dictType 字典类型
	 * @return 字典数据集合信息
	 */
	//@Cacheable(cacheNames = CacheNames.SYS_DICT, key = "#dictType")
	@Override
	public List<SysDictData> selectDictDataByType(String dictType) {

		List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictType);


		if (CollUtil.isNotEmpty(dictDatas)) {
			return dictDatas;
		}
		return null;
	}

	/**
	 * 根据字典类型ID查询信息
	 *
	 * @param dictId 字典类型ID
	 * @return 字典类型
	 */
	@Override
	public SysDictType selectDictTypeById(String dictId) {
		return baseMapper.selectById(dictId);
	}

	/**
	 * 根据字典类型查询信息
	 *
	 * @param dictType 字典类型
	 * @return 字典类型
	 */
	@Override
	public SysDictType selectDictTypeByType(String dictType) {
		return baseMapper.selectOne(new LambdaQueryWrapper<SysDictType>().eq(SysDictType::getDictType, dictType));
	}

	/**
	 * 批量删除字典类型信息
	 *
	 * @param dictIds 需要删除的字典ID
	 * @return 结果
	 */
	@Override
	public void deleteDictTypeByIds(String[] dictIds) {
		for (String dictId : dictIds) {
			SysDictType dictType = selectDictTypeById(dictId);
			if (dictDataMapper.selectCount(new LambdaQueryWrapper<SysDictData>()
				.eq(SysDictData::getDictType, dictType.getDictType())) > 0) {
				throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
			}
			CacheUtils.evict(CacheNames.SYS_DICT, dictType.getDictType());
		}
		baseMapper.deleteBatchIds(Arrays.asList(dictIds));
	}

	/**
	 * 加载字典缓存数据
	 */
	@Override
	public void loadingDictCache() {
		List<SysDictType> dictTypeList = baseMapper.selectList();
		for (SysDictType dictType : dictTypeList) {
			List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictType.getDictType());
			//CacheUtils.evict(CacheNames.SYS_DICT, dictType.getDictType());
			CacheUtils.put(CacheNames.SYS_DICT,dictType.getDictType(),dictDatas);
		}
	}

	/**
	 * 清空字典缓存数据
	 */
	@Override
	public void clearDictCache() {
		CacheUtils.clear(CacheNames.SYS_DICT);
	}

	/**
	 * 重置字典缓存数据
	 */
	@Override
	public void resetDictCache() {
		clearDictCache();
		loadingDictCache();
	}

	/**
	 * 新增保存字典类型信息
	 *
	 * @param dict 字典类型信息
	 * @return 结果
	 */
	@CachePut(cacheNames = CacheNames.SYS_DICT, key = "#dict.dictType")
	@Override
	public List<SysDictData> insertDictType(SysDictType dict) {
		int row = baseMapper.insert(dict);
		if (row > 0) {
			return new ArrayList<>();
		}
		throw new ServiceException("操作失败");
	}

	/**
	 * 修改保存字典类型信息
	 *
	 * @param dict 字典类型信息
	 * @return 结果
	 */
	@Override
	@Transactional
	public List<SysDictData> updateDictType(SysDictType dict) {
		SysDictType oldDict = baseMapper.selectById(dict.getDictId());
		dictDataMapper.update(null, new LambdaUpdateWrapper<SysDictData>()
				.set(SysDictData::getDictType, dict.getDictType())
				.eq(SysDictData::getDictType, oldDict.getDictType()));
		int row = baseMapper.updateById(dict);
		if (row > 0) {
			CacheUtils.evict(CacheNames.SYS_DICT, oldDict.getDictType());
			return dictDataMapper.selectDictDataByType(dict.getDictType());
		}
		throw new ServiceException("操作失败");
	}

	/**
	 * 校验字典类型称是否唯一
	 *
	 * @param dict 字典类型
	 * @return 结果
	 */
	@Override
	public boolean checkDictTypeUnique(SysDictType dict) {
		boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysDictType>()
				.eq(SysDictType::getDictType, dict.getDictType())
				.ne(ObjectUtil.isNotNull(dict.getDictId()), SysDictType::getDictId, dict.getDictId()));
		return !exist;
	}

	/**
	 * 根据字典类型和字典值获取字典标签
	 *
	 * @param dictType  字典类型
	 * @param dictValue 字典值
	 * @param separator 分隔符
	 * @return 字典标签
	 */
	@SuppressWarnings("unchecked cast")
	@Override
	public String getDictLabel(String dictType, String dictValue, String separator) {
		// 优先从本地缓存获取
		List<SysDictData> datas = (List<SysDictData>) SaHolder.getStorage().get(CacheConstants.SYS_DICT_KEY + dictType);
		if (ObjectUtil.isNull(datas)) {
			datas = SpringUtils.getAopProxy(this).selectDictDataByType(dictType);
			SaHolder.getStorage().set(CacheConstants.SYS_DICT_KEY + dictType, datas);
		}

		Map<String, String> map = StreamUtils.toMap(datas, SysDictData::getDictValue, SysDictData::getDictLabel);
		if (StringUtils.containsAny(dictValue, separator)) {
			return Arrays.stream(dictValue.split(separator))
					.map(v -> map.getOrDefault(v, StringUtils.EMPTY))
					.collect(Collectors.joining(separator));
		} else {
			return map.getOrDefault(dictValue, StringUtils.EMPTY);
		}
	}
	/**
	 * 根据字典类型和字典标签获取字典值
	 *
	 * @param dictType  字典类型
	 * @param dictLabel 字典标签
	 * @param separator 分隔符
	 * @return 字典值
	 */
	@Override
	public String getDictValue(String dictType, String dictLabel, String separator) {
		// 优先从本地缓存获取
		List<SysDictData> datas = (List<SysDictData>) SaHolder.getStorage().get(CacheConstants.SYS_DICT_KEY + dictType);
		if (ObjectUtil.isNull(datas)) {
			datas = SpringUtils.getAopProxy(this).selectDictDataByType(dictType);
			SaHolder.getStorage().set(CacheConstants.SYS_DICT_KEY + dictType, datas);
		}

		Map<String, String> map = StreamUtils.toMap(datas, SysDictData::getDictLabel, SysDictData::getDictValue);
		if (StringUtils.containsAny(dictLabel, separator)) {
			return Arrays.stream(dictLabel.split(separator))
					.map(l -> map.getOrDefault(l, StringUtils.EMPTY))
					.collect(Collectors.joining(separator));
		} else {
			return map.getOrDefault(dictLabel, StringUtils.EMPTY);
		}
	}
}
