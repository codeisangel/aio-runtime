<template>
  <div class="dashboard-container">
   <el-row :gutter="15">
     <el-col :span="4">
       <el-card class="box-card" style="height: 240px;">
         <el-result icon="success" :title="runtimeVersion" subTitle="当前系统版本">
         </el-result>
       </el-card>
     </el-col>

     <el-col :span="6">
       <el-card class="box-card" style="height: 240px;">
         <el-alert :title="'工作目录 : ' + runTimeWorkspace" type="success" :closable="false" show-icon style="margin-bottom: 8px"></el-alert>
         <el-alert :title="'系统开始时间 : ' + systemStartingTime" type="success" :closable="false" show-icon style="margin-bottom: 8px"></el-alert>
         <el-alert :title="'系统启动时间 : ' + systemStartedTime" type="success" :closable="false" show-icon style="margin-bottom: 8px"></el-alert>
         <el-alert :title="'程序PID :     ' + currentPid" type="warning" :closable="false" show-icon style="margin-bottom: 8px"></el-alert>
       </el-card>
     </el-col>
     <el-col :span="6">
       <el-card class="box-card" style="height: 240px;">
         <el-alert :title="'主机名 : ' + hostInfo.name" type="success" :closable="false" show-icon style="margin-bottom: 8px"></el-alert>
         <el-alert :title="'主机IP : ' + hostInfo.address" type="success" :closable="false" show-icon style="margin-bottom: 8px"></el-alert>
         <el-alert :title="'系统架构 : ' + osInfo.arch" type="success" :closable="false" show-icon style="margin-bottom: 8px"></el-alert>
         <el-alert :title="'系统名称 : ' + osInfo.name " type="success" :closable="false" show-icon style="margin-bottom: 8px"></el-alert>
         <el-alert :title="'系统版本号 : ' + osInfo.version  " type="success" :closable="false" show-icon style="margin-bottom: 10px"></el-alert>
       </el-card>
     </el-col>
     <el-col :span="8"  style="height: 240px;">
       <el-card class="box-card" style="height: 240px;">

       </el-card>
     </el-col>
     <el-col :span="24" style="margin-top: 15px;">
       <el-card class="box-card" style="height: 500px;">
         <div slot="header" class="clearfix">
           <span>Jvm内存情况</span>
         </div>
          <memory-line ref="memoryLine" />
       </el-card>
     </el-col>
     <el-col :span="24" style="margin-top: 15px;">
       <el-card class="box-card" style="height: 500px;">
         <el-card class="box-card" style="height: 500px;">
           <div slot="header" class="clearfix">
             <span>线程数量</span>
             <el-button style="float: right; padding: 3px 0" type="text" @click="clearJvmStatisticsBtn">清除数据</el-button>
           </div>
           <memory-line ref="threadLine" class-name="thread-chart"/>
         </el-card>
       </el-card>
     </el-col>

   </el-row>

  </div>
</template>

<script>
import {
  clearJvmDataApi,
  getHostInfoApi, getMemoryChartLineApi, getOSApi, getPIDApi,
  getRunTimeVersionApi,
  getRunTimeWorkspaceApi,
  getSystemStartedTimeApi,
  getSystemStartingTimeApi
} from "@/api/runtimeApi";
import MemoryLine from "@/views/dashboard/components/memoryLine.vue";



export default {
  name: 'Dashboard',
  components: {MemoryLine},
  data() {
    return {
      runtimeVersion:'',
      runTimeWorkspace:'',
      memoryChartLineMap:{},
      threadChartLineMap:{},
      systemStartingTime:'',
      systemStartedTime:'',
      currentPid:'',
      osInfo:{},
      hostInfo:{}
    }
  },
  created() {
    this.getRuntimeVersion()
    this.getChartLine()
  },
  methods: {
    getRuntimeVersion() {
      getRunTimeVersionApi().then(rsp => {
        this.runtimeVersion = rsp.data;
      })
      getRunTimeWorkspaceApi().then(rsp => {
        this.runTimeWorkspace = rsp.data;
      })
      getSystemStartingTimeApi().then(rsp => {
        this.systemStartingTime = rsp.data;
      })
      getSystemStartedTimeApi().then(rsp => {
        this.systemStartedTime = rsp.data;
      })
      getHostInfoApi().then(rsp => {
        this.hostInfo = rsp.data;
      })
      getPIDApi().then(rsp => {
        this.currentPid = rsp.data;
      })
      getOSApi().then(rsp => {
        this.osInfo = rsp.data;
      })

    },
    getChartLine(){
      getMemoryChartLineApi().then(rsp => {
        this.memoryChartLineMap = rsp.data.memory;
        this.threadChartLineMap = rsp.data.thread;
        this.$refs.memoryLine.refreshChart(this.memoryChartLineMap)
        this.$refs.threadLine.refreshChart(this.threadChartLineMap)
      })
    },
    clearJvmStatisticsBtn(){
      clearJvmDataApi().then(rsp => {
        this.$message.success(rsp.msg)
      })
    }
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
