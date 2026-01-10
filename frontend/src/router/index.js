import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/movies',
    name: 'Movies',
    component: () => import('@/views/Movies.vue')
  },
  {
    path: '/rankings',
    name: 'Rankings',
    component: () => import('@/views/Rankings.vue')
  },
  {
    path: '/movie/:id',
    name: 'MovieDetail',
    component: () => import('@/views/MovieDetail.vue')
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/Search.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/Settings.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('@/views/Notifications.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLogin.vue')
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/AdminDashboard.vue') },
      { path: 'movies', name: 'AdminMovies', component: () => import('@/views/admin/AdminMovies.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/AdminUsers.vue') },
      { path: 'reviews', name: 'AdminReviews', component: () => import('@/views/admin/AdminReviews.vue') },
      { path: 'ratings', name: 'AdminRatings', component: () => import('@/views/admin/AdminRatings.vue') },
      { path: 'messages', name: 'AdminMessages', component: () => import('@/views/admin/AdminMessages.vue') },
      { path: 'announcements', name: 'AdminAnnouncements', component: () => import('@/views/admin/AdminAnnouncements.vue') }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()
  const token = userStore.token || localStorage.getItem('token')
  const needAuth = to.meta.requiresAuth
  const needAdmin = to.meta.requiresAdmin

  if (needAdmin) {
    if (!token) {
      return { name: 'AdminLogin', query: { redirect: to.fullPath } }
    }
    if (!userStore.user) {
      await userStore.fetchUser()
    }
    if (userStore.user?.role !== 'ADMIN') {
      return { name: 'AdminLogin' }
    }
    return true
  }

  if (needAuth) {
    if (!token) {
      return { name: 'Login', query: { redirect: to.fullPath } }
    }
    if (!userStore.user) {
      await userStore.fetchUser()
    }
  }

  return true
})

export default router
