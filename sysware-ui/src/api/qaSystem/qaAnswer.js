import request from '@/utils/request'

// 查询问题回答列表（左侧）
export function listAnswerQa(query) {
  return request({
    url: '/qaSystem/qa/listAUser',
    method: 'get',
    params: query
  })
}

// 查询需提醒的问题回答列表
export function listAnswerNotify() {
  return request({
    url: '/qaSystem/qa/listAnswerNotify',
    method: 'get',
  })
}

// 回答问题（回答者）
export function ansQa(data) {
  return request({
    url: '/qaSystem/qa/answer',
    method: 'put',
    data: data
  })
}
