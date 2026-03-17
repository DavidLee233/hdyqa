import { reactive, computed } from '@vue/composition-api'
import { getToken } from "@/utils/auth";

export const qaState = () => {
  const state = reactive({
    // 用户信息
    userName: null,
    loginName: null,
    userId: null,

    // 界面状态
    // 是否开启消息提醒
    openNotify: true,
    // 遮罩层
    loading: true,
    // 右侧表格遮罩层
    admin_loading: true,
    replyLoading: false,
    // 导出遮罩层
    exportLoading: false,
    // 显示搜索条件
    showSearch: true,
    // 转换状态标志
    isConverting: false,
    // 转换进度（可选）
    convertProgress: 0,
    // 转换状态信息
    convertMessage: '',
    // 上传限制参数
    limit: 3,
    // 图片预览(根据是否是表单选择是否打开关闭当前表单)
    isForm: true,
    // 关闭表单模式选择
    mode: '',
    showImageViewer: false,
    previewImageUrl: '',

    // 选择状态
    // 选中数组
    ids: [],
    // 选中admin数组
    admin_ids: [],
    // 非单个禁用
    single: true,
    // 非多个禁用
    multiple: true,
    // admin单个和多个禁用
    admin_single: true,
    admin_mulitple: true,

    // 数据
    // 左侧表格数据
    qaList: [],
    // 提醒列表
    notifyList: [],
    // 右侧表格独立数据
    searchQaList: [],
    // 回复列表
    replies: [],
    // 下拉密级数据源
    securityOptions: [],
    // 下拉问题类型数据源
    typeOptions: [],
    // 用于缓存楼层号
    floorCache: {},
    // 存储当前选中的问题
    selectedQuestion: {},

    // 分页和查询
    // 总条数
    total: 0,
    searchTotal: 0,
    replyTotal: 0,
    // 左侧表格查询参数
    queryParams: {
      pageNum: 1,
      pageSize: 10,
      searchValue: ""
    },
    // 右侧表格查询参数
    searchQueryParams: {
      pageNum: 1,
      pageSize: 16,
      searchValue: "",
      searchUser: "",
      pageType: "",
      orderByColumn: "",
      isAsc: "",
    },
    // 论坛回复参数
    queryReply: {
      pageNum: 1,
      pageSize: 10,
      searchValue: ""
    },

    // 排序和过滤
    orderByColumn: 'viewCount',
    isAsc: 'desc',
    // 筛选自己或全部（默认查看全部）
    filterSelf: false,

    // 表单数据
    question_form: {
      createBy: undefined,
      createId: undefined,
      telephone: undefined,
      roomNumber: undefined,
      title: undefined,
      classification: "非密",
      type: undefined,
      severity: "轻微",
      questionContent: undefined,
      questionAttachList: []
    },
    // 提问表单校验
    questionRules: {
      createBy: [
        { required: true, message: "参数名称不能为空", trigger: "blur" }
      ],
      createId: [
        { required: true, message: "参数键值不能为空", trigger: "blur" }
      ],
      telephone: [
        { required: true, message: "参数名称不能为空", trigger: "blur" }
      ],
      roomNumber: [
        { required: true, message: "参数键值不能为空", trigger: "blur" }
      ],
      title: [
        { required: true, message: "参数名称不能为空", trigger: "blur" }
      ],
      classification: [
        { required: true, message: "参数键值不能为空", trigger: "blur" }
      ],
      type: [
        { required: true, message: "参数键值不能为空", trigger: "blur" }
      ],
      severity: [
        { required: false, message: "参数名称可不填写", trigger: "blur" }
      ],
      questionContent: [
        { required: true, message: "参数键名不能为空", trigger: "blur" }
      ]
    },
    // 回答表单参数
    answer_form: {
      createBy: undefined,
      createId: undefined,
      telephone: undefined,
      roomNumber: undefined,
      title: undefined,
      classification: undefined,
      type: undefined,
      severity: undefined,
      questionContent: undefined,
      answerContent: undefined,
      canSolve: "可解决",
      answerAttachList: []
    },
    // 回答表单校验
    answerRules: {
      canSolve: [
        { required: true, message: "参数名称不能为空", trigger: "blur" }
      ],
      answerContent: [
        { required: true, message: "参数键名不能为空", trigger: "blur" }
      ]
    },
    // 管理员表单参数
    admin_form: {
      createBy: undefined,
      createId: undefined,
      department: undefined,
      telephone: undefined,
      roomNumber: undefined,
      status: undefined,
      classification: undefined,
      type: undefined,
      title: undefined,
      severity: undefined,
      questionContent: undefined,
      updateBy: undefined,
      updateId: undefined,
      answerContent: undefined,
      canSolve: undefined,
      questionAttachList: [],
      answerAttachList: [],
    },
    // 管理员表单校验
    adminRules: {
    },
    // 导出表单参数
    export_form: {
      dataRange: "最近一月",
      savePath: "D:\\adminLogs",
      saveMode: "标准模式",
    },
    // 导出表单校验
    exportRules: {
      dataRange: [
        { required: true, message: "请选择时间范围", trigger: "blur" }
      ],
      savePath: [
        { required: true, message: "请输入保存目录", trigger: "blur" }
      ],
      saveMode: [
        { required: true, message: "请选择保存模式", trigger: "blur" }
      ],
    },

    // 对话框状态
    // 问题弹出层标题
    question_title: "",
    // 回答弹出层标题
    answer_title: "",
    // 管理员弹出层标题
    admin_title: "",
    // 是否显示问题表单
    question_open: false,
    // 是否显示回答表单
    answer_open: false,
    // 是否显示管理员表单
    admin_open: false,
    // 是否显示导出表单
    export_open: false,
    // 正在回复的楼层
    replyingTo: null,
    // 输入框内容
    replyInputContent: '',
    // 实际发送的内容
    actualContent: '',
  })

  // 计算属性
  const uploadFileUrl = computed(() => process.env.VUE_APP_BASE_API + "/system/oss/upload")
  const headers = computed(() => ({
    Authorization: "Bearer " + getToken(),
  }))

  return {
    ...state,
    uploadFileUrl,
    headers
  }
}
