<template>
  <MyContainer headerStyle="padding:0px">
    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction" @disableFn="disableFn"
                selection index >


      <template v-slot:tag="slot">
<!--        <dict-tag :dict-type="leaderType" :value="slot.data.leaderType"></dict-tag>-->
        <el-tag v-if="slot.data.leaderType === '2'" >
          主任
        </el-tag>
        <el-tag v-else-if="slot.data.leaderType === '1'">
          副主任
        </el-tag>
        <el-tag v-else>
            普通用户
        </el-tag>
      </template>

    </com-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>

    <select-user ref="selectUser" @getTableData="getTableData"  :dept-id="deptId" ></select-user>

  </MyContainer>
</template>

<script>
import {delRole, addRole, updateRole } from "@/api/system/role";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";
import SelectUser from "@/views/system/dept/selectUser";
import {cancelUserDept, updateLeaderType} from "@/api/system/user";

export default {
  name: "deptUser",
  components: {SelectUser, ComTable, OpenBase, MyContainer},
  props:{
    // 角色编号
    deptId: {
      type: String
    }
  },
  mixins: [mixin],
  data() {
    return {
      //列地址
      columnUrl: "/system/dept/user",
      //数据地址
      dataUrl: "/system/user/listByDept",
      //请求基础参数 searchField 表示需要搜索的字段 pagePath搜索列配置的路径
      baseParams: {deptId:"/",searchField:"deptId",path: "/system/dept/user",orderByColumn:'leaderType',isAsc:'desc'},
      //操作列的宽度
      //operationWidth: 200,
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      //职务数据字典类型名称
      leaderType: "sys_dept_leader"
    };
  },
  watch:{
    "deptId" : {
      handler(newValue){
        this.baseParams.deptId = newValue
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
    },

    /** 设置部门职务 */
    handleLeader(_this, item, row) {
      if(!row){
        row = _this.checkList[0];
      }
      _this.setFormItem(_this,item,row)
    },
    /** 删除按钮操作 */
    handleDelete(_this, item, row) {
      let userIds = [];
      if (row) {
        userIds.push(row.userId)
      } else {
        userIds = _this.checkList.map(item => item.userId)
      }
      cancelUserDept({userIds:userIds.join(",")}).then(() => {
        _this.msgSuccess("删除成功");
        _this.getTableData();
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
    submitLeaderForm(_this){
      updateLeaderType({userId: _this.form.fields.userId, leaderType : _this.form.fields.leaderType}).then(res => {
        if (200 == res.code) {
          _this.form.open = false;
          _this.msgSuccess(res.msg);
          _this.getTableData()
        }
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
     * 自定义按钮禁用状态
     * @param args
     * @param callback
     */
    disableFn(args,callback){
      const {item,checkData} = {...args}
      //控制添加按钮
      if(item.btnId === 'add'){
        callback(this.deptId === '');
      }
    }
  }
}
</script>
