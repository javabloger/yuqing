/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var a = c3.generate({
        bindto: "#stacked-bar",
        size: { height: 400 },
        color: { pattern: ["#2962FF", "#4fc3f7", "#f62d51", "#343a40"] },
        data: {
            columns: [
                ["option1", -150, 150, 100, 350, -150, 200],
                ["option2", 190, 250, -180, 100, -250, 150],
                ["option3", 200, 180, 250, -350, 150, 120]
            ],
            type: "bar",
            groups: [
                ["option1", "option2"]
            ]
        },
        grid: { y: { show: !0 } },
        axis: { rotated: !0 }
    });
    setTimeout(function() {
        a.groups([
            ["option1", "option2", "option3"]
        ])
    }, 1e3), setTimeout(function() {
        a.load({
            columns: [
                ["option4", 150, -20, 250, 270, -190, -250]
            ]
        })
    }, 1500), setTimeout(function() {
        a.groups([
            ["option1", "option2", "option3", "option4"]
        ])
    }, 2e3)
});