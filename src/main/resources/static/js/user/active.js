var pager = {page:1,start:0,limit:10};

$(function(){
    $('body').addClass('loaded');
    $.ajax({
        type: 'GET',
        url: '/account/load/initActivePage',
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

            loadList();
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

function  loadList() {
    var url = '/account/load/active';
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
    $("#main-article").html("");
}

$("#main-article").on('click','.subscribe-cancel',function () {
    var id = $(this).parent().data("id");
    swal({
            title: "Are you sure to cancel your membership?",
            text: "Submit to cancel membership.",
            type: "info",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
        },
        function () {
            $.ajax({
                url: '/account/membership/change',
                type: 'POST',
                data: {
                    'mode': id,
                    'updown': false
                },
                success: function (result) {
                    if (result.resultCode == "success") {
                        window.location.href = "/account/past";
                    }
                    else {
                        swal(result.errorInfo);
//                         window.location.href = "/";
                    }
                },
                error: function (result) {
                    swal('Please contact administrator. Stripe Configuration has some error');
                }
            });
        });
});