/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/
$(function() {
    var a = c3.generate({
        bindto: "#data-color",
        size: { height: 400 },
        data: {
            columns: [
                ["data1", 130, 200, 150, 40, 360, 50],
                ["data2", 100, 130, 100, 240, 130, 350],
                ["data3", 300, 240, 360, 400, 250, 250]
            ],
            type: "bar",
            colors: { data1: "#4fc3f7", data2: "#2962FF" },
            color: function(a, o) { return o.id && "data3" === o.id ? d3.rgb(a).darker(o.value / 150) : a }
        },
        grid: { y: { show: !0 } }
    });
});