<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px" class="search-form">
      <el-form-item label="字段类型" prop="type">
        <el-select
          v-model="queryParams.type"
          placeholder="请选择字段类型"
          clearable
          style="width: 200px">
          <el-option
            v-for="item in field_type"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="源字段" prop="sourceField">
        <el-input
          v-model="queryParams.sourceField"
          placeholder="请输入源字段"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标字段" prop="targetField">
        <el-input
          v-model="queryParams.targetField"
          placeholder="请输入目标字段"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="字段含义" prop="fieldMeaning">
        <el-input
          v-model="queryParams.fieldMeaning"
          placeholder="请输入目标字段"
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
      <right-toolbar :showSearch.sync="showSearch" :searchValue.sync="queryParams.searchValue" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="mainDataMappingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center">
        <template slot-scope="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="字段类型" align="center" prop="type">
        <template slot-scope="scope">
          <span>{{ formatFieldType(scope.row.type) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="源字段" align="center" prop="sourceField" />
      <el-table-column label="目标字段" align="center" prop="targetField" />
      <el-table-column label="字段含义" align="center" prop="fieldMeaning" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
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
        <!-- 组织部门信息 -->
        <div class="form-section">
          <div class="section-header">
            <i class="el-icon-user"></i>
            <span>字段信息</span>
          </div>
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="字段类型" prop="type">
                <el-select
                  v-model="form.type"
                  placeholder="请选择字段类型"
                  style="width: 100%"
                >
                  <el-option
                    v-for="item in field_type"
                    :key="item.dictValue"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="源字段" prop="sourceField">
                <el-input v-model="form.sourceField" placeholder="请输入源数据字段"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="目标字段" prop="targetField">
                <el-input v-model="form.targetField" placeholder="请输入目标数据字段"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="字段含义" prop="fieldMeaning">
                <el-input v-model="form.fieldMeaning" placeholder="请输入字段含义"/>
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
import { listMainDataMapping, getMainDataMapping, delMainDataMapping, addMainDataMapping, updateMainDataMapping } from "@/api/mainData/mainDataMapping";
import {getUser} from "@/api/qaSystem/qaCommon";

export default {
  name: "MainDataMapping",
  data() {
    return {
      userName: null,
      loginName: null,
      userId: null,
      // 遮罩层
      loading: true,
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
      mainDataMappingList: [],
      // 字段类型字典
      field_type: [],
      // 弹出层标题
      title: "",
      // 是否显示表单
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        searchValue: null,
        type: null,
        sourceField: null,
        targetField: null,
        fieldMeaning: null,
      },
      // 表单参数
      form: {
        mapId: null,
        createId: this.$store.state.user.id,
        createBy: null,
        createTime: null,
        type: null,
        sourceField: null,
        targetField: null,
        fieldMeaning: null,
        updateId: null,
        updateBy: null,
        updateTime: null,
      },
      // 表单校验
      rules: {
        type:[
          { required: true, message: '字段类型不能为空', trigger: 'blur' },
        ],
        sourceField:[
          { required: true, message: '源数据字段不能为空', trigger: 'blur' },
        ],
        targetField: [
          { required: true, message: '目标数据字段不能为空', trigger: 'blur' }
        ],
      },
    };
  },

  created() {
    this.getCurrentUser();
    this.getDicts("hdl_field_type").then(response => {
      this.field_type = response.data;
    });
    this.getList();
  },

  methods: {
    /**                      初始化方法                     */
    /** 格式化字段类型显示 */
    formatFieldType(type) {
      if (!type && type !== 0) return '';
      const item = this.field_type.find(item => item.dictValue === type);
      return item ? item.dictLabel : type;
    },

    /** 查询参数列表 */
    getList() {
      this.loading = true;
      listMainDataMapping(this.queryParams).then(response => {
        this.mainDataMappingList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(error =>{
        console.error("获取数据失败", error);
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
    /**   表单重置   */
    reset() {
      this.form = {
        mapId: null,
        createId: this.$store.state.user.id,
        createBy: null,
        createTime: null,
        type: null,
        sourceField: null,
        targetField: null,
        fieldMeaning: null,
        updateId: null,
        updateBy: null,
        updateTime: null,
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
        type: null,
        sourceField: null,
        targetField: null,
        fieldMeaning: null,
      };
      // 如果需要重置表单验证状态
      if (this.$refs.queryForm) {
        this.$refs.queryForm.resetFields();
      }
      this.handleQuery();
    },

    /**   多选框选中数据   */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.mapId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.form.createBy = this.userName;
      this.form.createId = this.loginName;
      this.form.type = "0";
      this.open = true;
      this.title = "添加主数据字段映射";
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const mapId = row.mapId || this.ids
      getMainDataMapping(mapId).then(response => {
        // 已解决form传递值出现的问题
        this.form = response.data;
        this.open = true;
        this.title = "修改主数据字段映射";
      });
    },

    /** 问题表单提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const isUpdate = !!this.form.mapId;
          const operation = isUpdate ? updateMainDataMapping(this.form) : addMainDataMapping(this.form);
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
      const mapId = row.mapId || this.ids;
      this.$modal.confirm('是否确认删除编号为"' + mapId + '"的数据项？').then(function() {
        return delMainDataMapping(mapId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
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
</style>
