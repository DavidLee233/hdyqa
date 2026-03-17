package com.sysware.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 角色和部门关联 sys_role_dept
 *
 * @author
 */

@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_role_dept")
public class SysRoleDept {
    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 部门ID
     */
    private String deptId;

}
