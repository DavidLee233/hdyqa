import request from '@/utils/request'

// 查询密级列表
export function listSecurity(query) {
  return request({
    url: '/system/security/list',
    method: 'get',
    params: query
  })
}

// 查询密级列表
export function listUserSecurity(query) {
  return request({
    url: '/system/security/listUser',
    method: 'get',
    params: query
  })
}

// 查询密级列表
export function listDataSecurity(query) {
  return request({
    url: '/system/security/listData',
    method: 'get',
    params: query
  })
}

// 查询密级详细
export function getSecurity(id) {
  return request({
    url: '/system/security/' + id,
    method: 'get'
  })
}

// 新增密级
export function addSecurity(data) {
  return request({
    url: '/system/security',
    method: 'post',
    data: data
  })
}

// 修改密级
export function updateSecurity(data) {
  return request({
    url: '/system/security',
    method: 'put',
    data: data
  })
}

// 删除密级
export function delSecurity(id) {
  return request({
    url: '/system/security/' + id,
    method: 'delete'
  })
}

// 导出密级
export function exportSecurity(query) {
  return request({
    url: '/system/security/export',
    method: 'get',
    params: query
  })
}
