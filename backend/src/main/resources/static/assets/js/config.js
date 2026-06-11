const GLOBAL_CONFIG = {
    // Anniversary Config
    anniversary: {
        startDate: '2018-03-13',
        nameMale: 'Nino 🤵 🌟',
        nameFemale: 'Béo 👸 💮',
        zodiacMale: 'Bọ Cạp',
        zodiacFemale: 'Xử Nữ',
        imgMale: 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%22200%22 height=%22200%22%3E%3Crect fill=%2336558f%22 width=%22200%22 height=%22200%22/%3E%3Ctext x=%2250%25%22 y=%2250%25%22 font-size=%2224%22 fill=%22%23fff%22 text-anchor=%22middle%22 dy=%22.3em%22%3E🤵%3C/text%3E%3C/svg%3E',
        imgFemale: 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%22200%22 height=%22200%22%3E%3Crect fill=%23ff69b4%22 width=%22200%22 height=%22200%22/%3E%3Ctext x=%2250%25%22 y=%2250%25%22 font-size=%2224%22 fill=%22%23fff%22 text-anchor=%22middle%22 dy=%22.3em%22%3E👸%3C/text%3E%3C/svg%3E'
    },
    // Exam/Academic Config
    academic: {
        examName: 'Kỳ thi Cuối kỳ - UET',
        examDate: new Date().toISOString(), // Fetched from backend if needed
        hackerrankUser: 'h25020423'
    },
    // Music Config
    music: {
        title: 'Perfect - Ed Sheeran',
        url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3'
    }
};

var API_CONFIG = window.API_CONFIG || {
    // PRODUCTION: https://my-porfolio-1-b1x3.onrender.com (Monolith Deploy)
    // LOCAL: http://localhost:8080
    BASE_URL: (window.location.hostname === '127.0.0.1' || 
               window.location.hostname === 'localhost' || 
               window.location.protocol === 'file:')
        ? 'http://localhost:8080' 
        : '' // Production: Frontend & Backend on same domain
};

// Export for use in other scripts
if (typeof module !== 'undefined') {
    module.exports = GLOBAL_CONFIG;
}
