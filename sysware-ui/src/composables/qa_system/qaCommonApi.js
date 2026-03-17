import { listAdminQa, listAdminNotify, listQa, expQa} from "@/api/qaSystem/qaAdmin";
import { addQa, updateQa, delQa, accQa} from "@/api/qaSystem/qaQuestion";
import { ansQa} from "@/api/qaSystem/qaAnswer";
import {refreshCache, openNotify, closeNotify, setNotify, markNotified, listSecurityOption,
        listTypeOption, getQa, updateVisits, getUser, updateRemoveRes, convertToPdf,
        getRepliesByRecordId, addReply, thumbsUpReply, delReply, getReplyFloor, getReplyPage} from "@/api/qaSystem/qaCommon";

export const qaCommonApi = (state) => {
  // 用户相关API
  const initUser = async () => {
    try {
      const response = await getUser();
      if(response.code === 200){
        state.userName = response.data.createBy;
        // 工号
        state.loginName = response.data.createId;
        // 用户唯一ID
        state.userId = response.data.userId;
        // 获取提醒消息状态
        state.openNotify = response.data.openNotify === 1;
      }
    } catch (error) {
      console.error('获取用户信息失败', error);
    }
  }

  /** 获取下拉问题类型栏数据 */
  const getTypeOptions = async () =>{
    try {
      const response = await listTypeOption();
      if(response.code === 200){
        state.typeOptions = response.data || [];
        // 排序显示在表单中
        state.typeOptions.sort((a, b) => a.sequence - b.sequence);
      }
    } catch (error) {
      console.error('获取问题类型失败', error);
    }
  }

  // 下拉问题类型选择变化
  const handleQuestionTypeChange = (val) => {
    state.question_form.type = val.type;
  }
  const handleAnswerTypeChange = (val) => {
    state.answer_form.type = val.type;
  }
  const handleAdminTypeChange = (val) => {
    state.admin_form.type = val.type;
  }

  /** 获取下拉密级栏数据 */
  const getSecurityOptions = async (securityType = "data") =>{
    try {
      // 只获取与数据相关的密级
      const response = await listSecurityOption(securityType);
      if(response.code === 200){
        state.securityOptions = response.data || [];
        // 排序显示在表单中
        state.securityOptions.sort((a, b) => a.sort - b.sort);
      }
    } catch (error) {
      console.error('获取密级信息失败', error);
    }
  }

  // 下拉密级选择变化
  const handleQuestionSecurityChange = (val) => {
    state.question_form.classification = val.securityName;
  }
  const handleAnswerSecurityChange = (val) => {
    state.answer_form.classification = val.securityName;
  }
  const handleAdminSecurityChange = (val) => {
    state.admin_form.classification = val.securityName;
  }

  /** 提醒流程 */
  const handleNotify = async (list) => {
    try {
      if(!list || !list.length) return
      // 1.拼接所有的note
      const content = `您有${list.length}条问题待处理，请尽快处理！`;
      // 2.收集ids
      const ids = list.map(qa => qa.recordId);
      // 3.右下角弹框
      state.$notify({
        title: '提醒',
        position: 'bottom-right',
        duration: 5000,
        dangerouslyUseHTMLString: false,
        offset: 24,
        showClose: true,
        onClose: async () => {
          // 4. 后端置0
          await markNotified(ids).then(response => {
            // 5. 本地置0
            list.forEach(qa => this.$set(qa, 'notify', 0))
          })
        },
        message: content,
      });
    } catch (error) {
      console.error("提醒消息服务出现故障", error)
    }
  }

  // 提醒对应运维人员或提问者处理相关操作
  const handleRemind = async (recordId) => {
    try {
      await setNotify(recordId)
    } catch (error) {
      console.error("设置问题通知失败", error)
    }
  }



  // 讨论区API
  const fetchReplies = async () => {
    state.replyLoading = true
    try {
      const response = await getRepliesByRecordId(state.queryReply)
      if (response.code === 200) {
        const startFloor = (state.queryReply.pageNum - 1) * state.queryReply.pageSize
        state.replies = (response.rows || []).map((reply, index) => ({
          ...reply,
          floorNumber: startFloor + index + 1
        }))
        state.replyTotal = response.total || 0

        state.replies.forEach((reply) => {
          state.floorCache[reply.replyId] = reply.floorNumber
        })
      }
    } finally {
      state.replyLoading = false
    }
  }

  return {
    initUser,
    fetchReplies,
  }
}
