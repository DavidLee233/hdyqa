import request from '@/utils/request'

export function listDownstreamSystems() {
  return request({
    url: '/mainData/connectivity/downstream/list',
    method: 'get'
  })
}

export function saveDownstreamSystem(data) {
  return request({
    url: '/mainData/connectivity/downstream/save',
    method: 'post',
    data
  })
}

export function deleteDownstreamSystems(ids) {
  return request({
    url: '/mainData/connectivity/downstream/delete',
    method: 'post',
    data: { ids: ids || [] }
  })
}

export function checkDownstreamSystems(ids) {
  return request({
    url: '/mainData/connectivity/downstream/check',
    method: 'post',
    data: { ids: ids || [] }
  })
}

export function listDownstreamLogs(params) {
  return request({
    url: '/mainData/connectivity/downstream/log/list',
    method: 'get',
    params
  })
}

export function deleteDownstreamLogs(ids) {
  return request({
    url: '/mainData/connectivity/downstream/log/delete',
    method: 'post',
    data: { ids: ids || [] }
  })
}

export function clearDownstreamLogs(systemId) {
  return request({
    url: '/mainData/connectivity/downstream/log/clear',
    method: 'post',
    data: { systemId: systemId || null }
  })
}

export function checkRemoteConnectivity() {
  return request({
    url: '/mainData/connectivity/remote/check',
    method: 'get'
  })
}

export function listRemoteLogs(params) {
  return request({
    url: '/mainData/connectivity/remote/log/list',
    method: 'get',
    params
  })
}

export function deleteRemoteLogs(ids) {
  return request({
    url: '/mainData/connectivity/remote/log/delete',
    method: 'post',
    data: { ids: ids || [] }
  })
}

export function clearRemoteLogs(dataType, callSource) {
  return request({
    url: '/mainData/connectivity/remote/log/clear',
    method: 'post',
    data: {
      dataType: dataType || null,
      callSource: callSource || 'sync'
    }
  })
}
