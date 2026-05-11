$(document).ready(function() {
    // 페이지 로드 시 초기 상태 설정
    if (!$('#status').val()) {
        $('.status-btn[data-value="ACTIVE"]').addClass('active');
        $('#status').val('ACTIVE'); // 기본값: ACTIVE (판매중)
    }

    // 상위 카테고리 변경 시 하위 카테고리 및 사이즈 테이블 업데이트
    $('#parentCategoryId').change(function() {
        let parentId = $(this).val();
        let $sizeTableBody = $('#sizeTable tbody');
        let $childCategory = $('#categoryId');
        $sizeTableBody.empty();
        $childCategory.empty().append('<option value="">하위 카테고리 선택</option>').prop('disabled', true);

        if (parentId) {
            // 하위 카테고리 조회
            $.get('/admin/products/categories/child', { parentId: parentId }, function(data) {
                $.each(data, function(index, category) {
                    $childCategory.append('<option value="' + category.categoryId + '">' + category.name + '</option>');
                });
                $childCategory.prop('disabled', false);
                // 첫 번째 하위 카테고리 자동 선택 (필수 필드 보장)
                if (data.length > 0) {
                    $childCategory.val(data[0].categoryId);
                }
            }).fail(function() {
                console.error('하위 카테고리 조회 실패');
            });

            // 사이즈 조회
            $.get('/admin/products/sizes', { parentId: parentId }, function(sizes) {
                $.each(sizes, function(index, size) {
                    $sizeTableBody.append(
                        '<tr>' +
                        '<td>' + size.description + '</td>' +
                        '<td>' +
                        '<input type="hidden" name="productSizes[' + index + '].sizeCode" value="' + size.code + '" />' +
                        '<input type="number" name="productSizes[' + index + '].stock" min="0" value="0" />' +
                        '</td>' +
                        '</tr>'
                    );
                });
            }).fail(function() {
                console.error('사이즈 조회 실패');
            });
        }
    });

    // 메인 이미지 미리보기
    $('#mainImageFile').change(function() {
        let file = this.files[0];
        if (file) {
            let reader = new FileReader();
            reader.onload = function(e) {
                $('#mainImagePreview').html('<img src="' + e.target.result + '" alt="Main Image Preview" />');
            };
            reader.readAsDataURL(file);
        } else {
            $('#mainImagePreview').html(''); // 파일 없으면 미리보기 제거
        }
    });

    // 추가 이미지 추가 및 미리보기
    let imageIndex = 1;
    $('#addImage').click(function() {
        let newInput = $(
            '<div class="additional-image">' +
            '<input type="file" name="additionalImageFiles" accept="image/*" />' +
            '<button type="button" class="remove-btn">삭제</button>' +
            '</div>'
        );
        $('#additionalImages').append(newInput);
        newInput.find('input').change(function() {
            let file = this.files[0];
            if (file) {
                let reader = new FileReader();
                reader.onload = function(e) {
                    $('#additionalImagePreview').append('<img src="' + e.target.result + '" alt="Additional Image Preview" />');
                };
                reader.readAsDataURL(file);
            }
        });
        imageIndex++;
    });

    // 추가 이미지 삭제
    $(document).on('click', '.remove-btn', function() {
        let $parent = $(this).parent();
        let $img = $('#additionalImagePreview img').eq($parent.index());
        $img.remove();
        $parent.remove();
    });

    // 상태 버튼 선택
    $('.status-btn').click(function() {
        $('.status-btn').removeClass('active');
        $(this).addClass('active');
        $('#status').val($(this).data('value'));
    });

    // 모달 창 열기
    $('#excelUploadBtn').click(function() {
        $('#excelModal').css('display', 'flex');
    });

    // 모달 창 닫기
    $('.close-btn').click(function() {
        $('#excelModal').hide();
    });

    // 모달 외부 클릭 시 닫기
    $(window).click(function(event) {
        if (event.target == $('#excelModal')[0]) {
            $('#excelModal').hide();
        }
    });
});