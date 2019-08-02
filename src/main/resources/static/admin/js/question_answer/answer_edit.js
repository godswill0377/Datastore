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

    var dataset_id = $('#dataset_id').val();
    var question_by_userid = $('#question_by_userid').val();
    var type = $('#type').val();
    var votes = $('#votes').val();
    var answer_by = $('#answer_by').val();
    var deleted_date = $('#deleted_date').val();
    var updated_date = $('#updated_date').val();

    if(content == ""){
        show_error("You must input the content Field!");
        $('#content').focus();
        return;
    }

    $.ajax({
        url:'/admin/dataset/question/answer/update',
        type:'POST',
        data: {
            'id' : id,
            'content' : content,
            'parent_id' : parent_id,
            'dataset_id' : dataset_id,
            'question_by_userid' : question_by_userid,
            'type' : type,
            'votes' : votes,
            'answer_by' : answer_by,
            'updated_date' : updated_date
        },
        success: function(data){
            autoCloseAlert('Success!',2000);
            setTimeout(function(){
                window.location.href = "/admin/dataset/question/answer/list/"+parent_id;

            },2000);
        }

    });

});

//
$('#btnSave').on('click',function(){
    var questionContent= $('#content').val();
    var questionId = $('#questionId').val();
    var createAnswerUrl = "/admin/dataset/question/answer/create/";

    if(questionContent == ""){
        show_error("You must input the Content Field!");
        $('#questionContent').focus();
        return;
    }

    $.ajax({
        type: 'POST',
        data: {
            'parent_id' : questionId,
            'content' : questionContent
        },
        url: createAnswerUrl,
        success: function (data) {
            window.location.href = "/admin/dataset/question/answer/list/"+questionId;
        }
    });
});