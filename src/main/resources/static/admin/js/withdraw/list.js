$(function() {

    $("#withdraw-manage-li").addClass("active");
    $("#withdraw-list-li").attr("class","active");
   // $("#user-list-li").parent().addClass("in");

    var page = $("#current-page").val();
    if (page == null || page == 0) {
        page = 1;
    }
    $.ajax({
        url: '/admin/withdraw/initPage',
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
	var page = $("#current-page").text();
	if(isEmpty(page) || page == 0){
		page = 1;
	}
	
	// 查询列表
	$.ajax({
        url : '/admin/withdraw/load',
        data : 'page='+page,
        success  : function(data) {
        	$("#dataList").html(data);
		}
    });

}

$("#dataList").on('click','.user-edit',function () {
    var id = $(this).parent().data("id");
    window.location.href= "/admin/withdraw/edit/"+id;
});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}

$("#dataList").on('click','.withdraw-cancel',function () {
    var id = $(this).parent().data("id");

    swal({
            title: "Are you sure?",
            text: "Reason To Disapprove This Withdraw!",
            type: "input",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
            inputPlaceholder: "Write Reason of Disapproval"
        },
        function(inputValue){
                if (inputValue === false) return ;

                if (inputValue === "") {
                    swal.showInputError("You need to write Disapproval Reason!");
                    return;
                }
            $.ajax({
                url: '/admin/withdraw/cancel/' + id,
                type: 'POST',
                data: {
                    id: id,
                    description:inputValue
                },
                success: function()
                {
                    swal({
                        title: "Success!",
                        text: "This Withdraw Request has been disapproved.",
                        timer: 2000
                    });
                    setTimeout(function(){
                        window.location.href = "/admin/withdraw/list";
                    } , 2000);
                }
            });

        });
});

$("#dataList").on('click','.withdraw-approve',function () {
    var id = $(this).parent().data("id");

    swal({
            title: "Are you sure?",
            text: "Sure To Approve This Withdraw!",
            type: "info",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
        },
        function(){

            $.ajax({
                url: '/admin/withdraw/approve/' + id,
                type: 'POST',
                data: {
                    id: id
                },
                success: function(data)
                {

                    if(data.resultCode == "success"){
                        swal({
                            title: "Success!",
                            text: "",
                            timer: 2000
                        });
                        setTimeout(function(){
                            window.location.href = "/admin/withdraw/list";
                        } , 2000);
                    }
                    else{
                        swal(data.errorInfo);
                        return;
                    }


                }
            });

        });
});

