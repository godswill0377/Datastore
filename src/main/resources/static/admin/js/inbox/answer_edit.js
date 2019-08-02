$(function(){
    $("#inbox-question-answer-manage-li").addClass("active");
    $("#inbox-question-answer-list-li").attr("class","active");
});
function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnUpdate').on('click',function(){
    var id = $('#data-id').val();
    var parent_id = $('#parent_id').val();
    var content = $('#content').val();

    if(content == ""){
        show_error("You must input the content Field!");
        $('#content').focus();
        return;
    }

    $.ajax({
        url:'/admin/inbox/question/answer/update',
        type:'POST',
        data: {
            'id' : id,
            'content' : content
        },
        success: function(data){
            if(data.resultCode == 'success'){
                autoCloseAlert(data.errorInfo,2000);
                setTimeout(function(){
                    window.location.href = "/admin/inbox/question/answer/list/"+parent_id;
                } , 2000);

            }else{
                autoCloseAlert(data.errorInfo,2000);
            }
        }

    });

});

//
$('#btnSave').on('click',function(){
    var questionContent= $('#content').val();
    var questionId = $('#questionId').val();
    var createAnswerUrl = "/admin/inbox/question/answer/create";

    if(questionContent == ""){
        show_error("You must input the Content Field!");
        $('#questionContent').focus();
        return;
    }
    $("#btWaiting").css('display','block');
    $.ajax({
        type: 'POST',
        data: {
            'parent_id' : questionId,
            'content' : questionContent
        },
        url: createAnswerUrl,
        success: function (data) {
            $("#btWaiting").css('display','none');

            window.location.href = "/admin/inbox/question/answer/list/"+questionId;
        }
    });
});