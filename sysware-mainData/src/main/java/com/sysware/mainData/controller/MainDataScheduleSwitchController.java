package com.sysware.mainData.controller;

import com.sysware.common.core.domain.R;
import com.sysware.mainData.domain.bo.MainDataScheduleSwitchBo;
import com.sysware.mainData.service.IMainDataScheduleSwitchService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @project npic
 * @description MainDataScheduleSwitchController控制器，负责提供主数据三类页面使用的定时同步与定时备份开关查询、切换接口。
 * @author DavidLee233
 * @date 2026/3/21
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mainData/scheduleSwitch")
public class MainDataScheduleSwitchController {

    private final IMainDataScheduleSwitchService mainDataScheduleSwitchService;

    /**
     * @description 查询指定主数据类型的定时同步开关与定时备份开关状态，供页面首次打开时回显当前开关值。
     * @params dataType 主数据类型编码（1组织部门、2员工基本信息、3员工工作信息）。
     *
     * @return R<Map<String, Object>> 统一响应结果（包含数据类型、定时同步是否开启、定时备份是否开启），用于前端页面初始化开关状态。
     * @author DavidLee233
     * @date 2026/3/21
     */
    @GetMapping("/{dataType}")
    public R<Map<String, Object>> getStatus(@PathVariable String dataType) {
        return R.ok(mainDataScheduleSwitchService.querySwitchStatus(dataType));
    }

    /**
     * @description 更新指定主数据类型的定时同步或定时备份开关状态，并返回更新后的状态供前端即时回显。
     * @params bo 主数据定时任务开关更新请求对象（包含数据类型、开关类型和目标状态）。
     *
     * @return R<Map<String, Object>> 统一响应结果（包含更新后的定时同步与定时备份状态），用于前端提示与界面状态刷新。
     * @author DavidLee233
     * @date 2026/3/21
     */
    @PostMapping("/toggle")
    public R<Map<String, Object>> toggle(@Validated @RequestBody MainDataScheduleSwitchBo bo) {
        return R.ok(mainDataScheduleSwitchService.updateSwitchStatus(
            bo.getDataType(),
            bo.getSwitchType(),
            bo.getEnabled()
        ));
    }
}
