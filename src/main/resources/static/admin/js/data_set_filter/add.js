$(function(){
    $("#data-set-filter-manage-li").addClass("active");
    $("#data-set-filter-add-li").attr("class","active");
});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$('#btnAdd').on('click',function(){
    /*var data_set_id = $('#data-set-id').val();*/
    var column_name = $('#data-column-name').val();
    var filter_value = $('#data-filter-value').val();
    var comparator = $('#data-comparator').val();



    if(column_name == ""){
        show_error("You must input the name!");
        $('#data-column-name').focus();
        return;
    }

    if(filter_value == ""){
        show_error("You must input the filter value!");
        $('#data-filter-value').focus();
        return;
    }


    if(comparator == ""){
        show_error("You must input the comparator!");
        $('#data-comparator').focus();
        return;
    }

    $.ajax({
        url:'/admin/data_set_filter/add',
        type:'POST',
        data:{
          /*  data_set_id:data_set_id,*/
            column_name:column_name,
            filter_value:filter_value,
            comparator:comparator
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/data_set_filter/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }
    });

});