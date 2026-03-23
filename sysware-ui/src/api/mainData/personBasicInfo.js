import request from '@/utils/request'

// 查询员工基本信息列表
export function listPersonBasicInfo(query) {
  return request({
    url: '/mainData/personBasicInfo/list',
    method: 'get',
    params: query
  })
}

// 查询员工基本信息详情
export function getPersonBasicInfo(pkPsndoc) {
  return request({
    url: '/mainData/personBasicInfo/' + pkPsndoc,
    method: 'get'
  })
}

// 新增员工基本信息
export function addPersonBasicInfo(data) {
  return request({
    url: '/mainData/personBasicInfo',
    method: 'post',
    data: data
  })
}

// 修改员工基本信息
export function updatePersonBasicInfo(data) {
  return request({
    url: '/mainData/personBasicInfo',
    method: 'put',
    data: data
  })
}

// 删除员工基本信息
export function delPersonBasicInfo(pkPsndoc) {
  return request({
    url: '/mainData/personBasicInfo/' + pkPsndoc,
    method: 'delete'
  })
}

// 查询远端员工基本信息列表
export function listRemotePersonBasicInfo(query) {
  return request({
    url: '/mainData/personBasicInfo/remote/list',
    method: 'post',
    data: query
  })
}

// 导出远端员工基本信息
export function exportRemotePersonBasicInfo(query) {
  return request({
    url: '/mainData/personBasicInfo/remote/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}

// 强制同步远端员工基本信息到本地
export function forceSyncRemotePersonBasicInfo(syncMode) {
  return request({
    url: '/mainData/personBasicInfo/remote/forceSync',
    method: 'post',
    data: {
      syncMode: syncMode || 'full'
    }
  })
}
