/**
 * 左侧菜单 点击
 * @param e
 * @returns
 */
$('body').on('click', '#sidebarnav li',function (e) {
    if (!$(this).hasClass("comactive")) {
    	$(this).siblings().removeClass("comactive")
    	$(this).addClass("comactive")
		if(menuStyle == 0 ){
			let arry = $(this).attr("data-id").split(',');
			//full_type = arry[0]
			idList = $(this).attr("data-id")
			full_poly = $(this).attr("data-poly")
			var params = '?searchword='+searchWord +'&fulltype=' + full_type + '&menuStyle=0&full_poly='+full_poly+'&pageSize=25&page=1';
			window.location.href = ctx + 'fullsearch/result' + params;
		}else{
			full_type = $(this).attr("data-id")
			var params = '?searchword='+searchWord +'&fulltype=' + full_type + '&menuStyle=1&page=1';
			window.location.href = ctx + 'fullsearch/result' + params;
		}
    }
})


/**
 * 顶部面包屑导航
 * @returns
 */
function breadCrumbs() {
	var onlyid = $.getUrlParam('onlyid')
	$.ajax({
        type: 'GET',
        url: ctx + "fullsearch/getBreadCrumbs",
        dataType: 'json',
        data: {menuStyle:menuStyle,fulltype:full_type,onlyid:onlyid,polyid:full_poly},
        contentType: 'application/json;charset=utf-8',
        success: function (res) {
        	console.log(res)
        	var menuHtml = '情报搜索';
            var html = '<li class="breadcrumb-item">' + menuHtml + '</li>';
            if(menuStyle == 1){ //展开模式
            	 html += '<li class="breadcrumb-item">'+ res.fullTypeName +'</li>';
            }
            if(menuStyle == 0){//聚合模式
            	html += '<li class="breadcrumb-item">'+ res.polyIdName +'</li>';
            	html += '<li class="breadcrumb-item">'+ res.onlyIdName +'</li>';
            }
           
            html += '<li class="breadcrumb-item">文章详情</li>';
            $('#breadCrumbs').html(html);
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

/**
 * 获取url中的参数
 * @param $
 * @returns
 */
(function ($) {
    $.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]); return null;
    }
})(jQuery);