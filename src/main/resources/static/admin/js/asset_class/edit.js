$(function(){

    $("#asset-class-manage-li").addClass("active");
    $("#asset-class-list-li").attr("class","active");

});
function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnUpdate').on('click',function(){
    var id = $('#asset-class-id').val();
    var name = $('#asset-class-name').val();

    if(name == ""){
        show_error("You must input the Name Field!");
        $('#asset-class-name').focus();
        return;
    }

    $.ajax({
        url:'/admin/asset_class/update',
        type:'POST',
        data:{
            id:id,
            name:name
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/asset_class/list"
            }
            else{
                show_error(data.errorInfo);
                window.location.href="#";
                return;
            }
        }

    });

});