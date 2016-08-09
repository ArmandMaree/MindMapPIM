var auth2; // The Sign-In object.
var googleUser; // The current user.
var authCodes = [];
var gmailUser= null;
var connected = false;
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

var sendUserReg = function(){
	$("#loadingAlert").fadeIn(1000, function() {
		// body...
	});
  var socket = new SockJS('/hello');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function(frame) {
	  console.log('Connected: ' + frame);
	  connected = true;
  });
  var userReg = {};
  if(gmailUser!=null){
	userReg={firstName:gmailUser.wc.Za,lastName:gmailUser.wc.Na,authCodes:authCodes};
	console.log(JSON.stringify(userReg));
  }
  setTimeout(function(){
	stompClient.send("/app/hello", {}, JSON.stringify(userReg));
	stompClient.subscribe('/topic/greetings', function(serverResponse){
		var jsonresponse = JSON.parse(serverResponse.body);
		console.log("Server says: "+jsonresponse.content);
		document.cookie="userId="+jsonresponse.content;
		$("#loadingAlert").fadeOut(1000, function() {
		// body...
		});
		window.location.assign('/mainpage');
	}, function(error) {
	    // display the error's message header:
	    console.log(error.headers.message);
  	});
  }, 3000);




  // console.log(document.createTextNode(message));

  // if (stompClient != null) {
  //   stompClient.disconnect();
  // }
  // setConnected(false);
  // console.log("Disconnected");
  // setTimeout(function(){ window.location.assign('/mainpage')},3000);
}
/**
 * A function that cheks where any sign in activity for Google has happened and responds.
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
	  // $("#facebookLogin").hide();
	  // $("#googleLogin").hide();

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
	  document.cookie = "login=1";
  }
};
/**
 * Google function that checks if the user is already logged in or if the user has just logged in and responds
 */
var refreshValues = function() {
  if (auth2){
	console.log('Refreshing values...');

	googleUser = auth2.currentUser.get();

	console.log(JSON.stringify(googleUser, undefined, 2));
	console.log(auth2.isSignedIn.get());
  }
}
/**
 * Google callback function for when a request has succeded.
 * @param {Object} user - the resulting object that is returned when a user logs in.
 */
var onSuccess = function(user) {
	gmailUser = user;
	console.log('Signed in as ' + user.getBasicProfile().getName());
	document.getElementById('welcome').innerHTML += ", " + user.getBasicProfile().getName();
 };
/**
 * Google callback function when a request has failed.
 * @param {string} error - The error message.
 */
var onFailure = function(error) {
	console.log(error);
};
/**
 * Google function that uses the API to retrieve an access token, this will be sent to back end to retrieve user information.
 */
function googleretrieve(){

  // var auth2 = gapi.auth2.getAuthInstance();
  auth2.grantOfflineAccess({'approval_prompt': 'force', 'redirect_uri': 'postmessage'}).then(signInCallback);

}
/**
 * Google callback function that returns the access token. This access token is sent via a websocket to the backend where it will be processed.
 * @param {string} authResult - the result of calling the google api and signing in
 */
  function signInCallback(authResult) {
	if (authResult['code']) {
	  console.log(authResult['code']);
	  $('#tickGoogle').show();
	  $('#nextButton').show();
	  // Send the code to the server
	  // console.log(JSON.stringify({ authCode: authResult['code'] }));
	  var gmailAuthCode = {id:gmailUser.getBasicProfile().getEmail(),pimSource:"Gmail",authCode:authResult['code']}
	  authCodes.push(gmailAuthCode);
	  console.log("added new AuthCode");

	  // $.ajax({
	  //   type: "POST",
	  //   url: "http://localhost:50001/google/token",
	  //   data: JSON.stringify({ authCode: authResult['code'] }),
	  //   contentType: 'application/json',
	  //   success: function(data) {
	  //     if(data == 'OK')
	  //       console.log('Sent Successful');
	  //     else
	  //       console.log("Sending failed");
	  //   }
	  // });
	} else {
	  // There was an error.
	}
  }
/**
 * Function to ajax the Privacy Policy document into a modal and display the Privacy policy.
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
  // xmlhttp.open("GET","newhtml.txt?rndstr=<%= getRandomStr() %>&fname=Henry&lname=Ford",true);
  xmlhttp.send();
}
/**
 * Function to ajax the Terms of service document into a modal and display the Terms of service.
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
  // xmlhttp.open("GET","newhtml.txt?rndstr=<%= getRandomStr() %>&fname=Henry&lname=Ford",true);
  xmlhttp.send();
}
/**
 *Function to load the selectdata files data into the login container to dynamically update the element to display the new information to select data sources.
 */
function loadXMLDoc(){
  if($(window).width()<=700){
	$('.login-container').animate({
		width:"110%",
		height:"100%"
	});
  }else{
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
 * Google function to sign the user out if they are signed in.
 */
function signOut() {
  var auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut().then(function () {
	console.log('User signed out.');
  });
}

/**
 * Function to hide a few elements when the page has loaded.
 */
jQuery(document).ready(function($){
	$("#submitID").hide();
	$("#welcome").hide();
	$("#continue").hide();
	$("#loadingAlert").hide();
});

////////////////////////////////////////////////////////////////////////////////////////////////// Facebook Code

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
function onFacebookLogin()
{
  console.log("onFacebookLogin");
  FB.login(function(response) {
	if (response.authResponse) {
	  console.log("Auth response:");
	  console.log(response.authResponse);
	  showtick();
	}
	FB.getLoginStatus(function(response) {
	  statusChangeCallback(response);
	});
  });
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
*	This function initialises the JavaScript SDK
*/
window.fbAsyncInit = function() {
FB.init({
  appId      : '1051696778242173',
  cookie     : true,  // enable cookies to allow the server to access
					  // the session
  xfbml      : true,  // parse social plugins on this page
  version    : 'v2.5' // use graph api version 2.5
});

FB.getLoginStatus(function(response) {
  statusChangeCallback(response);
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

/**
*	This function test of the Facebook Graph API after login is
*	successful.
*	This function is called through the statusChangeCallback function.
*/
function testAPI() {
  console.log('Welcome!  Fetching your information.... ');
  FB.api('/me', function(response) {
	console.log('Successful login for: ' + response.name);
	// document.getElementById('status').innerHTML =
	//   'Thanks for logging in, ' + response.name + '!';
	document.getElementById('welcome').innerHTML += ", " + response.name;
	document.cookie = "login=1";
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
////////////////////////////////////////////////////////////////////////////////////////////////End of Facebook Code
