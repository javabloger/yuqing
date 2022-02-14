/*
Template Name: Material Admin
Author: Wrappixel
Email: niravjoshi87@gmail.com
File: js
*/
$(function () {
    "use strict";
    // ============================================================== 
    // Last month earning
    // ==============================================================
    var sparklineLogin = function() {
        $('.crypto').sparkline([6, 10, 9, 11, 9, 10, 12], {
            type: 'bar',
            height: '30',
            barWidth: '4',
            width: '100%',
            resize: true,
            barSpacing: '5',
            barColor: '#ffffff'
        });

    };
    var sparkResize;

    $(window).resize(function(e) {
        clearTimeout(sparkResize);
        sparkResize = setTimeout(sparklineLogin, 500);
    });
    sparklineLogin();
    
    // ============================================================== 
    // BitCoin / Ethereum / Ripple
    // ==============================================================
    Morris.Area({
        element: 'btc-eth-rip',
        data: [{
                    period: '2010',
                    btc: 0,
                    eth: 0,
                    rip: 0
                }, {
                    period: '2011',
                    btc: 80,
                    eth: 35,
                    rip: 15
                }, {
                    period: '2012',
                    btc: 40,
                    eth: 30,
                    rip: 15
                }, {
                    period: '2013',
                    btc: 100,
                    eth: 60,
                    rip: 38
                }, {
                    period: '2014',
                    btc: 30,
                    eth: 20,
                    rip: 8
                }, {
                    period: '2015',
                    btc: 150,
                    eth: 80,
                    rip: 40
                }, {
                    period: '2016',
                    btc: 80,
                    eth: 50,
                    rip: 20
                }, {
                    period: '2017',
                    btc: 300,
                    eth: 180,
                    rip: 100
                }, {
                    period: '2018',
                    btc: 250,
                    eth: 150,
                    rip: 70
                }


                ],
                lineColors: ['#1240c2', '#40c4ff','#edf3f7'],
                xkey: 'period',
                ykeys: ['btc', 'eth', 'rip'],
                labels: ['Bitcoin', 'Ethereum', 'Ripple'],
                pointSize: 0,
                lineWidth: 0,
                resize:true,
                fillOpacity: 1,
                behaveLikeLine: true,
                gridLineColor: '#e0e0e0',
                hideHover: 'auto'
        
    });
    
 });