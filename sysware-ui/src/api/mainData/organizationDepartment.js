import request from '@/utils/request'

// 查询组织部门列表
export function listOrganizationDepartment(query) {
  return request({
    url: '/mainData/organizationDepartment/list',
    method: 'get',
    params: query
  })
}

// 查询组织部门详情
export function getOrganizationDepartment(pkDept) {
  return request({
    url: '/mainData/organizationDepartment/' + pkDept,
    method: 'get'
  })
}

// 新增组织部门
export function addOrganizationDepartment(data) {
  return request({
    url: '/mainData/organizationDepartment',
    method: 'post',
    data: data
  })
}

// 修改组织部门
export function updateOrganizationDepartment(data) {
  return request({
    url: '/mainData/organizationDepartment',
    method: 'put',
    data: data
  })
}

// 删除组织部门
export function delOrganizationDepartment(pkDept) {
  return request({
    url: '/mainData/organizationDepartment/' + pkDept,
    method: 'delete'
  })
}

// 查询远端组织部门列表
export function listRemoteOrganizationDepartment(query) {
  return request({
    url: '/mainData/organizationDepartment/remote/list',
    method: 'post',
    data: query
  })
}

// 导出远端组织部门
export function exportRemoteOrganizationDepartment(query) {
  return request({
    url: '/mainData/organizationDepartment/remote/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}

// 强制同步远端组织部门到本地
export function forceSyncRemoteOrganizationDepartment(syncMode) {
  return request({
    url: '/mainData/organizationDepartment/remote/forceSync',
    method: 'post',
    data: {
      syncMode: syncMode || 'full'
    }
  })
}
