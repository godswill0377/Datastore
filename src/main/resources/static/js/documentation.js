var pager = {page:1,start:0,limit:10,search_str:"",cateid:"1",parent_code:"" , is_sample_available:1,is_free:"0"};

$(function () {

    $('#data-tab').removeClass('active');
    $('#docu-tab').addClass('active');
    pager.cateid = $('#cateid').val();

    $('.main-nav-link').each(function () {
        $(this).removeClass('active');
    });

    $('#cateid' + pager.cateid).addClass('active');

    var is_free = $('#is_free').val();
    if(is_free == "1"){
       // $('#btview_pricing').css('display','none');
        $('#filter_viewer').css('display','none');
    }
    else{
     //   $('#btview_pricing').css('display','block');
        $('#filter_viewer').css('display','block');
    }
    pager.is_free = is_free;
    pager.parent_code = $("#parent_code").val();

    $("#document_section").html($("#document_data").val());
});



$('#btview_pricing').on('click',function(){
    var userid = $('#userid').val();
    var cateid = $("#cateid").val();
    var parentcode = $('#parent_code').val();

    window.location.href = "/data_sets/cateid/" + cateid +"/details/"  + parentcode + "/purchase";

});