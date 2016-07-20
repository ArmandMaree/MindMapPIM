var name = "login=";
var ca = document.cookie.split(';');
// console.log(ca)
for(var i = 0; i <ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0)==' ') {
        c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
        var x = c.substring(name.length,c.length);
    }
}

    if(x!="1"){
        window.location.assign('/');
    }
function toJSON(obj) {
    return JSON.stringify(obj, null, 4);
}
var nodes, edges, network;
$(document).ready(function($){
    document.oncontextmenu = function() {return false;};
    // $("canvas").click(function(event){
 //     event.preventDefault();
    // });
    var color = 'gray';
    var len = undefined;
    // nodes.on('*', function () {
    //     document.getElementById('nodes').innerHTML = JSON.stringify(nodes.get(), null, 4);
    // });
    nodes = [
        // {id: 0, label: "ME", group: 0,color:{background:"#D3D3D3"}},
        // {id: 1, label: "1", group: 0,color:{background:"#D3D3D3"}},
        // {id: 2, label: "2", group: 0,color:{background:"#D3D3D3"}},
        // {id: 3, label: "3", group: 1,color:{background:"#D3D3D3"}},
        // {id: 4, label: "4", group: 1,color:{background:"#D3D3D3"}},
        // {id: 5, label: "5", group: 1,color:{background:"#D3D3D3"}},
        // {id: 6, label: "6", group: 2,color:{background:"#D3D3D3"}},
        // {id: 7, label: "7", group: 2,color:{background:"#D3D3D3"}},
        // {id: 8, label: "8", group: 2,color:{background:"#D3D3D3"}},
        // {id: 9, label: "9", group: 3,color:{background:"#D3D3D3"}},
        // {id: 10, label: "10", group: 3,color:{background:"#D3D3D3"}},
        // {id: 11, label: "11", group: 3,color:{background:"#D3D3D3"}},
        // {id: 12, label: "12", group: 4,color:{background:"#D3D3D3"}},
        // {id: 13, label: "13", group: 4,color:{background:"#D3D3D3"}},
        // {id: 14, label: "14", group: 4,color:{background:"#D3D3D3"}},
        // {id: 15, label: "15", group: 5,color:{background:"#D3D3D3"}},
        // {id: 16, label: "16", group: 5,color:{background:"#D3D3D3"}},
        // {id: 17, label: "17", group: 5,color:{background:"#D3D3D3"}},
        // {id: 18, label: "18", group: 6,color:{background:"#D3D3D3"}},
        // {id: 19, label: "19", group: 6,color:{background:"#D3D3D3"}},
        // {id: 20, label: "20", group: 6,color:{background:"#D3D3D3"}},
        // {id: 21, label: "21", group: 7,color:{background:"#D3D3D3"}},
        // {id: 22, label: "22", group: 7,color:{background:"#D3D3D3"}},
        // {id: 23, label: "23", group: 7,color:{background:"#D3D3D3"}},
        // {id: 24, label: "24", group: 8,color:{background:"#D3D3D3"}},
        // {id: 25, label: "25", group: 8,color:{background:"#D3D3D3"}},
        // {id: 26, label: "26", group: 8,color:{background:"#D3D3D3"}},
        // {id: 27, label: "27", group: 9,color:{background:"#D3D3D3"}},
        // {id: 28, label: "28", group: 9,color:{background:"#D3D3D3"}},
        // {id: 29, label: "29", group: 9,color:{background:"#D3D3D3"}}
        // ];
        {id: 0, label: "ME", group: 0},
        {id: 1, label: "1", group: 0},
        {id: 2, label: "2", group: 0},
        {id: 3, label: "3", group: 1},
        {id: 4, label: "4", group: 1},
        {id: 5, label: "5", group: 1},
        {id: 6, label: "6", group: 2},
        {id: 7, label: "7", group: 2},
        {id: 8, label: "8", group: 2},
        {id: 9, label: "9", group: 3},
        {id: 10, label: "10", group: 3},
        {id: 11, label: "11", group: 3},
        {id: 12, label: "12", group: 4},
        {id: 13, label: "13", group: 4},
        {id: 14, label: "14", group: 4},
        {id: 15, label: "15", group: 5},
        {id: 16, label: "16", group: 5},
        {id: 17, label: "17", group: 5},
        {id: 18, label: "18", group: 6},
        {id: 19, label: "19", group: 6},
        {id: 20, label: "20", group: 6},
        {id: 21, label: "21", group: 7},
        {id: 22, label: "22", group: 7},
        {id: 23, label: "23", group: 7},
        {id: 24, label: "24", group: 8},
        {id: 25, label: "25", group: 8},
        {id: 26, label: "26", group: 8},
        {id: 27, label: "27", group: 9},
        {id: 28, label: "28", group: 9},
        {id: 29, label: "29", group: 9}
        ];
        var edges = [{from: 1, to: 0},
            {from: 2, to: 0},
            {from: 4, to: 3},
            {from: 5, to: 4},
            {from: 4, to: 0},
            {from: 7, to: 6},
            {from: 8, to: 7},
            {from: 7, to: 0},
            {from: 10, to: 9},
            {from: 11, to: 10},
            {from: 10, to: 4},
            {from: 13, to: 12},
            {from: 14, to: 13},
            {from: 13, to: 0},
            {from: 16, to: 15},
            {from: 17, to: 15},
            {from: 15, to: 10},
            {from: 19, to: 18},
            {from: 20, to: 19},
            {from: 19, to: 4},
            {from: 22, to: 21},
            {from: 23, to: 22},
            {from: 22, to: 13},
            {from: 25, to: 24},
            {from: 26, to: 25},
            {from: 25, to: 7},
            {from: 28, to: 27},
            {from: 29, to: 28},
            {from: 28, to: 0}
        ]

      // create a network
      var container = document.getElementById('mynetwork');
      var data = {
        nodes: new vis.DataSet(nodes),
        edges: new vis.DataSet(edges)
      };
      
        var options = {
            interaction:{
                hover: true,
                hoverConnectedEdges: true,
            },
            physics: {
              stabilization: false
            },
            clickToUse: false,
            nodes: {
                shape: 'circle',
                size: 50,
                color: '#7FBEEB ', //rgb(102,167,188)',
                font: {
                    size: 32,
                    color: 'black'
                },
                borderWidth: 1
            },
            edges: {
                width: 1
            }
        };
        var network = new vis.Network(container, data, options);

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
                    // items: [
                    //     {icon: '<i class="fa fa-hand-peace-o"></i>', label: "Menu A-0"},
                    //     {icon: '<i class="fa fa-hand-rock-o"></i>',label: "Menu A-1"},
                    //     {icon: '<i class="fa fa-hand-stop-o"></i>',label: "Menu A-2"}
                    // ]
                },
                {
                    icon: '<i class="fa fa-comments"></i>',
                    label: "Remove Bubble",
                    // items: [
                    //     {icon: '<i class="fa fa-pencil-square"></i>', label: "Menu B-0"},
                    //     {icon: '<i class="fa fa-phone-square"></i>', label: "Menu B-1"},
                    //     {icon: '<i class="fa fa-plus-square"></i>', label: "Menu B-2"}
                    // ]
                }
            ],
            onClick: function(){
                if(this.label=="Expand Bubble"){
                    console.log(this.label);
                    var branchinglimit = 4;
                    for(var i=0 ;i<branchinglimit;i++){
                        try {
                            data.nodes.add({
                                id: nodes.length,
                                label: nodes.length
                            });
                        }
                        catch (err) {
                            alert(err);
                        }
                        console.log()
                        try {
                            data.edges.add({
                                id: edges.length++,
                                from: selectedID,
                                to: nodes.length++
                            });
                        }
                        catch (err) {
                            alert(err);
                        }
                    }
                }

                if(this.label=="Remove Bubble"){
                    console.log(this.label);
                }
            }
        });
                
        network.on("click", function(){
            // for(var j=0;j<nodes.length;j++){
            // }

            var e = window.event;
            var posX = e.clientX;
            var posY = e.clientY - $("nav").height();
            console.log("X: "+ posX);
            console.log("Y: "+ posY);
            network.getNodeAt({"x": posX, "y": posY});
            
            var alledges = network.getSelectedEdges();
            var allnodes = []
            for(var i=0;i<alledges.length;i++){
                var node = network.getConnectedNodes(alledges[i]);
                console.log(node);
                for(var j=0;j<node.length;j++){
                    allnodes.push(node[j]);
                }
            }
            network.selectNodes(allnodes);
            console.log(nodes.length)
        });


        network.on("oncontext", function(){
            var e = window.event;
            var posX = e.clientX;
            var posY = e.clientY - $("nav").height();
            console.log("X: "+ posX);
            console.log("Y: "+ posY);
            console.log(network.getNodeAt({"x": posX, "y": posY}));
            selectedID = network.getNodeAt({"x": posX, "y": posY});
            network.selectNodes([network.getNodeAt({"x": posX, "y": posY})]);
            var node = network.getSelectedNodes();
            console.log(node);
                console.log("works on right click");
                // $(this).bind("contextmenu", function (e) {
                if(node.length != 0)
                {
                    menu.popup(e);
                    ax5.util.stopEvent(e);
                }
                // });
        });
      

});



// $(document).click(function(event) {
//     var text = $(event.target).text();
//     console.log($(event.target));
// });

    var menu;
    var selectedID;
