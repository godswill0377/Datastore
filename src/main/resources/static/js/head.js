var userid="",token="" ;
var reset_flag = 0; // Is current url is for reset password or not?

$(function(){
    var resetPassword = $("#resetPassword").val();
    if(resetPassword == 1){
        $("#resetFormSection").css('display','block');
        userid = $("#resetUserid").val();
        token = $("#resetToken").val();
    }

});


$("#bt-modal-close").on('click',function(){
    window.location.href="/";
});


$("#rs_btnReset").on('click',function(){
    var rs_password = $("#rs_password").val();
    var rs_confirmpassword = $("#rs_confirmpassword").val();

    var user_email = $('#user_email').val();
    if (rs_password.length > 0 && rs_confirmpassword.length > 0) {
        if (rs_password != rs_confirmpassword) {
            $("#valid_rs_password").html("PASSWORD DOESN'T MATCH");
            $("#valid_rs_password").css('visibility', 'visible');
            $("#valid_rs_confirmpassword").css('visibility','hidden');
            return;
        }
        else{
            if($("#password-result").length){
                var password_result = $("#password-result").text();
                if(password_result.indexOf("least 6 characters") >= 0){
                   return;
                }
            }
            $("#valid_rs_password").css('visibility','hidden');
            $("#valid_rs_confirmpassword").css('visibility','hidden');
        }

        $("#rs_btRsWaiting").css('display','');
        $.ajax({
            type: "POST",
            url: '/user/resetPassword/api',
            data: {email: user_email, password: rs_password , token:token},
            success: function (data) {

                if (data.resultCode == "success") {
                    $("#text_reset").css('background-color','#01adef');
                    $("#text_reset").html(data.errorInfo);
                    setTimeout(function(){

                        var forgetSSOPermit = $("#forgetSSOPermit").val();
                        window.location.href = forgetSSOPermit;

                        var login_time = 300 , signup_time = 300;
                        $("#resetFormSection").animate({
                            opacity:0
                        },login_time);

                        setTimeout(function(){
                            reset_flag = 1;
                            $("#resetFormSection").css('display','none');

                            $("#modal-login").css('opacity','0');
                            $("#modal-login").css('display','block');

                            $("#modal-login").animate({
                                opacity:1
                            },signup_time);
                        },login_time);

                    },2500);
                }
                else{
                    $("#text_reset").css('background-color','#e02f2f');
                    $("#text_reset").html(data.errorInfo);
                }
                $("#text_reset").css('display','block');

                setTimeout(function(){
                    $("#rs_btRsWaiting").css('display','none');
                },1000);

                setTimeout(function(){
                    $("#text_reset").css('display','none');

                    window.location.href = "/";
                },3500);

            }
        });
    } else {

        if(rs_password == ''){
            $("#valid_rs_password").css('visibility','visible');
        }
        else{
            $("#valid_rs_password").css('visibility','hidden');
        }
        if(rs_confirmpassword == ''){
            $("#valid_rs_confirmpassword").css('visibility','visible');
        }

    }
});

$('#rs_email').keyup(function(event){

    var keycode = (event.keyCode ? event.keyCode : event.which);
    if(keycode == '13'){
        $('#rs_password').focus();
    }
});


$('#rs_password').keyup(function(event){

    var keycode = (event.keyCode ? event.keyCode : event.which);
    if(keycode == '13'){
        $('#rs_confirmpassword').focus();
    }
});


$('#rs_confirmpassword').keyup(function(event){

    var keycode = (event.keyCode ? event.keyCode : event.which);
    if(keycode == '13'){
        $('#rs_btnReset').click();
    }
});

