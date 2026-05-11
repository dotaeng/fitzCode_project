let cartUpdateInterval;

// 인증 상태 체크 함수
function checkAuthenticated(callback) {
    $.ajax({
        url: '/api/user/check',
        type: 'GET',
        success: function (response) {
            console.log("인증 상태 체크 응답:", response);
            if (response.authenticated) {
                callback(true);
            } else {
                callback(false);
            }
        },
        error: function (xhr, status, error) {
            console.error("인증 상태 체크 오류:", error, "상태:", xhr.status, "응답:", xhr.responseText);
            callback(false);
        }
    });
}

// 헤더 액티브
$(document).ready(function () {
    const currentPath = window.location.pathname.split('?')[0].replace(/\/$/, '');
    $('.nav-link').each(function () {
        const linkPath = $(this).data('path').replace(/\/$/, '');
        if (currentPath === linkPath) {
            $(this).addClass('active');
        } else {
            $(this).removeClass('active');
        }
    });

    // 검색 입력창 Enter 키 (비로그인 포함)
    $('.input').on('keypress', function (e) {
        if (e.which === 13) {
            e.preventDefault();
            const keyword = $(this).val().trim();
            if (keyword) {
                search(keyword);
            }
        }
    });

    // 로그인 여부에 따른 추가 기능 활성화
    checkAuthenticated(function (isAuthenticated) {
        if (!isAuthenticated) {
            console.log("비로그인 사용자: 알림 및 카트 비활성화");
            $('#ex4 .p1').attr('data-count', '0');
            return;
        }

        // 알림 아이콘 클릭 시 드롭다운 표시
        $('#notificationBell').click(function (e) {
            e.preventDefault();
            const dropdown = $('#notificationDropdown');
            if (dropdown.is(':visible')) {
                dropdown.hide();
            } else {
                loadNotifications();
                dropdown.show();
                $(document).one('click', function (e) {
                    if (!$(e.target).closest('#notificationBell').length) {
                        dropdown.hide();
                    }
                });
            }
        });

        // 개별 알림 삭제
        $(document).on('click', '.close-btn', function (e) {
            e.stopPropagation();
            const notificationId = $(this).data('id');
            deleteNotification(notificationId);
            $(this).closest('.notification-item').remove();
            window.notificationCount--;
            updateNotificationBadge();
        });

        // 모두 읽음 버튼
        $(document).on('click', '#markAllRead', function () {
            $('.notification-item').addClass('read').find('.close-btn').show();
            updateNotificationsReadStatus();
        });

        // 모두 삭제 버튼
        $(document).on('click', '#deleteAll', function () {
            $('#notificationDropdown').empty();
            deleteAllNotifications();
            window.notificationCount = 0;
            updateNotificationBadge();
            addNotificationActions();
        });

        // 초기 카트 수량 로드
        updateCartCount();
        cartUpdateInterval = setInterval(updateCartCount, 10000);

        // 상품 추가 이벤트
        $('#addToCartButton').on('click', function () {
            const productId = $(this).data('product-id') || 1;
            const sizeCode = $(this).data('size-code') || 1;
            addToCart(productId, sizeCode);
        });

        // 알림 구독 시작
        subscribeToNotifications();
    });
});

// 알림 구독 시작
function subscribeToNotifications() {
    checkAuthenticated(function (isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자 : 알림 구독 비활성화");
            return;
        }

        const eventSource = new EventSource("/admin/notice/subscribe");

        eventSource.onopen = () => {
            console.log("알림 연결 시작: " + new Date().toLocaleTimeString());
        };

        eventSource.addEventListener("INQUIRY_ANSWERED", (event) => {
            console.log("1대1 문의 답변 이벤트 수신: " + new Date().toLocaleTimeString());
            console.log("이벤트 데이터:", event.data);
            try {
                const inquiry = JSON.parse(event.data);
                console.log("파싱된 문의:", inquiry);
                if (inquiry && inquiry.inquiryId && inquiry.subject) {
                    const notificationMessage = "1대1 문의에 대한 답변 도착: " + inquiry.subject;
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

        eventSource.onerror = (error) => {
            console.error("알림 연결 오류 발생: " + new Date().toLocaleTimeString(), error);
            eventSource.close();
            setTimeout(subscribeToNotifications, 1000);
        };
    });
}

// 검색 요청 실행 (비로그인 가능)
function search(keyword) {
    $.ajax({
        url: '/search',
        type: 'POST',
        data: { keyword: keyword },
        dataType: 'json',
        success: function (result) {
            window.location.href = '/search/result?keyword=' + encodeURIComponent(keyword);
        },
        error: function (xhr, status, error) {
            console.error('검색 오류:', error);
            window.location.href = '/search/result?keyword=' + encodeURIComponent(keyword);
        }
    });
}

// 알림 개수 뱃지 갱신
function updateNotificationBadge() {
    const notification = $('.notification');
    if (notification.length > 0) {
        notification.attr('data-count', window.notificationCount);
        notification.css('display', 'flex');
    }
}

// 카트 수량 업데이트
function updateCartCount() {
    checkAuthenticated(function (isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자 : 카트 수량 업데이트 중단");
            $('#ex4 .p1').attr('data-count', '0');
            return;
        }

        $.ajax({
            url: '/api/cart/count',
            type: 'GET',
            dataType: 'json',
            xhrFields: {withCredentials: true},
            success: function (data) {
                if (data && data.count !== undefined) {
                    const cartCount = data.count || 0;
                    $('#ex4 .p1').attr('data-count', cartCount);
                    console.log('카트 수량 업데이트 완료: ' + cartCount);
                } else {
                    console.warn('카트 수량 데이터가 없습니다.');
                    $('#ex4 .p1').attr('data-count', '0');
                }
            },
            error: function (xhr, status, error) {
                console.error('카트 수량 로드 실패:', error, '상태:', xhr.status, '응답:', xhr.responseText);
                $('#ex4 .p1').attr('data-count', '0');
            }
        });
    });
}


// 알림 항목 추가
function addNotificationItem(notificationId, message, type, relatedId) {
    const dropdown = $('#notificationDropdown');
    dropdown.find('.notification-actions').remove();
    const item = $(`<div class="notification-item" data-id="${notificationId}" data-type="${type}" data-related-id="${relatedId}"><span>${message}</span><button class="close-btn" data-id="${notificationId}">X</button></div>`);
    item.click(function (e) {
        if (!$(e.target).hasClass('close-btn')) {
            const notificationType = $(this).data('type');
            const relatedId = $(this).data('related-id');
            let redirectUrl = '/admin/notice'; // 공지사항
            if (notificationType === 'INQUIRY_RESPONSE') {
                redirectUrl = `/admin/inquiries/${relatedId}`; // 1대1 문의 알림
            }
            window.location.href = redirectUrl;
        }
    });
    dropdown.prepend(item);
    addNotificationActions();
}

// 알림 작업 버튼 추가
function addNotificationActions() {
    const dropdown = $('#notificationDropdown');
    if (dropdown.find('.notification-actions').length === 0) {
        const actions = $(`<div class="notification-actions"><button id="markAllRead">모두 읽음</button><button id="deleteAll" class="secondary">모두 삭제</button></div>`);
        dropdown.append(actions);
    }
}

// 알림 목록 로드
function loadNotifications() {
    checkAuthenticated(function (isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자 : 알림 로드 중단");
            $('#notificationDropdown').empty();
            $('#notificationDropdown').prepend('<div class="notification-item">로그인 후 알림을 확인하세요.</div>');
            return;
        }

        console.log("알림 로드 시작");
        $.ajax({
            url: '/api/notifications',
            type: 'GET',
            dataType: 'json',
            xhrFields: {withCredentials: true},
            success: function (data) {
                console.log("알림 목록 데이터 수신:", data);
                $('#notificationDropdown').empty();
                window.notificationCount = data ? data.length || 0 : 0;
                updateNotificationBadge();
                if (Array.isArray(data)) {
                    if (data.length === 0) {
                        console.log("알림이 없습니다.");
                        $('#notificationDropdown').prepend('<div class="notification-item">알림이 없습니다.</div>');
                    } else {
                        data.forEach(notification => {
                            console.log("알림 항목:", notification);
                            const message = notification.message || '새로운 공지사항이 있습니다.';
                            const type = notification.type || 'NOTICE'; // type 필드 추가
                            const relatedId = notification.relatedId || ''; // relatedId 추가
                            addNotificationItem(notification.notificationId, message, type, relatedId);
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

// 개별 알림 삭제
function deleteNotification(notificationId) {
    checkAuthenticated(function (isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자 : 알림 삭제 중단");
            return;
        }

        $.ajax({
            url: '/api/notifications/' + notificationId,
            type: 'DELETE',
            xhrFields: {withCredentials: true},
            success: function () {
                console.log('알림 삭제 성공:', notificationId);
                window.notificationCount--;
                updateNotificationBadge();
                $('#notificationDropdown').empty();
                loadNotifications();
            },
            error: function (xhr, status, error) {
                console.error('알림 삭제 실패:', error, '상태:', xhr.status, '응답:', xhr.responseText);
            }
        });
    });
}

// 모든 알림 삭제
function deleteAllNotifications() {
    checkAuthenticated(function (isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자 : 모든 알림 삭제를 중단");
            return;
        }

        $.ajax({
            url: '/api/notifications',
            type: 'DELETE',
            xhrFields: {withCredentials: true},
            success: function () {
                console.log('모든 알림 삭제 성공');
                window.notificationCount = 0;
                updateNotificationBadge();
                $('#notificationDropdown').empty();
                loadNotifications();
            },
            error: function (xhr, status, error) {
                console.error('모든 알림 삭제 실패:', error, '상태:', xhr.status, '응답:', xhr.responseText);
            }
        });
    });
}

// 모든 알림 읽음 처리
function updateNotificationsReadStatus() {
    checkAuthenticated(function (isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자 : 알림 읽음 처리 중단");
            return;
        }

        $.ajax({
            url: '/api/notifications/read',
            type: 'POST',
            xhrFields: {withCredentials: true},
            success: function () {
                console.log('모든 알림 읽음 처리 성공');
                $('.notification-item').addClass('read').find('.close-btn').show();
            },
            error: function (xhr, status, error) {
                console.error('모든 알림 읽음 처리 실패:', error, '상태:', xhr.status, '응답:', xhr.responseText);
            }
        });
    });
}

function addNotification(message) {
    const div = document.createElement("div");
    div.textContent = `${new Date().toLocaleTimeString()} - ${message}`;
    document.getElementById("notificationDropdown").appendChild(div);
}

function saveNotificationForInquiry(inquiry) {
    checkAuthenticated(function (isAuthenticated) {
        if (!isAuthenticated) {
            console.log("로그인하지 않은 사용자 : 알림 저장 중단");
            return;
        }

        const notificationDTO = {
            message: "1대1 문의에 대한 답변 도착: " + inquiry.subject,
            type: "INQUIRY_RESPONSE",
            relatedId: inquiry.inquiryId
        };

        $.ajax({
            url: '/api/notifications',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(notificationDTO),
            xhrFields: {withCredentials: true},
            success: function (response) {
                console.log('알림 저장 성공:', response);
            },
            error: function (xhr, status, error) {
                console.error('알림 저장 실패:', error, '상태:', xhr.status, '응답:', xhr.responseText);
            }
        });
    });
}