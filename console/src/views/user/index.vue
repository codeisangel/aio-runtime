<template>
  <div class="app-container">
    <el-descriptions title="用户信息"  :column="1">
      <el-descriptions-item label="用户编号">{{currentUser.id}}</el-descriptions-item>
      <el-descriptions-item label="用户名">{{currentUser.username}}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{currentUser.phone}}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{currentUser.email}}</el-descriptions-item>
      <el-descriptions-item label="真实姓名">{{currentUser.realName}}</el-descriptions-item>
      <el-descriptions-item label="证件号">{{currentUser.authCardId}}</el-descriptions-item>
      <el-descriptions-item label="注册时间">{{currentUser.createTime}}</el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<script>
import {getInfo} from '@/api/user'

export default {
  data() {
    return {
      currentUser: {
        id:'',
        realName: '',
        username: '',
        phone: '',
        email: '',
        authCardId: '',
        createTime: '',
        desc: ''
      }
    }
  },
  mounted() {
    this.queryUserInfo()
  },
  methods: {
    queryUserInfo() {
      getInfo().then(rsp => {
        console.log("用户信息 ： " + JSON.stringify(rsp.data))
        this.currentUser = rsp.data
      }).catch(err => {
        this.$message.error(err)
      })
    }
  }
}
</script>

<style scoped>
.line{
  text-align: center;
}
</style>

