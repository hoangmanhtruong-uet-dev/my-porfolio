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
        examDate: '2025-06-15T08:00:00'
    },
    // Music Config
    music: {
        title: 'Perfect - Ed Sheeran',
        url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3'
    }
};

// Export for use in other scripts
if (typeof module !== 'undefined') {
    module.exports = GLOBAL_CONFIG;
}
