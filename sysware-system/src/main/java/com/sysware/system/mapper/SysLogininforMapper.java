package com.sysware.system.mapper;

import com.sysware.common.core.mapper.BaseMapperPlus;
import com.sysware.system.domain.SysLogininfor;
import com.sysware.system.domain.bo.SysLogininforBo;
import com.sysware.system.domain.vo.SysLogininforVo;

import java.util.List;
import java.util.Map;

/**
 * 系统访问日志情况信息 数据层
 *
 * @author
 */
public interface SysLogininforMapper extends BaseMapperPlus<SysLogininforMapper, SysLogininfor, SysLogininforVo> {
    /**
     * 根据时间选择需要的实体类集合（导出）
     * @param  logininfor
     * @return 操作结果
     */
    List<SysLogininfor> selectLogininforList(SysLogininforBo logininfor);
}
