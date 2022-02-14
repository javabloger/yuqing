/*
Template Name: Admin Pro Admin
Author: Wrappixel
Email: niravjoshi87@gmail.com
File: js
*/
$(function() {
    "use strict";
    // ============================================================== 
    // Our Visitor
    // ============================================================== 
    var chart = c3.generate({
        bindto: '#poll',
        data: {
            columns: [
                ['A', 30],
                ['B', 15],
                ['C', 27],
                ['D', 18],
                ['None', 45]
            ],

            type: 'donut',
            onclick: function(d, i) { console.log("onclick", d, i); },
            onmouseover: function(d, i) { console.log("onmouseover", d, i); },
            onmouseout: function(d, i) { console.log("onmouseout", d, i); }
        },
        donut: {
            label: {
                show: false
            },
            title: "Ans: A",
            width: 15,

        },

        legend: {
            hide: true
            //or hide: 'data1'
            //or hide: ['data1', 'data2']
        },
        color: {
            pattern: ['#40c4ff', '#2961ff', '#ff821c', '#4CAF50', '#e9edf2']
        }
    });
    // ============================================================== 
    // Our Visitor
    // ============================================================== 
    var sparklineLogin = function() {
        $('#ravenue').sparkline([6, 10, 9, 11, 9, 10, 12, 10, 9, 11, 9, 10], {
            type: 'bar',
            height: '75',
            barWidth: '4',
            width: '100%',
            resize: true,
            barSpacing: '8',
            barColor: '#fff'
        });
        $('#active-users').sparkline([6, 10, 9, 11, 9, 10, 12, 10, 9, 11, 9, 10, 12, 10, 9, 11, 9], {
            type: 'bar',
            height: '60',
            barWidth: '4',
            width: '100%',
            resize: true,
            barSpacing: '8',
            barColor: '#4fc3f7'
        });
        $('#views').sparkline([6, 10, 9, 11, 9, 10, 12], {
            type: 'line',
            height: '45',
            lineColor: 'transparent',
            fillColor: 'rgba(255, 255, 255, 0.3)',
            width: '100%',
            resize: true,
        });

    };
    var sparkResize;

    $(window).resize(function(e) {
        clearTimeout(sparkResize);
        sparkResize = setTimeout(sparklineLogin, 500);
    });
    sparklineLogin();

    // ============================================================== 
    // world map
    // ==============================================================
    $('#usa').vectorMap({
        map: 'us_aea_en',
        backgroundColor: 'transparent',
        zoomOnScroll: false,
        regionStyle: {
            initial: {
                fill: '#4fc3f7'
            }
        },
        markerStyle: {
            initial: {
                r: 5,
                'fill': '#fff',
                'fill-opacity': 1,
                'stroke': '#fff',
                'stroke-width': 1,
                'stroke-opacity': 1
            },
        },
        enableZoom: true,
        hoverColor: '#fff',
        markers: [{
            latLng: [31.96, -99.90],
            name: 'Texas',
            style: { fill: '#fff' }
        }, {
            latLng: [43.07, -107.29],
            name: 'Wyoming',
            style: { fill: '#fff' }
        }, {
            latLng: [40.63, -89.39],
            name: 'Illinois',
            style: { fill: '#fff' }
        }],
    });
});