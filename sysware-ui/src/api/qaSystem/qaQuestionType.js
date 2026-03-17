import request from '@/utils/request'

// 查询问题类型表列表
export function listQaQuestionType(query) {
  return request({
    url: '/qaSystem/qaQuestionType/list',
    method: 'get',
    params: query
  })
}

// 查询问题类型表详细
export function getSequenceNum() {
  return request({
    url: '/qaSystem/qaQuestionType/sequenceNum',
    method: 'get'
  })
}


// 查询问题类型表详细
export function getQaQuestionType(questionId) {
  return request({
    url: '/qaSystem/qaQuestionType/' + questionId,
    method: 'get'
  })
}

// 新增问题类型表
export function addQaQuestionType(data) {
  return request({
    url: '/qaSystem/qaQuestionType',
    method: 'post',
    data: data
  })
}

// 修改问题类型表
export function updateQaQuestionType(data) {
  return request({
    url: '/qaSystem/qaQuestionType',
    method: 'put',
    data: data
  })
}

// 删除问题类型表
export function delQaQuestionType(questionId) {
  return request({
    url: '/qaSystem/qaQuestionType/' + questionId,
    method: 'delete'
  })
}

// 查询问题回答类型角色关联列表
export function listQaUserQuestion(data) {
  return request({
    url: '/qaSystem/qaUserQuestion/list',
    method: 'get',
    params: data
  })
}

// 查询问题回答类型角色关联详细
export function getQaUserQuestion(id) {
  return request({
    url: '/qaSystem/qaUserQuestion/' + id,
    method: 'get'
  })
}

// 新增问题回答类型角色关联
export function addQaUserQuestion(data) {
  return request({
    url: '/qaSystem/qaUserQuestion',
    method: 'post',
    data: data
  })
}

// 修改问题回答类型角色关联
export function updateQaUserQuestion(data) {
  return request({
    url: '/qaSystem/qaUserQuestion',
    method: 'put',
    data: data
  })
}

// 删除问题回答类型角色关联
export function delQaUserQuestion(id) {
  return request({
    url: '/qaSystem/qaUserQuestion/' + id,
    method: 'delete'
  })
}
