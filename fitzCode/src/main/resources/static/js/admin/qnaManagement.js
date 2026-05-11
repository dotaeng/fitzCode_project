$(document).ready(function () {
    $('.answer-qna-btn').click(function () {
        const qnaId = $(this).data('qna-id');
        const existingAnswer = $(this).data('answer') || '';
        $('#popupQnaId').val(qnaId);
        $('#popupAnswer').val(existingAnswer);
        $('.qna-answer-popup').addClass('active');
    });

    $('#qnaAnswerForm').submit(function (e) {
        e.preventDefault();
        const formData = {
            qnaId: $('#popupQnaId').val(),
            answer: $('#popupAnswer').val()
        };
        const actionUrl = formData.answer === '' ?
            '/admin/products/qna/' + $('#productId').val() + '/answer' :
            '/admin/products/qna/' + $('#productId').val() + '/update-answer';
        $.ajax({
            url: actionUrl,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                if (response === "success") {
                    alert('답변이 저장되었습니다.');
                    location.reload();
                } else {
                    alert(response);
                }
            },
            error: function (xhr, status, error) {
                alert('답변 저장 실패: ' + error);
            }
        });
    });

    $('.qna-answer-popup').click(function (e) {
        if (e.target === this) {
            $(this).removeClass('active');
        }
    });

    $('.delete-qna-btn').click(function () {
        const qnaId = $(this).data('qna-id');
        if (confirm('정말 이 Q&A를 삭제하시겠습니까?')) {
            $.ajax({
                url: '/admin/products/qna/delete/' + qnaId,
                type: 'POST',
                success: function (response) {
                    if (response === "success") {
                        alert('Q&A가 삭제되었습니다.');
                        location.reload();
                    } else {
                        alert(response);
                    }
                },
                error: function (xhr, status, error) {
                    alert('Q&A 삭제 실패: ' + error);
                }
            });
        }
    });
});