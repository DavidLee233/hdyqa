<template>
  <MyContainer>
    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction" >

      <template v-slot:signFile="slot">
        <el-tag v-if="slot.data.signFileName">已上传</el-tag>
      </template>


    </com-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>


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
    importTemplate,
    getUser
  } from "@/api/system/user";
  import {getToken} from "@/utils/auth";
  import MyContainer from '@/components/my-container'
  import {setUserSecurity} from "@/api/system/user";
  import OpenBase from "@/views/openBase";
  import {mixin} from "@/views/base";
  import ComTable from "@/components/control/table";

  export default {
    name: "User",
    components: {ComTable, OpenBase, MyContainer},
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
        //operationWidth:300,
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
        // 上传签名导入参数
        signUpload: {
          // 是否显示弹出层（上传签名）
          open: false,
          // 弹出层标题（上传签名）
          title: "",
          // 是否禁用上传
          isUploading: false,
          // 设置上传的请求头部
          headers: {Authorization: "Bearer " + getToken()},
          // 上传的地址
          url: process.env.VUE_APP_BASE_API + "/system/user/signUpload"
        }
      };
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
      },
      /** 修改按钮操作 */
      handleUpdate(_this,item,data) {
        if(!data){
          data = _this.checkList[0];
        }
        getUser(data.userId).then(res =>{
          console.log(res.data)
          _this.setFormItem(_this,item,res.data)
        })
      }, /** 修改用戶密级 */
      handleAuthSecurity(_this,item,data) {
        if(!data){
          data = _this.checkList[0];
        }
        _this.setFormItem(_this,item,data)
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
        if(!row){
          row = _this.checkList[0];
        }
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
          addUser(_this.form.fields).then(res => {
            _this.msgSuccess("新增成功");
            _this.form.open = false;
            _this.getTableData()
          });
        }
      },
      /** 删除按钮操作 */
      handleDelete(_this,item,row) {
        let userIds = [];
        if(row){
          userIds.push(row.userId)
        }else{
          userIds = _this.checkList.map(item => item.userId)
        }
        delUser(userIds.join(",")).then(()=>{
          _this.getTableData()
          _this.msgSuccess("删除成功");
        }).catch(() => {
        })

      },
      /** 导出按钮操作 */
      handleExport(_this) {
        const queryParams = _this.queryParams;
        _this.exportLoading = true;
        _this.download('system/user/export', {
          ..._this.queryParams
        }, `用户列表.xlsx`)

        /*return exportUser(queryParams).then(res =>{
            this.download(res);
            _this.exportLoading = false;
          }
        );*/

        /*this.$confirm('是否确认导出所有用户数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {

        }).then(response => {
          this.download(response.msg);
          this.exportLoading = false;
        }).catch(() => {
        });*/
      },
      /** 导入按钮操作 */
      handleImport(_this) {
        _this.upload.title = "用户导入";
        _this.upload.open = true;
      },
      handleSignFile(_this,item,row){
        if(!row){
          row = _this.checkList[0];
        }
        _this.setFormItem(_this,item,row)
      },
      /** 下载模板操作 */
      importTemplate() {
        this.download('system/user/importTemplate', {
        }, `导入用户模板.xlsx`)
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
        this.getTableData()
      },
      // 文件上传中处理
      handleSignFileUploadProgress(event, file, fileList) {
        this.signUpload.isUploading = true;
      },
      // 文件上传成功处理
      handleSignFileSuccess(response, file, fileList) {
        this.signUpload.open = false;
        this.signUpload.isUploading = false;
        this.$refs.signUpload.clearFiles();
        this.msgSuccess("上传成功");
        this.getTableData()
      },
      // 提交上传文件
      submitFileForm() {
        this.$refs.upload.submit();
      },
      submitEvent(fnName){
        this.callFunction(fnName)
      },
      submitSignFileForm(){
        console.log(111)
      }
    }
  };
</script>
