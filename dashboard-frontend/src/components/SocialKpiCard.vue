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
  min-width: 0;
  height: 100%;
  padding: 1rem;
  border-radius: 10px;
  margin: 0;
  box-shadow: 0 2px 6px #0001;
  width: 100%;
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
  overflow-wrap: anywhere;
}
.social-kpi-value {
  font-size: clamp(1.13rem, 5vw, 1.4rem);
  font-weight: bold;
  color: #333;
  margin-left: 0.7em;
  white-space: nowrap;
}

@media (min-width: 768px) {
  .social-kpi-card { padding: 1.1rem 1.25rem; }
  .social-kpi-value { margin-left: 1rem; }
}
</style>
