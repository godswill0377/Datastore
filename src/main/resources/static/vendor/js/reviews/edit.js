$(function(){

    $("#dataset-review-manage-li").addClass("active");
    $("#dataset-review-list-li").attr("class","active");

});
function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnUpdate').on('click',function(){
    var id = $('#data-id').val();
    var title = $('#title').val();
    var content = $('#content').val();
    var stars = $('#stars').val();
    var customer_id = $('#customer_id').val();
    var dataset_id = $('#dataset_id').val();
    var updated_date = $('#updated_date').val();
    var additional_imgs = $('#additional_imgs').val();
    var helpful_num = $('#helpful_num').val();

    if(title == ""){
        show_error("You must input the title Field!");
        $('#data-type-name').focus();
        return;
    }
    if(content == ""){
        show_error("You must input the title Field!");
        $('#data-type-name').focus();
        return;
    }

    $.ajax({
        url:'/vendor/reviews/update',
        type:'POST',
        data:{
            id:id,
            title: title,
            content: content,
            stars: stars,
            customer_id: customer_id,
            dataset_id: dataset_id,
            updated_date: updated_date,
            additional_imgs: additional_imgs,
            helpful_num: helpful_num
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/vendor/reviews/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }

    });

});