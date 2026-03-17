<template>

 <el-radio-group v-model="val" @input="changeValue">
    <el-radio v-for="item in options"  :key="item[props.value]"
              :label="item[props.value]">{{item[props.label]}}</el-radio>
  </el-radio-group>
<!--  <el-radio-group v-model="val" @change="changeValue">
    <el-radio label="1">是</el-radio>
    <el-radio label="0">否</el-radio>
  </el-radio-group>-->
</template>

<script>
import {baseProps,mixin} from "@/components/control/contorlBase";

export default {
  name: "ComRadio",
  mixins:[mixin],
  props:{
    ...baseProps
  },
  data(){
    return {
        val:""
      }
  },
  computed:{ },
  watch:{ },
  methods:{ }
}
</script>

<style scoped>

</style>
