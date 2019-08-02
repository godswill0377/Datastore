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
    var content = $('#content').val();
    var dataset_id = $('#dataset_id').val();
    var question_by_userid = $('#question_by_userid').val();
    var type = $('#type').val();
    var parent_id = $('#parent_id').val();
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
        url:'/admin/question/update',
        type:'POST',
        data:{
            id:id,
            content:content
            ,dataset_id:dataset_id
            ,question_by_userid:question_by_userid
            ,type:type
            ,parent_id:parent_id
            ,votes:votes
            ,answer_by:answer_by
            ,deleted_date:deleted_date
            ,updated_date:updated_date
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/dataset/question/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }

    });

});