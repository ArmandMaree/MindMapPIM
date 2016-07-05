        $(document).ready(function() {
              $.ajaxSetup({ cache: true });
              $.getScript('//connect.facebook.net/en_US/sdk.js', function(){
                FB.init({
                  appId: '1051696778242173',
                  version: 'v2.5' // or v2.0, v2.1, v2.2, v2.3
                });     
                $('#loginbutton,#feedbutton').removeAttr('disabled');
                FB.getLoginStatus(updateStatusCallback);
              });
            });

        var rightans;
        var count =0;
        var answers = new Array(6);
        var user;
        var password;


        function loadXMLDoc(){
                $('.login-container').animate({
                    width:"750px",
                    height:"750px"

                });
            var xmlhttp=new XMLHttpRequest();
            xmlhttp.onreadystatechange=function(){
                 if (xmlhttp.readyState==4 && xmlhttp.status==200){
                    document.getElementById("container").innerHTML=xmlhttp.responseText;
                    }
            }

            xmlhttp.open("GET","newhtml.txt?rndstr=<%= getRandomStr() %>&fname=Henry&lname=Ford",true);
            xmlhttp.send();
            var filename;

            count++;
            filename =  "Questions/Ques"+count +".js?rndstr=<%= getRandomStr() %>";
            $.getJSON(filename, function(result){
                document.getElementById("ques").innerHTML=result.Question;
                document.getElementById("btnone").innerHTML=result.fans1;
                document.getElementById("btntwo").innerHTML=result.fans2;
                document.getElementById("btnthree").innerHTML=result.fans3;
                document.getElementById("btnnext").style.display = "none";
                rightans = result.answer;
                // alert(rightans);
            });
        }   
            // $("#btnnext").hide();

        function signOut() {
          var auth2 = gapi.auth2.getAuthInstance();
          auth2.signOut().then(function () {
            console.log('User signed out.');
          });
        }
        function onFailure(error) {
          console.log(error);
        }
        function renderButton() {
          gapi.signin2.render('my-signin2', {
            'scope': 'profile email',
            'width': 200,
            'height': 50,
            'longtitle': true,
            'onsuccess': onSuccess,
            'onfailure': onFailure
          });

        function onSuccess(googleUser) {
          console.log('Logged in as: ' + googleUser.getBasicProfile().getName());
          // if( !$("#user").val()== "" && !$("#password").val()== "") {
            $("#my-signin2").animate({
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

          // }
        }
        }
        jQuery(document).ready(function($){
            $("#submitID").hide();
            $("#welcome").hide();
            $("#continue").hide();
         
    
        });

         function statusChangeCallback(response) {
                    console.log('statusChangeCallback');
                    console.log(response);
                    // The response object is returned with a status field that lets the
                    // app know the current login status of the person.
                    // Full docs on the response object can be found in the documentation
                    // for FB.getLoginStatus().
                    if (response.status === 'connected') {
                      // Logged into your app and Facebook.
                      testAPI();
                    } else if (response.status === 'not_authorized') {
                      // The person is logged into Facebook, but not your app.
                      document.getElementById('status').innerHTML = 'Please log ' +
                        'into this app.';
                    } else {
                      // The person is not logged into Facebook, so we're not sure if
                      // they are logged into this app or not.
                      document.getElementById('status').innerHTML = 'Please log ' +
                        'into Facebook.';
                    }
                  }

                  // This function is called when someone finishes with the Login
                  // Button.  See the onlogin handler attached to it in the sample
                  // code below.
                  function checkLoginState() {
                    FB.getLoginStatus(function(response) {
                      statusChangeCallback(response);
                    });
                  }

                  window.fbAsyncInit = function() {
                  FB.init({
                    appId      : '1051696778242173',
                    cookie     : true,  // enable cookies to allow the server to access 
                                        // the session
                    xfbml      : true,  // parse social plugins on this page
                    version    : 'v2.5' // use graph api version 2.5
                  });

                  // Now that we've initialized the JavaScript SDK, we call 
                  // FB.getLoginStatus().  This function gets the state of the
                  // person visiting this page and can return one of three states to
                  // the callback you provide.  They can be:
                  //
                  // 1. Logged into your app ('connected')
                  // 2. Logged into Facebook, but not your app ('not_authorized')
                  // 3. Not logged into Facebook and can't tell if they are logged into
                  //    your app or not.
                  //
                  // These three cases are handled in the callback function.

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
                      document.getElementById('status').innerHTML =
                        'Thanks for logging in, ' + response.name + '!';
                    });
                  }