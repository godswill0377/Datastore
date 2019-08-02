
$(function(){
    var sub_balance = parseInt($("#sub_balance").val());
    var sub_download_sum = parseInt($("#sub_download_sum").val());
    if(sub_balance > sub_download_sum){
        $("#btnProceed").css({'pointer-events':'none', 'background': '#a2a2a2'});
    }

    var checkoutsum = parseInt($("#checkout_sum").val());

    if(checkoutsum == 0){
        $("#proceed_pay_section").css('display' ,'none');
        $("#sub_des_section").css('display' ,'none');
        $("#sub_btn_section").css('display' ,'none');
        $("#freebtn_section").css('display' ,'block');
    }

});
$('.item-delete').on('click',function(){
    var next_ele = $(this).next();

    swal({
            title: "Confirm",
            text: "Please confirm to delete.",
            type: "info",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
        },
        function(){
            $.ajax({
                type: "POST",
                url: '/add_cart/data_sets/delete/api',
                data: "id="+next_ele.val(),
                success: function (result) {
                    if (result.resultCode == "success") {
                        swal({
                            title: "Success!",
                            text: "",
                            timer: 2000,
                            showConfirmButton: false
                        });
                        setTimeout(function () {
                            window.location.href = "/account/mycart";
                        }, 2000);
                    }
                    else{
                        swal({
                            title: "Fail!",
                            text: "",
                            timer: 2000,
                            showConfirmButton: false
                        });
                        setTimeout(function () {
                            window.location.href = "/account/mycart";
                        }, 2000);
                    }
                }
            });
        });

});

$('#btnProceed').on('click',function(){
    var userid = $("#userid").val();
    if(parseInt(userid) == 0){
        $("#btSignin").click();
        return;
    }
    var checkout_items = parseInt($('#checkout_items').val());
    if(checkout_items == 0){
        return;
    }
    var price = parseInt($('#checkout_sum').val());

    $.ajax({
        type: "POST",
        url: '/user/payment/checkout',
        data: "",
        success: function (result) {
            if (result.resultCode == "success") {

               window.location.href = "/payment/" + result.object;

            }
            else {
                swal({
                    title: "Fail",
                    text: "",
                    timer: 2000,
                    showConfirmButton: false
                });
                setTimeout(function () {
                    window.location.href = "/";
                }, 2000);
            }
        }
    });

});


$('#btnSubProceed').on('click',function(){
    var userid = $("#userid").val();
    if(parseInt(userid) == 0){
        $("#btSignin").click();
        return;
    }
    var checkout_items = parseInt($('#checkout_items').val());
    if(checkout_items == 0){
        return;
    }

    var sub_balance = parseInt($("#sub_balance").val());
    var sub_download_sum = parseInt($("#sub_download_sum").val());

   /* if(sub_balance < sub_download_sum){
        swal({
            title: "Fail",
            text: "You haven't got enough balance to download. You can upgrade your membership",
            timer: 2000,
            showConfirmButton: false
        });
        setTimeout(function () {
            window.location.href = "/account/subscriptions";
        }, 2000);
        return;
    }*/
    var price = parseInt($('#checkout_sum').val());

    swal({
            title: "Confirm!",
            text: "Are you confirm to purchase?",
            type: "info",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
        },
        function () {

            $.ajax({
                type: "POST",
                url: '/user/cart/subscription/checkout',
                data: "",
                success: function (result) {
                    if (result.resultCode == "success") {

                        window.location.href = "/account/mydata";
                    }
                    else
                    if (result.errorInfo == "payment")
                    {
                        swal({
                            title: "Success",
                            text: "There is more datasets. Please use payment method to purchase it. ",
                            timer: 2000,
                            showConfirmButton: false
                        });
                        setTimeout(function () {
                            window.location.href = "/account/mycart";
                        }, 2000);
                    }
                    else{

                        swal({
                            title: "Fail",
                            text: "",
                            timer: 2000,
                            showConfirmButton: false
                        });
                        setTimeout(function () {
                            window.location.href = "/";
                        }, 2000);
                    }
                }
            });

        });


});


$('#btnFreeProceed').on('click',function(){
    var userid = $("#userid").val();
    if(parseInt(userid) == 0){
        $("#btSignin").click();
        return;
    }
    var checkout_items = parseInt($('#checkout_items').val());
    if(checkout_items == 0){
        return;
    }
    var price = parseInt($('#checkout_sum').val());

    swal({
            title: "Confirm!",
            text: "Are you confirm to download?",
            type: "info",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
        },
        function () {

            $.ajax({
                type: "POST",
                url: '/user/cart/free_data/checkout',
                data: "",
                success: function (result) {
                    if (result.resultCode == "success") {

                        window.location.href = "/account/mydata";
                    }
                    else {
                        swal({
                            title: "Fail",
                            text: "",
                            timer: 2000,
                            showConfirmButton: false
                        });
                        setTimeout(function () {
                            window.location.href = "/";
                        }, 2000);
                    }
                }
            });

        });



});