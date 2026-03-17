<template>
  <MyContainer>

    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction"
                selection index operation >

      <template v-slot:setDictData="slot">
        <router-link :to="'/dict/type/data/' + slot.data.dictType" class="link-type">
          <span v-html="slot.data.dictTypeShow"></span>
        </router-link>
      </template>
      <template v-slot:tag="slot">
        <el-tag v-if="slot.data.status === '0'">正常</el-tag>
        <el-tag type="info" v-if="slot.data.status === '1'">停用</el-tag>
      </template>

    </com-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>

  </MyContainer>
</template>

<script>
import { listType, getType, delType, addType, updateType } from "@/api/system/dict/type";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";

export default {
  name: "Type",
  components: {ComTable, OpenBase, MyContainer},
  mixins: [mixin],
  data() {
    return {
      //列地址
      columnUrl: "/system/dict/index",
      //数据地址
      dataUrl: "/system/dict/type/list",

      //请求基础参数
      baseParams: {path: "/system/dict/index"},
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true

    };
  },
  methods: {
    /**
     *
     * 根据名称调用函数
     *
     * */
    callFunction(functionName, item, data) {
      let methods = this.$options.methods
      const _this = this;
      //console.log(methods,functionName)
      methods[functionName](_this, item, data)
    },
    /** 新增按钮操作 */
    handleAdd(_this, item, data) {
      _this.setFormItem(_this, item, data)
    },

    /** 修改按钮操作 */
    handleUpdate(_this, item, row) {
      if (!row) {
        row = _this.checkList[0];
      }
      const dictId = row.dictId
      getType(dictId).then(response => {
        const data = response.data
        _this.setFormItem(_this, item, data)
      });
    },
    /** 删除按钮操作 */
    handleDelete(_this, item, row) {

      let dictIds = [];
      if (row) {
        dictIds.push(row.dictId)
      } else {
        dictIds = _this.checkList.map(item => item.dictId)

      }
      delType(dictIds.join(",")).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {
      if (_this.form.fields.dictId !== undefined) {
        updateType(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.getTableData()
        });
      } else {
        addType(_this.form.fields).then(res => {
          _this.msgSuccess("新增成功");
          _this.form.open = false;
          _this.getTableData()
        });
      }
    },
    /**
     * 表单提交按钮事件
     * @param fnName 提交事件名称
     */
    submitEvent(fnName){
      this.callFunction(fnName)
    }

  }
}
</script>
