import Layout from '@/layout'
/**
 * 文章管理的路由地址
 */
export default {
  path: '/article',
  component: Layout,
  redirect: '/article/released',
  name: 'Article',
  meta: { title: 'Article', icon: 'el-icon-document' },
  children: [
    {
      path: 'released',
      name: 'Article Released',
      component: () => import('@/views/article/article-released.vue'),
      meta: { title: 'Released', icon: 'el-icon-tickets'}
    },
    {
      path: 'write',
      name: 'Article Write',
      component: () => import('@/views/article/article-create.vue'),
      meta: { title: 'Write', icon: 'el-icon-document-add' }
    },
    {
       path: 'edit/:id',  //'edit/:id(\\d+)'：限制ID为数字
      component: () => import('@/views/article/article-edit.vue'),
      name: 'Article Edit',
      //meta: { title: 'Edit', noCache: true, activeMenu: '/article/list', icon: 'el-icon-edit'},
      meta: { title: 'Edit', icon: 'el-icon-edit'},
      hidden: true
    },

    {
      path: 'draft',
      name: 'Article Draft',
      component: () => import('@/views/article/article-draft.vue'),
      meta: { title: 'Draft', icon: 'el-icon-tickets'}
    },
    {
      path: 'deleted',
      name: 'Article Deleted',
      component: () => import('@/views/article/article-deleted.vue'),
      meta: { title: 'Deleted', icon: 'el-icon-tickets'}
    },

    {
      path: 'author',
      name: 'Author',
      component: () => import('@/views/article/author.vue'),
      meta: { title: 'Author', icon: 'el-icon-collection' }
    },

    {
      path: 'category',
      name: 'Article Category',
      component: () => import('@/views/article/category.vue'),
      meta: { title: 'Category', icon: 'el-icon-collection' }
    },
    {
      path: 'tag',
      name: 'Article Tag',
      component: () => import('@/views/article/tag.vue'),
      meta: { title: 'Tag', icon: 'el-icon-collection-tag' }
    },
  ]
}
