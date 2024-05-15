<template>
  <div class="app-container">
    <iframe :src="url" 	frameborder="0"  width="100%" height="100%" style="min-height: 600px"></iframe>
  </div>
</template>

<script>
import MdView from '@/components/md/MdView'
import {getArticleById} from  '@/api/articleApi'

export default {
  components: {MdView},
  props:{
     articleId:{
       type: String,
       default: ''
     }
  },
  data() {
    return {
      articleInfo:{
        content:'',
        articleName:''
      },
      url:''
    }
  },
  created() {

  },
  mounted() {
    this.queryArticle()
    this.url='/article/model/html?id='+this.articleId;
  },
  methods: {
    queryArticle(){
      getArticleById(this.articleId).then(rsp => {
        console.log("文件信息 ： "+JSON.stringify(rsp.data))
        this.articleInfo = rsp.data;
      }).catch(err => {
        this.$message.error(err)
      })
    }

  }
}
</script>
