<template>
  <MyContainer>
    <Table ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
           :checkList.sync="checkList" :baseParams="baseParams"
           @click="callFunction"
           selection index operation :operation-width="operationWidth">
      <template v-slot:headerOperation="slot">

        <btn-list :buttonArray="headerBtnArr"
                  :checkList="checkList"
                  :check-data="null"
                  @click="callFunction"

        ></btn-list>

<!--        <el-col v-for="item in headerBtnArr" :span="1.5">

          <el-tooltip :content="item.text" placement="top">
            <el-button
              :type="item.type"
              :icon="item.icon"
              :size="item.size"
              :disabled="btnStatusChange(item)"
              @click="callFunction(item.functionName,item.openItem,slot.data)"
            ><span v-if="item.showText">{{ item.text }}</span>
            </el-button>
          </el-tooltip>


        </el-col>-->
<!--        @click="baseClick(item.openType)" @click="callFunction(item.functionName,item.openType,slot.data)"-->
<!--          <el-col :span="1.5">
            <el-button
              type="primary"
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
              v-hasPermi="['system:user:add']"
            >新增
            </el-button>
          </el-col>-->
<!--          <el-col :span="1.5">
            <el-button
              type="success"
              icon="el-icon-edit"
              size="mini"
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['system:user:edit']"
            >修改
            </el-button>
          </el-col>-->
          <el-col :span="1.5">
            <el-button
              type="danger"
              icon="el-icon-delete"
              size="mini"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['system:user:remove']"
            >冻结
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="success"
              icon="el-icon-upload2"
              size="mini"
              @click="handleImport"
              v-hasPermi="['system:user:import']"
            >导入
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="warning"
              icon="el-icon-download"
              size="mini"
              :loading="exportLoading"
              @click="handleExport"
              v-hasPermi="['system:user:export']"
            >导出
            </el-button>
          </el-col>
      </template>

      <template v-slot:operation="slot">

        <btn-list :buttonArray="operationBtnArr"
                  :checkList="checkList"
                  :check-data="slot.data"
                  @click="callFunction"

        ></btn-list>

<!--        <el-col v-for="item in operationBtnArr" :span="1.5">
          <el-tooltip :content="item.text" placement="top" :enterable="false">
            <template v-if="item.openItem.openType === 'popConfirm'">
              <el-popconfirm
                :confirm-button-text='item.openItem.confirmText'
                :cancel-button-text='item.openItem.cancelText'
                :icon="item.openItem.popConfirmIcon"
                :icon-color="item.openItem.color"
                :title="item.openItem.title"
                width="200"
                @confirm="callFunction(item.functionName,item.openItem,slot.data)"
              >
                <el-button
                  :type="item.type"
                  :icon="item.icon"
                  :size="item.size"
                  v-hasPermi="item.permission"
                  slot="reference"><span v-if="item.showText">{{ item.text }}</span></el-button>
              </el-popconfirm>

            </template>
            <el-button
              v-else
              :type="item.type"
              :icon="item.icon"
              :size="item.size"
              v-hasPermi="item.permission"
              @click="callFunction(item.functionName,item.openItem,slot.data)"
            >
              <span v-if="item.showText">{{ item.text }}</span>
            </el-button>
          </el-tooltip>
        </el-col>-->

<!--          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(slot.data)"
            v-has-role="['admin']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(slot.data)"
            v-hasPermi="['system:user:remove']"
          >删除
          </el-button>
          <el-button
          size="mini"
          type="text"
          icon="el-icon-circle-check"
          @click="handleAuthSecurity(slot.data)"
          v-hasPermi="['system:user:setSecurity']"
        >设置密级
        </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-key"
            @click="handleResetPwd(slot.data)"
            v-hasPermi="['system:user:resetPwd']"
          >重置密码
          </el-button>-->
<!--          <el-button
            size="mini"
            type="text"
            icon="el-icon-circle-check"
            @click="handleAuthRole(slot.data)"
            v-hasPermi="['system:user:setRole']"
          >分配角色
          </el-button>-->
      </template>


    </Table>


    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>





    <!--添加/修改用户对话框-->
    <el-dialog :title="userForm.title"  v-dialogDrag
               :visible.sync="userForm.open"
               :close-on-click-modal="false"
               :width="userFormWidth"
               :before-close="userFormClose">
      <Form ref="userForm" :fields="userForm.fields" :items="userForm.items" :lines="userForm.lines" ></Form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button type="danger" @click="userFormClose" >取 消</el-button>
      </span>
    </el-dialog>


    <!--修改用户密级对话框-->
    <el-dialog :title="securityForm.title"  v-dialogDrag
               :visible.sync="securityForm.open"
               :close-on-click-modal="false"
               :width="securityFormWidth"
               :before-close="securityFormClose">
      <Form ref="securityForm" :fields="securityForm.fields" :items="securityForm.items" :lines="securityForm.lines" ></Form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitSecurityForm">确 定</el-button>
        <el-button type="danger" @click="securityFormClose" >取 消</el-button>
      </span>
    </el-dialog>


    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          将文件拖到此处，或
          <em>点击上传</em>
        </div>
        <div class="el-upload__tip" slot="tip">
          <el-checkbox v-model="upload.updateSupport"/>
          是否更新已经存在的用户数据
          <el-link type="info" style="font-size:12px" @click="importTemplate">下载模板</el-link>
        </div>
        <div class="el-upload__tip" style="color:red" slot="tip">提示：仅允许导入“xls”或“xlsx”格式文件！</div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </MyContainer>

</template>

<script>
  import {
    delUser,
    addUser,
    updateUser,
    exportUser,
    resetUserPwd,
    changeUserStatus,
    importTemplate
  } from "@/api/system/user";
  import {getToken} from "@/utils/auth";
  import MyContainer from '@/components/my-container'
  import {setUserSecurity} from "@/api/system/user";
  import OpenBase from "@/views/openBase";
  import {mixin} from "@/views/base";
  import BtnList from "@/components/BtnList";

  export default {
    name: "User",
    components: {BtnList, OpenBase, MyContainer},
    mixins:[mixin],
    data() {
      return {
        //列地址
        columnUrl:"/system/user/index",
        //数据地址
        dataUrl:"/system/user/list",

        //请求基础参数
        baseParams:{path:"/system/user/index"},
        //操作列的宽度
        operationWidth:300,
        // 遮罩层
        loading: true,
        // 导出遮罩层
        exportLoading: false,
        visible:false,
        // 默认密码
        defaultPwd:"123456",
        // 用户导入参数
        upload: {
          // 是否显示弹出层（用户导入）
          open: false,
          // 弹出层标题（用户导入）
          title: "",
          // 是否禁用上传
          isUploading: false,
          // 是否更新已经存在的用户数据
          updateSupport: 0,
          // 设置上传的请求头部
          headers: {Authorization: "Bearer " + getToken()},
          // 上传的地址
          url: process.env.VUE_APP_BASE_API + "/system/user/importData"
        },
        // 修改密级参数
        securityForm: {
          // 是否显示弹出层（修改密级）
          open: false,
          // 弹出层标题（修改密级）
          title: "密级调整",
          items:[],
          fields:{},
          lines:1,
          formId:"/system/user/setSecurity"
        },
        // 增加/修改用户参数
        userForm: {
          // 是否显示弹出层（修改密级）
          open: false,
          // 弹出层标题（修改密级）
          title: "添加用户",
          items:[],
          fields:{},
          lines:1,
          addFormId:"/system/user/add",
          editFormId:"/system/user/edit"
        },
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          searchContent: '',
          orderByColumn: undefined,
          isAsc: undefined
        },
        headerBtnArr:[
          {
            btnId:"add",
            type:"primary",
            icon:"el-icon-plus",
            size:"mini",
            location:"header",
            text:"添加",
            showText:true,
            permission:['system:user:add'],
            openItem:{
              title:"添加用户",
              openType:"dialog",//弹出类型
              direction:"rtl",//弹出位置
              lines:1,
              formId:"/system/user/add",
              submitUrl:"/system/user",
              submitEventName:"submitForm",
              method:"post"

            },
            disabledType:"notOrSingle",
            functionName:"handleAdd"
          },
          {
            btnId:"edit",
            type:"success",
            icon:"el-icon-edit",
            size:"mini",
            location:"header",
            text:"修改",
            showText:true,
            permission:['system:user:edit'],
            openItem:{
              title:"修改用户",
              openType:"dialog",//弹出类型
              direction:"",//弹出位置
              lines:1,
              formId:"/system/user/edit",
              submitEventName:"submitForm",
            },
            disabledType:"single",
            functionName:"handleUpdate"
          }
        ],
        operationBtnArr:[
          {
            btnId:"edit",
            type:"primary",
            icon:"el-icon-edit",
            size:"mini",
            location:"header",
            text:"修改",
            showText:false,
            permission:['system:user:remove'],
            openItem:{
              title:"修改用户",
              lines:1,
              openType:"dialog",//弹出类型
              formId:"/system/user/edit",
              submitEventName:"submitForm",
            },
            disabledType:"single",
            functionName:"handleUpdate"
          },
          {
            type:"text",
            icon:"el-icon-delete",
            size:"mini",
            location:"operation",
            text:"删除",
            showText:true,
            permission:['system:user:remove'],
            openItem:{
              openType:"popConfirm",
              title:"提示",
              msg:"是否删除?",
              confirmText:"删除",
              cancelText:"取消",
              popConfirmIcon:"el-icon-info",
              color:"#F56C6C"
            },
            functionName:"handleDelete"
          },
          {
            btnId:"setSecurity",
            type:"text",
            icon:"el-icon-circle-check",
            size:"mini",
            location:"header",
            text:"密级调整",
            showText:true,
            permission:['system:user:setSecurity'],
            openItem:{
              title:"密级调整",
              openType:"dialog",//弹出类型
              lines:1,
              formId:"/system/user/setSecurity",
              submitEventName:"submitSecurityForm"
            },
            functionName:"handleAuthSecurity"
          },
          {
            btnId:"resetPassword",
            type:"text",
            icon:"el-icon-key",
            size:"mini",
            location:"header",
            text:"重置密码",
            permission:['system:user:remove'],
            openItem:{
              openType:"popConfirm",
              title:"确定重置密码为123456",
              confirmText:"重置",
              cancelText:"取消",
              popConfirmIcon:"el-icon-warning",
              color:"#f0cb16"
            },
            showText:true,
            functionName:"handleResetPwd"
          }
        ]
      };
    },
    created() {
      this.path = this.$route.path+'/index';
    },
    methods: {
      // 用户状态修改
      handleStatusChange(row) {
        let text = row.status === "0" ? "启用" : "停用";
        this.$confirm('确认要"' + text + '""' + row.userName + '"用户吗?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function () {
          return changeUserStatus(row.userId, row.status);
        }).then(() => {
          this.msgSuccess(text + "成功");
        }).catch(function () {
          row.status = row.status === "0" ? "1" : "0";
        });
      },
      callFunction(functionName,item,data){
        let methods = this.$options.methods
        const _this = this;
        //console.log(methods,functionName)
        methods[functionName](_this,item,data)
      },
      /** 新增按钮操作 */
      handleAdd(_this,item,data) {
        _this.setFormItem(_this,item,data)

       /* this.userForm.open = true;
        this.userForm.title = "新增用户";
        this.getFormDefaultValue("userForm",this.userForm.addFormId);
        this.getFormField("userForm",this.userForm.addFormId);*/


      },
      /** 修改按钮操作 */
      handleUpdate(_this,item,data) {

        if(!data){
          data =  _this.checkList[0];
        }
        console.log(data)
        //_this.form.fields = Object.assign({},row);
        _this.setFormItem(_this,item,data)
        //this.getFormField("userForm",this.userForm.editFormId);

      }, /** 修改用戶密级 */
      handleAuthSecurity(_this,item,data) {
        _this.setFormItem(_this,item,data)
       /* const form = this.securityForm;
        form.open = true;
        form.title = "修改用户密级";
        form.fields = Object.assign({},row);
        this.getFormField("securityForm",form.formId);
        this.securityForm.open = true;*/
      },
      /**
       * 提交密级修改
       */
      submitSecurityForm(_this) {
        setUserSecurity(_this.form.fields.userId, _this.form.fields.securityId).then(res => {
          if (200 == res.code) {
            _this.form.open = false;
            _this.msgSuccess(res.msg);
            _this.getTableData()
          }
        })

      },
      /** 重置密码按钮操作 */
      handleResetPwd(_this,item,row) {

        resetUserPwd(row.userId, _this.defaultPwd).then(res => {
          _this.msgSuccess("修改成功，新密码是：" + _this.defaultPwd);
        });
      },
      /** 分配角色操作 */
      handleAuthRole: function (row) {
        const userId = row.userId;
        this.$router.push("/auth/role/" + userId);
      },
      /** 提交按钮 */
      submitForm: function (_this) {
        if (_this.form.fields.userId != undefined) {
          updateUser(_this.form.fields).then(response => {
            _this.msgSuccess("修改成功");
            _this.form.open = false;
            _this.getTableData()
          });
        } else {
          addUser(_this.form.fields).then(response => {
            _this.msgSuccess("新增成功");
            _this.form.open = false;
            _this.getTableData()
          });
        }
      },
      /** 删除按钮操作 */
      handleDelete(_this,item,row) {
        const userIds = row.userId ;

     /*   const aa = {
          msg:"确定删除",
          title:"提示",
          type:"warning"
        }
*/
        delUser(userIds).then(()=>{
          _this.getTableData()
          _this.msgSuccess("删除成功");
        }).catch(() => {
        })

        /*_this.baseConfirm(aa).then(function () {
          return delUser(userIds);
        }).then(() => {
          _this.getTableData()
          _this.msgSuccess("删除成功");
        }).catch(() => {
        })*/

       /* _this.$confirm('是否确认删除用户名为"' + row.userName + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function () {
          return delUser(userIds);
        }).then(() => {
          _this.getTableData()
          _this.msgSuccess("删除成功");
        }).catch(() => {
        });*/
      },
      /** 导出按钮操作 */
      handleExport() {
        const queryParams = this.queryParams;
        this.$confirm('是否确认导出所有用户数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.exportLoading = true;
          return exportUser(queryParams);
        }).then(response => {
          this.download(response.msg);
          this.exportLoading = false;
        }).catch(() => {
        });
      },
      /** 导入按钮操作 */
      handleImport() {
        this.upload.title = "用户导入";
        this.upload.open = true;
      },
      /** 下载模板操作 */
      importTemplate() {
        importTemplate().then(response => {
          this.download(response.msg);
        });
      },
      // 文件上传中处理
      handleFileUploadProgress(event, file, fileList) {
        this.upload.isUploading = true;
      },
      // 文件上传成功处理
      handleFileSuccess(response, file, fileList) {
        this.upload.open = false;
        this.upload.isUploading = false;
        this.$refs.upload.clearFiles();
        this.$alert(response.msg, "导入结果", {dangerouslyUseHTMLString: true});
        this.getList();
      },
      // 提交上传文件
      submitFileForm() {
        this.$refs.upload.submit();
      },
      //关闭密级修改框
      securityFormClose(){
        this.securityForm.open = false
      },
      //关闭用户修改框
      userFormClose(){
        this.userForm.open = false
      },
      submitEvent(fnName){
        this.callFunction(fnName)
      }
    },
    computed:{
      //计算修改密级弹出框的宽度
      securityFormWidth(){
        let width = 420*this.securityForm.lines - 20*(this.securityForm.lines - 1);
        return width+"px";
      },
      //计算用户弹出框的宽度
      userFormWidth(){
        let width = 420*this.userForm.lines - 20*(this.userForm.lines - 1);
        return width+"px";
      }

    }
  };
</script>
