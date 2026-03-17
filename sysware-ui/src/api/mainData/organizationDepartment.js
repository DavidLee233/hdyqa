import request from '@/utils/request'

// 查询主数据系统列表
export function listOrganizationDepartment(query) {
  return request({
    url: '/mainData/organizationDepartment/list',
    method: 'get',
    params: query
  })
}

// 查询主数据系统详细
export function getOrganizationDepartment(pkDept) {
  return request({
    url: '/mainData/organizationDepartment/' + pkDept,
    method: 'get'
  })
}

// 新增主数据系统
export function addOrganizationDepartment(data) {
  return request({
    url: '/mainData/organizationDepartment',
    method: 'post',
    data: data
  })
}

// 修改主数据系统
export function updateOrganizationDepartment(data) {
  return request({
    url: '/mainData/organizationDepartment',
    method: 'put',
    data: data
  })
}

// 删除主数据系统
export function delOrganizationDepartment(pkDept) {
  return request({
    url: '/mainData/organizationDepartment/' + pkDept,
    method: 'delete'
  })
}

// 查询远程组织部门列表
export function listRemoteOrganizationDepartment(query) {
  return request({
    url: '/mainData/organizationDepartment/remote/list',
    method: 'post', // 改为POST方法
    data: query // 改为data传递
  })
}

// 导出远程组织部门数据
export function exportRemoteOrganizationDepartment(query) {
  return request({
    url: '/mainData/organizationDepartment/remote/export',
    method: 'post', // 改为POST方法
    data: query, // 改为data传递
    responseType: 'blob'
  })
}
