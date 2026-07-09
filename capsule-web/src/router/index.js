import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue')
    },
    {
      path: '/',
      component: () => import('@/layout/MainLayout.vue'),
      redirect: '/home',
      children: [
        {
          path: 'home',
          name: 'home',
          component: () => import('@/views/HomeView.vue')
        },
        {
          path: 'square',
          name: 'square',
          component: () => import('@/views/SquareView.vue')
        },
        {
          path: 'capsule/create',
          name: 'capsuleCreate',
          component: () => import('@/views/CapsuleCreateView.vue'),
          meta: { requireAuth: true }
        },
        {
          path: 'capsule/detail/:id',
          name: 'capsuleDetail',
          component: () => import('@/views/CapsuleDetailView.vue')
        },
        {
          path: 'my-capsules',
          name: 'myCapsules',
          component: () => import('@/views/MyCapsulesView.vue'),
          meta: { requireAuth: true }
        },
        {
          path: 'my-favorites',
          name: 'myFavorites',
          component: () => import('@/views/MyFavoritesView.vue'),
          meta: { requireAuth: true }
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/ProfileView.vue'),
          meta: { requireAuth: true }
        },
        {
          path: 'admin',
          name: 'admin',
          component: () => import('@/views/AdminView.vue'),
          meta: { requireAuth: true, requireAdmin: true }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

  if (to.meta.requireAuth && !token) {
    next('/login')
  } else if (to.meta.requireAdmin && userInfo.role !== 'ADMIN') {
    next('/home')
  } else {
    next()
  }
})

export default router
