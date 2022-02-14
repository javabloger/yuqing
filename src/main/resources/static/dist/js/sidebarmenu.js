// 点击方案组
$('#sidebarnav a').on('click', function (e) {
    if (!$(this).hasClass("active")) {
        $("ul", $(this).parents("ul:first")).removeClass("in");
        $("a", $(this).parents("ul:first")).removeClass("active");
        $(this).next("ul").addClass("in");
        $(this).addClass("active");
    }
    else if ($(this).hasClass("active")) {
        $(this).removeClass("active");
//        $(this).parents("ul:first").removeClass("active");
        $(this).next("ul").removeClass("in");
    }
})
$('#sidebarnav > li > a.has-arrow').on('click', function (e) {
    e.preventDefault();
});
// 控制二级菜单
//    $("#thirdMenu li a").click(function (e) {
//        var filter = $(this).parent().hasClass("dropdown");
//        if (filter) {
//            $(this).parent().siblings().removeClass("nav-active3")
//            $(this).parent().addClass("nav-active3")
//        }
//    });
// 返回
$("#goback").click(function (param) {
    console.log("back")
    window.history.back()
})
//  监测管理
$("#sidebarnav .sidebar-item").mouseover(function () {
    $(this).children().find("button").show(160)
});
$("#sidebarnav .sidebar-item").mouseleave(function () {
    $(this).children().find("button").hide(160)
});


function createNewPro(params) {
    var create =
        '<div class="shadebox" id="createmodel">' +
        '    <div class="modal-dialog" role="document">' +
        '        <div class="modal-content">' +
        '            <div class="modal-header align-flexend" style="border:none">' +
        '                <h5 class="modal-title"><i class="ti-marker-alt m-r-10"></i>创建新方案组</h5>' +
        '                <i class="mdi mdi-close-circle-outline font-18 cursor-po" id="closethis"></i>' +
        '            </div>' +
        '            <div class="modal-body">' +
        '                <div class="input-group mb-3">' +
        '                    <button type="button" class="btn " style="background:#cfcfd0"><i class="mdi mdi-mailbox text-white"></i></button>' +
        '                    <input type="text" class="form-control" id="projectName" placeholder="输入方案组名称，最六个字符" maxlength="10">' +
        '                </div>' +
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
        var name = $("#projectName").val()
        if (name == '' || name == undefined||name=="") {
            showtips("方案组名称不能为空！")
        }else if (name.length>6) {
            showtips("方案组名称长度最多六个字符")
        }else {
            createSolutionGroup(name);
            console.log(name)
            $("#createmodel").remove()
        }
    })
}

//创建方案组
function createSolutionGroup(name) {
    $.ajax({
        url: ctxPath + "project/mkdirgroup",
        data: {
            group_name: name
        },
        type: "post",
        success: function (req) {
            //请求成功时处理
            if (req == "success") {
                showtips("添加成功");
                window.location.href = ctxPath + "project"
            } else {
                showtips("添加失败");
            }
        },
    })
}

function showtips(name) {
    $.blockUI({
        message: name,
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

//加载页面前的加载动画
var showpageloading = function () {
    var pageloading =
        '<div class="preloader">' +
        '	<div class="lds-ripple">' +
        '		<div class="lds-pos"></div>' +
        '		<div class="lds-pos"></div>' +
        '	</div>' +
        '</div>'
    $("body").append(pageloading)
}

function hidepageloading() {
    $(".preloader").fadeOut();
    setTimeout(() => {
        $(".preloader").remove()
    }, 500)
}

hidepageloading()

//暂无数据的提示
function nodata(id, text, url) {
    if (text == undefined) {
        text = '暂无数据'
    }
    if (url == undefined) {
        url = "#"
    }
    var nodata =
        '<div class="nodata">' +
        '    <svg class="icon">' +
        '    	<use xlink:href="#icon-nodata3"></use>' +
        '	</svg>' +
        '	<p><a href=' + url + '>' + text + '</a></p>' +
        '</div>'
    $(id).html(nodata)
}

//局部加载转圈，位置错误需要在父元素加相对定位
function loading(id) {
    var load =
        '<div class="text-center over-load">' +
        '    <div class="spinner-border spinner-border text-info" role="status">' +
        '      <span class="sr-only"></span>' +
        '    </div>' +
        '</div>'
    $(id).html(load)
}

//请求失败的提示
function dataerror(id, text, url) {
    if (text == undefined) {
        text = '暂无数据'
    }
    if (url == undefined) {
        url = "#"
    }
    var dataerror =
        '<div class="nodata">' +
        '    <svg class="icon">' +
        '    	<use xlink:href="#icon-loaderror"></use>' +
        '	</svg>' +
        '	<p>加载失败 请稍后再试</p>' +
        '</div>'

    $(id).html(dataerror)
}

//主题观点类聚 弹出层<!-- showinfo 弹出层 -->
function showpoint(id) {
    var pointdata =
        '<div class="usercenter-box" id="showhot">' +
        '<div class="hotpoint">' +
        '<div class="hotpoint-title">' +
        '	全球主要产油国达成规模最大减产 业内人士：油价回升不会太快 ' +
        '	<i class="mdi mdi-close-circle-outline"></i>' +
        '</div>' +
        '<div class="hotpoint-content"> ' +
        '<a href="#">后疫情时代餐饮业发展趋势预判</a>' +
        '<a href="#">新天药业：预计一季度净利同比下降79.82%-85.58%</a>' +
        '<a href="#">原油亚盘：美国WTI原油大涨8%后回落 历史性减产协议仍存在隐患</a>' +
        '<a href="#">宣传“深呼吸十秒法”可测是否感染新冠，日本爱知县警方道歉</a>' +
        '<a href="#">午间公告|新乡化纤一季度净利润同比增长125.15%-168.04%</a>' +
        '<a href="#">全面复工难题如何破解？――民营经济大县福建晋江蹲点观察</a>' +
        '<a href="#">全球多行业停工停产，这些海外业务占比超50%的A股或受影响</a>' +
        '<a href="#">瑞幸"凉"了? 空头“猎杀”中概股十年之变：从无名之辈到明星公司</a>' +
        '</div>' +
        '</div>' +
        '</div>'
    $("body").append(pointdata)
    $(".mdi.mdi-close-circle-outline").click(function () {
        $("#showhot").remove()
    })
}
                 
        