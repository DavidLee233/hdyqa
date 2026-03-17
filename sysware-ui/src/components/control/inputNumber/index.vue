<template>
  <el-input-number v-model="val" @change="changeValue" :min="1"   :class="defaultClass"></el-input-number>
</template>

<script>
import {baseProps,mixin} from "@/components/control/contorlBase";
export default {
  name: "ComInputNumber",
  props:{ ...baseProps },
  mixins:[mixin],
  data(){
    return {
      val:1
    }
  },
  watch:{ },
  methods:{ },
  computed: { }
}
</script>

<style scoped>

</style>
