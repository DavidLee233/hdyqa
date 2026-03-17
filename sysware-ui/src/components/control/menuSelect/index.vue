<template>
  <treeselect
    v-model="val"
    :options="menuOptions"
    :normalizer="normalizer"
    :show-count="true"
    placeholder="选择上级菜单"
    :class="defaultClass"
    :disabled="disabled"
    @select="changeValue"
  />
</template>



<script>
import {baseProps,mixin} from "@/components/control/contorlBase";
import {listMenu} from "@/api/system/menu";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
export default {
  name: "ComMenuSelect",
  props:{ ...baseProps },
  components:{Treeselect},
  mixins:[mixin],
  data(){
    return {
      val:"",
      menuOptions:[]
    }
  },
  created() {
    this.initOptions();
  },
  methods:{
    /** 转换菜单数据结构 */
    normalizer(node) {
      if (node.children == null || node.children == undefined || node.children == 'null') {
        delete node.children;
      }
      return {
        id: node.menuId,
        label: node.menuName,
        children: node.children
      };
    },
    initOptions(){
      listMenu().then(response => {
        this.menuOptions = response.data;
        const menu = { menuId: 0, menuName: '主类目', children: [] };
        menu.children = this.handleTree(response.data, "menuId");
        this.menuOptions.push(menu);
      });
    },/**
     * 选择时回传数据
     */
    changeValue(v){

      this.$emit("update:value",v.menuId);
    },
  }
}
</script>

<style scoped>

</style>
