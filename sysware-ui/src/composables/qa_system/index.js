import { qaState } from './qaState'
import {qaCommonApi} from './qaCommonApi'
import { useQaTable } from './qaTable'
import { useQaDialog } from './qaDialog'
import { useQaForum } from './qaForum'
import { useQaUpload } from './qaUpload'
import { useQaUtils } from './qaUtils'

export const useQaSystem = () => {
  const state = qaState()
  const api = qaCommonApi(state)
  const table = useQaTable(state, api)
  const dialog = useQaDialog(state, api)
  const forum = useQaForum(state, api)
  const upload = useQaUpload(state, api)
  const utils = useQaUtils(state)

  return {
    // 状态
    ...state,

    // API相关
    ...api,

    // 表格相关
    ...table,

    // 对话框相关
    ...dialog,

    // 讨论区相关
    ...forum,

    // 上传相关
    ...upload,

    // 工具函数
    ...utils
  }
}
