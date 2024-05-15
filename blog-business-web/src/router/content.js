import Layout from '@/layout'

/**
 * 内容管理（Content）的路由地址
 */
export default {
  path: '/content',
  component: Layout,
  redirect: '/content/file',
  name: 'Content',
  meta: { title: 'Content', icon: 'el-icon-reading' },
  children: [
    {
      path: 'file',
      name: 'File',
      component: () => import('@/views/content/file'),
      meta: { title: 'File', icon: 'el-icon-files' }
    },
    {
      path: 'feedback-message',
      name: 'Feedback Message',
      component: () => import('@/views/content/feedback-message'),
      meta: { title: 'Feedback', icon: 'el-icon-message' }
    },
    {
      path: 'friend-link',
      name: 'Friend Link',
      component: () => import('@/views/content/friend-link'),
      meta: { title: 'Friend Link', icon: 'el-icon-link' }
    },

  ]
}
