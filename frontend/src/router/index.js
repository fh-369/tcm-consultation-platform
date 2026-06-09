import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('../layouts/PatientLayout.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/patient/HomeView.vue'),
        },
        {
          path: 'consultation/new',
          name: 'consultation-new',
          component: () => import('../views/patient/PatientPlaceholderView.vue'),
          meta: { title: '在线问诊', description: '患者问诊表单将在 Frontend Phase 3 接入真实接口。' },
        },
        {
          path: 'knowledge',
          name: 'knowledge',
          component: () => import('../views/patient/PatientPlaceholderView.vue'),
          meta: { title: '养生知识', description: '已发布知识文章将在后续内容阶段展示。' },
        },
        {
          path: 'recipes',
          name: 'recipes',
          component: () => import('../views/patient/PatientPlaceholderView.vue'),
          meta: { title: '药膳推荐', description: '药膳筛选和详情将在后续内容阶段实现。' },
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/patient/PatientPlaceholderView.vue'),
          meta: { title: '我的', description: '个人资料与我的问诊将在后续阶段实现。' },
        },
      ],
    },
    {
      path: '/admin',
      component: () => import('../layouts/AdminLayout.vue'),
      children: [
        {
          path: '',
          name: 'admin-dashboard',
          component: () => import('../views/admin/DashboardView.vue'),
        },
        {
          path: 'consultations',
          name: 'admin-consultations',
          component: () => import('../views/admin/AdminPlaceholderView.vue'),
          meta: { title: '问诊管理' },
        },
        {
          path: 'knowledge',
          name: 'admin-knowledge',
          component: () => import('../views/admin/AdminPlaceholderView.vue'),
          meta: { title: '知识文章' },
        },
        {
          path: 'recipes',
          name: 'admin-recipes',
          component: () => import('../views/admin/AdminPlaceholderView.vue'),
          meta: { title: '药膳管理' },
        },
        {
          path: 'export',
          name: 'admin-export',
          component: () => import('../views/admin/AdminPlaceholderView.vue'),
          meta: { title: '数据导出' },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../views/NotFoundView.vue'),
    },
  ],
})

export default router
