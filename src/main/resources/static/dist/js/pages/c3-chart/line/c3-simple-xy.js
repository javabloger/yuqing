/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var o = c3.generate({
        bindto: "#simple-xy",
        size: { height: 400 },
        point: { r: 4 },
        color: { pattern: ["#2962FF", "#4fc3f7"] },
        data: {
            x: "x",
            columns: [
                ["x", 30, 50, 100, 230, 300, 310],
                ["option1", 30, 280, 150, 400, 180, 280],
                ["option2", 130, 300, 200, 300, 250, 450]
            ]
        },
        grid: { y: { show: !0 } }
    });
    setTimeout(function() {
        o.load({
            columns: [
                ["option1", 100, 250, 150, 200, 100, 0]
            ]
        })
    }, 1e3), setTimeout(function() {
        o.load({
            columns: [
                ["option3", 0, 150, 50, 150, 50, 0]
            ]
        })
    }, 1500), setTimeout(function() { o.unload({ ids: "option2" }) }, 2e3)
});