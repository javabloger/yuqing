$(function() {

    var chatarea = $("#chat");

    $('#chat .message-center a').on('click', function() {

        var name = $(this).find(".mail-contnet h5").text();
        var img = $(this).find(".user-img img").attr("src");
        var id = $(this).attr("data-user-id");
        var status = $(this).find(".profile-status").attr("data-status");

        if ($(this).hasClass("active")) {
            $(this).toggleClass("active");
            $(".chat-windows #user-chat" + id).hide();
        } else {
            $(this).toggleClass("active");
            if ($(".chat-windows #user-chat" + id).length) {
                $(".chat-windows #user-chat" + id).removeClass("mini-chat").show();
            } else {
                var msg = msg_receive('I watched the storm, so beautiful yet terrific.');
                msg += msg_sent('That is very deep indeed!');
                var html = "<div class='user-chat' id='user-chat" + id + "' data-user-id='" + id + "'>";
                html += "<div class='chat-head'><img src='" + img + "' data-user-id='" + id + "'><span class='status " + status + "'></span><span class='name'>" + name + "</span><span class='opts'><i class='material-icons closeit' data-user-id='" + id + "'>clear</i><i class='material-icons mini-chat' data-user-id='" + id + "'>remove</i></span></div>";
                html += "<div class='chat-body'><ul class='chat-list'>" + msg + "</ul></div>";
                html += "<div class='chat-footer'><input type='text' data-user-id='" + id + "' placeholder='Type & Enter' class='form-control'></div>";
                html += "</div>";
                $(".chat-windows").append(html);
            }
        }
    });

    $("body").on('click', ".chat-windows .user-chat .chat-head .closeit", function(e) {
        var id = $(this).attr("data-user-id");
        $(".chat-windows #user-chat" + id).hide();
        $("#chat .message-center .user-info#chat_user_" + id).removeClass("active");
    });

    $("body").on('click', ".chat-windows .user-chat .chat-head img, .chat-windows .user-chat .chat-head .mini-chat", function(e) {
        var id = $(this).attr("data-user-id");
        if (!$(".chat-windows #user-chat" + id).hasClass("mini-chat")) {
            $(".chat-windows #user-chat" + id).addClass("mini-chat");
        } else {
            $(".chat-windows #user-chat" + id).removeClass("mini-chat");
        }
    });

    $("body").on('keypress', ".chat-windows .user-chat .chat-footer input", function(e) {
        if (e.keyCode == 13) {
            var id = $(this).attr("data-user-id");
            var msg = $(this).val();
            msg = msg_sent(msg);
            $(".chat-windows #user-chat" + id + " .chat-body .chat-list").append(msg);
            $(this).val("");
            $(this).focus();
        }
        $(".chat-windows #user-chat" + id + " .chat-body").perfectScrollbar({
            suppressScrollX: true
        });
    });
});

function msg_receive(msg) {
    var d = new Date();
    var h = d.getHours();
    var m = d.getMinutes();
    return "<li class='msg_receive'><div class='chat-content'><div class='box bg-light-info'>" + msg + "</div></div><div class='chat-time'>" + h + ":" + m + "</div></li>";
}

function msg_sent(msg) {
    var d = new Date();
    var h = d.getHours();
    var m = d.getMinutes();
    return "<li class='odd msg_sent'><div class='chat-content'><div class='box bg-light-info'>" + msg + "</div><br></div><div class='chat-time'>" + h + ":" + m + "</div></li>";
}