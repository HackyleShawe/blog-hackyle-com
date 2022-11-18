import Layout from '@/layout'

/**
 * 评论管理的路由地址
 */
export default {
  path: '/comment',
  component: Layout,
  redirect: '/comment/released',
  name: 'Comment',
  meta: { title: 'Comment', icon: 'el-icon-s-comment' },
  children: [
    {
      path: 'released',
      name: 'Comment Released',
      component: () => import('@/views/comment/comment-released.vue'),
      meta: { title: 'Released', icon: 'el-icon-s-comment' }
    },
    {
      path: 'unchecked',
      name: 'Comment Unchecked',
      component: () => import('@/views/comment/comment-unchecked.vue'),
      meta: { title: 'Unchecked', icon: 'el-icon-s-comment' }
    },
    {
      path: 'deleted',
      //每个路由、子路由的name都应该唯一，否则会有一个警告
      //Duplicate named routes definition: { name: "Deleted", path: "/comment/delete" }
      name: 'Comment Deleted',
      component: () => import('@/views/comment/comment-deleted.vue'),
      meta: { title: 'Deleted', icon: 'el-icon-s-comment' }
    }
  ]
}