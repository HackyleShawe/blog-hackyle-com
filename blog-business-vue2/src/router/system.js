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
      meta: { title: 'System Status', icon: 'el-icon-s-platform' }
    },

  ]
}
