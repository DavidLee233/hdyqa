<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px" class="search-form">
      <el-form-item label="字段类型" prop="type">
        <el-select
          v-model="queryParams.type"
          placeholder="请选择字段类型"
          clearable
          style="width: 200px"
        >
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
          placeholder="请输入字段含义"
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

    <el-table
      v-loading="loading"
      :data="mainDataMappingList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center">
        <template slot-scope="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="字段类型" align="center" min-width="180">
        <template slot-scope="scope">
          <div class="inline-editor" :data-edit-row-key="getRowEditKey(scope.row)">
            <el-select
              v-if="scope.row._editing"
              v-model="scope.row.type"
              size="mini"
              placeholder="请选择字段类型"
              @click.native.stop
            >
              <el-option
                v-for="item in field_type"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              />
            </el-select>
            <span v-else>{{ formatFieldType(scope.row.type) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="源字段" align="center" min-width="220">
        <template slot-scope="scope">
          <div class="inline-editor" :data-edit-row-key="getRowEditKey(scope.row)">
            <el-input
              v-if="scope.row._editing"
              v-model.trim="scope.row.sourceField"
              size="mini"
              placeholder="请输入源字段"
              @click.native.stop
            />
            <span v-else>{{ scope.row.sourceField || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="目标字段" align="center" min-width="220">
        <template slot-scope="scope">
          <div class="inline-editor" :data-edit-row-key="getRowEditKey(scope.row)">
            <el-input
              v-if="scope.row._editing"
              v-model.trim="scope.row.targetField"
              size="mini"
              placeholder="请输入目标字段"
              @click.native.stop
            />
            <span v-else>{{ scope.row.targetField || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="字段含义" align="center" min-width="220">
        <template slot-scope="scope">
          <div class="inline-editor" :data-edit-row-key="getRowEditKey(scope.row)">
            <el-input
              v-if="scope.row._editing"
              v-model.trim="scope.row.fieldMeaning"
              size="mini"
              placeholder="请输入字段含义"
              @click.native.stop
            />
            <span v-else>{{ scope.row.fieldMeaning || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            class="mapping-action-btn"
            @click.stop="handleUpdate(scope.row)"
          >编辑</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            class="mapping-action-btn"
            @click.stop="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      :page-sizes="[10, 20, 50, 100, 200]"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="85%" append-to-body class="goodsOut-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px" label-position="right">
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
                <el-input v-model="form.sourceField" placeholder="请输入源数据字段" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="目标字段" prop="targetField">
                <el-input v-model="form.targetField" placeholder="请输入目标数据字段" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="字段含义" prop="fieldMeaning">
                <el-input v-model="form.fieldMeaning" placeholder="请输入字段含义" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listMainDataMapping,
  getMainDataMapping,
  delMainDataMapping,
  addMainDataMapping,
  updateMainDataMapping
} from '@/api/mainData/mainDataMapping'
import { getUser } from '@/api/qaSystem/qaCommon'

export default {
  name: 'MainDataMapping',
  data() {
    return {
      userName: null,
      loginName: null,
      userId: null,
      loading: true,
      ids: [],
      selectedRows: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      mainDataMappingList: [],
      field_type: [],
      title: '',
      open: false,
      currentEditingRowKey: null,
      savingInline: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        searchValue: null,
        type: null,
        sourceField: null,
        targetField: null,
        fieldMeaning: null
      },
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
        updateTime: null
      },
      rules: {
        type: [
          { required: true, message: '字段类型不能为空', trigger: 'blur' }
        ],
        sourceField: [
          { required: true, message: '源数据字段不能为空', trigger: 'blur' }
        ],
        targetField: [
          { required: true, message: '目标数据字段不能为空', trigger: 'blur' }
        ]
      }
    }
  },

  created() {
    this.getCurrentUser()
    this.getDicts('hdl_field_type').then(response => {
      this.field_type = response.data || []
    })
    this.getList()
  },

  mounted() {
    document.addEventListener('click', this.handleDocumentClick)
  },

  beforeDestroy() {
    document.removeEventListener('click', this.handleDocumentClick)
  },

  methods: {
    formatFieldType(type) {
      if (!type && type !== 0) {
        return ''
      }
      const item = this.field_type.find(entry => String(entry.dictValue) === String(type))
      return item ? item.dictLabel : type
    },

    normalizeRow(row) {
      return {
        ...row,
        type: row.type != null ? String(row.type) : '',
        sourceField: row.sourceField || '',
        targetField: row.targetField || '',
        fieldMeaning: row.fieldMeaning || '',
        _editing: false,
        _original: null
      }
    },

    getRowEditKey(row) {
      return row && row.mapId ? `mapping_${row.mapId}` : ''
    },

    getCurrentEditingRow() {
      return this.mainDataMappingList.find(item => item._editing)
    },

    getList() {
      this.loading = true
      listMainDataMapping(this.queryParams).then(response => {
        const rows = Array.isArray(response.rows) ? response.rows : []
        this.mainDataMappingList = rows.map(item => this.normalizeRow(item))
        this.total = Number(response.total) || 0
      }).catch(error => {
        console.error('获取数据失败', error)
      }).finally(() => {
        this.loading = false
      })
    },

    getCurrentUser() {
      getUser().then(response => {
        if (response.code === 200) {
          this.userName = response.data.createBy
          this.loginName = response.data.createId
          this.userId = response.data.userId
        } else {
          this.$message.error('无此用户，请联系管理员')
        }
      }).catch(error => {
        console.error('无此用户，请联系管理员', error)
      })
    },

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
        updateTime: null
      }
      this.resetForm('form')
    },

    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: this.queryParams.pageSize,
        searchValue: null,
        type: null,
        sourceField: null,
        targetField: null,
        fieldMeaning: null
      }
      if (this.$refs.queryForm) {
        this.$refs.queryForm.resetFields()
      }
      this.handleQuery()
    },

    handleSelectionChange(selection) {
      this.selectedRows = selection || []
      this.ids = this.selectedRows.map(item => item.mapId)
      this.single = this.selectedRows.length !== 1
      this.multiple = !this.selectedRows.length
    },

    handleAdd() {
      this.reset()
      this.form.createBy = this.userName
      this.form.createId = this.loginName
      this.form.type = '1'
      this.open = true
      this.title = '添加主数据字段映射'
    },

    async handleUpdate(row) {
      const targetRow = row || this.selectedRows[0]
      if (!targetRow) {
        return
      }
      await this.startInlineEdit(targetRow)
    },

    async startInlineEdit(row) {
      const currentRow = this.getCurrentEditingRow()
      if (currentRow && currentRow.mapId === row.mapId) {
        return
      }
      if (currentRow) {
        const saved = await this.saveInlineRow(currentRow)
        if (!saved) {
          return
        }
      }
      this.mainDataMappingList.forEach(item => {
        item._editing = false
        item._original = null
      })
      row._editing = true
      row._original = {
        type: row.type,
        sourceField: row.sourceField,
        targetField: row.targetField,
        fieldMeaning: row.fieldMeaning
      }
      this.currentEditingRowKey = this.getRowEditKey(row)
    },

    validateInlineRow(row) {
      if (!row.type) {
        this.$message.warning('字段类型不能为空')
        return false
      }
      if (!row.sourceField) {
        this.$message.warning('源字段不能为空')
        return false
      }
      if (!row.targetField) {
        this.$message.warning('目标字段不能为空')
        return false
      }
      return true
    },

    async saveInlineRow(row) {
      if (!row || this.savingInline) {
        return false
      }
      if (!this.validateInlineRow(row)) {
        return false
      }
      const original = row._original || {}
      const changed =
        String(original.type || '') !== String(row.type || '') ||
        String(original.sourceField || '') !== String(row.sourceField || '') ||
        String(original.targetField || '') !== String(row.targetField || '') ||
        String(original.fieldMeaning || '') !== String(row.fieldMeaning || '')

      if (!changed) {
        row._editing = false
        row._original = null
        this.currentEditingRowKey = null
        return true
      }

      this.savingInline = true
      try {
        const response = await updateMainDataMapping({
          mapId: row.mapId,
          type: row.type,
          sourceField: row.sourceField,
          targetField: row.targetField,
          fieldMeaning: row.fieldMeaning
        })
        if (response.code === 200) {
          row._editing = false
          row._original = null
          this.currentEditingRowKey = null
          this.$message.success('保存成功')
          return true
        }
        this.$message.error(response.msg || '保存失败')
        return false
      } catch (error) {
        console.error('保存字段映射失败', error)
        this.$message.error('保存失败，请稍后重试')
        return false
      } finally {
        this.savingInline = false
      }
    },

    handleDocumentClick(event) {
      const row = this.getCurrentEditingRow()
      if (!row || !this.currentEditingRowKey) {
        return
      }
      const target = event.target
      if (target.closest(`[data-edit-row-key="${this.currentEditingRowKey}"]`)) {
        return
      }
      if (target.closest('.mapping-action-btn')) {
        return
      }
      this.saveInlineRow(row)
    },

    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const isUpdate = !!this.form.mapId
          const operation = isUpdate ? updateMainDataMapping(this.form) : addMainDataMapping(this.form)
          operation.then(response => {
            if (response.code === 200) {
              this.$message.success(`${isUpdate ? '修改' : '新增'}成功`)
              this.open = false
              this.getList()
            } else {
              this.$message.error(response.msg || `${isUpdate ? '修改' : '新增'}失败`)
            }
          }).catch(error => {
            console.error(`${isUpdate ? '修改' : '新增'}失败`, error)
          })
        }
      })
    },

    cancel() {
      this.open = false
      this.reset()
    },

    handleDelete(row) {
      const mapId = row ? row.mapId : this.ids
      this.$modal.confirm('是否确认删除选中的主数据映射项？').then(() => {
        return delMainDataMapping(mapId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
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

.dialog-footer {
  text-align: right;
  padding-top: 0;
}

.inline-editor {
  width: 100%;
}

.inline-editor :deep(.el-input),
.inline-editor :deep(.el-select) {
  width: 100%;
}

.mapping-action-btn {
  user-select: none;
}

@media screen and (max-width: 768px) {
  .form-section {
    padding: 15px;
  }

  .section-header {
    margin-bottom: 15px;
  }
}
</style>
