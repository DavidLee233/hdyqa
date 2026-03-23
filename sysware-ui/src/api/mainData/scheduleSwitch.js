import request from '@/utils/request'

// 查询指定主数据类型的定时同步/定时备份开关状态
export function getMainDataScheduleSwitchStatus(dataType) {
  return request({
    url: '/mainData/scheduleSwitch/' + dataType,
    method: 'get'
  })
}

// 更新指定主数据类型的定时同步/定时备份开关状态
export function updateMainDataScheduleSwitch(data) {
  return request({
    url: '/mainData/scheduleSwitch/toggle',
    method: 'post',
    data
  })
}
