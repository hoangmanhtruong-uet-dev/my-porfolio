const GLOBAL_CONFIG = {
    // Anniversary Config
    anniversary: {
        startDate: '2018-03-13',
        nameMale: 'Nino 🤵 🌟',
        nameFemale: 'Béo 👸 💮',
        zodiacMale: 'Bọ Cạp',
        zodiacFemale: 'Xử Nữ',
        imgMale: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80',
        imgFemale: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=200&q=80'
    },
    // Exam/Academic Config
    academic: {
        examName: 'Kỳ thi Cuối kỳ - UET',
        examDate: '2025-06-15T08:00:00',
        hackerrankUser: 'h25020423'
    },
    // Music Config
    music: {
        title: 'Perfect - Ed Sheeran',
        url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3'
    }
};

var API_CONFIG = window.API_CONFIG || {
    // Tự động nhận diện môi trường để gọi API Backend
    // Nếu deploy theo dạng Monolith (Option 1): để rỗng '' là đúng.
    // Nếu deploy tách Frontend/Backend (Option 2): hãy điền URL của Render Backend vào đây.
    BASE_URL: (window.location.hostname === '127.0.0.1' || 
               window.location.hostname === 'localhost' || 
               window.location.protocol === 'file:')
        ? 'http://localhost:8080' 
        : '' // Ví dụ: 'https://backend-mtruong.onrender.com'
};

// Export for use in other scripts
if (typeof module !== 'undefined') {
    module.exports = GLOBAL_CONFIG;
}
