<template>
  <div style="width: 100%;">
    <el-dialog :title="form.title"  v-dialogDrag append-to-body
               :visible.sync="form.open"
               :close-on-click-modal="false"
               :width="width"
               :before-close="handleClose">

      <Form ref="form" :fields.sync="form.fields" :items="form.items" :lines="lines" >
        <template v-slot:pre="slot">
          <slot name="pre"></slot>
        </template>
      </Form>

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :loading="loading">确 定</el-button>
        <el-button type="danger" @click="handleClose" >取 消</el-button>
      </span>
    </el-dialog>

<!--    <el-drawer
      v-if="form.openType === 'drawer'" v-if="form.openType === 'dialog'"
      :title="form.title"
      :before-close="handleClose"
      :visible.sync="form.open"
      :direction="form.direction"
      custom-class="demo-drawer"
      :size="formWidth"
      ref="drawer">

      <Form ref="form" :fields="form.fields" :items="form.items" :lines="form.lines" ></Form>
      <span >
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button type="danger" @click="handleClose" >取 消</el-button>
      </span>

    </el-drawer>-->
  </div>

</template>
<script>

import Form from "@/components/Form";


export default {
  name: "openBase",
  components: {Form},
  props:{
    baseForm:{
      type:Object,
      default:{}
    },
    width:{
      type:String,
      default: "1000px"
    },
    lines:{
      type: Number,
      default:2
    }
  },
  mounted() {
    //父组件可以调用子组件的方法
    this.$nextTick(() => {
      this.$on("setForm",() =>{
        this.setForm();
        this.loading = false
      })
    })

  },
  data(){
    return {
        form:{},
        defaultFields:"",
        loading:false
    }
  },
  methods:{
    setForm(){
      this.form = this.baseForm
      this.defaultFields = JSON.stringify(this.form.fields)
    },
    handleClose(){
      const changeFlag = this.defaultFields !== JSON.stringify(this.form.fields)

      if(changeFlag){
        this.$confirm('数据已变更，是否需要保存？').then(() =>{
          this.submitForm();
        }).catch(_ => {
          this.formClose()
        });
      }else{
        this.formClose()
      }

    },
    submitForm(){
      this.loading = true
      this.$refs.form.validate(valid => {
        if (valid) {
              //清空被隐藏字段的值
              this.form.items.map(data =>{
                if(data.visible && data.visible.length > 0 && data.visible[0].visible){

                  const showItem = data.visible
                  const item = showItem.filter(i =>{
                    return i.visibleValue === this.form.fields[i.visible] || i.visibleValue === this.form.fields[i.visible+"Label"]
                      || i.visibleValue === this.form.fields[i.visible.replace("Id","")+"Name"]
                  })

                  const flag = item.length >= showItem.filter(i =>
                    {
                      return i.status === '1'
                    }
                  ).length;
                  //清空隐藏字段的值
                  if(!flag){
                    this.form.fields[data.prop] = ""
                    this.form.fields[data.prop.replace("Id","")+"Name"] = ""
                    this.form.fields[data.prop+"Label"] = ""
                  }

                }
              })
          this.$emit("submit",this.form.submitEventName);
        }else{
          this.loading = false
        }
      });
    },
    //关闭用户修改框
    formClose(){
      this.form.open = false
      this.form.fields = {} ;
    }
  }
}
</script>

