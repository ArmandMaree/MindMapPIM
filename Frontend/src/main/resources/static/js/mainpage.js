$(document).ready(function($){

    var color = 'gray';
    var len = undefined;
    var nodes = [{id: 0, label: "ME", group: 0},
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
        nodes: nodes,
        edges: edges
      };
      
        var options = {
            nodes: {
                shape: 'circle',
                size: 50,
                font: {
                    size: 32,
                    color: '#ffffff'
                },
                borderWidth: 1
            },
            edges: {
                width: 1
            }
        };
      var network = new vis.Network(container, data, options);

    // $(function() {
    //     $( "#mynetwork" ).resizable();
    // });
    // var actSize = 100 - $("nav").height();
    // var para = actSize +'vh';
    // $("#sidepanel").css('height', para);

});

    var menu;
    $(document.body).ready(function () {
        menu = new ax5.ui.menu({
            position: "absolute", // default position is "fixed"
            theme: "primary",
            icons: {
                'arrow': '<i class="fa fa-caret-right"></i>'
            },
            items: [
                {
                    icon: '<i class="fa fa-comment"></i>',
                    label: "Menu A",
                    items: [
                        {icon: '<i class="fa fa-hand-peace-o"></i>', label: "Menu A-0"},
                        {icon: '<i class="fa fa-hand-rock-o"></i>',label: "Menu A-1"},
                        {icon: '<i class="fa fa-hand-stop-o"></i>',label: "Menu A-2"}
                    ]
                },
                {
                    icon: '<i class="fa fa-comments"></i>',
                    label: "Menu B",
                    items: [
                        {icon: '<i class="fa fa-pencil-square"></i>', label: "Menu B-0"},
                        {icon: '<i class="fa fa-phone-square"></i>', label: "Menu B-1"},
                        {icon: '<i class="fa fa-plus-square"></i>', label: "Menu B-2"}
                    ]
                }
            ]
        });
 
        $("#mynetwork").bind("contextmenu", function (e) {
            menu.popup(e);
            ax5.util.stopEvent(e);
            // e || {left: 'Number', top: 'Number', direction: '', width: 'Number'}
        });
    });