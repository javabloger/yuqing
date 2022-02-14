
// 15、热点地区排名 图表
function hotSpotRankingChart(datas){
	// 地图
//	window.onload = function () {
	    var url = "../dist/js/reportdata.json"/* json文件url，本地的就写本地的位置，如果是服务器的就写服务器的路径 */
	    var request = new XMLHttpRequest();
	    request.open("get", url);/* 设置请求方法与路径 */
	    request.send(null);/* 不发送数据到服务器 */
	    request.onload = function () {/* XHR对象获取到返回信息后执行 */
	        if (request.status == 200) {/* 返回状态为200，即为数据获取成功 */
	            var jsondata = JSON.parse(request.responseText);
	            var myChartdt = echarts.init(document.getElementById("chinadt"))
	            echarts.registerMap('china', jsondata);
	            var geoCoordMap = {
	            '台湾': [121.5135,25.0308],
	            '黑龙江': [127.9688, 45.368],
	            '内蒙古': [110.3467, 41.4899],
	            "吉林": [125.8154, 44.2584],
	            '北京市': [116.4551, 40.2539],
	            "辽宁": [123.1238, 42.1216],
	            "河北": [114.4995, 38.1006],
	            "天津": [117.4219, 39.4189],
	            "山西": [112.3352, 37.9413],
	            "陕西": [109.1162, 34.2004],
	            "甘肃": [103.5901, 36.3043],
	            "宁夏": [106.3586, 38.1775],
	            "青海": [101.4038, 36.8207],
	            "新疆": [87.9236, 43.5883],
	            "西藏": [91.11, 29.97],
	            "四川": [103.9526, 30.7617],
	            "重庆": [108.384366, 30.439702],
	            "山东": [117.1582, 36.8701],
	            "河南": [113.4668, 34.6234],
	            "江苏": [118.8062, 31.9208],
	            "安徽": [117.29, 32.0581],
	            "湖北": [114.3896, 30.6628],
	            "浙江": [119.5313, 29.8773],
	            "福建": [119.4543, 25.9222],
	            "江西": [116.0046, 28.6633],
	            "湖南": [113.0823, 28.2568],
	            "贵州": [106.6992, 26.7682],
	            "云南": [102.9199, 25.4663],
	            "广东": [113.12244, 23.009505],
	            "广西": [108.479, 23.1152],
	            "海南": [110.3893, 19.8516],
	            '上海': [121.4648, 31.2891],
	            
	        };
//	            var data = [
//	            {"name":"北京","value":0},
//	            {name:"香港",value:42},
//	            {name:"天津",value:42},
//	            {name:"河北",value:102},
//	            {name:"山西",value:81},
//	            {name:"内蒙古",value:47},
//	            {name:"辽宁",value:67},
//	            {name:"吉林",value:82},
//	            {"name":"黑龙江","value":12236},
//	            {name:"上海",value:24},
//	            {name:"江苏",value:92},
//	            {name:"浙江",value:114},
//	            {name:"安徽",value:109},
//	            {name:"福建",value:116},
//	            {name:"江西",value:91},
//	            {name:"山东",value:119},
//	            {name:"河南",value:137},
//	            {name:"湖北",value:116},
//	            {name:"湖南",value:114},
//	            {name:"重庆",value:91},
//	            {name:"四川",value:125},
//	            {name:"贵州",value:62},
//	            {name:"云南",value:83},
//	            {name:"西藏",value:9},
//	            {name:"陕西",value:80},
//	            {name:"甘肃",value:56},
//	            {name:"青海",value:10},
//	            {name:"宁夏",value:18},
//	            {name:"新疆",value:180},
//	            {name:"广东",value:123},
//	            {name:"广西",value:59},
//	            {name:"海南",value:14},
//	            ];
	            var data = datas;
	           var max = 480, min = 9; // todo
	            var maxSize4Pin = 100, minSize4Pin = 20;
	          var convertData = function (data) {
	            var res = [];
	            for (var i = 0; i < data.length; i++) {
	                var geoCoord = geoCoordMap[data[i].name];
	                if (geoCoord) {
	                    res.push({
	                        name: data[i].name,
	                        value: geoCoord.concat(data[i].value)
	                    });
	                }
	            }
	            return res;
	        };
	
	
	
	           var optiondt = {
	            // backgroundColor: {
	            // type: 'linear',
	            // x: 0,
	            // y: 0,
	            // x2: 1,
	            // y2: 1,
	            // colorStops: [{
	            // offset: 0, color: '#0f378f' // 0% 处的颜色
	            // }, {
	            // offset: 1, color: '#00091a' // 100% 处的颜色
	            // }],
	            // globalCoord: false // 缺省为 false
	            // },
	
	               tooltip: {
	                    trigger: 'item',
	                    formatter: function (params) {
	                      if(typeof(params.value)[2] == "undefined"){
	                      	return params.name + ' : ' + params.value;
	                      }else{
	                      	return params.name + ' : ' + params.value[2];
	                      }
	                    }
	                },
	             /*
					 * legend: { orient: 'vertical', y: 'bottom', x:
					 * 'right', data:['pm2.5'], textStyle: { color: '#fff' } },
					 */
	                legend: {
	                orient: 'vertical',
	                y: 'bottom',
	                x:'right',
	                data:['pm2.5'],
	                textStyle: {
	                    color: '#ccc'
	                }
	            }, 
	                visualMap: {
	                    show: false,
	                    min: 0,
	                    max: 500,
	                    left: 'left',
	                    top: 'bottom',
	                    text: ['高', '低'], // 文本，默认为数值文本
	                    calculable: true,
	                    seriesIndex: [1],
	                    inRange: {
	
	                    }
	                },
	                geo: {
	                    map: 'china',
	                    show: true,
	                    roam: true,
	                    label: {
	        				normal: {
	        					show: false
	        				},
	        				emphasis: {
	        					show: false,
	        				}
	        			},
	                    itemStyle: {
	                        normal: {
	                            areaColor: '#3a7fd5',
	                            borderColor: '#0a53e9',// 线
	                            shadowColor: '#092f8f',// 外发光
	                            shadowBlur: 20
	                        },
	        				 emphasis: {
	                            areaColor: '#0a2dae',// 悬浮区背景
	                        }
	                    }
	                },
	                series : [
	              {
	                 
	                    symbolSize: 5,
	                    label: {
	                        normal: {
	                            formatter: '{b}',
	                            position: 'right',
	                            show: true
	                        },
	                        emphasis: {
	                            show: true
	                        }
	                    },
	                    itemStyle: {
	                        normal: {
	                            color: '#fff'
	                        }
	                    },
	                    name: 'light',
	                    type: 'scatter',
	                    coordinateSystem: 'geo',
	                    data: convertData(data),
	                    
	                },
	                 {
	                    type: 'map',
	                    map: 'china',
	                    geoIndex: 0,
	                    aspectScale: 0.8, // 长宽比
	                    showLegendSymbol: false, // 存在legend时显示
	                    label: {
	                        normal: {
	                            show: false
	                        },
	                        emphasis: {
	                            show: false,
	                            textStyle: {
	                                color: '#fff'
	                            }
	                        }
	                    },
	                    roam: false,
	                    itemStyle: {
	                        normal: {
	                            areaColor: '#031525',
	                            borderColor: '#FFFFFF',
	                        },
	                        emphasis: {
	                            areaColor: '#2B91B7'
	                        }
	                    },
	                    animation: false,
	                    data: data
	                },
	                {
	                    name: 'Top 5',
	                    type: 'scatter',
	                    coordinateSystem: 'geo',
	                    symbol: 'pin',
	                    symbolSize: [50,50],
	                    label: {
	                        normal: {
	                            show: true,
	                            textStyle: {
	                                color: '#fff',
	                                fontSize: 9,
	                            },
	                            formatter (value){
	                                return value.data.value[2]
	                            }
	                        }
	                    },
	                    itemStyle: {
	                        normal: {
	                            color: '#D8BC37', // 标志颜色
	                        }
	                    },
	                    data: convertData(data),
	                    showEffectOn: 'render',
	                    rippleEffect: {
	                        brushType: 'stroke'
	                        },
	                        hoverAnimation: true,
	                        zlevel: 1
	                    },
	                ]
	                };
	                myChartdt.setOption(optiondt);
	            }
	        }
//	   }
}

// 4情感分析图表
function emotionAnalysisChart(data) {
    var chart = c3.generate({
        bindto: '#visitor2',
        data: {
//            columns: [
//                ['Success', 45],
//                ['Pending', 15],
//                ['Failed', 27]
//            ],
            columns: data,
            type: 'donut',
            onclick: function (d, i) { console.log("onclick", d, i); },
            onmouseover: function (d, i) { console.log("onmouseover", d, i); },
            onmouseout: function (d, i) { console.log("onmouseout", d, i); }
        },
        donut: {
            label: {
                show: false
            },
            title: "情感分析",
            width: 25,
        },
        legend: {
            hide: true
            // or hide: 'data1'
            // or hide: ['data1', 'data2']
        },
        color: {
            pattern: ['#40c4ff', '#2961ff', '#ff821c']
        }
    });
} 


// 11、网民高频词云 图表
function netizenWordCloudChart(data){
	anychart.onDocumentReady(function () {
	    var chart = anychart.tagCloud(data);
	    chart.container("keywords1");
	    chart.draw();
	});
	setTimeout(function () {
	    $('.anychart-credits').remove();
	}, 150);
}

// 12、媒体高频词云 图表
function mediaCordCloudChart(data){
	anychart.onDocumentReady(function () {
	    var chart = anychart.tagCloud(data);
	    chart.container("keywords2");
	    chart.draw();
	});
	setTimeout(function () {
	    $('.anychart-credits').remove();
	}, 150);
}




var a =
         [
            { x:"第一季度",value:"999721"},
            { x: "中共中央政治局", value: 51721 },
            { x: "常务委员会", value: 269721 },
            { x: "会议", value: 385721 },
            { x: "听取", value: 342721 },
            { x: "吉林长春", value: 477721 },
            { x: "长生", value: 368721 },
            { x: "公司", value: 421721 },
            { x: "疫苗", value: 43721 },
            { x: "案件", value: 368721 },
            { x: "调查", value: 411721 },
            { x: "问责", value: 219721 },
            { x: "情况", value: 412721 },
            { x: "汇报", value: 174721 },
            { x: "中共中央", value: 371721 },
            { x: "总书记", value: 475721 },
            { x: "习近平", value: 372721 },
            { x: "主持会议", value: 489721 },
            { x: "发表", value: 259721 },
            { x: "重要讲话", value: 388721 },
            { x: "这起", value: 422721 },
            { x: "发生", value: 195721 },
            { x: "高度重视", value: 102721 },
            { x: "作出", value: 75721 },
            { x: "指示", value: 116721 },
            { x: "查清", value: 273721 },
            { x: "事实真相", value: 299721 },
            { x: "严肃", value: 349721 },
            { x: "依法", value: 219721 },
            { x: "从严处理", value: 448721 },
            { x: "守住", value: 276721 },
            { x: "底线", value: 270721 },
            { x: "保障", value: 404721 },
            { x: "群众", value: 246721 },
            { x: "切身利益", value: 94721 },
            { x: "社会", value: 440721 },
         ]
  
         





        $("#showinfo").click(function (param) {
            $("#showhot").show()
         })
         $(".mdi.mdi-close-circle-outline").click(function (param) {
            $("#showhot").hide()
           })

        $(function () {
            // ==============================================================
            // Our Visitor
            // ==============================================================
            // onclick: function(d, i) { console.log("onclick", d, i); },
            // onmouseover: function(d, i) { console.log("onmouseover", d, i);
			// },
            // onmouseout: function(d, i) { console.log("onmouseout", d, i); }
            var chart = c3.generate({
                bindto: '#visitor',
                data: {
                    columns: [
                        ['正面', 290],
                        ['中性', 423],
                        ['负面', 128],
                    ],
                    type: 'donut',

                },
                donut: {
                    label: {
                        show: false
                    },
                    title: " ",
                    width: 30,
                },

                legend: {
                    hide: true
                    // or hide: 'data1'
                    // or hide: ['data1', 'data2']
                },
                color: {
                    pattern: ['#d65353', '#ffae00', '#43c0ab']
                }
            });


            // loadding
            setTimeout(() => {
                $("#loading1").remove()
                $("#loading2").remove()
                $("#loading3").remove()
                $("#loading4").remove()
                $("#loading5").remove()
                $("#loading6").remove()
                $("#loading7").remove()
                $("#loading8").remove()
                $("#loading9").remove()
            }, 100);


            new Chart(document.getElementById("campaign"), {
                type: 'line',
                data: {
                    labels: [1, 2, 3, 4, 5, 6, 7, 8],
                    datasets: [{
                        data: [3, 8, 2, 3, 2, 5, 6, 8],
                        label: "A",
                        borderColor: "#2961ff",
                        borderWidth: 1,
                        backgroundColor: "rgba(41, 97, 255, .3)",
                        pointBackgroundColor: "#2961ff",
                    }, {
                        data: [7, 6, 5, 8, 6, 7, 2, 1],
                        label: "B",
                        borderColor: "#4dd0e1",
                        borderWidth: 1,
                        backgroundColor: "rgba(77, 208, 225, .3)",
                        pointBackgroundColor: "#4dd0e1",
                    }]
                },
                options: {
                    elements: { point: { radius: 2 } },
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        xAxes: [{
                            gridLines: {
                                display: false,
                                drawBorder: false,
                            },
                            ticks: {
                                display: false
                            }
                        }],
                        yAxes: [{
                            gridLines: {
                                display: false,
                                drawBorder: false,
                            },
                            ticks: {
                                display: false
                            }
                        }]
                    },
                    layout: {
                        padding: {
                            left: -10,
                            right: 0,
                            top: 0,
                            bottom: -10
                        }
                    },
                    legend: {
                        display: false,
                        labels: {
                            fontColor: 'rgb(255, 99, 132)'
                        }
                    }
                }
            });



            var sparklineLogin = function () {
                $('#visits').sparkline([6, 10, 9, 11, 9, 10, 12, 10, 9, 11, 9, 10, 12, 10, 9, 11, 9, 9, 11, 9, 10, 12, 10], {
                    type: 'bar',
                    height: '60',
                    barWidth: '4',
                    width: '100%',
                    resize: true,
                    barSpacing: '6',
                    barColor: '#4dd0e1'
                });
            };
            // $(window).resize(function (e) {
            // clearTimeout(sparkResize);
            // sparkResize = setTimeout(sparklineLogin, 500);
            // });
            sparklineLogin();


            var chart = c3.generate({
                bindto: '#visitor',
                data: {
                    columns: [
                        ['Open', 45],
                        ['Clicked', 15],
                        ['Un-opened', 27],
                        ['Bounced', 18],
                    ],

                    type: 'donut',
                },
                donut: {
                    label: {
                        show: false
                    },
                    title: "Ratio",
                    width: 35,

                },

                legend: {
                    hide: true
                    // or hide: 'data1'
                    // or hide: ['data1', 'data2']
                },
                color: {
                    pattern: ['#40c4ff', '#2961ff', '#ff821c', '#7e74fb']
                }
            });


            $('#visitfromworld').vectorMap({
                map: 'world_mill_en',
                backgroundColor: 'transparent',
                borderColor: '#000',
                borderOpacity: 0,
                borderWidth: 0,
                zoomOnScroll: false,
                color: '#93d5ed',
                regionStyle: {
                    initial: {
                        fill: '#93d5ed',
                        'stroke-width': 1,
                        'stroke': '#fff'
                    }
                },
                markerStyle: {
                    initial: {
                        r: 5,
                        'fill': '#93d5ed',
                        'fill-opacity': 1,
                        'stroke': '#93d5ed',
                        'stroke-width': 1,
                        'stroke-opacity': 1
                    },
                },
                enableZoom: true,
                hoverColor: '#79e580',
                markers: [{
                    latLng: [21.00, 78.00],
                    name: 'India : 9347',
                    style: { fill: '#2961ff' }
                },
                {
                    latLng: [-33.00, 151.00],
                    name: 'Australia : 250',
                    style: { fill: '#ff821c' }
                },
                {
                    latLng: [36.77, -119.41],
                    name: 'USA : 250',
                    style: { fill: '#40c4ff' }
                },
                {
                    latLng: [55.37, -3.41],
                    name: 'UK   : 250',
                    style: { fill: '#398bf7' }
                },
                {
                    latLng: [25.20, 55.27],
                    name: 'UAE : 250',
                    style: { fill: '#6fc826' }
                }
                ],
                hoverOpacity: null,
                normalizeFunction: 'linear',
                scaleColors: ['#93d5ed', '#93d5ee'],
                selectedColor: '#c9dfaf',
                selectedRegions: [],
                showTooltip: true,
                onRegionClick: function (element, code, region) {
                    var message = 'You clicked "' + region + '" which has the code: ' + code.toUpperCase();
                    alert(message);
                }
            })
            var chart = c3.generate({
                bindto: '.earningsbox',
                data: {
                    columns: [
                        ['Site A', 5, 6, 3, 7, 9, 10, 14, 12, 11, 9, 8, 7, 10, 6, 12, 10, 8]
                    ],
                    type: 'area-spline'
                },
                axis: {
                    y: {
                        show: false,
                        tick: {
                            count: 0,
                            outer: false
                        }
                    },
                    x: {
                        show: false,
                    }
                },
                padding: {
                    top: 0,
                    right: -8,
                    bottom: -28,
                    left: -8,
                },
                point: {
                    r: 0,
                },
                legend: {
                    hide: true
                    // or hide: 'data1'
                    // or hide: ['data1', 'data2']
                },
                color: {
                    pattern: ['#2961ff']
                }
            });
        
        })
  
