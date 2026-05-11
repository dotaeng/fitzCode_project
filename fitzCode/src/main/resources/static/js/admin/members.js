function filterMembers() {
    var sortOrder = document.getElementById("sortFilter").value;
    var role = document.getElementById("roleFilter").value;
    var keyword = document.getElementById("searchKeyword").value;
    var url = '/admin/members?page=1&';

    if (sortOrder) {
        url += 'sortOrder=' + sortOrder + '&';
    }
    if (role) {
        url += 'role=' + role + '&';
    }
    if (keyword) {
        url += 'keyword=' + encodeURIComponent(keyword);
    }
    window.location.href = url;
}

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById("searchKeyword").addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
            filterMembers();
        }
    });

    document.querySelectorAll('.member-row').forEach(row => {
        row.addEventListener('click', function (e) {
            if (!e.target.closest('.actions') && e.target.tagName !== 'BUTTON' && e.target.tagName !== 'FORM') {
                const userId = this.getAttribute('data-id');
                window.location.href = '/admin/members/' + userId;
            }
        });
    });
});