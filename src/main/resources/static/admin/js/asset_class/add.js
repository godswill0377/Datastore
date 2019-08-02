$(function(){
    $("#asset-class-manage-li").addClass("active");
    $("#asset-class-add-li").attr("class","active");
});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnAdd').on('click',function(){
    var name = $('#asset-class-name').val();

    if(name == ""){
        show_error("You must input the name!");
        $('#asset-class-name').focus();
        return;
    }

    $.ajax({
        url:'/admin/asset_class/add',
        type:'POST',
        data:{
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