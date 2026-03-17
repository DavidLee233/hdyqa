<template>
  <el-dialog class="edit_dev" title="选择用户" :visible.sync="visible" width="900px" top="5vh" append-to-body>
    <el-transfer
      ref="transfer"
      v-model="value"
      filterable
      :render-content="renderFunc"
      :titles="['用户列表', '已选列表']"
      :props="{
        key: 'userId'
        }"
      :format="{
        noChecked: '${total}',
        hasChecked: '${checked}/${total}'
      }"
      target-order="push"
      :filter-method="filterMethod"
      @change="handleChange"
      :data="data">
    </el-transfer>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="handleSelectUser">确定</el-button>
      <el-button @click="visible = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Pinyin from 'js-pinyin'
Pinyin.setOptions({ charCase: 1 })
import {
  listCheckUser, updateUserDept
} from "@/api/system/user";

export default {
  name: "selectUser",
  props: {
    // 部门ID
    deptId: {
      type: String
    }
  },
  data() {
    return {
      visible: false,
      data: [],
      value: [],
      renderFunc(h, option) {
        return <span>{ option.deptName }-[{option.securityName}]{ option.userName }({ option.loginName })</span>;
      },
      queryParams:{},
      selectList:[]
    };
  },
  methods: {
    handleChange(value, direction, movedKeys) {
      this.selectList = value;
    },
    filterMethod(query, item) {
      const str = Pinyin.getCamelChars(item.userName)+item.userName + item.loginName
      return str.indexOf(query) > -1;
    },
    // 查询表数据
    getList() {
      listCheckUser(this.queryParams).then(res => {
        this.data = res.data;
      });
    },
    show(){
      this.value = [];
      this.visible = true
      this.queryParams.deptId = this.deptId
      this.queryParams.connector = "ne";
      this.getList();
    },
    handleSelectUser(){

      if(this.selectList && this.selectList.length > 0){
        updateUserDept({deptId:this.deptId,userIds:this.selectList.join(",")}).then(res =>{
          this.msgSuccess(res.msg);
          if (res.code === 200) {
            this.visible = false;
            this.$emit("getTableData");
          }
        })
      }else{
        this.msgError("请选择用户");
      }


    }
  }
}
</script>

<style scoped>
.edit_dev >>> .el-transfer-panel {
  width:340px;
  height: 500px;
}
.edit_dev >>> .el-transfer-panel__list.is-filterable{
  height: 400px;
}
</style>
