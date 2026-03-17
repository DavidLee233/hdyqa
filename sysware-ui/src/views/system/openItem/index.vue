<template>
  <MyContainer>

    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction" :headerBtnArr="headerBtnArr"
                :operationBtnArr="operationBtnArr"
                selection index operation :operation-width="operationWidth">

    </com-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>

  </MyContainer>
</template>

<script>
import { listOpenItem, getOpenItem, delOpenItem, addOpenItem, updateOpenItem } from "@/api/system/openItem";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";

export default {
  name: "OpenItem",
  components: {ComTable, OpenBase, MyContainer},
  mixins: [mixin],
  data() {
    return {
      //列地址
      columnUrl: "/system/openItem/index",
      //数据地址
      dataUrl: "/system/openItem/list",

      //请求基础参数
      baseParams: {path: "/system/openItem/index"},
      //操作列的宽度
      operationWidth: 200,
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      headerBtnArr: [
        {
          btnId: "add",
          type: "primary",
          icon: "el-icon-plus",
          size: "mini",
          location: "header",
          text: "添加",
          showText: true,
          permission: ['system:openItem:add'],
          openItem: {
            title: "添加弹窗信息",
            openType: "dialog",//弹出类型
            direction: "rtl",//弹出位置
            lines: 1,
            formId: "/system/openItem/add",
            submitEventName: "submitForm",
          },
          disabledType: "notOrSingle",
          functionName: "handleAdd"
        },
        {
          btnId: "edit",
          type: "success",
          icon: "el-icon-edit",
          size: "mini",
          location: "header",
          text: "修改",
          showText: true,
          permission: ['system:openItem:edit'],
          openItem: {
            title: "修改弹窗信息",
            openType: "dialog",//弹出类型
            direction: "",//弹出位置
            lines: 1,
            formId: "/system/openItem/edit",
            submitEventName: "submitForm",
          },
          disabledType: "single",
          functionName: "handleUpdate"
        }
      ],
      operationBtnArr: [
        {
          btnId: "edit",
          type: "text",
          icon: "el-icon-edit",
          size: "mini",
          location: "header",
          text: "修改",
          showText: true,
          permission: ['system:openItem:remove'],
          openItem: {
            title: "修改弹窗信息",
            lines: 1,
            openType: "dialog",//弹出类型
            formId: "/system/openItem/edit",
            submitEventName: "submitForm",
          },
          disabledType: "notCheck",
          functionName: "handleUpdate"
        },
        {
          type: "text",
          icon: "el-icon-delete",
          size: "mini",
          location: "operation",
          text: "删除",
          showText: true,
          permission: ['system:openItem:remove'],
          openItem: {
            openType: "popConfirm",
            title: "提示",
            msg: "是否删除?",
            confirmText: "删除",
            cancelText: "取消",
            popConfirmIcon: "el-icon-info",
            color: "#F56C6C"
          },
          functionName: "handleDelete"
        }
      ]
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
      _this.setFormItem(_this, item, data)
    },

    /** 修改按钮操作 */
    handleUpdate(_this, item, row) {
      if (!row) {
        row = _this.checkList[0];
      }
      const createBy = row.createBy
      getOpenItem(createBy).then(response => {
        const data = response.data
        _this.setFormItem(_this, item, data)
      });
    },
    /** 删除按钮操作 */
    handleDelete(_this, item, row) {

      let createBys = [];
      if (row) {
              createBys.push(row.createBy)
      } else {
              createBys = _this.checkList.map(item => item.createBy)

      }
      delOpenItem(createBys.join(",")).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {
      if (_this.form.fields.createBy != undefined) {
        updateOpenItem(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.getTableData()
        });
      } else {

        addOpenItem(_this.form.fields).then(res => {
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
