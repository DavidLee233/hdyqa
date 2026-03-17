package com.sysware.common.config;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author
 */

@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "sysware")
public class SyswareConfig {

    /**
     * 项目名称
     */
    private String name;

    /**
     * 版本
     */
    private String version;

    /**
     * 版权年份
     */
    private String copyrightYear;

    /**
     * 实例演示开关
     */
    private boolean demoEnabled;

    /**
     * 缓存懒加载
     */
    private boolean cacheLazy;

    /** 上传路径 */
   // private String profile;

    /** 上传路径 */
    private static String profile;

    /**
     * 获取地址开关
     */
    @Getter
    private static boolean addressEnabled;

    public void setAddressEnabled(boolean addressEnabled) {
        SyswareConfig.addressEnabled = addressEnabled;
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }

    public static String getProfile() {
        return profile;
    }
    public void setProfile(String profile)
    {
        SyswareConfig.profile = profile;
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }
}
