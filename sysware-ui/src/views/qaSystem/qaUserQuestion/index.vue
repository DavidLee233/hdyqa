<template>
    <div class="app-container">
      <el-row style="display: flex; height: 130vh; overflow: hidden;">
        <el-col style="flex: 0 0 38.6%; height: 100%; padding-right: 0px; overflow-y: auto;">
          <el-row :gutter="0" style="margin-bottom: 0px; height: 120vh; overflow: hidden;">
            <!-- 问题类型按钮 -->
            <el-row :gutter="10" class="mb8" :style="{height: '2vh',  marginTop: '0px'}">
              <el-col :span="1.5">
                <el-button type="primary" plain icon="el-icon-plus" size="mini" :disabled="!multiple" @click="handleTypeAdd">新增</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleTypeUpdate">修改</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleTypeDelete">删除</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="danger" plain icon="el-icon-refresh" size="mini" @click="handleRefreshCache">刷新缓存</el-button>
              </el-col>

              <right-toolbar :typeSearch.sync="typeSearch" :searchValue.sync="queryParams.searchValue" @queryTable="getTypeList"></right-toolbar>
            </el-row>
            <!-- 左侧问题类型表格 -->
            <el-table height="82.5vh" stripe border v-loading="loading" :data="typeList" @selection-change="handleSelectionChange" 
                      class="custom-bordered-table" style="width: 100%">
              <el-table-column type="selection" width="40" align="center"/>
              <el-table-column label="序号" prop="sequence" width="200" align="center"/>
              <el-table-column label="问题类型" prop="type" width="230" align="center"/>
              <el-table-column label="是否占用" prop="occupied" width="230" align="center">
                <template #default="{row}">
                  <span :style="{color: row.occupied ? '#F56C6C' : '#67C23A'}">
                    {{row.occupied ? '已占用' : '未占用'}}
                  </span>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              v-show="total >= 0"
              class="pagination-wrapper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page="queryParams.pageNum"
              :page-size="queryParams.pageSize"
              :page-sizes="[20, 40, 100, 200]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total">
            </el-pagination>
          </el-row>
        </el-col>
        
        <!-- 分割线 -->
        <div style="width: 1px; background-color: #dcdfe6; margin: 0 5px;"></div>

        <el-col style="flex: 0 0 51.2%; height: 100%; overflow-y: auto;">
          <!-- 问题类型及角色对应按钮 -->
          <el-row :gutter="10" class="mb8" :style="{height: '2vh',  marginTop: '0px'}">
            <el-col :span="1.5">
              <el-button type="primary" plain icon="el-icon-plus" size="mini" :disabled="!typeUser_multiple" @click="handleTypeUserAdd">新增</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="typeUser_single" @click="handleTypeUserUpdate">修改</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="typeUser_multiple" @click="handleTypeUserDelete">删除</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="danger" plain icon="el-icon-refresh" size="mini" @click="handleRefreshCache">刷新缓存</el-button>
            </el-col>

            <right-toolbar :typeUserSearch.sync="typeUserSearch" :searchValue.sync="searchQueryParams.searchValue" @queryTable="getTypeUserList"></right-toolbar>
          </el-row>
          <!-- 右侧问题类型及角色对应表格 -->
          <el-table height="82.5vh" stripe border v-loading="typeUser_loading" :data="typeUserList" @selection-change="handleUserTypeSelectionChange"
                    class="custom-bordered-table" style="width: 100%">
            <el-table-column type="selection" width="40" align="center" />
            <el-table-column label="序号" type="index" width="200" align="center">
              <template slot-scope="scope">{{(searchQueryParams.pageNum - 1) * searchQueryParams.pageSize + scope.$index + 1}}</template>
            </el-table-column>
            <el-table-column label="运维者姓名" width="230" align="center" prop="userName"/>
            <el-table-column label="运维者工号" width="230" align="center" prop="loginName"/>
            <el-table-column label="运维问题类型" width="230" align="center" prop="type"/>
          </el-table>

          <el-pagination
            v-show="searchTotal >= 0"
            class="pagination-wrapper"
            @size-change="handleSearchSizeChange"
            @current-change="handleSearchCurrentChange"
            :current-page="searchQueryParams.pageNum"
            :page-size="searchQueryParams.pageSize"
            :page-sizes="[20, 40, 100, 200]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="searchTotal">
          </el-pagination>
        </el-col>
      </el-row>

      <!-- 问题类型创建和修改表单 -->
      <el-dialog :title="type_title" :visible.sync="type_open" width="500px" v-dialogDrag append-to-body>
        <el-form ref="type_form" :model="type_form" :rules="typeRules" label-width="80px">
          <el-row :gutter="10">
            <el-col :span="16">
              <el-form-item label="问题类型" prop="type">
                <el-input v-model="type_form.type" :disabled="false"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="问题排序" prop="sequence">
                <el-input v-model="type_form.sequence" :disabled="false"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="是否占用" prop="occupied">
            <el-radio-group v-model="type_form.occupied" :disabled="false">
              <el-radio :label="1">已占用</el-radio>
              <el-radio :label="0">未占用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitTypeForm">确 定</el-button>
          <el-button @click="cancelTypeForm">取 消</el-button>
        </div>
      </el-dialog>

      <!-- 问题类型角色映射创建和修改表单 -->
      <el-dialog :title="typeUser_title" :visible.sync="typeUser_open" width="500px" v-dialogDrag append-to-body>
        <el-form ref="typeUser_form" :model="typeUser_form" :rules="typeUserRules" label-width="80px">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="问题类型" prop="type">
                <el-select v-model="typeUser_form.type" placeholder="请选择问题类型" value-key="typeId" @change="handleTypeChange" filterable :disabled="false" style="width: 60%;">
                  <el-option v-for="item in typeOptions" :key="item.typeId" :label="item.type" :value="item"/>
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="10">
              <el-form-item label="运维者姓名" prop="userName" label-width="100px">
                <el-select v-model="typeUser_form.userName" placeholder="请选择人员" value-key="userId" @change="handleUserChange" filterable :disabled="false" style="width: 100px;">
                  <el-option v-for="item in userOptions" :key="item.userId" :label="item.userName" :value="item"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="14">
              <el-form-item label="运维者工号" prop="loginName" label-width="100px">
                <el-input v-model="typeUser_form.loginName" :disabled="true" style="width: 150px;"/>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitTypeUserForm">确 定</el-button>
          <el-button @click="cancelTypeUserForm">取 消</el-button>
        </div>
      </el-dialog>
    </div>
</template>

<script>
import { listQaQuestionType, getSequenceNum, getQaQuestionType, delQaQuestionType, addQaQuestionType, updateQaQuestionType, 
         listQaUserQuestion, getQaUserQuestion, delQaUserQuestion, addQaUserQuestion, updateQaUserQuestion } from "@/api/qaSystem/qaQuestionType";
import {refreshCache, listUserOption, listPartTypeOption} from "@/api/qaSystem/qaCommon";
export default {
  name: "QaUserQuestion",
  data() {
    return {
      // 左侧问题类型表格
      // 遮罩层
      loading: true,
      // 是否显示添加问题类型表单
      type_open: false,
      // 问题类型表单标题
      type_title: "",
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      typeSearch: true,
      // 选中数组
      ids: [],
      // 左侧表格数据
      typeList: [],
      // 总条数
      total: 0,
      // 左侧查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        searchValue:""
      },
      // 问题类型表单参数
      type_form:{
        typeId: undefined,
        type: undefined,
        sequence: 0,
        occupied: 0,
      },
      // 问题类型表单校验
      typeRules: {
        type: [
          { required: true, message: "参数名称不能为空", trigger: "blur" }
        ],
        occupied: [
          { required: true, message: "参数名称不能为空", trigger: "blur" }
        ],
        sequence: [
          { required: true, message: "参数名称不能为空", trigger: "blur" }
        ]
      },

      // 右侧问题类型与角色对应表格
      // 遮罩层
      typeUser_loading: true,
      // 是否显示添加问题类型表单
      typeUser_open: false,
      // 问题类型表单标题
      typeUser_title: "",
      // 选中数组
      typeUser_ids: [],
      // 右侧表格独立数据
      typeUserList: [],
      // 总条数
      searchTotal: 0,
      // 非单个禁用
      typeUser_single: true,
      // 非多个禁用
      typeUser_multiple: true,
      // 显示搜索条件
      typeUserSearch: true,
      // 左侧查询参数
      searchQueryParams: {
        pageNum: 1,
        pageSize: 20,
        searchValue:""
      },
      // 问题类型表单参数
      typeUser_form:{
        id: undefined,
        typeId: undefined,
        type: undefined,
        userId: undefined,
        userName: undefined,
        loginName: undefined,
        sequence: 0,
      },
      // 问题类型表单校验
      typeUserRules: {
        type: [
          { required: true, message: "参数名称不能为空", trigger: "blur" }
        ],
        userName: [
          { required: true, message: "参数名称不能为空", trigger: "blur" }
        ],
        loginName: [
          { required: false, message: "参数名称不能为空", trigger: "blur" }
        ]
      },
      // 下拉用户数据源
      userOptions: [],
      // 下拉问题类型数据源
      typeOptions: [],
    };
  },
  created() {
    this.getTypeList();
    this.getTypeUserList();
    this.getUserOptions();
    this.getTypeOptions();
  },
  methods: {
    // 通用方法
    /** 刷新缓存按钮操作 */
    handleRefreshCache() {
      refreshCache().then(() => {
        /** 清除浏览器数据 */
        localStorage.clear();
        sessionStorage.clear();
        /** 强制刷新页面 */
        location.reload(true);
        this.$message.success("刷新成功");
      });
    },
    /** 获取下拉用户栏数据（全部用户） */
    async getUserOptions(){
      await listUserOption().then(response => {
          this.userOptions = response.data || [];
        }).catch(error =>{
          console.error("获取用户失败", error);
        });
    },
    // 下拉选择变化时，同步回填工号
    handleUserChange(val){
      this.typeUser_form.userId = val.userId;
      this.typeUser_form.userName = val.userName;
      this.typeUser_form.loginName = val.loginName;
    },
    /** 获取下拉问题类型栏数据（筛选占用位为0的） */
    async getTypeOptions(){
      await listPartTypeOption().then(response => {
          this.typeOptions = response.data || [];
          // 排序显示在表单中
          this.typeOptions.sort((a, b) => a.sequence - b.sequence);
        }).catch(error =>{
          console.error("获取问题类型失败", error);
        });
    },
    // 下拉选择变化
    handleTypeChange(val){
      this.typeUser_form.typeId = val.typeId;
      this.typeUser_form.type = val.type;
    },
    /** 表格分页功能汇总 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val;
      this.queryParams.pageNum = 1;
      this.getTypeList();
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val;
      this.getTypeList();
    },
    handleSearchSizeChange(val) {
      this.searchQueryParams.pageSize = val;
      this.searchQueryParams.pageNum = 1;
      this.getTypeUserList();
    },

    handleSearchCurrentChange(val) {
      this.searchQueryParams.pageNum = val;
      this.getTypeUserList();
    },

    /** 左侧查询参数列表 */
    getTypeList() {
      this.loading = true;
      listQaQuestionType(this.queryParams).then(response => {
          this.typeList = response.rows;
          this.total = response.total;
          this.loading = false;
        }).catch(error =>{
          console.error("获取问题类型列表失败", error);
          this.loading = false;
        });
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.typeId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 将二进制数据occupied转换为对应的文字 */
    formatOccupied(row, column, cellValue){
      return cellValue === 1 ? '已占用' : '未占用';
    },
    /** 增删改操作 */
    handleTypeAdd(_this, item, data) {
      this.resetType();
      getSequenceNum().then(response => {
        this.type_form.sequence = response.data,
        this.type_open = true;
        this.type_title = "添加问题类型";
      });
    },
    handleTypeUpdate(row) {
      this.resetType();
      const typeId = row.typeId || this.ids;
      getQaQuestionType(typeId).then(response => {
        this.type_form = {
          typeId: response.data.typeId,
          type: response.data.type,
          occupied: response.data.occupied,
          sequence: response.data.sequence
        };
        this.type_open = true;
        this.type_title = "修改问题类型";
      });
    },
    handleTypeDelete(row) {
      const typeId = row.typeId || this.ids;
      this.$confirm('是否确认删除选中数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delQaQuestionType(typeId);
        }).then(response => {
          if(response.code === 200){
            this.getTypeList();
            // 删除问题类型后需要刷一次
            this.getTypeOptions();
            this.$message.success("删除成功");
          }else{
            this.$message.error("删除失败" || response.msg);
          }
        }).catch(error => {
          console.error(`删除失败`, error);
        });
    },
    /** 问题类型表单提交按钮 */
    submitTypeForm() {
      this.$refs["type_form"].validate(valid => {
        if (valid) {
          const isUpdate = !!this.type_form.typeId;
          const operation = isUpdate ? updateQaQuestionType(this.type_form) : addQaQuestionType(this.type_form);
          operation.then(response => {
            if(response.code === 200){
              this.$message.success(`${isUpdate ? '修改' : '新增'}成功`);
              this.type_open = false;
              this.getTypeList();
              // 增改问题类型后需要刷一次
              this.getTypeOptions();
            }else{
              this.$message.error(`${isUpdate ? '修改' : '新增'}失败` || response.msg);
            }
          }).catch(error => {
            console.error(`${isUpdate ? '修改' : '新增'}失败`, error);
          });
        }
      });
    },
    // 问题类型表单取消按钮
    cancelTypeForm() {
      this.type_open = false;
      this.resetType();
    },
    // 问题类型表单重置
    resetType() {
      this.type_form = {
        typeId: undefined,
        type: undefined,
        sequence: 0,
        occupied: 0
      };
      this.resetForm("type_form");
    },


    /** 右侧查询参数列表 */
    getTypeUserList(){
      this.typeUser_loading = true;
      listQaUserQuestion(this.searchQueryParams).then(response => {
          this.typeUserList = response.rows;
          this.searchTotal = response.total;
          this.typeUser_loading = false;
        }).catch(error =>{
          console.error("获取查询列表失败", error);
          this.typeUser_loading = false;
        });
    },

    /** 多选框选中数据 */
    handleUserTypeSelectionChange(selection) {
      this.typeUser_ids = selection.map(item => item.id)
      this.typeUser_single = selection.length!=1
      this.typeUser_multiple = !selection.length
    },
    /** 增删改操作 */
    handleTypeUserAdd(_this, item, data) {
      this.resetTypeUser();
      this.typeUser_open = true;
      this.typeUser_title = "添加问题类型角色映射";
    },
    handleTypeUserUpdate(row) {
      this.resetTypeUser();
      const id = row.id || this.typeUser_ids;
      getQaUserQuestion(id).then(response => {
        this.typeUser_form = {
          id: response.data.id,
          typeId: response.data.typeId,
          type: response.data.type,
          userId: response.data.userId,
          userName: response.data.userName,
          loginName: response.data.loginName
        };
        this.typeUser_open = true;
        this.typeUser_title = "修改问题类型角色映射";
      });
    },
    handleTypeUserDelete(row) {
      const id = row.id || this.typeUser_ids;
      this.$confirm('是否确认删除选中数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delQaUserQuestion(id);
        }).then(response => {
          if(response.code === 200){
            this.getTypeUserList();
            // 删除问题角色映射后需要刷一次
            this.getTypeList();
            this.getTypeOptions();
            this.$message.success("删除成功");
          }else{
            this.$message.error("删除失败" || response.msg);
          }
        }).catch(error => {
          console.error(`删除失败`, error);
        });
    },
    /** 问题类型表单提交按钮 */
    submitTypeUserForm() {
      this.$refs["typeUser_form"].validate(valid => {
        if (valid) {
          console.log(this.typeUser_form);
          const isUpdate = !!this.typeUser_form.id;
          const operation = isUpdate ? updateQaUserQuestion(this.typeUser_form) : addQaUserQuestion(this.typeUser_form);
          operation.then(response => {
            if(response.code === 200){
              this.$message.success(`${isUpdate ? '修改' : '新增'}成功`);
              this.typeUser_open = false;
              this.getTypeUserList();
              // 增改问题角色映射后需要刷一次
              this.getTypeList();
              this.getTypeOptions();
            }else{
              this.$message.error(`${isUpdate ? '修改' : '新增'}失败` || response.msg);
            }
          }).catch(error => {
            console.error(`${isUpdate ? '修改' : '新增'}失败`, error);
          });
        }
      });
    },
    // 问题类型表单取消按钮
    cancelTypeUserForm() {
      this.typeUser_open = false;
      this.resetTypeUser();
    },
    // 问题类型表单重置
    resetTypeUser() {
      this.typeUser_form = {
        id: undefined,
        typeId: undefined,
        type: undefined,
        userId: undefined,
        userName: undefined,
        loginName: undefined
      };
      this.resetForm("typeUser_form");
    },
  }
}
</script>

<style scoped>
/* 自定义表格边框样式 */
.custom-bordered-table {
  border: 1px solid #EBEEF5;
  border-right: 1px solid #EBEEF5;
}
.custom-bordered-table .el-table__header-wrapper,
.custom-bordered-table .el-table__body-wrapper {
  border-left: 1px solid #EBEEF5;
  border-right: 1px solid #EBEEF5;
}
.custom-bordered-table th,
.custom-bordered-table td {
  border-right: 1px solid #EBEEF5;
}
.custom-bordered-table th:first-child,
.custom-bordered-table td:first-child {
  border-left: 1px solid #EBEEF5;
}
.pagination-wrapper{
  /* display: flex;
  justify-content: flex-end; */
  text-align: right;
  margin-top: 3px;
  margin-bottom: 3px;
}
</style>