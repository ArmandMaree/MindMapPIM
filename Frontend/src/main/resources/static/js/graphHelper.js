/**
*   A function that creates a refresh list that will be used to recreate the contacts nodes
*/
function buildrefreshContactList(){
    if(selectedID==0){ 
        for(var j=0;j<parentlist.length;j++){
            if(parentlist[j] == 1){
                refreshContactList.push(j);
            } 
        }
    }
}

/**
*   A function that populates the branch values 
*   @param newValue The new value that is used for updating
*/
function showBranchValue(newValue){
    document.getElementById("Branchrange").innerHTML=newValue;
    initialbranching = newValue;
    document.cookie = "branch="+initialbranching;
}
/**
*   A function that populates the depth values 
*   @param newValue The new value that is used for updating
*/
function showDepthValue(newValue){
    document.getElementById("Depthrange").innerHTML=newValue;
    initialdepth = newValue;
    document.cookie = "depth="+ initialdepth;
}

/**
*   A function that will expand a bubble
*   @param nextID The ID of the node that needs to be expanded
*/
function expandBubble(nextID){
    selectedID = nextID;
    try{
        network.selectNodes([nextID]);
    }catch(err){
    }
    var pathtoselectednode=[];
    if(selectedID!=0)
        var pathtoselectednode =[];
    var pathtoselectednodelabels =[]

    for(var i = selectedID; i > 0; i = parentlist[i]){
        if(i!=1)
            pathtoselectednode.push(i);
    }
    initialdepth = getCookie("depth");
    if((pathtoselectednode.length+1)<=initialdepth && !flagHasNodesToLoad){
        var pos=0;
        var branchinglimit = 4;

        var thiscolor = nodes[selectedID].color;

        var pathtoselectednodelabels =[]

        for(var i = selectedID; i > 0; i = parentlist[i]){
            pathtoselectednode.push(i);
        }

        var pos=0;
        var branchinglimit = 4;
        var thiscolor = nodes[selectedID].color;
        for(var i=pathtoselectednode.length-1;i>=0;i--){
           pathtoselectednodelabels.push(nodes[pathtoselectednode[i]].label.replace(/ /g,"").replace("\n"," "));
        }
        // pathtoselectednodelabels.push()
        pathtoselectednodelabels.splice(pathtoselectednodelabels.indexOf("Contacts"),1);

        var excludelist=[]
        for(var i = 1; i < parentlist.length;i++){
            if(parentlist[i]==selectedID){
                excludelist.push(nodes[i].label.replace(/ /g,"").replace("\n"," "));
            }
        }

        initialbranching = getCookie("branch");
        if(mocktesting)
            topicRequest = {userId: "mocktesting"+x1, path:pathtoselectednodelabels, exclude:excludelist, maxNumberOfTopics:initialbranching};
        else
            topicRequest = {userId: x1, path:pathtoselectednodelabels, exclude:excludelist, maxNumberOfTopics:initialbranching};
        
        document.cookie="lastselectednode="+selectedID;
        try{
            stompClient.send("/app/request", {}, JSON.stringify(topicRequest));
        }catch(err){
            $("#loadingAlert").fadeOut(1000, function() {
            // body...
            });
            $("#loadingAlertError").fadeIn(1000, function() {
            });
            $("#loadingAlertError").html("Error: We could not talk to the server. Please try again.")
        }
    }else{
         network.selectNodes([0]);
    }
    canExpand=false;
}
/**
*   Resets the graph to the default
*/
function refreshGraph(){
    $("#loadingAlertWarning").fadeOut(1000, function() {
        // body...
    });
    deleteBranch("0");   
    document.cookie="lastrefreshtime="+ Date.now();
}

setInterval(function(){ 
    if(shouldRebuild){
       //name1 - string that contains the last refresh time
        var name1 = "lastrefreshtime=";
        var ca1 = document.cookie.split(';');
        var datenow ="";
        for(var i = 0; i <ca1.length; i++) {
            var c = ca1[i];
            while (c.charAt(0)==' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name1) == 0) {
                datenow = c.substring(name1.length,c.length);
            }
        }
    
        if(Date.now()-datenow > 60000){
            $("#loadingAlert").fadeOut(1000, function() {
                    // body...
            });
            $("#loadingAlertWarning").fadeIn(1000, function() {
                // body...
            });
            $("#loadingAlertWarning").html("You havent refreshed the Bubble Map in a while. This could mean some of the bubbles are outdated. Would you like to refresh now? <br/><br/><button type='button' class='button btn btn-warning btn-block' onclick='refreshGraph()' >Refresh</button> ");
        }
    }


}, 10000);
/**
*   A function that will expand a bubble
*   @param nextID The ID of the node that needs to be expanded
*/
function deleteBranch(selectedID){
    if(selectedID!=0 &&  selectedID!=1){
        localStorage.setItem('nodes', "");
        localStorage.setItem('edges', "");
        localStorage.setItem('parentlist', "");
        var deletelist =[]
        var templist = []
        deletelist.push(selectedID);
        templist.push(selectedID);
        var count =0;
        while(templist.length>0 || count > 10000){
            count++;
            var parent = templist.pop();
            for(var i=0;i<parentlist.length;i++){
                if(parentlist[i] == parent){
                    templist.push(i);
                    deletelist.push(i);
                }
            }
        }
        parentlist[selectedID]=-1;
        
        network.selectNodes(deletelist);
        network.deleteSelected();


    }else{
        parentlist[0] = -1;
        parentlist[1] = -1;

        for(var i=0;i<parentlist.length;i++){
            if(parentlist[i]=="0" || parentlist[i]=="1"  ){
                deleteBranch(i);
            }
        }
        selectedID ="0";
        document.cookie="lastselectednode="+selectedID;
        
        nodePosition =0;
        nodes = [
            {id: 0, label: "   ME   ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'#1999d6',highlight:{background:'#1999d6',border:'#1999d6'},hover:{background:'#1999d6',border:'#1999d6'}}},
            {id: 1, label: "  Contacts  ",font:'20px Raleway '+fontcolor, color: {background:nodecolor, border:'purple',highlight:{background:'purple', border:'purple'},hover:{background:'purple', border:'purple'}}}
        ]

        edges = [
            {id:0, from: 1, to: 0}
        ]
        expandlist = [];
        parentlist = ["-1","-1"];
        var excludelist =[]
        for(var i = 1; i < parentlist.length;i++){
            if(parentlist[i]==selectedID){
                try{
                    excludelist.push(nodes[i].label.replace(/ /g,"").replace("\n"," "));
                }catch(err){

                }
            }
        }

        parentlist[0] = 0;
        parentlist[1] = 0;


        initialbranching = getCookie("branch");
        if(mocktesting)
            topicRequest = {userId: "mocktesting"+x1, path:[], exclude:excludelist, maxNumberOfTopics:initialbranching};
        else
            topicRequest = {userId: x1, path:[], exclude:excludelist, maxNumberOfTopics:initialbranching};

        try{
            stompClient.send("/app/request", {}, JSON.stringify(topicRequest));
        }catch(err){
            $("#loadingAlert").fadeOut(1000, function() {
            // body...
            });
            $("#loadingAlertError").fadeIn(1000, function() {
            });
            $("#loadingAlertError").html("Error: We could not talk to the server. Please try again.")
        }
        $("#loadingAlert").fadeIn(1000, function() {
            // body...
        });

        

    }
}