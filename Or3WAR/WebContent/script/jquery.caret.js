/**
Query Caret
This is a very simple lightweight plugin to allow you to move the caret (or cursor) position in an <input /> or <textarea> element.

By exposing three jQuery.fn methods you can easily move a a caret to any position you like:
$.fn.caret ( )

Use this method with no parameters to get the current position of the caret within the first element matched.
var position = $('input').caret();
$.fn.caret ( index , [ offset ] )

This methods first parameter is the index of where you want to move the caret to. In order to move to an index, index must be an integer.
Alternatively you can pass a string as an index and it will be used via .indexOf() the element's value to get an index to move to. You could also use a RegExp object.
The second parameter is to be used to move the caret to an offset of the index. When set to true, it will move the cursor after the string if a string was passed.
$('input').caret(10);
// Move to position just before word
$('input').caret('hello');
// Move to position just after word
$('input').caret('hello', true);
// Move to offset from word's beginning
$('input').caret('hello', 6);
$.fn.caretToStart ( )

This is a shortcut for $.fn.caret(0) as a convenience to you.
$('textarea').caretToStart();
$.fn.caretToEnd ( )

This method moves the caret to the end of the content within your element, also for your convenience.
$('input').caretToEnd();
*/

// Set caret position easily in jQuery
// Written by and Copyright of Luke Morton, 2011
// Licensed under MIT
(function ($) {
    // Behind the scenes method deals with browser
    // idiosyncrasies and such
    $.caretTo = function (el, index) {
        if (el.createTextRange) {
            var range = el.createTextRange();
            range.move("character", index);
            range.select();
        } else if (el.selectionStart != null) {
            el.focus();
            el.setSelectionRange(index, index);
        }
    };
    
    // Another behind the scenes that collects the
    // current caret position for an element
    
    // TODO: Get working with Opera
    $.caretPos = function (el) {
        if ("selection" in document) {
            var range = el.createTextRange();
            try {
                range.setEndPoint("EndToStart", document.selection.createRange());
            } catch (e) {
                // Catch IE failure here, return 0 like
                // other browsers
                return 0;
            }
            return range.text.length;
        } else if (el.selectionStart != null) {
            return el.selectionStart;
        }
    };

    // The following methods are queued under fx for more
    // flexibility when combining with $.fn.delay() and
    // jQuery effects.

    // Set caret to a particular index
    $.fn.caret = function (index, offset) {
        if (typeof(index) === "undefined") {
            return $.caretPos(this.get(0));
        }
        
        return this.queue(function (next) {
            if (isNaN(index)) {
                var i = $(this).val().indexOf(index);
                
                if (offset === true) {
                    i += index.length;
                } else if (typeof(offset) !== "undefined") {
                    i += offset;
                }
                
                $.caretTo(this, i);
            } else {
                $.caretTo(this, index);
            }
            
            next();
        });
    };

    // Set caret to beginning of an element
    $.fn.caretToStart = function () {
        return this.caret(0);
    };

    // Set caret to the end of an element
    $.fn.caretToEnd = function () {
        return this.queue(function (next) {
            $.caretTo(this, $(this).val().length);
            next();
        });
    };
}(jQuery));