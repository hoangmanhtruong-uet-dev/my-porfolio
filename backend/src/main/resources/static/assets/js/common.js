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
        background: rgba(0,0,0,0.85); backdrop-filter: blur(15px);
        display: none; align-items: center; justify-content: center; z-index: 20000;
        padding: 20px;
    `;
    adminModal.innerHTML = `
        <div class="admin-login-card">
            <div class="admin-lock-icon">
                <i class="fa-solid fa-lock"></i>
            </div>
            <h2 class="admin-title">Khu vực bị mật</h2>
            <p class="admin-subtitle">Nơi lưu giữ những kỉ niệm riêng từ ❤️</p>
            
            <div class="admin-input-wrapper">
                <i class="fa-solid fa-key admin-input-icon"></i>
                <input type="password" id="global-admin-pass" placeholder="Nhập mật mã..." class="admin-input">
            </div>
            
            <button onclick="submitAdminLogin()" class="admin-btn-primary">
                Truy cập <i class="fa-solid fa-chevron-right"></i>
            </button>
            
            <button onclick="closeAdminModal()" class="admin-btn-back">
                <i class="fa-solid fa-home"></i> Quay lại trang chủ
            </button>
            
            <div class="admin-hint">Gợi ý: 10/02</div>
        </div>
    `;
    document.body.appendChild(adminModal);
    
    // Add styles for admin modal
    const adminStyles = document.createElement('style');
    adminStyles.textContent = `
        .admin-login-card {
            background: linear-gradient(135deg, rgba(20, 12, 35, 0.95) 0%, rgba(35, 18, 55, 0.95) 100%);
            border: 2px solid rgba(168, 85, 247, 0.25);
            border-radius: 40px;
            padding: 50px 40px;
            max-width: 420px;
            width: 100%;
            text-align: center;
            box-shadow: 0 20px 60px rgba(168, 85, 247, 0.15), 
                        0 0 40px rgba(217, 70, 239, 0.1),
                        inset 0 1px 1px rgba(255, 255, 255, 0.1);
            animation: slideInUp 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
        }
        
        @keyframes slideInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .admin-lock-icon {
            font-size: 3.5rem;
            color: #d946ef;
            margin-bottom: 25px;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, rgba(217, 70, 239, 0.2), rgba(168, 85, 247, 0.15));
            border-radius: 20px;
            margin-left: auto;
            margin-right: auto;
            border: 1px solid rgba(217, 70, 239, 0.3);
        }
        
        .admin-title {
            font-size: 2rem;
            font-weight: 800;
            color: #ffffff;
            margin-bottom: 12px;
            letter-spacing: -0.5px;
        }
        
        .admin-subtitle {
            color: #a78bfa;
            font-size: 1rem;
            margin-bottom: 35px;
            line-height: 1.5;
            font-weight: 500;
        }
        
        .admin-input-wrapper {
            position: relative;
            margin-bottom: 25px;
        }
        
        .admin-input-icon {
            position: absolute;
            left: 18px;
            top: 50%;
            transform: translateY(-50%);
            color: #a855f7;
            font-size: 1.1rem;
        }
        
        .admin-input {
            width: 100%;
            padding: 16px 18px 16px 50px;
            background: rgba(0, 0, 0, 0.4);
            border: 2px solid rgba(168, 85, 247, 0.25);
            border-radius: 16px;
            color: #ffffff;
            font-size: 1rem;
            letter-spacing: 2px;
            outline: none;
            transition: all 0.3s ease;
            font-weight: 600;
        }
        
        .admin-input::placeholder {
            color: #7c3aed;
            opacity: 0.6;
            letter-spacing: 1px;
        }
        
        .admin-input:focus {
            background: rgba(0, 0, 0, 0.6);
            border-color: rgba(217, 70, 239, 0.6);
            box-shadow: 0 0 20px rgba(217, 70, 239, 0.2),
                        0 0 0 3px rgba(217, 70, 239, 0.1);
        }
        
        .admin-btn-primary {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, #a855f7 0%, #d946ef 100%);
            border: none;
            border-radius: 16px;
            color: #ffffff;
            font-size: 1.05rem;
            font-weight: 700;
            letter-spacing: 0.5px;
            cursor: pointer;
            margin-bottom: 12px;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }
        
        .admin-btn-primary:hover {
            background: linear-gradient(135deg, #b565ff 0%, #e055f7 100%);
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(217, 70, 239, 0.4),
                        0 0 20px rgba(168, 85, 247, 0.3);
        }
        
        .admin-btn-primary:active {
            transform: translateY(0);
        }
        
        .admin-btn-back {
            width: 100%;
            padding: 12px;
            background: transparent;
            border: 2px solid rgba(168, 85, 247, 0.3);
            border-radius: 14px;
            color: #a78bfa;
            font-size: 0.95rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            margin-bottom: 20px;
        }
        
        .admin-btn-back:hover {
            background: rgba(168, 85, 247, 0.1);
            border-color: rgba(168, 85, 247, 0.5);
            color: #d946ef;
        }
        
        .admin-hint {
            color: #7c3aed;
            font-size: 0.85rem;
            opacity: 0.7;
            font-weight: 500;
            letter-spacing: 0.5px;
        }
        
        @media (max-width: 480px) {
            .admin-login-card {
                padding: 40px 30px;
                border-radius: 30px;
            }
            
            .admin-title {
                font-size: 1.6rem;
            }
            
            .admin-subtitle {
                font-size: 0.9rem;
            }
            
            .admin-lock-icon {
                font-size: 3rem;
                width: 70px;
                height: 70px;
            }
        }
    `;
    document.head.appendChild(adminStyles);
});

window.addEventListener('load', () => {
    const loader = document.getElementById('page-loader');
    if (loader) {
        setTimeout(() => {
            loader.classList.add('hidden');
        }, 300);
    }
});

// Safety net: ẩn loader sau tối đa 2s dù window.load chưa fire
setTimeout(() => {
    const loader = document.getElementById('page-loader');
    if (loader && !loader.classList.contains('hidden')) {
        loader.classList.add('hidden');
    }
}, 2000);

// Auth and Navbar Logic
if (typeof API_CONFIG === 'undefined') {
    window.API_CONFIG = {
        // Cách 1: Đường dẫn tương đối — production tự gọi đúng domain, local trỏ về localhost:8080
        BASE_URL: (window.location.hostname === '127.0.0.1' || 
                   window.location.hostname === 'localhost' || 
                   window.location.protocol === 'file:') 
            ? 'http://localhost:8080'
            : '', // Để trống → trình duyệt tự gọi đúng domain hiện tại (Render, custom domain, v.v.)
    };
}

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
