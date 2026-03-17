import request from '@/utils/request'

// 查询操作日志列表
export function listOperlog(query) {
  return request({
    url: '/monitor/operlog/list',
    method: 'get',
    params: query
  })
}

// 获取操作对象详细信息
export function getOperDataInfo(query) {
  return request({
    url: '/monitor/operlog/getOperData',
    method: 'get',
    params: query
  })
}

// 查询操作日志详细
export function getOperlog(operIds) {
  return request({
    url: '/monitor/operlog/' + operIds,
    method: 'get'
  })
}

// 新增操作日志
export function addOperlog(data) {
  return request({
    url: '/monitor/operlog',
    method: 'post',
    data: data
  })
}

// 修改操作日志
export function updateOperlog(data) {
  return request({
    url: '/monitor/operlog',
    method: 'put',
    data: data
  })
}

// 删除操作日志
export function delOperlog(operIds) {
  return request({
    url: '/monitor/operlog/' + operIds,
    method: 'delete'
  })
}

// 清空操作日志
export function cleanOperlog() {
  return request({
    url: '/monitor/operlog/clean',
    method: 'delete'
  })
}

// 导出操作日志
export function exportOperlog(query) {
  return request({
    url: '/monitor/operlog/export',
    method: 'get',
    params: query
  })
}

// 获取用户名和ID、部门
export function getUser() {
  return request({
    url: '/monitor/operlog/user',
    method: 'get',
  })
}
