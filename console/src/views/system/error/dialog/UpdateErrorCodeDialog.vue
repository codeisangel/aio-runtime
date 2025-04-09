
<template>
  <div>
    <el-dialog title="更新错误码" :visible.sync="dialogVisible" top="30px" v-if="dialogVisible" width="60%">
          <el-form ref="queryForm" :model="queryForm" label-width="100px" size="small">
            <el-form-item label="错误码">
              <el-input v-model="queryForm.id"  disabled />
            </el-form-item>
            <el-form-item label="所属模块">
              <el-input v-model="queryForm.module"  />
            </el-form-item>
            <el-form-item label="错误码内容">
              <el-input  type="textarea" :rows="4" v-model="queryForm.content"  />
            </el-form-item>
            <el-form-item label="解决方案">
              <el-input  type="textarea" :rows="4"  v-model="queryForm.solution"  />
            </el-form-item>
            <el-form-item>
              <el-col :span="8" :offset="16">
                <el-button  @click="clearQueryFormBtn">清空</el-button>
                <el-button type="primary"  @click="addErrorCodeBtn">更新</el-button>
              </el-col>
            </el-form-item>
          </el-form>
    </el-dialog>

  </div>
</template>

<script>


import {updateErrorCodeApi} from "@/api/errorCodeApi";

export default {
  name: 'UpdateErrorCodeDialog',
  data() {
    return {
      dialogVisible: false,
      queryForm: {
        projectId:'',
        solutionName:''
      },
      title: '',
      Info: {},
      tableTotal: 0
    }
  },

  mounted() {
  },
  methods: {
    openDialog(info) {
      if (info) {
        this.queryForm = info
      }
      this.dialogVisible = true
    },
    closeDialog() {
      this.dialogVisible = false
    },
    clearQueryFormBtn(){
      this.queryForm = {}
      this.dialogVisible = false
    },
    addErrorCodeBtn(){
      updateErrorCodeApi(this.queryForm).then(response => {
        this.$message.success(response.msg)
        this.dialogVisible = false
        this.$emit('closed', response.data)
      })
    }


  }
}
</script>

<style lang="scss" scoped>
.solution-card-sty {
  .func-sty {
    padding-right: 15px;
    position: absolute;
    right: 10px;
    top: 5px;
    height: 100%;
    .exec-sty {
      margin-top: 2px;
      line-height: 45px;
      height: 45px;

    }
    .record-sty {
      margin-top: 5px;
      height: 45px;
      line-height: 45px;
    }
  }
  position: relative;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: white;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  height: 120px;
}
</style>
