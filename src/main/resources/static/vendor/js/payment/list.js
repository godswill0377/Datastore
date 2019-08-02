$(function() {

    $("#payment-manage-li").addClass("active");
   // $("#dataset-review-list-li").attr("class","active");
   // $("#user-list-li").parent().addClass("in");

    var page = $("#current-page").val();
    if (page == null || page == 0) {
        page = 1;
    }
    $.ajax({
        url: '/vendor/payment/initPage',
        data: 'page=' + page,
        success: function (data) {
            $("#total-num").text(data.totalCount);
            $("#total-page").text(data.totalPageNum);
            $("#current-page").text(data.page);
            if (data.totalCount > 0) {
                $.jqPaginator('#pagination', {
                    totalPages: data.totalPageNum,
                    visiblePages: 5,
                    currentPage: data.page,
                    prev: '<li class="prev"><a href="javascript:;">Previous</a></li>',
                    next: '<li class="next"><a href="javascript:;">Next</a></li>',
                    page: '<li class="page"><a href="javascript:;">{{page}}</a></li>',
                    onPageChange: function (num, type) {
                        // 加载管理员列表
                        $("#current-page").text(num);
                        loadList();
                    }
                });
            }
            else{
                $("#dataList").html("");
            }
        }
    });
});


// 加载管理员列表
function loadList(){
	// 收集参数
	var dateFrom = $("#date-from").val();
	var dateTo = $("#date-to").val();
    if(!isEmpty(dateFrom) || dateFrom != ''){
        dateFrom =  dateFrom + " 00:00";
    }
    if(!isEmpty(dateTo) ||dateTo != ''){
        dateTo =  dateTo + " 24:00";
    }
    var dayMode =$("#date-range").val();
	var page = $("#current-page").text();
	if(isEmpty(page) || page == 0){
		page = 1;
	}
	
	// 查询列表
	$.ajax({
        url : '/vendor/payment/load',
        data : 'page='+page+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&dayMode="+dayMode,
        success  : function(data) {
        	$("#dataList").html(data);
		}
    });

}


$("#tag-search").on('click',function () {
    var dateFrom = $("#date-from").val();
    var dateTo = $("#date-to").val();
    var dayMode =$("#date-range").val();
    if(!isEmpty(dateFrom) || dateFrom != ''){
        dateFrom =  dateFrom + " 00:00";
    }
    if(!isEmpty(dateTo) ||dateTo != ''){
        dateTo =  dateTo + " 24:00";
    }

    $.ajax({
        url: '/vendor/payment/initPage',
        data: {
            'page':1,
            'dateFrom':dateFrom,
            'dateTo': dateTo,
            'dayMode' : dayMode
        },
        success: function (data) {
            $("#total-num").text(data.totalCount);
            $("#total-page").text(data.totalPageNum);
            $("#current-page").text(data.page);
            if (data.totalCount > 0) {
                $.jqPaginator('#pagination', {
                    totalPages: data.totalPageNum,
                    visiblePages: 5,
                    currentPage: data.page,
                    prev: '<li class="prev"><a href="javascript:;">Previous</a></li>',
                    next: '<li class="next"><a href="javascript:;">Next</a></li>',
                    page: '<li class="page"><a href="javascript:;">{{page}}</a></li>',
                    onPageChange: function (num, type) {
                        $("#current-page").text(num);
                        loadList();
                    }
                });
            }
            else{
                $("#dataList").html("");
            }
        }
    });
});

$("#dataList").on('click','.user-edit',function () {
    var id = $(this).parent().data("id");
    window.location.href= "/vendor/payment/edit/"+id;
});

$('#date-range').on('change', function () {
    var date_range = $('#date-range').val();
    if(date_range == 6){
        $('label[for=date-from], input#date-from').show();
        $('label[for=date-to], input#date-to').show();
    }else{
        $('label[for=date-from], input#date-from').hide();
        $('label[for=date-to], input#date-to').hide();
    }

}).trigger('change');


