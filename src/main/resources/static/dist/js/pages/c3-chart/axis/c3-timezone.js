/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var t = c3.generate({
        bindto: "#axis-timezone",
        size: { height: 400 },
        color: { pattern: ["#2962FF", "#4fc3f7"] },
        data: {
            x: "x",
            xFormat: "%Y",
            columns: [
                ["x", "2015", "2014", "2013", "2012", "2011", "2010"],
                ["option1", 250, 150, 400, 100, 200, 30],
                ["option2", 350, 250, 500, 200, 340, 130]

            ]
        },
        axis: { x: { type: "timeseries", localtime: !1, tick: { format: "%Y-%m-%d %H:%M:%S" } } },
        grid: { y: { show: !0 } }
    });
});