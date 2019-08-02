$(function() {

    $("#trans-manage-li").addClass("active");
    $("#trans-list-li").attr("class","active");

 //   $("#trans-manage-li").addClass("active");
   // $("#dataset-review-list-li").attr("class","active");
   // $("#user-list-li").parent().addClass("in");

    var page = $("#current-page").val();
    if (page == null || page == 0) {
        page = 1;
    }
    $.ajax({
        url: '/vendor/transaction/initPage',
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
	var page = $("#current-page").text();

    if(!isEmpty(dateFrom) || dateFrom != ''){
        dateFrom =  dateFrom + " 00:00";
    }
    if(!isEmpty(dateTo) ||dateTo != ''){
        dateTo =  dateTo + " 24:00";
    }
	if(isEmpty(page) || page == 0){
		page = 1;
	}
	
	// 查询列表
	$.ajax({
        url : '/vendor/transaction/load',
        data : 'page='+page+"&dateFrom="+dateFrom+"&dateTo="+dateTo,
        success  : function(data) {
        	$("#dataList").html(data);
		}
    });

}


$("#tag-search").on('click',function () {
    var dateFrom = $("#date-from").val();
    var dateTo = $("#date-to").val();

    if(!isEmpty(dateFrom) || dateFrom != ''){
        dateFrom =  dateFrom + " 00:00";
    }
    if(!isEmpty(dateTo) ||dateTo != ''){
        dateTo =  dateTo + " 24:00";
    }
    $.ajax({
        url: '/vendor/transaction/initPage',
        data: {
            'page':1,
            'dateFrom':dateFrom,
            'dateTo': dateTo
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

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

