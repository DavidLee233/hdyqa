package com.sysware.system.mapper;

import com.sysware.common.core.mapper.BaseMapperPlus;
import com.sysware.system.domain.SysPost;

import java.util.List;
import java.util.Map;

/**
 * 岗位信息 数据层
 *
 * @author
 */
public interface SysPostMapper extends BaseMapperPlus<SysPostMapper,SysPost, Map> {

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    public List<Integer> selectPostListByUserId(String userId);

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    public List<SysPost> selectPostsByUserName(String userName);

}
