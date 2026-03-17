<template>
  <el-cascader
    v-model="deptId"
    :options="deptOptions"
    :props="props"
    filterable
    :show-all-levels="false"
    @change="setValue"
    ></el-cascader>
</template>

<script>
  import {treeselect} from "@/api/system/dept";
  export default {
    name: "deptSelect",
    props:['value'],
    data() {
      return {
        deptId: this.value,
        deptOptions: [],
        props:{
          value:'id',
          expandTrigger:'hover',
          emitPath:false
        }
      }
    },
    watch:{
      value(val){
        console.log(val)
        this.deptId = val
      }
    },
    created() {
      this.getDeptSelect();
    },
    methods: {
      getDeptSelect() {
        treeselect().then(response => {
          this.deptOptions = response.data;
        });
      },
      setValue(){
        this.$emit('input',this.deptId);
      }
    }
  }
</script>

<style scoped>

</style>
