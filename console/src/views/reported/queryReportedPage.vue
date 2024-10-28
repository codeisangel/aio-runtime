<template>
  <div class="app-container">
    <el-row :gutter="20" v-show="spreadQueryParams">
      <el-form :model="queryTable" class="demo-form-inline" size="mini" label-width="80px">
        <el-col :span="24">
          <el-col :span="8">
            <el-form-item label="接口地址">
              <el-input v-model="queryTable.apiUrl" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="5">
            <el-form-item label="终端">
              <el-input v-model="queryTable.terminal" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="5">
            <el-form-item label="追踪码">
              <el-input v-model="queryTable.traceId" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="备注">
              <el-input v-model="queryTable.remark" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>

        </el-col>

        <el-col :span="24">
          <el-col :span="8">
            <el-form-item label="时间">
              <el-date-picker v-model="createTimeRange" value-format="timestamp" type="datetimerange" @change="queryTablePage"
                              range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                              :picker-options="createTimeRangeOptions"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="平台">
              <el-input v-model="queryTable.platform" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)">
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :span="5">
            <el-form-item label="错误类型">
              <el-input v-model="queryTable.type" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="4" :offset="1">
            <el-form-item>
              <el-button @click="clearQueryParamsBtn">清除</el-button>
              <el-button type="primary" @click="queryTableBtn">查询</el-button>
            </el-form-item>
          </el-col>

        </el-col>
        <el-col :span="24">
          <el-col :span="6">
            <el-form-item label="内容">
              <el-input v-model="queryTable.keyword" @input="inputEvent($event)" clearable>
                <el-button slot="append" icon="el-icon-plus" @click="addToParam4Keyword()"></el-button>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-tag  v-for="(keyword,index) in queryTable.keywords" :key="keyword" closable style="margin-right: 10px;" @close="closeKeywordEvent(keyword)">{{keyword}}</el-tag>
          </el-col>

        </el-col>
      </el-form>

    </el-row>


    <el-table :data="articleTable"  border :height="tableHeight" size="mini"  :row-class-name="tableRowStyleClass" style="margin-top: 10px">
      <el-table-column label="内容" mni-width="400">
        <template slot-scope="scope">
          <span class="message-column-span-style">{{scope.row.message}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="traceId" label="追踪码" width="200" align="center" />
      <el-table-column prop="type" label="错误类型" width="200" align="center" />
      <el-table-column prop="level" label="错误级别" width="100" align="center" />
      <el-table-column prop="terminal" label="所属终端" width="200" align="center" />
      <el-table-column prop="platform" label="所属平台" mni-width="120" align="center" />
      <el-table-column prop="createTime" label="时间" width="140" align="center" sortable sort-by="createTimestamp" />
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="scope">
           <el-button type="text" @click="openReportedErrorDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="log-bottom-box-style">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
                     :page-sizes="[50,100, 200, 300, 400,500]" :page-size="currentPageSize"
                     layout="total, sizes, prev, pager, next, jumper" :total="tableTotal">
      </el-pagination>
    </div>

    <error-reported-details-dialog ref="errorReportedDetails" />

  </div>
</template>

<script>

import {getErrorReportedPageApi} from "@/api/errorReportedApi";
import ErrorReportedDetailsDialog from "@/views/reported/dialog/ErrorReportedDetailsDialog.vue";

export default {
  components: {ErrorReportedDetailsDialog},
  data() {
    return {
      saveQueryParamsSchemeDialogVisible:false,
      queryTable: {
        keywords:[],
        createToTime: '',
        createFromTime: '',
        querySchemeId:'',
        remark:'',
        querySchemeName:'',
      },
      querySchemeForm:{

      },
      querySchemeList:[],
      spreadQueryParams: true,
      tableHeight:5000,
      pageParams:{},
      createTimeRange: [],
      createTimeRangeOptions:{
        shortcuts: [
          {
            text: '5分钟',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - (1000 * 60 * 5));
              end.setTime(end.getTime() + (1000 * 60 * 60))
              picker.$emit('pick', [start, end]);
            }
          },
          {
            text: '10分钟',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - (1000 * 60 * 10));
              end.setTime(end.getTime() + (1000 * 60 * 60))
              picker.$emit('pick', [start, end]);
            }
          },{
            text: '30分钟',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - (1000 * 60 * 30));
              end.setTime(end.getTime() + (1000 * 60 * 60))
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近1小时',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - (1000 * 60 * 60));
              end.setTime(end.getTime() + (1000 * 60 * 60))
              picker.$emit('pick', [start, end]);
            }
          },   {
            text: '最近一天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - (1000 * 60 * 60 * 24));
              end.setTime(end.getTime() + (1000 * 60 * 60 * 4))
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              end.setTime(end.getTime() + (1000 * 60 * 60 * 4))
              picker.$emit('pick', [start, end]);
            }
          },
          {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              end.setTime(end.getTime() + (1000 * 60 * 60 * 4))
              picker.$emit('pick', [start, end]);
            }
          },]
      },
      articleTable: [],
      currentPage: 1,
      currentPageSize: 100,
      tableTotal: 0
    }
  },
  created() {
    this.initCreateTimeRangeDefault()
  },
  mounted() {
    this.queryTablePage()
  },
  methods: {
    initCreateTimeRangeDefault(){
      const date = new Date();
      this.createTimeRange[0] = date.getTime() - (1000 * 60 * 60 * 24)
      this.createTimeRange[1] = date.getTime() + (1000 * 60 * 60)
    },
    openReportedErrorDetail(val) {
      this.$refs.errorReportedDetails.openDialog(val)
    },
    handleSizeChange(val) {
      this.currentPageSize = val;
      this.currentPage = 1
      this.queryTablePage();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.queryTablePage();
    },
    clearQueryParamsBtn(){
      this.currentPage = 1
      this.queryTable.message=''
      this.queryTable.traceId=''
      this.queryTable.remark=''
      this.queryTable.apiUrl=''
      this.queryTable.terminal=''
      this.queryTable.platform=''
      this.queryTable.type=''
      this.queryTable.keywords = []
      this.queryTablePage()
    },

    queryTableBtn(){
      this.currentPage = 1
      this.queryTablePage()
    },
    addToParam4Keyword(){
      if (this.queryTable.keyword){
        this.queryTable.keywords.push(this.queryTable.keyword.trim())
        this.queryTable.keyword = ''
      }
    },
    closeKeywordEvent(keyword){
      this.queryTable.keywords.forEach((item, index, arr) => {
        if (item === keyword) {
          arr.splice(index,1)
        }
      });
    },
    queryTablePage() {
      this.queryTable.createFromTime = this.createTimeRange[0];
      this.queryTable.createToTime = this.createTimeRange[1];
      this.pageParams.pageNum = this.currentPage;
      this.pageParams.pageSize = this.currentPageSize;
      getErrorReportedPageApi(this.queryTable,this.pageParams).then(rsp => {
        this.articleTable = rsp.data.list;
        this.tableTotal = rsp.data.total;
      }).catch(err => {
        this.$message.error(err)
      })
    },

    tableRowStyleClassName(row){
      if (row.level === 'INFO'){
        return 'log-info-sty';
      }else if (row.level === 'WARN'){
        return 'log-warn-sty';
      }else if (row.level === 'ERROR'){
        return 'log-error-sty';
      }else if (row.level === 'DEBUG'){
        return 'log-debug-sty';
      }else {
        return ''
      }
    },
    tableRowStyleClass({row, rowIndex}){
      return this.tableRowStyleClassName(row);
    },

    inputEvent(){
      this.$forceUpdate()
    }

  }
}
</script>
<style>
.message-column-span-style {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 200px;
}

.log-bottom-box-style{
  background-color: red;
  position: fixed;
  bottom: 0;
  height: 50px;
  padding-top: 10px;
  background-color: white;
  border-top:2px solid #cccccc  ;
  width: 100%;
}
.log-debug-sty {
  color: #5FB878;
}
.log-info-sty {
  color: #009688;
}
.log-warn-sty {
  color: #E6A23C;
}
.log-error-sty {
  color: #FF5722;
  font-weight:bolder;
}
</style>
