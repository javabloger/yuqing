//if(!full_type) full_type = 1
if(menuStyle == 0){
	listFullPolymerization()
}else{
	listFullTypeByFirst()
}

/**
 * 展开模式菜单
 * @returns
 */
function listFullTypeByFirst(){
	$.ajax({
        type: 'GET',
        url: ctx + 'fullsearch/listFullTypeByFirst',
        dataType: 'json',
        data: {},
        contentType: 'application/json;charset=utf-8',
        beforeSend: function () {
            loading("#monitor-content")
        },
        success: function (res) {
        	console.log(full_type)
        	if(!full_type) full_type = 1
            for (var i = 0; i < res.length; i++) {
            	var aaa = 'sidebar-item'
            	if(full_type == res[i].id){
            		aaa = 'sidebar-item comactive'
            	}
				var htmlStr = '<li class="'+aaa+'" data-id="'+res[i].id+'">'
								+ '<a class="sidebar-link waves-effect waves-dark" href="javascript:void(0)">'
								+ '		<i class="'+res[i].icon+'"></i>'
								+ '		<span class="hide-menu">' + res[i].name + '</span>'
								+ '	</a>'
								+ '</li>'
				$('#sidebarnav').append(htmlStr)
			}
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
 * 聚合模式菜单
 * @returns
 */
function listFullPolymerization(){
	$.ajax({
        type: 'GET',
        url: ctx + 'fullsearch/listFullPolymerization',
        dataType: 'json',
        data: {},
        contentType: 'application/json;charset=utf-8',
        beforeSend: function () {
            loading("#monitor-content")
        },
        async: false,
        success: function (res) {
            for (var i = 0; i < res.length; i++) {
            	var aaa = 'sidebar-item'
            	console.log(full_poly+"    "+res[i].id)
            	if(full_poly == res[i].id){
            		aaa = 'sidebar-item comactive'
            		idList = res[i].value
            	}
				var htmlStr = '<li class="'+aaa+'" data-id="'+res[i].value+'" data-poly="'+res[i].id+'">'
								+ '<a class="sidebar-link waves-effect waves-dark" href="javascript:void(0)">'
								+ '		<i class="'+res[i].icon+'"></i>'
								+ '		<span class="hide-menu">' + res[i].name + '</span>'
								+ '	</a>'
								+ '</li>'
				$('#sidebarnav').append(htmlStr)
			}
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
