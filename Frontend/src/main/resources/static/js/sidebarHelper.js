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
    var title = t.charAt(0).toUpperCase() +array[0].substr(1).toLowerCase()
    var id = "#"+array[0];
    if(array.length >1)
        $("#accordion").append('<div class="panel panel-default"><div class="panel-heading"><h3 data-toggle="collapse" data-parent="#accordion" href="#collapse'+array[0]+'" class="panel-title">'+title+'</h3></div><div id="collapse'+array[0]+'" class="panel-collapse collapse"><div id="'+array[0]+'" class="panel-body"  style="max-height: 50vh;overflow-y: scroll;"></div></div></div>');
    for(var i = 1 ; i < array.length; i++ )
    {
        $(id).append("<div class='email panel'>"+array[i]+"</div>");
    }
	$(".panel-heading").css("backgroundColor",nav);
	$(".panel-default").css("backgroundColor",nav);

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
   $("#backfromsidebar").html(navbarReloadTextCondensed)
}
