import { STORAGE_KEYS, updateApiUrlDisplays } from './core/apiClient.js';
import './core/ui.js';
import './modules/auth.js';
import { loadDashboard, renderRegisteredUsersTable } from './modules/dashboard.js';
import { loadClients } from './modules/clients.js';
import { loadPipeline } from './modules/pipeline.js';
import { loadActivities } from './modules/activities.js';

const VIEW_META = {
    dashboard: { title: 'Dashboard Ejecutivo', subtitle: 'Resumen de KPIs comerciales y estado de autenticación JWT', loader: loadDashboard },
    clients: { title: 'Clientes & Leads', subtitle: 'GET / POST / PUT /api/v1/clients', loader: loadClients },
    pipeline: { title: 'Pipeline de Ventas', subtitle: 'GET / PATCH /api/v1/deal', loader: loadPipeline },
    activities: { title: 'Actividades & Tareas', subtitle: 'GET / POST /api/v1/activity', loader: loadActivities },
    admin: { title: 'Usuarios & Configuración API', subtitle: 'Conectado a POST /api/v1/users/', loader: renderRegisteredUsersTable }
};

export async function navigateTo(viewKey) {
    document.querySelectorAll('.view-section').forEach(sec => sec.classList.add('hidden'));
    document.getElementById(`view-${viewKey}`)?.classList.remove('hidden');

    document.querySelectorAll('.nav-item').forEach(btn => {
        if (btn.dataset.nav === viewKey) {
            btn.className = 'nav-item w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all bg-indigo-600/15 text-indigo-400 border border-indigo-500/20';
        } else {
            btn.className = 'nav-item w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium text-slate-400 hover:text-slate-100 hover:bg-slate-800/60 transition-all border border-transparent';
        }
    });

    const meta = VIEW_META[viewKey];
    if (meta) {
        document.getElementById('pageTitle').textContent = meta.title;
        document.getElementById('pageEndpointBadge').textContent = meta.subtitle;
        if (meta.loader) await meta.loader();
    }

    toggleMobileSidebar(false);
}

export function toggleMobileSidebar(show) {
    const sidebar = document.getElementById('sidebar');
    const backdrop = document.getElementById('sidebarBackdrop');
    if (show) {
        sidebar.classList.remove('-translate-x-full');
        backdrop.classList.remove('hidden');
    } else {
        sidebar.classList.add('-translate-x-full');
        backdrop.classList.add('hidden');
    }
}

export function handleGlobalSearch(query) {
    if (!query.trim()) return;
    navigateTo('clients').then(() => {
        const input = document.getElementById('clientSearchInput');
        if (input) {
            input.value = query;
            window.renderClientsTable();
        }
    });
}

window.navigateTo = navigateTo;
window.toggleMobileSidebar = toggleMobileSidebar;
window.handleGlobalSearch = handleGlobalSearch;

// Inicialización del Workspace Autenticado
document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
    if (!token) {
        window.location.href = '/home-crm';
        return;
    }

    const username = localStorage.getItem(STORAGE_KEYS.USERNAME) || 'Usuario';
    const isDemo = localStorage.getItem(STORAGE_KEYS.IS_DEMO) === 'true';

    document.getElementById('topbarUsername').textContent = username;
    document.getElementById('topbarAvatar').textContent = username.substring(0, 2).toUpperCase();
    document.getElementById('topbarRoleBadge').textContent = isDemo ? 'JWT Demo Mode' : 'JWT Activo';
    document.getElementById('sidebarJwtModeText').textContent = isDemo ? 'Token Simulado (Maqueta)' : 'Bearer Token Real';

    updateApiUrlDisplays();
    await navigateTo('dashboard'); // Solo carga el Dashboard al entrar
});