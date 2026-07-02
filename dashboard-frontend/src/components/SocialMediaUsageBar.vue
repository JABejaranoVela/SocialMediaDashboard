<template>
  <div class="chart-wrapper">
    <Bar :data="chartData" :options="chartOptions" v-if="chartData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Bar } from 'vue-chartjs'
import { Chart, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js'
Chart.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

const chartData = ref(null)
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false, // Para que el div controle el tamaño
  plugins: {
    legend: { display: false },
    title: {
      display: true,
      text: 'Uso medio diario de redes sociales según grupo de edad',
      align: 'center',
      font: {
        size: 14,
        weight: 'bold'
      },
      padding: {
        top: 10,
        bottom: 20
      }
    }
  },
  scales: {
    y: {
      title: {
        display: true,
        text: 'Minutos promedio por día',
        font: {
          size: 12,
          weight: 'bold'
        }
      }
    },
    x: {
      title: {
        display: true,
        text: 'Rango de edad',
        font: {
          size: 12,
          weight: 'bold'
        }
      }
    }
  }
}

onMounted(async () => {
  const resp = await fetch('/api/dashboard/social-media-usage/average-by-age')
  const data = await resp.json()
  chartData.value = {
    labels: data.labels,
    datasets: [{
      label: 'Minutos promedio/día',
      data: data.values,
      backgroundColor: '#339af0'
    }]
  }
})
</script>

<style scoped>
.chart-wrapper {
  position: relative;
  width: 100%;
  min-width: 0;
  margin: 0 auto;
  height: clamp(16rem, 75vw, 21rem);
}

/* Asegura que el canvas siempre ocupe todo el contenedor */
.chart-wrapper canvas {
  width: 100% !important;
  height: 100% !important;
  max-width: 100% !important;
  max-height: 100% !important;
  min-width: 0 !important;
}

@media (min-width: 768px) {
  .chart-wrapper { height: 21rem; }
}
</style>
