/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function(e) {

    // Callback that creates and populates a data table, instantiates the line chart, passes in the data and draws it.
    var lineChart = c3.generate({
        bindto: '#data-from-url',
        size: { height: 400 },
        color: {
            pattern: ['#2962FF', '#4fc3f7', '#f62d51']
        },

        // Create the data table.
        data: {
            url: '../../dist/js/pages/c3-chart/c3_test.csv'
        },
        grid: {
            y: {
                show: true
            }
        },
    });

    // Instantiate and draw our chart, passing in some options.
    setTimeout(function() {
        c3.generate({
            data: {
                url: '../../dist/js/pages/c3-chart/c3_test.json',
                mimeType: 'json'
            }
        });
    }, 1000);
});