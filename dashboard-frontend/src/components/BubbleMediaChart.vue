<template>
  <div class="chart-wrapper">
    <Bubble :data="chartData" :options="chartOptions" v-if="chartData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Bubble } from 'vue-chartjs'
import { Chart, LinearScale, PointElement, Tooltip, Legend, Title } from 'chart.js'
Chart.register(LinearScale, PointElement, Tooltip, Legend, Title)

const chartData = ref(null)
const chartOptions = ref({})

onMounted(async () => {
  const resp = await fetch('/api/dashboard/platform/bubble-count')
  const data = await resp.json()

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
  const backgroundColors = data.labels.map(p => platformColors[p] || '#888888')

  const datasets = [{
    label: 'Usuarios',
    data: data.labels.map((platform, idx) => ({
      x: idx + 1,
      y: 0.5,
      r: Math.sqrt(data.counts[idx]) * 2.5
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
        max: data.labels.length + 1,
        ticks: {
          callback: function(value) {
            return data.labels[value - 1] || ''
          },
          stepSize: 1
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
        font: { size: 16, weight: 'bold' },
        padding: { top: 10, bottom: 15 }
      },
      tooltip: {
        callbacks: {
          label: function(context) {
            const idx = context.dataIndex
            return `${data.labels[idx]}: ${data.counts[idx]} usuarios`
          }
        }
      }
    }
  }
})
</script>

<style scoped>
.chart-wrapper {
  width: 100%;
  max-width: 1000px;
  height: clamp(200px, 34vw, 360px);
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  padding: 0 clamp(0.2rem, 1.4vw, 0.8rem);
  min-height: 200px;
  margin-top: 10%;
}
</style>
