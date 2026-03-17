<template>
  <MyContainer>

    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction" :headerBtnArr="headerBtnArr"
                :operationBtnArr="operationBtnArr"
                 operation :operation-width="operationWidth(operationBtnArr)">

    </com-table>
    <import-table ref="import" @ok="handleQuery" />

    <el-dialog :title="preview.title" :visible.sync="preview.open" width="80%" append-to-body>
      <el-tabs v-model="preview.activeName">
        <el-tab-pane
          v-for="(value, key) in preview.data"
          :label="key.substring(key.lastIndexOf('/')+1,key.indexOf('.vm'))"
          :name="key.substring(key.lastIndexOf('/')+1,key.indexOf('.vm'))"
          :key="key"
        >
          <pre><code class="hljs" v-html="highlightedCode(value, key)"></code></pre>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </MyContainer>

</template>

<script>
import { listTable, previewTable, delTable, genCode, synchDb } from "@/api/tool/gen";
import importTable from "./importTable";
import { downLoadZip } from "@/utils/zipdownload";
const hljs = require("highlight.js/lib/core");
import "highlight.js/styles/github-gist.css";
import MyContainer from "@/components/my-container";
import ComTable from "@/components/control/table";
import {mixin} from "@/views/base";
hljs.registerLanguage("java", require("highlight.js/lib/languages/java"));
hljs.registerLanguage("xml", require("highlight.js/lib/languages/xml"));
hljs.registerLanguage("html", require("highlight.js/lib/languages/xml"));
hljs.registerLanguage("vue", require("highlight.js/lib/languages/xml"));
hljs.registerLanguage("javascript", require("highlight.js/lib/languages/javascript"));
hljs.registerLanguage("sql", require("highlight.js/lib/languages/sql"));

export default {
  name: "Gen",
  components: {ComTable, MyContainer, importTable },
  mixins:[mixin],
  data() {
    return {

      //列地址
      columnUrl:"/tool/gen/index",
      //数据地址
      dataUrl:"/tool/gen/list",
      //请求基础参数
      baseParams:{path:"/tool/gen/index"},
      // 遮罩层
      loading: true,
      // 唯一标识符
      uniqueId: "",
      // 选中数组
      ids: [],
      // 选中表数组
      tableNames: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表数据
      tableList: [],
      // 日期范围
      dateRange: "",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        tableName: undefined,
        tableComment: undefined
      },
      // 预览参数
      preview: {
        open: false,
        title: "代码预览",
        data: {},
        activeName: "domain.java"
      },
      headerBtnArr:[
        {
          btnId:"add",
          type:"primary",
          icon:"el-icon-download",
          size:"mini",
          location:"header",
          text:"生成",
          showText:true,
          permission:['tool:gen:code'],
          title:"添加用户",
          openType:"dialog",//弹出类型
          direction:"rtl",//弹出位置
          lines:1,
          formId:"/system/user/add",
          submitUrl:"/system/user",
          submitEventName:"submitForm",
          method:"post",
          disabledType:"single",
          functionName:"handleGenTable"
        },
        {
          btnId:"import",
          type:"info",
          icon:"el-icon-upload",
          size:"mini",
          location:"header",
          text:"导入",
          showText:true,
          permission:['tool:gen:import'],
          title:"修改用户",
          openType:"dialog",//弹出类型
          direction:"",//弹出位置
          lines:1,
          formId:"/system/user/edit",
          submitEventName:"submitForm",
          disabledType:"",
          functionName:"openImportTable"
        },
        {
          btnId:"edit",
          type:"success",
          icon:"el-icon-edit",
          size:"mini",
          location:"header",
          text:"修改",
          showText:true,
          permission:['tool:gen:edit'],
          disabledType:"single",
          functionName:"handleEditTable",
          title:"修改用户",
          openType:"dialog",//弹出类型
          direction:"",//弹出位置
          lines:1,
          formId:"/system/user/edit",
          submitEventName:"submitForm"
        },
        {
          btnId:"delete",
          type:"danger",
          icon:"el-icon-delete",
          size:"mini",
          location:"header",
          text:"删除",
          showText:true,
          permission:['tool:gen:remove'],
          disabledType:"multiple",
          functionName:"handleDelete",
          title:"修改用户",
          openType:"dialog",//弹出类型
          direction:"",//弹出位置
          lines:1,
          formId:"/system/user/edit",
          submitEventName:"submitForm"
        }
      ],
      operationBtnArr:[
        {
          btnId:"preview",
          type:"text",
          icon:"el-icon-view",
          size:"mini",
          location:"header",
          text:"预览",
          showText:true,
          permission:['tool:gen:preview'],
          disabledType:"",
          functionName:"handlePreview",
          title:"修改用户",
          lines:1,
          openType:"dialog",//弹出类型
          formId:"/system/user/edit",
          submitEventName:"submitForm"
        },
        {
          btnId:"edit",
          type:"text",
          icon:"el-icon-edit",
          size:"mini",
          location:"header",
          text:"编辑",
          showText:true,
          permission:['tool:gen:edit'],
          functionName:"handleEditTable",
          title:"修改用户",
          lines:1,
          openType:"dialog",//弹出类型
          formId:"/system/user/edit",
          submitEventName:"submitForm"
        },
        {
          btnId:"delete",
          type:"text",
          icon:"el-icon-delete",
          size:"mini",
          location:"header",
          text:"删除",
          showText:true,
          permission:['tool:gen:remove'],
          functionName:"handleDelete",
          title:"修改用户",
          lines:1,
          openType:"dialog",//弹出类型
          formId:"/system/user/edit",
          submitEventName:"submitForm"
        },
        {
          btnId:"refresh",
          type:"text",
          icon:"el-icon-refresh",
          size:"mini",
          location:"header",
          text:"同步",
          showText:true,
          permission:['tool:gen:edit'],
          functionName:"handleSynchDb",
          title:"修改用户",
          lines:1,
          openType:"dialog",//弹出类型
          formId:"/system/user/edit",
          submitEventName:"submitForm"
        },
        {
          btnId:"download",
          type:"text",
          icon:"el-icon-download",
          size:"mini",
          location:"header",
          text:"生成代码",
          showText:true,
          permission:['tool:gen:code'],
          functionName:"handleGenTable",
          title:"修改用户",
          lines:1,
          openType:"dialog",//弹出类型
          formId:"/system/user/edit",
          submitEventName:"submitForm"
        }
      ]
    };
  },
  created() {
    this.getList();
  },
  activated() {
    const time = this.$route.query.t;
    if (time != null && time != this.uniqueId) {
      this.uniqueId = time;
      this.resetQuery();
    }
  },
  methods: {
    /** 查询表集合 */
    getList() {
      this.loading = true;
      listTable(this.queryParams).then(response => {
          this.tableList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 生成代码操作 */
    handleGenTable(_this,item,row) {
     if(!row){
       row = _this.checkList[0]
     }
      const tableNames = row.tableName;
      if (tableNames == "") {
        _this.msgError("请选择要生成的数据");
        return;
      }

      if(row.genType === "1") {
        genCode(row.tableName).then(response => {
          _this.msgSuccess("成功生成到自定义路径：" + row.genPath);
        });
      } else {
        downLoadZip("/tool/gen/batchGenCode?tables=" + tableNames, "sysware");
      }
    },
    /** 同步数据库操作 */
    handleSynchDb(_this,item,row) {
      const tableName = row.tableName;
      _this.$confirm('确认要强制同步"' + tableName + '"表结构吗？', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
          return synchDb(tableName);
      }).then(() => {
        _this.msgSuccess("同步成功");
      }).catch(() => {});
    },
    /** 打开导入表弹窗 */
    openImportTable(_this) {
      _this.$refs.import.show();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 预览按钮 */
    handlePreview(_this,item,row) {
      previewTable(row.tableId).then(response => {
        _this.preview.data = response.data;
        _this.preview.open = true;
      });
    },
    /** 高亮显示 */
    highlightedCode(code, key) {
      const vmName = key.substring(key.lastIndexOf("/") + 1, key.indexOf(".vm"));
      var language = vmName.substring(vmName.indexOf(".") + 1, vmName.length);
      const result = hljs.highlight(language, code || "", true);
      return result.value || '&nbsp;';
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.tableId);
      this.tableNames = selection.map(item => item.tableName);
      this.single = selection.length != 1;
      this.multiple = !selection.length;
    },
    /** 修改按钮操作 */
    handleEditTable(_this,item,data) {
      if(!data){
        data = _this.checkList[0]
      }
      const tableId = data.tableId;
      _this.$router.push("/gen/edit/" + tableId);
    },
    /** 删除按钮操作 */
    handleDelete(_this,item,row) {
      let tableIds = [];
      if(row){
        tableIds.push(row.tableId)
      }else{
        tableIds =  _this.checkList.map(item => item.tableId)
      }
      delTable(tableIds).then(res =>{
        _this.getTableData()
        _this.msgSuccess("删除成功");
      })
    },
    callFunction(functionName,item,data){
      let methods = this.$options.methods
      const _this = this;
      methods[functionName](_this,item,data)
    },
  }
};
</script>
