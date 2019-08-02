

$( document ).ready(function() {

});

/**
 * submit sign up form event
 */

$("#general_profileForm").submit(function(e)
{

    var username = $("#username").val();
    var email = $("#email").val();
    var cur_password = $("#cur_password").val();
    var new_password = $("#new_password").val();
    var new_confirm_password = $("#new_confirm_password").val();

    var validation = true;
    var error_message = "";
    if(username == ""){
        validation  = false;
        error_message = "Please input Username!";

    }

    if(email == ""){
        validation  = false;
        error_message = "Please input Email!";
    }


    if(new_password != "" && new_password != new_confirm_password){
        validation  = false;
        error_message = "Password doesn't match!";
    }

    if(!validation){
        $("#message_content").text(error_message);
        $("#message").css('display','block');

        window.location.href= "#";
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
                    $("#message_content").text("Successfully Changed!");

                }
                else{
                    $("#message_content").text(data.errorInfo);
                }

                $("#message").css('display','block');

                $("#rg_btWaiting").css('display','none');

                window.location.href= "#";

            }
        });
    }

    e.preventDefault();
});
