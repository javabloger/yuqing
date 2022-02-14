let aaa = ''
if(one_type == 12){
    aaa = '<li class="create-tab-act" data-classify="4" data-id="12"> 热门</li>' +
        '<li class="" data-classify="1" data-id="9"> 资讯</li>' +
        '<li class="" data-classify="2" data-id="10"> 视频</li>' +
        '<li class="" data-classify="3" data-id="11"> 电商</li>'
}else if(one_type == 9){
    aaa = '<li class="" data-classify="4" data-id="12"> 热门</li>' +
        '<li class="create-tab-act" data-classify="1" data-id="9"> 资讯</li>' +
        '<li class="" data-classify="2" data-id="10"> 视频</li>' +
        '<li class="" data-classify="3" data-id="11"> 电商</li>'
}else if(one_type == 10){
    aaa = '<li class="" data-classify="4" data-id="12"> 热门</li>' +
        '<li class="" data-classify="1" data-id="9"> 资讯</li>' +
        '<li class="create-tab-act" data-classify="2" data-id="10"> 视频</li>' +
        '<li class="" data-classify="3" data-id="11"> 电商</li>'
}else if(one_type == 11){
    aaa = '<li class="" data-classify="4" data-id="12"> 热门</li>' +
        '<li class="" data-classify="1" data-id="9"> 资讯</li>' +
        '<li class="" data-classify="2" data-id="10"> 视频</li>' +
        '<li class="create-tab-act" data-classify="3" data-id="11"> 电商</li>'
}
$('#search-tab').html(aaa)
thirdBox(one_type)


/**
 * 加载网站分类
 * @param type_two_id
 * @returns
 */
function thirdBox(type_two_id){
	var data = {type_two_id:type_two_id}
	ajax('GET',ctx + 'fullsearch/listFullTypeByThird', data, setThirdBox);
}
function setThirdBox(data){
	$('#sourceName-select').html("")
    if(two_type == '全部'){
        var htmlHead = '<span class="seActive">全部</span>'
        $('#sourceName-select').append(htmlHead)
        for (var i = 0; i < data.length; i++) {
            var htmlStr = '<span class="">'+ data[i].name +'</span>'
            $('#sourceName-select').append(htmlStr)
        }
    }else{
        var htmlHead = '<span class="">全部</span>'
        $('#sourceName-select').append(htmlHead)
        for (var i = 0; i < data.length; i++) {
            var htmlStr = '<span class="">'+ data[i].name +'</span>'
            if(data[i].name == two_type){
                htmlStr = '<span class="seActive">'+ data[i].name +'</span>'
            }
            $('#sourceName-select').append(htmlStr)
        }
    }
	hotList(1)
}

function hotList(page){
	let data = new Object();
    let sourceName = $('#sourceName-select > .seActive').text();
    if(sourceName == '全部') sourceName = ''
	data.source_name = sourceName;
    data.pageNum = page;
    data.searchWord = $('#searchkeyword').val();
    data.classify = $('#search-tab > .create-tab-act').attr('data-classify');
    data.pageSize = 25;
    data.timeType = 2;
    let seturl = 'hotpage?' + 'one_type=' + one_type + '&two_type=' + encodeURIComponent(two_type) + '&page=' + page
    setUrl(seturl);
	ajax('GET',ctx + 'fullsearch/hotList', data, installHot);
}

function installHot(res){
	let code = res.code;
	if (code == 200) {
		let data = res.data
		totalPage = res.page_count;
        let totalCount = res.count;
        let currentPage = res.page;
        page = currentPage
        mescroll.endByPage(data.length, totalPage);
        // if(totalPage > page){
        // 	$('.van-list__placeholder').html('上拉加载更多...')
        // }else{
        // 	$('.van-list__placeholder').html('没有更多了')
        // }
        if (data.length > 0) {
        	for (let i = 0; i < data.length; i++) {
        		let source = data[i]._source
        		let source_name = source.source_name
        		let title = source.topic
        		let publish_time = source.spider_time
				if(!publish_time){
					publish_time = source.spider_time
				}
        		
        		let sales_volume = ''
                if (source.sentiment == 1) {
                	sales_volume = '<div class="right" style="color: #f62d51;">正面</div>'
                } else if (source.sentiment == 2) {
                	sales_volume = '<div class="right" style="color: #ffbc34;">中性</div>'
                } else if (source.sentiment == 3) {
                	sales_volume = '<div class="right" style="color: #36bea6;">负面</div>'
                }
                
                if(source.classify == 3){
                	if(source.sales_volume){
                		sales_volume = '<div class="right">销量 '+source.sales_volume+'</div>'
                	}else{
                		sales_volume = ''
                	}
                }else if(source.classify == 4 || source.classify == 2){
                	if(source.original_weight){
                		sales_volume = '<div class="right">'+source.original_weight+' <svg class="icon"><use xlink:href="#icon-remen3"></use></svg></div>'
                	}
                }

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
        		
        		let html = '<div class="line-info">'
						   + '	<div class="title"><span class="content-logo" style="background: url('+iconUrl+');"></span><a target="_blank" href="'+source.source_url+'">'+title+'</a></div>'
						   + '	<div class="hot-tips">'
						   + '		<div class="left">'
						   + '			<span><svg class="icon"><use xlink:href="#icon-news"></use></svg> '+source_name+'</span>'
						   + '			<span><svg class="icon"><use xlink:href="#icon-datatime"></use></svg> '+timeParse(publish_time)+'</span>'
						   + '		</div>'
						   + sales_volume
						   + '	</div>'
						   + '</div>'
				$('.hot-news-content-box').append(html)
        	}
        }
	}
}

$('body').on('click', '#sourceName-select span',function (e) {
    var active = "seActive";
    var normal = "";
    var act = $(this).hasClass(active);
    if (!act) {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        two_type = $(this).text();
        $('.hot-news-content-box').html('')
        $('.van-list__placeholder').html('加载中...')
        hotList(1)
    }
});

$('body').on('click', '#search-tab li',function (e) {
    var active = "create-tab-act";
    var normal = "";
    var act = $(this).hasClass(active);
    if (!act) {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        var id = $(this).attr('data-id')
        one_type = $(this).attr('data-id')
        two_type = '全部';
        $('.hot-news-content-box').html('')
        $('.van-list__placeholder').html('加载中...')
        thirdBox(id)
    }
});

$("#searchkeyword").on('keypress', function(e) {    //#keyword为input文本框
	var keycode = e.keyCode;
	var searchName = $(this).val();
	if(keycode == '13') {
		if(searchName != null && searchName != ''){
			$('.hot-news-content-box').html('')
	        $('.van-list__placeholder').html('加载中...')
			hotList(1)        //触发list()事件
		}
	}
});

//监听事件
window.addEventListener('scroll', function(){
	let t = $('body, html').scrollTop();   // 目前监听的是整个body的滚动条距离
	if(t > 214){
		$('#search-tab').addClass('van-sticky--fixed')
	}else{
		$('#search-tab').removeClass('van-sticky--fixed')
	}
});

$(function(){
    $(window).scroll(function(){

        var scrollH = document.documentElement.scrollHeight;

        var clientH = document.documentElement.clientHeight;
        if (scrollH == (document.documentElement.scrollTop | document.body.scrollTop) + clientH){


        }
    });

});

var mescroll = new MeScroll("content-box", { //第一个参数"mescroll"对应上面布局结构div的id (1.3.5版本支持传入dom对象)
    //如果您的下拉刷新是重置列表数据,那么down完全可以不用配置,具体用法参考第一个基础案例
    //解析: down.callback默认调用mescroll.resetUpScroll(),而resetUpScroll会将page.num=1,再触发up.callback
    // down: {
    //     callback: downCallback //下拉刷新的回调,别写成downCallback(),多了括号就自动执行方法了
    // },
    up: {
        callback: upCallback, //上拉加载的回调
    }
});

function upCallback(){
    //加载新数据
    console.log(22222222)
    if(page >= totalPage){
        console.log('没有更多了')
    }else{
        console.log('开始加载数据')

    }
    page++;
    hotList(page);
}


/**
 * ajax 同步
 * @param type
 * @param url
 * @param data
 * @param funcname
 * @returns
 */
function ajax(type,url, data,funcname){
	$.ajax({
        type: type,
        url: url,
        dataType: 'json',
        data: data,
        contentType: 'application/json;charset=utf-8',
        success: function (res) {
            funcname(res);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctx + "login";
            } else {
                $("#page").html("");
                dataerror("#monitor-content");
            }
        }
    });
}

function setUrl(url) {
    history.pushState({url: url, title: document.title}, document.title, url)
}