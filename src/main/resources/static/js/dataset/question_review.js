var pager = {page:1,start:0,limit:10,search_str:"",cateid:"1",parent_code:"" , is_sample_available:1,is_free:"0"};
var chart_param={id:0,data:""};
var filter_ar = [];
var base_url = "" ,chart_days=70;
var colors = ['#7cb5ec', '#434348', '#90ed7d', '#f7a35c', '#8085e9',
    '#f15c80', '#e4d354', '#2b908f', '#f45b5b', '#91e8e1'];
String.prototype.replaceAll  = function(s1,s2){
    return this.replace(new RegExp(s1,"gm"),s2);
};

$.cookie.json = true;


var chart;

$(function () {
    base_url = $("#base_url").val();
    pager.cateid = $('#cateid').val();

    $('.main-nav-link').each(function () {
        $(this).removeClass('active');
    });

    $('#cateid' + pager.cateid).addClass('active');

    var is_free = $('#is_free').val();
    if(is_free == "1"){
       // $('#btview_pricing').css('display','none');
        $('#filter_viewer').css('display','none');
    }
    else{
     //   $('#btview_pricing').css('display','block');
        $('#filter_viewer').css('display','block');
    }
    pager.is_free = is_free;
    pager.parent_code = $("#parent_code").val();

     //Hide the helpful div if makeHelpfulFlag equals 1

      var makeHelpfulFlags = $.cookie("makeHelpfulFlags");
      if (typeof makeHelpfulFlags === "undefined") {
        makeHelpfulFlags = new Array();
        $.cookie("makeHelpfulFlags",makeHelpfulFlags);
      }else{
        $.each( makeHelpfulFlags, function( key, item ) {
            if(item.flag==1)
                writeHelpfulDivHtml(item.reviewItemId);
        });
      }

        var makeVoteFlags = $.cookie("makeVoteFlags");
        if (typeof makeVoteFlags === "undefined") {
          makeVoteFlags = new Array();
          $.cookie("makeVoteFlags",makeVoteFlags);
        }
});


var index = 0;

// search question by content
$('#searchQuestionIcon').on('click',function(){

    var dataset_id = $('#dataset_id').val();
    var searchQuestion = $("#searchQuestion").val();

    var searchUrl = "/data_sets/questions";

    if( searchQuestion != ''){
        searchUrl = searchUrl + '?searchQuestion=' + encodeURI(searchQuestion) + '&dataset_id='+dataset_id;

       $("#questionListContent").load(searchUrl);
    }

});

$('#searchQuestion').keypress(function (e) {
     var key = e.which;
     if(key == 13)  // the enter key code
      {
        $("#searchQuestionIcon").click();
        return false;
      }
});

$('.btnVoteUp').on('click',function(){
    var questionItemId = $( this ).parent().children("input")[0].value;
    var votesInput = $( this ).parent().children("input")[1];
    var votesValue = $( this ).parent().children("span")[0];
    var votes = parseInt(votesInput.value) + 1;
    makeVote(questionItemId, votes,votesInput,votesValue);
});

$('.btnVoteDown').on('click',function(){
    var questionItemId = $( this ).parent().children("input")[0].value;
    var votesInput = $( this ).parent().children("input")[1];
    var votesValue = $( this ).parent().children("span")[0];
    var votes = parseInt(votesInput.value) -1;
    makeVote(questionItemId, votes,votesInput, votesValue);
});

function makeVote(questionItemId, votes, votesInput, votesValue){
      var voteFlag=0;
        //check vote flag
      var mvArray = $.cookie("makeVoteFlags");
      $.each( mvArray, function( key, item ) {
          if(item.questionItemId == questionItemId && item.flag==1){
                voteFlag=1;
                return false;
          }

      });
    if(voteFlag == 1)
        return;

    var voteUrl = "/data_sets/question/votes/change";

    $.ajax({
        type: 'POST',
        data: {
            'questionItemId' : questionItemId,
            'votes' : votes
        },
        url: voteUrl,
        success: function (data) {
            votesInput.value=votes;
            votesValue.innerHTML =votes;

            var makeVoteFlag = { questionItemId: questionItemId, flag: 1 };

            var mvArray = $.cookie("makeVoteFlags");
            if (typeof mhfarray === "undefined") {
                mvArray = new Array();
            }

            mvArray.push(makeVoteFlag);
            $.cookie("makeVoteFlags",mvArray);
        }
    });
}

$('#postQuestion').on('click',function(){
    var questionContent= $('#questionContent').val();
    var dataset_id = $('#dataset_id').val();
    var voteUrl = "/data_sets/question/create";

    if(questionContent == ""){
        show_error("You must input the Content Field!");
        $('#questionContent').focus();
        return;
    }

    $.ajax({
        type: 'POST',
        data: {
            'dataset_id' : dataset_id,
            'content' : questionContent
        },
        url: voteUrl,
        success: function (data) {
            reloadQuestionListContent();
        }
    });
});

/*$(document).on('show.bs.modal','#askQuestionModal', function () {

    $( this ).modal('hide');
    checkUser();
});*/

function checkUser(){
    var  checkUserUrl='/data_sets/question/checkUser';
    $.ajax({
        type: 'GET',
        url: checkUserUrl,
        success: function (data) {
             $( this ).modal('show');
        },
        error: function (x, status, error) {
            if (x.status == 403) {
               $("#btSignin").click();
            }
        }

    });
}

$('.helpful-yes-button').on('click',function(){
    var parent = $(this).parent().closest('div');
    var reviewItemId = parent.children("input")[0].value;
    var helpfulNumInput = parent.children("input")[1];
    var helpfulNum = parseInt(helpfulNumInput.value) +1;
    makeHelpful(reviewItemId, helpfulNum);
});

$('.helpful-no-button').on('click',function(){
     // just hide the div
    var parent = $(this).parent().closest('div');
    var reviewItemId = parent.children("input")[0].value;
    writeHelpfulDivHtml(reviewItemId);
});

function makeHelpful(reviewItemId, helpfulNum){
    var makeHelpfulUrl = "/data_sets/review/helpful/change";

    $.ajax({
        type: 'POST',
        data: {
            'reviewItemId' : reviewItemId,
            'helpfulNum' : helpfulNum
        },
        url: makeHelpfulUrl,
        success: function (data) {
            writeHelpfulDivHtml(reviewItemId);
        }
    });
}

function writeHelpfulDivHtml(reviewItemId){
       var divHtml ='<div id="fitRecommendationVoteThankYou" class="a-section a-spacing-mini a-popover-preload">';
       divHtml+= '<span class="a-color-success">';
       divHtml+='Thank you for your feedback.';
       divHtml+='</span>';
       divHtml+='</div>';

       $('#helpfulDiv-'+reviewItemId).html(divHtml);

        var makeHelpfulFlag = { reviewItemId: reviewItemId, flag: 1 };

        var mhfarray = $.cookie("makeHelpfulFlags");
        if (typeof mhfarray === "undefined") {
            mhfarray = new Array();
        }

        mhfarray.push(makeHelpfulFlag);
        $.cookie("makeHelpfulFlags",mhfarray);
}

$('#postReview').on('click',function(){
    var reviewTitle= $('#reviewTitle').val();
    var reviewContent= $('#reviewContent').val();
    var dataset_id = $('#dataset_id').val();
    var users_rating_stars = $('#users_stars').val();
    var postReviewUrl = "/data_sets/review/create";
    users_rating_stars = parseInt(users_rating_stars);

    if(reviewTitle == ""){
        show_error("You must input the Title Field!");
        $('#reviewTitle').focus();
        return;
    }

    if(reviewContent == "" ){
        show_error("You must input the Content Field!");
        $('#reviewContent').focus();
        return;
    }

    $.ajax({
        type: 'POST',
        data: {
            'dataset_id' : dataset_id,
            'title' : reviewTitle,
            'stars' : users_rating_stars,
            'content' : reviewContent
        },
        url: postReviewUrl,
        success: function (data) {
            reloadReviewListContent();
        },
        error: function (x, status, error) {
            alert("Sorry, has some problem! please refresh the page.");
        }
    });
});

/*
$(document).on('show.bs.modal','#reviewModal', function () {

    $( this ).modal('hide');

    checkUser();

});
*/

function reloadQuestionListContent(){
    var dataset_id = $('#dataset_id').val();
    var searchUrl = "/data_sets/questions";
    searchUrl = searchUrl + '?dataset_id='+dataset_id;
   $("#questionListContent").load(searchUrl);
}

function reloadReviewListContent(){
    var dataset_id = $('#dataset_id').val();
    var searchUrl = "/data_sets/reviews";
    searchUrl = searchUrl + '?dataset_id='+dataset_id;
   $("#reviewListContent").load(searchUrl);
}
