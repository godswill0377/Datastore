$(function() {

    $("#data-sales-manage-li").addClass("active");
    $("#data-membership-list-li").attr("class","active");


});

// 跳转编辑页
$("#dataList").on('click','.user-edit',function () {
    var id = $(this).parent().data("id");
    window.location.href= "/admin/membership/edit/"+id;
});
