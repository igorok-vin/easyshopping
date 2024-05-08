
$(window).on("load", function () {
    var menuItems = $(".nav-item-number").length;
      if (menuItems >= 6 & menuItems <= 8){
        $("#mediaChanger").removeClass('navbar-expand-xl').addClass('navbar-expand-md')
    }

});
