var userid = '0' ;
var flag = false;
var discount = 0;
$(function(){
    $('#container').css('height','100% !important');
    $('#discount-price').text('$' + discount);
    $('#final-price').text('$' + $('#onetime_price').val());
    userid = $('#userid').val();

});

$('#coupon-apply').click(function(){
  var coupon = $('#coupon-amount').val();
  var datasetId = $('#data_sets_id').val();
  var balance = $('#onetime_price').val();

    if(coupon == "" ){
        return;
    }
    if(flag){
        $('#applied-coupon').text("Sorry,Coupon Already Applied");
        return;
    }
    $.ajax({
        url: '/user/' + coupon +'/api/'+ datasetId,
        type: 'POST',
        success: function (data) {
            discount = (balance * (data.object)/100).toFixed(2);
            balance = (balance - discount).toFixed(2);
            $('#onetime_price').val(balance);
            console.log(($('#onetime_price').val()));

            if(data.object > 0){
                $('#applied-coupon').text("Coupon Applied!! ");
                if(data.errorInfo == "cart"){
                    $('#applied-coupon').append("Coupon Already Applied For Cart");
                }
                $('#coupon-amount').attr('disabled', 'disabled');
                flag = true;
            }

            if(data.errorInfo =="expired"){
                $('#applied-coupon').text("Coupon Already Expired");

            }

            if(data.errorInfo =="badcoupon"){
                $('#applied-coupon').text("Bad or Wrong Coupon");

            }

            if(data.errorInfo =="invalid"){
                $('#applied-coupon').text("Invalid Coupon");

            }

            if(data.errorInfo =="used"){
                $('#applied-coupon').text("Coupon Already Used");

            }

            if(data.errorInfo =="cart"){
                $('#applied-coupon').text("Coupon Already Applied To Cart");

            }

            if(data.errorInfo =="login"){
                $('#btSignin').click();
                return;
            }

            $('#discount-price').text('$' + discount);
            $('#final-price').text('$' + balance);

        }
    });

});

$('#btDownPurchase').on('click',function(){
    var membership = $('#membership').val();
    if(membership == 0 || membership == "0" ){
        swal({
                title: "Fail!",
                text: "You must be a subscription user to download.",
                type: "info",
                showCancelButton: true,
                closeOnConfirm: false,
                showLoaderOnConfirm: true,
            },
            function(){
                setTimeout(function(){
                    window.location.href = "/account/active";
                },1000);
            });
    }
    else {
        var balance = parseFloat($('#balance').val());
        var download_price = parseFloat($('#download_price').val());


        if (balance < download_price) {
            swal({
                    title: "Fail!",
                    text: "You haven't got enough balance. Please upgrade your membership.",
                    type: "info",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    showLoaderOnConfirm: true,
                },
                function () {
                    setTimeout(function () {
                        window.location.href = "/account/active";
                    }, 1000);
                });
        }
        else{
            var expired = $('#expired').val();
            if(expired == "1" || expired == 1){
                swal({
                        title: "Fail!",
                        text: "Your account is expired. Please use one-time purchase or upgrade your account.",
                        type: "info",
                        showCancelButton: true,
                        closeOnConfirm: false,
                        showLoaderOnConfirm: true,
                    },
                    function () {
                        setTimeout(function () {
                            window.location.href = "/account/active";
                        }, 1000);
                    });
            }
            else{

                var data_sets_id = $('#data_sets_id').val();
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
                           url: '/download_purchase/data_sets/api',
                           type:'POST',
                           data:'id='+data_sets_id+'&sId='+membership,
                           success: function(response){
                               if(response.resultCode ="success"){
                                   swal("Your purchased successfully! You can get download link in email.");
                                   setTimeout(function(){
                                       window.location.href="/account/mydata";
                                   },1500);

                               }
                               else{
                                   if(response.errorCode == "expire"){
                                       swal("Your membership is expired. Please upgrade your membership again..");
                                       setTimeout(function(){
                                           window.location.href="/membership/active";
                                       },1500);
                                   }
                                   else {
                                       if(result.errorInfo == "login"){
                                           $("#btSignin").click();
                                           return;
                                       }
                                       window.location.href = "/";
                                   }
                               }
                           }
                       });
                    });

            }
        }
    }
});

$('#btOnetimePurchase').on('click',function()
{

    var data_sets_id = $('#data_sets_id').val();
    var coupon = "coupon";
    if(userid == '0'){
        $('#btSignin').click();
        return;
    }
    if(flag){
        coupon = $('#coupon-amount').val();
    }
    swal({
            title: "Do you confirm to add?",
            text: "Submit your request",
            type: "info",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
        },
        function() {
            $.ajax({
                url: '/add_cart/data_sets/api',
                type: 'POST',
                data: 'id=' + data_sets_id+'&coupon='+ coupon,
                success: function (response) {
                    if (response.errorInfo == "cart") {
                        swal({
                            title: "This Coupon Already Applied For this cart! Dataset Added Without Discount !! Try Another Coupon",
                            text: "",
                            timer: 2000,
                            showConfirmButton: false
                        });
                        setTimeout(function () {
                            window.location.href = "/account/mycart";
                        }, 2000);
                    }
                    else if (response.resultCode = "success") {
                        swal({
                            title: "The data set added!",
                            text: "",
                            timer: 2000,
                            showConfirmButton: false
                        });
                        setTimeout(function(){
                            window.location.href= "/account/mycart";
                        },2000);

                    }
                    else {
                        window.location.href = "/";
                    }
                }
            });
        });
});

/**
 data_sets/cateid/1/details/ZIP/purchase#

  - click 'Pay Now' button
*/
$('#btPayNow').on('click',function()
{

    var userid = $("#userid").val();
    var coupon = "coupon";
    if(userid == '0'){
        $('#btSignin').click();
        return;
    }
    if(flag){
        coupon = $('#coupon-amount').val();
    }
    var data_sets_id = $('#data_sets_id').val();
    if(data_sets_id == "") return;
    if(data_sets_id == undefined) return;

    $.ajax({
        url: '/user/payment/order',
        type:'POST',
        data:'id='+data_sets_id +'&coupon='+ coupon,
        success: function(response){
            if(response.resultCode ="success"){
                window.location.href = "/payment/" + response.object;
            }
            else{
                swal("Error!" , response.errorInfo ,"error");
            }
        }
    });

});

