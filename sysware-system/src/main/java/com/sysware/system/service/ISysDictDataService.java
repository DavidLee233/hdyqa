package com.sysware.system.service;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.entity.SysDictData;
import com.sysware.common.core.page.TableDataInfo;

import java.util.List;
import java.util.Map;

/**
 * 字典 业务层
 *
 * @author
 */
public interface ISysDictDataService {


    TableDataInfo<Map> selectPageDictDataList(SysDictData dictData, PageQuery pageQuery);

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    public List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 根据字典类型和字典键值查询字典数据名称
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    public String selectDictLabel(String dictType, String dictValue);

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    public SysDictData selectDictDataById(String dictCode);

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    public void deleteDictDataByIds(String[] dictCodes);

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    public List<SysDictData> insertDictData(SysDictData dictData);

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    public List<SysDictData> updateDictData(SysDictData dictData);

    /**
     * 根据字典类型和字典键值查询字典信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    SysDictData selectDictData(String dictType, String dictValue);

    /**
     * 获取系统中表的信息
     * @return
     */
    List<Map> selectSysTableInfo();

    /**
     * 根据表名称获取表的字段
     * @param tableName
     * @return
     */
    List<Map> getSysTableField(String tableName);
}
