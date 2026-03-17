package com.sysware.mainData.service;

import com.sysware.mainData.domain.HdlPersonJobInfo;
import com.sysware.mainData.domain.vo.HdlPersonJobInfoVo;
import com.sysware.mainData.domain.bo.HdlPersonJobInfoBo;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 员工工作信息数据Service接口
 *
 * @author aa
 * @date 2026-01-15
 */
public interface IHdlPersonJobInfoService {

    /**
     * 查询员工工作信息数据
     */
    HdlPersonJobInfoVo queryById(String pkPsnjob);

    /**
     * 查询员工工作信息数据列表
     */
    TableDataInfo queryPageList(HdlPersonJobInfoBo bo, PageQuery pageQuery);

    /**
     * 新增员工工作信息数据
     */
    Boolean insertByBo(HdlPersonJobInfoBo bo);

    /**
     * 修改员工工作信息数据
     */
    Boolean updateByBo(HdlPersonJobInfoBo bo);

    /**
     * 校验并批量删除员工工作信息数据信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 查询导出数据
     */
    List<HdlPersonJobInfo> selectPJIList(HdlPersonJobInfo pji);
}
