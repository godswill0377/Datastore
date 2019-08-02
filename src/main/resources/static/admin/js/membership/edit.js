$(function(){

    $("#data-sales-manage-li").addClass("active");
    $("#data-membership-list-li").attr("class","active");

});

$('#btnUpdate').on('click',function(){
    var id = $('#data-id').val();
    var m_single = $('#m_single').val();
    var m_single_value = $('#m_single_value').val();
    var m_enterprise = $('#m_enterprise').val();
    var m_enterprise_value = $('#m_enterprise_value').val();
    var y_single = $('#y_single').val();
    var y_single_value = $('#y_single_value').val();
    var y_enterprise = $('#y_enterprise').val();
    var y_enterprise_value = $('#y_enterprise_value').val();


    if(m_single == ""){
        show_error("You must input the Mothly Single Price Field!");
        $('#m_single').focus();
        return;
    }

    if(m_single_value == ""){
        show_error("You must input the Mothly Single Value Field!");
        $('#m_single_value').focus();
        return;
    }
    if(m_enterprise == ""){
        show_error("You must input the Mothly Enterprise Price Field!");
        $('#m_enterprise').focus();
        return;
    }
    if(m_enterprise_value == ""){
        show_error("You must input the Mothly Enterprise Value Field!");
        $('#m_enterprise_value').focus();
        return;
    }

    if(y_single == ""){
        show_error("You must input the Yearly Single Price Field!");
        $('#m_single').focus();
        return;
    }

    if(y_single_value == ""){
        show_error("You must input the Yearly Single Value Field!");
        $('#m_single_value').focus();
        return;
    }
    if(y_enterprise == ""){
        show_error("You must input the Yearly Enterprise Price Field!");
        $('#m_enterprise').focus();
        return;
    }
    if(y_enterprise_value == ""){
        show_error("You must input the Yearly Enterprise Value Field!");
        $('#m_enterprise_value').focus();
        return;
    }

    $.ajax({
        url:'/admin/membership/update',
        type:'POST',
        data:{
            id:id,
            m_single:m_single,
            m_enterprise:m_enterprise,
            y_single:y_single,
            y_enterprise:y_enterprise,
            m_single_value:m_single_value,
            m_enterprise_value:m_enterprise_value,
            y_single_value:y_single_value,
            y_enterprise_value:y_enterprise_value
        },
        success: function(data){
            if(data.resultCode == "success"){
                window.location.href = "/admin/membership/list"
            }
            else{
                show_error(data.errorInfo);
                return;
            }
        }

    });

});

function show_error(text) {
    $('.callout-danger > p').text(text);
    $('.callout-danger').css('display','block');
}