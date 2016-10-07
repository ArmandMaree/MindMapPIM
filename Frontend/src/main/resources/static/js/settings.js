/**
*   @var {String} navbarReloadTextExpanded - Hold the text that updates whether the side bar should have an expanded default look or the condensed look for mobiles.
*/
var navbarReloadTextExpanded ="<a class='navbar-brand' href='/'><img alt='Brand' style='width:30px;height:30px' src='/images/bubblelogo3.png' onmouseover='hover(this);' onmouseout='unhover(this);'/></a><p class='navbar-text' id='navbartitle'><span id='navbartitle' style='font-family: 'Pacifico', cursive;'>unclutter</span></p>";
/**
*   @var {String} navbarReloadTextCondensed - Hold the text that updates whether the side bar should have an expanded default look or the condensed look for mobiles.
*/
var navbarReloadTextCondensed ="<a class='navbar-brand' href='/'><img alt='Brand' style='position:fixed;width:30px;height:30px;top:16px;padding:5px' src='/images/bubblelogo3.png' onmouseover='hover(this);' onmouseout='unhover(this);'/></a><p id='navbartitle' class='navbar-text' ><span id='navbartitle' style='font-family: 'Pacifico', cursive;'>unclutter</span></p>";
/**
*   A function that changes the brands icon on hover
*/
function hover(element) {
    element.setAttribute('src', '/images/bubblelogo.png');
}
/**
*   A function that changes the brands icon on hover
*/
function unhover(element) {
    element.setAttribute('src', '/images/bubblelogo3.png');
}
/**
*   A function that resizes the sidebar
*/
$( window ).resize(function() {
    if($(window).width()<=768){
        $("#backfromsidebar").html(navbarReloadTextCondensed)
        $("#help").html("   Help");
        $("#help").css('font-family','Raleway');
        $("#help").css('font-size','14pt');

        $("#logout").html("   Logout");
        $("#logout").css('font-family','Raleway');
        $("#logout").css('font-size','14pt');
    }else{
        $("#help").html("");
        $("#logout").html("");
        $("#backfromsidebar").html(navbarReloadTextExpanded)
    }
});
$(document).ready(function(){

	var navcolour = getCookie("nav");
	$("#nav").css("backgroundColor",navcolour);
	document.cookie = "G_AUTHUSER_H=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
	document.cookie = "G_ENABLED_IDPS=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
	$("#theme").hide();
	$("#userPreferences").hide();
	$("#Saved").hide();
	$("#Error").hide();
	if(getCookie("persistMap") == true)
	{
		$("reloadGraph").attr("checked",true);
	}
	
	$("li[role='presentation']").on("click", function(){
		$("li[role='presentation']").removeClass("active");
		$(this).addClass("active");
		 
	});

	/**
	*	Ajax call to display the data sources
	*/
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
	/**
	*	Function that displays the loading banner
	*/
	$("#Loading").fadeIn(1000,function(){

	});
	/**
	*	Function that adjusts the display for mobile devices
	*/
	if($(window).width()<=768){
        $("#backfromsidebar").html(navbarReloadTextCondensed)
        $("#help").html("   Help");
        $("#help").css('font-family','Raleway');
        $("#help").css('font-size','14pt');

        $("#logout").html("   Logout");
        $("#logout").css('font-family','Raleway');
        $("#logout").css('font-size','14pt');
    }else{
        $("#help").html("");
        $("#logout").html("");
        $("#backfromsidebar").html(navbarReloadTextExpanded)
    }	
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
	/**
	*	Changes the colour of the drop down button according to the user's settings
	*/
	$("#navColour").css("backgroundColor",getCookie("nav"));
	/**
	*	Changes the colour of the drop down button according to the user's settings
	*/
	$("#bubbleSpaceColour").css("backgroundColor",getCookie("map"));
	/**
	*	Changes the colour of the drop down button according to the user's settings
	*/
	$("#sidePanelColour").css("backgroundColor",getCookie("sidepanel"));
  	/**
  	*	This function sets up the first number spinner
  	*/

    $('#spinner').spinner({
        min: 2,
        max: 5,
        step: 1
    }).val(4);
    /**
  	*	This function sets up the second number spinner
  	*/
    $('#spinner2').spinner({
        min: 2,
        max: 40,
        step: 1
    }).val(2);


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
		"theme":[getCookie("nav"),getCookie("map"),getCookie("sidepanel")]
	};
	/**
	*	@var {JsonObject} userPreferences - The object that contains the users preferences
	*/
	var userPreferences={
		userId:"",
		initialDepth:0,
		initialBranchFactor:0,
		persistMap:true
	};
	/**
	*	Function that obtains the new theme selected by the user and constructs the object to send via the websocket
	*/
	$("ul li").on("click",function(){
		value= $(this).attr("data-value");
		component = $(this).attr("data-select");
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
			$("#sidePanelColour").css("backgroundColor", value);
		}
		console.log(themeObject);
	});

	/**
	*	Function that is called when the user clicks the save button on the Theme page
	*/
	$("#saveTheme").on("click",
		function(){
			var socket = new SockJS('/theme');
			stompClient = Stomp.over(socket);
			stompClient.connect({}, function(frame) {
			    console.log('Connected: ' + frame);
			    connected = true;
				var userId = getCookie("userId");
				themeObject.userId = userId;
				$("#nav").css("backgroundColor",themeObject.theme[0]);
				document.cookie ="nav="+themeObject.theme[0];
				document.cookie ="map="+themeObject.theme[1];
				document.cookie ="sidepanel="+themeObject.theme[2];
		        document.cookie = "mustreload=true";

				stompClient.subscribe('/user/topic/request', function(Response){
					var response = JSON.parse(Response.body);
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
			    		console.log(error.headers.message);
		  		});
				stompClient.send("/app/theme", {}, JSON.stringify(themeObject));
			});
	});
	/**
	*	@var {JSONObject} deactivate - The object that stores required details in order to deactivate the account
	*/
	var deactivate = {
		userId:"",
		isActive:false
	};
	/**
	*	Function that is called when the user clicks on the Deactivate button on the account settings page
	*/
	$("#deactivateAccount").on("click", function(){
		    document.cookie = "mustreload=true";
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
			    		console.log(error.headers.message);
		  		});
				stompClient.send("/app/deactivate", {}, JSON.stringify(deactivate));
			});
	});
checkDatabase();


}); //End of on load
/**
*	Function is called when the page loads.
*/
window.onload = function()
{
	$("#Loading").show();
	checkDatabase();
	console.log("On window load");
}
/**
*	Function that checks the database to determine which data sources the user has selected 
*/
function checkDatabase()
{
		console.log("Checking database");
		// $("#Loading").fadeIn(3000,function(){

		// });
		var pimIds = JSON.parse(getCookie("pimIds"));
		
		for(var i = 0 ; i < pimIds.length; i++)
		{
			var current = pimIds[i];
			var id = "#tick" +  current.pim.charAt(0).toUpperCase() + current.pim.substr(1).toLowerCase();			
			if(current.pim == "gmail")
			{
				$(id).show();
				$("#googlesigninButton").html("<span class='fa fa-google'></span> <span id='g' style='font-size:11pt'>Remove Gmail</span>");
			}
			if(current.pim == "facebook")
			{
				$(id).show();
				$("#facebooksignin").html("<span class='fa fa-facebook'></span> <span id='g' style='font-size:11pt'>Remove Facebook</span>");
			}
		} 
		$("#Loading").fadeOut(1000,function(){

		});
		
}
/**
*	@var {Integer} depth - Temporarily stores the value obtained from the spinner
*/
var depth=0;
/**
*	@var {Integer} branch - Temporarily stores the value obtained from the spinner
*/
var branch=0;
/**
*	Function that is called when the user clicks Save on the user preferences page
*/
var persistMap;
function saveUserPreferences()
{
    document.cookie = "mustreload=true";
	branch = $("#spinner").val();
	console.log("Branch: "+branch);
	depth = $("#spinner2").val();
	console.log("Depth: "+depth);
	if($("#reloadGraph").attr("checked") == "checked")
	{
		userPreferences.persistMap= true;
	}
	else
	{
		userPreferences.persistMap=false;
	}
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
				document.cookie = "persistMap"+ userPreferences.persistMap;

			}
			else if(response.code == 99 || response.code ==1)
			{
				$("#Error").fadeIn(1000, function() {
			   		setTimeout(function(){$("#Error").hide(); }, 4000);
				});
			}
		}, function(error) {
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
/**
*	@var {JSONObject} gmailUser - stores all information about a gmail user
*/
var gmailUser;
/**
*	Function that obtains the new gmail user
*/
var refreshValues = function() {
  if (auth2){
  console.log('Refreshing values...');
  gmailUser = auth2.currentUser.get();
  return auth2.isSignedIn.get();
  }
}
/**
*	@var {Array} pimIds -  an array that stores the id of each corresponding data source
*/
var pimIds = getCookie("pimIds");
function findPim(name)
{
	for(var i = 0; i < pimIds.length;i++)
	{
		if(pimIds[i].pim == name)
			return pimIds[i].uId;
	} 
}
/**
*	Function that is called when a user clicks on the Gmail button on the account settings page.
*/
function checkGoogle()
{
	startApp();
	if($("#tickGmail").is(":visible") == true)
	{
		$("#tickGmail").hide();
		$("#googlesigninButton").html("<span class='fa fa-google'></span> <span id='g' style='font-size:11pt'>Gmail</span>");
		var gmailEmail =findPim("gmail");
		var gmailAuthCode = {id:gmailEmail,pimSource:"gmail",authCode:""};
		UpdateSourcesObject.authcodes.push(gmailAuthCode);
		console.log("GmailAuthCode: " + JSON.stringify(gmailAuthCode));
	}
	else
	{
		googleretrieve();
		setTimeout(function(){
			console.log("Timeout");
		},5000);
		console.log("Gmail is now selected!");
			$("#Loading").fadeIn(1000, function() { 
			});

			setTimeout(function(){
		 		$("#Loading").fadeOut(1000, function(){}); 	
		 		$("#tickGmail").show();
		 		$("#googlesigninButton").html("<span class='fa fa-google'></span> <span id='g' style='font-size:11pt'>Remove Gmail</span>");
				
			},2000);
	}
}
/**
*	@var {JSONObject} facebookAuthCode - An object that stores the information needed to start or stop the pollers
*/
var facebookAuthCode;
/**
*	Function that is called when the Facebook button is clicked on the account settings page
*/
function checkFacebook()
{
	if($("#tickFacebook").is(":visible") == true)
	{
		var userId =findPim("facebook");
		var facebookAuthCode = {id:AuthResponse.userID,pimSource:"facebook",authCode:null}
		$("#tickFacebook").hide();
		UpdateSourcesObject.authcodes.push(facebookAuthCode);
	}
	else
	{
		FB.login(function(response) {
			if (response.authResponse) {
			   AuthResponse = response.authResponse;
			  facebookAuthCode= {"id":AuthResponse.userID,"pimSource":"facebook","authCode":AuthResponse.accessToken,"expireTime":AuthResponse.expiresIn};
			  console.log(response.authResponse);
			  showtick();
			}
			FB.getLoginStatus(function(response) {
			  statusChangeCallback(response);
					
			});
		});
		setTimeout(function(){
		UpdateSourcesObject.authcodes.push(facebookAuthCode);	
		},2000);
	}
}
/**
*	Function that is called when the user clicks on save on the account settings page
*/
function SaveAccountChanges()
{
	document.cookie = "mustreload=true";
	UpdateSourcesObject.userId = getCookie("userId");
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
	  		console.log(error.headers.message);
		});
		stompClient.send("/app/datasources", {}, JSON.stringify(UpdateSourcesObject));
	});
		
}

	



