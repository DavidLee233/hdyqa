<template>
  <el-input v-model="val" type="textarea" rows="3" @input="changeValue" maxlength="100"
            show-word-limit :disabled="disabled" :class="defaultClass"></el-input>
</template>

<script>
import {baseProps,mixin} from "@/components/control/contorlBase";
export default {
  name: "ComTextarea",
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
