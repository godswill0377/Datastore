$(function(){
    $("#inbox-question-answer-manage-li").addClass("active");
    $("#inbox-question-answer-list-li").attr("class","active");
});
function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

//
$('#btnSave').on('click',function(){
    var questionContent= $('#content').val();
    var createAnswerUrl = "/vendor/inbox/question/create";

    if(questionContent == ""){
        show_error("You must input the Content Field!");
        $('#questionContent').focus();
        return;
    }

    $.ajax({
        type: 'POST',
        data: {
            'content' : questionContent
        },
        url: createAnswerUrl,
        success: function (data) {
            window.location.href = "/vendor/inbox/question/list";
        }
    });
});