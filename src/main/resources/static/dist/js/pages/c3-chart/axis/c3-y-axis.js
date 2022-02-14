/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var o = c3.generate({
        bindto: "#y-axis",
        size: { height: 400 },
        color: { pattern: ["#2962FF", "#E91E63"] },
        data: {
            columns: [
                ["Profit", 2500, 150, 1000, 100, 500, 30]
            ]
        },
        axis: { y: { tick: { format: d3.format("$,") } } },
        grid: { y: { show: !0 } }
    });
});