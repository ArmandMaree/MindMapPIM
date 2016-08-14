$(document).ready(function(){
	$("#theme").hide();
	$("#userPreferences").hide();
	/**
	*	This function adds/removes the css class active for the selected tab
	*/
	$("li[role='presentation']").on("click", function(){
		$("li[role='presentation']").removeClass("active");
		$(this).addClass("active");
		
	});

	$.ajax({
		type: "GET",
		url:"ajax/selectdata.html",
		success: function(data)
		{
			$("#ajax").html(data);
			$("#ques").html("");
		}

	});
	websocket();
	/**
	*	This function updates the css settings selected tab and hides/shows the respective div
	*/
	$("a").on("click", function(){
			var title = $(this).text();
			console.log(title);
			if(title == "Account")
			{
				$("#theme").hide();
				$("#userPreferences").hide();
				$("#accountSettings").show();
			}
			else if(title == "Theme")
			{
				$("#accountSettings").hide();
				$("#userPreferences").hide();
				$("#theme").show();
			} 
			else if(title == "User Preferences")
			{
				$("#theme").hide();
				$("#accountSettings").hide();
				$("#userPreferences").show();
			}
		});

	$("#navColour").css("backgroundColor","#0f4d71");
	$("#bubbleSpaceColour").css("backgroundColor","#FFFFEA");
	$("#sidePanelColour").css("backgroundColor","white");

  	/**
  	*	This function sets up the first number spinner
  	*/
    $('#spinner').spinner({
        min: 2,
        max: 5,
        step: 1
    });
    /**
  	*	This function sets up the second number spinner
  	*/
    $('#spinner2').spinner({
        min: 2,
        max: 40,
        step: 1
    });

    /**
    *	@var {String} value - The value of the selected item in Theme
    */
    var value ="";
    /**
    *	@var {String} component - The component that the value must be applied to 
    */
    var component="";
    /**
    *	@var {JsonObject} themeObject - The object that contains the users theme preferences
    */
    var themeObject={
    	"nav":"",
    	"map":"",
    	"sidePanel":""
    };
    /**
    *	@var {JsonObject} userPreferences - The object that contains the users preferences
    */
    var userPreferences={
    	"branch":0,
    	"depth":0
    };

    var depth=0;
    var branch=0;

$("ul li").on("click",function(){
	value= $(this).attr("data-value");
	component = $(this).attr("data-select");
	console.log("Value"+ value);
	console.log("Comp"+ component);
	if(component == "nav")
	{
		$("#navColour").css("backgroundColor",value);
		themeObject.nav=value;
	}
	else if(component == "map")
	{
		$("#bubbleSpaceColour").css("backgroundColor",value);
		themeObject.map = value;
	}
	else if(component == "panel")
	{
		$("#sidePanelColour").css("backgroundColor",value);
		themeObject.sidePanel =value;
	}
	console.log(themeObject);
});

$("#saveTheme").on("click", function(){
	//Send themeObject through websocket
		// if(response.success == true)
		// {
		// 	$("#Saved").fadeIn(1000, function() {
		//    		setTimeout(function(){$("#Saved").hide(); }, 2000);
			 	
		// 	});
		// }
		// else if(response.success == false)
		// {
		// 	$("#Error").fadeIn(1000, function() {
		//    		setTimeout(function(){$("#Error").hide(); }, 4000);
			 	
		// 	});
		// }
})

$("#deactivateAccount").on("click", function(){

});
// $(".branch").on("change", function(){
// 	branch = $(this).val();
// 	console.log("Branch: "+branch);
// });

// $(".depth").on("change", function(){
// 	branch = $(this).val();
// 	console.log("Depth: "+branch);
// });
$("#saveUserPreferences").on("click", function(){
	branch = $("#spinner").val();
	console.log("Branch: "+branch);
	depth = $("#spinner2").val();
	console.log("Depth: "+depth);

	//Send themeObject through websocket
		// if(response.success == true)
		// {
		// 	$("#Saved").fadeIn(1000, function() {
		//    		setTimeout(function(){$("#Saved").hide(); }, 2000);
			 	
		// 	});
		// }
		// else if(response.success == false)
		// {
		// 	$("#Error").fadeIn(1000, function() {
		//    		setTimeout(function(){$("#Error").hide(); }, 4000);
			 	
		// 	});
		// }
});

$("#Saved").hide();
$("#Error").hide();
console.log("Depth: "+ $(".depth").val())
}); //End of on load

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
function websocket()
{
	//Check which sources user is registered for:
		var socket = new SockJS('/usercheck');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
		    console.log('Connected: ' + frame);
		    connected = true;
			
			var name= getCookie("name");
			var surname = getCookie("surname");
			var email = getCookie("email");
			console.log("Got cookie: "+ name,surname,email);
			var usercheck={firstName:name,lastName:surname,gmailId:email};
			// /app/hello is the destination that messages are sent to
			stompClient.send("/app/usercheck", {}, JSON.stringify(usercheck));
			// Must subscribe to a destination (/topic/greetings )to receive messages
			stompClient.subscribe('/topic/usercheck', function(serverResponse){
				var jsonresponse = JSON.parse(serverResponse.body);
				console.log("ServerResponse is : "+jsonresponse);
				console.log("Server asked if user is registered : "+jsonresponse.isRegistered);
		
				if(jsonresponse.gmailId != null || jsonresponse.gmailId !="")
				{
					$("#tickGoogle").show();
				}
				else
				{
					console.log("Jsonresponse error");
				}
			}, function(error) {
		    		// display the error's message header:
		    		console.log(error.headers.message);
	  		});
		}, 3000);
}

function googleretrieve()
{

}
function onFacebookLogin()
{

}
});