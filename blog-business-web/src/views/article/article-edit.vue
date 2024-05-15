<template>
  <article-editor :is-edit="true" />
</template>

<script>
import ArticleEditor from './components/ArticleEditor'

export default {
  name: 'ArticleEdit',
  components: { ArticleEditor }
}
</script>