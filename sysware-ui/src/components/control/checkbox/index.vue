<template>

    <el-checkbox-group v-model="val" @change="changeValue" >
    <el-checkbox v-for="item in options"  :key="item[props.value]"
                 :label="item[props.value]">{{item[props.label]}}</el-checkbox>
    </el-checkbox-group>


</template>

<script>
import {baseProps,mixin} from "@/components/control/contorlBase";

export default {
  name: "ComCheckBox",
  mixins:[mixin],
  props:{
    ...baseProps
  },
  data(){
    return {
        val:[]
      }
  },
  computed:{ },
  watch:{ },
  methods:{ }
}
</script>

<style scoped>

</style>
