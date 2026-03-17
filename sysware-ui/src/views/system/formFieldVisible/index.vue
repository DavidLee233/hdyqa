<template>
  <MyContainer>

    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction"  selection index>
      <template v-slot:checkBox="slot">
        <el-checkbox true-label="1" false-label="0" v-model="slot.data[slot.prop]" @change="checkChange($event,slot.prop,slot.data)"></el-checkbox>
      </template>

    </com-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent">
<!--      <template v-slot:pre="slot">
        <el-col :span="12">
          <el-form-item label="字段为" prop="modelId">
            <el-select v-model="selectFieldId" filterable @change="fieldSelect"  class="w-250" placeholder="请选择">
              <el-option
                v-for="item in fieldList"
                :key="item.fieldId"
                :label="item.label"
                :value="item.fieldId">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </template>-->

    </OpenBase>

  </MyContainer>
</template>

<script>
import { listFormFieldVisible,getFormFieldVisible, delFormFieldVisible, addFormFieldVisible, updateFormFieldVisible } from "@/api/system/formFieldVisible";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";
import {selectListByFieldId,getFormField} from "@/api/system/formField";

export default {
  name: "FormFieldVisible",
  components: {ComTable, OpenBase, MyContainer},
  mixins: [mixin],
  created() {
    const fieldId = this.$route.params && this.$route.params.fieldId;
    this.baseParams.fieldId = fieldId;
    this.fieldId = fieldId;

    this.getFormFieldList();
  },
  mounted() {
    getFormField(this.fieldId).then(res =>{
      this.fieldLabel = res.data.label;
    })
  },
  data() {
    return {
      //列地址
      columnUrl: "/system/formFieldVisible/index",
      //数据地址
      dataUrl: "/system/formFieldVisible/list",
      //请求基础参数
      baseParams: {path: "/system/formFieldVisible/index"},
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      fieldLabel: "",
      fieldId: "",
      fieldList: [],
      btnItem: {},
      selectFieldId:""
    };
  },
  methods: {
    getFormFieldList(){
      // /system/formField/selectFormFieldByFieldId/{fieldId}
      selectListByFieldId(this.fieldId).then(res =>{
       this.fieldList = res.rows
      })
    },
    /**
     * 复选框状态切换
     * @param value
     * @param prop
     * @param data
     */
    checkChange(value,prop,data){
      this.form_fields = data;
      this.form_fields[prop] = value

      updateFormFieldVisible(this.form_fields).then(res => {
      })
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
      _this.btnItem = item
      _this.setFormItem(_this, item, data,{fieldId:_this.fieldId,fieldLabel:_this.fieldLabel})
    },

    /** 修改按钮操作 */
    handleUpdate(_this, item, row) {
      if (!row) {
        row = _this.checkList[0];
      }
      const id = row.id
      getFormFieldVisible(id).then(response => {
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
      delFormFieldVisible(ids.join(",")).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {
      if (_this.form.fields.id != undefined) {
        updateFormFieldVisible(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.getTableData()
        });
      } else {

        //_this.form.fields.visibleLabel = _this.form.fields.visiblelabel[0];

        console.log(_this.form.fields)
        addFormFieldVisible(_this.form.fields).then(res => {
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
     * 关联字段改变事件
     *
     * */
    fieldSelect(fieldId){

      this.setFormItem(this, this.btnItem)
    }

  }
}
</script>
