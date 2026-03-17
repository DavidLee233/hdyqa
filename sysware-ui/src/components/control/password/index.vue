<template>
  <el-input v-model="val" @input="changeValue" show-password :disabled="disabled" :class="defaultClass"></el-input>
</template>

<script>
import {baseProps,mixin} from "@/components/control/contorlBase";
export default {
  name: "ComPassword",
  props:{ ...baseProps },
  mixins:[mixin],
  data(){
    return {
      val:""
    }
  },
  watch:{ },
  methods:{ },
  computed: { }
}
</script>

<style scoped>

</style>
