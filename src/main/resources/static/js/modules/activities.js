import { apiClient } from '../core/apiClient.js';
import { showToast, closeModal, escapeHtml } from '../core/ui.js';

let crmActivities = [];

export async function loadActivities() {
    try {
        const data = await apiClient.get('/activity/');
        crmActivities = Array.isArray(data) ? data : [];
        renderActivitiesList();
    } catch (error) {
        console.error('Error al cargar actividades:', error);
        showToast(error.message || 'No se pudieron cargar las actividades desde el servidor.', 'error');
        renderActivitiesList();
    }
}

export function renderActivitiesList() {
    const container = document.getElementById('activitiesListContainer');
    if (!container) return;

    const typeIcons = {
        CALL: 'fa-phone text-sky-400 bg-sky-500/10 border-sky-500/20',
        MEETING: 'fa-video text-purple-400 bg-purple-500/10 border-purple-500/20',
        EMAIL: 'fa-envelope text-amber-400 bg-amber-500/10 border-amber-500/20'
    };

    if (!crmActivities.length) {
        container.innerHTML = `<div class="col-span-full bg-slate-900/70 border border-slate-800 rounded-2xl p-6 text-center text-sm text-slate-400">No hay actividades registradas.</div>`;
        return;
    }

    container.innerHTML = crmActivities.map(act => {
        const isDone = Boolean(act.completed);
        const dateLabel = act.scheduledDate ? String(act.scheduledDate).replace('T', ' ').slice(0, 16) : 'Sin fecha';
        const clientLabel = act.clientName || act.client || 'General';

        return `
        <div class="bg-slate-900/70 border border-slate-800 rounded-2xl p-4 flex items-center justify-between gap-4 shadow">
            <div class="flex items-start gap-3.5">
                <div class="w-10 h-10 rounded-xl border ${typeIcons[act.type] || 'text-indigo-400 bg-indigo-500/10 border-indigo-500/20'} flex items-center justify-center shrink-0">
                    <i class="fa-solid ${typeIcons[act.type]?.split(' ')[0] || 'fa-calendar'}"></i>
                </div>
                <div>
                    <h4 class="text-sm font-bold ${isDone ? 'line-through text-slate-500' : 'text-white'}">${escapeHtml(act.subject || '')}</h4>
                    <p class="text-xs text-slate-400 mt-0.5">${escapeHtml(clientLabel)} &bull; <span class="font-mono text-slate-500">${escapeHtml(dateLabel)}</span></p>
                </div>
            </div>
            <button onclick="toggleActivityComplete('${act.id}')"
                class="px-3 py-1.5 rounded-xl text-xs font-semibold border transition-all ${isDone ? 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30' : 'bg-slate-800 text-slate-300 hover:bg-indigo-600 hover:text-white border-slate-700'}">
                ${isDone ? '<i class="fa-solid fa-check mr-1"></i>Hecho' : 'Completar'}
            </button>
        </div>
    `;
    }).join('');
}

export async function toggleActivityComplete(id) {
    const act = crmActivities.find(a => String(a.id) === String(id));
    if (!act) return;

    const nextCompleted = !Boolean(act.completed);
    try {
        const updated = await apiClient.put(`/activity/${id}`, {
            subject: act.subject,
            type: act.type,
            priority: act.priority,
            scheduledDate: act.scheduledDate,
            completed: nextCompleted
        });
        Object.assign(act, updated, { completed: updated?.completed ?? nextCompleted });
        renderActivitiesList();
        showToast(act.completed ? 'Actividad marcada como completada.' : 'Actividad reabierta.', 'success');
    } catch (error) {
        console.error('Error al actualizar actividad:', error);
        showToast(error.message || 'No se pudo actualizar la actividad.', 'error');
    }
}

export function openActivityModal() {
    document.getElementById('activityModal')?.classList.remove('hidden');
}

export async function handleSaveActivity(event) {
    event.preventDefault();

    // Fecha futura por defecto (1 hora adelante) en formato ISO compatible con LocalDateTime de Spring Boot
    const defaultScheduledDate = new Date(Date.now() + 60 * 60 * 1000).toISOString().slice(0, 19);

    const payload = {
        subject: document.getElementById('actSubject').value.trim(),
        type: document.getElementById('actType').value,
        priority: document.getElementById('actPriority').value,
        scheduledDate: defaultScheduledDate
    };

    try {
        const created = await apiClient.post('/activity/', payload);
        if (created && typeof created === 'object') {
            crmActivities.unshift(created);
        } else {
            await loadActivities();
        }
        closeModal('activityModal');
        event.target.reset();
        renderActivitiesList();
        showToast('Actividad agendada en el backend.', 'success');
    } catch (error) {
        console.error('Error al guardar actividad:', error);
        showToast(error.message || 'No se pudo guardar la actividad.', 'error');
    }
}

window.toggleActivityComplete = toggleActivityComplete;
window.openActivityModal = openActivityModal;
window.handleSaveActivity = handleSaveActivity;