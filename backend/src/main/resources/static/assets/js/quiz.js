const defaultExams = [
    {
        id: 1,
        title: "Đề thi thử Hóa học số 1",
        time: 15,
        questions: [
            {
                question: "Công thức hóa học của axit Sunfuric là gì?",
                options: ["H2SO3", "HCl", "H2SO4", "HNO3"],
                correct: 2,
                explanation: "Axit Sunfuric có công thức là H2SO4, là một axit mạnh và quan trọng trong công nghiệp."
            }
        ]
    }
];

let exams = JSON.parse(localStorage.getItem('chemistry_exams')) || defaultExams;
let currentExam = null;
let currentQuestionIndex = 0;
let score = 0;
let userAnswers = [];
let timer;
let timeLeft = 0;

const startScreen = document.getElementById('start-screen');
const quizScreen = document.getElementById('quiz-screen');
const resultScreen = document.getElementById('result-screen');
const examSelectionList = document.getElementById('exam-selection-list');
const questionText = document.getElementById('question-text');
const optionsContainer = document.getElementById('options-container');
const questionCount = document.getElementById('question-count');
const progressFill = document.getElementById('progress-fill');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');
const submitBtn = document.getElementById('submit-btn');
const timerDisplay = document.getElementById('timer');

// Initialize Exam Selection
function init() {
    examSelectionList.innerHTML = '';
    exams.forEach(exam => {
        const card = document.createElement('div');
        card.className = 'exam-card-pro';
        card.innerHTML = `
            <div class="exam-icon">
                <i class="fa-solid fa-file-lines"></i>
            </div>
            <h4 class="exam-title-pro">${exam.title}</h4>
            <div class="exam-meta-pro">
                <div class="meta-item"><i class="fa-solid fa-clock"></i> ${exam.time} phút</div>
                <div class="meta-item"><i class="fa-solid fa-circle-question"></i> ${exam.questions.length} câu hỏi</div>
            </div>
            <button class="btn-start-exam">
                BẮT ĐẦU THI <i class="fa-solid fa-arrow-right"></i>
            </button>
        `;
        card.onclick = () => startExam(exam.id);
        examSelectionList.appendChild(card);
    });
}

function startExam(id) {
    currentExam = exams.find(e => e.id === id);
    if (!currentExam || currentExam.questions.length === 0) {
        alert("Đề thi này chưa có câu hỏi!");
        return;
    }
    
    currentQuestionIndex = 0;
    score = 0;
    userAnswers = new Array(currentExam.questions.length).fill(null);
    timeLeft = currentExam.time * 60;
    
    startScreen.style.display = 'none';
    quizScreen.style.display = 'block';
    
    loadQuestion();
    startTimer();
}

function loadQuestion() {
    const q = currentExam.questions[currentQuestionIndex];
    questionText.innerText = q.question;
    questionCount.innerText = `Câu hỏi ${currentQuestionIndex + 1} / ${currentExam.questions.length}`;
    progressFill.style.width = `${((currentQuestionIndex + 1) / currentExam.questions.length) * 100}%`;
    
    optionsContainer.innerHTML = '';
    q.options.forEach((option, index) => {
        const div = document.createElement('div');
        div.className = `option-item ${userAnswers[currentQuestionIndex] === index ? 'selected' : ''}`;
        div.innerHTML = `
            <span>${String.fromCharCode(65 + index)}</span>
            <span>${option}</span>
        `;
        div.onclick = () => selectOption(index);
        optionsContainer.appendChild(div);
    });

    prevBtn.disabled = currentQuestionIndex === 0;
    if (currentQuestionIndex === currentExam.questions.length - 1) {
        nextBtn.style.display = 'none';
        submitBtn.style.display = 'block';
    } else {
        nextBtn.style.display = 'block';
        submitBtn.style.display = 'none';
    }
}

function selectOption(index) {
    userAnswers[currentQuestionIndex] = index;
    const options = document.querySelectorAll('.option-item');
    options.forEach(opt => opt.classList.remove('selected'));
    options[index].classList.add('selected');
}

function navigate(direction) {
    currentQuestionIndex += direction;
    loadQuestion();
}

function startTimer() {
    clearInterval(timer);
    timer = setInterval(() => {
        timeLeft--;
        updateTimerDisplay();
        if (timeLeft <= 0) endQuiz();
    }, 1000);
}

function updateTimerDisplay() {
    const min = Math.floor(timeLeft / 60);
    const sec = timeLeft % 60;
    timerDisplay.innerText = `${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`;
    if (timeLeft < 60) timerDisplay.style.color = '#ef4444';
}

function endQuiz() {
    clearInterval(timer);
    quizScreen.style.display = 'none';
    resultScreen.style.display = 'block';
    
    score = 0;
    userAnswers.forEach((ans, i) => {
        if (ans === currentExam.questions[i].correct) score++;
    });

    const finalScore = (score / currentExam.questions.length * 10).toFixed(1);
    document.getElementById('final-score').innerText = finalScore;
    document.querySelector('.score-circle span').innerText = `/ 10`;
    
    // Save to student profile (LocalStorage only)
    const student = JSON.parse(localStorage.getItem('current_student'));
    if (student) {
        if (!student.history) student.history = [];
        student.history.push({
            examTitle: currentExam.title,
            score: parseFloat(finalScore),
            date: new Date().toISOString()
        });
        localStorage.setItem('current_student', JSON.stringify(student));
        
        // Also update the general student_profile for backward compatibility if needed
        const profile = JSON.parse(localStorage.getItem('student_profile')) || { name: student.fullName, history: [], totalPoints: 0 };
        profile.history.push({
            examTitle: currentExam.title,
            score: parseFloat(finalScore),
            date: new Date().toISOString()
        });
        localStorage.setItem('student_profile', JSON.stringify(profile));
    }

    showReview();
}

function showReview() {
    const reviewContainer = document.getElementById('answer-review');
    reviewContainer.innerHTML = '';
    currentExam.questions.forEach((q, i) => {
        const isCorrect = userAnswers[i] === q.correct;
        const div = document.createElement('div');
        div.className = `review-item ${isCorrect ? 'correct' : ''}`;
        div.innerHTML = `
            <h4 class="mb-2">Câu ${i+1}: ${q.question}</h4>
            <p><strong>Bạn chọn:</strong> ${userAnswers[i] !== null ? q.options[userAnswers[i]] : 'Bỏ trống'}</p>
            <p><strong>Đáp án đúng:</strong> ${q.options[q.correct]}</p>
            <p class="text-muted mt-2"><i>${q.explanation}</i></p>
        `;
        reviewContainer.appendChild(div);
    });
}

nextBtn.addEventListener('click', () => navigate(1));
prevBtn.addEventListener('click', () => navigate(-1));
submitBtn.addEventListener('click', endQuiz);

document.addEventListener('DOMContentLoaded', () => {
    const isStudent = localStorage.getItem('current_student') !== null;
    const isAdmin = sessionStorage.getItem('isAdmin') === 'true';
    
    const authScreen = document.getElementById('auth-required-screen');
    const mainContent = document.getElementById('quiz-main-content');
    
    if (isStudent || isAdmin) {
        if (authScreen) authScreen.style.display = 'none';
        if (mainContent) mainContent.style.display = 'block';
        init();
    } else {
        if (authScreen) authScreen.style.display = 'flex';
        if (mainContent) mainContent.style.display = 'none';
    }
});
