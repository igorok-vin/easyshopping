
$(window).on("load", function () {
    var menuItems = $(".nav-item").length;
    if (menuItems >= 9 & menuItems <= 11) {
        $("#mediaChanger").removeClass('navbar-expand-xl').addClass('navbar-expand-lg')
    } else if (menuItems >= 7 & menuItems <= 8){
        $("#mediaChanger").removeClass('navbar-expand-xl').addClass('navbar-expand-md')
    }else if (menuItems >= 4 & menuItems <= 6){
        $("#mediaChanger").removeClass('navbar-expand-xl').addClass('navbar-expand-sm')
    }
});
