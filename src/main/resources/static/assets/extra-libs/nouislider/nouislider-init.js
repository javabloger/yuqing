$(function() {

    // Handles
    var handlesSlider = document.getElementById('slider-handles');

    noUiSlider.create(handlesSlider, {
        start: [4000, 8000],
        range: {
            'min': [2000],
            'max': [10000]
        }
    });


    // Range
    var rangeSlider = document.getElementById('slider-range');

    noUiSlider.create(rangeSlider, {
        start: [4000],
        range: {
            'min': [2000],
            'max': [10000]
        }
    });


    // Stepping and snapping to values
    var stepSlider = document.getElementById('slider-step');

    noUiSlider.create(stepSlider, {
        start: [4000],
        step: 1000,
        range: {
            'min': [2000],
            'max': [10000]
        }
    });


    // Non-linear sliders
    var nonLinearSlider = document.getElementById('slider-non-linear');

    noUiSlider.create(nonLinearSlider, {
        start: [4000],
        range: {
            'min': [2000],
            '30%': [4000],
            '70%': [8000],
            'max': [10000]
        }
    });


    // Stepping in non-linear sliders
    var nonLinearStepSlider = document.getElementById('slider-non-linear-step');

    noUiSlider.create(nonLinearStepSlider, {
        start: [500, 4000],
        range: {
            'min': [0],
            '10%': [500, 500],
            '50%': [4000, 1000],
            'max': [10000]
        }
    });


    // Snapping between steps
    var snapSlider = document.getElementById('slider-snap');

    noUiSlider.create(snapSlider, {
        start: [0, 500],
        snap: true,
        connect: true,
        range: {
            'min': 0,
            '10%': 50,
            '20%': 100,
            '30%': 150,
            '40%': 500,
            '50%': 800,
            'max': 1000
        }
    });

    // Tap
    tapSlider = document.getElementById('tap');

    noUiSlider.create(tapSlider, {
        start: 40,
        behaviour: 'tap',
        connect: 'upper',
        range: {
            'min': 20,
            'max': 80
        }
    });


    // Drag
    var dragSlider = document.getElementById('drag');

    noUiSlider.create(dragSlider, {
        start: [40, 60],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    });


    // Fixed dragging
    dragFixedSlider = document.getElementById('drag-fixed');

    noUiSlider.create(dragFixedSlider, {
        start: [40, 60],
        behaviour: 'drag-fixed',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    });


    // Snap
    snapSlider = document.getElementById('snap');

    noUiSlider.create(snapSlider, {
        start: 40,
        behaviour: 'snap',
        connect: 'lower',
        range: {
            'min': 20,
            'max': 80
        }
    });


    // Hover
    var hoverSlider = document.getElementById('hover'),
        field = document.getElementById('hover-val');

    noUiSlider.create(hoverSlider, {
        start: 20,
        behaviour: 'hover-snap',
        range: {
            'min': 0,
            'max': 10
        }
    });

    hoverSlider.noUiSlider.on('hover', function(value) {
        field.innerHTML = value;
    });


    // Combined options
    dragTapSlider = document.getElementById('combined');

    noUiSlider.create(dragTapSlider, {
        start: [40, 60],
        behaviour: 'drag-tap',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    });


    var range_all_sliders = {
        'min': [0],
        '10%': [5, 5],
        '50%': [40, 10],
        'max': [100]
    };

    // Range
    var pipsRange = document.getElementById('pips-range'),
        pipsRangeRtl = document.getElementById('pips-range-rtl');

    noUiSlider.create(pipsRange, {
        range: range_all_sliders,
        start: 0,
        pips: {
            mode: 'range',
            density: 3
        }
    });

    noUiSlider.create(pipsRangeRtl, {
        range: range_all_sliders,
        start: 0,
        direction: 'rtl',
        pips: {
            mode: 'range',
            density: 3
        }
    });


    // Steps

    function filter500(value, type) {
        return value % 1000 ? 2 : 1;
    }

    var pipsStepsFilter = document.getElementById('pips-steps-filter');

    noUiSlider.create(pipsStepsFilter, {
        range: range_all_sliders,
        start: 0,
        pips: {
            mode: 'steps',
            density: 3,
            filter: filter500,
            format: wNumb({
                decimals: 2,
                prefix: '$'
            })
        }
    });

    // Default
    var defaultColorSlider = document.getElementById('default-color-slider');

    noUiSlider.create(defaultColorSlider, {
        start: [45, 55],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    });


    // Success
    var successColorSlider = document.getElementById('success-color-slider');

    noUiSlider.create(successColorSlider, {
        start: [40, 60],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    });


    // Info
    var infoColorSlider = document.getElementById('info-color-slider');

    noUiSlider.create(infoColorSlider, {
        start: [35, 65],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    });


    // Warning
    var warningColorSlider = document.getElementById('warning-color-slider');

    noUiSlider.create(warningColorSlider, {
        start: [45, 55],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    });


    // Danger
    var dangerColorSlider = document.getElementById('danger-color-slider');

    noUiSlider.create(dangerColorSlider, {
        start: [40, 60],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    });


    // Custom Color
    var customColorSlider = document.getElementById('custom-color-slider');

    noUiSlider.create(customColorSlider, {
        start: [35, 65],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    });


    // Extra large options
    var xl_options = {
        start: [45, 55],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    };

    var lg_options = {
        start: [40, 60],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    };

    var default_options = {
        start: [35, 65],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    };

    var sm_options = {
        start: [30, 70],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    };

    var xs_options = {
        start: [25, 75],
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 20,
            'max': 80
        }
    };

    // Extra Large
    var extraLargeSlider = document.getElementById('extra-large-slider');
    var circleExtraLargeSlider = document.getElementById('circle-extra-large-slider');
    var squareExtraLargeSlider = document.getElementById('square-extra-large-slider');

    noUiSlider.create(extraLargeSlider, xl_options);
    noUiSlider.create(circleExtraLargeSlider, xl_options);
    noUiSlider.create(squareExtraLargeSlider, xl_options);

    // Large
    var largeSlider = document.getElementById('large-slider');
    var circleLargeSlider = document.getElementById('circle-large-slider');
    var squareLargeSlider = document.getElementById('square-large-slider');

    noUiSlider.create(largeSlider, lg_options);
    noUiSlider.create(circleLargeSlider, lg_options);
    noUiSlider.create(squareLargeSlider, lg_options);

    // Default
    var defaultSlider = document.getElementById('default-slider');
    var circleDefaultSlider = document.getElementById('circle-default-slider');
    var squareDefaultSlider = document.getElementById('square-default-slider');

    noUiSlider.create(defaultSlider, default_options);
    noUiSlider.create(circleDefaultSlider, default_options);
    noUiSlider.create(squareDefaultSlider, default_options);

    // Small
    var smallSlider = document.getElementById('small-slider');
    var circleSmallSlider = document.getElementById('circle-small-slider');
    var squareSmallSlider = document.getElementById('square-small-slider');

    noUiSlider.create(smallSlider, sm_options);
    noUiSlider.create(circleSmallSlider, sm_options);
    noUiSlider.create(squareSmallSlider, sm_options);

    // Extra Small
    var extraSmallSlider = document.getElementById('extra-small-slider');
    var circleExtraSmallSlider = document.getElementById('circle-extra-small-slider');
    var squareExtraSmallSlider = document.getElementById('square-extra-small-slider');

    noUiSlider.create(extraSmallSlider, xs_options);
    noUiSlider.create(circleExtraSmallSlider, xs_options);
    noUiSlider.create(squareExtraSmallSlider, xs_options);


    // Default
    var vertical_slider_1 = document.getElementById('slider-vertical-1');

    noUiSlider.create(vertical_slider_1, {
        start: 20,
        orientation: 'vertical',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var vertical_slider_2 = document.getElementById('slider-vertical-2');

    noUiSlider.create(vertical_slider_2, {
        start: 50,
        orientation: 'vertical',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var vertical_slider_3 = document.getElementById('slider-vertical-3');

    noUiSlider.create(vertical_slider_3, {
        start: 20,
        orientation: 'vertical',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var vertical_slider_4 = document.getElementById('slider-vertical-4');

    noUiSlider.create(vertical_slider_4, {
        start: 50,
        orientation: 'vertical',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var vertical_slider_5 = document.getElementById('slider-vertical-5');

    noUiSlider.create(vertical_slider_5, {
        start: 20,
        orientation: 'vertical',
        range: {
            'min': 0,
            'max': 100
        }
    });


    // Connect to lower
    var connectLowerSlider1 = document.getElementById('connect-lower-1');

    noUiSlider.create(connectLowerSlider1, {
        start: 30,
        orientation: 'vertical',
        connect: 'lower',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var connectLowerSlider2 = document.getElementById('connect-lower-2');

    noUiSlider.create(connectLowerSlider2, {
        start: 40,
        orientation: 'vertical',
        connect: 'lower',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var connectLowerSlider3 = document.getElementById('connect-lower-3');

    noUiSlider.create(connectLowerSlider3, {
        start: 50,
        orientation: 'vertical',
        connect: 'lower',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var connectLowerSlider4 = document.getElementById('connect-lower-4');

    noUiSlider.create(connectLowerSlider4, {
        start: 60,
        orientation: 'vertical',
        connect: 'lower',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var connectLowerSlider5 = document.getElementById('connect-lower-5');

    noUiSlider.create(connectLowerSlider5, {
        start: 70,
        orientation: 'vertical',
        connect: 'lower',
        range: {
            'min': 0,
            'max': 100
        }
    });


    // Connect to upper
    var connectUpperSlider1 = document.getElementById('connect-upper-1');

    noUiSlider.create(connectUpperSlider1, {
        start: 30,
        orientation: 'vertical',
        connect: 'upper',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var connectUpperSlider2 = document.getElementById('connect-upper-2');

    noUiSlider.create(connectUpperSlider2, {
        start: 40,
        orientation: 'vertical',
        connect: 'upper',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var connectUpperSlider3 = document.getElementById('connect-upper-3');

    noUiSlider.create(connectUpperSlider3, {
        start: 50,
        orientation: 'vertical',
        connect: 'upper',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var connectUpperSlider4 = document.getElementById('connect-upper-4');

    noUiSlider.create(connectUpperSlider4, {
        start: 60,
        orientation: 'vertical',
        connect: 'upper',
        range: {
            'min': 0,
            'max': 100
        }
    });

    var connectUpperSlider5 = document.getElementById('connect-upper-5');

    noUiSlider.create(connectUpperSlider5, {
        start: 70,
        orientation: 'vertical',
        connect: 'upper',
        range: {
            'min': 0,
            'max': 100
        }
    });


    // Tooltips
    var tooltipSlider1 = document.getElementById('slider-tooltips-1');

    noUiSlider.create(tooltipSlider1, {
        start: [20, 80],
        orientation: 'vertical',
        tooltips: [false, wNumb({
            decimals: 1
        })],
        range: {
            'min': 0,
            'max': 100
        }
    });

    var tooltipSlider2 = document.getElementById('slider-tooltips-2');

    noUiSlider.create(tooltipSlider2, {
        start: [20, 80],
        orientation: 'vertical',
        tooltips: [false, wNumb({
            decimals: 1
        })],
        range: {
            'min': 0,
            'max': 100
        }
    });

    var tooltipSlider3 = document.getElementById('slider-tooltips-3');

    noUiSlider.create(tooltipSlider3, {
        start: [20, 80],
        orientation: 'vertical',
        tooltips: [false, wNumb({
            decimals: 1
        })],
        range: {
            'min': 0,
            'max': 100
        }
    });

    // Direction top to bottom
    var directionTopBottom1 = document.getElementById('slider-direction-top-bottom-1');

    noUiSlider.create(directionTopBottom1, {
        range: range_all_sliders,
        start: 30,
        connect: 'lower',
        orientation: 'vertical',
        pips: {
            mode: 'range',
            density: 5
        }
    });

    var directionTopBottom2 = document.getElementById('slider-direction-top-bottom-2');

    noUiSlider.create(directionTopBottom2, {
        range: range_all_sliders,
        start: 50,
        connect: 'lower',
        orientation: 'vertical',
        pips: {
            mode: 'range',
            density: 5
        }
    });

    var directionTopBottom3 = document.getElementById('slider-direction-top-bottom-3');

    noUiSlider.create(directionTopBottom3, {
        range: range_all_sliders,
        start: 70,
        connect: 'lower',
        orientation: 'vertical',
        pips: {
            mode: 'range',
            density: 5
        }
    });


    // Direction bottom to top
    var directionBottomTop1 = document.getElementById('slider-direction-bottom-top-1');

    noUiSlider.create(directionBottomTop1, {
        range: range_all_sliders,
        start: 70,
        connect: 'lower',
        orientation: 'vertical',
        direction: 'rtl',
        pips: {
            mode: 'range',
            density: 5
        }
    });

    var directionBottomTop2 = document.getElementById('slider-direction-bottom-top-2');

    noUiSlider.create(directionBottomTop2, {
        range: range_all_sliders,
        start: 50,
        connect: 'lower',
        orientation: 'vertical',
        direction: 'rtl',
        pips: {
            mode: 'range',
            density: 5
        }
    });

    var directionBottomTop3 = document.getElementById('slider-direction-bottom-top-3');

    noUiSlider.create(directionBottomTop3, {
        range: range_all_sliders,
        start: 30,
        connect: 'lower',
        orientation: 'vertical',
        direction: 'rtl',
        pips: {
            mode: 'range',
            density: 5
        }
    });



    // Margin
    var verticalMarginSlider1 = document.getElementById('vertical-margin-1');

    noUiSlider.create(verticalMarginSlider1, {
        start: [40, 60],
        orientation: 'vertical',
        margin: 20,
        range: {
            'min': 0,
            'max': 100
        }
    });

    var verticalMarginSlider2 = document.getElementById('vertical-margin-2');

    noUiSlider.create(verticalMarginSlider2, {
        start: [35, 65],
        orientation: 'vertical',
        margin: 30,
        range: {
            'min': 0,
            'max': 100
        }
    });

    var verticalMarginSlider3 = document.getElementById('vertical-margin-3');

    noUiSlider.create(verticalMarginSlider3, {
        start: [30, 70],
        orientation: 'vertical',
        margin: 30,
        range: {
            'min': 0,
            'max': 100
        }
    });

    var verticalMarginSlider4 = document.getElementById('vertical-margin-4');

    noUiSlider.create(verticalMarginSlider4, {
        start: [25, 75],
        orientation: 'vertical',
        margin: 30,
        range: {
            'min': 0,
            'max': 100
        }
    });

    var verticalMarginSlider5 = document.getElementById('vertical-margin-5');

    noUiSlider.create(verticalMarginSlider5, {
        start: [20, 80],
        orientation: 'vertical',
        margin: 30,
        range: {
            'min': 0,
            'max': 100
        }
    });

    // Limit
    var verticalLimitSlider1 = document.getElementById('vertical-limit-1');

    noUiSlider.create(verticalLimitSlider1, {
        start: [40, 60],
        orientation: 'vertical',
        limit: 40,
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 0,
            'max': 100
        }
    });

    var verticalLimitSlider2 = document.getElementById('vertical-limit-2');

    noUiSlider.create(verticalLimitSlider2, {
        start: [35, 65],
        orientation: 'vertical',
        limit: 40,
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 0,
            'max': 100
        }
    });

    var verticalLimitSlider3 = document.getElementById('vertical-limit-3');

    noUiSlider.create(verticalLimitSlider3, {
        start: [30, 70],
        orientation: 'vertical',
        limit: 50,
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 0,
            'max': 100
        }
    });

    var verticalLimitSlider4 = document.getElementById('vertical-limit-4');

    noUiSlider.create(verticalLimitSlider4, {
        start: [25, 75],
        orientation: 'vertical',
        limit: 50,
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 0,
            'max': 100
        }
    });

    var verticalLimitSlider5 = document.getElementById('vertical-limit-5');

    noUiSlider.create(verticalLimitSlider5, {
        start: [20, 80],
        orientation: 'vertical',
        limit: 70,
        behaviour: 'drag',
        connect: true,
        range: {
            'min': 0,
            'max': 100
        }
    });

    // Step
    var stepsOptions = {
        start: [20, 80],
        orientation: 'vertical',
        step: 10,
        range: {
            'min': 0,
            'max': 100
        }
    };

    var verticalStepSlider1 = document.getElementById('vertical-steps-1');
    noUiSlider.create(verticalStepSlider1, stepsOptions);

    var verticalStepSlider2 = document.getElementById('vertical-steps-2');
    noUiSlider.create(verticalStepSlider2, stepsOptions);

    var verticalStepSlider3 = document.getElementById('vertical-steps-3');
    noUiSlider.create(verticalStepSlider3, stepsOptions);

    var verticalStepSlider4 = document.getElementById('vertical-steps-4');
    noUiSlider.create(verticalStepSlider4, stepsOptions);

    var verticalStepSlider5 = document.getElementById('vertical-steps-5');
    noUiSlider.create(verticalStepSlider5, stepsOptions);


    // Extra large options
    var vertical_xl_options = {
        start: [45, 55],
        behaviour: 'drag',
        connect: true,
        orientation: 'vertical',
        range: {
            'min': 20,
            'max': 80
        }
    };

    var vertical_lg_options = {
        start: [40, 60],
        behaviour: 'drag',
        connect: true,
        orientation: 'vertical',
        range: {
            'min': 20,
            'max': 80
        }
    };

    var vertical_default_options = {
        start: [35, 65],
        behaviour: 'drag',
        connect: true,
        orientation: 'vertical',
        range: {
            'min': 20,
            'max': 80
        }
    };

    var vertical_sm_options = {
        start: [30, 70],
        behaviour: 'drag',
        connect: true,
        orientation: 'vertical',
        range: {
            'min': 20,
            'max': 80
        }
    };

    var vertical_xs_options = {
        start: [25, 75],
        behaviour: 'drag',
        connect: true,
        orientation: 'vertical',
        range: {
            'min': 20,
            'max': 80
        }
    };

    // Extra Large
    var vertExtraLargeSlider = document.getElementById('vert-extra-large-slider');
    var vertCircleExtraLargeSlider = document.getElementById('vert-circle-extra-large-slider');
    var vertSquareExtraLargeSlider = document.getElementById('vert-square-extra-large-slider');

    noUiSlider.create(vertExtraLargeSlider, vertical_xl_options);
    noUiSlider.create(vertCircleExtraLargeSlider, vertical_xl_options);
    noUiSlider.create(vertSquareExtraLargeSlider, vertical_xl_options);

    // Large
    var vertLargeSlider = document.getElementById('vert-large-slider');
    var vertCircleLargeSlider = document.getElementById('vert-circle-large-slider');
    var vertSquareLargeSlider = document.getElementById('vert-square-large-slider');

    noUiSlider.create(vertLargeSlider, vertical_lg_options);
    noUiSlider.create(vertCircleLargeSlider, vertical_lg_options);
    noUiSlider.create(vertSquareLargeSlider, vertical_lg_options);

    // Default
    var vertDefaultSlider = document.getElementById('vert-default-slider');
    var vertCircleDefaultSlider = document.getElementById('vert-circle-default-slider');
    var vertSquareDefaultSlider = document.getElementById('vert-square-default-slider');

    noUiSlider.create(vertDefaultSlider, vertical_default_options);
    noUiSlider.create(vertCircleDefaultSlider, vertical_default_options);
    noUiSlider.create(vertSquareDefaultSlider, vertical_default_options);

    // Small
    var vertSmallSlider = document.getElementById('vert-small-slider');
    var vertCircleSmallSlider = document.getElementById('vert-circle-small-slider');
    var vertSquareSmallSlider = document.getElementById('vert-square-small-slider');

    noUiSlider.create(vertSmallSlider, vertical_sm_options);
    noUiSlider.create(vertCircleSmallSlider, vertical_sm_options);
    noUiSlider.create(vertSquareSmallSlider, vertical_sm_options);

    // Extra Small
    var vertExtraSmallSlider = document.getElementById('vert-extra-small-slider');
    var vertCircleExtraSmallSlider = document.getElementById('vert-circle-extra-small-slider');
    var vertSquareExtraSmallSlider = document.getElementById('vert-square-extra-small-slider');

    noUiSlider.create(vertExtraSmallSlider, vertical_xs_options);
    noUiSlider.create(vertCircleExtraSmallSlider, vertical_xs_options);
    noUiSlider.create(vertSquareExtraSmallSlider, vertical_xs_options);
});