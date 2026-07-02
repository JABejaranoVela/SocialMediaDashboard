<template>
  <div class="occupation-kpi-card" :style="{ borderColor: color }">
    <div class="occupation-kpi-value">{{ value }}</div>
    <div class="occupation-kpi-label">{{ label }}</div>
  </div>
</template>

<script setup>
defineProps(['label', 'value', 'color'])
</script>

<style scoped>
.occupation-kpi-card {
  border-left: 6px solid;
  background: #fff;
  min-width: 0;
  height: 100%;
  padding: 1rem;
  border-radius: 8px;
  margin: 0;
  box-shadow: 0 2px 6px #0001;
}
.occupation-kpi-value {
  font-size: clamp(1.5rem, 6vw, 2rem);
  font-weight: bold;
}
.occupation-kpi-label {
  color: #666;
  overflow-wrap: anywhere;
}
</style>
