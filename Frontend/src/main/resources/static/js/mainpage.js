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

$(document).ready(function($){
    document.oncontextmenu = function() {return false;};
    // $("canvas").click(function(event){
 //     event.preventDefault();
    // });
    var color = 'gray';
    var len = undefined;
    var nodes = [
        // {id: 0, label: "ME", group: 0,color:{background:"#D3D3D3"}},
        // {id: 1, label: "1", group: 0,color:{background:"#D3D3D3"}},
        // {id: 2, label: "2", group: 0,color:{background:"#D3D3D3"}},
        // {id: 3, label: "3", group: 1,color:{background:"#D3D3D3"}},
        // {id: 4, label: "4", group: 1,color:{background:"#D3D3D3"}},
        // {id: 5, label: "5", group: 1,color:{background:"#D3D3D3"}},
        // {id: 6, label: "6", group: 2,color:{background:"#D3D3D3"}},
        // {id: 7, label: "7", group: 2,color:{background:"#D3D3D3"}},
        // {id: 8, label: "8", group: 2,color:{background:"#D3D3D3"}},
        // {id: 9, label: "9", group: 3,color:{background:"#D3D3D3"}},
        // {id: 10, label: "10", group: 3,color:{background:"#D3D3D3"}},
        // {id: 11, label: "11", group: 3,color:{background:"#D3D3D3"}},
        // {id: 12, label: "12", group: 4,color:{background:"#D3D3D3"}},
        // {id: 13, label: "13", group: 4,color:{background:"#D3D3D3"}},
        // {id: 14, label: "14", group: 4,color:{background:"#D3D3D3"}},
        // {id: 15, label: "15", group: 5,color:{background:"#D3D3D3"}},
        // {id: 16, label: "16", group: 5,color:{background:"#D3D3D3"}},
        // {id: 17, label: "17", group: 5,color:{background:"#D3D3D3"}},
        // {id: 18, label: "18", group: 6,color:{background:"#D3D3D3"}},
        // {id: 19, label: "19", group: 6,color:{background:"#D3D3D3"}},
        // {id: 20, label: "20", group: 6,color:{background:"#D3D3D3"}},
        // {id: 21, label: "21", group: 7,color:{background:"#D3D3D3"}},
        // {id: 22, label: "22", group: 7,color:{background:"#D3D3D3"}},
        // {id: 23, label: "23", group: 7,color:{background:"#D3D3D3"}},
        // {id: 24, label: "24", group: 8,color:{background:"#D3D3D3"}},
        // {id: 25, label: "25", group: 8,color:{background:"#D3D3D3"}},
        // {id: 26, label: "26", group: 8,color:{background:"#D3D3D3"}},
        // {id: 27, label: "27", group: 9,color:{background:"#D3D3D3"}},
        // {id: 28, label: "28", group: 9,color:{background:"#D3D3D3"}},
        // {id: 29, label: "29", group: 9,color:{background:"#D3D3D3"}}
        // ];
        {id: 0, label: "    ME    ", group: 0},
        // {id: 1, label: "1", group: 0},
        // {id: 2, label: "2", group: 0},
        // {id: 3, label: "3", group: 1},
        {id: 4, label: "Cooking", group: 1},
        // {id: 5, label: "5", group: 1},
        {id: 6, label: "Amy \n Lochner", group: 2},
        {id: 7, label: "Horse", group: 2},
        // {id: 8, label: "8", group: 2},
        // {id: 9, label: "9", group: 3},
        // {id: 10, label: "10", group: 3},
        // {id: 11, label: "11", group: 3},
        // {id: 12, label: "12", group: 4},
        {id: 13, label: "COS301", group: 4},
        {id: 14, label: "Fritz \nSolms", group: 4},
        // {id: 15, label: "15", group: 5},
        // {id: 16, label: "16", group: 5},
        // {id: 17, label: "17", group: 5},
        // {id: 18, label: "18", group: 6},
        // {id: 19, label: "19", group: 6},
        // {id: 20, label: "20", group: 6},
        // {id: 21, label: "21", group: 7},
        // {id: 22, label: "22", group: 7},
        // {id: 23, label: "23", group: 7},
        // {id: 24, label: "24", group: 8},
        // {id: 25, label: "25", group: 8},
        // {id: 26, label: "26", group: 8},
        // {id: 27, label: "27", group: 9},
        {id: 28, label: "Holiday", group: 9},
        {id: 29, label: "Arno \nGrobler", group: 9}
        ];
        var edges = [{from: 1, to: 0},
            {from: 2, to: 0},
            {from: 4, to: 3},
            {from: 5, to: 4},
            {from: 4, to: 0},
            {from: 7, to: 6},
            {from: 8, to: 7},
            {from: 7, to: 0},
            {from: 10, to: 9},
            {from: 11, to: 10},
            {from: 10, to: 4},
            {from: 13, to: 12},
            {from: 14, to: 13},
            {from: 13, to: 0},
            {from: 16, to: 15},
            {from: 17, to: 15},
            {from: 15, to: 10},
            {from: 19, to: 18},
            {from: 20, to: 19},
            {from: 19, to: 4},
            {from: 22, to: 21},
            {from: 23, to: 22},
            {from: 22, to: 13},
            {from: 25, to: 24},
            {from: 26, to: 25},
            {from: 25, to: 7},
            {from: 28, to: 27},
            {from: 29, to: 28},
            {from: 28, to: 0}
        ]

      // create a network
      var container = document.getElementById('mynetwork');
      var data = {
        nodes: nodes,
        edges: edges
      };
      
        var options = {
            interaction:{
                hover: true,
                hoverConnectedEdges: true,
            },
            physics: {
              stabilization: false
            },
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
            // console.log("Array: " +array[1].topic);
            // var s = array.topic;
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
        network.on("click", function(){
           
           $("#facebook").html("");
           $("#gmail").html("");
           $("#twitter").html("");
           $("#linkedIn").html("");
           $("#sidepanelTitle").html("");

            var e = window.event;
            var posX = e.clientX;
            var posY = e.clientY - $("nav").height();
            console.log("X: "+ posX);
            console.log("Y: "+ posY);
            var selectedNode = network.getNodeAt({"x": posX, "y": posY});
            var s = network.getSelectedNodes();
            // var label = data.node(s);
            console.log("s: "  + s);
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
                    '<iframe src="https://www.facebook.com/plugins/post.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107109433055059&amp;width=500" width="500" height="142" style="border:none;" scrolling="no" frameborder="0" allowTransparency="true"></iframe>',
                    '<iframe src="https://www.facebook.com/plugins/comment_embed.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107109433055059%3Fcomment_id%3D107116923054310&amp;include_parent=false" width="500" height="141" style="border:none;background-color:white;" scrolling="no" frameborder="0" allowTransparency="true"></iframe>',
                    '<iframe src="https://www.facebook.com/plugins/post.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107109116388424&amp;width=500" width="500" height="142" style="border:none;overflow:hidden" scrolling="no" frameborder="0" allowTransparency="true"></iframe>'
                ],
                "Gmail" :[
                    { "subject" : "Confirmation of your ride","data" : "Dear Acuben<br /><br /> We would just like to confirm that you are still coming to the ride you booked for on Tuesday for 6 people. <br /><br />Kind regards <br />Marlene Kruger"}
                ]};
            var cooking = {
                "Topic":"Cooking",
                "Facebook": [
                    '<iframe src="https://www.facebook.com/plugins/post.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107115213054481&width=500" width="500" height="537" style="border:none;overflow:hidden" scrolling="no" frameborder="0" allowTransparency="true"></iframe>',
                    '<iframe src="https://www.facebook.com/plugins/post.php?href=https%3A%2F%2Fwww.facebook.com%2Facuben.cos%2Fposts%2F107115546387781&width=500" width="500" height="553" style="border:none;overflow:hidden" scrolling="no" frameborder="0" allowTransparency="true"></iframe>'
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
                if(s== "0" || s== null || s=="undefined")
                {
                    $("#sidepanel").hide();
                    console.log("Got here");
                }
                else
                    $("#sidepanel").show();
                if(s == "7")
                {
                    populateSidePanel(selectedNode, horse);
                    $("#breadcrumb").html('<li>Me</li><li>Horse</li>');
                }
                else if(s == "4")
                {
                    populateSidePanel(selectedNode, cooking);
                    $("#breadcrumb").html('<li>Me</li><li>Cooking</li>');
                }
                else if(s == "28")
                {
                    populateSidePanel(selectedNode, holiday);
                    $("#breadcrumb").html('<li>Me</li><li>Holiday</li>');
                }
                else if(s == "29")
                {
                    populateSidePanel(selectedNode, contact);
                    $("#breadcrumb").html('<li>Me</li><li>Holiday</li><li>Arno Grobler</li>');
                }
                else if(s == "6")
                {
                    populateSidePanel(selectedNode, contact2);
                    $("#breadcrumb").html('<li>Me</li><li>Horse</li><li>Amy Lochner</li>');
                }
                else if(s == "13")
                {
                    populateSidePanel(selectedNode, cos);
                    $("#breadcrumb").html('<li>Me</li><li>COS301</li>');
                }
                else if(s == "14")
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
            network.selectNodes([network.getNodeAt({"x": posX, "y": posY})]);
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
    $(document.body).ready(function () {
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
                    label: "Hide Bubble",
                    // items: [
                    //     {icon: '<i class="fa fa-pencil-square"></i>', label: "Menu B-0"},
                    //     {icon: '<i class="fa fa-phone-square"></i>', label: "Menu B-1"},
                    //     {icon: '<i class="fa fa-plus-square"></i>', label: "Menu B-2"}
                    // ]
                }
            ],

        });
 
        // $("#mynetwork").bind("contextmenu", function (e) {
        //     menu.popup(e);
        //     ax5.util.stopEvent(e);
        //     // e || {left: 'Number', top: 'Number', direction: '', width: 'Number'}
        // });
    });


var auth2; // The Sign-In object.
var googleUser; // The current user.

var startApp = function() {
  gapi.load('auth2', initSigninV2);
};

var initSigninV2 = function() {
  auth2 = gapi.auth2.init({
      client_id: '570253498384-r14raqpo4lcqpjggmp05h6359dm6ogfo.apps.googleusercontent.com',
      scope: 'profile'
  });
  auth2.isSignedIn.listen(signinChanged);
  if (auth2.isSignedIn.get() == true) {
    auth2.signIn();
  }
  refreshValues();
};

var signinChanged = function (val) {
  console.log('Signin state changed to ', val);
  if(val === true){
      auth2.signOut().then(function () {
        console.log('User signed out.');
    }); 
  }
};

function refreshValues() {
  if (auth2){
    console.log('Refreshing values...');

    googleUser = auth2.currentUser.get();

    console.log(JSON.stringify(googleUser, undefined, 2));
    console.log(auth2.isSignedIn.get());
  }
}

	(function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
  }(document, 'script', 'facebook-jssdk'));
	window.fbAsyncInit = function() {
	  FB.init({
	    appId      : '1051696778242173',
	    cookie     : true,  // enable cookies to allow the server to access 
	                        // the session
	    xfbml      : true,  // parse social plugins on this page
	    version    : 'v2.5' // use graph api version 2.5
	  });
	}
//  function statusChangeCallback(response) {
//     console.log('statusChangeCallback');
//     console.log(response);
//     // The response object is returned with a status field that lets the
//     // app know the current login status of the person.
//     // Full docs on the response object can be found in the documentation
//     // for FB.getLoginStatus().

//         if (response.status === 'connected') {
//         // Logged into your app and Facebook.
//          var accessToken = response.authResponse.accessToken;
//         console.log(response.authResponse);
//         console.log("Connected to facebook, accessToken:"+ response.authResponse);
//         testAPI();
//       } else if (response.status === 'not_authorized') {
//         // The person is logged into Facebook, but not your app.
//         // document.getElementById('status').innerHTML = 'Please log ' +
//         //   'into this app.';
//           console.log("status = not_authorized");
//       } else {
//         // The person is not logged into Facebook, so we're not sure if
//         // they are logged into this app or not.
//         // document.getElementById('status').innerHTML = 'Please log ' +
//         //   'into Facebook.';
//            console.log("status = something else");
//       }

    
// }
// function logout()
// {

//     startApp();
//     //Facebook logout
//     var flag =false;
//     FB.getLoginStatus(function(response) {
//         if (response && response.status === 'connected') {
//             flag = true;
//             FB.logout(function(response) {
//             });
//         }

//     });

//         console.log("facebook:"+ !flag)
//         console.log("google:"+!auth2.isSignedIn.get())
//         // if (flag && !auth2.isSignedIn.get()) {
//         setTimeout(function(){ document.cookie = "login=; expires=Thu, 01 Jan 1970 00:00:00 UTC";window.location.assign('/'); }, 3000);
        	
//         // }
        


// }
// $(document).ready(function(){

// });
