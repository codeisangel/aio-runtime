<template>
  <div class="app-container">

    <el-row :gutter="20">
      <el-form :model="queryTable" class="demo-form-inline" size="mini" label-width="120px">
          <el-col :span="4" :offset="20">
            <el-form-item>
              <el-button type="primary" @click="queryTableBtn">刷新</el-button>
            </el-form-item>
          </el-col>
      </el-form>
    </el-row>

    <el-table :data="articleTable" height="680" size="small" border style="margin-top: 10px">
      <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
      <el-table-column label="缓存实现类" min-width="600">
        <template slot-scope="scope">
          <span style="color: #009688;">{{scope.row.target}}</span>
        </template>
      </el-table-column>
      <el-table-column label="缓存管理" min-width="220"  align="center">
        <template slot-scope="scope">
          <span style="color: #009688;">{{scope.row.cacheManagerName}}</span>
        </template>
      </el-table-column>
      <el-table-column label="缓存名称" min-width="260"  align="center">
        <template slot-scope="scope">
          <span style="color: #009688;">{{scope.row.cacheName}}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="360" align="center">
        <template slot-scope="scope">
          <el-button type="text" plain size="mini" @click="openClearCacheDialog(scope.row)">清空缓存</el-button>
          <el-button type="text" plain size="mini" @click="deleteCacheContentDialog(scope.row)">删除缓存</el-button>
          <el-button type="text" plain size="mini" @click="getCacheContentDialog(scope.row)">获取缓存</el-button>
        </template>
      </el-table-column>

    </el-table>


    <el-dialog :title="cacheDialogTitle" :visible.sync="setCacheFormDialogVisible" width="30%">
      <el-form :model="setCacheForm" ref="setCacheForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="缓存管理名称">
          <el-input v-model="setCacheForm.cacheManagerName" disabled></el-input>
        </el-form-item>
        <el-form-item label="缓存名称">
          <el-input v-model="setCacheForm.cacheName" disabled></el-input>
        </el-form-item>
        <el-form-item label="缓存KEY" v-if="setCacheForm.func ==='deleteContent' || setCacheForm.func ==='getContent'">
          <el-input v-model="setCacheForm.key"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="setCacheFormDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="requestCacheBtn(setCacheForm.func)">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog :title="cacheDialogTitle" :visible.sync="showCacheValueDialogVisible" width="30%">
      <el-form :model="showCacheValueForm" ref="showCacheValueForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="缓存KEY">
          <el-input v-model="showCacheValueForm.key" disabled></el-input>
        </el-form-item>
        <el-form-item label="缓存值">
          <el-input v-model="showCacheValueForm.value"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="showCacheValueDialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>


  </div>
</template>

<script>
import {clearCacheContentApi, deleteCacheContentApi, getAllCacheApi, getCacheContentApi} from "@/api/cacheApi";
export default {
  data() {
    return {
      setCacheFormDialogVisible:false,
      showCacheValueDialogVisible:false,
      queryTable: {
        cacheManagerName:'',
        cacheName: ''
      },
      cacheDialogTitle:'',
      showCacheValueForm:{
        key: '',
        value:''
      },
      setCacheForm:{
        func:'',
        cacheManagerName:'',
        cacheName:'',
        key: ''
      },
      articleTable: [],
    }
  },
  created() {
    this.queryTablePage()
  },
  mounted() {
  },
  methods: {
    queryTableBtn(){
      this.queryTablePage()
    },
    queryTablePage() {
      getAllCacheApi(this.queryTable).then(rsp => {
        this.articleTable = rsp.data;
        console.log("缓存数据 ： {} ",this.articleTable)
      })
    },
    openClearCacheDialog(row){
      this.setCacheForm = row;
      this.setCacheForm.func = 'clear'
      this.cacheDialogTitle = "清空缓存"
      this.setCacheFormDialogVisible = true

    },
    deleteCacheContentDialog(row){
      this.setCacheForm = row;
      this.setCacheForm.func = 'deleteContent'
      this.cacheDialogTitle = "删除缓存"
      this.setCacheFormDialogVisible = true

    },
    getCacheContentDialog(row){
      this.setCacheForm = row;
      this.cacheDialogTitle = "获取缓存"
      this.setCacheForm.func = 'getContent'
      this.setCacheFormDialogVisible = true
    },
    requestCacheBtn(){
      console.log(" 功能  " + this.setCacheForm.func)
      if (this.setCacheForm.func === 'clear'){
        clearCacheContentApi(this.setCacheForm).then(rsp => {
          this.$message.success(rsp.msg)
          this.queryTablePage()
          this.setCacheFormDialogVisible = false
        })
      }else if (this.setCacheForm.func === 'getContent'){
        getCacheContentApi(this.setCacheForm).then(rsp => {
          this.$message.success(rsp.msg)
          this.queryTablePage()
          this.setCacheFormDialogVisible = false
          this.showCacheValueDialogVisible = true
          this.showCacheValueForm.key = this.setCacheForm.key
          this.showCacheValueForm.value = rsp.data
        })
      }else if (this.setCacheForm.func === 'deleteContent'){
        deleteCacheContentApi(this.setCacheForm).then(rsp => {
          this.$message.success(rsp.msg)
          this.queryTablePage()
          this.setCacheFormDialogVisible = false
        })
      }
    }

  }
}
</script>
