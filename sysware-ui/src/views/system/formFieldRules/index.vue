<template>
  <MyContainer>

    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction"  selection index operation >

    </com-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>

  </MyContainer>
</template>

<script>
import { listFormFieldRules, getFormFieldRules, delFormFieldRules, addFormFieldRules, updateFormFieldRules } from "@/api/system/formFieldRules";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";

export default {
  name: "FormFieldRules",
  components: {ComTable, OpenBase, MyContainer},
  mixins: [mixin],
  created() {


    const fieldId = this.$route.params && this.$route.params.fieldId;
    const fieldLabel = this.$route.params && this.$route.params.fieldLabel;
    this.baseParams.fieldId = fieldId;
    this.fieldId = fieldId;
    this.fieldLabel = fieldLabel;


  },
  data() {
    return {
      //列地址
      columnUrl: "/system/formFieldRules/index",
      //数据地址
      dataUrl: "/system/formFieldRules/list",
      //请求基础参数
      baseParams: {path: "/system/formFieldRules/index"},
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      fieldId:""

    };
  },
  methods: {
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
      _this.setFormItem(_this, item, data,{fieldId:_this.fieldId})
    },

    /** 修改按钮操作 */
    handleUpdate(_this, item, row) {
      if (!row) {
        row = _this.checkList[0];
      }
      const id = row.id
      getFormFieldRules(id).then(response => {
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
      delFormFieldRules(ids.join(",")).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {
      if (_this.form.fields.id != undefined) {

        updateFormFieldRules(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.getTableData()
        });
      } else {
        _this.form.fields.fieldId = _this.fieldId;
        addFormFieldRules(_this.form.fields).then(res => {
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
  }

  }
}
</script>
