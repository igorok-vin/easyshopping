var extraImagesCount = 0;
$(document).ready(function () {
   $("input[name='nameExtraImage']").each(function(index) {
       extraImagesCount++;
      // checkImageSize(this,index);
       showExtraImageThumbnail(this, index);
    });

   $("a[name='linkRemoveExtraImage']").each(function (index){
       $(this).click(function (){
           removeExtraImage(index)
       });
   });
});

function showExtraImageThumbnail(fileInput, index) {
    const def = document.getElementById("extraThumbnail" + index);
    $(fileInput).change(function() {
        var file = fileInput.files[0];
        fileName = file.name;
        imageNameHiddenField = $("#imageName" + index)
        if(imageNameHiddenField.length){
            imageNameHiddenField.val(fileName);
        }
 /*****************************************/
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
                    $("#extraThumbnail" + index).attr("src", e.target.result);
                    if (index >= (extraImagesCount - 1)) {
                        addExtraImage(index + 1);
                    }
                }
            }
        };
        reader.readAsDataURL(file);
    });
}

function addExtraImage(index) {
    htmlExtraImage = ` 
              <div class="col border m-3 p-2" id="divExtraImage${index}">
               <div id="extraImageHeader${index}"><label>Extra Image #${index + 1}:</label></div>
               <div>
                   <img id="extraThumbnail${index}" alt="Extra image #${index + 1} preview" class="img-fluid" src="${defaultExtraImageThumbnailSrc}"/>
               </div>
               <div>           
                   <input type="file" id="nameExtraImage${index}" name="nameExtraImage" accept="image/png, 
                   image/jpeg" onclick = "showExtraImageThumbnail(this, ${index} )" />

               </div>
           </div>
        `;

    htmlLinkRemove = ` <a class="btn fas fa-times fa-2x icon-dark float-right" title="Remove this image" href="javascript:removeExtraImage(${index - 1})"></a> `;

    $("#divProductImages").append(htmlExtraImage);

    $("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);

    extraImagesCount++;
}

function removeExtraImage(index) {
    $("#divExtraImage" + index).remove();
}
