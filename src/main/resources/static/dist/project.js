/**
 * @author huajiancheng
 * @date 2020/04/14
 * @description 请求方案组数据
 */
let groupid = '';
let currentPage = 1;
let projectsearch = '';


function sendGroup() {
    $.ajax({
        type: "POST",
        url: ctxPath + "project/groupandproject",
        dataType: 'json',
        data: {},
        success: function (res) {
            console.log(res);
            installGroup(res);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                console.log(thrownError);
            }
        }
    });
}

/**
 * 获取方案组id
 * kanshicheng
 * @param res
 */
function getgroupId(res) {
    groupid = res;
}


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 获取当前页信息
 */
function getCurrentPage(res) {
    currentPage = res;
}

/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 获取搜索关键词
 */
function getProjectsearch(res) {
    projectsearch = res;
}

/**
 * @author huajiancheng
 * @date 2020/04/14
 * @description 组装方案组数据
 */
function installGroup(res) {
    let code = res.code;
    let groupInfo = '';
    let addGroup = '<li class="p-15"><a onclick="createNewPro()" class="btn btn-block create-btn text-white d-flex align-items-center"><i class="fa fa-plus-square"></i><span class="hide-menu m-l-10">新建监测方案</span></a></li>';
    groupInfo = addGroup;  // 整的左侧方案组列表
    if (code == 200) {
        let data = res.data;
        if (data.length > 0) {
            for (let i = 0; i < data.length; i++) {
                let dataJson = data[i];
                let group_id = dataJson.group_id;
                let group_name = dataJson.group_name;
                let groupStr = '';
                if (groupid == '' || groupid == undefined || groupid == null || groupid == 'null') {
                    if (i == 0) {
                        groupid = group_id;
                        groupStr = '<li class="sidebar-item" onclick="getProjectList(&apos;' + group_id + '&apos;, this)"><a class="sidebar-link waves-effect waves-dark active"  aria-expanded="false"><i class="mdi mdi-adjust"></i><span class="hide-menu" data-index="' + group_id + '">' + group_name + '</span><button class="btn btn-sm project-number" style="display: none;"><i class="fas fa-pencil-alt" onclick="editGroup(&apos;' + group_id + '&apos;,&apos;' + group_name + '&apos;)"></i><i onclick="delGroup(&apos;' + group_id + '&apos;)" class="fas fa-trash-alt"></i></button></a></li>';
                        $("#currentGroup").html(group_name);
                    } else {
                        groupStr = '<li class="sidebar-item" onclick="getProjectList(&apos;' + group_id + '&apos;, this)"><a class="sidebar-link waves-effect waves-dark"  aria-expanded="false"><i class="mdi mdi-adjust"></i><span class="hide-menu" data-index="' + group_id + '">' + group_name + '</span><button class="btn btn-sm project-number" style="display: none;"><i class="fas fa-pencil-alt" onclick="editGroup(&apos;' + group_id + '&apos;,&apos;' + group_name + '&apos;)"></i><i onclick="delGroup(&apos;' + group_id + '&apos;)" class="fas fa-trash-alt"></i></button></a></li>';
                    }
                } else {
                    if (group_id == groupid) {
                        groupStr = '<li class="sidebar-item" onclick="getProjectList(&apos;' + group_id + '&apos;, this)"><a class="sidebar-link waves-effect waves-dark active"  aria-expanded="false"><i class="mdi mdi-adjust"></i><span class="hide-menu" data-index="' + group_id + '">' + group_name + '</span><button class="btn btn-sm project-number" style="display: none;"><i class="fas fa-pencil-alt" onclick="editGroup(&apos;' + group_id + '&apos;,&apos;' + group_name + '&apos;)"></i><i onclick="delGroup(&apos;' + group_id + '&apos;)" class="fas fa-trash-alt"></i></button></a></li>';
                        $("#currentGroup").html(group_name);
                    } else {
                        groupStr = '<li class="sidebar-item" onclick="getProjectList(&apos;' + group_id + '&apos;, this)"><a class="sidebar-link waves-effect waves-dark"  aria-expanded="false"><i class="mdi mdi-adjust"></i><span class="hide-menu" data-index="' + group_id + '">' + group_name + '</span><button class="btn btn-sm project-number" style="display: none;"><i class="fas fa-pencil-alt" onclick="editGroup(&apos;' + group_id + '&apos;,&apos;' + group_name + '&apos;)"></i><i onclick="delGroup(&apos;' + group_id + '&apos;)" class="fas fa-trash-alt"></i></button></a></li>';
                    }
                }
                groupInfo += groupStr;
            }
            $("#sidebarnav").html(groupInfo);
            let pathName = window.document.location.pathname;
            if (pathName == "/project") {
                sendProjectList(groupid, projectsearch, currentPage);
            } else if (pathName == "/project/addproject") {
                getGroupName(groupid);
            }
        } else {
            $("#sidebarnav").html(groupInfo);
            let lstHeader = '<div class="table-hover d-flex project-list-box"><div class="project-chb"><div class="custom-control custom-checkbox"><input type="checkbox" class="custom-control-input" id="cst0"><label class="custom-control-label" for="cst0"></label></div></div><div class="project-name">方案名称</div><div class="project-keywords text-over">方案关键词</div><div class="project-times">创建时间</div><div class="project-edit-delete"><i class="mdi mdi-table-edit"></i><i class="mdi mdi-filter-outline"></i><i class="mdi mdi-delete"></i></div></div>';
            let listStr = '<div class="table-hover d-flex project-list-box pro-bt">暂无数据！</div>';
            $("#projectlist").html(lstHeader + listStr);
            $("#cstall").attr("disabled", "disabled");
            $("#cst0").attr("disabled", "disabled");
            showInfo("暂无方案组,请先添加！");
        }
    }

}


/**
 * @author huajiancheng
 * @date 2020/04/21
 * @description 获取方案组名称
 */
function getGroupName(id) {
    $.ajax({
        type: "POST",
        url: ctxPath + 'project/names',
        dataType: 'json',
        data: {
            projectId: null,
            groupId: id
        },
        success: function (res) {
            $("#projectname").html(res.groupName);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                console.log(thrownError);
            }
        }
    });
}

/**
 * @author huajiancheng
 * @date 2020/04/14
 * @description 请求列表数据
 */
function sendProjectList(groupid, projectsearch, page) {
    breadCrumbs(groupid);
    let url = location.pathname + '?page=' + page + "&groupid=" + groupid + "&projectsearch=" + projectsearch;
    setUrl(url);
    $.ajax({
        type: "POST",
        url: ctxPath + "project/listproject",
        dataType: 'json',
        data: {
            groupid: groupid,
            projectsearch: projectsearch,
            page: page
        },
        beforeSend: function () {
            loading("#projectlist");
        },
        success: function (res) {
            console.log(res);
            installProjectList(res);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                console.log(thrownError);
            }
        }
    });
}

/**
 * @author huajiancheng
 * @date 2020/04/14
 * @description 组装列表数据
 */
function installProjectList(res) {
    let code = res.code;
    if (code == 200) {
        createprolist(res);
    } else {
        console.log("列表数据返回异常");
    }
}


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 组装列表数据
 */
function createprolist(res) {
    console.log(res)
    $("#page").html("");
    let prodata = res.data;
    let totalPage = res.totalPage;
    let totalData = res.totalData;
    let currentPage = res.page;

    $("#totalProject").html(totalData);
    var data = prodata;
    var listtr = '';
    if (data == undefined) {
        data = []
    }
    // let lstHeader = '<div class="table-hover d-flex project-list-box"><div class="project-chb"><div class="custom-control custom-checkbox"><input type="checkbox" class="custom-control-input" id="cst0"><label class="custom-control-label" for="cst0"></label></div></div><div class="project-name">方案名称</div><div class="project-keywords text-over">方案关键词</div><div class="project-times">创建时间</div><div class="project-edit-delete"><i class="mdi mdi-table-edit"></i><i class="mdi mdi-filter-outline"></i><i class="mdi mdi-delete"></i></div></div>';
    if (data.length > 0) {
        pageHleper(currentPage, totalPage);
        for (var i = 0; i < data.length; i++) {
            listtr +=
                '<div class="table-hover d-flex project-list-box pro-bt">' +
                '    <div class="project-chb">' +
                '        <div class="custom-control custom-checkbox">' +
                '            <input type="checkbox" class="custom-control-input projectCheck" data-projectid=' + data[i].project_id + ' id="cst' + (i + 1) + '">' +
                '            <label class="custom-control-label" for="cst' + (i + 1) + '"></label>' +
                '        </div>' +
                '    </div>' +
                '    <div class="project-name">' +
                data[i].project_name +
                '    </div>' +
                '    <div class="project-keywords text-over"><a href="/project/detail?groupid='+groupid+'&projectid=' + data[i].project_id + '">' +
                data[i].subject_word +
                '    </a></div>' +
                '    <div class="project-times">' +
                data[i].create_time +
                '    </div>' +
                '	<div class="project-handle">'+
            	'		<a href="#" onclick="toAnalysis(&apos;' + data[i].project_id + '&apos;,&apos;' + data[i].group_id + '&apos;)" data-id="' + data[i].project_id + '">监测分析</a>'+
            	'		<a href="#" onclick="toMonitor(&apos;' + data[i].project_id + '&apos;,&apos;' + data[i].group_id + '&apos;)" data-id="' + data[i].project_id + '">数据监测</a>'+
            	'	</div>'+
                '    <div class="project-edit-delete">' +
                '    <i class="fas fas fa-pencil-alt" title="编辑" onclick="proedit(&apos;' + data[i].project_id + '&apos;)" data-id="' + data[i].project_id + '"></i>' +
                '    <i class="mdi mdi-filter phsz" title="偏好设置" data-id=' + data[i].project_id + '></i>' +
                '    <i class="mdi mdi-delete" title="删除" onclick="prodelete(&apos;' + data[i].project_id + '&apos;)" data-id="' + data[i].project_id + '"></i>' +
                '    </div>' +
                '</div>'
        }
        $("#projectlist").html(listtr); // 组装列表数据
    } else {
        nodata("#projectlist", '暂无方案，去创建>', ctxPath + 'project/addproject?groupid='+groupid)
    }
    
}


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 分页信息
 */
function pageHleper(currentPage, totalPages) {
    $("#page").bootstrapPaginator({
        bootstrapMajorVersion: 3, //版本
        currentPage: currentPage, //当前页数
        numberOfPages: 20, //每次显示页数
        totalPages: totalPages, //总页数
        shouldShowPage: true, //是否显示该按钮
        useBootstrapTooltip: false,
        onPageClicked: function (event, originalEvent, type, page) {
            let projectsearch = $("#currentName").val();
            let url = location.pathname + '?page=' + page + "&groupid=" + groupid + "&projectsearch=" + projectsearch;
            setUrl(url);
            sendProjectList(groupid, projectsearch, page);
        }
    });
}

/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 搜索方案名称
 */

function searchProject() {
    let currentName = $("#currentName").val();
    let url = location.pathname + '?page=1' + "&groupid=" + groupid + "&projectsearch=" + currentName
    setUrl(url);
    sendProjectList(groupid, currentName, 1);
}


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 搜索支持回车
 */
$(document).keydown(function (event) {
    if (event.keyCode == 13) {
        let currentName = $("#currentName").val();
        // window.location.href = ctxPath + "project?page=1" + "&groupid=" + groupid + "&projectsearch=" + currentName + "&menu=";
        let url = location.pathname + '?page=1' + "&groupid=" + groupid + "&projectsearch=" + currentName;
        setUrl(url);
        sendProjectList(groupid, currentName, 1);
    }
});


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 新增方案前检查方案组数量
 */
function verifyGroupCount() {
    $.ajax({
        type: "POST",
        url: ctxPath + "project/verifygroup",
        dataType: 'json',
        data: {},
        success: function (res) {
            console.log(res);
            let code = res.code;
            if (code == 200) {
                window.location.href = "/project/addproject?groupid=" + groupid;
            } else {
                showInfo("请先创建方案组！");
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                console.log(thrownError);
            }
        }
    });
}

/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 点击跳转新增方案
 */
function addProject() {
    verifyGroupCount();
}

/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 提示信息框
 * @param message 提示的信息
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


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 创建的时候获取方案组信息
 * @param message 提示的信息
 */
function sendGroupByadd() {
    $.ajax({
        type: "POST",
        url: ctxPath + "project/groupandproject",
        dataType: 'json',
        data: {},
        success: function (res) {
            installGroupByadd(res);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                console.log(thrownError);
            }
        }
    });
}

/**
 * @author huajiancheng
 * @description 组装新建页面方案组数据
 */
function installGroupByadd(res) {
    let code = res.code;
    if (code == 200) {
        let data = res.data;
        let groupStr = '';
        for (let i = 0; i < data.length; i++) {
            let dataJson = data[i];
            let group_id = dataJson.group_id;
            let group_name = dataJson.group_name;
            if (group_id == groupId) {
                groupStr += '<option selected value="' + group_id + '">' + group_name + '</option>'
            } else {
                groupStr += '<option value="' + group_id + '">' + group_name + '</option>'
            }
        }
        $("#groupList").html(groupStr); // 修改方案 普通方案 方案组下拉框
        $("#groupList2").html(groupStr); // 修改方案 高级方案 方案组下拉框
    } else {
        showInfo("出错啦，请稍后再试");
    }
}


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 发送新增方案的信息
 */
function sendProjectInfo(data) {
    //新增方案
    $.ajax({
        url: ctxPath + "project/commitproject",
        type: "post",
        contentType: 'application/json;charset=utf-8',
        data: JSON.stringify(data),
        success: function (res) {
            console.log(res)
            let code = res.code;
            let msg = res.msg;
            if (code == 200) {
                let g_id = res.data.group_id;
                let p_id = res.data.project_id;
                $("#createcomplete").addClass("line-active");
                showInfoResult("方案新增完成", "稍后可以到数据监测中查看该方案的数据监测", g_id, p_id);
            } else {
                showInfo(msg);
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                console.log(thrownError);
            }
        }
    });
}

// 提示
function showInfoResult(title, text, g_id, p_id) {
    swal({
        title: title,
        text: text,
        type: "success",
        showCancelButton: true,
        confirmButtonColor: "#36bea6",
        confirmButtonText: "查看监测",
        cancelButtonColor: "#6c757d",
        cancelButtonText: "我知道了",
        closeOnConfirm: false,
        closeOnCancel: false
    }).then(function (that) {
        console.log(that.value)
        if (that.value) {
            $("#checkmonitor").addClass("line-active")
            window.location.href = ctxPath + "monitor?groupid=" + g_id + "&projectid=" + p_id;
        } else {
            $("#checkmonitor").addClass("line-active")
            window.location.href = ctxPath + "project?groupid=" + g_id + "&projectid=" + p_id;
        }
    });
}

/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 鼠标移动上方案组显示修改和删除
 */
$('#sidebarnav').on('mouseover', 'li', function () {
    let $this = $(this);
    $this.children("a").children("button").css("display", "block");
});

$('#sidebarnav').on('mouseout', 'li', function () {
    let $this = $(this);
    $this.children("a").children("button").css("display", "none");
});


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 修改方案组名称
 */
function editGroup(group_id, name) {
    createEditNewPro(group_id, name);
    window.event ? window.event.cancelBubble = true : e.stopPropagation(); // 阻止冒泡
}


//创建方案组
function editGroupData(group_id, group_name) {
    $.ajax({
        url: ctxPath + "project/editgroup",
        data: {
            group_id: group_id,
            group_name: group_name
        },
        type: "post",
        success: function (res) {
            let code = res.code;
            let msg = res.msg;
            if (code == 200) {
                showtips(msg);
                window.location.href = ctxPath + "project"
            } else {
                showtips(msg);
            }
            //请求成功时处理
            // if (req == "success") {
            //     showtips("修改方案组名成功");
            //     window.location.href = ctxPath + "project"
            // } else {
            //     showtips("修改方案组名失败");
            // }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                console.log(thrownError);
            }
        }
    });
}

/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 删除方案组名称
 */
function delGroup(group_id) {
    window.event ? window.event.cancelBubble = true : e.stopPropagation(); // 阻止冒泡
    $.ajax({
		  type: "POST",
		  url: ctxPath + "project/getProjectCountByGroupId",
		  dataType: 'json',
		  data: {
		      groupId: group_id
		  },
		  success: function (res) {
		      var count = res.count;
		      if(count == 0){
				        var confirmhtml =
				        '<div class="shadebox" id="confirmsure">' +
				        '    <div class="modal-dialog" role="document"> ' +
				        '        <div class="modal-content">' +
				        '            <div class="modal-header align-flexend no-border"> ' +
				        '                <h5 class="modal-title">提示</h5>' +
				        '                <i class="mdi mdi-close-circle-outline font-18 cursor-po" id="closethis"></i>' +
				        '            </div>' +
				        '            <div class="modal-body" style="padding:0 1rem;">' +
				        '                <div class="input-group alcenter"><i class="fa fa-exclamation-circle m-r-10 font-20"></i> 此操作将删除该方案组, 是否继续?' +
				        '                </div>' +
				        '            </div>' +
				        '            <div class="modal-footer no-border">' +
				        '                <button type="button" class="btn btn-info" id="confirm"> 确定</button> ' +
				        '                <button type="button" class="btn btn-secondary" id="cancel">取消</button>' +
				        '            </div>' +
				        '        </div>' +
				        '    </div>' +
				        '</div>'
				    $("body").append(confirmhtml)
				    $("#closethis").click(function (param) {
				        $("#confirmsure").remove()
				    })
				    $("#cancel").click(function (param) {
				        $("#confirmsure").remove()
				    })
				    $("#confirm").click(function (param) {
				        $.ajax({
				            type: "POST",
				            url: ctxPath + "project/updateSolutionGroupStatus",
				            dataType: 'json',
				            data: {
				                groupId: group_id,
				            },
				            success: function (res) {
				            	if(res.state){
				                    sendGroup();
				                    if(group_id == groupid){
				            			groupid = '';
				            		}else{
				            			breadCrumbs();
				            		}
				            	}
				            	showInfo(res.message);
				            },
				            error: function (xhr, ajaxOptions, thrownError) {
				                if (xhr.status == 403) {
				                    window.location.href = ctxPath + "login";
				                } else {
				                    console.log(thrownError);
				                }
				            }
				        });
				        $("#confirmsure").remove()
				    })
		      }else{
		    	  var confirmhtml =
				        '<div class="shadebox" id="confirmsure">' +
				        '    <div class="modal-dialog" role="document"> ' +
				        '        <div class="modal-content">' +
				        '            <div class="modal-header align-flexend no-border"> ' +
				        '                <h5 class="modal-title">提示</h5>' +
				        '                <i class="mdi mdi-close-circle-outline font-18 cursor-po" id="closethis"></i>' +
				        '            </div>' +
				        '            <div class="modal-body" style="padding:0 1rem;">' +
				        '                <div class="input-group alcenter"><i class="fa fa-exclamation-circle m-r-10 font-20"></i> 该方案组下拥有方案，不可删除！' +
				        '                </div>' +
				        '            </div>' +
				        '            <div class="modal-footer no-border">' +
				        '                <button type="button" class="btn btn-info" id="confirm">确定</button> ' +
//				        '                <button type="button" class="btn btn-secondary" id="cancel">取消</button>' +
				        '            </div>' +
				        '        </div>' +
				        '    </div>' +
				        '</div>'
				    $("body").append(confirmhtml)
				    $("#closethis").click(function (param) {
				        $("#confirmsure").remove()
				    })
				    $("#cancel").click(function (param) {
				        $("#confirmsure").remove()
				    })
				    $("#confirm").click(function (param) {
				        $("#confirmsure").remove()
				    })
		      }
		  },
		  error: function (xhr, ajaxOptions, thrownError) {
		      if (xhr.status == 403) {
		          window.location.href = ctxPath + "login";
		      } else {
		          console.log(thrownError);
		      }
		  }
	});

}

/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 点击获取方案组对应的列表
 */
function getProjectList(id, obj) {
    $("#currentName").val("");
    let pathname = window.location.pathname;
    if (pathname == "/project") {
        let $this = $(obj);
        $this.siblings().children("a").removeClass("active"); // 移除兄弟节点高亮
        $this.children("a").addClass("active"); // 添加本节点的高亮
        groupid = id;
        if (id != null) {
            sendProjectList(id, "", 1);
            breadCrumbs(id);
        } else {
            let html = '<li class="breadcrumb-item">监测管理</li>' +
                '<li class="breadcrumb-item">暂无方案组</li>' +
                '<li class="breadcrumb-item">方案列表</li>';
            $('#breadCrumbs').html(html);
            nodata("#monitor-content");
            showInfo("请先创建方案组");
        }
    } else {
        window.location.href = ctxPath + "project?groupid=" + id;
    }
}


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 修改方案组名称弹窗
 */
function createEditNewPro(group_id, name) {
    var create =
        '<div class="shadebox" id="createmodel">' +
        '    <div class="modal-dialog" role="document">' +
        '        <div class="modal-content">' +
        '            <div class="modal-header align-flexend" style="border:none">' +
        '                <h5 class="modal-title"><i class="ti-marker-alt m-r-10"></i>修改方案组名称</h5>' +
        '                <i class="mdi mdi-close-circle-outline font-18 cursor-po" id="closethis"></i>' +
        '            </div>' +
        '            <div class="modal-body">' +
        '                <div class="input-group mb-3">' +
        '                    <button type="button" class="btn " style="background:#cfcfd0"><i class="mdi mdi-mailbox text-white"></i></button>' +
        '                    <input type="text" class="form-control" id="projectName" value="' + name + '" placeholder="输入方案组名称，六个字符" maxlength="6">' +
        '                </div><small>方案组名,最多输入六个字符</small>' +
        '            </div>' +
        '            <div class="modal-footer" style="border:none">' +
        '                <button type="button" class="btn btn-info" id="confirm"> 确定</button>' +
        '                <button type="button" class="btn btn-secondary" id="cancel">取消</button>' +
        '             </div>' +
        '        </div>' +
        '    </div>' +
        '</div>'

    $("body").append(create)
    $("#closethis").click(function (param) {
        $("#createmodel").remove()
    })
    $("#cancel").click(function (param) {
        $("#createmodel").remove()
    })
    $("#confirm").click(function () {
        var name = $("#projectName").val();
        if (name == '' || name == undefined) {
            showtips("方案名称不能为空！")
        } else {
            editGroupData(group_id, name);
            $("#createmodel").remove()
        }
    });
}


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 设置url
 */
function setUrl(url) {
    history.pushState({url: url, title: document.title}, document.title, url)
}


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 监听返回事件
 */
function monitorGoBack() {
    pushHistory()
    window.addEventListener("popstate", function (e) {
        let curWwwPath = window.document.location.href;
        let pathname = window.document.location.pathname;
        let pos = curWwwPath.indexOf(pathname);
        let localhostPath = curWwwPath.substring(0, pos);
        let url = e.state.url;

        if (url.indexOf(localhostPath) == -1) {
            url = localhostPath + url;
        }

        // window.location.reload(false);//跳转后执行的方法
        // window.history.go(-1);window.location.reload()
    }, false);
}

/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 加入监听历史
 */
function pushHistory() {

    var state = {
        title: Date.parse(new Date()),
        url: window.location.href,
        date: Date.parse(new Date())
    };
    window.history.pushState(state, "title", window.location.href);
    // window.history.pushState('forward', 'title',  window.location.href);
    // window.history.forward(1);
}

/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description 删除方案
 */
function prodelete(id) {
    confirmDelete(id);
}


//  确认删除框
function confirmDelete(id) {
    var confirmhtml =
        '<div class="shadebox" id="confirmsure">' +
        '    <div class="modal-dialog" role="document"> ' +
        '        <div class="modal-content">' +
        '            <div class="modal-header align-flexend no-border"> ' +
        '                <h5 class="modal-title">提示</h5>' +
        '                <i class="mdi mdi-close-circle-outline font-18 cursor-po" id="closethis"></i>' +
        '            </div>' +
        '            <div class="modal-body" style="padding:0 1rem;">' +
        '                <div class="input-group alcenter"><i class="fa fa-exclamation-circle m-r-10 font-20"></i> 此操作将删除该方案, 是否继续?' +
        '                </div>' +
        '            </div>' +
        '            <div class="modal-footer no-border">' +
        '                <button type="button" class="btn btn-info" id="confirm"> 确定</button> ' +
        '                <button type="button" class="btn btn-secondary" id="cancel">取消</button>' +
        '            </div>' +
        '        </div>' +
        '    </div>' +
        '</div>'
    $("body").append(confirmhtml)
    $("#closethis").click(function (param) {
        $("#confirmsure").remove()

    })
    $("#cancel").click(function (param) {
        $("#confirmsure").remove()
    })
    $("#confirm").click(function (param) {
        $.ajax({
            type: "POST",
            url: ctxPath + "project/delProject",
            dataType: 'json',
            data: {
                groupid: groupid,
                projectid: id
            },
            success: function (res) {
                console.log(res);
                installProjectList(res);
            },
            error: function (xhr, ajaxOptions, thrownError) {
                if (xhr.status == 403) {
                    window.location.href = ctxPath + "login";
                } else {
                    console.log(thrownError);
                }
            }
        });
        $("#confirmsure").remove()
    })
}

/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description 跳转修改页面
 */
function proedit(id) {
    window.location.href = ctxPath + "project/editproject?projectid=" + id + '&groupid=' + groupid;
}

/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description 获取方案信息
 */
function getEditInfo(edit_projectid) {
    $.ajax({
        url: ctxPath + "project/getedit",
        type: "get",
        data: {
            projectid: edit_projectid
        },
        success: function (res) {
            sendGroupByadd();
            installProjectInfo(JSON.parse(res));
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                console.log(thrownError);
            }
        }
    });
}

/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description 设置修改方案的数据
 */
function installProjectInfo(res) {
    let code = res.code;
    let msg = res.msg;
    if (code == 200) {
        let data = res.data;
        let project_type = data.project_type;
        let group_id = data.group_id;
        let subject_word = data.subject_word;
        let stop_word = data.stop_word;
        let project_name = data.project_name;
        let group_name = data.group_name;
        let character_word = data.character_word;
        let event_word = data.event_word;
        let regional_word = data.regional_word;
        if (project_type == 2) { // 高级方案
            $("#search-tab").find("li").eq(0).removeClass("create-tab-act");
            $("#search-tab").find("li").eq(1).addClass("create-tab-act");
            $("#tab1").css("display","none");
            $("#tab2").css("display","block");
            $("#proname").val(project_name);
            $("#prokeywords").val(subject_word);
            $("#proshield").val(stop_word);
            $("#proarea").val(regional_word);
            $("#properson").val(character_word);
            $("#proaccident").val(event_word);
            
            $("#proname1").val(project_name);
            $("#prokeywords1").val(subject_word);
            $("#proshield1").val(stop_word);
        } else if(project_type == 1){ // 普通方案
        	$("#tab2").css("display","none");
            $("#tab1").css("display","block");
            $("#search-tab").find("li").eq(1).removeClass("create-tab-act");
            $("#search-tab").find("li").eq(0).addClass("create-tab-act");
            $("#proname1").val(project_name);
            $("#prokeywords1").val(subject_word);
            $("#proshield1").val(stop_word);
            
            $("#proname").val(project_name);
            $("#prokeywords").val(subject_word);
            $("#proshield").val(stop_word);
            if(regional_word) $("#proarea").val(regional_word);
            if(character_word) $("#properson").val(character_word);
            if(event_word) $("#proaccident").val(event_word);
        }
        $("#groupname").html(group_name);
        $("#projectname").html(project_name);
    } else {
        showInfo(msg);
    }
}


/**
 * @author huajiancheng
 * @date 2020/04/15
 * @description 发送修改方案的信息
 */
function sendEditProjectInfo(data) {
    $.ajax({
        url: ctxPath + "project/commiteditproject",
        type: "post",
        contentType: 'application/json;charset=utf-8',
        data: JSON.stringify(data),
        success: function (res) {
            console.log(res)
            let code = res.code;
            if (code == 200) {
                let g_id = data.group_id;
                let p_id = data.project_id;
                $("#createcomplete").addClass("line-active");
                showInfoResult("方案修改完成", "稍后可以到数据监测中查看该方案的数据监测", g_id, p_id);
            } else {
                showInfo("修改方案失败!");
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            } else {
                console.log(thrownError);
            }
        }
    });
}


// 顶部面包屑导航
function breadCrumbs(groupId) {
    if (groupId) {
        $.ajax({
            url: ctxPath + 'project/names',
            type: 'post',
            dataType: 'json',
            data: {
                projectId: null,
                groupId: groupId
            },
            success: function (res) {
                var html = '<li class="breadcrumb-item">监测管理</li>' +
                    '<li class="breadcrumb-item">' + res.groupName + '</li>' +
                    '<li class="breadcrumb-item">监测列表</li>';
                $('#breadCrumbs').html(html);
                $('#groupName').html(res.groupName);
            },
            error: function (xhr, ajaxOptions, thrownError) {
                if (xhr.status == 403) {
                    window.location.href = ctxPath + "login";
                } else {
                    var html = '<li class="breadcrumb-item">监测管理</li>' +
                        '<li class="breadcrumb-item">暂无方案组</li>' +
                        '<li class="breadcrumb-item">监测列表</li>';
                    $('#breadCrumbs').html(html);
                    $("#cstall").attr("disabled", "disabled");
                    $("#cst0").attr("disabled", "disabled");
                }
            }
        });
    } else {
        var html = '<li class="breadcrumb-item">监测管理</li>' +
            '<li class="breadcrumb-item">监测列表</li>';
        $('#breadCrumbs').html(html);
    }
}
function getwechatqrcode() {
    $.ajax({
        type: "GET",
        url: ctxPath + "user/getwechatqrcode",
        success: function (res) {
        	console.log("res:"+res)
        	let status = res.status;
    if (status == 200) {
    	let data = res.data;
    	let aaaa = '';
        console.info("ticket:"+data.ticket);
        aaa ='<img src="'+data.ticket+'" alt="wechat" style="width:120px;height:120px;border-radius:4px;">';
        $("#home-footer__bottom-content__share__qrcode").html(aaa);
    }
        },
        error: function (err) {
            console.log(err);
        }
    });
}

function toAnalysis(p_id, g_id) {
    window.location.href = ctxPath + "analysis?groupid=" + g_id + "&projectid=" + p_id;
    window.event ? window.event.cancelBubble = true : e.stopPropagation(); // 阻止冒泡
}


function toMonitor(p_id, g_id) {
    window.location.href = ctxPath + "monitor?groupid=" + g_id + "&projectid=" + p_id;
    window.event ? window.event.cancelBubble = true : e.stopPropagation(); // 阻止冒泡
}