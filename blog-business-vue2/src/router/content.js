import Layout from '@/layout'

/**
 * 内容管理（Content）的路由地址
 */
export default {
  path: '/content',
  component: Layout,
  redirect: '/content/feedback-message',
  name: 'Content',
  meta: { title: 'Content', icon: 'el-icon-reading' },
  children: [
    {
      path: 'feedback-message',
      name: 'Feedback Message',
      component: () => import('@/views/content/feedback-message'),
      meta: { title: 'Feedback Message', icon: 'el-icon-message' }
    },
    {
      path: 'friend-link',
      name: 'Friend Link',
      component: () => import('@/views/content/friend-link'),
      meta: { title: 'Friend Link', icon: 'el-icon-link' }
    },

  ]
}
