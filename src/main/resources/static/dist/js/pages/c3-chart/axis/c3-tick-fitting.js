/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var i = c3.generate({
        bindto: "#tick-fitting",
        size: { height: 400 },
        color: { pattern: ["#2962FF", "#E91E63"] },
        data: {
            x: "x",
            columns: [
                ["x", "2018-01-31", "2018-02-31", "2018-03-31", "2018-04-28"],
                ["days", 150, 400, 100, 30]

            ]
        },
        axis: { x: { type: "timeseries", tick: { fit: !0, format: "%e %b %y" } } },
        grid: { y: { show: !0 } }
    });
});