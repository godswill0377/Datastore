$(function(){
    $("#data-category-manage-li").addClass("active");
    $("#data-category-add-li").attr("class","active");
});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnAdd').on('click',function(){
    var name = $('#data-category-name').val();
    var description = $('#data-category-description').val();
    if(name == ""){
        show_error("You must input the name!");
        $('#data-category-name').focus();
        return;
    }

    $.ajax({
        url:'/admin/data_category/add',
        type:'POST',
        data:{
            name:name,
            description: description
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/data_category/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }
    });

});