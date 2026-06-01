const playlist = [
    { title: "Perfect - Ed Sheeran", url: "https://files.catbox.moe/979u74.mp3" },
    { title: "Beautiful In White", url: "https://files.catbox.moe/978t93.mp3" },
    { title: "Thinking Out Loud", url: "https://files.catbox.moe/6v7n6y.mp3" },
    { title: "My Love - Westlife", url: "https://files.catbox.moe/8y8m9f.mp3" }
];

let currentSongIndex = 0;
let isPlaying = false;
const audio = new Audio(playlist[currentSongIndex].url);
audio.loop = true;

function initMusicPlayer() {
    const playerContainer = document.querySelector('.music-player');
    if (!playerContainer) return;

    // Create UI
    playerContainer.innerHTML = `
        <i class="fa-solid fa-play music-btn" id="play-pause" onclick="toggleMusic()"></i>
        <div style="display: flex; flex-direction: column;">
            <span id="song-title" style="font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 150px;">${playlist[currentSongIndex].title}</span>
        </div>
        <i class="fa-solid fa-list music-btn" id="playlist-btn" onclick="togglePlaylist()" style="font-size: 1rem;"></i>
        
        <div class="playlist-dropdown" id="playlist-menu">
            <div style="font-weight: 800; font-size: 0.7rem; color: #94a3b8; margin-bottom: 10px; text-transform: uppercase; letter-spacing: 1px;">Danh sách phát</div>
            ${playlist.map((song, index) => `
                <div class="song-item ${index === 0 ? 'active' : ''}" onclick="changeSong(${index})">
                    <i class="fa-solid fa-music"></i>
                    <span>${song.title}</span>
                </div>
            `).join('')}
        </div>
    `;

    // Handle clicks outside to close playlist
    document.addEventListener('click', (e) => {
        const menu = document.getElementById('playlist-menu');
        const btn = document.getElementById('playlist-btn');
        if (menu && !menu.contains(e.target) && e.target !== btn) {
            menu.classList.remove('show');
        }
    });
}

function toggleMusic() {
    const btn = document.getElementById('play-pause');
    if (audio.paused) {
        audio.play().then(() => {
            isPlaying = true;
            btn.className = 'fa-solid fa-pause music-btn';
        }).catch(err => {
            console.log("Autoplay blocked");
            alert("Vui lòng nhấn lại lần nữa để phát nhạc (Quy định của trình duyệt) ❤️");
        });
    } else {
        audio.pause();
        isPlaying = false;
        btn.className = 'fa-solid fa-play music-btn';
    }
}

function togglePlaylist() {
    const menu = document.getElementById('playlist-menu');
    menu.classList.toggle('show');
}

function changeSong(index) {
    currentSongIndex = index;
    const song = playlist[index];
    
    // Update UI
    document.getElementById('song-title').innerText = song.title;
    document.querySelectorAll('.song-item').forEach((el, i) => {
        el.classList.toggle('active', i === index);
    });

    // Update Audio
    audio.src = song.url;
    if (isPlaying) {
        audio.play();
    }
    
    // Close menu
    togglePlaylist();
}

// Global initialization
document.addEventListener('DOMContentLoaded', initMusicPlayer);
