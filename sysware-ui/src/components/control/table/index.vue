<template>
  <MyContainer show-footer>
    <template #header v-if="showHeader">
      <slot name="top"></slot>
      <el-row :gutter="10" class="mb8" style="line-height: 32px">
        <slot name="headerBefore"></slot>
        <btn-list :buttonArray="headerBtnArr"
                  :checkList="checkList"
                  :check-data="null"
                  @click="callFunction"
                  @disableFn="disableFn"
        ></btn-list>
        <slot name="headerOperation"></slot>

        <right-toolbar v-if="showToolbar"  :showToolbarBtn="showToolbarBtn" :showSearch.sync="showSearch" :searchValue.sync="queryParams.searchValue"
                       @queryTable="handleQuery" @resetTable="resetQuery">
            <template v-slot:searchField="slot">
              <slot name="searchField">
              </slot>
            </template>
        </right-toolbar>
      </el-row>
    </template>

  <el-table
    height="'100%'" v-loading="loading" style="width: 100%;height:100%;"
    :data="tableData"   ref="table" border stripe  highlight-current-row fit
    @selection-change="handleSelectionChange" @sort-change="handleSortChange"
    @header-dragend="headerDragend" @select="headerSelect" @row-dblclick="handleDblclick"
    @row-click="headerRowClick"
    >
    <el-table-column v-if="isShowSelection()"   type="selection"
                       width="45" fixed >
    </el-table-column>

    <el-table-column v-if="isShowIndex()"  type="index"
                      width="50" label="序号" fixed>
    </el-table-column>
    <template  v-for="item in tableColumns">
      <template v-if="item.visible !=='1'">
          <el-table-column :key="item.prop" :sortable="item.sortable==='1'?'custom':false"
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

          <template v-if="(showSearch || item.showSearch === '1') && item.searchable==='1'">
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
                @change="handleQuery"
                :picker-options="pickerOptions"
              ></el-date-picker>

            </div>
            <div  v-if="item.type ==='select'">
              <el-select v-model="queryParams[updateSelectProp(item.prop)]" filterable default-first-option
                         size="mini" :multiple="item.isMultiple==='1'?true:false"
                         clearable collapse-tags @change="handleQuery" :placeholder="'请选择'+item.label">
                <el-option
                  v-for="item in optionMap[item.prop]"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
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
        <el-row :gutter="5" >
          <btn-list :buttonArray="operationBtnArr"
                    :checkList="checkList"
                    :check-data="scope.row"
                    @click="callFunction"
                    @disableFn="disableFn"
          ></btn-list>
        </el-row>
      </template>
    </el-table-column>
    <slot name="tableOperation"></slot>
  </el-table>
    <template #footer v-if="showPager">

      <my-pagination
        ref="pager"
        :total="total"
        :page-size="pageSize"
        :checked-count="checkList.length"
        :layout="pagerLayout"
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
  name: "ComTable",
  components: {BtnList, MyContainer,...modules},
  data(){
    return {
      tableColumns:[],
      tableData: [],
      search:"",
      loading:true,
      pageSize:20,
      showSearch:false,
      headerBtnArr:[],
      queryParams:this.params,
      operationBtnArr:[],
      operation: false,
      operationWidth:150,
      resourceData:[],
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
      optionMap:{},
      options:{

      },
      hasOption:false
    }

  },
  props:{
    columnUrl:{
      type:String,
      default:""
    },
    columnParams:{
      type:Object,
      default:null
    },
    dataList:{
      type:Array,
      default:()=>[]
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
    pagerLayout:{
      type: String,
      default: "full"
    },
    method:{
      type:String,
      default: "get"
    },
    definitionFilter:{
      type:Boolean,
      default:false
    },
    filterColumn:{
      type:String,
      default:""
    },

  },
  created(){
    this.getButtons();
    this.getTableColumns();
  },
  beforeMount() {
    this.getTableData();
  },
  destroyed() {
  },
  mounted() {

    //父组件可以调用子组件的方法
    this.$nextTick(() => {
      this.$on("getTableData",() =>{
        this.getTableData();
      })
    })

    //父组件可以调用子组件的方法
    this.$nextTick(() => {
      this.$on("getTableColumns",() =>{
        this.getTableColumns();
      })
    })

    //父组件可以调用子组件的方法
    this.$nextTick(() => {
      this.$on("clearData",() =>{
        this.clearData();
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
    },
    "dataList" : {
      handler(){
        this.tableData = this.dataList
        this.total = this.dataList.length;
        this.resourceData = Object.assign([],this.dataList);
      },
      deep:true
    }
  },
  methods:{
    /**
     * 获取表头
     */
    getTableColumns() {
      let queryParams ={'path': this.columnUrl};
      if(this.columnParams){
        queryParams = this.columnParams
      }
      listField(queryParams).then(res => {
        this.tableColumns = res.rows;


        //获取配置的数据字典
        this.tableColumns.forEach(item => {
          if(item.type === 'select'){

            this.getDicts(item.dictType).then(response => {
              let dictOptions = response.data;
              let options = [];
              Object.keys(dictOptions).some((key) => {
                options.push({value:dictOptions[key].dictValue,label:dictOptions[key].dictLabel});
              })
              this.$set(this.optionMap,item.prop,options)
            });
          }

        });
      })

    },
    /**
     * 获取页面按钮
     * */
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
     * 获取表数据
     */
    getTableData(){
      this.loading = true;

      //如果有自定义过滤，就不用重新请求数据
      if(this.definitionFilter && this.filterColumn !== "" &&  this.resourceData.length > 0){
         this.handleDefinitionFilter();
         return
      }

      //没有请求的数据地址则不需要请求数据
      if( this.dataUrl === ""){
        this.loading = false;
        return
      }

      //处理请求参数
      this.handleQueryParams()


      if("get" === this.method){
        request({
          url: this.dataUrl,
          method: this.method,
          params: this.queryParams
        }).then(res => {
          if(res !== null || res !== undefined){
            this.tableData = res.data || res.rows;
            this.resourceData = Object.assign([],this.tableData);
            this.total = res.total ? res.total:this.tableData.length;
            this.loading = false;
          }
        }).then(() =>{
          this.$nextTick(() => {
            this.$refs.table.doLayout();
          })
        })
      }else {
        request({
          url: this.dataUrl,
          method: this.method,
          data: this.queryParams
        }).then(res => {
          if(res !== null || res !== undefined) {
            this.tableData = res.data || res.rows;
            this.resourceData = Object.assign([],this.tableData);
            this.total = res.total ? res.total:this.tableData.length;
            this.loading = false;
          }
        }).then(() =>{
          this.$nextTick(() => {
            this.$refs.table.doLayout();
          })
        })
      }


    },
    /**
     * 处理自定义的过滤器
     * */
    handleDefinitionFilter(){

      if(this.queryParams.searchValue){
        this.tableData = this.resourceData.filter(item =>
          item[this.filterColumn].includes(this.queryParams.searchValue)
        );
      }else{
        this.tableData = this.resourceData;
      }

      this.total = this.tableData.length;
      this.loading = false;
    },
    /**
     * 处理请求参数
     * */
    handleQueryParams(){
      this.queryParams = Object.assign({}, this.queryParams, this.baseParams);
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
      this.$emit("select",selection,row);
    },
    headerRowClick(row, column, event){
      this.$refs.table.clearSelection();
      if (row) {
        this.$refs.table.toggleRowSelection(row);
      }
      this.$emit("rowClick",row,column);
    },
    handleDblclick(row, column, event){
      this.$emit("dblclick",row,column,event);
    },
    /**
     *
     * 调用父组件的 click 函数
     * */
    callFunction(functionName,item,data){
      this.$emit("click",functionName,item,data);
    },
    /**
     * 调用父组件的 disableFn 自定义按钮禁用函数
     * @param args
     * @param callback
     */
    disableFn(args,callback){
      this.$emit('disableFn',args, val => {callback(val)})
    },
    /**
     * 字段是否固定
     * @param location
     * @returns {boolean|*}
     */
    fixedStatus(location){
      if(location === 'false' || location === '' ){
        return false
      }
      return location
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
    /**
     *是否显示选中列
     */
    isShowSelection(){

      return this.selection || this.tableColumns.filter(item => item.prop === 'selection').length > 0

    },
    /**
     * 是否显示索引列
     * @returns {Boolean|boolean}
     */
    isShowIndex(){
      return this.index || this.tableColumns.filter(item => item.prop === 'index').length > 0
    },
    /**
     * 清除数据
     */
    clearData(){
      this.tableData = [];
      this.resourceData = [];
      this.checkList  = [];
    },
    /**
     *
     */
    updateSelectProp(prop){
      if(prop.endsWith("Label")){
        console.log(111111111,prop)
       return  prop.replace("Label","");
      }
      return prop;
    }

  },
  computed:{

  }
}
</script>

<style scoped>

</style>
