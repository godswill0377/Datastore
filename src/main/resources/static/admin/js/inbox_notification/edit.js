$(function(){

    $("#inbox-question-answer-manage-li").addClass("active");
    $("#inbox-notification-list-li").attr("class","active");

});
function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnUpdate').on('click',function(){
    var id = $('#data-id').val();
    var title = $('#title').val();
    var content = $('#content').val();
    var to_user_ids = $('#to_user_ids').val();

    if(title == ""){
        show_error("You must input the title Field!");
        $('#title').focus();
        return;
    }
    if(content == ""){
        show_error("You must input the content Field!");
        $('#content').focus();
        return;
    }

    $.ajax({
        url:'/admin/inbox/notification/update',
        type:'POST',
        data:{
            id:id,
            title:title
            , content:content
            , to_user_ids:to_user_ids
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/inbox/notification/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }

    });

});