var uploadedPath = "";
var domain = "";
$(function(){
    CKEDITOR.replace('editor1');
    //bootstrap WYSIHTML5 - text editor
    $(".textarea").wysihtml5();

    domain = $("#domain").val();
    $("#data-set-manage-li").addClass("active");
    $("#data-set-list-li").attr("class","active");

    $('#has_series > option').each(function(){
        if($(this).val() == $("#has_series").attr('value')){
            $(this).attr('selected','1');
        }
    });

    $('#category > option').each(function(){
        if($(this).val() == $("#category").attr('value')){
            $(this).attr('selected','1');
        }
    });

    $('#price_model > option').each(function(){

        if($(this).val() == $("#price_model").attr('value')){

            $(this).attr('selected','1');
        }
    });

    $('#asset_class > option').each(function(){
        if($(this).val() == $("#asset_class").attr('value')){
            $(this).attr('selected','1');
        }
    });

    $('#data_type > option').each(function(){
        if($(this).val() == $("#data_type").attr('value')){
            $(this).attr('selected','1');
        }
    });

    $('#region > option').each(function(){
        if($(this).val() == $("#region").attr('value')){
            $(this).attr('selected','1');
        }
    });

    $('#publisher > option').each(function(){
        if($(this).val() == $("#publisher").attr('value')){
            $(this).attr('selected','1');
        }
    });

    $('#api > option').each(function(){
        if($(this).val() == $("#api").attr('value')){
            $(this).attr('selected','1');
        }
    });


    hasSeries_change();
    price_change();
    api_change();

    var source_url = $('#source_url').val();

    if(source_url == ""){
        document.getElementById("source_type").checked = false;
        document.getElementById("embed_type").checked = true;

        $("#source_url").attr('disabled' , '1');
        $("#embed_url_datagrid").removeAttr('disabled' );
        $("#embed_url_chart").removeAttr('disabled' );

    }
    else{
        document.getElementById("source_type").checked = true;
        document.getElementById("embed_type").checked = false;


        $("#embed_url_datagrid").attr('disabled' , '1');
        $("#embed_url_chart").attr('disabled' , '1');
        $("#source_url").removeAttr('disabled' );
    }
});
function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}


function price_change(){
    if($('#price_model').val() == "1"){
        $("#onetime_price").val("0");
        $("#download_price").val("0");
        $("#onetime_expires").val("9999");
        $("#download_expires").val("9999");

        $("#onetime_price").attr('readonly','true');
        $("#download_price").attr('readonly','true');
        $("#onetime_expires").attr('readonly','true');
        $("#download_expires").attr('readonly','true');

        //  $('#price_section').css('display','none');
    }
    else{
       // $("#onetime_price").val("10");
       // $("#download_price").val("10");
      //  $("#onetime_expires").val("100");
      //  $("#download_expires").val("100");

        $("#onetime_price").removeAttr('readonly');
        $("#download_price").removeAttr('readonly');
        $("#onetime_expires").removeAttr('readonly');
        $("#download_expires").removeAttr('readonly');
        //   $('#price_section').css('display','block');
    }
}


function api_change(){
    if($('#api').val() == "TIME-SERIES"){
        $("#embed_url_chart").prev().css('display' ,'block');
        $("#embed_url_chart").css('display' ,'block');
    }
    else{
        $("#embed_url_chart").prev().css('display' ,'none');
        $("#embed_url_chart").css('display' ,'none');
    }
}

function hasSeries_change(){
    var has_series = $('#has_series').val();
    if(has_series == "0"){
        $('#sourceUrl_field').css('display','block');
    }
    else{
        $('#sourceUrl_field').css('display','none');
    }
}



function check_number(event){

    if((event.keyCode >= 48 && event.keyCode <= 57) || event.keyCode == 46){
    }
    else{
        event.preventDefault();
    }
}
$('#btnUpdate').on('click',function(){
    var id = $('#data-id').val();
    var name = $('#data-name').val();
    var code = $('#code').val();
    var description = $('#description').val();
    var asset_class = $('#asset_class').val();
    var category = $('#category').val();
    if(category != 4){
        asset_class = 1;
    }
    var price_model = $('#price_model').val();
    var data_type = $('#data_type').val();

    var region = $('#region').val();
    var publisher = $('#publisher').val();
    var api = $('#api').val();
    var path = $("#file-path").val();

    var onetime_price = $('#onetime_price').val();
    var onetime_expires = $('#onetime_expires').val();

    var download_price = $('#download_price').val();
    var download_expires = $('#download_expires').val();
    var download_url = $('#download_url').val();
    var has_series = $('#has_series').val();
    var source_url = $('#source_url').val();

    var table_name = $("#table_name").val();
    var schema_name = $("#schema_name").val();

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


    var limitation = $("#limitation").val();
    if(limitation == "") limitation = 10;

    if(onetime_price == "") onetime_price = 0;
    if(onetime_expires == "") onetime_expires = 100;
    if(download_price == "") download_price = 0;
    if(download_expires == "") download_expires = 100;

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

    if(code.indexOf("/") >= 0){
        show_error("You can't input '/' in code field!");
        window.location.href="#";
        return;
    }

    if(code.length < 3){
        show_error("The minium code length is 3 !");
        window.location.href="#";
        return;
    }

    if(description == ""){
        show_error("You must input the Description Field!");
        window.location.href="#";
        return;
    }
    if(download_url == ""){
        show_error("You must input the Download Url Field!");
        window.location.href="#";
        return;
    }

    if(has_series == "0" && source_url == "" && embed_url_chart == "" && embed_url_datagrid == ""){
        show_error("You must input the Source Url Field!");
        window.location.href="#";
        return;
    }
    if(api == 'TABLES'){
        embed_url_chart = "";
    }

    if(embed_url_chart != "" ||  embed_url_datagrid != ""){
        source_url = "";
    }

    var generateFlag = $("#generateFlag").val();
    var download_url_update_at = $("#download_url_update_at").val();
    if(generateFlag == "1"){
        download_url_update_at = "1";
    }
    var bucket_name = $("#bucket_name").val();
    var s3_file_key = $("#s3_file_key").val();
    if(bucket_name == ""){
        show_error("You must input the Bucket Name!");
        window.location.href="#";
        return;
    }
    if(s3_file_key == ""){
        show_error("You must input the s3_file_key!");
        window.location.href="#";
        return;
    }

    $.ajax({
        url:'/admin/data_set/update',
        type:'POST',
        data:{

            id:id,
            name:name,
            price_model_id:price_model,
            asset_class_id: asset_class,
            data_type_id: data_type,
            region_id: region,
            publisher_id: publisher,
            icon: path,
            description: description,
            code:code,
            api:api,
            data_category_id:category,
            onetime_price:onetime_price,
            onetime_expires:onetime_expires,
            download_price:download_price,
            download_expires:download_expires,
            download_url:download_url,
            description_data:CKEDITOR.instances.editor1.getData(),
            has_series:has_series,
            source_url:source_url,
            table_name:table_name,
            schema_name:schema_name,
            embed_url_chart:embed_url_chart,
            embed_url_datagrid: embed_url_datagrid,
            limitation: limitation,
            download_url_update_at:download_url_update_at,
            bucket_name: bucket_name,
            s3_file_key: s3_file_key

        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/data_set/list"
            }
            else{
                show_error(data.errorInfo);
                window.location.href="#";
                return;
            }
        }

    });

});


$("#file input.file").change(function () {
    $('input[type="file"]').ajaxfileupload({
        'action': '/admin/data_set/upload',
        'onComplete': function(response) {
            //   $('#upload').hide();
            alert("File SAVED!!");
        },
        'onStart': function() {
            // $('#upload').show();
        }
    });
});
function getBase64(file) {
    var reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function () {
        console.log(reader.result);
    };
    reader.onerror = function (error) {
        console.log('Error: ', error);
    };
}
function fileupload () {


    $("#btWaiting").css('display','');

    var x = document.getElementById("file");
    var file = x.files[0];

    var formData = new FormData();
    formData.append('file', file);
    $.ajax({
        type: 'post',
        url: '/admin/data_set/upload',
        data: formData,
        processData: false,
        contentType: false,
        cache: false,
        success: function(response) {
            $("#btWaiting").css('display','none');
            if(response.resultCode == "success"){
                $("#upload-text").css('display','none');
                uploadedPath = response.errorInfo;
                $("#icon").attr('src',domain+"/" + uploadedPath);
                $("#file-path").val(uploadedPath);
            }
            else{
                $("#upload-text").css('display','block');
            }
        }
    });

}


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



$('#btnDownUrlGen').on('click',function() {
    var bucket_name = $("#bucket_name").val();
    var s3_file_key = $("#s3_file_key").val();
    if(bucket_name  == ""){
        alert("Please input bucket name!");
        return;
    }
    if(s3_file_key == ""){
        alert("Please input S3 file key!");
        return;
    }

    $.ajax({
        url:'/admin/data_set/generate_s3Url',
        type:'POST',
        data:{
            bucket_name: bucket_name,
            s3_file_key: s3_file_key
        },
        success: function(data){
            if(data.resultCode == "success"){
                $("#download_url").val(data.object);
                $("#generateFlag").val("1");
            }
            else{
                $("#download_url").val("");
            }
        }

    });


});


$('#btnUrlCopy').on('click',function() {

    var Url2=document.getElementById("download_url");
    Url2.select();
    document.execCommand("Copy");

});


$('#btnUrlOpen').on('click',function() {
    var download_url = $("#download_url").val();
    window.open(download_url, "_blank");
});

$('#category').on('change', function () {
    var category_value = $('#category').val();
    if(category_value == 4){
        $('#asset_classes').show();
    }else{
        $('#asset_classes').hide();
    }
}).trigger('change');
