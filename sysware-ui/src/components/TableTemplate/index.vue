<template>
  <div>
    <template v-for="item in tableColumns" >
      <template v-if="item.type=='index' || item.type=='selection' ">
        <el-table-column :type="item.type" :label="item.label"
                         :width="item.width" :fixed="true" :align="item.align"/>
      </template>
      <template v-else>
        <template v-if="YorNFormat(item.searchable)">
          <template v-if="YorNFormat(item.dateselect)">
            <el-table-column v-if="YorNFormat(item.visible)" :label="item.label"
                             :sortable="YorNFormat(item.sortable)?'custom':false"
                             :key="item.key" :prop="item.prop" :width="item.width"
                             :align="item.align" :show-overflow-tooltip="YorNFormat(item.tooltip)">
              <template slot="header" slot-scope="scope">
                <div style="width: 80%;float: left;text-align: right;line-height: 34px;">
                  <span>{{item.label}}</span>
                </div>
                <div style="width: 100%;float: left">
                  <el-date-picker
                    v-if="showSearch"
                    v-model="date[item.prop]"
                    size="small"
                    style="max-width: 220px;"
                    value-format="yyyy-MM-dd"
                    type="daterange"
                    range-separator="-"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    @click.stop.native=""
                    @blur="handleDateQuery"
                    @change="handleDateQuery"
                  ></el-date-picker>
                </div>
              </template>
              <template slot-scope="scope">
                <template v-if="item.linkname">
                  <el-button type="text"  @click="callMethod(item.linkname,scope.row)">
                    <span v-html="scope.row[item.prop]"></span>
                  </el-button>
                </template>
                <template v-else>
                  <span v-html="scope.row[item.prop]"></span>
                </template>

              </template>
            </el-table-column>
          </template>
          <template v-else>
            <el-table-column v-if="YorNFormat(item.visible)" :label="item.label"
                             :sortable="YorNFormat(item.sortable)?'custom':false" :key="item.key" :prop="item.prop"
                             :width="item.width" :align="item.align" :show-overflow-tooltip="YorNFormat(item.tooltip)">
              <template slot="header" slot-scope="scope">
                <div style="width: 80%;float: left;text-align: right;line-height: 34px;">
                  <span>{{item.label}}</span>
                </div>
                <div style="width: 100%;float: left">
                  <el-input v-show="showSearch"
                            v-model="allQueryParams[item.prop]"
                            clearable
                            size="small"
                            @click.stop.native=""
                            @keyup.enter.native="handleQuery"/>

                </div>
              </template>
              <template slot-scope="scope">
                <template v-if="item.linkname">
                  <el-button type="text"  @click="callMethod(item.linkname,scope.row)">
                    <span v-html="scope.row[item.prop]"></span>
                  </el-button>
                </template>
                <template v-else>
                  <span  v-html="scope.row[item.prop]"></span>
                </template>

              </template>
            </el-table-column>
          </template>
        </template>
        <template v-else>
          <el-table-column v-if="YorNFormat(item.visible)" :label="item.label"
                           :sortable="YorNFormat(item.sortable)?'custom':false" :key="item.prop" :prop="item.prop"
                           :width="item.width" :align="item.align" :show-overflow-tooltip="YorNFormat(item.tooltip)"/>
        </template>

      </template>

    </template>
  </div>
</template>

<script>
  export default {
    name: "tableTemplate",
    props: ['tableColumns','showSearch','allQueryParams','queryParams','dateRange'],
    data() {
      return {
        // 日期范围
        date: this.dateRange,

      }
    },
    watch:{
      dateRange:'dataChange'
    },
    methods: {
      //Yes和No的转换
      YorNFormat(val) {
        return this.yOrNtoBoolean(val);
      },
      handleDateQuery() {
        this.$emit('handleDateQuery',this.date);
      },
      handleQuery() {
        this.$emit('handleQuery');
      },
      callMethod(name,row) {
          this.$emit('callMethods',{name,row})
      },
      //父组件搜索日期发生变化时改变子组件对应的日期
      dataChange(){
        this.date= this.dateRange;
      },
      getIndex(item){
        console.log(item)
      }

    }
  }
</script>

<style scoped>

</style>
