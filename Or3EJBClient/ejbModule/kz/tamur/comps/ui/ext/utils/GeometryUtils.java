package kz.tamur.comps.ui.ext.utils;

import java.awt.*;

/**
 * 
 * @author Sergey Lebedev
 *
 */
public class GeometryUtils {
    /**
     * Rectangle containing specified points
     */

    public static Rectangle getContainingRect(Point... points) {
        if (points != null && points.length > 0) {
            Rectangle rect = new Rectangle(points[0], new Dimension(0, 0));
            int i = 1;
            while (i < points.length) {
                Point p = points[i];
                if (p.x < rect.x) {
                    int diff = rect.x - p.x;
                    rect.x = p.x;
                    rect.width += diff;
                } else if (rect.x + rect.width < p.x) {
                    rect.width = p.x - rect.x;
                }
                if (p.y < rect.y) {
                    int diff = rect.y - p.y;
                    rect.y = p.y;
                    rect.height += diff;
                } else if (rect.y + rect.height < p.y) {
                    rect.height = p.y - rect.y;
                }
                i++;
            }
            if (rect.width == 0) {
                rect.width = 1;
            }
            if (rect.height == 0) {
                rect.height = 1;
            }
            return rect;
        } else {
            return null;
        }
    }

    /**
     * Rectangle containing specified ones retrieval
     */

    public static Rectangle getContainingRect(Rectangle... rects) {
        if (rects != null && rects.length > 0) {
            Rectangle rect = rects[0];
            int i = 1;
            while (i < rects.length) {
                rect = getContainingRect(rect, rects[i]);
                i++;
            }
            return rect;
        } else {
            return null;
        }
    }

    /**
     * Rectangle containing two specified ones retrieval
     */

    public static Rectangle getContainingRect(Rectangle r1, Rectangle r2) {
        if (r1 == null && r2 != null) {
            return r2;
        } else if (r2 == null && r1 != null) {
            return r1;
        } else if (r1 == null && r2 == null) {
            return new Rectangle(0, 0, 0, 0);
        }

        int minX = Math.min(r1.x, r2.x);
        int minY = Math.min(r1.y, r2.y);
        int maxX = Math.max(r1.x + r1.width, r2.x + r2.width);
        int maxY = Math.max(r1.y + r1.height, r2.y + r2.height);
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Checks if width or height is negative and flips them if needed
     */

    public static Rectangle verifyRect(Rectangle rect) {
        if (rect.width >= 0 && rect.height >= 0) {
            return rect;
        } else {
            int x = rect.x;
            int width = Math.abs(rect.width);
            if (rect.width < 0) {
                x = x - width;
            }
            int y = rect.y;
            int height = Math.abs(rect.height);
            if (rect.height < 0) {
                y = y - height;
            }
            return new Rectangle(x, y, width, height);
        }
    }

    /**
     * Returns middle point of specified rectangle
     */

    public static Point middle(Rectangle rectangle) {
        return new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
    }

    /**
     * Returns middle point between two specified points
     */

    public static Point middle(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }
}
