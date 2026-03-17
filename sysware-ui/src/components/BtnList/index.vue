<template>
  <div>
    <template v-for="item in btnArr">
      <el-tooltip :content="formatTipContent(item,checkData)" placement="top" :enterable="false">
       <el-col  :span="1.5">
          <template v-if="item.openType === 'popConfirm'">
            <el-popconfirm
              :confirm-button-text='item.confirmText'
              :cancel-button-text='item.cancelText'
              :icon="item.popConfirmIcon"
              :icon-color="item.color"
              :title="item.msg"
              width="200"
              @confirm="handleClick(item.functionName,item,checkData)"
            >
              <el-button
                :type="item.type"
                :icon="item.icon"
                :size="item.size"
                v-hasPermi="formatPermission(item.permission)"
                :disabled="disabled(item,checkData)"
                slot="reference">
                <span v-if="item.showText !== '1'">{{ item.text }}</span></el-button>
            </el-popconfirm>
          </template>
          <template v-else-if="item.openType === 'confirm'">
            <el-button
              :type="item.type"
              :icon="item.icon"
              :size="item.size"
              v-hasPermi="formatPermission(item.permission)"
              :disabled="disabled(item,checkData)"
              @click="confirmClick(item.functionName,item,checkData)"
            >
              <span v-if="item.showText !== '1'">{{ item.text }}</span>
            </el-button>
          </template>

          <el-button
            v-else
            :type="item.type"
            :icon="item.icon"
            :size="item.size"
            v-hasPermi="formatPermission(item.permission)"
            :disabled="disabled(item,checkData)"
            @click="handleClick(item.functionName,item,checkData)"
          >
            <span v-if="item.showText !== '1'">{{ item.text }}</span>
          </el-button>
        </el-col>
      </el-tooltip>
    </template>
  </div>
</template>

<script>
export default {
  name: "btnList",
  props:{
    buttonArray:{
      type:Array,
      default:() => []
    },
    checkData:{
      type:Object,
      default:() => ({})
    },
    checkList:{
      type:Array,
      default:() => []
    }
  },
  data(){
    return {
      btnArr:[],
      val:1,
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      //选择后禁用
      notCheck: false,
      //选择大于1个禁用
      notOrSingle: false
    }
  },
  watch: {
    buttonArray: {
      handler(newValue) {
        this.btnArr = newValue
      },
      immediate: true
    },/*
    checkData: {
      handler(newValue) {
        this.val = newValue
      },
      immediate: true
    },*/
    checkList:{
      handler(value) {
        this.multiple = value.length < 1
        this.single = value.length != 1
        this.notCheck = value.length != 0
        this.notOrSingle = value.length > 1
      }
    }
  },
  methods:{
    handleClick(functionName,item,data){
      this.$emit("click",functionName,item,data);
    },
    /**
     * 按钮禁用函数
     * @param item
     * @returns {*}
     */
    disabled(item,checkData){
      let result = this[item.disabledType];
      if(result  === undefined){
        this.$emit('disableFn',{item:item,checkData:checkData}, val => { result = val })
      }

      return result
    },
    /**
     * 确认按钮回调
     * @param functionName
     * @param item
     * @param data
     */
    confirmClick(functionName,item,data){
      this.$confirm(item.msg, item.title, {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: item.type
      }).then(() =>{
        this.$emit("click",functionName,item,data);
      })
    },
    /**
     * 格式化权限
     * @param permission
     * @returns {*}
     */
    formatPermission(permission){
      if(permission instanceof Array)
        return permission

      return permission.split(",")
    },
    /**
     * 处理提示信息
     */
    formatTipContent(item,checkData){

      let disable = this.disabled(item,checkData);

      return disable ? (item.tipContent ? item.tipContent : item.text) : item.text;
    }
  }
}
</script>

<style scoped>

</style>
