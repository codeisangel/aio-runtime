<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-form :model="queryTable" class="demo-form-inline" size="mini" label-width="80px">
        <el-col :span="24">
          <el-col :span="8">
            <el-form-item label="类名">
              <el-input v-model="queryTable.className" @keyup.native.enter="queryTableBtn"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="方法名">
              <el-input v-model="queryTable.methodName" @keyup.native.enter="queryTableBtn"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="发生时间">
              <el-date-picker v-model="createTimeRange" value-format="timestamp" type="datetimerange" @change="queryTablePage"
                              range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"
                              :picker-options="createTimeRangeOptions"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-col>

        <el-col :span="24">
        <el-col :span="8">
          <el-form-item label="消息内容">
            <el-input v-model="queryTable.message" @keyup.native.enter="queryTableBtn"></el-input>
          </el-form-item>
        </el-col>
          <el-col :span="6">
            <el-form-item label="订阅名">
              <el-input v-model="queryTable.subscribeName" @keyup.native.enter="queryTableBtn"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="处理状态">
              <el-select v-model="queryTable.handleStatus" clearable placeholder="请选择处理状态" @change="queryTableBtn">
                <el-option label="未处理" value="0"></el-option>
                <el-option label="已处理" value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>

        <el-col :span="4">
          <el-form-item>
            <el-button @click="clearQueryParamsBtn">清除</el-button>
            <el-button type="primary" @click="queryTableBtn">查询</el-button>
          </el-form-item>
        </el-col>
        </el-col>
      </el-form>

    </el-row>

    <el-table :data="articleTable" height="680" size="mini" style="margin-top: 10px">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form class="demo-table-expand" size="mini" label-width="120px">
            <el-form-item label="订阅名称">
              <span style="color: #009688;">{{props.row.subscribeName}}</span>
            </el-form-item>
            <el-form-item label="日志追踪码">
              <span>{{ props.row.traceId }}</span>
            </el-form-item>
            <el-form-item label="类名">
              <span style="color: #1E9FFF;">{{ props.row.className }}</span>
            </el-form-item>
            <el-form-item label="方法名">
              <el-tag>{{ props.row.methodName }}</el-tag>
            </el-form-item>
            <el-form-item label="消息内容">
              <span>{{ props.row.message }}</span>
            </el-form-item>
            <el-form-item label="发生时间">
              <span>{{ props.row.createTime }}</span>
            </el-form-item>
            <el-form-item label="用户ID">
              <span>{{ props.row.userId }}</span>
            </el-form-item>
            <el-form-item label="企业ID">
              <span>{{ props.row.companyId }}</span>
            </el-form-item>
            <el-form-item label="处理状态">
               <el-tag v-if="props.row.handleStatus === 0" type="warning">{{props.row.handleStatusDesc}}</el-tag>
              <el-tag v-if="props.row.handleStatus === 1" type="success">{{props.row.handleStatusDesc}}</el-tag>
              <el-button style="margin-left: 50px;" v-if="props.row.handleStatus === 0" type="primary" @click="updateSubscribeStatusToHandled(props.row)">标记为已处理</el-button>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column label="订阅名" min-width="100">
        <template slot-scope="scope">
          <span style="color: #009688;">{{scope.row.subscribeName}}</span>
        </template>
      </el-table-column>
      <el-table-column label="方法名" width="140">
        <template slot-scope="scope">
          <span style="color: #5FB878;">{{scope.row.methodName}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="message" label="消息内容" min-width="460"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="100" align="center"></el-table-column>
      <el-table-column prop="traceId" label="追踪码" width="200"></el-table-column>

    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
                   :page-sizes="[50,100, 200, 300, 400,500]" :page-size="50"
                   layout="total, sizes, prev, pager, next, jumper" :total="tableTotal">
    </el-pagination>


  </div>
</template>

<script>

import {getSubscribeLogPageApi, updateSubscribeStatusApi} from "@/api/subscribeApi";


export default {
  data() {
    return {
      queryTable: {
        handleStatus:'0',
        createFromTime: '',
        createToTime: '',
      },
      createTimeRange: [],
      createTimeRangeOptions:{
        shortcuts: [
          {
            text: '10分钟',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - (1000 * 60 * 10));
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
              end.setTime(end.getTime() + (1000 * 60 * 60))
              picker.$emit('pick', [start, end]);
            }
          }, {
          text: '最近一个周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        },
          {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          },]
      },
      articleTable: [],
      currentPage: 1,
      currentPageSize: 50,
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
    handleSizeChange(val) {
      this.currentPageSize = val;
      this.queryTablePage();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.queryTablePage();
    },
    clearQueryParamsBtn(){
      this.currentPage = 1
      this.queryTable.subscribeName = ''
      this.queryTable.className = ''
      this.queryTable.methodName=''
      this.queryTable.message=''
      this.queryTable.handleStatus = ''
      this.queryTablePage()
    },
    updateSubscribeStatusToHandled(row){
      updateSubscribeStatusApi(row).then(rsp => {
        this.$message.success(rsp.msg)
        this.queryTablePage()
      })
    },
    queryTableBtn(){
      this.currentPage = 1
      this.queryTablePage()
    },
    queryTablePage() {
      this.queryTable.createFromTime = this.createTimeRange[0];
      this.queryTable.createToTime = this.createTimeRange[1];
      this.queryTable.pageNum = this.currentPage;
      this.queryTable.pageSize = this.currentPageSize;
      console.log("文章列表查询条件 ： " + JSON.stringify(this.queryTable))
      getSubscribeLogPageApi(this.queryTable).then(rsp => {
        this.articleTable = rsp.data.list;
        this.tableTotal = rsp.data.total;
      }).catch(err => {
        this.$message.error(err)
      })
    }


  }
}
</script>
