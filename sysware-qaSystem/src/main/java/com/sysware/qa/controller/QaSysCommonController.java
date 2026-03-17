package com.sysware.qa.controller;

import com.sysware.qa.service.IQaSysCommonService;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.sysware.common.annotation.Log;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.enums.BusinessType;
import com.sysware.qa.domain.vo.QaSysVo;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.common.core.page.TableDataInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/qaSystem/qaCommon")
public class QaSysCommonController {

    private final IQaSysCommonService iQaSysCommonService;

    /**
     * 查询用户信息（通用方法）
     */
    @GetMapping("/user")
    public R<QaSysVo> getUser() {
        return iQaSysCommonService.getUser();
    }
    /**
     * 获取问题回答详细信息（通用方法）
     *
     * @param recordId 主键
     */
    @GetMapping("/{recordId}")
    public R<QaSysVo> getInfo(@NotNull(message = "主键不能为空")
                              @PathVariable String recordId, @RequestParam(required = false, defaultValue = "admin") String mode) {
        return R.ok(iQaSysCommonService.queryById(recordId, mode));
    }
    /**
     * 查询问题回答列表（右侧列表）
     */
    @GetMapping("/listQa")
    public TableDataInfo list(QaSysBo bo, PageQuery pageQuery) {
        return iQaSysCommonService.queryPageList(bo, pageQuery);
    }
    /**
     * 刷新缓存（通用方法）
     *
     * @param
     */
    @Log(title = "删除缓存", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R<Void> refreshCache(HttpServletResponse response) {
        iQaSysCommonService.refreshCache();
        return R.ok();
    }

    /**
     * 更新访问量（通用方法）
     */
    @PostMapping("/updateVisits/{recordId}")
    public R<Void> updateVisits(@PathVariable String recordId) {
        return iQaSysCommonService.updateVisits(recordId);
    }

    /**
     * 获取用户下拉框信息（全部）
     */
    @GetMapping("/userOption")
    public R<List<Map<String, Object>>> getUserOption() {
        return iQaSysCommonService.getUserOption();
    }

    /**
     * 获取问题类型下拉框信息（全部）
     */
    @GetMapping("/typeOption")
    public R<List<Map<String, Object>>> getTypeOption() {
        return iQaSysCommonService.getTypeOption();
    }
    /**
     * 获取问题类型下拉框信息（筛选占有位为0的）
     */
    @GetMapping("/partTypeOption")
    public R<List<Map<String, Object>>> getPartTypeOption() {
        return iQaSysCommonService.getPartTypeOption();
    }

    /**
     * 获取密级信息下拉框信息
     */
    @GetMapping("/securityOption/{securityType}")
    public R<List<Map<String, Object>>> getSecurityOption(@PathVariable String securityType) {
        return iQaSysCommonService.getSecurityOption(securityType);
    }

    /**
     * 开启消息提醒
     */
    @PostMapping("/openNotify")
    public R<Void> openNotify() {
        return iQaSysCommonService.openNotify();
    }

    /**
     * 关闭消息提醒
     */
    @PostMapping("/closeNotify")
    public R<Void> closeNotify() {
        return iQaSysCommonService.closeNotify();
    }

    /**
     * 将消息提醒位置1（提醒）
     */
    @PostMapping("/setNotify/{recordId}")
    public R<Void> setNotify(@PathVariable String recordId) {
        return iQaSysCommonService.setNotify(recordId);
    }

    /**
     * 将消息提醒位置0（不提醒）
     */
    @PostMapping("/markNotified/{recordIds}")
    public R<Void> markNotified(@PathVariable String[] recordIds) {
        return iQaSysCommonService.markNotified(Arrays.asList(recordIds));
    }

}
