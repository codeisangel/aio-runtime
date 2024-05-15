<template>
  <div class="app-container">
    <el-form  :model="queryArticle" :inline="true" class="demo-form-inline">
      <el-form-item label="帮助手册名称">
        <article-label-search-select v-model="queryArticle" @selectedLabel="selectedLabelEvent"></article-label-search-select>
      </el-form-item>

      <el-form-item>
<!--        <el-button type="primary" @click="addLabel">添加到条件</el-button>-->
        <el-button type="primary" @click="queryArticleList">查询</el-button>
        <el-button type="primary" @click="cleanLabel">清理标签</el-button>
      </el-form-item>
    </el-form>
    <el-tag v-for="tag in labels" :key="tag.value" closable  @close="handleClose(tag)">{{tag.label}}</el-tag>


    <el-table :data="articleTable" @row-click="viewArticle">
      <el-table-column prop="articleName" label="文章名称" width="280"></el-table-column>
      <el-table-column prop="remark" label="备注"></el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" top="5vh" :visible.sync="articleDialogVisible"  width="90%" >
      <article-view :article-id="articleId" v-if="articleDialogVisible"></article-view>
    </el-dialog>

  </div>
</template>

<script>
import MdEdit from '@/components/md/MdEdit'
import ArticleView from "./ArticleView";
import {getHelpArticle,cleanArticleLabel} from  '@/api/articleApi'
import ArticleLabelSearchSelect from '@/components/selects/ArticleLabelSearchSelect'
export default {
  components:{MdEdit,ArticleLabelSearchSelect,ArticleView},
  data() {
    return {
      queryArticle:{
        label:'',
        labelId:''
      },
      articleId:'',
      dialogTitle:'',
      articleTable:[],
      articleDialogVisible:false,
      labels:[]
    }
  },
  created() {

  },
  methods: {
    selectedLabelEvent(item){
      if (this.labels.indexOf(item.label) === -1) {
        this.labels.push({label:item.label,value:item.labelId});
      }
      this.queryArticleList();
    },
    cleanLabel(){
      cleanArticleLabel();
    },
    /**
     *
     */
    addLabel(){
      if (this.labels.indexOf(this.queryArticle.label) === -1) {
         this.labels.push({label:this.queryArticle.label,value:this.queryArticle.labelId});
      }
    },
    viewArticle(row, column){
      this.articleId = row.id;
      this.dialogTitle = row.articleName;
      this.articleDialogVisible = true
    },
    queryArticleList(){
      let labelIds = [];
      for (let label of this.labels) {
        labelIds.push(label.value);
      }
      let param = {'labelList':labelIds}

      console.log("差文章结果 ： "+JSON.stringify(param))
      getHelpArticle(param).then(rsp => {
        console.log("文章列表 ： "+JSON.stringify(rsp.data))
        this.articleTable = rsp.data;
      }).catch(err => {
        this.$message.error(err)
      })
    },
    handleClose(tab){
      this.labels.forEach((item,index,arr) => {
          if(item === tab){
            arr.splice(index,1)
          }
      });

    }


  }
}
</script>
