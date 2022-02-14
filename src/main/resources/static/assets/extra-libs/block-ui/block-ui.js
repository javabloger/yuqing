$(function() {
    $('.block-card').on('click', function() {
        var block_ele = $(this).closest('.card');
        $(block_ele).block({
            message: '<i class="fas fa-spin fa-sync text-white"></i>',
            timeout: 2000, //unblock after 2 seconds
            overlayCSS: {
                backgroundColor: '#000',
                opacity: 0.5,
                cursor: 'wait'
            },
            css: {
                border: 0,
                padding: 0,
                backgroundColor: 'transparent'
            }
        });
    });

    // Block sidebar
    $('.block-sidenav').on('click', function() {
        var block_ele = $('.left-sidebar');
        $(block_ele).block({
            message: '<i class="fas fa-spin fa-sync text-white"></i>',
            timeout: 2000, //unblock after 2 seconds
            overlayCSS: {
                backgroundColor: '#000',
                opacity: 0.5,
                cursor: 'wait'
            },
            css: {
                border: 0,
                padding: 0,
                backgroundColor: 'transparent'
            }
        });
    });


    // Block page
    $('.block-default').on('click', function() {
        $.blockUI({
            message: '<i class="fas fa-spin fa-sync text-white"></i>',
            timeout: 2000, //unblock after 2 seconds
            overlayCSS: {
                backgroundColor: '#000',
                opacity: 0.5,
                cursor: 'wait'
            },
            css: {
                border: 0,
                padding: 0,
                backgroundColor: 'transparent'
            }
        });
    });

    // onBlock callback
    $('.onblock').on('click', function() {
        $.blockUI({
            message: '<i class="fas fa-spin fa-sync text-white"></i>',
            fadeIn: 1000,
            timeout: 2000, //unblock after 2 seconds
            overlayCSS: {
                backgroundColor: '#000',
                opacity: 0.5,
                cursor: 'wait'
            },
            css: {
                border: 0,
                padding: 0,
                color: '#333',
                backgroundColor: 'transparent'
            },
            onBlock: function() {
                alert('Page blocked!');
            }
        });
    });


    // onUnblock callback
    $('.onunblock').on('click', function() {
        $.blockUI({
            message: '<i class="fas fa-spin fa-sync text-white"></i>',
            timeout: 2000, //unblock after 2 seconds
            overlayCSS: {
                backgroundColor: '#000',
                opacity: 0.5,
                cursor: 'wait'
            },
            css: {
                border: 0,
                padding: 0,
                color: '#333',
                backgroundColor: 'transparent'
            },
            onUnblock: function() {
                alert('Page unblocked!');
            }
        });
    });


    // Overlay callback
    $('.onoverlay-click').on('click', function() {
        $.blockUI({
            message: '<i class="fas fa-spin fa-sync text-white"></i>',
            overlayCSS: {
                backgroundColor: '#000',
                opacity: 0.5,
                cursor: 'wait'
            },
            css: {
                color: '#333',
                border: 0,
                padding: 0,
                backgroundColor: 'transparent'
            },
            onOverlayClick: $.unblockUI
        });
    });

    // Block Without Message
    $('.without-msg').on('click', function() {
        var block_ele = $(this).closest('.card');
        $(block_ele).block({
            message: null,
            timeout: 2000, //unblock after 2 seconds
            overlayCSS: {
                backgroundColor: '#000',
                opacity: 0.5,
                cursor: 'wait'
            },
            css: {
                border: 0,
                padding: 0,
                backgroundColor: 'transparent'
            }
        });
    });


    // Block without overlay
    $('.without-overlay').on('click', function() {
        var block_ele = $(this).closest('.card');
        $(block_ele).block({
            message: '<i class="fas fa-spin fa-sync text-white"></i>',
            showOverlay: false,
            timeout: 2000, //unblock after 2 seconds
            css: {
                width: 50,
                height: 50,
                lineHeight: 1,
                color: '#fff',
                border: 0,
                padding: 15,
                backgroundColor: '#000'
            }
        });
    });

    // Unblock on overlay click
    $('.overlay-unblock').on('click', function() {
        var block_ele = $(this).closest('.card');
        $(block_ele).block({
            message: '<i class="fas fa-spin fa-sync text-white"></i>',
            overlayCSS: {
                backgroundColor: '#000',
                opacity: 0.5,
                cursor: 'wait'
            },
            css: {
                border: 0,
                padding: 0,
                backgroundColor: 'transparent'
            }
        });

        $('.blockOverlay').on('click', function() {
            $(block_ele).unblock();
        });
    });

    // Growl notification
    $('.growl').on('click', function() {
        $.blockUI({
            message: $('.growl-notification-example'),
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

});