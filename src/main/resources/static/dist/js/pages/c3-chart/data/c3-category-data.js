/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var o = c3.generate({
        bindto: "#category-data",
        size: { height: 250 },
        color: { pattern: ["#4fc3f7", "#2962FF","#114365"] },
        data: {
            x: "x",
            columns: [
                ["x", "www.site1.com", "www.site2.com", "www.site3.com", "www.site4.com","www.site5.com", "www.site6.com", "www.site7.com", "www.site8.com", "www.site9.com" ,"www.site10.com"],
                ["正面", 4020, 2200, 1020, 240,400, 2020, 100, 40,99,765],
                ["中性", 190, 1020, 140, 920,400, 2020, 1002, 420,123,1231],
                ["负面", 930, 100, 140, 920,400, 2020, 1200, 240,990,77]
            ],
            groups: [
                ["正面", "中性","负面"]
            ],
            type: "bar"
        },
        axis: { x: { type: "category" } },
        grid: { y: { show: !0 } }
    });
    // setTimeout(function() {
    //     o.load({
    //         columns: [
    //             ["x", "www.siteA.com", "www.siteB.com", "www.siteC.com", "www.siteD.com"],
    //             ["complete", 350, 200, 150, 150],
    //             ["remaining", 190, 150, 290, 140]
    //         ]
    //     })
    // }, 1e3), setTimeout(function() {
    //     o.load({
    //         columns: [
    //             ["x", "www.siteE.com", "www.siteF.com", "www.siteG.com"],
    //             ["complete", 30, 300, 290],
    //             ["remaining", 90, 230, 240]
    //         ]
    //     })
    // }, 2e3), setTimeout(function() {
    //     o.load({
    //         columns: [
    //             ["x", "www.site1.com", "www.site2.com", "www.site3.com", "www.site4.com"],
    //             ["complete", 130, 350, 200, 470],
    //             ["remaining", 190, 130, 140, 340]
    //         ]
    //     })
    // }, 3e3), setTimeout(function() {
    //     o.load({
    //         columns: [
    //             ["complete", 30, 130, 100, 170],
    //             ["remaining", 190, 30, 140, 40]
    //         ]
    //     })
    // }, 4e3), setTimeout(function() { o.load({ url: "../c3_string_x.csv" }) }, 5e3)
});