
$(function () {
	popular_feelings_analys2(publicoption, 1)
	backanalysis2()
	eventContext2()
	eventTrace2()
	hotAnalysis2()
	netizensAnalysis2()
	statistics2()
	propagationAnalysis2()
	thematicAnalysis2()
	unscrambleContent2()
	wmgxt()
});

function popular_feelings_analys2(currentReport, page){
	$.ajax({
		url:ctxPath+'publicoption/loadInformation',
		type:'post',
		data:{
			eventstarttime:currentReport.eventstarttime,
			eventendtime:currentReport.eventendtime,
			eventkeywords:currentReport.eventkeywords,
			eventstopwords:currentReport.eventstopwords,
			page:page,
			emotionalIndex:'3'
		},
		success:function(res){
			if(res.data!=undefined && res.data.length>0){
				pageHelper(res.page, res.page_count)
				$('#information').html('<li>'+
												'<span>标题</span>'+
												'<span>来源</span>'+
												'<span>时间</span>'+
												'<span>情感</span>'+
											'</li>')
				for(let a of res.data){
					let source=a._source
					let emotionalIndex=''
					let emotionclass=''
					if(source.emotionalIndex==1){
						emotionalIndex='正面'
						emotionclass = 'color:#d65353'
					}else if(source.emotionalIndex==2){
						emotionalIndex='中性'
						emotionclass = 'color:#ffae00'
					}else if(source.emotionalIndex==3){
						emotionalIndex='负面'
						emotionclass = 'color:#36bea6'
					}
					$('#information').append('<li>'+
						'<span><a href="'+source.source_url+'" target="_blank">'+source.title+'</a></span>'+
						'<span>'+source.source_name+'</span>'+
						'<span>'+timeParse(source.publish_time)+'</span>'+
						' <span style="' + emotionclass + '">' + emotionalIndex + '</span>'
					)
				}
			}
		},
		error:function(res){
			console.log(res)
			$('#information').html('暂无数据')
		}
	})
}

function wmgxt(){
	
//	
	if(!(!netizens_analysis.relation)){
		let aaaaa= [{
            name: 'node01',
            des: 'nodedes01',
            symbolSize: 70,
            category: 0
        }, {
            name: 'node02',
            des: 'nodedes02',
            symbolSize: 50,
            category: 1,
        }, {
            name: 'node03',
            des: 'nodedes3',
            symbolSize: 50,
            category: 1,
        }, {
            name: 'node04',
            des: 'nodedes04',
            symbolSize: 50,
            category: 1,
        }, {
            name: 'node05',
            des: 'nodedes05',
            symbolSize: 50,
            category: 1,
        }]
		let bbbb= [{
            source: 'node01',
            target: 'node02',
            name: '',
            des: ''
        }, {
            source: 'node01',
            target: 'node03',
            name: '',
            des: ''
        }, {
            source: 'node01',
            target: 'node04',
            name: '',
            des: ''
        }, {
            source: 'node01',
            target: 'node05',
            name: '',
            des: ''
        }]
		console.log(aaaaa)
		console.log(bbbb)
		let edata = netizens_analysis.relation.data
		let elinks = netizens_analysis.relation.links
		console.log(edata)
		console.log(elinks)
		var myChart = echarts.init(document.getElementById('wmgxt'));
	    var categories = [];
	    for (var i = 0; i < 2; i++) {
	        categories[i] = {
	            name: '类目' + i
	        };
	    }
	    option = {
	        // 图的标题
//	        title: {
//	            text: '重点媒体关系图'
//	        },
	    	color:['#47C2E3','#83EBF8'],
	        // 提示框的配置
	        tooltip: {
	            formatter: function (x) {
	                return x.data.des;
	            }
	        },
	        // 工具箱
	        toolbox: {
	            // 显示工具箱
	            show: true,
	            feature: {
	                mark: {
	                    show: true
	                },
	                // 还原
//	                restore: {
//	                    show: true
//	                },
	                // 保存为图片
//	                saveAsImage: {
//	                    show: true
//	                }
	            }
	        },
//	        legend: [{
//	            // selectedMode: 'single',
//	            data: categories.map(function (a) {
//	                return a.name;
//	            })
//	        }],
	        series: [{
	            type: 'graph', // 类型:关系图
	            layout: 'force', //图的布局，类型为力导图
	            symbolSize: 40, // 调整节点的大小
//	            roam: true, // 是否开启鼠标缩放和平移漫游。默认不开启。如果只想要开启缩放或者平移,可以设置成 'scale' 或者 'move'。设置成 true 为都开启
	            edgeSymbol: ['circle', 'arrow'],
	            edgeSymbolSize: [2, 10],
	            edgeLabel: {
	                normal: {
	                    textStyle: {
	                        fontSize: 20
	                    }
	                }
	            },
	            force: {
	                repulsion: 2500,
	                edgeLength: [10, 50]
	            },
	            draggable: true,
	            lineStyle: {
	                normal: {
	                    width: 2,
	                    color: '#4b565b',
	                }
	            },
	            edgeLabel: {
	                normal: {
	                    show: true,
	                    formatter: function (x) {
	                        return x.data.name;
	                    }
	                }
	            },
	            label: {
	                normal: {
	                    show: true,
	                    textStyle: {}
	                }
	            },
	 
	            // 数据
	            data: edata,
	            links: elinks,
	            categories: categories,
	        }]
	    };
	    myChart.setOption(option);
		
		
	}else{
		$("#wmgxt").attr("style","width:100%")
		$("#wmgxt").html('<h2 align="center" style="margin-top: 4em;color:#a1aab2">暂无数据</h2>')
	}
	
}

function backanalysis2(){
	let fa = back_analysis.first_article
	$("#total").html(back_analysis.total)
	$("#highest").html(back_analysis.highest)
	$("#highest_total").html(back_analysis.highest_total)
	if(!(!fa)){
		$("#fa_publish_time").html(getvalues(fa.publish_time))
		$("#fa_source_name").html(fa.source_name)
		$("#fa_title").html(fa.title)
		$("#source_ranking").html(back_analysis.source_ranking)
		$("#trend").html(back_analysis.trend)
	}
}

function getvalues(val){
	if(!val){
		return "";
	}else{
		val
	}
}

function eventContext2(){
	console.log("start_time:"+start_time)
	let timelinehtml = '<div class="ev-analysis-detail-trend-con"><div class="ev-trend-start ev-trend-con"><span id="start_date">'
		+start_time.substring(1,5)+'年'+start_time.substring(5,8)+'月'+start_time.substring(8,11)+'日'+'</span></div></div>'

//	$('#start_date').html(start_time.substring(0,4)+'年'+start_time.substring(5,7)+'月'+start_time.substring(8,10)+'日')
	for(let i in event_context){
		timelinehtml+='<div class="ev-analysis-detail-trend-con"> <div class="ev-trend-con"> <div class="contenteditable"> [<span id="publish_time1">'+
		event_context[i].publish_time+'</span>]【<span id="title1">'+event_context[i].title+'</span>】<span id="contenta1">'+event_context[i].contenta+'</span> 影响力：<span id="heat1">'+
		event_context[i].heat+'</span></div></div></div>'
//		let id=Number(i)+1
//		$("#publish_time"+id).html(event_context[i].publish_time)
//		$("#title"+id).html(event_context[i].title)
//		$("#contenta"+id).html(event_context[i].contenta)
//		$("#heat"+id).html(event_context[i].heat)
	}
	timelinehtml+='<div class="ev-analysis-detail-trend-con"><div class="ev-trend-start ev-trend-con"><span>结束</span></div></div>'
	$("#timeline").html(timelinehtml)
	
}
function eventTrace2(){
	for(let i in event_trace.positive){
		$('#p_webname'+i).html(event_trace.positive[i].name)
		$('#p_account_count'+i).html(event_trace.positive[i].value)
	}
	for(let j in event_trace.negative){
		$('#n_webname'+j).html(event_trace.negative[j].name)
		$('#n_account_count'+j).html(event_trace.negative[j].value)
	}
}
function hotAnalysis2(){
	let hot_analysishtml=''
	if(hot_analysis.length ==0){
		$("#monitor-content").html('<h2 align="center" style="margin-top: 4em;color:#a1aab2">暂无数据</h2>')
	}else{
	for(let i in hot_analysis){
		if(hot_analysis[i].emotionalIndex==1){
			emotionalIndex='正面'
		}else if(hot_analysis[i].emotionalIndex==2){
			emotionalIndex='中性'
		}else if(hot_analysis[i].emotionalIndex==3){
			emotionalIndex='负面'
		}else{
			emotionalIndex='中性'
		}
		let source_name=hot_analysis[i].source_name
		let iconUrl = ''
		if(source_name == '网易'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/163.com.png'
		}else if(source_name == '36kr'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/36kr.com.png'
		}else if(source_name == '好奇心研究所'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/qdaily.com.png'
		}else if(source_name == '腾讯网'){
			iconUrl = 'https://mat1.gtimg.com/www/icon/favicon2.ico'
		}else if(source_name == '新浪'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/rank.sinanews.sina.cn.png'
		}else if(source_name == '抖音'){
			iconUrl = 'http://wx.qlogo.cn/mmhead/Q3auHgzwzM5veqh0jlpkNK7iaVic8T1icPATdlKB1eVZLVjbxiaibPP3I5A/132'
		}else if(source_name == '小红书'){
			iconUrl = 'http://wx.qlogo.cn/mmhead/Q3auHgzwzM6uD2n9QGUSN1PFY34un3ht0l9EBwfkrvkd3ov6paw1pg/132'
		}else if(source_name == '京东'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/jd.com.png'
		}else if(source_name == '淘宝'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/izhongchou.taobao.com.png'
		}else if(source_name == '什么值得买'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/smzdm.com.png'
		}else if(source_name == '微博'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/s.weibo.com.png'
		}else if(source_name == '知乎'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/zhihu.com.png'
		}else if(source_name == '百度风云榜'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/baidu.com.png'
		}else if(source_name == '360热榜'){
			iconUrl = 'https://s2.ssl.qhres.com/static/121a1737750aa53d.ico'
		}else if(source_name == '搜狗热点'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/sogou.com.png'
		}else if(source_name == '搜狗头图'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/sogou.com.png'
		}else if(source_name == '百度'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/baidu.com.png'
		}else if(source_name == '微信'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/mp.weixin.qq.com.png'
		}else if(source_name == '网易'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/163.com.png'
		}else if(source_name == '头条搜索'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/m.toutiao.com.png'
		}else if(source_name == '今日头条'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/toutiao.com.png'
		}else if(source_name == '豆瓣'){
			iconUrl = 'http://wx.qlogo.cn/mmhead/Q3auHgzwzM6pN8kDEzvUQxKRIzAkVdaxd5tRoEtyMztib8cbXFA76tA/132'
		}else if(source_name == '哔哩哔哩'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/bilibili.com.png'
		}else if(source_name == '爱奇艺'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/iqiyi.com.png'
		}else if(source_name == '腾讯视频'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/v.qq.com.png'
		}else if(source_name == '优酷视频'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/so.youku.com.png'
		}else if(source_name == '拼多多'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/p.pinduoduo.com.png'
		}else if(source_name == '澎湃新闻'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/thepaper.cn.png'
		}else if(source_name == 'ZAKER'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/myzaker.com.png'
		}else if(source_name == '中国政府网'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/gov.cn.png'
		}else if(source_name == 'CCTV央视新闻'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/news.cctv.com.png'
		}else if(source_name == '微信热词'){
			iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/weixin.sogou.com.png'
		}
//		$('#url'+i).attr('href',hot_analysis[i].source_url)
//		$('#logo'+i).css('background','url('+iconUrl+')')
//		$('#topic'+i).html(hot_analysis[i].topic)
//		$('#issueTime'+i).html(hot_analysis[i].publish_time)
//		$('#source_name'+i).html(source_name)
//		$('#emotionalIndex'+i).html(emotionalIndex)
//		$('#original_weight'+i).html(hot_analysis[i].original_weight)
		
		
		let original_weight="";
		if(!(!hot_analysis[i].original_weight)){
			original_weight = hot_analysis[i].original_weight
		}
		hot_analysishtml +='<div class="wb-content just-bet"><div class="monitor-check"><input type="checkbox" id="check1"><span></span></div><div class="monitor-right">'+
		'<div class="monitor-content-title"><a target="_blank" href="'+hot_analysis[i].source_url+
		'" class="link font-bold"> <span background="url('+iconUrl.replace(/^[\s\uFEFF]+|[\s\uFEFF]+$/g, '')+')" class="content-logo"></span><span >'+hot_analysis[i].topic+
		'</span> </a> <span  class="sl-date  ">'+hot_analysis[i].publish_time+
		'</span> </div><div class="wb-content-imgbox"><div class="wb-right-content"><div class="monitor-content-con font-13"></div></div></div>'+
		'<div class="like-comm m-t-10 font-13"> <span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 <span >'+source_name+
		'</span></span><span class="link f-right moodzm">'+emotionalIndex+'</span><span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;"> <i class="fas fa-fire" style="color: #fe475d;"> </i>  '+
		'<span >'+original_weight+'</span></span> </div></div></div><hr>'
	}
	$("#monitor-content").html(hot_analysishtml)
	}
}
function netizensAnalysis2(){
	let nafigure = "";
	let naf = netizens_analysis.figure
	console.log(naf)
//	naf= null;
	if(!naf||naf.length==0){
		
		$("#nafigure").html('<h2 align="center" style="margin-top: 1em;margin-bottom: 1em;color:#a1aab2">暂无数据</h2>')
	
	}else{
		
		let nafigurehtml = '<li><span style="width:15%;">昵称</span><span style="width:5%;">主页</span><span style="width:70%;">发表微博</span><span style="width:10%;">参与微博个数(转发)</span></li>'
	
	
	
	
	

		for(let i in netizens_analysis.figure){
			nafigurehtml += '<li><span style="width:15%;white-space:nowrap;text-overflow:ellipsis;overflow:hidden;" title="'+netizens_analysis.figure[i].name+'">'+netizens_analysis.figure[i].name+'</span><span style="width:5%;"><a href="'+netizens_analysis.figure[i].author_url+'">链接</a></span>'
			+'<span style="width:70%;white-space:nowrap;text-overflow:ellipsis;overflow:hidden;"  title="'+netizens_analysis.figure[i].content
			+'">'+netizens_analysis.figure[i].content.replace(/[\r\n]/g,"")+'</span><span style="width:10%;" >'+netizens_analysis.figure[i].value+'</span></li>'
//			$('#nickname'+i).html(netizens_analysis.figure[i].name)
//			$('#fans'+i).html(netizens_analysis.figure[i].fans)
//			$('#weiboNum'+i).html(netizens_analysis.figure[i].publish)
//			$('#publish_related'+i).html(netizens_analysis.figure[i].publish_related)
		}
		$("#nafigure").html(nafigurehtml)
	}
	
//	for(let j in netizens_analysis.object){
//		$('#objeventTitle'+j).html(netizens_analysis.objecat[j].title)
//		$('#author'+j).html(netizens_analysis.objecat[j].author)
//		$('#objeventTime'+j).html(netizens_analysis.objecat[j].publish_time)
//		$('#hot'+j).html(netizens_analysis.objecat[j].hot)
//	}
	let naobjcet = netizens_analysis.object
	let naohtml ="<li> <span>标题</span> <span>作者</span> <span>时间</span> <span>热度</span> </li>";
	if(!naobjcet || naobjcet.length==0	){
		
		$("#naobject").html('<h2 align="center" style="margin-top: 4em;color:#a1aab2">暂无数据</h2>')
	}else{
		//<span class="v-hot v-hot1">1</span>
		//<span class="v-hot v-hot2">2</span>
		//<span class="v-hot v-hot3">3</span>
		//<span class="v-hot">4</span>
		for(let j =0;j<naobjcet.length;j++){
			let indexspan = "";
			let indexval= j+1
			if(j<3){
				indexspan = '<span class="v-hot v-hot'+indexval+'">'+indexval+'</span>'
			}else{
				indexspan = '<span class="v-hot">'+indexval+'</span>'
			}
			let nao = naobjcet[j]
			naohtml+='<li><span>'+indexspan+'<span >'+nao.title+'</span></span>'+
			'<span >'+nao.author+'</span><span >'+nao.publish_time+
			'</span><span>'+nao.hot+
			'</span></li>'
		}
		$("#naobject").html(naohtml)
		
	}
}
function statistics2(){
	for(let j in statistics.website){
		$('#sm_name'+j).html(statistics.website[j].name)
		$('#sm_num'+j).html(statistics.website[j].value)
	}
	for(let j in statistics.weibo){
		$('#weibo_name'+j).html(statistics.weibo[j].name)
		$('#weibo_num'+j).html(statistics.weibo[j].value)
	}
	for(let j in statistics.social_media){
		$('#website_name'+j).html(statistics.social_media[j].name)
		$('#website_num'+j).html(statistics.social_media[j].value)
	}
	for(let j in statistics.wemedia){
		$('#wemedia_name'+j).html(statistics.wemedia[j].name)
		$('#wemedia_num'+j).html(statistics.wemedia[j].value)
	}
}
function propagationAnalysis2(){
	debugger;
     console.info("data:"+propagation_analysis)
	 propagation_analysis = JSON.parse(propagation_analysis.replace(/\n|\r/g,""));
	for(let i in propagation_analysis.media){
		let fans = '-'
		if(!(!propagation_analysis.media[i].fans)){
			fans = propagation_analysis.media[i].fans
		}
		$('#selfMediaRankingList').append(
			'<tr>'+
				'<td>'+
					'<div class="d-flex no-block align-items-center">'+
						'<div class="m-r-10"><img src="'+propagation_analysis.media[i].logo+'"   onerror="this.src=\'/dist/img/wemadie.jpg\'"  alt="user" class="rounded-circle" width="45"></div>'+
						'<div class="">'+
							'<h5 class="m-b-0 font-16 font-medium">'+propagation_analysis.media[i].name+'</h5>'+
							'<div style="margin-top: 5px;">'+propagation_analysis.media[i].abstract+'</div>'+
						'</div>'+
					'</div>'+
				'</td>'+
				'<td>'+propagation_analysis.media[i].source_name+'</td>'+
				'<td>'+fans+'</td>'+
				'<td class="blue-grey-text  text-darken-4 font-medium">'+propagation_analysis.media[i].publishs+'</td>'+
			'</tr>'
		)
	}
	if(!(!propagation_analysis) && !(!propagation_analysis.source)){
		
		let arr=propagation_analysis.source.all
		let rankingHtml=''
			if(!(!arr)){
				for(let i in arr){
					if(i==0){
						rankingHtml='<span><span class="v-hot v-hot1">1</span></span>'
					}else if(i==1){
						rankingHtml='<span><span class="v-hot v-hot2">2</span></span>'
					}else if(i==2){
						rankingHtml='<span><span class="v-hot v-hot3">3</span></span>'
					}else{
						rankingHtml='<span><span class="v-hot">'+(Number(i)+1)+'</span></span>'
					}
					$('#dataSourceAnalysis1').append(
							'<div class="overview-media-content">'+
							rankingHtml+
							'<span>'+arr[i].name+'</span>'+
							'<span>'+arr[i].media_type+'</span>'+
							'<span style="color: #8eb9f5;">'+arr[i].total+'</span>'+
							'<span style="color: #f54545;">'+arr[i].negative+'</span>'+
							'</div>'
					)
				}
			}
		arr=propagation_analysis.source.clinet
		for(let i in arr){
			if(i==0){
				rankingHtml='<span><span class="v-hot v-hot1">1</span></span>'
			}else if(i==1){
				rankingHtml='<span><span class="v-hot v-hot2">2</span></span>'
			}else if(i==2){
				rankingHtml='<span><span class="v-hot v-hot3">3</span></span>'
			}else{
				rankingHtml='<span><span class="v-hot">'+(Number(i)+1)+'</span></span>'
			}
			$('#dataSourceAnalysis2').append(
					'<div class="overview-media-content">'+
					rankingHtml+
					'<span>'+arr[i].name+'</span>'+
					'<span>'+arr[i].media_type+'</span>'+
					'<span style="color: #8eb9f5;">'+arr[i].total+'</span>'+
					'<span style="color: #f54545;">'+arr[i].negative+'</span>'+
					'</div>'
			)
		}
		arr=propagation_analysis.source.website
		for(let i in arr){
			if(i==0){
				rankingHtml='<span><span class="v-hot v-hot1">1</span></span>'
			}else if(i==1){
				rankingHtml='<span><span class="v-hot v-hot2">2</span></span>'
			}else if(i==2){
				rankingHtml='<span><span class="v-hot v-hot3">3</span></span>'
			}else{
				rankingHtml='<span><span class="v-hot">'+(Number(i)+1)+'</span></span>'
			}
			$('#dataSourceAnalysis3').append(
					'<div class="overview-media-content">'+
					rankingHtml+
					'<span>'+arr[i].name+'</span>'+
					'<span>'+arr[i].media_type+'</span>'+
					'<span style="color: #8eb9f5;">'+arr[i].total+'</span>'+
					'<span style="color: #f54545;">'+arr[i].negative+'</span>'+
					'</div>'
			)
		}
		arr=propagation_analysis.source.BBS
		for(let i in arr){
			if(i==0){
				rankingHtml='<span><span class="v-hot v-hot1">1</span></span>'
			}else if(i==1){
				rankingHtml='<span><span class="v-hot v-hot2">2</span></span>'
			}else if(i==2){
				rankingHtml='<span><span class="v-hot v-hot3">3</span></span>'
			}else{
				rankingHtml='<span><span class="v-hot">'+(Number(i)+1)+'</span></span>'
			}
			$('#dataSourceAnalysis4').append(
					'<div class="overview-media-content">'+
					rankingHtml+
					'<span>'+arr[i].name+'</span>'+
					'<span>'+arr[i].media_type+'</span>'+
					'<span style="color: #8eb9f5;">'+arr[i].total+'</span>'+
					'<span style="color: #f54545;">'+arr[i].negative+'</span>'+
					'</div>'
			)
		}
		arr=propagation_analysis.source.wechat
		for(let i in arr){
			if(i==0){
				rankingHtml='<span><span class="v-hot v-hot1">1</span></span>'
			}else if(i==1){
				rankingHtml='<span><span class="v-hot v-hot2">2</span></span>'
			}else if(i==2){
				rankingHtml='<span><span class="v-hot v-hot3">3</span></span>'
			}else{
				rankingHtml='<span><span class="v-hot">'+(Number(i)+1)+'</span></span>'
			}
			$('#dataSourceAnalysis5').append(
					'<div class="overview-media-content">'+
					rankingHtml+
					'<span>'+arr[i].name+'</span>'+
					'<span>'+arr[i].media_type+'</span>'+
					'<span style="color: #8eb9f5;">'+arr[i].total+'</span>'+
					'<span style="color: #f54545;">'+arr[i].negative+'</span>'+
					'</div>'
			)
		}
		arr=propagation_analysis.source.weibo
		for(let i in arr){
			if(i==0){
				rankingHtml='<span><span class="v-hot v-hot1">1</span></span>'
			}else if(i==1){
				rankingHtml='<span><span class="v-hot v-hot2">2</span></span>'
			}else if(i==2){
				rankingHtml='<span><span class="v-hot v-hot3">3</span></span>'
			}else{
				rankingHtml='<span><span class="v-hot">'+(Number(i)+1)+'</span></span>'
			}
			$('#dataSourceAnalysis6').append(
					'<div class="overview-media-content">'+
					rankingHtml+
					'<span>'+arr[i].name+'</span>'+
					'<span>'+arr[i].media_type+'</span>'+
					'<span style="color: #8eb9f5;">'+arr[i].total+'</span>'+
					'<span style="color: #f54545;">'+arr[i].negative+'</span>'+
					'</div>'
			)
		}
		$('#dataSourceAnalysis2').hide()
		$('#dataSourceAnalysis3').hide()
		$('#dataSourceAnalysis4').hide()
		$('#dataSourceAnalysis5').hide()
		$('#dataSourceAnalysis6').hide()
	}
}
function thematicAnalysis2(){
	let arr=thematic_analysis.view
	let rankingHtml=''
	for(let i in arr){
		if(i==0){
			rankingHtml='<span class="v-hot v-hot1">1</span>'
		}else if(i==1){
			rankingHtml='<span class="v-hot v-hot2">2</span>'
		}else if(i==2){
			rankingHtml='<span class="v-hot v-hot3">3</span>'
		}else{
			rankingHtml='<span class="v-hot">'+(Number(i)+1)+'</span>'
		}
		let title = arr[i].title;
		if (title.length > 50){
			title = title.substring(0,50) + "......"
		}

		$('#view').append(
			'<li>'+
				'<span title="'+arr[i].title+'">'+rankingHtml+title+'</span>'+
				'<span>'+arr[i].author+'</span>'+
				'<span>'+arr[i].publish_time+'</span>'+
				'<span>'+arr[i].hot+'</span>'+
			'</li>'
		)
	}
	arr=thematic_analysis.netizen
	for(let i in arr){
		if(i==0){
			rankingHtml='<span class="v-hot v-hot1">1</span>'
		}else if(i==1){
			rankingHtml='<span class="v-hot v-hot2">2</span>'
		}else if(i==2){
			rankingHtml='<span class="v-hot v-hot3">3</span>'
		}else{
			rankingHtml='<span class="v-hot">'+(Number(i)+1)+'</span>'
		}

		let title = arr[i].title;
		if (title.length > 50){
			title = title.substring(0 , 49) + "......"
		}

		$('#netizen').append(
			'<li>'+
				'<span title="'+arr[i].title+'">'+rankingHtml+title+'</span>'+
				'<span>'+arr[i].author+'</span>'+
				'<span>'+arr[i].publish_time+'</span>'+
				'<span>'+arr[i].hot+'</span>'+
			'</li>'
		)
	}
	arr=thematic_analysis.media
	for(let i in arr){
		if(i==0){
			rankingHtml='<span class="v-hot v-hot1">1</span>'
		}else if(i==1){
			rankingHtml='<span class="v-hot v-hot2">2</span>'
		}else if(i==2){
			rankingHtml='<span class="v-hot v-hot3">3</span>'
		}else{
			rankingHtml='<span class="v-hot">'+(Number(i)+1)+'</span>'
		}

		let title = arr[i].title;
		if(title.length > 50){
			title = title.substring(0 , 49) + "......"
		}

		$('#media').append(
			'<li>'+
				'<span title="'+arr[i].title+'">'+rankingHtml+title+'</span>'+
				'<span>'+arr[i].author+'</span>'+
				'<span>'+arr[i].publish_time+'</span>'+
				'<span>'+arr[i].hot+'</span>'+
			'</li>'
		)
	}
}
function unscrambleContent2(){
	$('#main_media').html(unscramble_content.main_media)
	$('#wemedia').html(unscramble_content.wemedia)
}
