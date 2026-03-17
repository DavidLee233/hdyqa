package com.sysware.common.core.page;

import com.sysware.common.core.domain.PageQuery;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 * 适配您的RuoYi版本
 */
@Data
public class RemoteTableDataInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<?> rows;

    /** 消息状态码 */
    private int code;

    /** 消息内容 */
    private String msg;

    /** 页码 */
    private int pageNum;

    /** 每页数量 */
    private int pageSize;

    /**
     * 表格数据对象
     */
    public RemoteTableDataInfo() {
    }

    /**
     * 分页
     *
     * @param list 列表数据
     * @param total 总记录数
     */
    public RemoteTableDataInfo(List<?> list, int total) {
        this.rows = list;
        this.total = total;
    }

    public static RemoteTableDataInfo build(List<?> list, long total, PageQuery pageQuery) {
        RemoteTableDataInfo rspData = new RemoteTableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(total);
        if (pageQuery != null) {
            rspData.setPageNum(pageQuery.getPageNum());
            rspData.setPageSize(pageQuery.getPageSize());
        }
        return rspData;
    }

    public static RemoteTableDataInfo build(List<?> list, long total) {
        RemoteTableDataInfo rspData = new RemoteTableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(total);
        return rspData;
    }
}
