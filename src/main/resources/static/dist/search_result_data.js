if (menuStyle == 0) {
	listFullTypeOneByIdList()
	$(".custom-box-info").css("padding", "20px 20px 0 20px")
	$(".p-20").css("padding-top", "125px")
	$("#search-tab").show()
} else {
	$(".custom-box-info").css("padding", "25px")
	$(".p-20").css("padding-top", "110px")
	$("#search-tab").hide()
}

// 聚合模式下 搜索框下面菜单
function listFullTypeOneByIdList() {
	var data = {
		id : idList
	}
	console.log(idList)
	ajax('GET', ctx + 'fullsearch/listFullTypeOneByIdList', data,
			setFullTypeOne);
}

/**
 * 左侧菜单 点击
 * 
 * @param e
 * @returns
 */
$('body').on(
		'click',
		'#sidebarnav li',
		function(e) {
			if (!$(this).hasClass("comactive")) {
				$(this).siblings().removeClass("comactive")
				$(this).addClass("comactive")
				if (menuStyle == 0) {
					let arry = $(this).attr("data-id").split(',');
					// full_type = arry[0]
					idList = $(this).attr("data-id")
					full_poly = $(this).attr("data-poly")
					if (full_poly == 1) {
						$('#searchWord').attr("placeholder",
								"请输入竞争对手企业名称、企业简称、产品名、行业名称、人名等，多关键词用空格隔开")
					}
					if (full_poly == 2) {
						$('#searchWord').attr("placeholder",
								"请输入领域范围、行业名称、人名等，多关键词用空格隔开")
					}
					if (full_poly == 3) {
						$('#searchWord').attr("placeholder",
								"请输入政策法规、关注产品、行业名称、人名等，多关键词用空格隔开")
					}
					if (full_poly == 4) {
						$('#searchWord').attr("placeholder",
								"请输入产业名称、企业名称、企业简称等，多关键词用空格隔开")
					}
					if (full_poly == 5) {
						$('#searchWord').attr("placeholder",
								"请输入产品名称、品牌名称、企业简称等，多关键词用空格隔开")
					}
					if (full_poly == 6) {
						$('#searchWord').attr("placeholder",
								"请输入技术名称、专家名称、企业简称等，多关键词用空格隔开")
					}
					listFullTypeOneByIdList()
				} else {
					full_type = $(this).attr("data-id")
				}
				initdata(full_type)
			}
		})

/**
 * 聚合模式下 li 菜单
 * 
 * @param e
 * @returns
 */
$('body').on('click', '#search-tab li', function(e) {
	// if (!$(this).hasClass("create-tab-act")) {
	$(this).siblings().removeClass("create-tab-act")
	$(this).addClass("create-tab-act")
	full_type = $(this).attr("data-id")
	onlyid = $(this).attr("data-onlyid")
	initdata(full_type)
	// }
});

/**
 * ajax 同步
 * 
 * @param type
 * @param url
 * @param data
 * @param funcname
 * @returns
 */
function ajax(type, url, data, funcname) {
	$.ajax({
		type : type,
		url : url,
		dataType : 'json',
		data : data,
		async : false,
		contentType : 'application/json;charset=utf-8',
		success : function(res) {
			funcname(res);
		},
		error : function(xhr, ajaxOptions, thrownError) {
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
 * 装载 一级分类
 * 
 * @param data
 * @returns
 */
function setFullTypeOne(data) {
	$('#search-tab').html('')
	// if(!full_type){
	full_type = data[0].id
	onlyid = data[0].only_id
	// }
	for (var i = 0; i < data.length; i++) {
		var htmlStr = ''
		if (full_type == data[i].id) {
			htmlStr = '<li class="create-tab-act" data-onlyid="'
					+ data[i].only_id + '" data-id="' + data[i].id
					+ '"><i class="' + data[i].icon + '"></i> ' + data[i].name
					+ '</li>'
		} else {
			htmlStr = '<li class="" data-onlyid="' + data[i].only_id
					+ '" data-id="' + data[i].id + '"><i class="'
					+ data[i].icon + '"></i> ' + data[i].name + '</li>'
		}
		$('#search-tab').append(htmlStr)
	}
}

/**
 * 数据来源数据装载
 * 
 * @param data
 * @returns
 */
function setFirstBox(data) {
	$('#classifybox').html("")
	for (var i = 0; i < data.length; i++) {
		var htmlStr = '<span data-id="' + data[i].id + '" data-classify="'
				+ data[i].value + '" class="badge badge-pill badge-light">'
				+ data[i].name + '</span>'
		if (i == 0) {
			htmlStr = '<span data-id="' + data[i].id + '" data-classify="'
					+ data[i].value + '" class="badge badge-pill badge-info">'
					+ data[i].name + '</span>'
		}
		$('#classifybox').append(htmlStr)
	}
}

/**
 * 来源网站数据装载
 * 
 * @param data
 * @returns
 */
function setThirdBox(data) {
	$('#sourceName-select').html("")
	var htmlStr = '<option value="">全部</option>'
	$('#sourceName-select').append(htmlStr)
	for (var i = 0; i < data.length; i++) {
		if (full_type == 8) {
			htmlStr = '<option value="' + data[i].value + '" data-icon="'
					+ data[i].icon + '">' + data[i].name + '</option>'
		} else {
			htmlStr = '<option value="' + data[i].value + '">' + data[i].name
					+ '</option>'
		}
		$('#sourceName-select').append(htmlStr)
	}
}

/**
 * 搜索设置
 * 
 * @param params
 * @returns
 */
function searchSetting(params) {
	let styleStr = ''
	if (menuStyle == 0) {
		styleStr = '<span data-style=0 class="badge badge-pill badge-info">聚合模式</span>'
				+ '<span data-style=1 class="badge badge-pill badge-light">展开模式</span>'
	} else {
		styleStr = '<span data-style=0 class="badge badge-pill badge-light">聚合模式</span>'
				+ '<span data-style=1 class="badge badge-pill badge-info">展开模式</span>'
	}
	let pageSizeStr = "";
	if (pageSize == 25) {
		pageSizeStr = '<span data-pageSize=25 class="badge badge-pill badge-info">25</span>'
				+ '<span data-pageSize=50 class="badge badge-pill badge-light">50</span>'
				+ '<span data-pageSize=100 class="badge badge-pill badge-light">100</span>'
	}
	if (pageSize == 50) {
		pageSizeStr = '<span data-pageSize=25 class="badge badge-pill badge-light">25</span>'
				+ '<span data-pageSize=50 class="badge badge-pill badge-info">50</span>'
				+ '<span data-pageSize=100 class="badge badge-pill badge-light">100</span>'
	}
	if (pageSize == 100) {
		pageSizeStr = '<span data-pageSize=25 class="badge badge-pill badge-light">25</span>'
				+ '<span data-pageSize=50 class="badge badge-pill badge-light">50</span>'
				+ '<span data-pageSize=100 class="badge badge-pill badge-info">100</span>'
	}

	var create = '<div class="shadebox" id="createmodel">'
			+ '    <div class="modal-dialog" role="document">'
			+ '        <div class="modal-content">'
			+ '            <div class="modal-header align-flexend" style="border:none">'
			+ '                <h5 class="modal-title"><i class="ti-marker-alt m-r-10"></i>搜索设置</h5>'
			+ '                <i class="mdi mdi-close-circle-outline font-18 cursor-po" id="closethis"></i>'
			+ '            </div>'
			+ '            <div class="modal-body">'
			+ '			 <div style="display: flex; align-items: center;">'
			+ '				<div class="card-title filter-title">菜单样式:</div>'
			+ '				<div class="inline-list" id="menu-style" style="margin-left: 15px;">'
			+ styleStr
			+ '				</div>'
			+ '			</div>'
			+ '			 <div style="display: flex; align-items: center;">'
			+ '				<div class="card-title filter-title">每页条目:</div>'
			+ '				<div class="inline-list" id="pageSize-style" style="margin-left: 15px;">'
			+ pageSizeStr
			+ '				</div>'
			+ '			</div>'
			+ '			 <div style="display: flex; align-items: center;">'
			+ '				<div class="card-title filter-title">是否打开新网页:</div>'
			+ '				<div class="inline-list" id="" style="margin-left: 15px;">'
			+ '					<span data-id="2" data-classify="3,6,7,8" class="badge badge-pill badge-info">是</span>'
			+ '					<span data-id="3" data-classify="8" class="badge badge-pill badge-light">否</span>'
			+ '				</div>'
			+ '			</div>'
			+ '            </div>'
			+ '            <div class="modal-footer" style="border:none">'
			+ '                <button type="button" class="btn btn-info" id="confirm"> 确定</button>'
			+ '                <button type="button" class="btn btn-secondary" id="cancel">取消</button>'
			+ '             </div>'
			+ '        </div>'
			+ '    </div>'
			+ '</div>'

	$("body").append(create)
	$("#closethis").click(function(param) {
		$("#createmodel").remove()
	})
	$("#cancel").click(function(param) {
		$("#createmodel").remove()
	})
	$("#confirm").click(
			function() {
				let params = ""
				if (menuStyle == 0) {
					params = "?" + "searchword=" + $('#searchWord').val()
							+ "&menuStyle=" + menuStyle + "&full_poly="
							+ full_poly + "&fulltype=" + full_type
							+ "&pageSize=" + pageSize + "&page=1" + "&only_id=" + onlyid;
				} else {
					params = "?" + "searchword=" + $('#searchWord').val()
							+ "&menuStyle=" + menuStyle + "&fulltype="
							+ full_type + "&pageSize=" + pageSize + "&page=1";
				}
				window.location.href = ctx + 'fullsearch/result' + params;
			})
}

/**
 * contentType: 'application/json;charset=utf-8',
 * 
 * @param param
 *            ajax请求基本参数
 * @param data
 *            用户传的参数
 * @param funcname
 *            方法名称
 * @description 请求列表数据
 */

function sendArticle(param, data, funcname) {
	$("#page").html("");
	$.ajax({
		type : param.type,
		url : param.url,
		dataType : 'json',
		data : data,
		contentType : param.contentType,
		beforeSend : function() {
			loading("#monitor-content")
		},
		success : function(res) {
			funcname(res);
		},
		error : function(xhr, ajaxOptions, thrownError) {
			if (xhr.status == 403) {
				window.location.href = ctxPath + "login";
			} else {
				$("#page").html("");
				dataerror("#monitor-content");
			}
		}
	});
}

/**
 * 组装律师数据
 * @param res
 * @returns
 */
function installLawyer(res){
	if(res.code==="200"){
		let data=res.list
		let totalCount=res.totalData
		let totalPage=res.totalPage
		let currentPage=res.currentPage
		if (totalCount > 5000) {
//			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if(data.length>0){
			pageHelper(currentPage, totalPage);
			$("#monitor-content").html('');
			for(let i=0;i<data.length;i++){
				let list1=data[i]
				 var curWwwPath=window.document.location.href;

				  //获取主机地址之后的目录，如： myproj/view/my.jsp

				  var pathName=window.document.location.pathname;

				  var pos=curWwwPath.indexOf(pathName);

				  //获取主机地址，如： http://localhost:8083

				  var localhostPaht=curWwwPath.substring(0,pos);
				let detailUrl = localhostPaht+'/fullsearch/lawyerDetail/'
					+ list1.article_public_id + '?fulltype=' + full_type
					+ '&menuStyle=' + menuStyle + '&fullpoly=' + full_poly
					+ '&onlyid=' + onlyid + '&searchWord='
					+ $('#searchWord').val()
				let html='<div class="feed-activity-list" id="list">'+
							'<div class="feed-element">'+
							'<div class="search-result" style="display: flex;">'+
							'<div style="float:left;min-height: 80px;">'+
								'<p><img style="width: 80px;" src="../assets/images/doctor.jpg"></p>'+
							'</div>'+
							'<div style="float: left;padding:0 0 0 20px;">'+
								'<h3 style="margin-bottom: 10px;font-size:16px;">'+
								'<a target="_blank" href="'+detailUrl+'">'+list1.name+'</a></h3>'+
								'<p style="font-size:14px;">'+
								'<span style="">手机号：  '+list1.telephone+'</span>'+
									'<span style="margin-left: 30px;">职位：  '+list1.kinds+'</span>'+
									'<span style="margin-left: 30px;">擅长：'+list1.goods+' </span>'+
									'<span style="margin-left: 30px;">学历：  '+list1.educationbackground+'</span>'+
									
								'</p>'+
								'<p style="font-size:14px;">'+
									'<span>邮件：  '+list1.email+' </span>'+
									'<span style="margin-left: 30px;">认证号：'+list1.certID+' </span>'+
									'<span style="margin-left: 30px;">任职时间：  '+list1.qualifitime.substring(0,10)+'</span>'+
								'</p>'+
								'<p style="font-size:14px;">'+
								'<span style="margin-left: 0px;">律所名称： '+list1.lawfirm+'</span>'+
								'<span style="margin-left: 30px;">律所地址：  '+list1.address+'</span>'+
								'<span style="margin-left: 30px;">城市：'+list1.city+' </span>'+
									
								'</p>'+
							'</div>'+
							'</div>'+
							'</div>'+
						 '</div>'+
						 '<hr>'
				$('#monitor-content').append(html)
			}
		}else{
			$("#page").html("");
			$('#monitor-content').html('<div class="feed-element">暂无数据！</div>')
		}
	}else{
		$("#page").html("");
		dataerror("#monitor-content");
	}
}

/**
 * 组装被执行人数据
 * @param res
 * @returns
 */
function installExecutionPerson(res){
	if(res.code==="200"){
		let data=res.list
		let totalCount=res.totalData
		let totalPage=res.totalPage
		let currentPage=res.currentPage
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if(data.length>0){
			pageHelper(currentPage, totalPage);
			$("#monitor-content").html('');
			for(let i=0;i<data.length;i++){
				let list1=data[i]
				var curWwwPath=window.document.location.href;

				  //获取主机地址之后的目录，如： myproj/view/my.jsp

				  var pathName=window.document.location.pathname;

				  var pos=curWwwPath.indexOf(pathName);

				  //获取主机地址，如： http://localhost:8083

				  var localhostPaht=curWwwPath.substring(0,pos);
				let detailUrl = localhostPaht+'/fullsearch/executionPersonDetail/'
					+ list1.article_public_id + '?fulltype=' + full_type
					+ '&menuStyle=' + menuStyle + '&fullpoly=' + full_poly
					+ '&onlyid=' + onlyid + '&searchWord='
					+ $('#searchWord').val()
				let html='<div class="feed-activity-list" id="list">'+
								'<div class="feed-element">'+
				                '<div class="search-result">'+
				                		'<h3 style="font-size:16px;"><a target="_blank" href="'+detailUrl+'">'+list1.iname+'</a></h3>'+
				                		'<br />'+
				                		
				                		'<p style="font-size:14px;">'+
				                			'<span style="margin-right: 30px;">做出执行单位: '+list1.gistUnit+'</span>'+
				                			'<span style="margin-right: 30px;">证件号: '+list1.cardNum+'</span>'+
				                			'<span style="margin-right: 30px;">类型: '+list1.type+'</span>'+
				                		'</p>'+
				                		'<p style="font-size:14px;">'+
				                			'<span style="margin-right: 30px;">案件编号: '+list1.caseCode+'</span>'+
				                			'<span style="margin-right: 30px;">执行依据文号: '+list1.gistId+'</span>'+
				                			'<span style="margin-right: 30px;">最新区域: '+list1.areaNameNew+'</span>'+
				                			'<span style="margin-right: 30px;">法院名称: '+list1.courtName+'</span>'+
				                		'</p>'+
				                		'<p style="font-size:14px;">职责: '+ list1.duty+'</p>'+
				                		'<p style="font-size:14px;">'+
				                			'<span style="margin-right: 30px;">履行情况: '+list1.performance+'</span>'+
				                			'<span style="margin-right: 30px;">行为: '+list1.disruptTypeName+'</span>'+
				                		'</p>'+
				                '</div>'+
				            ' </div>'+
						 '</div>'+
						 '<hr>'
				$('#monitor-content').append(html)
			}
		}else{
			$("#page").html("");
			$('#monitor-content').html('<div class="feed-element">暂无数据！</div>')
		}
	}else{
		$("#page").html("");
		dataerror("#monitor-content");
	}
}

/**
 * 组装专家人才数据
 */
function installProfessor(res){
	if(res.code==="200"){
		let data=res.list
		let totalCount=res.totalData
		let totalPage=res.totalPage
		let currentPage=res.currentPage
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if(data.length>0){
			pageHelper(currentPage, totalPage);
			$("#monitor-content").html('');
			for(let i=0;i<data.length;i++){
				let list1=data[i]
				var curWwwPath=window.document.location.href;

				  //获取主机地址之后的目录，如： myproj/view/my.jsp

				  var pathName=window.document.location.pathname;

				  var pos=curWwwPath.indexOf(pathName);

				  //获取主机地址，如： http://localhost:8083

				  var localhostPaht=curWwwPath.substring(0,pos);
				let detailUrl = localhostPaht+'/fullsearch/professorDetail/'
					+ list1.article_public_id + '?fulltype=' + full_type
					+ '&menuStyle=' + menuStyle + '&fullpoly=' + full_poly
					+ '&onlyid=' + onlyid + '&searchWord='
					+ $('#searchWord').val()
				let arr=JSON.parse(list1.field)
				let fields=new String()
				for(let j=0;j<arr.length;j++){
					fields+=arr[j]+"  "
				}
				let html = '<div class="feed-element">'+
			                           '<div class="search-result">'+
			                           	'<div style="display:flex;min-height: 140px;align-items: center;justify-content: space-between">'+
			                           	'<div style="width:140px;height:164px;overflow:hidden;display:flex;align-items:center;border-radius:3px;">'+
 			                           	'<img style="height:100%;margin:0 auto;" src="'+list1.avatar+'" onerror=src="../assets/images/doctor.png">'+
			                           	'</div>'+
			                           	'<div style="width:calc(100% - 160px)">'+
					                   		'<h3 style="font-size:16px;"><a target="_blank" href="'+detailUrl+'">'+list1.title+'</a></h3>'+
					                   		'<br />'+
					                   		// '<p>'+content+'</p>'+
					                   		// '</p>'+
					                   		'<p style="font-size:14px;">'+
					                   			'<span>来源: '+list1.source_name+'</span>'+
					                   			'<span style="margin-left: 30px;">研究机构: '+list1.institution+'</span>'+
												'<span style="margin-left: 30px;">研究领域: '+fields+'</span>'+
					                   		'</p>'+
					                   		'<p style="font-size:14px;">'+
					                   			'<span>发表文章数量: '+list1.works+'</span>'+
					                   			'<span style="margin-left: 30px;">被引频次: '+list1.times_cited+'</span>'+
					                   		'</p>'+
					                   	'</div>'+
					               ' </div>'+
					               '<hr>'
				$('#monitor-content').append(html)
			}
		}else{
			$("#page").html("");
			$('#monitor-content').html('<div class="feed-element">暂无数据！</div>')
		}
	}else{
		$("#page").html("");
		dataerror("#monitor-content");
	}
}

/**
 * 组装医生数据
 */
function installDoctor(res){
	if(res.code==="200"){
		let data=res.list
		let totalCount=res.totalData
		let totalPage=res.totalPage
		let currentPage=res.currentPage
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if(data.length>0){
			pageHelper(currentPage, totalPage);
			$("#monitor-content").html('');
			for(let i=0;i<data.length;i++){
				let list1=data[i]
				var curWwwPath=window.document.location.href;

				  //获取主机地址之后的目录，如： myproj/view/my.jsp

				  var pathName=window.document.location.pathname;

				  var pos=curWwwPath.indexOf(pathName);

				  //获取主机地址，如： http://localhost:8083

				  var localhostPaht=curWwwPath.substring(0,pos);
				let detailUrl = localhostPaht+'/fullsearch/doctorDetail/'
					+ list1.article_public_id + '?fulltype=' + full_type
					+ '&menuStyle=' + menuStyle + '&fullpoly=' + full_poly
					+ '&onlyid=' + onlyid + '&searchWord='
					+ $('#searchWord').val()
				var adept = list1.adept;
        		if (adept && adept.length > 100) {
        			adept = adept.substring(0, 100) + '......';
				}
        		var content = list1.content;
        		if (content && content.length > 100) {
        			content = content.substring(0, 100) + '......';
        		}
				let html = 
			 				"<div class='custom-article b-b'>"+
			 			    "    <div class='article-search-result'>"+
			 			    "    <div class='row my-3'>"+
			 			    "    <div class='col-lg-2'>"+
			 			    "        <div class='article-imgbox'>"+
			 			    "           <img style='width:100px;height:100%;margin:0 auto;' src='"+list1.profile+"' onerror=src='../assets/images/doctor.png'>"+
			 			    "        </div>"+
			 			    "    </div>"+
			 			   "    <div class='col-lg-10'>"+
			 			    "        <div class='article-content'>"+
			 			    "        	<div class='article-content-title' style='font-size:16px;'>"+
			 			    "          		<a href='"+detailUrl+"' target='_blank'><b>" + list1.name + "</b></a>"+
			 			    "            </div>"+
			 				"            <div class='article-content-con'> " + 
			 				'<div style="font-size:14px;">'+
                   			'<span style="margin-right: 30px;">所属医院: '+list1.hospital+'</span>'+
                   			'<span style="margin-right: 30px;">所属科室: '+list1.department+'</span>'+
                   			'<span style="margin-right: 30px;">居住地: '+list1.province+'  '+list1.city+'  '+list1.area+'</span>'+
                   			'<span style="margin-right: 30px;">学位: '+list1.degree+'</span>'+
                   			'<span style="margin-right: 30px;">论文发表: '+list1.paper+'</span>'+
                   			'<span style="margin-right: 30px;">电话: '+list1.phone_number+'</span>'+
                   			'<span style="margin-right: 30px;">工作地址: '+list1.location+'</span>'+
                   			'</div>'+
                   			'<div style="font-size:14px;">擅长: '+adept+'</div>'+
	                   		'<div style="font-size:14px;">简介: '+content+'</div>'+
	                   		"    </div>"+
			 				"			</div>"+
			 				"    	</div>"+
			 				"    </div>"+
			 				"<hr>"
				$('#monitor-content').append(html)
			}
		}else{
			$("#page").html("");
			$('#monitor-content').html('<div class="feed-element">暂无数据！</div>')
		}
	}else{
		$("#page").html("");
		dataerror("#monitor-content");
	}
}

/**
 * 组装热点数据
 * 
 * @param res
 * @returns
 */
function installHot(res) {
	debugger;
	let code = res.code;
	if (code == 200) {
		let data = res.data
		let totalPage = res.page_count;
		let totalCount = res.count;
		let currentPage = res.page;
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if (data.length > 0) {
			pageHelper(currentPage, totalPage);
			$("#monitor-content").html('');
			for (let i = 0; i < data.length; i++) {
				let source = data[i]._source
				let image_url = source.image_url
				let imageUrlStr = ''
				// if(image_url != null){
				// image_url = JSON.parse(image_url)
				// image_url = image_url[0]
				// imageUrlStr = ' <div class="wb-left-imgbox">'
				// +' <img src="'+ image_url +'" class="img-cover"alt=""></div>'
				// }else{
				// imageUrlStr = ""
				// }

				var content = ''
				let source_name = source.source_name
				// 判断，不同来源，显示的值也不同
				if (source.content) {
					content = source.content;
				}
				var publish_time = source.publish_time
				if (!publish_time) {
					publish_time = source.spider_time
				}

				// 获取三个关键字，使用JSON.parse前，需要判断
				let keyWords = ''
				if (source.key_words) {
					var keys = JSON.parse(source.key_words);
					var count = 0;
					var key_word = [];
					for ( var key in keys) {
						key_word.push(key)
						count++;
						if (count == 3) {
							break;
						}
					}
					if (key_word.length == 0) {
						key_word = "无";
					} else {
						keyWords = '        <span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 关键词 '
								+ key_word + '</span>'
					}
				} else {
					key_word = "无";
				}
				let strEmotion = '';
				if (source.sentiment == 1) {
					strEmotion = '<span class="link f-right moodzm">正面</span>';
				} else if (source.sentiment == 2) {
					strEmotion = '<span class="link f-right moodzx">中性</span>';
				} else if (source.sentiment == 3) {
					strEmotion = '<span class="link f-right moodfm">负面</span>';
				}

				let sales_volume = ''
				if (source.classify == 3) {
					if (source.sales_volume) {
						sales_volume = '<span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;">销量：'
								+ source.sales_volume + '</span>'
					}
				}
				if (source.classify == 4 || source.classify == 2) {
					if (source.original_weight) {
						sales_volume = '<span class="link f-right moodfm" style="color: #9ca8b3;margin: 0;"> <i class="fas fa-fire" style="color: #fe475d;"> </i>  '
								+ source.original_weight + '</span>'
					}
				}

				let iconUrl = ''
				if (source_name == '网易') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/163.com.png'
				} else if (source_name == '36kr') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/36kr.com.png'
				} else if (source_name == '好奇心研究所') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/qdaily.com.png'
				} else if (source_name == '腾讯网') {
					iconUrl = 'https://mat1.gtimg.com/www/icon/favicon2.ico'
				} else if (source_name == '新浪') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/rank.sinanews.sina.cn.png'
				} else if (source_name == '抖音') {
					iconUrl = 'http://wx.qlogo.cn/mmhead/Q3auHgzwzM5veqh0jlpkNK7iaVic8T1icPATdlKB1eVZLVjbxiaibPP3I5A/132'
				} else if (source_name == '小红书') {
					iconUrl = 'http://wx.qlogo.cn/mmhead/Q3auHgzwzM6uD2n9QGUSN1PFY34un3ht0l9EBwfkrvkd3ov6paw1pg/132'
				} else if (source_name == '京东') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/jd.com.png'
				} else if (source_name == '淘宝') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/izhongchou.taobao.com.png'
				} else if (source_name == '什么值得买') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/smzdm.com.png'
				} else if (source_name == '微博') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/s.weibo.com.png'
				} else if (source_name == '知乎') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/zhihu.com.png'
				} else if (source_name == '百度风云榜') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/baidu.com.png'
				} else if (source_name == '360热榜') {
					iconUrl = 'https://s2.ssl.qhres.com/static/121a1737750aa53d.ico'
				} else if (source_name == '搜狗热点') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/sogou.com.png'
				} else if (source_name == '搜狗头图') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/sogou.com.png'
				} else if (source_name == '百度') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/baidu.com.png'
				} else if (source_name == '微信') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/mp.weixin.qq.com.png'
				} else if (source_name == '网易') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/163.com.png'
				} else if (source_name == '头条搜索') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/m.toutiao.com.png'
				} else if (source_name == '今日头条') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/toutiao.com.png'
				} else if (source_name == '豆瓣') {
					iconUrl = 'http://wx.qlogo.cn/mmhead/Q3auHgzwzM6pN8kDEzvUQxKRIzAkVdaxd5tRoEtyMztib8cbXFA76tA/132'
				} else if (source_name == '哔哩哔哩') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/bilibili.com.png'
				} else if (source_name == '爱奇艺') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/iqiyi.com.png'
				} else if (source_name == '腾讯视频') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/v.qq.com.png'
				} else if (source_name == '优酷视频') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/so.youku.com.png'
				} else if (source_name == '拼多多') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/p.pinduoduo.com.png'
				} else if (source_name == '澎湃新闻') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/thepaper.cn.png'
				} else if (source_name == 'ZAKER') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/myzaker.com.png'
				} else if (source_name == '中国政府网') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/gov.cn.png'
				} else if (source_name == 'CCTV央视新闻') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/news.cctv.com.png'
				} else if (source_name == '微信热词') {
					iconUrl = 'https://file.ipadown.com/tophub/assets/images/media/weixin.sogou.com.png'
				}

				let html = '<div class="wb-content just-bet">'
						+ '<div class="monitor-check"><input type="checkbox" id="check1"><span></span></div>'
						+ '<div class="monitor-right">'
						+ '   <div class="monitor-content-title">'
						+ '       <a target="_blank" href="'
						+ source.source_url
						+ '" class="link font-bold">'
						+ '           <span class="content-logo"style="background: url('
						+ iconUrl
						+ ');"></span>'
						+ source.topic
						+ '       </a>'
						+ '        <span class="sl-date  ">'
						+ timeParse(publish_time)
						+ ' </span>'
						+ '   </div>'
						+ '   <div class="wb-content-imgbox">'
						+ imageUrlStr
						+ '        <div class="wb-right-content">'
						+ '            <div class="monitor-content-con font-13">'
						+ content
						+ '</div>'
						+ '        </div>'
						+ '    </div>'
						+ '    <div class="like-comm m-t-10 font-13">'
						+ '       <span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 '
						+ source.source_name
						+ '</span>'
						+ keyWords
						+ strEmotion
						+ sales_volume
						+ '   </div>'
						+ '</div>'
						+ '</div>' + '<hr/>'
				$("#monitor-content").append(html);
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	} else {
		$("#page").html("");
		dataerror("#monitor-content");
	}
}

/**
 * 组装投诉数据
 * 
 * @param res
 * @returns
 */
function installComplaint(res) {
	let code = res.code;
	if (code == 200) {
		let data = res.news
		let totalPage = res.page_count
		let totalCount = res.count
		let currentPage = res.page
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if (data.length > 0) {
			pageHelper(currentPage, totalPage);
			$("#monitor-content").html('');
			if (res.classify == 's') {
				for (var i = 0; i < data.length; i++) {
					var letter_content = data[i]._source.letter_content;
					if (letter_content.length - 300 > 0) {
						letter_content = letter_content.substring(0, 300)
								+ '......'
					}

					var reply_content = data[i]._source.reply_content;
					if (reply_content.length - 300 > 0) {
						reply_content = reply_content.substring(0, 300)
								+ '......'
					}
					var writer = data[i]._source.writer;
					if (writer == "") {
						writer = '匿名';
					}
					var reply = '<div class="reply" style="width:100%">'
							+ '<p>'
							+ '<span style="margin-right: 10px;">'
							+ '<img src="http://www.beijing.gov.cn/hudong/hdjl/web/base/bootstrap/images/icon3.png" width="41" height="41">'
							+ '</span>'
							+ '<a target="_Blank" class="link font-bold" href="'
							+ data[i]._source.detailUrl + '">[官方回答]:'
							+ data[i]._source.reply_source + '</a>'
							+ '<span style="margin-left: 30px;">答复时间:'
							+ data[i]._source.reply_time + '</span>' + '</p>'
							+ '<p>' + '<span style="margin-right: 30px;">'
							+ reply_content + '</span>' + '</p>' + '</div>';

					var release_time = data[i]._source.release_time;
					release_time = release_time.substr(0, 16);
					var html = '<div class="col" style="width:100%;margin-left: -10px;">'
							+ '<p>'
							+ '<span style="margin-right: 10px;">'
							+ '<img src="http://www.beijing.gov.cn/hudong/hdjl/web/base/bootstrap/images/icon2.png" width="41" height="41">'
							+ '</span>'
							+ '<a target="_Blank" class="link font-bold" href="'
							+ data[i]._source.detailUrl
							+ '">'
							+ data[i]._source.title
							+ '</a>'
							+ '</p>'
							+ '<p>'
							+ '<span style="margin-right: 30px;">来信人:'
							+ writer
							+ '</span>'
							+ '<span style="margin-right: 30px;">来信时间:'
							+ release_time
							+ '</span>'
							+ '<span style="margin-right: 30px;">来源:'
							+ data[i]._source.sourceName
							+ '</span>'
							+ '</p>'
							+ '<p>'
							+ '<span style="margin-right: 30px;">'
							+ letter_content
							+ '</span>'
							+ '</p>'
							+ '</div>'
							+ reply + '<hr class="stylehr">';

					$('#monitor-content').append(html)
				}
			} else {
				for (var i = 0; i < data.length; i++) {
					var content = data[i]._source.content;
					var answerHtml = "";
					if (data[i]._source.process != "") {
						var imghtml = "";
						var answerArray = JSON.parse(data[i]._source.process);
						for (var n = 0; n < answerArray.length; n++) {
							var answer = answerArray[n];
							var answerContent = answer.content;
							answerHtml = answerHtml
									+ ' <div class="search-answer"; style="margin-left:50px;">'
									+ '<span style="margin-right: 20px;"><img style="width:30px; height:30px; border-radius:50%; " src="'
									+ answer.uimg
									+ '"></span>'
									+ '<span style="margin-right: 20px;"><a target="_blank" href="">'
									+ answer.uname
									+ '</a></span>'
									+ '<span style="margin-right: 20px;">回复时间：'
									+ answer.date
									+ '</span>'
									+ '<div style="margin:0px 0px 5px 50px;">回复内容：'
									+ answer.content + '</div>' + '</div> 	'
						}
						if (answerArray.length == 0) {
							answerHtml = '<div class="search-answer">暂无回复！</div>'
						}
					}
					var s = "";
					if (i == 0) {
						s = '<div class="feed-element" style="padding-bottom:0px;margin-bottom:5px">'
					} else {
						s = '<div class="feed-element" style="padding-bottom:0px;margin-bottom:5px;margin-top:30px">'
					}
					var str = /(http[s]?:\/\/([\w-]+.)+([:\d+])?(\/[\w-\.\/\?%&=]*)?)/gi;
					var name = data[i]._source.uname
							.replace(str,
									'<img style="height:20px;margin-left:10px;" src=$1>');
					var html = s
							+ '<div class="search-result">'
							+ '<span style="margin-top: 30px;height:30px;">发布者：<a target="_blank" href="">'
							+ data[i]._source.uname
							+ '</a><span style="margin-left: 20px;">发布于：'
							+ data[i]._source.release_date
							+ '</span></span>'
							+ '<div style="margin:10px 0px 10px 0px;"><span><a target="_blank" class="link font-bold" href="'
							+ data[i]._source.detailUrl + '">' + '问题:'
							+ data[i]._source.problem + '</a></span></div>'
							+ '<p><div class="monitor-content-con font-13">'
							+ '内容:' + data[i]._source.detail + '</div></p>'
							+ '<p>' + '<span style="margin-right: 30px;">投诉对象:'
							+ data[i]._source.object + '</span>'
							+ '<span style="margin-right: 30px;">投诉金额:'
							+ data[i]._source.money + '</span>'
							+ '<span style="margin-right: 30px;">诉求类型:'
							+ data[i]._source.appeal + '</span>' + '</p>'
							+ '</div>' + ' </div>'
					html += '<div style="background-color:#F0F0F0">'
							+ answerHtml
							+ '</div>'
							+ '<div style="font-family:Arial;padding:0px 0px 15px 0px;border-bottom:1px solid #e7eaec;font-size:14px">'
							+ '<span style="font-size:14px">状态：'
							+ data[i]._source.progress
							+ '</span>'
							+ '<span style="margin-left: 20px;">来源：<a target="_blank" href="'
							+ data[i]._source.sourceUrl + '">'
							+ data[i]._source.sourceName + '</a></span>'
							+ '</div>';
					$('#monitor-content').append(html)
				}
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	} else {
		$("#page").html("");
		dataerror("#monitor-content");
	}
}

/**
 * 组装公告列表数据
 * 
 * @param res
 * @returns
 */
function installAnnouncement(res) {
	console.log(res);
	let code = res.code;
	if (code == 200) {
		let data = res.list
		let totalPage = res.totalPage
		let totalCount = res.totalData
		let currentPage = res.page
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if (data.length > 0) {
			if(totalPage>1){
				pageHelper(currentPage, totalPage);
			}else{
				$("#page").html("");
			}
			let headHtml = '<div class="table-responsive">'
					+ '     <div class="project-warning-title project-warning-list">'
					+ '         <div>机构名称</div>'
					+ '         <div style="width: 50%">标题</div>'
					+ '         <div style="width: 150px;min-width: 110px;">类型</div>'
					+ '         <div>报告时间</div>'
					+ '     </div>'
					+ '<div class="prolist-warning-box" id="tableData" style="min-height: 100px;"></div>'
					+ ' </div>'
			$("#monitor-content").html(headHtml);
			for (var i = 0; i < data.length; i++) {
				let tdHtml = '	<div class="project-warning-list" data-id="'
						+ data[i].article_public_id
						+ '">'
						+ '       <div>'
						+ data[i].codename
						+ '</div>'
						+ '       <div class="text-over" style="width: 50%"><a target="_blank" href="'
						+ ctx
						+ 'fullsearch/reportdetail/'
						+ data[i].article_public_id
						+ '/announcement?fulltype=28'
						+ '&menuStyle='
						+ menuStyle
						+ '&fullpoly='
						+ full_poly
						+ '&onlyid='
						+ onlyid
						+ '&searchWord='
						+ $('#searchWord').val()
						+ '">'
						+ data[i].title
						+ '</a></div>'
						+ '        <div style="width: 150px;min-width: 110px;">'
						+ data[i].rtype + '</div>' + '        <div>'
						+ data[i].reportDate.substring(0, 10) + '</div>'
						+ '  </div>'
				$('#tableData').append(tdHtml)
			}
		}else{
			$("#page").html("");
			nodata('#monitor-content');
		}
	} else {
		$("#page").html("");
		nodata('#monitor-content');
	}
}

/**
 * 组装研报列表数据
 * 
 * @param res
 * @returns
 */
function installReport(res) {
	console.log(res);
	let code = res.code;
	if (code == 200) {
		let data = res.list
		let totalPage = res.totalPage
		let totalCount = res.totalData
		let currentPage = res.page
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if (data.length > 0) {
			console.log("totalPage: "+totalPage)
			if(totalPage>1){
				pageHelper(currentPage, totalPage);
			}else{
				$("#page").html("");
			}
			let headHtml = '<div class="table-responsive">'
					+ '     <div class="project-warning-title project-warning-list">'
					+ '         <div>机构名称</div>'
					+ '         <div style="width: 100px;min-width: 100px;">股票代码</div>'
					+ '         <div style="width: 50%;min-width: 300px;">标题</div>'
					// +' <div style="width: 100px;min-width: 100px;">券商</div>'
					// +' <div style="width: 100px;min-width:
					// 100px;">评级类型</div>'
					+ '         <div style="width: 100px;min-width: 100px;">日期</div>'
					+ '     </div>'
					+ '	  <div class="prolist-warning-box" id="tableData" style="min-height: 100px;"></div>'
					+ ' </div>'
			$("#monitor-content").html(headHtml);
			for (var i = 0; i < data.length; i++) {
				var gucode = data[i].code;
				gucode = gucode.split(".");
				let reportDate = data[i].reportDate;
				reportDate = /\d{4}-\d{1,2}-\d{1,2}/g.exec(reportDate);
				var li = data[i].authorList;
				var obj = eval(li);
				var auth = "";
				for (var j = 0; j < obj.length; j++) {
					var objs = eval(obj[j]);
					auth += objs.auth + " ";
				}
				let tdHtml = '	<div class="project-warning-list" data-id="'
						+ data[i].article_public_id
						+ '">'
						+ '       <div>'
						+ data[i].codename
						+ '</div>'
						+ '       <div style="width: 100px;min-width: 100px;">'
						+ gucode[0]
						+ '</div>'
						+ '       <div class="text-over" style="width: 50%;min-width: 300px;"><a target="_blank" href="'
						+ ctx
						+ 'fullsearch/reportdetail/'
						+ data[i].article_public_id
						+ '/report?fulltype=35'
						+ '&menuStyle='
						+ menuStyle
						+ '&fullpoly='
						+ full_poly
						+ '&onlyid='
						+ onlyid
						+ '&searchWord='
						+ $('#searchWord').val()
						+ '">'
						+ data[i].title
						+ '</a></div>'
						// +' <div style="width: 100px;min-width: 100px;">'+
						// data[i].org +'</div>'
						// +' <div style="width: 100px;min-width: 100px;">'+
						// data[i].rate +'</div>'
						+ '        <div style="width: 100px;min-width: 100px;">'
						+ reportDate + '</div>' + '  </div>'
				$('#tableData').append(tdHtml)
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	}else {
		$("#page").html("");
		nodata('#monitor-content');
	}
}

/**
 * 组装招标列表数据
 * 
 * @param res
 * @returns
 */
function installBidding(res) {
	console.log(res);
	let code = res.code;
	if (code == 200) {
		let data = res.list
		let totalPage = res.totalPage
		let totalCount = res.totalCount
		let currentPage = res.page
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if (data.length > 0) {
			pageHelper(currentPage, totalPage);
			let headHtml = '<div class="table-responsive">'
					+ '     <div class="project-warning-title project-warning-list">'
					+ '         <div style="width: 50%;min-width: 100px;">标题</div>'
					+ '         <div style="width: 100px;min-width: 100px;">省份</div>'
					+ '         <div style="width: 100px;min-width: 100px;">发布日期</div>'
					+ '         <div style="width: 150px;min-width: 150px;">数据来源</div>'
					+ '     </div>'
					+ '	  <div class="prolist-warning-box" id="tableData" style="min-height: 100px;"></div>'
					+ ' </div>'
			$("#monitor-content").html(headHtml);
			for (var i = 0; i < data.length; i++) {
				let listDataObj = data[i];
				let province = listDataObj.province;
				let datasource = listDataObj.datasource;
				let publish_date = listDataObj.publish_time;
				let title = listDataObj.title;
				let article_public_id = listDataObj.article_public_id;
				publish_date = /\d{4}-\d{1,2}-\d{1,2}/g.exec(publish_date);
				if (province == "央企招投标") {
					province = "中央";
				}
				let tdHtml = '	<div class="project-warning-list" data-id="'
						+ data[i].article_public_id
						+ '">'
						+ '       <div class="text-over" style="width: 50%;min-width: 100px;"><a target="_blank" href="'
						+ ctx
						+ 'fullsearch/biddingdetail/'
						+ article_public_id
						+ '?fulltype=37'
						+ '&menuStyle='
						+ menuStyle
						+ '&fullpoly='
						+ full_poly
						+ '&onlyid='
						+ onlyid
						+ '&searchWord='
						+ $('#searchWord').val()
						+ '">'
						+ title
						+ '</a></div>'
						+ '        <div style="width: 100px;min-width: 100px;">'
						+ province
						+ '</div>'
						+ '        <div style="width: 100px;min-width: 100px;">'
						+ publish_date
						+ '</div>'
						+ '        <div style="width: 150px;min-width: 150px;">'
						+ datasource + '</div>' + '  </div>'
				$('#tableData').append(tdHtml)
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	}
}

/**
 * 组装招聘列表数据
 * 
 * @param res
 * @returns
 */
function installInvite(res) {
	let code = res.code;
	if (code == 200) {
		let data = res.data;
		let totalPage = res.totalPage;
		let totalCount = res.totalCount;
		let currentPage = res.page;
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		let headHtml = '<div class="table-responsive">'
				+ '     <div class="project-warning-title project-warning-list">'
				+ '         <div style="width: 200px;min-width: 200px;">职位</div>'
				+ '         <div style="width: 70%;min-width: 200px;">公司</div>'
				+ '         <div style="width: 100px;min-width: 100px;">地区</div>'
				+ '         <div style="width: 80px;min-width: 80px;">招聘人数</div>'
				+ '         <div style="width: 100px;min-width: 100px;">发布时间</div>'
				+ '     </div>'
				+ '	  <div class="prolist-warning-box" id="tableData" style="min-height: 100px;"></div>'
				+ ' </div>'
		$("#monitor-content").html(headHtml);
		if (data.length > 0) {
			pageHelper(currentPage, totalPage);
			for (let i = 0; i < data.length; i++) {
				let inviteObj = data[i];
				let job_type = inviteObj.job_type;
				let invite_count = inviteObj.invite_count;
				let invite_city = inviteObj.invite_city;
				let push_time = inviteObj.push_time;
				push_time = push_time.substring(0, 10);
				let edu_level = inviteObj.edu_level;
				let invite_title = inviteObj.invite_title;
				let company_name = inviteObj.company_name;
				let record_id = inviteObj.record_id;

				if (edu_level == 0) {
					edu_level = "学历不限";
				} else if (edu_level == 1) {
					edu_level = "初中及以下";
				} else if (edu_level == 2) {
					edu_level = "初中";
				} else if (edu_level == 3) {
					edu_level = "中专/中技/高中";
				} else if (edu_level == 4) {
					edu_level = "大专";
				} else if (edu_level == 5) {
					edu_level = "本科";
				} else if (edu_level == 6) {
					edu_level = "硕士";
				} else if (edu_level == 7) {
					edu_level = "博士";
				} else if (edu_level == 8) {
					edu_level = "MBA/EMBA";
				}

				if (job_type == "0") {
					job_type = "主营岗位"
				} else {
					job_type = "其他"
				}

				let tdHtml = '	<div class="project-warning-list" data-id="'
						+ record_id
						+ '">'
						+ '       <div class="text-over" style="width: 200px;min-width: 200px;"><a target="_blank" href="'
						+ ctx
						+ 'fullsearch/inviteDetails/'
						+ record_id
						+ '?fulltype=36'
						+ '&menuStyle='
						+ menuStyle
						+ '&fullpoly='
						+ full_poly
						+ '&onlyid='
						+ onlyid
						+ '&searchWord='
						+ $('#searchWord').val()
						+ '">'
						+ invite_title
						+ '</a></div>'
						+ '        <div style="width: 70%;min-width: 200px;">'
						+ company_name
						+ '</div>'
						+ '        <div style="width: 100px;min-width: 100px;">'
						+ invite_city
						+ '</div>'
						+ '        <div style="width: 80px;min-width: 80px;">'
						+ invite_count
						+ '</div>'
						+ '        <div style="width: 100px;min-width: 100px;">'
						+ push_time + '</div>' + '  </div>'
				$('#tableData').append(tdHtml)
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	}
}

/**
 * 组装 工商数据
 * 
 * @param res
 * @returns
 */
function installCompany(res) {
	let code = res.code;
	if (code == 200) {
		let data = res.list;
		let totalPage = res.totalPage;
		let totalCount = res.totalCount;
		let currentPage = res.page;
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		let headHtml = '<div class="table-responsive">'
				+ '     <div class="project-warning-title project-warning-list">'
				+ '         <div style="width: 50%;min-width: 200px;">企业名称</div>'
				+ '         <div style="width: 80px;min-width: 80px;">省份</div>'
				+ '         <div style="width: 80px;min-width: 80px;">城市</div>'
				+ '         <div style="width: 200px;min-width: 200px;">所属行业</div>'
				+ '         <div style="width: 100px;min-width: 100px;">法定代办人</div>'
				+ '         <div style="width: 100px;min-width: 100px;">注册资本(万元)</div>'
				+ '         <div style="width: 80px;min-width: 80px;">成立日期</div>'
				+ '     </div>'
				+ '	  <div class="prolist-warning-box" id="tableData" style="min-height: 100px;"></div>'
				+ ' </div>'
		$("#monitor-content").html(headHtml);
		if (data.length > 0) {
			pageHelper(currentPage, totalPage);
			for (let i = 0; i < data.length; i++) {
				var legal_representative = data[i].legal_representative;
				if (typeof legal_representative == "undefined") {
					legal_representative = "";
				}

				let tdHtml = '	<div class="project-warning-list" data-id="'
						+ data[i].article_public_id
						+ '">'
						+ '       <div class="text-over" style="width: 50%;min-width: 200px;"><a target="_blank" href="'
						+ ctx
						+ 'fullsearch/companyDetail/'
						+ data[i].article_public_id
						+ '?fulltype=39'
						+ '&menuStyle='
						+ menuStyle
						+ '&fullpoly='
						+ full_poly
						+ '&onlyid='
						+ onlyid
						+ '&searchWord='
						+ $('#searchWord').val()
						+ '">'
						+ data[i].name
						+ '</a></div>'
						+ '        <div style="width: 80px;min-width: 80px;">'
						+ data[i].province
						+ '</div>'
						+ '        <div style="width: 80px;min-width: 80px;">'
						+ data[i].city
						+ '</div>'
						+ '        <div style="width: 200px;min-width: 200px;">'
						+ data[i].industry_involved
						+ '</div>'
						+ '        <div style="width: 100px;min-width: 100px;">'
						+ legal_representative
						+ '</div>'
						+ '        <div style="width: 100px;min-width: 100px;">'
						// data[i].registered_capital.substring(0, 4)
						+ data[i].registered_capital + '</div>'
						+ '        <div style="width: 80px;min-width: 80px;">'
						+ data[i].establish_time.substring(0, 10) + '</div>'
						+ '  </div>'
				$('#tableData').append(tdHtml)
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	}

}
/**
 * 组装 法律文书数据
 * 
 * @param res
 * @returns
 */
function installJudgment(res) {
	let code = res.code;
	if (code == 200) {
		let data = res.list;
		let totalPage = res.totalPage;
		let totalCount = res.totalCount;
		let currentPage = res.page;
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if (data.length > 0) {
			$('#monitor-content').html('')
			pageHelper(currentPage, totalPage);
			for (var i = 0; i < data.length; i++) {
				var lawName = '';
				if (data[i].lawNameB == '') {

					lawName = data[i].lawNameA;
				} else {
					lawName = data[i].lawNameA + ',' + data[i].lawNameB;
				}

				var lawFirm = '';
				if (data[i].lawNameB == '') {
					lawFirm = data[i].lawFirmA;
				} else {
					lawFirm = data[i].lawFirmA + ',' + data[i].lawFirmB;
				}

				var partyName = '';// 当事人
				var ob = data[i]
				
				var list_map = new Array();
				if(!(!ob.accuser)){
					 var accuser = JSON.parse(ob.accuser); 
					 list_map.push(accuser.name)
				}
				if(!(!ob.defendant)){
					var defendant = JSON.parse(ob.defendant); 
					
					list_map.push(defendant.name)
				}
				if(!(!ob.participants)){
					var participants = JSON.parse(ob.participants); 
					list_map.push(participants.name)
				}
				if(!(!ob.extend_string_one)){
					var extend_string_one = JSON.parse(ob.extend_string_one); 
					for(var j=0 ;j<extend_string_one.length;j++){
						var eOb = extend_string_one[j]
						list_map.push(eOb.companyName)
					}
				}
				var deduplicationlist = deduplication(list_map)
				for(var z=0 ; z< deduplicationlist.length;z++){
					if(z+1 == deduplicationlist.length){
						partyName += deduplicationlist[z]
					}else{
						partyName += deduplicationlist[z]+","
					}
				}
//				if (typeof (data[i].accuser) == "undefined") {// 原告为空
//					if (typeof (data[i].defendant) == "undefined") {// 被告为空
//						if (typeof (data[i].participants) == "undefined") {
//							partyName = ' ';
//						} else {
//							partyName = data[i].participants;
//						}
//					} else {
//						if (typeof (data[i].participants) == "undefined") {
//							partyName = data[i].defendant;
//						} else {
//							partyName = data[i].defendant + ','
//									+ data[i].participants;
//						}
//					}
//				} else {
//					if (typeof (data[i].defendant) == "undefined") {
//						if (typeof (data[i].participants) == "undefined") {
//							var obj = data[i].accuser.parseJSON();
//							console.log(obj)
//							partyName = obj.name;
//							System.out.println(partyName);
//						} else {
//							partyName = data[i].accuser + ','
//									+ data[i].participants;
//							;
//						}
//					} else {
//						if (typeof (data[i].participants) == "undefined") {
//							partyName = data[i].accuser + ','
//									+ data[i].defendant;
//						} else {
//							partyName = data[i].accuser + ','
//									+ data[i].defendant + ','
//									+ data[i].participants;
//						}
//					}
//				}
				var partyNameHtml = '';
				if(partyName.length > 30){
					partyNameHtml = '<span>法院：'+ob.caseCourt+'</span><span style="margin: 30px 30px;" title="'+partyName+'" >当事人: '
					+ partyName.substring(0,30)+"..."
					+ '</span>'
				}else{
					partyNameHtml = '<span>法院：'+ob.caseCourt+'</span><span style="margin: 30px 30px;">当事人: '
						+ partyName
						+ '</span>'
				}
				
				var judgment_htmlHtml = '';
				var judgment_html = '';//内容
				if(ob.judgment_html!=undefined){
					var delHtmlTaglist = delHtmlTag(ob.judgment_html)
					
					if(delHtmlTaglist.length > 210){
						//console.log(delHtmlTaglist)
						//console.log(delHtmlTaglist.substring(0,210)+"...")
						judgment_htmlHtml = '<span style="margin-right: 30px;" title="'+delHtmlTaglist+'" >内容: '
						+ delHtmlTaglist.substring(0,210)+"..."
						+ '</span>'
					}else{
						judgment_htmlHtml = '<span style="margin-right: 30px;">内容: '
							+ delHtmlTaglist
							+ '</span>'
					}
				}
//				console.log(delHtmlTaglist.length)
//				console.log(delHtmlTaglist.length > 210)
				
				
				var refereeTime = '';//时间
				var refereeTimedate = deltime(ob.refereeTime)
				
				
				var html = '<div class="feed-element">'
						+ '<div class="search-result">'
						+ '<a target="_blank" class="link font-bold" href="'
						+ ctx
						+ 'fullsearch/judgmentDetail/'
						+ data[i].article_public_id
						+ '?fulltype=42'
						+ '&menuStyle='
						+ menuStyle
						+ '&fullpoly='
						+ full_poly
						+ '&onlyid='
						+ onlyid
						+ '&searchWord='
						+ $('#searchWord').val()
						+ '">'
						+ data[i].title
						+ '</a>'
						+ '<br />'
						+ '<p class="font-13" style="margin-top: 15px;">'
						+
						// '<span style="margin-right: 30px;">律师:
						// '+lawName+'</span>'+
						partyNameHtml
						+ '<span style="margin-right: 30px;">仲裁时间: '
						+ refereeTimedate
						+ '</span>'
						+
						// '<span style="margin-right: 30px;">案件原因:
						// '+data[i].caseCause+'</span>'+
						'</p>'
						+

						// '<p class="font-13">'+
						// '<span style="margin-right: 30px;">发布时间:
						// '+data[i].publishDate+'</span>'+
						// '<span style="margin-right: 30px;">律师事务所:
						// '+lawFirm+'</span>'+
						// '<span style="margin-right: 30px;">法官:
						// '+data[i].judgeName+'</span>'+
						// '<span style="margin-right: 30px;">案件类型:
						// '+data[i].caseType+'</span>'+
						// '</p>'+

						'<p class="font-13">'
						+ 
						/*
						'<span style="margin-right: 30px;">内容: '
						+ data[i].judgment_html
						+ '</span>'
						*/
						judgment_htmlHtml
						+ '</p>'
						+ '</div>'
						+ ' </div><hr/>';
				$('#monitor-content').append(html)
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	}
}

/**
 * 组装 知识产权 列表数据
 * 
 * @param res
 * @returns
 */
function installKnowLedge(res) {
	let code = res.code;
	if (code == 200) {
		let data = res.list;
		let totalPage = res.totalPage;
		let totalCount = res.totalCount;
		let currentPage = res.page;
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if (data.length > 0) {
			pageHelper(currentPage, totalPage);
			$("#monitor-content").html('');
			for (let i = 0; i < data.length; i++) {
				let obj = data[i];
				let openDay = obj.openDay;
				let title = obj.title;
				let type = obj.type;
				let applyDay = obj.applyDay;
				let applyMark = obj.applyMark;
				let proposerZh = obj.proposerZh;
				let proposerEn = obj.proposerEn;
				let inventorZh = obj.inventorZh;
				let openNumber = obj.openNumber;
				let article_public_id = obj.article_public_id;
				applyDay = /\d{4}-\d{1,2}-\d{1,2}/g.exec(applyDay);
				openDay = /\d{4}-\d{1,2}-\d{1,2}/g.exec(openDay);
				if (proposerZh == undefined || proposerZh == "") {
					proposerZh = proposerEn;
				}

				if (typeof inventorZh == "undefined") {
					inventorZh = "";
				}
				if (openNumber == undefined || openNumber == "") {
					openNumber = "暂无数据";
				}
				let detailUrl = ctx + 'fullsearch/knowLedgeDetail/'
						+ article_public_id + '?fulltype=' + full_type
						+ '&menuStyle=' + menuStyle + '&fullpoly=' + full_poly
						+ '&onlyid=' + onlyid + '&searchWord='
						+ $('#searchWord').val()

				var conhtml = '<div class="feed-element" style="overflow: hidden"><div class="search-result" ><div style="padding:0 0 0 20px;">'
						+ '<h5><a target="_blank" class="link font-bold" href="'
						+ detailUrl
						+ '">'
						+ title
						+ '</a></h5>'
						+ '<div class="row"><p style="font-size:14px;" class="col-lg-4">'
						+ '<strong class="str">发明人:&nbsp;</strong><span class="ti">'
						+ inventorZh
						+ '</span>'
						+ '</p>'
						+ '<p style="font-size:14px" class="col-lg-4">'
						+ '<strong class="str">申请号:&nbsp;</strong><span class="ti">'
						+ applyMark
						+ '</span>'
						+ '</p>'
						+ '<p style="font-size:14px" class="col-lg-4">'
						+ '<strong class="str">公开日期:&nbsp;</strong><span  class="ti">'
						+ openDay
						+ '</span>'
						+ '</p>'
						+ '</div>'
						+ '<div class="row"><p style="font-size:14px;" class="col-lg-4">'
						+ '<strong class="str">类型:&nbsp;</strong><span class="ti">'
						+ type
						+ '</span>'
						+ '</p>'
						+ '<p style="font-size:14px" class="col-lg-4">'
						+ '<strong class="str">公开号:&nbsp;</strong><span class="ti">'
						+ openNumber
						+ '</span>'
						+ '</p>'
						+ '<p style="font-size:14px" class="col-lg-4">'
						+ '<strong class="str">申请日期:&nbsp;</strong><span  class="ti">'
						+ applyDay
						+ '</span>'
						+ '</p>'
						+ '</div>'
						+ '<div class="row"><p style="font-size:14px" class="col-lg-12"><strong class="str">摘要:&nbsp;</strong>'
						+ (obj.abstractZh.length>150 ? obj.abstractZh.substring(0,150)+"..." : obj.abstractZh)
						+ '</p></div>'
						+ '</div></div>'
						+ '</div><hr/>'
				$("#monitor-content").append(conhtml);
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	}
}

/**
 * 组装 投资融资 数据
 * 
 * @param res
 * @returns
 */
function installInvestment(res) {
	let code = res.code;
	if (code == 200) {
		let data = res.list;
		let totalPage = res.totalPage;
		let totalCount = res.totalCount;
		let currentPage = res.page;
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		let headHtml = '<div class="table-responsive">'
				+ '     <div class="project-warning-title project-warning-list">'
				+ '         <div style="width: 80px;min-width: 80px;">图标</div>'
				+ '         <div style="width: 20%;min-width: 150px;">受资方</div>'
				+ '         <div style="width: 100px;min-width: 100px;">轮次</div>'
				+ '         <div style="width: 100px;min-width: 100px;">金额</div>'
				+ '         <div style="width: 200px;min-width: 200px;">投资方</div>'
				+ '         <div style="width: 100px;min-width: 100px;">时间</div>'
				+ '     </div>'
				+ '	  <div class="prolist-warning-box" id="tableData" style="min-height: 100px;"></div>'
				+ ' </div>'
		$("#monitor-content").html(headHtml);
		if (data.length > 0) {
			pageHelper(currentPage, totalPage);
			for (let i = 0; i < data.length; i++) {
				var investorArray = JSON.parse(data[i].investorArray)
				var investors = []
				if (investorArray.length > 0) {
					investors.push(investorArray[0].investorName)
				} else {
					investors.push("--")
				}
				var push_time = data[i].push_time;
				var push = /\d{4}-\d{1,2}-\d{1,2}/g.exec(push_time);

				let tdHtml = '	<div class="project-warning-list" data-id="'
						+ data[i].article_public_id
						+ '">'
						+ '        <div style="width: 80px;min-width: 80px;align-items: center; overflow: hidden; display: flex;"><img style="width: 50px;" src="'
						+ data[i].companyLogo
						+ '"></div>'
						+ '       <div class="text-over" style="width: 20%;min-width: 150px;"><a target="_blank" href="'
						+ ctx
						+ 'fullsearch/investmentDetail/'
						+ data[i].article_public_id
						+ '?fulltype=40'
						+ '&menuStyle='
						+ menuStyle
						+ '&fullpoly='
						+ full_poly
						+ '&onlyid='
						+ onlyid
						+ '&searchWord='
						+ $('#searchWord').val()
						+ '">'
						+ data[i].companyName
						+ '</a></div>'
						+ '        <div style="width: 100px;min-width: 100px;">'
						+ data[i].rounds
						+ '</div>'
						+ '        <div style="width: 100px;min-width: 100px;">'
						+ data[i].money
						+ '</div>'
						+ '        <div style="width: 200px;min-width: 200px;">'
						+ investors
						+ '</div>'
						+ '        <div style="width: 100px;min-width: 100px;">'
						+ push + '</div>' + '  </div>'
				$('#tableData').append(tdHtml)
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	}
}

/**
 * 组装 问答数据 列表
 * 
 * @param res
 * @returns
 */
function installBaiduKnows(res) {
	let code = res.code;
	if (code == 200) {
		let data = res.list;
		let totalPage = res.totalPage;
		let totalCount = res.totalCount;
		let currentPage = res.page;
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if (data.length > 0) {
			pageHelper(currentPage, totalPage);
			$('#monitor-content').html('')
			for (let i = 0; i < data.length; i++) {
				var answer = JSON.parse(data[i].answer_json);
				var content = data[i].content;
				if (content && content.length > 200) {
					content = content.substring(0, 200) + '......'
				}
				var html2 = '';
				if (answer.length == 0) {
					html2 = '    <div class="bd-answer mb-10">'
							+ '        <div class="bd-answer-con" >暂无回答</div>'
							+ '    </div>'
				} else {
					for (var j = 0; j < answer.length; j++) {
						var profile = answer[j].author_profile;
						var answer_text = answer[j].answer;
						if (!profile) {
							profile = ctx + 'dist/img/doctor.jpg'
						}
						html2 += '    <div class="bd-answer mb-10">'
								+ '        <div class="first-line">'
								+ '            <div class="head-img"><div style="background: url('
								+ profile
								+ ')"></div></div>'
								+ '            <div class="bd-name">'
								+ answer[j].author_name
								+ '</div>'
								+ '            <div class="bd-tips">'
								+ '                <span>等级：1</span>'
								+ '                <span>粉丝：7</span>'
								+ '                <span>获赞：'
								+ answer[j].agree_num
								+ '</span>'
								+ '                <span>回答时间：'
								+ answer[j].answer_time
								+ '</span>'
								+ '            </div>'
								+ '        </div>'
								+ '        <div class="bd-answer-con" title="'
								+ answer[j].answer
								+ '">'
								+ answer_text
								+ '</div>' + '    </div>'
					}
				}
				var html = '<div class="baidu-info b-b">'
						+ '    <div class="bd-title"><a target="_blank" class="link font-bold" href="'
						+ data[i].detailUrl + '">提问：' + data[i].title + ' </a>'
						+ '    <div class="bd-tips mb-10">' + '    <span>悬赏：'
						+ data[i].reward + '</span>' + '    <span>时间：'
						+ data[i].spider_time + '</span>' + '    </div>'
						+ '</div>' + '    <div class="bd-content mb-10">'
						+ content + '</div>' + '</div><hr/>';
				$('#monitor-content').append(html)
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	}
}

/**
 * 学术数据
 * 
 * @param res
 * @returns
 */
function installThesisn(res) {
	let code = res.code;
	if (code == 200) {
		let data = res.list;
		let totalPage = res.totalPage;
		let totalCount = res.totalCount;
		let currentPage = res.page;
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if (data.length > 0) {
			pageHelper(currentPage, totalPage);
			$('#monitor-content').html('')
			for (let i = 0; i < data.length; i++) {
				var content = data[i].summary
				if (content.length - 300 > 0) {
					content = content.substring(0, 300) + '...'
				}
				var push_time = data[i].spider_time;
				var push = push_time.substr(0, 16);
				var authorName = JSON.parse(data[i].co_author);
				var name = "";
				for (let j = 0; j < authorName.length; j++) {
					name = authorName[j].name + "," + name;
				}
				name = name.substring(0, name.length - 1)
				var html = '<div class="feed-element">'
						+ ' <div class="search-result">'
						+ '<a target="_blank" class="link font-bold" href="'
						+ ctx
						+ 'fullsearch/thesisnDetail/'
						+ data[i].article_public_id
						+ '?fulltype=45'
						+ '&menuStyle='
						+ menuStyle
						+ '&fullpoly='
						+ full_poly
						+ '&onlyid='
						+ onlyid
						+ '&searchWord='
						+ $('#searchWord').val()
						+ '">'
						+ data[i].title
						+ '</a>'
						+ '<br />'
						+ '<p class="monitor-content-con font-13">'
						+ content
						+ '</p>'
						+ '<p>'
						+ '<span class="link m-r-10">作者:'
						+ name
						+ '</span>'
						+ '<span class="link m-r-10" style="margin-left: 30px;">网站名:<a target="_blank" href="'
						+ data[i].detail_url
						+ '">'
						+ data[i].source_name
						+ '</a></span>'
						+ '<span class="link m-r-10" style="float: right;">发布时间:'
						+ push_time
						+ '</span>'
						+ '</p>'
						+ '</div>'
						+ ' </div><hr/>'
				$('#monitor-content').append(html)
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	}
}

/**
 * @description 组装资讯列表数据
 */
function installArticle3(res) {
	let code = res.code;
	let strAll = '';
	if (code == 200) {
		let data = res.data.data;
		let totalPage = res.data.totalPage;
		let totalCount = res.data.totalCount;
		let currentPage = res.data.currentPage;
		if (totalCount > 5000) {
			totalCount = "5000+";
			totalPage = 5000 / pageSize;
		}
		$("#totalCount").html(totalCount);
		if (res.data.hasOwnProperty("article_public_idList")) {
			article_public_idList = res.data.article_public_idList;
		}
		if (data.length > 0) {
			pageHelper(currentPage, totalPage);
			for (let i = 0; i < data.length; i++) {
				let dataJson = data[i];
				let classify = dataJson.classify; // 媒体分类
				// let classify = 2;
				let websitelogo = dataJson.websitelogo;
				let article_public_id = dataJson.article_public_id;
				let author = dataJson.author;
				let key_words = dataJson.key_words;
				let sourcewebsitename = dataJson.sourcewebsitename;
				let title = dataJson.title;
				let content = dataJson.content;
				let emotionalIndex = dataJson.emotionalIndex;
				let publish_time = dataJson.publish_time;
				let pub_data = dataJson.publish_time;
				let extend_string_one = dataJson.extend_string_one;
				let forwardingvolume = dataJson.forwardingvolume; // 转发量
				let commentsvolume = dataJson.commentsvolume; // 评论量
				let praisevolume = dataJson.praisevolume; // 点赞数
				let industrylable = dataJson.industrylable;
				let eventlable = dataJson.eventlable;
				let article_category = dataJson.article_category;
				publish_time = timeParse(publish_time);
				var relatedWord = dataJson.relatedWord;
				let ner = dataJson.ner;
				let num = 0
				let similarflag = 0;
				let source_url = dataJson.source_url;

				//判断是否合并
				$('span[data-similar]').each(function () {
					if ($(this).hasClass('badge-info')) {
						similarflag = $(this).data('similar');
					}
				});

				if (similarflag == '1') {
					debugger;
					console.log("合并")
					console.log(dataJson);
					num = dataJson.num;
				}else {
					console.log("不合并");
					console.log(dataJson);
				}


				let copytext = '';
				if (classify == 2) { // 微博
					let strStart = '<div class="wb-content just-bet" >';
					let strCheck = '<div class="monitor-check"><input type="checkbox" data-index="'
							+ article_public_id
							+ '" id="check2"><span></span></div>';
					let strContentStart = '<div class="monitor-right">';
					let strTitle = '<div class="monitor-content-title"><a target="_blank" href="'
							+ ctx
							+ 'fullsearch/detail/'
							+ article_public_id
							+ '?fulltype='
							+ full_type
							+ '&menuStyle='
							+ menuStyle
							+ '&fullpoly='
							+ full_poly
							+ '&onlyid='
							+ onlyid
							+ '&publish_time='
							+ pub_data
							+ '&searchWord='
							+ $('#searchWord').val()
							+ '" class="link font-bold"><span class="content-logo" style="background: url('
							+ websitelogo
							+ ');"></span>'
							+ title
							+ '</a>';
					let category = dealCate(article_category);

					if (industrylable != '') {
						strTitle += '<span class="sl-date industrylable" >' + industrylable + ' </span>';
					}

					if (eventlable != '') {
						strTitle += '<span class="sl-date eventlable">' + eventlable + ' </span>';
					}

					if (category != '') {
						strTitle += '<span class="sl-date categorylable">' + category + ' </span>';
					}

					strTitle += '<span class="sl-date  ">' + publish_time + '</span></div>';

					var str = "________________";
					/* 16 */
					let contentStart = ''; // 自己发的
					let contentEnd = '';
					if (content.indexOf(str) != -1) {
						let index = content.lastIndexOf(str);
						contentStart = content.substring(0, index);
						contentEnd = content.substring(index + 16,
								content.length);
					} else {
						contentStart = content;
					}

					let strContent = '<div class="monitor-content-con font-13">'
							+ contentStart + '​</div>'; // 自己的正文
					let strTranspond = '<div class="monitor-content-con font-13 wb-zf">'
							+ contentEnd + '</div>'; // 转发的原文
					let strImgStart = '<div class="img-group">';
					let strImgGroup = '';
					if (typeof (extend_string_one) == "object") {
						let imglist = extend_string_one.imglist;
						if (imglist != null) {
							for (let i = 0; i < imglist.length; i++) {
								let imgurl = imglist[i].imgurl;
								let imgurlstr = '<div class="img-box"><img src="'
										+ imgurl
										+ '" alt="" class="img-cover "></div>'
								strImgGroup += imgurlstr;
								if (i > 3) {
									break;
								}
							}
						}
					}
					let strImgEnd = '</div>';
					let strLikeStr = '<div class="like-comm">';
					let strSource = '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 '
							+ sourcewebsitename + '</span>';
					let strForward = '<span class="link m-r-10"> <i class="mdi mdi-shape-square-plus "></i> 转发 '
							+ forwardingvolume + '</span>';
					let strPraise = '<span class="link m-r-10"> <i class="mdi mdi-heart-outline "></i> 点赞 '
							+ praisevolume + '</span>';
					let strComment = '<span class="link m-r-10"> <i class="mdi mdi-comment-processing-outline"></i> 评论 '
							+ commentsvolume + '</span>';

					let keyword = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 关键词 '
						+ key_words + '</span>';

					if (similarflag == '1') {
						keyword =keyword+'<span class="link m-r-10"> <i class="mdi mdi-projector-screen"></i> 相似文章数 ' + num + '</span>';
					}


					let strEmotion = '';
					if (emotionalIndex == 1) {
						strEmotion = '<span class="link f-right moodzm">正面</span>';
					} else if (emotionalIndex == 2) {
						strEmotion = '<span class="link f-right moodzx">中性</span>';
					} else if (emotionalIndex == 3) {
						strEmotion = '<span class="link f-right moodfm">负面</span>';
					}

					//拼接机构
					let strcompanyandgov = '<div class="like-comm font-13"><span class="link m-r-10">'
					let org = JSON.parse(ner).org;
					let orgstr = "";
					var orgflag = 1;
					for (var key in org) {
						orgflag++;
						orgstr += '<a style="" class="ntag" href="###">' + key + '</a>'
						if (orgflag == 3) break;
					}
					strcompanyandgov = strcompanyandgov + orgstr + '</span><span class="link m-r-10">'
					//政府机构
					let nto = JSON.parse(ner).nto;
					let ntovstr = "";
					var netoflag = 1;
					for (var key in nto) {
						netoflag++;
						strcompanyandgov += '<a style="" class="govtag" href="###">' + key + '</a>'
						if (netoflag == 3) break;
					}
					strcompanyandgov += '</span>';

					//上市公司
					strcompanyandgov += '<span class="link m-r-10">';
					debugger;
					let ipo = JSON.parse(ner).IPO;
					let ipostr = "";
					var ipoflag = 1;
					for (var key in ipo) {
						ipoflag++;
						strcompanyandgov += '<a style="" class="ipotag" href="###">' + key + '</a>'
						if (ipoflag == 3) break;
					}
					strcompanyandgov += '</span></div>';

					let strLikeEnd = '</div>';
					let strContentEnd = '</div>';
					let strEnd = '</div><hr>';
					strAll += strStart + strCheck + strContentStart + strTitle
							+ strContent + strTranspond + strImgStart
							+ strImgGroup + strImgEnd + strLikeStr + strSource
							+ strForward + strPraise + strComment + keyword
							+ strEmotion + strLikeEnd +strcompanyandgov+ strContentEnd + strEnd;
				} else {
					if (typeof (extend_string_one) == "object") {
						let imglist = extend_string_one.imglist;
						if (imglist.length > 0) {
							let strStart = '<div class="wb-content just-bet" >';
							let strCheck = '<div class="monitor-check"><input type="checkbox" data-index="'
									+ article_public_id
									+ '" id="check1"><span></span></div>';
							let strContentStart = '<div class="monitor-right">';
							let websitelogoStr = '<span class="content-logo" style="background: url('
									+ websitelogo + ');"></span>';
							if (full_type == 38) {
								websitelogoStr = '';
							}
							let strTitle = '<div class="monitor-content-title"><a target="_blank" href="'
									+ ctx
									+ 'fullsearch/detail/'
									+ article_public_id
									+ '?fulltype='
									+ full_type
									+ '&menuStyle='
									+ menuStyle
									+ '&fullpoly='
									+ full_poly
									+ '&onlyid='
									+ onlyid
									+ '&publish_time='
									+ pub_data
									+ '&searchWord='
									+ $('#searchWord').val()
									+ '"  class="link font-bold">'
									+ websitelogoStr
									+ title
									+ '</a>';


							let category = dealCate(article_category);

							if (industrylable != '') {
								strTitle += '<span class="sl-date industrylable" >' + industrylable + ' </span>';
							}

							if (eventlable != '') {
								strTitle += '<span class="sl-date eventlable">' + eventlable + ' </span>';
							}

							if (category != '') {
								strTitle += '<span class="sl-date categorylable">' + category + ' </span>';
							}

							strTitle += '<span class="sl-date  ">' + publish_time + '</span></div>';

							// let strTitle = '<div
							// class="monitor-content-title"><a
							// onclick="toDetail(&apos;' + article_public_id +
							// '&apos;,&apos;' + monitor_groupid +
							// '&apos;,&apos;' + monitor_projectid + '&apos;)"
							// class="link font-bold"><span class="content-logo"
							// style="background: url(' + websitelogo +
							// ');"></span>' + title + '</a><span class="sl-date
							// ">' + publish_time + ' </span></div>';
							let strContent = '<div class="wb-content-imgbox"><div class="wb-left-imgbox"><img src="'
									+ imglist[0].imgurl
									+ '" class="img-cover" alt=""></div><div class="wb-right-content"><div class="monitor-content-con font-13">'
									+ content + '</div></div></div>';
							let strLikeStrat = '<div class="like-comm m-t-10 font-13">';
							let strSource = '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 '
									+ sourcewebsitename + '</span>';
							// let strKeywords = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 关键词 '
							// 		+ key_words + '</span>';


							let strKeywords = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 关键词 '
								+ key_words + '</span>';

							if (similarflag == '1') {
								strKeywords =strKeywords+'<span class="link m-r-10"> <i class="mdi mdi-projector-screen"></i> 相似文章数 ' + num + '</span>';
							}

							let strEmotion = '';
							if (emotionalIndex == 1) {
								strEmotion = '<span class="link f-right moodzm">正面</span>';
							} else if (emotionalIndex == 2) {
								strEmotion = '<span class="link f-right moodzx">中性</span>';
							} else if (emotionalIndex == 3) {
								strEmotion = '<span class="link f-right moodfm">负面</span>';
							}

//拼接机构
							let strcompanyandgov = '<div class="like-comm font-13"><span class="link m-r-10">'
							let org = JSON.parse(ner).org;
							let orgstr = "";
							var orgflag = 1;
							for (var key in org) {
								orgflag++;
								orgstr += '<a style="" class="ntag" href="###">' + key + '</a>'
								if (orgflag == 3) break;
							}
							strcompanyandgov = strcompanyandgov + orgstr + '</span><span class="link m-r-10">'
							//政府机构
							let nto = JSON.parse(ner).nto;
							let ntovstr = "";
							var netoflag = 1;
							for (var key in nto) {
								netoflag++;
								strcompanyandgov += '<a style="" class="govtag" href="###">' + key + '</a>'
								if (netoflag == 3) break;
							}
							strcompanyandgov += '</span>';

							//上市公司
							strcompanyandgov += '<span class="link m-r-10">';
							let ipo = JSON.parse(ner).IPO;
							debugger;
							let ipostr = "";
							var ipoflag = 1;
							for (var key in ipo) {
								ipoflag++;
								strcompanyandgov += '<a style="" class="ipotag" href="###">' + key + '</a>'
								if (ipoflag == 3) break;
							}
							strcompanyandgov += '</span></div>';

							let kuaijie = '<span class="link m-r-0">' +
								'<div class="btn-group inline-block" role="group"> <ul class="font-size-0" style="list-style-type: none;">' +
								'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-collection dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="添加至收藏夹"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="ng-binding datafavorite" data-type="' + article_public_id + '" ng-bind="collect.name">收藏夹</span> </a> </li> </ul> </li>' +
								//   						'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-news-material dropdown-toggle ng-scope" id="sck_916007411683191177512726" data-tippy="" title="发送、移动"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="briefFoldersList!=null&amp;&amp;briefFoldersList.length>0"> <li role="presentation" ng-repeat="brief in briefFoldersList" ng-if="brief!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(brief.folderId,2,icc)"> <span class="ng-binding sending" data-type="'+article_public_id+'" >发送、移动</span> </a> </li>  </ul> </li>'+
								//    						'<li class="inline-block dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-send-way dropdown-toggle ng-scope" data-tippy="" title="舆情下发渠道"></button> <ul class="dropdown-menu pt15 pb15" role="menu"> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(2,icc,1);"> <span class="fa-key">短信下发</span> </a> </li> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(1,icc,1);"> <span class="fa-key">邮件下发</span> </a> </li></ul> </li>'+
								'<li class="inline-block" style="float: left;"> <a href="' + source_url + '" target="_blank" class=""> <button type="button" data-tippy-placement="top" aria-expanded="false" class="tippy btn btn-default-icon fa-check-origin-link" data-tippy="" data-original-title="查看原文"></button> </a> </li>' +
								'<li class="inline-block dropdown" style="float: left;"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-copy-direct dropdown-toggle" data-tippy="" data-original-title="复制"> </button> <ul class="dropdown-menu" role="menu"> ' +



								'<li role="presentation"> <a href="javascript:void(0)" id="link_916007411683191177512726" role="menuitem" ng-click="copyLink(icc.webpageUrl,icc.id);"> <span class="copyurl" data-type="' + article_public_id + '" data-flag="' + source_url + '" >拷贝地址</span> </a> </li>' +
								'<li role="presentation"> <a href="javascript:void(0)" class="copy-link-custom" role="menuitem" ng-click="aKeyToCopy(icc);"> <span class="copytext" data-type="' + article_public_id + '" data-flag="' + copytext + '" >一键复制</span> </a> </li> </ul> </li>' +
								//'<li class="inline-block ng-scope" style="float: left;" ng-show="icc.readFlag==undefined &amp;&amp; view.resultPresent != 3" ng-if="view.showBatchLabel==0"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-unread-status" ng-click="readNews($event,icc)" data-tippy="" title="标已读"> </button> </li>'+
								//'<li class="inline-block ng-hide" style="float: left;" ng-show="icc.readFlag==\'1\' &amp;&amp; view.resultPresent != 3"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-read-status waves-effect waves-light color-blue" data-tippy="" data-original-title="已读"> </button> </li>'+
								//'<li class="inline-block dropdown" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle" data-tippy="" title="上报"> </button> <ul class="dropdown-menu" role="menu"> <li role="presentation"> <a href="#group_fenfa" ng-click="groupDistribute(icc,1);" data-toggle="modal" role="menuitem"> <span class="fa-key">上报</span> </a> </li>  </ul> </li>'+
								'<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-trash dropdown-toggle" data-tippy="" title="移除"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> ' +





								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span data-time="' + pub_data + '"  data-type="' + article_public_id + '" data-flag="1" class="deletedata">删除信息</span> </a> </li>' +
								//'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span   data-type="'+article_public_id+'" data-flag="2" class="fa-key deletedata">删除信息并排除来源</span> </a> </li> '+
								'</ul> </li>' +

								'<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> ' +
								'<button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-re-analysis dropdown-toggle" data-tippy="" title="情感标注"> </button>' +
								'<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">' +
								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="1">正面</span> </a> </li>' +
								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="2">中性</span> </a> </li>' +
								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="3">负面</span> </a> </li> ' +
								'</ul>' +
								'</li>' +

								//read+
								'<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-suyuan dropdown-toggle" data-tippy="" data-original-title="已读、未读"> </button>' +
								'<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">  ' +
								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span data-type="' + article_public_id + '" data-flag="1" class="isread">已读</span> </a> </li>' +
								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span data-type="' + article_public_id + '" data-flag="2" class="isread">未读</span> </a> </li> </ul> </li>' +

								//'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="创建跟踪项"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="track" data-type="'+article_public_id+'" data-keywords="'+relatedWord.join("，")+'" ng-bind="collect.name">创建跟踪项</span> </a> </li> </ul> </li>'+




								'</ul></div></span>';

							let strLikeEnd = '</div>';
							let strContentEnd = '</div>';
							let strEnd = '</div><hr>';
							strAll += strStart + strCheck + strContentStart
									+ strTitle + strContent + strLikeStrat
									+ strSource + strKeywords +kuaijie+ strEmotion
									+ strLikeEnd + strcompanyandgov +strContentEnd + strEnd;
						} else {
							let strStart = '<div class="wb-content just-bet">';
							let strCheck = '<div class="monitor-check"><input type="checkbox" data-index="'
									+ article_public_id
									+ '" id="check3"><span></span></div>';
							let strContentStart = '<div class="monitor-right">';
							let websitelogoStr = '<span class="content-logo" style="background: url('
									+ websitelogo + ');"></span>';
							if (full_type == 38) {
								websitelogoStr = '';
							}
							let strTitle = '<div class="monitor-content-title"><a target="_blank" href="'
									+ ctx
									+ 'fullsearch/detail/'
									+ article_public_id
									+ '?fulltype='
									+ full_type
									+ '&menuStyle='
									+ menuStyle
									+ '&fullpoly='
									+ full_poly
									+ '&publish_time='
									+ pub_data
									+ '&searchWord='
									+ $('#searchWord').val()
									+ '"  class="link font-bold">'
									+ websitelogoStr
									+ title
									+ '</a>';
							// let strTitle = '<div
							// class="monitor-content-title"><a
							// onclick="toDetail(&apos;' + article_public_id +
							// '&apos;,&apos;' + monitor_groupid +
							// '&apos;,&apos;' + monitor_projectid + '&apos;)"
							// class="link font-bold">' + title + '</a><span
							// class="sl-date ">' + publish_time +
							// '</span></div>';
							let category = dealCate(article_category);

							if (industrylable != '') {
								strTitle += '<span class="sl-date industrylable" >' + industrylable + ' </span>';
							}

							if (eventlable != '') {
								strTitle += '<span class="sl-date eventlable">' + eventlable + ' </span>';
							}

							if (category != '') {
								strTitle += '<span class="sl-date categorylable">' + category + ' </span>';
							}

							strTitle += '<span class="sl-date  ">' + publish_time + '</span></div>';

							let strContent = '<div class="monitor-content-con font-13">'
									+ content + '</div>';
							let strLikeStart = '<div class="like-comm m-t-10 font-13">';
							let strSource = '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 '
									+ sourcewebsitename + '</span>';
							let strKeywors = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 关键词 '
									+ key_words + '</span>';
							let strEmotion = '';
							if (emotionalIndex == 1) {
								strEmotion = '<span class="link f-right moodzm">正面</span>';
							} else if (emotionalIndex == 2) {
								strEmotion = '<span class="link f-right moodzx">中性</span>';
							} else if (emotionalIndex == 3) {
								strEmotion = '<span class="link f-right moodfm">负面</span>';
							}
							let strLikeEnd = '</div>';
							let strContentEnd = '</div>';
							let strEnd = '</div><hr>';

							let strcompanyandgov = '<div class="like-comm font-13"><span class="link m-r-10">'
							let org = JSON.parse(ner).org;
							let orgstr = "";
							var orgflag = 1;
							for (var key in org) {
								orgflag++;
								orgstr += '<a style="" class="ntag" href="###">' + key + '</a>'
								if (orgflag == 3) break;
							}
							strcompanyandgov = strcompanyandgov + orgstr + '</span><span class="link m-r-10">'
							//政府机构
							let nto = JSON.parse(ner).nto;
							let ntovstr = "";
							var netoflag = 1;
							for (var key in nto) {
								netoflag++;
								strcompanyandgov += '<a style="" class="govtag" href="###">' + key + '</a>'
								if (netoflag == 3) break;
							}
							strcompanyandgov += '</span>';

							//上市公司
							strcompanyandgov += '<span class="link m-r-10">';
							let ipo = JSON.parse(ner).IPO;
							let ipostr = "";
							var ipoflag = 1;
							debugger;
							for (var key in ipo) {
								ipoflag++;
								strcompanyandgov += '<a style="" class="ipotag" href="###">' + key + '</a>'
								if (ipoflag == 3) break;
							}
							strcompanyandgov += '</span></div>';

							let kuaijie = '<span class="link m-r-0">' +
								'<div class="btn-group inline-block" role="group"> <ul class="font-size-0" style="list-style-type: none;">' +
								'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-collection dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="添加至收藏夹"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="ng-binding datafavorite" data-type="' + article_public_id + '" ng-bind="collect.name">收藏夹</span> </a> </li> </ul> </li>' +
								//   						'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-news-material dropdown-toggle ng-scope" id="sck_916007411683191177512726" data-tippy="" title="发送、移动"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="briefFoldersList!=null&amp;&amp;briefFoldersList.length>0"> <li role="presentation" ng-repeat="brief in briefFoldersList" ng-if="brief!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(brief.folderId,2,icc)"> <span class="ng-binding sending" data-type="'+article_public_id+'" >发送、移动</span> </a> </li>  </ul> </li>'+
								//    						'<li class="inline-block dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-send-way dropdown-toggle ng-scope" data-tippy="" title="舆情下发渠道"></button> <ul class="dropdown-menu pt15 pb15" role="menu"> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(2,icc,1);"> <span class="fa-key">短信下发</span> </a> </li> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(1,icc,1);"> <span class="fa-key">邮件下发</span> </a> </li></ul> </li>'+
								'<li class="inline-block" style="float: left;"> <a href="' + source_url + '" target="_blank" class=""> <button type="button" data-tippy-placement="top" aria-expanded="false" class="tippy btn btn-default-icon fa-check-origin-link" data-tippy="" data-original-title="查看原文"></button> </a> </li>' +
								'<li class="inline-block dropdown" style="float: left;"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-copy-direct dropdown-toggle" data-tippy="" data-original-title="复制"> </button> <ul class="dropdown-menu" role="menu"> ' +



								'<li role="presentation"> <a href="javascript:void(0)" id="link_916007411683191177512726" role="menuitem" ng-click="copyLink(icc.webpageUrl,icc.id);"> <span class="copyurl" data-type="' + article_public_id + '" data-flag="' + source_url + '" >拷贝地址</span> </a> </li>' +
								'<li role="presentation"> <a href="javascript:void(0)" class="copy-link-custom" role="menuitem" ng-click="aKeyToCopy(icc);"> <span class="copytext" data-type="' + article_public_id + '" data-flag="' + copytext + '" >一键复制</span> </a> </li> </ul> </li>' +
								//'<li class="inline-block ng-scope" style="float: left;" ng-show="icc.readFlag==undefined &amp;&amp; view.resultPresent != 3" ng-if="view.showBatchLabel==0"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-unread-status" ng-click="readNews($event,icc)" data-tippy="" title="标已读"> </button> </li>'+
								//'<li class="inline-block ng-hide" style="float: left;" ng-show="icc.readFlag==\'1\' &amp;&amp; view.resultPresent != 3"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-read-status waves-effect waves-light color-blue" data-tippy="" data-original-title="已读"> </button> </li>'+
								//'<li class="inline-block dropdown" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle" data-tippy="" title="上报"> </button> <ul class="dropdown-menu" role="menu"> <li role="presentation"> <a href="#group_fenfa" ng-click="groupDistribute(icc,1);" data-toggle="modal" role="menuitem"> <span class="fa-key">上报</span> </a> </li>  </ul> </li>'+
								'<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-trash dropdown-toggle" data-tippy="" title="移除"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> ' +





								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span data-time="' + pub_data + '"  data-type="' + article_public_id + '" data-flag="1" class="deletedata">删除信息</span> </a> </li>' +
								//'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span   data-type="'+article_public_id+'" data-flag="2" class="fa-key deletedata">删除信息并排除来源</span> </a> </li> '+
								'</ul> </li>' +

								'<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> ' +
								'<button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-re-analysis dropdown-toggle" data-tippy="" title="情感标注"> </button>' +
								'<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">' +
								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="1">正面</span> </a> </li>' +
								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="2">中性</span> </a> </li>' +
								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="3">负面</span> </a> </li> ' +
								'</ul>' +
								'</li>' +

								//read+
								'<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-suyuan dropdown-toggle" data-tippy="" data-original-title="已读、未读"> </button>' +
								'<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">  ' +
								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span data-type="' + article_public_id + '" data-flag="1" class="isread">已读</span> </a> </li>' +
								'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span data-type="' + article_public_id + '" data-flag="2" class="isread">未读</span> </a> </li> </ul> </li>' +

								//'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="创建跟踪项"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="track" data-type="'+article_public_id+'" data-keywords="'+relatedWord.join("，")+'" ng-bind="collect.name">创建跟踪项</span> </a> </li> </ul> </li>'+




								'</ul></div></span>';


							strAll += strStart + strCheck + strContentStart
									+ strTitle + strContent + strLikeStart
									+ strSource + strKeywors +kuaijie+ strEmotion
									+ strLikeEnd +strcompanyandgov+ strContentEnd + strEnd;
						}
					} else {
						let strStart = '<div class="wb-content just-bet">';
						let strCheck = '<div class="monitor-check"><input type="checkbox" data-index="'
								+ article_public_id
								+ '" id="check3"><span></span></div>';
						let strContentStart = '<div class="monitor-right">';
						let websitelogoStr = '<span class="content-logo" style="background: url('
								+ websitelogo + ');"></span>';
						if (full_type == 38) {
							websitelogoStr = '';
						}
						let strTitle = '<div class="monitor-content-title"><a target="_blank" href="'
								+ ctx
								+ 'fullsearch/detail/'
								+ article_public_id
								+ '?fulltype='
								+ full_type
								+ '&menuStyle='
								+ menuStyle
								+ '&fullpoly='
								+ full_poly
								+ '&publish_time='
								+ pub_data
								+ '&searchWord='
								+ $('#searchWord').val()
								+ '"  class="link font-bold">'
								+ websitelogoStr
								+ title
								+ '</a>';

						let category = dealCate(article_category);

						if (industrylable != '') {
							strTitle += '<span class="sl-date industrylable" >' + industrylable + ' </span>';
						}

						if (eventlable != '') {
							strTitle += '<span class="sl-date eventlable">' + eventlable + ' </span>';
						}

						if (category != '') {
							strTitle += '<span class="sl-date categorylable">' + category + ' </span>';
						}

						strTitle += '<span class="sl-date  ">' + publish_time + '</span></div>';

						// let strTitle = '<div class="monitor-content-title"><a
						// onclick="toDetail(&apos;' + article_public_id +
						// '&apos;,&apos;' + monitor_groupid + '&apos;,&apos;' +
						// monitor_projectid + '&apos;)" class="link
						// font-bold">' + title + '</a><span class="sl-date ">'
						// + publish_time + '</span></div>';
						let strContent = '<div class="monitor-content-con font-13">'
								+ content + '</div>';
						let strLikeStart = '<div class="like-comm m-t-10 font-13">';
						let strSource = '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 '
								+ sourcewebsitename + '</span>';
						let strKeywors = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 关键词 '
								+ key_words + '</span>';
						let strEmotion = '';
						if (emotionalIndex == 1) {
							strEmotion = '<span class="link f-right moodzm">正面</span>';
						} else if (emotionalIndex == 2) {
							strEmotion = '<span class="link f-right moodzx">中性</span>';
						} else if (emotionalIndex == 3) {
							strEmotion = '<span class="link f-right moodfm">负面</span>';
						}
						let strLikeEnd = '</div>';
						let strContentEnd = '</div>';
						let strEnd = '</div><hr>';

						let strcompanyandgov = '<div class="like-comm font-13"><span class="link m-r-10">'
						let org = JSON.parse(ner).org;
						let orgstr = "";
						var orgflag = 1;
						for (var key in org) {
							orgflag++;
							orgstr += '<a style="" class="ntag" href="###">' + key + '</a>'
							if (orgflag == 3) break;
						}
						strcompanyandgov = strcompanyandgov + orgstr + '</span><span class="link m-r-10">'
						//政府机构
						let nto = JSON.parse(ner).nto;
						let ntovstr = "";
						var netoflag = 1;
						for (var key in nto) {
							netoflag++;
							strcompanyandgov += '<a style="" class="govtag" href="###">' + key + '</a>'
							if (netoflag == 3) break;
						}
						strcompanyandgov += '</span>';

						//上市公司
						strcompanyandgov += '<span class="link m-r-10">';
						let ipo = JSON.parse(ner).IPO;
						let ipostr = "";
						var ipoflag = 1;
						debugger;
						for (var key in ipo) {
							ipoflag++;
							strcompanyandgov += '<a style="" class="ipotag" href="###">' + key + '</a>'
							if (ipoflag == 3) break;
						}
						strcompanyandgov += '</span></div>';

						let kuaijie = '<span class="link m-r-0">' +
							'<div class="btn-group inline-block" role="group"> <ul class="font-size-0" style="list-style-type: none;">' +
							'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-collection dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="添加至收藏夹"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="ng-binding datafavorite" data-type="' + article_public_id + '" ng-bind="collect.name">收藏夹</span> </a> </li> </ul> </li>' +
							//   						'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-news-material dropdown-toggle ng-scope" id="sck_916007411683191177512726" data-tippy="" title="发送、移动"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="briefFoldersList!=null&amp;&amp;briefFoldersList.length>0"> <li role="presentation" ng-repeat="brief in briefFoldersList" ng-if="brief!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(brief.folderId,2,icc)"> <span class="ng-binding sending" data-type="'+article_public_id+'" >发送、移动</span> </a> </li>  </ul> </li>'+
							//    						'<li class="inline-block dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-send-way dropdown-toggle ng-scope" data-tippy="" title="舆情下发渠道"></button> <ul class="dropdown-menu pt15 pb15" role="menu"> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(2,icc,1);"> <span class="fa-key">短信下发</span> </a> </li> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(1,icc,1);"> <span class="fa-key">邮件下发</span> </a> </li></ul> </li>'+
							'<li class="inline-block" style="float: left;"> <a href="' + source_url + '" target="_blank" class=""> <button type="button" data-tippy-placement="top" aria-expanded="false" class="tippy btn btn-default-icon fa-check-origin-link" data-tippy="" data-original-title="查看原文"></button> </a> </li>' +
							'<li class="inline-block dropdown" style="float: left;"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-copy-direct dropdown-toggle" data-tippy="" data-original-title="复制"> </button> <ul class="dropdown-menu" role="menu"> ' +



							'<li role="presentation"> <a href="javascript:void(0)" id="link_916007411683191177512726" role="menuitem" ng-click="copyLink(icc.webpageUrl,icc.id);"> <span class="copyurl" data-type="' + article_public_id + '" data-flag="' + source_url + '" >拷贝地址</span> </a> </li>' +
							'<li role="presentation"> <a href="javascript:void(0)" class="copy-link-custom" role="menuitem" ng-click="aKeyToCopy(icc);"> <span class="copytext" data-type="' + article_public_id + '" data-flag="' + copytext + '" >一键复制</span> </a> </li> </ul> </li>' +
							//'<li class="inline-block ng-scope" style="float: left;" ng-show="icc.readFlag==undefined &amp;&amp; view.resultPresent != 3" ng-if="view.showBatchLabel==0"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-unread-status" ng-click="readNews($event,icc)" data-tippy="" title="标已读"> </button> </li>'+
							//'<li class="inline-block ng-hide" style="float: left;" ng-show="icc.readFlag==\'1\' &amp;&amp; view.resultPresent != 3"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-read-status waves-effect waves-light color-blue" data-tippy="" data-original-title="已读"> </button> </li>'+
							//'<li class="inline-block dropdown" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle" data-tippy="" title="上报"> </button> <ul class="dropdown-menu" role="menu"> <li role="presentation"> <a href="#group_fenfa" ng-click="groupDistribute(icc,1);" data-toggle="modal" role="menuitem"> <span class="fa-key">上报</span> </a> </li>  </ul> </li>'+
							'<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-trash dropdown-toggle" data-tippy="" title="移除"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> ' +





							'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span data-time="' + pub_data + '"  data-type="' + article_public_id + '" data-flag="1" class="deletedata">删除信息</span> </a> </li>' +
							//'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span   data-type="'+article_public_id+'" data-flag="2" class="fa-key deletedata">删除信息并排除来源</span> </a> </li> '+
							'</ul> </li>' +

							'<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> ' +
							'<button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-re-analysis dropdown-toggle" data-tippy="" title="情感标注"> </button>' +
							'<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">' +
							'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="1">正面</span> </a> </li>' +
							'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="2">中性</span> </a> </li>' +
							'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="3">负面</span> </a> </li> ' +
							'</ul>' +
							'</li>' +

							//read+
							'<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-suyuan dropdown-toggle" data-tippy="" data-original-title="已读、未读"> </button>' +
							'<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">  ' +
							'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span data-type="' + article_public_id + '" data-flag="1" class="isread">已读</span> </a> </li>' +
							'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span data-type="' + article_public_id + '" data-flag="2" class="isread">未读</span> </a> </li> </ul> </li>' +

							//'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="创建跟踪项"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="track" data-type="'+article_public_id+'" data-keywords="'+relatedWord.join("，")+'" ng-bind="collect.name">创建跟踪项</span> </a> </li> </ul> </li>'+




							'</ul></div></span>';


						strAll += strStart + strCheck + strContentStart
								+ strTitle + strContent + strLikeStart
								+ strSource + strKeywors +kuaijie+ strEmotion
								+ strLikeEnd+strcompanyandgov+strContentEnd + strEnd;
						// console.log(strAll);
					}
				}
				$("#monitor-content").html(strAll);
			}
		} else {
			$("#page").html("");
			nodata('#monitor-content');
		}
	} else {
		$("#page").html("");
		dataerror("#monitor-content");
	}
}

$('#date-range').datepicker({
	language : 'zh-CN',
	format : "yyyy-mm-dd",
	orientation : "bottom auto",
	toggleActive : true,
	keyboardNavigation : true,
	enableOnReadonly : false,
	todayHighlight : true,
	endDate : getnow(),
	autoclose : true,
});

function getnow() {
	var now = new Date();
	var nowday = now.getFullYear() + "-" + (now.getMonth() + 1) + "-"
			+ now.getDate();
	return nowday
}

/**
 * ajax 异步
 * 
 * @param type
 * @param url
 * @param data
 * @param funcname
 * @returns
 */
function ajaxAsync(type, url, data, funcname) {
	$.ajax({
		type : type,
		url : url,
		dataType : 'json',
		data : data,
		async : true,
		contentType : 'application/json;charset=utf-8',
		beforeSend : function() {
			loading("#monitor-content")
		},
		success : function(res) {
			funcname(res);
		},
		error : function(xhr, ajaxOptions, thrownError) {
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
 * list去重
 * @returns
 */
		
function deduplication(arr){
//	   = this,
	   var i,
	  obj = {},
	  result = [],
	  len = arr.length;
	 for(i = 0; i< arr.length; i++){
	  if(!obj[arr[i]]){ //如果能查找到，证明数组元素重复了
	   obj[arr[i]] = 1;
	   result.push(arr[i]);
	  }
	 }
	 return result;
	};
	
	/**
	 * html去除标签
	 * @returns
	 */	
	
	function delHtmlTag(str){
		var result = str.replace(/<[^>]+>/g,"");
		result = result.replace(/>/g,"");
		result = result.replace(/</g,"");
		//result = result.replace(/:/g,"");
		result = result.replace(/;/g,"");
		result = result.replace(/p/g,"");
		result = result.replace(/t/g,"");
		　　return result;
		}
	/**
	 * 去除时分秒
	 * @returns
	 */	
	
	function deltime(date){
		
		var newDate=/\d{4}-\d{1,2}-\d{1,2}/g.exec(date)
		return newDate;
		}
