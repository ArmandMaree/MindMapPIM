function saveTwitterDetails()
{
	var twitUser = $("#TwitUser").val();
	console.log("TwitUser: "+ twitUser);
	if(twitUser[0] == "@")
	{

		twitUser = twitUser.substring(1,twitUser.length);
	}
	document.cookie = "twitter="+ escape(twitUser);
	localStorage.setItem("twitterUser", escape(twitUser));
	// Retrieve
	window.close();
	// alert(JSON.stringify(authCodes));
} 