$(function(){

    $("#user-manage-li").addClass("active");
    $("#user-list-li").attr("class","active");

    $('.form_datetime').datetimepicker({
        //language:  'fr',
        weekStart: 1,
        todayBtn:  1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        forceParse: 0,
        showMeridian: 1
    });

    $('#membership > option').each(function(){
        if($(this).val() == $("#membership").attr('value')){
            $(this).attr('selected','1');
        }
    });
});

function check_number(event){

    if((event.keyCode >= 48 && event.keyCode <= 57) || event.keyCode == 46){
    }
    else{
        event.preventDefault();
    }
}


$('#btnUpdate').on('click',function(){

    var userName = $('#userName').val();
    var userEmail = $('#userEmail').val();
    var userPass = $('#userPass').val();
    var userConfirmPass = $('#userConfirmPass').val();

    var userApiKey = $('#userApiKey').val();

    var membership = $('#membership').val();
    var balance = $('#balance').val();
    var expires_date = $('#expires_date').val();

    var id = $('#data-id').val();

    var activate = document.getElementById('activate').checked;
    if(activate){
        activate = 1;
    }
    else{
        activate = 0;
    }

    if(balance == "")
        balance = 0;
    if(userName == ""){
        show_error("You must input the UserName!");
        $('#userName').focus();
        return;
    }
    if(userEmail == ""){
        show_error("You must input the Email address!");
        $('#userEmail').focus();
        return;
    }
    if(userEmail.indexOf("@") < 0){
        show_error("The email address is incorrect!");
        $('#userEmail').focus();
        return;
    }

    if(userPass != "") {
        if (userPass != userConfirmPass) {
            show_error("The password doesn't match!");
            $('#userConfirmPass').focus();
            return;
        }
    }

    new $.flavr({
        content: 'Confirm?',
        buttons: {
            primary: {
                text: 'OK', style: 'primary', action: function () {
                    $.ajax({
                        url:'/admin/user/update',
                        type:'POST',
                        data:{
                            id:id,
                            username:userName,
                            email:userEmail,
                            password:userPass,
                            membership:membership,
                            balance:balance,
                            expire_date:expires_date,
                            activate:activate,
                            apiKey:userApiKey
                        },
                        success: function(data){
                            if(data.resultCode == "success"){
                                window.location.href = "/admin/user/list"
                            }
                            else{
                                show_error(data.errorInfo);
                                return;
                            }
                        }
                    });

                }
            },
            success: {
                text: 'Cancel', style: 'danger', action: function () {

                }
            }
        }
    });


});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}