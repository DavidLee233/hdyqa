<template>
  <div>
    <el-checkbox v-model="expand" @change="handleCheckedTreeExpand">{{expandName}}</el-checkbox>
    <el-checkbox v-model="nodeAll" @change="handleCheckedTreeNodeAll">全选</el-checkbox>
    <el-checkbox v-model="checkStrictly" @change="handleCheckedTreeConnect">父子联动</el-checkbox>
    <el-tree
      ref="tree"
      class="tree-border"
      :data="options"
      show-checkbox
      node-key="id"
      empty-text="加载中，请稍后"
      :props="defaultProps"
      :check-strictly="!checkStrictly"
      @check="changeValue"
      :class="defaultClass"
    ></el-tree>
  </div>

</template>

<script>
import {baseProps,mixin} from "@/components/control/contorlBase";
export default {
  name: "ComTree",
  props:{ ...baseProps },
  mixins:[mixin],
  data(){
    return {
      val:"",
      defaultProps: {
        children: "children",
        label: "label"
      },
      expand:false,
      nodeAll:false,
      checkStrictly:true,
      expandName:"展开",
    }
  },
  watch:{
    value:{
      handler(newValue){

        //this.val = newValue
      },
      immediate:true
    }
  },
  updated() {
    this.setChecked();
  },
  methods:{
    /**
     * 选择时回传数据
     */
    changeValue(){
      // 目前被选中的节点
      let checkedKeys = this.$refs.tree.getCheckedKeys();
      // 半选中的节点
      let halfCheckedKeys = this.$refs.tree.getHalfCheckedKeys();
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);

      this.$emit("update:value",checkedKeys);

    },
    /**
     * 设置选中值
     */
    setChecked(){
      this.$refs.tree.setCheckedNodes([]);

      if(this.val){
        this.val.forEach((v) => {
          this.$nextTick(()=>{
            this.$refs.tree.setChecked(v, true ,false);
          })
        })
      }
    },
    /**
     * 展开收起树
     * @param value
     */
    handleCheckedTreeExpand(value){
      this.expandName = value?"收起":"展开"
      let treeList = this.options;
      for (let i = 0; i < treeList.length; i++) {
        this.$refs.tree.store.nodesMap[treeList[i].id].expanded = value;
      }
    },
    /**
     * 全选/全不选树
     * @param value
     */
    handleCheckedTreeNodeAll(value){
      console.log(value,this.options)
      this.$refs.tree.setCheckedNodes(value ? this.options: []);
    },
    /**
     * 是否父子联动
     * @param value
     */
    handleCheckedTreeConnect(value){
      this.checkStrictly = value ? true: false;
    }
  },
  computed: { }
}
</script>

<style scoped>

</style>
