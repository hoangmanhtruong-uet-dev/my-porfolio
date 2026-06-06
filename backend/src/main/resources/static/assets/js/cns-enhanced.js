/**
 * CNS Enhanced Interactions — cns-enhanced.js
 * Hiệu ứng tương tác cao cấp cho trang bài tập Portfolio CNS
 */

(function () {
    'use strict';

    /* ════════════════════════════════
       1. Aurora blobs injection
    ════════════════════════════════ */
    function injectAurora() {
        const wrap = document.createElement('div');
        wrap.className = 'aurora-wrap';
        wrap.innerHTML = `
            <div class="aurora-blob a1"></div>
            <div class="aurora-blob a2"></div>
            <div class="aurora-blob a3"></div>
        `;
        document.body.insertBefore(wrap, document.body.firstChild);
    }

    /* ════════════════════════════════
       2. Reading Progress Bar
    ════════════════════════════════ */
    function initReadingProgress() {
        const bar = document.createElement('div');
        bar.id = 'reading-progress';
        document.body.appendChild(bar);

        window.addEventListener('scroll', () => {
            const docH = document.documentElement.scrollHeight - window.innerHeight;
            const pct  = docH > 0 ? (window.scrollY / docH) * 100 : 0;
            bar.style.width = Math.min(pct, 100) + '%';
        }, { passive: true });
    }

    /* ════════════════════════════════
       3. Scroll-to-top Button
    ════════════════════════════════ */
    function initScrollTop() {
        const btn = document.createElement('button');
        btn.id = 'scroll-top-btn';
        btn.title = 'Lên đầu trang';
        btn.innerHTML = '<i class="fa-solid fa-chevron-up"></i>';
        document.body.appendChild(btn);

        window.addEventListener('scroll', () => {
            if (window.scrollY > 400) {
                btn.classList.add('visible');
            } else {
                btn.classList.remove('visible');
            }
        }, { passive: true });

        btn.addEventListener('click', () => {
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    }

    /* ════════════════════════════════
       4. Card Mouse Glow (magnetic gradient)
    ════════════════════════════════ */
    function initCardGlow() {
        function applyGlow(card) {
            card.addEventListener('mousemove', (e) => {
                const rect = card.getBoundingClientRect();
                const x = ((e.clientX - rect.left) / rect.width) * 100;
                const y = ((e.clientY - rect.top) / rect.height) * 100;
                card.style.setProperty('--mx', x + '%');
                card.style.setProperty('--my', y + '%');
            });
        }

        // Observe for dynamically rendered cards
        const observer = new MutationObserver(() => {
            document.querySelectorAll('.step-card, .sec-card, .assign-card, .source-card, .analysis-card')
                .forEach(card => {
                    if (!card.dataset.glowInit) {
                        card.dataset.glowInit = '1';
                        applyGlow(card);
                    }
                });
        });

        observer.observe(document.body, { childList: true, subtree: true });

        // Also apply to already-present cards
        document.querySelectorAll('.step-card, .sec-card, .assign-card, .source-card, .analysis-card')
            .forEach(card => {
                if (!card.dataset.glowInit) {
                    card.dataset.glowInit = '1';
                    applyGlow(card);
                }
            });
    }

    /* ════════════════════════════════
       5. Toast Notification System
    ════════════════════════════════ */
    const toast = (() => {
        let el = null;
        let timer = null;

        function getEl() {
            if (!el) {
                el = document.createElement('div');
                el.id = 'cns-toast';
                document.body.appendChild(el);
            }
            return el;
        }

        return {
            show(msg, type = 'info', duration = 2800) {
                const icons = { success: 'fa-circle-check', error: 'fa-circle-xmark', info: 'fa-circle-info' };
                const t = getEl();
                t.className = `show ${type}`;
                t.innerHTML = `<i class="fa-solid ${icons[type] || icons.info}"></i> ${msg}`;
                if (timer) clearTimeout(timer);
                timer = setTimeout(() => {
                    t.classList.remove('show');
                }, duration);
            }
        };
    })();

    // Expose globally
    window.cnsToast = toast;

    /* ════════════════════════════════
       6. Confetti on 100% completion
    ════════════════════════════════ */
    function triggerConfetti() {
        const colors = ['#3b82f6', '#06b6d4', '#a855f7', '#22c55e', '#f97316', '#facc15'];
        for (let i = 0; i < 60; i++) {
            setTimeout(() => {
                const piece = document.createElement('div');
                piece.className = 'confetti-piece';
                piece.style.cssText = `
                    left: ${Math.random() * 100}vw;
                    width: ${6 + Math.random() * 8}px;
                    height: ${6 + Math.random() * 8}px;
                    background: ${colors[Math.floor(Math.random() * colors.length)]};
                    border-radius: ${Math.random() > 0.5 ? '50%' : '2px'};
                    animation-duration: ${2 + Math.random() * 2}s;
                    animation-delay: ${Math.random() * 0.5}s;
                `;
                document.body.appendChild(piece);
                setTimeout(() => piece.remove(), 4000);
            }, i * 30);
        }
    }

    // Expose globally
    window.triggerConfetti = triggerConfetti;

    /* ════════════════════════════════
       7. Hook into toggleDone for toast
    ════════════════════════════════ */
    function patchProgressEvents() {
        // We watch for ring-pct changes to detect completion
        const pctEl = document.getElementById('ring-pct');
        if (!pctEl) return;

        const config = { childList: true, subtree: true, characterData: true };
        let prevPct = -1;

        const ob = new MutationObserver(() => {
            const val = parseInt(pctEl.textContent) || 0;
            if (val !== prevPct) {
                if (val > prevPct && prevPct >= 0) {
                    if (val === 100) {
                        toast.show('🎉 Hoàn thành tất cả các bước!', 'success', 4000);
                        setTimeout(triggerConfetti, 300);
                    } else {
                        toast.show(`✓ Đã đánh dấu hoàn thành`, 'success', 2000);
                    }
                } else if (val < prevPct && prevPct >= 0) {
                    toast.show('Đã bỏ đánh dấu', 'info', 1800);
                }
                prevPct = val;
            }
        });
        ob.observe(pctEl, config);
    }

    /* ════════════════════════════════
       8. Parallax subtle scroll effect for hero
    ════════════════════════════════ */
    function initParallax() {
        const blobs = document.querySelectorAll('.aurora-blob');
        if (!blobs.length) return;

        window.addEventListener('scroll', () => {
            const sy = window.scrollY;
            blobs[0] && (blobs[0].style.transform = `translateY(${sy * 0.08}px) scale(1)`);
            blobs[1] && (blobs[1].style.transform = `translateY(${-sy * 0.05}px) scale(1)`);
        }, { passive: true });
    }

    /* ════════════════════════════════
       9. Animated counter for ring label
    ════════════════════════════════ */
    function animateRing(targetPct, circum = 251.2) {
        const fill = document.getElementById('ring-fill');
        const pctEl = document.getElementById('ring-pct');
        if (!fill || !pctEl) return;

        const start = performance.now();
        const duration = 900;
        const startOffset = parseFloat(fill.style.strokeDashoffset) || circum;
        const endOffset = circum - (circum * targetPct / 100);

        function step(now) {
            const elapsed = now - start;
            const progress = Math.min(elapsed / duration, 1);
            const ease = 1 - Math.pow(1 - progress, 3); // easeOutCubic
            fill.style.strokeDashoffset = startOffset + (endOffset - startOffset) * ease;
            if (progress < 1) requestAnimationFrame(step);
        }
        requestAnimationFrame(step);
    }

    // Expose globally so pages can use it
    window.animateRingEnhanced = animateRing;

    /* ════════════════════════════════
       10. Particle canvas mini
    ════════════════════════════════ */
    function initParticles() {
        const canvas = document.createElement('canvas');
        canvas.id = 'cns-particles';
        document.body.insertBefore(canvas, document.body.firstChild);

        const ctx = canvas.getContext('2d');
        let W, H, particles = [];

        function resize() {
            W = canvas.width  = window.innerWidth;
            H = canvas.height = window.innerHeight;
        }
        resize();
        window.addEventListener('resize', resize, { passive: true });

        const count = Math.min(50, Math.floor(window.innerWidth / 30));
        for (let i = 0; i < count; i++) {
            particles.push({
                x: Math.random() * W,
                y: Math.random() * H,
                r: 0.8 + Math.random() * 1.6,
                vx: (Math.random() - 0.5) * 0.3,
                vy: -0.1 - Math.random() * 0.25,
                alpha: 0.1 + Math.random() * 0.35,
                color: ['#3b82f6', '#06b6d4', '#a855f7', '#22c55e'][Math.floor(Math.random() * 4)]
            });
        }

        function draw() {
            ctx.clearRect(0, 0, W, H);

            // Draw connection lines
            for (let i = 0; i < particles.length; i++) {
                for (let j = i + 1; j < particles.length; j++) {
                    const dx = particles[i].x - particles[j].x;
                    const dy = particles[i].y - particles[j].y;
                    const dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < 130) {
                        ctx.beginPath();
                        ctx.strokeStyle = `rgba(59,130,246,${0.08 * (1 - dist / 130)})`;
                        ctx.lineWidth = 0.5;
                        ctx.moveTo(particles[i].x, particles[i].y);
                        ctx.lineTo(particles[j].x, particles[j].y);
                        ctx.stroke();
                    }
                }
            }

            // Draw dots
            particles.forEach(p => {
                ctx.beginPath();
                ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
                ctx.fillStyle = p.color + Math.round(p.alpha * 255).toString(16).padStart(2, '0');
                ctx.fill();

                p.x += p.vx;
                p.y += p.vy;

                if (p.y < -5) { p.y = H + 5; p.x = Math.random() * W; }
                if (p.x < -5) p.x = W + 5;
                if (p.x > W + 5) p.x = -5;
            });

            requestAnimationFrame(draw);
        }
        draw();
    }

    /* ════════════════════════════════
       11. Keyboard shortcuts
    ════════════════════════════════ */
    function initKeyboardShortcuts() {
        document.addEventListener('keydown', (e) => {
            // Alt + T = scroll to top
            if (e.altKey && e.key === 't') {
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }
        });
    }

    /* ════════════════════════════════
       12. Image zoom — enhanced
    ════════════════════════════════ */
    // Enhance the existing zoomImg by overriding it
    window.addEventListener('DOMContentLoaded', () => {
        if (typeof window.zoomImg === 'function') {
            const origZoom = window.zoomImg;
            window.zoomImg = function (src) {
                const overlay = document.createElement('div');
                overlay.style.cssText = `
                    position:fixed; inset:0;
                    background:rgba(0,0,0,0.95);
                    z-index:99999;
                    display:flex;
                    align-items:center;
                    justify-content:center;
                    cursor:zoom-out;
                    backdrop-filter:blur(16px);
                    opacity:0;
                    transition:opacity 0.3s ease;
                `;
                overlay.innerHTML = `
                    <img src="${src}" style="
                        max-width:92vw; max-height:88vh;
                        border-radius:16px;
                        box-shadow:0 32px 80px rgba(0,0,0,0.8), 0 0 0 1px rgba(255,255,255,0.08);
                        transform:scale(0.92);
                        transition:transform 0.4s cubic-bezier(0.23,1,0.32,1);
                    ">
                    <button style="
                        position:absolute; top:20px; right:24px;
                        background:rgba(255,255,255,0.08);
                        border:1px solid rgba(255,255,255,0.12);
                        color:rgba(255,255,255,0.7);
                        width:40px; height:40px; border-radius:50%;
                        cursor:pointer; font-size:1rem;
                        display:flex; align-items:center; justify-content:center;
                        transition:all 0.2s;
                    " title="Đóng"><i class="fa-solid fa-xmark"></i></button>
                `;
                document.body.appendChild(overlay);

                requestAnimationFrame(() => {
                    overlay.style.opacity = '1';
                    const img = overlay.querySelector('img');
                    if (img) img.style.transform = 'scale(1)';
                });

                function close() {
                    overlay.style.opacity = '0';
                    const img = overlay.querySelector('img');
                    if (img) img.style.transform = 'scale(0.92)';
                    setTimeout(() => overlay.remove(), 300);
                }

                overlay.addEventListener('click', (e) => {
                    if (e.target === overlay) close();
                });
                overlay.querySelector('button').addEventListener('click', close);
                document.addEventListener('keydown', function esc(e) {
                    if (e.key === 'Escape') { close(); document.removeEventListener('keydown', esc); }
                });
            };
        }
    });

    /* ════════════════════════════════
       Init all
    ════════════════════════════════ */
    function init() {
        injectAurora();
        initParticles();
        initReadingProgress();
        initScrollTop();
        initCardGlow();
        initParallax();
        initKeyboardShortcuts();

        // Slight delay for DOM completeness
        setTimeout(() => {
            patchProgressEvents();
        }, 500);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();
