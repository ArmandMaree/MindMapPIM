function saveTwitterDetails()
{
	var twitUser = $("#TwitUser").val();
	console.log("TwitUser: "+ twitUser);
	document.cookie = "twitter="+ twitUser;
	localStorage.setItem("twitterUser", twitUser);
	// Retrieve
	window.close();
	// alert(JSON.stringify(authCodes));
} 