<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-form :model="queryTable" class="demo-form-inline" size="mini" label-width="80px">
        <el-col :span="24">
          <el-col :span="6">
            <el-form-item label="类名">
              <el-input v-model="queryTable.className"  @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="方法名">
              <el-input v-model="queryTable.methodName" @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="接口状态">
              <el-select v-model="queryTable.deprecated" clearable placeholder="请选择接口状态" @change="queryTableBtn">
                <el-option style="color: #FF5722;" label="已作废" :value=true></el-option>
                <el-option style="color: #67C23A;" label="未作废" :value=false></el-option>
              </el-select>
            </el-form-item>
          </el-col>

        </el-col>
        <el-col :span="24">
          <el-col :span="6">
            <el-form-item label="请求方式">
              <el-select v-model="queryTable.httpMethod" clearable placeholder="请选择请求方法" @change="queryTableBtn">
                <el-option label="GET" value="GET"></el-option>
                <el-option label="POST" value="POST"></el-option>
                <el-option label="DELETE" value="DELETE"></el-option>
                <el-option label="PUT" value="PUT"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="请求地址">
              <el-input v-model="queryTable.url" @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>
          <el-col :offset="8" :span="4">
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
            <el-form-item label="类名">
              <span style="color: #5FB878;">{{ props.row.className }}</span>
            </el-form-item>
            <el-form-item label="方法名">
              <span>{{ props.row.methodName }}</span>
            </el-form-item>
            <el-form-item label="请求方法">
              <span>{{ props.row.httpMethod }}</span>
            </el-form-item>
            <el-form-item label="请求地址">
              <span style="color: #009688;">{{ props.row.url }}</span>
            </el-form-item>
            <el-form-item label="是否作废">
              <el-tag type="danger" size="mini" v-if="props.row.deprecated">已作废</el-tag>
              <el-tag size="mini" v-else>未作废</el-tag>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column prop="httpMethod" label="请求方法" width="120"></el-table-column>
      <el-table-column label="请求地址" min-width="240" align="center">
        <template slot-scope="scope">
          <span style="color: #009688;">{{scope.row.url}}</span>
        </template>
      </el-table-column>
      <el-table-column label="接口状态" min-width="80">
        <template slot-scope="scope">
          <span style="color: #FF5722;" v-if="scope.row.deprecated">已作废</span>
          <span style="color: #67C23A;;" v-else>未作废</span>
        </template>
      </el-table-column>
      <el-table-column prop="methodName" label="方法名" min-width="180" align="center"></el-table-column>
    </el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
                   :page-sizes="[10,20,50,100]" :page-size="20"
                   layout="total, sizes, prev, pager, next, jumper" :total="tableTotal">
    </el-pagination>


  </div>
</template>

<script>

import {getMappingPageApi} from "@/api/mappingApi";

export default {
  data() {
    return {
      queryTable: {
      },
      articleTable: [],
      currentPage: 1,
      currentPageSize: 20,
      tableTotal: 0
    }
  },
  created() {

  },
  mounted() {
    this.queryTablePage()
  },
  methods: {
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
      this.queryTable.className = ''
      this.queryTable.methodName = ''
      this.queryTable.httpMethod = ''
      this.queryTable.deprecated = ''
      this.queryTable.url=''
      this.queryTablePage()
    },
    queryTableBtn(){
      this.currentPage = 1
      this.queryTablePage()
    },
    queryTablePage() {
      this.queryTable.pageNum = this.currentPage;
      this.queryTable.pageSize = this.currentPageSize;
      getMappingPageApi(this.queryTable).then(rsp => {
        this.articleTable = rsp.data.list;
        this.tableTotal = rsp.data.total;
      }).catch(err => {
        this.$message.error(err)
      })
    },
    inputEvent(){
      this.$forceUpdate()
    }


  }
}
</script>
