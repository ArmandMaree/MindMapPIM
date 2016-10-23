/**
*   A function that changes the brands icon on hover
*/
function hover(element) {
    element.setAttribute('src', '/images/bubblelogo.png');
}
function unhover(element) {
    element.setAttribute('src', '/images/bubblelogo3.png');
}

/**
*   A function that creates a json string from an object
*   @param obj An object that needs to be converted into a JSON string
*/
function toJSON(obj) {
    return JSON.stringify(obj, null, 4);
}

/**
*   A function that finds and returns the value for the cookie specified
*   @param cname The name of the cookie.
*/
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

function initialiseContextMenu(){
    // menu - variable that is assigned the context menu
    menu = new ax5.ui.menu({
        position: "absolute", // default position is "fixed"
        theme: "info",
        icons: {
            'arrow': '<i class="fa fa-caret-right"></i>'
        },
        items: [
            {
                icon: '<i class="fa fa-comment"></i>',
                label: "Expand Bubble",
            },
            {
                icon: '<i class="fa fa-comments"></i>',
                label: "Remove Bubble",
            }
        ],
        onClick: function(){
            if(this.label=="Expand Bubble"){
                $("#loadingAlert").fadeIn(1000, function() {
                    // body...
                });
                var pathtoselectednode=[];
                if(selectedID!=0)
                    var pathtoselectednode =[];
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
                if(pathtoselectednodelabels.indexOf("Contacts") >=0){
                    pathtoselectednodelabels.splice(pathtoselectednodelabels.indexOf("Contacts"),1);
                }

                var excludelist=[]
                for(var i = 1; i < parentlist.length;i++){
                    if(parentlist[i]==selectedID){
                        try{
                            excludelist.push(nodes[i].label.replace(/ /g,"").replace("\n"," "));
                        }catch(err){

                        }
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
            }

            if(this.label=="Remove Bubble"){
                deleteBranch(selectedID)
            }
        }
    });
}
/**
*   A function that coverts a string to title case
*   @param str The string to convert.
*/
function toTitleCase(str) {
    return str.replace(/(?:^|\s)\w/g, function(match) {
        return match.toUpperCase();
    });
}
