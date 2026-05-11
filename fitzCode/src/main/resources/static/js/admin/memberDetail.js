// 주문 내역 보기 클릭시
function toggleOrderHistory(userId) {
    var table = document.getElementById('orderHistoryTable'); // table 요소 가져옴
    var tbody = document.getElementById('orderList'); // body 요소 가져옴

    // 테이블이 보이는지 확인하고 토글
    if (table.style.display === 'table') {
        table.style.display = 'none'; // 테이블 숨김
    } else {
        // 서버에서 주문 내역 가져옴
        var request = new XMLHttpRequest(); // AJAX 요청 객체 생성
        request.open('GET', '/admin/members/' + userId + '/orders', true); // GET 요청 설정
        request.setRequestHeader('Accept', 'application/json'); // JSON 응답 요청

        request.onload = function () {
            if (request.status >= 200 && request.status < 400) {
                // 성공적으로 데이터를 받으면
                var data = JSON.parse(request.responseText); // JSON 데이터 -> JS 객체로 변환
                console.log('데이터:', data);

                // 테이블 본문 내용 초기화
                tbody.innerHTML = '';

                if (data.length === 0) {
                    // 데이터가 없으면 메시지 표시
                    var tr = document.createElement('tr');
                    tr.innerHTML = '<td colspan="3">주문 내역이 없습니다.</td>';
                    tbody.appendChild(tr);
                } else {
                    // 데이터가 있으면 각 주문 항목을 테이블 행으로 추가
                    for (var i = 0; i < data.length; i++) {
                        var order = data[i];
                        console.log('처리 중인 주문:', order);

                        var tr = document.createElement('tr');
                        tr.className = 'clickable-row';
                        tr.setAttribute('data-order-id', order.orderId);

                        // 주문 상태를 처리
                        var statusClass = '';
                        var statusText = '알 수 없는 상태';
                        if (order.orderStatusEnum && order.orderStatusEnum.description) {
                            statusClass = 'order-status-' + order.orderStatusEnum.name().toLowerCase();
                            statusText = order.orderStatusEnum.description;
                        } else if (order.orderStatus) {
                            try {
                                var status = OrderStatus.fromCode(order.orderStatus);
                                statusClass = 'order-status-' + status.name().toLowerCase();
                                statusText = status.description;
                            } catch (e) {
                                console.error('잘못된 주문 상태:', order.orderStatus, e);
                                statusClass = 'order-status-unknown';
                                statusText = '알 수 없는 상태';
                            }
                        }

                        // 행에 데이터 추가
                        tr.innerHTML = `
                <td><a href="/admin/orders/${order.orderId}">${order.orderId}</a></td>
                <td>${new Date(order.createdAt).toLocaleString('ko-KR', {
                            year: 'numeric', month: '2-digit', day: '2-digit',
                            hour: '2-digit', minute: '2-digit'
                        })}</td>
                <td class="${statusClass}">${statusText}</td>
              `;
                        tbody.appendChild(tr);
                    }
                }
                table.style.display = 'table'; // 테이블 보이게
                addRowClickHandlers(); // 행 클릭 이벤트 add
            } else {
                // 요청 실패 시 오류 메시지
                console.error('서버 오류:', request.status);
                tbody.innerHTML = '<tr><td colspan="3">주문 내역을 불러오는데 실패했습니다.</td></tr>';
                table.style.display = 'table';
            }
        };

        request.onerror = function () {
            // 네트워크 오류 처리
            console.error('네트워크 오류 발생');
            tbody.innerHTML = '<tr><td colspan="3">주문 내역을 불러오는데 실패했습니다.</td></tr>';
            table.style.display = 'table';
        };

        request.send(); // 요청 보내기
    }
}

// 테이블 행 클릭 이벤트를
function addRowClickHandlers() {
    // 모든 클릭 가능한 행 가져옴
    var rows = document.getElementsByClassName('clickable-row');
    for (var i = 0; i < rows.length; i++) {
        rows[i].onclick = function (e) {
            // 링크 클릭이 아니면
            if (e.target.tagName !== 'A') {
                // 주문 ID 가져오고
                var orderId = this.getAttribute('data-order-id');
                // 주문 상세 페이지로 이동
                window.location.href = '/admin/orders/' + orderId;
            }
        };
    }
}

// OrderStatus enum JavaScript에서 사용
const OrderStatus = {
    fromCode: function (code) {
        switch (code) {
            case 1:
                return {
                    name: function () {
                        return 'PLACED';
                    }, description: '주문 완료'
                };
            case 2:
                return {
                    name: function () {
                        return 'SHIPPED';
                    }, description: '배송 중'
                };
            case 3:
                return {
                    name: function () {
                        return 'DELIVERED';
                    }, description: '배송 완료'
                };
            case 4:
                return {
                    name: function () {
                        return 'CANCELLED';
                    }, description: '주문 취소'
                };
            default:
                throw new Error('잘못된 주문 상태 코드: ' + code);
        }
    }
};
