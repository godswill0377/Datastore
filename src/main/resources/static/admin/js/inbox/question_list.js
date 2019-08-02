$(function() {

    $("#inbox-question-answer-manage-li").addClass("active");
    $("#inbox-question-answer-list-li").attr("class","active");
   // $("#user-list-li").parent().addClass("in");

    var page = $("#current-page").val();
    if (page == null || page == 0) {
        page = 1;
    }
    $.ajax({
        url: '/admin/inbox/question/initPage',
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
	var param = $("#keyword").val();
	var page = $("#current-page").text();
	if(isEmpty(page) || page == 0){
		page = 1;
	}
	
	// 查询列表
	$.ajax({
        url : '/admin/inbox/question/load',
        data : 'page='+page+"&name="+param,
        success  : function(data) {
        	$("#dataList").html(data);

		}
    });

}



$("#tag-search").on('click',function () {
    $.ajax({
        url: '/admin/inbox/question/initPage',
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


//to add page
$('#addQuestion').on('click',function(){
    var questionId = $("#questionId").val();
    window.location.href= "/admin/inbox/question/add";
});



// jump to answer page
$("#dataList").on('click','.user-answer',function () {
    var questionId = $(this).parent().data("id");
	window.location.href= "/admin/inbox/question/answer/list/"+questionId;
});

$("#dataList").on('click','.user-edit',function () {
    var id = $(this).parent().data("id");
    window.location.href= "/admin/inbox/question/edit/"+id;
});


// 删除
$("#dataList").on('click','.user-delete',function () {
    var id = $(this).parent().data("id");
    new $.flavr({
        content: 'Confirm?',
        buttons: {
            primary: {
                text: 'OK', style: 'primary', action: function () {
                    $.ajax({
                        url : '/admin/inbox/question/delete',
                        method: "POST",
                        data: {
                            'id' : id
                        },
                        success  : function(data) {
                            autoCloseAlert(data.errorInfo,2000);
                            setTimeout(function(){
                                window.location.href = "/admin/inbox/question/list";

                            }, 2000);

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

