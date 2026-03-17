<template>
  <div>
    <el-table>
      <el-table v-loading="loading" height="'100%'" border stripe style="width: 100%;height:100%;" :data="dataList"
                >

        <template v-for="item in tableColumns" >
          <template v-if="item.type!='' && item.type!=undefined && item.type!=null" >
            <el-table-column :type="item.type" :label="item.label" :width="item.width" :align="item.align" />
          </template>
          <template v-if="item.type=='' || item.type==undefined || item.type==null" >
            <template v-if="YorNFormat(item.searchable)">
              <template v-if="YorNFormat(item.dateSelect)">
                <el-table-column v-if="YorNFormat(item.visible)" :label="item.label"
                                 :sortable="YorNFormat(item.sortable)?'custom':false"
                                 :key="item.key" :prop="item.prop" :width="item.width"
                                 :align="item.align"  :show-overflow-tooltip="YorNFormat(item.tooltip)">
                  <template slot="header" slot-scope="scope" >
                    <div style="width: 80%;float: left;text-align: right;line-height: 34px;">
                      <span>{{item.label}}</span>
                    </div>
                    <div style="width: 100%;float: left">
                      <el-date-picker
                        v-if="showSearch"
                        v-model="dateRange"
                        size="small"
                        style="max-width: 220px;"
                        value-format="yyyy-MM-dd"
                        type="daterange"
                        range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        @click.stop.native=""
                        @blur="handleQuery"
                        @change="handleQuery"
                      ></el-date-picker>
                    </div>
                  </template>
                  <template slot-scope="scope">
                    <span>{{ parseTime(scope.row[item.prop])}}</span>
                  </template>
                </el-table-column>
              </template>
              <template v-else>
                <el-table-column v-if="YorNFormat(item.visible)" :label="item.label" :sortable="YorNFormat(item.sortable)?'custom':false" :key="item.key" :prop="item.prop" :width="item.width" :align="item.align"  :show-overflow-tooltip="YorNFormat(item.tooltip)">
                  <template slot="header" slot-scope="scope">
                    <div  style="width: 80%;float: left;text-align: right;line-height: 34px;">
                      <span>{{item.label}}</span>
                    </div>
                    <div  style="width: 100%;float: left">
                      <el-input v-show="showSearch"
                                v-model="allQueryParams[item.prop]"
                                clearable
                                size="small"
                                @click.stop.native=""
                                @blur="handleQuery"
                                @keyup.enter.native="handleQuery"/>

                    </div>
                  </template>
                  <template slot-scope="scope">
                    <span v-html="handleHighLight(scope.row[item.prop],allQueryParams[item.prop],queryParams.searchContent)"></span>
                  </template>
                </el-table-column>
              </template>
            </template>
            <template v-else>
              <el-table-column v-if="YorNFormat(item.visible)" :label="item.label" :sortable="YorNFormat(item.sortable)?'custom':false" :key="item.prop" :prop="item.prop" :width="item.width" :align="item.align"  :show-overflow-tooltip="YorNFormat(item.tooltip)"/>
            </template>

          </template>

        </template>
        <el-table-column
          label="操作"
          width="260"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope" v-if="scope.row.userId !== 1">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:user:edit']"
            >修改
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:user:remove']"
            >删除
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-circle-check"
              @click="handleAuthSecurity(scope.row)"
              v-hasPermi="['system:user:setSecurity']"
            >设置密级
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-key"
              @click="handleResetPwd(scope.row)"
              v-hasPermi="['system:user:resetPwd']"
            >重置密码
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-circle-check"
              @click="handleAuthRole(scope.row)"
              v-hasPermi="['system:user:setRole']"
            >分配角色
            </el-button>
          </template>
        </el-table-column>
      </el-table>

    </el-table>

  </div>
</template>


<script>
  export default {
    name:'SyswareTable',
    props:{
      dataList: {
        required: true
      },
      tableColumns:{
        required: true
      }
    },
    data() {
      return {
        showSearch:false,
        allQueryParams:{},
        queryParams:{
          searchContent:''
        }

      }


    }

  }

</script>

<style>



</style>
