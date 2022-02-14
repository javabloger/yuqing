(function($) {
    "use strict";
    /**
     * Bootstrap分页构造函数
     *
     * @param 分页的容器对象
     * @param 分页的相关配置
     *
     * */
    var BootstrapPaginator = function(element, options) {
            this.init(element, options);
        },
        old = null;

    BootstrapPaginator.prototype = {

        /**
         * 分页初始化, 把element和options作为参数
         *
         * @param element 分页的容器对象
         * @param options 分页的相关配置
         *
         * */
        init: function(element, options) {

            this.$element = $(element);

            var version = (options && options.bootstrapMajorVersion) ? options.bootstrapMajorVersion : $.fn.bootstrapPaginator.defaults.bootstrapMajorVersion,
                id = this.$element.attr("id");
            if(version === 2 && !this.$element.is("div")) {
                throw "在Bootstrap2中，必须使用div作为分页的容器";
            } else if(version > 2 && !this.$element.is("ul")) {
                throw "在Bootstrap3中，必须使用ul作为分页的容器"
            }
            this.currentPage = 1;
            this.lastPage = 1;
            this.setOptions(options);
            this.initialized = true;
        },

        /**
         * 更新分页element的属性
         *
         * @param 分页的相关配置
         * */
        setOptions: function(options) {

            this.options = $.extend({}, (this.options || $.fn.bootstrapPaginator.defaults), options);

            this.totalPages = parseInt(this.options.totalPages, 10); //设置总页数
            this.numberOfPages = parseInt(this.options.numberOfPages, 10); //设置要显示的页数

            //设置总页数后移除当前页设置，否则会导致页面异常
            if(options && typeof(options.currentPage) !== 'undefined') {

                this.setCurrentPage(options.currentPage);
            }

            this.listen();

            //呈现分页
            this.render();

            if(!this.initialized && this.lastPage !== this.currentPage) {
                this.$element.trigger("page-changed", [this.lastPage, this.currentPage]);
            }

        },

        /**
         * 设置时间监听器，如果可用的话，页面点击和切换事件是关联的
         *
         * */
        listen: function() {

            this.$element.off("page-clicked");

            this.$element.off("page-changed"); // 卸载元素事件

            if(typeof(this.options.onPageClicked) === "function") {
                this.$element.bind("page-clicked", this.options.onPageClicked);
            }

            if(typeof(this.options.onPageChanged) === "function") {
                this.$element.on("page-changed", this.options.onPageChanged);
            }

            this.$element.bind("page-clicked", this.onPageClicked);
        },

        /**
         *
         *  销毁paginator元素，首先卸载事件，然后清空里面的内容。
         *
         * */
        destroy: function() {

            this.$element.off("page-clicked");

            this.$element.off("page-changed");

            this.$element.removeData('bootstrapPaginator');

            this.$element.empty();

        },

        /**
         * 显示分页
         *
         * */
        show: function(page) {

            this.setCurrentPage(page);

            this.render();

            if(this.lastPage !== this.currentPage) {
                this.$element.trigger("page-changed", [this.lastPage, this.currentPage]);
            }
        },

        /**
         * 显示下一页
         *
         * */
        showNext: function() {
            var pages = this.getPages();

            if(pages.next) {
                this.show(pages.next);
            }

        },

        /**
         * 显示上一页
         *
         * */
        showPrevious: function() {
            var pages = this.getPages();

            if(pages.prev) {
                this.show(pages.prev);
            }

        },

        /**
         * 显示第一页
         *
         * */
        showFirst: function() {
            var pages = this.getPages();

            if(pages.first) {
                this.show(pages.first);
            }

        },

        /**
         * 显示最后一页
         *
         * */
        showLast: function() {
            var pages = this.getPages();

            if(pages.last) {
                this.show(pages.last);
            }

        },

        /**
         * 页码单击处理程序，单击页码时，将当前页更改为相应的页面，并触发监听器的pageClick事件
         *
         *
         * */
        onPageItemClicked: function(event) {

            var type = event.data.type,
                page = event.data.page;

            this.$element.trigger("page-clicked", [event, type, page]);

        },
        /**
         * 点击跳转事件
         * @param event
         */
        onPageItemJumped: function(event) {
            var type = event.data.type;
            var value = this.$element.find('input').val();
            var page = parseInt(value, 10);
            if(!isNaN(value) && page > 0 && page != this.currentPage && page <= this.totalPages) {
                this.$element.trigger("page-clicked", [event, type, page]);
            }
        },
        onPageClicked: function(event, originalEvent, type, page) {

            //显示相应页面，并在事件返回之前检查新建的页面

            var currentTarget = $(event.currentTarget);

            switch(type) {
                case "first":
                    currentTarget.bootstrapPaginator("showFirst");
                    break;
                case "prev":
                    currentTarget.bootstrapPaginator("showPrevious");
                    break;
                case "next":
                    currentTarget.bootstrapPaginator("showNext");
                    break;
                case "last":
                    currentTarget.bootstrapPaginator("showLast");
                    break;
                case "page":
                    currentTarget.bootstrapPaginator("show", page);
                    break;
                case "jump":
                    currentTarget.bootstrapPaginator("show", page);
                    break;
            }

        },

        /**
         * 根据内部属性和设置呈现分页（生成分页主函数，要修改分页的生成主要就在这里）
         *
         *
         * */
        render: function() {

            //获取容器class并将其添加到容器中
            var containerClass = this.getValueFromOption(this.options.containerClass, this.$element),
                size = this.options.size || "normal",
                alignment = this.options.alignment || "left",
                pages = this.getPages(),
                listContainer = this.options.bootstrapMajorVersion === 2 ? $("<ul></ul>") : this.$element,
                listContainerClass = this.options.bootstrapMajorVersion === 2 ? this.getValueFromOption(this.options.listContainerClass, listContainer) : null,
                first = null,
                prev = null,
                next = null,
                last = null,
                p = null,
                i = 0;

            this.$element.prop("class","");

            this.$element.addClass("pagination float-right");

            switch(size.toLowerCase()) {
                case "large":
                case "small":
                case "mini":
                    this.$element.addClass($.fn.bootstrapPaginator.sizeArray[this.options.bootstrapMajorVersion][size.toLowerCase()]);
                    break;
                default:
                    break;
            }

            if(this.options.bootstrapMajorVersion === 2) {
                switch(alignment.toLowerCase()) {
                    case "center":
                        this.$element.addClass("pagination-centered");
                        break;
                    case "right":
                        this.$element.addClass("pagination-right");
                        break;
                    default:
                        break;
                }
            }

            this.$element.addClass(containerClass);

            //清空最外面的容器，然后把列表添加进容器中。
            this.$element.empty();

            if(this.options.bootstrapMajorVersion === 2) {
                this.$element.append(listContainer);

                listContainer.addClass(listContainerClass);
            }

            //更新页面元素引用
            this.pageRef = [];

            if(pages.first) { //如果是第1页
                first = this.buildPageItem("first", pages.first);
                if(first) {
                    listContainer.append(first);
                }

            }

            if(pages.prev) { //如果是上一页
                prev = this.buildPageItem("prev", pages.prev);
                if(prev) {
                    listContainer.append(prev);
                }

            }

            for(i = 0; i < pages.length; i = i + 1) { //填写数字
                p = this.buildPageItem("page", pages[i]);
                if(p) {
                    listContainer.append(p);
                }
            }

            if(pages.next) { //如果是下一页
                next = this.buildPageItem("next", pages.next);
                if(next) {
                    listContainer.append(next);
                }
            }

            if(pages.last) { //如果是最后一页
                last = this.buildPageItem("last", pages.last);
                if(last) {
                    listContainer.append(last);
                }
            }
            //新加的 跳转某一页
            // var itemCustom = $("<li class='page-item'></li>"); //创建一个容器
            // var itemHtml = "<span><input class='form-control text-center' type='text' style='width: 60px;' value='" + this.currentPage + "/" + this.totalPages + "'/> </span>";
            // itemCustom.append(itemHtml);
            // listContainer.append(itemCustom);
            // var itemlast = $("<li class='page-item'></li>"); //创建一个‘跳转’的span
            // var itemTz = $("<span class='btn btn-info'>跳转</span>").on("click", null, {
            //     type: 'jump'
            // }, $.proxy(this.onPageItemJumped, this)); //绑定点击事件
            // itemlast.append(itemTz);
            // listContainer.append(itemlast);
        },

        /**
         *
         * 根据给定的类型和页码创建page
         *
         * @param page 页码
         * @param type 类型（first, prev, page, next, last）
         *
         * @return 构造的页面元素
         * */
        buildPageItem: function(type, page) {
            var itemContainer = $("<li class='page-item'></li>"), //创建一个容器
                itemContent = $("<a class='page-link'></a>"), //创建一个内容
                text = "",
                title = "",
                itemContainerClass = this.options.itemContainerClass(type, page, this.currentPage),
                itemContentClass = this.getValueFromOption(this.options.itemContentClass, type, page, this.currentPage),
                tooltipOpts = null;

            switch(type) {
                case "first":
                    itemContainerClass = "";
                    if(!this.getValueFromOption(this.options.shouldShowPage, type, page, this.currentPage)) {
                        return;
                    }
                    text = this.options.itemTexts(type, page, this.currentPage);
                    title = this.options.tooltipTitles(type, page, this.currentPage);
                    break;
                case "last":
                    itemContainerClass = "";
                    if(!this.getValueFromOption(this.options.shouldShowPage, type, page, this.currentPage)) {
                        return;
                    }
                    text = this.options.itemTexts(type, page, this.currentPage);
                    title = this.options.tooltipTitles(type, page, this.currentPage);
                    break;
                case "prev":
                    itemContainerClass = "";
                    if(!this.getValueFromOption(this.options.shouldShowPage, type, page, this.currentPage)) {
                        return;
                    }
                    text = this.options.itemTexts(type, page, this.currentPage);
                    title = this.options.tooltipTitles(type, page, this.currentPage);
                    break;
                case "next":
                    itemContainerClass = "";
                    if(!this.getValueFromOption(this.options.shouldShowPage, type, page, this.currentPage)) {
                        return;
                    }
                    text = this.options.itemTexts(type, page, this.currentPage);
                    title = this.options.tooltipTitles(type, page, this.currentPage);
                    break;
                case "page":
                    if(!this.getValueFromOption(this.options.shouldShowPage, type, page, this.currentPage)) {
                        return;
                    }
                    text = this.options.itemTexts(type, page, this.currentPage);
                    title = this.options.tooltipTitles(type, page, this.currentPage);
                    break;
            }

            itemContainer.addClass(itemContainerClass).append(itemContent);

            itemContent.addClass(itemContentClass).html(text).on("click", null, {
                type: type,
                page: page
            }, $.proxy(this.onPageItemClicked, this));

            if(this.options.pageUrl) {
                itemContent.attr("href", this.getValueFromOption(this.options.pageUrl, type, page, this.currentPage));
            }

            if(this.options.useBootstrapTooltip) {
                tooltipOpts = $.extend({}, this.options.bootstrapTooltipOptions, {
                    title: title
                });
                itemContent.tooltip(tooltipOpts);
            } else {
                itemContent.attr("title", title);
            }
            return itemContainer;
        },

        setCurrentPage: function(page) {
            if(page > this.totalPages || page < 1) { // 如果当前页码超出范围，则抛出异常

                // console.log("页码超出范围");

            }
            this.lastPage = this.currentPage;
            this.currentPage = parseInt(page, 10);
        },

        /**
         * 获取表示页面对象当前状态的数组。
         *
         * @return 输出具有first, prev, next, last和中间页码的对象.
         * */
        getPages: function() {
            var totalPages = this.totalPages, //通过总记录获取或计算总页数
                pageStart = (this.currentPage % this.numberOfPages === 0) ? (parseInt(this.currentPage / this.numberOfPages, 10) - 1) * this.numberOfPages + 1 : parseInt(this.currentPage / this.numberOfPages, 10) * this.numberOfPages + 1, //calculates the start page.
                output = [],
                i = 0,
                counter = 0;

            pageStart = pageStart < 1 ? 1 : pageStart; //检查页面的开始范围，看它是否小于1。

            for(i = pageStart, counter = 0; counter < this.numberOfPages && i <= totalPages; i = i + 1, counter = counter + 1) { //填写页面
                output.push(i);
            }

            output.first = 1; //在当前页面离开第一页时添加first

            if(this.currentPage > 1) { // 在当前页面离开第一页时添加prev
                output.prev = this.currentPage - 1;
            } else {
                output.prev = 1;
            }

            if(this.currentPage < totalPages) { // 当前页不是最后一页时添加next
                output.next = this.currentPage + 1;
            } else {
                output.next = totalPages;
            }
            output.last = totalPages; // 在当前页面没有达到最后一页时添加last
            output.current = this.currentPage; //标记当前页面
            output.total = totalPages;
            output.numberOfPages = this.options.numberOfPages;
            return output;
        },

        /**
         * 从选项中获取值，这是为了处理其中value是函数的返回值的情况。
         *
         * @return 混合值取决于参数的类型，如果给定参数是函数，则返回评估结果。 否则参数本身将返回。
         * */
        getValueFromOption: function(value) {
            var output = null,
                args = Array.prototype.slice.call(arguments, 1);
            if(typeof value === 'function') {
                output = value.apply(this, args);
            } else {
                output = value;
            }
            return output;
        }
    };
    old = $.fn.bootstrapPaginator;
    $.fn.bootstrapPaginator = function(option) {
        var args = arguments,
            result = null;
        $(this).each(function(index, item) {
            var $this = $(item),
                data = $this.data('bootstrapPaginator'),
                options = (typeof option !== 'object') ? null : option;

            if(!data) {
                data = new BootstrapPaginator(this, options);

                $this = $(data.$element);

                $this.data('bootstrapPaginator', data);

                return;
            }

            if(typeof option === 'string') {

                if(data[option]) {
                    result = data[option].apply(data, Array.prototype.slice.call(args, 1));
                } else {
                    throw "Method " + option + " does not exist";
                }

            } else {
                result = data.setOptions(option);
            }
        });
        return result;
    };

    $.fn.bootstrapPaginator.sizeArray = {
        "2": {
            "large": "pagination-large",
            "small": "pagination-small",
            "mini": "pagination-mini"
        },
        "3": {
            "large": "pagination-lg",
            "small": "pagination-sm",
            "mini": ""
        }
    };

    $.fn.bootstrapPaginator.defaults = {
        containerClass: "",
        size: "normal",
        alignment: "left",
        bootstrapMajorVersion: 2,
        listContainerClass: "",
        itemContainerClass: function(type, page, current) {
            return(page === current) ? "active" : "";
        },
        itemContentClass: function(type, page, current) {
            return "";
        },
        currentPage: 1,
        numberOfPages: 5,
        totalPages: 1,
        pageUrl: function(type, page, current) {
            return null;
        },
        onPageClicked: null,
        onPageChanged: null,
        useBootstrapTooltip: false,
        shouldShowPage: function(type, page, current) {
            var result = true;
            switch(type) {
                case "first":
                    result = (current !== 1);
                    break;
                case "prev":
                    result = (current !== 1);
                    break;
                case "next":
                    result = (current !== this.totalPages);
                    break;
                case "last":
                    result = (current !== this.totalPages);
                    break;
                case "page":
                    result = true;
                    break;
            }
            return result;
        },
        itemTexts: function(type, page, current) {
            switch(type) {
                case "first":
                    return "首页";
                case "prev":
                    return "上一页";
                case "next":
                    return "下一页";
                case "last":
                    return "末页";
                case "page":
                    return page;
            }
        },
        tooltipTitles: function(type, page, current) {
            switch(type) {
                case "first":
                    return "首页";
                case "prev":
                    return "上一页";
                case "next":
                    return "下一页";
                case "last":
                    return "末页";
                case "page":
                    return(page === current) ? "当前是第" + page + "页" : "第" + page + "页";
            }
        },
        bootstrapTooltipOptions: {
            animation: true,
            html: true,
            placement: 'top',
            selector: false,
            title: "",
            container: false
        }
    };
    $.fn.bootstrapPaginator.Constructor = BootstrapPaginator;
}(window.jQuery));

