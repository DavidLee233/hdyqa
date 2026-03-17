<template>
  <MyContainer>

    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction"
                 @disableFn="disableFn"
                selection index operation >

      <template v-slot:headerOperation="slot">
        <el-col :span="1.5">
          <treeselect style="width: 300px" :showCheckedStrategy="true"  :normalizer="my_normalizer" @select="mySelect" :flat="true"
                      v-model="menuId"  :options="menuOptions"  :show-count="true" placeholder="请选择页面"/>
        </el-col>
        <el-col :span="1.5">
          <span>{{menu.path}}</span>
        </el-col>
      </template>


      <template  v-slot:input="slot">
        <el-input
          size="small"
          v-model="slot.data[slot.prop]"
          @change="handleValueChange($event,slot.prop,slot.data)" ></el-input>
      </template>
      <template v-slot:checkBox="slot">
        <el-checkbox true-label="1" false-label="0" v-model="slot.data[slot.prop]" @change="handleValueChange($event,slot.prop,slot.data)"></el-checkbox>
      </template>

    </com-table>

    <OpenBase ref="baseForm" :baseForm="form"  @submit="submitEvent"></OpenBase>

  </MyContainer>
</template>

<script>
import {getPageBtn, delPageBtn, addPageBtn, updatePageBtn,initBtn } from "@/api/system/pageBtn";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {pageselect} from "@/api/system/menu";

export default {
  name: "PageBtn",
  components: {ComTable, OpenBase, MyContainer,Treeselect},
  mixins: [mixin],
  data() {
    return {
      //列地址
      columnUrl: "/system/pageBtn/index",
      //数据地址
      dataUrl: "/system/pageBtn/list",
      //请求基础参数
      baseParams: {searchField:"pagePath",pagePath:'/',path: "/system/pageBtn/index"},
      // 按钮loading
      buttonLoading: false,
      //复制的数据
      copyData:undefined,
      //初始化菜单
      menu:{},
      //菜单ID
      menuId:null,
      //菜单项
      menuOptions:[],

    };
  },
  created() {
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
        children: node.children,
        isDisabled: node.menuType == 'M' ? true : false
      };
    },
    /**
     * 下拉列表选中
     * @param node
     */
    mySelect(node) {

      this.baseParams.menuId = node.id;
      this.selectMenu = false;

      this.menu = node;
      this.menu.path = '/'+node.component;
      this.baseParams.pagePath = '/'+node.component;

      this.getTableData();

    },
    /**
     * 获取菜单下拉框
     */
    getMenuList() {
      pageselect().then(res => {
        this.menuOptions = res.data;
      });
    },
    /**
     *
     * 根据名称调用函数
     *
     * */
    callFunction(functionName, item, data) {
      let methods = this.$options.methods
      const _this = this;
      methods[functionName](_this, item, data)
    },
    /** 新增按钮操作 */
    handleAdd(_this, item, data) {
      _this.setFormItem(_this, item,  _this.copyData,
        {pageName: _this.menu.label,
        menuId: _this.menu.id,path: _this.menu.path})
    },
    /**
     * 复制按钮
     * */
    handleCopy(_this){
      _this.copyData = _this.checkList[0];
      _this.copyData.id = undefined
    },
    /** 修改按钮操作 */
    handleUpdate(_this, item, row) {
      if (!row) {
        row = _this.checkList[0];
      }
      const id = row.id
      getPageBtn(id).then(response => {
        const data = response.data
        _this.setFormItem(_this, item, data)
      });
    },
    /** 删除按钮操作 */
    handleDelete(_this, item, row) {

      let ids = [];
      if (row) {
          ids.push(row.id)
      } else {
          ids = _this.checkList.map(item => item.id)
      }
      delPageBtn(ids.join(",")).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {
      if (_this.form.fields.id) {
        updatePageBtn(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.getTableData()
        });
      } else {
        _this.form.fields.pagePath = _this.menu.path;
        addPageBtn(_this.form.fields).then(res => {
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
     *
     * 初始化按钮事件
     * */
  handleInit(_this){
    initBtn().then(res => {
      _this.msgSuccess("初始化成功")
    })
  },


  /**
   * 改变值的时候更新数据
   */
  handleValueChange(value,prop,data){
    this.form_fields = data;
    this.form_fields[prop] = value

    updatePageBtn(this.form_fields).then(res => {
      this.msgSuccess("更新成功")
    })
  },
    /**
     * 按钮禁用回调事件
     * @param args
     * @param callback
     */
  disableFn(args,callback){
    const {item,checkData} = {...args}
      //控制添加按钮zh
      if(item.btnId === 'add'){
        callback(this.menu.path === undefined)
      }

  },



  }
}
</script>
