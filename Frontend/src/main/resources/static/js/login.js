var auth2; // The Sign-In object.
var googleUser; // The current user.

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
  auth2.grantOfflineAccess({'redirect_uri': 'postmessage'}).then(signInCallback);

}
/**
 * Google callback function that returns the access token.
 * @param {string} authResult - the result of calling the google api and signing in
 */
  function signInCallback(authResult) {
    if (authResult['code']) {
      console.log(authResult['code']);
      $('#tickGoogle').show();
      $('#nextButton').show();
      // Send the code to the server
      console.log(JSON.stringify({ authCode: authResult['code'] }));
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
    $('.login-container').animate({
        width:"550px",
        height:"550px"

    });

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
});

////////////////////////////////////////////////////////////////////////////////////////////////// Facebook Code

 $("facebookLogout").hide();
 function statusChangeCallback(response) {
    console.log('statusChangeCallback');
    console.log(response);
    if (response.status === 'connected') {
    // Logged into your app and Facebook.
     var accessToken = response.authResponse.accessToken;
    console.log(response.authResponse);
    console.log("Connected to facebook, accessToken:"+ response.authResponse);
    testAPI();
  } else if (response.status === 'not_authorized') {
    // The person is logged into Facebook, but not your app.
    // document.getElementById('status').innerHTML = 'Please log ' +
    //   'into this app.';
      console.log("status = not_authorized");
  } else {
    // The person is not logged into Facebook, so we're not sure if
    // they are logged into this app or not.
    // document.getElementById('status').innerHTML = 'Please log ' +
    //   'into Facebook.';
       console.log("status = something else");
  }

    
}
function onFacebookLogin()
{
  console.log("onFacebookLogin");
  FB.login(function(response) {
    if (response.authResponse) {
      // _wdfb_notifyAndRedirect();
      console.log("Auth response:");
      console.log(response.authResponse); //returns an object
      showtick();
    }
    FB.getLoginStatus(function(response) {
      statusChangeCallback(response);
    });
  });
}

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

// Load the SDK asynchronously
(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/sdk.js";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

// Here we run a very simple test of the Graph API after login is
// successful.  See statusChangeCallback() for when this call is made.
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
