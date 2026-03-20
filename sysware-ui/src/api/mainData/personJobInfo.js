import request from '@/utils/request'

// 查询员工工作信息列表
export function listPersonJobInfo(query) {
  return request({
    url: '/mainData/personJobInfo/list',
    method: 'get',
    params: query
  })
}

// 查询员工工作信息详情
export function getPersonJobInfo(pkPsnjob) {
  return request({
    url: '/mainData/personJobInfo/' + pkPsnjob,
    method: 'get'
  })
}

// 新增员工工作信息
export function addPersonJobInfo(data) {
  return request({
    url: '/mainData/personJobInfo',
    method: 'post',
    data: data
  })
}

// 修改员工工作信息
export function updatePersonJobInfo(data) {
  return request({
    url: '/mainData/personJobInfo',
    method: 'put',
    data: data
  })
}

// 删除员工工作信息
export function delPersonJobInfo(pkPsnjob) {
  return request({
    url: '/mainData/personJobInfo/' + pkPsnjob,
    method: 'delete'
  })
}

// 查询远端员工工作信息列表
export function listRemotePersonJobInfo(query) {
  return request({
    url: '/mainData/personJobInfo/remote/list',
    method: 'post',
    data: query
  })
}

// 导出远端员工工作信息
export function exportRemotePersonJobInfo(query) {
  return request({
    url: '/mainData/personJobInfo/remote/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}

// 强制同步远端员工工作信息到本地
export function forceSyncRemotePersonJobInfo() {
  return request({
    url: '/mainData/personJobInfo/remote/forceSync',
    method: 'post'
  })
}
