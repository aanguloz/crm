import { apiClient, STORAGE_KEYS } from '../core/apiClient.js';
import { showToast, escapeHtml } from '../core/ui.js';
import { cacheRegisteredUser } from './auth.js';

export async function loadDashboard() {
    await Promise.all([
        loadDashboardMetrics(),
        loadDashboardFunnel(),
        loadRecentActivities()
    ]);
    await verifyJwtProfileEndpoint();
}

export async function loadDashboardMetrics() {
    try {
        const metrics = await apiClient.get('/dashboard/metrics');
        if (!metrics) return;

        const leadsEl = document.getElementById('kpiActiveLeads');
        const dealsEl = document.getElementById('kpiOpenDeals');
        const convEl = document.getElementById('kpiConversionRate');
        const revEl = document.getElementById('kpiProjectedRevenue');

        if (leadsEl) leadsEl.textContent = Number(metrics.activeLeads ?? 0).toLocaleString();
        if (dealsEl) dealsEl.textContent = Number(metrics.pipelineOpportunities ?? 0).toLocaleString();
        if (convEl) convEl.textContent = `${Number(metrics.conversionRate ?? 0)}%`;
        if (revEl) revEl.textContent = `$${Number(metrics.projectedRevenue ?? 0).toLocaleString()}`;
    } catch (error) {
        console.error('Error al cargar métricas del dashboard:', error);
    }
}

function extractAmount(stage, count) {
    const preferred = ['totalAmount','amount','sumAmount','total','value',
        'totalValue','sumValue','dealValue','stageValue','revenue'];
    for (const k of preferred) {
        const n = Number(stage[k]);
        if (Number.isFinite(n) && n > 0) return n;
    }
    // red de seguridad: cualquier número que no sea el count
    for (const [k, v] of Object.entries(stage)) {
        if (typeof v === 'number' && k !== 'dealCount' && k !== 'count' && v !== count) return v;
    }
    return 0;
}

const STAGE_ORDER = ['PROSPECTION','QUALIFICATION','PROPOSAL','NEGOTIATION','CLOSED_WON'];

export async function loadDashboardFunnel() {
    const container = document.getElementById('dashboardFunnelContainer');
    if (!container) return;
    try {
        const stages = await apiClient.get('/dashboard/pipeline');
        if (!Array.isArray(stages) || stages.length === 0) {
            container.innerHTML = `<p class="text-xs text-slate-500">Sin datos de pipeline disponibles.</p>`;
            return;
        }

        const stageKey = s => String(s.stage ?? s.stageName ?? s.name ?? '');

        // ✔️ variable nueva, no reasignación
        const sortedStages = [...stages].sort((a, b) =>
            STAGE_ORDER.indexOf(stageKey(a)) - STAGE_ORDER.indexOf(stageKey(b))
        );

        const counts  = sortedStages.map(s => Number(s.dealCount ?? s.count ?? 0));
        const amounts = sortedStages.map((s, i) => extractAmount(s, counts[i]));
        const maxAmount = Math.max(...amounts, 1);

        const colors = ['bg-sky-500','bg-indigo-500','bg-purple-500','bg-amber-500','bg-emerald-500'];
        const stageLabels = {
            PROSPECTION: '1. Prospección Inicial',
            QUALIFICATION: '2. Calificación Técnica',
            PROPOSAL: '3. Propuesta Enviada',
            NEGOTIATION: '4. Negociación Contractual',
            CLOSED_WON: '5. Cerrado Ganado'
        };

        container.innerHTML = sortedStages.map((stage, idx) => {
            const rawKey = stageKey(stage) || `Etapa ${idx + 1}`;
            const label  = stageLabels[rawKey] || rawKey;
            const count  = counts[idx];
            const amount = amounts[idx];
            const pct    = Math.min(100, Math.round((amount / maxAmount) * 100));
            const barColor = colors[idx % colors.length];

            return `
                <div>
                    <div class="flex justify-between text-xs mb-1.5">
                        <span class="font-semibold text-slate-200">${escapeHtml(String(label))} (${count})</span>
                        <span class="font-mono text-slate-400">$${amount.toLocaleString()} (${pct}%)</span>
                    </div>
                    <div class="w-full h-3 bg-slate-950 rounded-full overflow-hidden border border-slate-800">
                        <div class="h-full ${barColor} rounded-full transition-all duration-300" style="width: ${pct}%"></div>
                    </div>
                </div>
            `;
        }).join('');
    } catch (error) {
        console.error('Error al cargar embudo del dashboard:', error);
        container.innerHTML = `<p class="text-xs text-rose-400">No se pudo cargar el embudo: ${escapeHtml(String(error.message))}</p>`;
    }
}

export async function loadRecentActivities() {
    const container = document.getElementById('recentActivityFeed');
    if (!container) return;

    const typeMeta = {
        CALL: { icon: 'fa-phone', color: 'text-sky-400 bg-sky-500/10' },
        MEETING: { icon: 'fa-video', color: 'text-purple-400 bg-purple-500/10' },
        EMAIL: { icon: 'fa-envelope', color: 'text-amber-400 bg-amber-500/10' }
    };

    try {
        const activities = await apiClient.get('/dashboard/recent-activities?limit=5');
        if (!Array.isArray(activities) || activities.length === 0) {
            container.innerHTML = `<p class="text-xs text-slate-500">No hay actividades recientes.</p>`;
            return;
        }

        container.innerHTML = activities.map(act => {
            const meta = typeMeta[act.type] || { icon: 'fa-calendar', color: 'text-indigo-400 bg-indigo-500/10' };
            const fecha = act.scheduledDate ? String(act.scheduledDate).replace('T', ' ').slice(0, 16) : '';

            return `
                <div class="flex items-start gap-3 p-3 rounded-xl bg-slate-950/60 border border-slate-800/70">
                    <div class="w-8 h-8 rounded-lg ${meta.color} flex items-center justify-center shrink-0 mt-0.5">
                        <i class="fa-solid ${meta.icon} text-xs"></i>
                    </div>
                    <div class="min-w-0 flex-1">
                        <p class="text-xs font-semibold text-white truncate">${escapeHtml(act.subject || 'Actividad')}</p>
                        <p class="text-[11px] text-slate-400 truncate">Prioridad: ${escapeHtml(String(act.priority || '-'))}</p>
                        <span class="text-[10px] font-mono text-slate-500">${escapeHtml(fecha)}</span>
                    </div>
                </div>
            `;
        }).join('');
    } catch (error) {
        console.error('Error al cargar actividades recientes:', error);
    }
}

export async function verifyJwtProfileEndpoint() {
    const isDemo = localStorage.getItem(STORAGE_KEYS.IS_DEMO) === 'true';
    const msgEl = document.getElementById('perfilResponseMessage');
    const badgeEl = document.getElementById('perfilStatusBadge');
    const indicatorEl = document.getElementById('backendConnectionIndicator');
    const jwtStatusDot = document.getElementById('jwtStatusDot');

    if (!msgEl || !badgeEl || !indicatorEl) return;

    if (isDemo) {
        msgEl.textContent = 'Modo Maqueta (Simulado): "¡Hola, has pasado el filtro JWT!"';
        badgeEl.textContent = 'SIMULADO OK';
        badgeEl.className = 'text-[10px] font-mono px-2 py-0.5 rounded-full bg-amber-500/15 text-amber-300 border border-amber-500/30';
        indicatorEl.className = 'w-2 h-2 rounded-full bg-amber-400';
        if (jwtStatusDot) jwtStatusDot.className = 'w-2 h-2 rounded-full bg-amber-400 animate-pulse';
        return;
    }

    try {
        const result = await apiClient.get('/users/perfil', { allow401: true });
        const responseText = typeof result === 'object' ? JSON.stringify(result) : (result || '¡Hola, has pasado el filtro JWT!');
        const hora = new Date().toLocaleTimeString();

        msgEl.textContent = `Respuesta GET /api/v1/users/perfil (${hora}): "${responseText}"`;
        badgeEl.textContent = '200 OK - FILTRO SUPERADO';
        badgeEl.className = 'text-[10px] font-mono px-2 py-0.5 rounded-full bg-emerald-500/15 text-emerald-400 border border-emerald-500/30';
        indicatorEl.className = 'w-2 h-2 rounded-full bg-emerald-400';
        if (jwtStatusDot) jwtStatusDot.className = 'w-2 h-2 rounded-full bg-emerald-400 animate-pulse';

        showToast(`Filtro JWT verificado (${hora})`, 'success');
    } catch (error) {
        msgEl.textContent = `Error de autenticación o servidor offline (${error.message})`;
        badgeEl.textContent = 'NO AUTORIZADO / OFFLINE';
        badgeEl.className = 'text-[10px] font-mono px-2 py-0.5 rounded-full bg-rose-500/15 text-rose-400 border border-rose-500/30';
        indicatorEl.className = 'w-2 h-2 rounded-full bg-rose-400';
        if (jwtStatusDot) jwtStatusDot.className = 'w-2 h-2 rounded-full bg-rose-400';
    }
}

export function openJwtInspectorModal() {
    const access = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN) || 'No disponible';
    const refresh = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN) || 'No disponible';
    document.getElementById('inspectorAccessToken').value = access;
    document.getElementById('inspectorRefreshToken').value = refresh;
    document.getElementById('inspectorAuthHeaderPreview').textContent = `Authorization: Bearer ${access.substring(0, 48)}...`;
    document.getElementById('jwtInspectorModal').classList.remove('hidden');
}

export function copyTokenToClipboard(textareaId) {
    const el = document.getElementById(textareaId);
    el.select();
    navigator.clipboard.writeText(el.value);
    showToast('Token copiado al portapapeles.', 'success');
}

export function renderRegisteredUsersTable() {
    const tbody = document.getElementById('registeredUsersTableBody');
    const countEl = document.getElementById('registeredUsersCount');
    if (!tbody) return;

    const users = JSON.parse(localStorage.getItem(STORAGE_KEYS.REGISTERED_USERS) || '[]');
    countEl.textContent = `${users.length} ${users.length === 1 ? 'Usuario' : 'Usuarios'}`;

    if (users.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4" class="py-8 text-center text-slate-500">Aún no se han registrado usuarios desde esta sesión.</td></tr>`;
        return;
    }

    tbody.innerHTML = users.map(u => `
        <tr class="hover:bg-slate-800/30">
            <td class="py-3 px-3 font-mono text-[11px] text-indigo-300">${escapeHtml(String(u.id))}</td>
            <td class="py-3 px-3 font-semibold text-white">${escapeHtml(u.username)}</td>
            <td class="py-3 px-3 text-slate-300">${escapeHtml(u.email)}</td>
            <td class="py-3 px-3"><span class="px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 text-[10px]">${escapeHtml(u.origin)}</span></td>
        </tr>
    `).join('');
}

export async function handleAdminCreateUser(event) {
    event.preventDefault();
    const btn = document.getElementById('btnAdminCreateUser');
    const originalHtml = btn.innerHTML;

    const userReqDTO = {
        email: document.getElementById('adminRegEmail').value.trim(),
        username: document.getElementById('adminRegUsername').value.trim(),
        password: document.getElementById('adminRegPassword').value
    };

    try {
        btn.disabled = true;
        btn.innerHTML = `<i class="fa-solid fa-circle-notch animate-spin"></i> Enviando DTO...`;

        const userRespDTO = await apiClient.post('/users/', userReqDTO);
        cacheRegisteredUser(userRespDTO, 'Panel Admin CRM');
        renderRegisteredUsersTable();
        event.target.reset();
        showToast(`Usuario ${userRespDTO.username} registrado exitosamente.`, 'success');
    } catch (error) {
        showToast(`Error Backend: ${error.message}`, 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = originalHtml;
    }
}

window.verifyJwtProfileEndpoint = verifyJwtProfileEndpoint;
window.openJwtInspectorModal = openJwtInspectorModal;
window.copyTokenToClipboard = copyTokenToClipboard;
window.handleAdminCreateUser = handleAdminCreateUser;