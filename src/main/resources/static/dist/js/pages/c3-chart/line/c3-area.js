/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var a = c3.generate({
        bindto: "#area-chart",
        size: { height: 200 },
        point: { r: 4 },
        color: { pattern: ["#2962FF", "#4fc3f7"] },
        data: {
            columns: [
                ["data1", 130, 100, 150, 200, 100, 50],
                ["data2", 0, 350, 200, 150, 50, 0]

            ],
            types: { data1: "area", data2: "area-spline" }
        },
        grid: { y: { show: !0 } }
    });
});