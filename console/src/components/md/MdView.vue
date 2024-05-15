<template>
  <div class="markdown">
    <div class="container">
<!--      <mavon-editor v-model="content" ref="md" @imgAdd="$imgAdd" @change="change" style="min-height: 600px"/>-->
      <mavon-editor v-model="value" ref="md" @change="change" v-show="false"/>
      <div v-html="html"></div>
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
        type: String,
        default: ''
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
      // 所有操作都会被解析重新渲染
      change(value, render){
        // render 为 markdown 解析后的结果[html]
        this.html = render;

      }
    }
  }
</script>
