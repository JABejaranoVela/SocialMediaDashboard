<template>
  <div>
    <Doughnut :data="chartData" :options="chartOptions" v-if="chartData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Doughnut } from 'vue-chartjs'
import { Chart, ArcElement, Tooltip, Legend } from 'chart.js'
Chart.register(ArcElement, Tooltip, Legend)

const chartData = ref(null)

const occupationColors = [
  '#6495ED',  // University Student (azul)
  '#40E0D0',  // School Student (turquesa)
  '#4B0082',  // Salaried Employee (índigo)
  '#FFB300',  // Unemployed (amarillo)
  '#8A2BE2',  // Freelancer (violeta)
  '#2E8B57'   // Business Owner (verde)
]

const chartOptions = {
  plugins: {
    legend: { display: true, position: 'right' },
    title: { display: true, text: 'Ocupación de los entrevistados' }
  }
}

onMounted(async () => {
  const resp = await fetch('http://localhost:9090/api/demographics/dashboard/occupation-status-pie')
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
