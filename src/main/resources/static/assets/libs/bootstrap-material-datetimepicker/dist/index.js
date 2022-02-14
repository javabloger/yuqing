"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var moment = require("moment/moment");
var jquery = require("jquery/dist/jquery");
require("bootstrap-material-design/dist/js/ripples.min");
require("bootstrap-material-design/dist/js/material.min.js");
require("bootstrap-material-datetimepicker/js/bootstrap-material-datetimepicker.js");
require("bootstrap-material-datetimepicker/css/bootstrap-material-datetimepicker.css");
var DatePickerDirective = (function () {
    function DatePickerDirective(elementRef) {
        this.elementRef = elementRef;
        this.dtpFormat = 'L LT';
        this.dtpLocale = null;
        this.dtpTime = true;
        this.dtpDate = true;
        this.dateChange = new core_1.EventEmitter();
        this.el = this.elementRef.nativeElement;
        this.dtpLocale = moment().locale() || this.dtpLocale;
    }
    Object.defineProperty(DatePickerDirective.prototype, "datetimepicker", {
        set: function (s) {
            this.dtpDate = true;
            this.dtpTime = true;
            this.dtpFormat = 'L LT';
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(DatePickerDirective.prototype, "datepicker", {
        set: function (s) {
            this.dtpDate = true;
            this.dtpTime = false;
            this.dtpFormat = 'L';
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(DatePickerDirective.prototype, "timepicker", {
        set: function (s) {
            this.dtpDate = false;
            this.dtpTime = true;
            this.dtpFormat = 'LT';
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(DatePickerDirective.prototype, "date", {
        set: function (d) {
            this.el.value = moment(new Date(d)).format(this.dtpFormat);
        },
        enumerable: true,
        configurable: true
    });
    DatePickerDirective.prototype.ngOnInit = function () {
        var _this = this;
        var $element = jquery(this.elementRef.nativeElement);
        $element.bootstrapMaterialDatePicker({
            date: this.dtpDate,
            time: this.dtpTime,
            format: this.dtpFormat,
            lang: this.dtpFormat,
        });
        $element.on('change', function () {
            _this.dateChange.emit(moment($element.val(), _this.dtpFormat).toDate());
        });
    };
    ;
    return DatePickerDirective;
}());
__decorate([
    core_1.Input(),
    __metadata("design:type", Object)
], DatePickerDirective.prototype, "dtpFormat", void 0);
__decorate([
    core_1.Input(),
    __metadata("design:type", Object)
], DatePickerDirective.prototype, "dtpLocale", void 0);
__decorate([
    core_1.Input(),
    __metadata("design:type", Object)
], DatePickerDirective.prototype, "dtpTime", void 0);
__decorate([
    core_1.Input(),
    __metadata("design:type", Object)
], DatePickerDirective.prototype, "dtpDate", void 0);
__decorate([
    core_1.Input(),
    __metadata("design:type", String),
    __metadata("design:paramtypes", [String])
], DatePickerDirective.prototype, "datetimepicker", null);
__decorate([
    core_1.Input(),
    __metadata("design:type", String),
    __metadata("design:paramtypes", [String])
], DatePickerDirective.prototype, "datepicker", null);
__decorate([
    core_1.Input(),
    __metadata("design:type", String),
    __metadata("design:paramtypes", [String])
], DatePickerDirective.prototype, "timepicker", null);
__decorate([
    core_1.Input(),
    __metadata("design:type", Date),
    __metadata("design:paramtypes", [Date])
], DatePickerDirective.prototype, "date", null);
__decorate([
    core_1.Output(),
    __metadata("design:type", core_1.EventEmitter)
], DatePickerDirective.prototype, "dateChange", void 0);
DatePickerDirective = __decorate([
    core_1.Directive({
        selector: '[datepicker],[datetimepicker],[timepicker]',
    }),
    __metadata("design:paramtypes", [core_1.ElementRef])
], DatePickerDirective);
exports.DatePickerDirective = DatePickerDirective;
