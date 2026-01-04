<template>
    <form class="register-form" @submit.prevent="handleSubmit">
        <!-- DATOS PERSONALES -->
        <fieldset>
            <legend>Datos personales</legend>
            <label>
                Edad:
                <input v-model="form.age" type="number" min="5" max="120" />
                <span v-if="showErrors && !form.age" class="error-msg">La edad es obligatoria.</span>
            </label>
            <label>
                Género:
                <select v-model="form.gender">
                    <option disabled value="">Selecciona...</option>
                    <option>Masculino</option>
                    <option>Femenino</option>
                    <option>Otro</option>
                    <option>Prefiero no decirlo</option>
                </select>
                <span v-if="showErrors && !form.gender" class="error-msg">El género es obligatorio.</span>
            </label>
        </fieldset>

        <!-- DEMOGRAFÍA -->
        <fieldset>
            <legend>Demografía</legend>
            <label>
                Estado de relación:
                <select v-model="form.relationship_status">
                    <option disabled value="">Selecciona...</option>
                    <option>Soltero</option>
                    <option>En relación</option>
                    <option>Casado</option>
                    <option>Divorciado</option>
                </select>
                <span v-if="showErrors && !form.relationship_status" class="error-msg">Este campo es obligatorio.</span>
            </label>
            <label>
                Situación ocupacional:
                <select v-model="form.occupation_status">
                    <option disabled value="">Selecciona...</option>
                    <option>Jubilado</option>
                    <option>Trabajador</option>
                    <option>Estudiante universitario</option>
                    <option>Estudiante escolar</option>
                    <option>Desempleado</option>
                </select>
                <span v-if="showErrors && !form.occupation_status" class="error-msg">Este campo es obligatorio.</span>
            </label>
        </fieldset>

        <!-- USO DE REDES SOCIALES -->
        <fieldset>
            <legend>Redes sociales</legend>
            <label>
                ¿Usas redes sociales?
                <select v-model="form.uses_social_media">
                    <option disabled value="">Selecciona...</option>
                    <option>Sí</option>
                    <option>No</option>
                </select>
                <span v-if="showErrors && !form.uses_social_media" class="error-msg">Este campo es obligatorio.</span>
            </label>
            <label>
                Tiempo medio diario:
                <select v-model="form.daily_average_time">
                    <option disabled value="">Selecciona...</option>
                    <option>Menos de 1 hora</option>
                    <option>Entre 1 y 2 horas</option>
                    <option>Entre 2 y 3 horas</option>
                    <option>Entre 3 y 4 horas</option>
                    <option>Entre 4 y 5 horas</option>
                    <option>Más de 5 horas</option>
                </select>
                <span v-if="showErrors && !form.daily_average_time" class="error-msg">Este campo es obligatorio.</span>
            </label>
            <label>
                Frecuencia de uso sin objetivo (1-5):
                <input v-model.number="form.aimless_usage_frequency" type="number" min="1" max="5" />
            </label>
            <label>
                Frecuencia de distracción (1-5):
                <input v-model.number="form.distraction_frequency" type="number" min="1" max="5" />
            </label>
            <label>
                Frecuencia de inquietud (1-5):
                <input v-model.number="form.restlessness_frequency" type="number" min="1" max="5" />
            </label>
        </fieldset>

        <!-- BIENESTAR MENTAL -->
        <fieldset>
            <legend>Bienestar mental</legend>
            <label>
                Facilidad para distraerse (1-5):
                <input v-model.number="form.easily_distracted_scale" type="number" min="1" max="5" />
            </label>
            <label>
                Intensidad de preocupación (1-5):
                <input v-model.number="form.worry_intensity_scale" type="number" min="1" max="5" />
            </label>
            <label>
                Dificultad para concentrarse (1-5):
                <input v-model.number="form.difficulty_concentrating" type="number" min="1" max="5" />
            </label>
            <label>
                Frecuencia de comparación social (1-5):
                <input v-model.number="form.social_comparison_frequency" type="number" min="1" max="5" />
            </label>
            <label>
                Problemas para dormir (1-5):
                <input v-model.number="form.sleep_issue_scale" type="number" min="1" max="5" />
            </label>
            <label>
                Frecuencia de búsqueda de validación (1-5):
                <input v-model.number="form.validation_seeking_frequency" type="number" min="1" max="5" />
            </label>
            <label>
                Frecuencia de depresión (1-5):
                <input v-model.number="form.depressed_frequency" type="number" min="1" max="5" />
            </label>
            <label>
                Fluctuación de interés (1-5):
                <input v-model.number="form.interest_fluctuation_scale" type="number" min="1" max="5" />
            </label>
            <label>
                Problemas de sueño (1-5):
                <input v-model.number="form.sleep_issue_scale" type="number" min="1" max="5" />
            </label>
        </fieldset>

        <!-- ORGANIZACIÓN (opcional) -->
        <fieldset>
            <legend>Organización (opcional)</legend>
            <label>
                Organización:
                <select v-model="form.organization_name">
                    <option disabled value="">Selecciona...</option>
                    <option>Empresa</option>
                    <option>Gobierno</option>
                    <option>Privada</option>
                    <option>Colegio</option>
                    <option>Universidad</option>
                </select>
            </label>
        </fieldset>

        <!-- PLATAFORMAS (opcional, selección múltiple) -->
        <fieldset>
            <legend>Plataformas que usas (opcional)</legend>
            <div class="platform-checkboxes">
                <label v-for="pl in platformList" :key="pl">
                    <input type="checkbox" :value="pl" v-model="form.platforms" /> {{ pl }}
                </label>
            </div>
        </fieldset>

        <button type="submit">Registrar</button>
        <!-- MODAL DE ÉXITO -->
        <div v-if="showSuccessModal" class="success-modal-mask">
            <div class="success-modal">
                <h2>Formulario enviado correctamente</h2>
                <button @click="showSuccessModal = false" class="success-close-btn">Cerrar</button>
            </div>
        </div>
    </form>
</template>

<script setup>
import { ref } from 'vue'

const platformList = [
    "Facebook", "Instagram", "Twitter", "YouTube", "TikTok",
    "Discord", "Pinterest", "Snapchat", "Reddit"
]

const form = ref({
    age: '',
    gender: '',
    relationship_status: '',
    occupation_status: '',
    uses_social_media: '',
    daily_average_time: '',
    aimless_usage_frequency: 1,
    distraction_frequency: 1,
    restlessness_frequency: 1,
    easily_distracted_scale: 1,
    worry_intensity_scale: 1,
    difficulty_concentrating: 1,
    social_comparison_frequency: 1,
    comparison_feeling: 1,
    validation_seeking_frequency: 1,
    depressed_frequency: 1,
    interest_fluctuation_scale: 1,
    sleep_issue_scale: 1,
    organization_name: '',
    platforms: []
})

const showErrors = ref(false)
const showSuccessModal = ref(false)

function scrollToFirstError() {
    setTimeout(() => {
        const firstError = document.querySelector('.error-msg');
        if (firstError) {
            firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }, 0);
}

async function handleSubmit() {
    showErrors.value = true;

    // Validación rápida de obligatorios (puedes mejorarla si quieres)
    if (
        !form.value.age ||
        !form.value.gender ||
        !form.value.relationship_status ||
        !form.value.occupation_status ||
        !form.value.uses_social_media ||
        !form.value.daily_average_time ||
        !form.value.difficulty_concentrating
    ) {
        scrollToFirstError();
        return;
    }

    // Mapeo del formulario al DTO del backend
    const data = {
        age: Number(form.value.age),
        gender: form.value.gender,
        demographics: {
            relationshipStatus: form.value.relationship_status,
            occupationStatus: form.value.occupation_status,
        },
        socialMediaUsage: {
            usesSocialMedia: form.value.uses_social_media,
            dailyAverageTime: form.value.daily_average_time,
            aimlessUsageFrequency: form.value.aimless_usage_frequency,
            distractionFrequency: form.value.distraction_frequency,
            restlessnessFrequency: form.value.restlessness_frequency,
        },
        mentalHealthMetrics: {
            easilyDistractedScale: form.value.easily_distracted_scale,
            worryIntensityScale: form.value.worry_intensity_scale,
            difficultyConcentrating: form.value.difficulty_concentrating,
            socialComparisonFrequency: form.value.social_comparison_frequency,
            comparisonFeeling: form.value.comparison_feeling,
            validationSeekingFrequency: form.value.validation_seeking_frequency,
            depressedFrequency: form.value.depressed_frequency,
            interestFluctuationScale: form.value.interest_fluctuation_scale,
            sleepIssueScale: form.value.sleep_issue_scale,
        },
        organizationName: form.value.organization_name,
        platforms: form.value.platforms,
    };

    try {
        // Añade el token JWT
        const token = localStorage.getItem('token');
        const response = await fetch('/api/respondents', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Si necesitas autorización:
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(data),
        });

        if (response.ok) {
            // Resetea el formulario
            form.value = {
                age: '',
                gender: '',
                relationship_status: '',
                occupation_status: '',
                uses_social_media: '',
                daily_average_time: '',
                aimless_usage_frequency: 1,
                distraction_frequency: 1,
                restlessness_frequency: 1,
                easily_distracted_scale: 1,
                worry_intensity_scale: 1,
                difficulty_concentrating: 1,
                social_comparison_frequency: 1,
                comparison_feeling: '',
                validation_seeking_frequency: 1,
                depressed_frequency: 1,
                interest_fluctuation_scale: 1,
                sleep_issue_scale: 1,
                organization_name: '',
                platforms: []
            };
            showErrors.value = false;
            showSuccessModal.value = true;
        } else {
            alert('Error al enviar el registro. Inténtalo de nuevo.');
        }
    } catch (e) {
        alert('Error de red. Inténtalo más tarde.');
    }
}
</script>

<style scoped>
.register-form {
    max-width: 580px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 1.7rem;
    background: #fff;
    border-radius: 18px;
    padding: 2.2rem 2.6rem 2.3rem 2.6rem;
    box-shadow: 0 2px 15px #0001;
}

fieldset {
    border: none;
    margin: 0 0 1rem 0;
    padding: 0 0 1rem 0;
    border-bottom: 1px solid #eee;
}

legend {
    font-size: 1.13rem;
    font-weight: 600;
    color: #3d4250;
    margin-bottom: 1rem;
}

label {
    display: flex;
    flex-direction: column;
    gap: 0.3rem;
    margin-bottom: 1rem;
    font-weight: 400;
    color: #222;
}

input[type="number"],
input[type="text"],
select {
    padding: 0.55rem 0.6rem;
    border-radius: 8px;
    border: 1px solid #ddd;
    font-size: 1rem;
}

button[type="submit"] {
    background: #3f6fff;
    color: #fff;
    padding: 0.8rem 1.6rem;
    border: none;
    border-radius: 12px;
    font-size: 1.15rem;
    font-weight: 600;
    margin-top: 1rem;
    cursor: pointer;
    transition: background 0.15s;
}

button[type="submit"]:hover {
    background: #2851c7;
}

.platform-checkboxes {
    display: flex;
    flex-wrap: wrap;
    gap: 1rem;
}

.error-msg {
    color: #c42d2d;
    background: #ffecec;
    border-radius: 8px;
    padding: 0.35rem 0.7rem;
    margin-top: 0.3rem;
    font-size: 0.97rem;
}

/* MODAL ÉXITO */
.success-modal-mask {
    position: fixed;
    z-index: 9999;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0,0,0,0.18);
    display: flex;
    align-items: center;
    justify-content: center;
}
.success-modal {
    background: #fff;
    border-radius: 13px;
    padding: 2.1rem 2.2rem 1.6rem 2.2rem;
    box-shadow: 0 8px 32px #0002;
    text-align: center;
    min-width: 260px;
    max-width: 360px;
}
.success-modal h2 {
    color: #22733a;
    font-size: 1.28rem;
    margin-bottom: 1.2rem;
    font-weight: 700;
}
.success-close-btn {
    background: #3f6fff;
    color: #fff;
    border: none;
    border-radius: 8px;
    padding: 0.7rem 2.2rem;
    font-size: 1.08rem;
    font-weight: 500;
    cursor: pointer;
    margin-top: 1rem;
    transition: background 0.14s;
}
.success-close-btn:hover {
    background: #2851c7;
}

@media (max-width: 900px) {
  .register-form {
    max-width: 97vw;
    padding: 2rem 1.1rem 2rem 1.1rem;
  }
}
@media (max-width: 600px) {
  .register-form {
    max-width: 100vw;
    padding: 1.2rem 0.4rem 1.3rem 0.4rem;
    border-radius: 0;
    box-shadow: none;
    font-size: 0.97rem;
  }
  fieldset {
    padding: 0 0 0.5rem 0;
  }
  legend {
    font-size: 1rem;
    margin-bottom: 0.6rem;
  }
  label {
    font-size: 0.99rem;
    gap: 0.18rem;
    margin-bottom: 0.8rem;
  }
  input[type="number"],
  input[type="text"],
  select {
    font-size: 0.99rem;
    padding: 0.45rem 0.5rem;
  }
  button[type="submit"],
  .success-close-btn {
    font-size: 1rem;
    padding: 0.7rem 1.3rem;
    border-radius: 9px;
  }
  .platform-checkboxes {
    flex-direction: column;
    gap: 0.7rem;
  }
  .success-modal {
    min-width: 180px;
    max-width: 90vw;
    padding: 1.1rem 0.8rem 1rem 0.8rem;
  }
  .success-modal h2 {
    font-size: 1.05rem;
    margin-bottom: 0.9rem;
  }
}

@media (max-width: 420px) {
  .register-form {
    padding: 0.7rem 0.1rem 0.7rem 0.1rem;
  }
  legend {
    font-size: 0.95rem;
  }
}

</style>
