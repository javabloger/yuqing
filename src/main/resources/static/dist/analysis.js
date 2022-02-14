/**
 * @author huajiancheng
 * @description 监测分析js
 */
let anal_projectId = '';
let anal_groupId = '';
let reporttime = '';
let nerresultdata = '';


let event_statistics = [];

function setId(groupId, projectId) {
    anal_projectId = projectId;
    anal_groupId = groupId;
}

/**
 * @author huajiancheng
 * @description 切换方案请求数据
 */
function switchProject(event) {
    let $event = $(event);
    let projectid = $event.attr("data-index");
    let group_id = $event.attr("data-groupid");
    anal_projectId = projectid;
    anal_groupId = group_id;
    projectname(projectid, group_id);
    getlatestnews(anal_projectId, anal_groupId);
    getAnalysisMonitorProjectid();
    let url = 'analysis?groupid=' + group_id + '&projectid=' + projectid
    setUrl(url);
}

// 时间周期选择
function timePeriodSelectFunc() {
    getlatestnews(anal_projectId, anal_groupId);
    getAnalysisMonitorProjectid()
}

// 设置面包屑导航
function projectname(projectid, groupId) {
    $.ajax({
        url: "/volume/projectname",
        type: "POST",
        dataType: "json",
        data: {
            "projectid": projectid,
            "groupId": groupId
        },
        success: function (res) {
            var groupname = res.group_name
            var projectname = res.project_name
            var namehtml = '<li class="breadcrumb-item">监测分析</li>';
            if (groupname) {
                namehtml += '<li class="breadcrumb-item">' + groupname + '</li>';
            }
            if (projectname) {
                namehtml += '<li class="breadcrumb-item">' + projectname + '</li>';
            }
            $("#navname").html(namehtml);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                console.log(xhr);
            }
        }
    });
}

/**
 * @author kanshicheng
 * @description 最新资讯
 */
function getlatestnews(analysis_projectid) {
    loading("#relative_news");
    if (!analysis_projectid) {
        nodata('#relative_news', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid=' + anal_groupId);
        return;
    }
    var timePeriod = $('#timePeriodSelect').val();
    $.ajax({
        url: "/analysis/latestnews",
        type: "post",
        data: {
            projectid: analysis_projectid,
            timePeriod: timePeriod
        },
        success: function (data) {
            var list = JSON.parse(data);
            if (list.length < 1) {
                nodata('#relative_news', '大概5-10分钟之后更新完成...');
            } else {
                var html = "";
                for (var i = 0; i < list.length; i++) {
                    var emotion = list[i].emotion;
                    var emotionclass = '';
                    if (emotion == 2) {
                        emotion = '中性';
                        emotionclass = 'emotion zx';
                    } else if (emotion == 1) {
                        emotion = '积极';
                        emotionclass = 'emotion zm';
                    } else {
                        emotion = '消极';
                        emotionclass = 'emotion fm';
                    }

                    let title = list[i].title;
                    if(title.length > 25){
                        title = title.substring(0 , 24) + "......";
                    }

                    html += '<li>' +
                        '<a target="_blank" href="' + ctxPath + 'monitor/detail/' + list[i].article_public_id + '?menu=analysis&groupid=' + anal_groupId + '&projectid=' + anal_projectId + '">' +
                        ' <div class="over-newsbox">' +
                        ' <div class="content-title">' +
                        ' <div class="over-title" title="'+list[i].title+'">' + title + '</div> ' +
                        ' <span class="' + emotionclass + '">' + emotion + '</span>' +
                        '</div>' +
                        '<div class="over-tips">' +
                        ' <span>' + timeParse(list[i].publish_time) + '</span>' +
                        ' <span>来源：' + list[i].source_name + '</span>' +
                        ' </div>' +
                        '</div>' +
                        '<i class="mdi mdi-chevron-right font-20"></i>' +
                        '</a>' +
                        '</li>'
                }
                $("#relative_news").html(html);
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                nodata("#relative_news", '大概5-10分钟之后更新完成...');
            }
        }
    })
}

var hotEventRanking = null;
var dataSourceAnalysis = null;

function noData() {
    nodata("#dataOverview", '暂无数据！');
    nodata("#emotionRateChart", '暂无数据！');
    $('#emotional_proportion').html('');
    nodata("#planwordhit", '暂无数据！');
    $('#keywordsMood').html('');
    nodata("#category-line", '暂无数据！');
    $('#hotEventRankingTable').css('min-height', '150px');
    nodata("#hotEventRanking", '暂无数据！');
    nodata("#keywords", '暂无数据！');
    nodata("#keywordindex", '暂无数据！');
    $('#mediaActivityAnalysis').html('');
    //nodata("#mediaActivityAnalysisList", '大概5-10分钟之后更新完成...');
    nodata("#mediaActivityAnalysisList", '暂无数据！');
    nodata("#dataSourceDistributionChart", '暂无数据！');
    nodata("#dataSourceAnalysis", '暂无数据！');
    nodata("#keyword_exposure_rank", '暂无数据！');
    nodata("#media_user_volume_rank", '暂无数据！');
    nodata("#hotSpotRankingChart", '暂无数据！');
    nodata("#eventStatisticsChart",'暂无数据！');
    nodata("#industrialDistributiontionChart",'暂无数据！');
    nodata("#industrial_distribution",'暂无数据！');

    $('#hotSpotRankingList').html('');
}

function loadingData() {
    loading("#dataOverview");
    loading("#emotionRateChart");
    loading("#planwordhit");
    loading("#category-line");
    loading("#hotEventRanking");
    loading("#keywords");
    loading("#InstitutionalunitsRanking");
    loading("#IndustryANDEvent");
    loading("#keywordindex");
    loading("#mediaActivityAnalysisList");
    loading("#dataSourceDistributionChart");
    loading("#dataSourceAnalysis");
    loading("#keyword_exposure_rank");
    loading("#media_user_volume_rank");
    loading("#highPolicyChinaIndexList");
    loading("#category_rank");
// loading("#emotional_proportion");
// loading("#hotSpotRankingList");
    $('#hotSpotRankingList').html('');
    loading("#hotSpotRankingChart");

}

// 获取监测分析数据
function getAnalysisMonitorProjectid() {
    loadingData();
    var timePeriod = $('#timePeriodSelect').val();
    $.ajax({
        url: ctxPath + "analysis/getAanlysisByProjectidAndTimeperiod",
        type: "post",
        data: {
            projectId: anal_projectId,
            timePeriod: timePeriod
        },
        success: function (res) {
            if (res == 'null' || !res) {
                noData();
                return;
            }
            noData();

            var analysis = JSON.parse(res);
            reporttime = analysis.create_time;
            $('#updateTime').html(timeParse(analysis.create_time));

            // 数据概览
            if (analysis.data_overview) {
                dataOverview(JSON.parse(analysis.data_overview));
            }
            // 情感占比
            if (analysis.emotional_proportion) {
                emotionalRate(JSON.parse(analysis.emotional_proportion));
            }

            // 行业分布统计
            if (analysis.industrial_distribution) {
                industrialDistribution(JSON.parse(analysis.industrial_distribution));
            }


            // 事件统计
            if (analysis.event_statistics) {
                /*event_statistics = JSON.parse(analysis.event_statistics);*/
                eventStatistics(JSON.parse(analysis.event_statistics));
            }

            // 方案命中主体词
            if (analysis.plan_word_hit) {
                planWordHit(JSON.parse(analysis.plan_word_hit));
            }
            
         // f分类趋势
            if (analysis.category_rank) {
            	category_rankdata(JSON.parse(analysis.category_rank));
            }
            
            
            
            // 关键词情感分析数据走势
            if (analysis.keyword_emotion_trend) {
                keywordsLine(JSON.parse(analysis.keyword_emotion_trend));
            }
            // 热点事件排名
            if (analysis.hot_event_ranking) {
                hotEventRanking = JSON.parse(analysis.hot_event_ranking);
                toHtmlHotEventRanking(0, hotEventRanking.negative);
            }
            // 关键词高频分布统计
            if (analysis.highword_cloud) {
                keywordsCloud(JSON.parse(analysis.highword_cloud));
            }
            // 高频词指数
            if (analysis.keyword_index) {
                keywordIndex(JSON.parse(analysis.keyword_index));
            }
            // 媒体活跃度分析
            if (analysis.media_activity_analysis) {
                toHtmlMediaActivityAnalysis(JSON.parse(analysis.media_activity_analysis));
            }
            // 热点地区排名
            if (analysis.hot_spot_ranking) {
                toHtmlHotSpotRanking(JSON.parse(analysis.hot_spot_ranking));
            }
            // 数据来源分布
            if (analysis.data_source_distribution) {
                dataSourceDistribution(JSON.parse(analysis.data_source_distribution));
            }
            
            // 数据来源分析
            if (analysis.data_source_analysis) {
                dataSourceAnalysis = JSON.parse(analysis.data_source_analysis);
                var list = dataSourceAnalysis.list;
                var html = '<li class="create-tab-act" data-type="0">全部</li>';
                if(list.length > 8 ) {
                    for (var i = 0; i < 8/*list.length*/; i++) {//修改展示8个
                        html += '<li  data-type=' + list[i].key + '>' + list[i].name + '</li>';
                    }
                }else{
                    for (var i = 0; i < list.length; i++) {
                        html += '<li  data-type=' + list[i].key + '>' + list[i].name + '</li>';
                    }
                }
                $('#search-tab2').html(html);
                dataSourceAnalysisHtml(dataSourceAnalysis.all);
            }
            // 关键词曝光度环比排行
            if (analysis.keyword_exposure_rank) {
                keywordExposureRank(JSON.parse(analysis.keyword_exposure_rank));
            }
            // 自媒体渠道声量排名
            if (analysis.selfmedia_volume_rank) {
                selfMediaRank(JSON.parse(analysis.selfmedia_volume_rank));
            }
            
         // 热点企业排名
//            if (analysis.ner) {
//            	var org = JSON.parse(analysis.ner).org;
//                hotOrgRank(org);
//            }
         // 热点银行排名
//            if (analysis.ner) {
//            	var nto = JSON.parse(analysis.ner).nto;
//                hotNtoRank(nto);
//            } 
         // 热点上市企业ipo_china
//            if (analysis.ner) {
//            	var ipo_foreign = JSON.parse(analysis.ner).ipo_foreign;
//            	highIPOForeignIndexList(ipo_foreign);
//            }
         // 热点海外上市企业ipo_china
//            if (analysis.ner) {
//            	var ipo_china = JSON.parse(analysis.ner).ipo_china;
//            	hotIPOChinaRank(ipo_china);
//            }
         // 政府及事业单位统计排名
//            if (analysis.ner) {
//            	var ner = JSON.parse(analysis.ner);
//            	institutionalunits(ner);
//            	nerresultdata = ner;
//            }
            
         // 上市企业
//            if (analysis.ner) {
//            	var ner = JSON.parse(analysis.ner);
//            	orgunits(ner);
//            	nerresultdata = ner;
//            }
            
            
            // 热门行业&事件统计
            industryandevent();
            
         // 政策相关
            if (analysis.ner) {
            	var policy = JSON.parse(analysis.ner).policy;
            	hotPolicyChinaRank(policy);
            }
            
            
            
            
// // 热点机构排名
// if (analysis.ner) {
// debugger;
// var org = JSON.parse(analysis.ner).org;
// hotOrgRank(org);
// }
// // 热点学校
// if (analysis.ner) {
// debugger;
// var school = JSON.parse(analysis.ner).school;
// hotSchoolRank(school);
// }
// // 热点政府机构
// if (analysis.ner) {
// debugger;
// var nto = JSON.parse(analysis.ner).nto;
// hotNtoRank(nto);
// }
// // 热点政府机构
// if (analysis.ner) {
// debugger;
// var ipo = JSON.parse(analysis.ner).ipo;
// hotIpoRank(ipo);
// }
// // 热点人物
// if (analysis.ner) {
// debugger;
// var per = JSON.parse(analysis.ner).per;
// hotPerRank(per);
// }
// // 热点人物
// if (analysis.ner) {
// debugger;
// var hospital = JSON.parse(analysis.ner).hospital;
// hotHospitalRank(hospital);
// }
            
            
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                noData();
            }
        }
    })
}

// 数据概览
function dataOverviewHelper(text, data, icon_circle, icons, color, styleClass, styles, rateFlag) {
    var aaa = '';
    if (rateFlag) {
        aaa = '<div style="position: absolute;right: -55px;bottom: -8px;">' + data.rate + '%</div>';
    }

    // 动态生成css
    let rateCss = data.rate;
    rateCss = rateCss.toFixed(0);
    addCss(styleClass + rateCss + '{' + styles + '}');

    var html = '<div class="col-sm-12 col-md-6 col-lg-3">' +
        '<div class="d-flex align-items-center">' +
        '<div class="m-r-20">' +
        // '<div data-label="20%" class="css-bar m-b-0 css-bar-primary
		// css-bar-'+data.rate+'">'+
        '<div data-label="20%" class="' + icon_circle + rateCss + '">' +
        // '<i class="mdi mdi-database text-info"></i>'+
        '<i class="' + icons + '"></i>' +
        '</div>' +
        '</div>' +
        '<div style="position: relative;">' +
        '<span>' + text + '</span>' +
        '<h3 class="m-b-0"><a>' + data.count + '</a></h3>' +
        '<div class="progress m-t-10">' +
        '<div class="progress-bar ' + color + '" role="progressbar" style="width: ' + data.rate + '%" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>' +
        '</div>' +
        aaa +
        '</div>' +
        '</div>' +
        '</div>';
    return html;
}

function dataOverview(data) {
    var aaa = '<div style="position: relative;">' +
        '<button class="growl btn btn-outline-secondary btn-sm bnone font-14" type="button" onclick="editWarning()" style="font-size: 12px;">' +
        '<i class="mdi mdi-plus-circle"></i>新增预警' +
        '</button>' +
        '</div>';
    if (data.earlyWarning.dataSwitch == 'open') {
        aaa = '<h3 class="m-b-0">' + data.earlyWarning.count + '</h3>' +
            '<div class="progress m-t-10">' +
            '<div class="progress-bar bg-cyan" role="progressbar" style="width: 100%" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>' +
            '</div>' +
            '<div style="position: absolute;right: -55px;bottom: -8px;">' + data.earlyWarning.rate + '%</div>';
    }
    var earlyWarningHtml = '<div class="col-sm-12 col-md-6 col-lg-3">' +
        '<div class="d-flex align-items-center">' +
        '<div class="m-r-20">' +
        // '<div data-label="20%" class="css-bar m-b-0 css-bar-primary css-bar-'
		// + data.earlyWarning.rate + '">' +
        '<div data-label="20%" class="css-bar m-b-0 css-bar-purple css-bar-' + data.earlyWarning.rate + '">' +
        // '<i class="mdi mdi-database text-info"></i>'+
        '<i class="mdi mdi-bell-ring-outline text-purple"></i>' +
        '</div>' +
        '</div>' +
        '<div style="position: relative;">' +
        '<span>预警信息</span>' +
        aaa +
        '</div>' +
        '</div>' +
        '</div>';
    // var html = dataOverviewHelper('全部舆情', data.all, "css-bar m-b-0
	// css-bar-primary css-bar-", "mdi mdi-database text-info", "bg-cyan",
	// false) + dataOverviewHelper('敏感舆情', data.sensitive, "css-bar m-b-0
	// css-bar-danger css-bar-", "mdi mdi-emoticon-sad text-danger", "bg-green",
	// true)
    // + dataOverviewHelper('非敏感舆情', data.noSensitive, "css-bar m-b-0
	// css-bar-success css-bar-", "mdi mdi-emoticon-happy text-cyan",
	// "bg-yellow", true) + earlyWarningHtml;
    // $('#dataOverview').html(html);

    // let allRate = data.all.rate;
    // allRate = allRate.toFixed(0);

    let noSensitiveRate = data.noSensitive.rate;
    noSensitiveRate = noSensitiveRate.toFixed(0);

    let sensitiveRate = data.sensitive.rate;
    sensitiveRate = sensitiveRate.toFixed(0);

    let positive1 = 90;
    let positive2 = 90;

    let negative1 = 90;
    let negative2 = 90;

    if (sensitiveRate == 50) {  // 敏感
        negative1 = 270;
        negative2 = 270;
    } else if (sensitiveRate < 50) {  // 小于50
        negative2 = negative2 + sensitiveRate * 3.6;
    } else if (sensitiveRate > 50) {  // 大于50
        let minus = sensitiveRate - 50;
        negative1 = minus * 3.6;
        negative2 = 270;
    }

    negative1 = negative1.toFixed(0);
    negative2 = negative2.toFixed(0);


    if (noSensitiveRate == 50) {  // 敏感
        positive1 = 270;
        positive2 = 270;
    } else if (noSensitiveRate < 50) {  // 小于50
        negative2 = positive2 + noSensitiveRate * 3.6;
    } else if (noSensitiveRate > 50) {  // 大于50
        let minus = noSensitiveRate - 50;
        positive1 = 270 + minus * 3.6;
        positive2 = 270;
    }

    positive1 = positive1.toFixed(0);
    positive2 = positive2.toFixed(0);


    let allStyle = 'background-image: linear-gradient(450deg, #2c63ff 50%, transparent 50%, transparent), linear-gradient(270deg, #2c63ff 50%, #fafafa 50%, #fafafa);';
    let noSensitiveStyle = 'background-image: linear-gradient(' + positive1 + 'deg, #26c6da 50%, transparent 50%, transparent), linear-gradient(' + positive2 + 'deg, #26c6da 50%, #fafafa 50%, #fafafa);';
    let sensitiveStyle = 'background-image: linear-gradient(' + negative1 + 'deg, #fafafa 50%, transparent 50%, transparent), linear-gradient(' + negative2 + 'deg, #fc4b6c 50%, #fafafa 50%, #fafafa)';
    let allStyleClass = '.css-bar-primary.css-bar-';
    let noSensitiveStyleClass = '.css-bar-success.css-bar-';
    let sensitiveStyleClass = '.css-bar-danger.css-bar-';

    var html = dataOverviewHelper('全部信息', data.all, "css-bar m-b-0 css-bar-primary css-bar-", "mdi mdi-database text-info", "bg-cyan", allStyleClass, allStyle, false) + dataOverviewHelper('消极信息', data.sensitive, "css-bar m-b-0 css-bar-danger css-bar-", "mdi mdi-emoticon-sad text-danger", "bg-green", sensitiveStyleClass, sensitiveStyle, true)
        + dataOverviewHelper('积极&中性信息', data.noSensitive, "css-bar m-b-0 css-bar-success css-bar-", "mdi mdi-emoticon-happy text-cyan", "bg-yellow", noSensitiveStyleClass, noSensitiveStyle, true) + earlyWarningHtml;
    $('#dataOverview').html(html);
}

function editWarning() {
    window.location.href = '/system/warningedit?groupid=' + anal_groupId + '&project_id=' + anal_projectId;
}

// 情感占比
function emotionalRate(data) {
    var negative = data.rate.negative;
    var neutral = data.rate.neutral;
    var positive = data.rate.positive;
    var html = "";
    html = '<div class="col-4 birder-right text-center">' +
        ' <h4 class="m-b-0 ">' + positive + '' +
        '  </h4>积极' +
        ' </div>' +
        ' <!-- column -->' +
        '<div class="col-4 birder-right text-center">' +
        '   <h4 class="m-b-0">' + neutral + '' +
        '  </h4>中性' +
        '</div>' +
        ' <!-- column -->' +
        ' <div class="col-4 text-center">' +
        ' <h4 class="m-b-0">' + negative + '' +
        ' </h4>消极' +
        '  </div>'
    $("#emotional_proportion").html(html);
    // 图
    var char = data.chart;
    for (var i = 0; i < char.length; i++) {
        var s = char[i];
        if (s[0] == "正面") {
            pos = s[1];
        } else if (s[0] == "负面") {
            neg = s[1];
        } else {
            neu = s[1];
        }
    }
    var chart = c3.generate({
        bindto: '#emotionRateChart',
        data: {
            columns: [
                ['积极', pos],
                ['中性', neu],
                ['消极', neg],
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
        },
        color: {
            pattern: ['#F0883B', '#65C2F9', '#2E66F6']
        }
    });
}

// 方案命中主体词
function planWordHit(data) {
    var html = "";
    if (data.length > 5) {
        for (var i = 0; i < 5; i++) {
            html += '<li class="' + (i == 0 ? "" : "m-t-25") + '">' +
                '<div class="d-flex align-items-center ">' +
                ' <div>' +
                ' <h6 class="m-b-0" ><span class="skipkeyword" data-keyword="'+data[i].keyword+'">' + data[i].keyword + '</span></h6>' +
                ' </div>' +
                ' <div class="ml-auto">' +
                ' <h6 class="m-b-0 font-bold">' + data[i].count + '</h6>' +
                '</div>' +
                '</div>' +
                ' <div class="progress m-t-10">' +
                ' <div class="progress-bar bg-info' + i + '" role="progressbar" style="width: ' + data[i].rate + '" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>' +
                '</div>' +
                '  </li>'
        }
        $("#planwordhit").html(html);
    } else {
        for (var i = 0; i < data.length; i++) {
            html += '<li class="' + (i == 0 ? "" : "m-t-25") + '">' +
                '<div class="d-flex align-items-center">' +
                ' <div>' +
                ' <h6 class="m-b-0 "><span class="skipkeyword" data-keyword="'+data[i].keyword+'">' + data[i].keyword + '</span></h6>' +
                ' </div>' +
                ' <div class="ml-auto">' +
                ' <h6 class="m-b-0 font-bold">' + data[i].count + '</h6>' +
                '</div>' +
                '</div>' +
                ' <div class="progress m-t-10">' +
                ' <div class="progress-bar bg-info' + i + '" role="progressbar" style="width: ' + data[i].rate + '" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>' +
                '</div>' +
                '  </li>'
        }
        $("#planwordhit").html(html);
    }
    
    
    
    
    
    if (data.length == 0) {
        nodata('#planwordhit', '暂无数据！')
    }
}

// 方案命中分类统计
function category_rankdata(data) {
	loading('#category_rank');
    var html = "";
    if (data.length > 5) {
        for (var i = 0; i < 10&&(i < data.length); i++) {
            html += '<li class="' + (i == 0 ? "" : "m-t-25") + '">' +
                '<div class="d-flex align-items-center ">' +
                ' <div>' +
                ' <h6 class="m-b-0" ><span class="skipkeyword" data-keyword="'+data[i].keyword+'">' + data[i].keyword + '</span></h6>' +
                ' </div>' +
                ' <div class="ml-auto">' +
                ' <h6 class="m-b-0 font-bold">' + data[i].count + '</h6>' +
                '</div>' +
                '</div>' +
                ' <div class="progress m-t-10">' +
                ' <div class="progress-bar bg-info' + i + '" role="progressbar" style="width: ' + data[i].rate + '%" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>' +
                '</div>' +
                '  </li>'
        }
        $("#category_rank").html(html);
    } else {
        for (var i = 0; i < data.length; i++) {
        	if(data[i].hasOwnProperty("keyword")){
            html += '<li class="' + (i == 0 ? "" : "m-t-25") + '">' +
                '<div class="d-flex align-items-center">' +
                ' <div>' +
                ' <h6 class="m-b-0 "><span class="skipkeyword" data-keyword="'+data[i].keyword+'">' + data[i].keyword + '</span></h6>' +
                ' </div>' +
                ' <div class="ml-auto">' +
                ' <h6 class="m-b-0 font-bold">' + data[i].count + '</h6>' +
                '</div>' +
                '</div>' +
                ' <div class="progress m-t-10">' +
                ' <div class="progress-bar bg-info' + i + '" role="progressbar" style="width: ' + data[i].rate + '%" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>' +
                '</div>' +
                '  </li>'
        }
        }
        $("#category_rank").html(html);
    }
    if (data.length == 0) {
        nodata('#category_rank', '暂无数据！')
    }
}








// 跳转到数据监测列表页面
$(document).on('click','.skipkeyword', function () {
    let keyword = $(this).attr("data-keyword");
    window.location.href = ctxPath + "monitor?projectid="+anal_projectId+"&groupid="+anal_groupId+"&search="+keyword;
});


// 监测分析刷新按钮
$(document).on('click','#updateanalysis', function () {
	var currenttime = new Date(new Date().Format("yyyy-MM-dd hh:mm:ss").replace(/-/g, "/"));
	var lasttime = new Date(reporttime.replace(/-/g, "/"));
	let minutesdiff = parseInt(currenttime - lasttime)/1000/60;// 两个时间相差的分钟数
	
	if(minutesdiff<15){
		showInfo("更新太频繁，稍后15分钟再试");
	}else{
		$.ajax({
	        url: "/analysis/updateanalysisdata",
	        type: "get",
	        data: {
	            projectid: anal_projectId,
	            groupid: anal_groupId
	        },
	        success: function (data) {
	           let status = JSON.parse(data).status;
	           if(status==200){
	        	   showInfo("定时任务已生成，请稍后...");
	           }
	        },
	        error: function (xhr, ajaxOptions, thrownError) {
	            if (xhr.status == 403) {
	                window.location.href = ctxPath + "login";
	            } else {
	                $("#popularinformation").css({"position": "relative", "min-height": "300px"})
	                nodata("#popularinformation", '大概5-10分钟之后更新完成...')
	            }
	        }
	    })
	}
	
	
	
    
});



// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18

Date.prototype.Format = function (fmt) { // author: meizz
var o = {
 "M+": this.getMonth() + 1, // 月份
 "d+": this.getDate(), // 日
 "h+": this.getHours(), // 小时
 "m+": this.getMinutes(), // 分
 "s+": this.getSeconds(), // 秒
 "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
 "S": this.getMilliseconds() // 毫秒
};
if (/(y+)/.test(fmt))
 fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
for (var k in o)
 if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
   return fmt;
}

/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 提示信息框
 * @param message
 *            提示的信息
 */
function showInfo(message) {
    $.blockUI({
        message: message,
        fadeIn: 700,
        fadeOut: 700,
        timeout: 3000,
        showOverlay: false,
        centerY: false,
        css: {
            width: '250px',
            top: '20px',
            left: '',
            right: '20px',
            border: 'none',
            padding: '15px 5px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: 0.9,
            color: '#fff'
        }
    });
}





// 关键词情感分析数据走势
function keywordsLine(data) {
    var context = '<i class="fa fa-info-circle m-r-10"></i>' + data.text;
    $("#keywordsMood").html(context);
    $("#category-line").html("")
// anychart.onDocumentReady(function () {
    var dataSet = anychart.data.set(data.chart);
    var seriesData_1 = dataSet.mapAs({'x': 0, 'value': 1});
    var seriesData_2 = dataSet.mapAs({'x': 0, 'value': 2});
    var chart = anychart.line();
    chart.yAxis().labels().format('{%Value}');
    chart.animation(true);
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
    chart.title('');
    var series_1 = chart.spline(seriesData_1);
    series_1.name('非敏感');
    series_1.markers().zIndex(100);
    series_1.hovered().markers()
        .enabled(true)
        .type('circle')
        .size(4);
    var series_2 = chart.spline(seriesData_2);
    series_2.name('敏感');
    series_2.markers().zIndex(100);
    series_2.hovered().markers()
        .enabled(true)
        .type('circle')
        .size(4);
    chart.legend()
        .enabled(true)
        .fontSize(13)
        .padding([0, 0, 20, 0]);
    chart.container('category-line');
    chart.draw();
    clearlogo();
// });
}

// 热点事件排名 tab切换事件
$("#search-tab li").on("click", function () {
    var type = $(this).attr("data-type");
    $(this).siblings().removeClass("create-tab-act");
    $(this).addClass("create-tab-act");
    if (type == '0') toHtmlHotEventRanking(0, hotEventRanking.all);
    if (type == '1') toHtmlHotEventRanking(1, hotEventRanking.positive);
    if (type == '3') toHtmlHotEventRanking(3, hotEventRanking.negative);
})

// 热点事件排名 html生成
function toHtmlHotEventRanking(type, data) {
    $('#hotEventRanking').html('');
    $('#hotEventRankingTable').css('min-height', 'none');
    for (var i = 0; i < data.length; i++) {
        var classArray = ['v-hot1', 'v-hot2', 'v-hot3'];
        var classHtml = '';
        if (i < 3) classHtml = classArray[i];
        var keywordArray = [];
        if (data[i].key_words) {
            var keywords = JSON.parse(data[i].key_words);
            for (key in keywords) {
                keywordArray.push(key);
            }
        }
        var emotionHtml = '';
        if (type == 0) {
            var emotion = data[i].emotion;
            if (emotion == 1) emotionHtml = '<span style="margin-right: 15px;" class="link moodzm">正面</span>';
            if (emotion == 2) emotionHtml = '<span style="margin-right: 15px;" class="link moodzx">中性</span>';
            if (emotion == 3) emotionHtml = '<span style="margin-right: 15px;" class="link moodfm">负面</span>';
        }
        var rate = data[i].rate;
        var _score = data[i]._score;
        if (!rate) rate = '00.00%';

        //标题长度控制
        let title = data[i].title;

        if (title.length >50){
            title = title.substring(0 , 50) + "......";
        }


        var html = '<tr>' +
            '<td>' +
            '<div style="display:flex;">' +
            '<a target="_blank" title="'+data[i].title+'" style="font-weight: 500;"href="' + ctxPath + 'monitor/detail/' + data[i].article_public_id + '?menu=report&groupid=' + anal_groupId + '&projectid=' + anal_projectId + '">' +
            '<span class="v-hot ' + classHtml + '">' + (i + 1) + '</span>' +
            title +
            '</a>' +
            '<div style="margin-left: auto;">' +
            '<span style="white-space: nowrap;margin-right: 15px;"><i class="mdi mdi-clock"></i> 时间：' + timeParse(data[i].publish_time) + '</span>' +
            '<span style="white-space: nowrap;margin-right: 15px;">来源：' + data[i].source_name + '</span>' +
            emotionHtml +
            '<span style="font-size: smaller;"><i class=" fa fas fa-fire"></i>&nbsp;' + _score + '</span>' +
            '</div>' +
            '</div>' +
            '<div class="text-over-2">' +
            data[i].content +
            '</div>' +
            '<div style="display: flex;">' +
            '</div>' +
            '</td>' +
            '</tr>';
        $('#hotEventRanking').append(html);

    }
    if (data.length == 0) {
        $('#hotEventRankingTable').css('min-height', '150px');
        nodata('#hotEventRanking', '暂无数据');
    }
}

// 关键词高频分布统计
function keywordsCloud(highwords) {
    $('#keywords').html('')
// anychart.onDocumentReady(function () {
    var a = highwords;
    var chart = anychart.tagCloud(a);
    chart.addEventListener('pointClick', onPointClick);
    chart.container("keywords");
    chart.draw();
    clearlogo();
// });
}
function onPointClick(e) {
	// 读取point name
	var name=e.data.name;
	// 读取point value
	var value=e.data.value;
// // 读取自定义属性point attribute
// var attribute = e.data.Attributes['test'];
	// 弹出提示框
	alert("point_name="+name+"  point_value="+value);
	}



// 政府及事业单位统计排名
function institutionalunits(data){
	let htmlstr = '';
	let units = {"school":"学校","hospital":"医院","bank":"银行","nto":"政府"};
	let activeflag = 0;
	for(var p in data){// 遍历json对象的每个key/value对,p为key
		  // alert(p + " " + data[p]);
		  if(p=='school'||p=='hospital'||p=='bank'||p=='nto'){
			  if(data[p].length>0){
				  if(activeflag==0){
					  activeflag = 1;
					  htmlstr+='<li class="create-tab-act" data-type="'+p+'">'+units[p]+'</li>';
				  }else{
					  htmlstr+='<li data-type="'+p+'">'+units[p]+'</li>';
				  }
				  
			  }
		  }
	}
	if(htmlstr==''){
		$("#govrow").hide();
	}else{
		$("#govrow").show();
		$('#institutionalunits-tab').html(htmlstr);
		
		
		let unitsdata = '';
		$('#institutionalunits-tab li').each(function () {
	        if ($(this).hasClass('create-tab-act')) {
	        	unitsdata = $(this).data('type');
	        }
	    });
		sendArticle(anal_projectId, anal_groupId,data,unitsdata,0);
		
	    if (data.length == 0) {
	        $('#hotEventRankingTable').css('min-height', '150px');
	        nodata('#hotEventRanking', '暂时没有计算结果，正在努力计算ing...');
	    }
	}
}


//企业及上市公司
function orgunits(data){
	let htmlstr = '';
	let units = {"org":"企业","ipo_china":"主板上市","ipo_foreign":"海外上市"};
	let activeflag = 0;
	for(var p in data){// 遍历json对象的每个key/value对,p为key
		  // alert(p + " " + data[p]);
		  if(p=='org'||p=='ipo_china'||p=='ipo_foreign'){
			  if(data[p].length>0){
				  if(activeflag==0){
					  activeflag = 1;
					  htmlstr+='<li class="create-tab-act" data-type="'+p+'">'+units[p]+'</li>';
				  }else{
					  htmlstr+='<li data-type="'+p+'">'+units[p]+'</li>';
				  }
				  
			  }
		  }
	}
	if(htmlstr==''){
		$("#orgrow").hide();
	}else{
		$("#orgrow").show();
		$('#org-tab').html(htmlstr);
		
		let unitsdata = '';
		$('#org-tab li').each(function () {
	        if ($(this).hasClass('create-tab-act')) {
	        	unitsdata = $(this).data('type');
	        }
	    });
		
		sendArticle(anal_projectId, anal_groupId,data,unitsdata,1);
	    if (data.length == 0) {
	        $('#orgTable').css('min-height', '150px');
	        nodata('#orgRanking', '暂时没有计算结果，正在努力计算ing...');
	    }
	}
}

function sendArticle(anal_projectId, anal_groupId, data,unitsdata,type) {
	
	if(type==0){
		$('#InstitutionalunitsRanking').html('');
	}else{
		$('#orgRanking').html('');
	}
	
	
    //$('#InstitutionalunitsTable').css('min-height', 'none');
    
    var classHtml = '';
    let Institutionalunitsstr = '';
    let nerdata = data[unitsdata];
    for (let i = 0; i < nerdata.length; i++) {
    	var classArray = ['v-hot1', 'v-hot2', 'v-hot3'];
    	if (i < 3) classHtml = classArray[i];
    	let data = nerdata[i].data;
    	let Institutionalunitsstr = ''
    		let emotionalIndex = data.emotionalIndex;
    		let strEmotion = '';
    		let _score = data._score;
            if (emotionalIndex == 1) {
                strEmotion = '<span style="margin-right: 15px;" class="link moodzm">正面</span>';
            } else if (emotionalIndex == 2) {
                strEmotion = '<span style="margin-right: 15px;" class="link moodzx">中性</span>';
            } else if (emotionalIndex == 3) {
                strEmotion = '<span style="margin-right: 15px;" class="link moodfm">负面</span>';
            }
    	Institutionalunitsstr += '<tr>'+
		'<td><div style="display: flex;">'+
		'<div class="wb-content-imgbox">'+
			'<div class="wb-left-imgbox" style="height: 60px; line-height:87px; background: white;width: 20%; max-width:18%;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">'+
				'<a target="_blank" style="font-weight: 500;" title="'+nerdata[i].keyword+'" href="#"><span class="v-hot '+classHtml+'">'+(i+1)+'</span>'+nerdata[i].keyword+'</a>'+
			'</div>'+
			'<div class="monitor-content-con font-13">'+
				'<div style="display: flex;margin-left: auto;">'+
					'<a target="_blank" style="font-weight: 500;" href="/monitor/detail/'+data.article_public_id+'?menu=report&amp;groupid='+anal_groupId+'&amp;projectid='+anal_projectId+'">'+
                      '<img class="rounded-circle content-logo" width="45" src="'+data.websitelogo+'" alt="">'+data.title+'</a>'+
					'<div style="margin-left: auto;">';
					if(data.hasOwnProperty('industrylable')){
						Institutionalunitsstr+='<span style="white-space: nowrap; margin-right: 15px;">行业：'+data.industrylable+'</span>';
					}else{
						Institutionalunitsstr+='<span style="white-space: nowrap; margin-right: 15px;"></span>';
					}
					if(data.hasOwnProperty('eventlable')){
						Institutionalunitsstr+='<span style="white-space: nowrap; margin-right: 15px;">事件：'+data.eventlable+'</span>';
					}else{
						Institutionalunitsstr+='<span style="white-space: nowrap; margin-right: 15px;"></span>';
					}
					Institutionalunitsstr+='<span style="white-space: nowrap; margin-right: 15px;"><i class="mdi mdi-clock"></i> 时间：'+timeParse(data.publish_time)+'</span>'+
						'<span style="white-space: nowrap; margin-right: 15px;">来源：'+data.sourcewebsitename+'</span>'+strEmotion+
						'<span style="font-size: smaller;"><i class=" fa fas fa-fire"></i>&nbsp;'+_score+'</span>'+
					'</div>'+
				'</div>'+
				'<div style="margin-left: auto;">'+
					'<span style="width: inherit;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:2;">'+data.content+'</span>'+
				'</div>'+
			'</div>'+
		'</div>'+
	'</div></td>'+
'</tr>';
    	
    	if(type==0){
    		$("#InstitutionalunitsRanking").append(Institutionalunitsstr); 
    	}else{
    		$("#orgRanking").append(Institutionalunitsstr); 
    	}
    	
    	
    	
    	
    	
    	
    }
    
}

function aa(i,keyword,type){
	let classHtml = '';
	var classArray = ['v-hot1', 'v-hot2', 'v-hot3'];
	if (i < 3) classHtml = classArray[i];
	let resultdata ;
	let articleData = new Object();
	articleData.projectid = anal_projectId;
    articleData.groupid = anal_groupId;
    articleData.searchkeyword = keyword;
    articleData.page = 1;
    articleData.size = 1;
    articleData.searchType = 3;
    articleData.similar = 0;
    articleData.matchingmode = 1;
    articleData.timeType = 5;
    articleData.emotionalIndex = [];
    articleData.eventIndex = [];
    articleData.industryIndex = [];
    articleData.province = [];
    articleData.city =[];
    articleData.organizationtype = [];
    articleData.categorylabledata = [];
    articleData.enterprisetypelist = [];
    articleData.hightechtypelist = [];
    articleData.policylableflag = [];
    console.info(articleData);
    $.ajax({
        type: 'POST',
        url: ctxPath+'monitor/getanalysisarticle',
        dataType: 'json',
       // async: false,
        data: JSON.stringify(articleData),
        contentType: 'application/json;charset=utf-8',
        success: function (res) {
        	let data = res.data.data;
        	let Institutionalunitsstr = ''
        	for (var m = 0; m < data.length&&m<1; m++) {
        		let emotionalIndex = data[m].emotionalIndex;
        		let strEmotion = '';
        		let _score = data[m]._score;
                if (emotionalIndex == 1) {
                    strEmotion = '<span style="margin-right: 15px;" class="link moodzm">正面</span>';
                } else if (emotionalIndex == 2) {
                    strEmotion = '<span style="margin-right: 15px;" class="link moodzx">中性</span>';
                } else if (emotionalIndex == 3) {
                    strEmotion = '<span style="margin-right: 15px;" class="link moodfm">负面</span>';
                }
        	Institutionalunitsstr += '<tr>'+
    		'<td><div style="display: flex;">'+
    		'<div class="wb-content-imgbox">'+
    			'<div class="wb-left-imgbox" style="height: 60px; line-height: 87px; background: white;width: 20%">'+
    				'<a target="_blank" style="font-weight: 500;" href="#"><span class="v-hot '+classHtml+'">'+(i+1)+'</span>'+articleData.searchkeyword+'</a>'+
    			'</div>'+
    			'<div class="monitor-content-con font-13">'+
    				'<div style="display: flex;margin-left: auto;">'+
    					'<a target="_blank" style="font-weight: 500;" href="/monitor/detail/f12d8c7b53d39aef1adbef314d11eb01?menu=report&amp;groupid=1382223209100873728&amp;projectid=1382237572998238208">'+
                          '<img class="rounded-circle content-logo" width="45" src="'+data[m].websitelogo+'" alt="">'+data[m].title+'</a>'+
    					'<div style="margin-left: auto;">'+
    					        '<span style="white-space: nowrap; margin-right: 15px;">行业：'+data[m].industrylable+'</span>'+
    							'<span style="white-space: nowrap; margin-right: 15px;">事件：'+data[m].eventlable+'</span>'+
    						'<span style="white-space: nowrap; margin-right: 15px;"><i class="mdi mdi-clock"></i> 时间：'+timeParse(data[m].publish_time)+'</span>'+
    						'<span style="white-space: nowrap; margin-right: 15px;">来源：'+data[m].sourcewebsitename+'</span>'+strEmotion+
    						'<span style="font-size: smaller;"><i class=" fa fas fa-fire"></i>&nbsp;'+_score+'</span>'+
    					'</div>'+
    				'</div>'+
    				'<div style="margin-left: auto;">'+
    					'<span>'+data[m].content+'</span>'+
    				'</div>'+
    			'</div>'+
    		'</div>'+
    	'</div></td>'+
    '</tr>';
        	}
        	
        	if(type==0){
        		$("#InstitutionalunitsRanking").append(Institutionalunitsstr); 
        	}else{
        		$("#orgRanking").append(Institutionalunitsstr); 
        	}
        	
        	 
        }
        
    });
	
	
	
	
}



//政府及事业单位统计排名-切换事件
$("#institutionalunits-tab").delegate("li","click",function(){
	  $(this).siblings().removeClass("create-tab-act");
	  $(this).addClass("create-tab-act");
		  let unitsdata = '';
		$('#institutionalunits-tab li').each(function () {
	      if ($(this).hasClass('create-tab-act')) {
	      	unitsdata = $(this).data('type');
	      }
	  });
	  
	  sendArticle(anal_projectId, anal_groupId,nerresultdata,unitsdata,0);
});


//企业及上市公司切换事件
$("#org-tab").delegate("li","click",function(){
	  $(this).siblings().removeClass("create-tab-act");
	  $(this).addClass("create-tab-act");
		  let unitsdata = '';
		$('#org-tab li').each(function () {
	      if ($(this).hasClass('create-tab-act')) {
	      	unitsdata = $(this).data('type');
	      }
	  });
	  
	  sendArticle(anal_projectId, anal_groupId,nerresultdata,unitsdata,1);
});






function industryandevent(){
		let labletype = '';
		$('#industryandevent-tab li').each(function () {
	      if ($(this).hasClass('create-tab-act')) {
	    	  labletype = $(this).data('type');
	      }
	  });
	 //拼接标签数据
	  sendArticleLable(labletype);
	/*//标签类型
	  let lable = '';
		$('#labletab span').each(function () {
	      if ($(this).hasClass('badge-info')) {
	    	  lable = $(this).data('type');
	      }
	  });
	//拼接列表数据
	sendIndustryAndEventArticleData(labletype,lable);*/
	
}





//热门行业&事件统计切换事件
$("#industryandevent-tab").delegate("li","click",function(){
	  $(this).siblings().removeClass("create-tab-act");
	  $(this).addClass("create-tab-act");
		let labletype = '';
		$('#industryandevent-tab li').each(function () {
	      if ($(this).hasClass('create-tab-act')) {
	    	  labletype = $(this).data('type');
	      }
	  });
	 //拼接标签数据
	  sendArticleLable(labletype);
	/*//标签类型
	  let lable = '';
		$('#labletab span').each(function () {
	      if ($(this).hasClass('badge-info')) {
	    	  lable = $(this).data('type');
	      }
	  });
	//拼接列表数据
	sendIndustryAndEventArticleData(labletype,lable);*/
});

//热门行业&事件统计切换事件
$("#labletab").delegate("span","click",function(){
	  $(this).siblings().removeClass("badge-info");
	  $(this).addClass("badge-info");
		let labletype = '';
		$('#industryandevent-tab li').each(function () {
	      if ($(this).hasClass('create-tab-act')) {
	    	  labletype = $(this).data('type');
	      }
	  });
	//标签类型
	  let lable = '';
		$('#labletab span').each(function () {
	      if ($(this).hasClass('badge-info')) {
	    	  lable = $(this).data('type');
	      }
	  });
	//拼接列表数据
	sendIndustryAndEventArticleData(labletype,lable);
});






function sendIndustryAndEventArticleData(labletype,lable){
	let articleData = new Object();
	let articleParam = new Object();
	articleData.projectid = anal_projectId;
    articleData.groupid = anal_groupId;
    articleData.searchkeyword = '';
    articleData.page = 1;
    articleData.size = 5;
    articleData.searchType = 3;
    articleData.similar = 1;
    articleData.matchingmode = 1;
    //articleData.timeType = 5;
    
    
    var timePeriod = $('#timePeriodSelect').val();
    if(timePeriod=='1'){
    	articleData.timeType = 1;
    }else if(timePeriod=='2'){
    	articleData.timeType = 4;
    }else if(timePeriod=='3'){
    	articleData.timeType = 5;
    }else if(timePeriod=='4'){
    	articleData.timeType = 6;
    }
    articleData.emotionalIndex = [];
    if(labletype==0){
    	articleData.eventIndex = [];
        articleData.industryIndex = [];
        articleData.industryIndex.push(lable);
    }else{
    	articleData.eventIndex = [];
        articleData.industryIndex = [];
        articleData.eventIndex.push(lable);
    }
    
    articleData.province = [];
    articleData.city =[];
    articleData.organizationtype = [];
    articleData.categorylabledata = [];
    articleData.enterprisetypelist = [];
    articleData.hightechtypelist = [];
    articleData.policylableflag = [];
    articleParam.type = "POST";
    articleParam.url = ctxPath + "monitor/getanalysisarticle";
    articleParam.contentType = 'application/json;charset=utf-8';
    console.info(articleData);
    $.ajax({
        type: articleParam.type,
        url: articleParam.url,
        dataType: 'json',
//        async: false,
        data: JSON.stringify(articleData),
        contentType: articleParam.contentType,
        beforeSend: function () {
            loading("#IndustryANDEvent")
        },
        success: function (res) {
            //判断是事件还是行业
            res.labletype = labletype;

        	funcLableData(res);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                $("#page").html("");
                dataerror("#monitor-content");
            }
        }
    });
	
	
	
}

function funcLableData(res){
    //判断是事件还是行业
    let labletype = res.labletype;


	let data = res.data.data;
	//如果是行业
    if (labletype == 0){

        $('#IndustryANDEvent').html('');
        $('#IndustryANDEventTable').css('min-height', 'none');
        for (var i = 0; i < data.length&i<5; i++) {
            var classArray = ['v-hot1', 'v-hot2', 'v-hot3'];
            var classHtml = '';
            if (i < 3) classHtml = classArray[i];
            let emotionalIndex = data[i].emotionalIndex;
            var emotionHtml = '';
            if (emotionalIndex == 1) emotionHtml = '<span style="margin-right: 15px;" class="link moodzm">正面</span>';
            if (emotionalIndex == 2) emotionHtml = '<span style="margin-right: 15px;" class="link moodzx">中性</span>';
            if (emotionalIndex == 3) emotionHtml = '<span style="margin-right: 15px;" class="link moodfm">负面</span>';
            var rate = data[i].rate;
            var _score = data[i]._score;
            if (!rate) rate = '00.00%';
            let title = data[i].title;
            if (title.length > 55){
                title = title.substring(0,54) + "......";
            }

            var html = '<tr>' +
                '<td>' +
                '<div style="display:flex;">' +
                '<a target="_blank"  title="'+data[i].title+'"  style="font-weight: 500;"href="' + ctxPath + 'monitor/detail/' + data[i].article_public_id + '?menu=report&groupid=' + anal_groupId + '&projectid=' + anal_projectId + '">' +
                '<span class="v-hot ' + classHtml + '">' + (i + 1) + '</span>' +
                title +
                '</a>' +
                '<div style="margin-left: auto;">' +
                /*'<span style="white-space: nowrap; margin-right: 15px;">行业：' + data[i].industrylable + '</span>' +*/
                '<span style="white-space: nowrap; margin-right: 15px;">事件：' + data[i].eventlable + '</span>' +
                '<span style="white-space: nowrap;margin-right: 15px;"><i class="mdi mdi-clock"></i> 时间：' + timeParse(data[i].publish_time) + '</span>' +
                '<span style="white-space: nowrap;margin-right: 15px;">来源：' + data[i].source_name + '</span>' +
                emotionHtml +
                '<span style="font-size: smaller;"><i class=" fa fas fa-fire"></i>&nbsp;' + _score + '</span>' +
                '</div>' +
                '</div>' +
                '<div class="text-over-2" style="-webkit-line-clamp:2">' +
                data[i].content +
                '</div>' +
                '<div style="display: flex;">' +
                '</div>' +
                '</td>' +
                '</tr>';
            $('#IndustryANDEvent').append(html);

        }
    }else{
        $('#IndustryANDEvent').html('');
        $('#IndustryANDEventTable').css('min-height', 'none');
        for (var i = 0; i < data.length&i<5; i++) {
            var classArray = ['v-hot1', 'v-hot2', 'v-hot3'];
            var classHtml = '';
            if (i < 3) classHtml = classArray[i];
            let emotionalIndex = data[i].emotionalIndex;
            var emotionHtml = '';
            if (emotionalIndex == 1) emotionHtml = '<span style="margin-right: 15px;" class="link moodzm">正面</span>';
            if (emotionalIndex == 2) emotionHtml = '<span style="margin-right: 15px;" class="link moodzx">中性</span>';
            if (emotionalIndex == 3) emotionHtml = '<span style="margin-right: 15px;" class="link moodfm">负面</span>';
            var rate = data[i].rate;
            var _score = data[i]._score;
            if (!rate) rate = '00.00%';

            let title = data[i].title;
            if (title.length > 55){
                title = title.substring(0,54) + "......";
            }

            var html = '<tr>' +
                '<td>' +
                '<div style="display:flex;">' +
                '<a target="_blank"  title="'+data[i].title+'"  style="font-weight: 500;"href="' + ctxPath + 'monitor/detail/' + data[i].article_public_id + '?menu=report&groupid=' + anal_groupId + '&projectid=' + anal_projectId + '">' +
                '<span class="v-hot ' + classHtml + '">' + (i + 1) + '</span>' +
                title +
                '</a>' +
                '<div style="margin-left: auto;">' +
                '<span style="white-space: nowrap; margin-right: 15px;">行业：' + data[i].industrylable + '</span>' +
                /*'<span style="white-space: nowrap; margin-right: 15px;">事件：' + data[i].eventlable + '</span>' +*/
                '<span style="white-space: nowrap;margin-right: 15px;"><i class="mdi mdi-clock"></i> 时间：' + timeParse(data[i].publish_time) + '</span>' +
                '<span style="white-space: nowrap;margin-right: 15px;">来源：' + data[i].source_name + '</span>' +
                emotionHtml +
                '<span style="font-size: smaller;"><i class=" fa fas fa-fire"></i>&nbsp;' + _score + '</span>' +
                '</div>' +
                '</div>' +
                '<div class="text-over-2" style="-webkit-line-clamp:2">' +
                data[i].content +
                '</div>' +
                '<div style="display: flex;">' +
                '</div>' +
                '</td>' +
                '</tr>';
            $('#IndustryANDEvent').append(html);

        }
    }
    if (data.length == 0) {
        $('#IndustryANDEventTable').css('min-height', '150px');
        nodata('#IndustryANDEvent', '暂无数据！.');
    }
}





function sendArticleLable(labletype) {
	let articleData = new Object();
	let articleParam = new Object();
	articleData.projectid = anal_projectId;
    articleData.groupid = anal_groupId;
    articleData.searchkeyword = '';
    articleData.page = 1;
    articleData.searchType = 0;
    articleData.similar = 0;
    articleData.matchingmode = 1;
    articleData.timeType = 7;
    articleData.emotionalIndex = [];
    articleData.eventIndex = [];
    articleData.industryIndex = [];
    articleData.province = [];
    articleData.city =[];
    articleData.organizationtype = [];
    articleData.categorylabledata = [];
    articleData.enterprisetypelist = [];
    articleData.hightechtypelist = [];
    articleData.policylableflag = [];
    articleParam.type = "POST";
    articleParam.contentType = 'application/json;charset=utf-8';
    if(labletype==0){
    	articleParam.url = ctxPath + "monitor/getindustry";
    }else{
    	articleParam.url = ctxPath + "monitor/getevent";
    }
    articleParam.contentType = 'application/json;charset=utf-8';
    $.ajax({
        type: articleParam.type,
        url: articleParam.url,
        dataType: 'json',
        data: JSON.stringify(articleData),
        //async: false,
        contentType: articleParam.contentType,
        beforeSend: function () {
            loading("#monitor-content")
        },
        success: function (res) {
        	funcLable(res);

            //标签类型
            let lable = '';
            $('#labletab span').each(function () {
                if ($(this).hasClass('badge-info')) {
                    lable = $(this).data('type');
                }
            });
            //拼接列表数据
            sendIndustryAndEventArticleData(labletype,lable);

        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                $("#page").html("");
                dataerror("#monitor-content");
            }
        }
    });
}
function funcLable(res){
	$("#labletab").html('');
	let lablestr = "";
	let totalindustrystr = '';
	let data = res.data.data;
	let flag = 0;
	for (let i = 0;i < data.length;i++){
		if(data[i].key!='其它'&&data[i].key!='total'){
			if(flag==0){
				lablestr+= '<span style="font-size:12px;width:75px;min-width:75px" data-type="'+data[i].key+'" class="badge badge-pill badge-light badge-info">'+data[i].key+'</span>';
				flag++;
			}else{
				lablestr+= '<span style="font-size:12px;width:75px;min-width:75px" data-type="'+data[i].key+'" class="badge badge-pill badge-light">'+data[i].key+'</span>';
				flag++;
			}
	       }
		if(flag==5)break;
		
		}
	$("#labletab").html(lablestr);
}



// 高频词指数
function keywordIndex(data) {
    var html = "";
    var trend = '';
    for (var i = 0; i < data.length; i++) {
        if (data[i].trend == 1) {
            trend = "mdi mdi-arrow-up";
        } else if (data[i].trend == 2) {
            trend = "mdi mdi-dots-horizontal";
        } else {
            trend = "mdi mdi-arrow-down fm";
        }

        let keyword = data[i].keyword;
        if (keyword.length > 4){
            keyword = keyword.substring(0 , 4) + "...";
        }

        html +=
            '<div class="over-event-listgpc">' +
            '<span><a target="_blank" href="' + ctxPath + 'monitor?groupid=' + anal_groupId + '&projectid=' + anal_projectId + '&search=' + data[i].keyword + '" title="'+ data[i].keyword +'">' + keyword + '</a></span>' +
            '<span><i class="zm ' + trend + '"></i></span>' +
            '<span>' + data[i].count + '</span>' +
            '<span>' + data[i].index + '</span> ' +
            '</div>'
    }
    $("#keywordindex").html(html);
    if (data.length == 0) {
        nodata('#keywordindex', '暂无数据！')
    }
}

// 媒体活跃度分析
function toHtmlMediaActivityAnalysis(data) {
    var mediaActivityAnalysis = data;
    var mediaActivityAnalysisHtml = '<div class="display-5 text-info"><i class="mdi mdi-pulse"></i>' +
        '<span>' + mediaActivityAnalysis.total_site + '</span>' +
        '</div>' +
        '<div class="m-l-10">' +
        '<h3 class="m-b-0">个站点日均</h3><small>' + mediaActivityAnalysis.total + '条数据更新，排名如下</small>' +
        '</div>';
    $('#mediaActivityAnalysis').html(mediaActivityAnalysisHtml);
    var list = mediaActivityAnalysis.sites;
    $('#mediaActivityAnalysisList').html('');
    for (var i = 0; i < list.length; i++) {
        var sourceName = '<span>' + list[i].name + '</span>';
        var sourceNameHtml = list[i].name;
        if (list[i].name.length > 5) {
            sourceNameHtml = '<span title="' + list[i].name + '">' + list[i].name.substring(0, 5) + '...' + '</span>';
        }
        var logo = list[i].logo;
        var logoHtml = '';
        if (logo) {
            logoHtml = '<img class="rounded-circle" width="24" src="' + logo + '" alt="">';
        }else{
            logoHtml = '<img class="rounded-circle" width="45" src="/assets/images/moren.jpg" alt="">';
        }
        var html = '<tr>' +
            '<td>' +
            logoHtml +
            '</td>' +
            '<td  class="text-muted">' + sourceNameHtml + '</td>' +
            '<td>' + list[i].rate + '</td>' +
            '<td class="font-medium">' + list[i].count + '</td>' +
            '</tr>';
        $('#mediaActivityAnalysisList').append(html);
    }
    if (list.length == 0) {
        nodata('#mediaActivityAnalysisList', '暂无数据！.');
    }
}

// 热点地区排名 html 生成
function hotSpotRankingChart(data, selectId) {
    var url = "../dist/js/reportdata.json"
    /* json文件url，本地的就写本地的位置，如果是服务器的就写服务器的路径 */
    var request = new XMLHttpRequest();
    request.open("get", url);
    /* 设置请求方法与路径 */
    request.send(null);
    /* 不发送数据到服务器 */
    request.onload = function () {/* XHR对象获取到返回信息后执行 */
        if (request.status == 200) {/* 返回状态为200，即为数据获取成功 */
            var jsondata = JSON.parse(request.responseText);
            document.getElementById(selectId).removeAttribute('_echarts_instance_'); // 移除容器上的
																						// _echarts_instance_
																						// 属性
            var myChartdt = echarts.init(document.getElementById(selectId))
            echarts.registerMap('china', jsondata);
            var geoCoordMap = {
                '台湾': [121.5135, 25.0308],
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
            // var data = [
            // {"name":"北京","value":8},
            // {name:"天津",value:42},
            // ];
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
                tooltip: {
                    trigger: 'item',
                    formatter: function (params) {
                        if (typeof(params.value)[2] == "undefined") {
                            return params.name + ' : ' + params.value;
                        } else {
                            return params.name + ' : ' + params.value[2];
                        }
                    }
                },
                legend: {
                    orient: 'vertical',
                    y: 'bottom',
                    x: 'right',
                    data: ['pm2.5'],
                    textStyle: {color: '#ccc'}
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
                    inRange: {}
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
                series: [
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
                        symbolSize: [50, 50],
                        label: {
                            normal: {
                                show: true,
                                textStyle: {
                                    color: '#fff',
                                    fontSize: 9,
                                },
                                formatter(value) {
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
}

function toHtmlHotSpotRanking(data) {
    var classArray = ['bg-info', 'bg-cyan', 'bg-purple', 'bg-orange', 'bg-blue'];
    var hotSpotRanking = data;
    hotSpotRankingChart(hotSpotRanking.chart, 'hotSpotRankingChart');
    var list = hotSpotRanking.list;
    $('#hotSpotRankingList').html('');
    for (var i = 0; i < list.length; i++) {
        var html = '<div class="row m-b-15">' +
            '<div class="col-3">' + list[i].name + '</div>' +
            '<div class="col-7">' +
            '<div class="progress m-t-5">' +
            '<div class="progress-bar ' + classArray[i] + '" role="progressbar" style="width: ' + list[i].rate + '"' +
            ' aria-valuenow="48" aria-valuemin="0" aria-valuemax="100"></div>' +
            '</div>' +
            '</div>' +
            '<div class="col-2">' + list[i].rate + '</div>' +
            '</div>';
        $('#hotSpotRankingList').append(html);
    }
    if (list.length == 0) {
        nodata('#hotSpotRankingChart', '暂无数据');
    }
}

// 热点机构排名

function hotOrgRank(data) {
    $('#orglist').html('');
    for (var i = 0; i < data.length; i++) {
        var html = '<li class="m-t-25" style="margin-top: 30px;"><div class="d-flex align-items-center">'
        	+'<div style="width: 70%;"> '
        	+'<h6 class="m-b-0 "> '
        	+'<span class="skipkeyword" data-keyword="'+data[i].keyword+'">'+data[i].keyword+'</span> </h6> </div>'
        	+' <div class="ml-auto1" style="width: 15%;"> '
        	+'<h6 class="m-b-0 font-bold">('+data[i].calculatedRatioWithPercentSign+')</h6> </div> '
        	+'<div class="ml-auto"> <h6 class="m-b-0 font-bold">'+data[i].count+'</h6>'
        	+' </div> </div> <div class="progress m-t-10"> '
        	+'<div class="progress-bar bg-info'+i+'" role="progressbar" style="width: '+data[i].calculatedRatioWithPercentSign+'" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100">'
        	+'</div> </div></li>';
        $('#orglist').append(html);
    }
    if (data.length == 0) {
        nodata('#orglist', '暂无数据');
    }
}

// 热点机构排名

function highIPOForeignIndexList(data) {
    $('#highIPOForeignIndexList').html('');
    for (var i = 0; i < data.length; i++) {
        var html = '<li class="m-t-25" style="margin-top: 30px;"><div class="d-flex align-items-center">'
        	+'<div style="width: 70%;"> '
        	+'<h6 class="m-b-0 "> '
        	+'<span class="skipkeyword" data-keyword="'+data[i].keyword+'">'+data[i].keyword+'</span> </h6> </div>'
        	+' <div class="ml-auto1" style="width: 15%;"> '
        	+'<h6 class="m-b-0 font-bold">('+data[i].calculatedRatioWithPercentSign+')</h6> </div> '
        	+'<div class="ml-auto"> <h6 class="m-b-0 font-bold">'+data[i].count+'</h6>'
        	+' </div> </div> <div class="progress m-t-10"> '
        	+'<div class="progress-bar bg-info'+i+'" role="progressbar" style="width: '+data[i].calculatedRatioWithPercentSign+'" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100">'
        	+'</div> </div></li>';
        $('#highIPOForeignIndexList').append(html);
    }
    if (data.length == 0) {
        nodata('#highIPOForeignIndexList', '暂无数据');
    }
}






// //热点机构排名
//
// function hotOrgRank(data) {
// debugger;
// $('#highOrgIndexList').html('');
// for (var i = 0; i < data.length; i++) {
// var html =
// '<div class="over-event-listgpc m-t-20">'+
// '<span><a target="_blank"
// href="'+ctxPath+'monitor?groupid='+anal_groupId+'&projectid='+anal_projectId+'&search='+data[i].keyword+'">'+data[i].keyword+'</a></span><span>'+data[i].count+'</span><span>'+data[i].calculatedRatioWithPercentSign+'</span>'+
// '</div>';
// $('#highOrgIndexList').append(html);
// }
// if (data.length == 0) {
// nodata('#highOrgIndexList', '暂无数据');
// }
// }









// 热点银行排名

function hotIPOChinaRank(data) {
	 $('#highIPOChinaIndexList').html('');
	var classArray = ['v-hot1', 'v-hot2', 'v-hot3'];
	var list = data;
	for(var i=0; i<list.length; i++){
		var classHtml = '';
		if(i < 3){
			classHtml = classArray[i];
		}
		var html = '<tr>'+
			           '<td>'+
			                '<span class="v-hot '+classHtml+'">'+(i+1)+'</span>'+
			                '<a href="'+ctxPath+'monitor?groupid='+anal_groupId+'&projectid='+anal_projectId+'&search='+list[i].keyword+'">'+list[i].keyword+'</a>'+
			            '</td>'+
			            '<td class="text-center">'+list[i].count+'</td>'+
			            '<td class="font-bold text-center">'+list[i].calculatedRatioWithPercentSign+'</td>'+
			        '</tr>';
		$('#highIPOChinaIndexList').append(html);
	}
	if(data.length == 0){
		nodata('#highIPOChinaIndexList', '数据正在计算中...');
    }
}

//政策法规排名

function hotPolicyChinaRank(data) {
	
	loading('#highPolicyChinaIndexList');
	 $('#highPolicyChinaIndexList').html('');
	var classArray = ['v-hot1', 'v-hot2', 'v-hot3'];
	var list = data;
	for(var i=0; i<list.length; i++){
		var classHtml = '';
		if(i < 3){
			classHtml = classArray[i];
		}
		var html = '<tr>'+
			           '<td>'+
			                '<span class="v-hot '+classHtml+'">'+(i+1)+'</span>'+
			                '<a href="'+ctxPath+'monitor?groupid='+anal_groupId+'&projectid='+anal_projectId+'&search='+list[i].keyword+'">《'+list[i].keyword+'》</a>'+
			            '</td>'+
//			            '<td class="text-center">'+list[i].count+'</td>'+
//			            '<td class="font-bold text-center">'+list[i].calculatedRatioWithPercentSign+'</td>'+
			        '</tr>';
		$('#highPolicyChinaIndexList').append(html);
	}
	if(list.length == 0){
		nodata('#highPolicyChinaIndexList', '暂无数据');
    }
}










// 热点银行排名

function hotNtoRank(data) {
	 $('#highNtoIndexList').html('');
	var classArray = ['v-hot1', 'v-hot2', 'v-hot3'];
	var list = data;
	for(var i=0; i<list.length; i++){
		var classHtml = '';
		if(i < 3){
			classHtml = classArray[i];
		}
		var html = '<tr>'+
			           '<td>'+
			                '<span class="v-hot '+classHtml+'">'+(i+1)+'</span>'+
			                '<a href="'+ctxPath+'monitor?groupid='+anal_groupId+'&projectid='+anal_projectId+'&search='+list[i].keyword+'">'+list[i].keyword+'</a>'+
			            '</td>'+
			            '<td class="text-center">'+list[i].count+'</td>'+
			            '<td class="font-bold text-center">'+list[i].calculatedRatioWithPercentSign+'</td>'+
			        '</tr>';
		$('#highNtoIndexList').append(html);
	}
	if(data.length == 0){
		nodata('#highNtoIndexList', '数据正在计算中...');
    }
}




// 数据来源分布
function dataSourceDistribution(data) {
// $("#dataSourceDistributionChart").html('')
// anychart.onDocumentReady(function () {
// var data = [
// ['微信', 6371664],
// ['微博', 7216301],
// ];
// var chart = anychart.pie(data);
// chart.labels().position('outside');
// chart.radius('43%').innerRadius('50%');
// chart.container('dataSourceDistributionChart');
// chart.draw();
// });
    document.getElementById("dataSourceDistributionChart").removeAttribute('_echarts_instance_'); // 移除容器上的
																									// _echarts_instance_
																									// 属性
    var basicdoughnutChart = echarts.init(document.getElementById('dataSourceDistributionChart'));
    var option = {
// title: {
// text: 'Browser popularity',
// subtext: 'Open source information',
// x: 'center'
// },
        legend: {
            orient: 'vertical',
            x: 'left',
            data: ['微信', '微博', '政务', '论坛', '新闻', '报刊', '客户端', '网站', '外媒', '视频', '博客']
        },
        color: ['#3688F7', '#4E99F7', '#6BAAF8', '#8ABBF9', '#A7CEFB' , '#C8DFFC' , '#E7F2FE' , '#8DD499' , '#A4DDAE' , '#BCE5C5' , '#D4F0DB' , '#EEF9F1'],
        toolbox: {
            show: true,
            orient: 'vertical',
            feature: {
                mark: {
                    show: true,
                    title: {
                        mark: 'Markline switch',
                        markUndo: 'Undo markline',
                        markClear: 'Clear markline'
                    }
                },
                magicType: {
                    show: true,
                    title: {
                        pie: 'Switch to pies',
                        funnel: 'Switch to funnel',
                    },
                    type: ['pie', 'funnel'],
                    option: {
                        funnel: {
                            x: '25%',
                            y: '20%',
                            width: '50%',
                            height: '70%',
                            funnelAlign: 'left',
                            max: 1548
                        }
                    }
                },
// restore: {
// show: true,
// title: 'Restore'
// },
// saveAsImage: {
// show: true,
// title: 'Same as image',
// lang: ['Save']
// }
            }
        },
        calculable: true,
        series: [
            {
                name: '',
                type: 'pie',
                radius: ['50%', '70%'],
                center: ['50%', '48%'],
                itemStyle: {
                    normal: {
                        label: {
                            show: true
                        },
                        labelLine: {
                            show: true
                        }
                    },
                    emphasis: {
                        label: {
                            show: true,
                            formatter: '{b}' + '\n\n' + '{c} ({d}%)',
                            position: 'center',
                            textStyle: {
                                fontSize: '17',
                                fontWeight: '500'
                            }
                        }
                    }
                },
                data: data
            }
        ]
    };
    basicdoughnutChart.setOption(option);
}

// 数据来源分析 tab切换事件
$("body").on("click", '#search-tab2 li', function () {
    var type = $(this).attr("data-type");
    $(this).siblings().removeClass("create-tab-act");
    $(this).addClass("create-tab-act");
    switch (type) {
        case '0':
            dataSourceAnalysisHtml(dataSourceAnalysis.all);
            break;
        case '1':
            dataSourceAnalysisHtml(dataSourceAnalysis.wechat);
            break;
        case '2':
            dataSourceAnalysisHtml(dataSourceAnalysis.weibo);
            break;
        case '3':
            dataSourceAnalysisHtml(dataSourceAnalysis.gov);
            break;
        case '4':
            dataSourceAnalysisHtml(dataSourceAnalysis.bbs);
            break;
        case '5':
            dataSourceAnalysisHtml(dataSourceAnalysis.news);
            break;
        case '6':
            dataSourceAnalysisHtml(dataSourceAnalysis.newspaper);
            break;
        case '7':
            dataSourceAnalysisHtml(dataSourceAnalysis.app);
            break;
        case '8':
            dataSourceAnalysisHtml(dataSourceAnalysis.web);
            break;
        case '9':
            dataSourceAnalysisHtml(dataSourceAnalysis.foreign_media);
            break;
        case '10':
            dataSourceAnalysisHtml(dataSourceAnalysis.video);
            break;
        case '11':
            dataSourceAnalysisHtml(dataSourceAnalysis.blog);
            break;
        default:
            dataSourceAnalysisHtml(dataSourceAnalysis.all);
            break;
    }
})

// 数据来源分析
function dataSourceAnalysisHtml(data) {
    var html = '';
    for (var i = 0; i < data.length; i++) {
        var xuhaoClass = '';
        if (i == 0) {
            xuhaoClass = 'v-hot1';
        } else if (i == 1) {
            xuhaoClass = 'v-hot2';
        } else if (i == 2) {
            xuhaoClass = 'v-hot3';
        }
        let na = data[i].name;
        if (data[i].name.length > 7){

            na = na.substring(0,6) + "...";

        }

        html += '<div class="overview-media-content">' +
            '<span><span class="v-hot ' + xuhaoClass + '">' + (i + 1) + '</span></span>' +
            '<span title='+data[i].name+'>' + na + '</span>' +
            '<span>' + data[i].type + '</span>' +
            '<span style="color: #8eb9f5;">' + data[i].allCount + '</span>' +
            '<span style="color: #f54545;">' + data[i].sensitiveCount + '</span>' +
            '</div>';
    }
    $('#dataSourceAnalysis').html(html);
    if (data.length == 0) {
        nodata('#dataSourceAnalysis', '暂无数据');
    }
}

// 关键词曝光度环比排行
function keywordExposureRank(data) {
    // console.error(data);
    var keywordrank = data;

    // var poleChart =
	// echarts.init(document.getElementById('keyword_exposure_rank'));
    // // Data style
    // var dataStyle = {
    // normal: {
    // label: {show: false},
    // labelLine: {show: false}
    // }
    // };
    //
    // // Placeholder style
    // var placeHolderStyle = {
    // normal: {
    // color: 'rgba(0,0,0,0)',
    // label: {show: false},
    // labelLine: {show: false}
    // },
    // emphasis: {
    // color: 'rgba(0,0,0,0)'
    // }
    // };
    // var option = {
    // title: {
    // text: '',
    // subtext: '',
    // x: 'center',
    // y: 'center',
    // itemGap: 10,
    // textStyle: {
    // color: 'rgba(30,144,255,0.8)',
    // fontSize: 19,
    // fontWeight: '500'
    // }
    // },
    //
    // // Add tooltip
    // tooltip: {
    // show: true,
    // formatter: "{a} <br/>{b}: {c} ({d}%)"
    // },
    //
    // // Add legend
    // legend: {
    // orient: 'vertical',
    // x: document.getElementById('keyword_exposure_rank').offsetWidth / 2,
    // y: 30,
    // x: '55%',
    // itemGap: 15,
    // data: ['60% Definitely yes', '30% Could be better', '10% Not at the
	// moment', '5% Not at the moment','1% Not at the moment']
    // },
    //
    // // Add custom colors
    // color: ['#2962FF', '#4fc3f7', '#f62d51'],
    //
    // // Add series
    // series: [
    // {
    // name: '1',
    // type: 'pie',
    // clockWise: false,
    // radius: ['75%', '90%'],
    // itemStyle: dataStyle,
    // data: [
    // {
    // value: 18,
    // name: '394% 苏宁易购'
    // },
    // {
    // value: 40,
    // name: 'invisible',
    // itemStyle: placeHolderStyle
    // }
    // ]
    // },
    // {
    // name: '2',
    // type: 'pie',
    // clockWise: false,
    // radius: ['60%', '75%'],
    // itemStyle: dataStyle,
    // data: [
    // {
    // value: 30,
    // name: '30% Could be better'
    // },
    // {
    // value: 70,
    // name: 'invisible',
    // itemStyle: placeHolderStyle
    // }
    // ]
    // },
    // {
    // name: '3',
    // type: 'pie',
    // clockWise: false,
    // radius: ['45%', '60%'],
    // itemStyle: dataStyle,
    // data: [
    // {
    // value: 10,
    // name: '10% Not at the moment'
    // },
    // {
    // value: 90,
    // name: 'invisible',
    // itemStyle: placeHolderStyle
    // }
    // ]
    // },
    // {
    // name: '4',
    // type: 'pie',
    // clockWise: false,
    // radius: ['30%', '45%'],
    // itemStyle: dataStyle,
    // data: [
    // {
    // value: 5,
    // name: '5% Not at the moment'
    // },
    // {
    // value: 95,
    // name: 'invisible',
    // itemStyle: placeHolderStyle
    // }
    // ]
    // },
    // {
    // name: '5',
    // type: 'pie',
    // clockWise: false,
    // radius: ['15%', '30%'],
    // itemStyle: dataStyle,
    // data: [
    // {
    // value: 1,
    // name: '1% Not at the moment'
    // },
    // {
    // value: 99,
    // name: 'invisible',
    // itemStyle: placeHolderStyle
    // }
    // ]
    // }
    // ]
    // };
    // poleChart.setOption(option);


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
            '	<span  class="skipkeyword" data-keyword="'+keyword+'">' + keyword + '</span>' +
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
    if (keywordrank.length == 0) {
        nodata('#keyword_exposure_rank', '暂无数据');
    }
}

// 自媒体渠道声量排名
function selfMediaRank(data) {
    var media_userrank = data;
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
        if (pnames.length <= 3) {
            platform_namehtml = platform_names;
        } else {
            platform_namehtml = pnames[0] + ',' + pnames[1] + ',' + pnames[2] + '...<span title="' + platform_names + '">(' + pnames.length + ')</span>';
        }
        var logoHtml = '<img src="' + logo + '"alt="user" class="rounded-circle" width="45"/>';
        if (!logo) logoHtml = '<img src="' + ctxPath + 'dist/img/user.jfif" alt="user" alt="user" class="rounded-circle" width="45"/>';
        var userrank = '<tr><td><div class="d-flex align-items-center"><div class="m-r-10">' + logoHtml + '</div>' +
            '<div class=""><h4 class="m-b-0 font-16">' + name + '</h4><span></span></div></div></td><td>' + platform_namehtml + '</td><td class="font-medium">' + volume + '</td></tr>';
        userranks += userrank
    }
    $("#media_user_volume_rank").html(userranks)
    if (media_userrank.length == 0) {
        nodata('#media_user_volume_rank', '暂无数据');
    }
}

function clearlogo() {
    setTimeout(() => {
        $(".anychart-credits").hide()
    }, 100);
}


// 历史代码 ********************************************

/**
 * @author kanshicheng
 * @date 2020/04/16
 * @description 获取热门资讯
 */
function getpopularinformation(analysis_projectid, group_id) {
    if (!analysis_projectid) {
        $('#popularinformation').css('min-height', '150px')
        nodata('#popularinformation', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid=' + group_id);
        return;
    }
    loading("#popularinformation")
    $('#popularinformation').css('min-height', 'none')
    var timePeriod = $('#timePeriodSelect').val();
    $.ajax({
        url: "/analysis/popularinformation",
        type: "post",
        data: {
            projectid: analysis_projectid,
            timePeriod: timePeriod
        },
        success: function (data) {
            var list = JSON.parse(data);
            if (list.length < 1) {
                $('#popularinformation').css('min-height', '150px');
                nodata('#popularinformation', '大概5-10分钟之后更新完成...');
            } else {
                $('#popularinformation').css('min-height', 'none');
                var html = "";
                var hot = '';
                if (list.length > 10) {
                    list.length = 10;
                }
                if (list.length > 0) {
                    var index = 0;
                    for (var i = 0; i < list.length; i++) {
                        switch (i) {
                            case 0:
                                hot = 'v-hot v-hot1'
                                break;
                            case 1:
                                hot = 'v-hot v-hot2'
                                break;
                            case 2:
                                hot = 'v-hot v-hot3'
                                break;
                            default:
                                hot = 'v-hot'
                                break;
                        }
                        if (list[i].title) {
                            var sourceNameHtml = '<span>' + list[i].source_name + '</span>';
                            if (list[i].source_name.length > 5) {
                                sourceNameHtml = '<span title="' + list[i].source_name + '">' + list[i].source_name.substring(0, 5) + '...' + '</span>';
                            }
                            html += '<div class="over-event-listrmzx">' +
                                '<span class="text-over"><span class="' + hot + '">' + (i + 1 - index) + '</span> <a target="_blank" href="' + ctxPath + 'monitor/detail/' + list[i].article_public_id + '?menu=analysis&groupid=' + anal_groupId + '&projectid=' + anal_projectId + '">' + list[i].title + '</a></span>' +
                                '<span>' + list[i].count + '</span>' +
                                '<span>' + list[i].rate + '</span>' +
                                '<span>' + timeParse(list[i].publish_time) + '</span>' +
                                sourceNameHtml +
                                '</div>'
                        } else {
                            index++;
                        }
                    }
                    $("#popularinformation").css("min-height", "0")
                    $("#popularinformation").html(html);
                } else {
                    $("#popularinformation").css({"position": "relative", "min-height": "300px"})
                    nodata("#popularinformation", '大概5-10分钟之后更新完成...')
                }
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                $("#popularinformation").css({"position": "relative", "min-height": "300px"})
                nodata("#popularinformation", '大概5-10分钟之后更新完成...')
            }
        }
    })
}


/**
 * @author wangyi
 * @date 2020/04/16
 * @description 关键词情感分析数据走势
 */
function getemotioncategory_line(analysis_projectid, group_id) {
    if (!analysis_projectid) {
        $('#category-line').css('min-height', '150px')
        nodata('#category-line', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid=' + group_id);
        return;
    }
    loading("#category-line")
    $('#category-line').css('min-height', 'none')
    var timePeriod = $('#timePeriodSelect').val();
    $.ajax({
        url: "/analysis/emotioncategory",
        type: "post",
        data: {
            projectid: analysis_projectid,
            timePeriod: timePeriod
        },
        success: function (data) {
            var data = JSON.parse(data);
            var list = JSON.parse(data.data.keyword_emotion_trend);
            var context = '<i class="fa fa-info-circle m-r-10"></i>' + data.china;
            $("#keywordsMood").html(context);
            keywordsLine(list);

            // 3、热点事件排名
            if (data.data.hot_event_ranking) {
                hotEventRanking = JSON.parse(data.data.hot_event_ranking);
                toHtmlHotEventRanking(0, hotEventRanking.all);
            }
            // 4、关键词高频分布统计

            if (data.data.highword_cloud) {
                var highwords = JSON.parse(data.data.highword_cloud);
                console.info(highwords);
                keywordsCloud(highwords);
            }

        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                $("#category-line").css({"position": "relative", "min-height": "300px"})
                nodata("#category-line", '大概5-10分钟之后更新完成...')
            }
        }
    })
}


// 热门人物地区机构
function popularKeyword(analysis_projectid) {
    if (!analysis_projectid) {
        nodata('#hot_company', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid=' + anal_groupId);
        nodata('#hot_people', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid=' + anal_groupId);
        nodata('#hotarea', '暂无方案，去创建>', ctxPath + 'project/addproject?groupid=' + anal_groupId);
        return;
    }
    loading("#hot_company");
    loading("#hot_people");
    loading("#hotarea");
    var timePeriod = $('#timePeriodSelect').val();
    $.ajax({
        url: ctxPath + "analysis/popularkeyword",
        type: "post",
        data: {
            projectid: analysis_projectid,
            timePeriod: timePeriod
        },
        success: function (data) {
            var datas = eval('(' + data + ')');
            if (anal_projectId) {
                if (datas.updateTime) {
                	reporttime = datas.updateTime;
                    $('#updateTime').html(timeParse(datas.updateTime));
                } else {
                    $('#updateTime').html('正在更新，请稍后再来查看。');
                }
            } else {
                $('#updateTime').html('暂无方案！');
            }
            if (data == "{}") {
                var peoples = '<div class="over-event-title"><span>人物</span><span>趋势</span><span>相关资讯</span></div>';
                nodata('#hot_company', '大概5-10分钟之后更新完成...');
                nodata('#hot_people', '大概5-10分钟之后更新完成...');
                nodata('#hotarea', '大概5-10分钟之后更新完成...');
            } else {
                var hotCompany = datas.hotCompany;
                if (hotCompany.length == 0) {
                    nodata('#hot_company', '大概5-10分钟之后更新完成...');
                } else {
                    var companys = '';
                    for (var i = 0; i < hotCompany.length; i++) {
                        var title = hotCompany[i].title;
                        var titleHtml = '';
                        if (title) {
                            if (title.length > 3) {
                                title = title.substring(0, 24) + "...";
                            }
                            titleHtml = '<a target="_blank" href="' + ctxPath + 'monitor/detail/' + hotCompany[i].article_public_id + '?menu=analysis&groupid=' + anal_groupId + '&projectid=' + anal_projectId + '">' + title + '</a>';
                        } else {
                            titleHtml = '-';
                        }
                        var companyNameHtml = '<span class="hotgpc-1"><a target="_blank" href="' + ctxPath + 'monitor?groupid=' + anal_groupId + '&projectid=' + analysis_projectid + '&search=' + hotCompany[i].keyword + '">' + hotCompany[i].keyword + '</a></span>';
                        if (hotCompany[i].keyword.length > 10) {
                            companyNameHtml = '<span class="hotgpc-1" title="' + hotCompany[i].keyword + '"><a target="_blank" href="' + ctxPath + 'monitor?groupid=' + anal_groupId + '&projectid=' + analysis_projectid + '&search=' + hotCompany[i].keyword + '">' + hotCompany[i].keyword.substring(0, 10) + '...' + '</a></span>';
                        }
                        var company = '<div class="over-event-listgpc">' + companyNameHtml + '<span><i class="' + trend(hotCompany[i].trend) + '"></i></span>' +
                            '<span> ' + hotCompany[i].count + '</span><span>' + hotCompany[i].index + '</span></div>';
// var company = '<div class="over-event-list">'+companyNameHtml+'<span><i
// class="' + trend(hotCompany[i].trend) + '"></i></span>' +
// '<span> '+titleHtml+'</span>' + emotionalIndex(hotCompany[i].emotionalIndex)
// +hotCompany[i].index+ '</div>';
                        companys += company;
                    }
                    $("#hot_company").html(companys);
                }
                var hot_people = datas.hotPeople;
                if (hot_people.length == 0) {
                    nodata('#hot_people', '大概5-10分钟之后更新完成...');
                } else {
                    var peoples = '<div class="over-event-title"><span>人物</span><span>趋势</span><span>相关资讯</span></div>';
                    for (var i = 0; i < hot_people.length; i++) {
                        var title = hot_people[i].title;
                        var titleHtml = '';
                        if (title) {
                            if (title.length > 24) {
                                title = title.substring(0, 24) + "...";
                            }
                            titleHtml = '<a target="_blank" href="' + ctxPath + 'monitor/detail/' + hot_people[i].article_public_id + '?menu=analysis&groupid=' + anal_groupId + '&projectid=' + anal_projectId + '">' + title + '</a>';
                        } else {
                            titleHtml = '-';
                        }
                        var people = '<div class="over-event-list">'
                            + '<span class="hotgpc-1"><a target="_blank" href="' + ctxPath + 'monitor?groupid=' + anal_groupId + '&projectid=' + analysis_projectid + '&search=' + hot_people[i].keyword + '">' + hot_people[i].keyword + '</a></span><span><i class="' + trend(hot_people[i].trend) + '"></i></span>' +
                            '<span> ' + titleHtml + '</span>' + emotionalIndex(hot_people[i].emotionalIndex) + hot_people[i].index + '</div>';
                        peoples += people;
                    }
                    $("#hot_people").html(peoples);
                }
                var hotSpot = datas.hotSpot;
                if (hotSpot.length == 0) {
                    nodata('#hotarea', '大概5-10分钟之后更新完成...');
                } else {
                    var spots = '';
                    for (var i = 0; i < hotSpot.length; i++) {
                        var title = hotSpot[i].title;
                        var titleHtml = '';
                        if (title) {
                            if (title.length > 24) {
                                title = title.substring(0, 24) + "...";
                            }
                            titleHtml = '<a target="_blank" href="' + ctxPath + 'monitor/detail/' + hotSpot[i].article_public_id + '?menu=analysis&groupid=' + anal_groupId + '&projectid=' + anal_projectId + '">' + title + '</a>';
                        } else {
                            titleHtml = '-';
                        }
                        var spot = '<div class="over-event-list"><span class="hotgpc-1"><a target="_blank" href="' + ctxPath + 'monitor?groupid=' + anal_groupId + '&projectid=' + analysis_projectid + '&search=' + hotSpot[i].keyword + '">' + hotSpot[i].keyword + '</a></span><span><i class="' + trend(hotSpot[i].trend) + '"></i></span>' +
                            '<span> ' + titleHtml + '</span>' + emotionalIndex(hotSpot[i].emotionalIndex) + hotSpot[i].index + '</div>';
                        spots += spot;
                    }
                    $("#hotarea").html(spots);
                }
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                nodata("#hot_company", '大概5-10分钟之后更新完成...');
            }
        }
    })
}

// 趋势
function trend(trend) {
    var trendover = '';
    if (trend == 3) {
        trendover = 'zm mdi mdi-arrow-up';
    }
    if (trend == 2) {
        trendover = 'mdi mdi-dots-horizontal';
    }
    if (trend == 1) {
        trendover = 'zm mdi mdi-arrow-down fm';
    }
    return trendover;
}

// 情感
function emotionalIndex(emotionalIndex) {
    var index = '-';
    if (emotionalIndex == 3) {
        index = '<span class="emotion fm">负面</span>';
    }
    if (emotionalIndex == 2) {
        index = '<span class="emotion zx">中性</span>';
    }
    if (emotionalIndex == 1) {
        index = '<span class="emotion zm">正面</span>';
    }
    return index;
}

function allway(analysis_projectid, anal_groupId, group_id) {
    getlatestnews(analysis_projectid, anal_groupId);
    getemotionalrate(analysis_projectid, anal_groupId);
    getplanwordhit(analysis_projectid, anal_groupId);
    getpopularinformation(analysis_projectid, anal_groupId);
    getkeywordindex(analysis_projectid, anal_groupId);
    popularKeyword(analysis_projectid);
    getemotioncategory_line(analysis_projectid, anal_groupId);
}


/**
 * 动态生成css
 */

function addCss(cssText) {
    let style = document.createElement('style'), // 创建一个style元素
        head = document.head || document.getElementsByTagName('head')[0]; // 获取head元素
    style.type = 'text/css'; // 这里必须显示设置style元素的type属性为text/css，否则在ie中不起作用
    if (style.styleSheet) { // IE
        let func = function () {
            try { // 防止IE中stylesheet数量超过限制而发生错误
                style.styleSheet.cssText = cssText;
            } catch (e) {

            }
        }
        // 如果当前styleSheet还不能用，则放到异步中则行
        if (style.styleSheet.disabled) {
            setTimeout(func, 10);
        } else {
            func();
        }
    } else { // w3c
        // w3c浏览器中只要创建文本节点插入到style元素中就行了
        var textNode = document.createTextNode(cssText);
        style.appendChild(textNode);
    }

    console.log(style)
    head.appendChild(style); // 把创建的style元素插入到head中
}


















// 行业分布统计
function industrialDistribution(data) {

    let names = [];
    for (var i = 0; i < data.length; i++){
        names.push(data[i].name);
    }


// $("#dataSourceDistributionChart").html('')
// anychart.onDocumentReady(function () {
// var data = [
// ['微信', 6371664],
// ['微博', 7216301],
// ];
// var chart = anychart.pie(data);
// chart.labels().position('outside');
// chart.radius('43%').innerRadius('50%');
// chart.container('dataSourceDistributionChart');
// chart.draw();
// });
    document.getElementById("industrialDistributiontionChart").removeAttribute('_echarts_instance_'); // 移除容器上的
    // _echarts_instance_
    // 属性
    var basicdoughnutChart = echarts.init(document.getElementById('industrialDistributiontionChart'));



    var option = {
// title: {
// text: 'Browser popularity',
// subtext: 'Open source information',
// x: 'center'
// }


        /*legend: {
            orient: 'vertical',
            x: 'right',
            data: names
            //['微信', '微博', '政务', '论坛', '新闻', '报刊', '客户端', '网站', '外媒', '视频', '博客']
        },*/
        color: ['#3688F7', '#4E99F7', '#6BAAF8', '#8ABBF9', '#A7CEFB' , '#C8DFFC' , '#E7F2FE' , '#8DD499' , '#A4DDAE' , '#BCE5C5' , '#D4F0DB' , '#EEF9F1'],
        toolbox: {
            show: true,
            orient: 'vertical',
            feature: {
                mark: {
                    show: true,
                    title: {
                        mark: 'Markline switch',
                        markUndo: 'Undo markline',
                        markClear: 'Clear markline'
                    }
                },
                magicType: {
                    show: true,
                    title: {
                        pie: 'Switch to pies',
                        funnel: 'Switch to funnel',
                    },
                    type: ['pie', 'funnel'],
                    option: {
                        funnel: {
                            x: '25%',
                            y: '20%',
                            width: '40%',
                            height: '60%',
                            funnelAlign: 'left',
                            max: 1548
                        }
                    }
                },
// restore: {
// show: true,
// title: 'Restore'
// },
// saveAsImage: {
// show: true,
// title: 'Same as image',
// lang: ['Save']
// }
            }
        },
        calculable: true,
        series: [
            {
                name: '',
                type: 'pie',
                radius: ['50%', '70%'],
                center: ['50%', '48%'],
                itemStyle: {
                    normal: {
                        label: {
                            show: true
                        },
                        labelLine: {
                            show: true
                        }
                    },
                    emphasis: {
                        label: {
                            show: true,
                            formatter: '{b}' + '\n\n' + '{c} ({d}%)',
                            position: 'center',
                            textStyle: {
                                fontSize: '17',
                                fontWeight: '500'
                            }
                        }
                    }
                },
                data: data
            }
        ]
    };
    basicdoughnutChart.setOption(option);
}



//事件分析
function eventStatistics(data) {
	debugger;

    let dataArr=new Array()
    let arr=data;
    for(let i in arr){
        let arr2=new Array()
        for(let j in arr[i]){
            arr2.push(arr[i][j])
        }
        dataArr.push(arr2)
    }
    // create data set on our data
    var dataSet = anychart.data.set(dataArr);

    // map data for the first series, take x from the zero column and value from the first column of data set
    var seriesData_1 = dataSet.mapAs({ 'x': 0, 'value': 1 });

    // map data for the second series, take x from the zero column and value from the second column of data set
    /*var seriesData_2 = dataSet.mapAs({ 'x': 0, 'value': 2 });

    var seriesData_3 = dataSet.mapAs({ 'x': 0, 'value': 3 });*/
    // create column chart
    var chart = anychart.column();

    // turn on chart animation
    chart.animation(true);

    // set chart title text settings
    chart.title('');

    // temp variable to store series instance
    var series;

    // helper function to setup label settings for all series
  /*  var setupSeries = function (series, name) {
        series.name(name);
        series.selected()
            .fill('#f48fb1')
            .stroke('1.5 #c2185b');
    };*/

    // create first series with mapped data
    series = chart.column(seriesData_1);
    series.xPointPosition(0.35);
   /* setupSeries(series, '');*/

    // create second series with mapped data
    /*    series = chart.column(seriesData_2);
        series.xPointPosition(0.55);
        setupSeries(series, '中性');

        series = chart.column(seriesData_3);
        series.xPointPosition(0.75);
        setupSeries(series, '负面');*/

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
    chart.container('eventStatisticsChart');

    // initiate chart drawing
    chart.legend(false);
    chart.draw();


    setTimeout(() => {
        $(".anychart-credits").hide()
    }, 10);
}



