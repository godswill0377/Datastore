/**
 * Created by lang on 4/9/2018.
 */
var captcha_handle;
var onloadCallback = function() {
     captcha_handle =  grecaptcha.render('captcha', {
        'sitekey' : $('#sitekey').val()
    });
};




$("#loginForm").submit(function(e)
{

    var username = $("#username").val();
    var password = $("#password").val();


    var validation = true;
    if(username == ""){
        validation  = false;
    }


    if(password == ""){
        validation  = false;
    }
    $("#text-logined").html("");
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

                    if(data.errorInfo == "noactivate"){
                        $("#text-logined").html("Not activated");
                        window.location.href = "/vendor/reg-auth-result";
                    }
                    else{
                        $("#text-logined").html("Success");
                        window.location.href = "/vendor/dashboard";
                    }

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
