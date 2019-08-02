

$("#resetForm").submit(function(e)
{
    $("#text-logined").css('display' , 'none');
    $("#text-logined").html("");
    var reset_password = $("#reset_password").val();
    var reset_confirmpassword = $("#reset_confirmpassword").val();

    var validation = true;
    if(reset_password == ""){
        validation  = false;
    }
    if(reset_confirmpassword != reset_password){
        $("#text-logined").css('display' , 'block');
        $("#text-logined").html("The passwords are different.");
        validation  = false;
    }

    $("#text-logined").html("");
    if(validation){
        $("#rg_btWaiting").css('display','');

        var postData = $(this).serializeArray();
        var formURL = $(this).attr("action");

        $.ajax({
            url : formURL,
            type: "POST",
            data : postData,
            success:function(data, textStatus, jqXHR)
            {
                $("#text-logined").css('display' , 'block');

                $("#rg_btWaiting").css('display','none');
                if (data.resultCode == "success") {
                    $("#text-logined").html("Success!");
                    setTimeout(function(){
                        window.location.href = "/vendor/dashboard";
                    },2000);
                }
                else{
                    if(data.errorInfo == "expire"){
                        $("#text-logined").html("Your token is expired. Please try again!");
                    }
                    else{
                        $("#text-logined").html("You have some problem to access. Please try again!");
                    }
                    setTimeout(function(){
                        $("#text-logined").css('display' , 'none');
                        $("#text-logined").html("");
                    },2000);
                }

            }
        });
    }

    e.preventDefault();
});
