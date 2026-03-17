import { listAdminQa, listAdminNotify, listQa, expQa} from "@/api/qaSystem/qaAdmin";


export const qaCommonApi = (state) => {

  // 问题列表API
  const getList = async () => {
    state.loading = true
    try {
      const response = await listAdminQa(state.queryParams)
      state.qaList = response.rows
      state.total = response.total
    } catch (error) {
      console.error("获取问题列表失败", error)
    } finally {
      state.loading = false
    }
  }

  const getSearchList = async () => {
    state.admin_loading = true
    try {
      state.searchQueryParams.orderByColumn = state.orderByColumn
      state.searchQueryParams.isAsc = state.isAsc
      state.searchQueryParams.pageType = "admin"
      const response = await listQa(state.searchQueryParams)
      state.searchQaList = response.rows
      state.searchTotal = response.total
    } catch (error) {
      console.error("获取问题总表列表失败", error)
    } finally {
      state.admin_loading = false
    }
  }

  // 通知相关API
  /** 查询需提醒的问题回答列表 */
  const getNotifyList = async () => {
    try {
      const response = await listAdminNotify()
      state.notifyList = response.data
    } catch (error) {
      console.error("获取提醒数据失败", error)
    }
  }

  return {
    getList,
    getSearchList,
    getNotifyList
  }
}
