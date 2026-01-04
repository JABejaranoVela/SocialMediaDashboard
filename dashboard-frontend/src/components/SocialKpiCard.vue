<template>
  <div class="social-kpi-card" :style="{borderLeftColor: color}">
    <div class="social-kpi-label">{{ label }}</div>
    <div class="social-kpi-value">{{ value }}</div>
  </div>
</template>

<script setup>
defineProps(['label', 'value', 'color'])
</script>

<style scoped>
.social-kpi-card {
  border-left: 6px solid #999;
  background: #fff;
  padding: 1.1rem 1.5rem;
  border-radius: 10px;
  margin: 0.5rem 0;
  box-shadow: 0 2px 6px #0001;
  min-width: 120px;
  width: 100%;
  max-width: 330px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;
  transition: box-shadow 0.2s;
}
.social-kpi-label {
  font-weight: 500;
  color: #444;
  font-size: 1rem;
  word-break: break-word;
}
.social-kpi-value {
  font-size: 1.4rem;
  font-weight: bold;
  color: #333;
  margin-left: 1.5em;
  white-space: nowrap;
}

@media (max-width: 700px) {
  .social-kpi-card {
    padding: 0.7rem 0.9rem;
    min-width: 0;
    max-width: 100%;
  }
  .social-kpi-value {
    font-size: 1.13rem;
    margin-left: 0.7em;
  }
}
</style>
