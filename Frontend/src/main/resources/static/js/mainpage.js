/**
*	@var {String} name - varible to hold where the user is current logged in.
*/
var name = "login=";
/**
*	@var {Boolean} rightClick - Variable to see where the user is currentluy right clicking
*/
var rightClick;
/**
*	@var {} ca - Variable to hold all split cookies
*/
var ca = document.cookie.split(';');
/**
*	@var {String} x - Temp Variable to hold the cookie result 
*/
var x="";
/**
*	@var {String} x1 - Temp Variable to hold the cookie result 
*/
var x1="";
/**
*	@var {} menu - Holds the right click context menu, as to be referenced later
*/
var menu;
/**
*	@var {int} selectedID - The currently selected nodes ID
*/
var selectedID;
/**
*	@var {String[]} mockArrayOfData - Mock data for testing purposes.
*/
var mockArrayOfData = ["Amy\nLochner", "Holiday", "Cooking", "Durban"]
/**
*	@var {int} parentlist - List to hold all the parent nodes, if you want to find the parent of node 1 for example, indice the array at [1] to find the parent.
*/
var parentlist =[0];
/**
*	@var {int} expandlist - Temporary array to hold all the nodes that need to be exapanded next
*/
var expandlist = [];
/**
*	@var {int} initialdepth - The intial depth that the graph needs to expand to when the user loads the page
*/
var initialdepth = 2;
/**
*   @var {} allPimIDlist - List to hold all the processed item ID's, used for populating the side bar, first indice is the node ID, second is the PIM data source and third is the processed ID item.
*/
var allPimIDlist = [][][];
allPimIDlist.push([null][null]);//test if this works here??
/**

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
*	A function that creates a json string from an object
*	@param obj An object that needs to be converted into a JSON string
*/
function toJSON(obj) {
    return JSON.stringify(obj, null, 4);
}
/**
*	A JQuery function that allows the sidepanel to be resizeable
*/
$( window ).resize(function() {
    if($(window).width()<=768){
        $("#backfromsidebar").html("<a class='navbar-brand' href='#'><img alt='Brand' style='position:fixed;width:30px;height:30px;top:16px;left:-0px;padding:5px' src='/images/bubblelogo.png'/></a><p class='navbar-text'>PIM</p>")
        $("#help").html("   Help");
        $("#settings").html("   Settings");
        $("#logout").html("   Logout");
    }else{
        $("#help").html("");
        $("#settings").html("");
        $("#logout").html("");
        $("#backfromsidebar").html("<a class='navbar-brand' href='#'><img alt='Brand' style='width:30px;height:30px' src='/images/bubblelogo.png'/></a><p class='navbar-text'>PIM</p>")
    }
});
/**
*	@var nodes - An array of node objects
*/
var nodes;
/**
*	@var {} edges - An array of edge objects
*/
var edges;
/**
*	@var {} network - A variable which holds the created network
*/
var network;
/**
*	Function that is executed when the document has loaded
*/
$(document).ready(function($){
	/**
	*	A function that displays the loading bar
	*/
    $("#loadingAlert").fadeIn(1000, function() {
        // body...
    });
    /**
    *	@var {String} color -  A varible that contains the colour of the text in the bubbles on the BubbleMap
    */
    var color = 'gray';
    /**
    *	@var len - 
    */	
    var len = undefined;
    nodes = [
        {id: 0, label: "    ME    ", group: 0},
        // {id: 1, label: "Cooking", group: 1},
        // {id: 2, label: "Horse", group: 2},
        // {id: 3, label: "Amy \n Lochner", group: 2},
        // {id: 4, label: "COS301", group: 4},
        // {id: 5, label: "Fritz \n Solms", group: 4},
        // {id: 6, label: "Holiday", group: 9},
        // {id: 7, label: "Arno \n Grobler", group: 9},
        // {id: 8, label: "Arno \n Grobler", group: 2}
        ];
        edges = [
            // {from: 0, to: 1},
            // {from: 2, to: 3},
            // {from: 2, to: 0},
            // {from: 5, to: 4},
            // {from: 4, to: 0},
            // {from: 7, to: 6},
            // {from: 6, to: 0},
            // {from: 2, to: 8}
        ]

	    /**
	      *	@var container - A variable that holds the html element that contains the BubbleMap
	    */
	    var container = document.getElementById('mynetwork');
	    /**
	      *	@var data - An object that contains the node and edge objects
	    */
	    var data = {
	       nodes: new vis.DataSet(nodes),
	       edges: new vis.DataSet(edges)
	    };

	    /**
	    *	@var options - An object that contains all settings for the BubbleMap
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
                color: '#7FBEEB ', //rgb(102,167,188)',
                font: {
                    size: 26,
                    color: 'black'
                },
                borderWidth: 1
            },
            edges: {
                width: 1
            }
        };
        network = new vis.Network(container, data, options);
        /**
        *	@var socket - holds the web socket object
        */
      	var socket = new SockJS('/request');
      	/**
      	*	@var stompClient - 
      	*/
      	stompClient = Stomp.over(socket);
      	/**
      	*	A function that connects the stompClient 
      	*/
      	stompClient.connect({}, function(frame) {
        	console.log('Connected: ' + frame);
      	});

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
       *	@var {String} name1 - string that contains the userId
       */
        var name1 = "userId=";
        /**
		*	@var ca1 - Cookie....
		*/
        var ca1 = document.cookie.split(';');
        /**
        *	@var x1 - ...
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
        *	@var topicRequest -  a JSON oject that contains information for a topic request
        */
        topicRequest = {userId: x1, path:[], exclude:[], maxNumberOfTopics:4};
        /**
        *	A function that sends the topicRequest object through the websocket in order to make the request
        */
        setTimeout(function(){
            stompClient.send("/app/request", {}, JSON.stringify(topicRequest));
            /**
            *	@var {integer} selectedID - contains the id of the last selected node
            */
			selectedID=0;
			document.cookie="lastselectednode="+selectedID;
            /**
            *	A function that displays the loading bar
            */
            $("#loadingAlert").fadeIn(1000, function() {
                // body...
            });
            /**
            *	A function that subscribes to a destination that the requests are sent to 
            */
            stompClient.subscribe('/topic/request', function(serverResponse){
                if(serverResponse.getItemIds()!=null){
                    console.log(serverResponse.getItemIds());
                    //Need to use data here to update sidebar 
                    break;
                }
				/**
				*	@var {String} name2 - a variable that contains the data for the last selected node for the cookie
				*/
				var name2 = "lastselectednode=";
				/**
				*	@var ca2 - Splits the document cookie on semicolons into an array
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
				*	@var JSONServerResponse - contains the parsed response from the websocket
				*/
				var JSONServerResponse = JSON.parse(serverResponse.body);
				/**
				*	@var topicsall - an array that contains ids for the ids of the items used by the pims.
				*/
				var topicsall = JSONServerResponse.topicsText;
                 /**
                *   @var pimSourceIds - an array that contains all topics in the JSONServerResponse variable
                */
                var pimSourceIds = JSONServerResponse.pimSourceIds;

                allPimIDlist.push(pimSourceIds);
                console.log("allPimIDlist :"+allPimIDlist); //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++Check here if this works


				/**
				*	@var {int} pos - a variable that contains the position
				*/
				var pos=0;
				/**
				*	@var {int} branchinglimit - contains the length of the topicsall array
				*/
				var branchinglimit = topicsall.length;
				/**
				*	@var thisgroup - ....
				*/
				var thisgroup = nodes[selectedID].group;
				/**
				*	@var tempnodelength - contains the length of the nodes array
				*/
                var tempnodelength = parseInt(nodes.length);
				for(var i=0 ;i<branchinglimit;i++){
					console.log("NodeLength: " + nodes.length + "          selectedID: "+selectedID)
					try {
                        console.log("push "+ tempnodelength)
                        expandlist.push(tempnodelength++);
						data.nodes.add({
							id: nodes.length,
							label: topicsall[pos],
							group: thisgroup
						});
						parentlist.push(selectedID);


					}
					catch (err) {
						alert(err);
					}
					try {
						data.edges.add({
							id: edges.length,
							from: selectedID,
							to: nodes.length
						});
					}
					catch (err) {
						alert(err);
					}
					nodes.push({
						id: nodes.length,
						label: topicsall[pos++],
						group: thisgroup
					})
					edges.push({
						id: edges.length,
						from: selectedID,
						to: nodes.length
					});
				}
                expandBubble(expandlist.shift());
                $("#loadingAlert").fadeOut(1000, function() {
                    // body...
                });
            });

      }, 3000);

    if($(window).width()<=768)
    {
         $("#backfromsidebar").html("<a class='navbar-brand' href='#'><img alt='Brand' style='position:fixed;width:30px;height:30px;top:16px;left:-0px;padding:5px' src='/images/bubblelogo.png'/></a><p class='navbar-text'>PIM</p>")
        $("#help").html("   Help");
        $("#settings").html("   Settings");
        $("#logout").html("   Logout");
    }
    else
    {
        $("#backfromsidebar").html("<a class='navbar-brand' href='#'><img alt='Brand' style='width:30px;height:30px' src='/images/bubblelogo.png'/></a><p class='navbar-text'>PIM</p>")
        $("#help").html("");
        $("#settings").html("");
        $("#logout").html("");
    }
    $("#sidepanel").hide();
    /**
    *	A function that disables the default event that occurs on rightclick event
    */
    document.oncontextmenu = function() {return false;};

    /**
    *	A function that populates the sidepanel with data
    *	@param node - the node that has been selected
    *	@param array - contains the data of the selected node
    */
    function populateSidePanel(node, array)
    {
        var gmailItemRequest = {itemIds:allPimIDlist[selectedID],userId:x1};
        /**
        *   A function that sends the gmailItemRequest object through the websocket in order to make the request
        */
        // setTimeout(function(){
        stompClient.send("/app/request", {}, JSON.stringify(gmailItemRequest));

        // }, 3000);

        $("#accordion").html("");
        if(array.Topic != "Contact")
        {
            $("#sidepanelTitle").html("<h2>"+array.Topic+"</h2>");
        }
        else
        {
            $("#sidepanelTitle").html("<h2>"+array.Name+"</h2>")
        }
        console.log("Title: "+$("#sidepanelTitle").text());
        if((array.hasOwnProperty('Name')))
        {
            $("#accordion").html('<div class="panel panel-default"><div class="panel-heading"><h3 data-toggle="collapse" data-parent="#accordion" href="#collapse1" class="panel-title">Details</h3></div><div id="collapse1" class="panel-collapse collapse"><div id="details" class="panel-body"  style="max-height: 50vh;overflow-y: scroll;"></div></div></div>');
            $("#details").html("Email Address: " + array.emailAddress);
        }
        if((array.hasOwnProperty('Facebook')))
        {
            $("#accordion").html('<div class="panel panel-default"><div class="panel-heading"><h3 data-toggle="collapse" data-parent="#accordion" href="#collapse2" class="panel-title">Facebook</h3></div><div id="collapse2" class="panel-collapse collapse"><div id="facebook" class="panel-body"  style="max-height: 50vh;overflow-y: scroll;"></div></div></div>');
            for(var i = 0 ; i < array.Facebook.length; i++ )
            {
                $("#facebook").append("<div>"+array.Facebook[i]+"</div>");
            }
        }
        if((array.hasOwnProperty('Gmail')))
        {
            $("#accordion").append('<div class="panel panel-default"><div class="panel-heading"><h3 data-toggle="collapse" data-parent="#accordion" href="#collapse3" class="panel-title">Gmail</h3></div><div id="collapse3" class="panel-collapse collapse"><div id="gmail" class="panel-body"  style="max-height: 50vh;overflow-y: scroll;"></div></div></div>');
            for(var i = 0 ; i < array.Gmail.length; i++ )
            {
                $("#gmail").append("<div class='email panel'><h3>"+array.Gmail[i].subject +"</h3><br />"+array.Gmail[i].data+"</div>");
            }
        }
        if((array.hasOwnProperty('Twitter')))
        {
            $("#accordion").append('<div class="panel panel-default"><div class="panel-heading"><h3 data-toggle="collapse" data-parent="#accordion" href="#collapse4" class="panel-title">Twitter</h3></div><div id="collapse4" class="panel-collapse collapse"><div id="twitter" class="panel-body"  style="max-height: 50vh;overflow-y: scroll;"></div></div></div>');
            for(var i = 0 ; i < array.Twitter.length; i++ )
            {
                $("#twitter").html("<div>"+array.Twitter.data+"</div>");
            }
        }
        if((array.hasOwnProperty('LinkedIn')))
        {
            $("#accordion").append('<div class="panel panel-default"><div class="panel-heading"><h3 data-toggle="collapse" data-parent="#accordion" href="#collapse5" class="panel-title">LinkedIn</h3></div><div id="collapse5" class="panel-collapse collapse"><div id="linkedIn" class="panel-body"  style="max-height: 50vh;overflow-y: scroll;"></div></div></div>');
            for(var i = 0 ; i < array.LinkedIn.length; i++ )
            {
                $("#linkedIn").html("<div>"+array[i].data+"</div>");
            }
        }


    }

    /**
    *	@var menu - variable that is assigned the context menu
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
                var thisgroup = nodes[selectedID].group;
                for(var i=pathtoselectednode.length-1;i>=0;i--){
                   pathtoselectednodelabels.push(nodes[pathtoselectednode[i]].label.replace("\n"," "));
                }
                // pathtoselectednodelabels.push()
                console.log("PathTo: " + pathtoselectednodelabels);

                var excludelist=[]
                for(var i = 1; i < parentlist.length;i++){
                    if(parentlist[i]==selectedID){
                        excludelist.push(nodes[i].label.replace("\n"," "));
                    }
                }

                console.log("exclude list:"+excludelist);

                topicRequest = {userId: x1, path:pathtoselectednodelabels, exclude:excludelist, maxNumberOfTopics:4};

				document.cookie="lastselectednode="+selectedID;
                stompClient.send("/app/request", {}, JSON.stringify(topicRequest));
                    // $("#loadingAlert").fadeOut(1000, function() {
                    //     // body...
                    // });
            }

            if(this.label=="Remove Bubble"){
                if(selectedID!=0){
                    // parentlist =[0,0,0,2,0,4,0,6,2];
                    var deletelist =[]
                    var templist = []
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

                    network.selectNodes(deletelist);
                    network.deleteSelected();
                }
            }
        }
	});
	/**
	*	A function that resets the html for certain divs
	*/
    network.on("click", function(){
       // console.log("nodes")
       $("#facebook").html("");
       $("#gmail").html("");
       $("#twitter").html("");
       $("#linkedIn").html("");
       $("#sidepanelTitle").html("");
       $("#sidepanel").hide();

       $("#backfromsidebar").html("<a class='navbar-brand' href='#'><img alt='Brand' style='position:fixed;width:30px;height:30px;top:16px;left:-0px;padding:5px' src='/images/bubblelogo.png'/></a><p class='navbar-text'>PIM</p>")
    });
    /**
    *	A function that handles the doubleClick event on the BubbleMap
    */
    network.on("doubleClick", function(){
       if($(window).width()<=768){
            $("#backfromsidebar").html("<a class='navbar-brand' onclick='hidesidebar()'><span  style='position:fixed;width:30px;height:30px;top:16px;left:-0px;cursor:pointer;padding:5px' class='glyphicon glyphicon-chevron-left' src=''/></a><p class='navbar-text' onclick='hidesidebar()' style='cursor:pointer'>Back</p>")
       }else{
            $("#backfromsidebar").html("<a class='navbar-brand' href='#'><img alt='Brand' style='position:fixed;width:30px;height:30px;top:16px;left:-0px;padding:5px' src='/images/bubblelogo.png'/></a><p class='navbar-text'>PIM</p>")
       }

       console.log(nodes)
       $("#facebook").html("");
       $("#gmail").html("");
       $("#twitter").html("");
       $("#linkedIn").html("");
       $("#sidepanelTitle").html("");
       try{
        var e = window.event;
        var posX = e.clientX;
        var posY = e.clientY - $("nav").height();
        console.log("X: "+ posX);
        console.log("Y: "+ posY);
        var selectedNode = network.getNodeAt({"x": posX, "y": posY});
        }catch(err){

        }
        var s = network.getSelectedNodes();
      
        var horse = {
            "Topic" : "Horse",
            "Facebook" : [
                '<iframe src="https://www.facebook.com/plugins/post.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107109433055059&amp;width=100%" width="100%" height="142" style="border:none;" scrolling="no" frameborder="0" allowTransparency="true"></iframe>',
                '<iframe src="https://www.facebook.com/plugins/comment_embed.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107109433055059%3Fcomment_id%3D107116923054310&amp;include_parent=false" width="100%" height="141" style="border:none;background-color:white;" scrolling="no" frameborder="0" allowTransparency="true"></iframe>',
                '<iframe src="https://www.facebook.com/plugins/post.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107109116388424&amp;width=100%" width="100%" height="142" style="border:none;overflow:hidden" scrolling="no" frameborder="0" allowTransparency="true"></iframe>'
            ],
            "Gmail" :[
                { "subject" : "Confirmation of your ride","data" : "Dear Acuben<br /><br /> We would just like to confirm that you are still coming to the ride you booked for on Tuesday for 6 people. <br /><br />Kind regards <br />Marlene Kruger"}
            ]};
        var cooking = {
            "Topic":"Cooking",
            "Facebook": [
                '<iframe src="https://www.facebook.com/plugins/post.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107115213054481&amp;width=100%" width="100%" height="537" style="border:none;" scrolling="no" frameborder="0" allowTransparency="true"></iframe>',
                // '<iframe src="https://www.facebook.com/plugins/post.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107115213054481&amp;width=100%" width="100%" height="537" style="border:none;overflow:hidden" scrolling="no" frameborder="0" allowTransparency="true"></iframe>',
                '<iframe src="https://www.facebook.com/plugins/post.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107115546387781&amp;width=100%" width="100%" height="553" style="border:none;" scrolling="no" frameborder="0" allowTransparency="true"></iframe>',
                // '<iframe src="https://www.facebook.com/plugins/post.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107115546387781&amp;width=100%" width="100%" height="553" style="border:none;overflow:hidden" scrolling="no" frameborder="0" allowTransparency="true"></iframe>'
            ],
            "Gmail" : [
                {"subject": "New recipe for Fridge cheesecake" , "data" : "Dear member <br /><br />Please find attached to your pamphlet a new recipe. Please try this recipe out before next week froday. <br /><br />Enjoy your day!"}
            ]
        };
        var holiday = {
            "Topic":"Holiday",
            "Gmail" : [
                {"subject": "Holiday" , "data" : "Dear Acuben<br /><br />How was your holiday in Durban last week?<br /><br />Kind Regards <br />Arno Grobler"}
            ]
        };
        var contact = {
            "Topic" : "Contact",
            "Name": "Arno Grobler",
            "emailAddress" : "arnogrobler@hott.com"
        }
        var contact2 = {
            "Topic" : "Contact",
            "Name": "Amy Lochner",
            "emailAddress" : "lochneramy@gmail.com"
        }
         var contact3 = {
            "Topic" : "Contact",
            "Name": "Fritz Solms",
            "emailAddress" : "fritzsolms@cs.up.ac.za"
        }
        var cos = {
            "Topic" : "COS301",
            "Gmail" : [
                {"subject": "COS301 Announcement" , "data" : "Dear Students<br /><br />Please note class will be suspendedd on the 25th July due to unforeseen circumstances. Please use this time to work with your main project group.<br /><br /> Thank you."},
                {"subject": "COS301 Announcement" , "data" : "Dear Students<br /><br />Assignment 2 now due. Please upload as soon as possible!<br /><br />Kind Regards<br />Fritz Solms"},
                {"subject": "COS301 Announcement" , "data" : "Dear Students<br /><br />Lecture notes have been uploaded! Please download asap<br /><br />Kind Regards<br />Fritz Solms"}
            ]
        }


        $("#sidepanel").show();
        if(nodes[s] =="undefined"  || s== null || s=="")
        {
            $("#sidepanel").hide();
            console.log("Got here");
        }
        else if(nodes[s].label =="Horse")
        {
            populateSidePanel(selectedNode, horse);
            $("#breadcrumb").html('<li>Me</li><li>Horse</li>');
        }
        else if(nodes[s].label =="Cooking")
        {
            populateSidePanel(selectedNode, cooking);
            $("#breadcrumb").html('<li>Me</li><li>Cooking</li>');
        }
        else if(nodes[s].label =="Holiday")
        {
            populateSidePanel(selectedNode, holiday);
            $("#breadcrumb").html('<li>Me</li><li>Holiday</li>');
        }
        else if(nodes[s].label =="Arno \n Grobler")
        {
            populateSidePanel(selectedNode, contact);
            $("#breadcrumb").html('<li>Me</li><li>Holiday</li><li>Arno Grobler</li>');
        }
        else if(nodes[s].label =="Amy \n Lochner")
        {
            populateSidePanel(selectedNode, contact2);
            $("#breadcrumb").html('<li>Me</li><li>Horse</li><li>Amy Lochner</li>');
        }
        else if(nodes[s].label =="COS301")
        {
            populateSidePanel(selectedNode, cos);
            $("#breadcrumb").html('<li>Me</li><li>COS301</li>');
        }
        else if(nodes[s].label =="Fritz \n Solms")
        {
            populateSidePanel(selectedNode, contact3);
            $("#breadcrumb").html('<li>Me</li><li>COS301</li><li>Fritz Solms</li>');
        }
        else
        {
            $("#sidepanel").hide();
        }
        console.log("works on right click");
    });

	/**
	*	A function that handles the rightClick event on the BubbleMap 
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

/**
*	A function that hides the sidebar
*/
function hidesidebar()
{
    $("#facebook").html("");
   $("#gmail").html("");
   $("#twitter").html("");
   $("#linkedIn").html("");
   $("#sidepanelTitle").html("");
   $("#sidepanel").hide();
   $("#backfromsidebar").html("<a class='navbar-brand' href='#'><img alt='Brand' style='position:fixed;width:30px;height:30px;top:16px;left:-0px;padding:5px' src='/images/bubblelogo.png'/></a><p class='navbar-text'>PIM</p>")
}
/**
*	A function that is called when a user clicks on the expand bubble option in the context menu
*/
function expandBubble(nextID)
{
    // var finishedtask=false;
    console.log("auto expanding: "+nextID)
    selectedID = nextID;
     network.selectNodes([nextID]);
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
        pathtoselectednode.push(i);
    }

    console.log("PathFrom: " + pathtoselectednode);
    console.log("pathtoselectednode.length+1:"+(pathtoselectednode.length+1));
    if((pathtoselectednode.length+1)<=initialdepth){
        var pos=0;
        var branchinglimit = 4;
        var thisgroup = nodes[selectedID].group;
        for(var i=pathtoselectednode.length-1;i>=0;i--){
           pathtoselectednodelabels.push(nodes[pathtoselectednode[i]].label.replace("\n"," "));
        }
        // pathtoselectednodelabels.push()
        console.log("PathTo: " + pathtoselectednodelabels);

        var excludelist=[]
        for(var i = 1; i < parentlist.length;i++){
            if(parentlist[i]==selectedID){
                excludelist.push(nodes[i].label.replace("\n"," "));
            }
        }

        console.log("exclude list:"+excludelist);

        topicRequest = {userId: x1, path:pathtoselectednodelabels, exclude:excludelist, maxNumberOfTopics:4};

        document.cookie="lastselectednode="+selectedID;
        stompClient.send("/app/request", {}, JSON.stringify(topicRequest));
    }else{
     network.selectNodes([0]);
    }
}