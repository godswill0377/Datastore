var membership_type = "month";
var handler  , request_type;

$(document).ready(function() {
    var pubkey = $('#pubkey').val();
    handler  = StripeCheckout.configure({
        key: pubkey,
        image: 'https://stripe.com/img/documentation/checkout/marketplace.png',
        locale: 'auto',
        token: function(token) {
            swal({
                    title: "Payment",
                    text: "Please confim your payment!",
                    type: "info",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    showLoaderOnConfirm: true,
                },
                function(){

                    $.ajax({
                        url: '/account/membership/order',
                        type: 'POST',
                        data: 'mode=' + request_type + '&tokenid='+token.id,
                        success: function (result) {

                            if (result.resultCode == "success") {
                                swal({
                                    title: "Success!",
                                    text: "",
                                    timer: 2000,
                                    showConfirmButton: false
                                });
                                setTimeout(function(){
                                   window.location.href= "/account/membership";
                                },2000);
                            }
                            else {
                                swal({
                                    title: "Fail",
                                    text: "",
                                    timer: 2000,
                                    showConfirmButton: false
                                });
                                setTimeout(function(){
                                    window.location.href = "/";

                                },2000);
                            }
                        }
                });

            });
            // You can access the token ID with `token.id`.
            // Get the token ID to your server-side code for use.
        }
    });

    var type = $('#user_membership_type').val();
    switch (type){
        case "0":
            $('#free_month').addClass('active');
            $('#free_month > ul > li > i').addClass('active');
            $('#free_month .btn-u').addClass('active');
            $('#free_year').addClass('active');
            $('#free_year > ul > li > i').addClass('active');
            $('#free_year .btn-u').addClass('active');

            break;
        case "1":
            $('#m_single_type').addClass('active');
            $('#m_single_type > ul > li > i').addClass('active');
            $('#m_single_type .btn-u').addClass('active');
            break;
        case "2":
            $('#m_enterprise_type').addClass('active');
            $('#m_enterprise_type > ul > li > i').addClass('active');
            $('#m_enterprise_type .btn-u').addClass('active');
            break;
        case "3":
            $('#y_single_type').addClass('active');
            $('#y_single_type > ul > li > i').addClass('active');
            $('#y_single_type .btn-u').addClass('active');
            $('#annual-switch').click();
            break;
        case "4":
            $('#y_enterprise_type').addClass('active');
            $('#y_enterprise_type > ul > li > i').addClass('active');
            $('#y_enterprise_type .btn-u').addClass('active');
            $('#annual-switch').click();
            break;
    }
});


$('.btn-u').on('click',function(e){
    var cur_type = $('#user_membership_type').val();
    var type = $(this).attr('value');
    if(cur_type == type)
        return;
    if(type == "0" || type == 0){
        swal({
                title: "Are you sure to cancel your membership?",
                text: "Submit to cancel membership.",
                type: "info",
                showCancelButton: true,
                closeOnConfirm: false,
                showLoaderOnConfirm: true,
            },
            function(){
                $.ajax({
                    url:'/account/membership/order',
                    type:'POST',
                    data:'mode='+type,
                    success: function(result){
                        if(result.resultCode == "success"){
                            window.location.href = "/account/membership";
                        }
                        else{
                            window.location.href = "/";
                        }
                    }
                });
            });
    }
    else {

        request_type = type;
        var price = 0;
        switch(parseInt(type)){
            case 1:
                price = parseInt($('#m_single').val());
                break;
            case 2:
                price = parseInt($('#m_enterprise').val());
                break;
            case 3:
                price = parseInt($('#y_single').val());
                break;
            case 4:
                price = parseInt($('#y_enterprise').val());
                break;

        }
        handler.open({
            name: 'Payment',
            description: 'Please confirm to payment.',
            amount: price * 100
        });
        e.preventDefault();

    }
});
$('#annual-switch').on('click',function(){
    if(membership_type == "month") {
        $('.ToggleSwitch-handle').animate({
            left: '31px'
        }, 400);

        $('#to-monthly').css('color', 'black');
        $('#to-annual').css('color', '#139ff0');

        $('#month_membership').animate({
            opacity:0
        },300,function () {
            $('#month_membership').css('display','none');
            $('#year_membership').css('opacity','0');
            $('#year_membership').css('display','block');

            $('#year_membership').animate({
                opacity:1
            },300,function () {
            });
        });
        membership_type = "year";
    }
    else{
        $('.ToggleSwitch-handle').animate({
            left: '2px'
        }, 400);
        $('#to-monthly').css('color', '#139ff0');
        $('#to-annual').css('color', 'black');

        $('#year_membership').animate({
            opacity:0
        },300,function () {
            $('#year_membership').css('display','none');
            $('#month_membership').css('opacity','0');
            $('#month_membership').css('display','block');

            $('#month_membership').animate({
                opacity:1
            },300,function () {
            });
        });

        membership_type = "month";

    }
});

// membership.html

$('#btnCancel').on('click',function(){
    swal({
            title: "Are you sure to cancel your membership?",
            text: "Submit to cancel membership.",
            type: "info",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
        },
        function(){
            $.ajax({
                url:'/account/membership/order',
                type:'POST',
                data:'mode=0',
                success: function(result){
                    if(result.resultCode == "success"){

                        swal({
                            title: "Success!",
                            text: "",
                            timer: 2000,
                            showConfirmButton: false
                        });

                        setTimeout(function(){
                            window.location.href = "/account/membership";
                        }, 2000);
                    }
                    else{
                        window.location.href = "/";
                    }
                }
            });
        });
});

var pager = {page:1,start:0,limit:7,search_str:""};

$(function(){

    if($('#pagination').length) {
        $.ajax({
            type: 'GET',
            url: '/pager/purhcase/load',
            data: pager,
            success: function (data) {
                pager = data;

                $("#pagination").data("type", "article");

                if (pager.totalCount > 0) {
                    initPage(null);
                }
                else {
                    noresult();
                }
            }
        });
    }
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
        prev: '<li class="prev"><a href="javascript:;">Previous</a></li>',
        next: '<li class="next"><a href="javascript:;">Next</a></li>',
        page: '<li class="page"><a href="javascript:;">{{page}}</a></li>',
        onPageChange: function (num, type) {
            pager.page = num;
            var type = $("#pagination").data("type");

            loadList(type);
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
    var url = "";
    url = '/account/load/purchase';

    $.ajax({
        type: 'GET',
        url: url,
        data: pager,
        success: function (data) {
            $("#main-article").html(data);
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
        prev: '<li class="prev"><a href="javascript:;">Previous</a></li>',
        next: '<li class="next"><a href="javascript:;">Next</a></li>',
        page: '<li class="page"><a href="javascript:;">{{page}}</a></li>'
    });
    $("#main-article").html(document.getElementById("noresult").innerHTML);
}


function keydownevent(){
    searchList();

}


function searchList(){

    pager.search_str = $("#search_text").val();
    $.ajax({
        type: 'GET',
        url: '/pager/purhcase/load',
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
}



window.addEventListener('popstate', function() {
    handler.close();
});