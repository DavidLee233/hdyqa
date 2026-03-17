package com.sysware.web.controller.system;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.sysware.common.annotation.Log;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.validate.QueryGroup;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.exception.ServiceException;
import com.sysware.common.utils.SyswareMD5Util;
import com.sysware.system.domain.bo.SysOssBo;
import com.sysware.system.domain.vo.SysOssVo;
import com.sysware.system.service.ISysOssService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传 控制层
 *
 * @author
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/oss")
public class SysOssController extends BaseController {

    private final ISysOssService iSysOssService;

    /**
     * 查询OSS对象存储列表
     */
    @GetMapping("/list")
    public TableDataInfo<SysOssVo> list(@Validated(QueryGroup.class) SysOssBo bo, PageQuery pageQuery) {
        return iSysOssService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询OSS对象基于id串
     *
     * @param ossIds OSS对象ID串
     */
    @GetMapping("/listByIds/{ossIds}")
    public R<List<SysOssVo>> listByIds(@NotEmpty(message = "主键不能为空")
                                       @PathVariable String[] ossIds) {
        List<SysOssVo> list = iSysOssService.listByIds(Arrays.asList(ossIds));
        return R.ok(list);
    }

    /**
     * 上传OSS对象存储
     *
     * @param file 文件
     */
    @Log(title = "OSS对象存储", businessType = BusinessType.INSERT)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R upload(@RequestPart("file") MultipartFile file) {
        if (ObjectUtil.isNull(file)) {
            throw new ServiceException("上传文件不能为空");
        }
        SysOssVo oss = iSysOssService.upload(file);
        try {
            System.out.println(SyswareMD5Util.generateFileMD5((FileInputStream)file.getInputStream()));

        }catch (Exception e){

        }
       /* Map<String, String> map = new HashMap<>(2);
        map.put("url", oss.getUrl());
        map.put("fileName", oss.getOriginalName());
        map.put("fileId", oss.getOssId());*/
        return R.ok(oss);
    }

    /**
     * 下载OSS对象
     *
     * @param ossId OSS对象ID
     */
    @GetMapping("/download/{ossId}")
    public void download(@PathVariable String ossId, HttpServletResponse response) throws IOException {
        iSysOssService.download(ossId,response);
    }

    /**
     * 删除OSS对象存储
     *
     * @param ossIds OSS对象ID串
     */
    @Log(title = "OSS对象存储", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ossIds) {
        return toAjax(iSysOssService.deleteWithValidByIds(Arrays.asList(ossIds), true));
    }

}
