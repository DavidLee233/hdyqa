<template>
  <MyContainer>

    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction"  selection index >

      <template v-slot:headerOperation="slot">
        <el-col :span="1.5">
          <treeselect style="width: 300px" :showCheckedStrategy="true" @visible-change="visibleChange" :normalizer="my_normalizer" @select="mySelect" :flat="true"
                      v-model="menuId"  :options="menuOptions"  :show-count="true" placeholder="请选择页面"/>
        </el-col>
        <el-col :span="1.5">
          <span>{{menu.path}}</span>
        </el-col>
      </template>

      <template v-slot:setRules="slot">
        <router-link :to="'/system/formFieldRules/index/'+slot.data.fieldId" class="link-type">
          <span>验证规则({{slot.data.ruleCount}})</span>
        </router-link>
<!--        <el-link type="primary" @click="toRules(slot.data)" :underline="false"  >验证规则({{slot.data.ruleCount}})</el-link>-->
      </template>

      <template v-slot:setVisible="slot">
        <el-link type="primary" @click="toVisible(slot.data)" :underline="false">显示规则({{slot.data.visibleCount}})</el-link>
      </template>

      <template v-slot:checkBox="slot">
        <el-checkbox true-label="1" false-label="0" v-model="slot.data[slot.prop]" @change="handleValueChange($event,slot.prop,slot.data)"></el-checkbox>
      </template>

      <template v-slot:number="slot">
        <el-input-number v-model="slot.data[slot.prop]" @change="handleValueChange($event,slot.prop,slot.data)" :min="1" size="mini"></el-input-number>
      </template>

    </com-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>

  </MyContainer>
</template>

<script>
import { listFormField, getFormField, delFormField, addOrUpdateFormField, updateFormField,copyFormField } from "@/api/system/formField";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {treeselect} from "@/api/system/menu";

export default {
  name: "FormField",
  components: {ComTable, OpenBase, MyContainer,Treeselect},
  mixins: [mixin],
  created() {
    this.getMenuList();
  },
  data() {
    return {
      //列地址
      columnUrl: "/system/formField/index",
      //数据地址
      dataUrl:"/system/formField/selectFormFieldList",
      //请求基础参数
      baseParams: {formId:"/",searchField:"formId",path: "/system/formField/index"},
      //初始化菜单
      menu:{},
      //菜单ID
      menuId:null,
      //菜单项
      menuOptions:[],
      //复制的数据
      copyData:undefined,

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
        isDisabled: node.menuType != 'F' ? true : false
      };
    },
    /**
     * 下拉列表选中
     * @param node
     */
    mySelect(node) {

      this.menu = node;
      this.menu.path = '/'+node.component;
      this.baseParams.formId = this.menu.path

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
      methods[functionName](_this, item, data)
    },
    /** 新增按钮操作 */
    handleAdd(_this, item, data) {
      _this.setFormItem(_this, item,  _this.copyData,{formId:_this.baseParams.formId})
    },

    /** 修改按钮操作 */
    handleUpdate(_this, item, row) {
      if (!row) {
        row = _this.checkList[0];
      }
      if(this.copyData){
        row = this.copyData
      }
      const fieldId = row.fieldId
      getFormField(fieldId).then(response => {
        const data = response.data
        _this.setFormItem(_this, item, data)
      });
    },
    /** 删除按钮操作 */
    handleDelete(_this, item, row) {

      let fieldIds = [];
      if (row) {
        fieldIds.push(row.fieldId)
      } else {
        fieldIds = _this.checkList.map(item => item.fieldId)
      }
      delFormField(fieldIds.join(",")).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {

      addOrUpdateFormField(_this.form.fields).then(res => {
        _this.msgSuccess("新增成功");
        _this.form.open = false;
        _this.getTableData()
      });
    },
    /**
     * 复制按钮
     * */
    handleCopy(_this, item, row){
      _this.setFormItem(_this, item)
    },
    /**
     * 复制提交事件
     * */
    submitCopy(_this){
      let fieldIds = [];
      let formId = '';
      if(_this.checkList.length > 0 ){
        fieldIds = _this.checkList.map(item => item.fieldId)
      }else{
        formId =  _this.menu.path
      }
      copyFormField({fieldIds:fieldIds,formId:formId,targetFormId:_this.form.fields.formId}).then(res => {
        _this.form.open = false;
        _this.getTableData();
        _this.msgSuccess("复制成功");
      })
    },
    /**
     * 表单提交按钮事件
     * @param fnName 提交事件名称
     */
    submitEvent(fnName){
      this.callFunction(fnName)
    },
    /**
     * 获取菜单下拉框
     */
    getMenuList() {
      treeselect().then(res => {
        this.menuOptions = res.data;
      });
    },
    /**
     * 复选框状态切换
     * @param value
     * @param prop
     * @param data
     */
    handleValueChange(value,prop,data){
      this.form_fields = data;
      this.form_fields[prop] = value

      addOrUpdateFormField(this.form_fields).then(res => {
        this.msgSuccess("更新成功")
      })
    },
    /**
     * 数字变更
     * @param value
     * @param prop
     * @param data
     */
    numberChange(value,prop,data){
      this.form_fields = data;
      this.form_fields[prop] = value
      addOrUpdateFormField(this.form_fields).then(res => {
        //this.getTableData()
      })
    },
    /**
     * 跳转到验证规则配置界面
     * @param data
     */
    toRules(data){
      this.$router.push("/system/formFieldRules/index/"+data.fieldId);
    },
    /**
     * 跳转到显示规则配置界面
     * @param data
     */
    toVisible(data){
      this.$router.push("/system/formFieldVisible/index/"+data.fieldId);
    },
    visibleChange(){
      console.log(111)
    }

  }
}
</script>

