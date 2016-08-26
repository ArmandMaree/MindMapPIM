// alert("")
$( window ).resize(function() {
    $("#centreText1").css('left',($(window).width()/2)- ($("#centreText1").width()/2));
    $("#centreText1").css('top',($(window).height()/2)- ($("#centreText1").height()/2));
    $("#centreText2").css('left',($(window).width()/2)- ($("#centreText2").width()/2));
    $("#centreText2").css('top',($(window).height()/2)- ($("#centreText2").height()/2));
    $("#centreText4").css('left',($(window).width()/2)- ($("#centreText4").width()/2));
    $("img").css('height',$(window).height()-250);
    // $(".carousel-caption").css('width',$(window).width()-550)
    

    // console.log($(".item").width());
    // if($("#centreText3").top == 150){

    // }
    // $("#centreText3").css('height',$(window).height()-500);

    // $("#centreText4").css('top',($(window).height()/5)- ($("#centreText4").height()/5));
    // $("#outline").css('width',$("#centreText3").width()+10);
    // $("#outline").css('height',$("#centreText3").height()+10);
    // $("#centreText3").css('top',($(window).height()/2)- ($("#centreText3").height()/2));
});
  
$(document).ready(function(){
    $('#myCarousel').carousel({
      interval: 20000
    })
    $("img").css('height',$(window).height()-250);
    
   // $(".carousel-caption").css('width',$(window).width()-550)
    // $(".carousel-caption").css('width',$(".item").width() )

    // $("#centreText3").css('width',$("#centreText3").height());
    // $("#outline").css('width',$("#centreText3").width()+100);
    // $("#outline").css('height',$("#centreText3").height()+100);

    $("#centreText1").fadeTo(0,0,function() {});
    $("#centreText2").fadeTo(0,0,function() {});
    $("#centreText3").fadeTo(0,0,function() {});
    $("#centreText4").fadeTo(0,0,function() {});
    $("#outline").fadeTo(0,0,function() {});
    $("#skip").fadeTo(0,0,function() {});

    $("#centreText1").css('left',($(window).width()/2)- ($("#centreText1").width()/2));
    $("#centreText1").css('top',($(window).height()/2)- ($("#centreText1").height()/2));
    $("#centreText2").css('left',($(window).width()/2)- ($("#centreText2").width()/2));
    $("#centreText2").css('top',($(window).height()/2)- ($("#centreText2").height()/2));
    $("#centreText4").css('left',($(window).width()/2)- ($("#centreText4").width()/2));
    // $("#centreText3").css('height',$(window).height()-500);

    // $("#centreText4").css('top',($(window).height()/5)- ($("#centreText4").height()/5));
    // $("#centreText3").css('left',($(window).width()/2)- ($("#centreText3").width()/2));
    // $("#centreText3").css('top',($(window).height()/2)- ($("#centreText3").height()/2));

    
    $("#centreText1").delay(3000).animate({
          opacity: '1'
    });
    $("#centreText1").delay(5000).animate({
          opacity: '0'
    });
    
    $("#centreText2").delay(9000).animate({
          opacity: '1'
    });
    $("#centreText2").delay(9000).animate({
          opacity: '0'
    });
    
    $("#centreText3").delay(19000).animate({
          opacity: '1'
    });
    $("#outline").delay(19000).animate({
          opacity: '1'
    });
    $("#centreText4").delay(19000).animate({
          opacity: '1'
    });
    setTimeout(function() {     
        $("#skip").animate({
          opacity: '1'
        });
    }, 40000);
});

// setInterval(function(){ $(".carousel-caption").css('width',$(".item").width() ) }, 500);