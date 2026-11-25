import { apiClient } from '../core/apiClient.js';
import { showToast, closeModal, escapeHtml } from '../core/ui.js';

const PIPELINE_STAGES = [
    { key: 'PROSPECTION', label: 'Prospección', dot: 'bg-sky-400' },
    { key: 'QUALIFICATION', label: 'Calificación', dot: 'bg-indigo-400' },
    { key: 'PROPOSAL', label: 'Propuesta', dot: 'bg-purple-400' },
    { key: 'NEGOTIATION', label: 'Negociación', dot: 'bg-amber-400' },
    { key: 'CLOSED_WON', label: 'Cerrado Ganado', dot: 'bg-emerald-400' }
];

let crmDeals = [
    { id: 'd-01', title: 'Licenciamiento CRM Enterprise', company: 'TechCorp Andina', amount: 45000, stage: 'NEGOTIATION' },
    { id: 'd-02', title: 'Automatización Flota GPS', company: 'Logística del Sur S.A.', amount: 28500, stage: 'PROPOSAL' },
    { id: 'd-03', title: 'Portal Pacientes & Citas', company: 'Clínica San Gabriel', amount: 18000, stage: 'QUALIFICATION' },
    { id: 'd-04', title: 'Módulo BI & Analítica', company: 'Minera Cerro Verde II', amount: 64000, stage: 'CLOSED_WON' },
    { id: 'd-05', title: 'Integración Pasarela Pagos', company: 'Financiera CrediMás', amount: 22000, stage: 'PROSPECTION' }
];

export async function loadPipeline() {
    await renderKanbanBoard(true);
}

export async function renderKanbanBoard(fetchFromServer = false) {
    const board = document.getElementById('kanbanBoard');
    if (!board) return;

    if (fetchFromServer) {
        try {
            const response = await apiClient.get('/deal');
            if (Array.isArray(response)) crmDeals = response;
        } catch (e) {
            console.warn('Usando respaldo local de deals:', e.message);
        }
    }

    board.innerHTML = PIPELINE_STAGES.map((stage, stageIdx) => {
        const stageDeals = crmDeals.filter(d => d.stage === stage.key);
        const totalAmount = stageDeals.reduce((acc, d) => acc + (Number(d.amount) || 0), 0);

        return `
        <div class="bg-slate-900/70 border border-slate-800 rounded-2xl p-3.5 flex flex-col gap-3 min-h-[380px]">
            <div class="pb-2.5 border-b border-slate-800 flex items-center justify-between">
                <div class="flex items-center gap-2">
                    <span class="w-2.5 h-2.5 rounded-full ${stage.dot}"></span>
                    <h4 class="text-xs font-bold uppercase tracking-wider text-slate-200">${stage.label}</h4>
                </div>
                <span class="text-[11px] font-mono bg-slate-800 text-slate-300 px-2 py-0.5 rounded-md">${stageDeals.length}</span>
            </div>
            <div class="text-[11px] font-mono text-slate-400 bg-slate-950/70 px-2.5 py-1.5 rounded-lg border border-slate-800/80 flex justify-between">
                <span>Total Etapa:</span><span class="text-white font-semibold">$${totalAmount.toLocaleString()}</span>
            </div>
            <div class="space-y-2.5 flex-1">
                ${stageDeals.map(deal => `
                    <div class="bg-slate-950/90 border border-slate-800 hover:border-indigo-500/40 rounded-xl p-3 shadow transition-all space-y-2">
                        <p class="text-xs font-bold text-white leading-snug">${escapeHtml(deal.title)}</p>
                        <p class="text-[11px] text-slate-400 flex items-center gap-1.5"><i class="fa-solid fa-building text-[10px] text-slate-500"></i> ${escapeHtml(deal.company)}</p>
                        <div class="pt-2 border-t border-slate-800/80 flex items-center justify-between">
                            <span class="font-mono text-xs font-bold text-emerald-400">$${Number(deal.amount || 0).toLocaleString()}</span>
                            <div class="flex items-center gap-1">
                                ${stageIdx > 0 ? `<button onclick="moveDealStage('${deal.id}', -1)" class="p-1 text-slate-400 hover:text-white bg-slate-800 rounded text-[10px]"><i class="fa-solid fa-chevron-left"></i></button>` : ''}
                                ${stageIdx < PIPELINE_STAGES.length - 1 ? `<button onclick="moveDealStage('${deal.id}', 1)" class="p-1 text-indigo-300 hover:text-white bg-indigo-600/30 hover:bg-indigo-600 rounded text-[10px]"><i class="fa-solid fa-chevron-right"></i></button>` : ''}
                            </div>
                        </div>
                    </div>
                `).join('')}
            </div>
        </div>`;
    }).join('');
}

export function moveDealStage(dealId, direction) {
    const deal = crmDeals.find(d => d.id === dealId);
    if (!deal) return;
    const currentIdx = PIPELINE_STAGES.findIndex(s => s.key === deal.stage);
    const nextIdx = currentIdx + direction;
    if (nextIdx >= 0 && nextIdx < PIPELINE_STAGES.length) {
        deal.stage = PIPELINE_STAGES[nextIdx].key;
        renderKanbanBoard(false);
        showToast(`Oportunidad movida a "${PIPELINE_STAGES[nextIdx].label}".`, 'info');
    }
}

export function openDealModal() {
    document.getElementById('dealModal').classList.remove('hidden');
}

export async function handleSaveDeal(event) {
    event.preventDefault();
    const dealReqDTO = {
        title: document.getElementById('dealTitle').value.trim(),
        company: document.getElementById('dealCompany').value.trim(),
        amount: Number(document.getElementById('dealAmount').value) || 0,
        stage: document.getElementById('dealStage').value
    };

    try {
        const createdDeal = await apiClient.post('/deal/', dealReqDTO);
        crmDeals.unshift(createdDeal);
        closeModal('dealModal');
        renderKanbanBoard(false);
        showToast('Oportunidad creada y sincronizada con el backend.', 'success');
    } catch (error) {
        showToast('Error al crear oportunidad: ' + error.message, 'error');
    }
}

window.moveDealStage = moveDealStage;
window.openDealModal = openDealModal;
window.handleSaveDeal = handleSaveDeal;