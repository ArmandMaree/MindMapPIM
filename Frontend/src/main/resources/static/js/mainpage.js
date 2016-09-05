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
var mocktesting = true;
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
var canExpand = false;
/**
*   @var {bool} canExpand - Checks whether the mindmap can expand.
*/
var shouldRebuild = false;
/**
*   @var {} allPimIDlist - List to hold all the processed item ID'selectedID, used for populating the side bar, first indice is the node ID, second is the PIM data source and third is the processed ID item.
*/
var nodecolor= 'white'
var fontcolor= 'black'

var nodePosition =0;
var allPimIDlist = new Array();
allPimIDlist[0] = new Array();
allPimIDlist[0][0] = new Array();
allPimIDlist[0]=[null][null];

for(var i = 0; i <ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0)==' ') {
        c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
        x = c.substring(name.length,c.length);
    }
}

if(x!="1"){
    window.location.assign('/login');
}
/**
*   A function that creates a json string from an object
*   @param obj An object that needs to be converted into a JSON string
*/
function toJSON(obj) {
    return JSON.stringify(obj, null, 4);
}
/**
*   A JQuery function that allows the sidepanel to be resizeable
*/
$( window ).resize(function() {
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
*   A function that changes the brands icon on hover
*/
function hover(element) {
    element.setAttribute('src', '/images/bubblelogo.png');
}
function unhover(element) {
    element.setAttribute('src', '/images/bubblelogo3.png');
}
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


    function getCookie(cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for(var i = 0; i <ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length,c.length);
            }
        }
        return "";
    }
$(document).ready(function(){
    // alert("Hey");
    if(getCookie("branch")!= "")
    {
        var initialbranching = getCookie("branch");
        $("#Branchrange").html(getCookie("branch"));
        $("#brange").attr("value",getCookie("branch"));
    }
    else
    {
        $("#Branchrange").html("4");
        $("#brange").attr("value","4");
    }
    
    if(getCookie("depth") != "")
    {
        var initialdepth = getCookie("depth");
        $("#Depthrange").html(getCookie("depth"));
        $("#drange").attr("value",getCookie("depth"));
    }
    else
    {
        $("#Depthrange").html("2");
        $("#drange").attr("value","2");
    }

    var nav=getCookie("nav");
    var map=getCookie("map");
    nodecolor= 'white'
    fontcolor= 'black'
    console.log("colour:"+map)
    if(map=='#1E2019'){
        console.log("colour yes!")
        nodecolor = '#1E2019';
        fontcolor= 'white'

    }
    var sidepanel=getCookie("sidepanel");
    if(nav!= "")
        $("#nav").css("backgroundColor",nav+" !important");
    if(map!= "")
        $("#mynetwork").css("backgroundColor", map+" !important");
    if(sidepanel!= "")
    {
        $("#sidepanel").css("backgroundColor",sidepanel);
        $("#sidepanelTitle").css("backgroundColor",nav);
        $(".panel-group").css("backgroundColor",nav);
         $(".breadcrumb").css("backgroundColor",nav);
     }
        // console.log("---------------------------------------changed sidepanel title colour----------------------------------------------");

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
    /**
    *   A function that hides the error
    */
    $("#loadingAlertError").hide();  
    /**
    *   A function that hides the warning
    */
    $("#loadingAlertWarning").hide();  
    /**
    *   A function that displays the loading bar
    */
    $("#loadingAlert").fadeIn(1000, function() {
        // body...
    });
    /**
    *   @var {String} color -  A varible that contains the colour of the text in the bubbles on the BubbleMap
    */
    var color = 'gray';
    /**
    *   @var len - 
    */  
    var len = undefined;

    /**
   *    @var {String} name1 - string that contains the userId
   */
    var name1 = "nodes=";
    /**
    *   @var ca1 - Cookie....
    */
    var ca1 = document.cookie.split(';');
    /**
    *   @var tempnodes - ...
    */
    tempnodes ="";
    for(var i = 0; i <ca1.length; i++) {
        var c = ca1[i];
        while (c.charAt(0)==' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name1) == 0) {
            tempnodes = c.substring(name1.length,c.length);
        }
    }
    if(tempnodes==""){
        if(mocktesting){
            nodes = [
                {id: 0, label: "   ME   ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'#1999d6',highlight:{background:'#1999d6',border:'#1999d6'},hover:{background:'#1999d6',border:'#1999d6'}}},
                {id: 1, label: "  Contacts  ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'purple',highlight:{background:'purple', border:'purple'},hover:{background:'purple', border:'purple'}}}
            
                // {id: 2, label: "Horse", group: 0},
                // {id: 3, label: "Amy \n Lochner", group: 0},
                // {id: 4, label: "COS301", group: 0},
                // {id: 5, label: "Fritz \n Solms", group: 0},
                // {id: 6, label: "Holiday", group: 0},
                // {id: 7, label: "Arno \n Grobler", group: 0},
                // {id: 8, label: "Arno \n Grobler", group: 0}
            ]
            // parentlist =["0","0","0","2","0","4","6","7","2"];
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
        nodes =[];
        // nodes.push( {id: 0, label: "   ME   ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'#1999d6',highlight:{background:'#1999d6',border:'#1999d6'},hover:{background:'#1999d6',border:'#1999d6'}}})
        // nodes.push({id: 1, label: "  Contacts  ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'purple',highlight:{background:'purple', border:'purple'},hover:{background:'purple', border:'purple'}}})
        var splitter = tempnodes.split('%');
        for(var i = 0; i <splitter.length; i++) {
            var c = splitter[i];
            console.log(c);
            if(c==""|| c=="undefined"){
                break;
            }
            tempnodes = JSON.parse(c);
            nodes.push(tempnodes);
        }
        flagHasNodesToLoad =true;
    }


    /**
   *    @var {String} name1 - string that contains the userId
   */
    var name1 = "edges=";
    /**
    *   @var ca1 - Cookie....
    */
    var ca1 = document.cookie.split(';');
    /**
    *   @var tempedges - ...
    */
    tempedges ="";
    for(var i = 0; i <ca1.length; i++) {
        var c = ca1[i];
        while (c.charAt(0)==' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name1) == 0) {
            tempedges = c.substring(name1.length,c.length);
        }
    }
    if(tempedges==""){
        if(mocktesting){
            edges = [
                {from: 0, to: 1}
                // {from: 2, to: 3},
                // {from: 2, to: 0},
                // {from: 5, to: 4},
                // {from: 4, to: 0},
                // {from: 7, to: 6},
                // {from: 6, to: 0},
                // {from: 2, to: 8}
            ]
        }else{
            edges = [
            {from: 1, to: 0}]
        }
    }else{
        edges =[];
        edges.push({from: 1, to: 0});
        var splitter = tempedges.split('%');
        for(var i = 0; i <splitter.length; i++) {
            var c = splitter[i];
            console.log(c);
            if(c=="" || c=="undefined"){
                break;
            }
            tempedges = JSON.parse(c);
            tempedges = {id: tempedges.id, from: (tempedges.from-1) , to: tempedges.to}
            // tempedges.id +=1;
            edges.push(tempedges);
        }
    }

    /**
   *    @var {String} name1 - string that contains the userId
   */
    var name1 = "parentlist=";
    /**
    *   @var ca1 - Cookie....
    */
    var ca1 = document.cookie.split(';');
    /**
    *   @var tempparent - ...
    */
    tempparent ="";
    for(var i = 0; i <ca1.length; i++) {
        var c = ca1[i];
        while (c.charAt(0)==' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name1) == 0) {
            tempparent = c.substring(name1.length,c.length);
        }
    }
    if(tempparent!="")
        parentlist =tempparent.split(',');


        /**
          * @var container - A variable that holds the html element that contains the BubbleMap
        */
        var container = document.getElementById('mynetwork');
        /**
          * @var data - An object that contains the node and edge objects
        */
        var data = {
           nodes: new vis.DataSet(nodes),
           edges: new vis.DataSet(edges)
        };
        /**
        *   @var options - An object that contains all settings for the BubbleMap
        */
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
                width: 1
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
                // console.log(evt.fps);
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
                // console.log("Current framerate: " + evt.fps + " fps");
            },
            false);

        // Start FPS analysis, optionnally specifying the rate at which FPS 
        // are evaluated (in seconds, defaults to 1).
        FPSMeter.run();
        /**
        *   @var socket - holds the web socket object
        */
        var socket = new SockJS('/request');
        /**
        *   @var stompClient - 
        */
        stompClient = Stomp.over(socket);
        /**
        *   A function that connects the stompClient 
        */
        stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);
          // var userReg = {};
          // if(gmailUser!=null){
          //   userReg={firstName:gmailUser.wc.Za,lastName:gmailUser.wc.Na,authCodes:authCodes};
          //   console.log(JSON.stringify(userReg));
          // }
          //   this.userId = userId;
          //   this.path = path;
          //   this.exclude = exclude;
          //   this.maxNumberOfTopics = maxNumberOfTopics;

           /**
           *    @var {String} name1 - string that contains the userId
           */
            var name1 = "userId=";
            /**
            *   @var ca1 - Cookie....
            */
            var ca1 = document.cookie.split(';');
            /**
            *   @var x1 - ...
            */
            x1 ="";
            for(var i = 0; i <ca1.length; i++) {
                var c = ca1[i];
                while (c.charAt(0)==' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name1) == 0) {
                    x1 = c.substring(name1.length,c.length);
                }
            }

            /**
            *   @var {integer} selectedID - contains the id of the last selected node
            */
            selectedID=0;
            document.cookie="lastselectednode="+selectedID;
        
            /**
            *   A function that subscribes to a destination that the requests are sent to 
            */
            stompClient.subscribe('/user/topic/request', function(serverResponse){
                console.log("serverResponse.contacts: "+JSON.parse(serverResponse.body).involvedContacts);
                if(JSON.parse(serverResponse.body).items!=null){
                    console.log("serverResponse.items: "+JSON.parse(serverResponse.body).items);
                    var items = JSON.parse(serverResponse.body).items;
                    if($(window).width()<=768){
                        $("#backfromsidebar").html("<a class='navbar-brand' onclick='hidesidebar()'><span  style='position:fixed;width:30px;height:30px;top:16px;left:-0px;cursor:pointer;padding:5px' class='glyphicon glyphicon-chevron-left' src=''/></a><p class='navbar-text' onclick='hidesidebar()' style='cursor:pointer'>Back</p>")
                    }else{
                        $("#backfromsidebar").html(navbarReloadTextCondensed)
                    }

                    // $("#facebook").html("");
                    // $("#gmail").html("");
                    // $("#twitter").html("");
                    // $("#linkedIn").html("");
                    // $("#sidepanelTitle").html("");
                    var selectedID = network.getSelectedNodes();
                    $("#sidepanel").show();

                    var pathtoselectednode=[];
                    if(selectedID!=0)
                        var pathtoselectednode =[];
                    var pathtoselectednodelabels =[]
                    console.log("selectedID:"+selectedID)
                    console.log("parentlist "+parentlist)

                    var dataForSideBar = {
                        "Topic" : nodes[selectedID].label,
                        "Gmail" : []
                    }
                    console.log("this object: "+JSON.stringify(dataForSideBar))
                    for(var i=0;i<items.length;i++){
                        dataForSideBar.Gmail.push({"subject": "" , "data" :items[i]})
                    }

                    for(var i = selectedID; i > 0; i = parentlist[i]){
                        console.log(i)
                        if(pathtoselectednode.indexOf(i)==-1)
                            pathtoselectednode.push(i);
                        else
                            break;
                    }

                    console.log("PathFrom: " + pathtoselectednode);
                    var breadcrumb = '<li>Me</li>';
                    console.log("pathtoselectednode.length: "+pathtoselectednode.length)
                    for(var i=pathtoselectednode.length-1;i>=0;i--){
                        breadcrumb+='<li>'+nodes[pathtoselectednode[i]].label+'</li>';
                        console.log(breadcrumb);
                        // break;
                    }
                    $("#breadcrumb").html(breadcrumb);
                    console.log(selectedID);

                    populateSidePanel(items);

                    $("#loadingAlert").fadeOut(1000, function() {
                        // body...
                    });
                }else{
                    canExpand=true;
                    /**
                    *   @var {String} name2 - a variable that contains the data for the last selected node for the cookie
                    */
                    var name2 = "lastselectednode=";
                    /**
                    *   @var ca2 - Splits the document cookie on semicolons into an array
                    */
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


                    // console.log("Server says: "+JSON.parse(serverResponse.body).topicsText);


                    //update graph with server response

                    /**
                    *   @var JSONServerResponse - contains the parsed response from the websocket
                    */
                    var JSONServerResponse = JSON.parse(serverResponse.body);
                    /**
                    *   @var topicsall - an array that contains names for the topics of the items used by the pims.
                    */
                    var topicsall = JSONServerResponse.topicsText;
                    /**
                    *   @var contactsAll - an array that contains names for the contatcs of the items used by the pims.
                    */
                    var contactsAll = JSONServerResponse.involvedContacts;

                    /**
                    *   @var {int} pos - a variable that contains the position
                    */
                    nodePosition=0;
                    /**
                    *   @var {int} branchinglimit - contains the length of the topicsall array
                    */
                    var branchinglimit = topicsall.length;
                    /**
                    *   @var thiscolour - ....
                    */
                    var thiscolour = {background:nodecolor, border:'#1999d6',highlight:{background:'#1999d6',border:'#1999d6'},hover:{background:'#1999d6',border:'#1999d6'}};
                    /**
                    *   @var tempnodelength - contains the length of the nodes array
                    */
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
                    // console.log("contactsAll.length:"+contactsAll.length);
                    if(topicsall != null && topicsall.length >0){
                        for(var k=0;k<topicsall.length;k++)
                            topicsall[k] = (topicsall[k]).split(' ').join('  \n  ');
                    }
                    if(contactsAll != null && contactsAll.length >0){
                        for(var k=0;k<contactsAll.length;k++)
                            contactsAll[k] = (contactsAll[k]).split(' ').join('  \n  ');
                    }

                    for(var i=0 ;i<branchinglimit;i++){
                        console.log("NodeLength: " + nodes.length + "          selectedID: "+selectedID)
                        // if(contactsAll != null&& contactsAll.length >0 && Math.abs(topicsall.length-contactsAll.length)<= i){
                        //     thiscolour = {background:nodecolor, border:'#8AC926',highlight:{background:'#8AC926', border:'#8AC926'},hover:{background:'#8AC926', border:'#8AC926'}};
                        // }
                        console.log("contactsAll "+contactsAll); 
                        console.log("topicsall "+topicsall);
                        console.log("contactsAll.indexOf("+topicsall[nodePosition]+")="+contactsAll.indexOf(topicsall[nodePosition])); 
                        if(contactsAll.indexOf(topicsall[nodePosition]) >=0)
                            thiscolour = {background:nodecolor, border:'#8AC926',highlight:{background:'#8AC926', border:'#8AC926'},hover:{background:'#8AC926', border:'#8AC926'}};
 
                        /**
                        *   @var pimSourceIds - an array that contains all topics in the JSONServerResponse variable
                        */
                        var pimSourceIds = JSONServerResponse.pimSourceIds;

                        allPimIDlist[nodes.length]=pimSourceIds[i];
                        if(topicsall[nodePosition]!="undefined" && topicsall[i]!=undefined){
                            console.log("allPimIDlist for "+nodes.length+": "+allPimIDlist[nodes.length]);
                            try {
                                console.log("push "+ tempnodelength)
                                console.log("expand parentlist: "+parentlist)
                                expandlist.push(tempnodelength++);
                                console.log("expand list:"+expandlist);
                                data.nodes.add({
                                    id: nodes.length,
                                    label: "  "+topicsall[nodePosition]+"  ",
                                    font:'20px Raleway '+fontcolor, 
                                    color: thiscolour
                                });
                                parentlist.push(selectedID);
                            }
                            catch (err) {
                                alert(err);
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
                                alert(err);
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
                        console.log("needs to be deleted: "+ j);
                        selectedID = refreshContactList[j];
                        var deletelist =[];
                        var templist = [];
                        deletelist.push(selectedID);
                        templist.push(selectedID);
                        var count =0;
                        while(templist.length>0 || count > 10000){
                            count++;
                            var parent = templist.pop();
                            // console.log(parent);
                            for(var i=0;i<parentlist.length;i++){
                                // console.log(parentlist[i])
                                if(parentlist[i] == parent){
                                    templist.push(i);
                                    deletelist.push(i);
                                }
                            }
                            // console.log(templist.length)

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
                        document.cookie = "nodes=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
                        document.cookie = "edges=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
                        document.cookie = "parentlist=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
                        var tempstring ="";
                        for(var i=0;i<nodes.length;i++){
                            tempstring+= JSON.stringify(nodes[i])+"%";
                        }
                        console.log(tempstring);
                        document.cookie="nodes="+tempstring;

                        var tempstring ="";
                        for(var i=0;i<edges.length;i++){
                            tempstring+= JSON.stringify(edges[i])+"%";
                        }
                        // tempstring+= JSON.stringify({"id":0,"from":"0","to":1})+"%";
                        console.log(tempstring);
                        console.log(edges);
                        document.cookie="edges="+tempstring;
                        document.cookie="parentlist="+ parentlist;

                    }else{
                        document.cookie = "nodes=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
                        document.cookie = "edges=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
                        document.cookie = "parentlist=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
                    }
                }
                // for(var k =0;k<expandlist.length;k++){
                //     if(parentlist[expandlist[i]]==1){
                //         console.log("expand found and removed from expand list: "+expandlist[i]);
                //         expandlist.remove(i);
                //     }
                // }
                console.log()
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

            console.log("exclude list:"+excludelist);
            initialbranching = getCookie("branch");
            /**    
            *   @var topicRequest -  a JSON oject that contains information for a topic request
            */
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
                /**
                *   A function that displays the loading bar
                */
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
    $("#sidepanel").hide();
    /**
    *   A function that disables the default event that occurs on rightclick event
    */
    document.oncontextmenu = function() {return false;};
    function showValue(newValue)
    {
        document.getElementById("range").innerHTML=newValue;
    }
    /**
    *   A function that populates the sidepanel with data
    *   @param node - the node that has been selected
    *   @param array - contains the data of the selected node
    */
    function populateSidePanel(array)
    {
        // console.log("node is this: "+node)
        // alert("refreshing now")
        var t = array[0];
        console.log(array)
        var title = t.charAt(0).toUpperCase() +array[0].substr(1).toLowerCase()
        var id = "#"+array[0];
        $("#accordion").html('<div class="panel panel-default"><div class="panel-heading"><h3 data-toggle="collapse" data-parent="#accordion" href="#collapse2" class="panel-title">'+title+'</h3></div><div id="collapse2" class="panel-collapse collapse"><div id="'+array[0]+'" class="panel-body"  style="max-height: 50vh;overflow-y: scroll;"></div></div></div>');
        for(var i = 1 ; i < array.length; i++ )
        {
            $(id).append("<div>"+array[i]+"</div>");
        }
        // if(array[0] == "facebook")
        // {
        //     // https://www.facebook.com/userId/posts/postid
        //     for(var i = 1 ; i < array.length; i++ )
        //     {
        //         $(id).append("<div class='fb-post'  data-width='500'data-href='https://www.facebook.com/"+getCookie("facebookId")+"/posts/"+array[i]+"'></div>");
        //     }
        // }
        // else
        // {
            
        // }
        // }

       
        // $("#accordion").html("");
        // if(array.Topic != "Contact")
        // {
        //     $("#sidepanelTitle").html("<h2>"+nodes[node].label+"</h2>");
        // if((array.hasOwnProperty('LinkedIn')))
        // {
        //     $("#accordion").append('<div class="panel panel-default"><div class="panel-heading"><h3 data-toggle="collapse" data-parent="#accordion" href="#collapse5" class="panel-title">LinkedIn</h3></div><div id="collapse5" class="panel-collapse collapse"><div id="linkedIn" class="panel-body"  style="max-height: 50vh;overflow-y: scroll;"></div></div></div>');
        //     for(var i = 0 ; i < array.LinkedIn.length; i++ )
        //     {
        //         $("#linkedIn").html("<div>"+array[i].data+"</div>");
        //     }
        // }

       
        // $("#accordion").html("");
        // if(array.Topic != "Contact")
        // {
        //     $("#sidepanelTitle").html("<h2>"+nodes[node].label+"</h2>");
        // if((array.hasOwnProperty('LinkedIn')))
        // {
        //     $("#accordion").append('<div class="panel panel-default"><div class="panel-heading"><h3 data-toggle="collapse" data-parent="#accordion" href="#collapse5" class="panel-title">LinkedIn</h3></div><div id="collapse5" class="panel-collapse collapse"><div id="linkedIn" class="panel-body"  style="max-height: 50vh;overflow-y: scroll;"></div></div></div>');
        //     for(var i = 0 ; i < array.LinkedIn.length; i++ )
        //     {
        //         $("#linkedIn").html("<div>"+array[i].data+"</div>");
        //     }
        // }


    }

    /**
    *   @var menu - variable that is assigned the context menu
    */
    menu = new ax5.ui.menu({
        position: "absolute", // default position is "fixed"
        theme: "info",
        icons: {
            'arrow': '<i class="fa fa-caret-right"></i>'
        },
        items: [
            {
                icon: '<i class="fa fa-comment"></i>',
                label: "Expand Bubble",
            },
            {
                icon: '<i class="fa fa-comments"></i>',
                label: "Remove Bubble",
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
                console.log("selectedID:"+selectedID)
                console.log("parentlist "+parentlist)

                for(var i = selectedID; i > 0; i = parentlist[i]){
                    pathtoselectednode.push(i);
                }

                console.log("PathFrom: " + pathtoselectednode);
                var pos=0;
                var branchinglimit = 4;
                var thiscolor = nodes[selectedID].color;
                for(var i=pathtoselectednode.length-1;i>=0;i--){
                   pathtoselectednodelabels.push(nodes[pathtoselectednode[i]].label.replace(/ /g,"").replace("\n"," "));
                }
                console.log(pathtoselectednodelabels);
                // pathtoselectednodelabels.push()
                if(pathtoselectednodelabels.indexOf("Contacts") >=0){
                    pathtoselectednodelabels.splice(pathtoselectednodelabels.indexOf("Contacts"),1);
                    console.log("PathTo: " + pathtoselectednodelabels);
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

                console.log("exclude list:"+excludelist);
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

            if(this.label=="Remove Bubble"){
                deleteBranch(selectedID)
            }
        }
    });
    /**
    *   A function that resets the html for certain divs
    */
    network.on("click", function(){
       // console.log("nodes")
       $("#facebook").html("");
       $("#gmail").html("");
       $("#twitter").html("");
       $("#linkedIn").html("");
       $("#sidepanelTitle").html("");
       $("#sidepanel").hide();

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
    *   A function that handles the doubleClick event on the BubbleMap
    */
    network.on("doubleClick", function(){

        $("#accordion").html("");
        $("#facebook").html("");
        $("#gmail").html("");
        $("#twitter").html("");
        $("#linkedIn").html("");
        
        /**
        *   A function that displays the loading bar
        */
        $("#loadingAlert").fadeIn(1000, function() {
            // body...
        });
        var name1 = "lastselectednode=";
        /**
        *   @var ca1 - Cookie....
        */
        var ca1 = document.cookie.split(';');
        var selectedID ="";
        for(var i = 0; i <ca1.length; i++) {
            var c = ca1[i];
            while (c.charAt(0)==' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name1) == 0) {
                selectedID = c.substring(name1.length,c.length);
            }
        }
        var node = network.getSelectedNodes();
        $("#sidepanelTitle").html("<h2>"+nodes[node].label+"</h2>");
        selectedID = node;
        console.log("dbl "+selectedID);
        console.log("dbl "+allPimIDlist);

        console.log("dbl "+allPimIDlist[selectedID]);
        // console.log(allPimIDlist[selectedID][0]);
        // console.log(allPimIDlist[selectedID][0][1]);
        for(var i=0;i<allPimIDlist[selectedID].length;i++){
            var uniqueIds = [];
                $.each(allPimIDlist[selectedID][i], function(j, el){
                    if($.inArray(el, uniqueIds) === -1) uniqueIds.push(el);
                });
                // alert(allPimIDlist[selectedID][i][0]+"Id")
                var ID =getCookie(allPimIDlist[selectedID][i][0]+"Id")
                console.log("dbl "+uniqueIds);
            if(!mocktesting)
                var itemRequest = {itemIds:uniqueIds,userId:ID};
            else
                var itemRequest = {itemIds:uniqueIds,userId:"mocktesting"+ID};
            /**
            *   A function that sends the itemRequest object through the websocket in order to make the request
            */
            // setTimeout(function(){
            console.log("dbl "+itemRequest)
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

    /**
    *   A function that handles the rightClick event on the BubbleMap 
    */
    network.on("oncontext", function(){
        var e = window.event;
        var posX = e.clientX;
        var posY = e.clientY - $("nav").height();
        console.log("X: "+ posX);
        console.log("Y: "+ posY);
        console.log(network.getNodeAt({"x": posX, "y": posY}));
        selectedID = network.getNodeAt({"x": posX, "y": posY});
        network.selectNodes([network.getNodeAt({"x": posX, "y": posY})]);
        var node = network.getSelectedNodes();
        console.log(node);
        console.log(nodes[node].label);

            console.log("works on right click");
            // $(this).bind("contextmenu", function (e) {
            if(node.length != 0)
            {
                menu.popup(e);
                ax5.util.stopEvent(e);
            }
            // });
        //Node is an array of nodes
        rightClick = network.getSelectedNodes();
        console.log(rightClick);

        if(rightClick.length != 0)
        {
            menu.popup(e);
            ax5.util.stopEvent(e);
        }

    });

});
// setInterval(function exapndingintervl(){console.log("canExpand "+canExpand);  if(canExpand){console.log("HERE:"+expandlist);expandBubble(expandlist.shift())} }, 500);
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
// $(".dropdown").click(function(event)
// {
//     event.stopPropagation();
// });
function keepOpen()
{
    // $(".dropdown").show();
    // console.log("Here");
}
function showBranchValue(newValue)
{
  // alert("Here");
  document.getElementById("Branchrange").innerHTML=newValue;
  initialbranching = newValue;
  // keepOpen();
}
function showDepthValue(newValue)
{
  // alert("Here");
  document.getElementById("Depthrange").innerHTML=newValue;
  initialdepth = newValue;
  // keepOpen();
}
/**
*   A function that is called when a user clicks on the expand bubble option in the context menu
*/
function expandBubble(nextID)
{
    // var finishedtask=false;
    console.log("auto expanding: "+nextID)
    selectedID = nextID;
    try{
        network.selectNodes([nextID]);
    }catch(err){
        console.log("expand "+err)
    }
    // $("#loadingAlert").fadeIn(1000, function() {
    //     // body...
    // });
    var pathtoselectednode=[];
    if(selectedID!=0)
        var pathtoselectednode =[];
    var pathtoselectednodelabels =[]
    console.log("selectedID:"+selectedID)
    console.log("parentlist "+parentlist)

    for(var i = selectedID; i > 0; i = parentlist[i]){
        if(i!=1)
            pathtoselectednode.push(i);
    }
    // for(var i=pathtoselectednode.length-1;i>=0;i--){
    //    pathtoselectednodelabels.push(nodes[pathtoselectednode[i]].label.replace(/ /g,"").replace("\n"," "));
    // }
    // pathtoselectednodelabels.push()
    // pathtoselectednodelabels.splice(pathtoselectednodelabels.indexOf("Contacts"),1);
    // console.log("expand PathTo: " + pathtoselectednodelabels);
    initialdepth = getCookie("depth");
    console.log("expand:"+ initialdepth)
    console.log("expand PathFrom: " + (pathtoselectednode.length+1));
    console.log("expand pathtoselectednode.length:"+(pathtoselectednodelabels.length+1));
    if((pathtoselectednode.length+1)<=2 && !flagHasNodesToLoad){
        var pos=0;
        var branchinglimit = 4;
        console.log("pooooo "+selectedID);
        console.log("pooooo "+nodes);

        console.log("pooooo "+parentlist)
        var thiscolor = nodes[selectedID].color;

        var pathtoselectednodelabels =[]
        console.log("selectedID:"+selectedID)
        console.log("parentlist "+parentlist)

        for(var i = selectedID; i > 0; i = parentlist[i]){
            pathtoselectednode.push(i);
        }

        console.log("PathFrom: " + pathtoselectednode);
        var pos=0;
        var branchinglimit = 4;
        var thiscolor = nodes[selectedID].color;
        for(var i=pathtoselectednode.length-1;i>=0;i--){
           pathtoselectednodelabels.push(nodes[pathtoselectednode[i]].label.replace(/ /g,"").replace("\n"," "));
        }
        console.log(pathtoselectednodelabels);
        // pathtoselectednodelabels.push()
        console.log("PathTo: " + pathtoselectednodelabels);
        pathtoselectednodelabels.splice(pathtoselectednodelabels.indexOf("Contacts"),1);

        var excludelist=[]
        for(var i = 1; i < parentlist.length;i++){
            if(parentlist[i]==selectedID){
                excludelist.push(nodes[i].label.replace(/ /g,"").replace("\n"," "));
            }
        }

        console.log("expand exclude list:"+excludelist);
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
    }else{
         network.selectNodes([0]);
    }
    canExpand=false;
}

function refreshGraph(){
    $("#loadingAlertWarning").fadeOut(1000, function() {
                // body...
    });

    deleteBranch("0");   
    document.cookie="lastrefreshtime="+ Date.now();


}

setInterval(function(){ 
    if(shouldRebuild){
       /**
       *    @var {String} name1 - string that contains the last refresh time
       */
        var name1 = "lastrefreshtime=";
        /**
        *   @var ca1 - Cookie....
        */
        var ca1 = document.cookie.split(';');
        /**
        *   @var datenow - ...
        */
        var datenow ="";
        for(var i = 0; i <ca1.length; i++) {
            var c = ca1[i];
            while (c.charAt(0)==' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name1) == 0) {
                datenow = c.substring(name1.length,c.length);
            }
        }
        // console.log(Date.now()-datenow);
        if(Date.now()-datenow > 60000){
            $("#loadingAlert").fadeOut(1000, function() {
                    // body...
            });
            /**
            *   A function that displays the loading bar
            */
            $("#loadingAlertWarning").fadeIn(1000, function() {
                // body...
            });
            $("#loadingAlertWarning").html("You havent refreshed the Bubble Map in a while. This could mean some of the bubbles are outdated. Would you like to refresh now? <br/><br/><button type='button' class='button btn btn-warning btn-block' onclick='refreshGraph()' >Refresh</button> ");
        }
    }


}, 1000);

function deleteBranch(selectedID){
    if(selectedID!=0 &&  selectedID!=1){
        // parentlist =[0,0,0,2,0,4,0,6,2];
        var deletelist =[]
        var templist = []
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
        parentlist[selectedID]=-1;
        
        network.selectNodes(deletelist);
        network.deleteSelected();


    }else{
        parentlist[0] = -1;
        parentlist[1] = -1;

        for(var i=0;i<parentlist.length;i++){
            console.log(parentlist[i]);
            if(parentlist[i]=="0" || parentlist[i]=="1"  ){
                deleteBranch(i);
            }
        }
       
       // var tempparentdeleted = parentlist.indexOf("0";
       //  console.log(tempparentdeleted)
       //  console.log(parentlist);
        // parentlist[Number(tempparentdeleted)] = -1;
        // console.log(tempparentdeleted+" "+ parentlist[tempparentdeleted]+" "+parentlist);

        // document.cookie = "nodes=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
        // document.cookie = "edges=; expires=Thu, 01 Jan 1970 00:00:00 UTC";

        // document.cookie = "parentlist=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
        selectedID ="0";
        document.cookie="lastselectednode="+selectedID;
        
        nodePosition =0;
        nodes = [
            {id: 0, label: "   ME   ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'#1999d6',highlight:{background:'#1999d6',border:'#1999d6'},hover:{background:'#1999d6',border:'#1999d6'}}},
            {id: 1, label: "  Contacts  ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'purple',highlight:{background:'purple', border:'purple'},hover:{background:'purple', border:'purple'}}}
        ]

        edges = [
            {from: 1, to: 0}
        ]
        expandlist = [];
        parentlist = ["-1","-1"];
        var excludelist =[]
        for(var i = 1; i < parentlist.length;i++){
            if(parentlist[i]==selectedID){
                try{
                    excludelist.push(nodes[i].label.replace(/ /g,"").replace("\n"," "));
                }catch(err){

                }
            }
        }

        parentlist[0] = 0;
        parentlist[1] = 0;


        console.log("exclude list:"+excludelist);
        initialbranching = getCookie("branch");
        if(mocktesting)
            topicRequest = {userId: "mocktesting"+x1, path:[], exclude:excludelist, maxNumberOfTopics:initialbranching};
        else
            topicRequest = {userId: x1, path:[], exclude:excludelist, maxNumberOfTopics:initialbranching};
        // if(!flagHasNodesToLoad){
            try{
                console.log("sending!")
                stompClient.send("/app/request", {}, JSON.stringify(topicRequest));
            }catch(err){
                $("#loadingAlert").fadeOut(1000, function() {
                // body...
                });
                $("#loadingAlertError").fadeIn(1000, function() {
                });
                $("#loadingAlertError").html("Error: We could not talk to the server. Please try again.")
            }
            /**
            *   A function that displays the loading bar
            */
            $("#loadingAlert").fadeIn(1000, function() {
                // body...
            });

        

    }
}