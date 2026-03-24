<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px" class="search-form">
      <el-form-item label="姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="员工号" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入员工号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="key号码" prop="keyNumber">
        <el-input
          v-model="queryParams.keyNumber"
          placeholder="请输入key号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!-- 数据源切换按钮组 -->
      <el-col :span="2.5">
        <div class="data-source-switch">
          <el-button-group>
            <el-button
              :type="dataSource === 'local' ? 'primary' : 'default'"
              size="mini"
              @click="switchDataSource('local')"
              :title="dataSource === 'local' ? '当前查看本地数据' : '切换到本地数据'">
              <i class="el-icon-s-home"></i>
              本地数据
            </el-button>
            <el-button
              :type="dataSource === 'remote' ? 'primary' : 'default'"
              size="mini"
              @click="switchDataSource('remote')"
              :title="dataSource === 'remote' ? '当前查看院主数据' : '切换到院主数据'">
              <i class="el-icon-cloudy"></i>
              院主数据
            </el-button>
          </el-button-group>
          <div class="data-source-indicator">
            <span class="indicator-dot" :class="{ active: dataSource === 'local' }"></span>
            <span class="indicator-dot" :class="{ active: dataSource === 'remote' }"></span>
          </div>
        </div>
      </el-col>
      <!-- 只有本地数据时显示操作按钮 -->
      <template v-if="dataSource === 'local'">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
          >修改</el-button>
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
      </template>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          :loading="exportLoading"
        >导出</el-button>
      </el-col>
      <el-col v-if="dataSource === 'local'" :span="2.4">
        <div class="schedule-switch-box">
          <span class="schedule-switch-text">{{ backupSwitchLabel }}</span>
          <el-switch
            v-model="backupTaskEnabled"
            :loading="scheduleSwitchLoading"
            :active-color="'#13ce66'"
            :inactive-color="'#909399'"
            @change="handleBackupTaskSwitchChange"
          />
        </div>
      </el-col>
      <el-col v-if="dataSource === 'remote'" :span="1.5">
        <el-dropdown @command="handleForceSync">
          <el-button
            type="danger"
            plain
            icon="el-icon-refresh"
            size="mini"
            :loading="syncLoading"
          >
            强制同步<i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="full">全量同步</el-dropdown-item>
            <el-dropdown-item command="incremental">增量同步</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-col>
      <el-col v-if="dataSource === 'remote'" :span="2.4">
        <div class="schedule-switch-box">
          <span class="schedule-switch-text">{{ syncSwitchLabel }}</span>
          <el-switch
            v-model="syncTaskEnabled"
            :loading="scheduleSwitchLoading"
            :active-color="'#13ce66'"
            :inactive-color="'#909399'"
            @change="handleSyncTaskSwitchChange"
          />
        </div>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" :searchValue.sync="queryParams.searchValue" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 远程数据提示 -->
    <div v-if="dataSource === 'remote'" class="remote-tip">
      <el-alert
        title="院主数据（只读模式）"
        type="info"
        :closable="false"
        show-icon>
        <span>当前查看的是院主数据，数据来源于远程数据库(139.10.2.90)，此处为只读模式。</span>
      </el-alert>
      <div class="remote-sync-meta">最近同步：{{ getLatestSyncSummary() }}</div>
    </div>

    <el-table v-loading="loading" :data="personJobInfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center">
        <template slot-scope="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="姓名" align="center" prop="name" />
      <el-table-column label="专业" align="center" prop="postalCode" />
      <el-table-column label="key号" align="center" prop="keyNumber" />
      <el-table-column label="人员类别" align="center" prop="pkPsncl" />
      <el-table-column label="涉密级别" align="center" prop="secretLevel" />
      <el-table-column label="是否主职" align="center" prop="isMainJob" />
      <el-table-column v-if="dataSource === 'local'" label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >编辑</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>

      <!-- 远程数据时显示数据源标识 -->
      <el-table-column v-if="dataSource === 'remote'" label="数据来源" align="center" width="100">
        <template>
          <el-tag size="mini" type="info">远程</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      :page-sizes="[10, 20, 50, 100, 200]"
      @pagination="getList"
    />

    <!-- 添加或修改综合办物品出门流程对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="85%" append-to-body class="goodsOut-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px" label-position="right">
        <!-- 员工工作信息 -->
        <div class="form-section">
          <div class="section-header">
            <i class="el-icon-user"></i>
            <span>员工工作信息</span>
          </div>
          <el-row :gutter="30">
            <el-col :span="8">
              <!-- 以下三个选项均禁用选择 -->
              <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入姓名"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="员工号" prop="code">
                <el-input v-model="form.code" placeholder="请输入员工号"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="涉密级别" prop="secretLevel">
                <el-input v-model="form.secretLevel" placeholder="请输入涉密级别"/>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="30">
            <el-col :span="8">
              <!-- 以下三个选项均禁用选择 -->
              <el-form-item label="key号码" prop="keyNumber">
                <el-input v-model="form.keyNumber" placeholder="请输入key号码"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="证件号码" prop="idNumber">
                <el-input v-model="form.idNumber" placeholder="请输入证件号码"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="人员类别" prop="pkPsncl">
                <el-input v-model="form.pkPsncl" placeholder="请输入人员类别"/>
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
  listPersonJobInfo,
  getPersonJobInfo,
  delPersonJobInfo,
  addPersonJobInfo,
  updatePersonJobInfo,
  listRemotePersonJobInfo,
  forceSyncRemotePersonJobInfo
} from "@/api/mainData/personJobInfo";
import {getUser} from "@/api/qaSystem/qaCommon";
import {getFieldMappingByType} from "@/api/mainData/mainDataMapping";
import { listLatestMainDataSyncBatch } from "@/api/mainData/syncBatch";
import { getMainDataScheduleSwitchStatus, updateMainDataScheduleSwitch } from "@/api/mainData/scheduleSwitch";

export default {
  name: "PersonJobInfo",
  data() {
    return {
      userName: null,
      loginName: null,
      userId: null,
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 强制同步遮罩层
      syncLoading: false,
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
      // 左侧表格数据
      personJobInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示表单
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        searchValue: null,
        name: null,
        code: null,
        keyNumber: null
      },
      // 表单参数
      form: {
        pkPsnjob: null,
        createId: this.$store.state.user.id,
        createBy: null,
        createTime: null,
        updateId: null,
        updateBy: null,
        updateTime: null,
        name: null,
        idNumber: null,
        inOutNumber: null,
        realPkDept: null,
        keyNumber: null,
        code: null,
        pkDept: null,
        pkJob: null,
        beginDate: null,
        endDate: null,
        pkPsncl: null,
        pkPsndoc: null,
        secretLevel: null,
        lastFlag: null,
        endFlag: null,
        pkOrg: null,
        isMainJob: null,
        oldPkPsnjob: null,
        otherJobTitle: null,
        jobLevel: null,
      },
      // 表单校验
      rules: {
        name:[
          { required: true, message: '姓名不能为空', trigger: 'blur' },
        ],
        code:[
          { required: true, message: '员工号不能为空', trigger: 'blur' },
        ],
        secretLevel: [
          { required: true, message: '涉密等级不能为空', trigger: 'blur' }
        ],
        keyNumber: [
          { required: true, message: 'key号码不能为空', trigger: 'blur' }
        ],
        idNumber: [
          { required: true, message: '证件号码不能为空', trigger: 'blur' }
        ],
        pkPsncl: [
          { required: true, message: '人员类别不能为空', trigger: 'blur' }
        ],
      },

      // 数据源类型：local-本地，remote-远程
      dataSource: 'local',
      // 字段映射配置
      fieldMapping: [],
      // 最近同步批次
      latestSyncBatchList: [],
      scheduleSwitchLoading: false,
      backupTaskEnabled: false,
      syncTaskEnabled: false,
      dataTypeCode: '3',
      dataTypeName: '员工工作信息',
      backupSwitchLabel: '定时备份',
      syncSwitchLabel: '定时同步',
    };
  },

  created() {
    this.getCurrentUser();
    this.getList();
    this.loadFieldMapping();
    this.loadScheduleSwitchStatus();
  },

  methods: {
    /**                      初始化方法                     */
    /** 加载字段映射配置 */
    async loadFieldMapping() {
      try {
        // 员工工作信息映射类型：3（兼容后端旧值2）
        const response = await getFieldMappingByType({ type: '3' });  // 查询字段映射列表
        if (response.code === 200) {
          this.fieldMapping = response.data || [];
        }
      } catch (error) {
        console.error("加载字段映射失败", error);
        this.fieldMapping = [];
      }
    },

    loadScheduleSwitchStatus() {
      getMainDataScheduleSwitchStatus(this.dataTypeCode).then(response => {
        const data = response.data || {};
        this.backupTaskEnabled = typeof data.backupEnabled === 'boolean' ? data.backupEnabled : false;
        this.syncTaskEnabled = typeof data.syncEnabled === 'boolean' ? data.syncEnabled : false;
      }).catch(error => {
        console.error('加载主数据定时开关状态失败', error);
      });
    },

    updateScheduleSwitch(switchType, enabled) {
      return updateMainDataScheduleSwitch({
        dataType: this.dataTypeCode,
        switchType,
        enabled
      });
    },

    handleBackupTaskSwitchChange(value) {
      const previousValue = !value;
      this.scheduleSwitchLoading = true;
      this.updateScheduleSwitch('backup', value).then(() => {
        this.backupTaskEnabled = value;
        this.$message.success(`${this.dataTypeName}${value ? '定时备份已开启' : '定时备份已关闭'}`);
      }).catch(error => {
        console.error('切换定时备份开关失败', error);
        this.backupTaskEnabled = previousValue;
        this.$message.error(`${this.dataTypeName}定时备份开关切换失败`);
      }).finally(() => {
        this.scheduleSwitchLoading = false;
      });
    },

    handleSyncTaskSwitchChange(value) {
      const previousValue = !value;
      this.scheduleSwitchLoading = true;
      this.updateScheduleSwitch('sync', value).then(() => {
        this.syncTaskEnabled = value;
        this.$message.success(`${this.dataTypeName}${value ? '定时同步已开启' : '定时同步已关闭'}`);
      }).catch(error => {
        console.error('切换定时同步开关失败', error);
        this.syncTaskEnabled = previousValue;
        this.$message.error(`${this.dataTypeName}定时同步开关切换失败`);
      }).finally(() => {
        this.scheduleSwitchLoading = false;
      });
    },

    /** 将查询参数转换为远程格式 */
    convertToRemoteQuery(query) {
      // 新接口中，后端会处理字段映射，前端只需要传递原始参数
      const remoteQuery = {
        pageNum: query.pageNum,
        pageSize: query.pageSize,
      };

      // 添加查询条件
      if (query.name) remoteQuery.name = query.name;
      if (query.code) remoteQuery.code = query.code;
      if (query.keyNumber) remoteQuery.keyNumber = query.keyNumber;
      return remoteQuery;
    },

    /** 查询参数列表 */
    getList() {
      this.loading = true;
      let apiParams = { ...this.queryParams };

      // 如果是远程数据，需要转换查询参数
      if (this.dataSource === 'remote') {
        apiParams = this.convertToRemoteQuery(this.queryParams);
      }

      // 根据数据源选择API
      const apiPromise = this.dataSource === 'local' ?
        listPersonJobInfo(apiParams) :
        listRemotePersonJobInfo(apiParams);

      apiPromise.then(response => {
        if (response && typeof response.code !== 'undefined' && response.code !== 200) {
          this.personJobInfoList = [];
          this.total = 0;
          this.$message.error(response.msg || '查询数据失败');
          this.loading = false;
          return;
        }
        const data = Array.isArray(response.rows) ? response.rows : [];
        const total = Number(response.total) || 0;

        this.personJobInfoList = data;
        this.total = total;
        this.loading = false;
      }).catch(error =>{
        console.error("获取数据失败", error);
        this.$message.error('获取数据失败，请稍后重试');
        this.loading = false;
      });
    },

    /** 获取当前用户信息  */
    getCurrentUser(){
      getUser().then(response => {
        if(response.code === 200){
          this.userName = response.data.createBy;
          // 工号
          this.loginName = response.data.createId;
          // 用户唯一ID
          this.userId = response.data.userId;
        }else{
          this.$message.error(`无此用户，请联系管理员`);
        }
      }).catch(error => {
        console.error(`无此用户，请联系管理员`, error);
      });
    },

    /**                      按钮方法                     */
    /** 切换数据源 */
    switchDataSource(source) {
      if (this.dataSource === source) {
        // 已经是当前数据源，不执行切换
        return;
      }
      this.dataSource = source;
      // 根据切换方向显示不同的提示
      if (source === 'local') {
        this.$message.success('已切换到本地数据（可编辑模式）');
      } else {
        this.$message.info('已切换到院主数据（只读模式）');
      }
      // 清空选择
      this.ids = [];
      this.single = true;
      this.multiple = true;
      // 重置查询并重新加载数据
      this.resetQuery();
      if (source === 'remote') {
        this.loadLatestSyncBatch();
      } else {
        this.latestSyncBatchList = [];
      }
    },
    /**   表单重置   */
    reset() {
      this.form = {
        pkPsnjob: null,
        createId: this.$store.state.user.id,
        createBy: null,
        createTime: null,
        updateId: null,
        updateBy: null,
        updateTime: null,
        name: null,
        idNumber: null,
        inOutNumber: null,
        realPkDept: null,
        keyNumber: null,
        code: null,
        pkDept: null,
        pkJob: null,
        beginDate: null,
        endDate: null,
        pkPsncl: null,
        pkPsndoc: null,
        secretLevel: null,
        lastFlag: null,
        endFlag: null,
        pkOrg: null,
        isMainJob: null,
        oldPkPsnjob: null,
        otherJobTitle: null,
        jobLevel: null,
      };
      this.resetForm("form");
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    /** 重置按钮操作 */
    resetQuery() {
      // 直接重置整个 queryParams 对象
      this.queryParams = {
        pageNum: 1,
        pageSize: this.queryParams.pageSize, // 保留当前的分页大小
        searchValue: null,
        name: null,
        code: null,
        keyNumber: null
      };
      // 如果需要重置表单验证状态
      if (this.$refs.queryForm) {
        this.$refs.queryForm.resetFields();
      }
      this.handleQuery();
    },

    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.pkPsnjob)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },

    /** 新增按钮操作 */
    handleAdd() {
      // 远程数据不允许新增
      if (this.dataSource === 'remote') {
        this.$message.warning('院主数据为只读模式，无法新增数据');
        return;
      }
      this.reset();
      this.form.createBy = this.userName;
      this.form.createId = this.loginName;
      this.open = true;
      this.title = "主数据员工工作信息添加数据";
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      // 远程数据不允许修改
      if (this.dataSource === 'remote') {
        this.$message.warning('院主数据为只读模式，无法修改数据');
        return;
      }
      this.reset();
      const pkPsnId = row.pkPsnjob || this.ids
      getPersonJobInfo(pkPsnId).then(response => {
        // 已解决form传递值出现的问题
        this.form = response.data;
        this.open = true;
        this.title = "主数据员工工作信息修改数据";
      });
    },

    /** 问题表单提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const isUpdate = !!this.form.pkPsnjob;
          const operation = isUpdate ? updatePersonJobInfo(this.form) : addPersonJobInfo(this.form);
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

    /**   取消按钮   */
    cancel() {
      this.open = false;
      this.reset();
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      // 远程数据不允许删除
      if (this.dataSource === 'remote') {
        this.$message.warning('院主数据为只读模式，无法删除数据');
        return;
      }
      const pkPsnId = row.pkPsnjob || this.ids;
      this.$modal.confirm('是否确认删除编号为"' + pkPsnId + '"的数据项？').then(function() {
        return delPersonJobInfo(pkPsnId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    /** 导出按钮操作 */
    handleExport() {
      const confirmMessage = this.dataSource === 'local'
        ? '是否确认导出本地数据？'
        : '是否确认导出院主数据？';
      this.$confirm(confirmMessage, "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.exportLoading = true;
        let queryParams = { ...this.queryParams };
        let exportUrl = '/mainData/personJobInfo/export';
        // 如果是远程数据，需要转换查询参数
        if (this.dataSource === 'remote') {
          queryParams = this.convertToRemoteQuery(this.queryParams);
          exportUrl = '/mainData/personJobInfo/remote/export';
        }
        const filename = `员工工作信息数据_${this.dataSource === 'local' ? '本地' : '远程'}_${new Date().getTime()}.xlsx`;
        this.download(exportUrl, queryParams, filename)
          .finally(() => {
            this.exportLoading = false;
          });
      }).catch(() => {
        this.exportLoading = false;
      });
    },

    /** 强制同步按钮操作 */
    handleForceSync(syncMode) {
      const mode = syncMode || 'full';
      const syncModeLabel = mode === 'incremental' ? '增量同步' : '全量同步';
      this.$confirm(`是否确认${syncModeLabel}院主员工工作信息到本地？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.syncLoading = true;
        forceSyncRemotePersonJobInfo(mode).then(response => {
          const stats = response.data || {};
          if (this.isSyncSuccessResponse(response, stats)) {
            this.$message.success(`同步完成：拉取${stats.fetched || 0}，新增${stats.inserted || 0}，更新${stats.updated || 0}，失效${stats.invalidated || 0}，失败${stats.failed || 0}`);
            this.getList();
            this.loadLatestSyncBatch();
            return;
          }
          const failMsg = stats.message || response.msg || '强制同步失败';
          this.$message.error(failMsg);
          this.loadLatestSyncBatch();
        }).catch(error => {
          console.error("强制同步失败", error);
          this.$message.error('强制同步失败，请稍后重试');
        }).finally(() => {
          this.syncLoading = false;
        });
      }).catch(() => {});
    },

    isSyncSuccessResponse(response, stats) {
      if (!response || response.code !== 200) {
        return false;
      }
      if (!stats || typeof stats.success === 'undefined' || stats.success === null) {
        return true;
      }
      if (typeof stats.success === 'boolean') {
        return stats.success;
      }
      const normalized = String(stats.success).trim().toLowerCase();
      return normalized === '1' || normalized === 'true';
    },

    /** 加载最近同步批次 */
    loadLatestSyncBatch() {
      listLatestMainDataSyncBatch('3', 1).then(response => {
        if (response.code === 200) {
          this.latestSyncBatchList = response.data || [];
        }
      }).catch(error => {
        console.error("加载最近同步批次失败", error);
      });
    },

    getLatestSyncSummary() {
      if (!this.latestSyncBatchList || this.latestSyncBatchList.length === 0) {
        return '暂无记录';
      }
      const latest = this.latestSyncBatchList[0];
      const timeText = latest.startTime ? String(latest.startTime).replace('T', ' ') : '-';
      const resultText = latest.success === '1' ? '成功' : '失败';
      return `${timeText}（${resultText}，批次：${latest.batchNo || '-'}）`;
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

.goodsOut-dialog :deep(.el-dialog__body) {
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

.remote-tip {
  margin-bottom: 15px;
}

.remote-sync-meta {
  margin-top: 8px;
  color: #606266;
  font-size: 12px;
}

.schedule-switch-box {
  display: flex;
  align-items: center;
  height: 28px;
}

.schedule-switch-text {
  margin-right: 8px;
  color: #606266;
  font-size: 12px;
}
</style>
