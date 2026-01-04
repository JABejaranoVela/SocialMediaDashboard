<template>
  <div class="login-container">
    <h2>Iniciar sesión</h2>
    <form @submit.prevent="login">
      <div class="form-group">
        <label for="username">Usuario</label>
        <input id="username" v-model="username" type="text" required />
      </div>
      <div class="form-group">
        <label for="password">Contraseña</label>
        <input id="password" v-model="password" type="password" required />
      </div>
      <div v-if="error" class="login-error">{{ error }}</div>
      <button type="submit">Entrar</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login as saveToken } from '../utils/auth'

const username = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()

const login = async () => {
  error.value = ''
  try {
    const res = await fetch('http://localhost:9090/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: username.value,
        password: password.value
      })
    })
    if (!res.ok) throw new Error('Usuario o contraseña incorrectos')
    const data = await res.json()
    saveToken(data.token)
    window.location.href = '/' // Refresca la página para que el sidebar detecte el login
  } catch (err) {
    error.value = err.message || 'Error de conexión'
  }
}
</script>

<style scoped>
.login-container {
  max-width: 350px;
  margin: 3rem auto;
  background: #fff;
  padding: 2.2rem 2rem 1.3rem 2rem;
  border-radius: 16px;
  box-shadow: 0 2px 20px #0002;
}
.login-container h2 {
  text-align: center;
  color: #4653a3;
  margin-bottom: 2rem;
  font-size: 1.3rem;
}
.form-group {
  margin-bottom: 1.2rem;
}
.form-group label {
  display: block;
  margin-bottom: 0.4rem;
  color: #2a2d3e;
  font-weight: 500;
}
.form-group input {
  width: 100%;
  padding: 0.6rem 0.7rem;
  border-radius: 8px;
  border: 1px solid #b2b6cc;
  background: #f3f4fa;
  font-size: 1rem;
  outline: none;
}
button[type="submit"] {
  width: 100%;
  background: #4653a3;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 0.8rem 0;
  font-size: 1.07rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
button[type="submit"]:hover {
  background: #334291;
}
.login-error {
  color: #c42d2d;
  background: #ffecec;
  border-radius: 8px;
  padding: 0.6rem 0.9rem;
  margin-bottom: 0.8rem;
  margin-top: 0.4rem;
  text-align: center;
  font-size: 1rem;
}
</style>
