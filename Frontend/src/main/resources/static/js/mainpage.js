$(document).ready(function($){

    var color = 'gray';
    var len = undefined;
    var nodes = [{id: 0, label: "ME"},
            {id: 1, label: "1"},
            {id: 2, label: "2"},
            {id: 3, label: "3"},
            {id: 4, label: "4"},
            {id: 5, label: "5"},
            {id: 6, label: "6"},
            {id: 7, label: "7"},
            {id: 8, label: "8"},
            {id: 9, label: "9"},
            {id: 10, label: "10"},
            {id: 11, label: "11"},
            {id: 12, label: "12"},
            {id: 13, label: "13"},
            {id: 14, label: "14"},
            {id: 15, label: "15"},
            {id: 16, label: "16"},
            {id: 17, label: "17"},
            {id: 18, label: "18"},
            {id: 19, label: "19"},
            {id: 20, label: "20"},
            {id: 21, label: "21"},
            {id: 22, label: "22"},
            {id: 23, label: "23"},
            {id: 24, label: "24"},
            {id: 25, label: "25"},
            {id: 26, label: "26"},
            {id: 27, label: "27"},
            {id: 28, label: "28"},
            {id: 29, label: "29"}
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
        	clickToUse: true,
            nodes: {
                shape: 'ellipse',
                size: 50,
                // style: 'shadow',
                font: {
                    size: 32,
                    color: 'black'
                },
                borderWidth: 1
            },
            edges: {
                width: 1
            }
        };
      var network = new vis.Network(container, data, options);

      network.on("click", function(){
      	console.log("works on right click");
      	// $(this).bind("contextmenu", function (e) {
       //      menu.popup(e);
       //      ax5.util.stopEvent(e);
      	// });
      });

});

    var menu;
    $(document.body).ready(function () {
        menu = new ax5.ui.menu({
            position: "absolute", // default position is "fixed"
            theme: "primary",
            icons: {
                'arrow': '<i class="fa fa-caret-right"></i>'
            },
            items: [
                {
                    icon: '<i class="fa fa-comment"></i>',
                    label: "Menu A",
                    items: [
                        {icon: '<i class="fa fa-hand-peace-o"></i>', label: "Menu A-0"},
                        {icon: '<i class="fa fa-hand-rock-o"></i>',label: "Menu A-1"},
                        {icon: '<i class="fa fa-hand-stop-o"></i>',label: "Menu A-2"}
                    ]
                },
                {
                    icon: '<i class="fa fa-comments"></i>',
                    label: "Menu B",
                    items: [
                        {icon: '<i class="fa fa-pencil-square"></i>', label: "Menu B-0"},
                        {icon: '<i class="fa fa-phone-square"></i>', label: "Menu B-1"},
                        {icon: '<i class="fa fa-plus-square"></i>', label: "Menu B-2"}
                    ]
                }
            ]
        });
 
        // $("#mynetwork").bind("contextmenu", function (e) {
        //     menu.popup(e);
        //     ax5.util.stopEvent(e);
        //     // e || {left: 'Number', top: 'Number', direction: '', width: 'Number'}
        // });
    });
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
function logout()
{

    
    //Facebook logout
    FB.getLoginStatus(function(response) {
        if (response && response.status === 'connected') {
            FB.logout(function(response) {
                window.location.assign('/');
            });
        }
    });


}
// $(document).ready(function(){

// });