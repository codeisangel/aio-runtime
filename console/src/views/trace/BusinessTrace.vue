<template>
  <div class="app-container">
    <div class="mermaid" ref="mermaidContent">
      graph LR
      A --- B
      B-->C[fa:fa-ban forbidden]
      B-->D(fa:fa-spinner);
    </div>

  </div>
</template>

<script>
import * as mermaid from 'mermaid';
export default {

  data() {
    return {
    }
  },
  created() {

  },

  mounted() {
    mermaid.initialize({ startOnLoad: true });
    this.renderMermaid();
  },
  methods: {
    renderMermaid() {
      mermaid.init(undefined, this.$refs.mermaidContent);
    },


  }
}
</script>
<style>

</style>
