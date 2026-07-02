<template>
  <aside class="sidebar" aria-label="Navegación principal">
    <div class="sidebar-logo-wrapper">
      <img :src="logoUrl" alt="DatosRedesSociales" class="sidebar-logo" />
    </div>
    <div class="sidebar-welcome">
      Bienvenido a <br />
      <span class="app-name">DatosRedesSociales</span>
    </div>
    <hr class="sidebar-divider" />
    <ul class="sidebar-menu">
      <li><router-link to="/" class="sidebar-link">Inicio</router-link></li>
      <li v-if="!isAuthenticated"><router-link to="/login" class="sidebar-link">Iniciar sesión</router-link></li>
      <li v-if="isAuthenticated"><router-link to="/register" class="sidebar-link">Ingresar registro</router-link></li>
      <li v-if="isAuthenticated"><router-link to="/edit" class="sidebar-link">Modificar registro</router-link></li>
      <li v-if="isAuthenticated"><a href="#" class="sidebar-link" @click.prevent="openLogoutModal">Cerrar sesión</a></li>
    </ul>

    <div v-if="showLogoutModal" class="logout-modal-mask" @click.self="closeLogoutModal">
      <div class="logout-modal" role="dialog" aria-modal="true" aria-labelledby="sidebar-logout-title">
        <h2 id="sidebar-logout-title">¿Seguro que quieres cerrar sesión?</h2>
        <div class="logout-modal-btn-row">
          <button class="logout-modal-yes" type="button" @click="handleLogout">Sí</button>
          <button class="logout-modal-cancel" type="button" @click="closeLogoutModal">Cancelar</button>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { isLoggedIn, logout } from '../utils/auth'
import logoUrl from '../assets/logoDashboard.png'

const isAuthenticated = ref(isLoggedIn())
const showLogoutModal = ref(false)

function onStorage() {
  isAuthenticated.value = isLoggedIn()
}

onMounted(() => window.addEventListener('storage', onStorage))
onBeforeUnmount(() => window.removeEventListener('storage', onStorage))

function handleLogout() {
  logout()
  showLogoutModal.value = false
  isAuthenticated.value = false
  window.location.href = '/'
}

function openLogoutModal() {
  showLogoutModal.value = true
}

function closeLogoutModal() {
  showLogoutModal.value = false
}
</script>

<style scoped>
.sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  z-index: 999;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 15.625rem;
  min-height: 100vh;
  min-height: 100dvh;
  padding-top: 2rem;
  overflow-y: auto;
  color: #fff;
  background: #2a2d3e;
}

.sidebar-logo-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  margin-bottom: 1rem;
}

.sidebar-logo {
  width: 6.875rem;
  height: auto;
  border-radius: 1rem;
  background: #fff;
}

.sidebar-welcome {
  margin-bottom: 1.7rem;
  font-size: 1rem;
  font-weight: 400;
  text-align: center;
}

.app-name {
  color: #64b5f6;
  font-size: 1.13rem;
  font-weight: 600;
  letter-spacing: 0.03rem;
}

.sidebar-divider {
  width: 90%;
  margin: 0.1rem 0 1.7rem;
  border: 0;
  border-top: 0.1rem solid #4653a3;
  opacity: 0.7;
}

.sidebar-menu {
  width: 100%;
  margin: 0;
  padding: 0;
  list-style: none;
}

.sidebar-menu li { width: 100%; margin-bottom: 0.65rem; }

.sidebar-link {
  display: flex;
  align-items: center;
  min-height: 2.75rem;
  margin-inline: 0.75rem;
  padding: 0.7rem 1rem;
  border-radius: 0.75rem;
  color: #fff;
  font-size: 1rem;
  text-decoration: none;
  transition: background 0.15s, color 0.15s;
}

.sidebar-link:hover,
.sidebar-link.router-link-exact-active {
  color: #fff;
  font-weight: 500;
  background: #4653a3;
}

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

.logout-modal h2 { margin: 0 0 1.25rem; font-size: 1.15rem; }

.logout-modal-btn-row {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.75rem;
}

.logout-modal-btn-row button {
  min-width: 8rem;
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
</style>
