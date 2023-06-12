import Layout from '@/layout'

/**
 * 系统管理（System Manage）的路由地址
 */
export default {
  path: '/system',
  component: Layout,
  redirect: '/system/system-status',
  name: 'System',
  meta: { title: 'System', icon: 'el-icon-takeaway-box' },
  children: [
    {
      path: 'system-status',
      name: 'System Status',
      component: () => import('@/views/system/system-status'),
      meta: { title: 'System Status', icon: 'el-icon-monitor' }
    },

    {
      path: 'data-backup',
      name: 'Data Backup',
      component: () => import('@/views/system/data-backup'),
      meta: { title: 'Data Backup', icon: 'el-icon-sort' }
    },

    {
      path: 'article-visit-log',
      name: 'Article Log',
      component: () => import('@/views/system/article-access-logger'),
      meta: { title: 'Article Log', icon: 'el-icon-receiving' }
    },

  ]
}
