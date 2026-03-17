import request from '@/utils/request'

// 查询登录日志列表
export function listLoginInfo(query) {
  return request({
    url: '/monitor/logininfor/list',
    method: 'get',
    params: query
  })
}

// 查询系统访问记录详细
export function getLogininfor(infoId) {
  return request({
    url: '/monitor/logininfor/' + infoId,
    method: 'get'
  })
}

// 新增系统访问记录
export function addLogininfor(data) {
  return request({
    url: '/monitor/logininfor',
    method: 'post',
    data: data
  })
}

// 修改系统访问记录
export function updateLogininfor(data) {
  return request({
    url: '/monitor/logininfor',
    method: 'put',
    data: data
  })
}

// 删除登录日志
export function delLoginInfor(infoId) {
  return request({
    url: '/monitor/logininfor/' + infoId,
    method: 'delete'
  })
}

// 清空登录日志
export function cleanLoginInfor() {
  return request({
    url: '/monitor/logininfor/clean',
    method: 'delete'
  })
}

// 导出登录日志
export function exportLoginInfor(query) {
  return request({
    url: '/monitor/logininfor/export',
    method: 'post',
    params: query
  })
}

/**
 * 获取人员部门树结构
 */
export function LoadDeptUserTree() {
  return request({
    url: '/system/deptUser/tree',
    method: 'get'
  });
}
