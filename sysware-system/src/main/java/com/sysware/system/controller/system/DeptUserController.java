package com.sysware.system.controller.system;

import com.sysware.common.core.domain.entity.SysDept;
import com.sysware.common.core.domain.entity.SysUser;
import com.sysware.system.domain.vo.DeptUserTreeVo;
import com.sysware.system.mapper.SysDeptMapper;
import com.sysware.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/deptUser")
public class DeptUserController {

    @Autowired
    private SysDeptMapper deptMapper;
    @Autowired
    private SysUserMapper userMapper;

    @GetMapping("/tree")
    public List<DeptUserTreeVo> deptUserTree(){
        // 查出所有的部门和员工（userName是工号，nickName是姓名）
        List<SysDept> depts = deptMapper.selectAllDept();
        Map<String, String> deptMap = depts.stream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName));
        List<SysUser> users = userMapper.selectAllUser();
        Map<String, List<SysUser>> userMap = users.stream().collect(Collectors.groupingBy(SysUser::getDeptId));
        // 将部门转换为VO
        Map<String, DeptUserTreeVo> nodeMap = depts.stream().collect(Collectors.toMap(
                SysDept::getDeptId,
                d -> {
                    DeptUserTreeVo vo = new DeptUserTreeVo();
                    vo.setId(d.getDeptId());
                    vo.setLabel(d.getDeptName());
                    vo.setType("dept");
                    vo.setChildren(new ArrayList<>());
                    return vo;
                }));
        // 挂员工到对应部门
        userMap.forEach((deptId, list) -> {
            DeptUserTreeVo parent = nodeMap.get(deptId);
            if (parent == null) return;
            list.forEach(e -> {
                DeptUserTreeVo u = new DeptUserTreeVo();
                u.setId(e.getUserId());
                u.setLabel(e.getUserName());
                u.setType("user");
                u.setUserName(e.getLoginName());
                u.setDeptName(deptMap.get(deptId));
                u.setDeptId(deptId);
                parent.getChildren().add(u);
            });
        });
        // 组装成树（根据parent_id挂到根）
        List<DeptUserTreeVo> result = new ArrayList<>();
        nodeMap.values().forEach(n -> {
            SysDept raw = depts.stream().filter(d -> d.getDeptId().equals(n.getId())).findFirst().orElse(null);
            if (raw == null || raw.getParentId() == null || raw.getParentId().equals("0")){
                result.add(n);
            }else{
                DeptUserTreeVo p = nodeMap.get(raw.getParentId());
                if(p != null) p.getChildren().add(n);
            }
        });
        return result;
    }
}
