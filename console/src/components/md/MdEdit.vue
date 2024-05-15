<template>
  <div class="markdown">
    <div class="container">
<!--      <mavon-editor v-model="content" ref="md" @imgAdd="$imgAdd" @change="change" style="min-height: 600px"/>-->
      <mavon-editor v-model="value.md" ref="md" @change="change" style="min-height: 600px"/>
    </div>
  </div>
</template>

<script>
  import { mavonEditor } from 'mavon-editor'
  import 'mavon-editor/dist/css/index.css'
  export default {
    component:{mavonEditor},
    props: {
      value: {
        type: Object,
        default: {}
      }
    },
    components: {
      mavonEditor,
    },
    data() {
      return {
        content:'',
        html:'',
        configs: {}
      }
    },
    methods: {
      // 将图片上传到服务器，返回地址替换到md中
      $imgAdd(pos, $file){
        let formdata = new FormData();

        this.$upload.post('/上传接口地址', formdata).then(res => {
          console.log(res.data);
          this.$refs.md.$img2Url(pos, res.data);
        }).catch(err => {
          console.log(err)
        })
      },
      // 所有操作都会被解析重新渲染
      change(value, render){
        // render 为 markdown 解析后的结果[html]
        this.html = render;
        //this.value.md = value;
      },
      // 提交
      submit(){
        console.log(this.value.md);
        console.log(this.content);
        console.log(this.html);
        this.$message.success('提交成功，已打印至控制台！');
      }
    },
    mounted() {

    }
  }
</script>
