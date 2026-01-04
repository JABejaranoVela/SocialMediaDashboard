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
        size: 18,
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
          size: 15,
          weight: 'bold'
        }
      }
    },
    x: {
      title: {
        display: true,
        text: 'Rango de edad',
        font: {
          size: 15,
          weight: 'bold'
        }
      }
    }
  }
}

onMounted(async () => {
  const resp = await fetch('http://localhost:9090/api/dashboard/social-media-usage/average-by-age')
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
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  /* Mantén altura flexible */
  height: 350px;
}

@media (max-width: 900px) {
  .chart-wrapper {
    height: 270px;
    max-width: 100%;
    min-width: 0;
  }
}

@media (max-width: 600px) {
  .chart-wrapper {
    height: 210px;
    max-width: 100vw;
    padding: 0 0.2rem;
  }
}

/* Asegura que el canvas siempre ocupe todo el contenedor */
.chart-wrapper canvas {
  width: 100% !important;
  height: 100% !important;
  max-width: 100% !important;
  max-height: 100% !important;
  min-width: 0 !important;
  min-height: 0 !important;
  box-sizing: border-box;
}
</style>
