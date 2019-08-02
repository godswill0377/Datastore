$(function(){

    $("#coupon-manage-li").addClass("active");
    $("#coupon-marketadd-li").attr("class","active");
});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnAdd').on('click',function(){
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


    $("#btWaiting").css('display' ,'block');
    $.ajax({
        url:'/vendor/couponmarketing/add',
        type:'POST',
        data:{
            title:title
            , content:content
            , dataset_ids:dataset_ids
            , email_address:email_address
        },
        success: function(data){
            if(data.resultCode == "success"){
                $("#btWaiting").css('display' ,'none');

                swal({
                        title: "Are you sure?",
                        text: "You will send coupon code to these recipients",
                        type: "info",
                        showCancelButton: true,
                        closeOnConfirm: false,
                        showLoaderOnConfirm: true,
                    },
                    function(isConfirm){

                        if (isConfirm) {
                            $.ajax({
                                url: '/vendor/couponmarketing/sendmails',
                                type: 'POST',
                                data: {
                                    id: data.object
                                },

                                success: function()
                                {
                                    swal({
                                        title: "Success!",
                                        text: "Your mail is sent to users.",
                                        timer: 2000
                                    });
                                    setTimeout(function(){
                                        window.location.href = "/vendor/couponmarketing/list";
                                    } , 2000);
                                }
                            });
                        }
                        else {
                            window.location.href = "/vendor/couponmarketing/list";
                        }
                    });

            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }
    });

});