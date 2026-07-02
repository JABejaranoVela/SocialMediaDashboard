<template>
  <div class="app-shell">
    <AppSidebar v-if="!showHamburgerMenu" />

    <div class="main-content">
      <header v-if="showHamburgerMenu" class="header-bar">
        <img :src="logoUrl" alt="DatosRedesSociales" class="header-logo" />
        <button
          class="hamburger-btn"
          type="button"
          aria-label="Abrir menú de navegación"
          aria-controls="mobile-navigation"
          :aria-expanded="menuOpen"
          @click="menuOpen = !menuOpen"
        >
          <span :class="{ open: menuOpen }"></span>
          <span :class="{ open: menuOpen }"></span>
          <span :class="{ open: menuOpen }"></span>
        </button>

        <transition name="fade">
          <nav v-if="menuOpen" id="mobile-navigation" class="hamburger-menu" aria-label="Navegación principal">
            <ul>
              <li><router-link to="/" @click="menuOpen = false">Inicio</router-link></li>
              <li v-if="!isAuthenticated"><router-link to="/login" @click="menuOpen = false">Iniciar sesión</router-link></li>
              <li v-if="isAuthenticated"><router-link to="/register" @click="menuOpen = false">Ingresar registro</router-link></li>
              <li v-if="isAuthenticated"><router-link to="/edit" @click="menuOpen = false">Modificar registro</router-link></li>
              <li v-if="isAuthenticated">
                <a href="#" class="sidebar-link" @click.prevent="openLogoutModal">Cerrar sesión</a>
              </li>
            </ul>
          </nav>
        </transition>
      </header>

      <main class="page-content">
        <router-view />
      </main>
    </div>

    <div v-if="showLogoutModal" class="logout-modal-mask" @click.self="closeLogoutModal">
      <div class="logout-modal" role="dialog" aria-modal="true" aria-labelledby="logout-title">
        <h2 id="logout-title">¿Seguro que quieres cerrar sesión?</h2>
        <div class="logout-modal-btn-row">
          <button class="logout-modal-yes" type="button" @click="handleLogout">Sí</button>
          <button class="logout-modal-cancel" type="button" @click="closeLogoutModal">Cancelar</button>
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

const BREAKPOINT = 991
const isAuthenticated = ref(isLoggedIn())
const menuOpen = ref(false)
const showLogoutModal = ref(false)
const showHamburgerMenu = ref(window.innerWidth <= BREAKPOINT)

function onResize() {
  showHamburgerMenu.value = window.innerWidth <= BREAKPOINT
  if (!showHamburgerMenu.value) menuOpen.value = false
}

function onStorage() {
  isAuthenticated.value = isLoggedIn()
}

onMounted(() => {
  window.addEventListener('resize', onResize)
  window.addEventListener('storage', onStorage)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  window.removeEventListener('storage', onStorage)
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
.app-shell {
  display: flex;
  width: 100%;
  min-width: 0;
  min-height: 100vh;
  min-height: 100dvh;
}

.main-content {
  display: flex;
  flex: 1 1 auto;
  flex-direction: column;
  width: 100%;
  min-width: 0;
}

.page-content {
  flex: 1 1 auto;
  width: 100%;
  min-width: 0;
  padding: clamp(0.75rem, 3vw, 1.5rem);
}

.header-bar {
  position: sticky;
  top: 0;
  z-index: 1010;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  min-height: 4rem;
  padding: 0.4rem 0.75rem;
  color: #fff;
  background: #2a2d3e;
}

.header-logo {
  width: 3rem;
  height: 3rem;
  object-fit: contain;
  border-radius: 0.5rem;
  background: #fff;
}

.hamburger-btn {
  z-index: 1020;
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 3rem;
  min-height: 3rem;
  padding: 0.65rem;
  gap: 0.35rem;
  border: 0;
  border-radius: 0.5rem;
  color: #fff;
  background: transparent;
  cursor: pointer;
}

.hamburger-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.hamburger-btn span {
  display: block;
  width: 100%;
  height: 0.2rem;
  border-radius: 1rem;
  background: currentColor;
  transition: transform 0.22s, opacity 0.22s;
}

.hamburger-btn span.open:nth-child(1) { transform: translateY(0.55rem) rotate(45deg); }
.hamburger-btn span.open:nth-child(2) { opacity: 0; }
.hamburger-btn span.open:nth-child(3) { transform: translateY(-0.55rem) rotate(-45deg); }

.hamburger-menu {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  padding: 0.75rem;
  border-radius: 0 0 1rem 1rem;
  background: #23243a;
  box-shadow: 0 0.5rem 1.25rem rgba(0, 0, 0, 0.25);
}

.hamburger-menu ul {
  max-width: 42rem;
  margin: 0 auto;
  padding: 0;
  list-style: none;
}

.hamburger-menu li + li { border-top: 1px solid rgba(255, 255, 255, 0.1); }

.hamburger-menu a {
  display: flex;
  align-items: center;
  min-height: 3rem;
  padding: 0.65rem 0.75rem;
  border-radius: 0.45rem;
  color: #fff;
  font-weight: 500;
  text-decoration: none;
}

.hamburger-menu a:hover,
.hamburger-menu a.router-link-exact-active {
  color: #fff;
  background: #4653a3;
}

.fade-enter-active,
.fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from,
.fade-leave-to { opacity: 0; }

.logout-modal-mask {
  position: fixed;
  inset: 0;
  z-index: 99999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.35);
}

.logout-modal {
  width: min(100%, 26rem);
  padding: 1.5rem;
  border-radius: 0.875rem;
  color: #222;
  text-align: center;
  background: #fff;
  box-shadow: 0 0.5rem 2rem rgba(0, 0, 0, 0.18);
}

.logout-modal h2 {
  margin: 0 0 1.25rem;
  font-size: 1.15rem;
}

.logout-modal-btn-row {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.logout-modal-btn-row button {
  min-height: 2.75rem;
  padding: 0.65rem 1.25rem;
  border: 0;
  border-radius: 0.5rem;
  font-weight: 600;
  cursor: pointer;
}

.logout-modal-yes { color: #fff; background: #d93636; }
.logout-modal-yes:hover { background: #b30000; }
.logout-modal-cancel { color: #222; background: #e9ecef; }
.logout-modal-cancel:hover { background: #d5d8dc; }

@media (min-width: 576px) {
  .header-bar { padding-inline: 1.25rem; }
  .logout-modal-btn-row { flex-direction: row; justify-content: center; }
  .logout-modal-btn-row button { min-width: 8rem; }
}

@media (min-width: 992px) {
  .main-content { margin-left: 15.625rem; }
  .page-content { padding: 2rem; }
}

@media (min-width: 1400px) {
  .page-content { padding-inline: 2.5rem; }
}
</style>
