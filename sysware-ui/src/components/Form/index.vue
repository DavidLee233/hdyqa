<template>
  <el-form  ref="form" :model="fields" label-width="100px" >
    <el-row>
      <slot name="pre"></slot>
      <template v-for="item in formItems">
        <el-col :span="item.span || span">

        <el-form-item v-if="showFunction(item)"
                      :key="item.prop"
                      :label="item.label"
                      :prop="item.prop"
                      :rules="item.rules"
        >
          <component :value.sync="fields[item.prop]" :formData.sync="fields"
                     :updateValue.sync="fields[item.updateField]"
                     :updateField="item.updateField"
                     :dynamicData.sync="dynamicItem"
                     :optionsCache.sync="optionsCache"
                     :config="updateItem(item)" :is="item.type?`com-${item.type}`:'com-text'"  >

          </component>
        </el-form-item>
        </el-col>
      </template>
    </el-row>
  </el-form>
</template>

<script>
import createRules from "@/components/Form/createRules";

const modules = {}
const files = require.context("../control",true,/\index.vue$/)
files.keys().forEach(item =>{
  const key = item.split("/")
  const name = key[1]
  modules[`com-${name}`] = files(item).default

})
  export default {
    name: "Form",
    components: {...modules},
    props:{
      //表单项数据
      fields:{
        type:Object,
        default:()=>({})
      },
      //表单项
      items:{
        type:Array,
        default:()=>[]
      },
      //按钮
      buttons:{
        type:Array,
        default:()=>[]
      },
      beforeSubmit:Function,
      beforeCancel:Function,
      //列数
      lines:{
        type:Number,
        default:1
      }

    },
    beforeMount() {
      this.formItems = createRules(this.items,this.fields)
    },
    data(){
      return {
        formItems:[],
        hiddenItemArr:[],
        dynamicItem:{},
        optionsCache:new Map(),
        tempData:{}//存储临时的数据
      }
    },
    watch :{
      "items" : {
        handler(value){
          this.formItems = createRules(value,this.fields)
        }
      }

    },
    computed:{
      span(){
        return 24/this.lines
      }

    },
    methods:{
      /**
       * 验证数据的方法
       * */
      validate(callback){
        this.$refs.form.validate((valid) => {
          callback(valid)
        })
      },
      /**
       * 判断字段是否显示
       * @param show
       * @param prop
       * @returns {boolean}
       */
      showFunction(data){


        if(data.showabled === '1'){
          return false
        }
        //console.log(data.prop,this.fields[data.prop])
        //如果属性不存在，则为属性设置默认值
        /*if(data.defaultValue && this.fields[data.prop] === undefined) {
          this.fields[data.prop] = data.defaultValue
        }*/



        if(data.visible && data.visible.length > 0 && data.visible[0].visible){

          const showItem = data.visible
          const item = showItem.filter(i =>{

           return i.visibleValue === this.fields[i.visible] || i.visibleValue === this.fields[i.visible+"Label"]
             || i.visibleValue === this.fields[i.visible.replace("Id","")+"Name"]
          })
          //字段出现的时候设置已设定好的默认值 && !this.fields[data.prop]
          if(item.length > 0 && item[0].visibleDefaultValue){
            if(data.config?.multiple){
              this.fields[data.prop] = item[0].visibleDefaultValue.split(",")
            }else{
              this.fields[data.prop] = item[0].visibleDefaultValue
            }
          }
        const flag = item.length >= showItem.filter(i => i.status === '1').length;
          //字段隐藏时清空值
          /*if(!flag && this.fields[data.prop]){
            this.fields[data.prop] = ""
          }*/

         return flag;
        }

        return true;
      },
      updateItem(item){


        if(item.dynamicLabel){


          const dynamicItem = this.dynamicItem[item.dynamicLabel];

          if(dynamicItem && dynamicItem.url){
            item.type = dynamicItem.type
            item.url = dynamicItem.url
            item.props =  dynamicItem.props
          }else{
            item.type = 'input'
            item.url = ''
            item.props = ''
          }
        }
        return item
      }

    }
  }
</script>

<style scoped>

</style>
