<template>
  <MyContainer>
    <el-row style="height: 100%">
      <el-col :span="5" style="height: 100%;">
        <com-tree-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                         :checkList.sync="checkList" :baseParams="baseParams"
                         @rowClick="handleSelect"   @click="callFunction" @disableFn="disableFn"

                         row-key="deptId" :showToolbarBtn="false" default-expand-all
                         >

          <template v-slot:nameFn="slot">
            <span v-html="slot.data[slot.prop]"></span>
          </template>

        </com-tree-table>
      </el-col>
      <el-col :span="19" style="height: 100%">
        <dept-user :dept-id="deptId"></dept-user>
      </el-col>
    </el-row>


    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>

  </MyContainer>
</template>

<script>
import { listDept, getDept, delDept, addDept, updateDept } from "@/api/system/dept";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTreeTable from "@/components/control/treeTable";
import DeptUser from "@/views/system/dept/deptUser";

export default {
  name: "Dept",
  components: {ComTreeTable, OpenBase, MyContainer,DeptUser},
  mixins: [mixin],
  data() {
    return {
      //列地址
      columnUrl: "/system/dept/index",
      //数据地址
      dataUrl: "/system/dept/list",

      //请求基础参数
      baseParams: {path: "/system/dept/index"},
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      //选中的部门ID
      deptId:"",
      headerBtnArr: [
        /*,
        {
          btnId: "edit",
          type: "success",
          icon: "el-icon-edit",
          size: "mini",
          location: "header",
          text: "修改",
          showText: true,
          permission: ['system:dept:add'],
          title: "修改部门",
          openType: "dialog",//弹出类型
          direction: "",//弹出位置
          lines: 1,
          formId: "/system/dept/edit",
          submitEventName: "submitForm",
          disabledType: "single",
          functionName: "handleUpdate"
        }*/
      ],
      operationBtnArr: [
        {
          btnId: "add",
          type: "text",
          icon: "el-icon-plus",
          size: "mini",
          location: "header",
          text: "添加",
          showText: true,
          permission: ['system:dept:add'],
          title: "添加部门",
          openType: "dialog",//弹出类型
          direction: "rtl",//弹出位置
          lines: 1,
          formId: "/system/dept/add",
          submitEventName: "submitForm",
          disabledType: "",
          functionName: "handleAdd"
        },
        {
          btnId: "edit",
          type: "text",
          icon: "el-icon-edit",
          size: "mini",
          location: "header",
          text: "修改",
          showText: true,
          permission: ['system:dept:edit'],
          title: "修改部门",
          lines: 1,
          openType: "dialog",//弹出类型
          formId: "/system/dept/add",
          submitEventName: "submitForm",
          disabledType: "",
          functionName: "handleUpdate"
        },
        {
          btnId:"remove",
          type: "text",
          icon: "el-icon-delete",
          size: "mini",
          location: "operation",
          text: "删除",
          showText: true,
          permission: ['system:dept:remove'],
          openType: "popConfirm",
          title: "提示",
          msg: "是否删除?",
          confirmText: "删除",
          cancelText: "取消",
          popConfirmIcon: "el-icon-info",
          color: "#F56C6C",
          disabledType: "disableFn",
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
    handleAdd(_this, item, row) {
      _this.setFormItem(_this, item, {parentId:row.deptId})
    },

    /** 修改按钮操作 */
    handleUpdate(_this, item, row) {
      /*if (!row) {
        row = _this.checkList[0];
      }*/
      const deptId = row.deptId
      getDept(deptId).then(response => {
        const data = response.data
        _this.setFormItem(_this, item, data)
      });
    },
    /** 删除按钮操作 */
    handleDelete(_this, item, row) {

      let deptIds = [];
      if (row) {
        deptIds.push(row.deptId)
      } else {
        deptIds = _this.checkList.map(item => item.deptId)

      }
      delDept(deptIds.join(",")).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {
      if (_this.form.fields.deptId != undefined) {
        updateDept(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.getTableData()
        });
      } else {
        /*_this.form.fields.parentId = _this.checkList[0].deptId*/
        addDept(_this.form.fields).then(res => {
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
     * 单行选中
     * @param row
     */
    handleSelect(row){
      this.deptId = row.deptId;
    },
    /**
     * 自定义按钮禁用状态
     * @param args
     * @param callback
     */
    disableFn(args,callback){
      const {item,checkData} = {...args}

      console.log(checkData,555555555)

      //控制添加按钮
      if(item.btnId === 'remove'){
        callback( checkData.children.length > 0 );
      }
    }

  }
}
</script>

