import request from '@/utils/request'

// 查询主数据字段映射列表
export function listMainDataMapping(query) {
  return request({
    url: '/mainData/mainDataMapping/list',
    method: 'get',
    params: query
  })
}

// 查询主数据字段映射详细
export function getMainDataMapping(mapId) {
  return request({
    url: '/mainData/mainDataMapping/' + mapId,
    method: 'get'
  })
}

// 新增主数据字段映射
export function addMainDataMapping(data) {
  return request({
    url: '/mainData/mainDataMapping',
    method: 'post',
    data: data
  })
}

// 修改主数据字段映射
export function updateMainDataMapping(data) {
  return request({
    url: '/mainData/mainDataMapping',
    method: 'put',
    data: data
  })
}

// 删除主数据字段映射
export function delMainDataMapping(mapId) {
  return request({
    url: '/mainData/mainDataMapping/' + mapId,
    method: 'delete'
  })
}


// 根据类型查询字段映射
export function getFieldMappingByType(query) {
  return request({
    url: '/mainData/mainDataMapping/getByType',
    method: 'get',
    params: query
  })
}

// 导出主数据字段映射
export function exportMainDataMapping(query) {
  return request({
    url: '/mainData/mainDataMapping/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
