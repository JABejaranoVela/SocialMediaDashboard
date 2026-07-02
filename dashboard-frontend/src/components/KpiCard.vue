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
  min-width: 0;
  padding: 1rem;
  border-radius: 8px;
  margin: 0;
  box-shadow: 0 2px 6px #0001;
}
.kpi-value {
  font-size: clamp(1.6rem, 6vw, 2rem);
  font-weight: bold;
}
.kpi-label {
  color: #666;
  overflow-wrap: anywhere;
}
</style>
