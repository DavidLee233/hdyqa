import request from '@/utils/request'
//                                 问题回答相关内容
// 获取用户名和ID
export function getUser() {
  return request({
    url: '/qaSystem/qaCommon/user',
    method: 'get',
  })
}
// 查询问题回答详细
export function getQa(recordId, mode) {
  return request({
    url: '/qaSystem/qaCommon/' + recordId,
    method: 'get',
    params: {mode}
  })
}
// 获取问题回答列表（右侧）
export function listQaCommon(query) {
  return request({
    url: '/qaSystem/qaCommon/listQa',
    method: 'get',
    params: query
  })
}
// 刷新参数缓存
export function refreshCache() {
  return request({
    url: '/qaSystem/qaCommon/refreshCache',
    method: 'delete'
  })
}

// 更新问题回答浏览量
export function updateVisits(recordId) {
  return request({
    url: '/qaSystem/qaCommon/updateVisits/' + recordId,
    method: 'post'
  })
}
//                                   附件相关内容
// 删除附件
export function updateRemoveRes(ossId) {
  return request({
    url: '/qaSystem/qaAttach/delete/' + ossId,
    method: 'delete'
  })
}

// 转换接口
export function convertToPdf(fileData, fileName) {
  const formData = new FormData();
  formData.append('file', new Blob([fileData]), fileName);
  formData.append('outputType', 'pdf');

  return request({
    url: '/qaSystem/qaAttach/convert',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}
//                                 论坛回复相关内容
// 获取回复列表
export function getRepliesByRecordId(query) {
  return request({
    url: '/qaSystem/qaReply/list',
    method: 'get',
    params: query
  });
}

// 添加回复
export function addReply(data) {
  return request({
    url: '/qaSystem/qaReply',
    method: 'post',
    data: data
  });
}

// 获取全局回复楼层号
export function getReplyFloor(replyId) {
  return request({
    url: `/qaSystem/qaReply/floor/${replyId}`,
    method: 'get'
  });
}

// 获取全局回复楼层号的页码数
export function getReplyPage(query) {
  return request({
    url: `/qaSystem/qaReply/floor`,
    method: 'get',
    params: query
  });
}

// 点赞回复
export function thumbsUpReply(replyId) {
  return request({
    url: `/qaSystem/qaReply/thumbsUp/${replyId}`,
    method: 'post'
  });
}

// 删除论坛回复
export function delReply(replyId) {
  return request({
    url: '/qaSystem/qaReply/' + replyId,
    method: 'delete'
  })
}

//                                 获取下拉框键值对
// 获取人员信息下拉框内容（全部）
export function listUserOption() {
  return request({
    url: '/qaSystem/qaCommon/userOption',
    method: 'get'
  })
}
// 获取问题类型信息下拉框内容（全部）
export function listTypeOption() {
  return request({
    url: '/qaSystem/qaCommon/typeOption',
    method: 'get'
  })
}

// 获取问题类型信息下拉框内容（筛选占有位为0的）
export function listPartTypeOption() {
  return request({
    url: '/qaSystem/qaCommon/partTypeOption',
    method: 'get'
  })
}

// 获取密级信息下拉框内容
export function listSecurityOption(securityType) {
  return request({
    url: `/qaSystem/qaCommon/securityOption/${securityType}`,
    method: 'get'
  })
}

//                                 消息提醒服务
// 开启消息提醒
export function openNotify() {
  return request({
    url: `/qaSystem/qaCommon/openNotify`,
    method: 'post'
  });
}

// 关闭消息提醒
export function closeNotify() {
  return request({
    url: `/qaSystem/qaCommon/closeNotify`,
    method: 'post'
  });
}

// 将消息提醒位置1（提醒）
export function setNotify(recordId) {
  return request({
    url: `/qaSystem/qaCommon/setNotify/` + recordId,
    method: 'post'
  });
}

// 将消息提醒位置0（不提醒）
export function markNotified(recordId) {
  return request({
    url: `/qaSystem/qaCommon/markNotified/` + recordId,
    method: 'post'
  });
}


