<template>
  <MyContainer headerStyle="padding:0px">
    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction" :headerBtnArr="headerBtnArr"
                @disableFn="disableFn"
                selection index >

    </com-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>

    <select-user ref="selectUser" @getTableData="getTableData" :roleId="roleId" ></select-user>

  </MyContainer>
</template>

<script>
import {delRole, addRole, updateRole, authUserCancelAll} from "@/api/system/role";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";
import SelectUser from "@/views/system/role/selectUser";

export default {
  name: "roleUser",
  components: {SelectUser, ComTable, OpenBase, MyContainer},
  props:{
    // 角色编号
    roleId: {
      type: String
    }
  },
  mixins: [mixin],
  data() {
    return {
      //列地址
      columnUrl: "/system/role/user",
      //数据地址
      dataUrl: "/system/user/listByRole",

      //请求基础参数
      baseParams: {roleId:"/",searchField:"roleId",path: "/system/role/user"},
      //操作列的宽度
      //operationWidth: 200,
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
          permission: ['system:userRole:add'],
          disabledType: "disableFn",
          functionName: "handleAdd"
        },
        {
          btnId: "edit",
          type: "danger",
          icon: "el-icon-delete",
          size: "mini",
          location: "header",
          text: "移除",
          showText: true,
          permission: ['system:userRole:remove'],
          openType: "popConfirm",
          title: "提示",
          msg: "是否删除?",
          confirmText: "删除",
          cancelText: "取消",
          popConfirmIcon: "el-icon-info",
          color: "#F56C6C",
          disabledType: "multiple",
          functionName: "handleDelete"
        }
      ]
    };
  },
  watch:{
    "roleId" : {
      handler(newValue){
        this.baseParams.roleId = newValue
        this.getTableData()
      },
      deep:true
    }
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
      _this.$refs.selectUser.show();
      /*_this.setFormItem(_this, item, data)*/
    },

    /** 修改按钮操作 */
    handleUpdate(_this, item, row) {

    },
    /** 删除按钮操作 */
    handleDelete(_this, item, row) {

      let userIds = []
      if(row){
        userIds.push(row.userId)
      }else{
        _this.checkList.forEach(i =>{
          userIds.push(i.userId)
        })
      }

      authUserCancelAll({ roleId: _this.roleId, userIds: userIds.join(",") }).then(() => {
        _this.getTableData();
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {
      if (_this.form.fields.roleId != undefined) {
        updateRole(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.getTableData()
        });
      } else {
        _this.form.fields.roleSort = _this.$refs.roleTable.total + 1;
        addRole(_this.form.fields).then(res => {
          _this.msgSuccess("新增成功");
          _this.form.open = false;
          _this.getTableData();
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
      if(item.btnId === 'add'){
        callback(this.roleId === '');
      }

    }
  }
}
</script>
