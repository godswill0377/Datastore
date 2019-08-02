/**
 * Created by lang on 4/9/2018.
 */
var captcha_handle;
var onloadCallback = function() {
     captcha_handle =  grecaptcha.render('captcha', {
        'sitekey' : $('#sitekey').val()
    });


};


$( document ).ready(function() {

    //start application
    passwordChecker.start({
        container: '#register_password'
    });

});

/**
 * submit sign up form event
 */

$("#signupForm").submit(function(e)
{

    var username = $("#register_username").val();
    var email = $("#register_email").val();
    var password = $("#register_password").val();
    var confirmpassword = $("#register_confirmpassword").val();



    var validation = true;
    if(username == ""){
        validation  = false;
    }

    if(email == ""){
        validation  = false;
    }

    if(password == ""){
        validation  = false;
    }

    if(confirmpassword == "") {
        validation = false;
    }
    else {
        if (password != confirmpassword) {

            validation = false;
        }
        else{
            if($("#password-result").length){
                var password_result = $("#password-result").text();
                if(password_result.indexOf("least 6 characters") >= 0){
                    validation = false;
                }
            }
        }
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
                    $("#text-logined").css('background-color','#01adef');
                    $("#text-logined").html("You have to setup your profile. ");

                    setTimeout(function(){
                       window.location.href = "/vendor/set-info";
                    },2500);

                }
                else{

                    grecaptcha.reset(captcha_handle);
                    $("#text-logined").css('background-color','#e02f2f');
                    $("#text-logined").html(data.errorInfo);

                }
                $("#text-logined").css('display','block');

                setTimeout(function(){
                    $("#rg_btWaiting").css('display','none');
                },1000);

                setTimeout(function(){
                    $("#text-logined").css('display','none');
                },3500);
            }
        });
    }

    e.preventDefault();
});
