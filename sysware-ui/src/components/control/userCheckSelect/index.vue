<template>
  <el-select v-model="val" ref="select" @change="changeValue" @visible-change="visibleChange" placeholder="请输入用户名"
             :filterable="searchable"
             :filter-method="filterMethod"
             clearable
             :multiple="multiple"
             :class="defaultClass"

  >
    <el-option v-for="item in options"
               :key="item.userId"
               :label="item.userName"
               :value="item.userId">
      <el-row :gutter="5">
        <el-col :span="12">
          <span>{{ item.userName }}</span>
        </el-col>
        <el-col :span="8">
          <span style="color: #8492a6">{{ item.deptName }}</span>
        </el-col>
      </el-row>

<!--      <span style="float: left; color: #8492a6; font-size: 13px;margin-left: 5px">{{ item[props.showLabel] }}</span>
      <span style="float: left; color: #8492a6; font-size: 13px;margin-left: 5px">{{ item.loginName }}</span>-->
    </el-option>
  </el-select>
</template>

<script>

import {baseProps,mixin} from "@/components/control/contorlBase";
import request from "@/utils/request";
import Pinyin from 'js-pinyin'
Pinyin.setOptions({ charCase: 1 })

export default {
  name: "ComUserSelect",
  props:{...baseProps},
  mixins:[mixin],
  data(){
    return {
      val:[],
      userMap:new Map(),
      oldOptions:[],
      labelName: ""
    }
  },
  created() {
    this.labelName = this.config.prop + "Label"
  },
  computed:{
    //过滤条件
    filterParams() {
      return this.config?.filterParams || []
    }
  },
  watch:{
    /*value:{
      handler(newValue){

        if( newValue  && this.multiple){
          const valArray = newValue.split(",");
          if(valArray.length>0 && valArray[0] !== ''){
            this.val = valArray
          }else{
            this.val = [];
          }
        }else{
          this.val = newValue
        }
      }
    }*/
  },
  methods:{
    /**
     * 远程获取数据
     */
    getOptions(value){
      const requestData = {
        url: this.url,
        method: this.method
      }
      /**
       * 处理请求参数
       */
      let params = {};
      this.keyword.split(",").forEach(key => {
        if(key.includes('=')){
          const keyValue = key.split("=")
          params.searchField = keyValue[0]
          params[keyValue[0]] = keyValue[1]
        }else{
          params[key] = this.formData[key]
        }
      })

      if(this.method === 'get'){
        requestData.params = params; //this.formData[this.keyword] ? this.formData[this.keyword] : {roleType:'project',searchField:'roleType'}
      }
      if(this.method === 'post'){
        requestData.data = params ; //this.formData[this.keyword] ? { [this.keyword]:this.formData[this.keyword] } : this.keyword
      }


      request(requestData).then(res => {
        this.options = res.data;

        this.oldOptions = res.data;

        if(this.multiple && this.value ){
          this.val = this.value.split(",")
        }else{
          this.val = this.value
        }

        this.options.forEach(item => this.userMap.set(item.userId,item))

      })
    },
    /**
     * 选择时回传数据
     */
    changeValue(v){

      if(this.val && this.multiple){
        this.updateFieldMethod(this.val[0]);
        this.$emit("update:value",this.val.join(","));
      }else{
        this.$emit("update:value",this.val);

        this.updateFieldMethod(this.val);
      }

     this.formData[this.labelName] = this.getUserName(v).join(",")
    },
    /**
     * 组装将传过来需要过滤的条件
     * @returns {{}}
     */
    setParams(){

      const params = {}
      const fields = this.filterParams
      params.connector  = "eq"


      if(fields && typeof fields === 'string'){
       const fieldArr =  fields.split(",")
       fieldArr.forEach(field =>{

          if(this.formData[field]){
            params[field] = this.formData[field];
          }
        })
      }

      return params
    },
      /**
      *
      * 初始化下拉列表显示配置
      *
      */
    initProps(){
      //获取配置的prop
      let options = this.config.props


      if(options){
        this.props.showLabel=options
      }
    },
    /**
     * 本地过滤
     * @param val
     */
    filterMethod(val){
      if(val){
        this.options =  this.oldOptions.filter(user => {
          const str = Pinyin.getCamelChars(user.userName)+user.userName+Pinyin.getFullChars(user.userName)
          return str.indexOf(val) > -1;
        })
      }else{
        this.options = Object.assign(this.oldOptions)
      }
    },
    /**
     * 获取用户名
     * @param ids
     * @returns {*[]}
     */
    getUserName(ids){
      let userNames = []
      if(ids){
        if(ids instanceof Array){
          ids.forEach(id => {
            userNames.push(this.userMap.get(id).userName)
          })
        }else{
          userNames.push(this.userMap.get(ids).userName)
        }
      }
      return userNames
    },
    /**
     * 更新字段
     */
    updateFieldMethod(userId){
      if(this.updateField){
        this.updateField.split(",").forEach(item =>{
          let fields = item.split("=");
          this.formData[fields[0]] = this.userMap.get(userId)[fields[1]]
        })
      }

    }
  }
}
</script>

<style scoped>

</style>
