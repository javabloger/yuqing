//********************************//
    //initialization of dualbox
    //********************************//
    // Basic Dual Listbox
    //********************************//
    $('.duallistbox').bootstrapDualListbox();

    //********************************//
    // Without Filter
    //********************************//
    $('.duallistbox-no-filter').bootstrapDualListbox({
        showFilterInputs: false
    });

    //********************************//
    // Multi selection Dual Listbox
    //********************************//
    $('.duallistbox-multi-selection').bootstrapDualListbox({
        nonSelectedListLabel: 'Non-selected Dual',
        selectedListLabel: 'Selected',
        preserveSelectionOnMove: 'moved',
        moveOnSelect: false
    });

    //********************************//
    //With Filter Options
    //********************************//
    $('.duallistbox-with-filter').bootstrapDualListbox({
        nonSelectedListLabel: 'Non-selected Dual',
        selectedListLabel: 'Selected',
        preserveSelectionOnMove: 'moved',
        moveOnSelect: false,
        nonSelectedFilter: 'Berlin|Frankfurt'
    });

    //********************************//
    // for font awesome
    //********************************//
    $(function() {
        
        $('.moveall i').removeClass().addClass('fas fa-angle-right');
        $('.removeall i').removeClass().addClass('fas fa-angle-left');
        $('.move i').removeClass().addClass('fas fa-angle-right');
        $('.remove i').removeClass().addClass('fas fa-angle-left');
    });

    //********************************//
    // Custom Text Support
    //********************************//
    $('.duallistbox-custom-text').bootstrapDualListbox({
        moveOnSelect: false,
        filterTextClear: "Show All Options",
        filterPlaceHolder: "Filter Options",
        infoText: 'Showing {0} Option(s)',
        infoTextFiltered: '<span class="badge badge-info">Filtered List</span> {0} from {1}',
        infoTextEmpty: 'No Options Listed',
    });

    //********************************//
    //Custom Height
    //********************************//
    $('.duallistbox-custom-height').bootstrapDualListbox({
        moveOnSelect: false,
        selectorMinimalHeight: 250
    });

    //********************************//
    // Add dynamic Option
    //********************************//
    var duallistboxDynamic = $('.duallistbox-dynamic').bootstrapDualListbox({
        moveOnSelect: false
    });
    var numb = 25;
    $(".duallistbox-add").on('click', function() {
        var opt1 = numb + 1;
        var opt2 = numb + 2;
        duallistboxDynamic.append('<option value="' + opt1 + '">London</option><option value="' + opt2 + '" selected>Rome</option>');
        duallistboxDynamic.bootstrapDualListbox('refresh');
    });

    $(".duallistbox-add-clear").on('click', function() {
        var opt1 = numb + 1;
        var opt2 = numb + 2;
        duallistboxDynamic.append('<option value="' + opt1 + '">London</option><option value="' + opt2 + '" selected>Rome</option>');
        duallistboxDynamic.bootstrapDualListbox('refresh', true);
    });