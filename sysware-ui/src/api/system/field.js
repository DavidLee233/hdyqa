import request from '@/utils/request'

// 查询自定义页面显示列表
export function listField(query) {
  return request({
    url: '/system/field/list',
    method: 'get',
    params: query
  })
}

// 查询自定义页面显示列表
export function listFieldByPath(query) {
  return request({
    url: '/system/field/listFieldByPath',
    method: 'get',
    params: query
  })


}

// 查询自定义页面显示详细
export function getField(id) {
  return request({
    url: '/system/field/' + id,
    method: 'get'
  })
}

// 新增自定义页面显示
export function addField(data) {
  return request({
    url: '/system/field',
    method: 'post',
    data: data
  })
}

// 修改自定义页面显示
export function updateField(data) {
  return request({
    url: '/system/field',
    method: 'put',
    data: data
  })
}

// 删除自定义页面显示
export function delField(id) {
  return request({
    url: '/system/field/' + id,
    method: 'delete'
  })
}

// 导出自定义页面显示
export function exportField(query) {
  return request({
    url: '/system/field/export',
    method: 'get',
    params: query
  })
}

// 根据父项ID查询项目树
export function project(query) {
  return request({
    url: '/project/project/selectProjectList',
    method: 'get',
    params: query
  })
}

