/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var a = c3.generate({
        bindto: "#data-order",
        size: { height: 400 },
        color: { pattern: ["#343a40", "#4fc3f7", "#f62d51", "#2962FF", "#ced4da"] },
        data: {
            columns: [
                ["option1", 750, 530, 400, 320, 200, 130],
                ["option2", 250, 150, 200, 130, 10, -130],
                ["option3", -150, -250, -200, -10, -50, -130]
            ],
            type: "bar",
            groups: [
                ["option1", "option2", "option3"]
            ],
            order: "desc"
        },
        grid: { x: { show: !0 } }
    });
    setTimeout(function() {
        a.load({
            columns: [
                ["option4", 1810, 1520, 1600, 1450, 1300, 1200]
            ]
        })
    }, 1e3), setTimeout(function() {
        a.load({
            columns: [
                ["option5", 800, 520, 600, 450, 300, 200]
            ]
        })
    }, 2e3), setTimeout(function() {
        a.groups([
            ["option1", "option2", "option3", "option4", "option5"]
        ])
    }, 3e3)
});