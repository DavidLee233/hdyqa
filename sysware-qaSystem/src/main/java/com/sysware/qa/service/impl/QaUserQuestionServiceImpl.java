package com.sysware.qa.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysUser;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.qa.domain.QaQuestionType;
import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaQuestionTypeBo;
import com.sysware.qa.domain.vo.QaQuestionTypeVo;
import com.sysware.qa.mapper.QaQuestionTypeMapper;
import com.sysware.qa.service.IQaQuestionTypeService;
import com.sysware.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.sysware.system.service.ISysTableFieldService;
import com.sysware.qa.domain.bo.QaUserQuestionBo;
import com.sysware.qa.domain.vo.QaUserQuestionVo;
import com.sysware.qa.domain.QaUserQuestion;
import com.sysware.qa.mapper.QaUserQuestionMapper;
import com.sysware.qa.service.IQaUserQuestionService;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.SyswareUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 问题回答类型角色关联Service业务层处理
 *
 * @author aa
 * @date 2025-08-19
 */
@RequiredArgsConstructor
@Service
public class QaUserQuestionServiceImpl implements IQaUserQuestionService {

    private final QaUserQuestionMapper baseMapper;
    private final QaQuestionTypeMapper questionTypeMapper;

    /**
     * 查询问题回答类型角色关联
     */
    @Override
    public QaUserQuestionVo queryById(String id){
        return baseMapper.selectNewVoById(id);
    }

    /**
     * 查询问题回答类型角色关联列表
     */
    @Override
    public TableDataInfo queryPageList(QaUserQuestionBo bo, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<QaUserQuestion> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<QaUserQuestionVo> result = baseMapper.selectNewVoPage(page, buildQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }

    public static QueryWrapper buildQueryWrapper(QaUserQuestionBo bo, PageQuery pageQuery) {
        QueryWrapper query = Wrappers.query();
        if(bo.getSearchValue() != null){
            // 加入模糊查询和根据问题排序正序排序逻辑
            query.like("type",bo.getSearchValue());
            query.or();
            query.like("user_name",bo.getSearchValue());
            query.or();
            query.like("login_name",bo.getSearchValue());
        }
        // 再进行正序排序搜索
        query.orderBy(true, true, "sequence");
        return query;
    }

    /**
     * 新增问题回答类型角色关联
     */
    @Override
    public R<Void> insertByBo(QaUserQuestionBo bo) {
        QaUserQuestion add = BeanUtil.toBean(bo, QaUserQuestion.class);
        validEntityBeforeSave(add);
        // 同一个问题类型只能由一个人管理（判断占用位是否为1）
        QaQuestionType type = questionTypeMapper.selectById(add.getTypeId());
        if(type.getOccupied() == 1){
            return R.fail("选择问题类型已被占用，请选择其他问题类型再试");
        }
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            // 增加问题角色映射后应将问题占用位置1然后更新数据库
            type.setOccupied(1L);
            questionTypeMapper.updateById(type);
        }
        return flag ? R.ok() : R.fail("新增失败，请稍后再试");
    }

    /**
     * 修改问题回答类型角色关联
     */
    @Override
    public R<Void> updateByBo(QaUserQuestionBo bo) {
        // 先查询数据库中的原始记录
        QaUserQuestion update = baseMapper.selectById(bo.getId());
        if (update == null) {
            return R.fail("记录不存在");
        }
        // 更新前判断和置位操作
        operateBeforeUpdate(bo, update);

        // 将bo中非null的属性复制到update中
        BeanUtil.copyProperties(bo, update, CopyOptions.create().setIgnoreNullValue(true));

        validEntityBeforeSave(update);
        boolean flag = baseMapper.updateById(update) > 0;
        return flag ? R.ok() : R.fail("修改失败，请稍后再试");
    }

    /**
     * 更新前判断和置位操作
     */
    private void operateBeforeUpdate(QaUserQuestionBo bo, QaUserQuestion update){
        // 如果更新前后的typeId不一样，则需要进行判断改后的问题类型是否被占用
        // 1.若占用，则返回不可修改 2.若未被占用，则修改前者和后者的占有位
        QaQuestionType beforeType = questionTypeMapper.selectById(update.getTypeId());
        QaQuestionType afterType = questionTypeMapper.selectById(bo.getTypeId());
        if(!beforeType.getType().equals(afterType.getType())){
            // 其实可以不用这一步，因前端已限制显示被占有的问题类型，仅确保逻辑安全
            if(afterType.getOccupied() == 1){
                R.fail("选择问题类型已被占用，请选择其他问题类型再试");
            }
            // 将之前占有的腾出来置0并更新数据库
            beforeType.setOccupied(0L);
            questionTypeMapper.updateById(beforeType);
            // 将之后占有的锁定置1并更新数据库
            afterType.setOccupied(1L);
            questionTypeMapper.updateById(afterType);
        }
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(QaUserQuestion entity){
        // 做一些数据校验,如唯一约束
        if(entity == null) throw new IllegalArgumentException("实体类不能为空");
        // 做唯一约束数据校验
        LambdaQueryWrapper<QaUserQuestion> lqw = Wrappers.<QaUserQuestion>lambdaQuery()
                .eq(QaUserQuestion::getUserId, entity.getUserId())
                .eq(QaUserQuestion::getTypeId, entity.getTypeId())
                // 如果是更新操作，需要排除当前记录本身进行校验
                .ne(entity.getId() != null, QaUserQuestion::getId, entity.getId());
        if (baseMapper.selectCount(lqw) > 0) throw new DuplicateKeyException("问题角色映射已存在");
        if (baseMapper.selectCount(lqw) > 0) throw new DuplicateKeyException("问题角色映射已存在");
    }

    /**
     * 批量删除问题回答类型角色关联
     */
    @Override
    public R<Void> deleteWithValidByIds(Collection<String> ids) {
        // 先检查从前端传过来的list
        if (CollUtil.isEmpty(ids)){
            return R.fail("问题角色映射为空，请重新选择");
        }
        List<QaUserQuestion> updates = baseMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(updates)){
            return R.fail("未找到对应问题角色映射");
        }
        if(!(baseMapper.deleteBatchIds(ids) > 0)){
            return R.fail("问题角色映射删除失败，请稍后再试");
        }
        for (QaUserQuestion update : updates) {
            QaQuestionType type = questionTypeMapper.selectById(update.getTypeId());
            // 删除问题角色映射后应将问题占用位置0然后更新数据库
            type.setOccupied(0L);
            questionTypeMapper.updateById(type);
        }

        return R.ok();
    }
}
