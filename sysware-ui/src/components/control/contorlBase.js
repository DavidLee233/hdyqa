import request from "@/utils/request";


/**
 *
 * 定义自定义组件默认的props属性
 * @type
 */
export const baseProps = {
  //字段的值
  value:{
    type:[String,Number,Array,Date,Boolean],
    default:""
  },
  //字段相关配置
  config:{
    type:Object,
    default:() =>({})
  },
  //需要关联更新的字段
  updateField:{
    type:String,
    default:""
  },
  //表单数据
  formData:{
    type:Object,
    default:() =>({})
  },
  dynamicData:{
    type:Object,
    default:() =>({})
  },
  optionsCache:{
    type:Map,
    default:() => new Map
  }
}

export const mixin = {

  data(){
    return {
      val:"",
      options:[],
      //默显示的属性名称
      props:{
        label:"label",
        value:"value"
      },
      queryParams:{
      }
    }
  },
  computed:{
    //请求的地址
    url() {
      return this.config?.url
    },
    //请求的方法
    method(){
      return this.config?.method || 'get'
    },
    //是否初始化数据
    initRequest(){
      return this.config?.initRequest === "1" || false
    },
    //是否可搜索
    searchable(){
      return this.config?.searchable === "1"  || false
    },
    //搜索时的列名
    keyword(){
      return this.config?.keyword || 'searchValue'
    },
    //是否可多选
    multiple(){
      return this.config?.multiple === "1" || false
    },
    //是否禁用
    disabled(){
      return this.config?.disabled === "1" || false
    },
    //是否显示
    showabled(){
      return this.config?.showabled === "1"  || false
    },
    //默认样式
    defaultClass(){
      return this.config?.defaultCss || "w-pre-90"
    },
    urlParams(){
      return  this.config?.urlParams || ''
    }
  }
  ,
  watch:{
    value:{
      handler(newValue){
        //对日期格式进行统一处理
        if((this.config.prop.includes("Time") || this.config.prop.includes("time"))&& this.config.prop != 'keepTime'){
          if(newValue!== null && newValue.length > 10){
            newValue = newValue.substring(0,10);
          }
        }

        if(newValue === null || newValue.length === 0 ) {
          if(this.multiple){
            newValue = []
          }else{
            newValue = ''
          }
        }

        if(newValue!== '' &&  newValue  && this.multiple && newValue.length > 0){
          const valArray = newValue.split(",");
          if(valArray.length>0 && valArray[0] !== ''){
            this.val = valArray
          }else{
            this.val = [];
          }
        }else{
          this.val = newValue
        }

      },
      immediate:true
    },
    config:{
      handler(newValue){
        this.initOptions();
        this.initProps();
      },
      immediate:true
    }
  },
  mounted() {
  },
  methods:{
    /**
     * 选择时回传数据
     */
    changeValue(v){

      if(this.options){

        const item =  this.options.filter(i => i[this.props.value] === this.val )

        const prop = this.config.prop

        let data = {}
        if(item.length > 0){
          data.url = item[0].url
          data.method = item[0].method
          data.props = item[0].props
          data.type = item[0].type

          this.dynamicData[prop] = data

          this.$emit("update:dynamicData",this.dynamicData);
        }
      }

      if(this.multiple){
        this.$emit("update:value",this.val.join(","));
      }else{
        this.$emit("update:value",this.val);
      }

     /* if(this.val!== '' && this.val && this.multiple && this.val.length > 0){
        this.$emit("update:value",this.val.join(","));
      }else{
        this.$emit("update:value",this.val);
      }*/


    },
    /**
     * 初始化下拉选项的值
     */
    initOptions(){
      /**
       * 如果存在远程请求地址，则远程请求数据
       */


      if(this.url){
        this.getOptions();
        return false
      }

      if(!this.options){

        const options = this.config?.options
        if(options && Array.isArray(options) && options.length>0){
          this.options = options
        }
      }




    },
    /**
     *
     * 初始化下拉列表显示配置
     *
     */
    initProps(){

      /*if(!this.config.props){
        return ;
      }*/

      //获取配置的prop
      const propsStr  = this.config?.props

      let options = {label:"label",value:"value"}
      if(propsStr){
        if(typeof propsStr === 'string' && propsStr.indexOf(",")!==-1){
          options.label =  propsStr.split(",")[0] //将配置的第一个属性作为显示名称
          options.value =  propsStr.split(",")[1] //将配置的第二个属性作为获取的值

        }
      }

      //获取默认的key
      const keys = Object.keys(this.props)
      //如果传过来的prop存在并且等于Object对象
      if(options && Object.prototype.toString.call(options) === '[object Object]'){
        this.props = options;
        //将传过来的prop替换成默认的prop
        for (const key in options){
          if(keys.includes(key)){
            this.props[key] = options[key]
          }
        }
      }
    },
    /**
     * 远程获取数据
     */
    getOptions(value){

      let url = this.url
      if(url && url.indexOf("{")!=-1){
        const urlParams =  url.substring(url.indexOf("{")+1,url.indexOf("}"))
        url =  url.replace(url.substring(url.indexOf("{"),url.indexOf("}")+1),this.formData[urlParams])
      }

      //如果请求地址不完整，则直接补充数据字典请求路径
      if(url.indexOf("/") === -1){
        url = "/system/dict/data/type/" + url
      }

      const requestData = {
        url: url,
        method: this.method
      }
      /**
       * 处理请求参数
       */
      if(this.keyword){

        let params = {};

        this.keyword.split(",").forEach(key => {
          if(key.includes('=')){
            const keyValue = key.split("=")
            params.searchField = keyValue[0]
            params[keyValue[0]] = keyValue[1]
          }else{
            params[key] = this.formData[key]
          }
        })

        if(this.method === 'get'){
          requestData.params = params; //this.formData[this.keyword] ? this.formData[this.keyword] : {roleType:'project',searchField:'roleType'}
        }
        if(this.method === 'post'){
          requestData.data = params ; //this.formData[this.keyword] ? { [this.keyword]:this.formData[this.keyword] } : this.keyword
        }
      }


      if(this.optionsCache.get(JSON.stringify(requestData))){
        this.options = this.optionsCache.get(JSON.stringify(requestData))
        return ;
      }

      request(requestData).then(res => {
        this.options = res.data ||  res.rows;

        let showOption = this.options.filter(i => i[this.props.value] === this.value)
        if(showOption && showOption.length > 0){
          this.formData[this.config.prop+"Label"] = showOption[0][this.props.label]
        }
        this.optionsCache.set(JSON.stringify(requestData),this.options)
        this.$emit("update:optionsCache",this.optionsCache);

        if(this.multiple ){
          if(this.value &&  this.value!==''){
            this.val = this.value.split(",")
          }else{
            this.val = []
          }
        }else{
          this.val = this.value
        }
      })
    },
    /**
     * 远程搜索数据
     */
    searchValue(query){

      if(this.url && this.searchable){
        this.getOptions(query)
      }

    },
    /**
     * 下拉选项框出现或隐藏
     * @param value
     */
    visibleChange(value){
      if(this.url && value){
        this.initProps()
        this.getOptions()

      }
    }

  }

}


