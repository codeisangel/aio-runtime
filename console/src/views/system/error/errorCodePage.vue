<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-form :model="queryTable" class="demo-form-inline" size="mini" label-width="80px">
        <el-col :span="24">
          <el-col :span="6">
            <el-form-item label="错误码">
              <el-input v-model="queryTable.id" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="内容">
              <el-input v-model="queryTable.content" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="所属模块">
              <el-input v-model="queryTable.module" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="解决方案">
              <el-input v-model="queryTable.solution" clearable @keyup.native.enter="queryTableBtn" @input="inputEvent($event)"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item>
              <el-button type="primary" @click="createErrorCodeBtn">创建错误码</el-button>
              <el-button type="primary" @click="exportErrorCodeBtn">导出</el-button>
              <el-button type="primary" @click="importErrorCodeBtn">导入</el-button>
            </el-form-item>
          </el-col>

          <el-col :span="4" :offset="15">
            <el-form-item>
              <el-button @click="clearQueryParamsBtn">清除</el-button>
              <el-button type="primary" @click="queryTableBtn">查询</el-button>
            </el-form-item>
          </el-col>

        </el-col>

      </el-form>

    </el-row>


    <el-table :data="articleTable"  border :height="tableHeight" size="mini" style="margin-top: 10px">
      <el-table-column prop="id" label="错误码" width="120" align="center" />
      <el-table-column prop="module" label="所属模块" width="200" align="center" />
      <el-table-column prop="content" label="错误内容" min-width="200" align="center" />
      <el-table-column prop="solution" label="解决方案" min-width="200" align="center" />
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
           <el-button type="text" size="mini" @click="editErrorCodeBtn(scope.row)">编辑</el-button>
           <el-button type="text" size="mini" @click="deletedErrorCodeBtn(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="log-bottom-box-style">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
                     :page-sizes="[10,30,50,100, 200, 300]" :page-size="currentPageSize"
                     layout="total, sizes, prev, pager, next, jumper" :total="tableTotal">
      </el-pagination>
    </div>

    <create-error-code-dialog ref="createErrorCode" />
    <update-error-code-dialog ref="updateErrorCode" />
  </div>
</template>

<script>


import {deleteErrorCodeApi, exportErrorCodeApi, getErrorCodePageApi, updateErrorCodeApi} from "@/api/errorCodeApi";
import CreateErrorCodeDialog from "@/views/system/error/dialog/CreateErrorCodeDialog.vue";
import UpdateErrorCodeDialog from "@/views/system/error/dialog/UpdateErrorCodeDialog.vue";

export default {
  components: {UpdateErrorCodeDialog, CreateErrorCodeDialog},
  data() {
    return {
      queryTable: {
        keywords:[],
        createToTime: '',
        createFromTime: '',
        querySchemeId:'',
        remark:'',
        querySchemeName:'',
      },
      tableHeight: 600,
      articleTable: [],
      currentPage: 1,
      currentPageSize: 10,
      tableTotal: 0
    }
  },
  created() {

  },
  mounted() {
    this.queryTablePage()
  },
  methods: {
    createErrorCodeBtn(){
      this.$refs.createErrorCode.openDialog()
    },
    editErrorCodeBtn(val){
      this.$refs.updateErrorCode.openDialog(val)
    },
    deletedErrorCodeBtn(val){
      this.$confirm(`此操作将删除该错误码【 ${val.id} 】, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
          deleteErrorCodeApi(val).then(response => {
            this.$message.success(response.msg)
            this.queryTablePage()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    /**
     * 导出错误码
     */
    exportErrorCodeBtn(){
      this.$confirm(`此操作根据当前查询条件导出最多5000条错误码, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        exportErrorCodeApi(this.queryTable).then(response => {

        })
      })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消导出'
          })
        })
    },
    importErrorCodeBtn(){

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
      this.queryTable.id=''
      this.queryTable.content=''
      this.queryTable.module=''
      this.queryTable.solution=''
      this.queryTablePage()
    },

    queryTableBtn(){
      this.currentPage = 1
      this.queryTablePage()
    },
    queryTablePage() {
      this.queryTable.pageNum = this.currentPage;
      this.queryTable.pageSize = this.currentPageSize;
      getErrorCodePageApi(this.queryTable).then(rsp => {
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
