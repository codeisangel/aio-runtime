<template>
  <div class="app-container">
    <el-row>
    <el-form :model="queryTable" :inline="true" class="demo-form-inline" size="mini" label-width="80px">

        <el-col :span="4">
          <el-form-item label="用户名" >
            <el-input v-model="queryTable.userName"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="用户ID">
            <el-input v-model="queryTable.userId"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="企业名称">
            <el-input v-model="queryTable.companyName"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="企业ID">
            <el-input v-model="queryTable.companyId"></el-input>
          </el-form-item>
        </el-col>

        <el-col :span="4">
          <el-form-item label="追踪码">
            <el-input v-model="queryTable.traceId"></el-input>
          </el-form-item>
        </el-col>

        <el-col :span="4">
          <el-form-item label="请求方式">
            <el-col :span="20">
            <el-select v-model="queryTable.httpMethod" clearable placeholder="请选择请求方式">
              <el-option label="GET" value="GET"></el-option>
              <el-option label="POST" value="POST"></el-option>
              <el-option label="PUT" value="PUT"></el-option>
              <el-option label="DELETE" value="DELETE"></el-option>
            </el-select>
            </el-col>
          </el-form-item>

        </el-col>

        <el-col :span="4">
          <el-form-item label="请求地址">
            <el-input v-model="queryTable.url"></el-input>
          </el-form-item>
        </el-col>

        <el-col :span="4">
          <el-form-item label="是否成功">
            <el-col :span="22">
            <el-select v-model="queryTable.success" clearable placeholder="请选择是否成功">
              <el-option label="成功" value="YES"></el-option>
              <el-option label="失败" value="NO"></el-option>
            </el-select>
            </el-col>
          </el-form-item>
        </el-col>


        <el-col :span="8">
          <el-form-item label="发生时间">
            <el-date-picker v-model="createTimeRange" value-format="timestamp" type="datetimerange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
          </el-form-item>
        </el-col>

        <el-col :offset="6" :span="2">
          <el-form-item>
            <el-button @click="queryTablePage">清除</el-button>
            <el-button type="primary" @click="queryTablePage">查询</el-button>
          </el-form-item>
        </el-col>
    </el-form>

    </el-row>

    <el-table :data="articleTable" border height="680" size="mini" style="margin-top: 10px">
      <el-table-column type="index" width="50" align="center"></el-table-column>
      <el-table-column prop="url" label="请求地址" min-width="260"></el-table-column>
      <el-table-column label="请求方式" width="100" align="center">
        <template slot-scope="scope">
          <el-tag  size="mini">{{scope.row.httpMethod}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="params" label="请求参数" min-width="300"></el-table-column>
      <el-table-column label="是否成功" width="80" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.success==='YES'" type="success" size="mini">{{scope.row.success}}</el-tag>
          <el-tag v-else type="danger"  size="mini">{{scope.row.success}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160"></el-table-column>
      <el-table-column prop="companyName" label="企业名称" width="200"></el-table-column>
      <el-table-column prop="userName" label="用户名称" width="200"></el-table-column>
      <el-table-column prop="mappingMethod" label="关联方法" width="140"></el-table-column>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template slot-scope="scope">
          <el-button @click="documentDetail(scope.row)" type="text" size="small">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                   :current-page="currentPage"
                   :page-sizes="[50,100, 200, 300, 400,500]"
                   :page-size="50"
                   layout="total, sizes, prev, pager, next, jumper"
                   :total="tableTotal">
    </el-pagination>


    <el-dialog :title="currentRow.url+' 记录详情'" :visible.sync="documentDetailDialogVisible" v-if="documentDetailDialogVisible" width="75%">
      <el-descriptions class="margin-top" title="详情" :column="3" border>
        <template slot="extra">
          <el-button type="primary" size="small" @click="deleteDocument()">删除文档</el-button>
        </template>

        <el-descriptions-item label="文档ID">{{currentRow.id}}</el-descriptions-item>
        <el-descriptions-item label="文档名称">{{currentRow.httpMethod}}</el-descriptions-item>
        <el-descriptions-item label="文档地址">{{currentRow.url}}</el-descriptions-item>

        <el-descriptions-item label="菜单名称">{{currentRow.mappingClass}}</el-descriptions-item>
        <el-descriptions-item label="菜单ID">{{currentRow.mappingMethod}}</el-descriptions-item>
        <el-descriptions-item label="模块">{{currentRow.createTime}}</el-descriptions-item>

        <el-descriptions-item label="追踪码" :span="3">{{currentRow.traceId}}</el-descriptions-item>

        <el-descriptions-item label="用户ID">{{currentRow.userId}}</el-descriptions-item>
        <el-descriptions-item label="用户">{{currentRow.userName}}</el-descriptions-item>
        <el-descriptions-item label="企业ID">{{currentRow.companyId}}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{currentRow.companyName}}</el-descriptions-item>


      </el-descriptions>

      <el-divider content-position="left">请求参数</el-divider>
      <div>
        <pre v-highlight-a>
           <code>{{currentRow.params}}</code>
        </pre>
      </div>

      <div v-if="currentRow.success === 'YES'">
        <el-divider content-position="left">返回结果</el-divider>
        <div>
          <pre v-highlight-a>
             <code>{{currentRow.result}}</code>
          </pre>
        </div>
      </div>
      <div v-else>
        <el-divider content-position="left">异常信息</el-divider>
        <div>
          <h3>异常类</h3>
          <pre v-highlight-a>
             <code>{{currentRow.throwable}}</code>
          </pre>
          <h3>异常信息</h3>
          <pre v-highlight-a>
             <code>{{currentRow.exceptionMsg}}</code>
          </pre>

          <h3>堆栈跟踪</h3>
          <template>
            <el-input placeholder="请输入内容" v-model="stackTraceCLass" class="input-with-select">
              <el-button slot="append" icon="el-icon-search" @click="filterHandler"></el-button>
            </el-input>

            <el-table :data="currentStackTrace" style="width: 90%;margin: 0 auto;">
              <el-table-column type="index" width="50" label="序号"></el-table-column>
              <el-table-column prop="className" label="类名" min-width="200"></el-table-column>
              <el-table-column prop="methodName" label="方法名" width="180"></el-table-column>
              <el-table-column prop="lineNumber" label="所在行" width="80"></el-table-column>
              <el-table-column label="原生方法" width="120">
                <template slot-scope="scope">
                  <el-tag>{{scope.row.nativeMethod}}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </template>

        </div>
      </div>



    </el-dialog>



  </div>
</template>

<script>
import {getLogPageApi} from '@/api/table'


export default {
  data() {
    return {
      queryTable: {
        httpMethod:'',
        mappingClass:'',
        mappingMethod:'',
        success:'',
        traceId:'',
        userId:'',
        userName:'',
        companyId:'',
        companyName:'',
        throwable:'',
        createFromTime:'',
        createToTime:'',
      },
      createTimeRange:[],
      stackTraceCLass:'',
      documentDetailDialogVisible: false,
      currentRow:{
        httpMethod:'',
        mappingClass:'',
        mappingMethod:'',
        success:'',
        traceId:'',
        userId:'',
        userName:'',
        companyId:'',
        companyName:'',
        throwable:'',
        createTime:''
      },
      currentStackTrace:[],
      articleTable: [],
      currentPage:1,
      currentPageSize:50,
      tableTotal:0
    }
  },
  created() {

  },
  mounted() {
    this.queryTablePage()
  },
  methods: {
    filterHandler(){
      if (this.stackTraceCLass === null || this.stackTraceCLass === undefined || this.stackTraceCLass.trim() === '') {
        this.currentStackTrace = this.currentRow.stackTrace;
        return
      }
      this.currentStackTrace = [];
      for(var index in this.currentRow.stackTrace){
        let row = this.currentRow.stackTrace[index];
        if (row.className.includes(this.stackTraceCLass)){
          this.currentStackTrace.push(row)
        }
      }
    },
    deleteDocument() {

    },
    documentDetail(row) {
      this.currentRow = {}
      this.documentDetailDialogVisible = true;
      this.$set(this,'currentRow',row)
      this.currentStackTrace = this.currentRow.stackTrace;
    },
    handleSizeChange(val){
      this.currentPageSize = val;
      this.queryTablePage();
    },
    handleCurrentChange(val){
      this.currentPage = val;
      this.queryTablePage();
    },
    queryTablePage() {
      this.queryTable.createFromTime = this.createTimeRange[0];
      this.queryTable.createToTime = this.createTimeRange[1];
      this.queryTable.pageNum = this.currentPage;
      this.queryTable.pageSize = this.currentPageSize;
      console.log("文章列表查询条件 ： " + JSON.stringify(this.queryTable))
      getLogPageApi(this.queryTable).then(rsp => {
        this.articleTable = rsp.data.list;
        this.tableTotal = rsp.data.total;
      }).catch(err => {
        this.$message.error(err)
      })
    }



  }
}
</script>
