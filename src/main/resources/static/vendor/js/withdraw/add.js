$(function(){

    $("#withdraw-manage-li").addClass("active");
    $("#withdraw-request-li").attr("class","active");

    var withdraw_request = $('#withdraw_request').val();
    if(withdraw_request == '1' || withdraw_request == 1){
        show_error("You have already requested withdraw, You are only allowed to request withdraw once");
        $('#withdraw_form').hide();
        $('label[for=total_amount], input#total_amount').hide();
    }
});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnAdd').on('click',function() {
    var withdraw_amount = $('#amount').val();
    var available_balance = $('#total_amount').val();
    var source = $('#source').val();
    var source_account = "";
    var source_name = "";
    var description = $('#description').val();
    var total = $('#total_amount').val();

    if(parseFloat(total) <= 0){
        show_error("You have not earned yet.");
        $('#amount').focus();
        return;
    }

    if(parseFloat(withdraw_amount) > parseFloat(total)){
        show_error("Withdraw Value Is Greater Than Your Earning");
        $('#amount').focus();
        return;
    }

    $("#btWaiting").css('display' ,'inline-block');
    $.ajax({
        url: '/vendor/withdraw/add',
        type: 'POST',
        data: {
            withdraw_amount: parseFloat(withdraw_amount)
            , available_balance: parseFloat(available_balance)
            , source: source
            , source_account: source_account
            , source_name: source_name
            , description : description
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/vendor/withdraw/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }
    });

});

