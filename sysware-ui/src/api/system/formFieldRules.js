import request from '@/utils/request'

// 查询校验规则列表
export function listFormFieldRules(query) {
  return request({
    url: '/system/formFieldRules/list',
    method: 'get',
    params: query
  })
}

// 查询校验规则详细
export function getFormFieldRules(id) {
  return request({
    url: '/system/formFieldRules/' + id,
    method: 'get'
  })
}

// 新增校验规则
export function addFormFieldRules(data) {
  return request({
    url: '/system/formFieldRules',
    method: 'post',
    data: data
  })
}

// 修改校验规则
export function updateFormFieldRules(data) {
  return request({
    url: '/system/formFieldRules',
    method: 'put',
    data: data
  })
}

// 删除校验规则
export function delFormFieldRules(id) {
  return request({
    url: '/system/formFieldRules/' + id,
    method: 'delete'
  })
}
