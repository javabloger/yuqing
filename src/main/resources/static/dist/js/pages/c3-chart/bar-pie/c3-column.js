/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
/*************************************/
// Column chart                      //
/*************************************/
$(function() {

    // Callback that creates and populates a data table, instantiates the column chart, passes in the data and draws it.
    var columnChart = c3.generate({
        bindto: '#column-chart',
        size: { height: 400 },
        color: {
            pattern: ['#2962FF', '#343a40', '#4fc3f7']
        },


        // Create the data table.
        data: {
            columns: [
                ['option1', 130, -90, 170, 90, 120, 250],
                ['option2', 90, 150, 140, -150, 150, 50]
            ],
            type: 'bar'
        },
        bar: {
            width: {
                ratio: 0.5 // this makes bar width 50% of length between ticks
            }
            // or
            //width: 100 // this makes bar width 100px
        },
        grid: {
            y: {
                show: true
            }
        }
    });

    // Instantiate and draw our chart, passing in some options.
    setTimeout(function() {
        columnChart.load({
            columns: [
                ['option3', 50, -45, 200, 300, -95, 100]
            ]
        });
    }, 1000);
});