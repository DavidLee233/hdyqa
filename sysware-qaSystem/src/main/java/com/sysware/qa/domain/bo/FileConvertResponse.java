package com.sysware.qa.domain.bo;/**
 * @Author lxd
 * @Date 2025/7/26 下午3:55
 * @PackageName:com.sysware.archives.domain.bo
 * @ClassName: FileConvertResponse
 * @Description: TODO
 * @Version 1.0
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName FileConvertResponse
 * @Description
 * @Author liuXXX
 * @date 2025-07-26
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileConvertResponse {
    private String filename;
    private String fileUrl;
    private long fileSize;
}
