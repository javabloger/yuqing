/*
Template Name: Admin Pro Admin
Author: Wrappixel
Email: niravjoshi87@gmail.com
File: js
*/
$(window).on("load", function() {
    "use strict";
    
    // ============================================================== 
    // Overview
    // ============================================================== 
    var chart = c3.generate({
        bindto: '.earningsbox'
        , data: {
            columns: [
                ['Site A', 5, 6, 3, 7, 9, 10, 14, 12, 11, 9, 8, 7, 10, 6, 12, 10, 8]
            ]
            , type: 'area-spline'
        }
        , axis: {
            y: {
                show: false
                , tick: {
                    count: 0
                    , outer: false
                }
            }
            , x: {
                show: false
            , }
        }
        , padding: {
            top: 0
            , right: -8
            , bottom: -28
            , left: -8
        , }
        , point: {
            r: 0
        , }
        , legend: {
            hide: true
                //or hide: 'data1'
                //or hide: ['data1', 'data2']
        }
        , color: {
            pattern: ['#40c4ff', '#dadada', '#ff821c', '#7e74fb']
        }
        
    });
    // ============================================================== 
    // Revenue
    // ============================================================== 
    
    var chart = c3.generate({
        bindto: '.revenue',
        data: {
            columns: [
                ['Email', 45],
                ['Website', 15],
                ['Mobile', 27],
                ['Other', 18],
            ],
            
            type : 'donut'
        },
        donut: {
            label: {
                show: false
              },
            title:"Revenue",
            width:35,
            
        },
        
        legend: {
          hide: false
          //or hide: 'data1'
          //or hide: ['data1', 'data2']
        },
        color: {
              pattern: ['#40c4ff', '#2961ff', '#ff821c', '#7e74fb']
        }
    });
    
    // ============================================================== 
    // product-sales
    // ============================================================== 
    var chart = c3.generate({
        bindto: '.product-sales'
        , data: {
            columns: [
                ['Site A', 5, 6, 3, 7, 9, 10, 14, 12, 11, 9, 8, 7, 10, 6, 12, 10, 8]
                , ['Site B', 1, 2, 8, 3, 4, 5, 7, 6, 5, 6, 4, 3, 3, 12, 5, 6, 3]
            ]
            , type: 'bar'
        }
        , axis: {
            y: {
                show: true
                , tick: {
                    count: 0
                    , outer: false
                }
            }
            , x: {
                show: true
            , }
        },bar: {
          
          width: 8
            
        }
        , padding: {
            top: 40
            , right: 10
            , bottom: 0
            , left: 20
        , }
        , point: {
            r: 0
        , }
        , legend: {
            hide: true
                //or hide: 'data1'
                //or hide: ['data1', 'data2']
        }
        , color: {
            pattern: ['#2961ff', '#40c4ff', '#ff821c', '#7e74fb']
        }
    });
    
    // ============================================================== 
    // Conversation Rate
    // ============================================================== 
    var chart = c3.generate({
        bindto: '.rate',
        data: {
            columns: [
                ['Conversation', 85],
                ['other', 15],
            ],
            
            type : 'donut'
        },
        donut: {
            label: {
                show: false
              },
            title:"Weekly",
            width:10,
            
        }
        , padding: {
            top:10,
             bottom:-20
            
        , },
        legend: {
          hide: true
          //or hide: 'data1'
          //or hide: ['data1', 'data2']
        },
        color: {
              pattern: ['#2961ff', '#dadada', '#ff821c', '#7e74fb']
        }
    });
    // ============================================================== 
    // Revenue
    // ============================================================== 
    
    var chart = c3.generate({
        bindto: '.status',
        data: {
            columns: [
                ['Success', 65],
                ['Pending', 15],
                ['Failed', 17]
            ],
            
            type : 'donut'
        },
        donut: {
            label: {
                show: false
              },
            title:"Orders",
            width:35,
            
        },
        
        legend: {
          hide: true
          //or hide: 'data1'
          //or hide: ['data1', 'data2']
        },
        color: {
              pattern: ['#40c4ff', '#2961ff', '#ff821c', '#7e74fb']
        }
    });
    $(".sidebartoggler").on("click", function() { chart.resize() });
        
    /*
    // ============================================================== 
    // Our Visitor
    // ============================================================== 
    var sparklineLogin = function () {
        $('#activeu').sparkline([6, 10, 9, 11, 9, 10, 12, 11, 10, 9, 11, 9, 10], {
            type: 'bar'
            , height: '122'
            , barWidth: '4'
            , width: '100%'
            , resize: true
            , barSpacing: '11'
            , barColor: '#fff'
        });
        
    };
    var sparkResize;
    $(window).resize(function (e) {
        clearTimeout(sparkResize);
        sparkResize = setTimeout(sparklineLogin, 500);
    });
    sparklineLogin();
    
    // ============================================================== 
    // EArnings
    // ============================================================== 
    var chart = c3.generate({
        bindto: '.earnings'
        , data: {
            columns: [
                ['Site A', 0, 6, 3, 7, 9, 10, 14, 12, 11, 9, 8, 7, 10, 6, 12, 10, 8]
                
            ]
            , type: 'spline'
        }
        , axis: {
            y: {
                show: false
                , tick: {
                    count: 0
                    , outer: false
                }
            }
            , x: {
                show: false
            , }
        }
        , padding: {
            top: 0
            , right: 10
            , bottom: 0
            , left: 20
        , }
        , point: {
            r: 0
        , }
        , legend: {
            hide: true
                //or hide: 'data1'
                //or hide: ['data1', 'data2']
        }
        , color: {
            pattern: ['#fff', '#ff821c', '#ff821c', '#7e74fb']
        }
    });*/
});