/**
*	@var {} auth2 - The sign-in object
*/
var auth2; 
/**
* 	@var {} googleUser - The current user
*/
var googleUser; 
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
      scope: 'profile'
  });
  auth2.isSignedIn.listen(signinChanged);
  if (auth2.isSignedIn.get() == true) {
    auth2.signIn();
  }
  refreshValues();
};
/**
* A function that checks the state of the sign-in object
*/
var signinChanged = function (val) {
  console.log('Signin state changed to ', val);
  if(val === true){
      auth2.signOut().then(function () {
        console.log('User signed out.');
    }); 
  }
};
/**
*	A function that refreshes the sign-in object to obtain the current Google user 
*/
function refreshValues() {
  if (auth2){
    console.log('Refreshing values...');

    googleUser = auth2.currentUser.get();

    console.log(JSON.stringify(googleUser, undefined, 2));
    console.log(auth2.isSignedIn.get());
  }
}
/**
*	This loads the Facebook SDK asynchronously
*/
(function(d, s, id) 
{
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs); 
}(document, 'script', 'facebook-jssdk'));
	
/**
* This function initialises the JavaScript SDK
* @property {String} appId - the id assigned to your app by Facebook
* @preperty {Boolean} cookie - enables cookies to allow the server to access the Facebook session
* @property {Boolean} xfbml - allows the page to parse social plugins
* @property {String} version - indicates the graph api version
*/
window.fbAsyncInit = function() 
{
    FB.init({
    appId      : '1051696778242173',
    cookie     : true,  
    xfbml      : true,  
    version    : 'v2.5' 
  });
}
/**
*	This function is called when a user clicks on the logout button. It determines which social platform the user logged in with.
*/
function logout()
{
    startApp();
    //Facebook logout
    var flag =false;
    FB.getLoginStatus(function(response) {
        if (response && response.status === 'connected') {
            flag = true;
            FB.logout(function(response) {
            });
        }

    });
    setTimeout(function(){ document.cookie = "login=; expires=Thu, 01 Jan 1970 00:00:00 UTC";window.location.assign('/'); }, 4000);

}