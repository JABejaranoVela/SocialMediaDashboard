import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:9090', // Cambia si tu backend está en otro puerto
        changeOrigin: true,
        secure: false
      }
    }
  }
})
