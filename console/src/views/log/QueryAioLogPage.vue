<template>
  <div class="app-container">
    <el-row :gutter="20" v-show="spreadQueryParams">
      <el-form :model="queryTable" class="demo-form-inline" size="mini" label-width="80px">
        <el-col :span="24">
          <el-col :span="8">
            <el-form-item label="类名">
              <el-input v-model="queryTable.className" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="5">
            <el-form-item label="方法名">
              <el-input v-model="queryTable.methodName" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="5">
            <el-form-item label="追踪码">
              <el-input v-model="queryTable.traceId" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="处理状态">
              <el-select v-model="queryTable.level" style="width: 280px;" clearable placeholder="请选择日志级别" @change="queryTableBtn">
                <el-option label="TRACE" value="TRACE"></el-option>
                <el-option label="DEBUG" value="DEBUG"></el-option>
                <el-option label="INFO" value="INFO"></el-option>
                <el-option label="WARN" value="WARN"></el-option>
                <el-option label="ERROR" value="ERROR"></el-option>
              </el-select>
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
            <el-form-item label="标记">
              <el-input v-model="queryTable.marker" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)">
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :span="5">
            <el-form-item label="线程名">
              <el-input v-model="queryTable.threadName" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="查询方案">
              <el-select v-model="queryTable.scheme" style="width: 280px;" clearable placeholder="请选择查询方案" @change="schemeChangeEvent">
                <el-option v-for="(item,index) in this.querySchemeList" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
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
          <el-col :span="14">
            <el-tag  v-for="(keyword,index) in queryTable.keywords" :key="keyword" closable style="margin-right: 10px;" @close="closeKeywordEvent(keyword)">{{keyword}}</el-tag>
          </el-col>

        </el-col>
        <el-col :span="24">
          <el-col :span="18">
            <div class="mdc-item-box-style">
              <el-form-item label="MDC">
                <div class="mdc-key-box-style">
                  <el-input v-model="mdcItem.key" clearable @input="inputEvent($event)" style="width: 200px"></el-input>
                </div>
                <span> - </span>
                <div class="mdc-value-box-style">
                  <el-input v-model="mdcItem.value" @input="inputEvent($event)" clearable>
                    <el-button slot="append" icon="el-icon-plus" @click="addToParam4Mdc()"></el-button>
                  </el-input>
                </div>
              </el-form-item>

            </div>
            <div class="mdc-list-box-style">
              <el-tag  v-for="(mdcV,mdcKey) in queryTable.mdc" :key="mdcKey"
                       closable style="margin-right: 10px;" type="success"  effect="plain"
                       @close="closeMdcIteEvent(mdcKey)">
                 <span style="color: #393D49;">{{mdcKey}}</span>
                <span style="margin-right: 5px;margin-left: 5px;"> - </span>
                <span style="color: #009688;"> {{mdcV}}</span>
              </el-tag>
            </div>

          </el-col>

          <el-col :span="6">
            <el-form-item>
              <el-button @click="clearQueryParamsBtn">清除</el-button>
              <el-button type="primary" @click="openSaveQueryParamsSchemeDialog">保存方案</el-button>
              <el-button type="primary" @click="queryTableBtn">查询</el-button>
            </el-form-item>
          </el-col>
        </el-col>
      </el-form>

    </el-row>


    <el-table :data="articleTable"  border :height="tableHeight" size="mini"  :row-class-name="tableRowStyleClass" style="margin-top: 10px">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form class="demo-table-expand" size="mini" label-width="120px">
            <el-form-item label="标记">
              <span style="color: #009688;">{{props.row.marker}}</span>
              <el-button v-if="props.row.marker" style="margin-left: 35px;" type="text" class="el-icon-search" @click="addToParams('marker',props.row.marker)" circle></el-button>
            </el-form-item>
            <el-form-item label="追踪码">
              <span>{{ props.row.traceId }}</span>
              <el-button v-if="props.row.traceId" style="margin-left: 35px;" type="text" class="el-icon-search" @click="addToParams('traceId',props.row.traceId)" circle></el-button>
            </el-form-item>
            <el-form-item label="线程名">
              <span style="color: #009688;">{{props.row.threadName}}</span>
              <el-button v-if="props.row.threadName" style="margin-left: 35px;" type="text" class="el-icon-search" @click="addToParams('threadName',props.row.threadName)" circle></el-button>
            </el-form-item>
            <el-form-item label="类名">
              <span style="color: #1E9FFF;">{{ props.row.className }}</span>
              <el-button v-if="props.row.className" style="margin-left: 35px;" type="text" class="el-icon-search" @click="addToParams('className',props.row.className)" circle></el-button>
            </el-form-item>
            <el-form-item label="方法名">
              <span>{{ props.row.methodName }}</span>
              <el-button v-if="props.row.methodName" style="margin-left: 35px;" type="text" class="el-icon-search" @click="addToParams('methodName',props.row.methodName)" circle></el-button>
            </el-form-item>

            <el-form-item label="发生时间">
              <span>{{ props.row.createTime }}</span>
            </el-form-item>
            <el-form-item label="日志级别">
              <span :class="tableRowStyleClassName(props.row)">{{ props.row.level }}</span>
              <el-button v-if="props.row.level" style="margin-left: 35px;" type="text" class="el-icon-search" @click="addToParams('level',props.row.level)" circle></el-button>
            </el-form-item>

            <el-form-item v-if="props.row.mdc">
              <el-descriptions class="margin-top" size="mini" title="MDC信息" :column="1" border style="width: 600px">
                <el-descriptions-item
                  label-class-name="mdc-row-label-style"
                  content-class-name="mdc-row-content-style"
                  v-for="(item,key) in props.row.mdc" :key="key"
                  :label="key">
                  <template slot="label">
                    <span>{{key}}</span>
                    <el-button style="float: right" type="text" class="el-icon-search"
                               @click="addToParam4MdcToMdcMap(key,item)" circle></el-button>
                  </template>
                    {{item}}
                </el-descriptions-item>
              </el-descriptions>
            </el-form-item>

            <el-form-item label="内容">
              <code :class="tableRowStyleClassName(props.row)">{{ props.row.message }}</code>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="140" align="center"></el-table-column>
      <el-table-column prop="traceId" label="追踪码" width="200" align="center"></el-table-column>
      <el-table-column label="内容" min-width="460">
        <template slot-scope="scope">
          <span>{{scope.row.message.slice(0,400)}}</span>
        </template>
      </el-table-column>

    </el-table>
    <div class="log-bottom-box-style">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
                     :page-sizes="[50,100, 200, 300, 400,500]" :page-size="currentPageSize"
                     layout="total, sizes, prev, pager, next, jumper" :total="tableTotal">
      </el-pagination>
    </div>

    <el-dialog title="保存查询方案" :visible.sync="saveQueryParamsSchemeDialogVisible" width="30%">
      <el-form :model="querySchemeForm" ref="querySchemeForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="查询名称">
          <el-input v-model.number="querySchemeForm.schemeName" autocomplete="off" @input="inputEvent($event)"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="saveQueryParamsSchemeDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveQueryParamsSchemeBtn">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>

import {getLogPageApi, getLogSchemeDetailsApi, getLogSchemeOptionsApi, saveLogSchemeApi} from "@/api/logApi";

export default {
  data() {
    return {
      saveQueryParamsSchemeDialogVisible:false,
      queryTable: {
        keywords:[],
        mdc:{},
        className:'',
        methodName:'',
        createToTime: '',
        createFromTime: '',
        querySchemeId:'',
        querySchemeName:'',
      },
      mdcItem:{
         key:'',
         value:''
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
    this.getQueryLogSchemeOptions()
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
      this.queryTable.querySchemeId=''
      this.queryTable.marker = ''
      this.queryTable.className = ''
      this.queryTable.methodName=''
      this.queryTable.threadName=''
      this.queryTable.message=''
      this.queryTable.traceId=''
      this.queryTable.mdc={}
      this.queryTable.keywords = []
      this.queryTablePage()
    },
    getQueryLogSchemeOptions(){
      getLogSchemeOptionsApi().then(rsp => {
         this.querySchemeList = rsp.data.options
      })
    },
    getQueryLogSchemeDetails(id){
      getLogSchemeDetailsApi(id).then(rsp => {
        this.queryTable = rsp.data.scheme
        this.queryTable.querySchemeId = rsp.data.id
        this.queryTable.querySchemeName = rsp.data.schemeName
      })
    },
    openSaveQueryParamsSchemeDialog(){
        if (this.queryTable.querySchemeId){
          this.querySchemeForm.schemeName = this.queryTable.querySchemeName;
        }

        this.saveQueryParamsSchemeDialogVisible = true

    },
    saveQueryParamsSchemeBtn(){
      this.querySchemeForm.schemeContent = JSON.stringify(this.queryTable);
      this.querySchemeForm.id = this.queryTable.querySchemeId
      saveLogSchemeApi(this.querySchemeForm).then(rsp => {
        this.$message.success("保存查询方案成功");
        this.saveQueryParamsSchemeDialogVisible = false;
        this.getQueryLogSchemeOptions();
      })
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
    addToParam4Mdc(){
      this.addToParam4MdcToMdcMap(this.mdcItem.key,this.mdcItem.value)
      this.mdcItem.key = ''
      this.mdcItem.value = ''
    },
    addToParam4MdcToMdcMap(key,value){
      if (!key){
        return;
      }
      if (!value){
        return;
      }
      this.$set(this.queryTable.mdc,key,value)
      console.log("mdc 数据 "+JSON.stringify(this.queryTable.mdc))
    },
    closeKeywordEvent(keyword){
      this.queryTable.keywords.forEach((item, index, arr) => {
        if (item === keyword) {
          arr.splice(index,1)
        }
      });
    },
    closeMdcIteEvent(mdcKey){
      this.$delete(this.queryTable.mdc,mdcKey)
    },
    addToParams(field,value){
      this.$set(this.queryTable,field,value)
      this.queryTablePage()
    },
    queryTablePage() {
      this.queryTable.createFromTime = this.createTimeRange[0];
      this.queryTable.createToTime = this.createTimeRange[1];
      this.pageParams.pageNum = this.currentPage;
      this.pageParams.pageSize = this.currentPageSize;
      getLogPageApi(this.queryTable,this.pageParams).then(rsp => {
        this.articleTable = rsp.data.list;
        this.tableHeight =  rsp.data.list.length * 50
        if (this.tableHeight < 600){
          this.tableHeight = 600
        }
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
    schemeChangeEvent(val){
      console.log("查询方案  "+val)
      this.getQueryLogSchemeDetails(val);
    },

    inputEvent(){
      this.$forceUpdate()
    }

  }
}
</script>
<style>
  .mdc-item-box-style {
     width: 550px;
     float: left;
  }
  .mdc-item-box-style .mdc-key-box-style{
      display: inline-block;
  }
  .mdc-item-box-style .mdc-value-box-style{
    display: inline-block;
  }
  .mdc-list-box-style{
     float: left;
     display: inline-block;
  }
  .spread-query-btn-sty{
    background-color: red;
    height: 10px;
  }
  .mdc-row-label-style{
     width: 260px;
  }
  .mdc-row-content-style{
    width: 400px;
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
