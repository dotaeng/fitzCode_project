$(document).ready(function() {
    let deleteImageIds = [];

    updateStatusButtons();

    $('.status-active-btn, .status-soldout-btn, .status-disabled-btn').click(function() {
        const productId = $('#productId').val();
        const newStatus = $(this).data('status');
        $.ajax({
            url: '/admin/products/' + productId + '/update-status',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ status: newStatus }),
            success: function(response) {
                if (response === "success") {
                    alert('상태가 성공적으로 변경되었습니다.');
                    $('#currentStatus').val(newStatus);
                    updateStatusButtons();
                    updateStatusText(newStatus);
                    window.location.reload();
                } else {
                    alert('상태 변경 실패: ' + response);
                }
            },
            error: function(xhr, status, error) {
                alert('상태 변경에 실패했습니다: ' + error);
            }
        });
    });

    $('#editImageBtn').click(function() {
        $('#imageEditModal').show();
        deleteImageIds = [];
        updateDeleteImageIds();
    });

    $('#imageEditModal .close').click(function() {
        $('#imageEditModal').hide();
        deleteImageIds = [];
        updateDeleteImageIds();
    });

    $('#confirmDeleteModal .close').click(function() {
        $('#confirmDeleteModal').hide();
    });

    $(document).on('click', '#imagePopupModal', function(e) {
        if (e.target === this) {
            $('#imagePopupModal').hide();
        }
    });

    $(document).on('click', '.image-item img', function() {
        const src = $(this).attr('src');
        $('#popupImage').attr('src', src);
        $('#imagePopupModal').show();
    });

    $(document).on('click', '.image-delete-btn', function() {
        const rawImageId = $(this).attr('data-image-id');
        console.log('클릭한 이미지 ID (raw):', rawImageId);
        if (rawImageId && rawImageId.trim() !== '') {
            const parsedId = parseInt(rawImageId);
            console.log('클릭한 이미지 ID (parsed):', parsedId);
            if (!isNaN(parsedId)) {
                $('#confirmImageId').val(parsedId);
                $('#confirmDeleteModal').show();
            } else {
                console.error('파싱 실패한 이미지 ID:', rawImageId);
            }
        } else {
            console.error('데이터 속성에서 ID를 가져올 수 없음:', $(this).attr('data-image-id'));
        }
    });

    $('#confirmDeleteBtn').click(function() {
        const imageId = $('#confirmImageId').val();
        console.log('삭제 확인된 이미지 ID (raw):', imageId);
        if (imageId && !isNaN(parseInt(imageId))) {
            const idNum = parseInt(imageId);
            if (!deleteImageIds.includes(idNum)) {
                deleteImageIds.push(idNum);
            }
            const imageItem = $('#deleteImage-' + idNum);
            imageItem.replaceWith('<div class="deleted-item" id="deleteImage-' + idNum + '"><span>삭제됨</span></div>');
            $('#confirmDeleteModal').hide();
            updateDeleteImageIds();
            console.log('삭제할 이미지 ID:', deleteImageIds);
        } else {
            console.error('유효하지 않은 이미지 ID:', imageId);
        }
    });

    $('#cancelDeleteBtn').click(function() {
        $('#confirmDeleteModal').hide();
    });

    $('#addImageBtn').click(function() {
        const newInput = `
                    <div class="additional-image-input">
                        <input type="file" name="additionalImages" accept="image/*"/>
                        <button type="button" class="remove-image-btn">제거</button>
                        <div class="preview-container"></div>
                    </div>`;
        $('#additionalImagesContainer').append(newInput);
    });

    $(document).on('click', '.remove-image-btn', function() {
        $(this).parent().remove();
    });

    $(document).on('change', 'input[name="mainImage"], input[name="additionalImages"]', function() {
        const file = this.files[0];
        const previewContainer = $(this).siblings('.preview-container');
        previewContainer.empty();

        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const previewHtml = `
                            <div class="preview-item">
                                <img src="${e.target.result}" alt="미리보기"/>
                                <span>${file.name}</span>
                            </div>`;
                previewContainer.append(previewHtml);
            };
            reader.readAsDataURL(file);
        }
    });

    $('#uploadBtn').click(function() {
        const formData = new FormData($('#imageUploadForm')[0]);
        deleteImageIds.forEach(id => {
            formData.append('deleteImageIds', id);
        });
        console.log('전송할 삭제 이미지 ID:', deleteImageIds);

        const fileInputs = $('#imageUploadForm input[name="additionalImages"]');
        fileInputs.each(function() {
            const files = this.files;
            if (files.length === 0 || files[0].size === 0) {
                $(this).remove();
            }
        });

        $.ajax({
            url: '/admin/products/' + $('#productId').val() + '/update-images',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                alert('이미지가 성공적으로 수정되었습니다.');
                $('#imageEditModal').hide();
                window.location.reload();
            },
            error: function(xhr, status, error) {
                console.error('이미지 수정 실패:', error, xhr.responseText);
                alert('이미지 수정에 실패했습니다: ' + (xhr.responseText || error));
            }
        });
    });

    function updateDeleteImageIds() {
        $('#deleteImagesContainer').empty();
        deleteImageIds.forEach(id => {
            $('#deleteImagesContainer').append(
                `<input type="hidden" name="deleteImageIds" value="${id}"/>`
            );
        });
    }
});

function updateStatusButtons() {
    const currentStatus = $('#currentStatus').val();
    $('.status-active-btn, .status-soldout-btn, .status-disabled-btn').removeClass('active sold-out disabled');
    switch (currentStatus) {
        case '1': $('#statusActive').addClass('active'); break;
        case '2': $('#statusSoldOut').addClass('sold-out'); break;
        case '3': $('#statusDisabled').addClass('disabled'); break;
    }
}

function updateStatusText(status) {
    let statusText = '';
    switch (status) {
        case '1': statusText = '판매 중'; break;
        case '2': statusText = '품절'; break;
        case '3': statusText = '비활성화'; break;
    }
    $('#statusText').text(statusText);
}