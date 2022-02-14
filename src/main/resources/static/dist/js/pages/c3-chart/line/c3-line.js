/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var t = c3.generate({
        bindto: "#line-chart",
        size: { height: 400 },
        point: { r: 4 },
        color: { pattern: ["#2962FF", "#4fc3f7"] },
        data: {
            columns: [
                ["option1", 30, 200, 100, 400, 150, 250],
                ["option2", 30, 120, 210, 40, 50, 205]
            ]
        },
        grid: { y: { show: !0, stroke: "#ff0" } }
    });
    setTimeout(function() {
        t.load({
            columns: [
                ["option1", 200, 150, 350, 250, 330, 500]
            ]
        })
    }, 1e3), setTimeout(function() {
        t.load({
            columns: [
                ["option3", 180, 250, 100, 350, 240, 150]
            ]
        })
    }, 1500), setTimeout(function() { t.unload({ ids: "option1" }) }, 2e3)
});