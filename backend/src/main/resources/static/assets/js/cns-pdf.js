/**
 * CNS PDF Upload & Inline View — Shared Module v2
 * Flow: Chọn file → Preview/Xác nhận → Upload lên Cloudinary
 */
window.CnsPdf = (() => {

  let _slotId   = null;
  let _getAdmin = () => false;
  let _getBase  = () => '';
  let _pending  = null; // File đang chờ xác nhận

  // ════════════════════════════════════════════════
  // PDF VIEWER MODAL
  // ════════════════════════════════════════════════
  function _ensureModal() {
    if (document.getElementById('pdf-viewer-modal')) return;
    const el = document.createElement('div');
    el.id = 'pdf-viewer-modal';
    el.style.cssText = 'display:none;position:fixed;inset:0;background:rgba(0,4,10,0.95);z-index:20000;flex-direction:column;align-items:stretch;';
    el.innerHTML = `
      <div style="display:flex;align-items:center;justify-content:space-between;padding:12px 20px;background:rgba(0,14,28,0.95);border-bottom:1px solid rgba(0,210,255,0.2);flex-shrink:0;">
        <div style="display:flex;align-items:center;gap:12px;">
          <i class="fa-solid fa-file-pdf" style="color:#ffb800;font-size:1.1rem;"></i>
          <span id="pdf-modal-title" style="font-family:'JetBrains Mono',monospace;font-size:0.82rem;color:#e8f4ff;letter-spacing:1px;">Đang tải PDF...</span>
        </div>
        <div style="display:flex;align-items:center;gap:10px;">
          <a id="pdf-modal-dl" href="#" target="_blank" style="display:inline-flex;align-items:center;gap:6px;padding:6px 14px;border:1px solid rgba(255,184,0,0.3);background:rgba(255,184,0,0.07);color:#ffb800;font-family:'JetBrains Mono',monospace;font-size:0.7rem;letter-spacing:1px;text-decoration:none;cursor:pointer;">
            <i class="fa-solid fa-download"></i> Tải về
          </a>
          <button onclick="CnsPdf.closeViewer()" style="display:flex;align-items:center;gap:6px;padding:6px 14px;border:1px solid rgba(0,210,255,0.2);background:rgba(0,210,255,0.06);color:rgba(0,210,255,0.8);font-family:'JetBrains Mono',monospace;font-size:0.7rem;cursor:pointer;">
            <i class="fa-solid fa-xmark"></i> Đóng
          </button>
        </div>
      </div>
      <div style="flex:1;position:relative;overflow:hidden;">
        <div id="pdf-modal-loading" style="position:absolute;inset:0;display:flex;flex-direction:column;align-items:center;justify-content:center;background:rgba(6,18,32,0.9);font-family:'JetBrains Mono',monospace;color:rgba(0,210,255,0.6);font-size:0.82rem;gap:14px;">
          <i class="fa-solid fa-spinner fa-spin" style="font-size:2rem;color:#00d2ff;"></i>
          <span>Đang tải PDF từ server...</span>
        </div>
        <iframe id="pdf-modal-frame" style="width:100%;height:100%;border:none;display:block;" src="about:blank"
          onload="document.getElementById('pdf-modal-loading').style.display='none';"></iframe>
      </div>`;
    document.body.appendChild(el);
    el.addEventListener('click', e => { if (e.target === el) CnsPdf.closeViewer(); });
    document.addEventListener('keydown', e => { if (e.key === 'Escape') CnsPdf.closeViewer(); });
  }

  function openViewer(viewUrl, downloadUrl, fileName) {
    _ensureModal();
    document.getElementById('pdf-modal-title').textContent = fileName || 'File PDF';
    document.getElementById('pdf-modal-dl').href = downloadUrl || viewUrl;
    document.getElementById('pdf-modal-loading').style.display = 'flex';
    document.getElementById('pdf-modal-frame').src = 'about:blank';
    document.getElementById('pdf-viewer-modal').style.display = 'flex';
    document.body.style.overflow = 'hidden';
    setTimeout(() => { document.getElementById('pdf-modal-frame').src = viewUrl; }, 80);
  }

  function closeViewer() {
    const m = document.getElementById('pdf-viewer-modal');
    if (m) { m.style.display = 'none'; document.getElementById('pdf-modal-frame').src = 'about:blank'; document.body.style.overflow = ''; }
  }

  // ════════════════════════════════════════════════
  // RENDER WRAP
  // ════════════════════════════════════════════════
  function render(saved, isAdmin, apiBase) {
    const wrap = document.getElementById('pdf-slot-wrap');
    if (!wrap) return;

    const viewUrl = apiBase + '/api/cns/pdf/view/' + _slotId;
    const fileUrl = saved ? (saved.url || saved.fileUrl || viewUrl) : null;
    const name    = saved ? (saved.name || saved.fileName || 'file.pdf') : null;

    if (saved && fileUrl) {
      // ── Đã có file ──
      wrap.className = 'pdf-upload-wrap has-file';
      wrap.innerHTML = `
        <div class="pdf-upload-icon"><i class="fa-solid fa-file-pdf"></i></div>
        <div class="pdf-upload-info">
          <div class="pdf-upload-title">${_esc(name)}</div>
          <div class="pdf-upload-hint">Đã lưu trên Cloudinary · Giáo viên click <strong>Xem PDF</strong> để đọc trực tiếp</div>
        </div>
        <div class="pdf-upload-actions">
          <button class="pdf-view-link" onclick="CnsPdf.openViewer('${viewUrl}','${_esc(fileUrl)}','${_esc(name)}')">
            <i class="fa-solid fa-eye"></i> Xem PDF
          </button>
          ${isAdmin ? `
            <label class="pdf-view-link" style="cursor:pointer;border-color:rgba(0,210,255,0.3);color:var(--cyan,#00d2ff);">
              <i class="fa-solid fa-arrow-up-from-bracket"></i> Đổi file
              <input type="file" accept="application/pdf" style="display:none;" onchange="CnsPdf._onFileChosen(event)">
            </label>
            <button class="pdf-del-btn" onclick="CnsPdf.handleDelete()">
              <i class="fa-solid fa-trash"></i> Xóa
            </button>` : ''}
        </div>`;
    } else if (isAdmin) {
      // ── Admin, chưa có file ──
      wrap.className = 'pdf-upload-wrap';
      wrap.innerHTML = `
        <label style="display:flex;align-items:center;gap:18px;width:100%;cursor:pointer;padding:4px 0;">
          <div class="pdf-upload-icon">
            <i class="fa-solid fa-arrow-up-from-bracket" style="color:rgba(0,210,255,0.5);"></i>
          </div>
          <div class="pdf-upload-info">
            <div class="pdf-upload-title">Nhấp để chọn file PDF</div>
            <div class="pdf-upload-hint">Sau khi chọn sẽ hiện xem trước · Bấm <strong>Xác nhận upload</strong> để lưu lên Cloudinary</div>
          </div>
          <input type="file" accept="application/pdf" style="display:none;" onchange="CnsPdf._onFileChosen(event)">
        </label>`;
    } else {
      // ── Khách, chưa có file ──
      wrap.className = 'pdf-upload-wrap';
      wrap.innerHTML = `
        <div class="pdf-upload-icon"><i class="fa-solid fa-file-pdf" style="color:rgba(255,255,255,0.12);"></i></div>
        <div class="pdf-upload-info">
          <div class="pdf-upload-title" style="color:rgba(255,255,255,0.35);">Chưa có file bài nộp</div>
          <div class="pdf-upload-hint">Admin chưa upload file PDF cho bài này</div>
        </div>
        <div class="pdf-upload-actions"></div>`;
    }
  }

  // ════════════════════════════════════════════════
  // FILE CHOSEN → HIỆN PREVIEW + NÚT XÁC NHẬN
  // ════════════════════════════════════════════════
  function _onFileChosen(event) {
    const file = event.target.files[0];
    if (!file) return;
    if (file.type !== 'application/pdf') { alert('Chỉ chấp nhận file .pdf'); return; }
    if (file.size > 20 * 1024 * 1024) { alert('File quá lớn (tối đa 20MB)'); return; }

    _pending = file;
    const blobUrl = URL.createObjectURL(file);
    const sizeMB  = (file.size / 1024 / 1024).toFixed(2);

    const wrap = document.getElementById('pdf-slot-wrap');
    if (!wrap) return;
    wrap.className = 'pdf-upload-wrap';
    wrap.style.flexDirection = 'column';
    wrap.style.alignItems = 'stretch';
    wrap.style.gap = '14px';

    wrap.innerHTML = `
      <!-- Preview header -->
      <div style="display:flex;align-items:center;gap:14px;">
        <div class="pdf-upload-icon">
          <i class="fa-solid fa-file-pdf" style="color:#ffb800;"></i>
        </div>
        <div class="pdf-upload-info">
          <div class="pdf-upload-title" style="color:#ffb800;">${_esc(file.name)}</div>
          <div class="pdf-upload-hint">${sizeMB} MB · Xem trước bên dưới, rồi bấm xác nhận để upload</div>
        </div>
      </div>

      <!-- Iframe preview -->
      <div style="position:relative;width:100%;height:420px;background:rgba(0,0,0,0.4);border:1px solid rgba(255,184,0,0.2);border-radius:4px;overflow:hidden;">
        <div id="pdf-preview-loading" style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;font-family:'JetBrains Mono',monospace;color:rgba(0,210,255,0.5);font-size:0.8rem;gap:10px;">
          <i class="fa-solid fa-spinner fa-spin"></i> Đang tải xem trước...
        </div>
        <iframe src="${blobUrl}" style="width:100%;height:100%;border:none;"
          onload="document.getElementById('pdf-preview-loading').style.display='none';"></iframe>
      </div>

      <!-- Action buttons -->
      <div style="display:flex;gap:10px;align-items:center;">
        <button onclick="CnsPdf._confirmUpload()" style="
          display:inline-flex;align-items:center;gap:8px;padding:10px 22px;
          background:rgba(0,255,136,0.1);border:1px solid rgba(0,255,136,0.35);
          color:#00ff88;font-family:'JetBrains Mono',monospace;font-size:0.78rem;
          letter-spacing:1px;text-transform:uppercase;cursor:pointer;
          clip-path:polygon(5px 0,100% 0,calc(100% - 5px) 100%,0 100%);
          transition:all 0.25s;
        " onmouseenter="this.style.background='rgba(0,255,136,0.2)'"
           onmouseleave="this.style.background='rgba(0,255,136,0.1)'">
          <i class="fa-solid fa-check"></i> Xác nhận upload
        </button>
        <button onclick="CnsPdf._cancelUpload()" style="
          display:inline-flex;align-items:center;gap:8px;padding:10px 18px;
          background:rgba(255,255,255,0.04);border:1px solid rgba(255,255,255,0.12);
          color:rgba(255,255,255,0.45);font-family:'JetBrains Mono',monospace;font-size:0.78rem;
          letter-spacing:1px;text-transform:uppercase;cursor:pointer;
          clip-path:polygon(5px 0,100% 0,calc(100% - 5px) 100%,0 100%);
        " onmouseenter="this.style.background='rgba(255,255,255,0.08)'"
           onmouseleave="this.style.background='rgba(255,255,255,0.04)'">
          <i class="fa-solid fa-xmark"></i> Hủy
        </button>
        <span style="font-family:'JetBrains Mono',monospace;font-size:0.72rem;color:rgba(255,255,255,0.25);margin-left:auto;">
          Chưa upload — cần bấm xác nhận
        </span>
      </div>`;
  }

  // ════════════════════════════════════════════════
  // XÁC NHẬN → UPLOAD LÊN CLOUDINARY
  // ════════════════════════════════════════════════
  async function _confirmUpload() {
    if (!_pending) return;
    const file    = _pending;
    const apiBase = _getBase();
    const isAdmin = _getAdmin();

    const wrap = document.getElementById('pdf-slot-wrap');
    if (wrap) {
      wrap.style.flexDirection = '';
      wrap.style.alignItems = '';
      wrap.style.gap = '';
      wrap.className = 'pdf-upload-wrap';
      wrap.innerHTML = `
        <div class="pdf-upload-icon"><i class="fa-solid fa-spinner fa-spin" style="color:#00d2ff;"></i></div>
        <div class="pdf-upload-info">
          <div class="pdf-upload-title" style="color:#00d2ff;">Đang upload lên Cloudinary...</div>
          <div class="pdf-upload-hint">${_esc(file.name)} · ${(file.size/1024/1024).toFixed(2)} MB</div>
        </div>
        <div class="pdf-upload-actions"></div>`;
    }

    try {
      const fd = new FormData();
      fd.append('file', file);
      const res = await fetch(`${apiBase}/api/cns/pdf/${_slotId}`, { method: 'POST', body: fd });
      if (!res.ok) throw new Error('HTTP ' + res.status);
      const data = await res.json();

      const saved = { name: file.name, url: data.fileUrl, fileUrl: data.fileUrl, viewUrl: data.viewUrl, fileName: data.fileName || file.name };
      let pdfData = {};
      try { pdfData = JSON.parse(localStorage.getItem('cns_pdf_data') || '{}'); } catch(e){}
      pdfData[_slotId] = saved;
      localStorage.setItem('cns_pdf_data', JSON.stringify(pdfData));
      _pending = null;
      if (wrap) { wrap.style.flexDirection = ''; wrap.style.alignItems = ''; wrap.style.gap = ''; }
      render(saved, isAdmin, apiBase);

    } catch (err) {
      console.error('Upload thất bại:', err);
      _pending = null;
      if (wrap) { wrap.style.flexDirection = ''; wrap.style.alignItems = ''; wrap.style.gap = ''; }
      render(null, isAdmin, apiBase);
      setTimeout(() => alert('Upload thất bại: ' + err.message + '\n\nKiểm tra backend đang chạy chưa?'), 100);
    }
  }

  function _cancelUpload() {
    _pending = null;
    const wrap = document.getElementById('pdf-slot-wrap');
    if (wrap) { wrap.style.flexDirection = ''; wrap.style.alignItems = ''; wrap.style.gap = ''; }
    render(null, _getAdmin(), _getBase());
  }

  // ════════════════════════════════════════════════
  // DELETE
  // ════════════════════════════════════════════════
  async function handleDelete() {
    if (!confirm('Xóa file PDF bài này?\nGiáo viên sẽ không còn xem được nữa.')) return;
    const apiBase = _getBase();
    try { await fetch(`${apiBase}/api/cns/pdf/${_slotId}`, { method: 'DELETE' }); } catch(e){}
    let pdfData = {};
    try { pdfData = JSON.parse(localStorage.getItem('cns_pdf_data') || '{}'); } catch(e){}
    delete pdfData[_slotId];
    localStorage.setItem('cns_pdf_data', JSON.stringify(pdfData));
    render(null, _getAdmin(), apiBase);
  }

  // ════════════════════════════════════════════════
  // LOAD FROM SERVER (với retry khi Render đang sleep)
  // ════════════════════════════════════════════════
  async function loadFromServer(apiBase) {
    const MAX = 5, DELAY = 3000;
    for (let i = 1; i <= MAX; i++) {
      try {
        const res = await fetch(`${apiBase}/api/cns/pdf/${_slotId}`, { signal: AbortSignal.timeout(9000) });
        if (res.status === 404) return; // Chưa có file — bình thường
        if (res.ok) {
          const d = await res.json();
          if (d && d.fileUrl) {
            const saved = { name: d.fileName || 'file.pdf', url: d.fileUrl, fileUrl: d.fileUrl, viewUrl: d.viewUrl, fileName: d.fileName };
            let pdfData = {};
            try { pdfData = JSON.parse(localStorage.getItem('cns_pdf_data') || '{}'); } catch(e){}
            pdfData[_slotId] = saved;
            localStorage.setItem('cns_pdf_data', JSON.stringify(pdfData));
            render(saved, _getAdmin(), apiBase);
            return;
          }
        }
      } catch(e) {
        if (i < MAX) await new Promise(r => setTimeout(r, DELAY));
      }
    }
  }

  // ════════════════════════════════════════════════
  // INIT
  // ════════════════════════════════════════════════
  function init(slotId, getAdminFn, getBaseFn) {
    _slotId   = slotId;
    _getAdmin = getAdminFn;
    _getBase  = getBaseFn;
    _pending  = null;
    _ensureModal();
    const apiBase = getBaseFn();
    // Render ngay từ cache localStorage
    let pdfData = {};
    try { pdfData = JSON.parse(localStorage.getItem('cns_pdf_data') || '{}'); } catch(e){}
    render(pdfData[slotId] || null, getAdminFn(), apiBase);
    // Load từ server (đồng bộ với DB thực)
    loadFromServer(apiBase);
  }

  // ── Escape để tránh XSS trong innerHTML ──
  function _esc(str) {
    if (!str) return '';
    return String(str).replace(/&/g,'&amp;').replace(/"/g,'&quot;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
  }

  return { init, render, openViewer, closeViewer, handleDelete, loadFromServer,
           _onFileChosen, _confirmUpload, _cancelUpload };

})();
