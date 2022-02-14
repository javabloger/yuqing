
let monitor_groupid = "";
let monitor_projectid ="";


let article_public_idList = [];
let currentPageByDetail = 1;

var timea = null;
var timeb = null;


/**
 * contentType: 'application/json;charset=utf-8',
 * @author huajiancheng
 * @date 2020/04/16
 * @param param ajax请求基本参数
 * @param data 用户传的参数
 * @param funcname 方法名称
 * @description 请求列表数据
 */

function sendArticleSearch(param, data, funcname) {
    window.clearTimeout(timea);
    window.clearInterval(timeb);
    debugger;
    let s = JSON.stringify(data);
    $("#page").html("");
    $.ajax({
        type: 'POST',
        url: "informationListpost",
        dataType: 'json',
        data:s,
        contentType :'application/json;charset=utf-8',
        beforeSend: function () {
            loading("#monitor-content")
        },
        success: function (res) {
            funcname(res);
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


/**
 * contentType: 'application/json;charset=utf-8',
 * @author wangyi
 * @date 2021/04/22
 * @param param ajax请求基本参数
 * @param data 用户传的参数
 * @param funcname 方法名称
 * @description 请求行业数据
 */

function sendArticleIndustrySearch(param, data, funcIndustry) {

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "industry";
    articleParam.contentType = 'application/json;charset=utf-8';

    window.clearTimeout(timea);
    window.clearInterval(timeb);
    // var aaa = JSON.parse(data);
    // if (!aaa.group_id) {
    //     showInfo('请先创建方案组');
    //     return;
    // }
    $("#page").html("");
    $.ajax({
        type: articleParam.type,
        url: articleParam.url,
        dataType: 'json',
        data: data,
        contentType: param.contentType,
        beforeSend: function () {
            loading("#monitor-content")
        },
        success: function (res) {
            funcIndustry(res);
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

/**
 * contentType: 'application/json;charset=utf-8',
 * @author wangyi
 * @date 2021/04/22
 * @param param ajax请求基本参数
 * @param data 用户传的参数
 * @param funcname 方法名称
 * @description 请求省份数据
 */

function sendArticleProvinceSearch(param, data, funcProvince) {

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "getProvinceList";
    articleParam.contentType = 'application/json;charset=utf-8';


    window.clearTimeout(timea);
    window.clearInterval(timeb);
    // var aaa = JSON.parse(data);
    // if (!aaa.group_id) {
    //     showInfo('请先创建方案组');
    //     return;
    // }
    $("#page").html("");
    $.ajax({
        type: articleParam.type,
        url: articleParam.url,
        dataType: 'json',
        data: data,
        contentType: param.contentType,
        beforeSend: function () {
            loading("#monitor-content")
        },
        success: function (res) {
            funcProvince(res);
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



var provplusbo = true

$('#provplus').click(function () {

    if (provplusbo) {
        $('#provincelist').removeClass('industrylist')
        $('#provincelist').addClass('industrylist1')
        // $("#provincelist").css({
        //     'display': 'none'
        // })

        // $("#provincelist1").css({
        //     'display': 'inline-block'
        // })
        var newaaa = `
        收起&nbsp<i data-v-8ad03c8e="" class="fa fa-angle-up"></i>`
        $("#provplus").html(newaaa);
        provplusbo = false
    } else {

        $('#provincelist').removeClass('industrylist1')
        $('#provincelist').addClass('industrylist')
        // $("#provincelist").css({
        //     'display': 'inline-block'
        // })

        // $("#provincelist1").css({
        //     'display': 'none'
        // })
        var newaaa = `
        更多&nbsp<i data-v-8ad03c8e="" class="fa fa-angle-down"></i>`
        $("#provplus").html(newaaa);
        provplusbo = true
    }
})

function funcProvince(res) {


    $("#provincelist").html('');
    $("#provincelist1").html('');
    let industrystr = "";
    let totalindustrystr = '';
    let industrystr1 = "";
    let totalindustrystr1 = '';
    let data = res.data.data;
    // 这是省份
    if (data.length == 0) {
        totalindustrystr += '<span data-province="0" style="font-size:12px;width:60px" class="badge badge-pill badge-info">全部</span>';
    }


    if (data.length <= 5) {
        $("#provplus").css({
            'display': 'none'
        })
    } else {
        $("#provplus").css({
            'display': 'block'
        })
    }
    // for (let i = 0; i < 19; i++) {
    //     if (data[i].key != '') {
    //         if (data[i].key != 'total') {
    //             industrystr += ' <span data-province="' + data[i].key + '"style="font-size:12px;width:55px" class="badge badge-pill badge-light">' + data[i].key + '</span>';
    //         }
    //     }
    // }
    for (let i = 0; i < data.length; i++) {
        if (data[i].key != '') {
            if (data[i].key == 'total') {
                totalindustrystr += '<span data-province="0" style="font-size:12px;width:55px" class="badge badge-pill badge-info">全部</span>';
            }
        }
    }
    for (let i = 0; i < data.length; i++) {
        if (data[i].key != '') {
            if (data[i].key != 'total') {
                industrystr1 += ' <span data-province="' + data[i].key + '"style="font-size:12px;width:55px" class="badge badge-pill badge-light">' + data[i].key + '</span>';
            } else {
                totalindustrystr1 += '<span data-province="0" style="font-size:12px;width:55px" class="badge badge-pill badge-info">全部</span>';
            }
        }
    }

    $("#provincelist").html(totalindustrystr1 + industrystr1);

    $("#provincelist1").html(totalindustrystr1 + industrystr1);

}


/**
 * contentType: 'application/json;charset=utf-8',
 * @author wangyi
 * @date 2021/04/22
 * @param param ajax请求基本参数
 * @param data 用户传的参数
 * @param funcname 方法名称
 * @description 请求省份数据
 */

function sendArticleCitySearch(param, data, funcCity) {

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "getArticleCityList";
    articleParam.contentType = 'application/json;charset=utf-8';

    window.clearTimeout(timea);
    window.clearInterval(timeb);
    // var aaa = JSON.parse(data);
    // if (!aaa.group_id) {
    //     showInfo('请先创建方案组');
    //     return;
    // }
    $("#page").html("");
    $.ajax({
        type: articleParam.type,
        url: articleParam.url,
        dataType: 'json',
        data: data,
        contentType: param.contentType,
        beforeSend: function () {
            loading("#monitor-content")
        },
        success: function (res) {

            funcCity(res);
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



var cityplusbo = true;

$('#cityplus').click(function () {

    if (cityplusbo) {

        $("#citylist").removeClass('industrylist')
        $("#citylist").addClass('industrylist1')
        //     'display': 'none'
        // })

        // $("#citylist1").css({
        //     'display': 'inline-block'
        // })
        var newaaa = `
        收起&nbsp<i data-v-8ad03c8e="" class="fa fa-angle-up"></i>`
        $("#cityplus").html(newaaa);
        cityplusbo = false
    } else {

        $("#citylist").removeClass('industrylist1')
        $("#citylist").addClass('industrylist')
        // console.log("false");
        // $("#citylist").css({
        //     'display': 'inline-block'
        // })

        // $("#citylist1").css({
        //     'display': 'none'
        // })
        var newaaa = `
        更多&nbsp<i data-v-8ad03c8e="" class="fa fa-angle-down"></i>`
        $("#cityplus").html(newaaa);
        cityplusbo = true
    }
})



function funcCity(res) {
    $("#citylist").html('');
    $("#citylist1").html('');
    let industrystr = "";
    let totalindustrystr = '';
    let industrystr1 = "";
    let totalindustrystr1 = '';
    let data = res.data.data;

    if (data.length == 0) {
        totalindustrystr += '<span data-city="0" style="font-size:12px;width:55px" class="badge badge-pill badge-info">全部</span>';
    }

    if (data.length <= 5) {
        $("#cityplus").css({
            'display': 'none'
        })
    } else {
        $("#cityplus").css({
            'display': 'block'
        })
    }

    // for (let i = 0; i < 20; i++) {
    //     if (data[i].key != '') {
    //         if (data[i].key != 'total') {
    //             industrystr += ' <span data-city="' + data[i].key + '"style="font-size:12px;width:55px" class="badge badge-pill badge-light">' + data[i].key + '</span>';
    //         }
    //     }
    // }
    for (let i = 0; i < data.length; i++) {
        if (data[i].key != '') {
            if (data[i].key == 'total') {
                totalindustrystr += '<span data-city="0" style="font-size:12px;width:55px" class="badge badge-pill badge-info">全部</span>';
            }
        }
    }
    for (let i = 0; i < data.length; i++) {
        if (data[i].key != '') {
            if (data[i].key != 'total') {
                industrystr1 += ' <span data-city="' + data[i].key + '"style="font-size:12px;width:55px" class="badge badge-pill badge-light">' + data[i].key + '</span>';
            } else {
                totalindustrystr1 += '<span data-city="0" style="font-size:12px;width:55px" class="badge badge-pill badge-info">全部</span>';
            }
        }
    }

    $("#citylist").html(totalindustrystr1 + industrystr1);

    $("#citylist1").html(totalindustrystr1 + industrystr1);

}




/**
 * contentType: 'application/json;charset=utf-8',
 * @author wangyi
 * @date 2021/04/22
 * @param param ajax请求基本参数
 * @param data 用户传的参数
 * @param funcname 方法名称
 * @description 请求事件数据
 */

function sendArticleEventSearch(param, data, funcEvent) {

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "getevent";
    articleParam.contentType = 'application/json;charset=utf-8';
    window.clearTimeout(timea);
    window.clearInterval(timeb);
    debugger;
    $("#page").html("");
    $.ajax({
        type: articleParam.type,
        url: articleParam.url,
        dataType: 'json',
        data: data,
        contentType: param.contentType,
        beforeSend: function () {
            loading("#monitor-content")
        },
        success: function (res) {
            funcEvent(res);
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


var plusbo = true
$("#plus").click(function () {
    if (plusbo) {
        $("#industrylist").removeClass("industrylist");
        $("#industrylist").addClass("industrylist1");
        var newaaa = `
        收起&nbsp<i data-v-8ad03c8e="" class="fa fa-angle-up"></i>`
        $("#plus").html(newaaa);
        plusbo = false
    } else {
        $("#industrylist").removeClass("industrylist1");
        $("#industrylist").addClass("industrylist");
        var newaaa = `
        更多&nbsp<i data-v-8ad03c8e="" class="fa fa-angle-down"></i>`
        $("#plus").html(newaaa);
        plusbo = true
    }

})

function funcIndustry(res) {
    $("#industrylist1").html('');
    $("#industrylist").html('');
    let industrystr = "";
    let totalindustrystr = '';
    let industrystr1 = "";
    let industayrstrother = '';
    let totalindustrystr1 = '';
    let data = res.data.data;
    if (data.length <= 5) {
        $("#plus").css({
            'display': 'none'
        })
    } else {
        $("#plus").css({
            'display': 'block'
        })
    }
    $('#industrylist').children().length;
    // for (let i = 0; i < 11; i++) {
    //     if (data[i].key != '') {
    //         if (data[i].key != 'total') {
    //             industrystr += ' <span data-industry="' + data[i].key + '"style="font-size:12px;width:90px" class="badge badge-pill badge-light">' + data[i].key + '(' + data[i].doc_count + ')' + '</span>';
    //         } else {
    //             totalindustrystr += '<span data-industry="0" style="font-size:12px;width:90px" class="badge badge-pill badge-info">全部(' + data[i].doc_count + ')' + '</span>';
    //         }
    //     }
    // }
    for (let i = 0; i < data.length; i++) {
        if (data[i].key != '') {
            if (data[i].key == 'total') {
                totalindustrystr += '<span data-industry="0" style="font-size:12px;width:90px" class="badge badge-pill badge-info">全部(' + data[i].doc_count + ')' + '</span>';
            }
        }
    }

    for (let i = 0; i < data.length; i++) {
        if (data[i].key != '') {
            if (data[i].key != 'total'&&data[i].key!='其它') {
                industrystr1 += ' <span data-industry="' + data[i].key + '"style="font-size:12px;width:90px" class="badge badge-pill badge-light">' + data[i].key + '(' + data[i].doc_count + ')' + '</span>';
            } else if(data[i].key=='其它'){
            	industayrstrother += ' <span data-industry="' + data[i].key + '"style="font-size:12px;width:90px" class="badge badge-pill badge-light">' + data[i].key + '(' + data[i].doc_count + ')' + '</span>';
            }else {
                totalindustrystr1 += '<span data-industry="0" style="font-size:12px;width:90px" class="badge badge-pill badge-info">全部(' + data[i].doc_count + ')' + '</span>';
            }
        }
    }

    console.log(totalindustrystr1 + industrystr1 + industayrstrother);

    $("#industrylist").html(totalindustrystr1 + industrystr1+industayrstrother);
    $("#industrylist1").html(totalindustrystr1 + industrystr1+industayrstrother);
}


var eventplusbo = true;
$('#eventplus').click(function () {
    if (eventplusbo) {


        $("#eventlist").removeClass("industrylist");
        $("#eventlist").addClass("industrylist1");
        // $("#eventlist").css({
        //     'display': 'none'
        // })

        // $("#eventlist1").css({
        //     'display': 'inline-block'
        // })
        var newaaa = `
        收起&nbsp<i data-v-8ad03c8e="" class="fa fa-angle-up"></i>`
        $("#eventplus").html(newaaa);
        eventplusbo = false
    } else {

        $("#eventlist").removeClass("industrylist1");
        $("#eventlist").addClass("industrylist");
        // $("#eventlist").css({
        //     'display': 'inline-block'
        // })

        // $("#eventlist1").css({
        //     'display': 'none'
        // })
        var newaaa = `
        更多&nbsp<i data-v-8ad03c8e="" class="fa fa-angle-down"></i>`
        $("#eventplus").html(newaaa);
        eventplusbo = true
    }

})


function funcEvent(res) {

    debugger;

    $("#eventlist").html('');
    $("#eventlist1").html('');
    let eventstr = "";
    let totaleventstr = '';
    let eventstr1 = '';
    let eventotherstr = '';
    let totaleventstr1 = '';
    let data = res.data.data;
    if (data.length <= 5) {
        $("#eventplus").css({
            'display': 'none'
        })
    } else {
        $("#eventplus").css({
            'display': 'block'
        })
    }
    // for (let i = 0; i < 11; i++) {
    //     if (data[i].key != '') {
    //         if (data[i].key != 'total') {
    //             eventstr += ' <span data-event="' + data[i].key + '"style="font-size:12px;width:90px" class="badge badge-pill badge-light">' + data[i].key + '(' + data[i].doc_count + ')' + '</span>';
    //         } else {
    //             totaleventstr += ' <span data-event="0" style="font-size:12px;width:90px" class="badge badge-pill badge-info">全部(' + data[i].doc_count + ')' + '</span>';
    //         }
    //     }
    // }
    // for (let i = 0; i < data.length; i++) {
    //     if (data[i].key != '') {
    //         if (data[i].key == 'total') {
    //             totaleventstr += ' <span data-event="0" style="font-size:12px;width:90px" class="badge badge-pill badge-info">全部(' + data[i].doc_count + ')' + '</span>';
    //         }
    //     }
    // }
    for (let i = 0; i < data.length; i++) {
        if (data[i].key != '') {
            if (data[i].key != 'total'&&data[i].key!='其它') {
                eventstr1 += ' <span data-event="' + data[i].key + '"style="font-size:12px;width:90px" class="badge badge-pill badge-light">' + data[i].key + '(' + data[i].doc_count + ')' + '</span>';
            }else if(data[i].key=='其它'){
            	eventotherstr += ' <span data-event="' + data[i].key + '"style="font-size:12px;width:90px" class="badge badge-pill badge-light">' + data[i].key + '(' + data[i].doc_count + ')' + '</span>';
            }else {
                totaleventstr1 += ' <span data-event="0" style="font-size:12px;width:90px" class="badge badge-pill badge-info">全部(' + data[i].doc_count + ')' + '</span>';
            }
        }
    }
    $("#eventlist").html(totaleventstr1 + eventstr1+eventotherstr);

    // $("#eventlist1").html(totaleventstr1 + eventstr1);

}


function dealCate(arg) {
    if (arg != '' && arg != undefined) {
        let parse = JSON.parse(arg);
        return parse[0].type;
    } else {
        return "";
    }

}


// /**
//  * @author huajiancheng
//  * @date 2020/04/16
//  * @description 组装列表数据
//  */
// function installArticle12(res) {
//     let code = res.code;
//     let msg = res.msg;
//     let strAll = '';
//     if (code == 200) {
//         let data = res.data.data;
//         let totalPage = res.data.totalPage;
//         let totalCount = res.data.totalCount;
//         let currentPage = res.data.currentPage;
//         //        if (totalCount > 5000) {
//         //            totalCount = "5000+";
//         //        }
//
//         let similarflag = 0;
//
//         //判断是否合并
//         $('span[data-similar]').each(function () {
//             if ($(this).hasClass('badge-info')) {
//                 similarflag = $(this).data('similar');
//             }
//         });
//         // debugger;
//         if (similarflag == 1) {
//             $("#totalCount").html(res.data.totalNum);
//         } else {
//             $("#totalCount").html(totalCount);
//         }
//
//
//         if (res.data.hasOwnProperty("article_public_idList")) {
//             article_public_idList = res.data.article_public_idList;
//         }
//         if (data.length > 0) {
//             pageHelper(currentPage, totalPage);
//             for (let i = 0; i < data.length; i++) {
//                 let dataJson = data[i];
//                 let datasource = dataJson.datasource; // 媒体分类
//                 let websitelogo = dataJson.websitelogo;
//                 let article_public_id = dataJson.article_public_id;
//                 let author = dataJson.author;
//                 let key_words = dataJson.key_words;
//                 let sourcewebsitename = dataJson.sourcewebsitename;
//                 let title = dataJson.title;
//                 let source_url = dataJson.source_url;
//                 let content = dataJson.content;
//                 let emotionalIndex = dataJson.emotionalIndex;
//                 let publish_time = dataJson.publish_time;
//                 let pub_data = dataJson.publish_time;
//                 let extend_string_one = dataJson.extend_string_one;
//                 let forwardingvolume = dataJson.forwardingvolume; // 转发量
//                 let commentsvolume = dataJson.commentsvolume; // 评论量
//                 let praisevolume = dataJson.praisevolume; // 点赞数
//                 publish_time = timeParse(publish_time);
//                 var relatedWord = dataJson.relatedWord;
//                 let ner = dataJson.ner;
//                 let eventlable = dataJson.eventlable;
//                 let industrylable = dataJson.industrylable;
//                 let article_category = dataJson.article_category;
//                 let num = 0
//                 let similarflag = 0;
//
//                 //判断是否合并
//                 $('span[data-similar]').each(function () {
//                     if ($(this).hasClass('badge-info')) {
//                         similarflag = $(this).data('similar');
//                     }
//                 });
//
//                 if (similarflag == '1') {
//                     num = dataJson.num;
//                 }
//
//
//                 debugger;
//
//                 let category = dealCate(article_category);
//
//                 let copytext = '';
//                 let read = '';
//                 if (datasource == 2) { // 微博
//                     debugger;
//                     let strStart = '<div class="wb-content just-bet" >';
//                     let strCheck = '<div class="monitor-check"><input type="checkbox" data-index="' + article_public_id + '" id="check2"><span></span></div>';
//                     let strContentStart = '<div class="monitor-right">';
//                     let strTitle = '<div class="monitor-content-title"><a target="_blank" href="' + ctxPath + 'monitor/detail/' + article_public_id + '?groupid=' + monitor_groupid + '&projectid=' + monitor_projectid + '&publish_time=' + pub_data + '&relatedWord=' + relatedWord.join("，") + '" class="link font-bold"><span class="content-logo" style="background: url(' + websitelogo + ');"></span>' + title + '</a>';
//                     if (industrylable != '') {
//                         strTitle += '<span class="sl-date industrylable" >' + industrylable + ' </span>';
//                     }
//
//                     if (eventlable != '') {
//                         strTitle += '<span class="sl-date eventlable">' + eventlable + ' </span>';
//                     }
//
//                     if (category != '') {
//                         strTitle += '<span class="sl-date categorylable">' + category + ' </span>';
//                     }
//
//
//                     strTitle += '<span class="sl-date">' + publish_time + ' </span></div>';
//                     // let strTitle = '<div class="monitor-content-title"><a onclick="toDetail(&apos;' + article_public_id + '&apos;,&apos;' + monitor_groupid + '&apos;,&apos;' + monitor_projectid + '&apos;)" class="link font-bold"><span class="content-logo" style="background: url(' + websitelogo + ');"></span>' + title + '</a><span class="sl-date">' + publish_time + ' </span></div>';
//
//                     var str = "________________";
//                     /* 16 */
//                     let contentStart = ''; // 自己发的
//                     let contentEnd = '';
//                     if (content.indexOf(str) != -1) {
//                         let index = content.lastIndexOf(str);
//                         contentStart = content.substring(0, index);
//                         contentEnd = content.substring(index + 16, content.length);
//                     } else {
//                         contentStart = content;
//                     }
//
//                     let strContent = '<div class="monitor-content-con font-13">' + contentStart + '​</div>'; // 自己的正文
//                     let strTranspond = '<div class="monitor-content-con font-13 wb-zf">' + contentEnd + '</div>'; // 转发的原文
//                     let strImgStart = '<div class="img-group">';
//                     let strImgGroup = '';
//                     if (typeof (extend_string_one) == "object") {
//                         let imglist = extend_string_one.imglist;
//                         if (imglist != null) {
//                             for (let i = 0; i < imglist.length; i++) {
//                                 let imgurl = imglist[i].imgurl;
//                                 let imgurlstr = '<div class="img-box"><img src="' + imgurl + '" alt="" class="img-cover "></div>'
//                                 strImgGroup += imgurlstr;
//                                 if (i > 3) {
//                                     break;
//                                 }
//                             }
//                         }
//                     }
//                     let strImgEnd = '</div>';
//                     let strLikeStr = '<div class="like-comm">';
//                     let strSource = '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 ' + sourcewebsitename + '</span>';
//                     let strForward = '<span class="link m-r-10"> <i class="mdi mdi-shape-square-plus "></i> 转发 ' + forwardingvolume + '</span>';
//                     let strPraise = '<span class="link m-r-10"> <i class="mdi mdi-heart-outline "></i> 点赞 ' + praisevolume + '</span>';
//                     let strComment = '<span class="link m-r-10"> <i class="mdi mdi-comment-processing-outline"></i> 评论 ' + commentsvolume + '</span>';
//                     let keyword = '';
//                     if (similarflag == '1') {
//                         keyword = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 涉及词 ' + relatedWord.join("，") + '</span>' + '<span class="link m-r-10"> <i class="mdi mdi-projector-screen"></i> 相似文章数 ' + num + '</span>';
//                     } else {
//                         keyword = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 涉及词 ' + relatedWord.join("，") + '</span>';
//                     }
//
//                     let kuaijie = '<span class="link m-r-0">' +
//                         '<div class="btn-group inline-block" role="group"> <ul class="font-size-0" style="list-style-type: none;">' +
//                         '<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-collection dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="添加至收藏夹"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="fa-key ng-binding datafavorite" data-type="' + article_public_id + '" ng-bind="collect.name">收藏夹</span> </a> </li> </ul> </li>' +
//                         //					'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-news-material dropdown-toggle ng-scope" id="sck_916007411683191177512726" data-tippy="" title="发送、移动"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="briefFoldersList!=null&amp;&amp;briefFoldersList.length>0"> <li role="presentation" ng-repeat="brief in briefFoldersList" ng-if="brief!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(brief.folderId,2,icc)"> <span class="fa-key ng-binding sending" data-type="'+article_public_id+'" ng-bind="brief.name">发送、移动</span> </a> </li>  </ul> </li>'+
//                         //					'<li class="inline-block dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-send-way dropdown-toggle ng-scope" data-tippy="" title="舆情下发渠道"></button> <ul class="dropdown-menu pt15 pb15" role="menu"> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(2,icc,1);"> <span class="fa-key">短信下发</span> </a> </li> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(1,icc,1);"> <span class="fa-key">邮件下发</span> </a> </li></ul> </li>'+
//                         '<li class="inline-block" style="float: left;"> <a href="' + source_url + '" target="_blank" class=""> <button type="button" data-tippy-placement="top" aria-expanded="false" class="tippy btn btn-default-icon fa-check-origin-link" data-tippy="" data-original-title="查看原文"></button> </a> </li>' +
//                         '<li class="inline-block dropdown" style="float: left;"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-copy-direct dropdown-toggle" data-tippy="" data-original-title="复制"> </button> <ul class="dropdown-menu" role="menu"> ' +
//
//
//
//
//                         '<li role="presentation"> <a href="javascript:void(0)" id="link_916007411683191177512726" role="menuitem" ng-click="copyLink(icc.webpageUrl,icc.id);"> <span class="fa-key copyurl" data-type="' + article_public_id + '" data-flag="' + source_url + '" >拷贝地址</span> </a> </li>' +
//                         '<li role="presentation"> <a href="javascript:void(0)" class="copy-link-custom" role="menuitem" ng-click="aKeyToCopy(icc);"> <span class="fa-key copytext" data-type="' + article_public_id + '" data-flag="' + copytext + '" >一键复制</span> </a> </li> </ul> </li>' +
//                         //	'<li class="inline-block ng-scope" style="float: left;" ng-show="icc.readFlag==undefined &amp;&amp; view.resultPresent != 3" ng-if="view.showBatchLabel==0"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-unread-status" ng-click="readNews($event,icc)" data-tippy="" title="标已读"> </button> </li>'+
//                         //	'<li class="inline-block ng-hide" style="float: left;" ng-show="icc.readFlag==\'1\' &amp;&amp; view.resultPresent != 3"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-read-status waves-effect waves-light color-blue" data-tippy="" data-original-title="已读"> </button> </li>'+
//                         //	'<li class="inline-block dropdown" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle" data-tippy="" title="上报"> </button> <ul class="dropdown-menu" role="menu"> <li role="presentation"> <a href="#group_fenfa" ng-click="groupDistribute(icc,1);" data-toggle="modal" role="menuitem"> <span class="fa-key">上报</span> </a> </li>  </ul> </li>'+
//                         '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-trash dropdown-toggle" data-tippy="" title="移除"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> ' +
//
//
//
//
//
//                         '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span  data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="1" class="fa-key deletedata">删除信息</span> </a> </li>' +
//                         //'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span   data-type="'+article_public_id+'" data-flag="2" class="fa-key deletedata">删除信息并排除来源</span> </a> </li> '+
//                         '</ul> </li>' +
//
//                         '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> ' +
//                         '<button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-re-analysis dropdown-toggle" data-tippy="" title="情感标注"> </button>' +
//                         '<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">' +
//                         '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="1">正面</span> </a> </li>' +
//                         '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="2">中性</span> </a> </li>' +
//                         '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="3">负面</span> </a> </li> ' +
//                         '</ul>' +
//                         '</li>' +
//
//                         //read+
//                         '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-suyuan dropdown-toggle" data-tippy="" data-original-title="已读、未读"> </button> ' +
//                         '<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">  ' +
//                         '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span data-type="' + article_public_id + '" data-flag="1" class="fa-key isread">已读</span> </a> </li>' +
//                         '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span data-type="' + article_public_id + '" data-flag="2" class="fa-key isread">未读</span> </a> </li> </ul> </li>' +
//
//                         //跟踪项
//                         //'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="创建跟踪项"></button> '+
//                         //					'<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> '+
//                         //					'<li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="fa-key track" data-type="'+article_public_id+'" data-keywords="'+relatedWord.join("，")+'" ng-bind="collect.name">创建跟踪项</span> </a> </li>'+
//                         //					'</ul> '+
//                         '</li>' +
//
//
//
//                         '</ul></div></span>';
//
//                     let strEmotion = '';
//                     if (emotionalIndex == 1) {
//                         strEmotion = '<span class="link f-right moodzm">正面</span>';
//                     } else if (emotionalIndex == 2) {
//                         strEmotion = '<span class="link f-right moodzx">中性</span>';
//                     } else if (emotionalIndex == 3) {
//                         strEmotion = '<span class="link f-right moodfm">负面</span>';
//                     }
//
//                     //拼接机构
//                     let strcompanyandgov = '<div class="like-comm font-13"><span class="link m-r-10">'
//                     let org = JSON.parse(ner).org;
//                     let orgstr = "";
//                     var orgflag = 1;
//                     for (var key in org) {
//                         orgflag++;
//                         orgstr += '<a style="" class="ntag" href="###">' + key + '</a>'
//                         if (orgflag == 3) break;
//                     }
//                     strcompanyandgov = strcompanyandgov + orgstr + '</span><span class="link m-r-10">'
//                     //政府机构
//                     let nto = JSON.parse(ner).nto;
//                     let ntovstr = "";
//                     var netoflag = 1;
//                     for (var key in nto) {
//                         netoflag++;
//                         strcompanyandgov += '<a style="" class="govtag" href="###">' + key + '</a>'
//                         if (netoflag == 3) break;
//                     }
//                     strcompanyandgov += '</span>';
//
//                     //上市公司
//                     strcompanyandgov += '<span class="link m-r-10">';
//                     let ipo = JSON.parse(ner).IPO;
//                     let ipostr = "";
//                     var ipoflag = 1;
//                     for (var key in ipo) {
//                         ipoflag++;
//                         strcompanyandgov += '<a style="" class="ipotag" href="###">' + key + '</a>'
//                     }
//                     strcompanyandgov += '</span></div>';
//
//
//
//
//
//
//
//                     let strLikeEnd = '</div>';
//                     let strContentEnd = '</div>';
//
//
//
//
//                     let strEnd = '</div><hr>';
//                     strAll += strStart + strCheck + strContentStart + strTitle + strContent + strTranspond + strImgStart + strImgGroup + strImgEnd + strLikeStr + strSource + strForward + strPraise + strComment + keyword + strEmotion + strLikeEnd + strcompanyandgov + strContentEnd + strEnd;
//                 } else {
//                     if (typeof (extend_string_one) == "object") {
//                         let imglist = extend_string_one.imglist;
//                         if (imglist.length > 0) {
//                             let strStart = '<div class="wb-content just-bet" >';
//                             let strCheck = '<div class="monitor-check"><input type="checkbox" data-index="' + article_public_id + '" id="check1"><span></span></div>';
//                             let strContentStart = '<div class="monitor-right">';
//                             //let strTitle = '<div class="monitor-content-title"><a target="_blank" href="' + ctxPath + 'monitor/detail/' + article_public_id + '?groupid=' + monitor_groupid + '&projectid=' + monitor_projectid + '&relatedWord=' + relatedWord.join("，") +'&publish_time='+pub_data+ '"  class="link font-bold"><span class="content-logo" style="background: url(' + websitelogo + ');"></span>' + title + '</a><span class="sl-date eventlable">' + industrylable + ' </span><span class="sl-date eventlable">' + eventlable + ' </span><span class="sl-date eventlable">' + category + ' </span><span class="sl-date  ">' + publish_time + ' </span></div>';
//
//                             let strTitle = '<div class="monitor-content-title"><a target="_blank" href="' + ctxPath + 'monitor/detail/' + article_public_id + '?groupid=' + monitor_groupid + '&projectid=' + monitor_projectid + '&publish_time=' + pub_data + '&relatedWord=' + relatedWord.join("，") + '" class="link font-bold"><span class="content-logo" style="background: url(' + websitelogo + ');"></span>' + title + '</a>';
//                             if (industrylable != '') {
//                                 strTitle += '<span class="sl-date industrylable" >' + industrylable + ' </span>';
//                             }
//
//                             if (eventlable != '') {
//                                 strTitle += '<span class="sl-date eventlable">' + eventlable + ' </span>';
//                             }
//
//                             if (category != '') {
//                                 strTitle += '<span class="sl-date categorylable">' + category + ' </span>';
//                             }
//
//
//                             strTitle += '<span class="sl-date">' + publish_time + ' </span></div>';
//
//                             // let strTitle = '<div class="monitor-content-title"><a onclick="toDetail(&apos;' + article_public_id + '&apos;,&apos;' + monitor_groupid + '&apos;,&apos;' + monitor_projectid + '&apos;)" class="link font-bold"><span class="content-logo" style="background: url(' + websitelogo + ');"></span>' + title + '</a><span class="sl-date  ">' + publish_time + ' </span></div>';
//                             let strContent = '<div class="wb-content-imgbox"><div class="wb-left-imgbox"><img src="' + imglist[0].imgurl + '" class="img-cover" alt=""></div><div class="wb-right-content"><div class="monitor-content-con font-13">' + content + '</div></div></div>';
//                             let strLikeStrat = '<div class="like-comm m-t-10 font-13">';
//                             let strSource = '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 ' + sourcewebsitename + '</span>';
//                             //let strKeywords = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 涉及词 ' + relatedWord.join("，") + '</span>';
//
//                             let strKeywords = '';
//                             if (similarflag == '1') {
//                                 strKeywords = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 涉及词 ' + relatedWord.join("，") + '</span>' + '<span class="link m-r-10"> <i class="mdi mdi-projector-screen"></i> 相似文章数 ' + num + '</span>';
//                             } else {
//                                 strKeywords = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 涉及词 ' + relatedWord.join("，") + '</span>';
//                             }
//
//
//                             let kuaijie = '<span class="link m-r-0">' +
//                                 '<div class="btn-group inline-block" role="group"> <ul class="font-size-0" style="list-style-type: none;">' +
//                                 '<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-collection dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="添加至收藏夹"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="fa-key ng-binding datafavorite" data-type="' + article_public_id + '" ng-bind="collect.name">收藏夹</span> </a> </li> </ul> </li>' +
//                                 //						'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-news-material dropdown-toggle ng-scope" id="sck_916007411683191177512726" data-tippy="" title="发送、移动"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="briefFoldersList!=null&amp;&amp;briefFoldersList.length>0"> <li role="presentation" ng-repeat="brief in briefFoldersList" ng-if="brief!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(brief.folderId,2,icc)"> <span class="fa-key ng-binding sending" data-type="'+article_public_id+'" ng-bind="brief.name">发送、移动</span> </a> </li>  </ul> </li>'+
//                                 //							'<li class="inline-block dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-send-way dropdown-toggle ng-scope" data-tippy="" title="舆情下发渠道"></button> <ul class="dropdown-menu pt15 pb15" role="menu"> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(2,icc,1);"> <span class="fa-key">短信下发</span> </a> </li> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(1,icc,1);"> <span class="fa-key">邮件下发</span> </a> </li></ul> </li>'+
//                                 '<li class="inline-block" style="float: left;"> <a href="' + source_url + '" target="_blank" class=""> <button type="button" data-tippy-placement="top" aria-expanded="false" class="tippy btn btn-default-icon fa-check-origin-link" data-tippy="" data-original-title="查看原文"></button> </a> </li>' +
//                                 '<li class="inline-block dropdown" style="float: left;"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-copy-direct dropdown-toggle" data-tippy="" data-original-title="复制"> </button> <ul class="dropdown-menu" role="menu"> ' +
//
//
//
//
//                                 '<li role="presentation"> <a href="javascript:void(0)" id="link_916007411683191177512726" role="menuitem" ng-click="copyLink(icc.webpageUrl,icc.id);"> <span class="fa-key copyurl" data-type="' + article_public_id + '" data-flag="' + source_url + '" >拷贝地址</span> </a> </li>' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" class="copy-link-custom" role="menuitem" ng-click="aKeyToCopy(icc);"> <span class="fa-key copytext" data-type="' + article_public_id + '" data-flag="' + copytext + '" >一键复制</span> </a> </li> </ul> </li>' +
//                                 //'<li class="inline-block ng-scope" style="float: left;" ng-show="icc.readFlag==undefined &amp;&amp; view.resultPresent != 3" ng-if="view.showBatchLabel==0"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-unread-status" ng-click="readNews($event,icc)" data-tippy="" title="标已读"> </button> </li>'+
//                                 //'<li class="inline-block ng-hide" style="float: left;" ng-show="icc.readFlag==\'1\' &amp;&amp; view.resultPresent != 3"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-read-status waves-effect waves-light color-blue" data-tippy="" data-original-title="已读"> </button> </li>'+
//                                 //'<li class="inline-block dropdown" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle" data-tippy="" title="上报"> </button> <ul class="dropdown-menu" role="menu"> <li role="presentation"> <a href="#group_fenfa" ng-click="groupDistribute(icc,1);" data-toggle="modal" role="menuitem"> <span class="fa-key">上报</span> </a> </li>  </ul> </li>'+
//                                 '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-trash dropdown-toggle" data-tippy="" title="移除"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> ' +
//
//
//
//
//
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span  data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="1" class="fa-key deletedata">删除信息</span> </a> </li>' +
//                                 //'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span   data-type="'+article_public_id+'" data-flag="2" class="fa-key deletedata">删除信息并排除来源</span> </a> </li> '+
//                                 '</ul> </li>' +
//
//                                 '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> ' +
//                                 '<button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-re-analysis dropdown-toggle" data-tippy="" title="情感标注"> </button>' +
//                                 '<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="1">正面</span> </a> </li>' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="2">中性</span> </a> </li>' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="3">负面</span> </a> </li> ' +
//                                 '</ul>' +
//                                 '</li>' +
//
//                                 //read+
//                                 '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-suyuan dropdown-toggle" data-tippy="" data-original-title="已读、未读"> </button> ' +
//                                 '<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">  ' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span data-type="' + article_public_id + '" data-flag="1" class="fa-key isread">已读</span> </a> </li>' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span data-type="' + article_public_id + '" data-flag="2" class="fa-key isread">未读</span> </a> </li> </ul> </li>' +
//
//
//                                 //'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="创建跟踪项"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="fa-key track" data-type="'+article_public_id+'" data-keywords="'+relatedWord.join("，")+'" ng-bind="collect.name">创建跟踪项</span> </a> </li> </ul> </li>'+
//
//
//
//
//                                 '</ul></div></span>';
//
//
//                             let strEmotion = '';
//                             if (emotionalIndex == 1) {
//                                 strEmotion = '<span class="link f-right moodzm">正面</span>';
//                             } else if (emotionalIndex == 2) {
//                                 strEmotion = '<span class="link f-right moodzx">中性</span>';
//                             } else if (emotionalIndex == 3) {
//                                 strEmotion = '<span class="link f-right moodfm">负面</span>';
//                             }
//
//                             let strcompanyandgov = '';
//                             if(ner!=''){
//                             	//拼接机构
//                                 strcompanyandgov = '<div class="like-comm font-13"><span class="link m-r-10">'
//                                 let org = JSON.parse(ner).org;
//                                 let orgstr = "";
//                                 var orgflag = 1;
//                                 for (var key in org) {
//                                     orgflag++;
//                                     orgstr += '<a style="" class="ntag" href="###">' + key + '</a>'
//                                     if (orgflag == 3) break;
//                                 }
//                                 strcompanyandgov = strcompanyandgov + orgstr + '</span><span class="link m-r-10">'
//                                 //政府机构
//                                 let nto = JSON.parse(ner).nto;
//                                 let ntovstr = "";
//                                 var netoflag = 1;
//                                 for (var key in nto) {
//                                     netoflag++;
//                                     strcompanyandgov += '<a style="" class="govtag" href="###">' + key + '</a>'
//                                     if (netoflag == 3) break;
//                                 }
//                                 strcompanyandgov += '</span>';
//
//                                 //上市公司
//                                 strcompanyandgov += '<span class="link m-r-10">';
//                                 let ipo = JSON.parse(ner).IPO;
//                                 let ipostr = "";
//                                 var ipoflag = 1;
//                                 for (var key in ipo) {
//                                     ipoflag++;
//                                     strcompanyandgov += '<a style="" class="ipotag" href="###">' + key + '</a>'
//                                     if (ipoflag == 3) break;
//                                 }
//
//                                 strcompanyandgov += '</span></div>';
//                             }
//
//
//
//
//
//
//
//                             let strLikeEnd = '</div>';
//                             let strContentEnd = '</div>';
//                             let strEnd = '</div><hr>';
//                             strAll += strStart + strCheck + strContentStart + strTitle + strContent + strLikeStrat + strSource + strKeywords + kuaijie + strEmotion + strLikeEnd + strcompanyandgov + strContentEnd + strEnd;
//                         } else {
//                             let strStart = '<div class="wb-content just-bet">';
//                             let strCheck = '<div class="monitor-check"><input type="checkbox" data-index="' + article_public_id + '" id="check3"><span></span></div>';
//                             let strContentStart = '<div class="monitor-right">';
//                             //let strTitle = '<div class="monitor-content-title"><a target="_blank" href="' + ctxPath + 'monitor/detail/' + article_public_id + '?groupid=' + monitor_groupid + '&projectid=' + monitor_projectid + '&relatedWord=' + relatedWord.join("，") +'&publish_time='+pub_data+ '"  class="link font-bold"><span class="content-logo" style="background: url(' + websitelogo + ');"></span>' + title + '</a><span class="sl-date eventlable">' + industrylable + ' </span><span class="sl-date eventlable">' + eventlable + ' </span><span class="sl-date eventlable">' + category + ' </span><span class="sl-date  ">' + publish_time + '</span></div>';
//                             let strTitle = '<div class="monitor-content-title"><a target="_blank" href="' + ctxPath + 'monitor/detail/' + article_public_id + '?groupid=' + monitor_groupid + '&projectid=' + monitor_projectid + '&publish_time=' + pub_data + '&relatedWord=' + relatedWord.join("，") + '" class="link font-bold"><span class="content-logo" style="background: url(' + websitelogo + ');"></span>' + title + '</a>';
//                             if (industrylable != '') {
//                                 strTitle += '<span class="sl-date industrylable" >' + industrylable + ' </span>';
//                             }
//
//                             if (eventlable != '') {
//                                 strTitle += '<span class="sl-date eventlable">' + eventlable + ' </span>';
//                             }
//
//                             if (category != '') {
//                                 strTitle += '<span class="sl-date categorylable">' + category + ' </span>';
//                             }
//
//
//                             strTitle += '<span class="sl-date">' + publish_time + ' </span></div>';
//
//
//
//                             // let strTitle = '<div class="monitor-content-title"><a onclick="toDetail(&apos;' + article_public_id + '&apos;,&apos;' + monitor_groupid + '&apos;,&apos;' + monitor_projectid + '&apos;)"  class="link font-bold">' + title + '</a><span class="sl-date  ">' + publish_time + '</span></div>';
//                             let strContent = '<div class="monitor-content-con font-13">' + content + '</div>';
//                             let strLikeStart = '<div class="like-comm m-t-10 font-13">';
//                             let strSource = '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 ' + sourcewebsitename + '</span>';
//                             //let strKeywors = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 涉及词 ' + relatedWord.join("，") + '</span>';
//
//                             let strKeywords = '';
//                             if (similarflag == '1') {
//                                 strKeywords = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 涉及词 ' + relatedWord.join("，") + '</span>' + '<span class="link m-r-10"> <i class="mdi mdi-projector-screen"></i> 相似文章数 ' + num + '</span>';
//                             } else {
//                                 strKeywords = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 涉及词 ' + relatedWord.join("，") + '</span>';
//                             }
//
//
//
//
//                             let kuaijie = '<span class="link m-r-0">' +
//                                 '<div class="btn-group inline-block" role="group"> <ul class="font-size-0" style="list-style-type: none;">' +
//                                 '<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-collection dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="添加至收藏夹"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="ng-binding datafavorite" data-type="' + article_public_id + '" ng-bind="collect.name">收藏夹</span> </a> </li> </ul> </li>' +
//                                 //   						'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-news-material dropdown-toggle ng-scope" id="sck_916007411683191177512726" data-tippy="" title="发送、移动"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="briefFoldersList!=null&amp;&amp;briefFoldersList.length>0"> <li role="presentation" ng-repeat="brief in briefFoldersList" ng-if="brief!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(brief.folderId,2,icc)"> <span class="ng-binding sending" data-type="'+article_public_id+'" >发送、移动</span> </a> </li>  </ul> </li>'+
//                                 //    						'<li class="inline-block dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-send-way dropdown-toggle ng-scope" data-tippy="" title="舆情下发渠道"></button> <ul class="dropdown-menu pt15 pb15" role="menu"> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(2,icc,1);"> <span class="fa-key">短信下发</span> </a> </li> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(1,icc,1);"> <span class="fa-key">邮件下发</span> </a> </li></ul> </li>'+
//                                 '<li class="inline-block" style="float: left;"> <a href="' + source_url + '" target="_blank" class=""> <button type="button" data-tippy-placement="top" aria-expanded="false" class="tippy btn btn-default-icon fa-check-origin-link" data-tippy="" data-original-title="查看原文"></button> </a> </li>' +
//                                 '<li class="inline-block dropdown" style="float: left;"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-copy-direct dropdown-toggle" data-tippy="" data-original-title="复制"> </button> <ul class="dropdown-menu" role="menu"> ' +
//
//
//
//                                 '<li role="presentation"> <a href="javascript:void(0)" id="link_916007411683191177512726" role="menuitem" ng-click="copyLink(icc.webpageUrl,icc.id);"> <span class="copyurl" data-type="' + article_public_id + '" data-flag="' + source_url + '" >拷贝地址</span> </a> </li>' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" class="copy-link-custom" role="menuitem" ng-click="aKeyToCopy(icc);"> <span class="copytext" data-type="' + article_public_id + '" data-flag="' + copytext + '" >一键复制</span> </a> </li> </ul> </li>' +
//                                 //'<li class="inline-block ng-scope" style="float: left;" ng-show="icc.readFlag==undefined &amp;&amp; view.resultPresent != 3" ng-if="view.showBatchLabel==0"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-unread-status" ng-click="readNews($event,icc)" data-tippy="" title="标已读"> </button> </li>'+
//                                 //'<li class="inline-block ng-hide" style="float: left;" ng-show="icc.readFlag==\'1\' &amp;&amp; view.resultPresent != 3"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-read-status waves-effect waves-light color-blue" data-tippy="" data-original-title="已读"> </button> </li>'+
//                                 //'<li class="inline-block dropdown" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle" data-tippy="" title="上报"> </button> <ul class="dropdown-menu" role="menu"> <li role="presentation"> <a href="#group_fenfa" ng-click="groupDistribute(icc,1);" data-toggle="modal" role="menuitem"> <span class="fa-key">上报</span> </a> </li>  </ul> </li>'+
//                                 '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-trash dropdown-toggle" data-tippy="" title="移除"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> ' +
//
//
//
//
//
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span data-time="' + pub_data + '"  data-type="' + article_public_id + '" data-flag="1" class="deletedata">删除信息</span> </a> </li>' +
//                                 //'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span   data-type="'+article_public_id+'" data-flag="2" class="fa-key deletedata">删除信息并排除来源</span> </a> </li> '+
//                                 '</ul> </li>' +
//
//                                 '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> ' +
//                                 '<button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-re-analysis dropdown-toggle" data-tippy="" title="情感标注"> </button>' +
//                                 '<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="1">正面</span> </a> </li>' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="2">中性</span> </a> </li>' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="3">负面</span> </a> </li> ' +
//                                 '</ul>' +
//                                 '</li>' +
//
//                                 //read+
//                                 '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-suyuan dropdown-toggle" data-tippy="" data-original-title="已读、未读"> </button>' +
//                                 '<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">  ' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span data-type="' + article_public_id + '" data-flag="1" class="isread">已读</span> </a> </li>' +
//                                 '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span data-type="' + article_public_id + '" data-flag="2" class="isread">未读</span> </a> </li> </ul> </li>' +
//
//                                 //'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="创建跟踪项"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="track" data-type="'+article_public_id+'" data-keywords="'+relatedWord.join("，")+'" ng-bind="collect.name">创建跟踪项</span> </a> </li> </ul> </li>'+
//
//
//
//
//                                 '</ul></div></span>';
//
//
//
//                             let strEmotion = '';
//                             if (emotionalIndex == 1) {
//                                 strEmotion = '<span class="link f-right moodzm">正面</span>';
//                             } else if (emotionalIndex == 2) {
//                                 strEmotion = '<span class="link f-right moodzx">中性</span>';
//                             } else if (emotionalIndex == 3) {
//                                 strEmotion = '<span class="link f-right moodfm">负面</span>';
//                             }
//                             //拼接机构
//                             let strcompanyandgov = '<div class="like-comm font-13"><span class="link m-r-10">'
//
//                             let org = JSON.parse(ner).org;
//                             let orgstr = "";
//                             var orgflag = 1;
//                             for (var key in org) {
//                                 orgflag++;
//                                 orgstr += '<a style="" class="ntag" href="###">' + key + '</a>'
//                                 if (orgflag == 3) break;
//                             }
//                             strcompanyandgov = strcompanyandgov + orgstr + '</span><span class="link m-r-10">'
//                             ' </span>'
//                             //政府机构
//                             let nto = JSON.parse(ner).nto;
//                             let ntovstr = "";
//                             var netoflag = 1;
//                             for (var key in nto) {
//                                 netoflag++;
//                                 strcompanyandgov += '<a style="" class="govtag" href="###">' + key + '</a>'
//                                 if (netoflag == 3) break;
//                             }
//                             strcompanyandgov += '</span>';
//
//                             //上市公司
//                             strcompanyandgov += '<span class="link m-r-10">';
//                             let ipo = JSON.parse(ner).IPO;
//                             let ipostr = "";
//                             var ipoflag = 1;
//                             for (var key in ipo) {
//                                 ipoflag++;
//                                 strcompanyandgov += '<a style="" class="ipotag" href="###">' + key + '</a>'
//                                 if (ipoflag == 3) break;
//                             }
//
//                             strcompanyandgov += '</span></div>';
//                             let strLikeEnd = '</div>';
//                             let strContentEnd = '</div>';
//
//                             let strEnd = '</div><hr>';
//                             strAll += strStart + strCheck + strContentStart + strTitle + strContent + strLikeStart + strSource + strKeywords + kuaijie + strEmotion + strLikeEnd + strcompanyandgov + strContentEnd + strEnd;
//                         }
//                     } else {
//                         let strStart = '<div class="wb-content just-bet">';
//                         let strCheck = '<div class="monitor-check"><input type="checkbox" data-index="' + article_public_id + '" id="check3"><span></span></div>';
//                         let strContentStart = '<div class="monitor-right">';
//                         //let strTitle = '<div class="monitor-content-title"><a target="_blank" href="' + ctxPath + 'monitor/detail/' + article_public_id + '?groupid=' + monitor_groupid + '&projectid=' + monitor_projectid + '&relatedWord=' + relatedWord.join("，") +'&publish_time='+pub_data+ '"  class="link font-bold"><span class="content-logo" style="background: url(' + websitelogo + ');"></span>' + title + '</a><span class="sl-date eventlable">' + industrylable + ' </span><span class="sl-date eventlable">' + eventlable + ' </span><span class="sl-date eventlable">' + category + ' </span><span class="sl-date">' + publish_time + '</span></div>';
//
//                         let strTitle = '<div class="monitor-content-title"><a target="_blank" href="' + ctxPath + 'monitor/detail/' + article_public_id + '?groupid=' + monitor_groupid + '&projectid=' + monitor_projectid + '&publish_time=' + pub_data + '&relatedWord=' + relatedWord.join("，") + '" class="link font-bold"><span class="content-logo" style="background: url(' + websitelogo + ');"></span>' + title + '</a>';
//                         if (industrylable != '') {
//                             strTitle += '<span class="sl-date industrylable" >' + industrylable + ' </span>';
//                         }
//
//                         if (eventlable != '') {
//                             strTitle += '<span class="sl-date eventlable">' + eventlable + ' </span>';
//                         }
//
//                         if (category != '') {
//                             strTitle += '<span class="sl-date categorylable">' + category + ' </span>';
//                         }
//
//
//                         strTitle += '<span class="sl-date">' + publish_time + ' </span></div>';
//
//
//                         // let strTitle = '<div class="monitor-content-title"><a onclick="toDetail(&apos;' + article_public_id + '&apos;,&apos;' + monitor_groupid + '&apos;,&apos;' + monitor_projectid + '&apos;)" class="link font-bold">' + title + '</a><span class="sl-date  ">' + publish_time + '</span></div>';
//                         let strContent = '<div class="monitor-content-con font-13">' + content + '</div>';
//                         let strLikeStart = '<div class="like-comm m-t-10 font-13">';
//                         let strSource = '<span class="link m-r-10"> <i class="mdi mdi-earth "></i> 来源 ' + sourcewebsitename + '</span>';
//                         //let strKeywors = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 涉及词 ' + relatedWord.join("，") + '</span>';
//
//                         let strKeywors = '';
//                         if (similarflag == '1') {
//                             strKeywors = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 涉及词 ' + relatedWord.join("，") + '</span>' + '<span class="link m-r-10"> <i class="mdi mdi-projector-screen"></i> 相似文章数 ' + num + '</span>';
//                         } else {
//                             strKeywors = '<span class="link m-r-10"> <i class="mdi mdi-tag-outline"></i> 涉及词 ' + relatedWord.join("，") + '</span>';
//                         }
//
//
//
//
//                         let kuaijie = '<span class="link m-r-0">' +
//                             '<div class="btn-group inline-block" role="group"> <ul class="font-size-0" style="list-style-type: none;">' +
//                             '<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-collection dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="添加至收藏夹"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="ng-binding datafavorite" data-type="' + article_public_id + '" ng-bind="collect.name">收藏夹</span> </a> </li> </ul> </li>' +
//                             //						'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-news-material dropdown-toggle ng-scope" id="sck_916007411683191177512726" data-tippy="" title="发送、移动"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="briefFoldersList!=null&amp;&amp;briefFoldersList.length>0"> <li role="presentation" ng-repeat="brief in briefFoldersList" ng-if="brief!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(brief.folderId,2,icc)"> <span class="ng-binding sending" data-type="'+article_public_id+'" >发送、移动</span> </a> </li>  </ul> </li>'+
//                             //						'<li class="inline-block dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-send-way dropdown-toggle ng-scope" data-tippy="" title="舆情下发渠道"></button> <ul class="dropdown-menu pt15 pb15" role="menu"> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(2,icc,1);"> <span class="fa-key">短信下发</span> </a> </li> <li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="getAddressBook(1,icc,1);"> <span class="fa-key">邮件下发</span> </a> </li></ul> </li>'+
//                             '<li class="inline-block" style="float: left;"> <a href="' + source_url + '" target="_blank" class=""> <button type="button" data-tippy-placement="top" aria-expanded="false" class="tippy btn btn-default-icon fa-check-origin-link" data-tippy="" data-original-title="查看原文"></button> </a> </li>' +
//                             '<li class="inline-block dropdown" style="float: left;"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-copy-direct dropdown-toggle" data-tippy="" data-original-title="复制"> </button> <ul class="dropdown-menu" role="menu"> ' +
//
//
//
//
//                             '<li role="presentation"> <a href="javascript:void(0)" id="link_916007411683191177512726" role="menuitem" ng-click="copyLink(icc.webpageUrl,icc.id);"> <span class="copyurl" data-type="' + article_public_id + '" data-flag="' + source_url + '" >拷贝地址</span> </a> </li>' +
//                             '<li role="presentation"> <a href="javascript:void(0)" class="copy-link-custom" role="menuitem" ng-click="aKeyToCopy(icc);"> <span class="copytext" data-type="' + article_public_id + '" data-flag="' + copytext + '" >一键复制</span> </a> </li> </ul> </li>' +
//                             //'<li class="inline-block ng-scope" style="float: left;" ng-show="icc.readFlag==undefined &amp;&amp; view.resultPresent != 3" ng-if="view.showBatchLabel==0"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-unread-status" ng-click="readNews($event,icc)" data-tippy="" title="标已读"> </button> </li>'+
//                             //'<li class="inline-block ng-hide" style="float: left;" ng-show="icc.readFlag==\'1\' &amp;&amp; view.resultPresent != 3"> <button type="button" data-tippy-placement="top" class="tippy btn btn-default-icon fa-read-status waves-effect waves-light color-blue" data-tippy="" data-original-title="已读"> </button> </li>'+
//                             //'<li class="inline-block dropdown" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle" data-tippy="" title="上报"> </button> <ul class="dropdown-menu" role="menu"> <li role="presentation"> <a href="#group_fenfa" ng-click="groupDistribute(icc,1);" data-toggle="modal" role="menuitem"> <span class="fa-key">上报</span> </a> </li>  </ul> </li>'+
//                             '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-trash dropdown-toggle" data-tippy="" title="移除"> </button> <ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> ' +
//                             '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span  data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="1" class="deletedata">删除信息</span> </a> </li>' +
//                             //'<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span   data-type="'+article_public_id+'" data-flag="2" class="fa-key deletedata">删除信息并排除来源</span> </a> </li> '+
//                             '</ul> </li>' +
//
//                             '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> ' +
//                             '<button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-re-analysis dropdown-toggle" data-tippy="" title="情感标注"> </button>' +
//                             '<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">' +
//                             '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="1">正面</span> </a> </li>' +
//                             '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="2">中性</span> </a> </li>' +
//                             '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span class="dataemtion" data-time="' + pub_data + '" data-type="' + article_public_id + '" data-flag="3">负面</span> </a> </li> ' +
//                             '</ul>' +
//                             '</li>' +
//
//                             //read+
//                             '<li class="inline-block" style="float: left;" ng-show="view.resultPresent != 3"> <button type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-suyuan dropdown-toggle" data-tippy="" data-original-title="已读、未读"> </button>' +
//                             '<ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0">  ' +
//                             '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="excludeCaptureWebsiteNameList(icc)"> <span data-type="' + article_public_id + '" data-flag="1" class="isread">已读</span> </a> </li>' +
//                             '<li role="presentation"> <a href="javascript:void(0)" role="menuitem" ng-click="deleteSolrIds(icc)"> <span data-type="' + article_public_id + '" data-flag="2" class="isread">未读</span> </a> </li> </ul> </li>' +
//
//                             //'<li class="inline-block  dropdown" style="float: left;" ng-class="{\'dropdown\':icclist.length != $index+1,\'dropup\':icclist.length == $index+1}" ng-show="view.resultPresent != 3"> <button ng-if="icclist.length != $index+1" type="button" data-tippy-placement="top" data-hover="dropdown" data-toggle="dropdown" data-animation="scale-up" data-delay="300" aria-expanded="false" class="tippy btn btn-default-icon fa-distribute dropdown-toggle ng-scope" id="scj_916007411683191177512726" data-tippy="" title="创建跟踪项"> </button><ul class="dropdown-menu ng-scope" role="menu" ng-if="collectFoldersList!=null&amp;&amp;collectFoldersList.length>0"> <li role="presentation" ng-repeat="collect in collectFoldersList" ng-if="collect!=null" class="ng-scope"> <a href="javascript:void(0)" role="menuitem" ng-click="insertMaterial(collect.folderId,1,icc)"> <span class="track" data-type="'+article_public_id+'" data-keywords="'+relatedWord.join("，")+'" ng-bind="collect.name">创建跟踪项</span> </a> </li> </ul> </li>'+
//
//                             '</ul></div></span>';
//                         let strEmotion = '';
//                         if (emotionalIndex == 1) {
//                             strEmotion = '<span class="link f-right moodzm">正面</span>';
//                         } else if (emotionalIndex == 2) {
//                             strEmotion = '<span class="link f-right moodzx">中性</span>';
//                         } else if (emotionalIndex == 3) {
//                             strEmotion = '<span class="link f-right moodfm">负面</span>';
//                         }
//
//
//                         let strcompanyandgov = '';
//                         if(ner!=''){
//                         	//拼接机构
//                             strcompanyandgov = '<div class="like-comm font-13"><span class="link m-r-10">'
//                             let org = JSON.parse(ner).org;
//                             let orgstr = "";
//                             var orgflag = 1;
//                             for (var key in org) {
//                                 orgflag++;
//                                 orgstr += '<a style="" class="ntag" href="###">' + key + '</a>'
//                                 if (orgflag == 3) break;
//                             }
//                             strcompanyandgov = strcompanyandgov + orgstr + '</span><span class="link m-r-10">'
//                             ' </span>'
//                             //政府机构
//                             let nto = JSON.parse(ner).nto;
//                             let ntovstr = "";
//                             var netoflag = 1;
//                             for (var key in nto) {
//                                 netoflag++;
//                                 strcompanyandgov += '<a style="" class="govtag" href="###">' + key + '</a>'
//                                 if (netoflag == 3) break;
//                             }
//                             strcompanyandgov += '</span>';
//
//                             //上市公司
//                             strcompanyandgov += '<span class="link m-r-10">';
//                             let ipo = JSON.parse(ner).IPO;
//                             let ipostr = "";
//                             var ipoflag = 1;
//                             for (var key in ipo) {
//                                 ipoflag++;
//                                 strcompanyandgov += '<a style="" class="ipotag" href="###">' + key + '</a>'
//                                 if (ipoflag == 3) break;
//                             }
//                             strcompanyandgov += '</span></div>';
//                         }
//
//
//
//
//
//
//                         let strLikeEnd = '</div>';
//                         let strContentEnd = '</div>';
//
//
//
//                         let strEnd = '</div><hr>';
//                         strAll += strStart + strCheck + strContentStart + strTitle + strContent + strLikeStart + strSource + strKeywors + kuaijie + strEmotion + strLikeEnd + strcompanyandgov + strContentEnd + strEnd;
//                     }
//                 }
//                 $("#monitor-content").html(strAll);
//             }
//         } else {
//             $("#page").html("");
//             nodata('#monitor-content');
//         }
//     } else {
//         $("#page").html("");
//         dataerror("#monitor-content");
//     }
// }

/**
 * @author 获取页面的用户选择的条件
 * @date 2020/04/16
 * @description 分页
 */
function getArticleData11(page, searchWord, currentProjectid, currentGroupid) {
    let articleData = new Object();
    let times = $("input[name='start']").val();
    let timee = $("input[name='end']").val();
    let precise;
    let emotionalIndex = [];

    let eventIndex = [];
    let industryIndex = [];
    let province = [];
    let city = [];
    let organizationtype = [];
    let categorylabledata = [];
    let enterprisetypelist = [];
    let hightechtypelist = [];
    let policylableflag = [];
    let classify = [];
    let classify1;
    let similar;
    let searchType;
    let matchingmode;
    let timeType;



    $('span[data-emotion]').each(function () {
        if ($(this).hasClass('badge-info')) {
            emotionalIndex.push($(this).data('emotion'));
        }
    });

    $('span[data-event]').each(function () {
        if ($(this).hasClass('badge-info')) {
            eventIndex.push($(this).data('event'));
        }
    });
    $('span[data-industry]').each(function () {
        if ($(this).hasClass('badge-info')) {
            industryIndex.push($(this).data('industry'));
        }
    });

    $('span[data-province]').each(function () {
        if ($(this).hasClass('badge-info')) {
            province.push($(this).data('province'));
        }
    });
    $('span[data-city]').each(function () {
        if ($(this).hasClass('badge-info')) {
            city.push($(this).data('city'));
        }
    });

    $('span[data-similar]').each(function () {
        if ($(this).hasClass('badge-info')) {
            similar = $(this).data('similar');
        }
    });


    $('span[data-sort]').each(function () {
        if ($(this).hasClass('badge-info')) {
            searchType = $(this).data('sort');
        }
    });

    $('span[data-matchs]').each(function () {
        if ($(this).hasClass('badge-info')) {
            matchingmode = $(this).data('matchs');
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

    //机构类型
    $("#organizationtype input[type='checkbox']:checked").each(function () {
        organizationtype.push($(this).val());
    });

    //文章分类
    $("#categorylabledata input[type='checkbox']:checked").each(function () {
        categorylabledata.push($(this).val());
    });

    //企业类型
    $("#enterprisetypelist input[type='checkbox']:checked").each(function () {
        enterprisetypelist.push($(this).val());
    });

    //高新技术企业
    $("#hightechtypelist input[type='checkbox']:checked").each(function () {
        hightechtypelist.push($(this).val());
    });

    //政策
    $("#policylableflag input[type='checkbox']:checked").each(function () {
        policylableflag.push($(this).val());
    });

    // //数据来源
    // $("#classify input[type='checkbox']:checked").each(function () {
    //     classify.push($(this).val());
    // });


    $('span[data-classify]').each(function () {
        if ($(this).hasClass('badge-info')) {
            classify1 = $(this).data('classify');
            classify.push(classify1);
        }
    });


    if (timeType == null || timeType == 0 || timeType == "") {


        timeType = 6;
    }


    articleData.times = times;
    articleData.timee = timee;
    articleData.page = page;
    articleData.searchType = searchType; // 排序
    articleData.similar = similar;
    articleData.matchingmode = matchingmode;
    articleData.emotionalIndex = emotionalIndex;


    if (province == null||province == "") {
        articleData.province = [];
    }else {
        articleData.province = province;
    }

    if (city == null || city == "") {
        articleData.city = [];
    } else {
        articleData.city = city;
    }

    if (eventIndex == null || eventIndex == "") {
        articleData.eventIndex = [];
    } else {
        articleData.eventIndex = eventIndex;
    }

    if (industryIndex == null || industryIndex == " ") {
        articleData.industryIndex = [];
    } else {
        articleData.industryIndex = industryIndex;
    }

    debugger;

    articleData.searchword = searchWord;
    articleData.projectid = currentProjectid;
    articleData.groupid = currentGroupid;
    articleData.group_id = currentProjectid;
    articleData.projectId = currentProjectid;

    articleData.timeType = timeType;
    articleData.precise = precise;

    articleData.organizationtype = organizationtype;
    articleData.categorylabledata = categorylabledata;
    articleData.enterprisetypelist = enterprisetypelist;
    articleData.hightechtypelist = hightechtypelist;
    articleData.policylableflag = policylableflag;
    articleData.classify = classify;

    debugger;
    return articleData;
}



/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description accurate 精准 筛选
 */
$("#sxshow").click(function (e) {
    $(this).hide();
    $("#sxhide").show();
    $("#sxinline").show();
});

$("#sxhide").click(function (e) {
    $(this).hide()
    $("#sxshow").show();
    $("#sxinline").hide();
});

$("#sxinline span").click(function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (act) {} else {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
    }

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);
    sendArticleSearch(articleParam, articleData, installArticle3);
    sendArticleIndustrySearch(articleParam, JSON.stringify(articleData), funcIndustry);
    sendArticleEventSearch(articleParam, JSON.stringify(articleData), funcEvent);
    sendArticleProvinceSearch(articleParam, JSON.stringify(articleData), funcProvince);
    sendArticleCitySearch(articleParam, JSON.stringify(articleData), funcCity);


});

/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description 相似文章 xsinline
 */
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
//相似文章
$("#xsinline span").click(function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (act) {} else {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
    }
    $("#industrylist span,#eventlist span,#provincelist span,#citylist span").removeClass(active);
    $("#industrylist span,#eventlist span,#provincelist span,#citylist span").addClass(normal);

    $("#industrylist span:nth-child(0),#eventlist span:nth-child(1),#provincelist span:nth-child(1),#citylist span:nth-child(1)").addClass(active);
    //  $("#industrylist > span:nth-child(1)").addClass(normal);

    $("#organizationtype input[type=checkbox],#enterprisetypelist input[type=checkbox],#categorylabledata input[type=checkbox],#hightechtypelist input[type=checkbox],#policylableflag input[type=checkbox],#classify input[type=checkbox]").prop("checked", false);
    $("#organizationtype  > ul > li:nth-child(1) > a > label > input[type=checkbox],#enterprisetypelist  > ul > li:nth-child(1) > a > label > input[type=checkbox],#categorylabledata  > ul > li:nth-child(1) > a > label > input[type=checkbox],#hightechtypelist  > ul > li:nth-child(1) > a > label > input[type=checkbox],#policylableflag  > ul > li:nth-child(1) > a > label > input[type=checkbox],#classify  > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop("checked", true);



    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);
    sendArticleSearch(articleParam, articleData, installArticle3)
    sendArticleIndustrySearch(articleParam, JSON.stringify(articleData), funcIndustry);
    sendArticleEventSearch(articleParam, JSON.stringify(articleData), funcEvent);
    sendArticleProvinceSearch(articleParam, JSON.stringify(articleData), funcProvince);
    sendArticleCitySearch(articleParam, JSON.stringify(articleData), funcCity);


});

/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description 数据导出 dcinline
 */
$("#dcshow").click(function (e) {
    $(this).hide();
    $("#dchide").show();
    $("#dcinline").show();
});

$("#dchide").click(function (e) {
    $(this).hide();
    $("#dcshow").show();
    $("#dcinline").hide();
});

/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description 全部导出
 */
$("#allexport").click(function (param) {
    swal({
        text: "最大导出5000条数据",
        showCancelButton: false,
        confirmButtonColor: "#36bea6",
        confirmButtonText: "导出",
    }).then(function (that) {
        if (that.value) {
            let searchWord = $("#searchWord").val();
            let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);
            articleData.exportType = 1;
            exportData(articleData);
        }
    });
});

/**
 * @author huajiancheng
 * @date 2020/04/17
 * @description 导出数据
 */
function exportData(data) {
    let form = $('<form method="POST"  action="monitor/exportarticle">');
    let value = data[name];
    form.append($("<input type='hidden' name='data' value='" + JSON.stringify(data) + "'/>"));
    $('body').append(form);
    form.submit();
}

/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description 部分导出
 */
$("#selectexport").click(function (param) {
    $(".monitor-content").find(".monitor-check").show();
});

/**
 * @author liyoulin
 * @date 2020/04/16
 * @description 确认导出
 */

$("#sureexport").click(function (param) {
    let exportlist = [];
    $("#check1:checked").each(function () {
        exportlist.push($(this).attr("data-index"));
    });

    $("#check2:checked").each(function () {
        exportlist.push($(this).attr("data-index"));
    });

    $("#check3:checked").each(function () {
        exportlist.push($(this).attr("data-index"));
    });

    if (exportlist.length == 0) {
        swal({
            text: "请选择需要导出的数据",
            timer: 2000,
            showConfirmButton: false
        });
    } else {
        let exportlistLen = exportlist.length;
        let tips = "已选择" + exportlistLen + "条信息";
        swal({
            text: tips,
            showCancelButton: true,
            confirmButtonColor: "#36bea6",
            confirmButtonText: "确认导出",
            cancelButtonText: "取消"
        }).then(function (that) {
            if (that.value) {
                let searchWord = $("#searchWord").val();
                let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);
                articleData.exportType = 2;
                articleData.exportlist = exportlist;
                exportData(articleData);
            }
        });
    }
});

/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description inlineList
 */
$("#inlineList span").click(function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var act = $(this).hasClass(active);
    if (act) {} else {
        $(this).siblings().removeClass(active);
        $(this).siblings().addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
    }

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);
    sendArticleSearch(articleParam, articleData, installArticle3);
});

//时间框变化
$("#date-range input").change(function () {
    var times = $("#date-range input[name=start]").val();
    var timed = $("#date-range input[name=end]").val();

    if (times > timed) {
        showInfo("开始时间不能迟于结束时间");
    } else {
        if (times != "" && timed != "") {
            let articleParam = new Object();
            articleParam.type = "POST";
            articleParam.url = ctxPath + "fullsearch/informationListpost";
            articleParam.contentType = 'application/json;charset=utf-8';
            let searchWord = $("#searchWord").val();
            let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);
            articleData.timeType = 8
            sendArticleSearch(articleParam,articleData, installArticle3);
        } else {
            showInfo("开始时间或结束时间不能为空");
        }
    }
})




$("#eventlist").on('click', 'span', function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var eventdata = $(this).attr("data-event");
    var act = $(this).hasClass(active);
    if (eventdata == '0') {
        if (!act) {
            $("#eventlist span").removeClass(active);
            $("#eventlist span").removeClass(normal);
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#provincelist span,#citylist span").removeClass(active);
            $("#provincelist span,#citylist span").addClass(normal);
            $("#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);

        } else {
            $("#eventlist span").removeClass(active);
            $("#eventlist span").removeClass(normal);
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#provincelist span,#citylist span").removeClass(active);
            $("#provincelist span,#citylist span").addClass(normal);
            $("#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);


        }
    } else {
        if (act) {
            $(this).removeClass(active);
            $(this).addClass(normal);
            $("#eventlist > span:nth-child(1)").removeClass(active);
            $("#eventlist > span:nth-child(1)").addClass(normal);
            $("#provincelist span,#citylist span").removeClass(active);
            $("#provincelist span,#citylist span").addClass(normal);
            $("#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);
        } else {
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#eventlist > span:nth-child(1)").removeClass(active);
            $("#eventlist > span:nth-child(1)").addClass(normal);
            $("#provincelist span,#citylist span").removeClass(active);
            $("#provincelist span,#citylist span").addClass(normal);
            $("#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);
        }
    }
    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);

    sendArticleSearch(articleParam,articleData,installArticle3)
    sendArticleProvinceSearch(articleParam, JSON.stringify(articleData), funcProvince);
    sendArticleCitySearch(articleParam, JSON.stringify(articleData), funcCity);


});

$("#eventlist1").on('click', 'span', function (e) {
    var active = "badge-info";
    var normal = "badge-light";
    var eventdata = $(this).attr("data-event");
    var act = $(this).hasClass(active);
    if (eventdata == '0') {
        if (!act) {
            $("#eventlist1 span").removeClass(active);
            $("#eventlist1 span").removeClass(normal);
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#provincelist span,#citylist span").removeClass(active);
            $("#provincelist span,#citylist span").addClass(normal);
            $("#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);

        } else {
            $("#eventlist1 span").removeClass(active);
            $("#eventlist1 span").removeClass(normal);
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#provincelist span,#citylist span").removeClass(active);
            $("#provincelist span,#citylist span").addClass(normal);
            $("#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);


        }
    } else {
        if (act) {
            $(this).removeClass(active);
            $(this).addClass(normal);
            $("#eventlist1 > span:nth-child(1)").removeClass(active);
            $("#eventlist1 > span:nth-child(1)").addClass(normal);
            $("#provincelist span,#citylist span").removeClass(active);
            $("#provincelist span,#citylist span").addClass(normal);
            $("#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);
        } else {
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#eventlist1 > span:nth-child(1)").removeClass(active);
            $("#eventlist1 > span:nth-child(1)").addClass(normal);
            $("#provincelist span,#citylist span").removeClass(active);
            $("#provincelist span,#citylist span").addClass(normal);
            $("#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);
        }
    }
    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);
    sendArticleSearch(articleParam,articleData, installArticle3)
    sendArticleProvinceSearch(articleParam, JSON.stringify(articleData), funcProvince);
    sendArticleCitySearch(articleParam, JSON.stringify(articleData), funcCity);
});

$("#industrylist").on('click', 'span', function (e) {
    var active = "badge-info";
    var normal = "badge-light";

    var industrydata = $(this).attr("data-industry");
    var act = $(this).hasClass(active);
    if (industrydata == '0') {
        if (!act) {
            $("#industrylist span").removeClass(active);
            $("#industrylist span").addClass(normal);
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#eventlist span,#provincelist span,#citylist span").removeClass(active);
            $("#eventlist span,#provincelist span,#citylist span").addClass(normal);
            $("#eventlist > span:nth-child(1),#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);
        } else {
            $("#industrylist span").removeClass(active);
            $("#industrylist span").removeClass(normal);
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#eventlist span,#provincelist span,#citylist span").removeClass(active);
            $("#eventlist span,#provincelist span,#citylist span").addClass(normal);
            $("#eventlist > span:nth-child(1),#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);
        }
    } else {
        if (act) {
            $(this).removeClass(active);
            $(this).addClass(normal);
            $("#industrylist > span:nth-child(1)").removeClass(active);
            $("#industrylist > span:nth-child(1)").addClass(normal);
            $("#eventlist span,#provincelist span,#citylist span").removeClass(active);
            $("#eventlist span,#provincelist span,#citylist span").addClass(normal);
            $("#eventlist > span:nth-child(1),#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);
        } else {
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#industrylist > span:nth-child(1)").removeClass(active);
            $("#industrylist > span:nth-child(1)").addClass(normal);
            $("#eventlist span,#provincelist span,#citylist span").removeClass(active);
            $("#eventlist span,#provincelist span,#citylist span").addClass(normal);
            $("#eventlist > span:nth-child(1),#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);

        }
    }

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);

    debugger;
    console.log(articleData, "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp");
    sendArticleSearch(articleParam,articleData, installArticle3);
    //sendArticleIndustrySearch(articleParam, JSON.stringify(articleData), funcIndustry);
    sendArticleEventSearch(articleParam, JSON.stringify(articleData), funcEvent);
    sendArticleProvinceSearch(articleParam, JSON.stringify(articleData), funcProvince);
    sendArticleCitySearch(articleParam, JSON.stringify(articleData), funcCity);


});

$("#industrylist1").on('click', 'span', function (e) {
    var active = "badge-info";
    var normal = "badge-light";

    var industrydata = $(this).attr("data-industry");
    var act = $(this).hasClass(active);
    if (industrydata == '0') {
        if (!act) {
            $("#industrylist1 span").removeClass(active);
            $("#industrylist1 span").addClass(normal);
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#eventlist span,#provincelist span,#citylist span").removeClass(active);
            $("#eventlist span,#provincelist span,#citylist span").addClass(normal);
            $("#eventlist > span:nth-child(1),#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);
        } else {
            $("#industrylist1 span").removeClass(active);
            $("#industrylist1 span").removeClass(normal);
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#eventlist span,#provincelist span,#citylist span").removeClass(active);
            $("#eventlist span,#provincelist span,#citylist span").addClass(normal);
            $("#eventlist > span:nth-child(1),#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);
        }
    } else {
        if (act) {
            $(this).removeClass(active);
            $(this).addClass(normal);
            $("#industrylist1 > span:nth-child(1)").removeClass(active);
            $("#industrylist1 > span:nth-child(1)").addClass(normal);
            $("#eventlist span,#provincelist span,#citylist span").removeClass(active);
            $("#eventlist span,#provincelist span,#citylist span").addClass(normal);
            $("#eventlist > span:nth-child(1),#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);
        } else {
            $(this).removeClass(normal);
            $(this).addClass(active);
            $("#industrylist1 > span:nth-child(1)").removeClass(active);
            $("#industrylist1 > span:nth-child(1)").addClass(normal);
            $("#eventlist span,#provincelist span,#citylist span").removeClass(active);
            $("#eventlist span,#provincelist span,#citylist span").addClass(normal);
            $("#eventlist > span:nth-child(1),#province > span:nth-child(1),#city > span:nth-child(1)").addClass(active);

        }
    }

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);
    sendArticleSearch(articleParam,articleData, installArticle3);
    //sendArticleIndustrySearch(articleParam, JSON.stringify(articleData), funcIndustry);
    sendArticleEventSearch(articleParam, JSON.stringify(articleData), funcEvent);
    sendArticleProvinceSearch(articleParam, JSON.stringify(articleData), funcProvince);
    sendArticleCitySearch(articleParam, JSON.stringify(articleData), funcCity);


});
$("#provincelist").on('click', 'span', function (e) {
    var active = "badge-info";
    var normal = "badge-light";

    var industrydata = $(this).attr("data-province");
    var act = $(this).hasClass(active);
    if (!act) {
        $("#provincelist span").removeClass(active);
        $("#provincelist span").addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        $("#citylist span").removeClass(active);
        $("#citylist span").addClass(normal);

    } else {
        $("#provincelist span").removeClass(active);
        $("#provincelist span").removeClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        $("#citylist span").removeClass(active);
        $("#citylist span").addClass(normal);
    }

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';
    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);
    sendArticleSearch(articleParam,articleData, installArticle3);
    //sendArticleIndustrySearch(articleParam, JSON.stringify(articleData), funcIndustry);
    sendArticleCitySearch(articleParam, JSON.stringify(articleData), funcCity);



});


$("#provincelist1").on('click', 'span', function (e) {
    var active = "badge-info";
    var normal = "badge-light";

    var industrydata = $(this).attr("data-province");
    var act = $(this).hasClass(active);
    if (!act) {
        $("#provincelist1 span").removeClass(active);
        $("#provincelist1 span").addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        $("#citylist span").removeClass(active);
        $("#citylist span").addClass(normal);

    } else {
        $("#provincelist1 span").removeClass(active);
        $("#provincelist1 span").removeClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
        $("#citylist span").removeClass(active);
        $("#citylist span").addClass(normal);
    }

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';
    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);
    sendArticleSearch(articleParam,articleData, installArticle3);
    //sendArticleIndustrySearch(articleParam, JSON.stringify(articleData), funcIndustry);
    sendArticleCitySearch(articleParam, JSON.stringify(articleData), funcCity);



});


//$("#organizationtype").on('change','span',function(e){
//	
//	var checks = "";
//    $("input[name='checkbox']").each(function(){
//        if($(this).attr("checked") == true){
//            checks += $(this).val() + "|";            //动态拼取选中的checkbox的值，用“|”符号分隔
//        }
//    })
//    alert(checks);
//})	
//机构类型
$("#organizationtype input[type=checkbox]").change(function () {
    if ($(this).is(':checked')) {
        if ($(this).val() == '0') {
            $("#organizationtype input[type=checkbox]").prop('checked', false);

            $(this).prop("checked", true);
        } else {
            $("#organizationtype > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $(this).prop("checked", true);
        }
    } else {
        let orgtypelist = [];
        //alert("未选中:"+$(this).val())
        if ($(this).val() == '0') {
            $("#organizationtype input[type=checkbox]").prop('checked', false);
            $(this).prop('checked', false);
            $("#organizationtype input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#organizationtype > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        } else {
            $(this).prop('checked', false);
            $("#organizationtype > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $("#organizationtype input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#organizationtype > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        }

    }


    //加载文章

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);


    sendArticleSearch(articleParam,articleData, installArticle3);


});


//文章类型
$("#categorylabledata input[type=checkbox]").change(function () {
    if ($(this).is(':checked')) {
        if ($(this).val() == '0') {
            $("#categorylabledata input[type=checkbox]").prop('checked', false);

            $(this).prop("checked", true);
        } else {
            $("#categorylabledata > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $(this).prop("checked", true);
        }
    } else {
        let orgtypelist = [];
        //alert("未选中:"+$(this).val())
        if ($(this).val() == '0') {
            $("#categorylabledata input[type=checkbox]").prop('checked', false);
            $(this).prop('checked', false);
            $("#categorylabledata input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#categorylabledata > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        } else {
            $(this).prop('checked', false);
            $("#categorylabledata > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $("#categorylabledata input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#categorylabledata > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        }

    }

    //加载文章

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);


    sendArticleSearch(articleParam,articleData, installArticle3);

});

//企业类型
$("#enterprisetypelist input[type=checkbox]").change(function () {
    if ($(this).is(':checked')) {
        if ($(this).val() == '0') {
            $("#enterprisetypelist input[type=checkbox]").prop('checked', false);

            $(this).prop("checked", true);
        } else {
            $("#enterprisetypelist > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $(this).prop("checked", true);
        }
    } else {
        let orgtypelist = [];
        //alert("未选中:"+$(this).val())
        if ($(this).val() == '0') {
            $("#enterprisetypelist input[type=checkbox]").prop('checked', false);
            $(this).prop('checked', false);
            $("#enterprisetypelist input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#enterprisetypelist > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        } else {
            $(this).prop('checked', false);
            $("#enterprisetypelist > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $("#enterprisetypelist input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#enterprisetypelist > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        }

    }

    //加载文章

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);


    sendArticleSearch(articleParam,articleData, installArticle3);

});

//企业类型
$("#hightechtypelist input[type=checkbox]").change(function () {
    if ($(this).is(':checked')) {
        if ($(this).val() == '0') {
            $("#hightechtypelist input[type=checkbox]").prop('checked', false);

            $(this).prop("checked", true);
        } else {
            $("#hightechtypelist > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $(this).prop("checked", true);
        }
    } else {
        let orgtypelist = [];
        //alert("未选中:"+$(this).val())
        if ($(this).val() == '0') {
            $("#hightechtypelist input[type=checkbox]").prop('checked', false);
            $(this).prop('checked', false);
            $("#hightechtypelist input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#hightechtypelist > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        } else {
            $(this).prop('checked', false);
            $("#hightechtypelist > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $("#hightechtypelist input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#hightechtypelist > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        }

    }

    //加载文章

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath+ "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);


    sendArticleSearch(articleParam,articleData, installArticle3);

});

//政策
$("#policylableflag input[type=checkbox]").change(function () {
    if ($(this).is(':checked')) {
        if ($(this).val() == '0') {
            $("#policylableflag input[type=checkbox]").prop('checked', false);

            $(this).prop("checked", true);
        } else {
            $("#policylableflag > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $(this).prop("checked", true);
        }
    } else {
        let orgtypelist = [];
        //alert("未选中:"+$(this).val())
        if ($(this).val() == '0') {
            $("#policylableflag input[type=checkbox]").prop('checked', false);
            $(this).prop('checked', false);
            $("#policylableflag input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#policylableflag > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        } else {
            $(this).prop('checked', false);
            $("#policylableflag > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $("#policylableflag input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#policylableflag > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        }

    }

    //加载文章

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);


    sendArticleSearch(articleParam,articleData, installArticle3);

});

//数据来源
$("#classify input[type=checkbox]").change(function () {
    if ($(this).is(':checked')) {
        if ($(this).val() == '0') {
            $("#classify input[type=checkbox]").prop('checked', false);

            $(this).prop("checked", true);
        } else {
            $("#classify > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $(this).prop("checked", true);
        }
    } else {
        let orgtypelist = [];
        //alert("未选中:"+$(this).val())
        if ($(this).val() == '0') {
            $("#classify input[type=checkbox]").prop('checked', false);
            $(this).prop('checked', false);
            $("#classify input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#classify > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        } else {
            $(this).prop('checked', false);
            $("#classify > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', false);
            $("#classify input[type='checkbox']:checked").each(function () {
                orgtypelist.push($(this).val());
            });
            if (orgtypelist.length == 0) {
                $("#classify > ul > li:nth-child(1) > a > label > input[type=checkbox]").prop('checked', true);
            }
        }

    }

    //加载文章

    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();

    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);

    sendArticleSearch(articleParam,articleData, installArticle3);

});


$("#citylist").on('click', 'span', function (e) {
    var active = "badge-info";
    var normal = "badge-light";

    var industrydata = $(this).attr("data-city");
    var act = $(this).hasClass(active);
    if (!act) {
        $("#citylist span").removeClass(active);
        $("#citylist span").addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);


    } else {
        $("#citylist span").removeClass(active);
        $("#citylist span").removeClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
    }


    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);

    //sendArticleIndustrySearch(articleParam, JSON.stringify(articleData), funcIndustry);
    //sendArticleCitySearch(articleParam, JSON.stringify(articleData), funcCity);
    sendArticleSearch(articleParam, articleData, installArticle3)
});

$("#citylist1").on('click', 'span', function (e) {
    var active = "badge-info";
    var normal = "badge-light";

    var industrydata = $(this).attr("data-city");
    var act = $(this).hasClass(active);
    if (!act) {
        $("#citylist1 span").removeClass(active);
        $("#citylist1 span").addClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);


    } else {
        $("#citylist1 span").removeClass(active);
        $("#citylist1 span").removeClass(normal);
        $(this).removeClass(normal);
        $(this).addClass(active);
    }


    let articleParam = new Object();
    articleParam.type = "POST";
    articleParam.url = ctxPath + "fullsearch/informationListpost";
    articleParam.contentType = 'application/json;charset=utf-8';

    let searchWord = $("#searchWord").val();
    let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);

    //sendArticleIndustrySearch(articleParam, JSON.stringify(articleData), funcIndustry);
    //sendArticleCitySearch(articleParam, JSON.stringify(articleData), funcCity);
    sendArticleSearch(articleParam, articleData, installArticle3)
});



/**
 * @author huajiancheng
 * @date 2020/04/16
 * @description emotionList
 */
$("#catgoryshow").click(function (e) {
    $(this).hide();
    $("#catgoryhide").show();
    $("#catgoryList").show();
});
$("#catgoryhide").click(function (e) {
    $(this).hide();
    $("#catgoryshow").show();
    $("#catgoryList").hide();
});



/**
 * @author lyoulin
 * @date 2020/04/16
 * @description rankList
 */
$("#rankList span").click(function (e) {
    var active = "rank-active";
    var act = $(this).hasClass(active);
    if (act) {} else {
        $(this).siblings().removeClass(active);
        $(this).addClass(active);
    }
});

/**
 * @author liyoulin
 * @date 2020/04/16
 * @description  Date Picker  时间控件
 */

$('#date-range').datepicker({
    language: 'zh-CN',
    format: "yyyy-mm-dd",
    orientation: "bottom auto",
    toggleActive: true,
    keyboardNavigation: true,
    enableOnReadonly: false,
    todayHighlight: true,
    endDate: getnow(),
    autoclose: true,
});

function getnow() {
    var now = new Date();
    var nowday = now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate();
    return nowday
}

/**
 * @author liyoulin
 * @date 2020/04/16
 * @description  查看大图  Custom Image wb-left-imgbox
 */
$(document).on('click', ".wb-left-imgbox", function () {
    var url = $(this).children().attr("src");
    swal({
        title: "",
        text: " ",
        imageUrl: url
    });
    $(".swal2-actions").hide();
    $(".swal2-popup").click(function (e) {
        $(".swal2-confirm").click();
    });
});

$(document).on('click', '.img-box', function (e) {
    var url = $(this).children().attr("src")
    swal({
        title: "",
        text: " ",
        imageUrl: url
    });
    $(".swal2-actions").hide();
    $(".swal2-popup").click(function (e) {
        $(".swal2-confirm").click();
    });
});


// 回车搜索
$('#searchWord').keydown(function (e) {
    if (e.keyCode == 13) {
        // 条件绑定成功，获取文章列表数据
        let articleParam = new Object();
        articleParam.type = "POST";
        articleParam.url = ctxPath + "fullsearch/informationListpost";
        articleParam.contentType = 'application/json;charset=utf-8';
        let searchWord = $("#searchWord").val();
        let articleData = getArticleData11(1, searchWord, monitor_projectid, monitor_groupid);
        sendArticleSearch(articleParam,articleData, installArticle3)
    }
});


/**
 * 组装数据
 * @param res
 */
function installSearchWord(res) {
    let code = res.code;
    if (code == 200) {
        let data = res.data;
        let StrAll = '';
        let str1 = '';
        let str2 = '';
        for (let i = 0; i < data.length; i++) {
            let dataJson = data[i];
            let search_word = dataJson.search_word;
            let index = i % 2;

            if (index == 0) {
                str1 += '<li><span class="v-hot">' + (i + 1) + '</span> <a href=""><span>' + search_word + '</span></a></li>';
            } else {
                str2 += '<li><span class="v-hot">' + (i + 1) + '</span> <a href=""><span>' + search_word + '</span></a></li>';
            }
        }
        let str3 = '<ul class="timeline timeline-left hot-content-list" style="padding-left: 200px;">' + str1 + '</ul>';
        let str4 = '<ul class="timeline timeline-left hot-content-list" style="padding-left: 100px;">' + str2 + '</ul>';
        StrAll = str3 + str4;
        $("#searchwords").html(StrAll);
    }
}

/**
 * 收藏功能
 * @returns
 */
$(document).on('click', '.datafavorite', function () {
    let id = $(this).data('type');
    let projectid = monitor_projectid;
    let groupid = monitor_groupid;

    $.ajax({
        url: ctxPath + 'datamonitor/addfavoritedata',
        type: 'post',
        dataType: 'json',
        data: {
            id: id,
            projectid: projectid,
            groupid: groupid
        },
        success: function (res) {
            let status = res.status;
            if (status == 200) {
                $.blockUI({
                    message: "收藏成功",
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

        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            }
        }
    });


});


/**
 * 发送、移动
 * @returns
 */
$(document).on('click', '.sending', function () {
    let id = $(this).data('type');
    let projectid = monitor_projectid;
    let groupid = monitor_groupid;

    $.ajax({
        url: ctxPath + 'datamonitor/sending',
        type: 'post',
        dataType: 'json',
        data: {
            id: id,
            projectid: projectid,
            groupid: groupid
        },
        success: function (res) {
            let status = res.status;
            if (status == 200) {
                $.blockUI({
                    message: "预警方案发送成功",
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
            } else if (status == 500) {
                $.blockUI({
                    message: "预警方案发送失败，请稍后尝试！",
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

        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            }
        }
    });

});

//情感标记

$(document).on('click', '.dataemtion', function () {
    let id = $(this).data('type');
    let flag = $(this).data('flag');
    let publish_time = $(this).data('time');
    $.ajax({
        url: ctxPath + 'datamonitor/updateemtion',
        type: 'post',
        dataType: 'json',
        data: {
            id: id,
            flag: flag,
            publish_time: publish_time
        },
        success: function (res) {
            if (res.status == 200) {
                $.blockUI({
                    message: "情感标记成功",
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
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            }
        }
    });
    //	$.ajax({
    //		url:ctxPath+'monitor/updateEmotion',
    //		type:'post',
    //		data:{
    //			article_public_id:id,
    //			emotion:flag
    //		},
    //		success:function(res){
    //			$.blockUI({
    //	            message: "情感标记成功",
    //	            fadeIn: 700,
    //	            fadeOut: 700,
    //	            timeout: 3000,
    //	            showOverlay: false,
    //	            centerY: false,
    //	            css: {
    //	                width: '250px',
    //	                top: '20px',
    //	                left: '',
    //	                right: '20px',
    //	                border: 'none',
    //	                padding: '15px 5px',
    //	                backgroundColor: '#000',
    //	                '-webkit-border-radius': '10px',
    //	                '-moz-border-radius': '10px',
    //	                opacity: 0.9,
    //	                color: '#fff'
    //	            }
    //	        });
    //			let articleParam = new Object();
    //			articleParam.type = "POST";
    //			articleParam.url = ctxPath + "fullsearch/informationList";
    //			articleParam.contentType = 'application/json;charset=utf-8';
    //			let searchWord = $("#searchWord").val();
    //			let articleData = getArticleData11(1, searchWord,
    //					monitor_projectid, monitor_groupid);
    //			sendArticle(articleParam, JSON.stringify(articleData),
    //					installArticle3)
    //		},
    //		error:function(res){
    //			$.blockUI({
    //	            message: "情感标记失败",
    //	            fadeIn: 700,
    //	            fadeOut: 700,
    //	            timeout: 3000,
    //	            showOverlay: false,
    //	            centerY: false,
    //	            css: {
    //	                width: '250px',
    //	                top: '20px',
    //	                left: '',
    //	                right: '20px',
    //	                border: 'none',
    //	                padding: '15px 5px',
    //	                backgroundColor: '#000',
    //	                '-webkit-border-radius': '10px',
    //	                '-moz-border-radius': '10px',
    //	                opacity: 0.9,
    //	                color: '#fff'
    //	            }
    //	        });
    //		}
    //	})
});
//已读、未读标记
$(document).on('click', '.isread', function () {
    let id = $(this).data('type');
    let flag = $(this).data('flag');
    $.ajax({
        url: ctxPath + 'datamonitor/isread',
        type: 'post',
        dataType: 'json',
        data: {
            id: id,
            flag: flag
        },
        success: function (res) {
            let status = res.status;
            if (status == 200) {
                $.blockUI({
                    message: "已读标记成功",
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
            } else if (status == 500) {
                $.blockUI({
                    message: "已标记",
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
            } else if (status == 300) {
                $.blockUI({
                    message: "取消已读标记",
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
            } else if (status == 400) {
                $.blockUI({
                    message: "取消已读标记失败，请稍后尝试！",
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

        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            }
        }
    });




});

//删除信息
$(document).on('click', '.deletedata', function () {
    let id = $(this).data('type');
    let publish_time = $(this).data('time');
    let flag = $(this).data('flag');
    $.ajax({
        url: ctxPath + 'datamonitor/deletedata',
        type: 'post',
        dataType: 'json',
        data: {
            id: id,
            flag: flag,
            publish_time: publish_time
        },
        success: function (res) {
            let status = res.status;
            if (status == 200) {
                $.blockUI({
                    message: "删除信息成功",
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

        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            }
        }
    });




});


$(document).on('click', '.deletedata', function () {
    let flag = $(this).data('flag');
    $.ajax({
        url: ctxPath + 'datamonitor/deletedata',
        type: 'post',
        dataType: 'json',
        data: {
            id: id,
            flag: flag
        },
        success: function (res) {
            let status = res.status;
            if (status == 200) {
                $.blockUI({
                    message: "删除信息成功",
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

        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            }
        }
    });




});



//地址拷贝
$(document).on('click', '.copyurl', function () {
    let flag = $(this).data('flag');
    copyText(flag);
    $.blockUI({
        message: "拷贝地址成功",
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

});


//文章拷贝	
$(document).on('click', '.copytext', function () {
    let flag = $(this).data('type');
    $.ajax({
        url: ctxPath + 'datamonitor/copytext',
        type: 'post',
        dataType: 'json',
        data: {
            id: flag
        },
        success: function (res) {
            let status = res.status;
            let text = res.result;
            if (status == 200) {
                copyText(text);
                $.blockUI({
                    message: "文章拷贝成功",
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

        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 403) {
                window.location.href = ctxPath + "login";
            }
        }
    });


});



function copyText(text) {
    var textarea = document.createElement("textarea");
    var currentFocus = document.activeElement;
    let state = false;
    document.body.appendChild(textarea);
    textarea.value = text;
    textarea.focus();
    if (textarea.setSelectionRange) {
        textarea.setSelectionRange(0, textarea.value.length);
    } else {
        textarea.select();
    }
    try {
        state = document.execCommand("copy");
    } catch (err) {
        state = false;
    }
    document.body.removeChild(textarea);
    currentFocus.focus();
    return state;
};



