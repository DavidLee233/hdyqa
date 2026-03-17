package com.sysware.common.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sysware.common.core.domain.entity.SysDept;
import com.sysware.common.core.domain.entity.SysMenu;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 * 
 * @author
 */

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TreeSelect implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private String id;

    /** 节点名称 */
    private String label;

    /** 节点路由 */
    private String path;

    /** 节点类型 */
    private String menuType;

    /** 节点主键 */
    private String component;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect(SysDept dept)
    {
        this.id = dept.getDeptId();
        this.label = dept.getDeptName();
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(SysMenu menu)
    {
        this.id = menu.getMenuId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
        this.menuType = menu.getMenuType();
        this.component = menu.getComponent();
        this.path = menu.getPath();
    }

}
