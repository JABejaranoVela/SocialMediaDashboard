<template>
  <div class="dashboard-wrapper">
    <!-- Primera fila: KPIs + Bar chart -->
    <div class="dashboard-row">
      <div class="kpis-col">
        <KpiCard label="Encuestados" :value="kpis.respondentCount" color="#339af0" />
        <KpiCard label="Usan redes sociales" :value="kpis.percentUseSocial" color="#51cf66" />
        <KpiCard label="Prom. distracción mental" :value="kpis.avgDistraction" color="#ff922b" />
      </div>
      <div class="bar-col">
        <SocialMediaUsageBar />
      </div>
    </div>

    <!-- Segunda fila: KPIs plataformas + Bubble chart -->
    <div class="bubble-row">
      <div class="bubble-col">
        <BubbleMediaChart />
      </div>
      <div class="social-kpis-col">
        <div class="kpis-grid">
          <SocialKpiCard v-for="(platform, idx) in bubbleData.labels" :key="platform" :label="platform"
            :value="bubbleData.counts[idx]" :color="platformColors[platform] || '#999'" />
        </div>
      </div>
    </div>

    <!-- TERCERA FILA: KPIs de ocupación + Pie chart -->
    <div class="occupation-row">
      <div class="occupation-kpis-col">
        <div class="kpis-grid">
          <OccupationKpiCard v-for="(label, idx) in occupationData.labels" :key="label" :label="label"
            :value="occupationPercents[idx] + '%'" :color="occupationColors[idx]" />
        </div>
      </div>
      <div class="occupation-pie-col">
        <OccupationPieChart />
      </div>
    </div>

    <!-- CUARTA FILA: Bullet charts de bienestar -->
    <div class="section-info">
      <span class="font-semibold">Impacto psicológico</span><br>
      Estos indicadores reflejan distracción, preocupación, comparación social y otros efectos asociados al uso de redes. Escala de 1 (bajo) a 5 (alto).
    </div>
    <div class="bullet-row">
      <BulletChart v-for="(label, idx) in bulletData.labels" :key="label" :title="label"
        :average="bulletData.averages[idx]" :max="5" />
    </div>

    <!-- RESUMEN FINAL -->
    <div class="section-info final">
      <span class="font-semibold">Resumen</span>:<br>
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

// KPIs principales
const kpis = ref({
  respondentCount: '...',
  percentUseSocial: '...',
  avgDistraction: '...'
})

// KPIs de plataformas (burbujas)
const bubbleData = ref({ labels: [], counts: [] })
const platformColors = {
  Facebook: '#1877F3',
  Instagram: '#E4405F',
  Twitter: '#1DA1F2',
  YouTube: '#FF0000',
  TikTok: '#000000',
  Discord: '#7289DA',
  Pinterest: '#E60023',
  Snapchat: '#FFFC00',
  Reddit: '#FF4500'
}

// KPIs de ocupación (pie chart)
const occupationColors = [
  '#6495ED',  // University Student
  '#40E0D0',  // School Student
  '#4B0082',  // Salaried Employee
  '#FFB300',  // Unemployed
]
const occupationData = ref({ labels: [], counts: [] })
const occupationPercents = ref([])

// Estado bullet charts (NUEVO)
const bulletData = ref({ labels: [], averages: [] })

onMounted(async () => {
  // Peticiones a la API del backend
  const [c1, c2, c3, bubbleResp, occupationResp, bulletResp] = await Promise.all([
    fetch('/api/dashboard/respondent/count').then(r => r.text()),
    fetch('/api/dashboard/social-media-users/percent').then(r => r.text()),
    fetch('/api/dashboard/distraction/average').then(r => r.text()),
    fetch('/api/dashboard/platform/bubble-count').then(r => r.json()),
    fetch('/api/demographics/dashboard/occupation-status-pie').then(r => r.json()),
    fetch('/api/dashboard/mental-health/bullet-averages').then(r => r.json())
  ])
  // KPIs principales
  kpis.value.respondentCount = c1
  kpis.value.percentUseSocial = Number(c2).toFixed(1) + '%'
  kpis.value.avgDistraction = Number(c3).toFixed(2)
  // KPIs plataformas
  bubbleData.value = bubbleResp
  // KPIs ocupación
  occupationData.value = occupationResp
  const total = occupationResp.counts.reduce((a, b) => a + b, 0)
  occupationPercents.value = occupationResp.counts.map(c => ((c * 100 / total).toFixed(1)))
  // Bullet charts (NUEVO)
  bulletData.value = bulletResp
})
</script>

<style scoped>
/* =========================
   Layout general
   ========================= */
.dashboard-wrapper {
  padding: clamp(0.6rem, 1.8vw, 2rem) clamp(0.4rem, 1.6vw, 1.2rem);
  max-width: 1400px;
  margin: 0 auto;
  box-sizing: border-box;
  width: 100%;
}

/* Info text blocks */
.section-info {
  margin-bottom: 1.2rem;
  margin-top: -0.6rem;
  color: #606c80;
  font-size: 1.05rem;
  line-height: 1.4;
  background: #f8fafc;
  border-radius: 8px;
  padding: 0.7rem 1.2rem;
  border-left: 4px solid #339af0;
  font-weight: 500;
}
.section-info.secondary {
  border-left: 4px solid #ff922b;
}
.section-info.final {
  border-left: 4px solid #868e96;
  margin-top: 2rem;
}

.font-semibold {
  font-weight: 600;
}

/* =========================
   FILA 1: KPIs + Bar chart
   ========================= */
.dashboard-row {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  gap: 2rem;
  min-height: 430px;
}

.kpis-col {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  flex: 0 0 250px;
  max-width: 270px;
  height: 100%;
}

.bar-col {
  flex: 1 1 0;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px #0001;
  display: flex;
  align-items: center;
  justify-content: center;
  height: auto;
  min-height: 360px;
  padding: clamp(0.8rem, 2vw, 1.5rem);
  min-width: 0;
  overflow: visible;
}


/* =========================
  FILA 2: Bubble + KPIs redes
  ========================= */
.bubble-row {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  gap: 2rem;
  margin-bottom: 3rem;
  margin-top: 3rem;
}

.bubble-col {
  flex: 1 1 0;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px #0001;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 340px;
  padding: clamp(0.8rem, 2vw, 1.5rem);
  min-width: 0;
  overflow: hidden;
}

.social-kpis-col {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  min-width: 300px;
  margin-top: 2%;
}

/* Grid de KPIs (para SocialKpiCard y OccupationKpiCard) */
.kpis-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

/* =========================
   FILA 3: Ocupación + Pie chart
   ========================= */
.occupation-row {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  gap: 2rem;
  margin-bottom: 3rem;
  margin-top: 3rem;
}

.occupation-kpis-col {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  min-width: 320px;
}

.occupation-pie-col {
  flex: 1 1 0;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px #0001;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 360px;
  padding: 2rem 2.5rem;
  min-width: 0;      /* importante en flex */
  overflow: hidden;  /* evita desplazamiento horizontal del canvas */
}

/* =========================
   FILA 4: Bullet charts
   ========================= */
.bullet-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 2rem 1.5rem;
  margin: 2.5rem 0 1rem 0;
  justify-items: center;
}

/* =========================
   RESPONSIVE
   (IMPORTANTE: al final para que gane)
   ========================= */
@media (max-width: 1200px) {
  .dashboard-row,
  .bubble-row,
  .occupation-row {
    flex-direction: column;
    gap: 1.2rem;
    min-height: unset;
  }

  .kpis-col,
  .social-kpis-col,
  .occupation-kpis-col {
    max-width: 100%;
    width: 100%;
    flex: none;
    align-items: stretch;
    justify-content: center;
    min-width: 0;
    margin-bottom: 0; /* quito el 20% que te descoloca el layout */
  }

  .bar-col,
  .bubble-col,
  .occupation-pie-col {
    width: 100%;
    min-width: 0;
    padding: 1rem 0.6rem;
  }

  /* 3 columnas por debajo de 1200 */
  .kpis-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .bar-col {
    height: auto;
    min-height: clamp(260px, 45vw, 380px);
  }
  .bubble-col {
    order: 2;
  }
  .social-kpis-col {
    order: 1;
  }
}

@media (max-width: 700px) {
  .kpis-col,
  .social-kpis-col,
  .occupation-kpis-col {
    gap: 0.5rem;
    min-width: 0;
    max-width: 100%;
    width: 100%;
    padding: 0;
  }

  .section-info {
    font-size: 0.96rem;
    padding: 0.6rem 0.6rem;
  }

  /* vuelve a 2 columnas en pantallas pequeñas */
  .kpis-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .bar-col,
  .bubble-col,
  .occupation-pie-col {
    padding: 0.5rem 0.4rem;
    min-width: 0;
  }
}

@media (max-width: 500px) {
  .bullet-row {
    grid-template-columns: 1fr;
    gap: 1rem 0;
  }

  .section-info {
    font-size: 0.92rem;
  }

  .kpis-grid {
    grid-template-columns: 1fr;
  }
}
</style>

