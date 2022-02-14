/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var t = c3.generate({
        bindto: "#multiple-xy",
        size: { height: 400 },
        point: { r: 4 },
        color: { pattern: ["#2962FF", "#4fc3f7"] },
        data: {
            xs: { option1: "x1", option2: "x2" },
            columns: [
                ["x1", 10, 20, 30, 50, 70, 100],
                ["x2", 25, 50, 75, 100, 120],
                ["option1", 30, 200, 50, 300, 85, 250],
                ["option2", 20, 200, 140, 100, 190]
            ]
        },
        grid: { y: { show: !0 } }
    });
});