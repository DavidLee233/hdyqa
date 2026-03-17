import request from '@/utils/request'

// 查询问题回答列表（左侧）
export function listAdminQa(query) {
  return request({
    url: '/qaSystem/qa/listAdmin',
    method: 'get',
    params: query
  })
}

// 查询需提醒的问题回答列表
export function listAdminNotify() {
  return request({
    url: '/qaSystem/qa/listAdminNotify',
    method: 'get',
  })
}


// 获取问题回答列表（右侧）
export function listQa(query) {
  return request({
    url: '/qaSystem/qa/listAdminAll',
    method: 'get',
    params: query
  })
}

// 导出问题回答（管理员）
export function expQa(data) {
  return request({
    url: '/qaSystem/qa/export',
    method: 'post',
    data: data
  })
}
