initdata(full_type);
/**
 * 点击左边菜单 加载数据
 * @param full_type
 * @returns
 */
function initdata(full_type){
	$('#company-status-box').hide()
	$('#establish-date-box').hide()
	$('#publish-date-box').hide()
	$('#publish-date-box-1').hide()
	firstBox(full_type)
	let html = '<span data-matchs=1 class="badge badge-pill badge-info">全文匹配</span>'
            +'<span data-matchs=2 class="badge badge-pill badge-light">标题匹配</span>'
            +'<span data-matchs=3 class="badge badge-pill badge-light">正文匹配</span>'
	if(full_type == 1){
		$('#ppinline').html(html)
		$('#similarbox').show()
		$("#sourceName-select").show()
		$('#emotionbox').show()
		$('#matchMethod').hide()
		$('#time-box').show()
		$('#timesbox').show()
		$('#similarNum').hide()
		$("#sourceName-select").hide()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		informationList(1)
	}
	if(full_type == 8){
        $('#ppinline').html(html)
    	$("#sourceName-select").show()
    	if(sourcename){
    		$("#sourceName-select").find("option[text='"+sourcename+"']").attr("selected",true);
    	}
        $('#matchMethod').hide()
		$('#similarbox').hide()
		$('#sortbox').hide()
		$('#matchsbox').hide()
		$('#timesbox').show()
		$('#emotionbox').show()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		thirdBox(9)
		hotList(1)
	}
	if(full_type == 23){
		html = '<span data-matchs=0 class="badge badge-pill badge-info">全部</span>'
            +'<span data-matchs=2 class="badge badge-pill badge-light">正文</span>'
            +'<span data-matchs=1 class="badge badge-pill badge-light">问题</span>'
        $('#ppinline').html(html)
		$('#similarbox').hide()
		$('#sortbox').show()
		$("#sourceName-select").hide()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#matchsbox').show()
		$('#matchMethod').hide()
		$('#similarNum').hide()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		complaintList(1)
	}
	if(full_type == 28){
		$('#sortbox').show()
		$('#similarbox').hide()
		$("#sourceName-select").show()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#matchsbox').hide()
		$('#matchMethod').show()
		$('#similarNum').hide()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		announcementList(1)
		announcementType()
	}
	if(full_type == 35){
		$('#sortbox').show()
		$('#similarbox').hide()
		$('#publish-date-box').show()
		$("#sourceName-select").show()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#matchsbox').hide()
		$('#matchMethod').show()
		$('#similarNum').hide()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		reportList(1)
		reportType()
	}
	if(full_type == 36){
		$('#similarbox').hide()
		$("#sourceName-select").hide()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#matchsbox').show()
		$('#similarNum').hide()
		$('#matchMethod').hide()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		inviteList(1)
	}
	if(full_type == 37){
		$('#similarbox').hide()
		$("#sourceName-select").hide()
		$('#publish-date-box-1').show()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#matchsbox').show()
		$('#similarNum').hide()
		$('#matchMethod').hide()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		biddingList(1)
	}
	if(full_type == 38){
		$('#similarbox').show()
		$("#sourceName-select").hide()
		$('#emotionbox').show()
		$('#time-box').show()
		$('#timesbox').show()
		$('#similarNum').show()
		$('#matchMethod').hide()
		$("#sourceName-select").hide()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		informationList(1);
	}
	if(full_type == 39){
		$('#company-status-box').show()
		$('#establish-date-box').show()
		$('#similarbox').hide()
		$("#sourceName-select").show()
		$('#emotionbox').hide()
		$('#matchMethod').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#similarNum').hide()
		$('#sortbox').hide()
		$('#matchsbox').hide()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		companyList(1)
		companyIndustry()
	}
	if(full_type == 40){
		$('#establish-date-box').hide()
		$('#company-status-box').hide()
		$('#publish-date-box').show()
		$('#similarbox').hide()
		$('#matchMethod').hide()
		$("#sourceName-select").show()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#similarNum').hide()
		$('#sortbox').hide()
		$('#matchsbox').hide()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		investmentList(1)
		investmentType()
	}
	if(full_type == 41){
		$('#establish-date-box').hide()
		$('#company-status-box').hide()
		$('#publish-date-box').hide()
		$('#similarbox').hide()
		$("#sourceName-select").hide()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#matchMethod').hide()
		$('#timesbox').hide()
		$('#similarNum').hide()
		$('#sortbox').show()
		$('#matchsbox').show()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		baiduKnowsList(1)
	}
	if(full_type == 42){
		$('#establish-date-box').hide()
		$('#company-status-box').hide()
		$('#publish-date-box').show()
		$('#similarbox').hide()
		$("#sourceName-select").show()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#matchMethod').hide()
		$('#similarNum').hide()
		$('#sortbox').hide()
		$('#matchsbox').hide()
		$('#filter').show()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		judgmentList(1)
		loadFilterType("judgment")
		judgmentCaseType()
	}
	if(full_type == 43){
		$('#establish-date-box').hide()
		$('#company-status-box').hide()
		$('#publish-date-box').show()
		$('#similarbox').hide()
		$("#sourceName-select").show()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#similarNum').hide()
		$('#sortbox').hide()
		$('#matchMethod').hide()
		$('#matchsbox').hide()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		knowLedgeList(1)
		knowLedgeCaseType()
	}
	if(full_type == 45){
		$('#establish-date-box').hide()
		$('#company-status-box').hide()
		$('#publish-date-box').hide()
		$('#similarbox').hide()
		$("#sourceName-select").hide()
		$('#matchMethod').hide()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#similarNum').hide()
		$('#sortbox').show()
		$('#matchsbox').show()
		$('#filter').hide()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		thesisnList(1)
	}
	if(full_type == 100){
		$('#establish-date-box').hide()
		$('#company-status-box').hide()
		$('#publish-date-box').hide()
		$('#similarbox').hide()
		$("#sourceName-select").hide()
		$('#matchMethod').hide()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#similarNum').hide()
		$('#sortbox').hide()
		$('#matchsbox').hide()
		$('#filter').show()
		$('#lawyer-type').show()
		$('#lawyer-workTime').show()
		lawyerList(1)
		loadFilterType("lawyer");
	}
	if(full_type == 101){
		$('#establish-date-box').hide()
		$('#company-status-box').hide()
		$('#publish-date-box').hide()
		$('#matchMethod').hide()
		$('#similarbox').hide()
		$("#sourceName-select").hide()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#similarNum').hide()
		$('#sortbox').hide()
		$('#matchsbox').hide()
		$('#filter').show()
		$('#lawyer-type').show()
		$('#lawyer-workTime').show()
		executionPersonList(1)
		loadFilterType("executionPerson");
	}
	if(full_type == 102){
		$('#establish-date-box').hide()
		$('#company-status-box').hide()
		$('#publish-date-box').hide()
		$('#similarbox').hide()
		$("#sourceName-select").hide()
		$('#emotionbox').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#similarNum').hide()
		$('#matchMethod').hide()
		$('#sortbox').hide()
		$('#matchsbox').hide()
		$('#filter').show()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		professorList(1)
		loadFilterType("professor");
	}
	if(full_type == 103){
		$('#establish-date-box').hide()
		$('#company-status-box').hide()
		$('#publish-date-box').hide()
		$('#similarbox').hide()
		$("#sourceName-select").hide()
		$('#emotionbox').hide()
		$('#matchMethod').hide()
		$('#time-box').hide()
		$('#timesbox').hide()
		$('#similarNum').hide()
		$('#sortbox').hide()
		$('#matchsbox').hide()
		$('#filter').show()
		$('#lawyer-type').hide()
		$('#lawyer-workTime').hide()
		doctorList(1)
		loadFilterType("doctor");
	}
}

/**
 * 获取二级分类
 * @param full_type
 * @returns
 */
function firstBox(full_type){
	let seturl = ""
	if(menuStyle == 0){
		seturl = "result?" + "searchword=" + $('#searchWord').val() + "&menuStyle=" + menuStyle + "&full_poly=" + full_poly + "&fulltype=" + full_type + "&pageSize="+ pageSize + "&page=1";
	}else{
		seturl = "result?" + "searchword=" + $('#searchWord').val() + "&menuStyle=" + menuStyle + "&fulltype=" + full_type + "&page=1";
	}
    setUrl(seturl);
	var data = {type_one_id:full_type}
	ajax('GET',ctx + 'fullsearch/listFullTypeBySecond', data, setFirstBox);
}

/**
 * 获取三级分类
 * @param full_type
 * @returns
 */
function thirdBox(type_two_id){
	var data = {type_two_id:type_two_id}
	ajax('GET',ctx + 'fullsearch/listFullTypeByThird', data, setThirdBox);
}

/**
 * 加载筛选项
 * @param str
 * @returns
 */
function loadFilterType(str){
	$('#filterType').html("")
	$('#typeRadio').html('')
	switch (str) {
		case 'lawyer':
			$('#filterType').append('<span onclick="setFilterType(\'lawyerName\')" id="lawyerName" data-filterType=0 class="badge badge-pill badge-info">姓名</span>')
			$('#filterType').append('<span onclick="setFilterType(\'lawpace\')" id="lawpace" data-filterType=1 class="badge badge-pill badge-light">律所名称</span>')
			$('#filterType').append('<span onclick="setFilterType(\'lawyerAdept\')" id="lawyerAdept" data-filterType=2 class="badge badge-pill badge-light">擅长领域</span>')
			$('#filterType').append('<span onclick="setFilterType(\'lawyerCity\')" id="lawyerCity" data-filterType=3 class="badge badge-pill badge-light">城市</span>')
			$('#typeRadio').append('<span onclick="setTypeRadio(\'noFilterObj\')" id="noFilterObj" data-typeRadio=0 class="badge badge-pill badge-info">不限</span>')
			$('#typeRadio').append('<span onclick="setTypeRadio(\'specifityLawyer\')" id="specifityLawyer" data-typeRadio=1 class="badge badge-pill badge-light">专职律师</span>')
			break;
		case 'executionPerson':
			$('#filterType').append('<span onclick="setFilterType(\'executionPersonArea\')" id="executionPersonArea" data-filterType=0 class="badge badge-pill badge-info">地区</span>')
			$('#filterType').append('<span onclick="setFilterType(\'executionPersonName\')" id="executionPersonName" data-filterType=1 class="badge badge-pill badge-light">名称</span>')
			$('#typeRadio').append('<span onclick="setTypeRadio(\'noFilterObj\')" id="noFilterObj" data-typeRadio=0 class="badge badge-pill badge-info">不限</span>')
			$('#typeRadio').append('<span onclick="setTypeRadio(\'company\')" id="company" data-typeRadio=1 class="badge badge-pill badge-light">企业</span>')
			$('#typeRadio').append('<span onclick="setTypeRadio(\'person\')" id="person" data-typeRadio=2 class="badge badge-pill badge-light">个人</span>')
			break;
		case 'professor':
			$('#filterType').append('<span onclick="setFilterType(\'professorName\')" id="professorName" data-filterType=0 class="badge badge-pill badge-info">姓名</span>')
			$('#filterType').append('<span onclick="setFilterType(\'professorAdept\')" id="professorAdept" data-filterType=1 class="badge badge-pill badge-light">研究领域</span>')
			$('#filterType').append('<span onclick="setFilterType(\'organization\')" id="organization" data-filterType=2 class="badge badge-pill badge-light">机构</span>')
			break;
		case 'doctor':
			$('#filterType').append('<span onclick="setFilterType(\'doctorName\')" id="doctorName" data-filterType=0 class="badge badge-pill badge-info">姓名</span>')
			$('#filterType').append('<span onclick="setFilterType(\'hospital\')" id="hospital" data-filterType=1 class="badge badge-pill badge-light">医院</span>')
			$('#filterType').append('<span onclick="setFilterType(\'doctorAdept\')" id="doctorAdept" data-filterType=2 class="badge badge-pill badge-light">擅长领域</span>')
			$('#filterType').append('<span onclick="setFilterType(\'doctorDept\')" id="doctorDept" data-filterType=3 class="badge badge-pill badge-light">所属科室</span>')
			break;
		case 'judgment':
			$('#filterType').append('<span onclick="setFilterType(\'all\')" id="all" data-filterType=0 class="badge badge-pill badge-info">全部</span>')
			$('#filterType').append('<span onclick="setFilterType(\'title\')" id="title" data-filterType=1 class="badge badge-pill badge-light">标题</span>')
			$('#filterType').append('<span onclick="setFilterType(\'parties\')" id="parties" data-filterType=3 class="badge badge-pill badge-light">当事人</span>')
			$('#filterType').append('<span onclick="setFilterType(\'court\')" id="court" data-filterType=4 class="badge badge-pill badge-light">法院</span>')
			$('#filterType').append('<span onclick="setFilterType(\'text\')" id="text" data-filterType=2 class="badge badge-pill badge-light">正文</span>')
			$('#filterType').append('<span onclick="setFilterType(\'area\')" id="area" data-filterType=5 class="badge badge-pill badge-light">地区</span>')
			break;
	}
}

/**
 * 全国律师
 * @param page
 * @returns
 */
function lawyerList(page){
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/lawyerList', data, installLawyer);
}

/**
 * 被执行人
 * @param page
 * @returns
 */
function executionPersonList(page){
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/executionPersonList', data, installExecutionPerson);
}

/**
 * 专家人才
 * @param page
 * @returns
 */
function professorList(page){
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/professorList', data, installProfessor);
}

/**
 * 医生
 */
function doctorList(page){
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/doctorList', data, installDoctor);
}

/**
 * 资讯数据
 * @param page
 * @returns
 */
function informationList(page){
	showorhidde();
	var data = getArticleData11(page,$("#searchWord").val());
	let parambasic = new Object(); // ajax基本的参数
	parambasic.contentType = 'application/json;charset=utf-8';
	sendArticleIndustrySearch(parambasic, JSON.stringify(data), funcIndustry);
	sendArticleEventSearch(parambasic, JSON.stringify(data), funcEvent);
	sendArticleProvinceSearch(parambasic, JSON.stringify(data), funcProvince);
	sendArticleCitySearch(parambasic, JSON.stringify(data), funcCity);
	//必须用这个
	// ajaxAsync('GET',ctx + 'fullsearch/informationList', data1, installArticle3);

	ajaxAsync('POST',ctx + 'fullsearch/informationListpost',JSON.stringify(data), installArticle3);
}


function showorhidde() {

	if (full_type != 1) {
		$("#my_serach_information").css("display", "none");
		$("#articlebody").css("margin-top", "");
	}else {
		$("#my_serach_information").css("display", "block");
		$("#articlebody").css("margin-top", "-100px");
	}
}



/**
 * 热门数据
 * @param page
 * @returns
 */
function hotList(page){
	debugger;
	var data = getArticleData(page);
	showorhidde();
	ajaxAsync('GET',ctx + 'fullsearch/hotList', data, installHot);
}

/**
 * 投诉数据
 * @param page
 * @returns
 */
function complaintList(page){
	showorhidde();
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/complaintList', data, installComplaint);
}

/**
 * 公告数据
 * @param page
 * @returns
 */
function announcementList(page){
	showorhidde();
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/announcementList', data, installAnnouncement);
}
/**
 * 公告分类
 * @param page
 * @returns
 */
function announcementType(){
	showorhidde();
	var data = getArticleData(1);
	ajaxAsync('GET',ctx + 'fullsearch/announcementrtype', data, setThirdBox);
}

/**
 * 研报数据
 * @param page
 * @returns
 */
function reportList(page){
	showorhidde();
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/reportList', data, installReport);
}
/**
 * 研报分类
 * @param page
 * @returns
 */
function reportType(){
	showorhidde();
	var data = getArticleData(1);
	ajaxAsync('GET',ctx + 'fullsearch/reportIndustry', data, setThirdBox);
}

/**
 * 招标数据
 * @param page
 * @returns
 */
function biddingList(page){
	showorhidde();
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/biddingList', data, installBidding);
}

/**
 * 招聘数据
 * @param page
 * @returns
 */
function inviteList(page){
	showorhidde();
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/inviteList', data, installInvite);
}

/**
 * 工商数据
 * @param page
 * @returns
 */
function companyList(page){
	showorhidde();
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/companyList', data, installCompany);
}

/**
 * 工商分类
 * @param page
 * @returns
 */
function companyIndustry(){
	var data = getArticleData(1);
	ajaxAsync('GET',ctx + 'fullsearch/companyIndustry', data, setThirdBox);
}

/**
 * 法律文书
 * @param page
 * @returns
 */
function judgmentList(page){
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/judgmentList', data, installJudgment);
}
/**
 * 法律文书案件类型
 * @returns
 */
function judgmentCaseType(){
	var data = getArticleData(1);
	ajaxAsync('GET',ctx + 'fullsearch/judgmentCaseType', data, setThirdBox);
}
/**
 * 知识产权
 * @param page
 * @returns
 */
function knowLedgeList(page){
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/knowLedgeList', data, installKnowLedge);
}
/**
 * 知识产权分类
 * @returns
 */
function knowLedgeCaseType(){
	var data = getArticleData(1);
	ajaxAsync('GET',ctx + 'fullsearch/knowLedgeCaseType', data, setThirdBox);
}

/**
 * 投资融资
 * @param page
 * @returns
 */
function investmentList(page){
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/investmentList', data, installInvestment);
}
/**
 * 投资融资 分类
 * @returns
 */
function investmentType(){
	var data = getArticleData(1);
	ajaxAsync('GET',ctx + 'fullsearch/investmentType', data, setThirdBox);
}

/**
 * 问答数据
 * @returns
 */
function baiduKnowsList(page){
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/baiduKnowsList', data, installBaiduKnows);
}

/**
 * 
 * @param page
 * @returns
 */
function thesisnList(page){
	var data = getArticleData(page);
	ajaxAsync('GET',ctx + 'fullsearch/thesisnList', data, installThesisn);
}


/**
 * @author 获取页面的用户选择的条件
 * @date 2020/04/16
 * @description 分页
 */
function getArticleData(page) {
	debugger;
    let articleData = new Object();
    let times = $("input[name='start']").val();
    let timee = $("input[name='end']").val();
    let timeType; //时间范围
    let emotionalIndex = []; //情感
    let mergeType; //合并
    let sortType; //
    let matchType;
    let classify;
    let status;
    let establish;
    let publish;
    let filterType;
    let filterObject;
    let WorkTime;
    let matchMethod;
    
    let searchkeyword = $('#searchWord').val().trim();
    for (const span of $('#filterType span')) {
		if(span.className.indexOf('badge-info')!=-1){
			filterType=span.textContent
			break
		}
	}
    
    if(searchkeyword===''){
    	filterType=undefined
    }
    for (const span of $('#typeRadio span')) {
		if(span.className.indexOf('badge-info')!=-1){
			filterObject=span.textContent
			break
		}
	}
    WorkTime=$('#lawyerWorkTime').val();
   
    
    $('span[data-emotion]').each(function () {
        if ($(this).hasClass('badge-info')) {
            emotionalIndex.push($(this).data('emotion'));
        }
    });

    $('span[data-similar]').each(function () {
        if ($(this).hasClass('badge-info')) {
        	mergeType = $(this).data('similar');
        }
    });


    $('span[data-sort]').each(function () {
        if ($(this).hasClass('badge-info')) {
        	sortType = $(this).data('sort');
        }
    });

    $('span[data-matchs]').each(function () {
        if ($(this).hasClass('badge-info')) {
        	matchType = $(this).data('matchs');
        }
    });
    
    $('span[data-matchs2]').each(function () {
        if ($(this).hasClass('badge-info')) {
        	matchMethod = $(this).data('matchs2');
        }
    });

    $('span[data-time]').each(function () {
        if ($(this).hasClass('badge-info')) {
            timeType = $(this).data('time');
        }
    });

    $('span[data-precise]').each(function () {
        if ($(this).hasClass('badge-info')) {
            precise = $(this).data('precise');
        }
    });
    
    $('span[data-companystatus]').each(function () {
        if ($(this).hasClass('badge-info')) {
        	status = $(this).data('companystatus');
        }
    });
    
    $('span[data-establish]').each(function () {
        if ($(this).hasClass('badge-info')) {
        	establish = $(this).data('establish');
        }
    });
    $('span[data-publish]').each(function () {
        if ($(this).hasClass('badge-info')) {
        	publish = $(this).data('publish');
        }
    });
    
    if(full_type == 38){
    	classify = 3
    }
    articleData.matchingmode=filterType;
    articleData.kinds=filterObject==='不限' ? undefined : filterObject;
    let month_day=jutils.formatDate(new Date(), "-MM-DD hh:ii:ss")
    if(full_type==100){
    	switch (WorkTime) {
		case '不限':
			times=undefined
			timee=undefined
			break;
		case '一年以内':
			times=jutils.formatDate(new Date(), "YYYY")-1+month_day
			timee=jutils.formatDate(new Date(), "YYYY")+month_day
			break;
		case '一到两年':
			times=jutils.formatDate(new Date(), "YYYY")-2+month_day
			timee=jutils.formatDate(new Date(), "YYYY")-1+month_day
			break;
		case '两到三年':
			times=jutils.formatDate(new Date(), "YYYY")-3+month_day
			timee=jutils.formatDate(new Date(), "YYYY")-2+month_day
			break;
		case '三到四年':
			times=jutils.formatDate(new Date(), "YYYY")-4+month_day
			timee=jutils.formatDate(new Date(), "YYYY")-3+month_day
			break;
		case '四到五年':
			times=jutils.formatDate(new Date(), "YYYY")-5+month_day
			timee=jutils.formatDate(new Date(), "YYYY")-4+month_day
			break;
		case '五到六年':
			times=jutils.formatDate(new Date(), "YYYY")-6+month_day
			timee=jutils.formatDate(new Date(), "YYYY")-5+month_day
			break;
		case '六到七年':
			times=jutils.formatDate(new Date(), "YYYY")-7+month_day
			timee=jutils.formatDate(new Date(), "YYYY")-6+month_day
			break;
		case '七到八年':
			times=jutils.formatDate(new Date(), "YYYY")-8+month_day
			timee=jutils.formatDate(new Date(), "YYYY")-7+month_day
			break;
		case '八到九年':
			times=jutils.formatDate(new Date(), "YYYY")-9+month_day
			timee=jutils.formatDate(new Date(), "YYYY")-8+month_day
			break;
		case '九到十年':
			times=jutils.formatDate(new Date(), "YYYY")-10+month_day
			timee=jutils.formatDate(new Date(), "YYYY")-9+month_day
			break;
		case '十年以上':
			times=jutils.formatDate(new Date(), "YYYY")-100+month_day
			timee=jutils.formatDate(new Date(), "YYYY")-10+month_day
			break;
	}
    }
	
    articleData.times=times;
    articleData.timee=timee;
    debugger;
    if(full_type==8){
    	
   
    if((sourcename=='微博'||sourcename=='微信'||sourcename=='百度')){
    	articleData.classify = 4;
        articleData.website_id = 4;
    	$('#classifybox > span').removeClass('badge-info');
    	$('#classifybox > span:nth-child(2),#classifybox > span:nth-child(1),#classifybox > span:nth-child(3)').addClass('badge-light');
    	$('#classifybox > span:nth-child(4)').removeClass('badge-light');
    	$('#classifybox > span:nth-child(4)').addClass('badge-info');
    	articleData.source_name = sourcename;
    	var twoid = 12
    	if(sourcename=='微博'){
        	articleData.rtype = 1;
            getSource_websitename(twoid,sourcename,articleData.rtype);
    	}else if(sourcename=='微信'){
        	articleData.rtype = 7;
            getSource_websitename(twoid,sourcename,articleData.rtype);
    	}
    	else if(sourcename=='百度'){
        	articleData.rtype = 6;
            getSource_websitename(twoid,sourcename,articleData.rtype);
    	}
    }else if((sourcename=='抖音'||sourcename=='哔哩哔哩'||sourcename=='腾讯视频')){
    	articleData.classify = 2;
        articleData.website_id = 2;
    	$('#classifybox > span').removeClass('badge-info');
    	$('#classifybox > span:nth-child(1),#classifybox > span:nth-child(3),#classifybox > span:nth-child(4)').addClass('badge-light');
    	$('#classifybox > span:nth-child(2)').removeClass('badge-light');
    	$('#classifybox > span:nth-child(2)').addClass('badge-info');
    	articleData.source_name = sourcename;
    	var twoid = 10
    	if(sourcename=='抖音'){
        	articleData.rtype = 1;
            getSource_websitename(twoid,sourcename,articleData.rtype);
    	}else if(sourcename=='哔哩哔哩'){
        	articleData.rtype = 2;
            getSource_websitename(twoid,sourcename,articleData.rtype);
    	}
    	else if(sourcename=='腾讯视频'){
        	articleData.rtype = 4;
            getSource_websitename(twoid,sourcename,articleData.rtype);
    	}
    	
    }else{

    	articleData.source_name = $("#sourceName-select option:selected").text();
        articleData.rtype = $("#sourceName-select option:selected").text();
    }
    }else {
		articleData.source_name = $("#sourceName-select option:selected").text();
		articleData.rtype = $("#sourceName-select option:selected").text();
	}


	debugger;
	let aa = articleData.rtype;
	debugger;
    $('span[data-classify]').each(function () {
        if ($(this).hasClass('badge-info')) {
        	classify = $(this).data('classify');
        }
    });
    articleData.classify = classify;
    articleData.website_id = classify;
    
//    articleData.source_name = $("#sourceName-select option:selected").text();
//    articleData.rtype = $("#sourceName-select option:selected").text();
    
    
    articleData.startTime = times;
    articleData.endTime = timee;
    articleData.pageNum = page;
    articleData.sortType = sortType; // 排序
    articleData.mergeType = mergeType;
    articleData.matchType = matchType;
    articleData.matchMethod = matchMethod;
    articleData.emotions = emotionalIndex.join(',');
    articleData.searchWord = searchkeyword;
    articleData.timeType = timeType;
    
    articleData.status = status;
    articleData.establish = establish;
    articleData.publish = publish;
    articleData.pageSize = pageSize;

	console.log(articleData, "1111111111111111");

    return articleData;
}

function getSource_websitename(twoid,sourcename,rtype){
	//sourcename = $("#sourceName-select option:selected").text()
	let seturl = "result?" + "searchword=" + $('#searchWord').val() + "&fulltype=" + full_type + "&page=1";
    setUrl(seturl);
	if(full_type == 1){
		informationList(1);
	}else if(full_type == 8){
		debugger;
		seturl = seturl + "&sourcename="+sourcename
		setUrl(seturl);
		thirdBox(twoid);
		$("#sourceName-select option").each(function(){
			console.info("data:"+$(this).val());
	        if($(this).val()==rtype){
	            $(this).attr("selected", true);
	        }else{
	        	$(this).attr("selected", false);
	        }
	    });
		
		
		
	}
}








/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description 分页
 */
function pageHelper(currentPage, totalPages) {



    $("#page").bootstrapPaginator({
        bootstrapMajorVersion: 3, //版本
        currentPage: currentPage, //当前页数
        numberOfPages: 10, //每次显示页数
        totalPages: totalPages, //总页数
        shouldShowPage: true, //是否显示该按钮
        useBootstrapTooltip: false,
        onPageClicked: function (event, originalEvent, type, page) {
            if (page > 500) {
                page = 500;
            }
            
            //let seturl = "result?" + "searchword=" + $('#searchWord').val() + "&full_poly=" +full_poly + "&fulltype=" + full_type + "&page=" + page;
            let seturl = ""
        	if(menuStyle == 0){
        		seturl = "result?" + "searchword=" + $('#searchWord').val() + "&menuStyle=" + menuStyle + "&full_poly=" + full_poly + "&fulltype=" + full_type + "&pageSize="+ pageSize + "&page=" + page;
        	}else{
        		seturl = "result?" + "searchword=" + $('#searchWord').val() + "&menuStyle=" + menuStyle + "&fulltype=" + full_type + "&pageSize="+ pageSize + "&page=" + page;
        	}
            
            
            let articleParam = new Object();
            articleParam.type = "GET";
            if(full_type == 8){
            	seturl = seturl + "&sourcename="+sourcename;
            	articleParam.url = ctx + "fullsearch/hotList";
            }else if(full_type == 1){
            	articleParam.url = ctx + "fullsearch/informationList";
            }else if(full_type == 23){
            	articleParam.url = ctx + "fullsearch/complaintList";
            }else if(full_type == 28){
            	seturl = seturl + "&rtype="+sourcename;
            	articleParam.url = ctx + "fullsearch/announcementList";
            }else if(full_type == 35){
            	seturl = seturl + "&rtype="+sourcename;
            	articleParam.url = ctx + "fullsearch/reportList";
            }else if(full_type == 36){
            	seturl = seturl + "&sourcename="+sourcename;
            	articleParam.url = ctx + "fullsearch/inviteList";
            }else if(full_type == 37){
            	seturl = seturl + "&website_id="+website_id;
            	articleParam.url = ctx + "fullsearch/biddingList";
            }else if(full_type == 38){
            	articleParam.url = ctx + "fullsearch/informationList";
            }else if(full_type == 39){
            	articleParam.url = ctx + "fullsearch/companyList";
            }else if(full_type == 40){
            	articleParam.url = ctx + "fullsearch/investmentList";
            }else if(full_type == 41){
            	articleParam.url = ctx + "fullsearch/baiduKnowsList";
            }else if(full_type == 42){
            	articleParam.url = ctx + "fullsearch/judgmentList";
            }else if(full_type == 43){
            	articleParam.url = ctx + "fullsearch/knowLedgeList";
            }else if(full_type == 45){
            	articleParam.url = ctx + "fullsearch/thesisnList";
            }else if(full_type == 100){
            	articleParam.url = ctx + "fullsearch/lawyerList";
            }else if(full_type == 101){
            	articleParam.url = ctx + "fullsearch/executionPersonList";
            }else if(full_type == 102){
            	articleParam.url = ctx + "fullsearch/professorList";
            }else if(full_type == 103){
            	articleParam.url = ctx + "fullsearch/doctorList";
            }
				setUrl(seturl);



            articleParam.contentType = 'application/json;charset=utf-8';
            let articleData = getArticleData(page);
            let similar = articleData.mergeType;
            if (similar == 1) {  // 合并
                let start = 10 * page - 10
                let end = start + 10
                if (end > article_public_idList.length) {
                    end = article_public_idList.length
                }

                let ids = "";
                for (let s = start; s < end; s++) {
                    if (s == (article_public_idList.length - 1)) {
                        ids += article_public_idList[s];
                    } else {
                        ids += article_public_idList[s] + ","
                    }
                }

                let totalPage = 1;
                console.log(article_public_idList.length)
                if (article_public_idList.length % 10 == 0) {
                    totalPage = article_public_idList.length / 10;
                } else {
                    totalPage = Math.ceil(article_public_idList.length/10);
                }
                console.log(ids)
                articleData.article_public_idstr = ids;
                articleData.totalCount = article_public_idList.length;
                articleData.totalPage = totalPage;
                console.log(articleData)
            }
            if(full_type == 8) sendArticle(articleParam, articleData, installHot);
            if(full_type == 1) {
				let articleParam2 = new Object();
				articleParam2.type = "POST";
				articleParam2.url = ctxPath + "fullsearch/informationListpost";
				articleParam2.contentType = 'application/json;charset=utf-8';
				debugger;
            	sendArticleSearch(articleParam2, getArticleData11(page,$('#searchWord').val()), installArticle3);
			}
            if(full_type == 23) sendArticle(articleParam, articleData, installComplaint);
            if(full_type == 28) sendArticle(articleParam, articleData, installAnnouncement);
            if(full_type == 35) sendArticle(articleParam, articleData, installReport);
            if(full_type == 36) sendArticle(articleParam, articleData, installInvite);
            if(full_type == 37) sendArticle(articleParam, articleData, installBidding);
            if(full_type == 38) sendArticle(articleParam, articleData, installArticle3);
            if(full_type == 39) sendArticle(articleParam, articleData, installCompany);
            if(full_type == 40) sendArticle(articleParam, articleData, installInvestment);
            if(full_type == 41) sendArticle(articleParam, articleData, installBaiduKnows);
            if(full_type == 42) sendArticle(articleParam, articleData, installJudgment);
            if(full_type == 43)sendArticle(articleParam, articleData, installKnowLedge);
            if(full_type == 45)sendArticle(articleParam, articleData, installThesisn);
            if(full_type == 100)sendArticle(articleParam, articleData, installLawyer);
            if(full_type == 101)sendArticle(articleParam, articleData, installExecutionPerson);
            if(full_type == 102)sendArticle(articleParam, articleData, installProfessor);
            if(full_type == 103)sendArticle(articleParam, articleData, installDoctor);
            currentPageByDetail = page;
        }
    });
}

/**
 * 搜索按钮
 * @param e
 * @returns
 */
$("#searchBtn").click(function (e) {
	let seturl = ''
	if(menuStyle == 0){
		seturl = "result?" + "searchword=" + $('#searchWord').val() + "&fulltype=" + full_type + "&menuStyle=" + menuStyle + "&sourcename="+sourcename + "&page=1";
	}else{
		seturl = "result?" + "searchword=" + $('#searchWord').val() + "&fulltype=" + full_type + "&menuStyle=" + menuStyle + "&full_poly="+full_poly + "&pageSize=" + pageSize + "&page=1";
	}
	if(full_type == 1){
		//如果是网络资讯
		informationList(1);

	}else if(full_type == 8){
		hotList(1)
	}else if(full_type == 23){
		complaintList(1)
	}else if(full_type == 28){
		announcementList(1)
	}else if(full_type == 35){
		reportList(1)
	}else if(full_type == 36){
		inviteList(1)
	}else if(full_type == 37){
		seturl = seturl + "&website_id=" + website_id
		biddingList(1)
	}else if(full_type == 38){
		informationList(1);
	}else if(full_type == 39){
		companyList(1)
	}else if(full_type == 40){
		investmentList(1)
		investmentType()
	}else if(full_type == 41){
		baiduKnowsList(1)
	}else if(full_type == 42){
		judgmentList(1)
	}else if(full_type == 43){
		knowLedgeList(1)
	}else if(full_type == 45){
		thesisnList(1)
	}else if(full_type == 100){
		lawyerList(1)
	}else if(full_type == 101){
		executionPersonList(1)
	}else if(full_type == 102){
		professorList(1)
	}else if(full_type == 103){
		doctorList(1)
	}
	debugger;
	setUrl(seturl);
});

/**
 * 下拉框
 * @param e
 * @returns
 */
$("#sourceName-select").change(function (e) {
	sourcename = $("#sourceName-select option:selected").text()
	let seturl = "result?" + "searchword=" + $('#searchWord').val() + "&fulltype=" + full_type + "&page=1";
    setUrl(seturl);
	if(full_type == 1){
		informationList(1);
	}else if(full_type == 8){
		seturl = seturl + "&sourcename="+sourcename
		setUrl(seturl);
		hotList(1)
	}else if(full_type == 28){
		seturl = seturl + "&rtype="+sourcename
		announcementList(1)
	}else if(full_type == 35){
		seturl = seturl + "&rtype="+sourcename
		reportList(1)
	}else if(full_type == 39){
		seturl = seturl + "&rtype="+sourcename
		companyList(1)
	}else if(full_type == 40){
		seturl = seturl + "&rtype="+sourcename
		investmentList(1)
	}else if(full_type == 42){
		seturl = seturl + "&rtype="+sourcename
		judgmentList(1)
	}else if(full_type == 43){
		seturl = seturl + "&rtype="+sourcename
		knowLedgeList(1)
	}
});

//数据来源
$("#classifyshow").click(function (e) {
    $(this).hide();
    $("#classifyhide").show();
    $("#classifybox").show();
});

$("#classifyhide").click(function (e) {
    $(this).hide();
    $("#classifyshow").show();
    $("#classifybox").hide();
});

//$("#filterType span").click(function (e) {
//    var active = "badge-info";
//    var normal = "badge-light";
//    var act = $(this).hasClass(active);
//    if (!act) {
//        $(this).siblings().removeClass(active);
//        $(this).siblings().addClass(normal);
//        $(this).removeClass(normal);
//        $(this).addClass(active);
//        if(full_type===100){
//        	lawyerList(1)
//        }else if(full_type===101){
//        	executionPersonList(1)
//        }
//    }
//});
//
//$("#typeRadio span").click(function (e) {
//    var active = "badge-info";
//    var normal = "badge-light";
//    var act = $(this).hasClass(active);
//    if (!act) {
//        $(this).siblings().removeClass(active);
//        $(this).siblings().addClass(normal);
//        $(this).removeClass(normal);
//        $(this).addClass(active);
//        if(full_type===100){
//        	lawyerList(1)
//        }else if(full_type===101){
//        	executionPersonList(1)
//        }
//    }
//});

$('body').on('click', '#classifybox span',function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (!act) {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        var twoid = $(this).attr('data-id')
        website_id = $(this).attr('data-classify')
        if(full_type == 1){
    		informationList(1)
    	}
    	if(full_type == 8){
    		thirdBox(twoid)
    		sourcename = "全部";
    		hotList(1)
    	}else if(full_type == 23){
    		complaintList(1)
    	}else if(full_type == 36){
    		inviteList(1)
    	}else if(full_type == 37){
    		biddingList(1)
    	}
    }
});

$('body').on('click', '#publish-date-1 span',function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (!act) {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        if(full_type == 37){
    		biddingList(1)
    	}
    }
});

//企业状态
$("#company-status span").click(function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    console.log(act)
    if (!act) {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        companyList(1);
    }
});

//企业成立时间
$("#establish-date span").click(function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (!act) {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        companyList(1);
    }
});

// 发布时间
$("#publish-date span").click(function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (!act) {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        if(full_type == 35){
        	reportList(1);
        }
        if(full_type == 40){
        	investmentList(1);
        }
        if(full_type == 42){
        	judgmentList(1);
        }
        if(full_type == 43){
        	knowLedgeList(1)
        }
    }
});

// 相似文章
$("#xsshow").click(function (e) {
    $(this).hide();
    $("#xshide").show();
    $("#xsinline").show();
});

$("#xshide").click(function (e) {
    $(this).hide();
    $("#xsshow").show();
    $("#xsinline").hide();
});

$("#xsinline span").click(function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (!act) {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        informationList(1);
    }
});

// 情感筛选
$("#qgshow").click(function (e) {
    $(this).hide();
    $("#qghide").show();
    $("#emotionList").show();
});

$("#qghide").click(function (e) {
    $(this).hide();
    $("#qgshow").show();
    $("#emotionList").hide();
});

$('body').on('click', '#pageSize-style span',function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (!act) {
    	$(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        pageSize = $(this).attr('data-pagesize')
    }
});

$('body').on('click', '#menu-style span',function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (!act) {
    	$(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        menuStyle = $(this).attr('data-style')
    }
});

$("#emotionList span").click(function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (act) {
        $(this).removeClass(active);
        $(this).addClass(normal);
    } else {
        $(this).removeClass(normal);
        $(this).addClass(active);
    }
    if(full_type == 1){
		informationList(1)
	}
	if(full_type == 8){
		hotList(1)
	}
	if(full_type == 38){
		informationList(1);
	}
});

// 信息排序
$("#inlineList span").click(function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (!act) {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        if(full_type == 1){
    		informationList(1)
    	}
    	if(full_type == 8){
    		hotList(1)
    	}
    	if(full_type == 23){
    		complaintList(1)
    	}
    	if(full_type == 28){
    		announcementList(1)
    	}
    	if(full_type == 35){
    		reportList(1)
    	}
    	if(full_type == 36){
    		inviteList(1)
    	}
    	if(full_type == 37){
    		biddingList(1)
    	}
    	if(full_type == 38){
    		informationList(1);
    	}
    	if(full_type == 41){
    		baiduKnowsList(1);
    	}
    	if(full_type == 45){
    		thesisnList(1);
    	}
    }
});

//匹配方式
$("#ppshow").click(function (e) {
    $(this).hide();
    $("#pphide").show();
    $("#ppinline").show();
});

$("#pphide").click(function (e) {
    $(this).hide();
    $("#ppshow").show();
    $("#ppinline").hide();
});

//匹配方式2
$("#ppshow2").click(function (e) {
    $(this).hide();
    $("#pphide2").show();
    $("#ppinline2").show();
});

$("#pphide2").click(function (e) {
    $(this).hide();
    $("#ppshow2").show();
    $("#ppinline2").hide();
});

$('body').on('click', '#ppinline2 span',function (e) {
	var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (!act) {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
    }
});

$('body').on('click', '#ppinline span',function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (!act) {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        if(full_type == 1){
			informationList(1);
    	}
    	if(full_type == 8){
    		hotList(1)
    	}
    	if(full_type == 23){
    		complaintList(1)
    	}
    	if(full_type == 36){
    		inviteList(1)
    	}
    	if(full_type == 37){
    		biddingList(1)
    	}
    	if(full_type == 38){
    		informationList(1);
    	}
    	if(full_type == 41){
    		baiduKnowsList(1);
    	}
    	if(full_type == 45){
    		thesisnList(1);
    	}
    }
});

// 精准 筛选
//$("#sxshow").click(function (e) {
//    $(this).hide();
//    $("#sxhide").show();
//    $("#sxinline").show();
//});
//
//$("#sxhide").click(function (e) {
//    $(this).hide()
//    $("#sxshow").show();
//    $("#sxinline").hide();
//});
//
//$("#sxinline span").click(function (e) {
//    var active = "badge-info";
//    var normal = "badge-light";
//    var act = $(this).hasClass(active);
//    if (!act) {
//        $(this).siblings().removeClass(active);
//        $(this).siblings().addClass(normal);
//        $(this).removeClass(normal);
//        $(this).addClass(active);
//    }
//});

//time
$("#inlineTime span").click(function () {
    var active = "badge-info";
    var normal = "badge-light";
    var type = $(this).attr("data-time")
    var act = $(this).hasClass(active);
    var timeselect = 1
    if (type == 0) {
        $(this).removeClass(normal);
        $(this).addClass(active)
        $("#time-box").show()
        timeselect = type
    } else {
        for (var i = 0; i < 9; i++) {
            $(".inlineTimebox span[data-time=" + (i) + "]").removeClass(active)
            $(".inlineTimebox span[data-time=" + (i) + "]").addClass(normal)
        }

		// (type > 3
        if (type<4||type>6) {
            $(".inlineTimebox span[data-time=0]").removeClass(normal);
            $(".inlineTimebox span[data-time=0]").addClass(active);
            if (type == 8) {
                $("#date-range").show()
            } else {
                $("#date-range").hide()
            }
        } else {
            $("#time-box").hide()
            $("#date-range").hide()
        }
        $(this).removeClass(normal);
        $(this).addClass(active);
        timeselect = type
        if (timeselect == 8) {
            let myDate = new Date();
            var month = myDate.getMonth() + 1;
            if (month < 10) {
                month = '0' + month;
            }
            var day = myDate.getDate();
            if (day < 10) day = '0' + day;
            let times = myDate.getFullYear() + "-" + month + "-" + day;
            let timee = myDate.getFullYear() + "-" + month + "-" + day;
            articleData.times = times;
            articleData.timee = timee;
            $("#date-range input[name='start']").val(times.trim());
            $("#date-range input[name='end']").val(timee.trim());
        }
        if(full_type == 1){
    		informationList(1)
    	}
    	if(full_type == 8){
    		hotList(1)
    	}
    	if(full_type == 38){
    		informationList(1);
    	}
    }
})

//时间框变化
$("#date-range input").change(function () {
    var times = $("#date-range input[name=start]").val();
    var timed = $("#date-range input[name=end]").val();
    debugger;
    if (times > timed) {
        showInfo("开始时间不能迟于结束时间");
        return
    } else {
        if (times != "" && timed != "") {
        	if(full_type == 1){
        		informationList(1)
        	}
        	if(full_type == 8){
        		hotList(1)
        	}
        	if(full_type == 38){
        		informationList(1);
        	}
        } else {
            showInfo("开始时间或结束时间不能为空");
        }
    }
});







