// Admin Panel settings
$.fn.AdminSettings = function (settings) {
    var myid = this.attr("id");
    // General option for vertical header 
    var defaults = {
        Theme: true, // this can be true or false ( true means dark and false means light ),
        Layout: 'vertical', // 
        LogoBg: 'skin1', // You can change the Value to be skin1/skin2/skin3/skin4/skin5/skin6 
        NavbarBg: 'skin6', // You can change the Value to be skin1/skin2/skin3/skin4/skin5/skin6 
        SidebarType: 'full', // You can change it full / mini-sidebar
        SidebarColor: 'skin1', // You can change the Value to be skin1/skin2/skin3/skin4/skin5/skin6
        SidebarPosition: false, // it can be true / false
        HeaderPosition: false, // it can be true / false
        BoxedLayout: false, // it can be true / false 
    };
    var settings = $.extend({}, defaults, settings);
    // Attribute functions 
    var AdminSettings = {
        // Settings INIT
        AdminSettingsInit: function () {
            AdminSettings.ManageTheme();
            AdminSettings.ManageThemeLayout();
            AdminSettings.ManageThemeBackground();
            AdminSettings.ManageSidebarType();
            AdminSettings.ManageSidebarColor();
            AdminSettings.ManageSidebarPosition();
            AdminSettings.ManageBoxedLayout();
        }
        , //****************************
        // ManageThemeLayout functions
        //****************************
        ManageTheme: function () {
            var themeview = settings.Theme;
            switch (settings.Layout) {
            case 'vertical':
                if (themeview == true) {
                    $('body').attr("data-theme", 'dark');
                    $("#theme-view").prop("checked", !0);
                }
                else {
                    $('#' + myid).attr("data-theme", 'light');
                    $("body").prop("checked", !1);
                }
                break;
                   
            default:
            }
        }
        , //****************************
        // ManageThemeLayout functions
        //****************************
        ManageThemeLayout: function () {
            switch (settings.Layout) {
            case 'horizontal':
                $('#' + myid).attr("data-layout", "horizontal");
                break;
            case 'vertical':
                $('#' + myid).attr("data-layout", "vertical");
                $('.scroll-sidebar').perfectScrollbar({ });
                break;
            default:
            }
        }
        , //****************************
        // ManageSidebarType functions 
        //****************************
        ManageThemeBackground: function () {
            // Logo bg attribute
            function setlogobg() {
                var lbg = settings.LogoBg;
                if (lbg != undefined && lbg != "") {
                    $('#' + myid + ' .topbar .top-navbar .navbar-header').attr("data-logobg", lbg);
                }
                else {
                    $('#' + myid + ' .topbar .top-navbar .navbar-header').attr("data-logobg", "skin1");
                }
            };
            setlogobg();
            // Navbar bg attribute
            function setnavbarbg() {
                var nbg = settings.NavbarBg;
                if (nbg != undefined && nbg != "") {
                    $('#' + myid + ' .topbar .navbar-collapse').attr("data-navbarbg", nbg);
                    $('#' + myid + ' .topbar').attr("data-navbarbg", nbg);
                    $('#' + myid).attr("data-navbarbg", nbg);
                }
                else {
                    $('#' + myid + ' .topbar .navbar-collapse').attr("data-navbarbg", "skin1");
                     $('#' + myid + ' .topbar').attr("data-navbarbg", "skin1");
                    $('#' + myid).attr("data-navbarbg", "skin1");
                }
            };
            setnavbarbg();
        }
        , //****************************
        // ManageThemeLayout functions
        //****************************
        ManageSidebarType: function () {
            switch (settings.SidebarType) {
                //****************************
                // If the sidebar type has full
                //****************************     
            case 'full':
                $('#' + myid).attr("data-sidebartype", "full");
                //****************************
                /* This is for the mini-sidebar if width is less then 1170*/
                //**************************** 
                var setsidebartype = function () {
                    var width = (window.innerWidth > 0) ? window.innerWidth : this.screen.width;
                    if (width < 1170) {
                        $("#main-wrapper").attr("data-sidebartype", "mini-sidebar");
                        $("#main-wrapper").addClass("mini-sidebar");
                    }
                    else {
                        $("#main-wrapper").attr("data-sidebartype", "full");
                        $("#main-wrapper").removeClass("mini-sidebar");
                    }
                };
                $(window).ready(setsidebartype);
                $(window).on("resize", setsidebartype);
                //****************************
                /* This is for sidebartoggler*/
                //****************************
                $('.sidebartoggler').on("click", function () {
                    $("#main-wrapper").toggleClass("mini-sidebar");
                    if ($("#main-wrapper").hasClass("mini-sidebar")) {
                        $(".sidebartoggler").prop("checked", !0);
                        $("#main-wrapper").attr("data-sidebartype", "mini-sidebar");
                    }
                    else {
                        $(".sidebartoggler").prop("checked", !1);
                        $("#main-wrapper").attr("data-sidebartype", "full");
                    }
                });
                break;
                //****************************
                // If the sidebar type has mini-sidebar
                //****************************       
            case 'mini-sidebar':
                $('#' + myid).attr("data-sidebartype", "mini-sidebar");
                //****************************
                /* This is for sidebartoggler*/
                //****************************
                $('.sidebartoggler').on("click", function () {
                    $("#main-wrapper").toggleClass("mini-sidebar");
                    if ($("#main-wrapper").hasClass("mini-sidebar")) {
                        $(".sidebartoggler").prop("checked", !0);
                        $("#main-wrapper").attr("data-sidebartype", "full");
                    }
                    else {
                        $(".sidebartoggler").prop("checked", !1);
                        $("#main-wrapper").attr("data-sidebartype", "mini-sidebar");
                    }
                });
                break;
                //****************************
                // If the sidebar type has iconbar
                //****************************       
            case 'iconbar':
                $('#' + myid).attr("data-sidebartype", "iconbar");
                //****************************
                /* This is for the mini-sidebar if width is less then 1170*/
                //**************************** 
                var setsidebartype = function () {
                    var width = (window.innerWidth > 0) ? window.innerWidth : this.screen.width;
                    if (width < 1170) {
                        $("#main-wrapper").attr("data-sidebartype", "mini-sidebar");
                        $("#main-wrapper").addClass("mini-sidebar");
                    }
                    else {
                        $("#main-wrapper").attr("data-sidebartype", "iconbar");
                        $("#main-wrapper").removeClass("mini-sidebar");
                    }
                };
                $(window).ready(setsidebartype);
                $(window).on("resize", setsidebartype);
                //****************************
                /* This is for sidebartoggler*/
                //****************************
                $('.sidebartoggler').on("click", function () {
                    $("#main-wrapper").toggleClass("mini-sidebar");
                    if ($("#main-wrapper").hasClass("mini-sidebar")) {
                        $(".sidebartoggler").prop("checked", !0);
                        $("#main-wrapper").attr("data-sidebartype", "mini-sidebar");
                    }
                    else {
                        $(".sidebartoggler").prop("checked", !1);
                        $("#main-wrapper").attr("data-sidebartype", "iconbar");
                    }
                });
                break;
                //****************************
                // If the sidebar type has overlay
                //****************************       
            case 'overlay':
                $('#' + myid).attr("data-sidebartype", "overlay");
                var setsidebartype = function () {
                    var width = (window.innerWidth > 0) ? window.innerWidth : this.screen.width;
                    if (width < 767) {
                        $("#main-wrapper").attr("data-sidebartype", "mini-sidebar");
                        $("#main-wrapper").addClass("mini-sidebar");
                    }
                    else {
                        $("#main-wrapper").attr("data-sidebartype", "overlay");
                        $("#main-wrapper").removeClass("mini-sidebar");
                    }
                };
                $(window).ready(setsidebartype);
                $(window).on("resize", setsidebartype);
                //****************************
                /* This is for sidebartoggler*/
                //****************************
                $('.sidebartoggler').on("click", function () {
                    $("#main-wrapper").toggleClass("show-sidebar");
                    if ($("#main-wrapper").hasClass("show-sidebar")) {
                        //$(".sidebartoggler").prop("checked", !0);
                        //$("#main-wrapper").attr("data-sidebartype","mini-sidebar");
                    }
                    else {
                        //$(".sidebartoggler").prop("checked", !1);
                        //$("#main-wrapper").attr("data-sidebartype","iconbar");
                    }
                });
                break;
            default:
            }
        }
        , //****************************
        // ManageSidebarColor functions 
        //****************************
        ManageSidebarColor: function () {
            // Logo bg attribute
            function setsidebarbg() {
                var sbg = settings.SidebarColor;
                if (sbg != undefined && sbg != "") {
                    $('#' + myid + ' .left-sidebar').attr("data-sidebarbg", sbg);
                }
                else {
                    $('#' + myid + ' .left-sidebar').attr("data-sidebarbg", "skin1");
                }
            };
            setsidebarbg();
        }
        , //****************************
        // ManageSidebarPosition functions
        //****************************
        ManageSidebarPosition: function () {
            var sidebarposition = settings.SidebarPosition;
            var headerposition = settings.HeaderPosition;
            switch (settings.Layout) {
            case 'vertical':
                if (sidebarposition == true) {
                    $('#' + myid).attr("data-sidebar-position", 'fixed');
                    $("#sidebar-position").prop("checked", !0);
                }
                else {
                    $('#' + myid).attr("data-sidebar-position", 'absolute');
                    $("#sidebar-position").prop("checked", !1);
                }
                if (headerposition == true) {
                    $('#' + myid).attr("data-header-position", 'fixed');
                    $("#header-position").prop("checked", !0);
                }
                else {
                    $('#' + myid).attr("data-header-position", 'relative');
                    $("#header-position").prop("checked", !1);
                }
                break;
            default:
            }
        }
        , //****************************
        // ManageBoxedLayout functions
        //****************************
        ManageBoxedLayout: function () {
            var boxedlayout = settings.BoxedLayout;
            switch (settings.Layout) {
            case 'vertical':
                if (boxedlayout == true) {
                    $('#' + myid).attr("data-boxed-layout", 'boxed');
                    $("#boxed-layout").prop("checked", !0);
                }
                else {
                    $('#' + myid).attr("data-boxed-layout", 'full');
                    $("#boxed-layout").prop("checked", !1);
                }
                break;
            case 'horizontal':
                if (boxedlayout == true) {
                    $('#' + myid).attr("data-boxed-layout", 'boxed');
                    $("#boxed-layout").prop("checked", !0);
                }
                else {
                    $('#' + myid).attr("data-boxed-layout", 'full');
                    $("#boxed-layout").prop("checked", !1);
                }
                break;        
            default:
            }
        }
    , };
    AdminSettings.AdminSettingsInit();
};
//****************************
// This is for the chat customizer setting
//****************************
$(function () {
    var chatarea = $("#chat");
    $('#chat .message-center a').on('click', function () {
        var name = $(this).find(".mail-contnet h5").text();
        var img = $(this).find(".user-img img").attr("src");
        var id = $(this).attr("data-user-id");
        var status = $(this).find(".profile-status").attr("data-status");
        if ($(this).hasClass("active")) {
            $(this).toggleClass("active");
            $(".chat-windows #user-chat" + id).hide();
        }
        else {
            $(this).toggleClass("active");
            if ($(".chat-windows #user-chat" + id).length) {
                $(".chat-windows #user-chat" + id).removeClass("mini-chat").show();
            }
            else {
                var msg = msg_receive('I watched the storm, so beautiful yet terrific.');
                msg += msg_sent('That is very deep indeed!');
                var html = "<div class='user-chat' id='user-chat" + id + "' data-user-id='" + id + "'>";
                html += "<div class='chat-head'><img src='" + img + "' data-user-id='" + id + "'><span class='status " + status + "'></span><span class='name'>" + name + "</span><span class='opts'><i class='ti-close closeit' data-user-id='" + id + "'></i><i class='ti-minus mini-chat' data-user-id='" + id + "'></i></span></div>";
                html += "<div class='chat-body'><ul class='chat-list'>" + msg + "</ul></div>";
                html += "<div class='chat-footer'><input type='text' data-user-id='" + id + "' placeholder='Type & Enter' class='form-control'></div>";
                html += "</div>";
                $(".chat-windows").append(html);
            }
        }
    });
    $(document).on('click', ".chat-windows .user-chat .chat-head .closeit", function (e) {
        var id = $(this).attr("data-user-id");
        $(".chat-windows #user-chat" + id).hide();
        $("#chat .message-center .user-info#chat_user_" + id).removeClass("active");
    });
    $(document).on('click', ".chat-windows .user-chat .chat-head img, .chat-windows .user-chat .chat-head .mini-chat", function (e) {
        var id = $(this).attr("data-user-id");
        if (!$(".chat-windows #user-chat" + id).hasClass("mini-chat")) {
            $(".chat-windows #user-chat" + id).addClass("mini-chat");
        }
        else {
            $(".chat-windows #user-chat" + id).removeClass("mini-chat");
        }
    });
    $(document).on('keypress', ".chat-windows .user-chat .chat-footer input", function (e) {
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
    $(".page-wrapper").on('click', function (e) {
        $('.chat-windows').addClass('hide-chat');
        $('.chat-windows').removeClass('show-chat');
    });
    $(".service-panel-toggle").on('click', function (e) {
        $('.chat-windows').addClass('show-chat');
        $('.chat-windows').removeClass('hide-chat');
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