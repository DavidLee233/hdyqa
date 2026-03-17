<template>
  <div>{{ data[prop] }}</div>
</template>

<script>
export default {
  name: "ComImage",
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

    }
  }
}
</script>

<style scoped>

</style>
