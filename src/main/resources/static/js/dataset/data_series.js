var pager = {page:1,start:0,limit:10,search_str:"",cateid:"1",parent_code:"" , is_sample_available:1,is_free:"0"};
var chart_param={id:0,data:""};
var filter_ar = [];
var base_url = "" ,chart_days=70;
var colors = ['#7cb5ec', '#434348', '#90ed7d', '#f7a35c', '#8085e9',
    '#f15c80', '#e4d354', '#2b908f', '#f45b5b', '#91e8e1'];
String.prototype.replaceAll  = function(s1,s2){
    return this.replace(new RegExp(s1,"gm"),s2);
};

$.cookie.json = true;

var stockData;
var seriesNormal = [];

var currentSeries;
var currentDataGrouping = {
    enabled: false
};
var chart;

$(function () {
    chart_days = $("#chart_days").val();
    base_url = $("#base_url").val();
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
    $('body').addClass('loaded');
            $.ajax({
                type: 'GET',
                url: '/pager/data_sets_series/load',
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

     //Hide the helpful div if makeHelpfulFlag equals 1

      var makeHelpfulFlags = $.cookie("makeHelpfulFlags");
      if (typeof makeHelpfulFlags === "undefined") {
        makeHelpfulFlags = new Array();
        $.cookie("makeHelpfulFlags",makeHelpfulFlags);
      }else{
        $.each( makeHelpfulFlags, function( key, item ) {
            if(item.flag==1)
                writeHelpfulDivHtml(item.reviewItemId);
        });
      }

        var makeVoteFlags = $.cookie("makeVoteFlags");
        if (typeof makeVoteFlags === "undefined") {
          makeVoteFlags = new Array();
          $.cookie("makeVoteFlags",makeVoteFlags);
        }
});
function keydownevent(){
    searchList();

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

function replaceStr(str){
    var text = str.replaceAll(" ","_");
    text = text.replaceAll("&","_");
   return text;
}

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
    if($('#noresult').length){
        $("#main-article").html(document.getElementById("noresult").innerHTML);
    }
}

function searchList(){

    pager.search_str = $("#search_text").val();
    $.ajax({
        type: 'GET',
        url: '/pager/data_sets_series/load',
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
var index = 0;
/*将加载文章,文章分类,标签分类重构成一个方法*/

function  loadList(type,id) {
    var url = "";
    url = '/data_sets/load/data_sets_series';

    $.ajax({
        type: 'GET',
        url: url,
        data: pager,
        success: function (data) {
            $("#main-article").html(data);
            $('#loader-wrapper .load_title').remove();

            $('.update_datas').each(function () {
                var sid =  $(this).attr('id');
                var today = new Date();
                var dd = today.getDate();
                var mm = today.getMonth()+1; //January is 0!
                var yyyy = today.getFullYear();

                if(dd<10) {
                    dd='0'+dd
                }

                if(mm<10) {
                    mm='0'+mm
                }

                today = yyyy + "-" + mm+'-'+dd;

                if(today != $(this).val()){

                    stockData  ="";
                    seriesNormal = [];

                    currentSeries = "";
                    currentDataGrouping = {
                        enabled: false
                    };

                    var flag = false;

                    if(flag){
                        readTextFile($("#base_url").val() + "/api/v3/datasets_series/" + sid, function (text)
                        {

                            stockData = JSON.parse(text);
                            if(stockData["dataset"] != null){
                                createSeries();
                                seriesNormal[0].visible = true;
                                currentSeries = seriesNormal;
                                createChart(currentSeries);

                                var svg = chart.getSVG();
                                var pos0 = svg.indexOf("highcharts-series-group", 0);
                                var pos = svg.indexOf('<path', pos0 + 1);
                                var pos2 = svg.indexOf('highcharts-graph', pos + 21);
                                var str = svg.substring(pos + 21, pos2 - 9);

                                chart_param.id = sid;
                                chart_param.data = svg;

                                $.ajax({
                                    type: 'POST',
                                    url: '/api/v3/update_chart',
                                    data: chart_param,

                                    success: function (data) {

                                    }
                                });
                            }
                        });
                    }

                }
            });
            $('.preview_diagram').each(function () {
                if($(this).val() != ""){
                    $(this).next().html($(this).val());
                }
            });
        }
    });
}

$("#main-article").on('click','.article-tag-link',function () {
    var tagId = $(this).data("id");
    var loadPager = {page:1,start:0,limit:10};
    $.ajax({
        type: 'GET',
        url: '/pager/tags/' + tagId,
        data: loadPager,
        success: function (data) {
            pager = data;
            $("#pagination").data("type","tags");
            initPage(tagId);
        }
    });
})

/*文章归档点击事件*/
$(".archive-list-link").on('click',function () {
    var createTime = $(this).data("id");
    var count  = $(this).next().text();
    pager.totalCount = parseInt(count);
    pager.totalPageNum = Math.floor(count/pager.limit)+1;
    pager.page = 1;
    $("#pagination").data("type","createTime");
    initPage(createTime);
})
/*文章分类点击事件*/
$(".category-list-link").on('click',function () {
    var categoryId = $(this).data("id");
    var loadPager = {page:1,start:0,limit:10};
    $.ajax({
        type: 'GET',
        url: '/pager/categories/' + categoryId,
        data: loadPager,
        success: function (data) {
            pager = data;
            $("#pagination").data("type","categories");
            initPage(categoryId);
        }
    });
})

/*为动态元素绑定lick事件*/
$("#main-article").on('click','.article-category-link',function () {
    var categoryId = $(this).data("id");
    var loadPager = {page:1,start:0,limit:10};
    $.ajax({
        type: 'GET',
        url: '/pager/categories/' + categoryId,
        data: loadPager,
        success: function (data) {
            pager = data;
            $("#pagination").data("type","categories");
            initPage(categoryId);
        }
    });
});

$('#all_view').on('click',function () {

   $(this).attr('class','allFilter');
   $('#sample_view').attr('class','sampleFilter');
   pager.is_sample_available = 2;
    searchList();
});

$('#sample_view').on('click',function () {
    $(this).attr('class','allFilter');
    $('#all_view').attr('class','sampleFilter');


    pager.is_sample_available = 1;
    searchList();
});

$(".js-dropdown-trigger").on('click',function () {

    var visible = $(this).next().css('display');
    if(visible == "block"){
        $(this).next().css( "display", "none" );
    }
    else{
        $(this).next().css( "display", "block" );
    }
});

$(".b-button--tiny").on('click',function(){
    var type = $(this).html();
    var value = $(this).attr('value');

    $('.b-button--tiny').each(function(){
        if(value == $(this).attr('value')){
            $(this).removeClass('active');
        }
    });
    $(this).addClass('active');

    var link = document.getElementById("link"+value).innerHTML;
    var type1 = type;
    if(type == "CSV"){
        link = link.replace("json","csv");
        link = link.replace("xml","csv");
        type1 = "csv";
    }
    if(type == "JSON"){
        link = link.replace("csv","json");
        link = link.replace("xml","json");
        type1 = "json";

    }
    if(type == "XML"){
        link = link.replace("csv","xml");
        link = link.replace("json","xml");
        type1 = "xml";

    }
    document.getElementById("link"+value).innerHTML = link;

    var url = base_url;
    url += "/api/v3/datatables/"+type1+"/"+value;
    document.getElementById("link"+value).href = url;
});


///////////////////////////////////////////////////////////////////////////////////////////////////////


function createSeries() {
    //    alert(stockData);
    /* first create the normal series */

    for (i = 1; i < stockData["dataset"]["column_names"].length; i++) {
        var series = {
            name: stockData["dataset"]["column_names"][i],
            data: [],
            showInLegend: true,
            visible: false
        };
        seriesNormal.push(series);
    }

    stockData["dataset"]["data"].forEach(function(data) {
        /*
         * Get the date in ms
         */
        var date = new Date(data[0]);
        var milliseconds = date.getTime();

        /* add series to the seriesNormal */
        for (i = 1; i < data.length; i++) {
            xy = [milliseconds, round(data[i], 2)];
            seriesNormal[i - 1].data.push(xy)
        }
    });

}

function createChart(seriesToDraw) {

    chart = Highcharts.stockChart('chart_container', {
        chart: {
            events: {
                load: function() {
                    this.xAxis[0].setExtremes(this.xAxis[0].max - chart_days * 24 * 3600 * 1000, this.xAxis[0].max);
                },
            } ,
            width:100,
            height:100
        },
        rangeSelector: {
            verticalAlign: 'bottom',
            selected: 5,
            inputEnabled: false,
            buttonTheme: {
                visibility: 'hidden'
            },

            labelStyle: {
                visibility: 'hidden'
            }
        },
        navigator: {
            enabled: false
        },
        xAxis:{
            labels:
            {
                enabled: false
            },
            lineWidth: 0,
            minorGridLineWidth: 0,
            lineColor: 'transparent',
            minorTickLength: 0,
            tickLength: 0,
            gridLineWidth: 0,
            minorGridLineWidth: 0
        },
        yAxis: {
            opposite: false,
            tickInterval: 50,
            plotLines: [{
                value: 0,
                width: 2,
                color: 'silver'
            }],
            labels:
            {
                enabled: false
            },
            gridLineWidth: 0,
            minorGridLineWidth: 0
        },
        scrollbar: {
            enabled: false
        },
        exporting: {
            enabled: false
        },
        series: seriesToDraw,
    });
}

function round(value, exp) {
    if (typeof exp === 'undefined' || +exp === 0)
        return Math.round(value);

    value = +value;
    exp = +exp;

    if (isNaN(value) || !(typeof exp === 'number' && exp % 1 === 0))
        return NaN;

    // Shift
    value = value.toString().split('e');
    value = Math.round(+(value[0] + 'e' + (value[1] ? (+value[1] + exp) : exp)));

    // Shift back
    value = value.toString().split('e');
    return +(value[0] + 'e' + (value[1] ? (+value[1] - exp) : -exp));
}

function readTextFile(file, callback) {


    var rawFile = new XMLHttpRequest();
    rawFile.withCredentials = false;

    rawFile.overrideMimeType("application/json");
    rawFile.open("GET", file, true);
    rawFile.onreadystatechange = function() {
        if (rawFile.readyState === 4 && rawFile.status == "200") {
            callback(rawFile.responseText);
        }
    }
    rawFile.send(null);
}
$('#btview_pricing').on('click',function(){
    var userid = $('#userid').val();
    var cateid = $("#cateid").val();
    var parentcode = $('#parent_code').val();

/*    if(userid == "0"){
        $("#btSignin").click();
        return;
    }*/
    window.location.href = "/data_sets/cateid/" + cateid +"/details/"  + parentcode + "/purchase";

});
