var membership_type = "month";
var handler, request_type;

$(document).ready(function () {
    var type = $('#current_plan_type').val();
    var upcoming = $('#upcoming_plan_type').val();
    var m_single_id = $('#m_single_id').val();
    var m_enterprise_id = $('#m_enterprise_id').val();
    var y_single_id = $('#y_single_id').val();
    var y_enterprise_id = $('#y_enterprise_id').val();



    if (type == 0 && upcoming == 0) {
        if(type == 0){
        $('#free_month').parent().parent().addClass('active-clr');
        $('#free_year').parent().parent().addClass('active-clr');}
        if(upcoming == 0){
            $('#free_month').parent().parent().addClass('selected-up');
            $('#free_year').parent().parent().addClass('selected-up');
        }
    } else if (type == m_single_id || upcoming == m_single_id) {
        if (type == m_single_id) {
            $('#m_single_type').parent().parent().addClass('active-clr');
        }
        if (upcoming == m_single_id) {
            $('#m_single_type').parent().parent().addClass('continue-plane');
        }
    }
    else if (type == m_enterprise_id || upcoming == m_enterprise_id) {
        if(type == m_enterprise_id) {
            $('#m_enterprise_type').parent().parent().addClass('active-clr');
        }if (upcoming == m_enterprise_id) {
            $('#m_enterprise_type').parent().parent().addClass('continue-plane');
        }
    } else if (type == y_single_id || upcoming == y_single_id) {
        if(type == y_single_id) {
            $('#y_single_type').parent().parent().addClass('active-clr');
        }
        if (upcoming == y_single_id) {
            $('#y_single_type').parent().parent().addClass('continue-plane');
        }
        $('#annual-switch').click();
    } else if (type == y_enterprise_id || upcoming == y_enterprise_id) {
        if(type == y_enterprise_id) {
            $('#y_enterprise_type').parent().parent().addClass('active-clr');
        }
        if (upcoming == y_enterprise_id) {
            $('#y_enterprise_type').parent().parent().addClass('continue-plane');
        }
        $('#annual-switch').click();
    }

// switch (type % 4) {
//     case 1:
//         $('#m_single_type').parent().parent().addClass('active-clr');
//         if (parseInt(upcoming) >= 0) {
//             $('#m_single_type').parent().parent().addClass('continue-plane');
//         }
//         break;
//     case 2:
//         $('#m_enterprise_type').parent().parent().addClass('active-clr');
//         if (parseInt(upcoming) >= 0) {
//             $('#m_enterprise_type').parent().parent().addClass('continue-plane');
//         }
//         break;
//     case 3:
//         $('#y_single_type').parent().parent().addClass('active-clr');
//         if (parseInt(upcoming) >= 0) {
//             $('#y_single_type').parent().parent().addClass('continue-plane');
//         }
//         $('#annual-switch').click();
//         break;
//     case 0:
//         $('#y_enterprise_type').parent().parent().addClass('active-clr');
//         if (parseInt(upcoming) >= 0) {
//             $('#y_enterprise_type').parent().parent().addClass('continue-plane');
//         }
//         $('#annual-switch').click();
//         break;
// }
// }

    //
    // if (upcoming == m_single_id) {
    //     $('#m_single_type').parent().parent().addClass('selected-up');
    //
    // } else if (upcoming == m_enterprise_id) {
    //     $('#m_enterprise_type').parent().parent().addClass('selected-up');
    //
    // } else if (upcoming == y_single_id) {
    //     $('#y_single_type').parent().parent().addClass('selected-up');
    //
    // } else if (upcoming == y_enterprise_id) {
    //     $('#y_enterprise_type').parent().parent().addClass('selected-up');
    //
    // }

//     {
//     switch (upcoming % 4) {
//         case 1:
//             $('#m_single_type').parent().parent().addClass('selected-up');
//             break;
//         case 2:
//             $('#m_enterprise_type').parent().parent().addClass('selected-up');
//             break;
//         case 3:
//             $('#y_single_type').parent().parent().addClass('selected-up');
//             break;
//         case 0:
//             $('#y_enterprise_type').parent().parent().addClass('selected-up');
//             break;
//     }
// }

})
;


$('.bttnCstmP').on('click', function (e) {
    //var cur_type = $('#user_membership_type').val();
    var cur_type = $('#current_plan_type').val();

    var type = $(this).attr('value');
    console.log(type);
    if (cur_type == type)
        return;
    if (type == null || typeof type == 'undefined') {
        return;
    }

    if (type == "0" || type == 0) {
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
                        'mode': type,
                        'updown': cur_type < type
                    },
                    success: function (result) {
                        if (result.resultCode == "success") {
                            window.location.href = "/account/active";
                        }
                        else {
                            swal(result.errorInfo);
                          //  window.location.href = "/";
                        }
                    },
                    error: function (result) {
                        swal('Please contact administrator. Stripe Configuration has some error');
                    }
                });
                /*
                    $.ajax({
                        url:'/account/membership/order',
                        type:'POST',
                        data:'mode='+type ,
                        success: function(result){
                            if(result.resultCode == "success"){
                                window.location.href = "/account/membership";
                            }
                            else{
                                window.location.href = "/";
                            }
                        }
                    });
                */
            });
    }
    else {
        $.ajax({
            url: '/account/membership/order',
            type: 'POST',
            data: 'mode=' + type,
            success: function (result) {

                if (result.resultCode == "success") {
                    window.location.href = "/payment/" + result.object;
                }
                else {
                    if (result.errorInfo == 'customerExist') {
                        swal({
                                title: cur_type < type ? "Are you sure to upgrade your membership?" : "Are you sure to downgrade your membership?",
                                text: cur_type < type ? "Submit to upgrade membership." : "Submit to downgrade membership.",
                                type: "info",
                                showCancelButton: true,
                                closeOnConfirm: false,
                                showLoaderOnConfirm: true,
                            },
                            function () {
                                $.ajax({
                                    url: '/account/membership/change',
                                    type: 'POST',
                                    //data:'mode='+type ,
                                    data: {
                                        'mode': type,
                                        'updown': true
                                    },
                                    success: function (result) {
                                        if (result.resultCode == "success") {
                                            window.location.href = "/account/active";
                                        }
                                        else {
                                            swal(result.errorInfo);
//                                            window.location.href = "/";
                                        }
                                    }
                                });
                            });
                    }
                    else {
                        window.location.href = "/";
                    }
                }
            },
            error: function (result) {
                swal('Please contact administrator. Stripe Configuration has some error');
            }
        });


    }
});

$('.labelContinue').on('click', function (e) {
    var type = $('#upcoming_plan_type').val();
    console.log(type);
    swal({
            title: "Are you sure to continue your membership?",
            text: "Submit to continue membership.",
            type: "info",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
        },
        function () {
            $.ajax({
                url: '/account/membership/continue',
                type: 'POST',
                data: 'id='+type,
                success: function (result) {
                    if (result.resultCode == "success") {
                        window.location.href = "/account/active";
                    }
                    else {
                        window.location.href = "/";
                    }
                },
                error: function (result) {
                    swal('Please contact administrator. Stripe Configuration has some error');
                }

            });
        }
    );
});

$('#annual-switch').on('click', function () {
    if (membership_type == "month") {

        $('#month_membership').animate({
            opacity: 0
        }, 300, function () {
            $('#month_membership').css('display', 'none');
            $('#year_membership').css('opacity', '0');
            $('#year_membership').css('display', 'block');

            $('#year_membership').animate({
                opacity: 1
            }, 300, function () {
            });
        });
        membership_type = "year";
    }
    else {

        $('#year_membership').animate({
            opacity: 0
        }, 300, function () {
            $('#year_membership').css('display', 'none');
            $('#month_membership').css('opacity', '0');
            $('#month_membership').css('display', 'block');

            $('#month_membership').animate({
                opacity: 1
            }, 300, function () {
            });
        });

        membership_type = "month";

    }
});

