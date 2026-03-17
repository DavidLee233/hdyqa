package com.sysware.qa.service.impl;

import com.sysware.qa.domain.bo.FileConvertResponse;
import com.sysware.common.core.domain.R;
import com.sysware.common.factory.OssFactory;
import com.sysware.system.domain.vo.SysOssVo;
import com.sysware.system.mapper.SysOssMapper;
import com.sysware.system.service.ISysOssService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.sysware.qa.domain.vo.QaSysAttachVo;
import com.sysware.qa.mapper.QaSysAttachMapper;
import com.sysware.qa.service.IQaSysAttachService;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.sysware.qa.utils.attachUtils.*;

/**
 * 问题回答附件Service业务层处理
 *
 * @author aa
 * @date 2025-07-20
 */
@RequiredArgsConstructor
@Service
public class QaSysAttachServiceImpl implements IQaSysAttachService {

    @Value("${libreoffice.path}")
    private String libreOfficePath;
    @Value("${libreoffice.tempDir}")
    private String tempDir;
    @Autowired
    private String bucketName;
    private final QaSysAttachMapper baseMapper;
    private final SysOssMapper ossMapper;
    private final ISysOssService iSysOssService;

    /**
     * @author lxd
     * @description: 更新删除问答附件数据表
     * @param ossId
     * @return com.sysware.common.core.domain.R<java.lang.Void>
     * @date 2025/7/26
     **/
    @Override
    public R<Void> updateDeleteRes(Long ossId) {
        if (ossId == null) {
            return R.fail("删除失败，文件不存在");
        }
        // 删除qa_sys_attach数据库中对应的附件信息（若有：已提交表单后修改时需删数据库，若无：未提交表单时不需删数据库）
        List<QaSysAttachVo> qaSysAttachVos = baseMapper.selectByOssId(ossId);
        if (qaSysAttachVos != null && !qaSysAttachVos.isEmpty()){
            qaSysAttachVos.forEach(vo -> {
                baseMapper.deleteById(vo.getId());
            });

        }
        // 删除sys_oss数据库中对应的附件信息
        return ossMapper.deleteById(ossId) > 0 ? R.ok() : R.fail("删除失败");
    }

    /**
     * @author lxd
     * @description: 将word\excel\ppt等文件转换为pdf文件
     * @param file
	 * @param outputType
     * @return com.sysware.common.core.domain.R<com.sysware.archives.domain.bo.FileConvertResponse>
     * @date 2025/7/26
     **/
    @Override
    public R<FileConvertResponse> convert2PDF(MultipartFile file, String outputType) {
        try{
            // 验证文件类型
            if (!isSupportedFileType(Objects.requireNonNull(file.getOriginalFilename()))) {
                return R.fail("不支持的文件类型");
            }

            // 验证输出类型
            if (!"pdf".equalsIgnoreCase(outputType)) {
                return R.fail("目前仅支持转换为PDF");
            }
            checkDir(tempDir);
            // 1. 保存临时文件
            File tempFile = saveTempFile(file, tempDir);

            // 2. 调用转换工具
            File pdfFile = convertWithLibreOffice(tempFile, tempDir, libreOfficePath);

            // 3. 生成访问URL
            String pdfUrl = generateFileUrl(pdfFile);

            // 4. 清理临时文件
            cleanTempFiles(tempFile, pdfFile);
            return R.ok(new FileConvertResponse(pdfFile.getName(), pdfUrl, pdfFile.length()));
        }catch (Exception e) {
            return R.fail("文件转换失败: " + e.getMessage());
        }
    }

    /**
     * @author lxd
     * @description: 生成文件URL
     * @param file
     * @return java.lang.String
     * @date 2025/7/26
     **/
    public String generateFileUrl(File file) {
        // 实际项目中应上传到文件存储服务（如OSS/MinIO）并返回CDN地址
        // 这里简化为返回可下载的临时URL
        SysOssVo ossVo = iSysOssService.upload2(file);
        // 2. 异步清理过期文件（7天前）
        CompletableFuture.runAsync(() -> {
            try {
                String currentBucket = OssFactory.instance().properties.getBucketName();
                iSysOssService.cleanupExpiredFiles(currentBucket, 7);
            } catch (Exception e) {
            }
        });
        return ossVo.getUrl();
    }

    /**
     * @author lxd
     * @description: 定时清理minio文件
     * @param
     * @return void
     * @date 2025/7/26
     **/
    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨3点执行
    private void scheduledCleanup() throws Exception {
        String currentBucket = OssFactory.instance().properties.getBucketName();
        iSysOssService.cleanupExpiredFiles(currentBucket, 7);
    }
}
