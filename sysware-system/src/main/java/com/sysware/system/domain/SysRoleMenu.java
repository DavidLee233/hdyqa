package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 角色和菜单关联 sys_role_menu
 *
 * @author
 */

@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_role_menu")
public class SysRoleMenu {
    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 菜单ID
     */
    private String menuId;

}
