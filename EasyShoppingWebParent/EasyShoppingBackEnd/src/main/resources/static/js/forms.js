$(document).ready(function () {
    $("#buttonCancel").on("click", function () {
        window.location = moduleURL;
    });

    $("#fileImage").each(function() {
        showMainImageThumbnail(this);
    });
});

function showMainImageThumbnail(fileInput) {
    const def = document.getElementById("thumbnail");
    $(fileInput).change(function() {
        var file = fileInput.files[0];
        var reader = new FileReader();
        reader.onload = function (e) {
            var image = new Image();
            image.src = e.target.result;
            image.onload = function () {
                height = this.height;
                width = this.width;
                if (height > IMAGE_HIGHT || width > IMAGE_WIDTH) {
                    fileInput.setCustomValidity("You must choose an image " + IMAGE_HIGHT + "x" + IMAGE_WIDTH + "(px) max");
                    fileInput.reportValidity();
                    $(fileInput).val(null);
                    $(def).attr("src", defaultExtraImageThumbnailSrc);
                    return false;
                } else if (fileInput.files[0].size>MAX_FILE_SIZE) {
                    $(fileInput).val(null);
                    fileInput.setCustomValidity("You must choose an image less than "+(MAX_FILE_SIZE/1000)+"KB!");
                    fileInput.reportValidity();
                    $(def).attr("src", defaultExtraImageThumbnailSrc);
                    return false;
                } else {
                    $("#thumbnail").attr("src", e.target.result);
                }
            }
        };
        reader.readAsDataURL(file);
    });
}

function checkPasswordMatch(confirmPassword) {
    if(confirmPassword.value != $("#password").val()){
        confirmPassword.setCustomValidity("Passwords do not match");
    } else {
        confirmPassword.setCustomValidity("");
    }
}

function checkEmailUnique(form) {
    var url = checkEmail;
    var userEmail = $("#email").val();
    var userId = $("#id").val();
    var csrfValue = $("input[name='_csrf']").val();

    params = {email: userEmail, id: userId, _csrf: csrfValue}

    $.get(url, params, function(response) {
        if (response == "OK") {
            form.submit();
        } else if (response == "Duplicated") {
            showWarningModal("There is another user having the email " + userEmail);
        } else {
            showErrorModal("Unknown response from server");
        }
    }).fail(function() {
        showErrorModal("Could not connect to the server");
    });

    return false;
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();
}

function showErrorModal(message) {
    showModalDialog("Error", message)
}

function showWarningModal(message) {
    showModalDialog("Warning", message)
}

