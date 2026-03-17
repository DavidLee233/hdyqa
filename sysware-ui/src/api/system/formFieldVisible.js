import request from '@/utils/request'

// 查询单项显示控制列表
export function listFormFieldVisible(query) {
  return request({
    url: '/system/formFieldVisible/list',
    method: 'get',
    params: query
  })
}

// 查询单项显示控制详细
export function getFormFieldVisible(id) {
  return request({
    url: '/system/formFieldVisible/' + id,
    method: 'get'
  })
}

// 新增单项显示控制
export function addFormFieldVisible(data) {
  return request({
    url: '/system/formFieldVisible',
    method: 'post',
    data: data
  })
}

// 修改单项显示控制
export function updateFormFieldVisible(data) {
  return request({
    url: '/system/formFieldVisible',
    method: 'put',
    data: data
  })
}

// 删除单项显示控制
export function delFormFieldVisible(id) {
  return request({
    url: '/system/formFieldVisible/' + id,
    method: 'delete'
  })
}
