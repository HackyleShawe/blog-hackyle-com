<!--
  文章分类选择组件
-->
<template>
  <el-select v-model="selectedCategory" placeholder="Please Select Categories"
             style="width: 100%;" :multiple="true" default-first-option @change="selectedCategoryHandler">
    <el-option :label="item.name" :value="item.id" v-for="item in categoryListOptions" :key="item.id"></el-option>
  </el-select>
</template>

<script>
import categoryApi from "@/api/article/category";

export default {
  name: "SelectCategory",
  props: ['initCategories'],
  data() {
    return {
      //当前选择了那些文章分类
      selectedCategory: [],
      //从后端获取所有的文章分类以供选择
      categoryListOptions: [],
    }
  },
  emits: ['chooseCategory'], //自定义事件，向父组件传递数据
  created() {
    this.getCategories();
  },
  beforeUpdate() {
    if(this.initCategories.length > 0) {
      this.selectedCategory = this.initCategories
    }
  },
  methods: {
    //获取所有文章分类
    getCategories() {
      categoryApi.fetchAll().then(resp => {
        this.categoryListOptions = resp.data;
      }).catch(err => {
        console.log(err)
      })
    },
    selectedCategoryHandler() {
      //向父组件传递已经选择了的分类信息
      this.$emit('chooseCategory', this.selectedCategory);
    }
  }
}
</script>

<style scoped>

</style>