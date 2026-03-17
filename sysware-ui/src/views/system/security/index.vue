<template>
  <MyContainer>

    <com-table  ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
                :checkList.sync="checkList" :baseParams="baseParams"
                @click="callFunction"
                selection index >

      <template v-slot:switch="slot">
        <el-switch  active-value="1" inactive-value="0" v-model="slot.data[slot.prop]" @change="handleValueChange($event,slot.prop,slot.data)" size="small"></el-switch>
      </template>
    </com-table>

    <OpenBase ref="baseForm" :baseForm="form" @submit="submitEvent"></OpenBase>

  </MyContainer>
</template>

<script>
import { getSecurity, delSecurity, addSecurity, updateSecurity } from "@/api/system/security";
import MyContainer from '@/components/my-container'
import OpenBase from "@/views/openBase";
import {mixin} from "@/views/base";
import ComTable from "@/components/control/table";

export default {
  name: "Security",
  dicts: ['sys_security_type'],
  components: {ComTable, OpenBase, MyContainer},
  mixins: [mixin],
  data() {
    return {
      //列地址
      columnUrl: "/system/security/index",
      //数据地址
      dataUrl: "/system/security/list",

      //请求基础参数
      baseParams: {path: "/system/security/index"},
      //操作列的宽度
      operationWidth: 200,
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
      const id = row.securityId
      getSecurity(id).then(response => {
        const data = response.data
        console.log(data)
        _this.setFormItem(_this, item, data)
      });
    },
    /** 删除按钮操作 */
    handleDelete(_this, item, row) {
      console.log(row);
      let ids = [];
      if (row) {
        ids.push(row.id)
      } else {
        ids = _this.checkList.map(item => item.id)

      }
      delSecurity(ids.join(",")).then(() => {
        _this.getTableData()
        _this.msgSuccess("删除成功");
      }).catch(() => {
      })

    },
    /** 提交按钮 */
    submitForm(_this) {
      if (_this.form.fields.id != undefined) {
        updateSecurity(_this.form.fields).then(res => {
          _this.msgSuccess("修改成功");
          _this.form.open = false;
          _this.getTableData()
        });
      } else {

        addSecurity(_this.form.fields).then(res => {
          _this.msgSuccess("新增成功");
          _this.form.open = false;
          _this.getTableData()
        });
      }
    },
    /**
     * 数据改变时触发
     * @param value
     * @param prop
     * @param data
     */
    handleValueChange(value,prop,data){
      this.form_fields = data;
      this.form_fields[prop] = value
      updateSecurity(this.form_fields).then(res => {
        this.msgSuccess("修改成功");
      })
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
<!--
<template>
  <MyContainer>
    <Table ref="table" :columnUrl="columnUrl" :dataUrl="dataUrl"
           :checkList.sync="checkList" :baseParams="baseParams" index operation :operation-width="operationWidth">
      <template v-slot:headerOperation="slot">
        <el-col :span="1.5">
          <el-button
            type="primary"
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['system:user:add']"
          >新增
          </el-button>
        </el-col>
      </template>
      <template v-slot:statusChange="slot">
        <el-switch
          v-model="slot.data.flag"
          :active-value="0"
          :inactive-value="1"
          @change="handleStatusChange(slot.data)"
        ></el-switch>
      </template>
      <template v-slot:operation="slot">
        <el-button
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

      </template>
    </Table>

    &lt;!&ndash;修改用户密级对话框&ndash;&gt;
    <el-dialog :title="form.title"  v-dialogDrag
               :visible.sync="form.open"
               :close-on-click-modal="false"
               :width="formWidth"
               :before-close="cancel">
      <Form ref="form" :fields="form.fields" :items="form.items" :lines="form.lines" ></Form>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button type="danger" @click="cancel" >取 消</el-button>
      </span>
    </el-dialog>

  </MyContainer>
</template>

<script>
  import {
    delSecurity,
    addSecurity,
    updateSecurity,
    exportSecurity
  } from "@/api/system/security";
  import MyContainer from '@/components/my-container';
  import {mixin} from "@/views/base";

  export default {
    name: "Security",
    components: {MyContainer},
    mixins:[mixin],
    data() {
      return {
        //列地址
        columnUrl:"/system/security/index",
        //数据地址
        dataUrl:"/system/security/list",
        //选择列表
        checkList:[],
        //请求基础参数
        baseParams:{path:"/system/security/index"},
        //操作列的宽度
        operationWidth:150,
        // 按钮loading
        buttonLoading: false,
        // 遮罩层
        loading: true,
        // 导出遮罩层
        exportLoading: false,
        // 查询参数
        queryParams: {},
        // 表单参数
        form: {
          // 是否显示弹出层（修改密级）
          open: false,
          // 弹出层标题（修改密级）
          title: "密级调整",
          items:[],
          fields:{},
          lines:1,
          addFormId:"/system/security/add"
        }
      };
    },
    created() {

    },
    methods: {
      // 取消按钮
      cancel() {
        this.form.open = false;
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.form.open = true;
        this.form.title = "新增密级";

        this.getFormDefaultValue("form",this.form.addFormId);
        this.getFormField("form",this.form.addFormId);


      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.form.open = true;
        this.form.title = "新增密级";
        this.form.fields = Object.assign({},row);
        this.getFormField("form",this.form.addFormId);
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updateSecurity(this.fields).then(response => {
                this.msgSuccess("修改成功");
                this.form.open = false;
                this.getTableData();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              //设置排序好为最后一个
              this.form.fields.sort = this.$refs.table.total + 1
              addSecurity(this.form.fields).then(response => {
                this.msgSuccess("新增成功");
                this.form.open = false;
                this.getTableData();
              }).finally(() => {
                this.buttonLoading = false;
              });
            }
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$confirm('是否确认删除?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.loading = true;
          return delSecurity(ids);
        }).then(() => {
          this.loading = false;
          this.getTableData();
          this.msgSuccess("删除成功");
        }).catch(() => {
        });
      },
      /** 导出按钮操作 */
      handleExport() {
        const queryParams = this.queryParams;
        this.$confirm('是否确认导出所有密级数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.exportLoading = true;
          return exportSecurity(queryParams);
        }).then(response => {
          this.download(response.msg);
          this.exportLoading = false;
        }).catch(() => {
        });
      },
      handleStatusChange(row) {
        let text = row.flag === "0" ? "启用" : "停用";
        this.$confirm('确认要"' + text + '""' + row.name + '"密级吗?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function () {
          return "0";
        }).then(() => {
          this.msgSuccess(text + "成功");
        }).catch(function () {
          row.flag = row.flag === "0" ? "1" : "0";
        });
      },
    },
    computed:{
      //计算修改密级弹出框的宽度
      formWidth(){
        let width = 420*this.form.lines - 20*(this.form.lines - 1);
        return width+"px";
      }

    }

  };
</script>
-->
