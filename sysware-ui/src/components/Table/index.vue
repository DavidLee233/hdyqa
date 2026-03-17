<template>
  <MyContainer show-footer>
    <template #header v-if="showHeader">
      <el-row :gutter="10" class="mb8" style="line-height: 32px">
        <slot name="headerOperation"></slot>
        <right-toolbar v-if="showToolbar" :showToolbarBtn="showToolbarBtn" :showSearch.sync="showSearch" :searchValue.sync="queryParams.searchValue"
                       @queryTable="handleQuery" @resetTable="resetQuery">
          <template  v-slot:searchField="slot">
            <slot name="searchField"></slot>
          </template>
        </right-toolbar>
      </el-row>
    </template>

  <el-table
    height="'100%'" style="width: 100%;height:100%;"
    :data="tableData"   ref="table" border stripe  highlight-current-row fit
    @selection-change="handleSelectionChange" @sort-change="handleSortChange"
    @header-dragend="headerDragend" @select="headerSelect" @row-dblclick="handleDblclick"
    >
    <el-table-column v-if="selection"  type="selection"
                     width="45" fixed >
    </el-table-column>
    <el-table-column v-if="index"  type="index"
                     width="50" label="序号" fixed>
    </el-table-column>
    <template  v-for="item in tableColumns">

      <template v-if="item.visible==='1'">
        <el-table-column :key="item.prop" :sortable="item.sortable==='1'?'custom':false"
                       :prop="item.prop"
                       :min-width="item.width"
                       :label="item.label"
                       :fixed="item.prop==='operation'?'right':false"
                         show-overflow-tooltip
        >
              <template slot-scope="scope">
                <slot v-if="item.slotname" :name="item.slotname" :config="item" :data="scope.row" :prop="item.prop"></slot>
                <component v-else :is="'com-text'" :value="scope.row[item.prop]"
                           :config="item" :prop="item.prop" ></component>
              </template>
        <template  slot="header" slot-scope="scope">

          <template v-if="showSearch && item.searchable==='1'">
            <div  v-if="item.type==='date'">
              <el-date-picker
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

            </div>

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
    <template #footer v-if="showPager">

      <my-pagination
        ref="pager"
        :total="total"
        :page-size="pageSize"
        :checked-count="checkList.length"
        @get-page="getTableData"
      />
    </template>
  </MyContainer>
</template>



<script>

import {listField} from "@/api/system/field";
import request from '@/utils/request';
import MyContainer from '@/components/my-container';


const modules = {}
const files = require.context("../control",true,/\index.vue$/)
files.keys().forEach(item =>{
  const key = item.split("/")
  const name = key[1]
  modules[`com-${name}`] = files(item).default

})

export default {
  name: "Table",
  components: {MyContainer,...modules},
  data(){
    return {
      tableColumns:[],
      tableData: [],
      search:"",
      pageSize:15,
      showSearch:false,
      queryParams:{},
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
      },
      total:0,

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
      default:() =>[]
    },
    baseParams:{
      type:Object,
      default:() =>({})
    },
    selection:Boolean,
    index:Boolean,
    operation:Boolean,
    showHeader:{
      type:Boolean,
      default:true
    },
    showToolbar:{
      type:Boolean,
      default:true
    },
    showToolbarBtn:{
      type:Boolean,
      default:true
    },
    showPager:{
      type:Boolean,
      default:true
    },
    operationWidth:{
      type:[Number,String],
      default:150
    }
  },
  beforeMount() {
    this.getTableColumns()
    this.getTableData();
  },
  destroyed() {
    console.log("destroyed")
  },
  mounted() {
    //父组件可以调用子组件的方法
    this.$nextTick(() => {
      this.$on("getTableData",() =>{
        this.getTableData();
      })
    })
  },
  updated() {
    this.$nextTick(() => {
      this.$refs.table.doLayout();
    })
  },
  watch:{
    "tableData" : {
      handler(){
        this.$nextTick(() => {
          this.$refs.table.doLayout();
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

      this.queryParams = Object.assign({}, this.queryParams, this.baseParams);
      this.queryParams.pageNum = 1;
      this.queryParams.pageSize = this.pageSize;
      if(!this.showPager){
        this.queryParams.pageSize = 1000
      }

      const pager = this.$refs.pager;
      if (pager) {
        this.queryParams.pageNum = pager.getPager().pageNum;
        this.queryParams.pageSize = pager.getPager().pageSize;
      }
      request({
        url: this.dataUrl,
        method: 'get',
        params: this.queryParams
      }).then(res => {
        if(res){
          if(res.data){
            this.total = res.total;
            this.tableData = res.data;
          }else{
            this.total = res.total;
            this.tableData = res.rows;
          }
        }

      }).then(() =>{
        this.$nextTick(() => {
          this.$refs.table.doLayout();
        })
      })
    },
    /**
     * 搜索数据
     */
    handleQuery(){
      this.getTableData();
    },
    /**
     * 重置搜索
     */
    resetQuery(){
      const pager = this.$refs.pager;
      if (pager) {
        pager.getPager().pageNum = 1;
        pager.getPager().pageSize = this.pageSize
      }
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
      this.queryParams.orderByColumn = params.prop;
      this.queryParams.isAsc = params.order;
      this.getTableData();
    },
    /**
     * 拖动列宽变化
     * @param newWidth
     * @param oldWidth
     * @param column
     * @param event
     */
    headerDragend(newWidth, oldWidth, column, event){
      console.log(newWidth, oldWidth,column,this.columnUrl)
    },
    /**
     * 选中列变化
     */
    headerSelect(selection, row){
      console.log()
    },
    handleDblclick(row, column, event){
      //this.$parent.dblclick();
    }
  }
}
</script>

<style scoped>

</style>
