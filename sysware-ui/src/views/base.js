import {getDefaultValue, listFormField} from "@/api/system/formField";
import log from "@/views/monitor/job/log";

export const mixin = {
  data(){



    return {
      // 增加/修改
      form: {
        // 是否显示弹出层
        open: false,
        // 弹出层标题
        title: "",
        items:[],
        fields:{},
        lines:1,
        formId:"",
        submitUrl:"",
        method:"",
        openType:""
      },
      //选择列表
      checkList:[],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      //选择后禁用
      notCheck: false,
      //选择大于1个禁用
      notOrSingle: false,
    }
  },
  watch:{
    checkList:{
      handler(value){
        this.multiple = value.length < 1
        this.single = value.length != 1
        this.notCheck = value.length != 0
        this.notOrSingle = value.length > 1

      }
    }
  },
  methods:{
    /**
     * 刷新列表
     */
    getTableData(table){
      let tableRef = "table"
      if(table){
        tableRef = table
      }
      if(this.$refs[tableRef]){
        //调用子组件的刷新方法
        this.$refs[tableRef].$emit("getTableData");
      }

    },
    /**
     * 刷新表头
     *
     */
    getTableColumns(table){
      let tableRef = "table"
      if(table){
        tableRef = table
      }
      if(this.$refs[tableRef]){
        //调用子组件的刷新方法
        this.$refs[tableRef].$emit("getTableColumns");
      }

    },
    /**
     * 刷新数据
     * @param refName 刷新对象名称
     * @param refreshId 刷新对象ID
     */
    refreshChildren(refName,refreshId){
      let tableRef = "table"
      if(refName){
        tableRef = refName
      }
      if(this.$refs[tableRef]){
        //调用子组件的刷新方法
        this.$refs[tableRef].$emit("refreshChildren",refreshId);
      }
    },
    /**
     * 清空选中
     * @param refName
     */
    clearSelection(refName){
      let tableRef = "table"
      if(refName){
        tableRef = refName
      }
      if(this.$refs[tableRef]){
        //调用子组件的清空方法
        this.$refs[tableRef].$emit("clearSelection");
      }
    },
    /**
     * 设置表单的参数
     */
    setFormItem(_this,item,data,baseData){

      const table = _this.$refs.table

      //如果是新添加数据，默认给出最大序号
      if(table){
        if(baseData){
          baseData.sort =table.total + 1
        }else{
          baseData = {sort: table.total + 1}
        }
      }


        _this.form.formId = item.formId;
        //设置将要保存的表单ID

        _this.form.title = item.title;
        _this.form.submitEventName = item.submitEventName;
        _this.form.openType = item.openType
        /*if(data){
          _this.form.fields = Object.assign({},data);
        }else{

        }*/
       _this.getFormDefaultValue(baseData,data)

        _this.getFormField().then(() =>{
        _this.form.open = true;
        _this.form.fields.validateFormId = item.formId;
        //调用子组件的刷新方法
        _this.$refs.baseForm.$emit("setForm");
      });
    },
    /**
     * 获取表单字段
     */
    getFormField(){
     return listFormField({formId:this.form.formId}).then(res =>{
        this.form.items= res.rows
      })
    },
    /**
     * 获取表单默认值
     */
    getFormDefaultValue(baseData,data){
      getDefaultValue({formId:this.form.formId}).then(res =>{
        this.form.fields = Object.assign({},res.data,baseData,data)
        this.form.fields.validateFormId = this.form.formId
      })
    },
    /**
     * 按钮点击事件
     * @param openType
     */
    baseClick(openItem){
      this.setFormItem(openItem);
    },
    /**
     * 清除数据
     * @param openType
     */
    clearData(refName){
      let tableRef = "table"
      if(refName){
        tableRef = refName
      }
      this.$refs[tableRef].$emit("clearData");
    },
    /**
     * 计算操作列的宽度
     * @param arr
     */
    operationWidth(arr){
      let textCount = 0
      if(arr){
        arr.map(i =>{
          if(i.showText){
            textCount = textCount + i.text.length
          }
        })
      }
      return arr? arr.length * 30 + textCount * 12 + 20 : 0;
    },
    setCustomFormItem(_this,item,data,title){
      _this.form.open = true;
      _this.form.items= item;
      _this.form.title = title;
      _this.form.fields = Object.assign({},data);
      _this.$refs.baseForm.$emit("setForm");
    }

  },
  computed:{
    //计算弹出框的宽度
    formWidth(){
      let width = 420*this.form.lines - 20*(this.form.lines - 1);
      return width+"px";
    }
  }
}


