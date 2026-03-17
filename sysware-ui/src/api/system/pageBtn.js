import request from '@/utils/request'

// 查询页面按钮列表
export function listPageBtn(query) {
  return request({
    url: '/system/pageBtn/list',
    method: 'get',
    params: query
  })
}

// 查询页面按钮详细
export function getPageBtn(id) {
  return request({
    url: '/system/pageBtn/' + id,
    method: 'get'
  })
}

// 新增页面按钮
export function addPageBtn(data) {
  return request({
    url: '/system/pageBtn',
    method: 'post',
    data: data
  })
}

// 修改页面按钮
export function updatePageBtn(data) {
  return request({
    url: '/system/pageBtn',
    method: 'put',
    data: data
  })
}

// 删除页面按钮
export function delPageBtn(id) {
  return request({
    url: '/system/pageBtn/' + id,
    method: 'delete'
  })
}

// 初始化增加、编辑、删除按钮
export function initBtn() {
  return request({
    url: '/system/pageBtn/initBtn' ,
    method: 'get'
  })
}
