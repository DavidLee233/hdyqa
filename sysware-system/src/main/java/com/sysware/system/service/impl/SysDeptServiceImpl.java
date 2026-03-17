package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sysware.common.constant.UserConstants;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.TreeSelect;
import com.sysware.common.core.domain.entity.SysDept;
import com.sysware.common.core.domain.entity.SysRole;
import com.sysware.common.core.domain.entity.SysUser;
import com.sysware.common.core.service.DeptService;
import com.sysware.common.exception.ServiceException;
import com.sysware.common.utils.BeanCopyUtils;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.utils.spring.SpringUtils;
import com.sysware.system.mapper.SysDeptMapper;
import com.sysware.system.mapper.SysRoleMapper;
import com.sysware.system.mapper.SysUserMapper;
import com.sysware.system.service.ISysDeptService;
import com.sysware.system.service.ISysTableFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门管理 服务实现
 *
 * @author
 */
@RequiredArgsConstructor
@Service
public class SysDeptServiceImpl implements ISysDeptService, DeptService {

    private final SysRoleMapper roleMapper;

    private final SysUserMapper userMapper;

    private final SysDeptMapper baseMapper;

    private final ISysTableFieldService tableFieldService;

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    public List<SysDept> selectDeptList(SysDept dept) {

        PageQuery pageQuery = new PageQuery();
        IPage<Map> result = tableFieldService.getResult(BeanUtil.beanToMap(dept),pageQuery , baseMapper, new ArrayList<>());
        List<Map> records = result.getRecords();
        List<SysDept> sysDepts = new ArrayList<>(); //baseMapper.selectDeptList(dept);
        records.forEach(map -> sysDepts.add(BeanCopyUtils.mapToBean(map,SysDept.class)));
        return buildDeptTree(sysDepts);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<SysDept>();
        List<String> tempList = new ArrayList<String>();
        for (SysDept dept : depts) {
            tempList.add(dept.getDeptId());
        }
		for (SysDept dept : depts) {
			// 如果是顶级节点, 遍历该父节点的所有子节点
			if (!tempList.contains(dept.getParentId())) {
				recursionFn(depts, dept);
				returnList.add(dept);
			}
		}
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts) {
        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Integer> selectDeptListByRoleId(String roleId) {
        SysRole role = roleMapper.selectById(roleId);
        return null;//baseMapper.selectDeptListByRoleId(roleId, role.getDeptCheckStrictly());
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDept selectDeptById(String deptId) {
        return baseMapper.selectById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public Long selectNormalChildrenDeptById(String deptId) {
      return  baseMapper.selectCount(new LambdaQueryWrapper<SysDept>()
              .eq(SysDept::getStatus, 0)
              .apply("find_in_set({0}, ancestors)", deptId));

    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(String deptId) {
        Long result = baseMapper.selectCount(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getParentId, deptId)
                .last("limit 1"));
        return result > 0l;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(String deptId) {
        Long result = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeptId, deptId));
        return result > 0l;
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getDeptName, dept.getDeptName())
                .eq(SysDept::getParentId, dept.getParentId())
                .ne(ObjectUtil.isNotNull(dept.getDeptId()), SysDept::getDeptId, dept.getDeptId()));
        return !exist;
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(SysDept dept) {
        dept.setStatus("0");
        dept.setDelFlag("0");
        SysDept info = baseMapper.selectById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
            throw new ServiceException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return baseMapper.insert(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int updateDept(SysDept dept) {
        SysDept newParentDept = baseMapper.selectById(dept.getParentId());
        SysDept oldDept = baseMapper.selectById(dept.getDeptId());
        if (Validator.isNotNull(newParentDept) && Validator.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = baseMapper.updateById(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept) {
		String ancestors = dept.getAncestors();
		String[] deptIds = Convert.toStrArray(ancestors);
        baseMapper.update(null, new LambdaUpdateWrapper<SysDept>()
                .set(SysDept::getStatus, "0")
                .in(SysDept::getDeptId, Arrays.asList(deptIds)));

    }

    /**
     * 修改子元素关系
     *
     * @param deptId 被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(String deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = baseMapper.selectList(new LambdaQueryWrapper<SysDept>()
                .apply("find_in_set({0},ancestors)",deptId));
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            baseMapper.updateDeptChildren(children);
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(String deptId) {
        return baseMapper.deleteById(deptId);
    }

    @Override
    public void checkDeptDataScope(String deptId) {

    }

    @Override
    public SysDept selectDeptByName(String deptName) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysDept>().eq(SysDept::getDeptName,deptName));
    }

    @Override
    public List<SysDept> selectDeptTag(SysDept dept) {
        List<SysDept> sysDepts =  baseMapper.selectDeptList(dept);
        return sysDepts;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<SysDept>();
		for (SysDept n : list) {
			if (Validator.isNotNull(n.getParentId()) && n.getParentId().equals(t.getDeptId())) {
				tlist.add(n);
			}
		}
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 通过部门ID查询部门名称
     *
     * @param deptIds 部门ID串逗号分隔
     * @return 部门名称串逗号分隔
     */
    @Override
    public String selectDeptNameByIds(String deptIds) {
        List<String> list = new ArrayList<>();
        for (String id : StringUtils.splitTo(deptIds, Convert::toStr)) {
            SysDept dept = selectDeptById(id);
            if (ObjectUtil.isNotNull(dept)) {
                list.add(dept.getDeptName());
            }
        }
        return String.join(StringUtils.SEPARATOR, list);
    }

    @Override
    public String selectDeptIdByNames(String deptNames) {
        List<String> list = new ArrayList<>();
        for (String id : StringUtils.splitTo(deptNames, Convert::toStr)) {
            SysDept dept = selectDeptByName(id);
            if (ObjectUtil.isNotNull(dept)) {
                list.add(dept.getDeptId());
            }
        }
        return String.join(StringUtils.SEPARATOR, list);
    }
}
