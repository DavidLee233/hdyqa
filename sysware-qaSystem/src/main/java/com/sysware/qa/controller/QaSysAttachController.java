package com.sysware.qa.controller;

import com.sysware.qa.domain.bo.FileConvertResponse;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.sysware.common.annotation.Log;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.R;
import com.sysware.common.enums.BusinessType;
import com.sysware.qa.service.IQaSysAttachService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 问题回答附件
 *
 * @author aa
 * @date 2025-07-20
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/qaSystem/qaAttach")
public class QaSysAttachController extends BaseController {

    private final IQaSysAttachService iQaSysAttachService;

    @Log(title = "删除问题回答附件", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{ossId}")
    public R<Void> updateDeleteRes(@NotNull(message = "主键不能为空") @PathVariable Long ossId) {
        return iQaSysAttachService.updateDeleteRes(ossId);
    }
   @Log(title = "附件转换接口", businessType = BusinessType.EXPORT)
    @PostMapping("/convert")
    public R<FileConvertResponse> convert2Pdf(@RequestParam("file") MultipartFile file,
                                              @RequestParam("outputType") String outputType) {
        return iQaSysAttachService.convert2PDF(file, outputType);
    }
}
