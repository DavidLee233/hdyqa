package com.sysware.mainData.controller;

import cn.hutool.core.util.StrUtil;
import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.mainData.domain.DownstreamPullRequest;
import com.sysware.mainData.domain.HdlOrganizationDepartment;
import com.sysware.mainData.domain.HdlPersonBasicInfo;
import com.sysware.mainData.domain.HdlPersonJobInfo;
import com.sysware.mainData.domain.vo.HdlOrganizationDepartmentVo;
import com.sysware.mainData.domain.vo.HdlPersonBasicInfoVo;
import com.sysware.mainData.domain.vo.HdlPersonJobInfoVo;
import com.sysware.mainData.mapper.HdlOrganizationDepartmentMapper;
import com.sysware.mainData.mapper.HdlPersonBasicInfoMapper;
import com.sysware.mainData.mapper.HdlPersonJobInfoMapper;
import com.sysware.mainData.service.IMainDataConnectivityService;
import com.sysware.common.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * @project npic
 * @description MainDataDownstreamController控制器，负责下游主数据接口相关接口请求接收、参数处理与结果响应。
 * @author DavidLee233
 * @date 2026/3/20
 */
@RestController
@RequiredArgsConstructor
@SaCheckLogin
@RequestMapping("/mainData/downstream")
public class MainDataDownstreamController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String TYPE_ORG_DEPT = "1";
    private static final String TYPE_PERSON_BASIC = "2";
    private static final String TYPE_PERSON_JOB = "3";

    private final HdlOrganizationDepartmentMapper organizationDepartmentMapper;
    private final HdlPersonBasicInfoMapper personBasicInfoMapper;
    private final HdlPersonJobInfoMapper personJobInfoMapper;
    private final IMainDataConnectivityService connectivityService;
    /**
     * @description 执行pullOrganizationDepartment方法，完成下游主数据接口相关业务处理。
     * @params request 下游系统拉取请求对象（包含分页、时间水位等过滤条件）
     *
      * @return R<TableDataInfo<HdlOrganizationDepartmentVo>> 下游拉取接口响应（包含数据列表、分页信息与时间水位）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostMapping("/organizationDepartment/pull")
    public R<TableDataInfo<HdlOrganizationDepartmentVo>> pullOrganizationDepartment(
                                                                                    @RequestBody(required = false) DownstreamPullRequest request,
                                                                                    HttpServletRequest httpServletRequest) {
        long startAt = System.currentTimeMillis();
        boolean success = false;
        long recordCount = 0L;
        String message = "success";
        DownstreamPullRequest req = normalizeRequest(request);
        LambdaQueryWrapper<HdlOrganizationDepartment> lqw = new LambdaQueryWrapper<>();

        try {
            if (!Boolean.TRUE.equals(req.getIncludeInvalid())) {
                lqw.and(w -> w.ne(HdlOrganizationDepartment::getEnableState, "3")
                    .or()
                    .isNull(HdlOrganizationDepartment::getEnableState));
            }
            if (StrUtil.isNotBlank(req.getName())) {
                lqw.like(HdlOrganizationDepartment::getName, req.getName());
            }
            if (StrUtil.isNotBlank(req.getCode())) {
                lqw.like(HdlOrganizationDepartment::getCode, req.getCode());
            }
            if (StrUtil.isNotBlank(req.getShortName())) {
                lqw.like(HdlOrganizationDepartment::getShortName, req.getShortName());
            }
            if (StrUtil.isNotBlank(req.getSign())) {
                lqw.like(HdlOrganizationDepartment::getSign, req.getSign());
            }
            applyUpdatedAfter(lqw, req.getUpdatedAfter());
            lqw.orderByDesc(HdlOrganizationDepartment::getUpdateTime, HdlOrganizationDepartment::getCreateTime);

            Page<HdlOrganizationDepartment> page = organizationDepartmentMapper.selectPage(
                new Page<>(req.getPageNum(), req.getPageSize()), lqw);
            success = true;
            recordCount = page.getTotal();
            return R.ok(buildPageResult(convertOrganizationDepartmentVo(page.getRecords()), page.getTotal()));
        } catch (Exception e) {
            message = e.getMessage();
            throw e;
        } finally {
            connectivityService.recordDownstreamPull(
                ServletUtils.getFullUrl(httpServletRequest),
                TYPE_ORG_DEPT,
                success,
                recordCount,
                System.currentTimeMillis() - startAt,
                message,
                ServletUtils.getClientIP(httpServletRequest)
            );
        }
    }
    /**
     * @description 执行pullPersonBasicInfo方法，完成下游主数据接口相关业务处理。
     * @params request 下游系统拉取请求对象（包含分页、时间水位等过滤条件）
     *
      * @return R<TableDataInfo<HdlPersonBasicInfoVo>> 下游拉取接口响应（包含数据列表、分页信息与时间水位）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostMapping("/personBasicInfo/pull")
    public R<TableDataInfo<HdlPersonBasicInfoVo>> pullPersonBasicInfo(
                                                                      @RequestBody(required = false) DownstreamPullRequest request,
                                                                      HttpServletRequest httpServletRequest) {
        long startAt = System.currentTimeMillis();
        boolean success = false;
        long recordCount = 0L;
        String message = "success";
        DownstreamPullRequest req = normalizeRequest(request);
        LambdaQueryWrapper<HdlPersonBasicInfo> lqw = new LambdaQueryWrapper<>();

        try {
            if (!Boolean.TRUE.equals(req.getIncludeInvalid())) {
                lqw.and(w -> w.ne(HdlPersonBasicInfo::getEnableState, "3")
                    .or()
                    .isNull(HdlPersonBasicInfo::getEnableState));
            }
            if (StrUtil.isNotBlank(req.getName())) {
                lqw.like(HdlPersonBasicInfo::getName, req.getName());
            }
            if (StrUtil.isNotBlank(req.getCode())) {
                lqw.like(HdlPersonBasicInfo::getCode, req.getCode());
            }
            if (StrUtil.isNotBlank(req.getMobile())) {
                lqw.like(HdlPersonBasicInfo::getMobile, req.getMobile());
            }
            if (StrUtil.isNotBlank(req.getIdNumber())) {
                lqw.like(HdlPersonBasicInfo::getIdNumber, req.getIdNumber());
            }
            applyUpdatedAfter(lqw, req.getUpdatedAfter());
            lqw.orderByDesc(HdlPersonBasicInfo::getUpdateTime, HdlPersonBasicInfo::getCreateTime);

            Page<HdlPersonBasicInfo> page = personBasicInfoMapper.selectPage(
                new Page<>(req.getPageNum(), req.getPageSize()), lqw);
            success = true;
            recordCount = page.getTotal();
            return R.ok(buildPageResult(convertPersonBasicInfoVo(page.getRecords()), page.getTotal()));
        } catch (Exception e) {
            message = e.getMessage();
            throw e;
        } finally {
            connectivityService.recordDownstreamPull(
                ServletUtils.getFullUrl(httpServletRequest),
                TYPE_PERSON_BASIC,
                success,
                recordCount,
                System.currentTimeMillis() - startAt,
                message,
                ServletUtils.getClientIP(httpServletRequest)
            );
        }
    }
    /**
     * @description 执行pullPersonJobInfo方法，完成下游主数据接口相关业务处理。
     * @params request 下游系统拉取请求对象（包含分页、时间水位等过滤条件）
     *
      * @return R<TableDataInfo<HdlPersonJobInfoVo>> 下游拉取接口响应（包含数据列表、分页信息与时间水位）。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @PostMapping("/personJobInfo/pull")
    public R<TableDataInfo<HdlPersonJobInfoVo>> pullPersonJobInfo(
                                                                  @RequestBody(required = false) DownstreamPullRequest request,
                                                                  HttpServletRequest httpServletRequest) {
        long startAt = System.currentTimeMillis();
        boolean success = false;
        long recordCount = 0L;
        String message = "success";
        DownstreamPullRequest req = normalizeRequest(request);
        LambdaQueryWrapper<HdlPersonJobInfo> lqw = new LambdaQueryWrapper<>();

        try {
            if (!Boolean.TRUE.equals(req.getIncludeInvalid())) {
                lqw.and(w -> w.ne(HdlPersonJobInfo::getEndFlag, "Y")
                    .or()
                    .isNull(HdlPersonJobInfo::getEndFlag));
            }
            if (StrUtil.isNotBlank(req.getName())) {
                lqw.like(HdlPersonJobInfo::getName, req.getName());
            }
            if (StrUtil.isNotBlank(req.getCode())) {
                lqw.like(HdlPersonJobInfo::getCode, req.getCode());
            }
            if (StrUtil.isNotBlank(req.getKeyNumber())) {
                lqw.like(HdlPersonJobInfo::getKeyNumber, req.getKeyNumber());
            }
            if (StrUtil.isNotBlank(req.getIdNumber())) {
                lqw.like(HdlPersonJobInfo::getIdNumber, req.getIdNumber());
            }
            applyUpdatedAfter(lqw, req.getUpdatedAfter());
            lqw.orderByDesc(HdlPersonJobInfo::getUpdateTime, HdlPersonJobInfo::getCreateTime);

            Page<HdlPersonJobInfo> page = personJobInfoMapper.selectPage(
                new Page<>(req.getPageNum(), req.getPageSize()), lqw);
            success = true;
            recordCount = page.getTotal();
            return R.ok(buildPageResult(convertPersonJobInfoVo(page.getRecords()), page.getTotal()));
        } catch (Exception e) {
            message = e.getMessage();
            throw e;
        } finally {
            connectivityService.recordDownstreamPull(
                ServletUtils.getFullUrl(httpServletRequest),
                TYPE_PERSON_JOB,
                success,
                recordCount,
                System.currentTimeMillis() - startAt,
                message,
                ServletUtils.getClientIP(httpServletRequest)
            );
        }
    }

    /**
     * @description 解析并转换下游主数据接口相关输入，生成可直接使用的数据结构。
     * @params request 下游系统拉取请求对象（包含分页、时间水位等过滤条件）
     *
      * @return DownstreamPullRequest 类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private DownstreamPullRequest normalizeRequest(DownstreamPullRequest request) {
        DownstreamPullRequest req = request == null ? new DownstreamPullRequest() : request;
        if (req.getPageNum() == null || req.getPageNum() <= 0) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null || req.getPageSize() <= 0) {
            req.setPageSize(200);
        } else if (req.getPageSize() > 1000) {
            req.setPageSize(1000);
        }
        return req;
    }

    /**
     * @description 执行applyUpdatedAfter方法，完成下游主数据接口相关业务处理。
     * @params lqw 下游数据查询条件构造器
     * @params updatedAfter 增量拉取起始时间（仅返回该时间之后更新的数据）
     *
      * @return void 无返回值，方法执行后通过副作用更新系统状态。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private <T> void applyUpdatedAfter(LambdaQueryWrapper<T> lqw, LocalDateTime updatedAfter) {
        if (updatedAfter == null) {
            return;
        }
        lqw.apply("ifnull(update_time, create_time) >= {0}", updatedAfter.format(DATE_TIME_FORMATTER));
    }

    /**
     * @description 将组织部门实体列表转换为下游接口VO列表，避免JDK17环境下cglib复制异常。
     * @params records 组织部门实体分页记录
     *
     * @return List<HdlOrganizationDepartmentVo> 下游接口组织部门数据列表。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private List<HdlOrganizationDepartmentVo> convertOrganizationDepartmentVo(List<HdlOrganizationDepartment> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        List<HdlOrganizationDepartmentVo> result = new ArrayList<>(records.size());
        for (HdlOrganizationDepartment record : records) {
            HdlOrganizationDepartmentVo vo = new HdlOrganizationDepartmentVo();
            BeanUtils.copyProperties(record, vo);
            result.add(vo);
        }
        return result;
    }

    /**
     * @description 将员工基本信息实体列表转换为下游接口VO列表，避免JDK17环境下cglib复制异常。
     * @params records 员工基本信息实体分页记录
     *
     * @return List<HdlPersonBasicInfoVo> 下游接口员工基本信息数据列表。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private List<HdlPersonBasicInfoVo> convertPersonBasicInfoVo(List<HdlPersonBasicInfo> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        List<HdlPersonBasicInfoVo> result = new ArrayList<>(records.size());
        for (HdlPersonBasicInfo record : records) {
            HdlPersonBasicInfoVo vo = new HdlPersonBasicInfoVo();
            BeanUtils.copyProperties(record, vo);
            result.add(vo);
        }
        return result;
    }

    /**
     * @description 将员工工作信息实体列表转换为下游接口VO列表，避免JDK17环境下cglib复制异常。
     * @params records 员工工作信息实体分页记录
     *
     * @return List<HdlPersonJobInfoVo> 下游接口员工工作信息数据列表。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private List<HdlPersonJobInfoVo> convertPersonJobInfoVo(List<HdlPersonJobInfo> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        List<HdlPersonJobInfoVo> result = new ArrayList<>(records.size());
        for (HdlPersonJobInfo record : records) {
            HdlPersonJobInfoVo vo = new HdlPersonJobInfoVo();
            BeanUtils.copyProperties(record, vo);
            result.add(vo);
        }
        return result;
    }

    /**
     * @description 构建下游分页响应对象，统一返回结构与分页元数据。
     * @params rows 业务结果列表
     * @params total 业务结果总条数
     *
     * @return TableDataInfo<T> 表格分页结果（包含记录列表与总条数），用于下游拉取展示。
     * @author DavidLee233
     * @date 2026/3/20
     */
    private <T> TableDataInfo<T> buildPageResult(List<T> rows, long total) {
        TableDataInfo<T> tableDataInfo = TableDataInfo.build();
        tableDataInfo.setRows(rows);
        tableDataInfo.setTotal(total);
        return tableDataInfo;
    }
}
