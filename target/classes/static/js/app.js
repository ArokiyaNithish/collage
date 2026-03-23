// FILE: src/main/resources/static/js/app.js
// Smart College Workshop Management System — Utility Library

const BASE_URL = '';

// ── Storage helpers ──────────────────────────────────────────────────────
const getToken    = () => localStorage.getItem('token');
const getRole     = () => localStorage.getItem('role');
const getUserId   = () => localStorage.getItem('userId');
const getFullName = () => localStorage.getItem('fullName');

const authHeader = () => ({
    'Authorization': 'Bearer ' + getToken(),
    'Content-Type': 'application/json'
});

function logout() {
    localStorage.clear();
    window.location.href = '/index.html';
}

// ── API fetch wrapper ────────────────────────────────────────────────────
async function apiFetch(method, path, body = null, withAuth = true) {
    const headers = withAuth
        ? authHeader()
        : { 'Content-Type': 'application/json' };

    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);

    try {
        const res = await fetch(BASE_URL + path, options);

        if (res.status === 401) {
            localStorage.clear();
            window.location.href = '/index.html';
            return;
        }

        const text = await res.text();
        let data = null;
        try { data = text ? JSON.parse(text) : null; } catch { data = { message: text }; }

        if (!res.ok) {
            throw { status: res.status, error: data?.error, message: data?.message || 'An error occurred', errors: data?.errors };
        }

        return data;
    } catch (err) {
        if (err.status) throw err;
        throw { status: 0, error: 'NETWORK_ERROR', message: 'Network error. Please check your connection.' };
    }
}

// ── Toast notifications ──────────────────────────────────────────────────
function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<span class="toast-icon">${icons[type] || 'ℹ️'}</span><span class="toast-body">${message}</span>`;

    container.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('removing');
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// ── Button spinner ───────────────────────────────────────────────────────
function showSpinner(btn) {
    btn.dataset.original = btn.innerHTML;
    btn.innerHTML = '<span class="spinner"></span>';
    btn.disabled = true;
}

function hideSpinner(btn) {
    if (btn.dataset.original) btn.innerHTML = btn.dataset.original;
    btn.disabled = false;
}

// ── Formatters ───────────────────────────────────────────────────────────
function formatDate(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
           + ', '
           + d.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' });
}

function formatDateShort(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

function formatCurrency(amount) {
    if (amount == null) return '₹0.00';
    return '₹ ' + parseFloat(amount).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function getInitials(name) {
    if (!name) return '?';
    return name.split(' ').map(p => p[0]).join('').substring(0, 2).toUpperCase();
}

function getAvatarColor(index) {
    const num = (Math.abs(index) % 7) + 1;
    return `av${num}`;
}

function formatCardNumber(val) {
    const digits = val.replace(/\D/g, '').substring(0, 16);
    return digits.replace(/(.{4})/g, '$1 ').trim();
}

function getBadgeClass(value) {
    if (!value) return '';
    const map = {
        PENDING: 'badge-pending', CONFIRMED: 'badge-confirmed', CANCELLED: 'badge-cancelled',
        SUCCESS: 'badge-success', FAILED: 'badge-failed', REFUNDED: 'badge-refunded',
        NOT_PAID: 'badge-not_paid', UPCOMING: 'badge-upcoming', ONGOING: 'badge-ongoing',
        COMPLETED: 'badge-completed', ADMIN: 'badge-admin', STUDENT: 'badge-student',
        CARD: 'badge-card', UPI: 'badge-upi', NET_BANKING: 'badge-net_banking', CASH: 'badge-cash'
    };
    return map[value.toUpperCase()] || 'badge-pending';
}

function getCategoryClass(category) {
    if (!category) return 'cat-default';
    const c = category.toLowerCase();
    if (c.includes('web')) return 'cat-web';
    if (c.includes('ai') || c.includes('machine') || c.includes('artifici')) return 'cat-ai';
    if (c.includes('cloud')) return 'cat-cloud';
    if (c.includes('cyber') || c.includes('security')) return 'cat-security';
    if (c.includes('data')) return 'cat-data';
    return 'cat-default';
}

// ── Form error helpers ───────────────────────────────────────────────────
function showFieldError(fieldId, message) {
    const field = document.getElementById(fieldId);
    if (field) {
        field.classList.add('error');
        let errEl = field.parentElement.querySelector('.field-error');
        if (!errEl) {
            errEl = document.createElement('span');
            errEl.className = 'field-error';
            field.parentElement.appendChild(errEl);
        }
        errEl.textContent = message;
    }
}

function clearFieldError(fieldId) {
    const field = document.getElementById(fieldId);
    if (field) {
        field.classList.remove('error');
        const errEl = field.parentElement.querySelector('.field-error');
        if (errEl) errEl.textContent = '';
    }
}

function clearAllErrors() {
    document.querySelectorAll('.field-error').forEach(el => el.textContent = '');
    document.querySelectorAll('.form-control.error').forEach(el => el.classList.remove('error'));
}

function renderApiErrors(errorsObj) {
    if (!errorsObj) return;
    Object.entries(errorsObj).forEach(([field, msg]) => showFieldError(field, msg));
}

// ── Password helpers ─────────────────────────────────────────────────────
function checkPasswordStrength(password) {
    if (!password || password.length < 6) return 'weak';
    let score = 0;
    if (password.length >= 8) score++;
    if (/[A-Z]/.test(password)) score++;
    if (/\d/.test(password)) score++;
    if (/[^A-Za-z0-9]/.test(password)) score++;
    if (score <= 1) return 'weak';
    if (score <= 2) return 'medium';
    return 'strong';
}

function updateStrengthMeter(strength, meterId) {
    const meter = document.getElementById(meterId || 'strength-meter');
    const label = document.getElementById((meterId || 'strength-meter') + '-label');
    if (!meter) return;
    meter.className = 'strength-meter strength-' + strength;
    if (label) {
        const labels = { weak: '🔴 Weak', medium: '🟡 Medium', strong: '🟢 Strong' };
        label.textContent = labels[strength] || '';
        label.style.color = strength === 'weak' ? '#e74c3c' : strength === 'medium' ? '#f39c12' : '#27ae60';
    }
}

// ── Guard ────────────────────────────────────────────────────────────────
function requireAuth(expectedRole) {
    const token = getToken();
    const role = getRole();
    if (!token) { window.location.href = '/index.html'; return false; }
    if (expectedRole && role !== expectedRole) { window.location.href = '/index.html'; return false; }
    return true;
}

// ── Confetti effect ──────────────────────────────────────────────────────
function showConfetti() {
    const colors = ['#f0a500', '#1a3c6e', '#27ae60', '#e74c3c', '#8e44ad', '#2980b9'];
    const container = document.createElement('div');
    container.className = 'confetti-container';
    document.body.appendChild(container);

    for (let i = 0; i < 60; i++) {
        const piece = document.createElement('div');
        piece.className = 'confetti-piece';
        piece.style.cssText = `
            left: ${Math.random() * 100}vw;
            background: ${colors[Math.floor(Math.random() * colors.length)]};
            --dur: ${2 + Math.random() * 2}s;
            --delay: ${Math.random() * 1}s;
            --dx: ${(Math.random() - 0.5) * 200}px;
            border-radius: ${Math.random() > 0.5 ? '50%' : '0'};
            width: ${6 + Math.random() * 8}px;
            height: ${6 + Math.random() * 8}px;
        `;
        container.appendChild(piece);
    }

    setTimeout(() => container.remove(), 4000);
}
