<template>
  <el-select v-model="id" @change="setValue" placeholder="请选择">
    <el-option
      v-for="item in securityList"
      :key="item.id"
      :label="item.name"
      :value="item.id">
    </el-option>
  </el-select>
</template>

<script>
  import {listDataSecurity} from "@/api/system/security";

  export default {
    name: "dataSecuritySelect",
    props:['value','securityValue'],
    data() {
      return {
        securityList: [],
        securityMap:new Map(),
        id:this.value
      }
    },
    watch:{
      value(val){
        this.id = val
      }
    }
    ,
    created() {
      this.getList();
    },
    methods: {
      getList() {
        listDataSecurity().then(res => {
          this.securityList = res.rows;
          this.securityList.forEach(item =>{
            this.securityMap.set(item.id,item)
          })
        })

      },
      /**
       * 设置选中的值
       */
      setValue(){
        this.$emit('input',this.id);
        //将密级值进行回传
        this.$emit('update:securityValue',this.getSelectSecurityValue());
      },
      getSelectSecurityValue(){
        return this.securityMap.get(this.id).value
      }
    }
  }
</script>

<style scoped>

</style>
