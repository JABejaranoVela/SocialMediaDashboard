<template>
  <div class="chart-wrapper">
    <Doughnut v-if="chartData" :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Doughnut } from 'vue-chartjs'
import { Chart, ArcElement, Tooltip, Legend, Title } from 'chart.js'
import { normalizeAggregatedSeries } from '../catalogs/respondentCatalogs.js'

Chart.register(ArcElement, Tooltip, Legend, Title)

const chartData = ref(null)
const occupationColors = {
  'University Student': '#6495ED', 'School Student': '#40E0D0',
  'Salaried Worker': '#4B0082', Retired: '#FFB300', Unemployed: '#8A2BE2'
}
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
  const normalized = normalizeAggregatedSeries('occupation', data.labels, data.counts)
  chartData.value = {
    labels: normalized.displayLabels,
    datasets: [{
      data: normalized.values,
      backgroundColor: normalized.labels.map(label => occupationColors[label] || '#888888')
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
