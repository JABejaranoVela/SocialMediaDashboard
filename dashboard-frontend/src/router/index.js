import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import RegisterView from '../views/RegisterView.vue'
import LoginView from '../views/LoginView.vue'
import EditView from '../views/EditView.vue'
import EditFormView from '../views/EditFormView.vue'

const routes = [
  { path: '/', name: 'Dashboard', component: Dashboard },
  { path: '/register', name: 'register', component: RegisterView },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/edit', name: 'edit', component: EditView },
  { path: '/edit/:id', name: 'edit-form', component: EditFormView}
]

export default createRouter({
  history: createWebHistory(),
  routes
})
