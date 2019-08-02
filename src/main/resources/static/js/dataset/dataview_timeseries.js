var dropshow = false;
var colors = ['#7cb5ec', '#434348', '#90ed7d', '#f7a35c', '#8085e9',
    '#f15c80', '#e4d354', '#2b908f', '#f45b5b', '#91e8e1' , '#ca5cf7' , '#f7eb5c' , '#f75c5c' ,'#5cd3f7', '#9ff75c', '#df5cf7'];
var base_url = "" , series_id="";
var webservice_err_content = "";
var tab_flag = false;   // false for chart and true for table
$('#btdownlist').on('click',function () {
    if($('#userid').val() == "0"){
        $("#btSignin").click();
        $("#btLogin").click();
        return;
    }

    if(!dropshow){
        $('#download_list').css('display','block');
    }
    else{
        $('#download_list').css('display','none');
    }
    dropshow = !dropshow;
});
$(function () {
    var cateid = $('#cateid').val();
    $('.main-nav-link').each(function () {
        $(this).removeClass('active');
    });

    $('#cateid' + cateid).addClass('active');

    base_url = $("#base_url").val();
    series_id = $("#data_set_series_id").val();
    var login_userid = $("#userid").val();
    var is_sample_available = $("#is_sample_available").val();
    var membership = parseInt($('#user_membership').val());
    var expired = parseInt($('#expired').val());

    if((login_userid != "0" && is_sample_available == "1") || (membership > 0 && expired == 0)) { // the user must logined and should be subsription.
        var source_type = $("#source_type").val();
        if(source_type == "1") {
            readTextFile(base_url + "/api/v3/datasets_series/" + series_id, function (text) {
                $("#error_alert").css('display', 'none');
                stockData = JSON.parse(text);
                createSeries();
                seriesNormal[0].visible = true;
                seriesChange[0].visible = true;
                seriesPercentageChange[0].visible = true;
                seriesCumulative[0].visible = true;
                currentSeries = seriesNormal;
                createChart(currentSeries);
                currentMinDate = chart.xAxis[1].min;
                currentMaxDate = chart.xAxis[1].max;

                $('#jqxFromDate').jqxDateTimeInput('setDate', new Date(currentMinDate));
                $('#jqxToDate').jqxDateTimeInput('setDate', new Date(currentMaxDate));
                $('#chart_jqxFromDate').jqxDateTimeInput('setDate', new Date(currentMinDate));
                $('#chart_jqxToDate').jqxDateTimeInput('setDate', new Date(currentMaxDate));


                //   document.getElementById('fromDate').value = timeConverter(currentMinDate);
                //   document.getElementById('toDate').value = timeConverter(currentMaxDate);

                updateTable();

                for (i = 0; i < stockData["dataset"]["column_names"].length; i++) {
                    var li_item = document.createElement("li");

                    var div_field = document.createElement("div");
                    div_field.className = "latest-value-label";
                    div_field.innerHTML = stockData["dataset"]["column_names"][i];

                    var div_val = document.createElement("div");
                    div_val.className = "latest-value";
                    div_val.innerHTML = stockData["dataset"]["newest_values"][i];

                    li_item.appendChild(div_field);
                    li_item.appendChild(div_val);

                    li_item.className = "latest-value-item";
                    document.getElementById("latest-values").appendChild(li_item);
                }

            }, function (err_txt) {
                webservice_err_content = err_txt;
                $("#first_waitingIcon").css('display', 'none');
                $("#error_alert").css('display', 'block');
            });
            $("#jqxToDate").jqxDateTimeInput({ width: '140px', height: '25px', formatString: 'yyyy-MM-dd'});
            $("#jqxFromDate").jqxDateTimeInput({ width: '140px', height: '25px', formatString: 'yyyy-MM-dd'});
            $("#chart_jqxToDate").jqxDateTimeInput({ width: '140px', height: '25px', formatString: 'yyyy-MM-dd'});
            $("#chart_jqxFromDate").jqxDateTimeInput({ width: '140px', height: '25px', formatString: 'yyyy-MM-dd'});
        }
    }

});

$('#modal-window').click(function () {
    var src = '';
    if(tab_flag){
        src = $('#table_url').val();
    }else{
        src = $('#chart_url').val();
    }
    $('#iframeModal').modal('show');
    $('#iframeModal iframe').attr('src', src);
    $('#iframeModal iframe').attr('height', screen.availHeight);
});

$('#tab-table-header').on('click',function(){
    $('.tabs__tab').each(function(){
       // $(this).removeClass('tabs__tab--active');
    });
    $(this).addClass('tabs__tab--active');
    $('#chart-tab').css('display','none');
    $('#table-tab').css('display','block');
    tab_flag = true;
});

$('#tab-chart-header').on('click',function(){
    $('.tabs__tab').each(function(){
     //   $(this).removeClass('tabs__tab--active');
    });
    $(this).addClass('tabs__tab--active');
    $('#chart-tab').css('display','block');
    $('#table-tab').css('display','none');
    tab_flag= false;
});


var stockData;
var seriesNormal = [];
var seriesChange = [];
var seriesPercentageChange = [];
var seriesCumulative = [];
var currentSeries;
var currentDataGrouping = {
    enabled: false
};
var totalColumns;
/* the drawn chart */
var chart;
var currentMinDate;
var currentMaxDate;
var isDataGrouped = false;

function createSeries() {
    currentVisibility = [];
    /* first create the normal series */
    for (i = 1; i < stockData["dataset"]["column_names"].length; i++) {

        var series = {
            name: stockData["dataset"]["column_names"][i],
            data: [],
            showInLegend: true,
            visible: false,
            dataGrouping: currentDataGrouping
        };
        seriesNormal.push(series);
    }
    totalColumns = stockData["dataset"]["column_names"].length;

    stockData["dataset"]["data"].forEach(function(data) {
        /*
         * Get the date in ms
         */
        var date = new Date(data[0]);
        var milliseconds = date.getTime();

        /* add series to the seriesNormal */
        for (i = 1; i < data.length; i++) {
            xy = [milliseconds, parseFloat(data[i].toFixed(2))];
            seriesNormal[i - 1].data.push(xy)
        }
    });

    /* create all the other transform series */

    function transform(series, transformType) {
        var data = series.data;
        var changeData = [];
        var percentAgeChangeData = [];
        var cumulativeData = [];
        var prevY = data[0][1];
        var yCumulative = 0;

        for (i = 0; i < data.length; i++) {
            x = data[i][0];
            yChange = data[i][1] - prevY;
            yPercentageChange = 0.00;
            if (prevY != 0) {
                yPercentageChange = ((yChange * 100.00) / prevY);
            }
            yCumulative += data[i][1];
            prevY = data[i][1];
            changeData.push([x, parseFloat(yChange.toFixed(2))]);
            percentAgeChangeData.push([x, parseFloat(yPercentageChange.toFixed(2))]);
            cumulativeData.push([x, parseFloat(yCumulative.toFixed(2))]);
        }

        /* add to the change series */
        var changeSeries = {
            name: series.name,
            data: changeData,
            showInLegend: true,
            visible: false,
            dataGrouping: currentDataGrouping
        };

        seriesChange.push(changeSeries);

        /* add to the percentage change series */
        var percentChangeSeries = {
            name: series.name,
            data: percentAgeChangeData,
            showInLegend: true,
            visible: false,
            dataGrouping: currentDataGrouping
        };

        seriesPercentageChange.push(percentChangeSeries);

        /* add to the cumulative  series */
        var cumulativeSeries = {
            name: series.name,
            data: cumulativeData,
            showInLegend: true,
            visible: false,
            dataGrouping: currentDataGrouping
        };

        seriesCumulative.push(cumulativeSeries);
    }

    seriesNormal.forEach(function(series) {
        transform(series);
    });
}


function createChart(seriesToDraw) {

    chart = Highcharts.stockChart('chart_container', {

        chart: {
            events: {
                load: function() {
                    document.getElementById('first_waitingIcon').style.visibility = "hidden";
                    document.getElementById('transform_chart').style.visibility = "visible";
                    document.getElementById('date_chart').style.visibility = "visible";
                    document.getElementById('latest_view').style.visibility = "visible";

                    document.getElementById('waitingIcon').style.visibility = "hidden";
                    $("#chart_selection").css('display','block');
                },
                redraw: function() {
                    document.getElementById('waitingIcon').style.visibility = "hidden";
                },
            },
        },
        scrollbar: {
            enabled: false
        },
        rangeSelector: {
            verticalAlign: 'bottom',
            selected: 5
        },
        xAxis: {
            events: {
                setExtremes: function(e) {
                    if (e.min) {
                        $('#jqxToDate').jqxDateTimeInput('setDate', new Date(currentMaxDate));
                        $('#chart_jqxToDate').jqxDateTimeInput('setDate', new Date(currentMaxDate));

                //        document.getElementById('fromDate').value = timeConverter(e.min);
                        currentMinDate = e.min;
                    }
                    if (e.max) {
                         currentMaxDate = e.max;


                        $('#jqxFromDate').jqxDateTimeInput('setDate', new Date(currentMinDate));
                        $('#chart_jqxFromDate').jqxDateTimeInput('setDate', new Date(currentMinDate));

                    //    document.getElementById('toDate').value = timeConverter(e.max);
                    }
                    updateTable();
                }
            }
        },
        yAxis: {
            opposite: false,
            softMin: 0,
            tickInterval: 50
        },

        plotOptions: {
            series: {
                compare: 'percent',
                showInNavigator: true,
                events: {
                    legendItemClick: function() {
                        var visibility = this.visible ? 'visible' : 'hidden';
                        var color = colors[this.index];
                        if(this.visible ){
                            $('#main_selitem'+this.index).css('color',color);
                            $('#main_selitem'+this.index).css('background-color','white');
                            $('#circle_selitem'+this.index).css('background-color',color);

                        }
                        else{
                            $('#main_selitem'+this.index).css('color','white');
                            $('#main_selitem'+this.index).css('background-color',color);
                            $('#circle_selitem'+this.index).css('background-color','white');
                        }


                        /* we need to updateTable after the data has been updated */
                        document.getElementById('waitingIcon').style.visibility = "visible";
                        /* update the visibility imediately after this */
                        setTimeout(function() {
                            for (i = 0; i < seriesNormal.length; i++) {
                                seriesNormal[i].visible = chart.series[i].visible;
                                seriesChange[i].visible = chart.series[i].visible;
                                seriesPercentageChange[i].visible = chart.series[i].visible;
                                seriesCumulative[i].visible = chart.series[i].visible;
                            }
                        }, 1);

                        setTimeout(updateTable, 1000);
                    }
                }
            },
        },
        exporting: {
            enabled: false
        },

        legend: {
            enabled: true,
            layout: 'horizontal',
            align: 'center',
            verticalAlign: 'top',
            y: 30,
            labelFormatter: function () {
                var html = "";
                if(this["index"] == 0){
                    html = '<div id="main_selitem'+this["index"]+'"  style="color:white;background-color:'+colors[this["index"]]+';" class="chart_item-main"><div  id="circle_selitem'+this["index"]+'" style="background-color: white" class="chart_item-circle"></div><span class="chart_item-text">' + this.name+'</span></div>';
                }

                else{
                    html = '<div id="main_selitem'+this["index"]+'"  style="color:'+colors[this["index"]]+'" class="chart_item-main"><div  id="circle_selitem'+this["index"]+'" style="background-color: '+colors[this["index"]]+'" class="chart_item-circle"></div><span class="chart_item-text">' + this.name+'</span></div>';
                }
                return html;
            },
            useHTML:true,
            symbolWidth:0
        },

        tooltip: {
            pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b> ({point.change}%)<br/>',
            valueDecimals: 2,
            split: true
        },

        series: seriesToDraw,
    });
}

/* called when a transform option is selected */
function onTransformSelected(index) {
    document.getElementById('waitingIcon').style.visibility = "visible";
    $("select#transform_chart1").prop('selectedIndex', index);
    $("select#transform_chart").prop('selectedIndex', index);

    setTimeout(function() {
        chart.destroy();
        if (0 == index) {
            currentSeries = seriesNormal;
        } else if (1 == index) {
            currentSeries = seriesChange;
        } else if (2 == index) {
            currentSeries = seriesPercentageChange;
        } else if (3 == index) {
            currentSeries = seriesCumulative;
        }
        createChart(currentSeries);
        updateTable();
    }, 1)
}

/* called when a transform option is selected */
function onDataGroupingChange(index) {
    document.getElementById('waitingIcon').style.visibility = "visible";

    $("select#date_chart1").prop('selectedIndex', index);
    $("select#date_chart").prop('selectedIndex', index);


    isDataGrouped = true;
    setTimeout(function() {
        if (0 == index) {
            currentDataGrouping = {
                enabled: false
            };
        } else
        if (1 == index) {
            currentDataGrouping = {
                enabled: false
            };
        } else if (2 == index) {
            currentDataGrouping = {
                approximation: 'average',
                enabled: true,
                units: [
                    ['week', [1]]
                ]
            };
        } else if (3 == index) {
            currentDataGrouping = {
                approximation: 'average',
                enabled: true,
                units: [
                    ['month', [1]]
                ]
            }
        } else if (4 == index) {
            currentDataGrouping = {
                approximation: 'average',
                enabled: true,
                units: [
                    ['month', [3]]
                ]
            }
        } else if (5 == index) {
            currentDataGrouping = {
                approximation: 'average',
                enabled: true,
                units: [
                    ['year', [1]]
                ]
            }
        }


        chart.series.forEach(function(ser) {
            ser.update({
                    dataGrouping: currentDataGrouping
                },
                false);
        });

        /* update the other series also which are not shown */
        seriesNormal.forEach(function(ser) {
            ser["dataGrouping"] = currentDataGrouping;
        });

        seriesChange.forEach(function(ser) {
            ser["dataGrouping"] = currentDataGrouping;
        });

        seriesPercentageChange.forEach(function(ser) {
            ser["dataGrouping"] = currentDataGrouping;
        });

        seriesCumulative.forEach(function(ser) {
            ser["dataGrouping"] = currentDataGrouping;
        });

        chart.redraw();

        updateTable();

    }, 1)
}
function updateTable() {
    /* make all the series visible for the purpose of retriving grouped data */
    var currentVisibility = [];
    if (isDataGrouped) {
        currentSeries.forEach(function(e, i) {
            currentVisibility.push(e.visible);
            chart.series[i].setVisible(true, false);
        });
        chart.redraw();
    }

    var tableContainer = document.getElementById('tableContainer');
    tableContainer.innerHTML = '';
    var tbl = document.createElement('table');
    tbl.style.border = "1px solid #000"
    tbl.style.width = '100%';
    var tbdy = document.createElement('tbody');

    var trArray = [];
    var rowLength = chart.series[0].data.length;
    if (rowLength == 0) {
        rowLength = chart.series[0].groupedData.length;
    }

    /* add header */
    var tr = document.createElement('tr');
    var td = document.createElement('td');
    td.style.textAlign = 'center';
    td.style.backgroundColor = '#e4e9ee';
    td.style.padding = '8px';
    td.style.border = '1px solid #dcdcdc';
    td.appendChild(document.createTextNode("Date"));
    tr.appendChild(td);
    for (k = 0; k < totalColumns - 1; k++) {
        var td = document.createElement('td');
        td.style.textAlign = 'center';
        td.style.backgroundColor = '#e4e9ee';
        td.style.padding = '8px';
        td.style.border = '1px solid #dcdcdc';
        td.appendChild(document.createTextNode(chart.series[k].name));
        tr.appendChild(td);
    }
    tbl.appendChild(tr);

    for (i = 0; i < rowLength; i++) {
        var tr = document.createElement('tr');
        for (j = 0; j < totalColumns - 1; j++) {
            var data = chart.series[j].data;
            if (data.length == 0) {
                data = chart.series[j].groupedData;
            }


            if (data && (data.length > i)) {
                /* range selector */
                if ((data[i].x < currentMinDate) || (data[i].x > currentMaxDate)) {
                    continue;
                }
            } else if (j == 0) {
                continue;
            }

            if (j == 0) {
                /* append date */
                var td = document.createElement('td');
                td.style.textAlign = 'center';
                td.style.padding = '7px 5px 0px';
                td.style.border = '1px solid #dcdcdc';
                td.appendChild(document.createTextNode(timeConverter(data[i].x)));
                tr.appendChild(td);
            }

            val = 0.00;
            if (data && data[i]) {
                val = parseFloat(data[i].y.toFixed(2));
            }
            var td = document.createElement('td');
            td.appendChild(document.createTextNode(val));
            td.style.textAlign = 'right';
            td.style.padding = '7px 5px 0px';
            td.style.border = '1px solid #dcdcdc';
            tr.appendChild(td);
        }

        tbdy.insertBefore(tr, tbdy.firstChild);
    }

    tbl.appendChild(tbdy);
    tableContainer.appendChild(tbl)

    if (isDataGrouped) {
        currentVisibility.forEach(function(e, i) {
            chart.series[i].setVisible(e, false);
        });
        chart.redraw();
    }
}
function timeConverter(msTime) {
    var a = new Date(msTime);
   var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    var year = a.getUTCFullYear();
    var month = months[a.getUTCMonth()];
    var date = a.getUTCDate();
    var hour = a.getUTCHours();
    var min = a.getUTCMinutes();
    var sec = a.getUTCSeconds();
    var time = date + ' ' + month + ' ' + year; // + ' ' + hour + ':' + min + ':' + sec;
    return time ;
}
function timeConverter1(msTime) {
    var a = new Date(msTime);
    var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    var year = a.getUTCFullYear();
    var month = a.getUTCMonth();
    var date = a.getUTCDate();
    var hour = a.getUTCHours();
    var min = a.getUTCMinutes();
    var sec = a.getUTCSeconds();
    var time = year + '-' + month + '-' + date; // + ' ' + hour + ':' + min + ':' + sec;
    return time;
}
var btnState = false;

function toggleTableVisibility(btn) {
    if (btnState) {
        btn.innerHTML = "Show Table"
        document.getElementById("tableContainer").style.visibility = "hidden";
    } else {
        btn.innerHTML = "Hide Table"
        document.getElementById("tableContainer").style.visibility = "visible";
    }
    btnState = !btnState;
}

function readTextFile(file, callback,error_callback) {

    var rawFile = new XMLHttpRequest();
    rawFile.withCredentials = false;

    rawFile.overrideMimeType("application/json");
    rawFile.open("GET", file, true);
    rawFile.onreadystatechange = function() {

        if (rawFile.readyState === 4 && rawFile.status == "200") {
            var res = JSON.parse(rawFile.responseText);
            if(res.status == "success"){
                callback(rawFile.responseText);
            }
            else{
                error_callback(res.error);
            }
        }
        else{
      //
        }
    }
    rawFile.onerror = function() {
        error_callback("The website server issue. ");
    }

    rawFile.send(null);
}
function onToDateChange(e, input) {
    var code = (e.keyCode ? e.keyCode : e.which);
    if (code == 13) { //Enter keycode
        if (Date.parse(input.value)) {
            var date = new Date(input.value);
            var milliseconds = date.getTime() - (date.getTimezoneOffset() * 60 * 1000);
            var min = chart.xAxis[0].setExtremes.min;
            chart.xAxis[0].setExtremes(min, milliseconds);
            chart.xAxis[1].setExtremes(min, milliseconds);
            chart.redraw();
        } else {

        }
    }
}

function onFromDateChange(e, input) {
    var code = (e.keyCode ? e.keyCode : e.which);
    if (code == 13) { //Enter keycode

        if (Date.parse(input.value)) {
            var date = new Date(input.value);
            var milliseconds = date.getTime() - (date.getTimezoneOffset() * 60 * 1000);
            var max = chart.xAxis[0].setExtremes.max;
            chart.xAxis[0].setExtremes(milliseconds, max);
            chart.xAxis[1].setExtremes(milliseconds, max);
            chart.redraw();
        } else {

        }
    }
}

// sign up
$('#btnCreateFreeAccount').on('click',function(){
    $("#btSignin").click();
    $("#btSignup").click();

});
//Log in
$('#btnLoginIn').on('click',function(){
    $("#btSignin").click();
});


$('#li-favourite').on('click',function(){
    if($('#userid').val() == "0"){
        $("#btSignin").click();
        $("#btLogin").click();
        return;
    }

    $.ajax({
        url:'/user/favourite/api',
        type:'POST',
        data:{
            id:$("#data_set_series_id").val()
        },
        success: function(data){
            if(data.resultCode == "success"){

                var classname = $('#i_heart_favour').attr('class');
                if(classname.indexOf('fa fa-heart-o') >= 0) {
                    $('#i_heart_favour').removeClass('fa fa-heart-o').addClass('fa fa-heart');
                }
                else{
                    $('#i_heart_favour').removeClass('fa fa-heart').addClass('fa fa-heart-o');
                }
            }
            else{
                if($('#userid').val() == "0"){
                    $("#btSignin").click();
                    $("#btLogin").click();
                    return;
                }
            }
        }
    });

});


$('#li-details').on('click',function(){

    if($('#userid').val() == "0"){
        $("#btSignin").click();
        $("#btLogin").click();
        return;
    }

    var user_apikey = $("#user_apikey").val();
    var zeppelin_url = $("#zeppelin_url").val();
    var key_data = $("#key_data").val();
    var enc_data = $("#enc_data").val();
    var series_id = $("#data_set_series_id").val();
    var filter_query = $("#filter_query").val();
    var notename = $("#notename").val();

    var params =  "apikey=" +user_apikey + "&dec_key=" + key_data + "&enc_key=" + enc_data
        + "&data_id=" + series_id+"&filter_query="+filter_query +"&notename="+notename + "&login=1";


    window.open( zeppelin_url + "?" + encodeURI(params), '_blank');

});

$('#btview_pricing').on('click',function(){
    var userid = $('#userid').val();
    var cateid = $("#cateid").val();
    var parentcode = $('#parent_code').val();

    /*if(userid == "0"){
        $("#btSignin").click();
        return;
    }*/
    window.location.href = "/data_sets/cateid/" + cateid +"/details/"  + parentcode + "/purchase";

});

function searchDate(){
        var fromdate =  $('#jqxFromDate').jqxDateTimeInput('getDate');
         var todate =  $('#jqxToDate').jqxDateTimeInput('getDate');

        var from_mile = fromdate.getTime() ;
        var to_mile = todate.getTime() ;
        if(to_mile <= from_mile){
            return;
        }

        chart.xAxis[0].setExtremes(from_mile, to_mile);
    chart.xAxis[1].setExtremes(from_mile, to_mile);
        chart.redraw();
}
function searchChartDate(){
    var fromdate =  $('#chart_jqxFromDate').jqxDateTimeInput('getDate');
    var todate =  $('#chart_jqxToDate').jqxDateTimeInput('getDate');

    var from_mile = fromdate.getTime() ;
    var to_mile = todate.getTime() ;
    if(to_mile <= from_mile){
        return;
    }

    chart.xAxis[0].setExtremes(from_mile, to_mile);
    chart.xAxis[1].setExtremes(from_mile, to_mile);
    chart.redraw();
}

function openEror(){
    sweetAlert(webservice_err_content);
}

function iframeLoad(){
    setTimeout(function(){
        var apikey = $("#user_apikey").val();
        var username = $("#login_username").val();
        if(username == "" || username == "0"){
            return ;
        }
        $.ajax({
            url: '/user/ssoauth',
            type: 'POST',
            data:{
                'username' : username,
                'apikey' : apikey
            },
            success: function(){ }
        });
    }, 2000);

}