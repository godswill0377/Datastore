$(function(){

    $("#question-answer-manage-li").addClass("active");
    $("#question-answer-list-li").attr("class","active");

});
function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnUpdate').on('click',function(){
    var id = $('#data-id').val();
    var parent_id = $('#parent_id').val();
    var content = $('#content').val();
    var votes = $('#votes').val();

    if(content == ""){
        show_error("You must input the content Field!");
        $('#content').focus();
        return;
    }

    $.ajax({
        url:'/vendor/dataset/question/answer/update/'+id,
        type:'POST',
        data: {
            'content' : content,
            'votes' : votes
        },
        success: function(data){
            autoCloseAlert('Success!',1000);
            window.location.href = "/vendor/dataset/question/answer/list/"+parent_id;
        }

    });

});

//
$('#btnSave').on('click',function(){
    var questionContent= $('#content').val();
    var questionId = $('#questionId').val();
    var createAnswerUrl = "/vendor/dataset/question/answer/create";
    var votes = $('#votes').val();

    if(questionContent == ""){
        show_error("You must input the Content Field!");
        $('#questionContent').focus();
        return;
    }

    $.ajax({
        type: 'POST',
        data: {
            'parent_id' : questionId,
            'content' : questionContent,
            'votes' : votes
        },
        url: createAnswerUrl,
        success: function (data) {
            window.location.href = "/vendor/dataset/question/answer/list/"+questionId;
        }
    });
});