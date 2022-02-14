/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: datatable_api_init
/*************************************************************************************/

//==================================================//
//                      Add row                     //
//==================================================//
var t = $('#t_add_row').DataTable();
var counter = 1;

$('#addRow').on('click', function() {
    t.row.add([
        counter + '.1',
        counter + '.2',
        counter + '.3',
        counter + '.4',
        counter + '.5'
    ]).draw(false);

    counter++;
});

// Automatically add a first row of data
$('#addRow').click();

//==================================================//
// Individual column searching (select inputs)       //
//==================================================//

$('.datatable-select-inputs').DataTable({
    initComplete: function() {
        this.api().columns().every(function() {
            var column = this;
            var select = $('<select><option value="">Select option</option></select>')
                .appendTo($(column.footer()).empty())
                .on('change', function() {
                    var val = $.fn.dataTable.util.escapeRegex(
                        $(this).val()
                    );

                    column
                        .search(val ? '^' + val + '$' : '', true, false)
                        .draw();
                });

            column.data().unique().sort().each(function(d, j) {
                select.append('<option value="' + d + '">' + d + '</option>');
            });
        });
    }
});

//==================================================//
// Individual column searching (text inputs)        //
//==================================================//
// Setup - add a text input to each footer cell
$('.text-inputs-searching tfoot th').each(function() {
    var title = $(this).text();
    $(this).html('<input type="text" placeholder="Search ' + title + '" />');
});

// DataTable
var tableSearching = $('.text-inputs-searching').DataTable();

// Apply the search
tableSearching.columns().every(function() {
    var that = this;

    $('input', this.footer()).on('keyup change', function() {
        if (that.search() !== this.value) {
            that
                .search(this.value)
                .draw();
        }
    });
});



//==================================================//
// Child rows (show extra / detailed information)   //
//==================================================//
/* Formatting function for row details - modify as you need */
function format(d) {
    // `d` is the original data object for the row
    return '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">' +
        '<tr>' +
        '<td>Full name:</td>' +
        '<td>' + d.name + '</td>' +
        '</tr>' +
        '<tr>' +
        '<td>Extension number:</td>' +
        '<td>' + d.extn + '</td>' +
        '</tr>' +
        '<tr>' +
        '<td>Extra info:</td>' +
        '<td>And any further details here (images etc)...</td>' +
        '</tr>' +
        '</table>';
}

//=============================================//
// -- Child rows 
//=============================================//
var tableChildRows = $('.show-child-rows').DataTable({
    "ajax": "../../dist/js/pages/datatable/data.json",
    "columns": [{
            "className": 'details-control',
            "orderable": false,
            "data": null,
            "defaultContent": ''
        },
        { "data": "name" },
        { "data": "position" },
        { "data": "office" },
        { "data": "salary" }
    ],
    "order": [
        [1, 'asc']
    ]
});

//=============================================//
// Add event listener for opening and closing details
//=============================================//
$('.show-child-rows tbody').on('click', 'td.details-control', function() {
    var tr = $(this).closest('tr');
    var row = tableChildRows.row(tr);

    if (row.child.isShown()) {
        // This row is already open - close it
        row.child.hide();
        tr.removeClass('shown');
    } else {
        // Open this row
        row.child(format(row.data())).show();
        tr.addClass('shown');
    }
});


//==================================================//
//          Row selection (multiple rows)           //
//==================================================//
var table1 = $('#row_select').DataTable();

$('#row_select tbody').on('click', 'tr', function() {
    $(this).toggleClass('selected');
});

$('#button').click(function() {
    alert(table1.rows('.selected').data().length + ' row(s) selected');
});

//==================================================//
//              Form Inputs                         //
//==================================================//

var table2 = $('#form_inputs').DataTable();

$('.inputs-submit').click(function() {
    var data = table2.$('input, select').serialize();
    alert(
        "The following data would have been submitted to the server: \n\n" +
        data.substr(0, 120) + '...'
    );
    return false;
});

//==================================================//
//  Row selection and deletion (single row)         //
//==================================================//
var table3 = $('#sing_row_del').DataTable();

$('#sing_row_del tbody').on('click', 'tr', function() {
    if ($(this).hasClass('selected')) {
        $(this).removeClass('selected');
    } else {
        table3.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');
    }
});

$('#delete-row').click(function() {
    table3.row('.selected').remove().draw(false);
});