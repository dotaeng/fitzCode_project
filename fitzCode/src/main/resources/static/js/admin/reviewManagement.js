$(document).ready(function() {
    // 삭제 버튼 이벤트
    $('.delete-review-btn').click(function() {
        const reviewId = $(this).data('review-id');
        if (confirm('정말 이 리뷰를 삭제하시겠습니까?')) {
            $.ajax({
                url: '/admin/products/review/delete/' + reviewId,
                type: 'POST',
                success: function(response) {
                    if (response === "success") {
                        alert('리뷰가 삭제되었습니다.');
                        location.reload();
                    } else {
                        alert(response);
                    }
                },
                error: function(xhr, status, error) {
                    alert('리뷰 삭제 실패: ' + error);
                }
            });
        }
    });

    // 이미지 클릭 시 팝업 표시
    $('.review-img').click(function() {
        const src = $(this).attr('src');
        $('#popupImage').attr('src', src);
        $('.image-popup').addClass('active');
    });

    // 팝업 닫기 (배경 클릭 시)
    $('.image-popup').click(function(e) {
        if (e.target === this) {
            $(this).removeClass('active');
        }
    });
});