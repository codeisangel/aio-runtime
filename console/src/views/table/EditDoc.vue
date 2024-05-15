<template>
  <div class="app-container">
    <el-form :model="docForm" label-width="120px">

      <el-form-item label="帮助手册名称">
        <el-input v-model="docForm.articleName" placeholder="请输入手册名称"></el-input>
      </el-form-item>

      <el-tag :key="tag.id" v-for="tag in lableAddArray" closable :disable-transitions="false"
              @close="handleClose(tag)">{{tag.label}}
      </el-tag>
      <el-input class="input-new-tag" v-if="inputVisible" v-model="labelValue" ref="saveTagInput" size="small"
                @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm"></el-input>
      <el-button v-else class="button-new-tag" size="small" @click="showInput">+添加标签</el-button>

      <div style="margin: 15px 0">
        <md-edit v-model="docForm"></md-edit>
      </div>

      <el-form-item label="备注">
        <el-input type="textarea" :rows="4" placeholder="请输入内容" v-model="docForm.remark">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveArticle">保存文章</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
  import MdEdit from '@/components/md/MdEdit'
  import {saveArticle, addArticleLabel,getArticleById,getArticleLabelList} from '@/api/articleApi'
  export default {
    components: {MdEdit},
    data() {
      return {
        articleId:'',
        docForm: {
          articleName:'',
          labelList: [],
          md: ''
        },
        lableAddArray: [],
        newLabelArray:[],
        removeLableArray:[],
        inputVisible: false,
        labelValue: ''
      }
    },
    created() {
      this.articleId = this.$route.query.articleId;
      console.log("文章ID ： "+this.articleId);
    },
    mounted() {
      this.queryArticle();
      this.queryArticleLable();
    },
    methods: {
      queryArticle(){
        getArticleById(this.articleId).then(rsp => {
          this.docForm = rsp.data;
          this.docForm.md = rsp.data.content;
        }).catch(err => {
          this.$message.error(err)
        })
      },
      queryArticleLable(){
        getArticleLabelList(this.articleId).then(rsp => {
          console.log("文件信息 ： "+JSON.stringify(rsp.data))
          this.lableAddArray = rsp.data;
        }).catch(err => {
          this.$message.error(err)
        })
      },
      saveArticle() {
        this.docForm.newLabelList= [];
        for (let item of this.newLabelArray) {
          this.docForm.newLabelList.push(item.id);
        }
        this.docForm.removeLabelList= [];
        for (let item of this.removeLableArray) {
          this.docForm.removeLabelList.push(item.id);
        }
        this.docForm.articleId = this.articleId;
        saveArticle(this.docForm).then(rsp => {
          console.log("更新文章结果 ： " + JSON.stringify(rsp.data))
          this.$message.success("更新文章成功");
        }).catch(err => {
          this.$message.error(err)
        })
      },
      showInput() {
        this.inputVisible = true;
        this.$nextTick(_ => {
          this.$refs.saveTagInput.$refs.input.focus();
        });
      },
      // 处理关闭标签
      handleClose(tab) {
        this.lableAddArray.forEach((item, index, arr) => {
          if (item.label === tab.label) {
            arr.splice(index,1);
            this.removeLableArray.push(item);
          }
        });
      },
      /**
       * 处理输入的标签
       */
      handleInputConfirm() {
        let inputLabel = this.labelValue;
        addArticleLabel(inputLabel).then(rsp => {
          let exsit = false;
          this.lableAddArray.forEach((item, index, arr) => {
            if (item.label === inputLabel) {
              exsit = true;
            }
          });

          if (exsit) {
            return
          } else {
            this.newLabelArray.push(rsp.data);
            this.lableAddArray.push(rsp.data)
          }

        }).catch(err => {
          this.$message.error(err)
        })
        this.inputVisible = false;
        this.labelValue = '';
      }
    }
  }
</script>
<style>
  .el-tag + .el-tag {
    margin-left: 10px;
  }

  .button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
  }

  .input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }
</style>
