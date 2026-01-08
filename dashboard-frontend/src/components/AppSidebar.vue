<template>
  <aside class="sidebar">
    <!-- Imagen superior -->
    <div class="sidebar-logo-wrapper">
      <img :src="logoUrl" alt="Logo" class="sidebar-logo" />
    </div>
    <!-- Texto de bienvenida -->
    <div class="sidebar-welcome">
      Bienvenido a <br />
      <span class="app-name">DatosRedesSociales</span>
    </div>
    <hr class="sidebar-divider" />
    <!-- Opciones -->
    <ul class="sidebar-menu">
      <li>
        <router-link to="/" class="sidebar-link">Inicio</router-link>
      </li>
      <li v-if="!isAuthenticated">
        <router-link to="/login" class="sidebar-link">Iniciar sesión</router-link>
      </li>
      <li v-if="isAuthenticated">
        <router-link to="/register" class="sidebar-link">Ingresar registro</router-link>
      </li>
      <li v-if="isAuthenticated">
        <router-link to="/edit" class="sidebar-link">Modificar registro</router-link>
      </li>
      <li v-if="isAuthenticated">
        <a href="#" @click.prevent="openLogoutModal" class="sidebar-link">Cerrar sesión</a>
      </li>
    </ul>
    <!-- Modal de confirmación de logout -->
    <div v-if="showLogoutModal" class="logout-modal-mask">
      <div class="logout-modal">
        <h3>¿Seguro que quieres cerrar sesión?</h3>
        <div class="logout-modal-btn-row">
          <button class="logout-modal-yes" @click="handleLogout">Sí</button>
          <button class="logout-modal-cancel" @click="closeLogoutModal">Cancelar</button>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { isLoggedIn, logout } from '../utils/auth'
import logoUrl from '../assets/logoDashboard.png'


const isAuthenticated = ref(isLoggedIn())
const showLogoutModal = ref(false);

// Actualizar si hay cambios en localStorage (multi-pestaña)
onMounted(() => {
  window.addEventListener('storage', () => {
    isAuthenticated.value = isLoggedIn()
  })
})

function handleLogout() {
  logout();                // Elimina el token/session
  showLogoutModal.value = false; // Oculta el modal
  isAuthenticated.value = false; // (opcional) cambia estado login en el menú
  window.location.href = '/';    // Redirige a inicio
}

function openLogoutModal() {
  showLogoutModal.value = true;
}

function closeLogoutModal() {
  showLogoutModal.value = false;
}
</script>

<style scoped>
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  min-height: 100vh;
  height: auto;
  background: #2a2d3e;
  color: #fff;
  width: 250px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 2rem;
  overflow-y: auto;
  z-index: 999;
}

.sidebar-logo-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 1rem;
}

.sidebar-logo {
  width: 110px;
  /* ajusta según tu imagen */
  height: auto;
  border-radius: 16px;
  background: #fff;
}

.sidebar-welcome {
  font-size: 1rem;
  margin-bottom: 1.7rem;
  text-align: center;
  font-weight: 400;
}

.app-name {
  font-size: 1.13rem;
  font-weight: 600;
  color: #64b5f6;
  letter-spacing: 0.5px;
}

.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
  width: 100%;
}

.sidebar-menu li {
  width: 100%;
  margin-bottom: 0.9rem;
}

.sidebar-link {
  display: block;
  color: #fff;
  padding: 0.9rem 1.5rem;
  text-decoration: none;
  border-radius: 12px;
  font-size: 1rem;
  transition: background 0.15s, color 0.15s;
}

.sidebar-link:hover,
.router-link-exact-active {
  background: #4653a3;
  color: #fff;
  font-weight: 500;
}

.sidebar-divider {
  border: none;
  border-top: 1.5px solid #4653a3;
  margin: 0.1rem 0 1.7rem 0;
  width: 90%;
  align-self: center;
  opacity: 0.7;
}

.logout-modal-mask {
  position: fixed;
  z-index: 99999;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.19);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logout-modal {
  background: #fff;
  color: #222;
  border-radius: 14px;
  box-shadow: 0 8px 32px #0002;
  padding: 2rem 2.2rem 1.5rem 2.2rem;
  text-align: center;
  min-width: 250px;
}

.logout-modal h3 {
  font-size: 1.15rem;
  margin-bottom: 1.3rem;
  font-weight: 600;
}

.logout-modal-btn-row {
  display: flex;
  gap: 1.2rem;
  justify-content: center;
  margin-top: 0.2rem;
}

.logout-modal-yes {
  background: #e04444;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 0.7rem 2.2rem;
  font-size: 1.08rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.14s;
}

.logout-modal-yes:hover {
  background: #b30000;
}

.logout-modal-cancel {
  background: #eee;
  color: #222;
  border: none;
  border-radius: 8px;
  padding: 0.7rem 2.2rem;
  font-size: 1.08rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.14s;
}

.logout-modal-cancel:hover {
  background: #dadada;
}
</style>
