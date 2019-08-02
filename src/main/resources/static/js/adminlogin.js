
///忘记密码
$("#iforget").click(function () {
    $("#login_model").hide();
    $("#forget_model").show();

});


//返回
$("#denglou").click(function () {
    $("#usrmail").val("");
    $("#username").val("");
    $("#userpwd").val("");
    $("#login_model").show();
    $("#forget_model").hide();

});

$('#open-button').click(function(){

    $.ajax({
        url:'/admin/check_login/auth',
        type:'POST',
        data:'password='+$('#orginalpassword').val(),
        success:function(response){
            if(response.resultCode == "success"){
                $('#password').attr('disabled',false);
                $('#confirmpassword').attr('disabled',false);
            }
        }
    });
});

$('#reset-button').click(function(){

    if($('#password').val() != $('#confirmpassword').val()){
        alert("The password doesn't match!");
        return;
    }
    $.ajax({
        url:'/admin/reset/auth',
        type:'POST',
        data:'password='+$('#password').val(),
        success:function(response){
            if(response.resultCode == "success"){
                window.location.href = "/login";
            }
        }
    });
});

$('#username').keyup(function(event){

    var keycode = (event.keyCode ? event.keyCode : event.which);
    if(keycode == '13'){
        $('#password').focus();
    }
});
/*
$('#password').keyup(function(event){


});*/

function detectEnter(event){
    var keycode = (event.keyCode ? event.keyCode : event.which);
    if(keycode == '13'){
        $('#login-button').click();
    }
}
