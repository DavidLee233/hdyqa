import request from '@/utils/request'

// 查询员工基本信息数据列表
export function listPersonBasicInfo(query) {
  return request({
    url: '/mainData/personBasicInfo/list',
    method: 'get',
    params: query
  })
}

// 查询员工基本信息数据详细
export function getPersonBasicInfo(pkPsndoc) {
  return request({
    url: '/mainData/personBasicInfo/' + pkPsndoc,
    method: 'get'
  })
}

// 新增员工基本信息数据
export function addPersonBasicInfo(data) {
  return request({
    url: '/mainData/personBasicInfo',
    method: 'post',
    data: data
  })
}

// 修改员工基本信息数据
export function updatePersonBasicInfo(data) {
  return request({
    url: '/mainData/personBasicInfo',
    method: 'put',
    data: data
  })
}

// 删除员工基本信息数据
export function delPersonBasicInfo(pkPsndoc) {
  return request({
    url: '/mainData/personBasicInfo/' + pkPsndoc,
    method: 'delete'
  })
}
