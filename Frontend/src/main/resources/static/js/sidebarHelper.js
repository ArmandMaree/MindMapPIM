/**
*   A function that initialilise the sidebar to prepare it for the insertion of items
*   @param items JSON object that holds the item to populate with
*/
function initialiliseSidebar(items){
    if($(window).width()<=768){
        $("#backfromsidebar").html("<a class='navbar-brand' onclick='hidesidebar()'><span  style='position:fixed;width:30px;height:30px;top:16px;left:-0px;cursor:pointer;padding:5px' class='glyphicon glyphicon-chevron-left' src=''/></a><p class='navbar-text' onclick='hidesidebar()' style='cursor:pointer'>Back</p>")
    }else{
        $("#backfromsidebar").html(navbarReloadTextCondensed)
    }

    var selectedID = network.getSelectedNodes();
    $("#sidepanel").show();

    var pathtoselectednode=[];
    if(selectedID!=0)
        var pathtoselectednode =[];

    var pathtoselectednodelabels =[]

    var dataForSideBar = {
        "Topic" : nodes[selectedID].label,
        "Gmail" : []
    }
    for(var i=0;i<items.length;i++){
        dataForSideBar.Gmail.push({"subject": "" , "data" :items[i]})
    }

    for(var i = selectedID; i > 0; i = parentlist[i]){
        if(pathtoselectednode.indexOf(i)==-1)
            pathtoselectednode.push(i);
        else
            break;
    }

    var breadcrumb = '<li>Me</li>';
    for(var i=pathtoselectednode.length-1;i>=0;i--){
        breadcrumb+='<li>'+nodes[pathtoselectednode[i]].label+'</li>';
    }
    $("#breadcrumb").html(breadcrumb);
}

/**
*   A function that populates the sidepanel with data
*   @param array - contains the data of the selected node
*/
function populateSidePanel(array)
{
    var t = array[0];
    var title = t.charAt(0).toUpperCase() +array[0].substr(1).toLowerCase();
    var id = "#"+array[0]+"tab";
    // if(array.length >1)
    //     $("#tabs").append('<div class="panel panel-default"><div class="panel-heading"><h3 data-toggle="collapse" data-parent="#accordion" href="#collapse'+array[0]+'" class="panel-title">'+title+'</h3></div><div id="collapse'+array[0]+'" class="panel-collapse collapse"><div id="'+array[0]+'" class="panel-body"  style="max-height: 50vh;overflow-y: scroll;"></div></div></div>');
    // for(var i = 1 ; i < array.length; i++ )
    // {
    //     $(id).append("<div class='email panel'>"+array[i]+"</div>");
    // }
    if(array.length >1){
        $(".tabs").append('<div id="'+id+'" class="tab">'+title+'</div>');
        $($(".tabs").children()[0]).addClass('tabopen')
    }
    $('.tab').click(function () {
        $('.tabopen').removeClass('tabopen');
        $(this).addClass('tabopen');
        $("#cards").children().hide();
        // alert("."+($(this).attr('id')).replace("tab","card").replace("#",""))

        $("."+($(this).attr('id')).replace("tab","card").replace("#","")).show();
        // $(".facebookcard").show();
    });
    for(var i = 1 ; i < array.length; i++ ){
        var temp = (id).replace("tab","card").replace("#","");
        // alert(temp)
        $("#cards").append("<div class='"+temp+"' id='card'>"+array[i]+"</div>");

    }


	$(".panel-heading").css("backgroundColor",getCookie("nav"));
	$(".panel-default").css("backgroundColor",getCookie("nav"));
}

/**
*   A function that hides the sidebar
*/
function hidesidebar()
{
   $("#facebook").html("");
   $("#gmail").html("");
   $("#twitter").html("");
   $("#linkedIn").html("");
   $("#sidepanelTitle").html("");
   $("#sidepanel").hide();
   $("#cards").empty();
   $(".tabs").empty();
   $("#backfromsidebar").html(navbarReloadTextCondensed)
   $("#overlay").fadeOut(300, function() {
       $("#overlay").hide();
   });
}
var sidebarAni;
function showsidebar(){
  var sidepanelcolor = getCookie("sidepanel");
  var hilightcolor = getCookie("nav");
  if(sidepanelcolor == "rgba(255,255,255,1)"){
    $(".avatar").removeClass("avatardark")
    $(".sidepanel").removeClass("sidepaneldark")
    $(".sidepanelTitlewords").removeClass("sidepanelTitlewordsdark")
  }else{
    $(".avatar").addClass("avatardark")
    $(".sidepanel").addClass("sidepaneldark")
    $(".sidepanelTitlewords").addClass("sidepanelTitlewordsdark")

  }



   if(!$("#sidepanel").is(":visible")){
        $("#sidepanel").show();
        $("#sidepanel").width(0);
        console.log("sidebar show started")
       $("#overlay").fadeIn(300)
        sidebarAni = setInterval(function(){
            if($("#sidepanel").width()<700){
               $("#sidepanel").width($("#sidepanel").width()+40);
               $("#overlay").show();
            }else{
               clearInterval(sidebarAni);
            }
        }, 1);
    }
}