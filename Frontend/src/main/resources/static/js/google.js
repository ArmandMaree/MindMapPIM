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
}
/**
 * A function that checks where any sign in activity for Google has happened and responds.
 * @param {boolean} val - true if a sign in has occured.
 */
var signinChanged = function (val) {
  console.log('Signin state changed to ', val);
  if(val === true){
	  googleUser = auth2.currentUser.get();
	  var gmailAuthCode = {id:googleUser.w3.U3,pimSource:"gmail",authCode:getCookie("auth")}
	  UpdateSourcesObject.authcodes.push(gmailAuthCode);
	  console.log("added new AuthCode");
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
	  document.cookie= "googleUser="+user.getBasicProfile().getEmail();
	  console.log(gmailUser.wc.Za+","+ gmailUser.wc.Na);
	refreshValues();
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

   var auth2 = gapi.auth2.getAuthInstance();
  auth2.grantOfflineAccess({'approval_prompt': 'force', 'redirect_uri': 'postmessage'}).then(signInCallback);

}
/**
 * Google callback function that returns the access token. This access token is sent via a websocket to the backend where it will be processed.
 * @param {string} authResult - the result of calling the google api and signing in
 */
  function signInCallback(authResult) {
	if (authResult['code']) {
		console.log("authResult:" +JSON.stringify(authResult));
		
	  console.log(authResult['code']);
	  document.cookie = "auth=" + authResult['code'];
	  $('#tickGoogle').show();
	  
	  	onSuccess();
	} else {
	  	console.log("An error occurred!");
	}
  }
  /**
  *		Function that retrieves the value of a cookie
  *		@param {String} cname The name of the cookie
  *		@return The value of the cookie
  */

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