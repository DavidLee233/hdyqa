package com.sysware.qa.service;

import com.sysware.qa.domain.bo.FileConvertResponse;
import com.sysware.common.core.domain.R;
import org.springframework.web.multipart.MultipartFile;

/**
 * 问题回答附件Service接口
 *
 * @author aa
 * @date 2025-07-20
 */
public interface IQaSysAttachService {
    /**
     * 根据OSSID删除问题回答附件
     */
    R<Void> updateDeleteRes(Long ossId);
    /**
     * 在线转换PDF
     */
    R<FileConvertResponse> convert2PDF(MultipartFile file, String outputType);
}
