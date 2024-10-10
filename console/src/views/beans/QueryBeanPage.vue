<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-form :model="queryTable" class="demo-form-inline" size="mini" label-width="80px">
        <el-col :span="24">
          <el-col :span="6">
            <el-form-item label="接口名称">
              <el-input v-model="queryTable.interfaceName"  @keyup.native.enter="queryTableBtn"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="父类">
              <el-input v-model="queryTable.superclass" @keyup.native.enter="queryTableBtn"></el-input>
            </el-form-item>
          </el-col>
        </el-col>
        <el-col :span="24">
          <el-col :span="6">
            <el-form-item label="bean名称">
              <el-input v-model="queryTable.beanName"  @keyup.native.enter="queryTableBtn"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="bean别名">
              <el-input v-model="queryTable.aliase" @keyup.native.enter="queryTableBtn"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="所属类">
              <el-input v-model="queryTable.className" @keyup.native.enter="queryTableBtn"></el-input>
            </el-form-item>
          </el-col>

          <el-col :offset="2" :span="4">
            <el-form-item>
              <el-button @click="clearQueryParamsBtn">清除</el-button>
              <el-button type="primary" @click="queryTableBtn">查询</el-button>
            </el-form-item>
          </el-col>
        </el-col>
      </el-form>

    </el-row>

    <el-table :data="articleTable" height="680" border size="mini" style="margin-top: 10px">

      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form class="demo-table-expand" size="mini" label-width="120px">
            <el-form-item label="所属类">
              <span>{{ props.row.className }}</span>
            </el-form-item>
            <el-form-item label="来源">
              <span>{{ props.row.resource }}</span>
            </el-form-item>
            <el-form-item label="bean名称">
              <span>{{ props.row.beanName }}</span>
            </el-form-item>
            <el-form-item label="bean类型">
              <span>{{ props.row.scope }}</span>
            </el-form-item>
            <el-form-item label="父类">
              <span>{{ props.row.superclass }}</span>
            </el-form-item>
            <el-form-item label="依赖的Bean">
              <el-tag v-for="(dependency) in props.row.dependencies" :key="dependency" size="mini" type="success" style="margin-right: 10px">{{dependency}}</el-tag>
            </el-form-item>
            <el-form-item label="所属接口">
              <el-tag v-for="(inf) in props.row.interfaces" :key="inf" size="mini" type="success" style="margin-right: 10px">{{inf}}</el-tag>
            </el-form-item>
            <el-form-item label="别名">
              <el-tag v-for="(aliase) in props.row.aliases" :key="aliase" size="mini" type="success" style="margin-right: 10px">{{aliase}}</el-tag>
            </el-form-item>

            <el-form-item label="Bean操作">
              <el-button type="primary" @click="openRunBeanDialogVisible(props.row)">执行Bean</el-button>
            </el-form-item>

          </el-form>
        </template>
      </el-table-column>
      <el-table-column label="序号" width="50" type="index" align="center"></el-table-column>
      <el-table-column label="bean名称" width="600">
        <template slot-scope="scope">
          <span style="color: #5FB878;">{{scope.row.beanName}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="scope" label="bean类型" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.scope === 'singleton'" size="mini">单例</el-tag>
          <el-tag v-else-if="scope.row.scope === 'prototype'" size="mini" type="success">原型</el-tag>
          <el-tag v-else  size="mini">{{scope.row.scope}}</el-tag>
        </template>

      </el-table-column>
      <el-table-column label="类名" min-width="260">
        <template slot-scope="scope">
          <span style="color: #009688;">{{scope.row.className}}</span>
        </template>
      </el-table-column>


    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
                   :page-sizes="[50,100,200,300]" :page-size="100"
                   layout="total, sizes, prev, pager, next, jumper" :total="tableTotal">
    </el-pagination>


    <el-dialog title="执行Bean" :visible.sync="runBeanDialogVisible" top="40px" width="55%">
      <el-form :model="runBeanForm" ref="runBeanForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="Bean名称">
          <el-input v-model="runBeanForm.beanName" disabled></el-input>
        </el-form-item>
        <el-form-item label="Class名称">
          <el-input v-model="runBeanForm.className" disabled></el-input>
        </el-form-item>
        <el-form-item label="方法名" class="method-form-style">
          <el-select v-model="runBeanForm.method" @change="getMethodParamsInfo" clearable filterable placeholder="请选择只需执行的方法名">
            <el-option v-for="(methodItem) in runBeanForm.methods" :key="methodItem.methodDesc" :label="methodItem.methodDesc" :value="methodItem.methodDesc"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="方法参数描述">
          <json-viewer :value="runParamsDesc" copyable theme="my-awesome-json-theme">
            <template slot="copy">
              <el-button type="text">复制</el-button>
            </template>
          </json-viewer>
        </el-form-item>
        <el-form-item label="方法参数">
          <el-input  type="textarea" v-model="runBeanForm.params" :rows="8"></el-input>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="runBeanDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="runBeanBtn()">确 定</el-button>
      </span>
    </el-dialog>


    <el-dialog title="执行结果" :visible.sync="runBeanResultDialogVisible" top="40px" width="50%">
      <json-viewer :value="runResultObj" copyable theme="my-awesome-json-theme">
        <template slot="copy">
          <el-button type="text">复制</el-button>
        </template>
      </json-viewer>
    </el-dialog>
    <run-bean-method-dialog ref="runBeanMethod" />
  </div>
</template>

<script>

import {getBeanPageApi, getMethodListApi, getMethodParametersApi, runMethodApi} from "@/api/beanApi";
import RunBeanMethodDialog from "@/views/beans/dialog/RunBeanMethodDialog.vue";


export default {
  components: {RunBeanMethodDialog},
  data() {
    return {
      queryTable: {
        beanName : '',
        aliase : ''
      },
      runBeanForm: {
         methods: []
      },
      runBeanResultDialogVisible : false,
      runBeanDialogVisible : false,
      runParamsDesc:[],
      runResultObj:{},
      articleTable: [],
      currentPage: 1,
      currentPageSize: 100,
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
      this.queryTable.environmentGroup = ''
      this.queryTable.propertyKey = ''
      this.queryTable.propertyValue=''
      this.queryTablePage()
    },
    queryTableBtn(){
      this.currentPage = 1
      this.queryTablePage()
    },
    queryTablePage() {
      this.queryTable.pageNum = this.currentPage;
      this.queryTable.pageSize = this.currentPageSize;
      getBeanPageApi(this.queryTable).then(rsp => {
        this.articleTable = rsp.data.list;
        this.tableTotal = rsp.data.total;
      }).catch(err => {
        this.$message.error(err)
      })
    },
    getMethodParamsInfo(){
      this.runBeanForm.methods.forEach(item => {
        if (item.methodDesc === this.runBeanForm.method){
          getMethodParametersApi(item).then(rsp => {
            this.runParamsDesc = rsp.data
          })
        }
      })
    },
    openRunBeanDialogVisible(row){
      this.$refs.runBeanMethod.openDialog(row)
      // this.runBeanForm = row
      // const params = {className : this.runBeanForm.className}
      // getMethodListApi(params).then(rsp => {
      //   this.runBeanForm.methods = rsp.data
      //   this.runBeanDialogVisible = true
      // })
    },


  }
}
</script>
<style>
.method-form-style .el-select .el-input{
  width: 400px;
}
</style>
