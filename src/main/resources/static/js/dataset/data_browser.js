var pager = {page:1,start:0,limit:10,cateid:"1",search_str:"" , price_model_ids: "", asset_class_ids: "", data_type_ids: "", region_ids: "", publisher_ids: ""};
var filter_ar = [];

String.prototype.replaceAll  = function(s1,s2){
    return this.replace(new RegExp(s1,"gm"),s2);
}

$(function () {

    pager.cateid = $('#cateid').val();

    $('.main-nav-link').each(function () {
        $(this).removeClass('active');
    });

    $('#cateid' + pager.cateid).addClass('active');

    $('#container').css({'background-color':'#edf1f5','height':'auto'});
     $('body').addClass('loaded');
            $.ajax({
                type: 'GET',
                url: '/pager/data_sets/load',
                data:pager,
                success: function (data){
                    pager = data;

                    $("#pagination").data("type","article");

                    if (pager.totalCount > 0){
                        initPage(null);
                    }
                    else{
                        noresult();
                    }
                }
        });
});

$("#search_text").on('change paste keyup' , function(){
//   var text=  $("#search_text").val();
    searchList();
});

function check_clearall_visible(){
    var len = $("#browser_filter_list").children().length;
    if(len == 0){
        $("#browser_clear_all").css('display',"none");
    }
    else{
        $("#browser_clear_all").css('display',"block");
    }
}

function append_ids(ids , val){
    if(ids.indexOf(val+",") < 0){
        ids += val + ",";
        return ids;
    }
    else{
        return false;
    }
}

function remove_ids(ids , val){
    return ids.replace(val+",","");
}

$("#browser_clear_all").on('click',function () {
    $('#browser_filter input[type=checkbox]').each(function () {
         this.checked = false;
    });
    $('#browser_assetclass input[type=checkbox]').each(function () {
        this.checked = false;
    });
    $('#browser_datatype input[type=checkbox]').each(function () {
        this.checked = false;
    });
    $('#browser_region input[type=checkbox]').each(function () {
        this.checked = false;
    });
    $('#browser_publisher input[type=checkbox]').each(function () {
        this.checked = false;
    });

    pager.price_model_ids = "";
    pager.asset_class_ids = "";
    pager.publisher_ids = "";
    pager.region_ids = "";
    pager.data_type_ids = "";

    document.getElementById("browser_filter_list").innerHTML = "";
    check_clearall_visible();
    searchList();
});
function replaceStr(str){
    var text = str.replaceAll(" ","_");
    text = text.replaceAll("&","_");
    text = text.replaceAll(/\./g,"");
   return text;
}
function remove_filter(ids,id , type){
    var text = filter_ar[ids+""];
    if( $("#" +replaceStr(text)).length){
        $("#" +replaceStr(text)).remove();
        if(type == 1){
            pager.price_model_ids = remove_ids(pager.price_model_ids,id);

            $('#browser_filter input[type=checkbox]').each(function () {
                if($(this).val() == text){
                    this.checked = false;
                }
            });
        }
        if(type == 2){
            pager.asset_class_ids = remove_ids(pager.asset_class_ids,id);

            $('#browser_assetclass input[type=checkbox]').each(function () {
                if($(this).val() == text){
                    this.checked = false;
                }
            });
        }

        if(type == 3){

            pager.data_type_ids = remove_ids(pager.data_type_ids,id);

            $('#browser_datatype input[type=checkbox]').each(function () {
                if($(this).val() == text){
                    this.checked = false;
                }
            });
        }

        if(type == 4){
            pager.region_ids = remove_ids(pager.region_ids,id);

            $('#browser_region input[type=checkbox]').each(function () {
                if($(this).val() == text){
                    this.checked = false;
                }
            });
        }
        if(type == 5){
            pager.publisher_ids = remove_ids(pager.publisher_ids,id);

            $('#browser_publisher input[type=checkbox]').each(function () {
                if($(this).val() == text){
                    this.checked = false;
                }
            });
        }
    }
    check_clearall_visible();
    searchList();
}
//todo
$(".ais-refinement-list--checkbox").on('change' , function()
{
    $('#browser_filter input[type=checkbox]').each(function () {
        var text = "1"+ $(this).prev().val() ;
        filter_ar[text] = $(this).val();
        if(this.checked){
            var result = append_ids(pager.price_model_ids,$(this).prev().val());
            if(result) {

                pager.price_model_ids = result;

                var item = '<div id="' +replaceStr($(this).val())+'" class="ais-current-refined-values--item tag">';
                    item += '<a class="ais-current-refined-values--link" href="javascript: remove_filter(1'+$(this).prev().val()+','+$(this).prev().val() + ',1);">';
                        item += '<div>' + $(this).val() + '</div>';
                    item += '</a>';
                item += '</div>';
                $("#browser_filter_list").append(item);
            }
        }
        else{

            var text = replaceStr($(this).val());
            if(text != "") {
                if ($("#" + text).length) {
                    pager.price_model_ids = remove_ids(pager.price_model_ids, $(this).prev().val());

                    $("#" + replaceStr($(this).val())).remove();
                }
            }
        }
    });

    $('#browser_assetclass input[type=checkbox]').each(function () {
        var text = "2"+ $(this).prev().val() ;
        filter_ar[text] = $(this).val();
        if(this.checked){
            var result = append_ids(pager.asset_class_ids,$(this).prev().val());
            if(result) {

                pager.asset_class_ids = result;

                var item = '<div id="' +replaceStr($(this).val())+'" class="ais-current-refined-values--item tag">';
                item += '<a class="ais-current-refined-values--link" href="javascript: remove_filter(2'+$(this).prev().val()+','+$(this).prev().val() + ',2);">';
                item += '<div>' + $(this).val() + '</div>';
                item += '</a>';
                item += '</div>';
                $("#browser_filter_list").append(item);
            }
        }
        else{

            var text = replaceStr($(this).val());
            if(text != "") {
                if ($("#" + text).length) {
                    pager.asset_class_ids = remove_ids(pager.asset_class_ids, $(this).prev().val());

                    $("#" + replaceStr($(this).val())).remove();
                }
            }
        }
    });


    $('#browser_datatype input[type=checkbox]').each(function () {
        var text = "3"+ $(this).prev().val() ;
        filter_ar[text] = $(this).val();
        replaceStr($(this).val());
        if(this.checked){
            var result = append_ids(pager.data_type_ids,$(this).prev().val());
            if(result) {
                pager.data_type_ids = result;

                var item = '<div id="' +replaceStr($(this).val())+'" class="ais-current-refined-values--item tag">';
                item += '<a class="ais-current-refined-values--link" href="javascript: remove_filter(3'+$(this).prev().val()+','+$(this).prev().val() + ',3);">';
                item += '<div>' + $(this).val() + '</div>';
                item += '</a>';
                item += '</div>';
                $("#browser_filter_list").append(item);
            }
        }
        else{
            var text = replaceStr($(this).val());
            if(text != "") {
                if ($("#" + text).length) {
                    pager.data_type_ids = remove_ids(pager.data_type_ids, $(this).prev().val());

                    $("#" + replaceStr($(this).val())).remove();
                }
            }
        }
    });


    $('#browser_region input[type=checkbox]').each(function () {
        var text = "4"+ $(this).prev().val() ;
        filter_ar[text] = $(this).val();
        if(this.checked){
            var result = append_ids(pager.region_ids,$(this).prev().val());
            if(result) {

                pager.region_ids = result;

                var item = '<div id="' +replaceStr($(this).val())+'" class="ais-current-refined-values--item tag">';
                item += '<a class="ais-current-refined-values--link" href="javascript: remove_filter(4'+$(this).prev().val()+','+$(this).prev().val() + ',4);">';
                item += '<div>' + $(this).val() + '</div>';
                item += '</a>';
                item += '</div>';
                $("#browser_filter_list").append(item);
            }
        }
        else{
            var text = replaceStr($(this).val());
            if(text != "") {
                if ($("#" + text).length) {
                    pager.region_ids = remove_ids(pager.region_ids, $(this).prev().val());

                    $("#" + replaceStr($(this).val())).remove();
                }
            }
        }
    });


    $('#browser_publisher input[type=checkbox]').each(function () {
        var temp = $(this).prev().val();
        if(temp == "-1"){
            temp = "0";
        }
        var text = "5"+ temp ;
        filter_ar[text] = $(this).val();
        if(this.checked){
            var result = append_ids(pager.publisher_ids,$(this).prev().val());
            if(result) {
                console.log(result);

                pager.publisher_ids = result;

                var item = '<div id="' +replaceStr($(this).val())+'" class="ais-current-refined-values--item tag">';
                item += '<a class="ais-current-refined-values--link" href="javascript: remove_filter(5'+temp+','+$(this).prev().val() + ',5);">';
                item += '<div>' + $(this).val() + '</div>';
                item += '</a>';
                item += '</div>';
                $("#browser_filter_list").append(item);
            }
        }
        else{
            var text = replaceStr($(this).val());
            if(text != "") {
                if ($("#" + text).length) {
                    pager.publisher_ids = remove_ids(pager.publisher_ids, $(this).prev().val());

                    $("#" + replaceStr($(this).val())).remove();
                }
            }
        }
    });
    check_clearall_visible();

    searchList();
});

function noresult(){
    $("#total-num").text(1);
    $("#total-page").text(1);
    $("#current-page").text(1);

    $.jqPaginator('#pagination', {
        totalPages: 1,
        totalCounts: 1,
        visiblePages: 5,
        currentPage: 1,
        prev: '<li class="page-item"><a href="javascript:;"><i class="fa fa-angle-left" aria-hidden="true"></i></a></li>',
        next: '<li class="page-item"><a href="javascript:;"><i class="fa fa-angle-right" aria-hidden="true"></i></a></li>',
        page: '<li class="page-item"><a href="javascript:;">{{page}}</a></li>'
    });
    $("#main-article").html(document.getElementById("noresult").innerHTML);
}
function searchList(){

    pager.price_model_itr = null;
    pager.asset_class_itr = null;
    pager.data_type_itr = null;
    pager.region_itr = null;
    pager.publisher_itr = null;

    pager.search_str = $("#search_text").val();
    $.ajax({
        type: 'GET',
        url: '/pager/data_sets/load',
        data:pager,
        success: function (data){
            pager = data;

            $("#pagination").data("type","article");
            if (pager.totalCount > 0){
                initPage(null);
            }
            else{
                noresult();
            }
            refreshing = false;

        }
    });
}

function initPage(id) {

    $("#total-num").text(pager.totalCount);
    $("#total-page").text(pager.totalPageNum);
    $("#current-page").text(pager.page);
    $.jqPaginator('#pagination', {
        totalPages: pager.totalPageNum,
        totalCounts: pager.totalCount,
        visiblePages: 5,
        currentPage: pager.page,
        prev: '<li class="page-item"><a href="javascript:;"><i class="fa fa-angle-left" aria-hidden="true"></i></a></li>',
        next: '<li class="page-item"><a href="javascript:;"><i class="fa fa-angle-right" aria-hidden="true"></i></a></li>',
        page: '<li class="page-item"><a href="javascript:;">{{page}}</a></li>',
        onPageChange: function (num, type) {
            pager.page = num;
            var type = $("#pagination").data("type");

            loadList(type,id);
            // 当前第几页
            $("#current-page").text(num);
            $(".chosen-select").chosen({
                max_selected_options: 5,
                no_results_text: "没有找到",
                allow_single_deselect: true
            });
            $(".chosen-select").trigger("liszt:updated");
        }
    });
}


function  loadList(type,id) {
    var url = "";
    url = '/data_sets/load/data_sets';
    pager.price_model_itr = null;
    pager.asset_class_itr = null;
    pager.data_type_itr = null;
    pager.region_itr = null;
    pager.publisher_itr = null;

    $.ajax({
        type: 'GET',
        url: url,
        data: pager,
        success: function (data) {
            $("#main-article").html(data);
            $('#loader-wrapper .load_title').remove();
        }
    });
}
