/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var a = c3.generate({
        bindto: "#rotated-axis",
        size: { height: 400 },
        color: { pattern: ["#4fc3f7", "#2962FF"] },
        data: {
            columns: [
                ["data1", 50, 250, 90, 400, 300, 150],
                ["data2", 30, 100, 85, 50, 15, 25]
            ],
            types: { data1: "bar" }
        },
        axis: { rotated: !0 },
        grid: { y: { show: !0 } }
    });
});