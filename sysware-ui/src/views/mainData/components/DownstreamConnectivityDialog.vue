<template>
  <el-dialog
    title="连接检测"
    :visible.sync="visible"
    width="1200px"
    append-to-body
    @close="handleClose"
  >
    <div class="toolbar-line">
      <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleAdd">添加</el-button>
      <el-button type="success" size="mini" icon="el-icon-edit" :disabled="selectedRows.length !== 1" @click="handleEdit">修改</el-button>
      <el-button type="danger" size="mini" icon="el-icon-delete" :disabled="selectedRows.length === 0" @click="handleDelete">删除</el-button>
      <el-button type="warning" size="mini" icon="el-icon-connection" :loading="checking" @click="handleCheck">检测</el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="systemRows"
      row-key="_rowKey"
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="下游系统名称" min-width="200">
        <template slot-scope="scope">
          <el-input
            v-if="scope.row._editing"
            v-model.trim="scope.row.systemName"
            size="mini"
            maxlength="120"
            placeholder="请输入下游系统名称"
          />
          <span v-else>{{ scope.row.systemName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="下游系统IP" min-width="180">
        <template slot-scope="scope">
          <el-input
            v-if="scope.row._editing"
            v-model.trim="scope.row.systemIp"
            size="mini"
            maxlength="64"
            placeholder="请输入下游系统IP"
          />
          <span v-else>{{ scope.row.systemIp || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="IP连接状况" width="240" align="center">
        <template slot-scope="scope">
          <span :class="String(scope.row.ipConnectStatus) === '1' ? 'status-ok' : 'status-fail'">
            {{ String(scope.row.ipConnectStatus) === '1' ? '已连接' : '未连接' }}
          </span>
          <span class="status-msg">{{ scope.row.ipCheckMessage || '' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="端口" width="140" align="center">
        <template slot-scope="scope">
          <el-input
            v-model.trim="scope.row.systemPort"
            size="mini"
            maxlength="5"
            placeholder="请输入端口"
            @input="markRowEditing(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="端口可用状况" width="260" align="center">
        <template slot-scope="scope">
          <span :class="String(scope.row.portConnectStatus) === '1' ? 'status-ok' : 'status-fail'">
            {{ String(scope.row.portConnectStatus) === '1' ? '可用' : '不可用' }}
          </span>
          <span class="status-msg">{{ scope.row.portCheckMessage || '' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="同步日志" width="120" align="center">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="mini"
            :disabled="!scope.row.id"
            @click="openLogDialog(scope.row)"
          >
            同步日志
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" :loading="saving" @click="handleConfirm">确认</el-button>
      <el-button @click="visible = false">关闭</el-button>
    </div>

    <el-dialog
      title="同步日志"
      :visible.sync="logDialogVisible"
      width="1180px"
      append-to-body
    >
      <div class="toolbar-line">
        <el-button
          type="danger"
          size="mini"
          icon="el-icon-delete"
          :disabled="selectedLogRows.length === 0"
          @click="handleDeleteLogs"
        >
          删除
        </el-button>
        <el-button
          type="warning"
          size="mini"
          icon="el-icon-delete-solid"
          @click="handleClearLogs"
        >
          清空
        </el-button>
      </div>

      <el-table
        v-loading="logLoading"
        :data="logRows"
        border
        @selection-change="handleLogSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="接口URL" prop="requestUrl" min-width="260" show-overflow-tooltip />
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
        v-show="logTotal > 0"
        :total="logTotal"
        :page.sync="logQuery.pageNum"
        :limit.sync="logQuery.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        @pagination="loadLogs"
      />

      <div slot="footer" class="dialog-footer">
        <el-button @click="logDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </el-dialog>
</template>

<script>
import {
  checkDownstreamSystems,
  clearDownstreamLogs,
  deleteDownstreamLogs,
  deleteDownstreamSystems,
  listDownstreamLogs,
  listDownstreamSystems,
  saveDownstreamSystem
} from '@/api/mainData/connectivity'

export default {
  name: 'DownstreamConnectivityDialog',
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
      saving: false,
      checking: false,
      systemRows: [],
      selectedRows: [],
      logDialogVisible: false,
      currentLogSystem: null,
      logLoading: false,
      logRows: [],
      selectedLogRows: [],
      logTotal: 0,
      logQuery: {
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  methods: {
    open() {
      this.visible = true
      this.loadSystems()
    },
    handleClose() {
      this.selectedRows = []
    },
    loadSystems() {
      this.loading = true
      listDownstreamSystems().then(res => {
        const rows = Array.isArray(res.data) ? res.data : []
        this.systemRows = rows.map(item => this.wrapRow(item))
      }).finally(() => {
        this.loading = false
      })
    },
    wrapRow(row) {
      const copy = { ...row }
      copy._rowKey = copy.id || `new_${Date.now()}_${Math.random().toString(36).slice(2, 8)}`
      copy._editing = false
      copy.systemPort = copy.systemPort ? String(copy.systemPort) : '80'
      copy.ipConnectStatus = String(copy.ipConnectStatus || copy.lastConnectStatus || '0')
      copy.portConnectStatus = String(copy.portConnectStatus || copy.lastConnectStatus || '0')
      copy.ipCheckMessage = copy.ipCheckMessage || ''
      copy.portCheckMessage = copy.portCheckMessage || copy.lastCheckMessage || ''
      return copy
    },
    markRowEditing(row) {
      if (row) {
        row._editing = true
      }
    },
    handleSelectionChange(rows) {
      this.selectedRows = rows || []
    },
    handleAdd() {
      const row = this.wrapRow({
        id: null,
        systemName: '',
        systemIp: '',
        systemPort: '80',
        lastConnectStatus: '0',
        lastCheckMessage: ''
      })
      row._editing = true
      this.systemRows.unshift(row)
    },
    handleEdit() {
      if (this.selectedRows.length !== 1) {
        return
      }
      this.selectedRows[0]._editing = true
    },
    handleDelete() {
      if (this.selectedRows.length === 0) {
        return
      }
      this.$confirm('是否确认删除选中的下游系统配置？', '提示', {
        type: 'warning'
      }).then(() => {
        const selectedIds = this.selectedRows.filter(item => item.id).map(item => item.id)
        this.systemRows = this.systemRows.filter(row => !this.selectedRows.includes(row))
        this.selectedRows = []
        if (selectedIds.length === 0) {
          return
        }
        return deleteDownstreamSystems(selectedIds)
      }).then(() => {
        this.$message.success('删除完成')
        this.loadSystems()
      }).catch(() => {})
    },
    handleCheck() {
      this.checking = true
      const ids = this.selectedRows.filter(item => item.id).map(item => item.id)
      checkDownstreamSystems(ids).then(res => {
        const rows = Array.isArray(res.data) ? res.data : []
        if (ids.length === 0) {
          this.systemRows = rows.map(item => this.wrapRow(item))
        } else {
          const updateMap = {}
          rows.forEach(item => {
            updateMap[item.id] = item
          })
          this.systemRows = this.systemRows.map(item => {
            if (item.id && updateMap[item.id]) {
              const merged = this.wrapRow({ ...item, ...updateMap[item.id] })
              merged._editing = item._editing
              return merged
            }
            return item
          })
        }
        const connectedCount = rows.filter(item => String(item.lastConnectStatus) === '1').length
        const disconnectedCount = Math.max(rows.length - connectedCount, 0)
        if (rows.length === 0) {
          this.$message.warning('未检测到可用的下游系统配置')
        } else if (disconnectedCount > 0) {
          this.$message.warning(`连接检测完成：已连接${connectedCount}，未连接${disconnectedCount}`)
        } else {
          this.$message.success(`连接检测完成：${connectedCount}个系统全部已连接`)
        }
      }).finally(() => {
        this.checking = false
      })
    },
    handleConfirm() {
      const editingRows = this.systemRows.filter(item => item._editing)
      if (editingRows.length === 0) {
        this.$message.success('当前无待保存修改')
        return
      }
      for (const item of editingRows) {
        if (!item.systemName) {
          this.$message.warning('下游系统名称不能为空')
          return
        }
        if (!item.systemIp) {
          this.$message.warning('下游系统IP不能为空')
          return
        }
        if (!/^\d+$/.test(String(item.systemPort || ''))) {
          this.$message.warning('端口必须为数字')
          return
        }
        const port = Number(item.systemPort)
        if (port < 1 || port > 65535) {
          this.$message.warning('端口必须在1到65535之间')
          return
        }
      }
      this.saving = true
      const tasks = editingRows.map(item => {
        return saveDownstreamSystem({
          id: item.id || null,
          systemName: item.systemName,
          systemIp: item.systemIp,
          systemPort: Number(item.systemPort) || 80
        })
      })
      Promise.all(tasks).then(() => {
        this.$message.success('保存成功')
        this.loadSystems()
      }).finally(() => {
        this.saving = false
      })
    },
    openLogDialog(row) {
      this.currentLogSystem = row
      this.logDialogVisible = true
      this.logQuery.pageNum = 1
      this.loadLogs()
    },
    loadLogs() {
      if (!this.currentLogSystem || !this.currentLogSystem.id) {
        this.logRows = []
        this.logTotal = 0
        return
      }
      this.logLoading = true
      listDownstreamLogs({
        systemId: this.currentLogSystem.id,
        dataType: this.dataType,
        pageNum: this.logQuery.pageNum,
        pageSize: this.logQuery.pageSize
      }).then(res => {
        this.logRows = Array.isArray(res.rows) ? res.rows : []
        this.logTotal = Number(res.total) || 0
      }).finally(() => {
        this.logLoading = false
      })
    },
    handleLogSelectionChange(rows) {
      this.selectedLogRows = rows || []
    },
    handleDeleteLogs() {
      if (this.selectedLogRows.length === 0) {
        return
      }
      const ids = this.selectedLogRows.map(item => item.id).filter(Boolean)
      if (ids.length === 0) {
        return
      }
      deleteDownstreamLogs(ids).then(() => {
        this.$message.success('日志删除成功')
        this.loadLogs()
      })
    },
    handleClearLogs() {
      if (!this.currentLogSystem || !this.currentLogSystem.id) {
        return
      }
      this.$confirm('是否清空当前下游系统的同步日志？', '提示', { type: 'warning' }).then(() => {
        return clearDownstreamLogs(this.currentLogSystem.id)
      }).then(() => {
        this.$message.success('日志已清空')
        this.loadLogs()
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

.status-msg {
  margin-left: 6px;
  color: #909399;
  font-size: 12px;
}
</style>
