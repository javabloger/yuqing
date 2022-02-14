$(function() {

    // Success Type
    $('#ts-success').on('click', function() {
        toastr.success('Have fun storming the castle!', 'Miracle Max Says');
    });

    // Success Type
    $('#ts-info').on('click', function() {
        toastr.info('We do have the Kapua suite available.', 'Turtle Bay Resort');
    });

    // Success Type
    $('#ts-warning').on('click', function() {
        toastr.warning('My name is Inigo Montoya. You killed my father, prepare to die!');
    });

    // Success Type
    $('#ts-error').on('click', function() {
        toastr.error('I do not think that word means what you think it means.', 'Inconceivable!');
    });

    // Position Top Left
    $('#pos-top-left').on('click', function() {
        toastr.info('I do not think that word means what you think it means.', 'Top Left!', { positionClass: 'toastr toast-top-left', containerId: 'toast-top-left' });
    });

    // Position Top Center
    $('#pos-top-center').on('click', function() {
        toastr.info('I do not think that word means what you think it means.', 'Top Center!', { positionClass: 'toastr toast-top-center', containerId: 'toast-top-center' });
    });

    // Position Top Right
    $('#pos-top-right').on('click', function() {
        toastr.info('I do not think that word means what you think it means.', 'Top Right!', { positionClass: 'toastr toast-top-right', containerId: 'toast-top-right' });
    });

    // Position Top Full Width
    $('#pos-top-full').on('click', function() {
        toastr.info('I do not think that word means what you think it means.', 'Top Full Width!', { positionClass: 'toastr toast-top-full-width', containerId: 'toast-top-full-width' });
    });

    // Position Bottom Left
    $('#pos-bottom-left').on('click', function() {
        toastr.info('I do not think that word means what you think it means.', 'Bottom Left!', { positionClass: 'toastr toast-bottom-left', containerId: 'toast-bottom-left' });
    });

    // Position Bottom Center
    $('#pos-bottom-center').on('click', function() {
        toastr.info('I do not think that word means what you think it means.', 'Bottom Center!', { positionClass: 'toastr toast-bottom-center', containerId: 'toast-bottom-center' });
    });

    // Position Bottom Right
    $('#pos-bottom-right').on('click', function() {
        toastr.info('I do not think that word means what you think it means.', 'Bottom Right!', { positionClass: 'toastr toast-bottom-right', containerId: 'toast-bottom-right' });
    });

    // Position Bottom Full Width
    $('#pos-bottom-full').on('click', function() {
        toastr.info('I do not think that word means what you think it means.', 'Bottom Full Width!', { positionClass: 'toastr toast-bottom-full-width', containerId: 'toast-bottom-full-width' });
    });

    // Text Notification
    $('#text-notification').on('click', function() {
        toastr.info('Have fun storming the castle!', 'Miracle Max Says');
    });

    // Close Button
    $('#close-button').on('click', function() {
        toastr.success('Have fun storming the castle!', 'With Close Button', { "closeButton": true });
    });

    // Progress Bar
    $('#progress-bar').on('click', function() {
        toastr.warning('Have fun storming the castle!', 'Progress Bar', { "progressBar": true });
    });

    // Clear Toast Button
    $('#clear-toast-btn').on('click', function() {
        toastr.error('Clear itself?<br /><br /><button type="button" class="btn btn-secondary clear">Yes</button>', 'Clear Toast Button');
    });

    // Slide Down / Slide Up
    $('#slide-toast').on('click', function() {
        toastr.success('I do not think that word means what you think it means.', 'Slide Down / Slide Up!', { "showMethod": "slideDown", "hideMethod": "slideUp", timeOut: 2000 });
    });

    // Fade In / Fade Out
    $('#fade-toast').on('click', function() {
        toastr.success('I do not think that word means what you think it means.', 'Slide Down / Slide Up!', { "showMethod": "fadeIn", "hideMethod": "fadeOut", timeOut: 2000 });
    });
});