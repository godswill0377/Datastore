$(function(){

    $("#coupon-manage-li").addClass("active");
    $("#coupon-add-li").attr("class","active");
});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnAdd').on('click',function() {
    var coupon = $('#coupon').val();
    var discount = $('#discount').val();
    var expiry_date = $('#date-expiry').val();
    var dataset_ids = $('#dataset_ids').val();
    var coupon_for = $('#coupon-for').val();

    if (discount == "") {
        show_error("You must input the discount Field!");
        $('#content').focus();
        return;
    }
    if (expiry_date == "") {
        show_error("You must select the Expiry Date!");
        return;
    }

    $("#btWaiting").css("display","inline-block");
    $.ajax({
        url: '/vendor/coupon/add',
        type: 'POST',
        data: {
            coupon: coupon
            , discount: discount
            , dataset_ids: dataset_ids
            , expiry_date: expiry_date
            , coupon_for: coupon_for
        },
        success: function(data){
            $("#btWaiting").css("display","none");
            if(data.resultCode == "success"){
                window.location.href = "/vendor/coupon/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }
    });

});