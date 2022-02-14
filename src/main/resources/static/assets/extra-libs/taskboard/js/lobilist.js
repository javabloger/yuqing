/**
 * @name LobiList - Responsive jQuery todo list plugin
 * LobiList is todo list jquery plugin. Support multiple list with different styles, communication to backend, drag & drop of todos
 *
 * @author Zura Sekhniashvili <zurasekhniashvili@gmail.com>
 * @version 1.0.0
 * @licence MIT
 */
$(function() {

    var LIST_COUNTER = 0;
    /**
     * List class
     *
     * @class
     * @param {Object} $lobiList - jQuery element
     * @param {Object} options - Options for <code>List</code> 'class'
     * @constructor
     */
    var List = function($lobiList, options) {
        this.$lobiList = $lobiList;
        this.$options = options;
        this.$globalOptions = $lobiList.$options;
        this.$items = {};

        this._init();
    };

    List.prototype = {
        $lobiList: null,
        $el: null,
        $elWrapper: null,
        $options: {},
        $items: {},
        $globalOptions: {},
        $ul: null,
        $header: null,
        $title: null,
        $form: null,
        $footer: null,
        $body: null,

        eventsSuppressed: false,

        /**
         *
         * @private
         */
        _init: function() {
            var me = this;
            me.suppressEvents();
            if (!me.$options.id) {
                me.$options.id = 'lobilist-list-' + (LIST_COUNTER++);
            }
            var $wrapper = $('<div class="lobilist-wrapper"></div>');
            var $div = $('<div id="' + me.$options.id + '" class="lobilist"></div>').appendTo($wrapper);

            if (me.$options.defaultStyle) {
                $div.addClass(me.$options.defaultStyle);
            }
            me.$el = $div;
            me.$elWrapper = $wrapper;
            me.$header = me._createHeader();
            me.$title = me._createTitle();
            me.$body = me._createBody();
            me.$ul = me._createList();
            if (me.$options.items) {
                me._createItems(me.$options.items);
            }
            me.$form = me._createForm();
            me.$body.append(me.$ul, me.$form);
            me.$footer = me._createFooter();
            if (me.$globalOptions.sortable) {
                me._enableSorting();
            }
            me.resumeEvents();
        },

        /**
         * Add item. If <code>action.insert</code> url is provided request is sent to the server.
         * Server response example: <code>{"success": Boolean}</code>.
         * If <code>response.success</code> is true item is added.
         * Otherwise <code>errorCallback</code> callback is called if it was provided.
         *
         * @method addItem
         * @param {Object} item - The item <code>Object</code>
         * @param {Function} errorCallback - The callback which is called when server returned response but
         * <code>response.success=false</code>
         * @returns {List}
         */
        addItem: function(item, errorCallback) {
            var me = this;
            if (me._triggerEvent('beforeItemAdd', [me, item]) === false) {
                return me;
            }

            item = me._processItemData(item);
            if (me.$globalOptions.actions.insert) {
                $.ajax(me.$globalOptions.actions.insert, {
                        data: item,
                        method: 'POST'
                    })
                    //res is JSON object of format {"success": Boolean, "id": Number, "msg": String}
                    .done(function(res) {
                        if (res.success) {
                            item.id = res.id;
                            me._addItemToList(item);
                        } else {
                            if (errorCallback && typeof errorCallback === 'function') {
                                errorCallback(res)
                            }
                        }
                    });
            } else {
                item.id = me.$lobiList.getNextId();
                me._addItemToList(item);
            }
            return me;
        },

        /**
         * Update item. If <code>action.update</code> url is provided request is sent to the server.
         * Server response example: <code>{"success": Boolean}</code>.
         * If <code>response.success</code> is true item is updated.
         * Otherwise <code>errorCallback</code> callback is called if it was provided.
         *
         * @method updateItem
         * @param {Object} item  - The item <code>Object</code> to update
         * @param {Function} errorCallback - The callback which is called when server returned response but
         * <code>response.success=false</code>
         * @returns {List}
         */
        updateItem: function(item, errorCallback) {
            var me = this;
            if (me._triggerEvent('beforeItemUpdate', [me, item]) === false) {
                return me
            }
            if (me.$globalOptions.actions.update) {
                $.ajax(me.$globalOptions.actions.update, {
                        data: item,
                        method: 'POST'
                    })
                    //res is JSON object of format {"id": Number, "success": Boolean, "msg": String}
                    .done(function(res) {
                        if (res.success) {
                            me._updateItemInList(item);
                        } else {
                            if (errorCallback && typeof errorCallback === 'function') {
                                errorCallback(res)
                            }
                        }
                    });
            } else {
                me._updateItemInList(item);
            }
            return me;
        },

        /**
         * Delete item from the list. If <code>action.delete</code> url is provided request is sent to the server.
         * Server response example: <code>{"success": Boolean}</code>
         * If <code>response.success=true</code> item is deleted from the list and <code>afterItemDelete</code> event
         * if triggered. Otherwise <code>errorCallback</code> callback is called if it was provided.
         *
         * @method deleteItem
         * @param {Object} item - The item <code>Object</code> to delete
         * @param {Function} errorCallback - The callback which is called when server returned response but
         * <code>response.success=false</code>
         * @returns {List}
         */
        deleteItem: function(item, errorCallback) {
            var me = this;
            if (me._triggerEvent('beforeItemDelete', [me, item]) === false) {
                return me
            }
            if (me.$globalOptions.actions.delete) {
                return me._sendAjax(me.$globalOptions.actions.delete, {
                        data: item,
                        method: 'POST'
                    })
                    //res is JSON object of format
                    .done(function(res) {
                        if (res.success) {
                            me._removeItemFromList(item);
                        } else {
                            if (errorCallback && typeof errorCallback === 'function') {
                                errorCallback(res)
                            }
                        }
                    });
            } else {
                me._removeItemFromList(item);
            }
            return me;
        },

        /**
         * If item does not have id, it is considered as new and is added to the list.
         * If it has id it is updated. If update and insert actions are provided corresponding request is sent to the server
         *
         * @method saveOrUpdateItem
         * @param {Object} item  - The item <code>Object</code>
         * @param {Function} errorCallback - The callback which is called when server returned response but
         * <code>response.success=false</code>
         * @returns {List}
         */
        saveOrUpdateItem: function(item, errorCallback) {
            var me = this;
            if (item.id) {
                me.updateItem(item, errorCallback);
            } else {
                me.addItem(item, errorCallback);
            }
            return me;
        },

        /**
         * Start title editing
         *
         * @method startTitleEditing
         * @returns {List}
         */
        startTitleEditing: function() {
            var me = this;
            var input = me._createInput();
            me.$title.attr('data-old-title', me.$title.html());
            input.val(me.$title.html());
            input.insertAfter(me.$title);
            me.$title.addClass('hide');
            me.$header.addClass('title-editing');
            input[0].focus();
            input[0].select();
            return me;
        },

        /**
         * Finish title editing
         *
         * @method finishTitleEditing
         * @returns {List}
         */
        finishTitleEditing: function() {
            var me = this;
            var $input = me.$header.find('input');
            var oldTitle = me.$title.attr('data-old-title');
            me.$title.html($input.val()).removeClass('hide').removeAttr('data-old-title');
            $input.remove();
            me.$header.removeClass('title-editing');
            console.log(oldTitle, $input.val());
            me._triggerEvent('titleChange', [me, oldTitle, $input.val()]);
            return me;
        },

        /**
         * Cancel title editing
         *
         * @method cancelTitleEditing
         * @returns {List}
         */
        cancelTitleEditing: function() {
            var me = this;
            var $input = me.$header.find('input');
            if ($input.length === 0) {
                return me;
            }
            me.$title.html(me.$title.attr('data-old-title')).removeClass('hide');
            $input.remove();
            me.$header.removeClass('title-editing');
            return me;
        },

        /**
         * Remove list
         *
         * @method remove
         * @returns {List} - Just removed <code>List</code> instance
         */
        remove: function() {
            var me = this;
            me.$lobiList.$lists.splice(me.$el.index(), 1);
            me.$elWrapper.remove();

            return me;
        },

        /**
         * Start editing of item
         *
         * @method editItem
         * @param {String} id - The id of the item to start updating
         * @returns {List}
         */
        editItem: function(id) {
            var me = this;
            var $item = me.$lobiList.$el.find('li[data-id=' + id + ']');
            var $form = $item.closest('.lobilist').find('.lobilist-add-todo-form');
            var $footer = $item.closest('.lobilist').find('.lobilist-footer');

            $form.removeClass('hide');
            $footer.addClass('hide');
            $form[0].id.value = $item.attr('data-id');
            $form[0].title.value = $item.find('.lobilist-item-title').html();
            $form[0].description.value = $item.find('.lobilist-item-description').html() || '';
            $form[0].dueDate.value = $item.find('.lobilist-item-duedate').html() || '';
            return me;
        },

        /**
         * Suppress events. None of the events will be triggered until you call <code>resumeEvents</code>
         * @returns {List}
         */
        suppressEvents: function() {
            this.eventsSuppressed = true;
            return this;
        },

        /**
         * Resume all events.
         * @returns {List}
         */
        resumeEvents: function() {
            this.eventsSuppressed = false;
            return this;
        },

        _processItemData: function(item) {
            var me = this;
            return $.extend({}, me.$globalOptions.itemOptions, item);
        },

        _createHeader: function() {
            var me = this;
            var $header = $('<div>', {
                'class': 'lobilist-header'
            });
            var $actions = $('<div>', {
                'class': 'lobilist-actions'
            }).appendTo($header);
            if (me.$options.controls && me.$options.controls.length > 0) {
                if (me.$options.controls.indexOf('styleChange') > -1) {
                    $actions.append(me._createDropdownForStyleChange());
                }

                if (me.$options.controls.indexOf('edit') > -1) {
                    $actions.append(me._createEditTitleButton());
                    $actions.append(me._createFinishTitleEditing());
                    $actions.append(me._createCancelTitleEditing());
                }
                if (me.$options.controls.indexOf('add') > -1) {
                    $actions.append(me._createAddNewButton());
                }
                if (me.$options.controls.indexOf('remove') > -1) {
                    $actions.append(me._createCloseButton());
                }
            }
            me.$el.append($header);
            return $header;
        },

        _createTitle: function() {
            var me = this;
            var $title = $('<div>', {
                'class': 'lobilist-title',
                html: me.$options.title
            }).appendTo(me.$header);
            if (me.$options.controls && me.$options.controls.indexOf('edit') > -1) {
                $title.on('dblclick', function() {
                    me.startTitleEditing();
                });
            }
            return $title;
        },

        _createBody: function() {
            var me = this;
            return $('<div>', {
                'class': 'lobilist-body'
            }).appendTo(me.$el);

        },

        _createForm: function() {
            var me = this;
            var $form = $('<form>', {
                'class': 'lobilist-add-todo-form hide'
            });
            $('<input type="hidden" name="id">').appendTo($form);
            $('<div class="form-group">').append(
                $('<input>', {
                    'type': 'text',
                    name: 'title',
                    'class': 'form-control',
                    placeholder: 'TODO title'
                })
            ).appendTo($form);
            $('<div class="form-group">').append(
                $('<textarea>', {
                    rows: '2',
                    name: 'description',
                    'class': 'form-control',
                    'placeholder': 'TODO description'
                })
            ).appendTo($form);
            $('<div class="form-group">').append(
                $('<input>', {
                    'type': 'text',
                    name: 'dueDate',
                    'class': 'datepicker form-control',
                    placeholder: 'Due Date'
                })
            ).appendTo($form);
            var $ft = $('<div class="lobilist-form-footer">');
            $('<button>', {
                'class': 'btn btn-primary btn-sm btn-add-todo',
                html: 'Add/Update'
            }).appendTo($ft);
            $('<button>', {
                type: 'button',
                'class': 'btn btn-danger btn-sm btn-discard-todo',
                html: 'Cancel'
            }).click(function() {
                $form.addClass('hide');
                me.$footer.removeClass('hide');
            }).appendTo($ft);
            $ft.appendTo($form);

            me._formHandler($form);

            me.$el.append($form);
            return $form;
        },

        _formHandler: function($form) {
            var me = this;
            $form.on('submit', function(ev) {
                ev.preventDefault();
                me._submitForm();
            });
        },

        _submitForm: function() {
            var me = this;
            if (!me.$form[0].title.value) {
                me._showFormError('title', 'Title can not be empty');
                return;
            }
            me.saveOrUpdateItem({
                id: me.$form[0].id.value,
                title: me.$form[0].title.value,
                description: me.$form[0].description.value,
                dueDate: me.$form[0].dueDate.value
            });
            me.$form.addClass('hide');
            me.$footer.removeClass('hide');
        },

        _createFooter: function() {
            var me = this;
            var $footer = $('<div>', {
                'class': 'lobilist-footer'
            });
            $('<button>', {
                type: 'button',
                'class': 'btn-link btn-show-form',
                'html': 'Add new'
            }).click(function() {
                me._resetForm();
                me.$form.removeClass('hide');
                $footer.addClass('hide');
            }).appendTo($footer);
            me.$el.append($footer);
            return $footer;
        },

        _createList: function() {
            var me = this;
            var $list = $('<ul>', {
                'class': 'lobilist-items'
            });
            me.$el.append($list);
            return $list;
        },

        _createItems: function(items) {
            var me = this;
            for (var i = 0; i < items.length; i++) {
                me._addItem(items[i]);
            }
        },

        /**
         * This method is called when plugin is initialized
         * and initial items are added to the list
         *
         * @type Object
         */
        _addItem: function(item) {
            var me = this;
            if (!item.id) {
                item.id = me.$lobiList.getNextId();
            }
            if (me._triggerEvent('beforeItemAdd', [me, item]) !== false) {
                item = me._processItemData(item);
                me._addItemToList(item);
            }
        },

        _createCheckbox: function() {
            var me = this;
            var $item = $('<input>', {
                'type': 'checkbox'
            });

            $item.change(function() {
                me._onCheckboxChange(this);
            });
            return $('<label>', {
                'class': 'checkbox-inline lobilist-check'
            }).append($item);
        },

        _onCheckboxChange: function(checkbox) {
            var me = this;
            var $this = $(checkbox);
            if ($this.prop('checked')) {
                me._triggerEvent('afterMarkAsDone', [me, $this])
            } else {
                me._triggerEvent('afterMarkAsUndone', [me, $this])
            }

            $this.closest('.lobilist-item').toggleClass('item-done');
        },

        _createDropdownForStyleChange: function() {
            var me = this;
            var $dropdown = $('<div>', {
                'class': 'dropdown'
            }).append(
                $('<button>', {
                    'type': 'button',
                    'data-toggle': 'dropdown',
                    'class': 'btn btn-xs',
                    'html': '<i class="ti-view-grid"></i>'
                })
            );
            var $menu = $('<div>', {
                'class': 'dropdown-menu dropdown-menu-right'
            }).appendTo($dropdown);

            for (var i = 0; i < me.$globalOptions.listStyles.length; i++) {
                var st = me.$globalOptions.listStyles[i];
                $('<div class="' + st + '"></div>')
                    .on('mousedown', function(ev) {
                        ev.stopPropagation()
                    })
                    .click(function() {
                        var classes = me.$el[0].className.split(' ');
                        var oldClass = null;
                        for (var i = 0; i < classes.length; i++) {
                            if (me.$globalOptions.listStyles.indexOf(classes[i]) > -1) {
                                oldClass = classes[i];
                            }
                        }
                        me.$el.removeClass(me.$globalOptions.listStyles.join(" "))
                            .addClass(this.className);

                        me._triggerEvent('styleChange', [me, oldClass, this.className]);

                    })
                    .appendTo($menu);
            }
            return $dropdown;
        },

        _createEditTitleButton: function() {
            var me = this;
            var $btn = $('<button>', {
                'class': 'btn btn-xs',
                html: '<i class="ti-pencil"></i>'
            });
            $btn.click(function() {
                me.startTitleEditing();
            });

            return $btn;
        },

        _createAddNewButton: function() {
            var me = this;
            var $btn = $('<button>', {
                'class': 'btn btn-xs',
                html: '<i class="ti-plus"></i>'
            });
            $btn.click(function() {
                var list = me.$lobiList.addList();
                list.startTitleEditing();
            });
            return $btn;
        },

        _createCloseButton: function() {
            var me = this;
            var $btn = $('<button>', {
                'class': 'btn btn-xs',
                html: '<i class="ti-trash"></i>'
            });
            $btn.click(me._onRemoveListClick);
            return $btn;
        },

        _onRemoveListClick: function() {
            var me = this;
            me._triggerEvent('beforeListRemove', [me]);
            me.remove();
            me._triggerEvent('afterListRemove', [me]);
            return me;
        },

        _createFinishTitleEditing: function() {
            var me = this;
            var $btn = $('<button>', {
                'class': 'btn btn-xs btn-finish-title-editing',
                html: '<i class="ti-check-box"></i>'
            });
            $btn.click(function() {
                me.finishTitleEditing();
            });
            return $btn;
        },

        _createCancelTitleEditing: function() {
            var me = this;
            var $btn = $('<button>', {
                'class': 'btn btn-xs btn-cancel-title-editing',
                html: '<i class="ti-close"></i>'
            });
            $btn.click(function() {
                me.cancelTitleEditing();
            });
            return $btn;
        },

        _createInput: function() {
            var me = this;
            var input = $('<input type="text" class="form-control">');
            input.on('keyup', function(ev) {
                if (ev.which === 13) {
                    me.finishTitleEditing();
                }
            });
            return input;
        },

        _showFormError: function(field, error) {
            var $fGroup = this.$form.find('[name="' + field + '"]').closest('.form-group')
                .addClass('has-error');
            $fGroup.find('.help-block').remove();
            $fGroup.append(
                $('<span class="help-block">' + error + '</span>')
            );
        },

        _resetForm: function() {
            var me = this;
            me.$form[0].reset();
            me.$form[0].id.value = "";
            me.$form.find('.form-group').removeClass('has-error').find('.help-block').remove();
        },

        _enableSorting: function() {
            var me = this;
            me.$el.find('.lobilist-items').sortable({
                connectWith: '.lobilist .lobilist-items',
                items: '.lobilist-item',
                handle: '.drag-handler',
                cursor: 'move',
                placeholder: 'lobilist-item-placeholder',
                forcePlaceholderSize: true,
                opacity: 0.9,
                revert: 70,
                update: function(event, ui) {
                    me._triggerEvent('afterItemReorder', [me, ui.item]);
                }
            });
        },

        _addItemToList: function(item) {
            var me = this;
            var $li = $('<li>', {
                'data-id': item.id,
                'class': 'lobilist-item'
            });
            $li.append($('<div>', {
                'class': 'lobilist-item-title',
                'html': item.title
            }));
            if (item.description) {
                $li.append($('<div>', {
                    'class': 'lobilist-item-description',
                    html: item.description
                }));
            }
            if (item.dueDate) {
                $li.append($('<div>', {
                    'class': 'lobilist-item-duedate',
                    html: item.dueDate
                }));
            }
            $li = me._addItemControls($li);
            if (item.done) {
                $li.find('input[type=checkbox]').prop('checked', true);
                $li.addClass('item-done');
            }
            $li.data('lobiListItem', item);
            me.$ul.append($li);
            me.$items[item.id] = item;
            me._triggerEvent('afterItemAdd', [me, item]);

            return $li;
        },

        _addItemControls: function($li) {
            var me = this;
            if (me.$options.useCheckboxes) {
                $li.append(me._createCheckbox());
            }
            var $itemControlsDiv = $('<div>', {
                'class': 'todo-actions'
            }).appendTo($li);

            if (me.$options.enableTodoEdit) {
                $itemControlsDiv.append($('<div>', {
                    'class': 'edit-todo todo-action',
                    html: '<i class="ti-pencil"></i>'
                }).click(function() {
                    me.editItem($(this).closest('li').data('id'));
                }));
            }

            if (me.$options.enableTodoRemove) {
                $itemControlsDiv.append($('<div>', {
                    'class': 'delete-todo todo-action',
                    html: '<i class="ti-close"></i>'
                }).click(function() {
                    me._onDeleteItemClick($(this).closest('li').data('lobiListItem'));
                }));
            }

            $li.append($('<div>', {
                'class': 'drag-handler'
            }));
            return $li;
        },

        _onDeleteItemClick: function(item) {
            this.deleteItem(item);
        },

        _updateItemInList: function(item) {
            var me = this;
            var $li = me.$lobiList.$el.find('li[data-id="' + item.id + '"]');
            $li.find('input[type=checkbox]').prop('checked', item.done);
            $li.find('.lobilist-item-title').html(item.title);
            $li.find('.lobilist-item-description').remove();
            $li.find('.lobilist-item-duedate').remove();

            if (item.description) {
                $li.append('<div class="lobilist-item-description">' + item.description + '</div>');
            }
            if (item.dueDate) {
                $li.append('<div class="lobilist-item-duedate">' + item.dueDate + '</div>');
            }
            $li.data('lobiListItem', item);
            $.extend(me.$items[item.id], item);
            me._triggerEvent('afterItemUpdate', [me, item]);
        },

        _triggerEvent: function(type, data) {
            var me = this;
            if (me.eventsSuppressed) {
                return;
            }
            if (me.$options[type] && typeof me.$options[type] === 'function') {
                return me.$options[type].apply(me, data);
            } else {
                return me.$el.trigger(type, data);
            }
        },

        _removeItemFromList: function(item) {
            var me = this;
            me.$lobiList.$el.find('li[data-id=' + item.id + ']').remove();
            me._triggerEvent('afterItemDelete', [me, item]);
        },

        _sendAjax: function(url, params) {
            var me = this;
            return $.ajax(url, me._beforeAjaxSent(params))
        },

        _beforeAjaxSent: function(params) {
            var me = this;
            var eventParams = me._triggerEvent('beforeAjaxSent', [me, params]);
            return $.extend({}, params, eventParams || {});
        }
    };
    //||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

    /**
     * LobiList class
     *
     * @param {Object} $el - jQuery element
     * @param {Object} options - Options for <code>LobiList</code> 'class'
     * @constructor
     */
    var LobiList = function($el, options) {
        this.$el = $el;
        this.init(options);
    };

    LobiList.prototype = {
        $el: null,
        $lists: [],
        $options: {},
        _nextId: 1,

        eventsSuppressed: false,

        init: function(options) {
            var me = this;
            me.suppressEvents();

            me.$options = this._processInput(options);
            me.$el.addClass('lobilists');
            if (me.$options.onSingleLine) {
                me.$el.addClass('single-line');
            }

            me._createLists();
            me._handleSortable();
            me._triggerEvent('init', [me]);
            me.resumeEvents();
        },

        /**
         *
         * @param options
         * @returns {*}
         * @private
         */
        _processInput: function(options) {
            options = $.extend({}, $.fn.lobiList.DEFAULT_OPTIONS, options);
            if (options.actions.load) {
                $.ajax(options.actions.load, {
                    async: false
                }).done(function(res) {
                    options.lists = res.lists;
                });
            }
            return options;
        },

        /**
         * @private
         */
        _createLists: function() {
            var me = this;
            for (var i = 0; i < me.$options.lists.length; i++) {
                me.addList(me.$options.lists[i]);
            }
            return me;
        },

        /**
         * @private
         * @returns {LobiList}
         */
        _handleSortable: function() {
            var me = this;
            if (me.$options.sortable) {
                me.$el.sortable({
                    items: '.lobilist-wrapper',
                    handle: '.lobilist-header',
                    cursor: 'move',
                    placeholder: 'lobilist-placeholder',
                    forcePlaceholderSize: true,
                    opacity: 0.9,
                    revert: 70,
                    update: function(event, ui) {
                        me._triggerEvent('afterListReorder', [me, ui.item.find('.lobilist').data('lobiList')]);
                    }
                });
            } else {
                me.$el.addClass('no-sortable');
            }
            return me;
        },

        /**
         * Add new list
         *
         * @public
         * @method addList
         * @param {List|Object} list - The <code>List</code> instance or <code>Object</code>
         * @returns {List} Just added <code>List</code> instance
         */
        addList: function(list) {
            var me = this;
            if (!(list instanceof List)) {
                list = new List(me, me._processListOptions(list));
            }
            if (me._triggerEvent('beforeListAdd', [me, list]) !== false) {
                me.$lists.push(list);
                me.$el.append(list.$elWrapper);
                list.$el.data('lobiList', list);
                me._triggerEvent('afterListAdd', [me, list]);
            }
            return list;
        },

        /**
         * Destroy the <code>LobiList</code>.
         *
         * @public
         * @method destroy
         * @returns {LobiList}
         */
        destroy: function() {
            var me = this;
            if (me._triggerEvent('beforeDestroy', [me]) !== false) {
                for (var i = 0; i < me.$lists.length; i++) {
                    me.$lists[i].remove();
                }
                if (me.$options.sortable) {
                    me.$el.sortable("destroy");
                }
                me.$el.removeClass('lobilists');
                if (me.$options.onSingleLine) {
                    me.$el.removeClass('single-line');
                }
                me.$el.removeData('lobiList');
                me._triggerEvent('afterDestroy', [me]);
            }

            return me;
        },

        /**
         * Get next id which will be assigned to new item
         *
         * @public
         * @method getNextId
         * @returns {Number}
         */
        getNextId: function() {
            return this._nextId++;
        },

        /**
         *
         * @param listOptions
         * @returns {*}
         * @private
         */
        _processListOptions: function(listOptions) {
            var me = this;
            listOptions = $.extend({}, me.$options.listsOptions, listOptions);

            for (var i in me.$options) {
                if (me.$options.hasOwnProperty(i) && listOptions[i] === undefined) {
                    listOptions[i] = me.$options[i];
                }
            }
            return listOptions;
        },

        suppressEvents: function() {
            this.eventsSuppressed = true;
            return this;
        },

        resumeEvents: function() {
            this.eventsSuppressed = false;
            return this;
        },

        /**
         * @param type
         * @param data
         * @returns {*}
         * @private
         */
        _triggerEvent: function(type, data) {
            var me = this;
            if (me.eventsSuppressed) {
                return;
            }
            if (me.$options[type] && typeof me.$options[type] === 'function') {
                return me.$options[type].apply(me, data);
            } else {
                return me.$el.trigger(type, data);
            }
        }
    };

    $.fn.lobiList = function(option) {
        var args = arguments;
        var ret;
        return this.each(function() {
            var $this = $(this);
            var data = $this.data('lobiList');
            var options = typeof option === 'object' && option;

            if (!data) {
                $this.data('lobiList', (data = new LobiList($this, options)));
            }
            if (typeof option === 'string') {
                args = Array.prototype.slice.call(args, 1);
                ret = data[option].apply(data, args);
            }
        });
    };
    $.fn.lobiList.DEFAULT_OPTIONS = {
        // Available style for lists
        'listStyles': ['lobilist-default', 'lobilist-danger', 'lobilist-success', 'lobilist-warning', 'lobilist-info', 'lobilist-primary'],
        // Default options for all lists
        listsOptions: {
            id: false,
            title: '',
            items: []
        },
        // Default options for all todo items
        itemOptions: {
            id: false,
            title: '',
            description: '',
            dueDate: '',
            done: false
        },

        lists: [],
        // Urls to communicate to backend for todos
        actions: {
            'load': '',
            'update': '',
            'insert': '',
            'delete': ''
        },
        // Whether to show checkboxes or not
        useCheckboxes: true,
        // Show/hide todo remove button
        enableTodoRemove: true,
        // Show/hide todo edit button
        enableTodoEdit: true,
        // Whether to make lists and todos sortable
        sortable: true,
        // Default action buttons for all lists
        controls: ['edit', 'add', 'remove', 'styleChange'],
        //List style
        defaultStyle: 'lobilist-default',
        // Whether to show lists on single line or not
        onSingleLine: true,

        // Events
        /**
         * @event init
         * Fires when <code>LobiList</code> is initialized
         * @param {LobiList} The <code>LobiList</code> instance
         */
        init: null,

        /**
         * @event beforeDestroy
         * Fires before <code>Lobilist</code> is destroyed. Return false if you do not want <code>LobiList</code> to be destroyed.
         * @param {LobiList} The <code>LobiList</code> to be destroyed
         */
        beforeDestroy: null,

        /**
         * @event afterDestroy
         * Fires after <code>Lobilist</code> is destroyed.
         * @param {LobiList} The destroyed <code>LobiList</code> instance
         */
        afterDestroy: null,

        /**
         * @event beforeListAdd
         * Fires before <code>List</code> is added to <code>LobiList</code>. Return false to prevent adding list.
         * @param {LobiList} The <code>LobiList</code> instance
         * @param {List} The <code>List</code> instance to be added
         */
        beforeListAdd: null,

        /**
         * @event afterListAdd
         * Fires after <code>List</code> is added to <code>LobiList</code>.
         * @param {LobiList} The <code>LobiList</code> instance
         * @param {List} Just added <code>List</code> instance
         */
        afterListAdd: null,

        /**
         * @event beforeListRemove
         * Fires before <code>List</code> is removed. Returning false will prevent removing the list
         * @param {List} The <code>List</code> to be removed
         */
        beforeListRemove: null,

        /**
         * @event afterListRemove
         * Fires after <code>List</code> is removed
         * @param {List} The remove <code>List</code>
         */
        afterListRemove: null,

        /**
         * @event beforeItemAdd
         * Fires before item is added in <code>List</code>. Return false if you want to prevent removing item
         * @param {List} The <code>List</code> in which the item is going to be added
         * @param {Object} The item object
         */
        beforeItemAdd: null,

        /**
         * @event afterItemAdd
         * Fires after item is added in <code>List</code>
         * @param {List} The <code>List</code> in which the item is added
         * @param {Object} The item object
         */
        afterItemAdd: null,

        /**
         * @event beforeItemUpdate
         * Fires before item is updated. Returning false will prevent updating item
         * @param {List} The <code>List</code> instance
         * @param {Object} The item object which is going to be updated
         */
        beforeItemUpdate: null,

        /**
         * @event afterItemUpdate
         * Fires after item is updated
         * @param {List} The <code>List</code> object
         * @param {Object} The updated item object
         */
        afterItemUpdate: null,

        /**
         * @event beforeItemDelete
         * Fires before item is deleted. Returning false will prevent deleting the item
         * @param {List} The <code>List</code> instance
         * @param {Object} The item object to be deleted
         */
        beforeItemDelete: null,

        /**
         * @event afterItemDelete
         * Fires after item is deleted.
         * @param {List} The <code>List</code> object
         * @param {Object} The deleted item object
         */
        afterItemDelete: null,

        /**
         * @event afterListReorder
         * Fires after <code>List</code> position is changed among its siblings
         * @param {LobiList} The <code>LobiList</code> instance
         * @param {List} The <code>List</code> instance which changed its position
         */
        afterListReorder: null,

        /**
         * @event afterItemReorder
         * Fires after item position is changed (it is reordered) in list
         * @param {List} The <code>List</code> instance
         * @param {Object} The jQuery object of item
         */
        afterItemReorder: null,

        /**
         * @event afterMarkAsDone
         * Fires after item is marked as done.
         * @param {List} The <code>List</code> instance
         * @param {Object} The jQuery checkbox object
         */
        afterMarkAsDone: null,

        /**
         * @event afterMarkAsUndone
         * Fires after item is marked as undone
         * @param {List} The <code>List</code> instance
         * @param {Object} The jQuery checkbox object
         */
        afterMarkAsUndone: null,

        /**
         * @event beforeAjaxSent
         * Fires before ajax call is sent to backend. This event is very useful is you want to add default parameters
         * or headers to every request. Such as CSRF token parameter or Access Token header
         * @param {List} The <code>List</code> instance
         * @param {Object} The jquery ajax parameters object. You can add additional headers or parameters
         * to this object and must return the object which will be used for sending request
         */
        beforeAjaxSent: null,

        /**
         * @event styleChange
         * Fires when list style is changed
         * @param {List} The <code>List</code> instance
         * @param {String} Old class name
         * @param {String} New class name
         */
        styleChange: null,

        /**
         * @event titleChange
         * Fires when list title is change
         * @param {List} The <code>List</code> instance
         * @param {String} Old title name
         * @param {String} New title name
         */
        titleChange: null
    };
});