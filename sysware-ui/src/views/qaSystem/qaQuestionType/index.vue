<template>
  <MyContainer>

    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction" @disableFn="disableFn"
                >

    </com-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>

  </MyContainer>
</template>

<script>
import { listQaQuestionType, getQaQuestionType, delQaQuestionType, addQaQuestionType, updateQaQuestionType } from "@/api/qaSystem/qaQuestionType";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";

export default {
  name: "QaQuestionType",
  components: {ComTable, OpenBase, MyContainer},
  mixins: [mixin],
  data() {
    return {
      //列地址
      columnUrl: "/qaSystem/qaQuestionType/index",
      //数据地址
      dataUrl: "/qaSystem/qaQuestionType/list",

      //请求基础参数
      baseParams: {path: "/qaSystem/qaQuestionType/index"},
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true

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
      methods[functionName](_this, item, data)
    },
    /** 新增按钮操作 */
    handleAdd(_this, item, data) {
      _this.setFormItem(_this, item, data)
    },

    /** 修改按钮操作 */
    handleUpdate(_this, item, row) {
      if (!row) {
        row = _this.checkList[0];
      }
      const questionId = row.questionId
      getQaQuestionType(questionId).then(response => {
        const data = response.data
        _this.setFormItem(_this, item, data)
      });
    },
    /** 删除按钮操作 */
    handleDelete(_this, item, row) {

      let questionIds = [];

      questionIds = _this.checkList.map(item => item.questionId)

      delQaQuestionType(questionIds.join(",")).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {
      if (_this.form.fields.questionId != undefined) {
        updateQaQuestionType(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.getTableData()
        });
      } else {

        addQaQuestionType(_this.form.fields).then(res => {
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
     * 按钮禁用情况控制
     * @param args
     * @param callback
     */
  disableFn(args,callback){
    const {item,checkData} = {...args}

    //控制添加按钮
    if(item.btnId === 'add'){
      if(this.checkList && this.checkList.length > 0){
        callback(false);
      }else{
        callback(true)
      }
    }
  }
  }
}
</script>
