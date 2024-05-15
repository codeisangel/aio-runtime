<template>
    <div>
      <pre v-highlight-a>
         <code :class="type">{{watch_data}}</code>
      </pre>
    </div>
</template>

<script>
  export default {
    name:"CodePreview",
    props:{
      type: {
        type: String,
        default: 'java'
      }
    },
    data() {
      return {
        watch_data:{
          test:'sdfasdfasdfasdfasdfasf'
        }
      }
    },
    methods: {

    }
  }
</script>

<style scoped>

</style>

