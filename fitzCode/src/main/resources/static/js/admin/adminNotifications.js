window.adminNotificationCount = 0;
let eventSource = null; // SSE 연결 객체 저장

// 인증 상태 체크 함수 (관리자용)
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

// 관리자 알림 이벤트 바인딩
function bindAdminNotificationEvents() {
    const notificationBell = document.getElementById('adminNotificationBell');
    const notificationDropdown = document.getElementById('adminNotificationDropdown');
    if (notificationBell) {
        notificationBell.addEventListener('click', function(e) {
            e.preventDefault();
            console.log("관리자 알림 벨 클릭됨");
            if (notificationDropdown.style.display === 'block') {
                notificationDropdown.style.display = 'none';
            } else {
                adminLoadNotifications(); // 알림 목록 갱신
                notificationDropdown.style.display = 'block';
                document.addEventListener('click', function closeDropdown(event) {
                    if (!notificationBell.contains(event.target) && !notificationDropdown.contains(event.target)) {
                        notificationDropdown.style.display = 'none';
                        document.removeEventListener('click', closeDropdown);
                    }
                }, { once: true });
            }
        });
    } else {
        console.warn("관리자 알림 벨 요소를 찾을 수 없음");
    }
}

// 페이지 로드 시 초기 알림 개수 및 SSE 구독 시작
$(document).ready(function() {
    checkAuthenticated(function(isAuthenticated) {
        console.log("페이지 로드 시 인증 상태:", isAuthenticated);
        if (isAuthenticated) {
            bindAdminNotificationEvents(); // 이벤트 바인딩
            adminLoadNotifications();
            adminSubscribeToNotifications(); // SSE 연결 시작
        } else {
            console.log("인증되지 않은 사용자: 관리자 알림 로드 및 구독 중단");
        }
    });
});

// 알림 구독 시작 (관리자용)
function adminSubscribeToNotifications() {
    checkAuthenticated(function(isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자: 관리자 알림 구독 비활성화");
            return;
        }

        // 기존 SSE 연결이 있으면 종료
        if (eventSource) {
            console.log("기존 SSE 연결 종료");
            eventSource.close();
            eventSource = null;
        }

        console.log("SSE 연결 시작: /admin/admin-notice/subscribe");
        eventSource = new EventSource("/admin/admin-notice/subscribe");

        eventSource.onopen = () => {
            console.log("관리자 알림 연결 시작: " + new Date().toLocaleTimeString());
        };

        eventSource.addEventListener("INQUIRY_CREATED", (event) => {
            console.log("1대1 문의 등록 이벤트 수신: " + new Date().toLocaleTimeString());
            console.log("이벤트 데이터:", event.data);
            try {
                const inquiry = JSON.parse(event.data);
                console.log("파싱된 문의:", inquiry);
                if (inquiry && inquiry.inquiryId && inquiry.subject) {
                    adminLoadNotifications(); // 알림 목록 갱신
                } else {
                    console.error("문의 데이터 오류: inquiryId 또는 subject가 없음", inquiry);
                }
            } catch (e) {
                console.error("JSON 파싱 오류:", e, "데이터:", event.data);
            }
        });

        eventSource.onerror = (error) => {
            console.error("관리자 알림 연결 오류 발생: " + new Date().toLocaleTimeString(), error);
            eventSource.close();
            eventSource = null;
            // 재연결
            setTimeout(() => {
                adminSubscribeToNotifications();
            }, 3000);
        };
    });
}

// 알림 메시지를 화면에 추가 (관리자용)
function adminAddNotification(message) {
    const dropdown = $('#adminNotificationDropdown');
    const div = $('<div>').addClass('notification-item').text(`${new Date().toLocaleTimeString()} - ${message}`);
    dropdown.append(div);
    dropdown.show();
    console.log("관리자 알림 추가됨:", message);
}

// 알림 배지 업데이트 (관리자용)
function adminUpdateNotificationBadge() {
    const notificationBell = $('#adminNotificationBell');
    if (notificationBell.length > 0) {
        if (window.adminNotificationCount > 0) {
            notificationBell.css('position', 'relative');
            notificationBell[0].style.setProperty('--notification-count', `"${window.adminNotificationCount}"`);
            notificationBell.addClass('has-notifications');
        } else {
            notificationBell.removeClass('has-notifications');
            notificationBell[0].style.setProperty('--notification-count', '""');
        }
        console.log("관리자 알림 배지 업데이트됨, 개수:", window.adminNotificationCount);
    } else {
        console.warn("관리자 알림 벨 요소를 찾을 수 없음");
    }
}

// 알림 로드 함수 (관리자용)
function adminLoadNotifications() {
    checkAuthenticated(function(isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자: 관리자 알림 로드 중단");
            $('#adminNotificationDropdown').empty();
            $('#adminNotificationDropdown').prepend('<div class="notification-item">로그인 후 알림을 확인하세요.</div>');
            return;
        }

        console.log("관리자 알림 로드 시작");
        $.ajax({
            url: '/api/notifications',
            type: 'GET',
            dataType: 'json',
            xhrFields: { withCredentials: true },
            success: function(data) {
                console.log("관리자 알림 목록 데이터 수신:", typeof data, data);
                const dropdown = $('#adminNotificationDropdown');
                dropdown.empty(); // 드롭다운 초기화
                window.adminNotificationCount = Array.isArray(data) ? data.filter(n => n.type === 5).length : 0;
                adminUpdateNotificationBadge();
                if (Array.isArray(data)) {
                    if (data.length === 0) {
                        console.log("관리자 알림이 없습니다.");
                        dropdown.prepend('<div class="notification-item">알림이 없습니다.</div>');
                    } else {
                        data.forEach(notification => {
                            console.log("관리자 알림 항목:", notification);
                            if (notification.type === 5) { // INQUIRY_CREATED만 표시
                                const message = notification.message || '새로운 알림이 있습니다.';
                                adminAddNotificationItem(notification.notificationId, notification.relatedId, message);
                            }
                        });
                    }
                } else {
                    console.warn('예상치 못한 데이터 형식:', typeof data, data);
                    dropdown.prepend('<div class="notification-item">데이터 형식 오류</div>');
                }
            },
            error: function(xhr, status, error) {
                console.error('관리자 알림 목록 로드 실패:', error, '상태:', xhr.status, '응답:', xhr.responseText);
                $('#adminNotificationDropdown').prepend('<div class="notification-item">알림 로드 실패 (상태: ' + xhr.status + ')</div>');
            }
        });
    });
}

// 알림 항목 추가 (관리자용)
function adminAddNotificationItem(notificationId, relatedId, message) {
    const dropdown = $('#adminNotificationDropdown');
    dropdown.find('.notification-actions').remove();
    const item = $(`<div class="notification-item" data-notification-id="${notificationId}" data-related-id="${relatedId}"><span>${message}</span><button class="close-btn" data-notification-id="${notificationId}">X</button></div>`);
    item.click(function(e) {
        if (!$(e.target).hasClass('close-btn')) {
            const relatedId = $(this).data('related-id');
            window.location.href = '/admin/inquiries/' + relatedId; // related_id로 이동
        }
    });
    // 개별 삭제 버튼 이벤트
    item.find('.close-btn').click(function(e) {
        e.stopPropagation(); // 상위 이벤트 방지
        const notificationId = $(this).data('notification-id');
        adminDeleteNotification(notificationId);
    });
    dropdown.prepend(item);
    adminAddNotificationActions();
    console.log("관리자 알림 항목 추가됨, notificationId:", notificationId, "relatedId:", relatedId);
}

// 알림 작업 버튼 추가 (관리자용)
function adminAddNotificationActions() {
    const dropdown = $('#adminNotificationDropdown');
    if (dropdown.find('.notification-actions').length === 0) {
        const actions = $(`<div class="notification-actions"><button id="markAllRead">모두 읽음</button><button id="deleteAll" class="secondary">모두 삭제</button></div>`);
        dropdown.append(actions);

        // 모두 읽음 버튼 이벤트
        $('#markAllRead').click(function() {
            adminUpdateNotificationsReadStatus();
        });

        // 모두 삭제 버튼 이벤트
        $('#deleteAll').click(function() {
            adminDeleteAllNotifications();
        });
    }
}

// 개별 알림 삭제 (관리자용)
function adminDeleteNotification(notificationId) {
    checkAuthenticated(function(isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자: 관리자 알림 삭제 중단");
            return;
        }

        $.ajax({
            url: '/api/notifications/' + notificationId,
            type: 'DELETE',
            xhrFields: { withCredentials: true },
            success: function() {
                console.log('관리자 알림 삭제 성공:', notificationId);
                window.adminNotificationCount--;
                adminUpdateNotificationBadge();
                $('#adminNotificationDropdown').empty();
                adminLoadNotifications();
            },
            error: function(xhr, status, error) {
                console.error('관리자 알림 삭제 실패:', error, '상태:', xhr.status, '응답:', xhr.responseText);
            }
        });
    });
}

// 모든 알림 삭제 (관리자용)
function adminDeleteAllNotifications() {
    checkAuthenticated(function(isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자: 모든 관리자 알림 삭제 중단");
            return;
        }

        $.ajax({
            url: '/api/notifications',
            type: 'DELETE',
            xhrFields: { withCredentials: true },
            success: function() {
                console.log('모든 관리자 알림 삭제 성공');
                window.adminNotificationCount = 0;
                adminUpdateNotificationBadge();
                $('#adminNotificationDropdown').empty();
                adminLoadNotifications();
            },
            error: function(xhr, status, error) {
            }
        });
    });
}

// 모든 알림 읽음 처리 (관리자용)
function adminUpdateNotificationsReadStatus() {
    checkAuthenticated(function(isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자: 관리자 알림 읽음 처리 중단");
            return;
        }

        $.ajax({
            url: '/api/notifications/read',
            type: 'POST',
            xhrFields: { withCredentials: true },
            success: function() {
                console.log('모든 관리자 알림 읽음 처리 성공');
                $('.notification-item').addClass('read').find('.close-btn').show();
                adminLoadNotifications();
            },
            error: function(xhr, status, error) {
            }
        });
    });
}