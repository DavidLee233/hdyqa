package com.sysware.system.service;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.system.domain.bo.SysOssBo;
import com.sysware.system.domain.vo.SysOssVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 文件上传 服务层
 *
 * @author Lion Li
 */
public interface ISysOssService {

    TableDataInfo<SysOssVo> queryPageList(SysOssBo sysOss, PageQuery pageQuery);

    List<SysOssVo> listByIds(Collection<String> ossIds);

    SysOssVo getById(String ossId);

    SysOssVo upload(MultipartFile file);
    SysOssVo upload2(File file);

    void download(String ossId, HttpServletResponse response) throws IOException;

    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
    /**
     * 清理指定桶中超过指定天数的文件
     * @param bucketName 桶名称
     * @param days 保留天数
     * @return 删除的文件数量
     */
    int cleanupExpiredFiles(String bucketName, int days) throws Exception;
}
