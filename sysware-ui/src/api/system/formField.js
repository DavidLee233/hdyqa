import request from '@/utils/request'

// 查询表单配置列表
export function listFormField(query) {
  return request({
    url: '/system/formField/list',
    method: 'get',
    params: query
  })
}

// 获取表单项的默认值
export function getDefaultValue(query) {
  return request({
    url: '/system/formField/getDefaultValue',
    method: 'get',
    params: query
  })
}

// 获取默认的数据库表结构
export function getDefaultFields() {
  return request({
    url: '/system/formField/getDefaultFields',
    method: 'get'
  })
}


// 查询表单配置详细
export function getFormField(fieldId) {
  return request({
    url: '/system/formField/' + fieldId,
    method: 'get'
  })
}


// 新增表单配置
export function addOrUpdateFormField(data) {
  return request({
    url: '/system/formField',
    method: 'post',
    data: data
  })
}

// 修改表单配置
export function updateFormField(data) {
  return request({
    url: '/system/formField',
    method: 'put',
    data: data
  })
}

// 删除表单配置
export function delFormField(fieldId) {
  return request({
    url: '/system/formField/' + fieldId,
    method: 'delete'
  })
}

// 导出表单配置
export function exportFormField(query) {
  return request({
    url: '/system/formField/export',
    method: 'get',
    params: query
  })
}


// 获取表单字段
export function selectFormFieldList(query) {
  return request({
    url: '/system/formField/selectFormFieldList',
    method: 'get',
    params: query
  })
}

// 获取表单字段
export function selectListByFieldId(fieldId) {
  return request({
    url: '/system/formField/selectFormFieldByFieldId/'+fieldId,
    method: 'get'
  })
}
