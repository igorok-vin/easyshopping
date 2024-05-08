$(document).ready(function () {
    customizeDropDownMenu();
});

function customizeDropDownMenu() {
    $(".navbar .dropdown").hover(
        function () {
            $(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
        },
        function () {
            $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
    });

    $(".dropdown > a").click(function () {
        location.href = this.href;
    });
}

function showDeleteConfirmModal(link, entityName) {
    entityId = link.attr("entityDeleteId");
    $("#yesButton").attr("href", link.attr("href"));
    $("#confirmText").text("Are you sure you want to delete this "
        + entityName + " ID " + entityId + "?");
    $("#confirmModal").modal();
}


