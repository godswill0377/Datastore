

$("#resetForm").submit(function(e)
{

    var email = $("#email").val();

    var validation = true;
    if(email == ""){
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
                $("#rg_btWaiting").css('display','none');
                $("#text-logined").html("If your email exists in our database, you will receive an email with a password recovery link.");
                $("#text-logined").css({'display':'block', 'background-color': 'red', 'color':'white'});
                setTimeout(function(){
                    window.location.href = "/vendor/login";
                },3500);

            }
        });
    }

    e.preventDefault();
});
