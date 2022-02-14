/***********************************/
// Basic date
/***********************************/
$('.pickadate').pickadate();

/***********************************/
// Change Day & Month strings
/***********************************/
$('.pickadate-short-string').pickadate({
    weekdaysShort: ['S', 'M', 'Tu', 'W', 'Th', 'F', 'S'],
    showMonthsShort: true
});

/***********************************/
// Select Year
/***********************************/
$('.pickadate-select-year').pickadate({
    selectYears: 8
});

/***********************************/
// Change first weekday
/***********************************/
$('.pickadate-firstweek').pickadate({
    firstDay: 1
});

/***********************************/
// Button options
/***********************************/
$('.pickadate-clear-buttons').pickadate({
    today: '',
    close: 'Close Picker',
    clear: ''
});

/***********************************/
// Date limits
/***********************************/
$('.pickadate-datelimits').pickadate({
    min: [2016, 8, 20],
    max: [2016, 10, 30]
});

/***********************************/
// Format options
/***********************************/
$('.pickadate-change-format').pickadate({
    // Escape any 'rule' characters with an exclamation mark (!).
    format: 'Selecte!d Date : dddd, dd mmmm, yyyy',
    formatSubmit: 'mm/dd/yyyy',
    hiddenPrefix: 'prefix__',
    hiddenSuffix: '__suffix'
});

$('.pickadate-changearrow').pickadate({
    monthPrev: '&larr;',
    monthNext: '&rarr;',
    weekdaysShort: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
    showMonthsFull: false
});

/***********************************/
// Disable weekday range
/***********************************/
$('.pickadate-disable-week').pickadate({
    disable: [
        3
    ]
});

/***********************************/
// Disable dates
/***********************************/
$('.pickadate-disable-arbitrarydates').pickadate({
    disable: [
        [2016, 5, 10],
        [2016, 5, 15],
        [2016, 5, 20]
    ]
});

/***********************************/
// Month & Year selectors
/***********************************/
$('.pickadate-selectors').pickadate({
    labelMonthNext: 'Next month',
    labelMonthPrev: 'Previous month',
    labelMonthSelect: 'Pick a Month',
    labelYearSelect: 'Pick a Year',
    selectMonths: true,
    selectYears: true
});

/***********************************/
// With Select
/***********************************/
$('.pickadate-monyear-dropdown').pickadate({
    selectMonths: true,
    selectYears: true
});

/***********************************/
// Events
/***********************************/
$('.pickadate-events').pickadate({
    onStart: function() {
        console.log('Hi there!!!');
    },
    onRender: function() {
        console.log('Holla... rendered new');
    },
    onOpen: function() {
        console.log('Picker Opened');
    },
    onClose: function() {
        console.log("I'm Closed now");
    },
    onStop: function() {
        console.log('Have a great day ahead!!');
    },
    onSet: function(context) {
        console.log('All stuff:', context);
    }
});

/***********************************/
// Picker Translations
/***********************************/
$('.pickadate-translations').pickadate({
    formatSubmit: 'dd/mm/yyyy',
    monthsFull: ['Janvier', 'FÃ©vrier', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'AoÃ»t', 'Septembre', 'Octobre', 'Novembre', 'DÃ©cembre'],
    monthsShort: ['Jan', 'Fev', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Aou', 'Sep', 'Oct', 'Nov', 'Dec'],
    weekdaysShort: ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'],
    today: 'aujourd\'hui',
    clear: 'clair',
    close: 'Fermer'
});

/***********************************/
// Picker min-max range
/***********************************/
$('.pickadate-minmax').pickadate({
    dateMin: -8,
    dateMax: true
});

/***********************************/
// Date Range from & to
/***********************************/
var from_$input = $('#picker_from').pickadate(),
    from_picker = from_$input.pickadate('picker');

var to_$input = $('#picker_to').pickadate(),
    to_picker = to_$input.pickadate('picker');


if (from_picker.get('value')) {
    to_picker.set('min', from_picker.get('select'));
}
if (to_picker.get('value')) {
    from_picker.set('max', to_picker.get('select'));
}

from_picker.on('set', function(event) {
    if (event.select) {
        to_picker.set('min', from_picker.get('select'));
    } else if ('clear' in event) {
        to_picker.set('min', false);
    }
});
to_picker.on('set', function(event) {
    if (event.select) {
        from_picker.set('max', to_picker.get('select'));
    } else if ('clear' in event) {
        from_picker.set('max', false);
    }
});


/***********************************/
//Pick-a-time Picker
/***********************************/
/***********************************/
// Basic time
/***********************************/
$('.pickatime').pickatime();

/***********************************/
// Hide Button
/***********************************/
$('.pickatime-button').pickatime({
    clear: '',
});

/***********************************/
// Format options
/***********************************/
$('.pickatime-format').pickatime({
    format: 'T!ime selected: h:i a',
    formatLabel: 'h:i a',
    formatSubmit: 'HH:i',
    hiddenPrefix: 'prefix__',
    hiddenSuffix: '__suffix'
});

/***********************************/
// Format options
/***********************************/
$('.pickatime-formatTime-display').pickatime({
    format: 'T!ime selected: h:i a',
    formatLabel: '<b>h</b>:i <!i>a</!i>',
    formatSubmit: 'HH:i',
    hiddenPrefix: 'prefix__',
    hiddenSuffix: '__suffix'
});

/***********************************/
// Format options
/***********************************/
$('.pickatime-format-label').pickatime({
    formatLabel: function(time) {
        var hours = (time.pick - this.get('now').pick) / 60,
            label = hours < 0 ? ' !hours to now' : hours > 0 ? ' !hours from now' : 'now';
        return 'h:i a <sm!all>' + (hours ? Math.abs(hours) : '') + label + '</sm!all>';
    }
});

/***********************************/
// Date range to select
/***********************************/
$('.pickatime-minmax-range').pickatime({
    min: new Date(2018, 3, 20, 7),
    max: new Date(2018, 7, 14, 18, 30)
});

/***********************************/
// Time using Integer & Boolean
/***********************************/
$('.pickatime-time-limits').pickatime({
    min: -5,
    max: true
});

/***********************************/
// Intervals
/***********************************/
$('.pickatime-intervals').pickatime({
    interval: 150
});

/***********************************/
//  Diasable Time sets 
/***********************************/
$('.pickatime-disable').pickatime({
    // Disable Using Javascript
    disable: [
        new Date(2016, 3, 20, 4, 30),
        new Date(2016, 3, 20, 9)
    ]
    // Disable Using Array
    /*disable: [
        [0,30],
        [2,0],
        [8,30],
        [9,0]
    ]*/
});

/***********************************/
// Disable using integers
/***********************************/
$('.pickatime-timedisable-integer').pickatime({
    disable: [
        3, 5, 7, 13, 17, 21
    ]
});

/***********************************/
// Disable using object
/***********************************/
$('.pickatime-timedisable-object').pickatime({
    disable: [
        { from: [2, 0], to: [5, 30] }
    ]
});

/***********************************/
// Disable All
/***********************************/
$('.pickatime-disable-all').pickatime({
    disable: [
        true,
        3, 5, 7, [0, 30],
        [2, 0],
        [8, 30],
        [9, 0]
    ]
});

/***********************************/
// Close on a user action
/***********************************/
$('.pickatime-closeuser-action').pickatime({
    closeOnSelect: false,
    closeOnClear: false
});

/***********************************/
// Events
/***********************************/
$('.pickatime-events').pickatime({
    onStart: function() {
        console.log('Hello there :)');
    },
    onRender: function() {
        console.log('Whoa.. rendered anew');
    },
    onOpen: function() {
        console.log('Opened up');
    },
    onClose: function() {
        console.log("Closed now");
    },
    onStop: function() {
        console.log('See ya.');
    },
    onSet: function(context) {
        console.log('Just set stuff:', context);
    }
});

/***********************************/
// Picker Translations
/***********************************/
$('.pickatime-translations').pickatime({
    formatSubmit: 'dd/mm/yyyy',
    monthsFull: ['Janvier', 'FÃ©vrier', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'AoÃ»t', 'Septembre', 'Octobre', 'Novembre', 'DÃ©cembre'],
    monthsShort: ['Jan', 'Fev', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Aou', 'Sep', 'Oct', 'Nov', 'Dec'],
    weekdaysShort: ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'],
    today: 'aujourd\'hui',
    clear: 'clair',
    close: 'Fermer'
});

/***********************************/
// Basic Date Range Picker
/***********************************/
$('.daterange').daterangepicker();

/***********************************/
// Date & Time
/***********************************/
$('.datetime').daterangepicker({
    timePicker: true,
    timePickerIncrement: 30,
    locale: {
        format: 'MM/DD/YYYY h:mm A'
    }
});

/***********************************/
//Calendars are not linked
/***********************************/
$('.timeseconds').daterangepicker({
    timePicker: true,
    timePickerIncrement: 30,
    timePicker24Hour: true,
    timePickerSeconds: true,
    locale: {
        format: 'MM-DD-YYYY h:mm:ss'
    }
});

/***********************************/
// Single Date Range Picker
/***********************************/
$('.singledate').daterangepicker({
    singleDatePicker: true,
    showDropdowns: true
});

/***********************************/
// Auto Apply Date Range
/***********************************/
$('.autoapply').daterangepicker({
    autoApply: true,
});

/***********************************/
// Calendars are not linked
/***********************************/
$('.linkedCalendars').daterangepicker({
    linkedCalendars: false,
});

/***********************************/
// Date Limit
/***********************************/
$('.dateLimit').daterangepicker({
    dateLimit: {
        days: 7
    },
});

/***********************************/
// Show Dropdowns
/***********************************/
$('.showdropdowns').daterangepicker({
    showDropdowns: true,
});

/***********************************/
// Show Week Numbers
/***********************************/
$('.showweeknumbers').daterangepicker({
    showWeekNumbers: true,
});

/***********************************/
// Date Ranges
/***********************************/
$('.dateranges').daterangepicker({
    ranges: {
        'Today': [moment(), moment()],
        'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
        'Last 7 Days': [moment().subtract(6, 'days'), moment()],
        'Last 30 Days': [moment().subtract(29, 'days'), moment()],
        'This Month': [moment().startOf('month'), moment().endOf('month')],
        'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
    }
});

/***********************************/
// Always Show Calendar on Ranges
/***********************************/
$('.shawCalRanges').daterangepicker({
    ranges: {
        'Today': [moment(), moment()],
        'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
        'Last 7 Days': [moment().subtract(6, 'days'), moment()],
        'Last 30 Days': [moment().subtract(29, 'days'), moment()],
        'This Month': [moment().startOf('month'), moment().endOf('month')],
        'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
    },
    alwaysShowCalendars: true,
});

/***********************************/
// Date Limit
/***********************************/
$('.openRight').daterangepicker({
    opens: "left" // left/right/center
});

/***********************************/
// Date Limit
/***********************************/
$('.drops').daterangepicker({
    drops: "up" // up/down
});

/***********************************/
// Date Limit
/***********************************/
$('.buttonClass').daterangepicker({
    drops: "up",
    buttonClasses: "btn",
    applyClass: "btn-info",
    cancelClass: "btn-danger"
});

/***********************************/
// language
/***********************************/
$('.localeRange').daterangepicker({
    ranges: {
        "Aujourd'hui": [moment(), moment()],
        'Hier': [moment().subtract('days', 1), moment().subtract('days', 1)],
        'Les 7 derniers jours': [moment().subtract('days', 6), moment()],
        'Les 30 derniers jours': [moment().subtract('days', 29), moment()],
        'Ce mois-ci': [moment().startOf('month'), moment().endOf('month')],
        'le mois dernier': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
    },
    locale: {
        applyLabel: "Vers l'avant",
        cancelLabel: 'Annulation',
        startLabel: 'Date initiale',
        endLabel: 'Date limite',
        customRangeLabel: 'SÃ©lectionner une date',
        // daysOfWeek: ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi','Samedi'],
        daysOfWeek: ['Di', 'Lu', 'Ma', 'Me', 'Je', 'Ve', 'Sa'],
        monthNames: ['Janvier', 'fÃ©vrier', 'Mars', 'Avril', 'ÐœÐ°i', 'Juin', 'Juillet', 'AoÃ»t', 'Septembre', 'Octobre', 'Novembre', 'Decembre'],
        firstDay: 1
    }
});