package com.sysware.system.service;

import com.sysware.system.domain.DocType;
import com.sysware.system.domain.vo.DocTypeVo;
import com.sysware.system.domain.bo.DocTypeBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 文件类型Service接口
 *
 * @author aa
 * @date 2023-09-02
 */
public interface IDocTypeService {

    /**
     * 查询文件类型
     */
    DocTypeVo queryById(String createBy);

    /**
     * 查询文件类型列表
     */
    TableDataInfo queryPageList(DocTypeBo bo, PageQuery pageQuery);

    /**
     * 新增文件类型
     */
    Boolean insertByBo(DocTypeBo bo);

    /**
     * 修改文件类型
     */
    Boolean updateByBo(DocTypeBo bo);

    /**
     * 校验并批量删除文件类型信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);


    List<DocTypeVo> queryList(DocTypeBo bo);
}
