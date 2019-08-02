$(function() {

    $("#coupon-manage-li").addClass("active");
    $("#coupon-list-li").attr("class","active");
    // $("#user-list-li").parent().addClass("in");

    var page = $("#current-page").val();
    if (page == null || page == 0) {
        page = 1;
    }
    $.ajax({
        url: '/vendor/coupon/initPage',
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
        url : '/vendor/coupon/load',
        data : 'page='+page,
        success  : function(data) {
            $("#dataList").html(data);
        }
    });

}



// 删除
$("#dataList").on('click','.user-delete',function () {
    var id = $(this).parent().data("id");
    new $.flavr({
        content: 'Confirm?',
        buttons: {
            primary: {
                text: 'OK', style: 'primary', action: function () {
                    $.ajax({
                        url : '/vendor/coupon/delete',
                        method: "POST",
                        data:'id='+id,
                        success  : function(data) {
                            if(data.resultCode == 'success'){
                                autoCloseAlert(data.errorInfo,1000);
                                window.location.href = "/vendor/coupon/list";
                            }else{
                                autoCloseAlert(data.errorInfo,1000);
                            }
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

// 跳转编辑页
$("#dataList").on('click','.user-edit',function () {
    var id = $(this).parent().data("id");
    window.location.href= "/vendor/coupon/edit/"+id;
});

//ask and do
$("#dataList").on('click','.send-mail',function () {
    var id = $(this).parent().data("id");


    swal({
            title: "Are you sure?",
            text: "You will send coupon code to these recipients",
            type: "info",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
        },
        function(){

            $.ajax({
                url: '/vendor/marketing/sendmails',
                type: 'POST',
                data: {
                    id: id
                },
                success: function()
                {
                    swal({
                        title: "Success!",
                        text: "Your mail is sent to users.",
                        timer: 2000
                    });
                    setTimeout(function(){
                        window.location.href = "/vendor/marketing/list";
                    } , 2000);
                }
            });

        });


});



