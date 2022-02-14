//$(function() {
////	hotList(1);
//	reportchange(1)
//	weibo()
//	allhot()
//});

window.onload = function(){
	//reportchange(1)
	wexin()
	weibo()
	douyin()
	policydata()
	finaceData()
	scienceandtechnology()
	bilibili()
	tecentvedio()
	allhot()
	//leaders()
	warrning()
	//upload()
	todaypostatus()
	projectpostatus()
	//showPushPO(0)
	//reprintPO()
	//collectionpo()
	//onlinestatus()
};

function onlinestatus(){
	$.ajax({
		type:'post',
		url:ctx + '/onlinestatistical',
		contentType: 'application/json;charset=utf-8',
        success: function (res) {
        	// console.log(res)
        	if(res.code==1){
        		let data = res.onlinedata
        		$("#endlogintime").html(formatDate(data.end_login_time))
        		$("#logincount").html(data.login_count +'次')
        		$("#onlinetotal").html(data.val + '人')
        	}
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctx + "login";
            } 
        }
	})
}




function collectionpo(){
	let data = new Object();
	data.user_id = user.user_id;
	$.ajax({
		type:"POST",
		url:ctx+"/displayboard/collection2",
		data:{
			user_id:user.user_id
			},
		success:function(res){
			res = JSON.parse(res)
			// console.log(res)
			let collectionpo = res.data
			
			html = ''
				for (var i = 0; i < collectionpo.length; i++) {
					collection = collectionpo[i]
					var groupid = '';
					var projectid = '';
					
					let emotionalIndex
					switch(collection.emotionalIndex){
					case 1:emotionalIndex = '<span class="emotion zm">正面</span>';break;
					case 2:emotionalIndex = '<span class="emotion zx">中性</span>';break;
					case 3:emotionalIndex = '<span class="emotion fm">负面</span>';break;
					default:emotionalIndex = '<span class="emotion zx">中性</span>';
					}
					groupid = collection.groupid;
					projectid = collection.projectid;
//					console.log(collection.favoritetime)
//					console.log(timeParse(collection.favoritetime))
					html+='<li><a target="_blank" href="/monitor/detail/'+collection.article_public_id+'?menu=analysis&amp;groupid='+groupid+'&amp;projectid='+projectid+'">'
					+'<div class="over-newsbox"><div class="content-title"><div class="over-title">'
					+collection.title+'</div>'+emotionalIndex+'</div><div class="over-tips"> <span>'
					+timeParse(collection.favoritetime)+'</span> <span>来源：'+collection.source_name+'</span> </div>'
					+'</div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
					
				}
			if('' != html){
				$("#collection").html(html)
			}else{
				$("#collection").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
			}
			
		},
		error:function(res){
			
		}
	})
	
//	if(!(!collection_po)){
//		let collectionpo = JSON.parse(escape2Html(collection_po)) 
//		html = ''
//			for (var i = 0; i < collectionpo.length; i++) {
//				collection = collectionpo[i]
//				
//				let emotionalIndex
//				switch(collection.emotionalIndex){
//				case 1:emotionalIndex = '<span class="emotion zm">正面</span>';break;
//				case 2:emotionalIndex = '<span class="emotion zx">中性</span>';break;
//				case 3:emotionalIndex = '<span class="emotion fm">负面</span>';break;
//				default:emotionalIndex = '<span class="emotion zx">中性</span>';
//				}
//				html+='<li><a target="_blank" href="/monitor/detail/'+collection.id+'?menu=analysis&amp;groupid='+collection.groupid+'&amp;projectid='+collection.projectid+'">'
//				+'<div class="over-newsbox"><div class="content-title"><div class="over-title">'
//				+collection.title+'</div>'+emotionalIndex+'</div><div class="over-tips"> <span>'
//				+collection.publish_time+'</span> <span>来源：'+collection.source_name+'</span> </div>'
//				+'</div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
//				
//			}
//		
//		$("#collection").html(html)
//	}else{
//		$("#collection").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
//	}

}
function reprintPO(){
	let reprintPO = JSON.parse(escape2Html(reprint_PO)) 
	let html = ''
	for (let i = 0; i < 5; i++) {
		let reprint = reprintPO[i]
//		console.log(reprint)
		debugger;
		let emotionalIndex
		switch(reprint.emotionalIndex){
		case 1:emotionalIndex = '<span class="emotion zm">正面</span>';break;
		case 2:emotionalIndex = '<span class="emotion zx">中性</span>';break;
		case 3:emotionalIndex = '<span class="emotion fm">负面</span>';break;
		default:emotionalIndex = '<span class="emotion zx">中性</span>';
		}
		
		html +='<li><a target="_blank" href="/monitor/detail/'+reprint.id+'">'
		+'<div class="over-newsbox"><div class="content-title"><div class="over-title">'
		+reprint.title+'</div>'+emotionalIndex+'</div><div class="over-tips"><span>'
		+timeParse(reprint.publish_time)+'</span> <span>来源：'+reprint.source_name+'</span> <span>转载量：'
		+reprint.forwardingvolume+'</span></div></div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
	}
	// console.log(html)
	$("#reprint").html(html)
}

$("#search-tab li").on("click", function () {
    var type = $(this).attr("data-type");
    $(this).siblings().removeClass("create-tab-act");
    $(this).addClass("create-tab-act");
    showPushPO(type)
})

function showPushPO(type){
	// console.log(push_PO)
	debugger;
	// console.log(escape2Html(push_PO))
	debugger;
	let pushPO = JSON.parse(escape2Html(push_PO)) 
	let list
	if(type == 1){
		list = pushPO.positive
	}else if(type == 2){
		list = pushPO.negative
	}else{
		list = pushPO.all
	}
	// console.log(list)
	let html =''
	for (let i = 0; i < list.length; i++) {
		let detail = list[i]
		
		
		let emotionalIndex
		switch(detail.emotionalIndex){
		case 1:emotionalIndex = '<span style="margin-right: 15px;" class="link moodzm">正面</span>';break;
		case 2:emotionalIndex = '<span style="margin-right: 15px;" class="link moodzx">中性</span>';break;
		case 3:emotionalIndex = '<span style="margin-right: 15px;" class="link moodfm">负面</span>';break;
		default:emotionalIndex = '<span style="margin-right: 15px;" class="link moodzx">中性</span>';
		}
		
		
		html +='<tr><td><div style="display:flex;"><a target="_blank" style="font-weight: 500;" href="/monitor/detail/'+detail.id+'">'
			+'<span class="v-hot v-hot'+(i+1)+'">'+(i+1)+'</span>'+detail.title+'</a>'
            +'<div style="margin-left: auto;"><span style="white-space: nowrap;margin-right: 15px;"><i class="mdi mdi-clock"></i>时间：' 
            +timeParse(detail.publish_time)+'</span><span style="white-space: nowrap;margin-right: 15px;">来源：'+detail.source_name
            +'</span>'+emotionalIndex
//            <span style="font-size: smaller;"><i class=" fa fas fa-fire"></i>&nbsp;55.56%</span>
            +'</div></div><div class="text-over-2">'
            +detail.content +'</div><div style="display: flex;"></div></td></tr>'
	}
	// console.log(html)
	$("#hotEventRanking").html(html)
	
}
 
function projectpostatus(){
	let html = ''
	if(!(!project_PO_status)){
		let projectpostatus = JSON.parse(escape2Html(project_PO_status)) 
		let allvalue =0
		for (let i = 0; i < projectpostatus.length; i++) {
			let a = projectpostatus[i]
			allvalue += a.value
		}
		for(let i=0;i<projectpostatus.length;i++){
			for(let j=i+1;j<projectpostatus.length;j++){
				let val1 = projectpostatus[i].value
				if(!val1 || val1 == "undefined"){
					val1 = 0
				}
				let val2 = projectpostatus[j].value
				if(!val2 || val2 == "undefined"){
					val2 = 0
				}
				if(val1<val2){
					let temp=projectpostatus[i]
					projectpostatus[i]=projectpostatus[j]
					projectpostatus[j]=temp
				}
			}
		}
		for (let i = 0; i < projectpostatus.length; i++) {
			let detail = projectpostatus[i]
			let color
			
			switch(i){
			case 0:color = 'bg-info';break;
			case 1:color = 'bg-cyan';break;
			case 2:color = 'bg-success';break;
			case 3:color = 'bg-danger';break;
			case 4:color = 'bg-warning';break;
			}
			
			if(i==0){
				html += '<li>'
			}else{
				html += '<li class="m-t-25">'
			}
			html += '<div class="d-flex align-items-center"><div><h6 class="m-b-0 "><span>'
				+detail.project_name+'</span></h6></div><div class="ml-auto"><h6 class="m-b-0 font-bold">'
				+detail.value +'</h6></div></div><div class="progress m-t-10">'
				+'<div class="progress-bar '+color+'" role="progressbar" style="width: '+Percentage(detail.value,allvalue)+'%" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>'
				+'</div></li>'
		}
	}
	if('' == html){
		$("#projectstatus").html("<h4 style='text-align: center;margin-top: 20%;'>暂无数据<br>请确认是否配置专题方案</h4>")
	}else{
		$("#projectstatus").html(html)
	}

}


function todaypostatus(){
	let html = ''
	if(!(!today_PO_status)){
			let todaypostatus = JSON.parse(escape2Html(today_PO_status)) 
			let allvalue =0
			for (let i = 0; i < todaypostatus.length; i++) {
				let a = todaypostatus[i]
				
				let val = a.value
				if(!val || val == "undefined"){
					val = 0
				}
				allvalue += val
			}
			for(let i=0;i<todaypostatus.length;i++){
				for(let j=i+1;j<todaypostatus.length;j++){
					let val1 = todaypostatus[i].value
					if(!val1 || val1 == "undefined"){
						val1 = 0
					}
					let val2 = todaypostatus[j].value
					if(!val2 || val2 == "undefined"){
						val2 = 0
					}
					if(val1<val2){
						let temp=todaypostatus[i]
						todaypostatus[i]=todaypostatus[j]
						todaypostatus[j]=temp
					}
				}
			}
			// console.log("todaypostatus:"+todaypostatus)
			for (let i = 0; i < todaypostatus.length; i++) {
				let detail = todaypostatus[i]
				let color
				
				switch(i){
				case 0:color = 'bg-info';break;
				case 1:color = 'bg-cyan';break;
				case 2:color = 'bg-success';break;
				case 3:color = 'bg-danger';break;
				case 4:color = 'bg-warning';break;
				}
				
				let val = detail.value
				if(!val || val == "undefined"){
					val = 0
				}
				if(i==0){
					html += '<li>'
				}else{
					html += '<li class="m-t-25">'
				}
				html += '<div class="d-flex align-items-center"><div><h6 class="m-b-0 "><span>'
					+detail.project_name+'</span></h6></div><div class="ml-auto"><h6 class="m-b-0 font-bold">'
					+val +'</h6></div></div><div class="progress m-t-10">'
					+'<div class="progress-bar '+color+'" role="progressbar" style="width: '+Percentage(val,allvalue)+'%" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>'
					+'</div></li>'
			}
	}
	if('' == html){
		$("#todaystatus").html("<h4 style='text-align: center;margin-top: 20%;'>暂无数据<br>请确认是否配置方案</h4>")
	}else{
		$("#todaystatus").html(html)
	}
}

function upload(){
	
	$.ajax({
		type:'post',
		url:ctx + '/upload/getupload_PO',
		contentType: 'application/json;charset=utf-8',
        success: function (res) {
        	// console.log(res)
        	// console.log(user.user_id)
        	let list = JSON.parse(res).list
        	let html = "<ul>"
        	for (var i = 0; i < list.length; i++) {
        		let upload = list[i]
				let status
				switch(upload.status){
				case 1:status = '已上报';break;
				case 2:status = '已处理';break;
				case 4:status = '已完成';break;
				}
        		let typedata = '0';
				let type = '我上报的'
        		if(upload.create_user_id != user.user_id){
        			type = "上报给我的"
        				typedata = '1';
        			
        		}
        		
				html+= '<li> <a target="_blank" href="/upload/edit/'+upload.upid+'/'+upload.uaid+'/'+typedata+'">'
				+'<div class="over-newsbox"><div class="content-title"><div class="over-weibohottopictitle" title="'+upload.title+'" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;">'+upload.title
				+'</div></div><div class="over-tips"> <span>'	+timeParse(formatDate(upload.updatetime))+'</span> <span>类型：'+type+'</span><span>状态：'
				+status+'</span> <span>上报人：'+upload.username+'</span></div>'
				+'</div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
			}
		html += "</ul>"
			$("#upload").html(html)
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctx + "login";
            } 
        }
	})
	
//	if(!(!upload_PO)){
//		
//		let uploads = JSON.parse(escape2Html(upload_PO)) 
//		let html = "<ul>"
//			for (let i = 0; i < uploads.length; i++) {
//				let upload = uploads[i]
//				let status
//				switch(upload.status){
//				case 1:status = '已上报';break;
//				case 2:status = '已处理';break;
//				case 4:status = '已完成';break;
//				}
//				
//				html+= '<li> <a target="_blank" href="/upload/edit/'+upload.upid+'/'+upload.uaid+'/1">'
//				+'<div class="over-newsbox"><div class="content-title"><div class="over-weibohottopictitle">'+upload.title
//				+'</div></div><div class="over-tips"> <span>'	+upload.updatetime+'</span> <span>类型：我上报的</span><span>状态：'
//				+status+'</span> <span>上报人：'+user.username+'</span></div>'
//				+'</div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
//			}
//		html += "</ul>"
//			$("#upload").html(html)
//	}else{
//		$("#upload").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
//	}
}


function leaders(){
	if(!(!leaders_PO)){
		let leaders = JSON.parse(escape2Html(leaders_PO)) 
		let html = "<ul>"
			for (let i = 0; i < leaders.length; i++) {
				let leader = leaders[i]
				let emotionalIndex
				switch(leader.emotionalIndex){
				case 1:emotionalIndex = '<span class="emotion zm">正面</span>';break;
				case 2:emotionalIndex = '<span class="emotion zx">中性</span>';break;
				case 3:emotionalIndex = '<span class="emotion fm">负面</span>';break;
				default:emotionalIndex = '<span class="emotion zx">中性</span>';
				}
				
				html+= '<li><a target="_blank" href="/monitor/detail/'+leader.id+'">'
				+'<div class="over-newsbox"><div class="content-title"><div class="over-title">'
				+leader.title+'</div> '	+emotionalIndex
				+'</div><div class="over-tips"> <span>'
				+timeParse(leader.publish_time)+'</span> <span>来源：'+leader.source_name+'</span> </div>'
				+'</div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
				
			}
		html += "</ul>"
			$("#leaders").html(html)
	}else{
//		$("#leaders").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")

	}
}

function warrning(){
	let html = ""
	if(!(!warning_PO)){
		let warnings = JSON.parse(escape2Html(warning_PO))
		html += "<ul>"
			for (let i = 0; i < warnings.length; i++) {
				let warning = warnings[i]
				console.log(warning.source_name)
				let emotionalIndex
				switch(warning.emotionalIndex){
				case 1:emotionalIndex = '<span class="emotion zm">正面</span>';break;
				case 2:emotionalIndex = '<span class="emotion zx">中性</span>';break;
				case 3:emotionalIndex = '<span class="emotion fm">负面</span>';break;
				default:emotionalIndex = '<span class="emotion zx">中性</span>';
				}
				
				html+= '<li><a target="_blank" href="/monitor/detail/'+warning.id+'">'
				+'<div class="over-newsbox"><div class="content-title"><div class="over-title">'
				+warning.title+'</div> '	+emotionalIndex
				+'</div><div class="over-tips"> <span>'
				+timeParse(warning.publish_time)+'</span> <span>来源：'+warning.source_name+'</span> </div>'
				+'</div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
			}
		html += "</ul>"
	}
	if(html.length < 10){
		$("#warrning").html("<h4 style='text-align: center;margin-top: 20%;'>暂无预警信息</h4>")
	}else{
		$("#warrning").html(html)
	}
}
	

var ctx = getRootPath();
function getRootPath(){  
    //获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp  
    var curWwwPath=window.document.location.href;  
    //获取主机地址之后的目录，如： uimcardprj/share/meun.jsp  
    var pathName=window.document.location.pathname;  
    var pos=curWwwPath.indexOf(pathName);  
    //获取主机地址，如： http://localhost:8083  
    var localhostPaht=curWwwPath.substring(0,pos);  
    //获取带"/"的项目名，如：/uimcardprj  
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);  
    return(localhostPaht+projectName);  
}  

$("#report-tab li").on("click", function () {
    var type = $(this).attr("data-type");
    $(this).siblings().removeClass("create-tab-act");
    $(this).addClass("create-tab-act");
});

function weibo(){

	loading('#weibo');
	if(!(!hot_weibo)){
		let weibo =	JSON.parse(escape2Html(hot_weibo)) 
		let html='<ul>'
			for(let i = 0;i < weibo.length&&i<5; i++){
				let weibodetail = weibo[i]
				let emotionalIndex
				switch(weibodetail.emotionalIndex){
				case 1:emotionalIndex = '正面';break;
				case 2:emotionalIndex = '中性';break;
				case 3:emotionalIndex = '负面';break;
				default:emotionalIndex = '中性';
				}
				
				html += '<li><a target="_blank" href="'+weibodetail.source_url+'"><div class="over-newsbox"><div class="content-title">'
				+ '<div class="over-weibohottopictitle" title="'+weibodetail.topic+'" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;"><span class="content-logo" style="background: url(https://file.ipadown.com/tophub/assets/images/media/s.weibo.com.png);"></span>'
				+ weibodetail.topic+'</div></div><div class="over-tips">'
				+ '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 微博</span><span class="link f-right moodzm">'+emotionalIndex+'</span>'
				+ '<span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;"><i class="fas fa-fire" style="color: #fe475d;"> </i> '+weibodetail.original_weight+'</span>'
				+ '</div></div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
				
			}
		html +='</ul>'
			$("#weibo").html(html)
	}else{
		$("#weibo").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
	}
}
/**
 * 抖音热点
 * @returns
 */
function douyin(){

	loading('#douyin');
	if(!(!hot_douyin)){
		let douyin =JSON.parse(escape2Html(hot_douyin)) 
		let html='<ul>'
			for(let i = 0;i < douyin.length&&i<5; i++){
				let douyindetail = douyin[i]
				let emotionalIndex
				switch(douyindetail.emotionalIndex){
				case 1:emotionalIndex = '正面';break;
				case 2:emotionalIndex = '中性';break;
				case 3:emotionalIndex = '负面';break;
				default:emotionalIndex = '中性';
				}
				
				html += '<li><a target="_blank" href="'+douyindetail.source_url+'"><div class="over-newsbox"><div class="content-title">'
				+ '<div class="over-weibohottopictitle" title="'+douyindetail.topic+'" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;"><span class="content-logo" style="background: url(https://file.ipadown.com/tophub/assets/images/media/iesdouyin.com.png);"></span>'
				+ douyindetail.topic+'</div></div><div class="over-tips">'
				+ '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 抖音</span><span class="link f-right moodzm">'+emotionalIndex+'</span>'
				+ '<span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;"><i class="fas fa-fire" style="color: #fe475d;"> </i> '+douyindetail.original_weight+'</span>'
				+ '</div></div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
				
			}
		html +='</ul>'
			$("#douyin").html(html)
	}else{
		$("#douyin").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
	}
}

/**
 * 政策
 * @returns
 */
function policydata(){

	loading('#hot_policydata');
	if(!(!hot_policydata)){
		let douyin =JSON.parse(escape2Html(hot_policydata)) 
		let html='<ul>'
			for(let i = 0;i < douyin.length&&i<5; i++){
				let douyindetail = douyin[i]
				let emotionalIndex
				switch(douyindetail.emotionalIndex){
				case 1:emotionalIndex = '正面';break;
				case 2:emotionalIndex = '中性';break;
				case 3:emotionalIndex = '负面';break;
				default:emotionalIndex = '中性';
				}
				
				html += '<li><a target="_blank" href="'+douyindetail.source_url+'"><div class="over-newsbox"><div class="content-title">'
				+ '<div class="over-weibohottopictitle" title="'+douyindetail.topic+'" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;"><span class="content-logo" style="background: url(http://www.gov.cn/govweb/xhtml/2016gov/images/public/logo_wz1.jpg);"></span>'
				+ douyindetail.topic+'</div></div><div class="over-tips">'
				+ '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 国务院</span><span class="link f-right moodzm">'+emotionalIndex+'</span>'
//				+ '<span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;"><i class="fas fa-fire" style="color: #fe475d;"> </i> '+douyindetail.original_weight+'</span>'
				+ '</div></div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
				
			}
		html +='</ul>'
			$("#hot_policydata").html(html)
	}else{
		$("#hot_policydata").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
	}
}


/**
 * 经济
 * @returns
 */
function finaceData(){

	loading('#hot_finaceData');
	if(!(!hot_finaceData)){
		let douyin =JSON.parse(escape2Html(hot_finaceData)) 
		let html='<ul>'
			for(let i = 0;i < douyin.length&&i<5; i++){
				let douyindetail = douyin[i]
				let emotionalIndex
				switch(douyindetail.emotionalIndex){
				case 1:emotionalIndex = '正面';break;
				case 2:emotionalIndex = '中性';break;
				case 3:emotionalIndex = '负面';break;
				default:emotionalIndex = '中性';
				}
				
				html += '<li><a target="_blank" href="'+douyindetail.source_url+'"><div class="over-newsbox"><div class="content-title">'
				+ '<div class="over-weibohottopictitle" title="'+douyindetail.topic+'" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;"><span class="content-logo" style="background: url(https://g1.dfcfw.com/g3/201909/20190912110958.jpg);"></span>'
				+ douyindetail.topic+'</div></div><div class="over-tips">'
				+ '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 东方财富网</span><span class="link f-right moodzm">'+emotionalIndex+'</span>'
//				+ '<span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;"><i class="fas fa-fire" style="color: #fe475d;"> </i> '+douyindetail.original_weight+'</span>'
				+ '</div></div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
				
			}
		html +='</ul>'
			$("#hot_finaceData").html(html)
	}else{
		$("#hot_finaceData").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
	}
}

/**
 * 科技
 * @returns
 */
function scienceandtechnology(){

	loading('#hot_36kr');
	if(!(!hot_36kr)){
		let douyin =JSON.parse(escape2Html(hot_36kr)) 
		let html='<ul>'
			for(let i = 0;i < douyin.length&&i<5; i++){
				let douyindetail = douyin[i]
				let emotionalIndex
				switch(douyindetail.emotionalIndex){
				case 1:emotionalIndex = '正面';break;
				case 2:emotionalIndex = '中性';break;
				case 3:emotionalIndex = '负面';break;
				default:emotionalIndex = '中性';
				}
				
				html += '<li><a target="_blank" href="'+douyindetail.source_url+'"><div class="over-newsbox"><div class="content-title">'
				+ '<div class="over-weibohottopictitle" title="'+douyindetail.topic+'" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;"><span class="content-logo" style="background: url(https://file.ipadown.com/tophub/assets/images/media/36kr.com.png_50x50.png);"></span>'
				+ douyindetail.topic+'</div></div><div class="over-tips">'
				+ '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 36kr</span><span class="link f-right moodzm">'+emotionalIndex+'</span>'
//				+ '<span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;"><i class="fas fa-fire" style="color: #fe475d;"> </i> '+douyindetail.original_weight+'</span>'
				+ '</div></div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
				
			}
		html +='</ul>'
			$("#hot_36kr").html(html)
	}else{
		$("#hot_36kr").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
	}
}






/**
 * bilibili
 * @returns
 */
function bilibili(){

	loading('#bilibili')
	if(!(!hot_bilibili)){
		let bilibili =JSON.parse(escape2Html(hot_bilibili)) 
		let html='<ul>'
			for(let i = 0;i < bilibili.length&&i<5; i++){
				let bilibilidetail = bilibili[i]
				let emotionalIndex
				switch(bilibilidetail.emotionalIndex){
				case 1:emotionalIndex = '正面';break;
				case 2:emotionalIndex = '中性';break;
				case 3:emotionalIndex = '负面';break;
				default:emotionalIndex = '中性';
				}
				
				html += '<li><a target="_blank" href="'+bilibilidetail.source_url+'"><div class="over-newsbox"><div class="content-title">'
				+ '<div class="over-weibohottopictitle" title="'+bilibilidetail.topic+'" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;"><span class="content-logo" style="background: url(https://file.ipadown.com/tophub/assets/images/media/bilibili.com.png);"></span>'
				+ bilibilidetail.topic+'</div></div><div class="over-tips">'
				+ '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 bilibili</span><span class="link f-right moodzm">'+emotionalIndex+'</span>'
				+ '<span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;"><i class="fas fa-fire" style="color: #fe475d;"> </i> '+bilibilidetail.original_weight+'</span>'
				+ '</div></div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
				
			}
		html +='</ul>'
			$("#bilibili").html(html)
	}else{
		$("#bilibili").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
	}
}

/**
 * 腾讯视频
 * @returns
 */
function tecentvedio(){

	loading('#tecentvedio');
	if(!(!hot_tecentvedio)){
		let tecentvedio =JSON.parse(escape2Html(hot_tecentvedio)) 
		let html='<ul>'
			for(let i = 0;i < tecentvedio.length&&i<5; i++){
				let tecentvediodetail = tecentvedio[i]
				let emotionalIndex
				switch(tecentvediodetail.emotionalIndex){
				case 1:emotionalIndex = '正面';break;
				case 2:emotionalIndex = '中性';break;
				case 3:emotionalIndex = '负面';break;
				default:emotionalIndex = '中性';
				}
				
				html += '<li><a target="_blank" href="'+tecentvediodetail.source_url+'"><div class="over-newsbox"><div class="content-title">'
				+ '<div class="over-weibohottopictitle" title="'+tecentvediodetail.topic+'" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;"><span class="content-logo" style="background: url(https://file.ipadown.com/tophub/assets/images/media/v.qq.com.png);"></span>'
				+ tecentvediodetail.topic+'</div></div><div class="over-tips">'
				+ '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 腾讯视频</span><span class="link f-right moodzm">'+emotionalIndex+'</span>'
				+ '<span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;"><i class="fas fa-fire" style="color: #fe475d;"> </i> '+tecentvediodetail.original_weight+'</span>'
				+ '</div></div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
				
			}
		html +='</ul>'
			$("#tecentvedio").html(html)
	}else{
		$("#tecentvedio").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
	}
}


/**
 * 微信热点
 * @returns
 */
function wexin(){
	debugger;
	loading('#weixin');
	if(!(!hot_wechat)){
		let wechat =	JSON.parse(escape2Html(hot_wechat)) 
		let html='<ul>'
			for(let i = 0;i < wechat.length&&i<5; i++){
				let wechatdetail = wechat[i]
				let emotionalIndex
				switch(wechatdetail.emotionalIndex){
				case 1:emotionalIndex = '正面';break;
				case 2:emotionalIndex = '中性';break;
				case 3:emotionalIndex = '负面';break;
				default:emotionalIndex = '中性';
				}
				
				html += '<li><a target="_blank" href="'+wechatdetail.source_url+'"><div class="over-newsbox"><div class="content-title">'
				+ '<div class="over-weibohottopictitle" title="'+wechatdetail.topic+'" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;"><span class="content-logo" style="background: url(https://file.ipadown.com/tophub/assets/images/media/mp.weixin.qq.com.png_160x160.png);"></span>'
				+ wechatdetail.topic+'</div></div><div class="over-tips">'
				+ '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 微信</span><span class="link f-right moodzm">'+emotionalIndex+'</span>'
				+ '<span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;"><i class="fas fa-fire" style="color: #fe475d;"> </i> '+wechatdetail.original_weight+'</span>'
				+ '</div></div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
				
			}
		html +='</ul>'
			$("#weixin").html(html)
	}else{
		$("#weixin").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
	}
}



function allhot(){
	loading('#all-hot');
	if(!(!hot_All)){
		let hot_all =	JSON.parse(escape2Html(hot_All)) 
		let html='<ul>'
			for(let i = 0;i < hot_all.length&&i<5; i++){
				let hot = hot_all[i]
				let emotionalIndex
				switch(hot.emotionalIndex){
				case 1:emotionalIndex = '正面';break;
				case 2:emotionalIndex = '中性';break;
				case 3:emotionalIndex = '负面';break;
				default:emotionalIndex = '中性';
				}
				let source_name = hot.source_name
				
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
				
				html += '<li><a target="_blank" href="'+hot.source_url+'"><div class="over-newsbox"><div class="content-title">'
				+ '<div class="over-weibohottopictitle" title="'+hot.topic+'" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;"><span class="content-logo" style="background: url('+iconUrl+');"></span>'
				+ hot.topic+'</div></div><div class="over-tips">'
				+ '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 '+source_name+'</span><span class="link f-right moodzm">'+emotionalIndex+'</span>'
				+ '<span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;"><i class="fas fa-fire" style="color: #fe475d;"> </i> '+hot.original_weight+'</span>'
				+ '</div></div><i class="mdi mdi-chevron-right font-20"></i></a></li>'
				
			}
		html +='</ul>'
			
			
			$("#loading3").remove();
		
		$("#all-hot").html(html)
	}else{
		$("#all-hot").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
	}

}


function reportchange(type){
	let report
	if(type == 1){
		if(!(!report_day)){
			report=	JSON.parse(escape2Html(report_day))
		}
	}else{
		if(!(!report_week)){
			report=	JSON.parse(escape2Html(report_week))
		}
	}
	if(!(!report)){
		
		let html = "<ul>"
		let size = report.length
		if(size >4){
			size =4
		}
			for (let i = 0; i <size ; i++) {
				let reportdetail = report[i]
				let status
				switch(reportdetail.report_status){
				case 1:status = '正在编制';break;
				case 2:status = '编制成功';break;
				case 3:status = '编制失败';break;
				}
				// console.log(reportdetail.report_time/1)
				// console.log(formatDate(reportdetail.report_time/1))
				html +='<li><a target="_blank" href="'+ctx +'/report/'+reportdetail.report_id+'?groupid='+reportdetail.groupid+'&projectid='+reportdetail.projectid+'"><div class="over-newsbox"><div class="content-title"><div class="over-title">'
				+reportdetail.report_name+'</div></div><div class="over-tips"><span>'+timeParse(formatDate(reportdetail.report_time/1))+'</span>'
				+'<span><div><i class="mdi mdi-check-circle-outline fm"></i> '+status+'</div></span>'
				+'</div> </div> <i class="mdi mdi-chevron-right font-20"></i> </a> </li>'
			}
		html += "</ul>"
		$("#report").html(html)
	}else{
		$("#report").html("<h3 style='text-align: center;margin-top: 20%;'>暂无数据</h3>")
	}
}


function hotList(page){
	let data = new Object();
	data.source_name = '';
    data.pageNum = page;
    data.searchWord = '';
    data.classify = 4;
    data.pageSize = 5;
    data.timeType = 1;
	ajax('GET',ctx + '/fullsearch/hotList', data, installHot);
	
	
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



function installHot(res){
	let code = res.code 
	if(code == 200){
		$("#loading3").remove();
		//all-hot
//		res.data
		
		let data = res.data
        let html = 	    "<ul>\r\n" 
        if (data.length > 0) {
        	for (let i = 0; i < data.length; i++) {
        		let source = data[i]._source
        		let source_name = source.source_name
        		let title = source.topic
        		let publish_time = source.spider_time
				if(!publish_time){
					publish_time = source.spider_time
				}
        		let original_weight = source.original_weight
        		let sales_volume = ''
                if (source.sentiment == 1) {
                	sales_volume = '<span class="link f-right moodzm">正面</span>'
                } else if (source.sentiment == 2) {
                	sales_volume = '<span class="link f-right moodzx">中性</span>'
                } else if (source.sentiment == 3) {
                	sales_volume = '<span class="link f-right moodfm">负面</span>'
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
        		
//        		let html = '<div class="line-info">'
//						   + '	<div class="title"><span class="content-logo" style="background: url('+iconUrl+');"></span><a target="_blank" href="'+source.source_url+'">'+title+'</a></div>'
//						   + '	<div class="hot-tips">'
//						   + '		<div class="left">'
//						   + '			<span><svg class="icon"><use xlink:href="#icon-news"></use></svg> '+source_name+'</span>'
//						   + '			<span><svg class="icon"><use xlink:href="#icon-datatime"></use></svg> '+timeParse(publish_time)+'</span>'
//						   + '		</div>'
//						   + sales_volume
//						   + '	</div>'
//						   + '</div>'
						   html += "    <li>\r\n" + 
			    "        <a target='_blank' th:href=\""+source.source_url+"\">\r\n" + 
			    "            <div class=\"over-newsbox\">\r\n" + 
			    "                <div class=\"content-title\">\r\n" + 
			    "                    <div class=\"over-weibohottopictitle\"><span class=\"content-logo\" style=\"background: url("+iconUrl+");\"></span>"+title+"</div>\r\n" + 
			    "                </div>\r\n" + 
			    "                <div class=\"over-tips\">\r\n" + 
			    "                    <span class=\"link m-r-10\"> <i class=\"mdi mdi-earth \"></i> 来源 "+source_name+"</span>\r\n" + 
			    "                    "+sales_volume+"\r\n" + 
			    "                    <span class=\"link f-right moodfm\" style=\"color: #9ca8b3;margin: 0;\"><i class=\"fas fa-fire\" style=\"color: #fe475d;\"> </i>  "+original_weight+"</span>\r\n" + 
			    "\r\n" + 
			    "                </div>\r\n" + 
			    "            </div>\r\n" + 
			    "            <i class=\"mdi mdi-chevron-right font-20\"></i>\r\n" + 
			    "        </a>\r\n" + 
			    "    </li>\r\n" 
        	}
        }
	    html += "</ul>"
	    	$("#all-hot").html(html)
	}
}



/**
 * 计算百分比
 * @param   {number} num   分子
 * @param   {number} total 分母
 * @returns {number} 返回数百分比
 */
function Percentage(num, total) { 
    if (num == 0 || total == 0){
        return 0;
    }
    return (Math.round(num / total * 10000) / 100.00);// 小数点后两位百分比
}


function formatDate(date) {
	  var date = new Date(date);
	  var YY = date.getFullYear() + '-';
	  var MM = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
	  var DD = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate());
	  var hh = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
	  var mm = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
	  var ss = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
	  return YY + MM + DD +" "+hh + mm + ss;
	}