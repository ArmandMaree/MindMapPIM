$(document).ready(function(){
	$("#theme").hide();
	$("#userPreferences").hide();
	$("li[role='presentation']").on("click", function(){
		$("li[role='presentation']").removeClass("active");
		$(this).addClass("active");
		

		$("a").on("click", function(){
			var title = $(this).text();
			console.log(title);
			if(title == "Account")
			{
				$("#theme").hide();
				$("#userPreferences").hide();
				$("#account").show();
			}
			else if(title == "Theme")
			{
				$("#account").hide();
				$("#userPreferences").hide();
				$("#theme").show();
			} 
			else if(title == "User Preferences")
			{
				$("#theme").hide();
				$("#account").hide();
				$("#userPreferences").show();
			}
		});
	









	});






















})