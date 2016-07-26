var name = "login=";
var rightClick;
var ca = document.cookie.split(';');
// console.log(ca)
for(var i = 0; i <ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0)==' ') {
        c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
        var x = c.substring(name.length,c.length);
    }
}

    if(x!="1"){
        window.location.assign('/');
    }
function toJSON(obj) {
    return JSON.stringify(obj, null, 4);
}
$( window ).resize(function() {
    if($(window).width()<=700){
        $("#help").html("   Help");
        $("#settings").html("   Settings");
        $("#logout").html("   Logout");
    }else{
        $("#help").html("");
        $("#settings").html("");
        $("#logout").html("");
    }
});
var nodes, edges, network;
$(document).ready(function($){
    if($(window).width()<=700){
        $("#help").html("   Help");
        $("#settings").html("   Settings");
        $("#logout").html("   Logout");
    }else{
        $("#help").html("");
        $("#settings").html("");
        $("#logout").html("");
    }
    $("#sidepanel").hide();
    document.oncontextmenu = function() {return false;};
    // $("canvas").click(function(event){
 //     event.preventDefault();
    // });
    var color = 'gray';
    var len = undefined;
    nodes = [
        {id: 0, label: "    ME    ", group: 0},
        {id: 1, label: "Cooking", group: 1},
        {id: 2, label: "Horse", group: 2},
        {id: 3, label: "Amy \n Lochner", group: 2},
        {id: 4, label: "COS301", group: 4},
        {id: 5, label: "Fritz \n Solms", group: 4},
        {id: 6, label: "Holiday", group: 9},
        {id: 7, label: "Arno \n Grobler", group: 9},
        {id: 8, label: "Arno \n Grobler", group: 2}
        ];
        edges = [
            {from: 1, to: 0},
            {from: 2, to: 3},
            {from: 2, to: 0},
            {from: 5, to: 4},
            {from: 4, to: 0},
            {from: 7, to: 6},
            {from: 6, to: 0},
            {from: 2, to: 8}
        ]

      // create a network
      var container = document.getElementById('mynetwork');
      var data = {
        nodes: new vis.DataSet(nodes),
        edges: new vis.DataSet(edges)
      };
      
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

        function populateSidePanel(node, array)
        {

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
        var network = new vis.Network(container, data, options);

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
                    // items: [
                    //     {icon: '<i class="fa fa-hand-peace-o"></i>', label: "Menu A-0"},
                    //     {icon: '<i class="fa fa-hand-rock-o"></i>',label: "Menu A-1"},
                    //     {icon: '<i class="fa fa-hand-stop-o"></i>',label: "Menu A-2"}
                    // ]
                },
                {
                    icon: '<i class="fa fa-comments"></i>',
                    label: "Remove Bubble",
                    // items: [
                    //     {icon: '<i class="fa fa-pencil-square"></i>', label: "Menu B-0"},
                    //     {icon: '<i class="fa fa-phone-square"></i>', label: "Menu B-1"},
                    //     {icon: '<i class="fa fa-plus-square"></i>', label: "Menu B-2"}
                    // ]
                }
            ],
            onClick: function(){
                if(this.label=="Expand Bubble"){
                    var currentNodeID = nodes.length;
                    console.log("size"+currentNodeID)
                    var pos=0;
                    console.log(this.label);
                    var branchinglimit = 4;
                    var thisgroup = nodes[selectedID].group;
                    for(var i=0 ;i<branchinglimit;i++){
                        try {
                            data.nodes.add({
                                id: nodes.length,
                                label: mockArrayOfData[pos % branchinglimit],
                                group: thisgroup
                            });

                        }
                        catch (err) {
                            alert(err);
                        }
                        console.log()
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
                            label: mockArrayOfData[pos++ % branchinglimit],
                            group: thisgroup
                        })
                        edges.push({
                            id: edges.length++,
                            from: selectedID,
                            to: nodes.length++
                        });
                    }
                }

                if(this.label=="Remove Bubble"){
                    // console.log(this.label);
                    // var e = window.event;
                    // var posX = e.clientX;
                    // var posY = e.clientY - $("nav").height();
                    // console.log("X: "+ posX);
                    // console.log("Y: "+ posY);
                    // var selectednode = network.getNodeAt({"x": posX, "y": posY});
                    var allnodes = [];
                    var visited=[];
                    allnodes.push(selectedID);
                    while(allnodes.length>0){
                        console.log("Node clicked on :" + selectedID);
                        // network
                        var alledges = network.getSelectedEdges();
                        
                        // allnodes.push(selectedID);
                        for(var i=0;i<alledges.length;i++){
                            var node = network.getConnectedNodes(alledges[i]);
                            for(var j=0;j<node.length;j++){
                                console.log(node[j]+">"+ selectedID)
                                if(node[j]> selectedID){
                                    allnodes.push(node[j]);
                                    
                                }
                            }
                        }
                        visited.push(selectedID);
                        console.log(visited);
                            allnodes.splice(0,1);
                            selectedID = allnodes[0];
                        if(selectedID!=undefined){
                            network.selectNodes([selectedID]);
                        }


                    }
                    
                    network.selectNodes(visited);
                    network.deleteSelected();

                    //Loop through all nodes of selected node
                    //get their children nodes
                    //check if there are no chilren nodes
                    //call function recursively on children nodes 
                    
                    // console.log(nodes.length)
                        }
                    }
        });
        network.on("click", function(){
           // console.log("nodes")
           $("#facebook").html("");
           $("#gmail").html("");
           $("#twitter").html("");
           $("#linkedIn").html("");
           $("#sidepanelTitle").html("");
           $("#sidepanel").hide();
        });
        network.on("doubleClick", function(){
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
            // var label = data.node(s);
            console.log("s: "  + s);
            // console.log("+++++++++++++"+nodes[s].label )
            var alledges = network.getSelectedEdges();
            var allnodes = []
            for(var i=0;i<alledges.length;i++){
                var node = network.getConnectedNodes(alledges[i]);
                console.log(node);
                for(var j=0;j<node.length;j++){
                    allnodes.push(node[j]);
                }
            }
            network.selectNodes(allnodes);
            // console.log(nodes.length);
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



// $(document).click(function(event) {
//     var text = $(event.target).text();
//     console.log($(event.target));
// });

    var menu;
    var selectedID;
    var mockArrayOfData = ["Amy \n Lochner", "Holiday", "Cooking", "Durban"]
    