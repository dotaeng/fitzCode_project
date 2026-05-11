let timeLeft = 1800;

function syncSessionTime(callback) {
    fetch('/admin/session-info', {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
    })
        .then(response => response.json())
        .then(data => {
            console.log('Server timeLeft:', data.timeLeft);
            const storedTimeLeft = parseInt(localStorage.getItem('sessionTimeLeft') || '1800', 10);
            // 서버 값이랑 로컬 값 중 더 작은 값 사용
            timeLeft = Math.min(storedTimeLeft, data.timeLeft);
            localStorage.setItem('sessionTimeLeft', timeLeft);
            if (callback) callback();
        })
        .catch(error => {
            console.error('Error fetching session info:', error);
            if (callback) callback();
        });
}

window.addEventListener('load', function() {
    const storedTimeLeft = parseInt(localStorage.getItem('sessionTimeLeft') || '1800', 10);
    timeLeft = storedTimeLeft;
    syncSessionTime(updateTimer);
});

const timerElement = document.getElementById('session-timer');
const extendButton = document.getElementById('extend-session-btn');

function updateTimer() {
    if (!timerElement) return;

    let minutes = Math.floor(timeLeft / 60);
    let seconds = timeLeft % 60;
    timerElement.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

    if (timeLeft <= 0) {
        alert("세션이 만료되었습니다. 다시 로그인해 주세요.");
        window.location.href = "/login?expired";
        return;
    } else if (timeLeft <= 30) {
        timerElement.style.color = "red";
        if (!timerElement.classList.contains('warning')) {
            alert("세션이 30초 후 만료됩니다");
            timerElement.classList.add('warning');
        }
    }

    timeLeft--;
    localStorage.setItem('sessionTimeLeft', timeLeft);
}

if (timerElement) {
    setInterval(updateTimer, 1000);
    setInterval(() => syncSessionTime(updateTimer), 60000);
}

if (extendButton) {
    extendButton.addEventListener('click', function() {
        fetch('/admin/extend-session', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => {
                if (response.ok) {
                    timeLeft = 1800;
                    if (timerElement) {
                        timerElement.style.color = "#000000";
                        timerElement.classList.remove('warning');
                    }
                    localStorage.setItem('sessionTimeLeft', timeLeft);
                    updateTimer();
                    alert("세션이 30분 연장되었습니다.");
                    syncSessionTime(updateTimer);
                } else {
                    alert("세션 연장에 실패했습니다. 다시 로그인해 주세요.");
                }
            })
            .catch(error => {
                console.error('Error extending session:', error);
                alert("세션 연장에 실패했습니다. 다시 로그인해 주세요.");
            });
    });
}