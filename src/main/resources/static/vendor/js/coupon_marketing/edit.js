$(function(){

    $("#coupon-manage-li").addClass("active");
    $("#coupon-market-li").attr("class","active");

});
function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnUpdate').on('click',function(){
    var id = $('#data-id').val();
    var title = $('#title').val();
    var content = $('#content').val();
    var dataset_ids = $('#dataset_ids').val();
    var email_address = $('#email_address').val();

    if(title == ""){
        show_error("You must input the title Field!");
        $('#title').focus();
        return;
    }
    if(content == ""){
        show_error("You must input the content Field!");
        $('#content').focus();
        return;
    }
    if(email_address == ""){
        show_error("You must input the Email Address Field!");
        $('#email_address').focus();
        return;
    }

    if(title == ""){
        show_error("You must input the title Field!");
        $('#title').focus();
        return;
    }
    if(content == ""){
        show_error("You must input the content Field!");
        $('#content').focus();
        return;
    }

    $.ajax({
        url:'/vendor/marketing/update',
        type:'POST',
        data:{
            id:id,
            title:title
            , content:content
            , dataset_ids:dataset_ids
            , email_address:email_address
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/vendor/couponmarketing/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }

    });

});