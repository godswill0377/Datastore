$(function(){
    $("#setting-li").addClass("active");
    $("#setting-fee-li").attr("class","active");

});
function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnUpdate').on('click',function(){
    var id = $('#data-id').val();
    var fee_name = $('#fee-name').val();
    var fee_percent = $('#fee-percent').val();

    if(fee_name == ""){
        show_error("You must input the Fee Name!");
        $('#data-type-name').focus();
        return;
    }
    if(fee_percent == ""){
        show_error("You must input the Fee Percent!");
        $('#data-type-name').focus();
        return;
    }

    $.ajax({
        url:'/admin/fee/update',
        type:'POST',
        data:{
            id:id,
            fee_name: fee_name,
            fee_percent: fee_percent
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/fee/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }

    });

});