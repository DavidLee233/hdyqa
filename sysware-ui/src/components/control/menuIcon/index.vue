<template>
  <el-popover
    placement="bottom-start"
    width="250"
    trigger="click"
    @show="$refs['iconSelect'].reset()"
  >
    <IconSelect ref="iconSelect" @selected="selected" />
    <el-input slot="reference" v-model="val" placeholder="点击选择图标"  :class="defaultClass" readonly>
      <svg-icon
        v-if="val"
        slot="prefix"
        :icon-class="val"
        class="el-input__icon"
        style="height: 32px;width: 16px;"
      />
      <i v-else slot="prefix" class="el-icon-search el-input__icon" />
    </el-input>
  </el-popover>
</template>



<script>
import {baseProps,mixin} from "@/components/control/contorlBase";
import IconSelect from "@/components/IconSelect";
export default {
  name: "ComMenuIcon",
  props:{ ...baseProps },
  components:{IconSelect},
  mixins:[mixin],
  data(){
    return {
      val:""
    }
  },
  methods:{
    // 选择图标
    selected(name) {
      this.val = name;
      this.$emit("update:value",name);
    },
  }
}
</script>

<style scoped>

</style>
