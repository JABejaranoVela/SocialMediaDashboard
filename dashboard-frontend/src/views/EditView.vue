<template>
  <div class="edit-table-container">
    <!-- Encabezado de la tabla (solo desktop) -->
    <div class="edit-header-table">
      <div class="edit-header-cell edit-header-id">ID</div>
      <div class="edit-header-cell edit-header-user">
        <i class="fas fa-user"></i> Usuario
      </div>
      <div class="edit-header-cell">Edad</div>
      <div class="edit-header-cell">Género</div>
      <div class="edit-header-cell">Situación laboral</div>
      <div class="edit-header-cell">Fecha de registro</div>
      <div class="edit-header-cell acciones-header">Modificar</div>
      <div class="edit-header-cell acciones-header">Eliminar</div>
    </div>
    <!-- Cada registro -->
    <div v-for="registro in registros" :key="registro.respondentId" class="edit-row"
      @click="seleccionarRegistro(registro)"
      :class="{ 'row-selected': selectedRegistro && selectedRegistro.respondentId === registro.respondentId }">

      <!-- Desktop: normal grid -->
      <div class="edit-cell edit-cell-id" data-label="ID">{{ registro.respondentId }}</div>
      <div class="edit-cell" data-label="Usuario">
        {{ registro.user?.username || 'Desconocido' }}
        <span class="edit-role">({{ registro.user?.role || '-' }})</span>
      </div>
      <div class="edit-cell" data-label="Edad">{{ registro.age ?? '-' }}</div>
      <div class="edit-cell" data-label="Género">{{ displayCategory('gender', registro.gender) }}</div>
      <div class="edit-cell" data-label="Situación laboral">{{ displayCategory('occupation', registro.demographics?.occupationStatus) }}</div>
      <div class="edit-cell" data-label="Fecha de registro">
        {{ registro.timestamp ? (new Date(registro.timestamp)).toLocaleDateString() : '-' }}
      </div>
      <div class="edit-cell acciones" data-label="Modificar" @click.stop>
        <button class="edit-btn" type="button" aria-label="Modificar registro" title="Modificar registro" @click="editarRegistro(registro)">
          <span class="action-icon" aria-hidden="true">✎</span>
          <span class="action-text">Editar</span>
        </button>
      </div>
      <div class="edit-cell acciones" data-label="Eliminar" @click.stop>
        <button class="delete-btn" type="button" aria-label="Eliminar registro" title="Eliminar registro" @click="openDeleteModal(registro)">
          <span class="action-icon" aria-hidden="true">×</span>
          <span class="action-text">Eliminar</span>
        </button>
      </div>

      <!-- Móvil: tabla 2xN + fila final centrada -->
      <div class="edit-mobile-fields">
        <div class="edit-fields-grid">
          <div class="respondent-card-field">
            <span class="edit-label">ID:</span>
            <span class="edit-value">{{ registro.respondentId }}</span>
          </div>
          <div class="respondent-card-field">
            <span class="edit-label">Usuario:</span>
            <span class="edit-value">
              {{ registro.user?.username || 'Desconocido' }}
              <span class="edit-role">({{ registro.user?.role || '-' }})</span>
            </span>
          </div>
          <div class="respondent-card-field">
            <span class="edit-label">Edad:</span>
            <span class="edit-value">{{ registro.age ?? '-' }}</span>
          </div>
          <div class="respondent-card-field">
            <span class="edit-label">Género:</span>
            <span class="edit-value">{{ displayCategory('gender', registro.gender) }}</span>
          </div>
          <div class="respondent-card-field respondent-card-field-wide">
            <span class="edit-label">Situación laboral:</span>
            <span class="edit-value">{{ displayCategory('occupation', registro.demographics?.occupationStatus) }}</span>
          </div>
          <div class="respondent-card-field">
            <span class="edit-label">Fecha de registro:</span>
            <span class="edit-value">{{ registro.timestamp ? (new Date(registro.timestamp)).toLocaleDateString() : '-' }}</span>
          </div>
        </div>
        <div class="edit-mobile-actions">
          <button class="edit-btn" type="button" aria-label="Modificar registro" title="Modificar registro" @click.stop="editarRegistro(registro)">
            <span class="action-icon" aria-hidden="true">✎</span>
            <span class="action-text">Editar</span>
          </button>
          <button class="delete-btn" type="button" aria-label="Eliminar registro" title="Eliminar registro" @click.stop="openDeleteModal(registro)">
            <span class="action-icon" aria-hidden="true">×</span>
            <span class="action-text">Eliminar</span>
          </button>
        </div>
      </div>
    </div>

    <!-- MODAL DE BORRADO: SIEMPRE FUERA DEL v-for Y DE LAS ROWS -->
    <div v-if="showDeleteModal" class="modal-mask" @click.self="closeDeleteModal">
      <div class="modal" role="dialog" aria-modal="true" aria-labelledby="delete-modal-title">
        <h3 id="delete-modal-title">¿Seguro que quieres borrar este registro?</h3>
        <div class="modal-btn-row">
          <button class="modal-delete-btn" type="button" @click="confirmDelete">Borrar</button>
          <button class="modal-cancel-btn" type="button" @click="closeDeleteModal">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { getCategoryLabel, isInvalidCategoryValue } from '../catalogs/respondentCatalogs.js'

const router = useRouter()
const registros = ref([])
const selectedRegistro = ref(null)

function displayCategory(category, value) {
  return isInvalidCategoryValue(value) ? 'Pendiente de revisión' : getCategoryLabel(category, value)
}

async function fetchRegistros() {
  const token = localStorage.getItem('token')
  const res = await fetch('/api/respondents/by-user', {
    headers: { Authorization: `Bearer ${token}` }
  })
  registros.value = await res.json()
}

onMounted(fetchRegistros)
onActivated(fetchRegistros)

function seleccionarRegistro(reg) {
  selectedRegistro.value = reg
}

function editarRegistro(registro) {
  router.push({ name: 'edit-form', params: { id: registro.respondentId } })
}

// Borrado
const showDeleteModal = ref(false)
const registroAEliminar = ref(null)

function openDeleteModal(registro) {
  registroAEliminar.value = registro
  showDeleteModal.value = true
}
function closeDeleteModal() {
  showDeleteModal.value = false
  registroAEliminar.value = null
}
async function confirmDelete() {
  if (!registroAEliminar.value) return;
  const token = localStorage.getItem('token');
  try {
    const res = await fetch(`/api/respondents/${registroAEliminar.value.respondentId}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` }
    });
    if (res.ok) {
      registros.value = registros.value.filter(r => r.respondentId !== registroAEliminar.value.respondentId);
      if (selectedRegistro.value && selectedRegistro.value.respondentId === registroAEliminar.value.respondentId) {
        selectedRegistro.value = null;
      }
      closeDeleteModal();
    } else {
      alert('Error al borrar el registro.');
    }
  } catch (e) {
    alert('Error de red al borrar el registro.');
  }
}
</script>

<style scoped>
.edit-table-container {
  width: 100%;
  max-width: 1200px;
  margin: 2rem auto 0 auto;
  padding: 0 1.5rem;
  min-width: 320px;
  overflow-x: auto;
}

/* ----- DESKTOP TABLE ----- */
.edit-header-table,
.edit-row {
  display: grid;
  grid-template-columns: 0.6fr 2fr 0.8fr 1fr 1.6fr 1.3fr 0.8fr 0.8fr;
  align-items: center;
  padding-left: 0.7rem;
  padding-right: 0.7rem;
}

.edit-header-table {
  background: #282c37;
  color: #fff;
  border-radius: 12px 12px 0 0;
  padding-top: 1.1rem;
  padding-bottom: 1.1rem;
  font-size: 1.06rem;
  font-weight: 600;
  box-shadow: 0 2px 10px #0002;
  gap: 0.6rem;
}

.edit-header-cell {
  padding: 0 0.2rem;
  display: flex;
  align-items: center;
  letter-spacing: 0.5px;
  justify-content: center;
}

.acciones-header { justify-content: center; }
.edit-header-user { font-size: 1.07rem; }
.edit-header-user i { margin-right: 0.7rem; color: #5ab4f3; font-size: 1.1rem; }

.edit-row {
  padding-top: 0.85rem;
  padding-bottom: 0.85rem;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  background: #f9fafb;
  transition: background 0.13s;
}

.edit-row:hover,
.row-selected {
  background: #e2edfa;
}

.edit-cell {
  padding: 0 0.2rem;
  font-size: 1rem;
  color: #252c36;
  justify-content: center;
  display: flex;
  align-items: center;
}

.acciones {
  display: flex;
  justify-content: center;
}
.edit-btn,
.delete-btn {
  background: none;
  border: none;
  font-size: 1.18rem;
  cursor: pointer;
  padding: 0.1rem 0.7rem;
  transition: color 0.15s, background 0.15s;
  border-radius: 50%;
  margin-right: 0.2rem;
}

.edit-btn { color: #3276d1; }
.edit-btn:hover { background: #e7f1fd; }
.delete-btn { color: #e04444; }
.delete-btn:hover { color: #b30000; background: #f8d7da; }

.selected-detail {
  margin-top: 2rem;
  background: #fff;
  border-radius: 12px;
  padding: 1.2rem 2rem;
  box-shadow: 0 2px 12px #0002;
}
.edit-role { font-size: 0.9em; color: #6498ca; margin-left: 0.4em; }

/* ========== MOBILE STYLE ========== */
.edit-mobile-fields { display: none; }

@media (max-width: 900px) {
  /* Oculta la tabla de desktop en móvil */
  .edit-header-table, .edit-cell { display: none !important; }
  .edit-row {
    display: flex;
    flex-direction: column;
    background: #f7faff;
    border-radius: 12px;
    box-shadow: 0 2px 8px #b4c7dd22;
    border: 1px solid #dde6f1;
    max-width: 520px;
    margin-left: auto;
    margin-right: auto;
    margin-bottom: 1.3rem;
    padding: 1.2rem 0.9rem 1.2rem 0.9rem;
    justify-content: center;
    align-items: center;
    text-align: center;
    gap: 0;
    position: relative;
  }
  .edit-mobile-fields { display: flex !important; flex-direction: column; width: 100%; }

  .edit-fields-grid {
    display: grid;
    grid-template-columns: 1fr 1.3fr;
    gap: 0.15em 0.4em;
    width: 100%;
    justify-items: end;
    align-items: center;
    margin-bottom: 1.2em;
  }
  .edit-label {
    color: #2476d2;
    font-weight: 700;
    font-size: 1.13em;
    justify-self: end;
    text-align: right;
    padding-right: 0.45em;
    word-break: keep-all;
    white-space: nowrap;
  }
  .edit-value {
    color: #222;
    font-size: 1.10em;
    font-weight: 400;
    justify-self: start;
    text-align: left;
    word-break: break-word;
    min-width: 0;
    max-width: 100%;
  }
  .edit-role { font-size: 0.9em; color: #6498ca; margin-left: 0.4em; }

  .edit-mobile-actions {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 2.2em;
    width: 100%;
    margin-top: 0.7em;
  }
  .edit-btn, .delete-btn {
    font-size: 1.35rem;
    margin: 0;
    padding: 0.12rem 0.95rem;
    vertical-align: middle;
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }
}

@media (max-width: 600px) {
  .edit-row { padding: 0.8rem 0.2rem 0.8rem 0.2rem; font-size: 0.98rem; }
  .edit-fields-grid { grid-template-columns: 1fr 1.2fr; }
  .edit-label { font-size: 1.08em; padding-right: 0.32em; }
}

/* MODAL SIEMPRE VISIBLE ENCIMA DE TODO */
.modal-mask {
  position: fixed;
  z-index: 99999;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.16);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
}
.modal {
  background: #fff;
  border-radius: 12px;
  padding: 1.3rem 1.5rem 1.1rem 1.5rem;
  box-shadow: 0 8px 32px #0004;
  width: 100%;
  max-width: 320px;
  min-width: 200px;
  text-align: center;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0;
  max-height: 25vh;
  overflow: auto;
}
.modal-btn-row {
  display: flex;
  justify-content: center;
  gap: 1rem;
  width: 100%;
  margin-top: 1.6rem;
}
.modal-delete-btn,
.modal-cancel-btn {
  min-width: 130px;
  padding: 0.7rem 0;
  font-size: 1.08rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.15s;
  border: none;
  text-align: center;
  margin: 0;
  box-sizing: border-box;
  display: inline-block;
}
.modal-delete-btn { background: #e04444; color: #fff; }
.modal-delete-btn:hover { background: #b30000; }
.modal-cancel-btn { background: #eee; color: #222; }
.modal-cancel-btn:hover { background: #dadada; }
.modal h3 {
  margin-bottom: 1.1rem;
  color: #222;
  font-size: 1.18rem;
  font-weight: 600;
}
</style>

<style scoped>
.edit-table-container {
  width: 100%;
  max-width: 75rem;
  min-width: 0;
  margin: 1rem auto 0;
  padding: 0;
  overflow: visible;
}

.edit-header-table,
.edit-cell { display: none; }

.edit-row {
  display: block;
  width: 100%;
  max-width: none;
  min-width: 0;
  margin: 0 auto 1rem;
  padding: 1rem;
  border: 1px solid #dde6f1;
  border-radius: 0.75rem;
  background: #f7faff;
  box-shadow: 0 0.125rem 0.5rem rgba(80, 110, 145, 0.12);
}

.edit-mobile-fields {
  display: flex;
  flex-direction: column;
  width: 100%;
  min-width: 0;
}

.edit-fields-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 0.65rem;
  width: 100%;
  min-width: 0;
  margin-bottom: 1rem;
}

.respondent-card-field {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  min-width: 0;
  gap: 0.25rem 0.4rem;
}

.edit-label {
  flex: 0 0 auto;
  margin: 0;
  color: #2476d2;
  font-size: 0.9rem;
  font-weight: 700;
  text-align: left;
  white-space: normal;
  overflow-wrap: anywhere;
}

.edit-value {
  min-width: 0;
  color: #222;
  text-align: left;
  overflow-wrap: anywhere;
}

.edit-mobile-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.75rem;
  width: 100%;
}

.edit-btn,
.delete-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: auto;
  min-width: 7.5rem;
  min-height: 2.75rem;
  margin: 0;
  padding: 0.55rem 0.85rem;
  gap: 0.4rem;
  border: 1px solid currentColor;
  border-radius: 0.6rem;
  font-size: 0.95rem;
  font-weight: 600;
}

.edit-btn { background: #edf5ff; }
.delete-btn { background: #fff1f1; }
.action-icon { font-size: 1.15em; line-height: 1; }
.action-text { white-space: nowrap; }

.modal-mask { padding: 1rem; }

.modal {
  width: min(100%, 24rem);
  min-width: 0;
  max-width: 100%;
  max-height: calc(100dvh - 2rem);
  padding: 1.25rem;
}

.modal h3 {
  margin: 0 0 1.25rem;
  color: #222;
  font-size: 1.1rem;
}

.modal-btn-row {
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 0;
}

.modal-delete-btn,
.modal-cancel-btn {
  width: 100%;
  min-width: 0;
  min-height: 2.75rem;
}

@media (min-width: 576px) {
  .edit-table-container { margin-top: 1.5rem; }
  .edit-fields-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 0.75rem 1.25rem; }
  .respondent-card-field-wide { grid-column: 1 / -1; }
  .edit-label { text-align: left; }
  .modal-btn-row { flex-direction: row; }
  .modal-delete-btn,
  .modal-cancel-btn { width: auto; min-width: 8rem; }
}

@media (max-width: 575.98px) {
  .edit-mobile-actions { flex-direction: column; }
  .edit-mobile-actions .edit-btn,
  .edit-mobile-actions .delete-btn { width: 100%; }
}

@media (min-width: 576px) and (max-width: 991.98px) {
  .edit-table-container {
    max-width: 45rem;
    margin-inline: auto;
  }
}

@media (min-width: 1200px) {
  .edit-table-container { padding-inline: 1rem; overflow-x: auto; }
  .edit-header-table,
  .edit-row {
    display: grid;
    grid-template-columns: 0.6fr 2fr 0.8fr 1fr 1.6fr 1.3fr 0.8fr 0.8fr;
    min-width: 56rem;
  }
  .edit-row { margin: 0; padding: 0.85rem 0.7rem; border: 0; border-bottom: 1px solid #eee; border-radius: 0; box-shadow: none; }
  .edit-cell { display: flex; min-width: 0; overflow-wrap: anywhere; }
  .edit-mobile-fields { display: none; }
  .acciones .edit-btn,
  .acciones .delete-btn {
    width: 100%;
    min-width: 0;
    padding: 0.4rem 0.25rem;
    font-size: 0.78rem;
  }
}
</style>
