$(document).ready(function(){
	$("#theme").hide();
	$("#userPreferences").hide();
	$("#Saved").hide();
	$("#Error").hide();
	/**
	*	This function adds/removes the css class active for the selected tab
	*/
	$("li[role='presentation']").on("click", function(){
		$("li[role='presentation']").removeClass("active");
		$(this).addClass("active");
		
	});

	$.ajax({
		type: "GET",
		url:"ajax/selectdata.html",
		success: function(data)
		{
			$("#ajax").html(data);
			$("#ques").html("");
			$("#googlesigninButton").attr("onclick","checkGoogle()");
			$("#facebooksignin").attr("onclick","checkFacebook()");
		}

	});
	$("#Loading").fadeIn(1000,function(){

	});
	
	checkDatabase();
	/**
	*	This function updates the css settings selected tab and hides/shows the respective div
	*/
	$("a").on("click", function(){
			var title = $(this).text();
			console.log(title);
			if(title == "Account")
			{
				$("#theme").hide();
				$("#userPreferences").hide();
				$("#accountSettings").show();
			}
			else if(title == "Theme")
			{
				$("#accountSettings").hide();
				$("#userPreferences").hide();
				$("#theme").show();
			} 
			else if(title == "User Preferences")
			{
				$("#theme").hide();
				$("#accountSettings").hide();
				$("#userPreferences").show();
			}
		});

	$("#navColour").css("backgroundColor","#0f4d71");
	$("#bubbleSpaceColour").css("backgroundColor","#FFFFEA");
	$("#sidePanelColour").css("backgroundColor","white");

  	/**
  	*	This function sets up the first number spinner
  	*/
    $('#spinner').spinner({
        min: 2,
        max: 5,
        step: 1
    });
    /**
  	*	This function sets up the second number spinner
  	*/
    $('#spinner2').spinner({
        min: 2,
        max: 40,
        step: 1
    });

    /**
    *	@var {String} value - The value of the selected item in Theme
    */
    var value ="";
    /**
    *	@var {String} component - The component that the value must be applied to 
    */
    var component="";
    /**
    *	@var {JsonObject} themeObject - The object that contains the users theme preferences
    */
    var themeObject={
    	"returnId": "",
    	"user":"",
    	"nav":"",
    	"map":"",
    	"sidePanel":""
    };
    /**
    *	@var {JsonObject} userPreferences - The object that contains the users preferences
    */
    var userPreferences={
    	"returnId": "",
    	"user":"",
    	"branch":0,
    	"depth":0
    };

    var depth=0;
    var branch=0;

$("ul li").on("click",function(){
	value= $(this).attr("data-value");
	component = $(this).attr("data-select");
	console.log("Value"+ value);
	console.log("Comp"+ component);
	if(component == "nav")
	{
		$("#navColour").css("backgroundColor",value);
		themeObject.nav=value;
	}
	else if(component == "map")
	{
		$("#bubbleSpaceColour").css("backgroundColor",value);
		themeObject.map = value;
	}
	else if(component == "panel")
	{
		$("#sidePanelColour").css("backgroundColor",value);
		themeObject.sidePanel =value;
	}
	console.log(themeObject);
});

$("#saveTheme").on("click",function(){

	console.log("Theme settings: " + JSON.stringify(themeObject));

	//Send the data sources object through to backend:
		// var socket = new SockJS('/theme');
		// stompClient = Stomp.over(socket);
		// stompClient.connect({}, function(frame) {
		//     console.log('Connected: ' + frame);
		//     connected = true;
			
			var name= getCookie("name");
			var surname = getCookie("surname");
			var email = getCookie("email");
			console.log("Got cookie: "+ name,surname,email);

			var usr={firstName:name,lastName:surname,gmailId:email};
			themeObject.user = usr;
			//Change theme
			$("#nav").css("backgroundColor",themeObject.nav);
			$("#mynetwork").css("backgroundColor", themeObject.map);
			$(".panel-default").css("backgroundColor",themeObject.sidePanel);
			// stompClient.send("/app/theme", {}, JSON.stringify(themeObject));
			// stompClient.subscribe('/settings/theme', function(Response){
			// 	var response = JSON.parse(Response.body);
			// 	console.log("Response is : "+ response);
		
			// 	if(response.success == true)
			// 	{
			// 		$("#Saved").fadeIn(1000, function() {
			// 	   		setTimeout(function(){$("#Saved").hide(); }, 2000); 	
			// 		});
			// 	}
			// 	else if(response.success == false)
			// 	{
			// 		$("#Error").fadeIn(1000, function() {
			// 	   		setTimeout(function(){$("#Error").hide(); }, 4000);
			// 		});
			// 	}
			// }, function(error) {
		 //    		// display the error's message header:
		 //    		console.log(error.headers.message);
	  // 		});
		// }, 3000);
});

$("#deactivateAccount").on("click", function(){

});



console.log("Depth: "+ $(".depth").val())
}); //End of on load

// window.onload = function()
// {
// 	$("#Loading").fadeIn(1000, function() {
// 	});
// }
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
function checkDatabase()
{
	//Check which sources user is registered for:
		$("#Loading").fadeIn(3000,function(){

		});
		// var socket = new SockJS('/usercheck');
		// stompClient = Stomp.over(socket);
		// stompClient.connect({}, function(frame) {
		//     console.log('Connected: ' + frame);
		//     connected = true;
			
		// 	var name= getCookie("name");
		// 	var surname = getCookie("surname");
		// 	var email = getCookie("email");
		// 	console.log("Got cookie: "+ name,surname,email);
		// 	var usercheck={firstName:name,lastName:surname,gmailId:email};
			
		// 	stompClient.send("/app/usercheck", {}, JSON.stringify(usercheck));
		// 	stompClient.subscribe('/topic/request', function(serverResponse){
		// 		console.log("subscribing")
		// 		var jsonresponse = JSON.parse(serverResponse.body);
		// 		console.log("ServerResponse is : "+ jsonresponse);
		// 		console.log("Server asked if user is registered : "+jsonresponse.isRegistered);
		
		// 		if(jsonresponse.gmailId != null || jsonresponse.gmailId !="")
		// 		{
		// 			$("#tickGoogle").show();
		// 		}
				//DONT DELETE!!!!!!!!!!!!!!

				// if(jsonresponse.facebookId != null || jsonresponse.facebookId !="")
				// {
				// 	$("#tickFacebook").show();
				// }
				// else
				// {
				// 	console.log("Jsonresponse error");
				// }
				// $("#Loading").fadeOut(1000,function(){

				// });
		// 	}, function(error) {
		//     		// display the error's message header:
		//     		console.log(error.headers.message);
	 //  		});
			
		// });
		
}

function saveUserPreferences()
{
	branch = $("#spinner").val();
	console.log("Branch: "+branch);
	depth = $("#spinner2").val();
	console.log("Depth: "+depth);

	// console.log("Save user preferences");
	// var socket = new SockJS('/userPreferences');
	// 	stompClient = Stomp.over(socket);
	// 	stompClient.connect({}, function(frame) {
	// 	    console.log('Connected: ' + frame);
	// 	    connected = true;
			
	// 		var name= getCookie("name");
	// 		var surname = getCookie("surname");
	// 		var email = getCookie("email");
	// 		console.log("Got cookie: "+ name,surname,email);

	// 		var usr={firstName:name,lastName:surname,gmailId:email};
	// 		userPreferences.user = usr;
	// 		stompClient.subscribe('/settings/userPreferences', function(Response){
	// 			var response = JSON.parse(Response.body);
	// 			console.log("Response is : "+ response);
		
	// 			if(response.success == true)
	// 			{
	// 				$("#Saved").fadeIn(1000, function() {
	// 			   		setTimeout(function(){$("#Saved").hide(); }, 2000); 	
	// 				});
	// 			}
	// 			else if(response.success == false)
	// 			{
	// 				$("#Error").fadeIn(1000, function() {
	// 			   		setTimeout(function(){$("#Error").hide(); }, 4000);
	// 				});
	// 			}
	// 		}, function(error) {
	// 	    		// display the error's message header:
	// 	    		console.log(error.headers.message);
	//   		});
	// 	});
	// 	stompClient.send("/app/userPreferences", {}, JSON.stringify(userPreferences));
}

/**
*	@var {JSON object} newDataSources - A JSON object that contains the user ids for the respective selected data sources
*/
var UserRegistrationIdentified = {
	"firstName":"",
	"lastName": "", 
	"authCodes":[],
	"id":""
}
function checkGoogle()
{
	if($("#tickGoogle").is(":visible") == true)
	{
		console.log("Google is selected!");
		//Unselect it
		var gmailAuthCode = {id:null,pimSource:"Gmail",authCode:null};
		$("#tickGoogle").hide();
	}
	else
	{
		//Select it'
		console.log("Gmail is now selected!");
		// googleretrieve();
		function getGmailResponse(gmailUser,response) //Response is authResult['code']
		{
			var gmailAuthCode = {id:gmailUser.getBasicProfile().getEmail(),pimSource:"Gmail",authCode:response};
		}

	}
	UserRegistrationIdentified.authCodes.push(gmailAuthCode);
}
function checkFacebook()
{
	if($("#tickFacebook").is(":visible") == true)
	{
		//Unselect it
		console.log("Facebook is selected!");
		var facebookAuthCode = {id:null,pimSource:"Facebook",authCode:null}
		$("#tickFacebook").hide();
	}
	else
	{
		console.log("Facebook is now selected!");
		onFacebookLogin();
		
		console.log("Response: "+ JSON.stringify(AuthResponse));
			// newDataSources.facebookAccessToken = response.authResponse.accessToken;
		var facebookAuthCode = {"id":AuthResponse.userID,"pimSource":"Facebook","authCode":AuthResponse.accessToken}
		console.log("Facebook Auth code: "+JSON.stringify(facebookAuthCode));
		
	}
	UserRegistrationIdentified.authCodes.push(facebookAuthCode);
}
function SaveAccountChanges()
{
	
	console.log("New sources: " + JSON.stringify(UserRegistrationIdentified));

	//Send the data sources object through to backend:
		// var socket = new SockJS('/datasources');
		// stompClient = Stomp.over(socket);
		// stompClient.connect({}, function(frame) {
		//     console.log('Connected: ' + frame);
		//     connected = true;
			
		// 	var name= getCookie("name");
		// 	var surname = getCookie("surname");
		// 	var email = getCookie("email");
		// 	console.log("Got cookie: "+ name,surname,email);

		// 	UserRegistrationIdentified.firstName = name;
		// 	UserRegistrationIdentified.lastName =surname;
		// 	UserRegistrationIdentified.gmailId=email;
		// 	stompClient.send("/app/datasources", {}, JSON.stringify(UserRegistrationIdentified));
		// 	stompClient.subscribe('/topic/request', function(Response){
		// 		var response = JSON.parse(Response.body);
		// 		console.log("Response is : "+ response);
		
		// 		if(response.success == true)
		// 		{
		// 			$("#Saved").fadeIn(1000, function() {
		// 		   		setTimeout(function(){$("#Saved").hide(); }, 2000); 	
		// 			});
		// 		}
		// 		else if(response.success == false)
		// 		{
		// 			$("#Error").fadeIn(1000, function() {
		// 		   		setTimeout(function(){$("#Error").hide(); }, 4000);
		// 			});
		// 		}
		// 	}, function(error) {
		//     		// display the error's message header:
		//     		console.log(error.headers.message);
	 //  		});
		// });
}
	



