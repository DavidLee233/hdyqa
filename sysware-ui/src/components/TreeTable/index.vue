<template>
  <MyContainer>
    <template #header>
      <el-row :gutter="10" class="mb8">
        <slot name="headerOperation"></slot>
        <right-toolbar :showSearch.sync="showSearch" :searchValue.sync="queryParams.searchValue"
                       @queryTable="handleQuery" @resetTable="resetQuery"></right-toolbar>
      </el-row>
    </template>

  <el-table
    height="'100%'" style="width: 100%;height:100%;" v-loading="loading"
    :data="tableData" ref="multipleTable" border stripe  highlight-current-row
    element-loading-text="拼命加载中"
    element-loading-spinner="el-icon-loading"
    element-loading-background="rgba(0, 0, 0, 0.8)"
    row-key="id" :load="load"  lazy :expand-row-keys="openId"
    :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    @selection-change="handleSelectionChange" @sort-change="handleSortChange" @select="headerSelect"
    >

    <el-table-column v-if="selection"  type="selection"
                     width="45" fixed >
    </el-table-column>
<!--    <el-table-column prop="index"  type="index"
                     width="50" label="序号"  fixed>
    </el-table-column>-->
    <template  v-for="item in tableColumns">
      <template v-if="item.visible!=='1'">

        <el-table-column v-if="item.prop === 'rowNum' " :key="item.prop"
                         :prop="item.prop"
                         :fixed="fixedStatus(item.fixedLocation)"
                         :width="item.width"
                         :label="item.label"
                         show-overflow-tooltip
                         type=""
        ></el-table-column>

        <el-table-column  v-else :key="item.prop"
                       :prop="item.prop"
                       :fixed="fixedStatus(item.fixedLocation)"
                       :width="item.width"
                       :label="item.label"
                       show-overflow-tooltip
        >
        <template slot-scope="scope">
          <slot v-if="item.slotName" :name="item.slotName" :data="scope.row" :prop="item.prop"></slot>
          <component v-else is="com-text" :data="scope.row"
                     :value="scope.row[item.prop]"
                     :config="item"
                     :prop="item.prop"></component>
        </template>
        <template  slot="header" slot-scope="scope">
          <template v-if="showSearch && item.searchable==='1'">
            <el-date-picker
              v-if="item.dateSelect==='1'"
              v-model="queryParams[item.prop]"
              size="small"
              style="max-width: 220px;"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @click.stop.native=""
              @blur="handleQuery"
              @change="handleQuery"
              :picker-options="pickerOptions"
            ></el-date-picker>
            <el-input v-else v-model="queryParams[item.prop]" size="mini" style="width: 80%"  @change="handleQuery"
                      :placeholder="'请输入'+item.label"/>
          </template>
          <span v-else >{{item.label}}</span>

        </template>
      </el-table-column>
      </template>

    </template>
    <el-table-column
      v-if="operation"
      fixed="right"
      label="操作"
      :width="operationWidth">
      <template slot-scope="scope">
        <slot name="operation" :data="scope.row"></slot>
      </template>
    </el-table-column>

  </el-table>

  </MyContainer>
</template>



<script>

import {listField} from "@/api/system/field";
import request from '@/utils/request';
import MyContainer from '@/components/my-container';
import {selectProjectList} from "@/api/project/project";


const modules = {}
const files = require.context("../control",true,/\index.vue$/)
files.keys().forEach(item =>{
  const key = item.split("/")
  const name = key[1]
  modules[`com-${name}`] = files(item).default

})
export default {
  name: "TreeTable",
  components: {MyContainer,...modules},
  data(){
    return {
      indexNumber:1,
      tableColumns:[],
      tableData: [],
      search:"",
      showSearch:false,
      queryParams:{},
      loading:false,
      pickerOptions: {
        shortcuts: [{
          text: '最近7天',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近3天',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近90天',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近180天',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 180);
            picker.$emit('pick', [start, end]);
          }
        }]
      }
    }

  },
  props:{
    column:{
      type:Array,
      default:()=>[]
    },
    columnUrl:{
      type:String,
      default:""
    },
    dataUrl:{
      type:String,
      default:""
    },
    checkList:{
      type:Array,
      default:() =>{}
    },
    baseParams:{
      type:Object,
      default:() =>({})
    },
    selection:Boolean,
    index:Boolean,
    operation:Boolean,
    operationWidth:{
      type:[Number,String],
      default:150
    },
    openId:{
      type:Array,
      default:()=>[]
    }
  },
  beforeMount() {
    this.getTableColumns()
    this.getTableData();
  },
  watch:{
    "tableData" : {
      handler(){
        this.$nextTick(() => {
          this.$refs.multipleTable.doLayout();
        })
      },
      deep:true
    }
  },
  methods:{
    /**
     * 获取表头
     */
    getTableColumns() {
      listField({'path': this.columnUrl}).then(res => {
        this.tableColumns = res.rows;
      })

    },
    /**
     * 获取表数据
     */
    getTableData(){

      console.log("getTableDataTree")
      this.queryParams = Object.assign({}, this.queryParams, this.baseParams);

      request({
        url: this.dataUrl,
        method: 'get',
        params: this.queryParams
      }).then(res => {
        this.tableData = res.data;
        this.loading = false;
      })
    },
    load(tree, treeNode, resolve){
      this.loading = true;
      request({
        url: this.dataUrl,
        method: 'get',
        params: {parentId:tree.id}
      }).then(res => {
        resolve(res.data)
        this.loading = false;
      });

    }
    ,
    /**
     * 搜索数据
     */
    handleQuery(){
      this.loading = true;
      this.queryParams.parentId = '';
      this.getTableData();
    },
    /**
     * 重置搜索
     */
    resetQuery(){
      this.loading = true;
      this.queryParams={}
      this.getTableData();
    },
    /**
     * 选中行事件
     * @param data
     */
    handleSelectionChange(data){
      this.$emit("update:checkList",data);
    },
    /**
     * 远程排序
     * @param params
     */
    handleSortChange(params) {
      this.loading = true;
      this.queryParams.orderByColumn = params.prop;
      this.queryParams.isAsc = params.order;
      this.getTableData();
    },
    /**
     * 选中列变化
     */
    headerSelect(selection, row){
      this.$emit("select",selection,row);
    },
    fixedStatus(location){

      if(location === 'false' || location === '' ){
        return false
      }
      return location
    }
  }
}
</script>

<style scoped>

</style>
