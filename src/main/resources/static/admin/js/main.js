
function sidebar_item(element){

    $('.treeview-menu').each(function(){
        if($(this).parent().attr('class').indexOf('active') >= 0){
            $(this).parent().removeClass('active');

            $(this).animate({
                height:0
            },500);

            setTimeout(function(){
                $(this).css({'display':'none','overflow':'hidden'});

            },600);
        }
    });

    var treemenu = $(element).find('.treeview-menu');

    var height = treemenu.children().length * 32;
      treemenu.css({'display':'block','overflow':'hidden','height':'0'});
      treemenu.animate({
          height:height
      },500);

    $(element).addClass('active');
}