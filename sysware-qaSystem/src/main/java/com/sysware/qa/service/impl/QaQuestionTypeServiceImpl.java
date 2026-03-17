package com.sysware.qa.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.entity.SysUser;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.QaUserQuestion;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.domain.vo.QaSysVo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.sysware.system.service.ISysTableFieldService;
import com.sysware.qa.domain.bo.QaQuestionTypeBo;
import com.sysware.qa.domain.vo.QaQuestionTypeVo;
import com.sysware.qa.domain.QaQuestionType;
import com.sysware.qa.mapper.QaQuestionTypeMapper;
import com.sysware.qa.service.IQaQuestionTypeService;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.SyswareUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.*;

import static com.sysware.qa.actions.adminAction.buildAdminQueryWrapper;

/**
 * 问题类型表Service业务层处理
 *
 * @author aa
 * @date 2025-08-19
 */
@RequiredArgsConstructor
@Service
public class QaQuestionTypeServiceImpl implements IQaQuestionTypeService {

    private final QaQuestionTypeMapper baseMapper;

    /**
     * 查询问题类型表
     */
    @Override
    public QaQuestionTypeVo queryById(String questionId){
        return baseMapper.selectVoById(questionId);
    }

    /**
     * 查询问题类型表列表
     */
    @Override
    public TableDataInfo queryPageList(QaQuestionTypeBo bo, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<QaQuestionType> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<QaQuestionTypeVo> result = baseMapper.selectVoPage(page, buildQueryWrapper(bo, pageQuery));
        return TableDataInfo.build(result);
    }

    /**
     * @author lxd
     * @description: 根据序号正序排列查询
     * @param bo
     * @param pageQuery
     * @return LambdaQueryWrapper<com.sysware.qa.domain.QaQuestionType>
     * @date 2025/7/19
     **/
    public static LambdaQueryWrapper<QaQuestionType> buildQueryWrapper(QaQuestionTypeBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<QaQuestionType> lqw = Wrappers.lambdaQuery();
        // 首先进行模糊搜索
        if (pageQuery != null && StrUtil.isNotBlank(bo.getSearchValue())) {
            lqw.and(wrapper -> {
                wrapper.like(QaQuestionType::getType, bo.getSearchValue());
            });
        }
        // 再进行正序排序搜索
        lqw.orderBy(true, true, QaQuestionType::getSequence);
        return lqw;
    }


    /**
     * 新增问题类型表
     */
    @Override
    public R<Void> insertByBo(QaQuestionTypeBo bo) {
        QaQuestionType add = BeanUtil.toBean(bo, QaQuestionType.class);
        add.setCreateTime(LocalDateTime.now());
        add.setCreateBy(LoginHelper.getUserId());
        add.setUpdateBy(LoginHelper.getUserId());
        add.setUpdateTime(LocalDateTime.now());
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTypeId(add.getTypeId());
        }
        return flag ? R.ok() : R.fail("新增失败，请稍后再试");
    }

    /**
     * 修改问题类型表
     */
    @Override
    public R<Void> updateByBo(QaQuestionTypeBo bo) {
        // 先查询数据库中的原始记录
        QaQuestionType update = baseMapper.selectById(bo.getTypeId());
        if (update == null) {
            return R.fail("记录不存在");
        }
        // 将bo中非null的属性复制到update中
        BeanUtil.copyProperties(bo, update, CopyOptions.create().setIgnoreNullValue(true));
        update.setUpdateBy(LoginHelper.getUserId());
        update.setUpdateTime(LocalDateTime.now());

        validEntityBeforeSave(update);
        boolean flag = baseMapper.updateById(update) > 0;
        return flag ? R.ok() : R.fail("修改失败，请稍后再试");
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(QaQuestionType entity){
        // 做数据校验,如唯一约束
        if(entity == null) throw new IllegalArgumentException("实体类不能为空");
        // 做唯一约束数据校验
        LambdaQueryWrapper<QaQuestionType> lqw = Wrappers.<QaQuestionType>lambdaQuery()
                .eq(QaQuestionType::getType, entity.getType())
                // 如果是更新操作，需要排除当前记录本身进行校验
                .ne(entity.getTypeId() != null, QaQuestionType::getTypeId, entity.getTypeId());
        if (baseMapper.selectCount(lqw) > 0) throw new DuplicateKeyException("问题已存在");
        if (baseMapper.selectCount(lqw) > 0) throw new DuplicateKeyException("问题已存在");
    }

    /**
     * 批量删除问题类型表
     */
    @Override
    public R<Void> deleteWithValidByIds(Collection<String> ids) {
        // 先检查从前端传过来的list
        if (CollUtil.isEmpty(ids)){
            return R.fail("问题角色映射为空，请重新选择");
        }
        List<QaQuestionType> updates = baseMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(updates)){
            return R.fail("未找到对应问题角色映射");
        }
        for (QaQuestionType update : updates) {
            // 无法删除处于占用状态的问题类型
            if (update.getOccupied() == 1) {
                return R.fail("问题类型处于已占用状态，无法删除");
            }
        }
        if(!(baseMapper.deleteBatchIds(ids) > 0)){
            return R.fail("问题角色映射删除失败，请稍后再试");
        }
        return R.ok();
    }

    /**
     * 获取所有的问题类型信息
     */
    @Override
    public List<QaQuestionType> selectTypeList() {
        List<QaQuestionType> list = baseMapper.selectAllType();
        return list;
    }

    /**
     * 获取占有位为0的问题类型信息
     */
    @Override
    public List<QaQuestionType> selectPartTypeList() {
        List<QaQuestionType> list = baseMapper.selectPartType();
        return list;
    }

    /**
     * 获取问题类型顺序
     */
    @Override
    public R<Long> getSeqenceNum() {
        // 获取排序索引
        LambdaQueryWrapper<QaQuestionType> lqw = new LambdaQueryWrapper<>();
        Long count = baseMapper.selectCount(lqw);
        return R.ok(count + 1);
    }
}
