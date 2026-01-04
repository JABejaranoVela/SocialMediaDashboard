<template>
  <div class="bullet-card">
    <div class="bullet-title">{{ title }}</div>
    <div class="bullet-value">{{ average.toFixed(2) }}</div>
    <div class="bullet-bar-wrapper">
      <div class="bullet-label left">1</div>
      <div class="bullet-bar-bg">
        <div
          class="bullet-bar-fg"
          :style="{ width: ((average - 1) / 4 * 100) + '%' }"
        ></div>
      </div>
      <div class="bullet-label right">5</div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  title: String,
  average: Number,
  max: {
    type: Number,
    default: 5
  }
})
</script>

<style scoped>
.bullet-card {
  background: #fff;
  border-radius: 13px;
  box-shadow: 0 2px 9px #0002;
  padding: 1.3rem 2rem 1.6rem 2rem;
  margin: 1.5rem 0;
  width: 100%;
  max-width: 420px;
}
.bullet-title {
  font-size: 1.05rem;
  font-weight: 500;
  margin-bottom: 0.8rem;
  text-align: center;
  color: #222;
}
.bullet-value {
  font-size: 2.6rem;
  font-weight: bold;
  color: #484e63;
  margin-bottom: 1.1rem;
  text-align: center;
}
.bullet-bar-wrapper {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  margin-top: 0.4rem;
}
.bullet-bar-bg {
  flex: 1;
  height: 38px;
  background: #e3e3e3;
  border-radius: 10px;
  position: relative;
  overflow: hidden;
  min-width: 140px;
}
.bullet-bar-fg {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  background: #657bffcc;
  border-radius: 10px 0 0 10px;
  transition: width 0.4s cubic-bezier(.65,.05,.36,1);
}
.bullet-label {
  font-size: 1.3rem;
  color: #888;
  width: 22px;
  text-align: center;
  user-select: none;
}
.bullet-label.left {
  margin-right: 4px;
}
.bullet-label.right {
  margin-left: 4px;
}
</style>