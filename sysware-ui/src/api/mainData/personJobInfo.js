import request from '@/utils/request'

// 查询员工工作信息数据列表
export function listPersonJobInfo(query) {
  return request({
    url: '/mainData/personJobInfo/list',
    method: 'get',
    params: query
  })
}

// 查询员工工作信息数据详细
export function getPersonJobInfo(pkPsnjob) {
  return request({
    url: '/mainData/personJobInfo/' + pkPsnjob,
    method: 'get'
  })
}

// 新增员工工作信息数据
export function addPersonJobInfo(data) {
  return request({
    url: '/mainData/personJobInfo',
    method: 'post',
    data: data
  })
}

// 修改员工工作信息数据
export function updatePersonJobInfo(data) {
  return request({
    url: '/mainData/personJobInfo',
    method: 'put',
    data: data
  })
}

// 删除员工工作信息数据
export function delPersonJobInfo(pkPsnjob) {
  return request({
    url: '/mainData/personJobInfo/' + pkPsnjob,
    method: 'delete'
  })
}

// 查询远程员工基本信息列表
export function listRemotePersonJobInfo(query) {
  return request({
    url: '/mainData/personJobInfo/remote/list',
    method: 'post', // 改为POST方法
    data: query // 改为data传递
  })
}

// 导出远程员工基本信息
export function exportRemotePersonJobInfo(query) {
  return request({
    url: '/mainData/personJobInfo/remote/export',
    method: 'post', // 改为POST方法
    data: query, // 改为data传递
    responseType: 'blob'
  })
}
