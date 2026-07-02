<template>
  <div class="chart-wrapper">
    <Doughnut v-if="chartData" :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Doughnut } from 'vue-chartjs'
import { Chart, ArcElement, Tooltip, Legend, Title } from 'chart.js'

Chart.register(ArcElement, Tooltip, Legend, Title)

const chartData = ref(null)
const occupationColors = ['#6495ED', '#40E0D0', '#4B0082', '#FFB300', '#8A2BE2', '#2E8B57']
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      position: 'bottom',
      labels: { boxWidth: 14, padding: 12, usePointStyle: true }
    },
    title: {
      display: true,
      text: 'Ocupación de los entrevistados',
      font: { size: 15, weight: 'bold' },
      padding: { bottom: 12 }
    }
  }
}

onMounted(async () => {
  const resp = await fetch('/api/demographics/dashboard/occupation-status-pie')
  const data = await resp.json()
  chartData.value = {
    labels: data.labels,
    datasets: [{
      data: data.counts,
      backgroundColor: occupationColors.slice(0, data.labels.length)
    }]
  }
})
</script>

<style scoped>
.chart-wrapper {
  position: relative;
  width: 100%;
  min-width: 0;
  height: clamp(18rem, 78vw, 23rem);
}

@media (min-width: 768px) {
  .chart-wrapper { height: 23rem; }
}
</style>
