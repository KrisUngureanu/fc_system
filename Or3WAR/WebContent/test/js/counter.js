var LuxUtilities = (function () {
    return {
        hasClass: function (b, a) {
            return b.className.match(new RegExp("(\\s|^)" + a + "(\\s|$)"))
        },
        addClass: function (b, a) {
            if (!this.hasClass(b, a)) {
                b.className += " " + a
            }
        },
        removeClass: function (c, a) {
            if (this.hasClass(c, a)) {
                var b = new RegExp("(\\s|^)" + a + "(\\s|$)");
                c.className = c.className.replace(b, " ")
            }
        },
        getElementsByClassName: function (b, a, c) {
            if (document.getElementsByClassName) {
                getElementsByClassName = function (j, m, h) {
                    h = h || document;
                    var d = h.getElementsByClassName(j),
                        l = (m) ? new RegExp("\\b" + m + "\\b", "i") : null,
                        e = [],
                        g;
                    for (var f = 0, k = d.length; f < k; f += 1) {
                        g = d[f];
                        if (!l || l.test(g.nodeName)) {
                            e.push(g)
                        }
                    }
                    return e
                }
            } else {
                if (document.evaluate) {
                    getElementsByClassName = function (o, r, n) {
                        r = r || "*";
                        n = n || document;
                        var g = o.split(" "),
                            p = "",
                            l = "http://www.w3.org/1999/xhtml",
                            q = (document.documentElement.namespaceURI === l) ? l : null,
                            h = [],
                            d, f;
                        for (var i = 0, k = g.length; i < k; i += 1) {
                            p += "[contains(concat(' ', @class, ' '), ' " + g[i] + " ')]"
                        }
                        try {
                            d = document.evaluate(".//" + r + p, n, q, 0, null)
                        } catch (m) {
                            d = document.evaluate(".//" + r + p, n, null, 0, null)
                        }
                        while ((f = d.iterateNext())) {
                            h.push(f)
                        }
                        return h
                    }
                } else {
                    getElementsByClassName = function (r, u, q) {
                        u = u || "*";
                        q = q || document;
                        var h = r.split(" "),
                            t = [],
                            d = (u === "*" && q.all) ? q.all : q.getElementsByTagName(u),
                            p, j = [],
                            o;
                        for (var i = 0, e = h.length; i < e; i += 1) {
                            t.push(new RegExp("(^|\\s)" + h[i] + "(\\s|$)"))
                        }
                        for (var g = 0, s = d.length; g < s; g += 1) {
                            p = d[g];
                            o = false;
                            for (var f = 0, n = t.length; f < n; f += 1) {
                                o = t[f].test(p.className);
                                if (!o) {
                                    break;
                                }
                            }
                            if (o) {
                                j.push(p);
                            }
                        }
                        return j;
                    }
                }
            }
            return getElementsByClassName(b, a, c)
        },
        getNextElement: function (a) {
            var b = a;
            do {
                b = b.nextSibling;
            } while (b && b.nodeType != 1);
            return b;
        },
        getPrevElement: function (a) {
            var b = a;
            do {
                b = b.previousSibling
            } while (b && b.nodeType != 1);
            return b;
        }
    }
})();

function luxCountdown(a) {
	this._stop = false;
    this._settings = {};
    this._start = null;
    this._end = null;
    this._timerId = null;
    this._daysTxt = {
        one: " day ",
        more: " days "
    };
    this._timeSeparator = ":";
    this._notStartedTxt = " - : - : -";
    this._setup(a);
    this._theTimer = this._build();
    this._init();
}
luxCountdown.prototype.getCountdown = function () {
    return this._theTimer;
};
luxCountdown.prototype._setup = function (a, b) {
    if ( !! a) {
        this._settings = a;
    } else {
        this._settings = {};
    }
};
luxCountdown.prototype._stringOrEmpty = function (a) {
    var b = a;
    if (b == "" || b == null || typeof b === "undefined") {
        b = "";
    }
    return b;
};
luxCountdown.prototype._isNullOrNothing = function (a) {
    var b = false;
    if (a == "" || a == null || typeof a === "undefined") {
        b = true;
    }
    return b;
};
luxCountdown.prototype._build = function () {
    var b = document.createElement("div");
    LuxUtilities.addClass(b, "lux-countdown");
    var f = document.createElement("p");
    LuxUtilities.addClass(f, "lux-countdown-title");
    //f.innerHTML = this._stringOrEmpty(this._settings.title);
    var l = document.createElement("p");
    LuxUtilities.addClass(l, "lux-countdown-text");
    //l.innerHTML = this._stringOrEmpty(this._settings.text);
    var k = document.createElement("p");
    LuxUtilities.addClass(k, "lux-countdown-countdown");
    var h = document.createElement("span");
    k.appendChild(h);
    var c = document.createElement("p");
    LuxUtilities.addClass(c, "lux-countdown-datesWrap");
    var a = document.createElement("span");
    LuxUtilities.addClass(a, "lux-countdown-start");
    a.innerHTML = this._stringOrEmpty(this._settings.startText);
    c.appendChild(a);
    var g = document.createElement("span");
    LuxUtilities.addClass(g, "lux-countdown-perc");
    c.appendChild(g);
    var e = document.createElement("span");
    LuxUtilities.addClass(e, "lux-countdown-end");
    e.innerHTML = this._stringOrEmpty(this._settings.endText);
    c.appendChild(e);
    var j = document.createElement("div");
    LuxUtilities.addClass(j, "lux-countdown-countdownBar");
    var d = document.createElement("span");
    LuxUtilities.addClass(d, "lux-countdown-countdownBar-bar");
    j.appendChild(d);
    //b.appendChild(f);
    //b.appendChild(l);
    b.appendChild(k);
    //b.appendChild(c);
    //b.appendChild(j);
    return b;
};
luxCountdown.prototype._setDateObject = function (a) {
    return new Date(a);
};
luxCountdown.prototype._init = function () {
    this._start = this._setDateObject(this._settings.start);
    this._end = this._setDateObject(this._settings.end);
    if ( !! !this._isNullOrNothing(this._settings.daysOneText)) {
        this._daysTxt.one = this._settings.daysOneText;
    }
    if ( !! !this._isNullOrNothing(this._settings.daysMoreText)) {
        this._daysTxt.more = this._settings.daysMoreText;
    }
    if ( !! !this._isNullOrNothing(this._settings.notStartedTxt)) {
        this._notStartedTxt = this._settings.notStartedTxt;
    }
    /*var a = this._theTimer.childNodes[3].childNodes[1];
    var b = navigator.userAgent.toLowerCase().indexOf("msie") > -1;
    if (b) {
        a.style.visibility = "hidden"
    }
    /*this._theTimer.childNodes[4].onmouseover = function () {
        a.style.opacity = 1;
        var c = navigator.userAgent.toLowerCase().indexOf("msie") > -1;
        if (c) {
            a.style.visibility = "visible"
        }
    };*/
    /*this._theTimer.childNodes[4].onmouseout = function () {
        a.style.opacity = 0;
        var c = navigator.userAgent.toLowerCase().indexOf("msie") > -1;
        if (c) {
            a.style.visibility = "hidden"
        }
    };*/
    this._startTimer();
};
luxCountdown.prototype._getCss = function (c, g) {
    var f = getComputedStyle || currentStyle;
    var e = f(c, g);
    var k = "";
    var a = navigator.userAgent.toLowerCase().indexOf("chrome") > -1;
    if (a) {
        var j = e.cssText.split(";");
        var h = {};
        for (i = 0; i < j.length - 1; i++) {
            var d = j[i].split(":");
            var b = d[0].replace(" ", "");
            h[b] = (d[1] != "undefined" ? d[1] : " ").replace(" ", "");
        }
        k = h[g];
    } else {
        k = e[g];
    }
    return k;
};
luxCountdown.prototype._updateTimer = function () {
    var c = new Date();
    var e = parseInt(((c.getTime() - this._start.getTime()) / ((this._end.getTime() - this._start.getTime()) / 100)), 10);
    if (e > 100) {
        e = 100;
    } else {
        if (e < 0) {
            e = 0;
        }
    }
    //var d = this._theTimer.childNodes[4];
    //var a = d.childNodes[0];
    //a.style.width = e + "%";
    //var b = this._theTimer.childNodes[3].childNodes[1];
    //b.innerHTML = e + "%";
    if (e == 100) {
    	console.log(this._timerId+ " is timer id");
    	clearTimeout(this._timerId);
        this._settings.onend();
    }
};
luxCountdown.prototype._startTimer = function () {
    var h = new Date();
    if ((h.getTime() >= this._start.getTime())) {
        var j = (this._end.getTime() - h.getTime());
        var f = "00";
        var g = "00";
        var b = "00";
        var d = "00";
        if (j > 0) {
            f = Math.floor(j / (60 * 60 * 1000 * 24) * 1);
            g = Math.floor((j % (60 * 60 * 1000 * 24)) / (60 * 60 * 1000) * 1);
            b = Math.floor(((j % (60 * 60 * 1000 * 24)) % (60 * 60 * 1000)) / (60 * 1000) * 1);
            d = Math.floor((((j % (60 * 60 * 1000 * 24)) % (60 * 60 * 1000)) % (60 * 1000)) / 1000 * 1);
            g = this._addZero(g);
            b = this._addZero(b);
            d = this._addZero(d);
        }
        ddayStr = '<span class="lux-countdown-days">' + f + "</span>";
        dhourStr = '<span class="lux-countdown-hours">' + g + "</span>";
        dminStr = '<span class="lux-countdown-mins">' + b + "</span>";
        dsecStr = '<span class="lux-countdown-dsec">' + d + "</span>";
        var k = (f > 1 ? this._daysTxt.more : this._daysTxt.one);
        var c = ddayStr + k + dhourStr + this._timeSeparator + dminStr + this._timeSeparator + dsecStr;
        if (f <= 0) {
            c = dhourStr + this._timeSeparator + dminStr + this._timeSeparator + dsecStr;
        }
        this._theTimer.childNodes[0].innerHTML = c;
        this._updateTimer();
        if (!this._stop && j > 0) {
            var e = this;
            var a = function () {
                e._startTimer();
            };
            this._timerId = setTimeout(a, 500);
        }
    } else {
        var c = this._notStartedTxt;
        console.log(h.getTime());
        console.log(this._start.getTime());
        //this._theTimer.childNodes[2].childNodes[0].innerHTML = c;
    }
};
luxCountdown.prototype.stop = function () {
	this._stop = true;
};
luxCountdown.prototype._addZero = function (a) {
    if (a < 10) {
        a = "0" + a;
    }
    return a;
};