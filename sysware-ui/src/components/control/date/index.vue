<template>
  <div :class="defaultClass">
    <el-date-picker
      v-model="val"
      :type="config.dateType || 'date'"
      :placeholder="config.placeholder || '请选择日期'"
      :start-placeholder="config.placeholder || '请选择开始日期'"
      :end-placeholder="config.placeholder || '请选择结束日期'"
      :range-separator="config.range || '-'"
      :picker-options="pickerOptions()"
      @change="changeValue"
      style="width:100%"
    >

    </el-date-picker>
  </div>
</template>

<script>
import {baseProps,mixin} from "@/components/control/contorlBase";

export default {
  name: "ComDate",
  props:{...baseProps},
  mixins:[mixin],
  data(){
    return {
      val : ""
    }
  },
  computed:{ },
  watch:{ },
  methods:{
    pickerOptions(){

      //禁用类型 0标识禁用今天以后的日期  1表示禁用今天之前的日期
      const disabledDateType = this.config.disabledDateType;
      const disabledDateDays = this.config.disabledDateDays || 0;

      return {
        disabledDate : (time) => {
          if(disabledDateType === "0"){
            return time.getTime() > new Date().getTime()+ disabledDateDays*24*60*60*1000
          }else if(disabledDateType === "1"){
            return time.getTime() < new Date().getTime() + (disabledDateDays - 1) *24*60*60*1000
          }else{
            return false;
          }
        }
      }
    }

  }
}
</script>

<style scoped>

</style>
