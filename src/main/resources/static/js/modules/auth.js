import { apiClient, STORAGE_KEYS, updateApiUrlDisplays } from '../core/apiClient.js';
import { showToast, closeModal } from '../core/ui.js';

export function switchAuthTab(tab) {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const tabLoginBtn = document.getElementById('tabLoginBtn');
    const tabRegisterBtn = document.getElementById('tabRegisterBtn');

    if (tab === 'login') {
        loginForm.classList.remove('hidden');
        registerForm.classList.add('hidden');
        tabLoginBtn.className = 'py-2.5 text-sm font-semibold rounded-lg bg-indigo-600 text-white shadow transition-all';
        tabRegisterBtn.className = 'py-2.5 text-sm font-semibold rounded-lg text-slate-400 hover:text-white transition-all';
    } else {
        loginForm.classList.add('hidden');
        registerForm.classList.remove('hidden');
        tabRegisterBtn.className = 'py-2.5 text-sm font-semibold rounded-lg bg-emerald-600 text-white shadow transition-all';
        tabLoginBtn.className = 'py-2.5 text-sm font-semibold rounded-lg text-slate-400 hover:text-white transition-all';
    }
}

export async function handleLoginSubmit(event) {
    event.preventDefault();
    const btn = document.getElementById('btnLoginSubmit');
    const originalHtml = btn.innerHTML;

    const loginReqDTO = {
        userName: document.getElementById('loginUserName').value.trim(),
        password: document.getElementById('loginPassword').value
    };

    try {
        btn.disabled = true;
        btn.innerHTML = `<i class="fa-solid fa-circle-notch animate-spin"></i> Autenticando...`;

        const authRespDTO = await apiClient.post('/users/login', loginReqDTO, { skipAuth: true });
        if (!authRespDTO.accessToken) throw new Error('La respuesta no contiene accessToken');

        localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, authRespDTO.accessToken);
        localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, authRespDTO.refreshToken || '');
        localStorage.setItem(STORAGE_KEYS.USERNAME, loginReqDTO.userName);
        localStorage.setItem(STORAGE_KEYS.IS_DEMO, 'false');

        window.location.href = '/app.html';
    } catch (error) {
        showToast(error.message || 'Error al conectar con Spring Boot.', 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = originalHtml;
    }
}

export async function handleRegisterSubmit(event) {
    event.preventDefault();
    const btn = document.getElementById('btnRegisterSubmit');
    const originalHtml = btn.innerHTML;

    const userReqDTO = {
        email: document.getElementById('regEmail').value.trim(),
        username: document.getElementById('regUsername').value.trim(),
        password: document.getElementById('regPassword').value
    };

    try {
        btn.disabled = true;
        btn.innerHTML = `<i class="fa-solid fa-circle-notch animate-spin"></i> Registrando...`;

        // CORREGIDO: apiClient.post ya devuelve el JSON parseado (UserRespDTO)
        const userRespDTO = await apiClient.post('/users/', userReqDTO, { skipAuth: true });
        cacheRegisteredUser(userRespDTO, 'Registro Público');

        showToast(`Usuario "${userRespDTO.username}" creado con éxito.`, 'success');
        document.getElementById('loginUserName').value = userRespDTO.username;
        document.getElementById('registerForm').reset();
        switchAuthTab('login');
    } catch (error) {
        showToast(error.message, 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = originalHtml;
    }
}

export function simulateJwtLogin() {
    localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock.token");
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock.refresh");
    localStorage.setItem(STORAGE_KEYS.USERNAME, 'admin.demo');
    localStorage.setItem(STORAGE_KEYS.IS_DEMO, 'true');
    window.location.href = '/app.html';
}

export function logout() {
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USERNAME);
    localStorage.removeItem(STORAGE_KEYS.IS_DEMO);
    closeModal('jwtInspectorModal');
    window.location.href = '/home-crm';
}

export function cacheRegisteredUser(userRespDTO, origin) {
    const existing = JSON.parse(localStorage.getItem(STORAGE_KEYS.REGISTERED_USERS) || '[]');
    existing.unshift({
        id: userRespDTO.id || 'UUID-PENDIENTE',
        username: userRespDTO.username,
        email: userRespDTO.email,
        origin
    });
    localStorage.setItem(STORAGE_KEYS.REGISTERED_USERS, JSON.stringify(existing));
}

// Exponer al HTML
window.switchAuthTab = switchAuthTab;
window.handleLoginSubmit = handleLoginSubmit;
window.handleRegisterSubmit = handleRegisterSubmit;
window.simulateJwtLogin = simulateJwtLogin;
window.logout = logout;

// Si estamos en index.html y ya hay sesión, redirigir al workspace
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('authScreen')) {
        updateApiUrlDisplays();
        if (localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN)) {
            window.location.href = '/app.html';
        }
    }
});