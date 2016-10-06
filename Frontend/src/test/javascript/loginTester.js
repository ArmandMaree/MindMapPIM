module.exports = {
	'Can user log in?': function (test) {
	  test
	  	.open('https://accounts.google.com/login')
	    .type('#Email','acubencos@gmail.com')
	    .assert.val('#Email', 'acubencos@gmail.com', 'Entered in users email address.')
	    .click('#next')
	    .wait(2000)
	    .type('#Passwd','acuben1234')
	    .assert.val('#Passwd', 'acuben1234', 'Entered in users password.')
	    .click('#signIn')
	    .wait(10000)
    	.open("https://unclutter.iminsys.com/login")
    	.click('#login')
	    .wait(2000)
	    .assert.title().is('Register', 'Webpage has the correct title.')
	    .wait(2000)
	    .click('#googleLogin')
		.wait(10000)
	    .assert.text('#welcome','Welcome, Acuben Cos',"User successfully logged in.")
    	.click('#continue')
    	.wait(10000)
	    .done()
	},
	'Can user refresh the graph and get new nodes?' : function (test) {
		test
			.open('http://unclutter.iminsys.com')
			.wait(10000)
			.execute(function(){
	  			refreshGraph();

	  		})
			.wait(10000)
			.execute(function(){
	  			if(nodes.length>2){
	  				document.title = "nodes have changed"
	  			}
	  		})
		    .assert.title().is('nodes have changed', 'Nodes have expanded successfully.')
			.execute(function(){
					$("#accordion").html("");
			        $("#facebook").html("");
			        $("#gmail").html("");
			        $("#twitter").html("");
			        $("#linkedIn").html("");

			        $("#sidepanelTitle").html("<h2>"+nodes[3].label+"</h2>");
			        selectedID = 3;

			        for(var i=0;i<allPimIDlist[selectedID].length;i++){
			            var uniqueIds = [];
			                $.each(allPimIDlist[selectedID][i], function(j, el){
			                    if($.inArray(el, uniqueIds) === -1) uniqueIds.push(el);
			                });
			                var ID =getCookie(allPimIDlist[selectedID][i][0]+"Id")
			            if(!mocktesting)
			                var itemRequest = {itemIds:uniqueIds,userId:ID};
			            else
			                var itemRequest = {itemIds:uniqueIds,userId:"mocktesting"+ID};
			            
			            // setTimeout(function(){
			            try{
			                stompClient.send("/app/items", {}, JSON.stringify(itemRequest));
			            }catch(err){
			                $("#loadingAlert").fadeOut(1000, function() {
			                // body...
			                });
			                $("#loadingAlertError").fadeIn(1000, function() {
			                });
			                $("#loadingAlertError").html("Error: We could not talk to the server. Please try again.")
			            }
			        }
			})
			
			.wait(20000)
			.execute(function(){
	  			if(sidebarexpanded==true){
	  				document.title = "sidebar expanded"
	  			}
	  		})
		    .assert.title().is("sidebar expanded", 'Sidebar has expanded successfully.')
			.done()
	},
	'Can user load graph from loacal storage' : function (test) {
		test
			.open('http://unclutter.iminsys.com')
			.wait(10000)
			.execute(function(){
	  			if(nodes.length>2){
	  				document.title = "nodes have changed"
	  			}
	  		})
		    .assert.title().is('nodes have changed', 'Nodes have loaded from local storage successfully.')
			// .execute(function(){
			// 		$("#accordion").html("");
			//         $("#facebook").html("");
			//         $("#gmail").html("");
			//         $("#twitter").html("");
			//         $("#linkedIn").html("");

			//         $("#sidepanelTitle").html("<h2>"+nodes[3].label+"</h2>");
			//         selectedID = 3;

			//         for(var i=0;i<allPimIDlist[selectedID].length;i++){
			//             var uniqueIds = [];
			//                 $.each(allPimIDlist[selectedID][i], function(j, el){
			//                     if($.inArray(el, uniqueIds) === -1) uniqueIds.push(el);
			//                 });
			//                 var ID =getCookie(allPimIDlist[selectedID][i][0]+"Id")
			//             if(!mocktesting)
			//                 var itemRequest = {itemIds:uniqueIds,userId:ID};
			//             else
			//                 var itemRequest = {itemIds:uniqueIds,userId:"mocktesting"+ID};
			            
			//             // setTimeout(function(){
			//             try{
			//                 stompClient.send("/app/items", {}, JSON.stringify(itemRequest));
			//             }catch(err){
			//                 $("#loadingAlert").fadeOut(1000, function() {
			//                 // body...
			//                 });
			//                 $("#loadingAlertError").fadeIn(1000, function() {
			//                 });
			//                 $("#loadingAlertError").html("Error: We could not talk to the server. Please try again.")
			//             }
			//         }
			// })
			
			// .wait(20000)
			// .execute(function(){
	  // 			if(sidebarexpanded==true){
	  // 				document.title = "sidebar expanded"
	  // 			}
	  // 		})
		 //    .assert.title().is("sidebar expanded", 'Sidebar has expanded successfully.')
			.done()
	},
	'Can user navigate to help page?': function (test) {
		test
			.open('https://unclutter.iminsys.com/help')
			.wait(10000)
		    .assert.title().is('Welcome', 'Navigated to help page successfully.')
		.done()
	},
	'Can user log out?': function (test) {
		test
			.open('http://unclutter.iminsys.com/')
			.execute(function(){
				document.cookie= "login=; expires=Thu, 01 Jan 1970 00:00:00 UTC";

			})
			.open('http://unclutter.iminsys.com/')
		    .assert.title().is('Register', 'Webpage has the correct title.')
		    .done()

	}
};
