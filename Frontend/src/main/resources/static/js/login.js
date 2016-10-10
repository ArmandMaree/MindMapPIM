/**
*	@var {String} force_prompt - whether or not the google api should retrieve a refresh token
*/
var force_prompt = 'force'
/**
*	@var {Obj} auth2 - The sign-in object
*/
var auth2; 
/**
* 	@var {Obj} googleUser - The current user
*/
var googleUser;
/**
* 	@var {Obj[]} authCodes - Array to hold all of the authCodes
*/ 
var authCodes = [];
/** 
* 	@var {} gmailUser - The object that contains all information about the signed-in Gmail user
*/
var gmailUser= null;
/**
* 	@var {Boolean} connected - indicates whether or not the client is connected to the websocket
*/
var connected = false;
/**
*	Function that prevents auto sign in for Gmail
*/
window.onbeforeunload = function(e){
  gapi.auth2.getAuthInstance().signOut();
};
/**
 * Starts the log in process by calling the google api.
 */
var startApp = function() {
  gapi.load('auth2', initSigninV2);
};
/**
 * Initialises the google api with the specific app ID. Attaches code and callbacks to a custom made button.
 * @param {id} googleLogin - ID of the element for the custom button
 */
var initSigninV2 = function() {
  auth2 = gapi.auth2.init({
	  client_id: '570253498384-r14raqpo4lcqpjggmp05h6359dm6ogfo.apps.googleusercontent.com',
	  scope: 'profile email https://www.googleapis.com/auth/gmail.labels https://www.googleapis.com/auth/gmail.readonly'
  });
/**
* Function to attach the google api to the custom button
* @param {string} customButton - Custom button you want to attch the google API code to.
* @param {string} additionalParams - Any additional parameters needed.
* @param {function} successFunc - attach a callback function if request succeded
* @param {function} failFunc - attach a callback function if request fails
*/
auth2.attachClickHandler('googleLogin', {}, onSuccess, onFailure)
auth2.isSignedIn.listen(signinChanged);
if (auth2.isSignedIn.get() == true) {
	auth2.signIn();
}
refreshValues();
};
/**
* Function to send a User Registration Object through to the frontend application
*/
var sendUserReg = function(){
	if(getCookie("facebookId") != "")
	{
		authCodes.push({id:getCookie("facebookId"),pimSource:"facebook",authCode:getCookie("fAT"),expireTime:getCookie("fExpireTime")});
	}
	$("#loadingAlert").fadeIn(1000, function() {
	});
	var socket = new SockJS('/hello');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
	    connected = true;
	  	var userReg = {};
		userReg={firstName:getCookie("name"),lastName:getCookie("surname"),authCodes:authCodes};
	  
		stompClient.subscribe('/user/topic/greetings', function(serverResponse){
			var jsonresponse = JSON.parse(serverResponse.body);
			document.cookie="userId="+jsonresponse.userId;
			document.cookie="branch="+jsonresponse.branchingFactor;
			document.cookie="depth="+jsonresponse.initialDepth;
			document.cookie = "nav="+jsonresponse.theme[0];
			document.cookie = "map="+jsonresponse.theme[1];
			document.cookie = "sidepanel="+jsonresponse.theme[2];
			document.cookie = "persistMap="+jsonresponse.persistMap;
			document.cookie = "pimIds=" +JSON.stringify(jsonresponse.pimIds);
			
			$("#loadingAlert").fadeOut(1000, function() {
				
			});
			window.location.assign('/help');
		}, function(error) {
	    		console.log(error.headers.message);
  		});
			stompClient.send("/app/hello", {}, JSON.stringify(userReg));
	});

}
/**
 * A function that checks where any sign in activity for Google has happened and responds.
 * @param {boolean} val - true if a sign in has occured.
 */
var signinChanged = function (val) {
  console.log('Signin state changed to ', val);
  if(val === true){
	 $("#googleLogin").animate({
		  top: '100px',
		  opacity: '0.0'

	  });
	  $("#facebookLogin").animate({
		  top: '100px',
		  opacity: '0.0'

	  });
	  $("#errr").animate({
		  top: '100px',
		  opacity: '0.0'

	  });
	  $("#tos").animate({
		  top: '100px',
		  opacity: '0.0'

	  });
	  $("#tos2").animate({
		  top: '100px',
		  opacity: '0.0'

	  });
	  $("#web").animate({
		  top: '100px',
		  opacity: '0.0'

	  });
	  $("#tos").hide();
	  $("#tos2").hide();
	  $("#web").hide();

	  $("#avatar").delay("slow").animate({
		  top: '70px',
		  opacity: '0.3'

	  });
	  $("#welcome").show();
	  $("#welcome").delay(1000).animate({
		  opacity: '1'
	  });
	  $('#avatar').fadeOut(0, function() {
		  $('#avatar').fadeIn(0);
		  $('#avatar').css("background","#eee url('/images/avatar3.png')");
		  $('#avatar').css("background-size","cover");
		  $('#avatar').css("opacity","1");
	  });
	  $("#continue").show();
	  $("#continue").delay(2000).animate({
		  opacity: '1'
	  });
	  googleUser = auth2.currentUser.get();
	  document.cookie = "login=1";
  }
};
/**
 * A function that checks if the user is already logged in to Google or if the user has just logged in and responds
 */
var refreshValues = function() {
  if (auth2){
	googleUser = auth2.currentUser.get();
  }
}
/**
 * Google callback function for when a request has succeded.
 * @param {Object} user - the resulting object that is returned when a user logs in.
 */
var onSuccess = function(user) {
	gmailUser = user;
	document.cookie = "name="+gmailUser.w3.ofa;
	document.cookie ="surname="+gmailUser.w3.wea;
	document.cookie= "email="+gmailUser.w3.U3;
	document.getElementById('welcome').innerHTML += ", " + user.w3.ig;
 };
/**
 * A Google callback function when a request has failed.
 * @param {string} error - The error message.
 */
var onFailure = function(error) {
	console.log(error);
};
var myVar;
/**
 * A function for Google that uses the API to retrieve an access token, this will be sent to back end to retrieve user information.
 */
function googleretrieve(){
	if(gmailUser==null){
		$('#googleLogin2').click();
		auth2.signIn();
		myVar = setInterval(function(){ checkofflineprompt() }, 500);
	}else{
		auth2.grantOfflineAccess({'approval_prompt': force_prompt, 'redirect_uri': 'postmessage'}).then(signInCallback);
	}
}
/**
* Function for gmail which forces a prompt to ask the user for permissions and sets the cookies
*/
function checkofflineprompt() {
	gmailUser = auth2.currentUser.get();
	if(gmailUser.w3.U3!=null){
		auth2.grantOfflineAccess({'approval_prompt': force_prompt, 'redirect_uri': 'postmessage'}).then(signInCallback);
	  	document.cookie = "name="+gmailUser.w3.ofa;
		document.cookie ="surname="+gmailUser.w3.wea;
		document.cookie= "email="+gmailUser.w3.U3;
	  	clearInterval(myVar);	 
	}
}
/**
* Google callback function that returns the access token. This access token is sent via a websocket to the backend where it will be processed.
* @param {string} authResult - the result of calling the google api and signing in
*/
function signInCallback(authResult) {
  	clearInterval(myVar);
	if (authResult['code']) {
	  $('#tickGmail').show();
	  $('#nextButton').show();
	  var gmailAuthCode = {id:gmailUser.w3.U3,pimSource:"gmail",authCode:authResult['code']}
	  authCodes.push(gmailAuthCode);

	} else {
	  	console.log("An error occurred!");
	}
  }
/**
* A function to ajax the Privacy Policy document into a modal and display the Privacy policy.
*/
function loadPrivacy(){
  document.getElementById("modaltitle").innerHTML="Privacy Policy";
  var xmlhttp=new XMLHttpRequest();
  xmlhttp.onreadystatechange=function(){
	   if (xmlhttp.readyState==4 && xmlhttp.status==200){
		  document.getElementById("modelbod").innerHTML=xmlhttp.responseText;
		  }
  }
  xmlhttp.open("GET","ajax/PrivacyPolicy.txt");
  xmlhttp.send();
}
function loadError(){
  document.getElementById("modaltitle").innerHTML="Sorry";
  var xmlhttp=new XMLHttpRequest();
  xmlhttp.onreadystatechange=function(){
	   if (xmlhttp.readyState==4 && xmlhttp.status==200){
		  document.getElementById("modelbod").innerHTML=xmlhttp.responseText;
		  }
    }
  xmlhttp.open("GET","ajax/error.txt");
  xmlhttp.send();
}
/**
 * A function to ajax the Terms of service document into a modal and display the Terms of service.
 */
function loadTos(){
  	document.getElementById("modaltitle").innerHTML="Terms of Service";
 	var xmlhttp=new XMLHttpRequest();
 	xmlhttp.onreadystatechange=function(){
	   	if (xmlhttp.readyState==4 && xmlhttp.status==200){
			document.getElementById("modelbod").innerHTML=xmlhttp.responseText;
		}
    }
  	xmlhttp.open("GET","ajax/TermsofService.txt");
  	xmlhttp.send();
}
/**
*	Function that animates the log in screen
*/
function loadReg(){
	$("#googleLogin").animate({
	  top: '100px',
	  opacity: '0.0'

	});
	$("#facebookLogin").animate({
	  top: '100px',
	  opacity: '0.0'

	});
	$("#tos").animate({
	  top: '100px',
	  opacity: '0.0'

	});
	$("#tos2").animate({
	  top: '100px',
	  opacity: '0.0'

	});
	$("#web").animate({
	  top: '100px',
	  opacity: '0.0'

	});
	$("#errr").hide();
	$("#tos").hide();
	$("#tos2").hide();
	$("#web").hide();

	$("#avatar").delay("slow").animate({
	  top: '70px',
	  opacity: '0.3'

	});
	$("#welcome").show();
	$("#welcome").delay(1000).animate({
	  opacity: '1'
	});
	$('#avatar').fadeOut(0, function() {
	  $('#avatar').fadeIn(0);
	  $('#avatar').css("background","#eee url('/images/avatar3.png')");
	  $('#avatar').css("background-size","cover");
	  $('#avatar').css("opacity","1");
	});
	$("#continue").show();
	$("#continue").delay(2000).animate({
	  opacity: '1'
	});
	$("#cssload-pgloading").show();
    if($(window).width()<=700)
    {
		$('.login-container').animate({
			width:"110%",
			height:"100%"
		});
    }
    else
    {
		$('.login-container').animate({
			width:"450px",
			height:"450px",
		});
  	}
	$("#continue").animate({
		top: '100px',
		opacity: '0.0'

	});
	$("#avatar").animate({
		top: '100px',
		opacity: '0.0'

	});
	$("#welcome").animate({
		top: '100px',
		opacity: '0.0'

	});
	$("#cssload-pgloading").hide();

	$("#loadingAlert").fadeOut(1000, function() {
		
	});
	var xmlhttp=new XMLHttpRequest();
	xmlhttp.onreadystatechange=function(){
		if (xmlhttp.readyState==4 && xmlhttp.status==200){
			document.getElementById("container").innerHTML=xmlhttp.responseText;
		}
	}
	xmlhttp.open("GET","ajax/selectdata.html");
	xmlhttp.send();
	var filename;
}
/**
 * A function to load the selectdata.html file data into the login container to dynamically update the element to display the new information to select data sources.
 */
function loadXMLDoc(){
	$("#cssload-pgloading").show();
    if($(window).width()<=700)
    {
		$('.login-container').animate({
			width:"110%",
			height:"100%"
		});
    }
    else
    {
		$('.login-container').animate({
			width:"450px",
			height:"450px",
		});
  	}
	$("#continue").animate({
		top: '100px',
		opacity: '0.0'

	});

	$("#avatar").animate({
		top: '100px',
		opacity: '0.0'

	});
	$("#welcome").animate({
		top: '100px',
		opacity: '0.0'

	});

	var socket = new SockJS('/hello');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
	    console.log('Connected: ' + frame);
	    connected = true;
			
		if(gmailUser !=null){
			var userReg={firstName:getCookie("name"),lastName:getCookie("surname"),authCodes:[{id:gmailUser.w3.U3,pimSource:"gmail",authCode:null}]};
			document.cookie="gmailId="+gmailUser.w3.U3;
		
		}else{
			var userReg={firstName:getCookie("name"),lastName:getCookie("surname"),authCodes:[{id:getCookie("facebookId"),pimSource:"facebook",authCode:null}]};
		}
		stompClient.subscribe('/user/topic/greetings', function(serverResponse){
			var jsonresponse = JSON.parse(serverResponse.body);
			console.log("ServerResponse is : "+JSON.stringify(jsonresponse));
			console.log("ServerResponse is : "+jsonresponse.userId);
				
			document.cookie = "nav="+jsonresponse.theme[0];
			document.cookie = "map="+jsonresponse.theme[1];
			document.cookie = "sidepanel="+jsonresponse.theme[2];
			document.cookie = "branch="+jsonresponse.branchingFactor;
			document.cookie = "depth="+jsonresponse.initialDepth;
			document.cookie = "persistMap="+jsonresponse.persistMap;
			document.cookie = "pimIds="+ JSON.stringify(jsonresponse.pimIds);

			console.log("Server asked if user is registered : "+jsonresponse.isRegistered);

			document.cookie="userId="+jsonresponse.userId;
				
				if(jsonresponse.isRegistered){
					document.cookie = "login=1";

					window.location.assign('/');
				}else{
					liftdown();
					$('#myModal').modal('show') 
					loadError();
				}
			}, function(error) {
		    		console.log(error.headers.message);
	  		});
			stompClient.send("/app/hello", {}, JSON.stringify(userReg));
		});

}
/**
 * A Google function to sign the user out if they are signed in.
 */
function signOut() {
  var auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut().then(function () {
	console.log('User signed out.');
  });
}

/**
 * A function to hide a few elements when the page has loaded.
 */
jQuery(document).ready(function($){
	$("#submitID").hide();
	$("#welcome").hide();
	$("#continue").hide();
	$("#loadingAlert").hide();
	$("#cssload-pgloading").hide();
	startApp()

/**
*	This function initialises the JavaScript SDK
*	@property {String} appId - the id assigned to your app by Facebook
*	@preperty {Boolean} cookie - enables cookies to allow the server to access the Facebook session
*	@property {Boolean} xfbml - allows the page to parse social plugins
*	@property {String} version - indicates the graph api version
*/
window.fbAsyncInit = function() {
FB.init({
  appId      : '1051696778242173',
  cookie     : false,  
  xfbml      : true, 
  version    : 'v2.5' 
});

};

/**
*	This loads the Facebook SDK asynchronously
*/
(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/sdk.js";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));
});

/**
*		@var {} AuthResponse - object containing user information sent by Facebook
*/
var AuthResponse;
/**
* 		@var {} FacebookUser - object containing user personal information
*/
var FacebookUser;
 $("facebookLogout").hide();
  /**
 * This function is called with the results from from FB.getLoginStatus().
 *	The response object is returned with a status field that lets the
 *  application using a Facebook know the current login status of the person.
 *  status = connected, means that the person is logged into our app and Facebook
 *	status = not_authorized, the person is logged into Facebook but not our app
 *	any other case means that the person is not logged into Facebook and possibly not our app
 *	@param {string} response -  an object which contains the status of the Facebook connection
 */
 function statusChangeCallback(response)
 {
	console.log('statusChangeCallback');
	console.log(response);
	if (response.status === 'connected')
	{
		var accessToken = response.authResponse.accessToken;

		console.log(response.authResponse);
		console.log("Connected to facebook, accessToken:"+ response.authResponse);
		testAPI();
	}
	else if (response.status === 'not_authorized')
	{
	  console.log("status = not_authorized");
	}
	else
	{
	   console.log("status = something else");
	}
}

/**
*	This function is called when a client clicks on the Facebook button to login or signup
*	It prompts the user to log in to Facebook through the Facebook login dialogue
*/
var  facebookId = "";
var fAT = "";
function onFacebookLogin()
{
  console.log("onFacebookLogin");
  FB.login(function(response) {
	if (response.authResponse) {
	  AuthResponse = response;
	  document.cookie = "facebookId="+ String(response.authResponse.userID);
	  document.cookie = "fAT="+ response.authResponse.accessToken;
	  document.cookie = "fExpireTime="+ response.authResponse.expiresIn;
	  showtick();
	}
	FB.getLoginStatus(function(response) {
	  statusChangeCallback(response);
	});
  }, {scope: 'user_posts'});
}
function sendUserObjectForFacebook()
{
	authCodes.push({id:getCookie("facebookId"),pimSource:"facebook",authCode:getCookie("fAT"),expireTime:getCookie("fExpireTime")});
}
/**
*	This function is called after a person selects Facebook as a data source and successfully logs in with Facebook
*/
function showtick()
{
  console.log("Facebook selected!");
  $("#tickFacebook").show();
  if($('#nextButton').show() == true)
  {
	console.log("show is true");
  }
  $('#nextButton').show();
}
/**
*	This function test of the Facebook Graph API after login is
*	successful.
*	This function is called through the statusChangeCallback function.
*/
function testAPI() {
  console.log('Welcome!  Fetching your information.... ');
  FB.api('/me', function(response) {
	Facebook = response;
	var fullName = Facebook.name.split(" ");
	var fname = fullName[0];
	var lname = fullName[fullName.length-1];
	document.cookie = "name="+fname;
	document.cookie ="surname="+lname;
	document.getElementById('welcome').innerHTML += ", " + response.name;
	onSuccessFacebook();

  });
}

/**
*	This function is called after a successful login to Facebook occurs.
*	unction to load the selectdata files data into the login container to dynamically update the element to display the new information to select data sources.
*/
function onSuccessFacebook() {
  $("#googleLogin").animate({
	  top: '100px',
	  opacity: '0.0'

  });

  $("#facebookLogin").animate({
	  top: '100px',
	  opacity: '0.0'

  });
  $("#web").animate({
	  top: '100px',
	  opacity: '0.0'

  });

  $("#avatar").delay("slow").animate({
	  top: '70px',
	  opacity: '0.3'

  });
   $("#errr").animate({
	  top: '100px',
	  opacity: '0.0'
  });

  $("#tos").animate({
	  top: '100px',
	  opacity: '0.0'

  });
  $("#tos2").animate({
	top: '100px',
	opacity: '0.0'
  });

  $("#welcome").show();
  $("#welcome").delay(1000).animate({
	  opacity: '1'

  });
  $('#avatar').fadeOut(0, function() {
	  $('#avatar').fadeIn(0);
	  $('#avatar').css("background","#eee url('/images/avatar3.png')");
	   $('#avatar').css("background-size","cover");
		$('#avatar').css("opacity","1");
  });
  $("#continue").show();
  $("#continue").delay(2000).animate({
	  opacity: '1'

  });
}
////////////////////////////////////////////////////////
function onTwitterLogin()
{

}
///////////////////////////////////////////////////////
/**
*	@var {HTMLContainer} divClone - This holds an html element that can be used to restore state of another html element
*/
var divClone
function liftup(){
 	divClone = $(".container").clone();

 	 $("#centreText4").slideUp( "medium", function() {
  	});
 	  $("#ptext").slideUp( "medium", function() {
  	});
 	  $("#ptext2").slideUp( "medium", function() {
  	});
	$( "#landingside" ).slideUp( "slow", function() {
	});
	$( "#landingpage" ).slideUp( "slow", function() {
    
 	});
}
function liftdown(){
	$("#centreText4").slideDown( "slow", function() {
  	});
 	  $("#ptext").slideDown( "slow", function() {
  	});
 	  $("#ptext2").slideDown( "slow", function() {
  	});
	$( "#landingside" ).slideDown( "slow", function() {
	});
	$( "#landingpage" ).slideDown( "slow", function() {
	$(".container").replaceWith(divClone);
    gapi.auth2.getAuthInstance().signOut();
    auth2.attachClickHandler('googleLogin', {}, onSuccess, onFailure)
 	});
}

