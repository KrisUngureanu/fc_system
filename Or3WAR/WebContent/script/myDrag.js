function myDrag() {
  var obj = null;
  var left = null;
  var right = null;
//  this.obj = null;
	  this.init = function(o, leftPane, rightPane, orientation) {
		if (o == null || leftPane == null || rightPane == null) {
			return;
		}
		o.onmousedown = this.start;
		o.orientation = orientation;
		o.root = o;
		obj = o;

		left = leftPane;
		right = rightPane;

		if (o.orientation == 0) {
			var div1 = leftPane.getElementsByTagName("div")[0];
			o.lp = leftPane.clientWidth - div1.offsetWidth;

		} else
			o.lp = leftPane.offsetHeight - leftPane.clientHeight;
		if (o.orientation == 0) {
			var div1 = rightPane.getElementsByTagName("div")[0];
			o.rp = rightPane.clientWidth - div1.offsetWidth;
		} else
			o.rp = rightPane.offsetHeight - rightPane.clientHeight;

		o.root.onDragStart = new Function();
		o.root.onDragEnd = new Function();
		o.root.onDrag = new Function();
	};
  
  this.start = function(e)
  {
    var o = obj = this;
    e = fixE(e);

    var y = parseInt(o.offsetTop);
    var x = parseInt(o.offsetLeft);
    o.root.onDragStart(x, y);
    o.lastMouseX  = e.clientX;
    o.lastMouseY  = e.clientY;

    document.onmousemove  = drag;
    document.onmouseup    = end;

    return false;
  };
  var drag = function(e)
  {
    e = fixE(e);
    var o = obj;

    var ey  = e.clientY;
    var ex  = e.clientX;
  
    var y = parseInt(o.offsetTop);
    var x = parseInt(o.offsetLeft);
    
    var leftW = (o.orientation == 0)
                ? parseInt(left.clientWidth) - o.lp
                : parseInt(left.clientHeight) + o.lp;
    var rightW = (o.orientation == 0)
                ? parseInt(right.clientWidth) - o.rp
                : parseInt(right.clientHeight) + o.rp;

    var nx, ny;

    nx = x + (ex - o.lastMouseX);
    ny = y + (ey - o.lastMouseY);

    var lw = (o.orientation == 0) 
              ? Math.max(leftW + (ex - o.lastMouseX), 0)
              : Math.max(leftW + (ey - o.lastMouseY), 0);
    var rw = (o.orientation == 0) 
              ? Math.max(rightW - (ex - o.lastMouseX), 0)
              : Math.max(rightW - (ey - o.lastMouseY), 0);

    if (o.orientation == 0) {
      left.style.width = lw + "px";
      right.style.width = rw + "px";
    } else {
      left.style.height = lw + "px";
      right.style.height = rw + "px";
    }

    o.lastMouseX  = ex;
    o.lastMouseY  = ey;

    obj.root.onDrag(nx, ny);
    return false;
  };
  var end = function()
  {
    document.onmousemove = null;
    document.onmouseup   = null;
    obj.root.onDragEnd(  parseInt(obj.root.style["width"]), 
                  parseInt(obj.root.style["height"]));
    obj = null;
  };
  
  var fixE = function(e)
  {
    if (typeof e == 'undefined') e = window.event;
    if (typeof e.layerX == 'undefined') e.layerX = e.offsetX;
    if (typeof e.layerY == 'undefined') e.layerY = e.offsetY;
    return e;
  };
}
