/*
Template Name: Admin Pro Admin
Author: Wrappixel
Email: niravjoshi87@gmail.com
File: js
*/
$(function() {
    "use strict";

    // ==============================================================
    // Real Time Visits
    // ==============================================================
    var data = [5, 10, 15, 20, 15, 30, 40],
        totalPoints = 100;

    function getRandomData() {
        if (data.length > 0) data = data.slice(1);
        // Do a random walk
        while (data.length < totalPoints) {
            var prev = data.length > 0 ? data[data.length - 1] : 10,
                y = prev + Math.random() * 10 - 5;
            if (y < 0) {
                y = 0;
            } else if (y > 100) {
                y = 100;
            }
            data.push(y);
        }
        // Zip the generated y values with the x values
        var res = [];
        for (var i = 0; i < data.length; ++i) {
            res.push([i, data[i]])
        }
        return res;
    }
    // Set up the control widget
    var updateInterval = 1000;
    $("#updateInterval").val(updateInterval).change(function() {
        var v = $(this).val();
        if (v && !isNaN(+v)) {
            updateInterval = +v;
            if (updateInterval < 1) {
                updateInterval = 1;
            } else if (updateInterval > 1000) {
                updateInterval = 1000;
            }
            $(this).val("" + updateInterval);
        }
    });
    var plot = $.plot("#placeholder", [getRandomData()], {
        series: {
            shadowSize: 1, // Drawing is faster without shadows
            lines: { fill: true, fillColor: 'transparent' },
        },
        yaxis: {
            min: 0,
            max: 100,
            show: true
        },
        xaxis: {
            show: false
        },
        colors: ["#fe5419"],
        grid: {
            color: "#AFAFAF",
            hoverable: true,
            borderWidth: 0,
            backgroundColor: 'transparent'
        },
        tooltip: true,
        tooltipOpts: {
            content: "Visits: %x",
            defaultTheme: false
        }
    });
    window.onresize = function(event) {
        $.plot($("#placeholder"), [getRandomData()]);
    }

    function update() {
        plot.setData([getRandomData()]);
        // Since the axes don't change, we don't need to call plot.setupGrid()
        plot.draw();
        setTimeout(update, updateInterval);
    }
    update();
    // ============================================================== 
    // world map
    // ==============================================================
    jQuery('#visitfromworld').vectorMap({
        map: 'world_mill_en',
        backgroundColor: 'transparent',
        borderColor: '#fff',
        borderOpacity: 0,
        borderWidth: 0,
        zoomOnScroll: false,
        color: '#dfe2e9',
        regionStyle: {
            initial: {
                fill: '#dfe2e9',
                'stroke-width': 1,
                'stroke': '#dfe2e9'
            }
        },
        markerStyle: {
            initial: {
                r: 5,
                'fill': '#dfe2e9',
                'fill-opacity': 1,
                'stroke': '#dfe2e9',
                'stroke-width': 1,
                'stroke-opacity': 1
            },
        },
        enableZoom: true,
        hoverColor: '#79e580',
        markers: [{
                latLng: [21.00, 78.00],
                name: 'India : 9347',
                style: { fill: '#2961ff' }
            },
            {
                latLng: [-33.00, 151.00],
                name: 'Australia : 250',
                style: { fill: '#2961ff' }
            },
            {
                latLng: [36.77, -119.41],
                name: 'USA : 250',
                style: { fill: '#2961ff' }
            },
            {
                latLng: [55.37, -3.41],
                name: 'UK   : 250',
                style: { fill: '#2961ff' }
            },
            {
                latLng: [25.20, 55.27],
                name: 'UAE : 250',
                style: { fill: '#2961ff' }
            }
        ],
        hoverOpacity: null,
        normalizeFunction: 'linear',
        scaleColors: ['#93d5ed', '#93d5ee'],
        selectedColor: '#cbd0db',
        selectedRegions: [],
        showTooltip: true,
        onRegionClick: function(element, code, region) {
            var message = 'You clicked "' + region + '" which has the code: ' + code.toUpperCase();
            alert(message);
        }
    });
});