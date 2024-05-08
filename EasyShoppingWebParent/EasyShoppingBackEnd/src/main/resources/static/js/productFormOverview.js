dropdownBrands = $("#brand");
dropDownCategories = $("#category");

$(document).ready(function () {
    dropdownBrands.change(function () {
        dropDownCategories.empty();
        getCategories();
    });

    getCategoriesForNewForm();

});

function  getCategoriesForNewForm() {
    categorytIdFieldInProduct = $("#categoryIdInProduct");
    editMode = false;
    if(categorytIdFieldInProduct.length){
        editMode = true;
    }
    if(!editMode){
        getCategories();
    }
}

function getCategories() {
    brandId = dropdownBrands.val();
    url = brandModuleURL + "/" + brandId + "/categories";
    $.get(url, function (responseJson) {
        $.each(responseJson, function (index, category) {
            $("<option>").val(category.id).text(category.name).appendTo(dropDownCategories);
        });
    });
}

function checkUnique(form) {
    var url = checkUniqueURL;
    var productId = $("#id").val();
    var productName = $("#name").val();
    var csrfValue = $("input[name='_csrf']").val();

    params = {id: productId, name: productName, _csrf: csrfValue}

    $.get(url, params, function (response) {
        if (response == "OK") {
            form.submit();
        } else if (response == "DuplicatedProduct") {
            showWarningModal("This product name: " + productName + " already in use");
        } else {
            showErrorModal("Unknown response from server");
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");
    });
    return false;
}