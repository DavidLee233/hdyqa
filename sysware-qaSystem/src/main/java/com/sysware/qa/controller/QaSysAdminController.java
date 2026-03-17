package com.sysware.qa.controller;

import com.sysware.qa.domain.QaSys;
import com.sysware.qa.domain.bo.QaSysBo;
import com.sysware.qa.domain.vo.QaSysVo;
import com.sysware.qa.service.IQaSysAdminService;
import com.sysware.common.annotation.Log;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.enums.BusinessType;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 问题回答
 *
 * @author aa
 * @date 2025-07-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/qaSystem/qa")
public class QaSysAdminController extends BaseController {

    private final IQaSysAdminService iQaSysService;
    /**
     * 查询问题回答列表（左侧列表）
     */
    @GetMapping("/listAdmin")
    public TableDataInfo listAdmin(QaSysBo bo, PageQuery pageQuery) {
        return iQaSysService.queryAdminPageList(bo, pageQuery);
    }
    /**
     * 查询需提醒的问题回答列表
     */
    @GetMapping("/listAdminNotify")
    public R<List<QaSys>> listAdminNotify() {
        return iQaSysService.queryAdminNotifyList();
    }

    /**
     * 查询问题回答列表（右侧列表）
     */
    @GetMapping("/listAdminAll")
    public TableDataInfo list(QaSysBo bo, PageQuery pageQuery) {
        return iQaSysService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出问题回答列表（管理员）
     */
    @Log(title = "问答导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public R<Void> export(@RequestBody Map<String, String> params, HttpServletResponse response) throws IOException {
        return iQaSysService.exportExcel(params);
    }
}
