$(function(){

    $("#setting-li").addClass("active");
    $("#setting-list-li").attr("class","active");

});
function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnUpdate').on('click',function(){

    var id = $('#data-id').val();
    var plan_id = $('#plan_id').val();
    var plan_name = $('#plan_name').val();
    var frequency = $('#frequency').val();
    var real_price = $('#real_price').val();
    var vr_price = $('#vr_price').val();
    var vendor_id = $('#vendor_id').val();

    if(plan_name == ""){
        show_error("You must input the Plan Name Field!");
        $('#plan_name').focus();
        return;
    }

    if(real_price == ""){
        show_error("You must input the Real Price Field!");
        $('#real_price').focus();
        return;
    }

    if(vr_price == ""){
        show_error("You must input the Virtual Price Field!");
        $('#vr_price').focus();
        return;
    }

    $.ajax({
        url:'/vendor/plan/update',
        type:'POST',
        data:{
            id:id,
            plan_id:plan_id,
            plan_name:plan_name,
            frequency:frequency,
            real_price:real_price,
            vr_price:vr_price,
            vendor_id: vendor_id
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/vendor/plan/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }

    });

});