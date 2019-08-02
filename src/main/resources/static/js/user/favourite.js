function unlike(element){
   var id =  $(element).parent().parent().parent().parent().attr('value');

   $.ajax({
       url:'/user/favourite/unlike/api',
       type:'POST',
       data:'id='+id,
       success:function(response){
           window.location.href= "/favourite";
       }
   });
}