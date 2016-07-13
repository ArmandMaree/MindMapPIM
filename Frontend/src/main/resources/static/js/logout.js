var auth2; // The Sign-In object.
var googleUser; // The current user.

var startApp = function() {
  gapi.load('auth2', initSigninV2);
};

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

var signinChanged = function (val) {
  console.log('Signin state changed to ', val);
  if(val === true){
      auth2.signOut().then(function () {
        console.log('User signed out.');
    }); 
  }
};

function refreshValues() {
  if (auth2){
    console.log('Refreshing values...');

    googleUser = auth2.currentUser.get();

    console.log(JSON.stringify(googleUser, undefined, 2));
    console.log(auth2.isSignedIn.get());
  }
}

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
    setTimeout(function(){ window.location.assign('/'); }, 3000);

}