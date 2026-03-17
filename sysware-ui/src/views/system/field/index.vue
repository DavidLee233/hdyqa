<template>
  <MyContainer>

    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction" @disableFn="disableFn" selection index>

      <template v-slot:headerOperation="slot" >
        <el-col :span="1.5">
          <treeselect style="width: 300px"  size="small" :showCheckedStrategy="true" @visible-change="visibleChange" :normalizer="my_normalizer" @select="mySelect" :flat="true"
                      v-model="menuId"  :options="menuOptions"  :show-count="true" placeholder="请选择页面"/>
        </el-col>
        <el-col :span="1.5">
          <span>{{menu.path}}</span>
        </el-col>
      </template>

      <template v-slot:checkBox="slot" >
        <el-checkbox :disabled="slot.data.onlyShow==='1'" true-label="1" false-label="0" v-model="slot.data[slot.prop]" @change="handleValueChange($event,slot.prop,slot.data)"></el-checkbox>
      </template>

      <template v-slot:number="slot">
        <el-input-number  v-model="slot.data[slot.prop]" @change="handleValueChange($event,slot.prop,slot.data)" :min="1" size="small"></el-input-number>
      </template>
      <template v-slot:input="slot">
        <el-input :disabled="slot.data.onlyShow==='1'" v-model="slot.data[slot.prop]" @change="handleValueChange($event,slot.prop,slot.data)" size="small"></el-input>
      </template>

    </com-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>

  </MyContainer>
</template>

<script>
import { listField, getField, delField, addField, updateField } from "@/api/system/field";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {pageselect} from "@/api/system/menu";
import ComSelect from "@/components/control/select";

export default {
  name: "Field",
  components: {ComSelect, ComTable, OpenBase, MyContainer,Treeselect},
  mixins: [mixin],
  created() {
    this.getMenuList();
  },
  data() {
    return {
      //列地址
      columnUrl: "/system/field/index",
      //数据地址
      dataUrl: "/system/field/list",

      //请求基础参数
      baseParams: {path: "/"},
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      //初始化菜单
      menu:{},
      //菜单ID
      menuId:null,
      //菜单项
      menuOptions:[],


    };
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
      this.menuId = node.id;
      this.menu = node;
      this.menu.path = '/'+node.component;
      this.baseParams.path = '/'+node.component;

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
      //console.log(methods,functionName)
      methods[functionName](_this, item, data)
    },
    /** 新增按钮操作 */
    handleAdd(_this, item, data) {
      _this.setFormItem(_this, item, data,{pageName: _this.menu.label, menuId: _this.menu.id,path: _this.menu.path})
    },
    /** 新增按钮操作 */
    handleAddSelection(_this, item, data) {

      let addData ={
        prop:'selection',
        type:'selection',
        label:'复选框',
        visible:'1',
        width:'45',
        pageName:_this.menu.label,
        menuId:_this.menu.id,
        path:_this.menu.path
      }

      addField(addData).then(res => {
        _this.msgSuccess("新增成功");
        _this.form.open = false;
        _this.getTableData()
      });
    },
    /** 新增按钮操作 */
    handleAddIndex(_this, item, data) {
      let addData ={
        prop:'index',
        type:'index',
        label:'序号',
        visible:'1',
        width:'50',
        pageName:_this.menu.label,
        menuId:_this.menu.id,
        path:_this.menu.path
      }
      addField(addData).then(res => {
        _this.msgSuccess("新增成功");
        _this.form.open = false;
        _this.getTableData()
      });
    },
    /** 修改按钮操作 */
    handleUpdate(_this, item, row) {
      if (!row) {
        row = _this.checkList[0];
      }
      const id = row.id
      getField(id).then(response => {
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
      delField(ids.join(",")).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {
      if (_this.form.fields.id != undefined) {
        updateField(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.getTableData()
        });
      } else {
        addField(_this.form.fields).then(res => {
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
     * 数据改变时触发
     * @param value
     * @param prop
     * @param data
     */
    handleValueChange(value,prop,data){
      this.form_fields = data;
      this.form_fields[prop] = value
      updateField(this.form_fields).then(res => {
         this.getTableData()
       })
    },
    /**
     * 禁用函数
     * @param args
     * @param callback
     */
    disableFn(args,callback){
      const {item, checkData} = {...args}
      //控制新增按钮 //列表中存在序号或复选框时添加按钮置灰
      if(item.btnId.startsWith('add')){
        if(this.menuId === null || this.menuId === undefined){
          callback(true)
        }
      }
    },
    visibleChange(){
      console.log(111)
    }

  }
}
</script>

