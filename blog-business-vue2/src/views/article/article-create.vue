<template>
  <article-editor :is-edit="false" />
</template>

<script>
import ArticleEditor from './components/ArticleEditor'

export default {
  name: 'CreateArticle',
  components: { ArticleEditor }
}
</script>
