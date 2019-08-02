$( document ).ready(function() {
    var navListItems = $('div.setup-panel div a'),
        allWells = $('.setup-content'),
        allNextBtn = $('.nextBtn');

    allWells.hide();

    navListItems.click(function (e) {
        e.preventDefault();
        var $target = $($(this).attr('href')),
            $item = $(this);

        if (!$item.hasClass('disabled')) {
            navListItems.removeClass('btn-primary').addClass('btn-default');
            $item.addClass('btn-primary');
            allWells.hide();
            $target.show();
            $target.find('input:eq(0)').focus();
        }

    });

    allNextBtn.click(function(){
        var curStep = $(this).closest(".setup-content"),
            curStepBtn = curStep.attr("id"),
            nextStepWizard = $('div.setup-panel div a[href="#' + curStepBtn + '"]').parent().next().children("a"),
            curInputs = curStep.find("input[type='text'],input[type='url']"),
            isValid = true;


        $(".form-group").removeClass("has-error");
        for(var i=0; i<curInputs.length; i++){
            if (!curInputs[i].validity.valid){
                isValid = false;
                $(curInputs[i]).closest(".form-group").addClass("has-error");
            }
        }
        if(curStepBtn == "step-1"){
            var check=document.getElementById('chb_agree');
            if (!check.checked) {
                isValid = false;
                $(curInputs[i]).closest(".form-group").addClass("has-error");
            }
        }
        if (isValid)
            nextStepWizard.removeAttr('disabled').trigger('click');
    });

    $('div.setup-panel div a.btn-primary').trigger('click');
});

/**
 * submit sign up form event
 */
$("#registerForm").submit(function(e)
{

    var legalname = $("#legal_name").val();
    var address = $("#address").val();
    var city = $("#city").val();
    var state_province = $("#state_province").val();
    var country = $("#country").val();
    var zip_postal = $("#zip_postal").val();
    var business_name = $("#business_name").val();
    var website_url = $("#website_url").val();
    var mobile_num = $("#mobile_num").val();

    var validation = true;
    if(legalname == ""){
        validation  = false;
    }

    if(address == ""){
        validation  = false;
    }

    if(city == ""){
        validation  = false;
    }

    if(state_province == ""){
        validation  = false;
    }

    if(country == "") {
        validation = false;
    }

    if(city == "") {
    validation = false;
}

    if(zip_postal == "") {
        validation = false;
    }

    if(business_name == "") {
        validation = false;
    }
    if(mobile_num == "") {
        validation = false;
    }

    if(validation){
        $("#rg_btWaiting").css('display','');

        var postData = $(this).serializeArray();
        var formURL = $(this).attr("action");

        $.ajax({
            url : formURL,
            type: "POST",
            data : postData,
            success:function(data, textStatus, jqXHR)
            {
                if (data.resultCode == "success") {
                    setTimeout(function () {
                        window.location.href = "/vendor/reg-auth-result"
                    }, 1500);
                }
                else{
                    swal(data.errorInfo);
                }

                $("#rg_btWaiting").css('display','none');
            }
        });
    }

    e.preventDefault();
});
