package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户和角色关联 sys_user_role
 *
 * @author
 */

@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_user_role")
public class SysUserRole {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 角色ID
     */
    private String roleId;

}
