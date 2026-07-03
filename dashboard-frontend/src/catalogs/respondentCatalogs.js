const option = (value, label) => Object.freeze({ value, label })

export const genderOptions = Object.freeze([
  option('Male', 'Masculino'),
  option('Female', 'Femenino')
])

export const relationshipOptions = Object.freeze([
  option('Single', 'Soltero/a'),
  option('In a relationship', 'En una relación'),
  option('Married', 'Casado/a'),
  option('Divorced', 'Divorciado/a')
])

export const occupationOptions = Object.freeze([
  option('University Student', 'Estudiante universitario'),
  option('School Student', 'Estudiante escolar'),
  option('Salaried Worker', 'Trabajador asalariado'),
  option('Retired', 'Jubilado'),
  option('Unemployed', 'Desempleado')
])

export const socialMediaUseOptions = Object.freeze([
  option('Yes', 'Sí'),
  option('No', 'No')
])

// Exact values used by db/dashboard.sql.
export const dailyTimeOptions = Object.freeze([
  option('Less than an Hour', 'Menos de 1 hora'),
  option('Between 1 and 2 hours', 'Entre 1 y 2 horas'),
  option('Between 2 and 3 hours', 'Entre 2 y 3 horas'),
  option('Between 3 and 4 hours', 'Entre 3 y 4 horas'),
  option('Between 4 and 5 hours', 'Entre 4 y 5 horas'),
  option('More than 5 hours', 'Más de 5 horas')
])

export const platformOptions = Object.freeze([
  'Facebook', 'Twitter', 'Instagram', 'YouTube', 'Discord',
  'Reddit', 'Pinterest', 'TikTok', 'Snapchat'
].map(value => option(value, value)))

// Affiliation combinations are canonical values from db/dashboard.sql.
export const organizationOptions = Object.freeze([
  'Company',
  'Company, Private',
  'Goverment',
  'N/A',
  'Private',
  'School',
  'School, Company',
  'School, N/A',
  'School, Private',
  'School, University',
  'School, University, Private',
  'University',
  'University, Company',
  'University, Company, Goverment',
  'University, Company, Private',
  'University, Goverment',
  'University, Goverment, Private',
  'University, N/A',
  'University, Private'
].map(value => option(value, value
  .replaceAll('Goverment', 'Gobierno')
  .replaceAll('Company', 'Empresa')
  .replaceAll('Private', 'Privada')
  .replaceAll('School', 'Colegio')
  .replaceAll('University', 'Universidad'))))

const catalogs = Object.freeze({
  gender: genderOptions,
  relationship: relationshipOptions,
  occupation: occupationOptions,
  socialMediaUse: socialMediaUseOptions,
  dailyTime: dailyTimeOptions,
  platform: platformOptions,
  organization: organizationOptions
})

const historicalValues = Object.freeze({
  gender: Object.freeze({ Masculino: 'Male', Femenino: 'Female' }),
  relationship: Object.freeze({
    Soltero: 'Single',
    Casado: 'Married',
    Divorciado: 'Divorced',
    'En relación': 'In a relationship'
  }),
  occupation: Object.freeze({
    'Estudiante universitario': 'University Student',
    'Estudiante escolar': 'School Student',
    Trabajador: 'Salaried Worker',
    'Trabajador asalariado': 'Salaried Worker',
    Jubilado: 'Retired',
    Desempleado: 'Unemployed'
  }),
  socialMediaUse: Object.freeze({ Sí: 'Yes', Si: 'Yes' }),
  dailyTime: Object.freeze({
    'Menos de 1 hora': 'Less than an Hour',
    'Entre 1 y 2 horas': 'Between 1 and 2 hours',
    'Entre 2 y 3 horas': 'Between 2 and 3 hours',
    'Entre 3 y 4 horas': 'Between 3 and 4 hours',
    'Entre 4 y 5 horas': 'Between 4 and 5 hours',
    'Más de 5 horas': 'More than 5 hours'
  }),
  organization: Object.freeze({
    Empresa: 'Company',
    Gobierno: 'Goverment',
    Colegio: 'School',
    Universidad: 'University',
    Privada: 'Private'
  })
})

export const isInvalidCategoryValue = value =>
  typeof value !== 'string' || value.trim() === '' || value.trim().toLowerCase() === 'string'

export function normalizeHistoricalValue(category, value) {
  if (isInvalidCategoryValue(value)) return null
  const trimmed = value.trim()
  return historicalValues[category]?.[trimmed] ?? trimmed
}

export function getCategoryLabel(category, value) {
  const canonical = normalizeHistoricalValue(category, value)
  if (canonical === null) return ''
  return catalogs[category]?.find(item => item.value === canonical)?.label ?? canonical
}

export function normalizeAggregatedSeries(category, labels = [], values = []) {
  const totals = new Map()
  labels.forEach((label, index) => {
    const canonical = normalizeHistoricalValue(category, label)
    if (canonical === null) return
    const numericValue = Number(values[index])
    if (!Number.isFinite(numericValue)) return
    totals.set(canonical, (totals.get(canonical) ?? 0) + numericValue)
  })

  const normalizedLabels = [...totals.keys()]
  return {
    labels: normalizedLabels,
    displayLabels: normalizedLabels.map(label => getCategoryLabel(category, label)),
    values: normalizedLabels.map(label => totals.get(label))
  }
}
