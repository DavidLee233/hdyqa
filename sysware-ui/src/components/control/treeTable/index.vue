<template>
  <MyContainer>
    <template #header v-if="showHeader">
      <el-row :gutter="10" class="mb8" style="line-height: 32px">
        <btn-list :buttonArray="headerBtnArr"
                  :checkList="checkList"
                  :check-data="null"
                  @click="callFunction"
                  @disableFn="disableFn"
        ></btn-list>
        <slot name="headerOperation"></slot>

        <right-toolbar v-if="showToolbar" :showToolbarBtn="showToolbarBtn" :showSearch.sync="showSearch" :searchValue.sync="queryParams.searchValue"
                       @queryTable="handleQuery" @resetTable="resetQuery"></right-toolbar>
      </el-row>
    </template>

  <el-table
    height="'100%'" v-loading="loading" style="width: 100%;height:100%;"
    :data="tableData"   ref="table" border stripe  highlight-current-row fit
    element-loading-text="拼命加载中"
    element-loading-spinner="el-icon-loading"
    element-loading-background="rgba(0, 0, 0, 0.8)"
    :lazy="lazy" :default-expand-all="defaultExpandAll"
    :row-key="rowKey" :load="load"  :expand-row-keys="openId"
    :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    @selection-change="handleSelectionChange" @sort-change="handleSortChange"
    @header-dragend="headerDragend" @select="headerSelect" @row-dblclick="handleDblclick"
    @row-click="headerRowClick" @expand-change="expandChange"
    >
    <el-table-column v-if="selection"  type="selection"
                     width="45" fixed >
    </el-table-column>
    <template  v-for="item in tableColumns">

      <template v-if="item.visible !=='1'">

        <el-table-column v-if="item.prop === 'rowNum' " :key="item.prop"
                         :prop="item.prop"
                         :fixed="fixedStatus(item.fixedLocation)"
                         :width="item.width"
                         :label="item.label"
                         show-overflow-tooltip
                         type=""
        ></el-table-column>

        <el-table-column v-else :key="item.prop" :sortable="item.sortable==='1'?'custom':false"
                       :prop="item.prop"
                       :min-width="item.width"
                       :label="item.label"
                       :fixed="fixedStatus(item.fixedLocation)"
                        show-overflow-tooltip

        >
              <template slot-scope="scope">
                <slot v-if="item.slotName" :name="item.slotName"  :data="scope.row" :prop="item.prop"></slot>
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
            <el-input v-else v-model="queryParams[changeSearchProp(item.prop)]" size="mini" style="width: 80%"  @change="handleQuery"
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
        <el-row :gutter="5"  >
          <btn-list :buttonArray="operationBtnArr"
                    :checkList="checkList"
                    :check-data="scope.row"
                    @click="callFunction"
                    @disableFn="disableFn"
          ></btn-list>
        </el-row>
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
import BtnList from "@/components/BtnList";
import {listPageBtn} from "@/api/system/pageBtn";


const modules = {}
const files = require.context("../",true,/\index.vue$/)
files.keys().forEach(item =>{
  const key = item.split("/")
  const name = key[1]
  modules[`com-${name}`] = files(item).default

})

export default {
  name: "ComTreeTable",
  components: {BtnList, MyContainer,...modules},
  data(){
    return {
      tableColumns:[],
      tableData: [],
      search:"",
      loading:true,
      pageSize:20,
      showSearch:false,
      queryParams:this.params,
      headerBtnArr:[],
      operationBtnArr:[],
      operation: false,
      operationWidth:150,
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
    params:{
      type:Object,
      default:() =>({})
    },
    selection:Boolean,
    index:Boolean,
    //operation:Boolean,
    lazy:Boolean,
    defaultExpandAll:Boolean,
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
    /*operationWidth:{
      type:[Number,String],
      default:150
    },
    headerBtnArr:{
      type:Array,
      default:() => []
    },
    operationBtnArr:{
      type:Array,
      default:() => []
    },*/
    openId:{
      type:Array,
      default:()=>[]
    },
    rowKey:{
      type:String,
      default:"id"
    }
  },
  created() {
    this.getButtons();
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
    });
    //父组件可以调用子组件的方法
    this.$nextTick(() => {
      this.$on("refreshChildren",(id) =>{
        this.refreshChildren(id);
      })
    })
    //父组件可以调用子组件的方法
    this.$nextTick(() => {
      this.$on("clearSelection",(id) =>{
        this.clearSelection(id);
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
      listField({path: this.columnUrl}).then(res => {
        this.tableColumns = res.rows;
      })

    },
    /**
     *
     * 加载数据
     * */
    load(tree, treeNode, resolve){
      resolve(tree.children)

     // this.getChildList();
      /*this.loading = true;
      request({
        url: this.dataUrl,
        method: 'get',
        params: {parentId:tree.id,path: this.columnUrl}
      }).then(res => {
        resolve(res.data)
        this.$set(this.$refs.table.store.states.lazyTreeNodeMap, tree.id, res.data);
        this.loading = false;
      });*/
    },
    refreshChildren(id,rowNum){
      this.getChildList(id,rowNum);
    },
    getChildList(id,rowNum){
      this.loading = true;
      request({
        url: this.dataUrl,
        method: 'get',
        params: {parentId:id,path: this.columnUrl}
      }).then(res => {
        this.setRowNum(res.data,rowNum)

        this.$set(this.$refs.table.store.states.lazyTreeNodeMap, id, res.data);
        this.loading = false;
      });
    },
    clearSelection(){
      this.$refs.table.clearSelection();
    }
    ,
    /**
     * 获取表数据
     */
    getTableData(){
      console.log(this.queryParams)
      this.loading = true;
      this.queryParams = Object.assign({}, this.queryParams, this.baseParams);
      //this.queryParams.path = this.baseParams.path;
     //console.log("getTableData",this.dataUrl)
      this.queryParams.pageNum = 1;
      this.queryParams.pageSize = this.pageSize;
      if(!this.showPager){
        this.queryParams.pageSize = 100
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

        this.tableData = res.data || res.rows;
        //this.tableData = this.handleTree(res.data, "menuId");
        this.loading = false;
        this.setRowNum(this.tableData);

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
      this.queryParams=this.params;
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
      this.$emit("select",selection,row);
    },
    headerRowClick(row, column, event){
     this.clearSelection();
      if (row) {
        this.$refs.table.toggleRowSelection(row);
      }
      this.$emit("rowClick",row,column);
    },
    handleDblclick(row, column, event){
      //this.$parent.dblclick();
    },
    callFunction(functionName,item,data){
      this.$emit("click",functionName,item,data);
    },
    fixedStatus(location){

      if(location === 'false' || location === '' ){
        return false
      }
      return location
    },
    disableFn(args,callback){
      this.$emit('disableFn',args, val => {callback(val)})
    },
    /**
     * 获取按钮
     */
    getButtons(){
      listPageBtn({searchField:"pagePath",'pagePath':this.columnUrl}).then(res =>{
        const btnArr = res.rows;
        this.headerBtnArr =   btnArr.filter(item => item.location ==='header')
        this.operationBtnArr = btnArr.filter(item => item.location ==='operation')
        if(this.operationBtnArr.length > 0){
          this.operation = true
          this.operationWidth = this.operationWidthFn(this.operationBtnArr)
        }

      })
    },
    /**
     * 计算操作列的宽度
     * @param arr
     */
    operationWidthFn(arr){
      let textCount = 0
      if(arr){
        arr.map(i =>{
          if(i.showText !== "1"){
            textCount = textCount + i.text.length
          }
        })
      }
      return arr? arr.length * 30 + textCount * 12 + 20 : 0;
    },
    setRowNum(dataList, parentNum) {
      dataList.forEach((item,i) => {
        item.rowNum = i + 1
        if (item.children && item.children.length > 0){
          this.setRowNum(item.children)
        }
      })

      /*if(parentNum === undefined){
        dataList.forEach((item,i) => {
          item.rowNum = i + 1
          if (item.children && item.children.length > 0){
            this.setRowNum(item.children,  item.rowNum)
          }
        })
      }else{
        dataList.forEach((item,i) => {
          item.rowNum = parentNum + "." + (i + 1)
          if (item.children && item.children.length > 0){
            this.setRowNum(item.children,  item.rowNum)
          }
        })
      }*/
    },
    expandChange(row,expanded){
      if(expanded){
        this.refreshChildren(row[this.rowKey],row.rowNum);
        //this.$set(this.$refs.table.store.states.lazyTreeNodeMap, row.id, row.children);
      }
    },
    /**
     * 修改显示的字段属性
     * @returns {string}
     */
    changeSearchProp(prop){
      if(prop.endsWith("Show")){
        return prop.substring(0,prop.length - 4)
      }
      return prop
    }

  }
}
</script>

<style scoped>

</style>
