$(function(){

    $("#vendor-manage-li").addClass("active");
    $("#vendor-list-li").attr("class","active");

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

    var id = $('#data-id').val();
    var vendor_id = $('#vendor_id').val();



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


    var legal_name = $("#legal_name").val();
    var address = $("#address").val();
    var city = $("#city").val();
    var state_province = $("#state_province").val();
    var country = $("#country").val();
    var zip_postal = $("#zip_postal").val();
    var business_name = $("#business_name").val();
    var website_url = $("#website_url").val();
    var mobile_num = $("#mobile_num").val();

    new $.flavr({
        content: 'Confirm?',
        buttons: {
            primary: {
                text: 'OK', style: 'primary', action: function () {
                    $.ajax({
                        url:'/admin/vendor/update',
                        type:'POST',
                        data:{
                            id:id,
                            username:userName,
                            email:userEmail,
                            password:userPass,
                            vendor_id: vendor_id,
                            legal_name:legal_name,
                            address:address,
                            city:city,
                            state_province:state_province,
                            country:country,
                            zip_postal:zip_postal,
                            business_name:business_name,
                            website_url:website_url,
                            mobile_num:mobile_num
                        },
                        success: function(data){
                            if(data.resultCode == "success"){
                                window.location.href = "/admin/vendor/list"
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