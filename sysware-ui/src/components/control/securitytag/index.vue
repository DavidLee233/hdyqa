<template>
<!--  <div>{{data}}</div>-->
  <el-tag v-if="value" :type="type">{{ value }}</el-tag>
</template>

<script>
export default {
  name: "ComTag",
  props:{
    data:{
      type:Object,
      default: ()=>({})
    },
    prop:{
      type:String,
      default:''
    }
  },
  data(){
    return {
      value:this.data[this.prop]
    }
  },
  computed: {
    type(){
      if(this.value=='核心')
        return "danger"
      else if(this.value=='重要')
        return "warning"
      else
        return ""
    }
  }
}
</script>

<style scoped>

</style>
