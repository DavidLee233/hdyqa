<template>

  <el-input placeholder="请选择文件" v-model="val" clearable @clear="handleClear"  @input="changeValue" :disabled="disabled" :class="defaultClass">
    <template slot="append">
      <el-upload
        :action="uploadFileUrl"
        :before-upload="handleBeforeUpload"
        :limit="limit"
        :on-error="handleUploadError"
        :on-exceed="handleExceed"
        :on-success="handleUploadSuccess"
        :show-file-list="false"
        :headers="headers"
        :on-progress="handleOn"
        :accept="fileType"
        ref="upload"
        v-loading.fullscreen.lock="fullscreenLoading"
      >
        <el-button icon="el-icon-search" ></el-button>
      </el-upload>
    </template>
  </el-input>

</template>

<script>
import {baseProps,mixin} from "@/components/control/contorlBase";
import { getToken } from "@/utils/auth";

export default {
  name: "comUpload",
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
      limit:1,
      fullscreenLoading: false
    };
  },
  watch: { },
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
        this.val = file.name
        this.fileId = res.data.fileId
        this.$emit("update:value",this.val);
        this.$emit("update:updateValue",this.fileId);
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
      this.$emit("update:updateValue","");
    }
  }
};
</script>

<style scoped >

</style>
