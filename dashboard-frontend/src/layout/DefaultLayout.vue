<template>
  <div class="d-flex" style="min-height:100vh;">
    <!-- Sidebar visible solo si pantalla > 991px -->
    <AppSidebar v-if="!showHamburgerMenu" />
    <div class="main-content" :class="{ 'with-header-menu': showHamburgerMenu }" style="flex:1; display:flex; flex-direction:column;">
      <!-- Header hamburguesa visible solo si pantalla <= 991px -->
      <div v-if="showHamburgerMenu" class="header-bar">
        <img :src="logoUrl" alt="Logo" class="header-logo" />
        <button class="hamburger-btn" @click="menuOpen = !menuOpen" aria-label="Abrir menú de navegación">
          <span :class="{open: menuOpen}"></span>
          <span :class="{open: menuOpen}"></span>
          <span :class="{open: menuOpen}"></span>
        </button>
        <transition name="fade">
          <nav v-if="menuOpen" class="hamburger-menu">
            <ul>
              <li><router-link @click="menuOpen=false" to="/">Inicio</router-link></li>
              <li v-if="!isAuthenticated"><router-link @click="menuOpen=false" to="/login">Iniciar sesión</router-link></li>
              <li v-if="isAuthenticated"><router-link @click="menuOpen=false" to="/register">Ingresar registro</router-link></li>
              <li v-if="isAuthenticated"><router-link @click="menuOpen=false" to="/edit">Modificar registro</router-link></li>
              <li v-if="isAuthenticated">
                <a href="#" @click.prevent="openLogoutModal" class="sidebar-link">Cerrar sesión</a>
              </li>
            </ul>
          </nav>
        </transition>
      </div>
      <!-- Contenido principal -->
      <main style="flex:1; padding: 2rem;">
        <router-view />
      </main>
    </div>

    <!-- Modal logout común -->
    <div v-if="showLogoutModal" class="logout-modal-mask">
      <div class="logout-modal">
        <h3>¿Seguro que quieres cerrar sesión?</h3>
        <div class="logout-modal-btn-row">
          <button class="logout-modal-yes" @click="handleLogout">Sí</button>
          <button class="logout-modal-cancel" @click="closeLogoutModal">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import AppSidebar from '../components/AppSidebar.vue'
import logoUrl from '../assets/logoDashboard.png'
import { isLoggedIn, logout } from '../utils/auth'

const isAuthenticated = ref(isLoggedIn())
const menuOpen = ref(false)
const showLogoutModal = ref(false)

// Breakpoint a 991px
const BREAKPOINT = 991
const showHamburgerMenu = ref(window.innerWidth <= BREAKPOINT)

function onResize() {
  showHamburgerMenu.value = window.innerWidth <= BREAKPOINT
  if (!showHamburgerMenu.value) menuOpen.value = false
}
onMounted(() => {
  window.addEventListener('resize', onResize)
  window.addEventListener('storage', () => {
    isAuthenticated.value = isLoggedIn()
  })
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
})

function openLogoutModal() {
  showLogoutModal.value = true
  menuOpen.value = false
}
function closeLogoutModal() {
  showLogoutModal.value = false
}
function handleLogout() {
  logout()
  showLogoutModal.value = false
  isAuthenticated.value = false
  window.location.href = '/'
}
</script>

<style scoped>
/* Sidebar margin en escritorio */
@media (min-width: 992px) {
  .main-content {
    margin-left: 250px;
  }
}
@media (max-width: 991px) {
  .main-content {
    margin-left: 0 !important;
  }
}

/* HEADER hamburguesa */
.header-bar {
  width: 100%;
  min-height: 60px;
  background: #2a2d3e;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 1010;
  padding: 0 1.2rem;
}
.header-logo {
  width: 50px;
  height: auto;
  border-radius: 8px;
  background: #fff;
  margin-right: 1rem;
}
.hamburger-btn {
  background: none;
  border: none;
  outline: none;
  cursor: pointer;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 6px;
  z-index: 1020;
}
.hamburger-btn span {
  display: block;
  width: 28px;
  height: 4px;
  background: #fff;
  border-radius: 4px;
  transition: all 0.22s;
}
.hamburger-btn span.open:nth-child(1) {
  transform: translateY(8px) rotate(45deg);
}
.hamburger-btn span.open:nth-child(2) {
  opacity: 0;
}
.hamburger-btn span.open:nth-child(3) {
  transform: translateY(-8px) rotate(-45deg);
}
/* Menú hamburguesa desplegable */
.hamburger-menu {
  position: absolute;
  top: 58px;
  left: 0;
  width: 100vw;
  background: #23243a;
  box-shadow: 0 6px 20px #0004;
  border-radius: 0 0 18px 18px;
  z-index: 1100;
  padding: 0.8rem 0 1rem 0;
}
.hamburger-menu ul {
  list-style: none;
  padding: 0 1.2rem;
  margin: 0;
}
.hamburger-menu li {
  border-bottom: 1px solid #34344a55;
  margin-bottom: 0.8rem;
  padding-bottom: 0.7rem;
}
.hamburger-menu li:last-child {
  border-bottom: none;
}
.hamburger-menu a,
.hamburger-menu .sidebar-link {
  color: #fff;
  text-decoration: none;
  font-size: 1.08rem;
  font-weight: 500;
  display: block;
  padding: 0.5rem 0.6rem;
  border-radius: 7px;
  transition: background 0.16s;
}
.hamburger-menu a:hover {
  background: #4653a3;
  color: #fff;
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
