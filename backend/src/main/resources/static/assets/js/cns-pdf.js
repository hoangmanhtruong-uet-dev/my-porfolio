/**
 * CNS PDF Upload & Inline View — Shared Module
 * Dùng chung cho cns_bai1.html → cns_bai6.html
 *
 * Sử dụng:
 *   <div class="pdf-upload-wrap" id="pdf-slot-wrap"></div>
 *   CnsPdf.init('bai1', isAdminFn, apiBaseUrlFn);
 */

window.CnsPdf = (() => {

  let _slotId   = null;
  let _getAdmin = () => false;
  let _getBase  = () => '';

  // ── PDF Viewer Modal (tạo 1 lần, dùng cho mọi bài) ──────────────────────
  function _ensureModal() {
    if (document.getElementById('pdf-viewer-modal')) return;
    const overlay = document.createElement('div');
    overlay.id = 'pdf-viewer-modal';
    overlay.style.cssText = [
      'display:none',
      'position:fixed',
      'inset:0',
      'background:rgba(0,4,10,0.95)',
      'z-index:20000',
      'flex-direction:column',
      'align-items:stretch',
    ].join(';');

    overlay.innerHTML = `
      <div style="
        display:flex;align-items:center;justify-content:space-between;
        padding:12px 20px;
        background:rgba(0,14,28,0.95);
        border-bottom:1px solid rgba(0,210,255,0.2);
        flex-shrink:0;
      ">
        <div style="display:flex;align-items:center;gap:12px;">
          <i class="fa-solid fa-file-pdf" style="color:#ffb800;font-size:1.1rem;"></i>
          <span id="pdf-modal-title" style="
            font-family:'JetBrains Mono',monospace;font-size:0.82rem;
            color:#e8f4ff;letter-spacing:1px;
          ">Đang tải PDF...</span>
        </div>
        <div style="display:flex;align-items:center;gap:10px;">
          <a id="pdf-modal-download" href="#" target="_blank" style="
            display:inline-flex;align-items:center;gap:6px;
            padding:6px 14px;
            border:1px solid rgba(255,184,0,0.3);
            background:rgba(255,184,0,0.07);
            color:#ffb800;
            font-family:'JetBrains Mono',monospace;font-size:0.7rem;letter-spacing:1px;
            text-decoration:none;cursor:pointer;transition:all 0.2s;
          " onmouseenter="this.style.background='rgba(255,184,0,0.18)'"
             onmouseleave="this.style.background='rgba(255,184,0,0.07)'">
            <i class="fa-solid fa-download"></i> Tải về
          </a>
          <button onclick="CnsPdf.closeViewer()" style="
            display:flex;align-items:center;gap:6px;
            padding:6px 14px;
            border:1px solid rgba(0,210,255,0.2);
            background:rgba(0,210,255,0.06);
            color:rgba(0,210,255,0.8);
            font-family:'JetBrains Mono',monospace;font-size:0.7rem;letter-spacing:1px;
            cursor:pointer;transition:all 0.2s;
          " onmouseenter="this.style.background='rgba(0,210,255,0.14)'"
             onmouseleave="this.style.background='rgba(0,210,255,0.06)'">
            <i class="fa-solid fa-xmark"></i> Đóng
          </button>
        </div>
      </div>
      <div style="flex:1;position:relative;overflow:hidden;">
        <div id="pdf-modal-loading" style="
          position:absolute;inset:0;
          display:flex;flex-direction:column;align-items:center;justify-content:center;
          background:rgba(6,18,32,0.9);
          font-family:'JetBrains Mono',monospace;color:rgba(0,210,255,0.6);
          font-size:0.82rem;gap:14px;
        ">
          <i class="fa-solid fa-spinner fa-spin" style="font-size:2rem;color:#00d2ff;"></i>
          <span>Đang tải PDF từ server...</span>
        </div>
        <iframe
          id="pdf-modal-frame"
          style="width:100%;height:100%;border:none;display:block;"
          src="about:blank"
          onload="document.getElementById('pdf-modal-loading').style.display='none';"
        ></iframe>
      </div>
    `;
    document.body.appendChild(overlay);

    // Đóng khi click nền
    overlay.addEventListener('click', function(e) {
      if (e.target === overlay) CnsPdf.closeViewer();
    });
    // Phím Escape
    document.addEventListener('keydown', function(e) {
      if (e.key === 'Escape') CnsPdf.closeViewer();
    });
  }

  // ── Mở PDF viewer ────────────────────────────────────────────────────────
  function openViewer(viewUrl, downloadUrl, fileName) {
    _ensureModal();
    const modal   = document.getElementById('pdf-viewer-modal');
    const frame   = document.getElementById('pdf-modal-frame');
    const loading = document.getElementById('pdf-modal-loading');
    const title   = document.getElementById('pdf-modal-title');
    const dl      = document.getElementById('pdf-modal-download');

    title.textContent = fileName || 'File PDF';
    dl.href = downloadUrl || viewUrl;
    loading.style.display = 'flex';
    frame.src = 'about:blank';
    modal.style.display = 'flex';
    document.body.style.overflow = 'hidden';

    // Load PDF qua view endpoint (inline, không bắt tải)
    setTimeout(() => { frame.src = viewUrl; }, 50);
  }

  // ── Đóng viewer ──────────────────────────────────────────────────────────
  function closeViewer() {
    const modal = document.getElementById('pdf-viewer-modal');
    if (modal) {
      modal.style.display = 'none';
      document.getElementById('pdf-modal-frame').src = 'about:blank';
      document.body.style.overflow = '';
    }
  }

  // ── Render PDF Upload Wrap ────────────────────────────────────────────────
  function render(saved, isAdmin, apiBase) {
    const wrap = document.getElementById('pdf-slot-wrap');
    if (!wrap) return;

    const viewUrl = apiBase + '/api/cns/pdf/view/' + _slotId;
    const fileUrl = saved ? (saved.url || saved.fileUrl || viewUrl) : null;
    const name    = saved ? (saved.name || saved.fileName || 'Bài PDF') : null;

    if (saved && fileUrl) {
      wrap.className = 'pdf-upload-wrap has-file';
      wrap.innerHTML = `
        <div class="pdf-upload-icon"><i class="fa-solid fa-file-pdf"></i></div>
        <div class="pdf-upload-info">
          <div class="pdf-upload-title">${name}</div>
          <div class="pdf-upload-hint">File đã upload — Giáo viên click để xem trực tiếp</div>
        </div>
        <div class="pdf-upload-actions">
          <button class="pdf-view-link" onclick="CnsPdf.openViewer('${viewUrl}','${fileUrl}','${name}')">
            <i class="fa-solid fa-eye"></i> Xem PDF
          </button>
          ${isAdmin ? `
            <label class="pdf-view-link" style="cursor:pointer;border-color:rgba(0,210,255,0.25);color:var(--cyan);">
              <i class="fa-solid fa-arrow-up-from-bracket"></i> Đổi file
              <input type="file" accept="application/pdf" onchange="CnsPdf.handleUpload(event)" style="display:none;">
            </label>
            <button class="pdf-del-btn" onclick="CnsPdf.handleDelete()">
              <i class="fa-solid fa-trash"></i> Xóa
            </button>
          ` : ''}
        </div>
      `;
    } else {
      wrap.className = 'pdf-upload-wrap';
      if (isAdmin) {
        wrap.innerHTML = `
          <label style="display:flex;align-items:center;gap:16px;width:100%;cursor:pointer;">
            <div class="pdf-upload-icon"><i class="fa-solid fa-arrow-up-from-bracket" style="color:rgba(0,210,255,0.4);"></i></div>
            <div class="pdf-upload-info">
              <div class="pdf-upload-title">Nhấp để upload file PDF bài nộp</div>
              <div class="pdf-upload-hint">Chấp nhận file .pdf · Tối đa 10MB · File sẽ được lưu lên Cloudinary</div>
            </div>
            <input type="file" accept="application/pdf" onchange="CnsPdf.handleUpload(event)" style="display:none;">
          </label>
        `;
      } else {
        wrap.innerHTML = `
          <div class="pdf-upload-icon"><i class="fa-solid fa-file-pdf" style="color:rgba(255,255,255,0.1);"></i></div>
          <div class="pdf-upload-info">
            <div class="pdf-upload-title" style="color:rgba(255,255,255,0.3);">Chưa có file bài nộp</div>
            <div class="pdf-upload-hint">Admin chưa upload file PDF cho bài này</div>
          </div>
          <div class="pdf-upload-actions"></div>
        `;
      }
    }
  }

  // ── Upload handler ────────────────────────────────────────────────────────
  async function handleUpload(event) {
    const file = event.target.files[0];
    if (!file) return;
    if (file.type !== 'application/pdf') { alert('Chỉ chấp nhận file .pdf'); return; }
    if (file.size > 10 * 1024 * 1024) { alert('File vượt quá 10MB'); return; }

    const isAdmin = _getAdmin();
    const apiBase = _getBase();

    // Show loading state
    const wrap = document.getElementById('pdf-slot-wrap');
    if (wrap) {
      wrap.className = 'pdf-upload-wrap';
      wrap.innerHTML = `
        <div class="pdf-upload-icon"><i class="fa-solid fa-spinner fa-spin" style="color:#00d2ff;"></i></div>
        <div class="pdf-upload-info">
          <div class="pdf-upload-title" style="color:#00d2ff;">Đang upload lên Cloudinary...</div>
          <div class="pdf-upload-hint">${file.name} · ${(file.size/1024/1024).toFixed(2)} MB</div>
        </div>
        <div class="pdf-upload-actions"></div>
      `;
    }

    try {
      const fd = new FormData();
      fd.append('file', file);
      const res = await fetch(`${apiBase}/api/cns/pdf/${_slotId}`, { method: 'POST', body: fd });
      if (!res.ok) throw new Error('Server trả về lỗi ' + res.status);
      const data = await res.json();

      const saved = {
        name: file.name,
        url: data.fileUrl,
        viewUrl: data.viewUrl,
        fileName: data.fileName || file.name,
      };

      // Cập nhật localStorage
      let pdfData = {};
      try { pdfData = JSON.parse(localStorage.getItem('cns_pdf_data') || '{}'); } catch(e){}
      pdfData[_slotId] = saved;
      localStorage.setItem('cns_pdf_data', JSON.stringify(pdfData));

      render(saved, isAdmin, apiBase);

    } catch (err) {
      console.error('Upload PDF thất bại:', err);
      alert('Upload thất bại: ' + err.message + '\n\nKiểm tra:\n• Backend đang chạy không?\n• File PDF hợp lệ không?');
      render(null, isAdmin, apiBase);
    }
  }

  // ── Delete handler ────────────────────────────────────────────────────────
  async function handleDelete() {
    if (!confirm('Xóa file PDF bài này?')) return;
    const apiBase = _getBase();

    try {
      await fetch(`${apiBase}/api/cns/pdf/${_slotId}`, { method: 'DELETE' });
    } catch(e) { console.warn('Delete PDF lỗi:', e); }

    let pdfData = {};
    try { pdfData = JSON.parse(localStorage.getItem('cns_pdf_data') || '{}'); } catch(e){}
    delete pdfData[_slotId];
    localStorage.setItem('cns_pdf_data', JSON.stringify(pdfData));

    render(null, _getAdmin(), apiBase);
  }

  // ── Load from server ──────────────────────────────────────────────────────
  async function loadFromServer(apiBase) {
    const RETRY_MAX = 5, RETRY_DELAY = 3000;
    for (let i = 1; i <= RETRY_MAX; i++) {
      try {
        const res = await fetch(`${apiBase}/api/cns/pdf/${_slotId}`, {
          signal: AbortSignal.timeout(8000)
        });
        if (res.ok) {
          const d = await res.json();
          if (d && (d.fileUrl || d.viewUrl)) {
            const saved = {
              name: d.fileName || 'Bài PDF',
              url:  d.fileUrl,
              viewUrl: d.viewUrl,
              fileName: d.fileName,
            };
            let pdfData = {};
            try { pdfData = JSON.parse(localStorage.getItem('cns_pdf_data') || '{}'); } catch(e){}
            pdfData[_slotId] = saved;
            localStorage.setItem('cns_pdf_data', JSON.stringify(pdfData));
            render(saved, _getAdmin(), apiBase);
            return;
          }
        }
        // 404 = chưa có file — bình thường
        if (res.status === 404) return;
      } catch(e) {
        if (i < RETRY_MAX) await new Promise(r => setTimeout(r, RETRY_DELAY));
      }
    }
  }

  // ── Init ──────────────────────────────────────────────────────────────────
  function init(slotId, getAdminFn, getBaseFn) {
    _slotId   = slotId;
    _getAdmin = getAdminFn;
    _getBase  = getBaseFn;
    _ensureModal();

    const apiBase = getBaseFn();

    // Render từ localStorage trước (nếu có)
    let pdfData = {};
    try { pdfData = JSON.parse(localStorage.getItem('cns_pdf_data') || '{}'); } catch(e){}
    render(pdfData[slotId] || null, getAdminFn(), apiBase);

    // Load từ server (có retry khi server đang wake up)
    loadFromServer(apiBase);
  }

  // ── Public API ────────────────────────────────────────────────────────────
  return { init, render, openViewer, closeViewer, handleUpload, handleDelete, loadFromServer };

})();
