<template>
  <MyContainer>

<!--    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction" :headerBtnArr="headerBtnArr"
                :operationBtnArr="operationBtnArr"
                selection index operation :operation-width="operationWidth(operationBtnArr)">

    </com-table>-->

    <com-tree-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                     :checkList.sync="checkList" :baseParams="baseParams"
                     @click="callFunction"
                     :open-id="openId" :rowKey="rowKey"  @disableFn="disableFn"
                     selection :lazy="false"   >
      <template v-slot:labelFn="slot">
        <span v-html="slot.data[slot.prop]"></span>
      </template>

    </com-tree-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent">

    </OpenBase>

  </MyContainer>
</template>

<script>
import { listData, getData, delData, addData, updateData } from "@/api/system/dict/data";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";
import ComTreeTable from "@/components/control/treeTable";

export default {
  name: "Data",
  components: {ComTable, OpenBase, MyContainer,ComTreeTable},
  mixins: [mixin],
  data() {
    return {
      //列地址
      columnUrl: "/system/dict/data",
      //数据地址
      dataUrl: "/system/dict/data/list",
      //请求基础参数
      baseParams: {path: "/system/dict/data"},
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      dictType:"",
      openId:[],
      rowKey:"dictCode"

    };
  },
  created() {
    const dictType = this.$route.params && this.$route.params.dictType;

    this.baseParams.dictType = dictType;
    this.dictType = dictType;
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
      const dictCode = row.dictCode
      getData(dictCode).then(response => {
        const data = response.data
        _this.setFormItem(_this, item, data)
      });
    },
    /** 删除按钮操作 */
    handleDelete(_this, item, row) {

      let dictCodes = [];
      if (row) {
        dictCodes.push(row.dictCode)
      } else {
        dictCodes = _this.checkList.map(item => item.dictCode)

      }
      delData(dictCodes.join(",")).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {
      let pId = "0";
      if(_this.checkList.length>0){
        pId = _this.checkList[0].dictCode
      }

      if (_this.form.fields.dictCode !== undefined) {
        updateData(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.openId.push(pId)
          _this.getTableData()
        });
      } else {
        _this.form.fields.dictType = _this.dictType;

        _this.form.fields.parentId = pId;
        addData(_this.form.fields).then(res => {
          _this.msgSuccess("新增成功");
          _this.form.open = false;
          _this.openId.push(pId)
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
    },

    disableFn(){

    }

  }
}
</script>
