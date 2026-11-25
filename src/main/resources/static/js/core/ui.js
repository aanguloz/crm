import { getApiBaseUrl, setApiBaseUrl, updateApiUrlDisplays} from "./apiClient.js";

export function escapeHtml(str) {
    return String(str ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

export function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    const styles = {
        success: 'bg-slate-900/95 border-emerald-500/40 text-emerald-200',
        error: 'bg-slate-900/95 border-rose-500/40 text-rose-200',
        info: 'bg-slate-900/95 border-indigo-500/40 text-indigo-200'
    };
    const icons = {
        success: 'fa-circle-check text-emerald-400',
        error: 'fa-circle-exclamation text-rose-400',
        info: 'fa-circle-info text-indigo-400'
    };

    toast.className = `pointer-events-auto flex items-start gap-3 p-3.5 rounded-xl border shadow-2xl backdrop-blur-md text-xs transition-all duration-300 translate-y-2 opacity-0 ${styles[type] || styles.info}`;
    toast.innerHTML = `<i class="fa-solid ${icons[type] || icons.info} text-base mt-0.5"></i><span class="leading-relaxed font-medium">${escapeHtml(message)}</span>`;

    container.appendChild(toast);
    requestAnimationFrame(() => toast.classList.remove('translate-y-2', 'opacity-0'));

    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-y-2');
        setTimeout(() => toast.remove(), 300);
    }, 3800);

}

export function closeModal(id) {
    document.getElementById(id)?.classList.add('hidden');
}

export function openConfigModal() {
    updateApiUrlDisplays();
    document.getElementById('configApiModal')?.classList.remove('hidden');
}

export function saveApiBaseUrlFromModal() {
    const val = document.getElementById('modalApiUrlInput')?.value;
    if (val?.trim()) {
        setApiBaseUrl(val);
        closeModal('configApiModal');
        showToast(`URL base actualizada a: ${getApiBaseUrl()}`, 'success');
    }
}

export function saveApiBaseUrlFromInline() {
    const val = document.getElementById('inlineApiUrlInput')?.value;
    if (val?.trim()) {
        setApiBaseUrl(val);
        showToast(`Endpoint base actualizado: ${getApiBaseUrl()}`, 'success');
    }
}

export function togglePasswordVisibility(inputId, btn) {
    const input = document.getElementById(inputId);
    const icon = btn.querySelector('i');
    if (input.type === 'password') {
        input.type = 'text';
        icon.className = 'fa-solid fa-eye-slash text-sm';
    } else {
        input.type = 'password';
        icon.className = 'fa-solid fa-eye text-sm';
    }
}

window.closeModal = closeModal;
window.openConfigModal = openConfigModal;
window.saveApiBaseUrlFromModal = saveApiBaseUrlFromModal;
window.saveApiBaseUrlFromInline = saveApiBaseUrlFromInline;
window.togglePasswordVisibility = togglePasswordVisibility;
