<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-row :gutter="10">
        <el-col :span="4">
          <el-form-item label="模块标题" prop="title">
            <el-input
              v-model="queryParams.title"
              placeholder="请输入模块标题"
              clearable
              style="width: 240px;"
              size="small"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="操作人员" prop="operName">
            <el-input
              v-model="queryParams.operName"
              placeholder="请输入操作人员"
              clearable
              style="width: 240px;"
              size="small"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="业务类型" prop="businessType">
            <el-select
              v-model="queryParams.businessType"
              placeholder="操作类型"
              clearable
              size="small"
              style="width: 240px"
            >
              <el-option
                v-for="dict in typeOptions"
                :key="dict.dictValue"
                :label="dict.dictLabel"
                :value="dict.dictValue"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="状态" prop="status">
            <el-select
              v-model="queryParams.status"
              placeholder="操作状态"
              clearable
              size="small"
              style="width: 240px"
            >
              <el-option
                v-for="dict in statusOptions"
                :key="dict.dictValue"
                :label="dict.dictLabel"
                :value="dict.dictValue"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="操作时间">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              size="small"
              value-format="yyyy-MM-dd"
              format="yyyy-MM-dd"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="2">
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >添加</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
        >编辑</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete-solid"
          size="mini"
          @click="handleClean"
        >清空</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          :loading="exportLoading"
          @click="openExportDialog"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" :searchValue.sync="queryParams.searchValue" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 导出对话框 -->
    <el-dialog
      title="导出数据"
      :visible.sync="exportDialogVisible"
      width="500px"
      append-to-body
    >
      <el-form ref="exportForm" :model="exportQueryParams" label-width="100px">
        <el-form-item label="导出时间范围" prop="dateRange">
          <el-date-picker
            v-model="exportDateRange"
            type="daterange"
            value-format="yyyy-MM-dd"
            format="yyyy-MM-dd"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
            @change="handleExportDateChange"
          >
          </el-date-picker>
        </el-form-item>

        <!-- 可选：添加快速选择按钮 -->
        <el-form-item label="快捷选择">
          <el-button-group>
            <el-button size="mini" @click="quickSelectExportTime('week')">近一周</el-button>
            <el-button size="mini" @click="quickSelectExportTime('month')">近一月</el-button>
            <el-button size="mini" @click="quickSelectExportTime('quarter')">近三月</el-button>
            <el-button size="mini" @click="quickSelectExportTime('year')">近一年</el-button>
            <el-button size="mini" @click="quickSelectExportTime('all')">全部</el-button>
          </el-button-group>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCancelExport">取消</el-button>
        <el-button type="primary" @click="confirmExport">确定导出</el-button>
      </div>
    </el-dialog>

    <el-table height="69.5vh" ref="tables" v-loading="loading" :data="list" @selection-change="handleSelectionChange"
              :default-sort="defaultSort" @sort-change="handleSortChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center">
        <template slot-scope="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="模块标题" align="center" prop="title" />
      <el-table-column label="操作类型" align="center" prop="businessType" :formatter="typeFormat" />
      <el-table-column label="操作人员" align="center" prop="operName" width="100" :show-overflow-tooltip="true" sortable="custom" :sort-orders="['desc', 'asc']" />
      <el-table-column label="操作地址" align="center" prop="operIp" width="130" :show-overflow-tooltip="true" />
      <el-table-column label="操作地点" align="center" prop="operLocation" :show-overflow-tooltip="true" />
      <el-table-column label="操作状态" align="center" prop="status" :formatter="statusFormat" />
      <el-table-column label="操作日期" align="center" prop="operTime" sortable="custom" :sort-orders="['desc', 'asc']" width="180">
        <template slot-scope="scope">
          <span>{{timeParser(scope.row.operTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row,scope.index)"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      class="pagination-wrapper"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      :page-sizes="[15, 30, 50, 100]"
      @pagination="getList"
    />

    <!-- 操作日志详情 -->
    <el-dialog title="操作日志详细" :visible.sync="view_open" width="700px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px" size="mini">
        <el-row>
          <el-col :span="12">
            <el-form-item label="模块标题：">{{ form.title }} / {{ typeValueToLabel(form.businessType) }}</el-form-item>
            <el-form-item label="登录信息：">{{ form.operName }} / {{ form.operIp }} / {{ form.operLocation }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求地址：">{{ form.operUrl }}</el-form-item>
            <el-form-item label="请求方式：">{{ form.requestMethod }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作方法：">{{ form.method }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="请求参数：">{{ form.operParam }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回参数：">{{ form.jsonResult }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="错误消息：">{{ form.errorMsg }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作状态：">
              <div v-if="form.status === 0">正常</div>
              <div v-else-if="form.status === 1">失败</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作时间：">{{ timeParser(form.operTime) }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="异常信息：" v-if="form.status === 1">{{ form.errorMsg }}</el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="view_open = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改操作日志 -->
    <el-dialog :title="title" :visible.sync="open" width="85%" append-to-body class="operLog-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="operLog-form"  label-position="right">
        <!-- 操作信息 -->
        <div class="form-section">
          <div class="section-header">
            <i class="el-icon-user"></i>
            <span>操作信息</span>
          </div>
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="模块标题" prop="title">
                <el-input v-model="form.title" class="custom-input"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="操作类型" prop="businessType">
                <el-select
                  v-model="form.businessType"
                  placeholder="请选择操作类型"
                  style="width: 100%">
                  <el-option
                    v-for="item in typeOptions"
                    :key="item.dictValue"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 登录信息 -->
        <div class="form-section">
          <div class="section-header">
            <i class="el-icon-user"></i>
            <span>登录信息</span>
          </div>
          <el-row :gutter="30">
            <el-col :span="8">
              <el-form-item label="操作人员" prop="operName">
                <el-input v-model="form.operName" class="custom-input"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="操作人员工号" prop="operUserId">
                <el-input v-model="form.operUserId" class="custom-input"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="主机地址" prop="operIp">
                <el-input v-model="form.operIp" class="custom-input"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="30">
            <el-col :span="8">
              <el-form-item label="操作时间" prop="operTime">
                <el-date-picker clearable
                                v-model="form.operTime"
                                type="datetime"
                                format="yyyy-MM-dd HH:mm:ss"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                placeholder="请选择操作时间"
                                style="width: 100%">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="操作地点" prop="operLocation">
                <el-input v-model="form.operLocation" class="custom-input"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="操作状态" prop="status">
                <el-select
                  v-model="form.status"
                  placeholder="请选择操作状态"
                  style="width: 100%">
                  <el-option
                    v-for="item in statusOptions"
                    :key="item.dictValue"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 请求信息 -->
        <div class="form-section">
          <div class="section-header">
            <i class="el-icon-user"></i>
            <span>请求信息</span>
          </div>
          <el-row :gutter="30">
            <el-col :span="8">
              <el-form-item label="请求地址" prop="operUrl">
                <el-input v-model="form.operUrl" class="custom-input"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="请求方式" prop="requestMethod">
                <el-input v-model="form.requestMethod" class="custom-input"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="操作方法" prop="method">
                <el-input v-model="form.method" class="custom-input"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="30">
            <el-col :span="8">
              <el-form-item label="请求参数" prop="operParam">
                <el-input v-model="form.operParam" class="custom-input"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="返回参数" prop="jsonResult">
                <el-input v-model="form.jsonResult" class="custom-input"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="错误消息" prop="errorMsg">
                <el-input v-model="form.errorMsg" class="custom-input"/>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addOperlog,
  cleanOperlog,
  delOperlog,
  exportOperlog,
  getUser,
  getOperlog,
  listOperlog, updateOperlog
} from "@/api/monitor/operlog";

export default {
  name: "OperLog",
  data() {
    return {
      userName: null,
      loginName: null,
      userId: null,
      deptName: null,
      securityId: null,
      securityName: null,
      securityValue: null,
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 是否显示弹出层
      view_open: false,
      open: false,
      // 弹出层标题
      title: "",
      // 类型数据字典
      typeOptions: [],
      statusOptions: [],

      // 日期范围
      dateRange: [],
      // 默认排序
      defaultSort: {prop: 'operTime', order: 'desc'},

      // 导出相关
      exportDialogVisible: false,  // 导出对话框显示控制
      exportDateRange: [],         // 导出时间范围
      exportType: '',              // 记录导出的类型
      exportQueryParams: {         // 导出专用参数
        beginTime: '',
        endTime: ''
      },

      // 表单参数
      form: {
        operId: null,
        title: null,
        businessType: null,
        method: null,
        requestMethod: null,
        operatorType: null,
        operName: null,
        deptName: null,
        operUrl: null,
        operIp: null,
        operLocation: null,
        operParam: null,
        jsonResult: null,
        status: null,
        errorMsg: null,
        operTime: null,
        operUserId: null,
        operUserSecurityId: null,
        operUserSecurityName: null,
        operUserSecurityValue: null,
        operObjectId: null,
        operObjectName: null,
        operObjectSecurityName: null,
        operObjectSecurityId: null,
        operObjectSecurityValue: null,
        operObjectTableName: null,
      },
      // 表单校验
      rules: {
        title:[
          { required: true, message: '模块标题不能为空', trigger: 'blur' },
        ],
        businessType:[
          { required: true, message: '操作类型不能为空', trigger: 'change' },
        ],
        operName:[
          { required: true, message: '操作人姓名不能为空', trigger: 'blur' },
        ],
        operIp: [
          { required: true, message: '操作人IP不能为空', trigger: 'blur' }
        ],
        operLocation: [
          { required: true, message: '操作人地址不能为空', trigger: 'blur' }
        ],
        operUrl: [
          { required: true, message: '请求地址不能为空', trigger: 'blur' }
        ],
        requestMethod: [
          { required: true, message: '请求方式不能为空', trigger: 'blur' }
        ],
        method: [
          { required: true, message: '操作方法不能为空', trigger: 'blur' }
        ],
        operParam: [
          { required: true, message: '请求参数不能为空', trigger: 'blur' }
        ],
        operTime: [
          { required: true, message: '请选择操作时间', trigger: 'change' }
        ],
        status: [
          { required: true, message: '操作状态能为空', trigger: 'blur' }
        ],
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 15,
        orderByColumn: null,
        isAsc: null,
        title: null,
        operName: null,
        businessType: null,
        status: null
      },
    };
  },

  created() {
    this.getCurrentUser();
    this.getList();
    this.getDicts("sys_oper_type").then(response => {
      this.typeOptions = response.data;
    });
    this.getDicts("sys_oper_status").then(response => {
      this.statusOptions = response.data;
    });
  },

  // 监听对话框关闭
  watch: {
    exportDialogVisible(val) {
      if (!val) {
        // 对话框关闭时，重置表单
        if (this.$refs.exportForm) {
          this.$refs.exportForm.resetFields();
        }
        this.exportDateRange = [];
        this.exportQueryParams = {
          beginTime: '',
          endTime: ''
        };
      }
    }
  },

  methods: {
    /**                      初始化方法                     */
    /** 查询登录日志 */
    getList() {
      this.loading = true;
      listOperlog(this.handleListParams(this.queryParams, this.dateRange)).then( response => {
          this.list = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },

    /** 处理查询参数（加入日期）  */
    handleListParams(params, range) {
      // 使用日期专用参数
      const listParams = {
        ...params,  // 保留其他查询条件
      };
      if (range && range.length === 2) {
        listParams.beginTime = range[0];
        listParams.endTime = range[1];
      } else if(range && range.length === 1){
        listParams.beginTime = range[0];
        listParams.endTime = '';
      } else {
        listParams.beginTime = '';
        listParams.endTime = '';
      }
      return listParams;
    },

    /** 获取当前用户信息  */
    getCurrentUser(){
      getUser().then(response => {
        if(response.code === 200){
          this.userName = response.data.userName;
          // 工号
          this.loginName = response.data.loginName;
          // 用户唯一ID
          this.userId = response.data.userId;
          // 用户部门
          this.deptName = response.data.deptName;
          // 用户密级ID
          this.securityId = response.data.securityId;
          // 用户密级名称
          this.securityName = response.data.securityName;
          // 用户密级值
          this.securityValue = response.data.securityValue;
        }else{
          this.$message.error(`无此用户，请联系管理员`);
        }
      }).catch(error => {
        console.error(`无此用户，请联系管理员`, error);
      });
    },

    /**   表单重置   */
    reset() {
      this.form = {
        operId: null,
        title: null,
        businessType: null,
        method: null,
        requestMethod: null,
        operatorType: null,
        operName: null,
        deptName: null,
        operUrl: null,
        operIp: null,
        operLocation: null,
        operParam: null,
        jsonResult: null,
        status: null,
        errorMsg: null,
        operTime: null,
        operUserId: null,
        operUserSecurityId: null,
        operUserSecurityName: null,
        operUserSecurityValue: null,
        operObjectId: null,
        operObjectName: null,
        operObjectSecurityName: null,
        operObjectSecurityId: null,
        operObjectSecurityValue: null,
        operObjectTableName: null,
      };
      this.resetForm("form");
    },

    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.operId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 排序触发事件 */
    handleSortChange(column, prop, order) {
      this.queryParams.orderByColumn = column.prop;
      this.queryParams.isAsc = column.order;
      this.getList();
    },

    /**                      字段转换方法                     */
    /** 操作日志状态字典翻译 */
    statusFormat(row, column) {
      return this.selectDictLabel(this.statusOptions, row.status);
    },
    /** 操作日志类型字典翻译 */
    typeFormat(row, column) {
      return this.selectDictLabel(this.typeOptions, row.businessType);
    },
    /** 操作日志类型字典转换 */
    statusValueToLabel(value) {
      return this.selectDictLabel(this.statusOptions, value);
    },
    /** 操作日志类型字典转换 */
    typeValueToLabel(value) {
      return this.selectDictLabel(this.typeOptions, value);
    },
    /** 值转换为标签 */
    selectDictLabel(dict, value){
      const item = (dict || []).find(d => d.dictValue === String(value));
      return item ? item.dictLabel : '';
    },
    /** 时间格式转换 */
    timeParser(time){
      return this.parseTime(time);
    },

    /**                      按钮方法                     */
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      // 直接重置整个 queryParams 对象
      this.queryParams = {
        pageNum: 1,
        pageSize: this.queryParams.pageSize, // 保留当前的分页大小
        orderByColumn: null,
        isAsc: null,
        title: null,
        operName: null,
        businessType: null,
        status: null
      };
      // 如果需要重置表单验证状态
      if (this.$refs.queryForm) {
        this.$refs.queryForm.resetFields();
      }
      this.$refs.tables.sort(this.defaultSort.prop, this.defaultSort.order)
      this.handleQuery();
    },

    /** 详细按钮操作 */
    handleView(row) {
      this.view_open = true;
      this.form = row;
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const operIds = row.operId || this.ids;
      this.$confirm('是否确认删除日志编号为"' + operIds + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return delOperlog(operIds);
      }).then(() => {
        this.getList();
        this.msgSuccess("删除成功");
      }).catch(() => {});
    },

    /** 清空按钮操作 */
    handleClean() {
      this.$confirm('是否确认清空所有操作日志数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return cleanOperlog();
      }).then(() => {
        this.getList();
        this.msgSuccess("清空成功");
      }).catch(() => {});
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.form.operName = this.userName;
      this.form.operUserId = this.loginName;
      this.form.deptName = this.deptName;
      this.form.operUserSecurityId = this.securityId;
      this.form.operUserSecurityName = this.securityName;
      this.form.operUserSecurityValue = this.securityValue;
      this.open = true;
      this.title = "添加操作日志";
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const operIds = row.operId || this.ids;
      getOperlog(operIds).then(response => {
        const formData = response.data;
        // 关键：确保字典字段是字符串类型（因为 el-select 需要字符串）
        if (formData.businessType !== undefined && formData.businessType !== null) {
          formData.businessType = String(formData.businessType);
        }
        if (formData.status !== undefined && formData.status !== null) {
          formData.status = String(formData.status);
        }
        formData.deptName = this.deptName;
        this.form = formData;
        this.open = true;
        this.title = "修改操作日志";
      });
    },

    /** 表单提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 创建提交数据副本
          const submitData = { ...this.form };
          // 关键：将字符串类型的字典值转换回后端需要的类型
          // 如果后端需要数字，就转数字；如果后端可以接受字符串，就不需要转换
          if (submitData.businessType !== undefined && submitData.businessType !== null) {
            submitData.businessType = Number(submitData.businessType); // 如果需要数字
          }
          if (submitData.status !== undefined && submitData.status !== null) {
            submitData.status = Number(submitData.status); // 如果需要数字
          }
          const isUpdate = !!this.form.operId;
          const operation = isUpdate ? updateOperlog(submitData) : addOperlog(submitData);
          operation.then(response => {
            if(response.code === 200){
              this.$message.success(`${isUpdate ? '修改' : '新增'}成功`);
              this.open = false;
              this.getList();
            }else{
              this.$message.error(`${isUpdate ? '修改' : '新增'}失败` || response.msg);
            }
          }).catch(error => {
            console.error(`${isUpdate ? '修改' : '新增'}失败`, error);
          });
        }
      });
    },

    /**   表单取消按钮   */
    cancel() {
      this.open = false;
      this.reset();
    },

    /** 导出按钮操作 */
    handleExport() {
      // 日期格式转换
      const date = this.ExportFormatExchange(new Date());
      // 使用导出专用参数
      const exportParams = {
        ...this.queryParams,  // 保留其他查询条件
      };

      // 只有在选择具体日期范围时才添加时间参数
      if (this.exportQueryParams.beginTime && this.exportQueryParams.endTime) {
        exportParams.beginTime = this.exportQueryParams.beginTime;
        exportParams.endTime = this.exportQueryParams.endTime;
      }

      // 获取导出类型
      const typeName = this.getExportTypeName(this.exportType);

      // 生成文件名 - 根据类型生成
      let fileName;
      if (typeName === '自定义范围') {
        fileName = `操作日志_自定义范围_${date}.xlsx`;
      } else if (typeName) {
        fileName = `操作日志_${typeName}_${date}.xlsx`;
      } else {
        fileName = `操作日志_${date}.xlsx`;
      }
      this.download('monitor/operlog/export', exportParams, fileName);
    },

    /** 导出相关操作 */
    // 打开导出对话框
    openExportDialog() {
      // 先清空之前的设置
      this.exportDateRange = [];
      this.exportQueryParams = {
        beginTime: '',
        endTime: ''
      };
      // 再显示
      this.exportDialogVisible = true;
      // 设置默认值为全部
      this.exportType = 'all';
      // 生成全部日期范围
      const allDateRange = this.generateAllDateRange();
      // 设置日期范围
      this.exportDateRange = allDateRange.dateRange;
      this.exportQueryParams.beginTime = allDateRange.beginTime;
      this.exportQueryParams.endTime = allDateRange.endTime;
    },

    // 生成全部日期范围
    generateAllDateRange() {
      // 设置开始日期为2026年1月1日
      const beginTime = '2026-01-01';
      // 设置结束日期为2099年12月31日
      const endTime = '2099-12-31';
      return {
        beginTime,
        endTime,
        dateRange: [beginTime, endTime]
      };
    },

    // 处理导出日期范围变化
    handleExportDateChange(range) {
      if (range && range.length === 2) {
        this.exportQueryParams.beginTime = range[0];
        this.exportQueryParams.endTime = range[1];
        this.exportType = 'custom'; // 标记为自定义范围
      } else {
        this.exportQueryParams.beginTime = '';
        this.exportQueryParams.endTime = '';
        this.exportType = '';
      }
    },

    // 快捷选择导出时间
    quickSelectExportTime(type) {
      this.exportType = type;
      if (type === 'all') {
        // 生成全部日期范围
        const allDateRange = this.generateAllDateRange();
        this.exportDateRange = allDateRange.dateRange;
        this.exportQueryParams.beginTime = allDateRange.beginTime;
        this.exportQueryParams.endTime = allDateRange.endTime;
        return;
      }
      const end = new Date();
      const start = new Date();
      switch(type) {
        case 'week':
          start.setDate(start.getDate() - 7);
          break;
        case 'month':
          start.setMonth(start.getMonth() - 1);
          break;
        case 'quarter':
          start.setMonth(start.getMonth() - 3);
          break;
        case 'year':
          start.setFullYear(start.getFullYear() - 1);
          break;
      }
      // 格式化日期
      const formatDate = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      };
      this.exportDateRange = [formatDate(start), formatDate(end)];
      this.exportQueryParams.beginTime = this.exportDateRange[0];
      this.exportQueryParams.endTime = this.exportDateRange[1];
    },

    // 确认导出
    confirmExport() {
      // 导出全部时间无需校验
      if (this.exportType !== 'all'){
        // 验证时间范围
        const validation = this.validateExportDate();
        if (validation !== true) {
          this.$message.warning(validation);
          return;
        }
      }
      // 执行导出
      this.handleExport();
      // 关闭对话框
      this.exportDialogVisible = false;
    },

    // 取消导出
    handleCancelExport() {
      // 关闭对话框
      this.exportDialogVisible = false;
      // 显示提示信息
      this.$message({
        type: 'info',
        message: '已取消导出'
      });
    },

    // 验证导出时间范围
    validateExportDate() {
      if (!this.exportQueryParams.beginTime || !this.exportQueryParams.endTime) {
        return '请选择完整的时间范围';
      }
      const beginDate = new Date(this.exportQueryParams.beginTime);
      const endDate = new Date(this.exportQueryParams.endTime);
      // 检查时间范围是否超过一年（可选限制）
      const oneYearLater = new Date(beginDate);
      oneYearLater.setFullYear(oneYearLater.getFullYear() + 1);
      // 检查开始时间是否大于结束时间
      if (beginDate > endDate) {
        return '开始时间不能大于结束时间';
      }
      return true;
    },

    // 获取类型中文名称
    getExportTypeName(type) {
      const typeMap = {
        'week': '近一周',
        'month': '近一月',
        'quarter': '近三月',
        'year': '近一年',
        'all': '全部日期',
        'custom': '自定义范围'
      };
      return typeMap[type] || '';
    },

    // 日期转换为yyyy_MM_dd_HH_mm_ss
    ExportFormatExchange(date){
      const pad = (num) => String(num).padStart(2, '0');
      const year = date.getFullYear();
      const month = pad(date.getMonth() + 1);
      const day = pad(date.getDate());
      const hours = pad(date.getHours());
      const minutes = pad(date.getMinutes());
      const seconds = pad(date.getSeconds());
      return `${year}年${month}月${day}日`;
    },
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.el-table-column--selection .cell {
  text-align: center !important;
}

.operLog-dialog :deep(.el-dialog__body) {
  padding: 20px;
  margin-bottom: 0;
}

.form-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #ebeef5;
}

.section-header i {
  font-size: 20px;
  color: #409EFF;
  margin-right: 8px;
}

.section-header span {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.search-form {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.search-input {
  width: 220px;
}

.borrow-dialog :deep(.el-dialog__body) {
  padding: 20px 30px;
}

.form-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #ebeef5;
}

.section-header i {
  font-size: 20px;
  color: #409EFF;
  margin-right: 8px;
}

.section-header span {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.custom-input :deep(.el-input__inner) {
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
}

.custom-input :deep(.el-input__inner:hover),
.custom-input :deep(.el-input__inner:focus) {
  border-color: #409EFF;
}

.custom-date-picker {
  width: 100%;
}

.custom-textarea :deep(.el-textarea__inner) {
  border-radius: 4px;
}

.dialog-footer {
  text-align: right;
  padding-top: 0px;
}

@media screen and (max-width: 768px) {
  .form-section {
    padding: 15px;
  }

  .section-header {
    margin-bottom: 15px;
  }
}

.section-actions {
  margin-left: auto;
  display: flex;
  gap: 10px;
}

.upload-tip {
  color: #C03639;
  font-size: 12px;
  margin-top: 8px;
}

.selected-table-section {
  margin-top: 20px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.selected-table-section .section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #ebeef5;
}

.selected-table-section .section-header i {
  font-size: 20px;
  color: #67C23A;
  margin-right: 8px;
}

.selected-table-section .section-header span {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 在对应的CSS中添加 */
.pagination-wrapper {
  text-align: right;
  margin-top: 3px;
  margin-bottom: 3px;
}

</style>
