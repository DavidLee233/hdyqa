<template>
  <MyContainer>
    <el-row style="height: 100%">
      <el-col :span="7" style="height: 100%">
        <com-table  ref="roleTable" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams" :params="queryParams"
                @click="callFunction"
                @rowClick="roleSelect"
                :showToolbarBtn="false"
                 index selection >
        </com-table>
      </el-col>
      <el-col :span="17" style="height: 100%">
        <role-user :role-id="roleId"></role-user>
      </el-col>
    </el-row>
    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>

  </MyContainer>
</template>

<script>
import {getRole, delRole, addRole, updateRole } from "@/api/system/role";
import { treeselect as menuTreeselect, roleMenuTreeselect } from "@/api/system/menu";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";
import RoleUser from "@/views/system/role/roleUser";

export default {
  name: "Role",
  components: {RoleUser, ComTable, OpenBase, MyContainer},
  mixins: [mixin],
  data() {
    return {
      //列地址
      columnUrl: "/system/role/index",
      //数据地址
      dataUrl: "/system/role/list",

      //请求基础参数 更改查询条件不会被清空
      baseParams: {path: "/system/role/index",roleType:"system"},
      //重新查询时会被清空
      queryParams: {},
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      roleId:""
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
      const roleId = row.roleId
      const roleMenu = _this.getRoleMenuTreeselect(roleId);
      getRole(roleId).then(response => {
        const data = response.data
         roleMenu.then(res => {
          data.menuIds = res.data.checkedKeys
          _this.setFormItem(_this, item, data)
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(_this, item, row) {

      let roleIds = _this.checkList.map(item => item.roleId)

      delRole(roleIds.join(",")).then(() => {
        _this.getTableData("roleTable")
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
          _this.getTableData("roleTable")
        });
      } else {
        _this.form.fields.roleSort = _this.$refs.roleTable.total + 1;
        _this.form.fields.roleType = "system";
        addRole(_this.form.fields).then(res => {
          _this.msgSuccess("新增成功");
          _this.form.open = false;
          _this.getTableData("roleTable")
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
     * @param selection
     * @param row
     */
    roleSelect(row,column){
      this.roleId = row.roleId
    },
    getRoleMenuTreeselect(roleId) {
      return roleMenuTreeselect(roleId).then(response => {
        this.menuOptions = response.data.menus;
        return response;
      });
    }
  }
}
</script>

