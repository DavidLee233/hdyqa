<template>
  <el-select v-model="val" @change="changeValue" @visible-change="visibleChange" placeholder="请输入密级"
             :filterable="searchable"
             clearable
             :class="defaultClass"
             :disabled="disabled"

  >
    <el-option v-for="item in options"
               :key="item.securityId"
               :label="item.securityName"
               :value="item.securityId">
      <span style="float: left">{{ item.securityName }}</span>
<!--      <span style="float: right; color: #8492a6; font-size: 13px">{{ item[props.showLabel] }}</span>-->
    </el-option>
  </el-select>
</template>

<script>

import {baseProps,mixin} from "@/components/control/contorlBase";
import request from "@/utils/request";

export default {
  name: "ComSecuritySelect",
  props:{...baseProps},
  mixins:[mixin],
  created() {
    this.labelName = this.config.prop.replace("Id","") + "Name"
  },
  data(){
    return {
      val:[],
      securityMap:new Map(),
      labelName: ""
    }
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
        this.options = res.rows;
        this.options.forEach(item => this.securityMap.set(item.securityId,item))
      }).then(() =>{
        if(this.multiple){
          this.val = this.value.split(",")
        }else{
          this.val = this.value
        }
      })
    },
    /**
     * 选择时回传数据
     */
    changeValue(selectValue){
      /*if(this.securityMap.get(this.val)) {
        this.formData.securityValue = this.securityMap.get(this.val).value;
      }*/
       /* }else{
         this.$emit("update:updateValue","");
       }*/
      this.$emit("update:value",this.val);
      this.updateFieldMethod(this.val)

      //回传下拉框显示的值
       this.formData[this.labelName] = this.getLabel(selectValue).join(",");
    },
    /**
     * 组装将传过来需要过滤的条件
     * @returns {{}}
     */
    setParams(){

      const params = {}
      const fields = this.filterParams

      fields.map( field => {
        if(this.formData[field]){
          params[field] = this.formData[field];
        }
      })

      return params
    },
    /**
     * 获取显示的值
     * @returns {*[]}
     */
    getLabel(selectValue){
      let labels = []

      if(selectValue){
        if(selectValue instanceof Array ){
          selectValue.forEach(value => {
            this.options.filter(opt => opt.securityId === value).forEach(opt => {
              labels.push(opt.securityName)
            })
          })
        }else{
          this.options.filter(opt => opt.securityId === selectValue).forEach(opt => {
            labels.push(opt.securityName)
          })
        }
      }
      return labels
    },
    /**
     *
     * 初始化下拉列表显示配置
     *
     */
    initProps(){

    },
    /**
     * 更新字段
     */
    updateFieldMethod(securityId){

      if(this.updateField ){
        this.updateField.split(",").forEach(item =>{
          let fields = item.split("=");
          if(securityId){
            this.formData[fields[0]] = this.securityMap.get(securityId)[fields[1]]
          }else{
            this.formData[fields[0]] = null
          }
        })
      }

      console.log( this.formData,31)
    }
  }
}
</script>

<style scoped>

</style>
