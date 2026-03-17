<template>

  <el-input placeholder="请选择文件" v-model="fileName" clearable @clear="handleClear"  @input="changeValue" :disabled="disabled" :class="defaultClass">
    <template slot="append">
      <el-upload
        :action="uploadFileUrl"
        :before-upload="handleBeforeUpload"
        :on-error="handleUploadError"
        :on-success="handleUploadSuccess"
        :show-file-list="false"
        :headers="headers"
        :on-progress="handleOn"
        :accept="fileType"
        ref="upload"
        v-loading.fullscreen.lock="fullscreenLoading"
      >
        <el-tooltip slot="trigger" class="item" effect="dark" content="本地上传" placement="left">
          <el-button   icon="el-icon-paperclip"></el-button>
        </el-tooltip>
        <el-tooltip slot="trigger" class="item" effect="dark" content="线上模板" placement="right">
          <el-button style="margin-left: 1px;" icon="el-icon-search" @click="handleSelectTemp"></el-button>
        </el-tooltip>

      </el-upload>
    </template>
  </el-input>

</template>

<script>
import {baseProps,mixin} from "@/components/control/contorlBase";
import { getToken } from "@/utils/auth";
import {listByIds} from "@/api/system/oss";

export default {
  name: "comDocUpload",
  props: {...baseProps
  },
  mixins:[mixin],
  data() {
    return {
      uploadFileUrl: process.env.VUE_APP_BASE_API + "/system/oss/upload", // 上传文件服务器地址
      headers: {
        Authorization: "Bearer " + getToken(),
      },
      fileId:"",
      fileName:"",
      limit:5,
      fullscreenLoading: false,
      fileList:new Map()
    };
  },
  watch: {
    val:{
      handler(newValue){
        this.fileName = "";
        if(newValue){
          this.getFileByIds(newValue)
        }
      },
      immediate:true
    }
  },
  computed: {
    //上传限制格式
    fileType(){
      return this.config?.fileType || ""
    },
    //上传限制大小
    fileSize(){
      return this.config?.fileSize || 2048
    }
  },
  methods: {
    // 上传前校检格式和大小
    handleBeforeUpload(file) {

      const namePrefix = ["[非密]","[内部]"]

      const prefix =  namePrefix.filter(prefix => file.name.startsWith(prefix)).length > 0;

      //验证密级匹配情况

      /*if(!prefix){
        this.msgError(`文件名格式不正确, 文件名称请以${namePrefix.join("|")}格式开头!`);
        return false;
      }*/

      // 校检文件类型
      if (this.fileType) {
        let fileExtension = "";
        if (file.name.lastIndexOf(".") > -1) {
          fileExtension = file.name.slice(file.name.lastIndexOf("."));
        }
        const isTypeOk = this.fileType.split(",").some((type) => {
          if (file.type.indexOf(type) > -1) return true;
          if (fileExtension && fileExtension.indexOf(type) > -1) return true;
          return false;
        });
        if (!isTypeOk) {
          this.$message.error(`文件格式不正确, 请上传${this.fileType.split(",").join("|")}格式文件!`);
          return false;
        }
      }
      /*// 校检文件大小
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize;
        if (!isLt) {
          this.$message.error(`上传文件大小不能超过 ${this.fileSize} MB!`);
          return false;
        }
      }*/
      return true;
    },
    // 文件个数超出
    handleExceed() {
      this.$message.error(`上传文件数量不能超过 ${this.limit} 个!`);
    },
    // 上传失败
    handleUploadError(err) {
      this.$message.error("上传失败, 请重试");
    },
    // 上传成功回调
    handleUploadSuccess(res, file) {
      this.$loading({}).close();

      if(res.code === 200){
        this.fileName = file.name
        this.fileId = res.data.ossId
        this.$emit("update:value",this.fileId);

        this.fileList.set(this.fileId, file.name);
        //this.$emit("update:updateValue",this.fileId);
      }else{
        this.$message.error(res.msg);
      }
    },
    handleOn(){
      this.$loading({
        lock: true,
        text: '正在上传,请稍后...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
    },
    // 获取文件名称
    getFileName(name) {
      if (name.lastIndexOf("/") > -1) {
        return name.slice(name.lastIndexOf("/") + 1,name.lastIndexOf(".")).toLowerCase();
      } else {
        return "";
      }
    },
    handleClear(){
      this.$refs.upload.clearFiles();
      this.$emit("update:value","");
    },
    handleSelectTemp(){

    },
    getFileByIds(fileId){

      console.log(this.fileList)
      const name = this.fileList.get(fileId)
      console.log(name);

      if(name){
        this.fileName = name
      }else{
        console.log("重新加载")
        if(fileId && fileId.indexOf(".") === -1){
          listByIds(fileId.split(",")).then(res =>{
            this.fileName =   res.data[0].originalName
            this.fileList.set(fileId,  this.fileName);
          })
        }
      }


    }
  }
};
</script>

<style scoped >

</style>
