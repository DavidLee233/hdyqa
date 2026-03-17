package com.sysware.system.mapper;

import com.sysware.common.core.mapper.BaseMapperPlus;
import com.sysware.system.domain.SysOss;
import com.sysware.system.domain.vo.SysOssVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件上传 数据层
 *
 * @author Lion Li
 */
public interface SysOssMapper extends BaseMapperPlus<SysOssMapper, SysOss, SysOssVo> {
    /**
     * 根据URL列表删除记录
     * @param urls 文件URL列表
     * @return 删除数量
     */
    @Delete("<script>" +
            "DELETE FROM sys_oss WHERE url IN " +
            "<foreach collection='urls' item='url' open='(' separator=',' close=')'>" +
            "#{url}" +
            "</foreach>" +
            "</script>")
    int deleteByUrls(@Param("urls") List<String> urls);
}
