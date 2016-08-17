/**
*	@var {} auth2 - The sign-in object
*/
var auth2; 
/**
* 	@var {} googleUser - The current user
*/
var googleUser;
/**
* 	@var {} authCodes - ....
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
	$("#loadingAlert").fadeIn(1000, function() {
		// body...
	});
	// var socket = new SockJS('/hello');
	// stompClient = Stomp.over(socket);
	// stompClient.connect({}, function(frame) {
	//     console.log('Connected: ' + frame);
	//     connected = true;
	//   	var userReg = {};
	//   	if(gmailUser!=null){
	// 		userReg={firstName:gmailUser.wc.Za,lastName:gmailUser.wc.Na,authCodes:authCodes};
	// 		console.log("User registration object:" +JSON.stringify(userReg));
	//   	}
	//   	setTimeout(function(){
	// 		stompClient.send("/app/hello", {}, JSON.stringify(userReg));
	// 		stompClient.subscribe('/topic/greetings', function(serverResponse){
	// 			var jsonresponse = JSON.parse(serverResponse.body);
	// 			console.log("Server says: "+jsonresponse.content);
	// 			document.cookie="userId="+jsonresponse.content;
	// 			$("#loadingAlert").fadeOut(1000, function() {
	// 				// body...
	// 			});
	// 			// if (stompClient != null) {
	// 	  //           stompClient.disconnect();
	// 	  //       }
	// 			window.location.assign('/mainpage');
	// 		}, function(error) {
	// 	    		// display the error's message header:
	// 	    		console.log(error.headers.message);
	//   			});
	//   	}, 3000);
	getGmailResponse(gmailUser,authCodes[2]);
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
	  document.cookie = "login=1";
  }
};
/**
 * A function that checks if the user is already logged in to Google or if the user has just logged in and responds
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
	//Create cookie
	  document.cookie = "name="+gmailUser.wc.Za;
	  document.cookie ="surname="+gmailUser.wc.Na;
	  document.cookie= "email="+user.getBasicProfile().getEmail();
	  console.log(gmailUser.wc.Za+","+ gmailUser.wc.Na);
	document.getElementById('welcome').innerHTML += ", " + user.getBasicProfile().getName();
 };
/**
 * A Google callback function when a request has failed.
 * @param {string} error - The error message.
 */
var onFailure = function(error) {
	console.log(error);
};
/**
 * A function for Google that uses the API to retrieve an access token, this will be sent to back end to retrieve user information.
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
	  var gmailAuthCode = {id:gmailUser.getBasicProfile().getEmail(),pimSource:"Gmail",authCode:authResult['code']}
	  authCodes.push(gmailAuthCode);
	  console.log("added new AuthCode");

	} else {
	  	console.log("An error occurred!");
	}
  }