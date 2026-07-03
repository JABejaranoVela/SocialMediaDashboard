<template>
  <div ref="chartWrapper" class="chart-wrapper">
    <Bubble v-if="chartData" :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { Bubble } from 'vue-chartjs'
import { Chart, LinearScale, PointElement, Tooltip, Legend, Title } from 'chart.js'
import { getCategoryLabel, normalizeAggregatedSeries } from '../catalogs/respondentCatalogs.js'

Chart.register(LinearScale, PointElement, Tooltip, Legend, Title)

const chartData = ref(null)
const chartOptions = ref({})
const chartWrapper = ref(null)
let resizeObserver
let normalized = { labels: [], values: [] }

const platformColors = {
  Discord: '#7289DA', Facebook: '#1877F3', Instagram: '#E4405F',
  Pinterest: '#E60023', Reddit: '#FF4500', Snapchat: '#FFFC00',
  TikTok: '#010101', Twitter: '#1DA1F2', YouTube: '#FF0000'
}

const mobileLabels = {
  Facebook: 'FB', Twitter: 'TW', Instagram: 'IG', YouTube: 'YT',
  Discord: 'DIS', Reddit: 'RED', Pinterest: 'PIN', TikTok: 'TT', Snapchat: 'SC'
}

function buildChart(width) {
  if (!normalized.labels.length || width <= 0) return

  const isMobile = width < 576
  const isTablet = width < 992
  const itemCount = normalized.labels.length
  const maxValue = Math.max(...normalized.values, 1)
  const radiusLimit = isMobile
    ? Math.min(14, Math.max(8, width / (itemCount * 2.4)))
    : isTablet
      ? Math.min(22, Math.max(12, width / (itemCount * 2)))
      : Math.min(38, Math.max(18, width / (itemCount * 1.7)))

  chartData.value = {
    datasets: [{
      label: 'Usuarios',
      data: normalized.labels.map((platform, index) => ({
        x: index + 1,
        y: 0.5,
        r: Math.max(4, radiusLimit * Math.sqrt(normalized.values[index] / maxValue))
      })),
      backgroundColor: normalized.labels.map(platform => platformColors[platform] || '#888888')
    }]
  }

  chartOptions.value = {
    responsive: true,
    maintainAspectRatio: false,
    animation: false,
    layout: { padding: { top: isMobile ? 4 : 8, right: 8, bottom: 4, left: 8 } },
    scales: {
      x: {
        type: 'linear',
        min: 0,
        max: itemCount + 1,
        ticks: {
          callback(value) {
            const platform = normalized.labels[value - 1]
            if (!platform) return ''
            return isMobile ? mobileLabels[platform] : getCategoryLabel('platform', platform)
          },
          stepSize: 1,
          autoSkip: false,
          maxRotation: 0,
          minRotation: 0,
          padding: isMobile ? 2 : 5,
          font: { size: isMobile ? 9 : isTablet ? 9 : 10 }
        },
        grid: { display: !isMobile },
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
        font: { size: isMobile ? 12 : 14, weight: 'bold' },
        padding: { top: 4, bottom: isMobile ? 8 : 12 }
      },
      tooltip: {
        callbacks: {
          label(context) {
            const index = context.dataIndex
            return `${getCategoryLabel('platform', normalized.labels[index])}: ${normalized.values[index]} usuarios`
          }
        }
      }
    }
  }
}

onMounted(async () => {
  const response = await fetch('/api/dashboard/platform/bubble-count')
  const data = await response.json()
  normalized = normalizeAggregatedSeries('platform', data.labels, data.counts)
  await nextTick()
  buildChart(chartWrapper.value?.clientWidth ?? 0)
  resizeObserver = new ResizeObserver(entries => buildChart(entries[0].contentRect.width))
  resizeObserver.observe(chartWrapper.value)
})

onBeforeUnmount(() => resizeObserver?.disconnect())
</script>

<style scoped>
.chart-wrapper {
  position: relative;
  width: 100%;
  min-width: 0;
  height: clamp(18rem, 75vw, 21rem);
  margin: 0 auto;
}

@media (min-width: 768px) {
  .chart-wrapper { height: 21.5rem; }
}

@media (min-width: 1200px) {
  .chart-wrapper { height: 22rem; }
}
</style>
