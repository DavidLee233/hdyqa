package com.sysware.system.service;

import com.sysware.common.core.domain.R;
import com.sysware.system.domain.DocField;
import com.sysware.system.domain.vo.DocFieldVo;
import com.sysware.system.domain.bo.DocFieldBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 文档著录字段Service接口
 *
 * @author aa
 * @date 2023-09-02
 */
public interface IDocFieldService {

    /**
     * 查询文档著录字段
     */
    DocFieldVo queryById(String id);

    /**
     * 查询文档著录字段列表
     */
    TableDataInfo queryPageList(DocFieldBo bo, PageQuery pageQuery);

    /**
     * 新增文档著录字段
     */
    R insertByBo(List<DocFieldBo> list);

    /**
     * 修改文档著录字段
     */
    R updateByBo(DocFieldBo bo);

    /**
     * 校验并批量删除文档著录字段信息
     */
    R deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 分页查询需要导入的字段列表
     * @param bo
     * @return
     */
    List<Map> queryImportFieldList(DocFieldBo bo);

    /**
     * 通过字段类型获取文档著录字段
     * @param fieldId
     * @return
     */
    List<DocFieldVo> selectVoListByType(Collection<String> fieldId);
}
