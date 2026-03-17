import request from "@/utils/request";
import log from "@/views/monitor/job/log";

const createRules = (data,fields) => {

  data.forEach(item => {
    let rules_array = [];
    //如果有必填选项 增加必填规则
    /*if((item.required && item.required!=="0") || item.required==="1"){
      const rule = { required: true, message: item.message || createMessage(item),trigger: 'blur'}
      rules_array.push(rule)
    }*/

    //如果有必填选项 增加必填规则
    if(item.ruleList  && Array.isArray(item.ruleList) && item.ruleList.length>0){
      item.ruleList.map(ruleItem =>{
        let rule = {} ;
        switch (ruleItem.ruleValue) {
          //必填验证
          case 'required':
              rule = { required: true, message: ruleItem.message || createMessage(item),trigger: 'blur'}
              rules_array.push(rule)
              break;
          //正则表达式验证
          case 'verifyRegText':
              rule = { validator: verifyRegText,regText:ruleItem.regText, message: ruleItem.message,trigger: 'blur'}
              rules_array.push(rule)
              break;
          //长度验证
          case 'verifySize':
              rule = { max:ruleItem.max,min:ruleItem.min, message: ruleItem.message || item.label+"长度在"+ruleItem.min+"-"+ruleItem.max+"之间",trigger: 'blur'}
              rules_array.push(rule)
              break;
          //重复性验证
          case 'verifyRepetition':
              rule = { validator: verifyRepetition,validateIdName:ruleItem.validateIdName
                ,otherField:ruleItem.otherField,fields:fields,tableName:ruleItem.tableName,
              message: ruleItem.message,trigger: 'blur'}
              rules_array.push(rule)
              break;
          //密码验证
          case 'verifyPassword':
              rule = { validator: verifyPassword,fields:fields,checkName:ruleItem.checkName,
              regText:ruleItem.regText,message: ruleItem.message,trigger: 'blur'}
              rules_array.push(rule)
            break;
          //密级匹配验证
          case 'security':
            rule = { validator: security,fields:fields,checkName:ruleItem.checkName,
              regText:ruleItem.regText,message: ruleItem.message,trigger: 'blur'}
            rules_array.push(rule)
            break;
          //角色匹配验证
          case 'roleKey':
            rule = { validator: verifyRoleKey,fields:fields,checkName:ruleItem.checkName,
              regText:ruleItem.regText,message: ruleItem.message,trigger: 'blur'}
            rules_array.push(rule)
            break;
        }
      })

    }


    /*//有自定义验证规则
    if(item.rules && Array.isArray(item.rules) && item.rules.length>0 ){
      rules_array = rules_array.concat(item.rules)
    }
    item.rules = rules_array*/

    item.rules = rules_array
  })
  return data;
}

const  createMessage = (data)=>{
  let msg='';
  switch (data.type) {
    case 'input':
    case 'inputNumber':
    case 'password':
      msg = '请输入'
      break;
    case 'select':
    case 'userSelect':
    case 'deptSelect':
    case 'date':
    case 'checkbox':
    case 'tree':
      msg = '请选择'
      break;

  }
  return msg+data.label;
}

/**
 * 验证数据是否重复
 * @param rule
 * @param value
 * @param callback
 */
const verifyRepetition = (rule, value, callback) =>{
  checkRepetition(rule,value,callback).then(res =>{
    callback();
  })
}
/**
 * 查询数据是否重复
 * @param rule
 * @param value
 * @param callback
 * @returns {Promise<AxiosResponse<any>>}
 */
const checkRepetition = (rule,value,callback) =>{

  let params = {}

  params.validateIdName = rule.validateIdName
  params.tableName = rule.tableName

  params.field = rule.field

  let otherFieldValidate = {}
  if(rule.otherField){
   const filedArr =  rule.otherField.split(",");
    filedArr.map(i =>{
      otherFieldValidate[i] = rule.fields[i]
    })

  }
  params.validateIdValue = rule.fields[rule.validateIdName]
  params.otherFieldValidate = otherFieldValidate
  params.value  =  value



 return request({ url: "/system/validate/verifyRepetition", method: "get",
    params:params}
  ).then(res => {
    if(!res.data){
      callback(new Error("已经存在"))
    }
  })

}

/**
 * 密码验证
 * @param rule
 * @param value
 * @param callback
 */
const verifyPassword = (rule, value, callback) =>{

  if (value !== rule.fields[rule.checkName]) {
    rule.message = '两次输入密码不一致!'
    callback(new Error());
  }
  if(rule.regText && value){
    const reg =  RegExp(rule.regText)
    if (!reg.test(value)) {
      callback(new Error());
    }
  }

  callback();
}

/**
 * 正则表达式验证
 * @param rule
 * @param value
 * @param callback
 */
const verifyRegText = (rule, value, callback) =>{
  if(rule.regText && value){
    const reg =  RegExp(rule.regText)
    if (!reg.test(value)) {
      callback(new Error());
    }
  }
  callback();
}

/**
 * 密级匹配验证
 * @param rule
 * @param value
 * @param callback
 */
const security = (rule, value, callback) =>{
  if(rule.regText){
   let regTexts = rule.regText.split(",");
   if(rule.fields[regTexts[2]]){
     let flag = true;
     switch (regTexts[1]) {
       case '>':
         flag =   rule.fields[regTexts[0]] > rule.fields[regTexts[2]]
         break;
       case '>=':
         flag =   rule.fields[regTexts[0]] >= rule.fields[regTexts[2]]
         break;
       case '=':
         flag =   rule.fields[regTexts[0]] = rule.fields[regTexts[2]]
         break;
       case '<':
         flag =   rule.fields[regTexts[0]] < rule.fields[regTexts[2]]
         break;
       case '<=':
         flag =   rule.fields[regTexts[0]] <= rule.fields[regTexts[2]]
         break;
     }
     if(!flag){
       callback(new Error());
     }
   }
  }
  callback();
}

const verifyRoleKey = (rule, value, callback) =>{
 let params  = {}

  if(rule.regText){
    let regTexts = rule.regText.split(",");
    params.userId = rule.fields[regTexts[0]];
    params.roleKey = rule.fields[regTexts[1]];
    if(regTexts.length > 2){
      params.rowType = rule.fields[regTexts[2]];
    }

      return request({
        url: "/system/validate/verifyRoleKey",
        method: "get",
        params:params
      }
      ).then(res => {
        if(!res.data){
          callback(new Error())
        }
      })
  }
  callback();

}

export default createRules;
