<template>
  <el-switch
    v-model="val"
    active-text="是"
    inactive-text="否"
    @change="changeValue"
    :class="defaultClass"
   >
  </el-switch>
</template>

<script>
import {baseProps,mixin} from "@/components/control/contorlBase";
export default {
  name: "ComSwitch",
  props:{ ...baseProps },
  mixins:[mixin],
  data(){
    return {
      val:""
    }
  },
  watch:{
    value:{
      handler(newValue){
        this.val = newValue === "1"
      },
      immediate:true
    } },
  methods:{
    /**
     * 选择时回传数据
     */
    changeValue(){
      this.$emit("update:value",this.val?"1":"0");
    }
  },
  computed: { }
}
</script>

<style scoped>

</style>
