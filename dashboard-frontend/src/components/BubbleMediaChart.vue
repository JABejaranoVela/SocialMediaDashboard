<template>
  <div class="chart-wrapper">
    <Bubble :data="chartData" :options="chartOptions" v-if="chartData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Bubble } from 'vue-chartjs'
import { Chart, LinearScale, PointElement, Tooltip, Legend, Title } from 'chart.js'
import { getCategoryLabel, normalizeAggregatedSeries } from '../catalogs/respondentCatalogs.js'
Chart.register(LinearScale, PointElement, Tooltip, Legend, Title)

const chartData = ref(null)
const chartOptions = ref({})

onMounted(async () => {
  const resp = await fetch('/api/dashboard/platform/bubble-count')
  const data = await resp.json()
  const normalized = normalizeAggregatedSeries('platform', data.labels, data.counts)

  // Colores corporativos solo para las plataformas presentes
  const platformColors = {
    Discord: '#7289DA',
    Facebook: '#1877F3',
    Instagram: '#E4405F',
    Pinterest: '#E60023',
    Reddit: '#FF4500',
    Snapchat: '#FFFC00',
    TikTok: '#010101',
    Twitter: '#1DA1F2',
    YouTube: '#FF0000'
  }
  const backgroundColors = normalized.labels.map(p => platformColors[p] || '#888888')

  const datasets = [{
    label: 'Usuarios',
    data: normalized.labels.map((platform, idx) => ({
      x: idx + 1,
      y: 0.5,
      r: Math.sqrt(normalized.values[idx]) * 2.5
    })),
    backgroundColor: backgroundColors
  }]

  chartData.value = { datasets }
  chartOptions.value = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {
        type: 'linear',
        min: 0,
        max: normalized.labels.length + 1,
        ticks: {
          callback: function(value) {
            const valueAtTick = normalized.labels[value - 1]
            return valueAtTick ? getCategoryLabel('platform', valueAtTick) : ''
          },
          stepSize: 1,
          autoSkip: true,
          maxRotation: 45,
          minRotation: 0,
          font: { size: 10 }
        },
        title: { display: false }
      },
      y: { min: 0, max: 1, display: false }
    },
    plugins: {
      legend: { display: false },
      title: {
        display: true,
        text: 'Uso de plataformas por número de usuarios',
        align: 'center',
        font: { size: 14, weight: 'bold' },
        padding: { top: 10, bottom: 15 }
      },
      tooltip: {
        callbacks: {
          label: function(context) {
            const idx = context.dataIndex
            return `${getCategoryLabel('platform', normalized.labels[idx])}: ${normalized.values[idx]} usuarios`
          }
        }
      }
    }
  }
})
</script>

<style scoped>
.chart-wrapper {
  position: relative;
  width: 100%;
  min-width: 0;
  height: clamp(15rem, 70vw, 20rem);
  margin: 0 auto;
}

@media (min-width: 768px) {
  .chart-wrapper { height: 20rem; }
}
</style>
