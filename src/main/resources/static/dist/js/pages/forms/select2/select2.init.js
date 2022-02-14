/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: c3_chart_JS
/*************************************************************************************/

//***********************************//
//initialization of select2
//***********************************//

//***********************************//
// For select 2
//***********************************//
$(".select2").select2();

// Single Select Placeholder
$("#select2-with-placeholder").select2({
    placeholder: "Select a state",
    allowClear: true
});

//***********************************//
// Hiding the search box
//***********************************//
$("#select2-search-hide").select2({
    minimumResultsForSearch: Infinity
});

//***********************************//
// Select With Icon
//***********************************//
$("#select2-with-icons").select2({
    minimumResultsForSearch: Infinity,
    templateResult: iconFormat,
    templateSelection: iconFormat,
    escapeMarkup: function(es) { return es; }
});

function iconFormat(icon) {
    var originalOption = icon.element;
    if (!icon.id) { return icon.text; }
    var $icon = "<i class='fab fa-" + $(icon.element).data('icon') + "'></i>" + icon.text;
    return $icon;
}

//***********************************//
// Limiting the number of selections
//***********************************//
$("#select2-max-length").select2({
    maximumSelectionLength: 3,
    placeholder: "Select only maximum 3 items"
});

//***********************************//
//multiple-select2-with-icons
//***********************************//
$("#multiple-select2-with-icons").select2({
    minimumResultsForSearch: Infinity,
    templateResult: iconFormat,
    templateSelection: iconFormat,
    escapeMarkup: function(es) { return es; }
});

function iconFormat(icon) {
    var originalOption = icon.element;
    if (!icon.id) { return icon.text; }
    var $icon = "<i class='fab fa-" + $(icon.element).data('icon') + "'></i>" + icon.text;
    return $icon;
}

//***********************************//
// DOM Events
//***********************************//
var $selectEvent = $(".js-events");
$selectEvent.select2({
    placeholder: "DOM Events"
});
$selectEvent.on("select2:open", function(e) {
    alert("Open Event Fired.");
});
$selectEvent.on("select2:close", function(e) {
    alert("Close Event Fired.");
});
$selectEvent.on("select2:select", function(e) {
    alert("Select Event Fired.");
});
$selectEvent.on("select2:unselect", function(e) {
    alert("Unselect Event Fired.");
});

$selectEvent.on("change", function(e) {
    alert("Change Event Fired.");
});

//***********************************//
// Programmatic access
//***********************************//
var $select = $(".js-programmatic").select2();
var $selectMulti = $(".js-programmatic-multiple").select2();
$selectMulti.select2({
    placeholder: "Programmatic Events"
});
$(".js-programmatic-set-val").on("click", function() { $select.val("NM").trigger("change"); });

$(".js-programmatic-open").on("click", function() { $select.select2("open"); });
$(".js-programmatic-close").on("click", function() { $select.select2("close"); });

$(".js-programmatic-init").on("click", function() { $select.select2(); });
$(".js-programmatic-destroy").on("click", function() { $select.select2("destroy"); });

$(".js-programmatic-multi-set-val").on("click", function() { $selectMulti.val(["UT", "NM"]).trigger("change"); });
$(".js-programmatic-multi-clear").on("click", function() { $selectMulti.val(null).trigger("change"); });

//***********************************//
// Loading array data
//***********************************//
var data = [
    { id: 0, text: 'Web Designer' },
    { id: 1, text: 'Mobile App Developer' },
    { id: 2, text: 'Graphics Designer' },
    { id: 3, text: 'Hacker' },
    { id: 4, text: 'Animation Designer' }
];

$("#select2-data-array").select2({
    data: data
});


//***********************************//
// Loading remote data
//***********************************//
$(".select2-data-ajax").select2({
    placeholder: "Loading remote data",
    ajax: {
        url: "http://api.github.com/search/repositories",
        dataType: 'json',
        delay: 250,
        data: function(params) {
            return {
                q: params.term, // search term
                page: params.page
            };
        },
        processResults: function(data, params) {
            // parse the results into the format expected by Select2
            // since we are using custom formatting functions we do not need to
            // alter the remote JSON data, except to indicate that infinite
            // scrolling can be used
            params.page = params.page || 1;

            return {
                results: data.items,
                pagination: {
                    more: (params.page * 30) < data.total_count
                }
            };
        },
        cache: true
    },
    escapeMarkup: function(markup) { return markup; }, // let our custom formatter work
    minimumInputLength: 1,
    templateResult: formatRepo, // omitted for brevity, see the source of this page
    templateSelection: formatRepoSelection // omitted for brevity, see the source of this page
});

function formatRepo(repo) {
    if (repo.loading) return repo.text;

    var markup = "<div class='select2-result-repository clearfix'>" +
        "<div class='select2-result-repository__avatar'><img src='" + repo.owner.avatar_url + "' /></div>" +
        "<div class='select2-result-repository__meta'>" +
        "<div class='select2-result-repository__title'>" + repo.full_name + "</div>";

    if (repo.description) {
        markup += "<div class='select2-result-repository__description'>" + repo.description + "</div>";
    }

    markup += "<div class='select2-result-repository__statistics'>" +
        "<div class='select2-result-repository__forks'><i class='la la-code-fork mr-0'></i> " + repo.forks_count + " Forks</div>" +
        "<div class='select2-result-repository__stargazers'><i class='la la-star-o mr-0'></i> " + repo.stargazers_count + " Stars</div>" +
        "<div class='select2-result-repository__watchers'><i class='la la-eye mr-0'></i> " + repo.watchers_count + " Watchers</div>" +
        "</div>" +
        "</div></div>";

    return markup;
}

function formatRepoSelection(repo) {
    return repo.full_name || repo.text;
}

//***********************************//
// Multiple languages
//***********************************//
$("#select2-language").select2({
    language: "es"
});

//***********************************//
// Theme support
//***********************************//
$("#select2-theme").select2({
    placeholder: "Classic Theme",
    theme: "classic"
});


//***********************************//
//templete with flag icons
//***********************************//
$("#template-with-flag-icons").select2({
    minimumResultsForSearch: Infinity,
    templateResult: iconFormat,
    templateSelection: iconFormat,
    escapeMarkup: function(es) { return es; }
});

function iconFormat(ficon) {
    var originalOption = ficon.element;
    if (!ficon.id) { return ficon.text; }
    var $ficon = "<i class='flag-icon flag-icon-" + $(ficon.element).data('flag') + "'></i>" + ficon.text;
    return $ficon;
}

//***********************************//
// Tagging support
//***********************************//
$("#select2-with-tags").select2({
    tags: true
});

//***********************************//
// Automatic tokenization
//***********************************//
$("#select2-with-tokenizer").select2({
    tags: true,
    tokenSeparators: [',', ' ']
});

//***********************************//
// RTL support
//***********************************//
$("#select2-rtl-multiple").select2({
    placeholder: "RTL Select",
    dir: "rtl"
});

//***********************************//
// Language Files
//***********************************//
$("#select2-transliteration-multiple").select2({
    placeholder: "Type 'aero'",
});

//***********************************//
// Color Options
//***********************************//

//***********************************//
// Background Color
//***********************************//
$('.select2-with-bg').each(function(i, obj) {
    var variation = "",
        textVariation = "",
        textColor = "";
    var color = $(this).data('bgcolor');
    variation = $(this).data('bgcolor-variation');
    textVariation = $(this).data('text-variation');
    textColor = $(this).data('text-color');
    if (textVariation !== "") {
        textVariation = " " + textVariation;
    }
    if (variation !== "") {
        variation = " bg-" + variation;
    }
    var className = "bg-" + color + variation + " " + textColor + textVariation + " border-" + color;

    $(this).select2({
        containerCssClass: className
    });
});

//***********************************//
// Menu Background Color
//***********************************//
$('.select2-with-menu-bg').each(function(i, obj) {
    var variation = "",
        textVariation = "",
        textColor = "";
    var color = $(this).data('bgcolor');
    variation = $(this).data('bgcolor-variation');
    textVariation = $(this).data('text-variation');
    textColor = $(this).data('text-color');
    if (variation !== "") {
        variation = " bg-" + variation;
    }
    if (textVariation !== "") {
        textVariation = " " + textVariation;
    }
    var className = "bg-" + color + variation + " " + textColor + textVariation + " border-" + color;

    $(this).select2({
        dropdownCssClass: className
    });
});

//***********************************//
// Full Background Color
//***********************************//
$('.select2-with-full-bg').each(function(i, obj) {
    var variation = "",
        textVariation = "",
        textColor = "";
    var color = $(this).data('bgcolor');
    variation = $(this).data('bgcolor-variation');
    textVariation = $(this).data('text-variation');
    textColor = $(this).data('text-color');
    if (variation !== "") {
        variation = " bg-" + variation;
    }
    if (textVariation !== "") {
        textVariation = " " + textVariation;
    }
    var className = "bg-" + color + variation + " " + textColor + textVariation + " border-" + color;

    $(this).select2({
        containerCssClass: className,
        dropdownCssClass: className
    });
});

$('select[data-text-color]').each(function(i, obj) {
    var text = $(this).data('text-color'),
        textVariation;
    textVariation = $(this).data('text-variation');
    if (textVariation !== "") {
        textVariation = " " + textVariation;
    }
    $(this).next(".select2").find(".select2-selection__rendered").addClass(text + textVariation);
});

//***********************************//
// Border Color
//***********************************//
$('.select2-with-border').each(function(i, obj) {
    var variation = "",
        textVariation = "",
        textColor = "";
    var color = $(this).data('border-color');
    textVariation = $(this).data('text-variation');
    variation = $(this).data('border-variation');
    textColor = $(this).data('text-color');
    if (textVariation !== "") {
        textVariation = " " + textVariation;
    }
    if (variation !== "") {
        variation = " border-" + variation;
    }

    var className = "border-" + color + " " + variation + " " + textColor + textVariation;

    $(this).select2({
        containerCssClass: className
    });
});