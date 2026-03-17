<template>
<!--  <el-input v-model="val" @input="changeValue" :disabled="disabled" :class="defaultClass"></el-input>-->
 <div>
    <el-input
      v-model="val"
      @input="changeValue" :disabled="disabled" :class="defaultClass">
      <i
        class="el-icon-refresh el-input__icon"
        slot="suffix"
        @click="handleIconClick">
      </i>
    </el-input>



 </div>

</template>

<script>
import {baseProps,mixin} from "@/components/control/contorlBase";
export default {
  name: "ComCodeInput",
  props:{ ...baseProps },
  mixins:[mixin],
  data(){
    return {
      val:""
    }
  },
  watch:{ },
  methods:{
    handleIconClick(){
      if( this.val != "" && this.val!=undefined){
        this.$confirm("重新生成会将【"+this.val+"】覆盖并且无法恢复，是否确定重新生成？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type:"warning"
        }).then(() =>{
          this.getCode("改变后的编码");
        })
      }else{
       this.getCode("新生成的编码");
      }
    },
    getCode(code){
      this.val = code
    }
  },
  computed: { }
}
</script>

<style scoped>

</style>
