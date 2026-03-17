import request from '@/utils/request'

// 查询问题回答列表（左侧）
export function listQuestionQa(query) {
  return request({
    url: '/qaSystem/qa/listQUser',
    method: 'get',
    params: query
  })
}

// 查询需提醒的问题回答列表
export function listQuestionNotify() {
  return request({
    url: '/qaSystem/qa/listQuestionNotify',
    method: 'get',
  })
}

// 新增问题回答
export function addQa(data) {
  return request({
    url: '/qaSystem/qa',
    method: 'post',
    data: data
  })
}

// 修改问题回答
export function updateQa(data) {
  return request({
    url: '/qaSystem/qa',
    method: 'put',
    data: data
  })
}

// 删除问题回答
export function delQa(recordId) {
  return request({
    url: '/qaSystem/qa/' + recordId,
    method: 'delete'
  })
}

// 采纳问题回答（提问者）
export function accQa(recordId) {
  return request({
    url: '/qaSystem/qa/' + recordId,
    method: 'put'
  })
}
