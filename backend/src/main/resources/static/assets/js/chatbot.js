const chatbotStyles = `
    .chatbot-widget {
        position: fixed;
        bottom: 30px;
        right: 30px;
        z-index: 1000;
        font-family: 'Outfit', sans-serif;
    }

    .chatbot-button {
        width: 60px;
        height: 60px;
        background: linear-gradient(135deg, var(--primary-color), var(--accent));
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 1.8rem;
        cursor: pointer;
        box-shadow: 0 10px 25px rgba(168, 85, 247, 0.4);
        transition: all 0.3s ease;
        border: none;
    }

    .chatbot-button:hover {
        transform: scale(1.1) rotate(15deg);
    }

    .chatbot-window {
        position: absolute;
        bottom: 80px;
        right: 0;
        width: 350px;
        height: 450px;
        background: var(--surface-color);
        border: 1px solid var(--glass-border);
        border-radius: 1.5rem;
        display: none;
        flex-direction: column;
        overflow: hidden;
        box-shadow: 0 15px 50px rgba(0,0,0,0.5);
        backdrop-filter: blur(10px);
        animation: slideUp 0.3s ease;
    }

    @keyframes slideUp {
        from { transform: translateY(20px); opacity: 0; }
        to { transform: translateY(0); opacity: 1; }
    }

    .chatbot-window.active {
        display: flex;
    }

    .chatbot-header {
        background: linear-gradient(90deg, var(--primary-color), var(--accent));
        padding: 1.2rem;
        color: white;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .chatbot-header h3 {
        margin: 0;
        font-size: 1.1rem;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .chatbot-messages {
        flex: 1;
        padding: 1rem;
        overflow-y: auto;
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .message {
        max-width: 80%;
        padding: 0.8rem 1rem;
        border-radius: 1rem;
        font-size: 0.95rem;
        line-height: 1.4;
    }

    .message.bot {
        background: rgba(255, 255, 255, 0.05);
        align-self: flex-start;
        border-bottom-left-radius: 0.2rem;
        border: 1px solid var(--glass-border);
    }

    .message.user {
        background: var(--primary-color);
        color: white;
        align-self: flex-end;
        border-bottom-right-radius: 0.2rem;
    }

    .chatbot-input {
        padding: 1rem;
        border-top: 1px solid var(--glass-border);
        display: flex;
        gap: 0.5rem;
    }

    .chatbot-input input {
        flex: 1;
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid var(--glass-border);
        padding: 0.6rem 1rem;
        border-radius: 50px;
        color: white;
        outline: none;
    }

    .chatbot-input button {
        background: var(--primary-color);
        color: white;
        border: none;
        width: 38px;
        height: 38px;
        border-radius: 50%;
        cursor: pointer;
        transition: background 0.3s;
    }

    .chatbot-input button:hover {
        background: var(--accent);
    }

    .typing-indicator {
        font-size: 0.8rem;
        color: var(--text-muted);
        margin-bottom: 0.5rem;
        display: none;
    }
`;

const faqData = [
    { keywords: ['học phí', 'tiền', 'giá', 'bao nhiêu'], response: "Học phí thường dao động từ 200k - 350k/buổi (2 giờ) tùy vào khối lớp và môn học. Mình có ưu đãi nếu bạn đăng ký theo nhóm đấy!" },
    { keywords: ['địa chỉ', 'ở đâu', 'chỗ nào', 'hà nội'], response: "Mình dạy trực tiếp tại khu vực Cầu Giấy, Nam Từ Liêm (Hà Nội). Ngoài ra, mình có các lớp Online chất lượng cao qua Zoom/Google Meet." },
    { keywords: ['liên hệ', 'sđt', 'điện thoại', 'zalo', 'facebook', 'fb'], response: "Bạn có thể liên hệ trực tiếp qua Zalo: 033.xxx.xxxx hoặc inbox Fanpage Facebook Mtruongdayyy để mình phản hồi nhanh nhất nhé!" },
    { keywords: ['môn', 'dạy gì', 'toán', 'lý', 'hóa'], response: "Mình chuyên dạy Toán, Lý, Hóa cấp 2 và cấp 3, đặc biệt là ôn thi THPT Quốc gia môn Hóa học (mình đạt 10 điểm tuyệt đối môn này)." },
    { keywords: ['hi', 'chào', 'hello', 'tư vấn'], response: "Xin chào! Mình là trợ lý ảo của anh Trường. Bạn cần hỏi về học phí, lịch học hay tài liệu môn gì không?" }
];

class Chatbot {
    constructor() {
        this.render();
        this.initElements();
        this.addEventListeners();
        this.addWelcomeMessage();
    }

    render() {
        const styleSheet = document.createElement("style");
        styleSheet.innerText = chatbotStyles;
        document.head.appendChild(styleSheet);

        const container = document.createElement('div');
        container.className = 'chatbot-widget';
        container.innerHTML = `
            <button class="chatbot-button" id="chat-toggle">
                <i class="fa-solid fa-comments"></i>
            </button>
            <div class="chatbot-window" id="chat-window">
                <div class="chatbot-header">
                    <h3><i class="fa-solid fa-robot"></i> Mtruong Support</h3>
                    <i class="fa-solid fa-xmark" style="cursor:pointer" id="chat-close"></i>
                </div>
                <div class="chatbot-messages" id="chat-messages">
                    <div class="typing-indicator" id="typing">Đang trả lời...</div>
                </div>
                <form class="chatbot-input" id="chat-form">
                    <input type="text" id="user-input" placeholder="Nhập câu hỏi của bạn..." autocomplete="off">
                    <button type="submit"><i class="fa-solid fa-paper-plane"></i></button>
                </form>
            </div>
        `;
        document.body.appendChild(container);
    }

    initElements() {
        this.toggleBtn = document.getElementById('chat-toggle');
        this.closeBtn = document.getElementById('chat-close');
        this.window = document.getElementById('chat-window');
        this.messagesContainer = document.getElementById('chat-messages');
        this.form = document.getElementById('chat-form');
        this.input = document.getElementById('user-input');
        this.typing = document.getElementById('typing');
    }

    addEventListeners() {
        this.toggleBtn.addEventListener('click', () => this.window.classList.toggle('active'));
        this.closeBtn.addEventListener('click', () => this.window.classList.remove('active'));
        this.form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleUserMessage();
        });
    }

    addWelcomeMessage() {
        setTimeout(() => {
            this.addMessage("Chào bạn! Mình là trợ lý của anh Trường. Bạn cần hỏi về lịch học, học phí hay môn học nào không?", 'bot');
        }, 1000);
    }

    async handleUserMessage() {
        const text = this.input.value.trim();
        if (!text) return;

        this.addMessage(text, 'user');
        this.input.value = '';
        
        this.showTyping(true);
        
        try {
            const aiResponse = await this.fetchAIResponse(text);
            this.showTyping(false);
            this.addMessage(aiResponse, 'bot');
        } catch (error) {
            console.error("Chatbot Error:", error);
            // Fallback to keyword matching if AI fails
            const fallbackResponse = this.getFallbackResponse(text);
            this.showTyping(false);
            this.addMessage(fallbackResponse, 'bot');
        }
    }

    async fetchAIResponse(prompt) {
        // Only call backend if it looks like a question or complex query
        const response = await fetch(`${API_CONFIG.BASE_URL}/api/ai/chat`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ prompt: prompt })
        });
        
        if (!response.ok) throw new Error('API Error');
        const data = await response.json();
        return data.response;
    }

    getFallbackResponse(text) {
        const lowerText = text.toLowerCase();
        for (const item of faqData) {
            if (item.keywords.some(k => lowerText.includes(k))) {
                return item.response;
            }
        }
        return "Hiện tại bộ não AI của mình đang nghỉ ngơi một chút. Bạn vui lòng nhắn tin trực tiếp cho anh Trường qua Facebook 'Mtruongdayyy' nhé!";
    }

    addMessage(text, sender) {
        const msgDiv = document.createElement('div');
        msgDiv.className = `message ${sender}`;
        msgDiv.innerText = text;
        this.messagesContainer.insertBefore(msgDiv, this.typing);
        this.messagesContainer.scrollTop = this.messagesContainer.scrollHeight;
    }

    showTyping(show) {
        this.typing.style.display = show ? 'block' : 'none';
        this.messagesContainer.scrollTop = this.messagesContainer.scrollHeight;
    }
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    new Chatbot();
});
