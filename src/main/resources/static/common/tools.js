
function timeParse(arg) {
    if (!arg || arg.length != 19) {
        return arg;
    }
    var arg2 = arg.replace(/-/g, '/')
    var nowTime = new Date().getTime();
    var argTime = new Date(arg2).getTime();
    var timeDifference = nowTime - argTime;
    if (timeDifference < 0) {
        return arg;
    }
    var days = Math.floor(timeDifference / (24 * 3600 * 1000));//计算出相差天数
    var leave1 = timeDifference % (24 * 3600 * 1000); //计算天数后剩余的毫秒数
    var hours = Math.floor(leave1 / (3600 * 1000)); //计算出小时数
    var leave2 = leave1 % (3600 * 1000); //计算小时数后剩余的毫秒数
    var minutes = Math.floor(leave2 / (60 * 1000));//计算相差分钟数
    //var leave3 = leave2 % (60 * 1000); //计算分钟数后剩余的毫秒数
    //var seconds = Math.round(leave3 / 1000); //计算相差秒数
    if (days > 0) {
        return days + '天前';
    } else {
        if (hours > 0) {
            return hours + '小时前';
        } else {
            if (minutes > 0) {
                return minutes + '分钟前';
            } else {
                return '刚刚';
            }
        }
    }
}

function dealCate(arg) {
    if(arg!=''&& arg !=undefined){
        console.log(arg)
        let parse = JSON.parse(arg);
        console.log(parse[0].type)
        return parse[0].type;
    }else {
        return "";
    }

}



//提示
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