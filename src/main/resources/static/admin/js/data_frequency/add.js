$(function(){
    $("#data-frequency-manage-li").addClass("active");
    $("#data-frequency-add-li").attr("class","active");
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
        url:'/admin/data_frequency/add',
        type:'POST',
        data:{
            name:name
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/data_frequency/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }
    });

});