<template>
  <el-select
    v-model="value.labelId"
    filterable
    remote
    @change="labelSelected"
    reserve-keyword
    placeholder="请选择标签"
    :remote-method="remoteApplicationOptions"
    :loading="loading">
    <el-option
      v-for="item in labelOptions"
      :key="item.value"
      :label="item.label"
      :value="item.value">
    </el-option>
  </el-select>
</template>

<script>
  import { getArticleLabel } from '@/api/articleApi'
  export default {
    name:'ArticleLabelSearchSelect',
    props:{
      value:{
        type: Object,
        default: {}
      }
    },
    data() {
      return {
        labelName:'',
        labelOptions:[],
        loading:false
      }
    },
    mounted() {
      this.queryLabelOptions()
    },
    methods: {
      labelSelected(value){
         console.log("选择器确定 ： "+ value)
        for (let labelOption of this.labelOptions) {
          if (labelOption.value === value){
            this.value.label = labelOption.label;
          }
        }
        this.$emit("selectedLabel",this.value)
      },
      remoteApplicationOptions(query){
        this.labelName = query;
        this.queryLabelOptions();
      },
      queryLabelOptions(){
        getArticleLabel(this.labelName).then(rsp => {
          console.log("查询结果 " + JSON.stringify(rsp.data))
          this.labelOptions = rsp.data.options;
        }).catch(err => {
          this.$message.error(err);
        })
      }
    }
  }
</script>

<style lang="less" scoped>
</style>
