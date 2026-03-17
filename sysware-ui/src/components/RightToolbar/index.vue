<template>
  <div class="top-right-btn">
    <el-row>

      <slot  name="searchField"></slot>
      <el-input
        v-model="searchText"
        placeholder="请输入关键字"
        clearable
        size="small"
        style="width: 240px;margin-right: 5px;"
        @keyup.enter.native="search"
      />
      <template v-if="showToolbarBtn">
        <el-tooltip class="item" effect="dark" content="搜索" placement="top">
          <el-button size="mini" circle icon="el-icon-search" @click="search()" />
        </el-tooltip>

        <el-tooltip class="item" effect="dark" :content="showSearch ? '隐藏列搜索' : '显示列搜索'" placement="top">
          <el-button size="mini" circle icon="el-icon-view" @click="toggleSearch()" />
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="重置刷新" placement="top">
          <el-button size="mini" circle icon="el-icon-refresh" @click="refresh()" />
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="显隐列" placement="top" v-if="columns">
          <el-button size="mini" circle icon="el-icon-menu" @click="showColumn()" />
        </el-tooltip>
      </template>
    </el-row>

    <el-dialog :title="title" width="30%"  :visible.sync="open" append-to-body>
      <el-transfer
        :titles="['显示', '隐藏']"
        v-model="value"
        :data="columns"
        @change="dataChange"
      ></el-transfer>
    </el-dialog>
  </div>
</template>
<script>
export default {
  name: "RightToolbar",
  data() {
    return {
      // 显隐数据
      value: [],
      // 弹出层标题
      title: "显示/隐藏",
      // 是否显示弹出层
      open: false,
      searchText: ''
    };
  },
  props: {
    showSearch: {
      type: Boolean,
      default: true
    },
    showToolbarBtn: {
      type: Boolean,
      default: true
    },
    searchValue: {
      type: String,
      default: ''
    },
    columns: {
      type: Array,
      default:()=>[]
    },
  },
  created() {
    // 显隐列初始默认隐藏列
    for (let item in this.columns) {
      if (this.columns[item].visible === false) {
        this.value.push(parseInt(item));
      }
    }

  },
  methods: {
    // 搜索
    toggleSearch() {
      this.$emit("update:showSearch", !this.showSearch);
    },
    // 刷新
    search() {
      this.$emit("update:searchValue", this.searchText);
      this.$emit("queryTable");
    },
    // 重置
    refresh() {
      this.$emit("update:searchValue", "");
      this.searchText="";
      this.$emit("resetTable");
    },
    // 右侧列表元素变化
    dataChange(data) {
      for (var item in this.columns) {
        const key = this.columns[item].key;
        this.columns[item].visible = !data.includes(key);
      }
    },
    // 打开显隐列dialog
    showColumn() {
      this.open = true;
    }
  },
};
</script>
<style lang="scss" scoped>
::v-deep .el-transfer__button {
  border-radius: 50%;
  padding: 12px;
  display: block;
  margin-left: 0px;
}
::v-deep .el-transfer__button:first-child {
  margin-bottom: 10px;
}
</style>
