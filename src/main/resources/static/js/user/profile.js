$(function () {
    if($("#reset_password").length != 0) {
        var reset_password = $('#reset_password').val();
        if(reset_password == "1"){
            $("#text-current-password").text("Please change your password.(password->username)");
        }
    }
})
function resetPassword(){
    var current_password = $('#current-password').val();
    var new_password = $('#new-password').val();
    var confirm_password = $('#confirm-password').val();

    var valid = true;

    $('#text-current-password').css('color' ,'#303e4c');
    $('#text-new-password').css('color' ,'#303e4c');
    $('#text-confirm-password').css('color' ,'#303e4c');

    $('#text-current-password').text("Old Password");
    $('#text-new-password').text("New Password");
    $('#text-confirm-password').text("Conform New Password");

    if(current_password == ""){
        $("#text-current-password").css('color' ,'red');
        $('#text-current-password').text("CAN'T BE EMPTY");
        valid = false;
    }
    if(new_password == ""){
        $("#text-new-password").css('color' ,'red');
        $('#text-new-password').text("CAN'T BE EMPTY");
        valid = false;
    }
    if(confirm_password == ""){
        $("#text-confirm-password").css('color' ,'red');
        $('#text-confirm-password').text("CAN'T BE EMPTY");
        valid = false;
    }
    if(valid){
        if(new_password != confirm_password) {
            $("#text-new-password").css('color' ,'red');
            $('#text-new-password').text("The password doesn't match!");
            return;
        }
        if(new_password.length < 6){
            $("#text-new-password").css('color' ,'red');
            $('#text-new-password').text("The password is too short");
            return;
        }

        $("#btWaiting").css('display' ,'inline');
        $.ajax({
            url:'/account/check_login/api',
            type:'POST',
            data:"password="+current_password,
            success:function (response) {
                if(response.resultCode == "success"){
                    $.ajax({
                        url:'/account/password/reset/api',
                        type:'POST',
                        data:'password='+ new_password,
                        success:function(response){
                            if(response.resultCode == "success"){
                                swal("Success!", "", "success");
                            }
                            else{
                                swal("Error!", response.errorInfo, "error");
                            }

                            $("#btWaiting").css('display' ,'none');
                        }
                    });
                }
                else{
                    swal("Error!", response.errorInfo, "error");

                    $("#btWaiting").css('display' ,'none');
                }
            }
        });
    }
}

$('#current-password').keyup(function(event){

    var keycode = (event.keyCode ? event.keyCode : event.which);
    if(keycode == '13'){
        $('#new-password').focus();
    }
});


$('#new-password').keyup(function(event){

    var keycode = (event.keyCode ? event.keyCode : event.which);
    if(keycode == '13'){
        $('#confirm-password').focus();
    }
});

$('#confirm-password').keyup(function(event){

    var keycode = (event.keyCode ? event.keyCode : event.which);
    if(keycode == '13'){
        resetPassword();
    }
});


$('#btnUpdate_info').on('click',function(){
    var email = $('#email').val();
    var username = $('#reset_username').val();
    var valid = true;

    $("#email_error").css('color' ,'#303e4c');
    $("#username_error").css('color' ,'#303e4c');

    $("#email_error").text("Email:");
    $("#username_error").text("User name:");

    if(email == ""){
        $("#email_error").css('color' ,'red');
        $("#email_error").text("CAN'T BE EMPTY");
        valid = false;
    }

    if(email.indexOf("@") < 0){
        $("#email_error").css('color' ,'red');
        $("#email_error").text("PLEASE INPUT CORRECT INFO");
        valid = false;
    }

    if(username == ""){
        $("#username_error").css('color' ,'red');
        $("#username_error").text("CAN'T BE EMPTY");
        valid = false;
    }


    if(valid){
        $("#btWaiting").css('display' ,'inline');
        $.ajax({
            url:'/account/profile/api',
            type:'POST',
            data:{
                username:username,
                email:email
            },
            success:function(response){
                $("#btWaiting").css('display' ,'none');

                if(response.resultCode == "success"){
                    swal("Success!", "", "success");
                }
                else{
                    swal("Error!", response.errorInfo, "error");
                }

            },
            error: function(){
                $("#btWaiting").css('display' ,'none');
                swal("Error!", "", "error");
            }
        });
    }

});