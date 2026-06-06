import re, os

# New renderPDF template for each bai - single slot, no clip-path issue
# Params: slotId, slotLabel, slotSub, localStorage_key

def make_render_pdf(slot_id, slot_label, slot_sub, ls_key):
    return f"""    function renderPDF() {{
        const saved = pdfData['{slot_id}'];
        const wrap = document.getElementById('pdf-slot-wrap');
        if (!wrap) return;

        if (saved) {{
            wrap.classList.add('has-file');
            wrap.querySelector('.pdf-upload-icon i').className = 'fa-solid fa-file-pdf';
            wrap.querySelector('.pdf-upload-title').textContent = saved.name || '{slot_label}.pdf';
            wrap.querySelector('.pdf-upload-hint').textContent = 'Đã upload — click "Xem PDF" để mở';
            // Actions
            let actions = wrap.querySelector('.pdf-upload-actions');
            actions.innerHTML = `
                <a href="${{saved.url || saved.fileUrl}}" target="_blank" class="pdf-view-link">
                    <i class="fa-solid fa-eye"></i> Xem PDF
                </a>
                ${{isAdmin ? `<button class="pdf-del-btn" onclick="removePDF()"><i class="fa-solid fa-trash"></i> Xóa</button>` : ''}}
            `;
            // Disable file input if not admin
            const inp = wrap.querySelector('input[type="file"]');
            if (inp) inp.style.display = isAdmin ? 'block' : 'none';
        }} else {{
            wrap.classList.remove('has-file');
            wrap.querySelector('.pdf-upload-icon i').className = 'fa-solid fa-file-pdf';
            wrap.querySelector('.pdf-upload-title').textContent = isAdmin ? 'Nhấp để upload file PDF bài {slot_label}' : 'Chưa có file bài nộp';
            wrap.querySelector('.pdf-upload-hint').textContent = isAdmin ? 'Chấp nhận file .pdf, kích thước tối đa 10MB' : 'Admin chưa upload file bài này';
            wrap.querySelector('.pdf-upload-actions').innerHTML = '';
            const inp = wrap.querySelector('input[type="file"]');
            if (inp) inp.style.display = isAdmin ? 'block' : 'none';
        }}
    }}

    async function handlePDF(event) {{
        const file = event.target.files[0];
        if (!file) return;
        if (file.type !== 'application/pdf') {{ alert('Chỉ chấp nhận file PDF!'); return; }}
        const blobUrl = URL.createObjectURL(file);
        pdfData['{slot_id}'] = {{ name: file.name, url: blobUrl }};
        renderPDF();
        try {{
            const formData = new FormData();
            formData.append('file', file);
            const res = await fetch(`${{API_CONFIG.BASE_URL}}/api/cns/pdf/{slot_id}`, {{ method: 'POST', body: formData }});
            if (res.ok) {{
                const data = await res.json();
                pdfData['{slot_id}'] = {{ name: file.name, url: data.fileUrl }};
                localStorage.setItem('{ls_key}', JSON.stringify(pdfData));
                renderPDF();
            }}
        }} catch (e) {{
            localStorage.setItem('{ls_key}', JSON.stringify(pdfData));
            console.warn('Upload PDF loi:', e);
        }}
    }}

    function removePDF() {{
        if (!confirm('Xóa file PDF bài {slot_label}?')) return;
        delete pdfData['{slot_id}'];
        localStorage.setItem('{ls_key}', JSON.stringify(pdfData));
        renderPDF();
        fetch(`${{API_CONFIG.BASE_URL}}/api/cns/pdf/{slot_id}`, {{ method: 'DELETE' }}).catch(() => {{}});
    }}

    async function loadPDFFromServer() {{
        try {{
            const res = await fetch(`${{API_CONFIG.BASE_URL}}/api/cns/pdf/{slot_id}`, {{ signal: AbortSignal.timeout(8000) }});
            if (res.ok) {{
                const d = await res.json();
                if (d && d.fileUrl) {{
                    pdfData['{slot_id}'] = {{ name: d.fileName || '{slot_label}.pdf', url: d.fileUrl }};
                    localStorage.setItem('{ls_key}', JSON.stringify(pdfData));
                    renderPDF();
                }}
            }}
        }} catch (e) {{ console.warn('Không load PDF:', e.message); }}
    }}"""

# New PDF HTML section for each bai
def make_pdf_html(slot_label):
    return f"""                <div class="pdf-upload-wrap" id="pdf-slot-wrap">
                    <input type="file" accept="application/pdf" onchange="handlePDF(event)" style="display:none;">
                    <div class="pdf-upload-icon"><i class="fa-solid fa-file-pdf"></i></div>
                    <div class="pdf-upload-info">
                        <div class="pdf-upload-title">Chưa có file bài nộp</div>
                        <div class="pdf-upload-hint">Admin chưa upload file bài này</div>
                    </div>
                    <div class="pdf-upload-actions"></div>
                </div>"""

configs = [
    ('cns_bai1.html', 'bai1', 'Bài 1',  'Máy Tính & Ngoại Vi',    'cns_pdf_data'),
    ('cns_bai2.html', 'bai2', 'Bài 2',  'Khai Thác Dữ Liệu',     'cns_pdf_data'),
    ('cns_bai3.html', 'bai3', 'Bài 3',  'Tổng Quan AI',           'cns_pdf_data'),
    ('cns_bai4.html', 'bai4', 'Bài 4',  'Giao Tiếp & Hợp Tác',   'cns_pdf_data'),
]

pages_dir = r'c:\JavaProjects\My_project\Website_canhan\pages'

for fname, slot_id, slot_label, slot_sub, ls_key in configs:
    fpath = os.path.join(pages_dir, fname)
    with open(fpath, 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. Replace the pdf-modules div with new single slot html
    old_pdf_div = re.search(
        r'<div class="pdf-modules"[^>]*id="pdf-grid"[^>]*></div>',
        content
    )
    if old_pdf_div:
        content = content[:old_pdf_div.start()] + make_pdf_html(slot_label) + content[old_pdf_div.end():]
        print(f'  [{fname}] Replaced pdf-modules HTML')
    else:
        print(f'  [{fname}] WARNING: pdf-modules not found')

    # 2. Replace renderPDF + handlePDF + removePDF + loadPDFFromServer block
    # Find from "function renderPDF" to end of "loadPDFFromServer"
    rend_match = re.search(
        r'function renderPDF\(\).*?(?=function toggleAdmin)',
        content,
        re.DOTALL
    )
    if rend_match:
        new_block = make_render_pdf(slot_id, slot_label, slot_sub, ls_key) + '\n\n    '
        content = content[:rend_match.start()] + new_block + content[rend_match.end():]
        print(f'  [{fname}] Replaced renderPDF block')
    else:
        print(f'  [{fname}] WARNING: renderPDF block not found')

    # 3. Remove PDF_SLOTS array (no longer needed)
    content = re.sub(
        r'\n\s*const PDF_SLOTS = \[[\s\S]*?\];\n',
        '\n',
        content
    )

    # 4. Fix pdfData localStorage key — use correct key per bai
    # Change: JSON.parse(localStorage.getItem('cns_pdf_data') || '{}')
    # to use bai-specific key for isolation
    # Actually keep shared key so all bais share pdfData object

    with open(fpath, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f'[{fname}] Done\n')

print('All done.')
