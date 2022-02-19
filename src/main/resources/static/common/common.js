/**
 * @author huajiancheng
 * @date 2020/04/14
 * @description 公共部分
 */

//监测分析时需要用到默认的第一个groupid和projectid
let analysis_groupid = '';
let analysis_projectid = '';
let groupid = '';
let common_menu = '';
let projectFlag = false;

/**
 * @author huajiancheng
 * @date 2020/04/14
 * @description 获取方案组和方案列表数据
 */
function sendProjectAndProject() {
    $.ajax({
        type: "POST",
        url: ctxPath + "project/getGroupAndProject",
        dataType: 'json',
        data: {
            groupid: groupid,
            page: 1,
            projectsearch: "",
            menu: ""
        },
        async: false,
        success: function (res) {
            installGroupAndProject(res);
            console.log(res)
        },
        error: function (err) {
            console.log(err);
        }
    });
}

//function getwechatqrcode() {
//    $.ajax({
//        type: "GET",
//        url: ctxPath + "user/getwechatqrcode",
//        success: function (res) {
//            console.log("res:" + res)
//            let status = res.status;
//            if (status == 200) {
//                let data = res.data;
//                let aaaa = '';
//                console.info("ticket:" + data.ticket);
//                aaa = '<img src="' + data.ticket + '" alt="wechat" style="width:120px;height:120px;border-radius:4px;">';
//                $("#home-footer__bottom-content__share__qrcode").html(aaa);
//            }
//        },
//        error: function (err) {
//            console.log(err);
//        }
//    });
//}

/**
 * @author huajiancheng
 * @date 2020/04/14
 * @description 组装方案组和方案列表数据
 */
function installGroupAndProject(res) {
    let code = res.code;
    if (code == 200) {
        let data = res.data;
        let flag = res.flag;
        projectFlag = flag;
        if (data.length > 0) {
            let groupStrAll = '<li class="p-15"><a onclick="createNewPro()" class="btn btn-block create-btn text-white d-flex align-items-center"><i class="fa fa-plus-square"></i><span class="hide-menu m-l-10">新建监测方案</span></a></li>';
            for (let i = 0; i < data.length; i++) {
                let dataJson = data[i];
                for (let key in dataJson) {
                    let value = dataJson[key];
                    let group_id = key.split("-")[0];
                    let group_name = key.split("-")[1];
                    let groupStr1 = '';
                    if (analysis_groupid) {
                        if (analysis_groupid == group_id) {
                            // groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark active" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust "></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span><div class="cf"><i  class=" mdi dd mdi-plus-circle" onclick="addProjectByGroupId(this,&apos;' + group_id + '&apos;)"></i>  <i class=" mdi df mdi-plus-circle"></i><i class=" mdi df mdi-plus-circle"></i></div></a><ul aria-expanded="false" class="collapse  first-level in">';
                            groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark active" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust "></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span></a><ul aria-expanded="false" class="collapse  first-level in">';
                        } else {
                            // groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust"></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span><div class="cf"><i  class=" mdi dd mdi-plus-circle" onclick="addProjectByGroupId(this,&apos;' + group_id + '&apos;)"></i><i class=" mdi df mdi-plus-circle"></i><i class=" mdi df mdi-plus-circle"></i></div></a><ul aria-expanded="false" class="collapse  first-level">';
                            groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust"></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span></a><ul aria-expanded="false" class="collapse  first-level">';
                        }
                    } else {
                        if (i == 0) {
                            analysis_groupid = group_id;
                            // groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark active" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust"></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span><div class="cf"><i  class=" mdi dd mdi-plus-circle" onclick="addProjectByGroupId(this,&apos;' + group_id + '&apos;)"></i> < <i class=" mdi df mdi-plus-circle"></i><i class=" mdi df mdi-plus-circle"></i></div></a><ul aria-expanded="false" class="collapse  first-level in">';
                            groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark active" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust"></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span></a><ul aria-expanded="false" class="collapse  first-level in">';
                        } else {
                            // groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust"></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span><div class="cf"><i  class=" mdi dd mdi-plus-circle" onclick="addProjectByGroupId(this,&apos;' + group_id + '&apos;)"></i><i class=" mdi df mdi-plus-circle"></i><i class=" mdi df mdi-plus-circle"></i></div></a><ul aria-expanded="false" class="collapse  first-level">';
                            groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust"></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span></a><ul aria-expanded="false" class="collapse  first-level">';
                        }
                    }
                    let projectStr = '';
                    for (let j = 0; j < value.length; j++) {
                        let valueJson = value[j];
                        let project_id = valueJson.project_id;
                        let project_name = valueJson.project_name;
                        if (!analysis_projectid) {
                            if (j == 0 && analysis_groupid == group_id) {
                                analysis_projectid = project_id;
                                let liStart = '<li style="cursor:pointer;" class="sidebar-item" onclick="switchProject(this)" data-groupid= "' + group_id + '" data-index="' + project_id + '">';
                                let aStr = '<a class="sidebar-link active"> <span class="hide-menu"> ' + project_name + ' </span> </a>';
                                let divStart = '<div class="project-operation" data-index="' + project_id + '">';
                                let iStr = spliceIcon(common_menu, project_id, group_id);
                                let divEnd = '</div>';
                                let liEnd = '</li>';
                                projectStr += liStart + aStr + divStart + iStr + divEnd + liEnd;
                            } else {
                                let liStart = '<li style="cursor:pointer;" class="sidebar-item" onclick="switchProject(this)" data-groupid= "' + group_id + '" data-index="' + project_id + '">';
                                let aStr = '<a class="sidebar-link"> <span class="hide-menu"> ' + project_name + ' </span> </a>';
                                let divStart = '<div class="project-operation" data-index="' + project_id + '">';
                                let iStr = spliceIcon(common_menu, project_id, group_id);
                                let divEnd = '</div>';
                                let liEnd = '</li>';
                                projectStr += liStart + aStr + divStart + iStr + divEnd + liEnd;
                            }
                        } else {
                            if (analysis_projectid == project_id && analysis_groupid == group_id) {
                                let liStart = '<li style="cursor:pointer;" class="sidebar-item" onclick="switchProject(this)" data-groupid= "' + group_id + '" data-index="' + project_id + '">';
                                let aStr = '<a class="sidebar-link active"> <span class="hide-menu"> ' + project_name + ' </span> </a>';
                                let divStart = '<div class="project-operation" data-index="' + project_id + '">';
                                let iStr = spliceIcon(common_menu, project_id, group_id);
                                let divEnd = '</div>';
                                let liEnd = '</li>';
                                projectStr += liStart + aStr + divStart + iStr + divEnd + liEnd;
                            } else {
                                let liStart = '<li style="cursor:pointer;" class="sidebar-item" onclick="switchProject(this)" data-groupid= "' + group_id + '" data-index="' + project_id + '">';
                                let aStr = '<a class="sidebar-link"> <span class="hide-menu"> ' + project_name + ' </span> </a>';
                                let divStart = '<div class="project-operation" data-index="' + project_id + '">';
                                let iStr = spliceIcon(common_menu, project_id, group_id);
                                let divEnd = '</div>';
                                let liEnd = '</li>';
                                projectStr += liStart + aStr + divStart + iStr + divEnd + liEnd;
                            }
                        }
                    }
                    let groupStr2 = '</ul></li>';
                    groupStrAll += groupStr1 + projectStr + groupStr2;
                }
            }
            if (common_menu == 'project') {
                let addGroup = '<li class="p-15"><a onclick="createNewPro()" class="btn btn-block create-btn text-white d-flex align-items-center"><i class="fa fa-plus-square"></i><span class="hide-menu m-l-10">新建监测方案</span></a></li>';
                // groupStrAll =  addGroup + groupStrAll;
                groupStrAll = groupStrAll;
            }
            $("#sidebarnav").html(groupStrAll);
        } else {
            var url = ctxPath + 'project';
            var add =
                '<li class="p-15">' +
                '	<a href="' + url + '" class="btn btn-block create-btn text-white d-flex align-items-center">' +
                '	<i class="fa fa-plus-square"></i>' +
                '	<span class="hide-menu m-l-10">新建方案</span>' +
                '	</a>' +
                '</li>'
            $("#sidebarnav").html(add);
        }
    }

    $(".sidebar-item").mouseover(function () {
        var id = $(this).attr("data-index")
        $(".project-operation[data-index=" + id + "]").show(100)
    });
    $(".sidebar-item").mouseleave(function () {
        var id = $(this).attr("data-index")
        $(".project-operation[data-index=" + id + "]").hide(160)
    });
}

///**
// * @author huajiancheng
// * @date 2020/04/14
// * @description 组装方案组和方案列表数据
// */
//function installGroupAndProject(res) {
//    let code = res.code;
//    if (code == 200) {
//        let data = res.data;
//        if (data.length > 0) {
//            let groupStrAll = '';
//            for (let i = 0; i < data.length; i++) {
//                let dataJson = data[i];
//                for (let key in dataJson) {
//                    let value = dataJson[key];
//                    let group_id = key.split("-")[0];
//                    let group_name = key.split("-")[1];
//                    let groupStr1 = '';
//                    if (analysis_groupid == '' || analysis_groupid == undefined || analysis_groupid == null || analysis_groupid == 'null') {
//                        if (i == 0) {
//                            analysis_groupid = group_id;
//                            groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark active" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust"></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span></a><ul aria-expanded="false" class="collapse  first-level in">';
//                        } else {
//                            groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust"></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span></a><ul aria-expanded="false" class="collapse  first-level">';
//                        }
//                    } else {
//                        if (analysis_groupid == group_id) {
//                            groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark active" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust"></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span></a><ul aria-expanded="false" class="collapse  first-level in">';
//                        } else {
//                            groupStr1 = '<li class="sidebar-item"><a class="sidebar-link has-arrow waves-effect waves-dark" href="javascript:void(0)" aria-expanded="false"><i class="mdi mdi-adjust"></i><span data-index="' + group_id + '" class="hide-menu">' + group_name + '</span></a><ul aria-expanded="false" class="collapse  first-level">';
//                        }
//                    }
//                    let projectStr = '';
//                    for (let j = 0; j < value.length; j++) {
//                        let valueJson = value[j];
//                        let project_id = valueJson.project_id;
//                        let project_name = valueJson.project_name;
//                        if (analysis_projectid == '' || analysis_projectid == undefined || analysis_projectid == null || analysis_projectid == 'null') {
//                        	if (j == 0) {
//                                analysis_projectid = project_id;
//                                let liStart = '<li style="cursor:pointer;" class="sidebar-item" onclick="switchProject(this)" data-groupid= "' + group_id + '" data-index="' + project_id + '">';
//                                let aStr = '<a class="sidebar-link active"> <span class="hide-menu"> ' + project_name + ' </span> </a>';
//                                let divStart = '<div class="project-operation" data-index="' + project_id + '">';
//                                let iStr = spliceIcon(common_menu, project_id, group_id);
//                                let divEnd = '</div>';
//                                let liEnd = '</li>';
//                                projectStr += liStart + aStr + divStart + iStr + divEnd + liEnd;
//                            } else {
//                                let liStart = '<li style="cursor:pointer;" class="sidebar-item" onclick="switchProject(this)" data-groupid= "' + group_id + '" data-index="' + project_id + '">';
//                                let aStr = '<a class="sidebar-link"> <span class="hide-menu"> ' + project_name + ' </span> </a>';
//                                let divStart = '<div class="project-operation" data-index="' + project_id + '">';
//                                let iStr = spliceIcon(common_menu, project_id, group_id);
//                                let divEnd = '</div>';
//                                let liEnd = '</li>';
//                                projectStr += liStart + aStr + divStart + iStr + divEnd + liEnd;
//                            }
//                        } else {
//                            if (analysis_projectid == project_id && analysis_groupid == group_id) {
//                                let liStart = '<li style="cursor:pointer;" class="sidebar-item" onclick="switchProject(this)" data-groupid= "' + group_id + '" data-index="' + project_id + '">';
//                                let aStr = '<a class="sidebar-link active"> <span class="hide-menu"> ' + project_name + ' </span> </a>';
//                                let divStart = '<div class="project-operation" data-index="' + project_id + '">';
//                                let iStr = spliceIcon(common_menu, project_id, group_id);
//                                let divEnd = '</div>';
//                                let liEnd = '</li>';
//                                projectStr += liStart + aStr + divStart + iStr + divEnd + liEnd;
//                            } else {
//                                let liStart =  '<li style="cursor:pointer;" class="sidebar-item" onclick="switchProject(this)" data-groupid= "' + group_id + '" data-index="' + project_id + '">';
//                                let aStr = '<a class="sidebar-link"> <span class="hide-menu"> ' + project_name + ' </span> </a>';
//                                let divStart =  '<div class="project-operation" data-index="' + project_id + '">';
//                                let iStr = spliceIcon(common_menu, project_id, group_id);
//                                let divEnd = '</div>';
//                                let liEnd = '</li>';
//                                projectStr += liStart + aStr + divStart + iStr + divEnd + liEnd;
//                            }
//                        }
//                    }
//                    let groupStr2 = '</ul></li>';
//                    groupStrAll += groupStr1 + projectStr + groupStr2;
//                }
//            }
//            if(common_menu == 'project'){
//                let addGroup = '<li class="p-15"><a onclick="createNewPro()" class="btn btn-block create-btn text-white d-flex align-items-center"><i class="fa fa-plus-square"></i><span class="hide-menu m-l-10">新建监测方案组</span></a></li>';
//                groupStrAll =  addGroup + groupStrAll;
//            }
//            $("#sidebarnav").html(groupStrAll);
//        } else {
//            var url = ctxPath + 'project';
//            var add =
//                '<li class="p-15">' +
//                '	<a href="' + url + '" class="btn btn-block create-btn text-white d-flex align-items-center">' +
//                '	<i class="fa fa-plus-square"></i>' +
//                '	<span class="hide-menu m-l-10">新建方案</span>' +
//                '	</a>' +
//                '</li>'
//            $("#sidebarnav").html(add);
//        }
//    }
//
//    $(".sidebar-item").mouseover(function () {
//        var id = $(this).attr("data-index")
//        $(".project-operation[data-index=" + id + "]").show(100)
//    });
//    $(".sidebar-item").mouseleave(function () {
//        var id = $(this).attr("data-index")
//        $(".project-operation[data-index=" + id + "]").hide(160)
//    });
//}

function spliceIcon(menu, project_id, group_id) {
    var iStr = '';
    if (menu == "monitor") {
        let iStr1 = '<i class="fa fa-chart-bar" title="监测分析"  onclick="toAnalysis(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        let iStr2 = '<i class="fas fa-pencil-alt" title="监测管理"  onclick="toProject(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        let iStr3 = '<i class="fa fa-rss" title="方案查看"  onclick="toVolume(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        iStr = iStr1 + iStr2 + iStr3;
    } else if (menu == "analysis") {
        let iStr1 = '<i class="mdi mdi-eye-outline" title="数据监测"  onclick="toMonitor(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        let iStr2 = '<i class="fas fa-pencil-alt" title="监测管理"  onclick="toProject(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        let iStr3 = '<i class=" fa fa-rss" title="方案查看"  onclick="toVolume(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        iStr = iStr1 + iStr2 + iStr3;
    } else if (menu == "volume") {
        let iStr1 = '<i class="fa fa-chart-bar" title="监测分析"  onclick="toAnalysis(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        let iStr2 = '<i class="mdi mdi-eye-outline" title="数据监测"  onclick="toMonitor(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        let iStr3 = '<i class="fas fa-pencil-alt" title="监测管理"  onclick="toProject(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        iStr = iStr1 + iStr2 + iStr3;
    } else if (menu == "report") {
        let iStr1 = '<i class="fa fa-chart-bar" title="监测分析"  onclick="toAnalysis(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        let iStr2 = '<i class="mdi mdi-eye-outline" title="数据监测"  onclick="toMonitor(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        let iStr3 = '<i class="fas fa-pencil-alt" title="监测管理"  onclick="toProject(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        iStr = iStr1 + iStr2 + iStr3;
    } else if (menu == "project") {
        let iStr1 = '<i class="fa fa-chart-bar" title="监测分析"  onclick="toAnalysis(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        let iStr2 = '<i class="mdi mdi-eye-outline" title="数据监测"  onclick="toMonitor(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        let iStr3 = '<i class=" fa fa-rss" title="方案查看"  onclick="toVolume(&apos;' + project_id + '&apos;,&apos;' + group_id + '&apos;)"></i>';
        iStr = iStr1 + iStr2 + iStr3;
    }
    return iStr;
}

function getanalysisgroupId(res1, res2) {

    console.log("getanalysisgroupId-sdfhsdjfhsdljfhsdjklfhksdj");

    analysis_groupid = res1;
    analysis_projectid = res2;
}


function toAnalysis(p_id, g_id) {
    window.location.href = ctxPath + "analysis?groupid=" + g_id + "&projectid=" + p_id;
    window.event ? window.event.cancelBubble = true : e.stopPropagation(); // 阻止冒泡
}

function toProject(p_id, g_id) {
    window.location.href = ctxPath + "project/editproject?groupid=" + g_id + "&projectid=" + p_id;
    window.event ? window.event.cancelBubble = true : e.stopPropagation(); // 阻止冒泡
}


function toMonitor(p_id, g_id) {
    window.location.href = ctxPath + "monitor?groupid=" + g_id + "&projectid=" + p_id;
    window.event ? window.event.cancelBubble = true : e.stopPropagation(); // 阻止冒泡
}

function toVolume(p_id, g_id) {
    // /project/detail  volume
    window.location.href = ctxPath + "project/detail?groupid=" + g_id + "&projectid=" + p_id;
    window.event ? window.event.cancelBubble = true : e.stopPropagation(); // 阻止冒泡
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
 * @description 设置url
 */
function setUrl(url) {
    history.pushState({url: url, title: document.title}, document.title, url)
}


/**
 * @author huajiancheng
 * @date 2020/04/20
 * @description 获取ip和端口
 */

function getUrlAndPort() {
    // let host = window.location.host;
    let host = location.pathname;
    return host;
}


//  确认删除框
function confirmDelete() {
    var confirmhtml =
        '<div class="shadebox" id="confirmsure">' +
        '    <div class="modal-dialog" role="document"> ' +
        '        <div class="modal-content">' +
        '            <div class="modal-header align-flexend no-border"> ' +
        '                <h5 class="modal-title">提示</h5>' +
        '                <i class="mdi mdi-close-circle-outline font-18 cursor-po" id="closethis"></i>' +
        '            </div>' +
        '            <div class="modal-body" style="padding:0 1rem;">' +
        '                <div class="input-group alcenter"><i class="fa fa-exclamation-circle m-r-10 font-20"></i> 此操作将删除该账号, 是否继续?' +
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
        $("#confirmsure").remove()
    })
}


//exportthis 导出提示框
function exportbox() {
    var inputhtml =
        '<div class="shadebox" id="confirmsure">' +
        '    <div class="modal-dialog" role="document"> ' +
        '        <div class="modal-content">' +
        '            <div class="modal-header align-flexend no-border"> ' +
        '                <h5 class="modal-title">提示</h5>' +
        '                <i class="mdi mdi-close-circle-outline font-18 cursor-po" id="closethis"></i>' +
        '            </div>' +
        '            <div class="modal-body p_0_20">' +
        '                <div class="m-b-10">创建导出任务 <small>(创建成功后可在右上角导出列表中查看人任务进度)</small></div>' +
        '                <div class="input-group mb-3">' +
        '                    <input type="text" class="form-control" id="projectName" placeholder="请设置导出任务名称" />' +
        '                </div>' +
        '            </div>' +
        '            <div class="modal-footer no-border">' +
        '                <button type="button" class="btn btn-info" id="confirm"> 确定</button> ' +
        '                <button type="button" class="btn btn-secondary" id="cancel">取消</button>' +
        '            </div>' +
        '        </div>' +
        '    </div>' +
        '</div>'
    $("body").append(inputhtml)
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

//添加账号
function addaccount() {
    console.log("添加账号")
    var addaccounthtml =
        '<div class="shadebox" id="createaccount">' +
        '    <div class="modal-dialog" role="document"> ' +
        '        <div class="modal-content">' +
        '            <div class="modal-header align-flexend no-border"> ' +
        '                <h5 class="modal-title"> <i class="ti-marker-alt m-r-10"></i>添加监测账号 </h5>' +
        '                <i class="mdi mdi-close-circle-outline font-18 cursor-po" id="closethis"></i>' +
        '            </div>' +
        '            <div class="modal-body ">' +
        '                <div class="input-group mb-3">' +
        '                    <select class="custom-select form-control account-select">' +
        '                       <option>Twitter</option>' +
        '                    </select>' +
        '                    <input type="text" class="form-control" id="projectName" placeholder="请输入账号名称" />' +
        '                </div>' +
        '            </div>' +
        '            <div class="modal-footer no-border">' +
        '                <button type="button" class="btn btn-info" id="confirm"> 确定</button> ' +
        '                <button type="button" class="btn btn-secondary" id="cancel">取消</button>' +
        '            </div>' +
        '        </div>' +
        '    </div>' +
        '</div>'
    $("body").append(addaccounthtml)
    $("#closethis").click(function (param) {
        $("#createaccount").remove()
    })
    $("#cancel").click(function (param) {
        $("#createaccount").remove()
    })
    $("#confirm").click(function (param) {
        $("#createaccount").remove()
    })
}


// 添加方案
function addProjectByGroupId(obj, group_id) {
    let $obj = $(obj);
    window.location.href = ctxPath + 'project/addproject?groupid=' + group_id;
    window.event ? window.event.cancelBubble = true : e.stopPropagation(); // 阻止冒泡
}