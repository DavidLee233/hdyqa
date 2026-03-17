<template>
  <div>
    <template v-for="(item, index) in dictOptions">
      <template v-if="values.includes(item.dictValue)">
        <span
          v-if="item.listClass == 'default' || item.listClass == ''|| item.listClass == 'text'"
          :key="item.dictValue"
          :index="index"
          :class="item.cssClass"
          >{{ item.dictLabel }}</span
        >
        <el-tag
          v-else
          :key="item.dictValue"
          :index="index"
          :type="item.listClass == 'primary' ? '' : item.listClass"
          :class="item.cssClass"
        >
          {{ item.dictLabel }}
        </el-tag>
      </template>
    </template>
  </div>
</template>

<script>
export default {
  name: "DictTag",
  props: {
    options: {
      type: Array,
      default: null,
    },
    value: [String, Array],
    dictType:{
      type: String,
      default: '',
    }
  },
  created() {
    if(this.dictType){
      this.getDicts(this.dictType).then(response => {
        this.dictOptions = response.data;
        let actions = [];
        Object.keys(this.dictOptions).some((key) => {
          if (this.dictOptions[key].value == ('0')) {
            actions.push(this.dictOptions[key].label);
            return true;
          }
        })
      });
    }else if(this.options){
      this.dictOptions = this.options
    }
  },
  data(){
    return {
      dictOptions: []
    }
  },
  computed: {
    values() {
      if (this.value) {
        return Array.isArray(this.value) ? this.value : [this.value];
      } else {
        return [];
      }
    },
  }
};
</script>
<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
