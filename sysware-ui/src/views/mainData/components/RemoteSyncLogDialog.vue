<template>
  <el-dialog
    title="同步日志"
    :visible.sync="visible"
    width="1180px"
    append-to-body
  >
    <div class="toolbar-line">
      <el-button
        type="danger"
        size="mini"
        icon="el-icon-delete"
        :disabled="selectedRows.length === 0"
        @click="handleDelete"
      >
        删除
      </el-button>
      <el-button
        type="warning"
        size="mini"
        icon="el-icon-delete-solid"
        @click="handleClear"
      >
        清空
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="rows"
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="接口URL" prop="requestUrl" min-width="280" show-overflow-tooltip />
      <el-table-column label="同步数据类型" prop="dataTypeName" width="130" />
      <el-table-column label="同步时间" prop="syncTime" width="170" />
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <span :class="scope.row.success === '1' ? 'status-ok' : 'status-fail'">
            {{ scope.row.success === '1' ? '成功' : '失败' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="记录数" prop="recordCount" width="90" align="center" />
      <el-table-column label="耗时(ms)" prop="durationMs" width="100" align="center" />
      <el-table-column label="备注" prop="message" min-width="220" show-overflow-tooltip />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="query.pageNum"
      :limit.sync="query.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      @pagination="loadData"
    />

    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { clearRemoteLogs, deleteRemoteLogs, listRemoteLogs } from '@/api/mainData/connectivity'

export default {
  name: 'RemoteSyncLogDialog',
  props: {
    dataType: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      visible: false,
      loading: false,
      rows: [],
      selectedRows: [],
      total: 0,
      query: {
        pageNum: 1,
        pageSize: 10,
        callSource: 'sync'
      }
    }
  },
  methods: {
    open() {
      this.visible = true
      this.query.pageNum = 1
      this.loadData()
    },
    loadData() {
      this.loading = true
      listRemoteLogs({
        dataType: this.dataType,
        callSource: this.query.callSource,
        pageNum: this.query.pageNum,
        pageSize: this.query.pageSize
      }).then(res => {
        this.rows = Array.isArray(res.rows) ? res.rows : []
        this.total = Number(res.total) || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleSelectionChange(rows) {
      this.selectedRows = rows || []
    },
    handleDelete() {
      if (this.selectedRows.length === 0) {
        return
      }
      const ids = this.selectedRows.map(item => item.id).filter(Boolean)
      if (ids.length === 0) {
        return
      }
      deleteRemoteLogs(ids).then(() => {
        this.$message.success('日志删除成功')
        this.loadData()
      })
    },
    handleClear() {
      this.$confirm('是否清空当前数据类型的同步日志？', '提示', { type: 'warning' }).then(() => {
        return clearRemoteLogs(this.dataType, this.query.callSource)
      }).then(() => {
        this.$message.success('日志已清空')
        this.loadData()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.toolbar-line {
  margin-bottom: 10px;
  display: flex;
  gap: 8px;
}

.status-ok {
  color: #67c23a;
  font-weight: 600;
}

.status-fail {
  color: #f56c6c;
  font-weight: 600;
}
</style>
