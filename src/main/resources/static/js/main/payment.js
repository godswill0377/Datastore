var membership_type = "month";
var handler  , request_type;

$(document).ready(function() {

    if($("#sourceNums").val() == "0"){
        $("#paymentForm").removeAttr("novalidate");
    }
    else{
        $("#paymentForm").attr("novalidate", "true");
    }
    $("#example2-card-expiry").mask('00/00');
    $("#example2-card-number").mask('0000 0000 0000 0000');
    $("#example2-card-cvc").mask('0000');
    // PayPal payment

    var price = $("#final_amount").val();

    var paypalMode = $("#paypalMode").val();
    var paypalClientId = $("#paypalClientId").val();

    paypal.Button.render({

        // Set your environment

        env: paypalMode, // sandbox | production

        // Specify the style of the button

        style: {
            label: 'checkout',  // checkout | credit | pay | buynow | generic
            size:  'responsive', // small | medium | large | responsive
            shape: 'pill',   // pill | rect
            color: 'gold'   // gold | blue | silver | black
        },

        // PayPal Client IDs - replace with your own
        // Create a PayPal app: https://developer.paypal.com/developer/applications/create

        client: {
/*
            sandbox:    'AZDxjDScFpQtjWTOUtWKbyN_bDt4OgqaF4eYXlewfBP4-8aqX3PiV8e1GWU6liB2CUXlkA59kJXE7M6R',
            production: '<insert production client id>'
*/
            sandbox: paypalClientId,
            production: paypalClientId
        },

        // Wait for the PayPal button to be clicked

        payment: function(data, actions) {
            return actions.payment.create({
                payment: {
                    transactions: [
                        {
                            amount: { total: price, currency: 'USD' }
                        }
                    ]
                }
            });
        },

        // Wait for the payment to be authorized by the customer

        onAuthorize: function(data, actions) {
            return actions.payment.execute().then(function() {

                finalOrder("paypal" , {
                        email: "",
                        address_line1: "",
                        address_city: "",
                        address_state: "",
                        address_zip: "",
                        number: "",
                        expiry: "",
                        cvc: ""
                    });

            });
        },
        onCancel: function (data) {
            console.log('checkout.js payment cancelled', JSON.stringify(data, 0, 2));
        },

        onError: function (err) {
            console.error('checkout.js error', err);
        }
    }, '#btnDoPaypal');

    // Stripe Card Payment

});


$('input[name=payment-option]').on('click' , function(e){

    var opt_type = $('input[name=payment-option]:checked').val();

    if(opt_type == '0'){
        $("#paymentForm").attr("novalidate", "true");
        $("#btnDoPaypal").css('display','none');

        $("#card_section").attr("overflow" ,"hidden");
        $("#card_section").animate({
            height: '0px'
        }, 300, function(){
            $("#card_section").css('display' ,'none');
        });
    }

    else if(opt_type == '1'){
        $("#paymentForm").removeAttr("novalidate");
        $("#btnDoPaypal").css('display','none');

        $("#card_section").attr("overflow" ,"initial");
        $("#card_section").animate({
            height: '255px'
        }, 300 , function(){
            $("#card_section").css('display' ,'block');
        });

    }
    else if(opt_type == '2'){
        $("#paymentForm").attr("novalidate", "true");

        $("#btnDoPaypal").css('display','block');

        $("#card_section").attr("overflow" ,"hidden");
        $("#card_section").animate({
            height: '0px'
        }, 300, function(){
            $("#card_section").css('display' ,'none');
        });
    }

});


function finalOrder(paymode, cardinfo){

    var order_id = $("#order_id").val();
    $("#btWaiting").css('display' ,'inline-block');

    $("#submitBtn").attr('disabled' , "1");
    $.ajax({
        url: '/user/payment/do',
        type: 'POST',
        data: {
            'order_id' : order_id
            ,"paymode" : paymode
            ,"email" : cardinfo.email
            ,"address_line1" : cardinfo.address_line1
            ,"address_city" : cardinfo.address_city
            ,"address_state" : cardinfo.address_state
            ,"address_zip" : cardinfo.address_zip
            ,"number" : cardinfo.number
            ,"expiry" : cardinfo.expiry
            ,"cvc" : cardinfo.cvc
        } ,

        success: function (result) {

            if (result.resultCode == "success") {

                swal({
                    title: "Success!",
                    text: "",
                    timer: 2000,
                    showConfirmButton: false
                });
                setTimeout(function(){
                    var mode = $("#mode").val();
                    if(mode == 'checkout'){
                        window.location.href= "/account/mydata";
                    }
                    else{
                        window.location.href= "/account/active";
                    }
                },2000);

            }
            else {
                if(result.errorInfo == "login"){
                    $("#btSignin").click();
                    return;
                } else
                if(result.errorInfo == "token"){
                    swal("Error" , "You have input incorrect card information. Please check again!" , "error");
                }
                else
                if(result.errorInfo == "subscription_null"){
                    swal("Error" , "Stripe Subscription has some problem. Please try again later." , "error");
                }
                else{
                    swal("Error" , result.errorInfo , "error");
                }

            }

            $("#btWaiting").css('display' ,'none');
            $("#submitBtn").removeAttr('disabled');
        },
        error: function(){
            $("#btWaiting").css('display' ,'none');
            $("#submitBtn").removeAttr('disabled');
        }
    });
}
