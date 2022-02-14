$(function() {

    $.fn.raty.defaults.path = '../../assets/images/rating/';

    // Default
    $('#default-star-rating').raty();

    // Default Score
    $('#score-rating').raty({
        score: 3
    });

    // Number of stars
    $('#no-of-stars').raty({
        number: 10
    });

    // Maximum number of stars
    $('#max-number-stars').raty({
        numberMax: 5,
        number: 100
    });

    // Read Only
    $('#read-only-stars').raty({
        readOnly: true,
        score: 3
    });

    // No Rated Message
    $('#no-rated-msg').raty({
        half: true,
        readOnly: true
    });

    // Cancel Star
    $('#cancel-star').raty({
        cancel: true
    });

    // Space
    $('#space-star').raty({
        space: false
    });

    // Single
    $('#single-star').raty({
        single: true
    });

    // Half Star
    $('#half-star').raty({
        half: true,
    });

    // Star Half
    $('#star-half').raty({
        half: true,
        starHalf: 'star-half-mono.png'
    });

    // Round Enabled
    $('#round-enabled').raty({
        score: 3.26
    });

    // Round Disabled
    $('#round-disabled').raty({
        halfShow: false,
        score: 3.26
    });

    // Custom Icon
    $('#custom-icon-star').raty({
        starOff: 'heart.png',
        starOn: 'like.png'
    });

    // Icon Range
    $('#icon-range').raty({
        iconRange: [
            { range: 1, on: 'wi wi-day-rain-wind font-16', off: 'wi wi-day-cloudy font-16' },
            { range: 2, on: 'wi wi-day-sleet font-16', off: 'wi wi-day-sleet-storm font-16' },
            { range: 3, on: 'wi wi-day-snow font-16', off: 'wi wi-day-storm-showers font-16' },
            { range: 4, on: 'wi wi-night-alt-snow font-16', off: 'wi wi-night-alt-thunderstorm font-16' },
            { range: 5, on: 'wi wi-night-snow-thunderstorm font-16', off: 'wi wi-night-snow-wind font-16' }
        ],
        starType: 'i'
    });

});