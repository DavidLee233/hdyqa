package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.sysware.common.core.domain.entity.SysMenu;
import com.sysware.common.helper.LoginHelper;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sysware.common.utils.SyswareUtil;
import com.sysware.system.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sysware.system.service.ISysTableFieldService;
import com.sysware.system.domain.bo.SysPageBtnBo;
import com.sysware.system.domain.vo.SysPageBtnVo;
import com.sysware.system.domain.SysPageBtn;
import com.sysware.system.mapper.SysPageBtnMapper;
import com.sysware.system.service.ISysPageBtnService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 页面按钮Service业务层处理
 *
 * @author aa
 * @date 2023-05-22
 */
@RequiredArgsConstructor
@Service
public class SysPageBtnServiceImpl implements ISysPageBtnService {

    private final SysPageBtnMapper baseMapper;
    private final ISysTableFieldService tableFieldService;
    private final ISysMenuService menuService;

    /**
     * 查询页面按钮
     */
    @Override
    public SysPageBtnVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询页面按钮列表
     */
    @Override
    public TableDataInfo queryPageList(SysPageBtnBo bo, PageQuery pageQuery) {

        return TableDataInfo.build(tableFieldService.getResult(BeanUtil.beanToMap(bo), pageQuery, baseMapper, SyswareUtil.getSearchFieldMap(bo.getSearchField())));

    }

    /**
     * 查询页面按钮列表
     */
    @Override
    public List<SysPageBtnVo> queryList(SysPageBtnBo bo) {
        LambdaQueryWrapper<SysPageBtn> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysPageBtn> buildQueryWrapper(SysPageBtnBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysPageBtn> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getBtnId()), SysPageBtn::getBtnId, bo.getBtnId());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), SysPageBtn::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getIcon()), SysPageBtn::getIcon, bo.getIcon());
        lqw.eq(StringUtils.isNotBlank(bo.getSize()), SysPageBtn::getSize, bo.getSize());
        lqw.eq(StringUtils.isNotBlank(bo.getLocation()), SysPageBtn::getLocation, bo.getLocation());
        lqw.eq(StringUtils.isNotBlank(bo.getText()), SysPageBtn::getText, bo.getText());
        lqw.eq(StringUtils.isNotBlank(bo.getShowText()), SysPageBtn::getShowText, bo.getShowText());
        lqw.eq(StringUtils.isNotBlank(bo.getPermission()), SysPageBtn::getPermission, bo.getPermission());
        lqw.eq(StringUtils.isNotBlank(bo.getDisabledType()), SysPageBtn::getDisabledType, bo.getDisabledType());
        lqw.eq(StringUtils.isNotBlank(bo.getPagePath()), SysPageBtn::getPagePath, bo.getPagePath());
        lqw.like(StringUtils.isNotBlank(bo.getFunctionName()), SysPageBtn::getFunctionName, bo.getFunctionName());
        return lqw;
    }

    /**
     * 新增页面按钮
     */
    @Override
    public Boolean insertByBo(SysPageBtnBo bo) {
        SysPageBtn add = BeanUtil.toBean(bo, SysPageBtn.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改页面按钮
     */
    @Override
    public Boolean updateByBo(SysPageBtnBo bo) {
        SysPageBtn update = BeanUtil.toBean(bo, SysPageBtn.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysPageBtn entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除页面按钮
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 初始化页面按钮
     * @return
     */
    @Override
    public Boolean initBtn() {

        /*List<SysMenu> menuList = menuService.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(StrUtil.isNotBlank(menu.getStatus()),SysMenu::getStatus,menu.getStatus())
                .ne(SysMenu::getMenuType,"F"));*/
        SysMenu menu = new SysMenu();
        List<SysMenu> menus = menuService.selectPageList(menu, LoginHelper.getUserId());

        List<SysPageBtn> addBtnList = new ArrayList<>();


        List<SysPageBtn> btnList = baseMapper.selectList();

        Map<String, String> btnMap = btnList.stream().distinct().collect(Collectors.toMap(SysPageBtn::getPagePath, SysPageBtn::getBtnId, (v1, v2) -> v2));

        menus.stream().forEach(m ->{

            if(btnMap.get("/"+m.getComponent()) == null && StringUtils.isNotEmpty(m.getComponent()) && "C".equals(m.getMenuType())){
                String path = m.getComponent().substring(0,m.getComponent().lastIndexOf("/"));
                String formId = path + "/add";
                addBtnList.add(initAddBtn("/"+m.getComponent(),path.replace("/",":")+":add",formId));
                addBtnList.add(initEditBtn("/"+m.getComponent(),path.replace("/",":")+":edit",formId));
                addBtnList.add(initDeleteBtn("/"+m.getComponent(),path.replace("/",":")+":delete"));
            }
        });
        baseMapper.insertBatch(addBtnList);

        return true;
    }



    /**
     * 初始化添加按钮的属性。
     *
     * @param pagePath 页面路径，用于设置按钮的禁用状态。
     * @param permission 权限标识，用于设置按钮的显示权限。
     * @param formId 表单ID，用于关联按钮和表单。
     * @return 初始化后的按钮对象。
     */
    private SysPageBtn initAddBtn(String pagePath,String permission,String formId) {
        // 创建一个新的按钮对象
        SysPageBtn add = new SysPageBtn();
        add.setId("");
        // 设置按钮的唯一标识
        add.setBtnId("add");
        // 设置按钮的尺寸
        add.setSize("mini");
        // 设置按钮显示的文本
        add.setText("添加");
        // 设置按钮的类型
        add.setType("primary");
        // 设置按钮的图标
        add.setIcon("el-icon-plus");
        // 设置按钮在页面中的位置
        add.setLocation("header");
        // 设置按钮是否显示文本
        add.setShowText("0");
        // 设置按钮的禁用状态的判断函数
        add.setDisabledType("notCheck");
        // 根据页面路径设置按钮的禁用状态
        add.setPagePath(pagePath);
        // 设置按钮的权限标识
        add.setPermission(permission);
        // 设置按钮的点击事件处理函数名
        add.setFunctionName("handleAdd");
        // 设置按钮的标题
        add.setTitle("添加");
        // 设置按钮点击后的打开方式
        add.setOpenType("dialog");
        // 设置按钮关联的表单ID
        add.setFormId(formId);
        // 设置按钮提交表单的事件名
        add.setSubmitEventName("submitForm");
        // 设置按钮的排序号
        add.setSort(1);
        // 返回初始化后的按钮对象
        return add;
    }

    /**
     * 初始化编辑按钮的属性。
     *
     * @param pagePath 页面路径
     * @param permission 权限标识，用于设置按钮的显示权限。
     * @param formId 表单ID，用于关联按钮和表单。
     * @return 初始化后的按钮对象。
     */
    private SysPageBtn initEditBtn(String pagePath,String permission,String formId) {
        // 创建一个新的按钮对象
        SysPageBtn edit = new SysPageBtn();
        // 设置按钮的唯一标识
        edit.setBtnId("edit");
        // 设置按钮的尺寸
        edit.setSize("mini");
        // 设置按钮显示的文本
        edit.setText("编辑");
        // 设置按钮的类型
        edit.setType("primary");
        // 设置按钮的图标
        edit.setIcon("el-icon-edit");
        // 设置按钮在页面中的位置
        edit.setLocation("header");
        // 设置按钮是否显示文本
        edit.setShowText("0");
        // 设置按钮的禁用状态的判断函数
        edit.setDisabledType("single");
        // 根据页面路径设置按钮的禁用状态
        edit.setPagePath(pagePath);
        // 设置按钮的权限标识
        edit.setPermission(permission);
        // 设置按钮的点击事件处理函数名
        edit.setFunctionName("handleUpdate");
        // 设置按钮的标题
        edit.setTitle("编辑");
        // 设置按钮点击后的打开方式
        edit.setOpenType("dialog");
        // 设置按钮关联的表单ID
        edit.setFormId(formId);
        // 设置按钮提交表单的事件名
        edit.setSubmitEventName("submitForm");
        // 设置按钮的排序号
        edit.setSort(2);
        // 返回初始化后的按钮对象
        return edit;
    }
    /**
     * 初始化删除按钮的属性。
     *
     * @param pagePath 页面路径，用于特殊处理或逻辑判断。
     * @param permission 删除操作需要的权限标识。
     * @return 初始化配置后的删除按钮对象。
     */
    private SysPageBtn initDeleteBtn(String pagePath, String permission) {
        SysPageBtn delete = new SysPageBtn();
        delete.setBtnId("delete");
        delete.setSize("mini");
        delete.setText("删除");
        delete.setType("danger");
        delete.setIcon("el-icon-delete");
        delete.setLocation("header");
        delete.setShowText("0");
        delete.setDisabledType("multiple");
        delete.setPermission(permission);
        delete.setFunctionName("handleDelete");
        delete.setTitle("删除");
        delete.setOpenType("confirm");
        delete.setPagePath(pagePath);
        delete.setMsg("确认删除选中项吗？");
        delete.setSort(3);
        delete.setCancelText("取消");
        delete.setConfirmText("删除");
        delete.setPopConfirmIcon("el-icon-info");
        delete.setColor("#F56C6C");
        return delete;
    }
}
