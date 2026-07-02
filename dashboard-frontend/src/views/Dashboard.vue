<template>
  <div class="dashboard-wrapper">
    <section class="dashboard-row" aria-label="Indicadores generales">
      <div class="kpis-col">
        <KpiCard label="Encuestados" :value="kpis.respondentCount" color="#339af0" />
        <KpiCard label="Usan redes sociales" :value="kpis.percentUseSocial" color="#51cf66" />
        <KpiCard label="Prom. distracción mental" :value="kpis.avgDistraction" color="#ff922b" />
      </div>
      <div class="chart-card bar-col"><SocialMediaUsageBar /></div>
    </section>

    <section class="bubble-row" aria-label="Uso de plataformas">
      <div class="social-kpis-col">
        <div class="kpis-grid">
          <SocialKpiCard
            v-for="(platform, idx) in bubbleData.labels"
            :key="platform"
            :label="platform"
            :value="bubbleData.counts[idx]"
            :color="platformColors[platform] || '#999'"
          />
        </div>
      </div>
      <div class="chart-card bubble-col"><BubbleMediaChart /></div>
    </section>

    <section class="occupation-row" aria-label="Situación ocupacional">
      <div class="occupation-kpis-col">
        <div class="kpis-grid occupation-grid">
          <OccupationKpiCard
            v-for="(label, idx) in occupationData.labels"
            :key="label"
            :label="label"
            :value="occupationPercents[idx] + '%'"
            :color="occupationColors[idx]"
          />
        </div>
      </div>
      <div class="chart-card occupation-pie-col"><OccupationPieChart /></div>
    </section>

    <div class="section-info">
      <span class="font-semibold">Impacto psicológico</span><br />
      Estos indicadores reflejan distracción, preocupación, comparación social y otros efectos asociados al uso de redes. Escala de 1 (bajo) a 5 (alto).
    </div>
    <section class="bullet-row" aria-label="Indicadores de impacto psicológico">
      <BulletChart
        v-for="(label, idx) in bulletData.labels"
        :key="label"
        :title="label"
        :average="bulletData.averages[idx]"
        :max="5"
      />
    </section>

    <div class="section-info final">
      <span class="font-semibold">Resumen</span>:<br />
      Este panel permite visualizar patrones de uso y el impacto de las redes sociales en diferentes perfiles.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import KpiCard from '../components/KpiCard.vue'
import SocialMediaUsageBar from '../components/SocialMediaUsageBar.vue'
import BubbleMediaChart from '../components/BubbleMediaChart.vue'
import OccupationPieChart from '../components/OccupationPieChart.vue'
import SocialKpiCard from '../components/SocialKpiCard.vue'
import OccupationKpiCard from '../components/OccupationKpiCard.vue'
import BulletChart from '../components/BulletChart.vue'

const kpis = ref({ respondentCount: '...', percentUseSocial: '...', avgDistraction: '...' })
const bubbleData = ref({ labels: [], counts: [] })
const platformColors = {
  Facebook: '#1877F3', Instagram: '#E4405F', Twitter: '#1DA1F2', YouTube: '#FF0000',
  TikTok: '#000000', Discord: '#7289DA', Pinterest: '#E60023', Snapchat: '#FFFC00', Reddit: '#FF4500'
}
const occupationColors = ['#6495ED', '#40E0D0', '#4B0082', '#FFB300']
const occupationData = ref({ labels: [], counts: [] })
const occupationPercents = ref([])
const bulletData = ref({ labels: [], averages: [] })

onMounted(async () => {
  const [c1, c2, c3, bubbleResp, occupationResp, bulletResp] = await Promise.all([
    fetch('/api/dashboard/respondent/count').then(r => r.text()),
    fetch('/api/dashboard/social-media-users/percent').then(r => r.text()),
    fetch('/api/dashboard/distraction/average').then(r => r.text()),
    fetch('/api/dashboard/platform/bubble-count').then(r => r.json()),
    fetch('/api/demographics/dashboard/occupation-status-pie').then(r => r.json()),
    fetch('/api/dashboard/mental-health/bullet-averages').then(r => r.json())
  ])
  kpis.value.respondentCount = c1
  kpis.value.percentUseSocial = Number(c2).toFixed(1) + '%'
  kpis.value.avgDistraction = Number(c3).toFixed(2)
  bubbleData.value = bubbleResp
  occupationData.value = occupationResp
  const total = occupationResp.counts.reduce((a, b) => a + b, 0)
  occupationPercents.value = occupationResp.counts.map(c => ((c * 100 / total).toFixed(1)))
  bulletData.value = bulletResp
})
</script>

<style scoped>
.dashboard-wrapper {
  width: 100%;
  max-width: 90rem;
  min-width: 0;
  margin: 0 auto;
}

.dashboard-row,
.bubble-row,
.occupation-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 1rem;
  min-width: 0;
  margin-bottom: 2rem;
}

.kpis-col,
.kpis-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 0.75rem;
  min-width: 0;
}

.social-kpis-col,
.occupation-kpis-col,
.bar-col,
.bubble-col,
.occupation-pie-col {
  min-width: 0;
}

.chart-card {
  display: flex;
  align-items: stretch;
  justify-content: center;
  width: 100%;
  min-width: 0;
  padding: clamp(0.75rem, 2.5vw, 1.5rem);
  border-radius: 1rem;
  background: #fff;
  box-shadow: 0 0.125rem 0.75rem rgba(0, 0, 0, 0.07);
}

.bar-col { min-height: clamp(18rem, 82vw, 23rem); }
.bubble-col { min-height: clamp(17rem, 78vw, 22rem); }
.occupation-pie-col { min-height: clamp(20rem, 90vw, 25rem); }

.section-info {
  margin: 0 0 1.25rem;
  padding: 0.75rem;
  border-left: 0.25rem solid #339af0;
  border-radius: 0.5rem;
  color: #526173;
  background: #f8fafc;
  font-size: 0.94rem;
  font-weight: 500;
  line-height: 1.5;
}

.section-info.final {
  margin-top: 1.5rem;
  border-left-color: #868e96;
}

.font-semibold { font-weight: 700; }

.bullet-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 1rem;
  min-width: 0;
  margin-bottom: 1rem;
}

@media (min-width: 576px) {
  .kpis-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .section-info { padding: 0.9rem 1rem; font-size: 1rem; }
}

@media (min-width: 768px) {
  .dashboard-row,
  .bubble-row,
  .occupation-row { gap: 1.5rem; margin-bottom: 2.5rem; }

  .kpis-col { grid-template-columns: repeat(3, minmax(0, 1fr)); }
  .kpis-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); }
  .occupation-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .bullet-row { grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 1.25rem; }
  .bar-col { min-height: 23rem; }
  .bubble-col { min-height: 22rem; }
  .occupation-pie-col { min-height: 25rem; }
}

@media (min-width: 1200px) {
  .dashboard-row { grid-template-columns: minmax(14rem, 17rem) minmax(0, 1fr); align-items: stretch; }
  .dashboard-row .kpis-col { grid-template-columns: minmax(0, 1fr); align-content: stretch; }
  .bubble-row { grid-template-columns: minmax(0, 1fr) minmax(19rem, 23rem); align-items: stretch; }
  .bubble-row .social-kpis-col { order: 2; }
  .bubble-row .bubble-col { order: 1; }
  .bubble-row .kpis-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .occupation-row { grid-template-columns: minmax(19rem, 23rem) minmax(0, 1fr); align-items: stretch; }
  .bullet-row { grid-template-columns: repeat(3, minmax(0, 1fr)); }
}

@media (min-width: 1400px) {
  .dashboard-row,
  .bubble-row,
  .occupation-row { gap: 2rem; margin-bottom: 3rem; }
}
</style>
