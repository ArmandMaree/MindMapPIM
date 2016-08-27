var googleUser = {};
  var startApp = function() {
    gapi.load('auth2', function(){
      // Retrieve the singleton for the GoogleAuth library and set up the client.
      auth2 = gapi.auth2.init({
        client_id:'570253498384-r14raqpo4lcqpjggmp05h6359dm6ogfo.apps.googleusercontent.com',
        // cookiepolicy: 'single_host_origin',
        scope: 'profile email https://www.googleapis.com/auth/gmail.labels https://www.googleapis.com/auth/gmail.readonly'
        // Request scopes in addition to 'profile' and 'email'
        //scope: 'additional_scope'
      });
      auth2.grantOfflineAccess({'approval_prompt': 'force', 'redirect_uri': 'postmessage'}).then(signInCallback);
      if($("#tickGoogle").is(":visible")== false)
      {
        console.log("If statement....");
        
        attachSignin(document.getElementById('googlesigninButton'));
       
      }
    });
  };

function signInCallback(authResult) {
  if (authResult['code']) {
    console.log(authResult['code']);
  //  var gmailAuthCode = {id:gmailUser.getBasicProfile().getEmail(),pimSource:"Gmail",authCode:authResult['code']}
    
    if(googleUser != null)
            var gmailAuthCode = {id:gmailUser.getBasicProfile().getEmail(),pimSource:"Gmail",authCode:authResult['code']};
          console.log(gmailAuthCode);
          UpdateSourcesObject.authcodes.push(gmailAuthCode);
          $("#Loading").fadeOut(1000, function(){});  
          $("#tickGoogle").show();
          $("#googlesigninButton").html("<span class='fa fa-google'></span> <span id='g' style='font-size:11pt'>Remove Gmail</span>");
          
    console.log("added new AuthCode");


    } else {
        console.log("An error occurred!");
    }
  }

  function attachSignin(element) {
    console.log(element.id);
    gapi.auth2.getAuthInstance().signOut();
    auth2.attachClickHandler(element, {},
        function(googleUser) {
          // document.getElementById('name').innerText = 
          console.log("Signed in: " +
              googleUser.getBasicProfile().getName());
          
           // refreshValues();
        }, function(error) {
          alert(JSON.stringify(error, undefined, 2));
        });
  }

 