<!--<template>
  <el-select v-model="value" value-key="value" filterable multiple placeholder="请选择">
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item">
    </el-option>
  </el-select>
</template>

<script>
  export default {
    name:'userSelect',
    data() {
      return {
        options: [{
          value: '选项1',
          label: '黄金糕'
        }, {
          value: '选项2',
          label: '双皮奶'
        }, {
          value: '选项3',
          label: '蚵仔煎'
        }, {
          value: '选项4',
          label: '龙须面'
        }, {
          value: '选项5',
          label: '北京烤鸭'
        }],
        value: []
      }
    }
  }
</script>-->
<template>
  <el-select v-model="id" @visible-change="resetList" @change="setValue" :multiple="multiple" :filter-method="searchUser" filterable
             placeholder="请选择">
    <el-option
      v-for="item in userList"
      :key="item.userId"
      :label="item.userName"
      :value="item.userId">
      <span style="float: left">{{ item.userName }}</span>
      <span style="float: right; color: #8492a6; font-size: 13px">{{ item.loginName }}</span>
    </el-option>
  </el-select>
</template>

<script>
  import {selectUserForTag} from "@/api/system/user";
  import pinyin from 'js-pinyin'

  export default {
    name: "userSelect",
    props: {
      //过滤的角色ID
      roleId: {
        type: String
      },
      //过滤的部门ID
      deptId: {
        type: String
      },
      //过滤的密级ID
      securityId: {
        type: String
      },
      //过滤的密级值
      securityValue: {},
      //过滤密级值的条件 lt小于 le小于等于 eq等于 ge大于等于 gt大于
      connector: {
        type: String,
        default: 'ge'
      },
      //绑定的值
      value: {},
      //是否可以多选
      multiple:{
        type:Boolean,
        default:false
      }
    },
    data() {
      return {
        userList: [],
        oldList: [],
        selectUserList:[],
        userMap:new Map(),
        id: this.value
      }
    },
    created() {
      this.getList();
    },
    watch: {
      value(val) {
        this.id = val;
      },
      securityValue(val){
        this.getList();
      },
      deptId(val){
        this.getList();
      }
    }
    , methods: {
      /**
       * 获取用户数据
       * */
      getList() {
        selectUserForTag().then(res => {
          let list = this.filterData(res.data)
          this.userList = list;
          this.oldList = JSON.parse(JSON.stringify(list));

          this.userList.forEach(item => this.userMap.set(item.userId,item))

        })
      },
      setValue(val) {
        this.$emit('input', val);
        this.$emit('update:userName', this.getSelectUserName());
        this.$emit('update:userMaxSecurityValue', this.getSelectMaxSecurityValue());
        this.$emit('update:userMinSecurityValue', this.getSelectMinSecurityValue());
        this.$emit('update:userDeptId', this.getSelectUserDept());
      },
      //获取选择用户的姓名
      getSelectUserName(){
        if(this.multiple){
          let userName = []
          this.id.forEach(id =>{
            userName.push(this.userMap.get(id).userName);
          })
          return userName.join(",")
        }else{
          return this.userMap.get(this.id).userName;
        }

      },
      //获取选择用户的最高密级
      getSelectMaxSecurityValue(){
        if(this.multiple){
          let securityValue = 0;
          this.id.forEach(id =>{
            if(this.userMap.get(id).securityValue > securityValue){
              securityValue = this.userMap.get(id).securityValue
            }
          })
          return securityValue
        }else{
          return this.userMap.get(this.id).securityValue;
        }

      },
      //获取选择用户的最低密级
      getSelectMinSecurityValue(){
        if(this.multiple) {
          let securityValue = 10;
          this.id.forEach(id => {
            if (this.userMap.get(id).securityValue < securityValue) {
              securityValue = this.userMap.get(id).securityValue
            }
          })
          return securityValue
        }else{
          return this.userMap.get(this.id).securityValue;
        }
      },
      //获取选择用户的部门ID 只取选择第一个用户
      getSelectUserDept(){
        if(this.multiple){
          if(this.id && this.id.length>0){
            return this.userMap.get(this.id[0]).deptId;
          }
          return '';
        }else{
          return this.userMap.get(this.id).deptId;
        }

      },
      /***
       * 重置下拉框数据
       * */
      resetList(val) {
        if (val) {
          this.userList = this.oldList
        }
      },
      /**
       * 搜索框检索用户
       * */
      searchUser(s) {
        if (s) {
          this.userList = this.oldList.filter(item => item.userName.includes(s)
            || pinyin.getFullChars(item.userName).toLocaleLowerCase().includes(s.toLocaleLowerCase())
            || pinyin.getCamelChars(item.userName).toLocaleLowerCase().includes(s.toLocaleLowerCase()))
        } else {
          this.userList = this.oldList
        }
      },
      /**
       * 根据条件赛选用户
       * @param data
       * @returns {*}
       */
      filterData(data) {
        let dataList = data;
        if (this.roleId) {
          dataList = dataList.filter(item => {
            return item.roleId.includes(this.roleId)
          })
        }
        if (this.deptId) {
          dataList = dataList.filter(item => {
            return item.deptId == this.deptId
          })
        }

        if (this.securityId) {
          dataList = dataList.filter(item => {
            return item.securityId == this.securityId
          })
        }

        if (this.securityValue) {
          if ("lt" == this.connector) {
            dataList = dataList.filter(item => {
              return item.securityValue < this.securityValue
            })
          } else if ("le" == this.connector) {
            dataList = dataList.filter(item => {
              return item.securityValue <= this.securityValue
            })
          } else if ("eq" == this.connector) {
            dataList = dataList.filter(item => {
              return item.securityValue == this.securityValue
            })
          } else if ("gt" == this.connector) {
            dataList = dataList.filter(item => {
              return item.securityValue > this.securityValue
            })
          } else {
            dataList = dataList.filter(item => {
              return item.securityValue >= this.securityValue
            })
          }

        }


        return dataList;
      }
    }
  }
</script>

<style scoped>

</style>
