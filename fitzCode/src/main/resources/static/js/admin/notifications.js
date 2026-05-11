window.notificationCount = 0;

// 인증 상태 체크 함수
function checkAuthenticated(callback) {
    $.ajax({
        url: '/api/user/check',
        type: 'GET',
        success: function(response) {
            console.log("인증 상태 체크 응답:", response);
            if (response.authenticated) {
                callback(true);
            } else {
                callback(false);
            }
        },
        error: function(xhr, status, error) {
            console.error("인증 상태 체크 오류:", error, "상태:", xhr.status, "응답:", xhr.responseText);
            callback(false);
        }
    });
}

// 페이지 로드 시 초기 알림 개수
$(document).ready(function () {
    checkAuthenticated(function(isAuthenticated) {
        if (isAuthenticated) {
            loadNotifications();
        }
    });
});

// 알림 구독 시작
function subscribeToNotifications() {
    checkAuthenticated(function(isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인 안한 사용자 : 알림 구독 비활성화");
            return;
        }

        const eventSource = new EventSource("/admin/notice/subscribe");

        eventSource.onopen = () => {
            console.log("알림 연결 시작: " + new Date().toLocaleTimeString());
        };

        eventSource.addEventListener("NOTICE_CREATED", (event) => {
            console.log("공지사항 생성 이벤트 수신: " + new Date().toLocaleTimeString());
            console.log("이벤트 데이터:", event.data);
            try {
                const notice = JSON.parse(event.data);
                console.log("파싱된 공지사항:", notice);
                if (notice && notice.noticeId && notice.title) {
                    const notificationMessage = "새로운 공지사항: " + notice.title;
                    addNotification(notificationMessage);
                    window.notificationCount++;
                    updateNotificationBadge();
                    saveNotification(notice);
                    loadNotifications(); // 알림 목록 갱신
                } else {
                    console.error("공지사항 데이터 오류: noticeId 또는 title이 없음", notice);
                }
            } catch (e) {
                console.error("JSON 파싱 오류:", e, "데이터:", event.data);
            }
        });

        eventSource.addEventListener("INQUIRY_ANSWERED", (event) => {
            console.log("1대1 문의 답변 이벤트 수신: " + new Date().toLocaleTimeString());
            console.log("이벤트 데이터:", event.data);
            try {
                const inquiry = JSON.parse(event.data);
                console.log("파싱된 문의:", inquiry);
                if (inquiry && inquiry.inquiryId && inquiry.subject) {
                    const notificationMessage = "1대1 문의 답변이 있습니다.";
                    addNotification(notificationMessage);
                    window.notificationCount++;
                    updateNotificationBadge();
                    saveNotificationForInquiry(inquiry);
                    loadNotifications(); // 알림 목록 갱신
                } else {
                    console.error("문의 데이터 오류: inquiryId 또는 subject가 없음", inquiry);
                }
            } catch (e) {
                console.error("JSON 파싱 오류:", e, "데이터:", event.data);
            }
        });

        eventSource.addEventListener("INQUIRY_CLOSED", (event) => {
            console.log("1대1 문의 종료 이벤트 수신: " + new Date().toLocaleTimeString());
            console.log("이벤트 데이터:", event.data);
            try {
                const inquiry = JSON.parse(event.data);
                console.log("파싱된 문의:", inquiry);
                if (inquiry && inquiry.inquiryId && inquiry.subject) {
                    const notificationMessage = "1대1 문의가 종료되었습니다.";
                    addNotification(notificationMessage);
                    window.notificationCount++;
                    updateNotificationBadge();
                    saveNotificationForInquiryClosed(inquiry);
                    loadNotifications(); // 알림 목록 갱신
                } else {
                    console.error("문의 데이터 오류: inquiryId 또는 subject가 없음", inquiry);
                }
            } catch (e) {
                console.error("JSON 파싱 오류:", e, "데이터:", event.data);
            }
        });

        eventSource.onerror = (error) => {
            console.error("알림 연결 오류 발생: " + new Date().toLocaleTimeString(), error);
            eventSource.close();
            setTimeout(subscribeToNotifications, 1000);
        };
    });
}

// 알림 메시지를 화면에 추가
function addNotification(message) {
    const div = document.createElement("div");
    div.textContent = `${new Date().toLocaleTimeString()} - ${message}`;
    document.getElementById("notifications").appendChild(div);
}

// 공지사항 알림 서버 저장
function saveNotification(notice) {
    checkAuthenticated(function(isAuthenticated) {
        if (!isAuthenticated) return;

        $.ajax({
            url: '/api/notifications',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                userId: null,
                type: 1, // NOTICE
                message: "새로운 공지사항: " + notice.title,
                relatedId: notice.noticeId
            }),
            success: function () {
                console.log('알림 저장 성공');
            },
            error: function (xhr, status, error) {
                console.error('알림 저장 실패:', error, '상태:', xhr.status, '응답:', xhr.responseText);
            }
        });
    });
}

// 1대1 문의 답변 알림 서버 저장
function saveNotificationForInquiry(inquiry) {
    checkAuthenticated(function(isAuthenticated) {
        if (!isAuthenticated) return;

        $.ajax({
            url: '/api/notifications',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                userId: null,
                type: 4, // INQUIRY_RESPONSE
                message: "1대1 문의 답변이 있습니다.",
                relatedId: inquiry.inquiryId
            }),
            success: function () {
                console.log('1대1 문의 답변 알림 저장 성공');
            },
            error: function (xhr, status, error) {
                console.error('1대1 문의 답변 알림 저장 실패:', error, '상태:', xhr.status, '응답:', xhr.responseText);
            }
        });
    });
}

// 1대1 문의 종료
function saveNotificationForInquiryClosed(inquiry) {
    checkAuthenticated(function(isAuthenticated) {
        if (!isAuthenticated) return;

        $.ajax({
            url: '/api/notifications',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                userId: null,
                type: 10, // INQUIRY_CLOSED
                message: "1대1 문의가 종료되었습니다.",
                relatedId: inquiry.inquiryId
            }),
            success: function () {
                console.log('1대1 문의 종료 알림 저장 성공');
            },
            error: function (xhr, status, error) {
                console.error('1대1 문의 종료 알림 저장 실패:', error, '상태:', xhr.status, '응답:', xhr.responseText);
            }
        });
    });
}

// 알림 구독 시작
window.onload = () => {
    checkAuthenticated(function(isAuthenticated) {
        if (isAuthenticated) {
            subscribeToNotifications();
        }
    });
};

// 알림 배지 업데이트
function updateNotificationBadge() {
    const notificationElement = document.querySelector('.notification');
    if (notificationElement) {
        notificationElement.setAttribute('data-count', window.notificationCount);
    }
}

// 알림 로드 함수
function loadNotifications() {
    checkAuthenticated(function(isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자 : 알림 로드를 중단");
            return;
        }

        console.log("알림 로드 시작");
        $.ajax({
            url: '/api/notifications',
            type: 'GET',
            dataType: 'json',
            xhrFields: {
                withCredentials: true
            },
            success: function (data) {
                console.log("알림 목록 데이터 수신:", data);
                $('#notificationDropdown').empty(); // headerNotification.js와 통합 위해
                window.notificationCount = data ? data.length || 0 : 0;
                updateNotificationBadge();
                if (Array.isArray(data)) {
                    if (data.length === 0) {
                        console.log("알림이 없음");
                        $('#notificationDropdown').prepend('<div class="notification-item">알림이 없습니다.</div>');
                    } else {
                        data.forEach(notification => {
                            console.log("알림 항목:", notification);
                            const message = notification.message || '새로운 공지사항이 있음';
                            addNotificationItem(notification.notificationId, message); // headerNotification.js와 통합
                        });
                    }
                } else {
                    console.warn('예상치 못한 데이터 형식:', data);
                    $('#notificationDropdown').prepend('<div class="notification-item">데이터 형식 오류</div>');
                }
            },
            error: function (xhr, status, error) {
                console.error('알림 목록 로드 실패:', error, '상태:', xhr.status, '응답:', xhr.responseText);
                $('#notificationDropdown').prepend('<div class="notification-item">알림 로드 실패 (상태: ' + xhr.status + ')</div>');
            }
        });
    });
}

// headerNotification.js와 통합
function addNotificationItem(notificationId, message) {
    const dropdown = $('#notificationDropdown');
    dropdown.find('.notification-actions').remove();
    const item = $(`<div class="notification-item" data-id="${notificationId}"><span>${message}</span><button class="close-btn" data-id="${notificationId}">X</button></div>`);
    item.click(function (e) {
        if (!$(e.target).hasClass('close-btn')) {
            window.location.href = '/notice';
        }
    });
    dropdown.prepend(item);
    addNotificationActions();
}

function addNotificationActions() {
    const dropdown = $('#notificationDropdown');
    if (dropdown.find('.notification-actions').length === 0) {
        const actions = $(`<div class="notification-actions"><button id="markAllRead">모두 읽음</button><button id="deleteAll" class="secondary">모두 삭제</button></div>`);
        dropdown.append(actions);
    }
}