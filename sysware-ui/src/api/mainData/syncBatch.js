import request from '@/utils/request'

// 查询同步批次分页列表
export function listMainDataSyncBatch(query) {
  return request({
    url: '/mainData/syncBatch/list',
    method: 'get',
    params: query
  })
}

// 查询指定类型最近同步批次
export function listLatestMainDataSyncBatch(dataType, limit = 5) {
  return request({
    url: '/mainData/syncBatch/latest',
    method: 'get',
    params: { dataType, limit }
  })
}
