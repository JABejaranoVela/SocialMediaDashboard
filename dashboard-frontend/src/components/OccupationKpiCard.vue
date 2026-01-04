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
  padding: 1.1rem 1.5rem;
  border-radius: 8px;
  margin: 0.3rem 0;
  box-shadow: 0 2px 6px #0001;
}
.occupation-kpi-value {
  font-size: 2rem;
  font-weight: bold;
}
.occupation-kpi-label {
  color: #666;
}
</style>
