$(function(){

    $("#inbox-question-answer-manage-li").addClass("active");
    $("#inbox-notification-list-li").attr("class","active");
});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnAdd').on('click',function(){
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

    swal({
            title: "Are you sure?",
            text: "You will send this notification to these users!",
            type: "info",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
        },
        function(){

            $.ajax({
                url:'/admin/inbox/notification/add',
                type:'POST',
                data:{
                    title:title
                    , content:content
                    , to_user_ids:to_user_ids
                },
                success: function(data){
                    if(data.resultCode == "success"){
                        window.location.href = "/admin/inbox/notification/list";
                    }
                    else{
                        show_error(data.errorInfo);
                        return;
                    }
                }
            });
        });



});