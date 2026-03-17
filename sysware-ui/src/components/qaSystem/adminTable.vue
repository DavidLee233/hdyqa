<template>
  <div>
    <el-table
      height="31.5vh"
      stripe
      border
      v-loading="loading"
      :data="data"
      @selection-change="$emit('selection-change', $event)"
      class="custom-bordered-table"
      style="width: 100%"
    >
      <el-table-column type="selection" width="40" align="center"/>
      <el-table-column label="序号" type="index" width="50" align="center">
        <template slot-scope="scope">
          {{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}
        </template>
      </el-table-column>
      <el-table-column label="用户姓名" prop="createBy" align="center" />
      <el-table-column label="用户工号" prop="createId" align="center"/>
      <el-table-column label="用户电话" prop="telephone" align="center"/>
      <el-table-column label="问题标题" prop="title" align="center" :show-overflow-tooltip="true" />
      <el-table-column label="问题内容" prop="questionContent" align="center" :show-overflow-tooltip="true" />
      <el-table-column label="问题状态" prop="status" align="center">
        <template slot-scope="scope">
          <span :class="`status-${scope.row.status}`">
            {{ scope.row.status }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="问题类别" prop="type" width="110" align="center"/>
      <el-table-column label="严重程度" prop="severity" align="center"/>
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button @click="$emit('row-click', scope.row)" type="text" size="small">查看</el-button>
          <el-button @click="$emit('remind', scope.row)" type="text" size="small">提醒</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-show="total > 0"
      class="pagination-wrapper"
      @size-change="$emit('size-change', $event)"
      @current-change="$emit('current-change', $event)"
      :current-page="queryParams.pageNum"
      :page-size="queryParams.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
    />
  </div>
</template>

<script>
export default {
  name: "QaTable",
  props: {
    loading: Boolean,
    data: Array,
    total: Number,
    queryParams: Object
  }
}
</script>
