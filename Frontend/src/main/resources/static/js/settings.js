$(document).ready(function(){
	// alert("Here Tonight!");
	var navcolour = getCookie("nav");
	$("#nav").css("backgroundColor",navcolour);
	 document.cookie = "G_AUTHUSER_H=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
	 document.cookie = "G_ENABLED_IDPS=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
	$("#theme").hide();
	$("#userPreferences").hide();
	$("#Saved").hide();
	$("#Error").hide();
	// $("settings").show();
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
			// alert("Ajax success!");
		} 

	});
	$("#Loading").fadeIn(1000,function(){

	});
	$("#oauth2relay298397952").hide();
	// $("#spinner").value = 4
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
			else
			{
				return;
			}
		});

	$("#navColour").css("backgroundColor","#ffffff");
	$("#bubbleSpaceColour").css("backgroundColor","#ffffff");
	$("#sidePanelColour").css("backgroundColor","#ffffff");

  	/**
  	*	This function sets up the first number spinner
  	*/
    $('#spinner').spinner({
        min: 2,
        max: 5,
        step: 1
    }).val(2);
    /**
  	*	This function sets up the second number spinner
  	*/
    $('#spinner2').spinner({
        min: 2,
        max: 40,
        step: 1
    }).val(4);

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
    	"userId":"",
    	"theme":["#0f4d71","#ffffff","#0f4d71"]
    };
    /**
    *	@var {JsonObject} userPreferences - The object that contains the users preferences
    */
    var userPreferences={
    	userId:"",
    	initialDepth:0,
    	initialBranchFactor:0
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
		$("#nav").css("backgroundColor",value);
		themeObject.theme[0]=value;
		$("#navColour").css("backgroundColor",value);
	}
	else if(component == "map")
	{
		themeObject.theme[1] = value;
		$("#bubbleSpaceColour").css("backgroundColor", value);
	}
	else if(component == "panel")
	{
		themeObject.theme[2] =value;
		// if(value!= "#ffffff")
			
		$("#sidePanelColour").css("backgroundColor", value);
	}
	console.log(themeObject);
});

$("#saveTheme").on("click",
	function(){

	console.log("Theme settings: " + JSON.stringify(themeObject));

	//Send the data sources object through to backend:
		var socket = new SockJS('/theme');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
		    console.log('Connected: ' + frame);
		    connected = true;
			
			
			var userId = getCookie("userId");
			console.log("Got cookie: "+ userId);
			themeObject.userId = userId;
			//Change theme
			$("#nav").css("backgroundColor",themeObject.theme[0]);
			// $("#mynetwork").css("backgroundColor", themeObject.theme[1]);
			// $("#sidepanelTitle").css("backgroundColor",themeObject.theme[2]);

			//Set Cookie
			document.cookie ="nav="+themeObject.theme[0];
			document.cookie ="map="+themeObject.theme[1];
			document.cookie ="sidepanel="+themeObject.theme[2];
			stompClient.subscribe('/user/topic/request', function(Response){
				var response = JSON.parse(Response.body);
				console.log("Response is : "+ response);
		
				if(response.code == 0)
				{
					$("#Saved").fadeIn(1000, function() {
				   		setTimeout(function(){$("#Saved").hide(); }, 2000); 	
					});
				}
				else if(response.code == 99 || response.code == 1)
				{
					$("#Error").fadeIn(1000, function() {
				   		setTimeout(function(){$("#Error").hide(); }, 4000);
					});
				}
			}, function(error) {
		    		// display the error's message header:
		    		console.log(error.headers.message);
	  		});
			stompClient.send("/app/theme", {}, JSON.stringify(themeObject));
		});
});

$("#deactivateAccount").on("click", function(){
	var deactivate = {
		userId:"",
		isActive:false
	};
	console.log("Deactivate Account: " + JSON.stringify(deactivate));

	//Send the data sources object through to backend:
		var socket = new SockJS('/deactivate');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
		    console.log('Connected: ' + frame);
		    connected = true;
			var userId = getCookie("userId");
			console.log("Got cookie: "+ userId);
			deactivate.userId = userId;
			
			stompClient.subscribe('/user/topic/request', function(Response){
				var response = JSON.parse(Response.body);
				console.log("Response is : "+ response);
		
				if(response.code == 0)
				{
					document.cookie= "login=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
					window.location.assign('/');
				}
				else if(response.code == 99 || response.code == 1)
				{
					$("#Error").fadeIn(1000, function() {
				   		setTimeout(function(){$("#Error").hide(); }, 4000);
					});
				}
			}, function(error) {
		    		// display the error's message header:
		    		console.log(error.headers.message);
	  		});
			stompClient.send("/app/deactivate", {}, JSON.stringify(deactivate));
		});
});


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
		var socket = new SockJS('/usercheck');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
		    console.log('Connected: ' + frame);
		    connected = true;
			
			var name= getCookie("name");
			var surname = getCookie("surname");
			var email = getCookie("email");
			console.log("Got cookie: "+ name,surname,email);
			var usercheck={firstName:name,lastName:surname,gmailId:email};
			
			stompClient.subscribe('/user/topic/request', function(serverResponse){
				console.log("subscribing")
				var jsonresponse = JSON.parse(serverResponse.body);
				console.log("ServerResponse is : "+ jsonresponse);
				console.log("BOOOOOOOOOOOOO: "+jsonresponse.isRegistered);
		
				if(jsonresponse.gmailId != null || jsonresponse.gmailId !="")
				{
					console.log("Ticked google");
					$("#tickGoogle").show();

					// console.log("Google button: "+$("#googlesigninButton").html());
					$("#googlesigninButton").html("<span class='fa fa-google'></span> <span id='g' style='font-size:11pt'>Remove Gmail</span>");

				}
				else
				{
					console.log("Else blockk");
					//startApp();
				}
				//DONT DELETE!!!!!!!!!!!!!!

				// if(jsonresponse.facebookId != null || jsonresponse.facebookId !="")
				// {
				// 	$("#tickFacebook").show();
				// }
				// else
				// {
				// 	console.log("Jsonresponse error");
				// }
				$("#Loading").fadeOut(1000,function(){

				});
			}, function(error) {
		    		// display the error's message header:
		    		console.log(error.headers.message);
	  		});
			stompClient.send("/app/usercheck", {}, JSON.stringify(usercheck));
			
		});
		
}

function saveUserPreferences()
{
	branch = $("#spinner").val();
	console.log("Branch: "+branch);
	depth = $("#spinner2").val();
	console.log("Depth: "+depth);

	if(branch != null || branch!="")
	{
		userPreferences.initialBranchFactor = branch;
	}
	if(depth != null || depth != "")
	{
		userPreferences.initialDepth = depth;
	}
	userPreferences.userId = getCookie("userId");
	console.log("Save user preferences");
	var socket = new SockJS('/mapsettings');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
		    console.log('Connected: ' + frame);
		    connected = true;			
			stompClient.subscribe('/user/topic/request', function(Response){
				var response = JSON.parse(Response.body);
				console.log("Response is : "+ response);
		
				if(response.code == 0)
				{
					$("#Saved").fadeIn(1000, function() {
				   		setTimeout(function(){$("#Saved").hide(); }, 2000); 	
					});
					document.cookie = "depth="+ depth;
					document.cookie = "branch="+branch;
				}
				else if(response.code == 99 || response.code ==1)
				{
					$("#Error").fadeIn(1000, function() {
				   		setTimeout(function(){$("#Error").hide(); }, 4000);
					});
				}
			}, function(error) {
		    		// display the error's message header:
		    		console.log(error.headers.message);
	  		});
			stompClient.send("/app/mapsettings", {}, JSON.stringify(userPreferences));
		});
}

/**
*	@var {JSON object} newDataSources - A JSON object that contains the user ids for the respective selected data sources
*/
var UpdateSourcesObject = {
	userId: "", 
	authcodes:[]
}
var gmailUser;
var refreshValues = function() {
  if (auth2){
  console.log('Refreshing values...');

  gmailUser = auth2.currentUser.get();

  console.log("GmailUser: "+JSON.stringify(gmailUser, undefined, 2));
  console.log(auth2.isSignedIn.get());
  return auth2.isSignedIn.get();
  }
}
function checkGoogle()
{
	startApp();
	if($("#tickGoogle").is(":visible") == true)
	{
		console.log("Google is selected!");
		//Unselect it
		$("#tickGoogle").hide();
		$("#googlesigninButton").html("<span class='fa fa-google'></span> <span id='g' style='font-size:11pt'>Gmail</span>");

		//startApp();
		//gapi.auth2.getAuthInstance().signOut();
		var gmailEmail = getCookie("email");
		var gmailAuthCode = {id:gmailEmail,pimSource:"Gmail",authCode:""};
		UpdateSourcesObject.authcodes.push(gmailAuthCode);
		console.log("GmailAuthCode: " + JSON.stringify(gmailAuthCode));

	}
	else
	{
		//Select it'
		googleretrieve();
		setTimeout(function(){
			console.log("Timeout");
		},5000);
		console.log("Gmail is now selected!");

		//gapi.auth2.getAuthInstance().signOut();
		// googleretrieve();
		// getGmailResponse(gmailUser,authCodes[2]);
		// function getGmailResponse(gmailUser,response) //Response is authResult['code']
		// {
			
			
			$("#Loading").fadeIn(1000, function() { 
			});
				// while(refreshValues() == false)
				// {
				// 	console.log("While");
				// }
				setTimeout(function(){
					//refreshValues();

					// if(gmailUser != null)
					// 	var gmailAuthCode = {id:gmailUser.wc.hg,pimSource:"Gmail",authCode:gmailUser.hg.access_token};
					// console.log(gmailAuthCode);
					// UpdateSourcesObject.authcodes.push(gmailAuthCode);
			 		$("#Loading").fadeOut(1000, function(){}); 	
			 		$("#tickGoogle").show();
			 		$("#googlesigninButton").html("<span class='fa fa-google'></span> <span id='g' style='font-size:11pt'>Remove Gmail</span>");
					
				},5000);
			// },5000);
		// }

	}
	//console.log("User reg obj: "+ UpdateSourcesObject);
}
var facebookAuthCode;
function checkFacebook()
{
	if($("#tickFacebook").is(":visible") == true)
	{
		//Unselect it
		console.log("Facebook is selected!");
		var userId = getCookie("userIdFaceebook");
		var facebookAuthCode = {id:AuthResponse.userID,pimSource:"Facebook",authCode:null}
		$("#tickFacebook").hide();
		UpdateSourcesObject.authcodes.push(facebookAuthCode);
	}
	else
	{
		console.log("Facebook is now selected!");
		// onFacebookLogin();
		// function onFacebookLogin()
		// {
		  console.log("onFacebookLogin");
		  FB.login(function(response) {
			if (response.authResponse) {
			   AuthResponse = response.authResponse;
			  facebookAuthCode= {"id":AuthResponse.userID,"pimSource":"Facebook","authCode":AuthResponse.accessToken};
			  console.log(response.authResponse);
			
			  showtick();
			}
			FB.getLoginStatus(function(response) {
			  statusChangeCallback(response);
					
			});
		  });
		// }
		setTimeout(function(){
		console.log("Response: "+ JSON.stringify(AuthResponse));
			// newDataSources.facebookAccessToken = response.authResponse.accessToken;
		 // = {"id":AuthResponse.userID,"pimSource":"Facebook","authCode":AuthResponse.accessToken}
		console.log("Facebook Auth code: "+JSON.stringify(facebookAuthCode));
		UpdateSourcesObject.authcodes.push(facebookAuthCode);
			
		},5000);
		
	}
	
}
function SaveAccountChanges()
{
	
	console.log("New sources: " + JSON.stringify(UpdateSourcesObject));
	UpdateSourcesObject.userId = getCookie("userId");
	//console.log("Authocodes length:"+ UpdateSourcesObject.authcodes.length);
	//Send the data sources object through to backend:
		var socket = new SockJS('/datasources');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
		    console.log('Connected: ' + frame);
		    connected = true;
			
			stompClient.subscribe('/user/topic/request', function(Response){
				var response = JSON.parse(Response.body);
				console.log("Response is : "+ response);
		
				if(response.code == 0)
				{
					$("#Saved").fadeIn(1000, function() {
				   		setTimeout(function(){$("#Saved").hide(); }, 2000); 	
					});
				}
				else if(response.code == 99 || response.code == 1)
				{
					$("#Error").fadeIn(1000, function() {
				   		setTimeout(function(){$("#Error").hide(); }, 4000);
					});
				}
			}, function(error) {
		    		// display the error's message header:
		    		console.log(error.headers.message);
	  		});
			stompClient.send("/app/datasources", {}, JSON.stringify(UpdateSourcesObject));
		});
		//UpdateSourcesObject.authcodes = [];
}
	



