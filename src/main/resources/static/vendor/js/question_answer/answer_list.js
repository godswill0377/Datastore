$(function() {

    $("#question-answer-manage-li").addClass("active");
    $("#question-answer-list-li").attr("class","active");
   // $("#user-list-li").parent().addClass("in");

    var page = $("#current-page").val();
    if (page == null || page == 0) {
        page = 1;
    }
    var questionId = $("#questionId").val();
    $.ajax({
        url: '/vendor/dataset/question/answer/initPage',
        data: 'page=' + page+'&questionId='+questionId,
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
	var param = $("#keyword").val();
	var page = $("#current-page").text();
	var questionId = $("#questionId").val();
	if(isEmpty(page) || page == 0){
		page = 1;
	}

	// 查询列表
	$.ajax({
        url : '/vendor/dataset/question/answer/load',
        data : 'page='+page+"&name="+param+"&questionId="+questionId,
        success  : function(data) {
        	$("#dataList").html(data);
		}
    });

}



$("#tag-search").on('click',function () {
    $.ajax({
        url: '/vendor/dataset/question/answer/initPage',
        data: {
            'page':1,
            'search_str':$("#keyword").val()
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



// 删除
$("#dataList").on('click','.user-delete',function () {
    var id = $(this).parent().data("id");
    var questionId = $("#questionId").val();
    new $.flavr({
        content: 'Confirm?',
        buttons: {
            primary: {
                text: 'OK', style: 'primary', action: function () {
                    $.ajax({
                        url : '/vendor/dataset/question/answer/delete/'+id,
                        method: "GET",
                        success  : function(data) {
                            autoCloseAlert(data.errorInfo,1000);
                            window.location.href = "/vendor/dataset/question/answer/list/"+questionId;

                        }
                    });
                }
            },
            success: {
                text: 'Cancel', style: 'danger', action: function () {

                }
            }
        }
    });

});

// to edit page
$("#dataList").on('click','.user-edit',function () {
    var id = $(this).parent().data("id");
	window.location.href= "/vendor/dataset/question/answer/edit/"+id;
});
//to add page
$('#addAnswer').on('click',function(){
    var questionId = $("#questionId").val();
	window.location.href= "/vendor/dataset/question/answer/add/"+questionId;
});

