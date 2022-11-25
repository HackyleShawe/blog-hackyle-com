import Layout from '@/layout'

/**
 * 配置中心的路由地址
 */
export default {
  path: '/config',
  component: Layout,
  redirect: '/config/friend-link',
  name: 'Configuration',
  meta: { title: 'Configuration', icon: 'el-icon-setting' },
  children: [
    {
      path: 'friend-link',
      name: 'Friend Link',
      component: () => import('@/views/configuration/friend-link'),
      meta: { title: 'Friend Link', icon: 'el-icon-link' }
    },

  ]
}
