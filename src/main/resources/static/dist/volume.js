/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 声量监测js
 */

/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 切换方案请求数据
 */
var groupId = analysis_groupid;
var projectid = analysis_projectid;
$(function () {
    //var projectid="1248505925241802752";
    var time_period = $("#select").val();
    project(projectid, time_period);
    projectname(projectid, groupId);
});


function switchProject(event) {
    let $event = $(event);
    let projectId = $event.attr("data-index");
    let group_id = $event.attr("data-groupid");
    groupId = group_id;
    projectid = projectId;

    projectname(projectid, groupId);
    console.log("projectid=====>" + projectid);
    console.log("group_id=====>" + group_id);
    var time_period = $("#select").val();
    project(projectid, time_period)
}

function projectname(projectid, groupId) {
    $.ajax({
        url: ctxPath + "volume/projectname",
        type: "POST",
        dataType: "json",
        data: {
            "projectid": projectid,
            "groupId": groupId
        },
        success: function (res) {
            var groupname = res.group_name
            var projectname = res.project_name
            var namehtml = '<li class="breadcrumb-item">声量监测</li>';
            if(groupname){
                namehtml += '<li class="breadcrumb-item">' + groupname + '</li>';
            }
            if(projectname){
                namehtml += '<li class="breadcrumb-item">' + projectname + '</li>';
            }
            $("#navname").html(namehtml);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                console.log("请求出错");
            }
        }
    });
}

function func() {
    //获取被选中的option标签
    var vs = $('select  option:selected').val();
    console.info(vs)
//	 yan();
    project(projectid, vs)
}

function project(projectId, time_period) {
    loading("#biaoge")
    loading("#category-data")
    loading("#category-line")
    loading("#keyword_news_rank")
    loading("#keywords")
    loading("#keyword_exposure_rank")
    loading("#media_user_volume_rank")
    if(!projectId){
    	nodata('#category-data', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#category-line', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#guandian', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#rdian', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#biaoge', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#category', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#category', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#keyword_news_rank', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#keywords', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#container2', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#keyword_exposure_rank', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#shejiao', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
        nodata('#media_user_volume_rank', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupId);
    	return;
    }
    $.ajax({
        url: ctxPath + "volume/getproject",
        type: "POST",
        dataType: "json",
        data: {
            "projectid": projectId,
            "time_period": time_period
        },
        success: function (res) {
//        	 关键词情感info
            if (JSON.stringify(res) == JSON.stringify({})) {
                nodata('#category-data', '数据正在更新中...');
                nodata('#category-line', '数据正在更新中...');
                nodata('#guandian', '数据正在更新中...');
                nodata('#rdian', '数据正在更新中...');
                nodata('#biaoge', '数据正在更新中...');
                nodata('#category', '数据正在更新中...');
                nodata('#category', '数据正在更新中...');
                nodata('#keyword_news_rank', '数据正在更新中...');
                nodata('#keywords', '数据正在更新中...');
                nodata('#container2', '数据正在更新中...');
                nodata('#keyword_exposure_rank', '数据正在更新中...');
                nodata('#shejiao', '数据正在更新中...');
                nodata('#media_user_volume_rank', '数据正在更新中...');
            } else {
                var context = '<i class="fa fa-info-circle m-r-10"></i>' + res.keywordsMood.china;
                $("#keywordsMood").html(context);
//	        	 关键词情感图表
                keywordsMood(res.keywordsMood.list)

//	        	 关键词来源分布
                var biaocontext = '<div class="volume-tips"><i class="fa fa-info-circle m-r-10"></i>' + res.biaoge.china + '</div>'
//	        	 $("#biaocontext").html(biaocontext);
                var list = res.biaoge.list;
                var hot = ''
                var title =
                    '<div class="volume-line-title">' +
                    '    <span>关键词</span>' +
                    '    <span>总数量</span>' +
                    '    <span>网站</span>' +
                    '    <span>客户端</span>' +
                    '    <span>新闻</span>' +
                    '    <span>微博</span>' +
                    '    <span>论坛</span>' +
                    '    <span>报刊</span>' +
                    '    <span>微信</span>' +
                    '    <span>视频</span>' +
                    '    <span>政务</span>' +
                    '</div>'
                var biage = ''
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        if (i < 3) {
                            hot = 'v-hot' + (i + 1)
                        }
                        var keywordHtml = '<span><span class="v-hot ' + hot + '">' + (i + 1) +'</span>' + list[i].keyword + '</span>';
                        if(list[i].keyword.length > 5){
                        	keywordHtml = '<span title="' + list[i].keyword + '"><span class="v-hot ' + hot + '">' + (i + 1) +'</span>' + list[i].keyword.substring(0, 5) +'...' + '</span>';
                        }
                        biage += '<div class="volume-line-content">'+ keywordHtml
                            + '<span>' + list[i].total + '</span><span>' + list[i].web + '</span><span>' + list[i].app + '</span><span>' + list[i].news + '</span><span>' + list[i].weibo + '</span><span>' + list[i].bbs + '</span><span>' + list[i].newspaper + '</span>'
                            + '<span>' + list[i].wechat + '</span><span>' + list[i].video + '</span><span>' + list[i].gov + '</span></div>';
                    }
                    var keysource = biaocontext + '<div class="volume-data">' + title + biage + '</div>'
                    $("#biaoge").html(keysource);
                } else {
                    $("#biaoge").css("min-height", "300px")
                    nodata("#biaoge")
                }


                var zou = res.keywordsLine;
                keywordsLine(zou);

                var news_rank = res.keyword_news_rank;
                var keywordnew = ''
                for (var i = 0; i < news_rank.length; i++) {
                    var keyword = news_rank[i].keyword;
                    var count = news_rank[i].count;
                    var rate = news_rank[i].rate;
                    var pming = "";
                    if (i < 3) {
                        pming = " v-hot" + (i + 1);
                    }
                    keywordnew += '<div class="volume-line2-content"><span> <span class="v-hot' + pming + '">' + (i + 1) + '</span>' + keyword + '</span>' +
                        '<span><span title="' + count + '条" style="width: ' + rate + '"></span></span><span>' + rate + '</span></div>';

                }
                $("#keyword_news_rank").html(keywordnew);

                var highwords = res.highword_cloud;
                keywordsCloud(highwords)

                loading("#keyword_exposure_rank")
                var keywordrank = res.keyword_exposure_rank;
                var keywordsranks = '<div class="expose-rank-title"><span>关键词</span><span>总数量</span><span>曝光量</span><span>环比增速(%)</span><span>排名</span></div>';
                for (var i = 0; i < keywordrank.length; i++) {
                    console.log(keywordrank[i])
                    var total = keywordrank[i].total
                    var trend = keywordrank[i].trend
                    var keyword = keywordrank[i].keyword
                    var chain_growth = keywordrank[i].chain_growth
                    var positive_rate = parseInt(keywordrank[i].positive_rate)
                    var negative_rate = parseInt(keywordrank[i].negative_rate)
                    if (keywordrank[i].positive_rate == null) {
                        positive_rate = 0
                    }
                    if (keywordrank[i].negative_rate == null) {
                        negative_rate = 0
                    }
                    var arrow = ''
                    var arrownum = parseFloat(keywordrank[i].chain_growth.replace(/,/g, ''))
                    console.log(arrownum)
                    if (arrownum > 0) {
                        arrow = "e-zm mdi-arrow-up"
                    } else if (arrownum < 0) {
                        arrow = "e-fm mdi-arrow-down"
                    } else {
                        arrow = "mdi-dots-horizontal"
                    }
                    var arrowtext = parseInt(keywordrank[i].chain_growth.replace(/,/g, ''))
                    var keywordsrank =
                        '<div class="expose-rank-content">' +
                        '	<span>' + keyword + '</span>' +
                        '<span>' +
                        '	<div>' + total + '</div>' +
                        '	<div>' +
                        '		<div class="expose-zm" style="width: ' + positive_rate + '%;" title="正面"></div>' +
                        '		<div class="expose-fm" style="width: ' + negative_rate + '%;left: ' + positive_rate + '%;" title="负面"></div>' +
                        '		<div class="expose-zx" style="width: ' + (100 - negative_rate - positive_rate) + '%;left: ' + (positive_rate + negative_rate) + '%;" title="中性"></div>' +
                        '	</div>' +
                        '</span>' +
                        '<span>' + arrowtext + '<i class="mdi ' + arrow + '"></i></span>' +
                        '<span>' + (i + 1) + '</span>' +
                        '</div>';
                    keywordsranks += keywordsrank;
                }
                $("#keyword_exposure_rank").html(keywordsranks)

                var media_userrank = res.media_user_volume_rank
                var userranks = '';
                for (var i = 0; i < media_userrank.length; i++) {
                    var logo = media_userrank[i].logo;
                    var platform_name = media_userrank[i].platform_name;
                    var name = media_userrank[i].name;
                    var release_count = media_userrank[i].release_count;
                    var fans_count = media_userrank[i].fans_count;
                    var volume = media_userrank[i].volume;
                    var logo = media_userrank[i].logo;
                    var email = media_userrank[i].email;
                    var platform_names = media_userrank[i].platform_names;
	        		var pnames = platform_names.split(',');
	        		var platform_namehtml = '';
	        		if(pnames.length <= 3){
	        			platform_namehtml = platform_names;
	        		}else{
	        			platform_namehtml = pnames[0] + ',' + pnames[1] + ',' + pnames[2] + '...<span title="'+platform_names+'">('+pnames.length+')</span>';
	        		}
	        		var logoHtml = '<img src="'+logo+'"alt="user" class="rounded-circle" width="45"/>';
	        		if(!logo) logoHtml = '<img src="'+ctxPath+'dist/img/user.jfif" alt="user" alt="user" class="rounded-circle" width="45"/>';
	        		var userrank='<tr><td><div class="d-flex align-items-center"><div class="m-r-10">'+logoHtml+'</div>'+
	        				'<div class=""><h4 class="m-b-0 font-16">'+name+'</h4><span></span></div></div></td><td>'+platform_namehtml+'</td><td class="font-medium">'+volume+'</td></tr>';
	        		userranks += userrank
                }
                $("#media_user_volume_rank").html(userranks)
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                $("#popularinformation").css({"position": "relative", "min-height": "300px"})
                dataerror("#popularinformation")
            }
        }
    })
}

//柱状图
//keywordsMood()
//折线图
//keywordsLine()
//词云
//keywordsCloud()
//用户画像
userPortrayal()

// 关键词高频分布统计
function keywordsCloud(highwords) {
	if(highwords.length == 0){
		nodata('#keywords', '数据正在更新中...')
	}else{
	    anychart.onDocumentReady(function () {
	        // Creates Tag Cloud chart.
	        var a = highwords
	        var chart = anychart.tagCloud(a);
	        chart.container("keywords");
	        chart.draw();
	        clearlogo();
	    });
	}

}

function userPortrayal() {
    anychart.onDocumentReady(function () {
        // The data used in this sample can be obtained from the CDN  Frequency
        // https://cdn.anychart.com/samples/tag-cloud/most-frequent-words-us-presidents/data.js
        var stage = acgraph.create('container2');

        // creates standalones title
        var title = anychart.standalones.title();
        title.padding(10)
            .text('');

        // create tag cloud with Barack Obama data
        var chartFirst = anychart.tagCloud();
        chartFirst.data(anychart.data.parseText(getDataFirst()))
        // set color scale
            .colorScale(anychart.scales.ordinalColor().colors(['#60727b']))
            // set starting angle for automatic angle availability calculation
            .fromAngle(-45)
            // set finishing angle for automatic angle availability calculation
            .toAngle(45)
            // set number of angles for automatic calculation of available angles
            .anglesCount(3)
            // additional empty space in all directions from the text, only in pixels
            .textSpacing(5)
            // set chart bounds
            .bounds(0, '5%', '33%', '95%')
            // enabled legend
            .legend(true);

        // create tag cloud with George Bush data
        var chartSecond = anychart.tagCloud();
        console.log(getDataThird(), "++++++++++++")
        chartSecond.data(anychart.data.parseText(getDataSecond()))
        // set color scale
            .colorScale(anychart.scales.ordinalColor().colors(['#f18126']))
            // set starting angle for automatic angle availability calculation
            .fromAngle(-45)
            // set finishing angle for automatic angle availability calculation
            .toAngle(45)
            // set number of angles for automatic calculation of available angles
            .anglesCount(3)
            // additional empty space in all directions from the text, only in pixels
            .textSpacing(5)
            // set chart bounds
            .bounds('33%', '5%', '33%', '95%')
            // enabled legend
            .legend(true);

        // create tag cloud with Ronald Reagan data
        var chartThird = anychart.tagCloud();
        chartThird.data(anychart.data.parseText(getDataThird()))
        // set color scale
            .colorScale(anychart.scales.ordinalColor().colors(['#3b8ad8']))
            // set starting angle for automatic angle availability calculation
            .fromAngle(-45)
            // set finishing angle for automatic angle availability calculation
            .toAngle(45)
            // set number of angles for automatic calculation of available angles
            .anglesCount(3)
            // additional empty space in all directions from the text, only in pixels
            .textSpacing(5)
            // set chart bounds
            .bounds('66%', '5%', '33%', '95%')
            // enabled legend
            .legend(true);

        // set stage for the title
        title.container(stage);
        // initiate title drawing
        title.draw();

        // set stage for the chart
        chartFirst.container(stage);
        // initiate chart drawing
        chartFirst.draw();

        // set stage for the chart
        chartSecond.container(stage);
        // initiate chart drawing
        chartSecond.draw();

        // set stage for the chart
        chartThird.container(stage);
        // initiate chart drawing
        chartThird.draw();
        clearlogo();
    });
}


//关键词情感分析数据统计分布
function keywordsMood(data) {
    anychart.onDocumentReady(function () {
        // create data set on our data
        var dataSet = anychart.data.set(data);

        // map data for the first series, take x from the zero column and value from the first column of data set
        var seriesData_1 = dataSet.mapAs({'x': 0, 'value': 1});

        // map data for the second series, take x from the zero column and value from the second column of data set
        var seriesData_2 = dataSet.mapAs({'x': 0, 'value': 2});

        // create column chart
        var chart = anychart.column();

        // turn on chart animation
        chart.animation(true);

        // set chart title text settings
        chart.title('');

        // temp variable to store series instance
        var series;

        // helper function to setup label settings for all series
        var setupSeries = function (series, name) {
            series.name(name);
            series.selected()
                .fill('#f48fb1')
                .stroke('1.5 #c2185b');
        };

        // create first series with mapped data
        series = chart.column(seriesData_1);
        series.xPointPosition(0.45);
        setupSeries(series, '正面');

        // create second series with mapped data
        series = chart.column(seriesData_2);
        series.xPointPosition(0.25);
        setupSeries(series, '负面');

        // set chart padding
        chart.barGroupsPadding(0.3);

        // format numbers in y axis label to match browser locale
        chart.yAxis().labels().format('{%Value}{groupsSeparator: }');

        // set titles for Y-axis
        chart.yAxis().title('');

        // turn on legend
        chart.legend()
            .enabled(true)
            .fontSize(13)
            .padding([0, 0, 20, 0]);

        chart.interactivity().hoverMode('single');

        chart.tooltip().format('{%Value}{groupsSeparator: }');

        // set container id for the chart
        chart.container('category-data');

        // initiate chart drawing
        chart.draw();
        clearlogo()
    });
}

function clearlogo() {
    setTimeout(() => {
        $(".anychart-credits").hide()
    }, 100);
}

// 关键词情感分析数据走势
function keywordsLine(datalist) {
    loading("#category-line")

    function getData() {
    	
        return datalist
    }

    anychart.onDocumentReady(function () {
        // create data set on our data
        var dataSet = anychart.data.set(getData());

        // map data for the first series, take x from the zero column and value from the first column of data set
        var seriesData_1 = dataSet.mapAs({'x': 0, 'value': 1});

        // map data for the second series, take x from the zero column and value from the second column of data set
        var seriesData_2 = dataSet.mapAs({'x': 0, 'value': 2});

        // create line chart
        var chart = anychart.line();

        // adding dollar symbols to yAxis labels
        chart.yAxis().labels().format('{%Value}');

        // turn on chart animation
        chart.animation(true);

        // turn on the crosshair
        chart.crosshair()
            .enabled(true)
            .yLabel({enabled: false})
            .yStroke(null)
            .xStroke('#cecece')
            .zIndex(99);

        chart.yAxis()
            .title('')
            .labels({'padding': [5, 5, 0, 5]});
        chart.xAxis().title('');

        // set chart title text settings
        chart.title('');

        // create first series with mapped data
        var series_1 = chart.spline(seriesData_1);
        series_1.name('正面');
        series_1.markers().zIndex(100);
        series_1.hovered().markers()
            .enabled(true)
            .type('circle')
            .size(4);

        // create second series with mapped data
        var series_2 = chart.spline(seriesData_2);
        series_2.name('负面');
        series_2.markers().zIndex(100);
        series_2.hovered().markers()
            .enabled(true)
            .type('circle')
            .size(4);

        // turn the legend on
        chart.legend()
            .enabled(true)
            .fontSize(13)
            .padding([0, 0, 20, 0]);

        // set container id for the chart
        chart.container('category-line');

        // initiate chart drawing
        chart.draw();
        clearlogo();
    });
}

//观点聚类
$("#viewponitbox").on(".viewpoint-content", "click", function (that) {
    console.log(that)
    showpoint()
})
