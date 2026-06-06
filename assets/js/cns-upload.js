// Enhance PDF module with drag & drop and preview
document.addEventListener('DOMContentLoaded', () => {
  function makeDroppable(modEl) {
    modEl.addEventListener('dragenter', e => { e.preventDefault(); modEl.classList.add('drag-over'); });
    modEl.addEventListener('dragover', e => { e.preventDefault(); });
    modEl.addEventListener('dragleave', e => { modEl.classList.remove('drag-over'); });
    modEl.addEventListener('drop', e => {
      e.preventDefault(); modEl.classList.remove('drag-over');
      const f = e.dataTransfer.files && e.dataTransfer.files[0];
      if (!f) return; if (f.type !== 'application/pdf') { alert('Chỉ nhận file PDF'); return; }
      // if input exists, set file via DataTransfer
      const input = modEl.querySelector('input[type=file]');
      if (input) {
        const dt = new DataTransfer(); dt.items.add(f); input.files = dt.files;
        input.dispatchEvent(new Event('change'));
      } else if (typeof handlePDF === 'function') {
        // create fake event
        const ev = { target: { files: [f] } };
        handlePDF(ev, modEl.id?.replace('slot-',''));
      } else {
        // fallback: store locally
        const url = URL.createObjectURL(f);
        const id = modEl.id || ('slot-' + Date.now());
        const pdfData = JSON.parse(localStorage.getItem('cns_pdf_data')||'{}');
        pdfData[id.replace('slot-','')] = { name: f.name, url };
        localStorage.setItem('cns_pdf_data', JSON.stringify(pdfData));
        if (typeof renderPDF === 'function') renderPDF();
      }
    });
  }

  document.querySelectorAll('.pdf-mod').forEach(makeDroppable);

  // Add preview behavior for existing links
  function attachPreview() {
    document.querySelectorAll('.pdf-mod.has-file').forEach(m => {
      const btn = m.querySelector('.pdf-mod-btn');
      if (!btn) return;
      btn.style.border = '1px solid rgba(110,231,255,0.09)';
      btn.addEventListener('mouseenter', ()=> btn.style.boxShadow = '0 8px 28px rgba(6,182,212,0.06)');
      btn.addEventListener('mouseleave', ()=> btn.style.boxShadow = '');
    });
  }
  attachPreview();

  // Re-attach when renderPDF is called (if available)
  const origRender = window.renderPDF;
  if (typeof origRender === 'function') {
    window.renderPDF = function(){ origRender(); attachPreview(); };
  }
});
