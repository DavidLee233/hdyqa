package com.sysware.qa.actions;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.QaSysAttach;
import com.sysware.qa.domain.bo.QaSysAttachBo;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.domain.vo.QaSysVo;
import com.sysware.qa.mapper.QaSysAttachMapper;
import com.sysware.qa.mapper.QaSysMapper;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.model.LoginUser;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class commonAction {
    /**
     * @author lxd
     * @description: 根据具体模式查询问题回答附件
     * @param recordId
     * @param mode
     * @param qaSysVo
     * @param attachMapper
     * @return void
     * @date 2025/7/20
     **/
    public static void setAttachByMode(String recordId, String mode, QaSysVo qaSysVo, QaSysAttachMapper attachMapper) {
        // 将查到的附件交给qaSysVo
        // 如果是管理员模式，则直接查询当前recordId对应的全部附件显示到表单中
        if("admin".equals(mode)){
            qaSysVo.setQuestionAttachList(attachMapper.selectByRecordIdAndType(recordId, "question"));
            qaSysVo.setAnswerAttachList(attachMapper.selectByRecordIdAndType(recordId, "answer"));
        }else if("question".equals(mode)){
            qaSysVo.setQuestionAttachList(attachMapper.selectByRecordIdAndType(recordId, "question"));
        }else if("answer".equals(mode)){
            qaSysVo.setAnswerAttachList(attachMapper.selectByRecordIdAndType(recordId, "answer"));
        }
    }

    /**
     * @author lxd
     * @description: 根据具体模式查询问题回答
     * @return com.sysware.archives.vo.QaSysVo
     * @date 2025/7/20
     **/
    public static QaSysVo getLoginUser() {
        QaSysVo qaSysVo = new QaSysVo();
        LoginUser user = LoginHelper.getLoginUser();
        qaSysVo.setCreateBy(user.getUsername());
        qaSysVo.setCreateId(user.getLoginName());
        qaSysVo.setUserId(user.getUserId());
        qaSysVo.setDepartment(user.getDeptName());
        qaSysVo.setOpenNotify(user.getOpenNotify());
        return qaSysVo;
    }
    /**
     * @author lxd
     * @description: 进行模糊（可选）和排序字段搜索（可选）查询
     * @param bo
     * @return LambdaQueryWrapper<com.sysware.archives.domain.QaSys>
     * @date 2025/7/19
     **/
    public static LambdaQueryWrapper<QaSys> buildQueryWrapper(QaSysBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<QaSys> lqw = Wrappers.lambdaQuery();
        // 同管理员不同的是，普通角色只能查看已采纳状态的问题回答组（如果用户点击采纳的数量不多的话就加一个已处理状态数据）
        lqw.and(wrapper -> wrapper
                .eq(QaSys::getStatus, "已采纳")
                .or()
                .eq(QaSys::getStatus, "已处理")
        );
        // 进行人员查询->模糊查询->排序查询的三级搜索
        queryWithCondition(bo, pageQuery, lqw);
        return lqw;
    }


    /**
     * @author lxd
     * @description: 条件查询语句
     * @param bo
     * @param pageQuery
     * @param lqw
     * @return LambdaQueryWrapper<com.sysware.archives.domain.QaSys>
     * @date 2025/9/2
     **/
    public static void queryWithCondition(QaSysBo bo, PageQuery pageQuery, LambdaQueryWrapper<QaSys> lqw) {
        // 首先按照人员进行查询
        if(bo.getSearchUser() != null && StrUtil.isNotBlank(bo.getSearchUser())){
            if("self".equals(bo.getSearchUser())){
                selfLQWProcessor(lqw, bo.getPageType());
            }
        }
        // 添加模糊查询逻辑
        if (pageQuery != null && StrUtil.isNotBlank(bo.getSearchValue())) {
            lqw.and(wrapper -> {
                wrapper.like(QaSys::getCreateBy, bo.getSearchValue())
                        .or()
                        .like(QaSys::getCreateId, bo.getSearchValue())
                        .or()
                        .like(QaSys::getTitle, bo.getSearchValue())
                        .or()
                        .like(QaSys::getQuestionContent, bo.getSearchValue())
                        .or()
                        .like(QaSys::getAnswerContent, bo.getSearchValue())
                        .or()
                        .like(QaSys::getStatus, bo.getSearchValue())
                        .or()
                        .like(QaSys::getType, bo.getSearchValue())
                        .or()
                        .like(QaSys::getSeverity, bo.getSearchValue())
                        .or()
                        .like(QaSys::getCanSolve, bo.getSearchValue())
                        .or()
                        .like(QaSys::getUpdateId, bo.getSearchValue())
                        .or()
                        .like(QaSys::getUpdateBy, bo.getSearchValue());
            });
        }

        // 添加排序查询逻辑
        if (pageQuery != null && StrUtil.isNotBlank(pageQuery.getOrderByColumn()) && isValidSortField(pageQuery.getOrderByColumn())) {
            String sortField = pageQuery.getOrderByColumn();
            boolean isAsc = "asc".equalsIgnoreCase(pageQuery.getIsAsc());

            // 将前端字段名映射到实体属性
            switch (sortField) {
                case "createTime":
                    lqw.orderBy(true, isAsc, QaSys::getCreateTime);
                    break;
                case "solveTime":
                    lqw.orderBy(true, isAsc, QaSys::getSolveTime);
                    break;
                case "viewCount":
                    lqw.orderBy(true, isAsc, QaSys::getVisits);
                    break;
                case "type":
                    lqw.orderBy(true, isAsc, QaSys::getType);
                    break;
                // 添加其他可排序字段...
                default:
                    // 默认按创建时间降序
                    lqw.orderBy(true, false, QaSys::getCreateTime);
                    break;
            }
        }
    }

    /**
     * @author lxd
     * @description: 不同页面的条件查询
     * @param lqw
     * @param mode
     * @return LambdaQueryWrapper<com.sysware.archives.domain.QaSys>
     * @date 2025/9/2
     **/
    private static void selfLQWProcessor(LambdaQueryWrapper<QaSys> lqw, String mode) {
        LoginUser user = LoginHelper.getLoginUser();

        String username = user.getUsername();
        String loginName = user.getLoginName();
        boolean hasUsername = StringUtils.isNotBlank(username);
        boolean hasLoginName = StringUtils.isNotBlank(loginName);

        switch (mode) {
            case "admin":  // 筛选条件：姓名+工号 满足管理者
                lqw.and(wrapper -> wrapper
                        .and(w1 -> w1
                                .eq(hasUsername, QaSys::getCreateBy, username)
                                .eq(hasLoginName, QaSys::getCreateId, loginName))
                        .or(w2 -> w2
                                .eq(hasUsername, QaSys::getUpdateBy, username)
                                .eq(hasLoginName, QaSys::getUpdateId, loginName))
                );
                break;
            case "question":  // 筛选条件：姓名+工号 满足问题创建者
                lqw.and(wrapper -> wrapper
                        .eq(hasUsername, QaSys::getCreateBy, username)
                        .eq(hasLoginName, QaSys::getCreateId, loginName));
                break;
            case "answer":   // 筛选条件：姓名+工号 满足问题回答者
                lqw.and(wrapper -> wrapper
                        .eq(hasUsername, QaSys::getUpdateBy, username)
                        .eq(hasLoginName, QaSys::getUpdateId, loginName));
                break;
            default:
                // 默认不需筛选
                break;
        }
    }

    /**
     * @author lxd
     * @description: 排序搜索查询前字段校验
     * @param field
     * @return boolean
     * @date 2025/7/20
     **/
    public static boolean isValidSortField(String field) {
        if (StrUtil.isBlank(field)) {
            return false;
        }
        // 定义允许排序的字段
        Set<String> allowedFields = new HashSet<>(Arrays.asList(
                "createTime", "solveTime", "viewCount", "type"
        ));
        return allowedFields.contains(field);
    }

    /**
     * @author lxd
     * @description: 保存前的数据校验
     * @param entity
     * @param baseMapper
     * @return void
     * @date 2025/7/19
     **/
    public static void validEntityBeforeSave(QaSys entity, QaSysMapper baseMapper){
        if(entity == null) throw new IllegalArgumentException("实体类不能为空");
        // 做唯一约束数据校验
        LambdaQueryWrapper<QaSys> lqw = Wrappers.<QaSys>lambdaQuery()
                .eq(QaSys::getQuestionContent, entity.getQuestionContent())
                .eq(QaSys::getCreateBy, entity.getCreateBy())
                .eq(QaSys::getCreateId, entity.getCreateId())
                // 如果是更新操作，需要排除当前记录本身进行校验
                .ne(entity.getRecordId() != null, QaSys::getRecordId, entity.getRecordId());
        if (baseMapper.selectCount(lqw) > 0) throw new DuplicateKeyException("问题已存在");
        if (baseMapper.selectCount(lqw) > 0) throw new DuplicateKeyException("问题已存在");
    }
    /**
     * @author lxd
     * @description: 更新附件逻辑：如附件已经在数据库中
     * @param attachBoList
     * @param qaSys
     * @param attachMapper
     * @return void
     * @date 2025/7/25
     **/
    public static void updateAttach(List<QaSysAttachBo> attachBoList, QaSys qaSys, QaSysAttachMapper attachMapper) {
        if (attachBoList != null && !attachBoList.isEmpty()){
            for (QaSysAttachBo attachBo : attachBoList) {
                if (attachBo.getRecordId() != null) {continue;}
                QaSysAttach attachAdd = BeanUtil.toBean(attachBo, QaSysAttach.class);
                attachAdd.setCreateBy(LoginHelper.getUsername());
                attachAdd.setCreateTime(LocalDateTime.now());
                attachAdd.setRecordId(qaSys.getRecordId());
                if (attachMapper.insert(attachAdd) > 0) {attachBo.setId(attachAdd.getId());}
            }
        }
    }
}
