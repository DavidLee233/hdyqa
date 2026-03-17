<template>
  <el-cascader
    ref="dept"
    v-model="val"
    :options="options"
    :props="props"
    :filterable="searchable"
    :remote="searchable"
    clearable
    :show-all-levels="false"
    @change="changeValue"
    @visible-change="visibleChange"
    :class="defaultClass"
  ></el-cascader>
</template>

<script>

import {baseProps,mixin} from "@/components/control/contorlBase";

export default {
  name: "ComDeptSelect",
  props:{...baseProps},
  mixins:[mixin],
  data(){
    return {
      val:[],
      props:{
        value : "id",emitPath : false
      },
      labelName: ""
    }
  },
  created() {
    this.props.multiple = this.multiple
    this.labelName = this.config.prop + "Label"
  },
  computed:{ },
  watch:{ },
  methods:{
    /**
     *
     * 初始化下拉列表显示配置
     *
     */
    initProps(){

    },
    /**
     * 选择时回传数据
     */
    changeValue(v){

      if(this.val!== '' && this.val && this.multiple && this.val.length > 0){
        this.$emit("update:value",this.val.join(","));
        this.formData.deptId = this.val.join(",");
      }else{
        this.$emit("update:value",this.val);
        this.formData.deptId = this.val;
      }
      this.formData[this.labelName] = this.getLabel().join(",");


    },
    /**
     * 获取显示的值
     * @returns {*[]}
     */
    getLabel(){
      let labels = []
      this.$refs.dept.getCheckedNodes(true).forEach(item => {
        if(item){
          labels.push(item.label);
        }
      })
      return labels
    }

  }
}
</script>

<style scoped>

</style>
