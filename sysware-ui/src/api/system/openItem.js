import request from '@/utils/request'

// 查询弹窗信息列表
export function listOpenItem(query) {
  return request({
    url: '/system/openItem/list',
    method: 'get',
    params: query
  })
}

// 查询弹窗信息详细
export function getOpenItem(createBy) {
  return request({
    url: '/system/openItem/' + createBy,
    method: 'get'
  })
}

// 新增弹窗信息
export function addOpenItem(data) {
  return request({
    url: '/system/openItem',
    method: 'post',
    data: data
  })
}

// 修改弹窗信息
export function updateOpenItem(data) {
  return request({
    url: '/system/openItem',
    method: 'put',
    data: data
  })
}

// 删除弹窗信息
export function delOpenItem(createBy) {
  return request({
    url: '/system/openItem/' + createBy,
    method: 'delete'
  })
}
