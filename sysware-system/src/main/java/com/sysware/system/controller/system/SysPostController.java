package com.sysware.web.controller.system;

import java.util.List;

import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysDictType;
import org.springframework.beans.factory.annotation.Autowired;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sysware.common.annotation.Log;
import com.sysware.common.constant.UserConstants;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.utils.SecurityUtils;
import com.sysware.common.utils.poi.ExcelUtil;
import com.sysware.system.domain.SysPost;
import com.sysware.system.service.ISysPostService;

import javax.servlet.http.HttpServletResponse;

/**
 * 岗位信息操作处理
 * 
 * @author
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController
{
    @Autowired
    private ISysPostService postService;

    /**
     * 获取岗位列表
     */
    @SaCheckPermission("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPost post, PageQuery pageQuery)
    {
        return postService.selectPagePostList(post,pageQuery);
    }
    
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("@ss.hasPermi('system:post:export')")
    @PostMapping("/export")
    public void export(SysPost post, HttpServletResponse response)
    {
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil.exportExcel(list, "岗位数据", SysPost.class, response);
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @SaCheckPermission("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public R<SysPost> getInfo(@PathVariable String postId)
    {
        return R.ok(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    @SaCheckPermission("@ss.hasPermi('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysPost post)
    {
        if (postService.checkPostNameUnique(post))
        {
            return R.warn("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (postService.checkPostCodeUnique(post))
        {
            return R.warn("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreateBy(SecurityUtils.getUsername());
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @SaCheckPermission("@ss.hasPermi('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysPost post)
    {
        if (postService.checkPostNameUnique(post))
        {
            return R.ok("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (postService.checkPostCodeUnique(post))
        {
            return R.ok("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(postService.updatePost(post));
    }

    /**
     * 删除岗位
     */
    @SaCheckPermission("@ss.hasPermi('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public R<Void> remove(@PathVariable String[] postIds)
    {
        return toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public R<List<SysPost>> optionselect()
    {
        List<SysPost> posts = postService.selectPostAll();
        return R.ok(posts);
    }
}
