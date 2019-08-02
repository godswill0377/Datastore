$(function(){
    $("#data-type-manage-li").addClass("active");
    $("#data-type-add-li").attr("class","active");
});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnAdd').on('click',function(){
    var name = $('#data-type-name').val();

    if(name == ""){
        show_error("You must input the name!");
        $('#data-type-name').focus();
        return;
    }

    $.ajax({
        url:'/admin/data_type/add',
        type:'POST',
        data:{
            name:name
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/data_type/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }
    });

});