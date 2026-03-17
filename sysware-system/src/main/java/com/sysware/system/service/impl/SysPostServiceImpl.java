package com.sysware.system.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sysware.common.constant.UserConstants;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.exception.ServiceException;
import com.sysware.system.domain.SysPost;
import com.sysware.system.domain.SysUserPost;
import com.sysware.system.mapper.SysPostMapper;
import com.sysware.system.mapper.SysUserPostMapper;
import com.sysware.system.service.ISysPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 岗位信息 服务层处理
 *
 * @author
 */
@RequiredArgsConstructor
@Service
public class SysPostServiceImpl implements ISysPostService {

    private final SysUserPostMapper userPostMapper;
    private final SysPostMapper baseMapper;

    @Override
    public TableDataInfo<Map> selectPagePostList(SysPost post, PageQuery pageQuery) {
        LambdaQueryWrapper<SysPost> lqw = new LambdaQueryWrapper<SysPost>()
                .like(StrUtil.isNotBlank(post.getPostCode()), SysPost::getPostCode, post.getPostCode())
                .eq(StrUtil.isNotBlank(post.getStatus()), SysPost::getStatus, post.getStatus())
                .like(StrUtil.isNotBlank(post.getPostName()), SysPost::getPostName, post.getPostName());
        IPage<Map> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post) {
        return baseMapper.selectList(new LambdaQueryWrapper<SysPost>()
                .like(StrUtil.isNotBlank(post.getPostCode()), SysPost::getPostCode, post.getPostCode())
                .eq(StrUtil.isNotBlank(post.getStatus()), SysPost::getStatus, post.getStatus())
                .like(StrUtil.isNotBlank(post.getPostName()), SysPost::getPostName, post.getPostName()));
    }

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll() {
        return baseMapper.selectList();
    }

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(String postId) {
        return baseMapper.selectById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Integer> selectPostListByUserId(String userId) {
        return baseMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostNameUnique(SysPost post) {
        Long postId = Validator.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = baseMapper.selectOne(new LambdaQueryWrapper<SysPost>()
                .eq(SysPost::getPostName, post.getPostName()).last("limit 1"));
        if (Validator.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return true;
        }
        return false;
    }

    /**
     * 校验岗位编码是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostCodeUnique(SysPost post) {
        Long postId = Validator.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = baseMapper.selectOne(new LambdaQueryWrapper<SysPost>()
                .eq(SysPost::getPostCode, post.getPostCode()).last("limit 1"));
        if (Validator.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return true;
        }
        return false;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int countUserPostById(String postId) {
        Long count = userPostMapper.selectCount(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getPostId, postId));
        return count.intValue();

    }

    /**
     * 删除岗位信息
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int deletePostById(String postId) {
        return baseMapper.deleteById(postId);
    }

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public int deletePostByIds(String[] postIds) {
        for (String postId : postIds) {
            SysPost post = selectPostById(postId);
            if (countUserPostById(postId) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", post.getPostName()));
            }
        }
        return baseMapper.deleteBatchIds(Arrays.asList(postIds));
    }

    /**
     * 新增保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPost post) {

        return baseMapper.insert(post);
    }

    /**
     * 修改保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPost post) {
        return baseMapper.updateById(post);
    }
}
