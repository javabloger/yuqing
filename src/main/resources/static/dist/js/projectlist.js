// jQuery

$(function() {
    "use strict";

    var mail = $('.email-table .max-texts a');

    // Highlight row when checkbox is checked
    $('.project-list-box').find('.project-chb').find('input[type=checkbox]').on('change', function() {
        if ($(this).is(':checked')) {
            $(this).parents('.project-list-box').addClass('selected');
        } else {
            $(this).parents('.project-list-box').removeClass('selected');
        }
    });

    $(".sl-all").on('click', function() {
        $('.project-chb input:checkbox').not(this).prop('checked', this.checked);
        if ($('.project-chb input:checkbox').is(':checked')) {
            $('.project-chb input:checkbox').parents('.project-list-box').addClass('selected');
        } else {
            $('.project-chb input:checkbox').parents('.project-list-box').removeClass('selected');
        }
    });

});