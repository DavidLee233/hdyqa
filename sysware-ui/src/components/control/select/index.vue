<template>
  <el-select ref="select" v-model="val" @change="changeValue" @visible-change="visibleChange" placeholder="请输入关键词"
             :filterable="searchable"
             :remote="searchable"
             clearable
             :multiple="multiple"
             :class="defaultClass"
             :disabled="disabled"

  >
    <el-option v-for="item in options"
               :key="item[props.value]"
               :label="item[props.label]"
               :value="item[props.value]">
      <template v-if="props.span">
        <el-row :gutter="5">
          <el-col :span="12">
            <span>{{item[props.label]}}</span>
          </el-col>
          <el-col :span="8">
            <span style="color: #8492a6">{{ item[props.span]}}</span>
          </el-col>
        </el-row>
      </template>

    </el-option>
  </el-select>
</template>

<script>

import {baseProps,mixin} from "@/components/control/contorlBase";

export default {
  name: "ComSelect",
  props:{...baseProps},
  mixins:[mixin],
  created() {
    this.labelName = this.config.prop + "Label"
  },
  data(){
    return {
      val:[],
      labelName: ""
    }
  },
  computed:{ },
  watch:{ },
  methods:{
    /**
     * 选择时回传数据
     */
    changeValue(selectValue){

      if(this.options){

        const item =  this.options.filter(i => i[this.props.value] === this.val )

        const prop = this.config.prop


        let data = {}
        if(item.length > 0){
          data.url = item[0].url
          data.method = item[0].method
          data.props = item[0].props
          data.type = item[0].type

          this.dynamicData[prop] = data

          this.$emit("update:dynamicData",this.dynamicData);
        }
      }

      if(this.val!== '' && this.val && this.multiple){
        this.$emit("update:value",this.val.join(","));
      }else if(this.val.length === 0){
        this.$emit("update:value","");
      }else{
        this.$emit("update:value",this.val);

      }

      //回传下拉框显示的值
      this.formData[this.labelName] = this.getLabel(selectValue).join(",");



    },
    /**
     * 获取显示的值
     * @returns {*[]}
     */
    getLabel(selectValue){
      let labels = []
      if(selectValue instanceof Array ){
        selectValue.forEach(value => {
          this.options.filter(opt => opt[this.props.value] === value).forEach(opt => {
            labels.push(opt[this.props.label])
          })
        })
      }else{
        this.options.filter(opt => opt[this.props.value] === selectValue).forEach(opt => {
          labels.push(opt[this.props.label])
        })
      }


      return labels
    }

  }
}
</script>

<style scoped>

</style>
