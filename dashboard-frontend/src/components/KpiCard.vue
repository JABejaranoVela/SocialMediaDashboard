<template>
  <div class="kpi-card" :style="{borderColor: color}">
    <div class="kpi-value">{{ value }}</div>
    <div class="kpi-label">{{ label }}</div>
  </div>
</template>

<script setup>
defineProps(['label', 'value', 'color'])
</script>

<style scoped>
.kpi-card {
  border-left: 5px solid #339af0;
  background: #fff;
  padding: 1rem 1.5rem;
  border-radius: 8px;
  margin: 0.5rem;
  box-shadow: 0 2px 6px #0001;
}
.kpi-value {
  font-size: 2rem;
  font-weight: bold;
}
.kpi-label {
  color: #666;
}
</style>
