package com.sysware.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sysware.common.core.domain.PageQuery;
import com.sysware.common.core.domain.R;
import com.sysware.common.core.domain.event.OperLogEvent;
import com.sysware.common.core.page.TableDataInfo;
import com.sysware.common.enums.BusinessType;
import com.sysware.common.utils.JsonUtils;
import com.sysware.common.utils.StringUtils;
import com.sysware.common.utils.SyswareUtil;
import com.sysware.common.utils.ip.AddressUtils;
import com.sysware.system.domain.SysOperLog;
import com.sysware.system.domain.SysOperParams;
import com.sysware.system.domain.bo.SysOperLogBo;
import com.sysware.system.domain.vo.SysOperLogVo;
import com.sysware.system.mapper.SysOperLogMapper;
import com.sysware.system.mapper.SysOperParamsMapper;
import com.sysware.system.service.ISysOperLogService;
import com.sysware.system.service.ISysTableFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 操作日志 服务层处理
 *
 * @author
 */
@RequiredArgsConstructor
@Service
public class SysOperLogServiceImpl implements ISysOperLogService {

    private final SysOperLogMapper baseMapper;
    private final ISysTableFieldService tableFieldService;
    private final SysOperParamsMapper paramsMapper;

    /**
     * 操作日志记录
     *
     * @param operLogEvent 操作日志事件
     */
    @Async
    @EventListener
    public void recordOper(OperLogEvent operLogEvent) {
        SysOperLog operLog = BeanUtil.toBean(operLogEvent, SysOperLog.class);
        // 操作日志时跳出，防止循环
        List<String> skipUrls = Arrays.asList("/monitor/operlog", "/monitor/logininfor");
        if (skipUrls.contains(operLog.getOperUrl())) {
            return;
        }
        // 远程查询操作地点
        operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
        insertOperlog(operLog);
        insertOperParams(operLog);
    }

    private void insertOperParams(SysOperLog operLog) {

        List<SysOperParams> paramsList = new ArrayList<>();

        Object jsonResult = operLog.getJsonResult();
        if(jsonResult instanceof R){
            R<Object> r = (R<Object>) jsonResult;
            Object data = r.getData();
            if(data instanceof List){
                List<Object> list = (List<Object>) data;
                for (Object object : list){
                    SysOperParams operParams = new SysOperParams();
                    operParams.setOperId(operLog.getOperId());
                    operParams.setOperTableName(operLog.getOperObjectTableName());
                    if(operLog.getBusinessType().equals(BusinessType.INSERT.ordinal())){
                        operParams.setOperAfterParam(JsonUtils.toJsonString(object));
                    }else{
                        operParams.setOperPreParam(JsonUtils.toJsonString(object));
                    }
                    paramsList.add(operParams);
                }
            }else{
                SysOperParams operParams = new SysOperParams();
                operParams.setOperId(operLog.getOperId());
                operParams.setOperTableName(operLog.getOperObjectTableName());
                operParams.setOperAfterParam(operLog.getOperParam());
                operParams.setOperPreParam(JsonUtils.toJsonString(data));
                paramsList.add(operParams);
            }
        }
        paramsMapper.insertBatch(paramsList);
    }

    @Override
    public TableDataInfo<SysOperLogVo> selectPageOperLogList(SysOperLogBo operLog, PageQuery pageQuery) {
        // 使用传入的pageQuery构建分页参数
        Page<SysOperLog> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        // 执行分页查询
        IPage<SysOperLogVo> result = baseMapper.selectVoPage(page, buildQueryWrapper(operLog, pageQuery));
        return TableDataInfo.build(result);
    }

    private LambdaQueryWrapper<SysOperLog> buildQueryWrapper(SysOperLogBo operLog, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOperLog> lqw = Wrappers.lambdaQuery();

        // 添加查询条件
        if (operLog != null) {
            // 多个条件之间是 AND 关系
            lqw.like(StrUtil.isNotBlank(operLog.getTitle()), SysOperLog::getTitle, operLog.getTitle())
                    .eq(operLog.getBusinessType() != null, SysOperLog::getBusinessType, operLog.getBusinessType())
                    .like(StrUtil.isNotBlank(operLog.getOperName()), SysOperLog::getOperName, operLog.getOperName())
                    .eq(operLog.getStatus() != null, SysOperLog::getStatus, operLog.getStatus());
        }

        // 添加日期范围查询逻辑
        if (operLog != null) {
            // 均不为空时 beginTime =< operTime <= endTime
            if (operLog.getBeginTime() != null &&  !"".equals(operLog.getBeginTime()) && (operLog.getEndTime() != null && !"".equals(operLog.getEndTime()))) {
                lqw.between(SysOperLog::getOperTime, operLog.getBeginTime(), operLog.getEndTime());
            }
            // 开始时间不为空时，查询 operTime >= beginTime
            if(operLog.getBeginTime() != null &&  !"".equals(operLog.getBeginTime()) && (operLog.getEndTime() == null || "".equals(operLog.getEndTime()))){
                lqw.ge(SysOperLog::getOperTime, operLog.getBeginTime());
            }
            // 结束时间不为空时，查询 operTime <= endTime
            if(operLog.getEndTime() != null && !"".equals(operLog.getEndTime()) && (operLog.getBeginTime() == null || "".equals(operLog.getBeginTime()))){
                lqw.le(SysOperLog::getOperTime, operLog.getEndTime());
            }
        }

        // 添加排序查询逻辑
        if (pageQuery != null && StrUtil.isNotBlank(pageQuery.getOrderByColumn()) && isValidSortField(pageQuery.getOrderByColumn())) {
            String sortField = pageQuery.getOrderByColumn();
            boolean isAsc = "asc".equalsIgnoreCase(pageQuery.getIsAsc());

            // 将前端字段名映射到实体属性
            switch (sortField) {
                case "operName":
                    lqw.orderBy(true, isAsc, SysOperLog::getOperName);
                    break;
                case "operTime":
                    lqw.orderBy(true, isAsc, SysOperLog::getOperTime);
                    break;
                // 添加其他可排序字段...
                default:
                    // 默认按操作时间降序
                    lqw.orderBy(true, false, SysOperLog::getOperTime);
                    break;
            }
        } else {
            // 默认按操作时间降序排序
            lqw.orderBy(true, false, SysOperLog::getOperTime);
        }
        return lqw;
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
                "operName", "operTime"
        ));
        return allowedFields.contains(field);
    }

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLog operLog) {
        operLog.setOperTime(new Date());
        baseMapper.insert(operLog);
    }

    /**
     * 查询操作日志记录
     * @return
     */
    @Override
    public SysOperLogVo queryById(String operId){
        SysOperLogVo operLogVo = baseMapper.selectVoById(operId);
        return operLogVo;
    }

    /**
     * 新增操作日志记录
     */
    @Override
    public Boolean insertByBo(SysOperLogBo bo) {
        SysOperLog add = BeanUtil.toBean(bo, SysOperLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setOperId(add.getOperId());
        }
        return flag;
    }

    /**
     * 修改操作日志记录
     */
    @Override
    public Boolean updateByBo(SysOperLogBo bo) {
        SysOperLog update = BeanUtil.toBean(bo, SysOperLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysOperLog entity){
        //TODO 做一些数据校验,如唯一约束
    }


    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLogBo operLog) {
        return baseMapper.selectOperLogList(operLog);
        //Map<String, Object> params = operLog.getParams();
        //return null;
            /*baseMapper.selectList(new LambdaQueryWrapper<SysOperLog>()
                .like(StrUtil.isNotBlank(operLog.getTitle()),SysOperLogBo::getTitle,operLog.getTitle())
                .eq(operLog.getBusinessType() != null && operLog.getBusinessType() > 0,
                        SysOperLogBo::getBusinessType,operLog.getBusinessType())
                .func(f -> {
                    if (ArrayUtil.isNotEmpty(operLog.getBusinessTypes())){
                        f.in(SysOperLogBo::getBusinessType, Arrays.asList(operLog.getBusinessTypes()));
                    }
                })
                .eq(operLog.getStatus() != null && operLog.getStatus() > 0,
                        SysOperLogBo::getStatus,operLog.getStatus())
                .like(StrUtil.isNotBlank(operLog.getOperName()),SysOperLogBo::getOperName,operLog.getOperName())
                .apply(Validator.isNotEmpty(params.get("beginTime")),
                        "date_format(oper_time,'%y%m%d') >= date_format({0},'%y%m%d')",
                        params.get("beginTime"))
                .apply(Validator.isNotEmpty(params.get("endTime")),
                        "date_format(oper_time,'%y%m%d') <= date_format({0},'%y%m%d')",
                        params.get("endTime"))
                .orderByDesc(SysOperLogBo::getOperId));*/
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Collection<String> operIds) {
        return baseMapper.deleteBatchIds(operIds);
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLog selectOperLogById(Long operId) {
        return baseMapper.selectById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog() {
        baseMapper.delete(new LambdaQueryWrapper<>());
    }

    @Override
    public R getOperData(SysOperLogBo operLog) {
        JSONObject jsonObject = new JSONObject();
        List<Map> columnList =  baseMapper.getTableColumn(operLog.getOperObjectTableName());
        columnList.forEach(map -> {
            map.keySet().forEach(key -> {
                map.put(key, StringUtils.toCamelCase(String.valueOf(map.get(key))));
            });
            map.put("type","input");
            map.put("disabled","1");

            map.put("defaultCss","w-500");
            map.put("span","24");

        });
        List<Map> maps = SyswareUtil.listToCamelCase(columnList);
        jsonObject.put("items", maps);
       List<Map> operDataInfo = baseMapper.getOperDataInfo(operLog.getOperId());
       if(operDataInfo.size() > 0){

           Dict oldData = JsonUtils.parseMap(operDataInfo.get(0).get("oldData").toString());
           Dict newData = JsonUtils.parseMap(operDataInfo.get(0).get("newData").toString());
           oldData.forEach((key,value) -> {
               if(!SyswareUtil.ObjectToString(newData.get(key)).equals(SyswareUtil.ObjectToString(value))){
                   oldData.set(key,value+"->"+newData.get(key));
               }

           });


           jsonObject.put("oldData",oldData);
           //jsonObject.put("newData",JsonUtils.parseMap(operDataInfo.get(0).get("newData").toString()));
       }
       return R.ok(jsonObject);
    }



}
