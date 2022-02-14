/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var t = c3.generate({
        bindto: "#category-axis",
        size: { height: 400 },
        color: { pattern: ["#2962FF", "#4fc3f7"] },
        data: {
            columns: [
                ["year", 50, 250, 100, 400, 150, 250, 50, 100, 250]
            ]
        },
        axis: { x: { type: "year", categories: ["2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009"] } },
        grid: { y: { show: !0 } }
    });
});