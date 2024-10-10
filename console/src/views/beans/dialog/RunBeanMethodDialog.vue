<!--
 * @Description: 平台下所有企业
-->
<template>
  <el-dialog title="执行bean方法" :visible.sync="dialogVisible" v-if="dialogVisible" top="30px" width="60%">
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
      <el-form-item label="方法参数">
        <json-editor ref="jsonEditor" v-model="runParams.parameters" />
      </el-form-item>
      <el-form-item label="执行结果">
        <json-viewer :value="runResultObj" copyable theme="my-awesome-json-theme">
          <template slot="copy">
            <el-button type="text">复制</el-button>
          </template>
        </json-viewer>
      </el-form-item>


    </el-form>
    <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="runBeanBtn()">确 定</el-button>
    </span>

  </el-dialog>
</template>

<script>
import {getMethodListApi, getMethodParametersApi, runMethodApi} from "@/api/beanApi";
import JsonEditor from '@/components/GDJsonEditor'
export default {
  name: 'RunBeanMethodDialog',
  components: { JsonEditor },
  data() {
    return {
      dialogVisible: false,
      queryForm: {
      },
      runBeanForm: {

      },
      runParams:[],
      runResultObj:{},
      companyUserList: [],
      tableTotal: 0
    }
  },
  created() {

  },
  mounted() {
  },
  methods: {
    openDialog(bean) {
      this.runBeanForm = bean
      const params = {className : bean.className,beanName : bean.beanName}
      getMethodListApi(params).then(rsp => {
        this.runBeanForm.methods = rsp.data
        this.dialogVisible = true
      })

    },
    closeDialog() {
      this.dialogVisible = false
    },
    getMethodParamsInfo(){
      this.runBeanForm.methods.forEach(item => {
        if (item.methodDesc === this.runBeanForm.method){
          getMethodParametersApi(item).then(rsp => {
            this.runParams = rsp.data
          })
        }
      })
    },
    runBeanBtn(){
      runMethodApi(this.runParams).then(rsp => {
        this.runResultObj = rsp.data
        this.$message.success(rsp.msg)
      })
    }
  }
}
</script>

<style>
.company-select-sty .el-select {
  width: 380px;
}
.platform-select-sty .el-select {
  width: 320px;
}
</style>
