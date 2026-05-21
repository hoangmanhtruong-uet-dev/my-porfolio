// API Configuration for deployment
// Page Transition Injector
document.addEventListener('DOMContentLoaded', () => {
    const loader = document.createElement('div');
    loader.className = 'page-transition';
    loader.id = 'page-loader';
    loader.innerHTML = `
        <div class="loader-content">
            <div class="loader-logo">Mtruong<span>_dev</span></div>
            <div class="loader-bar"></div>
        </div>
    `;
    document.body.prepend(loader);

    // Inject Admin Modal
    const adminModal = document.createElement('div');
    adminModal.id = 'global-admin-modal';
    adminModal.style.cssText = `
        position: fixed; top: 0; left: 0; width: 100%; height: 100%;
        background: rgba(0,0,0,0.8); backdrop-filter: blur(10px);
        display: none; align-items: center; justify-content: center; z-index: 20000;
    `;
    adminModal.innerHTML = `
        <div class="login-card" style="max-width: 400px; padding: 40px; text-align: center; background: rgba(30, 41, 59, 0.7); border: 1px solid rgba(168, 85, 247, 0.3); border-radius: 30px;">
            <div style="font-size: 2.5rem; color: #a855f7; margin-bottom: 20px;"><i class="fa-solid fa-user-shield"></i></div>
            <h2 style="color: white; margin-bottom: 10px;">Admin Access</h2>
            <p style="color: #94a3b8; margin-bottom: 30px; font-size: 0.9rem;">Nhập mã truy cập để tiếp tục</p>
            <input type="password" id="global-admin-pass" placeholder="••••" style="width: 100%; padding: 15px; background: rgba(0,0,0,0.3); border: 1px solid rgba(255,255,255,0.1); border-radius: 15px; color: white; text-align: center; font-size: 1.5rem; letter-spacing: 5px; margin-bottom: 20px; outline: none;">
            <div style="display: flex; gap: 10px;">
                <button onclick="closeAdminModal()" style="flex: 1; padding: 12px; border-radius: 12px; background: rgba(255,255,255,0.05); color: white; border: none; cursor: pointer;">Hủy</button>
                <button onclick="submitAdminLogin()" style="flex: 2; padding: 12px; border-radius: 12px; background: linear-gradient(135deg, #a855f7, #d946ef); color: white; border: none; cursor: pointer; font-weight: 700;">Đăng Nhập</button>
            </div>
        </div>
    `;
    document.body.appendChild(adminModal);
});

window.addEventListener('load', () => {
    const loader = document.getElementById('page-loader');
    if (loader) {
        setTimeout(() => {
            loader.classList.add('hidden');
        }, 300);
    }
});

// Auth and Navbar Logic
const API_CONFIG = {
    // Tự động nhận diện môi trường: Nếu chạy trên máy (localhost/file) thì trỏ về backend local
    BASE_URL: (window.location.hostname === '127.0.0.1' || 
               window.location.hostname === 'localhost' || 
               window.location.protocol === 'file:') 
        ? 'http://localhost:8080'
        : 'https://my-portfolio-api.onrender.com', // Thay bằng URL Render thực tế của bạn
};

document.addEventListener('DOMContentLoaded', () => {
    // 1. Unified Navbar Scroll Handling
    const navbar = document.querySelector('.navbar-unified');
    window.addEventListener('scroll', () => {
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });

    // 2. Premium Theme Switching Logic
    const themeBtn = document.querySelector('.theme-toggle');
    const currentTheme = localStorage.getItem('theme') || 'dark';
    
    document.documentElement.setAttribute('data-theme', currentTheme);
    updateThemeIcon(currentTheme);

    themeBtn?.addEventListener('click', () => {
        let theme = document.documentElement.getAttribute('data-theme');
        let newTheme = theme === 'dark' ? 'light' : 'dark';
        
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        updateThemeIcon(newTheme);
    });

    function updateThemeIcon(theme) {
        const icon = themeBtn?.querySelector('i');
        if (icon) {
            icon.className = theme === 'dark' ? 'fa-solid fa-moon' : 'fa-solid fa-sun';
        }
    }

    // 3. Scroll Reveal Engine (Intersection Observer)
    const revealElements = document.querySelectorAll('.reveal, .reveal-left, .reveal-right');
    const revealObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('active');
                // Optional: Unobserve after activation for performance
                // revealObserver.unobserve(entry.target);
            }
        });
    }, {
        threshold: 0.15,
        rootMargin: '0px 0px -50px 0px'
    });

    revealElements.forEach(el => revealObserver.observe(el));

    // 4. Global Auth State Handling for Navbar
    updateNavbarAuth();
});

function updateNavbarAuth() {
    const isAdmin = sessionStorage.getItem('isAdmin') === 'true';
    const guestLinks = document.getElementById('guest-links');
    const userLinks = document.getElementById('user-links');
    const navUserName = document.getElementById('nav-user-name');
    const adminLabel = document.getElementById('admin-login-label');

    if (!guestLinks || !userLinks) return;

    if (isAdmin) {
        guestLinks.style.display = 'none';
        userLinks.style.display = 'flex';
        if (navUserName) navUserName.innerHTML = '<i class="fa-solid fa-user-shield"></i> Admin';
    } else {
        guestLinks.style.display = 'flex';
        userLinks.style.display = 'none';
        if (adminLabel) adminLabel.innerText = 'Đăng nhập Admin';
    }
}

function logout() {
    sessionStorage.removeItem('isAdmin');
    localStorage.removeItem('current_student'); // Keep student logout just in case
    window.location.href = (window.location.pathname.includes('index.html') || window.location.pathname.endsWith('/')) ? 'index.html' : '../index.html';
}

window.promptAdmin = function() {
    const modal = document.getElementById('global-admin-modal');
    if (modal) modal.style.display = 'flex';
    document.getElementById('global-admin-pass')?.focus();
};

window.closeAdminModal = function() {
    const modal = document.getElementById('global-admin-modal');
    if (modal) modal.style.display = 'none';
    const passInput = document.getElementById('global-admin-pass');
    if (passInput) passInput.value = '';
};

window.submitAdminLogin = function() {
    const passInput = document.getElementById('global-admin-pass');
    const pass = passInput ? passInput.value : '';
    if (pass === "1002") {
        sessionStorage.setItem('isAdmin', 'true');
        alert("Đăng nhập Admin thành công!");
        updateNavbarAuth();
        closeAdminModal();
        location.reload();
    } else {
        alert("Sai mã truy cập!");
        if (passInput) passInput.value = '';
    }
};

// Enter key for admin modal
document.addEventListener('keypress', (e) => {
    if (e.key === 'Enter' && document.getElementById('global-admin-modal')?.style.display === 'flex') {
        submitAdminLogin();
    }
});
