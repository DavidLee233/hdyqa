package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户配置关联 sys_user_config
 *
 * @author
 */

@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_user_config")

public class SysUserConfig {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 是否提醒
     */
    private Integer openNotify;
}
