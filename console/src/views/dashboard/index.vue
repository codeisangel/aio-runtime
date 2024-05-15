<template>
  <div class="dashboard-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>车管地址</span>
      </div>
      <div class="el-link-content-sty">
        <el-link type="success" href=" http://192.168.2.137:8080/ " target="_blank">车主端</el-link>
        <el-link type="success" href=" http://192.168.2.137:8081/ " target="_blank">运维端</el-link>
        <el-link type="success" href=" http://192.168.2.137:8082/ " target="_blank">金融机构</el-link>
        <el-link type="success" href=" http://192.168.2.137:8083/ " target="_blank">车管所</el-link>
        <el-link type="success" href=" http://192.168.1.61:9120/plumelog/# " target="_blank">日志</el-link>
        <el-link type="success" href=" http://192.168.1.233:9001/plumelog/# " target="_blank">用户中心日志</el-link>
      </div>
    </el-card>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  computed: {
    ...mapGetters([
      'name'
    ])
  }
}
</script>

<style lang="scss" scoped>
  .el-link-content-sty .el-link{
     margin-left: 50px;
  }
.dashboard {
  &-container {
    margin: 30px;
  }
  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}
</style>
