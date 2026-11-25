import { apiClient } from '../core/apiClient.js';
import { showToast, closeModal, escapeHtml } from '../core/ui.js';

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

let crmClients = [
    { id: 'c-101', identifier: 'CLI-1001', fullName: 'Mariana Torres', company: 'TechCorp Andina', email: 'mtorres@techcorp.pe', phone: '+51 984 112 233', sector: 'Software', status: 'CUSTOMER', estimatedValue: 45000 },
    { id: 'c-102', identifier: 'CLI-1002', fullName: 'Roberto Mendoza', company: 'Logística del Sur S.A.', email: 'rmendoza@logisur.com', phone: '+51 951 445 667', sector: 'Transporte', status: 'QUALIFIED', estimatedValue: 28500 },
    { id: 'c-103', identifier: 'CLI-1003', fullName: 'Valeria Paredes', company: 'Clínica San Gabriel', email: 'vparedes@sangabriel.pe', phone: '+51 992 883 110', sector: 'Salud', status: 'LEAD', estimatedValue: 18000 }
];

export async function loadClients() {
    await renderClientsTable(true);
}

export async function renderClientsTable(fetchFromServer = false) {
    const tbody = document.getElementById('clientsTableBody');
    const search = (document.getElementById('clientSearchInput')?.value || '').toLowerCase();
    const statusFilter = document.getElementById('clientStatusFilter')?.value || '';

    if (fetchFromServer) {
        try {
            const data = await apiClient.get('/clients' + (search ? `?filter=${encodeURIComponent(search)}` : ''));
            if (Array.isArray(data)) crmClients = data;
        } catch (e) {
            console.warn('Usando datos locales de clientes:', e.message);
        }
    }

    const filtered = crmClients.filter(c => {
        const matchesText = (c.fullName?.toLowerCase() || '').includes(search) ||
            (c.company?.toLowerCase() || '').includes(search) ||
            (c.email?.toLowerCase() || '').includes(search);
        const matchesStatus = !statusFilter || c.status === statusFilter;
        return matchesText && matchesStatus;
    });

    const countBadge = document.getElementById('navClientCount');
    if (countBadge) countBadge.textContent = crmClients.length;

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="py-10 text-center text-slate-500 text-sm">No hay clientes registrados.</td></tr>`;
        return;
    }

    const statusBadges = {
        LEAD: 'bg-sky-500/10 text-sky-400 border-sky-500/20',
        QUALIFIED: 'bg-indigo-500/10 text-indigo-400 border-indigo-500/20',
        CUSTOMER: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
        CHURNED: 'bg-rose-500/10 text-rose-400 border-rose-500/20'
    };

    tbody.innerHTML = filtered.map(c => `
        <tr class="hover:bg-slate-800/30 transition-colors">
            <td class="py-3.5 px-4">
                <p class="font-semibold text-white">${escapeHtml(c.company)}</p>
                <p class="text-xs text-slate-400">${escapeHtml(c.fullName)}</p>
            </td>
            <td class="py-3.5 px-4">
                <p class="text-xs text-slate-300 font-mono">${escapeHtml(c.email)}</p>
                <p class="text-[11px] text-slate-500">${escapeHtml(c.phone || '-')}</p>
            </td>
            <td class="py-3.5 px-4 text-xs text-slate-300">${escapeHtml(c.sector)}</td>
            <td class="py-3.5 px-4">
                <span class="inline-flex px-2.5 py-0.5 rounded-full text-[11px] font-bold border ${statusBadges[c.status] || ''}">${c.status}</span>
            </td>
            <td class="py-3.5 px-4 font-mono text-xs text-emerald-400 font-semibold">$${Number(c.estimatedValue || 0).toLocaleString()}</td>
            <td class="py-3.5 px-4 text-right space-x-1">
                <button onclick="openClientModal('${c.id}')" class="p-1.5 text-slate-400 hover:text-indigo-400 rounded-lg hover:bg-indigo-500/10" title="Editar"><i class="fa-solid fa-pen-to-square"></i></button>
                <button onclick="deleteClient('${c.id}')" class="p-1.5 text-slate-400 hover:text-rose-400 rounded-lg hover:bg-rose-500/10" title="Eliminar"><i class="fa-solid fa-trash-can"></i></button>
            </td>
        </tr>
    `).join('');
}

export function resetClientFilters() {
    document.getElementById('clientSearchInput').value = '';
    document.getElementById('clientStatusFilter').value = '';
    renderClientsTable(true);
}

export function openClientModal(id = null) {
    const modal = document.getElementById('clientModal');
    if (id) {
        const c = crmClients.find(item => item.id === id);
        if (!c) return;
        document.getElementById('clientModalTitle').textContent = 'Editar Cliente / Lead';
        document.getElementById('clientEditId').value = c.id;
        document.getElementById('clientIdentifier').value = c.identifier || '';
        document.getElementById('clientName').value = c.fullName || '';
        document.getElementById('clientCompany').value = c.company || '';
        document.getElementById('clientEmail').value = c.email || '';
        document.getElementById('clientPhone').value = c.phone || '';
        document.getElementById('clientIndustry').value = c.sector || '';
        document.getElementById('clientStatus').value = c.status || 'LEAD';
        document.getElementById('clientValue').value = c.estimatedValue || 0;
    } else {
        document.getElementById('clientModalTitle').textContent = 'Registrar Nuevo Cliente / Lead';
        document.getElementById('clientEditId').value = '';
        document.getElementById('clientIdentifier').value = 'CLI-' + Math.floor(1000 + Math.random() * 9000);
        document.getElementById('clientName').value = '';
        document.getElementById('clientCompany').value = '';
        document.getElementById('clientEmail').value = '';
        document.getElementById('clientPhone').value = '';
        document.getElementById('clientIndustry').value = 'Tecnología';
        document.getElementById('clientStatus').value = 'LEAD';
        document.getElementById('clientValue').value = '5000';
    }
    modal.classList.remove('hidden');
}

export async function handleSaveClient(event) {
    event.preventDefault();
    const editId = document.getElementById('clientEditId').value;

    if (editId && !UUID_REGEX.test(editId)) {
        showToast('ID de cliente inválido. Recargando lista...', 'error');
        await renderClientsTable(true);
        return;
    }

    const payload = {
        identifier: document.getElementById('clientIdentifier').value.trim(),
        fullName: document.getElementById('clientName').value.trim(),
        company: document.getElementById('clientCompany').value.trim(),
        email: document.getElementById('clientEmail').value.trim(),
        phone: document.getElementById('clientPhone').value.trim(),
        sector: document.getElementById('clientIndustry').value.trim(),
        status: document.getElementById('clientStatus').value,
        estimatedValue: Number(document.getElementById('clientValue').value) || 0
    };

    try {
        if (editId) {
            await apiClient.put(`/clients/${editId}`, payload);
            showToast('Cliente actualizado correctamente.', 'success');
        } else {
            const created = await apiClient.post('/clients', payload);
            if (created && created.id) {
                crmClients.push(created);  // Agregar con el UUID real
            }
            showToast('Cliente registrado exitosamente.', 'success');
        }
    } catch (error) {
        showToast(error.message || 'Error al guardar cliente.', 'error');
    }
}

export async function deleteClient(id) {
    try {
        await apiClient.del(`/clients/${id}`);
        showToast('Cliente eliminado.', 'info');
    } catch {
        // Si es maqueta local, lo quitamos del array igualmente
    }
    crmClients = crmClients.filter(c => c.id !== id);
    renderClientsTable(false);
}

window.renderClientsTable = () => renderClientsTable(false);
window.resetClientFilters = resetClientFilters;
window.openClientModal = openClientModal;
window.handleSaveClient = handleSaveClient;
window.deleteClient = deleteClient;