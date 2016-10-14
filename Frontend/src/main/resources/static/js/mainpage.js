//mainpage
/**
*   @var {String} name - varible to hold where the user is current logged in.
*/
var name = "login=";
/**
*   @var {Boolean} rightClick - Variable to see where the user is currentluy right clicking
*/
var rightClick;
/**
*   @var {} ca - Variable to hold all split cookies
*/
var ca = document.cookie.split(';');
/**
*   @var {String} x - Temp Variable to hold the cookie result
*/
var x="";
/**
*   @var {String} x1 - Temp Variable to hold the cookie result
*/
var x1="";
/**
*   @var {} menu - Holds the right click context menu, as to be referenced later
*/
var menu;
/**
*   @var {int} selectedID - The currently selected nodes ID
*/
var selectedID;
/**
*   @var {int} parentlist - List to hold all the parent nodes, if you want to find the parent of node 1 for example, indice the array at [1] to find the parent.
*/
var parentlist =["-1","-1"];
/**
*   @var {int} expandlist - Temporary array to hold all the nodes that need to be exapanded next
*/
var expandlist = [];
/**
*   @var {int} initialdepth - The intial depth that the graph needs to expand to when the user loads the page
*/
var initialdepth ;
/**
*   @var {int} initialbranching - The branching depth that the graph needs to expand to when the user loads the page
*/
var initialbranching;
/**
*   @var {bool} flagHasNodesToLoad - Checks whether there is old nodes to load from cache and if it should request some more
*/
var flagHasNodesToLoad = false;
/**
*   @var {bool} mocktesting - Checks whether to use mock data rather than requesting data for testing data
*/
var mocktesting = false;
/**
*   @var {bool} currFramerate - Stores the current framerate.
*/
var currFramerate = 60;
/**
*   @var {String} navbarReloadTextExpanded - Hold the text that updates whether the side bar should have an expanded default look or the condensed look for mobiles.
*/
var navbarReloadTextExpanded ="<a class='navbar-brand' href='#'><img alt='Brand' style='width:30px;height:30px' src='/images/bubblelogo3.png' onmouseover='hover(this);' onmouseout='unhover(this);'/></a><p class='navbar-text' id='navbartitle'><span id='navbartitle' style='font-family: 'Pacifico', cursive;'>unclutter</span></p>";
/**
*   @var {String} navbarReloadTextCondensed - Hold the text that updates whether the side bar should have an expanded default look or the condensed look for mobiles.
*/
var navbarReloadTextCondensed ="<a class='navbar-brand' href='#'><img alt='Brand' style='position:fixed;width:30px;height:30px;top:16px;padding:5px' src='/images/bubblelogo3.png' onmouseover='hover(this);' onmouseout='unhover(this);'/></a><p id='navbartitle' class='navbar-text' ><span id='navbartitle' style='font-family: 'Pacifico', cursive;'>unclutter</span></p>";
/**
*   @var {bool} shouldRebuild - Checks whether the mindmap should be saved if the user closes the session
*/
var shouldRebuild = true;
/**
*   @var {bool} canExpand - Checks whether the mindmap can expand.
*/
var canExpand = false;
/**
*   @var {} allPimIDlist - List to hold all the processed item ID'selectedID, used for populating the side bar, first indice is the node ID, second is the PIM data source and third is the processed ID item.
*/
var allPimIDlist = new Array();
var sidebarexpanded =false;
var nodecolor= 'white'
var fontcolor= 'black'

var nodePosition =0;
allPimIDlist[0] = new Array();
allPimIDlist[0][0] = new Array();
allPimIDlist[0]=[null][null];

x =getCookie("login");

if(x!="1"){
    window.location.assign('/login');
}

/**
*   A JQuery function that allows the sidepanel to be resizeable
*/
$( window ).resize(function() {
    var map=getCookie("map");
    console.log(map)
    if($(window).width()<=768){
        $("#backfromsidebar").html(navbarReloadTextCondensed)
        $("#help").html("   Help");
        $("#help").css('font-family','Raleway');
        $("#help").css('font-size','14pt');

        $("#settings").html("   Settings");
        $("#settings").css('font-family','Raleway');
        $("#settings").css('font-size','14pt');

        $("#logout").html("   Logout");
        $("#logout").css('font-family','Raleway');
        $("#logout").css('font-size','14pt');
    }else{
        $("#help").html("");
        $("#settings").html("");
        $("#logout").html("");
        $("#backfromsidebar").html(navbarReloadTextExpanded)
    }
});

/**
*   @var nodes - An array of node objects
*/
var nodes;
/**
*   @var {} edges - An array of edge objects
*/
var edges;
/**
*   @var {} network - A variable which holds the created network
*/
var network;
/**
*   Function that is executed when the document has loaded
*/

$(document).ready(function(){
    $('#overlay').hide();
    $('#overlay').fadeOut();
    if(getCookie("mustreload")!=""){
        localStorage.setItem('nodes', "");
        localStorage.setItem('edges', "");
        localStorage.setItem('parentlist', "");
        document.cookie="lastrefreshtime="+ Date.now();
        document.cookie = "mustreload=; expires=Thu, 01 Jan 1970 00:00:00 UTC";   
    }

    var nav=getCookie("nav");
    var map=getCookie("map");
    nodecolor= 'white'
    fontcolor= 'black'
    if(map=='#1E2019'){
        nodecolor = '#1E2019';
        fontcolor= 'white'
        $("#mynetwork").css("backgroundColor", "#1E2019 !important")

    }
    var sidepanel=getCookie("sidepanel");
    if(nav!= "")
    {
        $("#nav").css("backgroundColor",nav+" !important");
        $("#sidepanelTitle").css("backgroundColor",nav);
        $(".panel-group").css("backgroundColor",nav);
        // $(".breadcrumb").css("backgroundColor",nav+" !important");
    }
    if(map!= "")
        $("#mynetwork").css("backgroundColor", map+" !important");
    else
        $("#mynetwork").css("backgroundColor", "white !important");


    if($(window).width()<=768){
        $("#backfromsidebar").html(navbarReloadTextCondensed)
        $("#help").html("   Help");
        $("#help").css('font-family','Raleway');
        $("#help").css('font-size','14pt');

        $("#settings").html("   Settings");
        $("#settings").css('font-family','Raleway');
        $("#settings").css('font-size','14pt');

        $("#logout").html("   Logout");
        $("#logout").css('font-family','Raleway');
        $("#logout").css('font-size','14pt');
    }else{
        $("#help").html("");
        $("#settings").html("");
        $("#logout").html("");
        $("#backfromsidebar").html(navbarReloadTextExpanded)
    }

    //A function that hides the error
    $("#loadingAlertError").hide();

    //A function that hides the warning
    $("#loadingAlertWarning").hide();

    //A function that displays the loading bar
    $("#loadingAlert").fadeIn(1000, function() {
        // body...
    });

    //color -  A varible that contains the colour of the text in the bubbles on the BubbleMap
    var color = 'gray';
    var len = undefined;

    tempnodes =localStorage.getItem('nodes');

    if(tempnodes==""||tempnodes==null){
        if(mocktesting){
            nodes = [
                {id: 0, label: "   ME   ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'#1999d6',highlight:{background:'#1999d6',border:'#1999d6'},hover:{background:'#1999d6',border:'#1999d6'}}},
                {id: 1, label: "  Contacts  ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'purple',highlight:{background:'purple', border:'purple'},hover:{background:'purple', border:'purple'}}}
            ]
            allPimIDlist[1] = [["gmail","1","2"],["facebook","9","10"]];
            allPimIDlist[2] = [["gmail","3","4"],["facebook","9","10"]];
            allPimIDlist[3] = [["gmail","5","6"],["facebook","10","9"]];
            allPimIDlist[4] = [["gmail","7","8"],["facebook","10","9"]];
            allPimIDlist[5] = [["gmail","7","8"],["facebook","10","9"]];
            allPimIDlist[6] = [["gmail","1","2"],["facebook","10","9"]];
            allPimIDlist[7] = [["gmail","3","4"],["facebook","10","9"]];
            allPimIDlist[8] = [["gmail","5","6"],["facebook","10","9"]];
        }else{
            nodes = [
                {id: 0, label: "   ME   ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'#1999d6',highlight:{background:'#1999d6',border:'#1999d6'},hover:{background:'#1999d6',border:'#1999d6'}}},
                {id: 1, label: "  Contacts  ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'purple',highlight:{background:'purple', border:'purple'},hover:{background:'purple', border:'purple'}}}
            ]
        }
        document.cookie="lastrefreshtime="+ Date.now();

    }else{
        console.log(localStorage.getItem('nodes'))
        nodes =JSON.parse(localStorage.getItem('nodes'));
        flagHasNodesToLoad =true;
    }

    // tempedges =getCookie("edges");
    tempedges = localStorage.getItem('edges')
    if(tempedges==""||tempedges==null){
        if(mocktesting){
            edges = [
                {id:0,from: 0, to: 1}
            ]
        }else{
            edges = [
            {id:0,from: 1, to: 0}]
        }
    }else{
        console.log(localStorage.getItem('edges'))

        var temp = localStorage.getItem('edges');
        // {"from":1,"to":0}

        while(temp.indexOf('{"from":1,"to":0}')!=-1){

            temp =temp.replace('{"from":1,"to":0},','');

        }
        edges =JSON.parse(temp);
        edges.push({
            id: 0,
            from:  1,
            to: 0
        });

        $("#loadingAlert").fadeOut(1000, function() {
            // body...
        });
    }

    tempparent = localStorage.getItem('parentlist');
    // alert(localStorage.getItem('parentlist')==null)
    if(tempparent!="" &&tempparent!=null)
        parentlist =tempparent.split(',');

    //container - A variable that holds the html element that contains the BubbleMap
    var container = document.getElementById('mynetwork');

    //data - An object that contains the node and edge objects
    var data = {
       nodes: new vis.DataSet(nodes),
       edges: new vis.DataSet(edges)
    };

    //options - An object that contains all settings for the BubbleMap
    var options = {
        interaction:{
            hover: true,
            hoverConnectedEdges: true,
        },
        physics: {
          stabilization: false
        },
        clickToUse: false,
        nodes: {
            shape: 'circle',
            size: 50,
            color: nodecolor, //rgb(102,167,188)',
            font: {
                size: 26,
                color: fontcolor
            },
            borderWidth: 3
        },
        configure:function (option, path) {
          if (path.indexOf('smooth') !== -1 || option === 'smooth') {
            return true;
          }
          return false;
        },
        edges: {
            width: 1,
            length: 150
        }
    };
    network = new vis.Network(container, data, options);

    if(!window.FPSMeter){
    alert("This test page doesn't seem to include FPSMeter: aborting");
    return;
    }

    // Register a progress call-back
    document.addEventListener('fps',
        function(evt) {
            currFramerate = evt.fps

            if(currFramerate<25){
                var options = {
                  "edges": {
                    "smooth": false
                    }
                  }
                  network.setOptions(options);
            }else if(currFramerate>40){
                var options = {
                  "edges": {
                    "smooth": true
                    }
                  }
                  network.setOptions(options);
            }

        },
    false);

    // Start FPS analysis, optionnally specifying the rate at which FPS
    // are evaluated (in seconds, defaults to 1).
    FPSMeter.run();

    //socket - holds the web socket object
    var socket = new SockJS('/request');
    stompClient = Stomp.over(socket);
    //A function that connects the stompClient
    stompClient.connect({}, function(frame) {
        x1 =getCookie("userId");

        //SelectedID - contains the id of the last selected node
        selectedID=0;
        document.cookie="lastselectednode="+selectedID;

        //A function that subscribes to a destination that the requests are sent to
        stompClient.subscribe('/user/topic/request', function(serverResponse){
            if(JSON.parse(serverResponse.body).items!=null){
                var items = JSON.parse(serverResponse.body).items;
                if($(window).width()<=768){
                    $("#backfromsidebar").html("<a class='navbar-brand' onclick='hidesidebar()'><span  style='position:fixed;width:30px;height:30px;top:16px;left:-0px;cursor:pointer;padding:5px' class='glyphicon glyphicon-chevron-left' src=''/></a><p class='navbar-text' onclick='hidesidebar()' style='cursor:pointer'>Back</p>")
                }else{
                    $("#backfromsidebar").html(navbarReloadTextCondensed)
                }

                var selectedID = getCookie("lastselectednode");
                
                showsidebar()

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

                populateSidePanel(items);
                $("#loadingAlert").fadeOut(1000, function() {
                    // body...
                });
            }else{
                canExpand=true;
                //name2 - a variable that contains the data for the last selected node for the cookie
                var name2 = "lastselectednode=";
                //ca2 - Splits the document cookie on semicolons into an array
                var ca2 = document.cookie.split(';');
                selectedID ="";
                for(var i = 0; i <ca2.length; i++) {
                    var c = ca2[i];
                    while (c.charAt(0)==' ') {
                        c = c.substring(1);
                    }
                    if (c.indexOf(name2) == 0) {
                        selectedID = c.substring(name2.length,c.length);
                    }
                }

                //update graph with server response

                //JSONServerResponse - contains the parsed response from the websocket
                var JSONServerResponse = JSON.parse(serverResponse.body);
                // topicsall - an array that contains names for the topics of the items used by the pims.
                var topicsall = JSONServerResponse.topicsText;
                //contactsAll - an array that contains names for the contatcs of the items used by the pims.
                var contactsAll = JSONServerResponse.involvedContacts;

                //pos - a variable that contains the position
                nodePosition=0;
                //branchinglimit - contains the length of the topicsall array
                var branchinglimit = topicsall.length;
                var thiscolour = {background:nodecolor, border:'#1999d6',highlight:{background:'#1999d6',border:'#1999d6'},hover:{background:'#1999d6',border:'#1999d6'}};
                //tempnodelength - contains the length of the nodes array
                var tempnodelength = parseInt(nodes.length);
                var refreshContactList =[];
                var dontConcateAgain =false;
                if(topicsall.length<= i && !dontConcateAgain && contactsAll!=null && selectedID != 0){
                    topicsall = topicsall.concat(contactsAll);
                    dontConcateAgain=true;
                    branchinglimit= (topicsall.length + contactsAll.length) ;
                    if(branchinglimit>initialbranching){
                        branchinglimit=4;
                    }
                }
                if(selectedID==0){
                    for(var j=0;j<parentlist.length;j++){
                        if(parentlist[j] == 1){
                            refreshContactList.push(j);
                        }
                    }
                }

                if(topicsall != null && topicsall.length >0){
                    for(var k=0;k<topicsall.length;k++)
                        topicsall[k] = (topicsall[k]).split(' ').join('  \n  ');
                }
                if(contactsAll != null && contactsAll.length >0){
                    for(var k=0;k<contactsAll.length;k++)
                        contactsAll[k] = (contactsAll[k]).split(' ').join('  \n  ');
                }

                for(var i=0 ;i<branchinglimit;i++){
                    if(contactsAll.indexOf(topicsall[nodePosition]) >=0)
                        thiscolour = {background:nodecolor, border:'#8AC926',highlight:{background:'#8AC926', border:'#8AC926'},hover:{background:'#8AC926', border:'#8AC926'}};

                    var pimSourceIds = JSONServerResponse.pimSourceIds;
                    allPimIDlist[nodes.length]=pimSourceIds[i];
                    if(topicsall[nodePosition]!="undefined" && topicsall[i]!=undefined){
                        try {
                            expandlist.push(tempnodelength++);
                            data.nodes.add({
                                id: nodes.length,
                                label: "  "+topicsall[nodePosition]+"  ",
                                font:'20px Raleway '+fontcolor,
                                color: thiscolour
                            });
                            parentlist.push(selectedID);
                        }
                        catch (err) {
                            $("#loadingAlert").fadeOut(1000, function() {
                                // body...
                            });
                            $("#loadingAlertError").fadeIn(1000, function() {
                                // body...
                            });
                        }

                        try {
                            data.edges.add({
                                id: edges.length,
                                from:  nodes.length,
                                to: selectedID
                            });
                        }
                        catch (err) {
                            // alert(err);
                            $("#loadingAlert").fadeOut(1000, function() {
                                // body...
                            });
                            $("#loadingAlertError").fadeIn(1000, function() {
                                // body...
                            });
                        }

                        nodes.push({
                            id: nodes.length,
                            label: "  "+topicsall[nodePosition]+"  ",
                            font:'20px Raleway '+fontcolor,
                            color: thiscolour
                        })
                        edges.push({
                            id: edges.length,
                            from:  nodes.length,
                            to: selectedID
                        });

                        if(selectedID==0  && contactsAll != null && contactsAll.length >0 && i<contactsAll.length && contactsAll[i]!="undefined"){
                            allPimIDlist[nodes.length]=pimSourceIds[i];
                            expandlist.push(tempnodelength++);
                            data.nodes.add({
                                id: nodes.length,
                                label:"  "+ contactsAll[i]+"  ",
                                font:'20px Raleway '+fontcolor,
                                color: {background:nodecolor, border:'purple',highlight:{background:'purple', border:'purple'},hover:{background:'purple', border:'purple'}}
                            });
                            parentlist.push("1");

                            data.edges.add({
                                id: edges.length,
                                from: nodes.length,
                                to: 1
                            });
                            nodes.push({
                                id: nodes.length,
                                label:"  "+ contactsAll[i]+"  ",
                                font:'20px Raleway '+fontcolor,
                                color: {background:nodecolor, border:'purple',highlight:{background:'purple', border:'purple'},hover:{background:'purple', border:'purple'}}
                            })
                            edges.push({
                                id: edges.length,
                                from: nodes.length,
                                to: 1
                            });
                        }
                        nodePosition++;
                    }
                }
                for(var j=0;j<refreshContactList.length;j++){
                    selectedID = refreshContactList[j];
                    var deletelist =[];
                    var templist = [];
                    deletelist.push(selectedID);
                    templist.push(selectedID);
                    var count =0;
                    while(templist.length>0 || count > 10000){
                        count++;
                        var parent = templist.pop();

                        for(var i=0;i<parentlist.length;i++){

                            if(parentlist[i] == parent){
                                templist.push(i);
                                deletelist.push(i);
                            }
                        }


                    }
                    parentlist[refreshContactList[j]]=-1;
                    try{
                        network.selectNodes(deletelist);
                        network.deleteSelected();
                    }catch(err){}
                }
                $("#loadingAlert").fadeOut(1000, function() {
                    // body...
                });

                if(shouldRebuild){
                    localStorage.setItem('nodes', "");
                    localStorage.setItem('edges', "");
                    localStorage.setItem('parentlist', "");

                    localStorage.setItem('nodes', JSON.stringify(nodes));

                    console.log(parentlist)
                    var tempedges=[];
                    if(tempedges.indexOf({from:1,to:0})==-1)
                        tempedges.push({from:1,to:0})


                    for(var i=1;i<parentlist.length;i++){
                        if(parentlist[i]!=-1||!(i==1&&parentlist[i]==0))
                            tempedges.push({from:i,to:parentlist[i]})
                    }
                    console.log(JSON.stringify(tempedges))

                    localStorage.setItem('edges', JSON.stringify(tempedges));
                    localStorage.setItem('parentlist', parentlist);
                    localStorage.setItem('pimlist', JSON.stringify(allPimIDlist));

                }else{
                    localStorage.setItem('nodes', "");
                    localStorage.setItem('edges', "");
                    localStorage.setItem('parentlist', "");
                    localStorage.setItem('pimlist', "");
                }
            }
            expandBubble(expandlist.shift());
        });
        var excludelist =[]
        for(var i = 1; i < parentlist.length;i++){
            if(parentlist[i]==selectedID){
                try{
                    excludelist.push(nodes[i].label.replace(/ /g,"").replace("\n"," "));
                }catch(err){

                }
            }
        }

        initialbranching = getCookie("branch");
        // topicRequest -  a JSON oject that contains information for a topic request
        if(mocktesting)
            topicRequest = {userId: "mocktesting"+x1, path:[], exclude:excludelist, maxNumberOfTopics:initialbranching};
        else
            topicRequest = {userId: x1, path:[], exclude:excludelist, maxNumberOfTopics:initialbranching};
        if(!flagHasNodesToLoad){
            try{
                stompClient.send("/app/request", {}, JSON.stringify(topicRequest));
            }catch(err){
                $("#loadingAlert").fadeOut(1000, function() {
                // body...
                });
                $("#loadingAlertError").fadeIn(1000, function() {
                });
                $("#loadingAlertError").html("Error: We could not talk to the server. Please try again.")
            }
            //A function that displays the loading bar
            $("#loadingAlert").fadeIn(1000, function() {
                // body...
            });
        }
    });


    if($(window).width()<=768)
    {
        $("#backfromsidebar").html(navbarReloadTextCondensed)
        $("#help").html("   Help");
        $("#settings").html("   Settings");
        $("#logout").html("   Logout");
    }
    else
    {
        $("#backfromsidebar").html(navbarReloadTextExpanded)
        $("#help").html("");
        $("#settings").html("");
        $("#logout").html("");
    }
    // $("#sidepanel").hide();
    hidesidebar();
    //A function that disables the default event that occurs on rightclick event
    document.oncontextmenu = function() {return false;};
    

    //menu - variable that is assigned the context menu
    menu = new ax5.ui.menu({
        position: "absolute", // default position is "fixed"
        theme: "primary",
        icons: {
            'arrow': '<i class="fa fa-caret-right"></i>'
        },
        items: [
            {
                label: "Expand Bubble"
            },
            {
                label: "Remove Bubble"
            },
            {
                label: "Refresh"
            },
            {
                label: "Delete topic"
            },
            {
                label: "Toggle topic/contact bubble"
            }
        ],
        onClick: function(){
            if(this.label=="Expand Bubble"){
                $("#loadingAlert").fadeIn(1000, function() {
                    // body...
                });
                var pathtoselectednode=[];
                if(selectedID!=0)
                    var pathtoselectednode =[];
                var pathtoselectednodelabels =[]

                for(var i = selectedID; i > 0; i = parentlist[i]){
                    pathtoselectednode.push(i);
                }

                var pos=0;
                var branchinglimit = 4;
                var thiscolor = nodes[selectedID].color;
                for(var i=pathtoselectednode.length-1;i>=0;i--){
                   pathtoselectednodelabels.push(nodes[pathtoselectednode[i]].label.replace(/ /g,"").replace("\n"," "));
                }
                if(pathtoselectednodelabels.indexOf("Contacts") >=0){
                    pathtoselectednodelabels.splice(pathtoselectednodelabels.indexOf("Contacts"),1);
                }

                var excludelist=[]
                for(var i = 1; i < parentlist.length;i++){
                    if(parentlist[i]==selectedID){
                        try{
                            excludelist.push(nodes[i].label.replace(/ /g,"").replace("\n"," "));
                        }catch(err){

                        }
                    }
                }

                initialbranching = getCookie("branch");
                if(mocktesting)
                    topicRequest = {userId: "mocktesting"+x1, path:pathtoselectednodelabels, exclude:excludelist, maxNumberOfTopics:initialbranching};
                else
                    topicRequest = {userId: x1, path:pathtoselectednodelabels, exclude:excludelist, maxNumberOfTopics:initialbranching};

                document.cookie="lastselectednode="+selectedID;
                try{
                    stompClient.send("/app/request", {}, JSON.stringify(topicRequest));
                }catch(err){
                    $("#loadingAlert").fadeOut(1000, function() {
                    // body...
                    });
                    $("#loadingAlertError").fadeIn(1000, function() {
                    });
                    $("#loadingAlertError").html("Error: We could not talk to the server. Please try again.")
                }
                    // $("#loadingAlert").fadeOut(1000, function() {
                    //     // body...
                    // });
            }

            if(this.label=="Refresh"){
                refreshGraph();
            }
            if(this.label=="Delete topic"){
                // alert("hello!")
                var socket = new SockJS('/update');
                stompClient = Stomp.over(socket);
                stompClient.connect({}, function(frame) {
                    console.log('Connected: ' + frame);
                    connected = true;
                    var userId = getCookie("userId");
                    // alert(selectedID);
                    topicWrapperRequest = {"userId": getCookie("userId"), topicname:nodes[selectedID].label, "hidden":true};
                    alert(JSON.stringify(topicWrapperRequest))
                    stompClient.send("/app/update", {}, JSON.stringify(topicWrapperRequest));

                });
                
            }
            if(this.label=="Toggle topic/contact bubble"){ 
                
            }
        }
    });
    //A function that resets the html for certain divs
    network.on("click", function(){

       $("#facebook").html("");
       $("#gmail").html("");
       $("#twitter").html("");
       $("#linkedIn").html("");
       $("#sidepanelTitle").html("");
       $("#sidepanelTitlewords").html("");
       // $("#sidepanel").hide();
       hidesidebar();
        sidebarexpanded=false;
        if($(window).width()<=768){
            $("#backfromsidebar").html(navbarReloadTextCondensed)
            $("#help").html("   Help");
            $("#help").css('font-family','Raleway');
            $("#help").css('font-size','14pt');

            $("#settings").html("   Settings");
            $("#settings").css('font-family','Raleway');
            $("#settings").css('font-size','14pt');

            $("#logout").html("   Logout");
            $("#logout").css('font-family','Raleway');
            $("#logout").css('font-size','14pt');
        }else{
            $("#help").html("");
            $("#settings").html("");
            $("#logout").html("");
            $("#backfromsidebar").html(navbarReloadTextExpanded)
        }

    });
     // A function that handles the doubleClick event on the BubbleMap
    
    network.on("doubleClick", function(){

        $("#accordion").html("");
        $("#facebook").html("");
        $("#gmail").html("");
        $("#twitter").html("");
        $("#linkedIn").html("");

        // A function that displays the loading bar
        $("#loadingAlert").fadeIn(1000, function() {
            // body...
        });
        var node = getCookie("lastselectednode");
        console.log(node);
        selectedID = node;
        $("#sidepanelTitlewords").html("<h2><b>"+toTitleCase(nodes[node].label)+"</b></h2>");
        var avatarlink =""; 
        var nodeswithplus = nodes[node].label;

            $('#avatar').css("background","#eee url('/images/bubblelogo3.png')");
            $('#avatar').css("background-size","cover"); 
            $('#avatar').css("background-position","center");
            $("#sidepanelTitle").css("background","#eee url('/images/back.jpg')");
            $('#sidepanelTitle').css("background-size","cover");
            $('#sidepanelTitle').css("background-position","center");
            $('#sidepanelTitle').addClass("blurclass");
            $('#sidepanelTitlewords').css("color","black");
            // $.get("https://www.googleapis.com/customsearch/v1?q="+nodeswithplus.replace(" ","+")+"&cx=004184724144738447691%3Aahmdf8he_fu&imgSize=medium&num=1&safe=high&searchType=image&key=AIzaSyCukG3Zs_BoObdL5NEUqA7uZeouPc7Xpf4", function(data, status){
                // console.log("Data: " + JSON.stringify(data)+ "link: " +data.items[0].link+ "\nStatus: " + status);
                // avatarlink = data.items[0].link;
                $.get("https://pixabay.com/api/?key=3499301-3dec69a66cfd20291e8a03c40&q="+nodeswithplus.replace(" ","+")+"&safesearch=true&order=latest", function(data, status){
                    // console.log("Data: " + JSON.stringify(data)+ "\nStatus: " + status);
                        avatarlink = data.hits[0].previewURL;
                        $('#avatar').css("background","#eee url('"+avatarlink+"')");
                        $('#avatar').css("background-size","cover");
                        $('#avatar').css("background-position","center");
                        $("#sidepanelTitle").css("background","#eee url('"+avatarlink+"')");
                        $('#sidepanelTitle').css("background-size","cover");
                        $('#sidepanelTitle').css("background-position","center");
                        $('#sidepanelTitle').addClass("blurclass");
                });
            // }
        for(var i=0;i<allPimIDlist[selectedID].length;i++){
            var uniqueIds = [];
            $.each(allPimIDlist[selectedID][i], function(j, el){
                if($.inArray(el, uniqueIds) === -1) uniqueIds.push(el);
            });
            // var ID =getCookie(allPimIDlist[selectedID][i][0]+"Id")
            var pimIds = JSON.parse(getCookie("pimIds"));
            var ID = "";
            for(var j = 0 ; j < pimIds.length; j++)
            {
                var current = pimIds[j];        
                if(current.pim == allPimIDlist[selectedID][i][0])
                {
                    ID =current.uId;
                }
            }
            if(!mocktesting)
                var itemRequest = {itemIds:uniqueIds,userId:ID};
            else
                var itemRequest = {itemIds:uniqueIds,userId:"mocktesting"+ID};
            
            try{
                stompClient.send("/app/items", {}, JSON.stringify(itemRequest));
            }catch(err){
                $("#loadingAlert").fadeOut(1000, function() {
                // body...
                });
                $("#loadingAlertError").fadeIn(1000, function() {
                });
                $("#loadingAlertError").html("Error: We could not talk to the server. Please try again.")
            }
        }
    });

    //A function that handles the rightClick event on the BubbleMap
    network.on("oncontext", function(){
        var e = window.event;
        var posX = e.clientX;
        var posY = e.clientY - $("nav").height();
        selectedID = network.getNodeAt({"x": posX, "y": posY});
        console.log(selectedID);
        network.selectNodes([network.getNodeAt({"x": posX, "y": posY})]);
        var node = network.getSelectedNodes();
            if(node.length != 0)
            {
                menu.popup(e);
                ax5.util.stopEvent(e);
            }
        rightClick = network.getSelectedNodes();

        if(rightClick.length != 0)
        {
            menu.popup(e); 
            ax5.util.stopEvent(e);
        }

    });
    network.on("selectNode", function(e){
        console.log(e.nodes);
        document.cookie="lastselectednode="+e.nodes[0];
    });
 
});
