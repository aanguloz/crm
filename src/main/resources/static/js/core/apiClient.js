export const STORAGE_KEYS = {
    ACCESS_TOKEN: 'crm_access_token',
    REFRESH_TOKEN: 'crm_refresh_token',
    USERNAME: 'crm_username',
    IS_DEMO: 'crm_is_demo_mode',
    API_BASE_URL: 'crm_api_base_url',
    REGISTERED_USERS: 'crm_registered_users_cache'
};

export function getApiBaseUrl() {
    return localStorage.getItem(STORAGE_KEYS.API_BASE_URL) || 'http://localhost:8080/api/v1';
}

export function setApiBaseUrl(url) {
    const cleanUrl = url.trim().replace(/\/+$/, '');
    localStorage.setItem(STORAGE_KEYS.API_BASE_URL, cleanUrl);
    updateApiUrlDisplays();
}

export function updateApiUrlDisplays() {
    const currentUrl = getApiBaseUrl();
    const authLabel = document.getElementById('authCurrentApiUrl');
    const modalInput = document.getElementById('modalApiUrlInput');
    const inlineInput = document.getElementById('inlineApiUrlInput');
    if (authLabel) authLabel.textContent = `API: ${currentUrl}`;
    if (modalInput) modalInput.value = currentUrl;
    if (inlineInput) inlineInput.value = currentUrl;
}

export const apiClient = {
    get baseUrl() {
        return getApiBaseUrl().replace(/\/+$/, '');
    },
    get token() {
        return localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
    },

    async request(path, options = {}) {
        const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
        if (this.token && !options.skipAuth) {
            headers['Authorization'] = 'Bearer ' + this.token;
        }

        const res = await fetch(this.baseUrl + path, { ...options, headers });
        const ct = res.headers.get('content-type') || '';
        const body = ct.includes('application/json') ? await res.json() : await res.text();

        if (!res.ok) {
            if (res.status === 401 && !options.skipAuth && !options.allow401) {
                localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
                localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
                localStorage.removeItem(STORAGE_KEYS.USERNAME);

                // ⚠️ Asegúrate de que esta ruta exista (usa '/home-crm' si '/home.html' da 404)
                window.location.href = '/home-crm';
            }
            throw new Error(typeof body === 'string' ? body : (body.message || `Error ${res.status}`));
        }
        return body;
    },

    get(p, opts = {}) { return this.request(p, opts); },  // ← Aceptar opciones
    // get(p)              { return this.request(p); },
    post(p, body, opts) { return this.request(p, { method: 'POST', body: JSON.stringify(body), ...opts }); },
    put(p, body)        { return this.request(p, { method: 'PUT',  body: JSON.stringify(body) }); },
    patch(p, body)      { return this.request(p, { method: 'PATCH', body: body ? JSON.stringify(body) : undefined }); },
    del(p)              { return this.request(p, { method: 'DELETE' }); }
};