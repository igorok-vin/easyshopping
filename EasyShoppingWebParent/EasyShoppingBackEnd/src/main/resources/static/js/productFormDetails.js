$(document).ready(function () {
    $("a[name='linkRemoveDetail']").each(function (index){
        $(this).click(function (){
            removeDetailSectionById(index);
        });
    });
});
function addNextDetailSection() {
    allDivDetails = $("[id^='divDetail']");
    divDetailsCount = allDivDetails.length;
/*********************************/
    htmlDetailsSection = ` 
     <div class="form-inline" id = "divDetail${divDetailsCount}">
     <!--Edit mode: The value of the ID is always zero, meaning that it is a new detail section.-->
     <input type="hidden" name="detailIDs" value="0">
          <label class="m-3">Name:</label>
          <input type="text" class="form-control w-25" name="detailNames" maxlength="255"/>
          <label class="m-3">Value:</label>
          <input type="text" class="form-control w-25" name="detailValues" maxlength="255"/>
       </div> `;

    allDetailNames = $("[name^='detailNames']");
    allDetailValues = $("[name^='detailValues']");
    previousDetailName = allDetailNames.last();
    previousDetailValue = allDetailValues.last();
    if((previousDetailName.val() == null || previousDetailName.val() == "") && (previousDetailValue.val() == null || previousDetailValue.val() == "")){
        return false;
    } else{
        $("#divProductDetails").append(htmlDetailsSection);
    }

/********************************/
    previousDivDetailSection = allDivDetails.last();
    previousDivDetailID = previousDivDetailSection.attr("id");

    htmlLinkRemove = `
     <a class="btn fas fa-times fa-2x icon-dark" 
         title="Remove this detail" 
         href="javascript:removeDetailSectionById('${previousDivDetailID}')">
     </a> `;

    previousDivDetailSection.append(htmlLinkRemove);

    $("input[name='detailNames']").last().focus();
}

function removeDetailSectionById(divDetail){
    $("#" + divDetail).remove();
}