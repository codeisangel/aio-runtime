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
          <el-tag v-else  size="mini">{{aliase}}</el-tag>
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


  </div>
</template>

<script>

import {getBeanPageApi} from "@/api/beanApi";


export default {
  data() {
    return {
      queryTable: {
        beanName : '',
        aliase : ''
      },
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
    }


  }
}
</script>
