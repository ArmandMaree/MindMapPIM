 /**
 *	@var AuthResponse -Facebook object that returns user information
 */
var AuthResponse; 
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
		// console.log(response.authResponse);
		console.log("Connected to facebook, accessToken:"+ response.authResponse);
		testAPI();
		return;
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
	  AuthResponse = response.authResponse;
	  console.log(response.authResponse);
	
	  showtick();
	}
	FB.getLoginStatus(function(response) {
	  statusChangeCallback(response);
	  return;
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
}

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
	// console.log('Successful login for: ' + response.name);
	AuthResponse = response.authResponse;
  });
return;
}

/**
*	This function is called after a successful login to Facebook occurs.
*	unction to load the selectdata files data into the login container to dynamically update the element to display the new information to select data sources.
*/
function onSuccessFacebook(response) {
	console.log("Success: " + response.authResponse);
  	getResponse(response);
  	return true;
  
}
////////////////////////////////////////////////////////////////////////////////////////////////End of Facebook Code
