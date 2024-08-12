<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-form :model="queryTable" class="demo-form-inline" size="mini" label-width="80px">
        <el-col :span="24">
          <el-col :span="8">
            <el-form-item label="日志名">
              <el-input v-model="queryTable.name" @keyup.native.enter="queryTableBtn"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="日志配置">
              <el-select v-model="queryTable.configuredLevel" clearable placeholder="请选择处理状态" @change="queryTableBtn">
                <el-option label="TRACE" value="TRACE"></el-option>
                <el-option label="DEBUG" value="DEBUG"></el-option>
                <el-option label="INFO" value="INFO"></el-option>
                <el-option label="WARN" value="WARN"></el-option>
                <el-option label="ERROR" value="ERROR"></el-option>
              </el-select>
            </el-form-item>
          </el-col>

        <el-col :span="4" :offset="6">
          <el-form-item>
            <el-button @click="clearQueryParamsBtn">清除</el-button>
            <el-button type="primary" @click="queryTableBtn">查询</el-button>
          </el-form-item>
        </el-col>
        </el-col>
      </el-form>

    </el-row>

    <el-row>
      <el-button type="primary" size="small" @click="operationEditLogLevelDialog">修改日志级别</el-button>
    </el-row>

    <el-table :data="articleTable" height="680" size="small" border style="margin-top: 10px">
      <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
      <el-table-column label="类名" min-width="800">
        <template slot-scope="scope">
          <span style="color: #009688;">{{scope.row.name}}</span>
        </template>
      </el-table-column>
      <el-table-column label="配置级别" width="120" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.configuredLevel === 'DEBUG' " type="success" size="mini">{{scope.row.configuredLevel}}</el-tag>
          <el-tag v-else-if="scope.row.configuredLevel === 'WARN' " type="warning" size="mini">{{scope.row.configuredLevel}}</el-tag>
          <el-tag v-else-if="scope.row.configuredLevel === 'ERROR' " type="danger" size="mini">{{scope.row.configuredLevel}}</el-tag>
          <el-tag v-else-if="!scope.row.configuredLevel" type="info" size="mini">未配置</el-tag>
          <el-tag v-else size="mini">{{scope.row.configuredLevel}}</el-tag>

        </template>
      </el-table-column>
      <el-table-column label="生效级别" width="120" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.effectiveLevel === 'DEBUG' " style="color: #5FB878; font-weight: bold;">{{scope.row.effectiveLevel}}</span>
          <span v-else-if="scope.row.effectiveLevel === 'WARN' " style="color: #FFB800;font-weight: bold;">{{scope.row.effectiveLevel}}</span>
          <span v-else-if="scope.row.effectiveLevel === 'ERROR' " style="color: #FF5722;font-weight: bold;">{{scope.row.effectiveLevel}}</span>
          <span v-else style="color: #009688;">{{scope.row.effectiveLevel}}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" min-width="200" align="center">
        <template slot-scope="scope">
          <el-button type="text" plain size="mini" @click="setLogLevelRowBtn(scope.row)">设置日志级别</el-button>
        </template>
      </el-table-column>

    </el-table>


    <el-dialog title="设置日志级别" :visible.sync="setLogLevelDialogVisible" width="30%">
      <el-form :model="setLogLevelForm" ref="querySchemeForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="日志名称">
          <el-input v-if="setLogLevelForm.func === 'operation' " v-model="setLogLevelForm.name"></el-input>
          <el-input v-else v-model="setLogLevelForm.name" disabled></el-input>
        </el-form-item>
        <el-form-item label="生效日志" v-if="setLogLevelForm.func !== 'operation'">
          <el-input  v-model="setLogLevelForm.effectiveLevel" disabled></el-input>
        </el-form-item>
        <el-form-item label="配置状态">
          <el-select v-model="setLogLevelForm.configuredLevel" clearable placeholder="请选择配置状态">
            <el-option label="TRACE" value="TRACE"></el-option>
            <el-option label="DEBUG" value="DEBUG"></el-option>
            <el-option label="INFO" value="INFO"></el-option>
            <el-option label="WARN" value="WARN"></el-option>
            <el-option label="ERROR" value="ERROR"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="setLogLevelDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="setLogLevelBtn()">确 定</el-button>
      </span>
    </el-dialog>


  </div>
</template>

<script>
import {getLogLevelListApi, updateLogLevelApi} from "@/api/logApi";


export default {
  data() {
    return {
      setLogLevelDialogVisible:false,
      queryTable: {
        name:'',
        configuredLevel: ''
      },
      setLogLevelForm:{
        name:'',
        effectiveLevel:'',
        configuredLevel: ''
      },
      articleTable: [],
    }
  },
  created() {

  },
  mounted() {
    this.queryTablePage()
  },
  methods: {
    operationEditLogLevelDialog(){
      this.setLogLevelForm.name = ''
      this.setLogLevelForm.func = 'operation'
      this.setLogLevelDialogVisible = true
    },
    clearQueryParamsBtn(){
      this.queryTable.name = ''
      this.queryTable.configuredLevel = ''
      this.queryTablePage()
    },
    queryTableBtn(){
      this.currentPage = 1
      this.queryTablePage()
    },
    queryTablePage() {

      getLogLevelListApi(this.queryTable).then(rsp => {
        this.articleTable = rsp.data;
      }).catch(err => {
        this.$message.error(err)
      })
    },
    setLogLevelRowBtn(row){
      this.setLogLevelDialogVisible = true
      this.setLogLevelForm = row;
    },
    setLogLevelBtn(){
      updateLogLevelApi(this.setLogLevelForm).then(rsp => {
        this.$message.success(rsp.msg)
        this.queryTablePage()
        this.setLogLevelDialogVisible = false
      })
    }

  }
}
</script>
