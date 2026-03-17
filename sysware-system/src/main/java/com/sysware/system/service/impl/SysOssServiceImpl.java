package com.sysware.system.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.constant.CacheNames;
import com.sysware.common.core.OssClient;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.service.OssService;
import com.sysware.common.entity.UploadResult;
import com.sysware.common.enumd.AccessPolicyType;
import com.sysware.common.exception.ServiceException;
import com.sysware.common.factory.OssFactory;
import com.sysware.common.utils.BeanCopyUtils;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.utils.file.FileUtils;
import com.sysware.common.utils.spring.SpringUtils;
import com.sysware.system.domain.SysOss;
import com.sysware.system.domain.bo.SysOssBo;
import com.sysware.system.domain.vo.SysOssVo;
import com.sysware.system.mapper.SysOssMapper;
import com.sysware.system.service.ISysOssService;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件上传 服务层实现
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysOssServiceImpl implements ISysOssService, OssService {
    private final MinioClient minioClient;
    private final SysOssMapper baseMapper;

    @Override
    public TableDataInfo<SysOssVo> queryPageList(SysOssBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOss> lqw = buildQueryWrapper(bo);
        Page<SysOssVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        List<SysOssVo> filterResult = result.getRecords().stream().map(this::matchingUrl).collect(Collectors.toList());
        result.setRecords(filterResult);
        return TableDataInfo.build(result);
    }

    @Override
    public List<SysOssVo> listByIds(Collection<String> ossIds) {
        List<SysOssVo> list = new ArrayList<>();
        for (String id : ossIds) {
            SysOssVo vo = SpringUtils.getAopProxy(this).getById(id);
            if (ObjectUtil.isNotNull(vo)) {
                list.add(this.matchingUrl(vo));
            }
        }
        return list;
    }

    @Override
    public String selectUrlByIds(String ossIds) {
        List<String> list = new ArrayList<>();
        for (String id : StringUtils.splitTo(ossIds, Convert::toStr)) {
            SysOssVo vo = SpringUtils.getAopProxy(this).getById(id);
            if (ObjectUtil.isNotNull(vo)) {
                list.add(this.matchingUrl(vo).getUrl());
            }
        }
        return String.join(StringUtils.SEPARATOR, list);
    }

    private LambdaQueryWrapper<SysOss> buildQueryWrapper(SysOssBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysOss> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getFileName()), SysOss::getFileName, bo.getFileName());
        lqw.like(StringUtils.isNotBlank(bo.getOriginalName()), SysOss::getOriginalName, bo.getOriginalName());
        lqw.eq(StringUtils.isNotBlank(bo.getFileSuffix()), SysOss::getFileSuffix, bo.getFileSuffix());
        lqw.eq(StringUtils.isNotBlank(bo.getUrl()), SysOss::getUrl, bo.getUrl());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            SysOss::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        lqw.eq(StringUtils.isNotBlank(bo.getCreateBy()), SysOss::getCreateBy, bo.getCreateBy());
        lqw.eq(StringUtils.isNotBlank(bo.getService()), SysOss::getService, bo.getService());
        return lqw;
    }

    @Cacheable(cacheNames = CacheNames.SYS_OSS, key = "#ossId")
    @Override
    public SysOssVo getById(String ossId) {
        return baseMapper.selectVoById(ossId);
    }

    @Override
    public void download(String ossId, HttpServletResponse response) throws IOException {
        SysOssVo sysOss = getById(ossId);
        if (ObjectUtil.isNull(sysOss)) {
            throw new ServiceException("文件数据不存在!");
        }
        FileUtils.setAttachmentResponseHeader(response, sysOss.getOriginalName());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
        OssClient storage = OssFactory.instance();
        try(InputStream inputStream = storage.getObjectContent(sysOss.getUrl())) {
            int available = inputStream.available();
            IoUtil.copy(inputStream, response.getOutputStream(), available);
            response.setContentLength(available);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public SysOssVo upload(MultipartFile file) {

        String originalfileName = file.getOriginalFilename();
        String suffix = StringUtils.substring(originalfileName, originalfileName.lastIndexOf("."), originalfileName.length());
        OssClient storage = OssFactory.instance();
        UploadResult uploadResult;
        try {
            uploadResult = storage.uploadSuffix(file.getBytes(), suffix, file.getContentType());
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }

        // 保存文件信息
        SysOss oss = new SysOss();
        oss.setUrl(uploadResult.getUrl());
        oss.setFileSuffix(suffix);
        oss.setFileName(uploadResult.getFilename());
        oss.setOriginalName(originalfileName);
        oss.setService(storage.getConfigKey());
        oss.setFileSize(file.getSize());


        baseMapper.insert(oss);
        SysOssVo sysOssVo = new SysOssVo();
        BeanCopyUtils.copy(oss, sysOssVo);
        return this.matchingUrl(sysOssVo);
    }
    /**
     * @author lxd
     * @description: File类型的文件上传
     * @param file
     * @return com.sysware.system.domain.vo.SysOssVo
     * @date 2025/7/26
     **/
    @Override
    public SysOssVo upload2(File file) {
        // 参数校验
        if (file == null || !file.exists()) {
            throw new ServiceException("文件不存在或不可读");
        }

        String originalfileName = file.getName();
        String suffix = StringUtils.substring(originalfileName,
                originalfileName.lastIndexOf("."),
                originalfileName.length());

        OssClient storage = OssFactory.instance();
        UploadResult uploadResult;

        try (InputStream inputStream = new FileInputStream(file)) {
            // 方法1：直接使用文件流上传（推荐）
            uploadResult = storage.uploadSuffix(
                    inputStream,
                    suffix,
                    Files.probeContentType(file.toPath())
            );

            // 方法2：如果需要字节数组（兼容旧版API）
            // byte[] bytes = Files.readAllBytes(file.toPath());
            // uploadResult = storage.uploadSuffix(bytes, suffix, Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }

        // 保存文件信息
        SysOss oss = new SysOss();
        oss.setUrl(uploadResult.getUrl());
        oss.setFileSuffix(suffix);
        oss.setFileName(uploadResult.getFilename());
        oss.setOriginalName(originalfileName);
        oss.setService(storage.getConfigKey());
        oss.setFileSize(file.length()); // 使用file.length()获取大小

        baseMapper.insert(oss);
        SysOssVo sysOssVo = new SysOssVo();
        BeanCopyUtils.copy(oss, sysOssVo);
        return this.matchingUrl(sysOssVo);
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        List<SysOss> list = baseMapper.selectBatchIds(ids);
        for (SysOss sysOss : list) {
            OssClient storage = OssFactory.instance(sysOss.getService());
            storage.delete(sysOss.getUrl());
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
    @Override
    public int cleanupExpiredFiles(String bucketName, int days) {
        try {
            // 1. 清理MinIO中的物理文件
            List<String> deletedObjects = cleanupExpiredObjects(bucketName, days);

            // 2. 清理数据库记录
            if (!deletedObjects.isEmpty()) {
                return baseMapper.deleteByUrls(deletedObjects);
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("清理过期文件失败", e);
        }
    }
    /**
     * 清理过期文件
     *
     * @param bucketName 桶名称
     * @param days       保留天数
     * @return 删除的文件列表
     */
    public List<String> cleanupExpiredObjects(String bucketName, int days) throws Exception {
        List<String> deletedFiles = new ArrayList<>();
        Instant cutoffDate = Instant.now().minus(days, ChronoUnit.DAYS);

        // 1. 列出所有对象
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .build()
        );

        // 2. 筛选并删除过期文件
        for (Result<Item> result : results) {
            Item item = result.get();
            if (item.lastModified().toInstant().isBefore(cutoffDate)) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(item.objectName())
                                .build()
                );
                deletedFiles.add(item.objectName());
            }
        }
        return deletedFiles;
    }
    /**
     * 匹配Url
     *
     * @param oss OSS对象
     * @return oss 匹配Url的OSS对象
     */
    private SysOssVo matchingUrl(SysOssVo oss) {
        OssClient storage = OssFactory.instance(oss.getService());
        // 仅修改桶类型为 private 的URL，临时URL时长为120s
        if (AccessPolicyType.PRIVATE == storage.getAccessPolicy()) {
            oss.setUrl(storage.getPrivateUrl(oss.getFileName(), 120));
        }
        return oss;
    }
}
