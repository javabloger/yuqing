/**********************************/
// Stacked Column Chart           //
/**********************************/
$(window).on("load", function() {

    // Callback that creates and populates a data table, instantiates the stacked column chart, passes in the data and draws it.
    var stackedColumnChart = c3.generate({
        bindto: '#stacked-column',
        size: { height: 400 },
        color: {
            pattern: ['#2962FF', '#ced4da', '#4fc3f7', '#f62d51']
        },

        // Create the data table.
        data: {
            columns: [
                ['option1', -130, 200, 200, 400, 400, 250],
                ['option2', 100, 50, -100, 200, -150, 150],
                ['option3', -85, 200, 200, -300, 250, 250]
            ],
            type: 'bar',
            groups: [
                ['option1', 'option2']
            ]
        },
        grid: {
            y: {
                show: true
            }
        },
    });

    // Instantiate and draw our chart, passing in some options.
    setTimeout(function() {
        stackedColumnChart.groups([
            ['option1', 'option2', 'option3']
        ]);
    }, 1000);

    setTimeout(function() {
        stackedColumnChart.load({
            columns: [
                ['option4', 50, -150, 150, 200, -300, -100]
            ]
        });
    }, 1500);

    setTimeout(function() {
        stackedColumnChart.groups([
            ['option1', 'option2', 'option3', 'option4']
        ]);
    }, 2000);

    // Resize chart on sidebar width change
    $(".sidebartoggler").on('click', function() {
        stackedColumnChart.resize();
    });
});