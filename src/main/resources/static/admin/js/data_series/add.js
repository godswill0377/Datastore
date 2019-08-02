$(function(){

    $("#data-series-manage-li").addClass("active");
    $("#data-series-add-li").attr("class","active");

    $('.form_datetime').datetimepicker({
        //language:  'fr',
        weekStart: 1,
        todayBtn:  1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        forceParse: 0,
        showMeridian: 1
    });
});
function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}




$('#btnAdd').on('click',function(){
    var name = $('#data-name').val();

    var parent_code = $("#parent_code").val();

    var code = parent_code+ "/" + $('#code').val();
    var latest_update_date=$('#latest_update_date').val();
    var description = $('#description').val();
    var  data_update_frequency = $('#data_update_frequency').val();
    var is_sample_available = $('#is_sample_available').val();
    var source_url = $('#source_url').val();
    var filter_condition = $('#filter_condition').val();
    var filter_id = $('#filter_id').val();
    var data_set_id = $('#data-set-id').val();


    var embed_url_chart = $("#embed_url_chart").val();
    var embed_url_datagrid = $("#embed_url_datagrid").val();

    var source_type = document.getElementById("source_type").checked;
    if(source_type){
        embed_url_datagrid = "";  embed_url_chart = "";

        if(source_url.indexOf("http://") == 0){
        }
        else{
            source_url = "http://" + source_url;
        }
    }
    else{
        source_url = "";
    }

    if(data_set_id == ""){
        show_error("You must input the Dataset ID Field!");
        window.location.href="#";
        return;
    }

    if(name == ""){
        show_error("You must input the Name Field!");
        window.location.href="#";
        return;
    }

    if(code == ""){
        show_error("You must input the Code Field!");
        window.location.href="#";
        return;
    }
   var pos = code.indexOf("/");
    if(pos < 0){
        show_error("You must input correct code! (FIN/MON) like this.  The first part is dataset code and second part is dataset-series code.");
        window.location.href="#";
        return;
    }

    var pos1 = code.indexOf("/" , pos + 1);
    if(pos1 >= 0){
        show_error("You must input correct code! (FIN/MON) like this.  The first part is dataset code and second part is dataset-series code.");
        window.location.href="#";
        return;
    }

    if(description == ""){
        show_error("You must input the Description Field!");
        window.location.href="#";
        return;
    }

    if(filter_condition != "" && filter_id != ""){
        show_error("You cannot input filter_condition and filter_id. You can input only one field!");
        window.location.href="#";
        return;
    }

/*    if(latest_update_date == ""){
        show_error("You must input the Latest Update Date Field!");
        window.location.href="#";
        return;
    }
*/

    if(source_url == "" && embed_url_chart == ""){
        show_error("You must input the Source Url Field!");
        window.location.href="#";
        return;
    }


    $.ajax({
        url:'/admin/data_series/add',
        type:'POST',
        data:{
            name:name,
            description:description,
            code:code,
            latest_update_date:latest_update_date,
            data_update_frequency_id:data_update_frequency,
            is_sample_available: is_sample_available,
            source_url:source_url,
            filter_condition:filter_condition,
            filter_id:filter_id,
            data_set_id:data_set_id,
            embed_url_chart:embed_url_chart,
            embed_url_datagrid: embed_url_datagrid
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/data_series/list"
            }
            else{
                show_error(data.errorInfo);
                window.location.href="#";
                return;
            }
        }

    });

});


$('#btnShowFilterList').on('click',function(){

    var display = $("#data_set_filter_section").css('display');
    if(display == 'block'){
        $("#data_set_filter_section").css('display' ,'none');
    }
    else{
        $("#data_set_filter_section").css('display' ,'block');
    }
});



$('.radiobox').on('click',function(){
    var val = $(this).attr('value');
    if(val == "source"){
        $("#embed_url_datagrid").attr('disabled' , '1');
        $("#embed_url_chart").attr('disabled' , '1');
        $("#source_url").removeAttr('disabled' );
    }
    if(val == "embed"){
        $("#source_url").attr('disabled' , '1');
        $("#embed_url_datagrid").removeAttr('disabled' );
        $("#embed_url_chart").removeAttr('disabled' );
    }
});