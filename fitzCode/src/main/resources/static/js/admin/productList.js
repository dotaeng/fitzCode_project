$(document).ready(function() {
    // 정렬 선택 시 페이지 새로고침
    $("#sortOrder").change(function() {
        let sort = $(this).val();
        let parentCategoryId = $("#parentCategory").val();
        let childCategoryId = $("#childCategory").val();
        let keyword = $("#searchKeyword").val();

        let url = "/admin/products";
        let params = [];

        if (sort) {
            params.push("sort=" + sort);
        }
        if (childCategoryId && childCategoryId !== "") {
            params.push("childCategoryId=" + childCategoryId);
        } else if (parentCategoryId && parentCategoryId !== "") {
            params.push("parentCategoryId=" + parentCategoryId);
        }
        if (keyword && keyword.trim() !== "") {
            params.push("keyword=" + encodeURIComponent(keyword));
        }

        if (params.length > 0) {
            url += "?" + params.join("&");
        }

        window.location.href = url;
    });

    // 상위 카테고리 변경 시 하위 카테고리 로딩
    $("#parentCategory").change(function() {
        let parentId = $(this).val();
        $("#childCategory").empty().append(`<option value="">전체</option>`).prop("disabled", true);

        if (parentId) {
            $.ajax({
                url: `/admin/products/categories/child?parentId=${parentId}`,
                method: "GET",
                success: function(categories) {
                    if (categories.length > 0) {
                        $("#childCategory").prop("disabled", false);
                        categories.forEach(function(category) {
                            $("#childCategory").append(
                                `<option value="${category.categoryId}">${category.name}</option>`
                            );
                        });
                    }
                },
                error: function() {
                    alert("하위 카테고리를 불러오는 데 실패했습니다.");
                }
            });
        }
    });

    // 검색 버튼 클릭 페이지 새로고침
    $("#searchBtn").click(function() {
        let sort = $("#sortOrder").val();
        let parentCategoryId = $("#parentCategory").val();
        let childCategoryId = $("#childCategory").val();
        let keyword = $("#searchKeyword").val();

        let url = "/admin/products";
        let params = [];

        if (sort) {
            params.push("sort=" + sort);
        }
        if (childCategoryId && childCategoryId !== "") {
            params.push("childCategoryId=" + childCategoryId);
        } else if (parentCategoryId && parentCategoryId !== "") {
            params.push("parentCategoryId=" + parentCategoryId);
        }
        if (keyword && keyword.trim() !== "") {
            params.push("keyword=" + encodeURIComponent(keyword));
        }

        if (params.length > 0) {
            url += "?" + params.join("&");
        }

        window.location.href = url;
    });
});