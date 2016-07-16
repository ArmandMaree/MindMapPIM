$(document).ready(function(){
	$("#theme").hide();
	$("#userPreferences").hide();
	$("li[role='presentation']").on("click", function(){
		$("li[role='presentation']").removeClass("active");
		$(this).addClass("active");
		
	});
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
				console.log("Got Here");
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

  
    $('#spinner').spinner({
        min: 2,
        max: 5,
        step: 1
    });
    $('#spinner2').spinner({
        min: 2,
        max: 40,
        step: 1
    });























})