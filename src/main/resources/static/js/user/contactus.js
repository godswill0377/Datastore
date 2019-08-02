var onloadCallback = function() {
    var captcha_handle =  grecaptcha.render('captcha', {
        'sitekey' : $('#sitekey').val()
    });
};
$("#form").submit(function(e)
{

    $('#valid_ab_name').css('visibility','hidden');
    $('#valid_ab_email').css('visibility','hidden');
    $('#valid_ab_content').css('visibility','hidden');

    var flag = true;
    if($('#name').val() == ""){
        $('#valid_ab_name').css('visibility','visible');
        flag = false;
    }
    if($('#email').val() == ""  ){
        $('#valid_ab_email').css('visibility','visible');
        flag = false;
    }
    else{
        if($('#email').val().indexOf("@") < 0 ){
            $('#valid_ab_email').css('visibility','visible');
            flag = false;
        }
    }
    if($('#content').val() == ""){
        $('#valid_ab_content').css('visibility','visible');
        flag = false;
    }
    if(!flag){
        e.preventDefault();
        return;
    }


    $('#sm_btWaiting').css('display','block');

    var postData = $(this).serializeArray();
    var formURL = $(this).attr("action");
    $.ajax(
        {
            url : formURL,
            type: "POST",
            data : postData,
            success:function(data, textStatus, jqXHR)
            {
                $('#sm_btWaiting').css('display', 'none');
                if(data.resultCode == "success") {


                    $('#valid_ab_name').css('visibility', 'hidden');
                    $('#valid_ab_email').css('visibility', 'hidden');
                    $('#valid_ab_content').css('visibility', 'hidden');
                    swal({
                        title: "Success",
                        text: "",
                        timer: 2000,
                        showConfirmButton: false
                    });
                    setTimeout(function(){
                        window.location.href="";
                    },2000);
                }
                else{
                    $('#valid_ab_captcha').css('visibility', 'visible');
                }
            },
            error: function(jqXHR, textStatus, errorThrown)
            {
                $('#sm_btWaiting').css('display', 'none');
                sweetAlert("There is an error while send the email!");
            }
        });

    e.preventDefault();
});
