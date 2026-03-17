<template>
  <MyContainer header-style="padding:20px 0px 0px 20px;">
    <template #header>
      <el-row :gutter="10" class="mb8" style="line-height: 32px">
        <el-button
          size="mini"
          type="primary"
          icon="el-icon-plus"
          @click="handleAdd"
          v-hasPermi="['system:menu:add']"
        >新增</el-button>


        <right-toolbar :showSearch.sync="showSearch" :searchValue.sync="queryParams.searchValue"
                       @queryTable="handleQuery" @resetTable="resetQuery"></right-toolbar>
      </el-row>
    </template>


    <el-table
      v-loading="loading"
      :data="tableData"
      row-key="menuId"
      height="'100%'"
      style="height: 100%;width: 100%"
      border stripe  highlight-current-row fit
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column
        type="selection"
        width="50">
      </el-table-column>

      <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="220"></el-table-column>
      <el-table-column prop="icon" label="图标" align="center" width="100">
        <template slot-scope="scope">
          <svg-icon :icon-class="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" width="60"></el-table-column>
      <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="component" label="路径" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="status" label="状态"  width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.menuType !== 'F' ">{{statusFormat(scope.row)}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime,'{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        label="操作"
        width="185">
        <template slot-scope="scope">
          <el-button size="mini"
                     type="text"
                     icon="el-icon-edit"
                     @click="handleUpdate(scope.row)"
                     v-hasPermi="['system:menu:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleAdd(scope.row)"
            v-hasPermi="['system:menu:add']"
          >新增</el-button>
          <el-popconfirm
            confirm-button-text='删除'
            cancel-button-text='取消'
            popConfirmIcon="el-icon-info"
            color= "#F56C6C"
            title="是否确认删除？"
            width="200"
            @confirm="handleDelete(scope.row)"
          >
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              v-hasPermi="['system:menu:remove']"
              slot="reference"
            >删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent">
      <template v-slot:pre="slot">
        <el-col style="width: 430px">
          <el-form-item label="上级菜单" prop="parentId" >
            <treeselect style="width: 250px"
                        :showCheckedStrategy="true" @visible-change="visibleChange"
                        :normalizer="my_normalizer" @select="mySelect" :flat="true"
                        v-model="menuId"   :options="menuOptions"   placeholder="请选择菜单"/>

          </el-form-item>
        </el-col>
      </template>

    </OpenBase>



  </MyContainer>
</template>

<script>
import {listMenu, getMenu, delMenu, addMenu, updateMenu, treeselect} from "@/api/system/menu";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTreeTable from "@/components/control/treeTable";
import BtnList from "@/components/BtnList";
import Treeselect from "@riophae/vue-treeselect";
import {listField} from "@/api/system/field";
import ComText from "@/components/control/text";

export default {
  name: "Menu",
  components: {ComText, ComTreeTable,BtnList, OpenBase, MyContainer,Treeselect},
  mixins: [mixin],
  data() {
    return {
      //列地址
      columnUrl: "/system/menu/index",
      //表头数据
      tableColumns:[],
      //表数据
      tableData: [],
      //请求基础参数
      baseParams: {path: "/system/menu/index"},
      queryParams: {},
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      //表头搜索显示
      showSearch: false,
      //状态数据字典
      statusOptions: [],
      menuOptions:[],
      menuId:0,

    };
  },
  created() {
    //this.getTableColumns();
    this.getTableData();
    this.getDicts("sys_normal_disable").then(response => {
      this.statusOptions = response.data;
      let actions = [];
      Object.keys(this.statusOptions).some((key) => {
        if (this.statusOptions[key].value == ('0')) {
          actions.push(this.statusOptions[key].label);
          return true;
        }
      })
    });
    this.getMenuList();
  },
  methods: {
    /**
     * 格式化下来列表的禁用状态
     * @param node
     * @returns {{children, id, label, isDisabled: (boolean)}}
     */
    my_normalizer(node) {
      return {
        id: node.id,
        label: node.label,
        children: node.children
      }
    },
    /**
     * 获取菜单下拉框
     */
    getMenuList() {
      treeselect().then(res => {
        //this.menuOptions = ;
        const menu = { id: 0, label: '主类目', children: res.data };
        //menu.children = this.handleTree(res.data, "menuId");
        this.menuOptions.push(menu);
      });
    },
    /**
     * 下拉列表选中
     * @param node
     */
    mySelect(node) {



    },
    // 选择图标
    selected(name) {
      this.form.icon = name;
    },
    /** 查询菜单列表 */
    getTableData() {
      this.loading = true;
      listMenu(this.queryParams).then(response => {
        this.tableData = this.handleTree(response.data, "menuId");
        this.loading = false;
      });
    },
    // 菜单状态字典翻译
    statusFormat(row, column) {

      if (row.menuType === "F") {
        return "";
      }
      return this.selectDictLabel(this.statusOptions, row.status);
    }
    /**
     * 获取表头
     */
    /*getTableColumns() {
      listField({'path': this.columnUrl}).then(res => {
        this.tableColumns = res.rows;
      })
    },*/
    ,
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
      this.queryParams={}
      this.getTableData();
    },
    /**
     *
     * 根据名称调用函数
     *
     * */
    callFunction(functionName, item, data) {
      let methods = this.$options.methods
      const _this = this;
      //console.log(methods,functionName)
      methods[functionName](_this, item, data)
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      const _this = this;
      const item = {
        title: "添加菜单",
        openType: "dialog",//弹出类型
        direction: "rtl",//弹出位置
        lines: 2,
        formId: "/system/menu/add",
        submitEventName: "submitForm"
      }
      let parentId = 0;
      if (row != null && row.menuId) {
        parentId = row.menuId;
      }
      this.menuId = parentId
      _this.setFormItem(_this, item)
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      const _this = this;
      const item = {
        title: "修改菜单",
        openType: "dialog",//弹出类型
        direction: "rtl",//弹出位置
        lines: 2,
        formId: "/system/menu/add",
        submitEventName: "submitForm"
      }
      const menuId = row.menuId
      this.menuId = row.parentId
      getMenu(menuId).then(response => {
        const data = response.data
        _this.setFormItem(_this, item, data)
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const _this = this;
      delMenu(row.menuId).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })
    },
    /** 提交按钮 */
    submitForm(_this) {
      _this.form.fields.parentId = _this.menuId

      if (_this.form.fields.menuId != undefined) {
        updateMenu(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.getTableData()
        });
      } else {
        addMenu(_this.form.fields).then(res => {
          _this.msgSuccess("新增成功");
          _this.form.open = false;
          _this.getTableData()
        });
      }
    },
    /**
     * 表单提交按钮事件
     * @param fnName 提交事件名称
     */
    submitEvent(fnName){
      this.callFunction(fnName)
    },
    /**
     * 自定义按钮禁用状态
     * @param args
     * @param callback
     */
    disableFn(args,callback){
      const {item,checkData} = {...args}
      //控制添加按钮
      /* if(item.btnId === 'add'){
         callback(this.deptId === '');
       }*/
    },
    fixedStatus(location){

      if(location === 'false' || location === '' ){
        return false
      }
      return location
    },
  visibleChange(){
    console.log(111)
  }

  }
}
</script>
