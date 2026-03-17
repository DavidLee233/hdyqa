<template>
 <div v-html="val"
      :disabled="disabled"
 />
</template>



<script>
import {baseProps,mixin} from "@/components/control/contorlBase";
export default {
  name: "ComText",
  props:{ ...baseProps},
  mixins:[mixin],
  data(){
    return {
      val:""
    }
  }
}
</script>

<style scoped>

</style>
