
<template>
  <div>
    <el-dialog title="错误上报详情" :visible.sync="dialogVisible" v-if="dialogVisible" top="60px" width="75%">
      <el-descriptions class="margin-top" :column="3" border labelClassName="error-reported-descriptions-label-sty" >
        <el-descriptions-item label="消息内容" :span="3">
          <span style="color: #009688">{{reportedInfo.message}}</span>
        </el-descriptions-item>

        <el-descriptions-item label="级别">{{reportedInfo.level}}</el-descriptions-item>
        <el-descriptions-item label="追踪码">{{reportedInfo.traceId}}</el-descriptions-item>
        <el-descriptions-item label="错误类型">{{reportedInfo.type}}</el-descriptions-item>

        <el-descriptions-item label="所属终端">{{reportedInfo.terminal}}</el-descriptions-item>
        <el-descriptions-item label="所属平台">{{reportedInfo.platform}}</el-descriptions-item>
        <el-descriptions-item label="用户令牌">{{reportedInfo.token}}</el-descriptions-item>

        <el-descriptions-item label="终端信息" :span="3">
          <span>{{reportedInfo.terminalInfo}}</span>
        </el-descriptions-item>

        <el-descriptions-item label="备注" :span="3">
          <span>{{reportedInfo.remark}}</span>
        </el-descriptions-item>
        <el-descriptions-item label="请求地址" :span="3">
          <span style="color: #01AAED">{{reportedInfo.apiUrl}}</span>
        </el-descriptions-item>
      </el-descriptions>

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>


export default {
  name: 'ErrorReportedDetailsDialog',
  data() {
    return {
      dialogVisible: false,
      reportedInfo: {}
    }
  },

  mounted() {
  },
  methods: {
    openDialog(value) {
      this.reportedInfo = value
      this.dialogVisible = true
    },
    closeDialog() {
      this.dialogVisible = false
    }
  }
}
</script>

<style>
.error-reported-descriptions-label-sty {
  width: 120px;
}
</style>
