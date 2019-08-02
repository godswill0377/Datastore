$(function(){
    $("#data-publisher-manage-li").addClass("active");
    $("#data-publisher-add-li").attr("class","active");
});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnAdd').on('click',function(){
    var name = $('#data-name').val();

    if(name == ""){
        show_error("You must input the name!");
        $('#data-name').focus();
        return;
    }

    $.ajax({
        url:'/admin/plan/add',
        type:'POST',
        data:{
            name:name
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/plan/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }
    });

});