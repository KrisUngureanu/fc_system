package kz.tamur.comps.ui.ext.graphics.filters;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * A filter which produces motion blur the faster, but lower-quality way.
 *
 * @author Lebedev Sergey
 */

public class MotionBlurOp extends AbstractBufferedImageOp {
    
    /** align x. */
    private float alignX;
    
    /** align y. */
    private float alignY;
    
    /** distance. */
    private float distance;
    
    /** angle. */
    private float angle;
    
    /** rotation. */
    private float rotation;
    
    /** zoom. */
    private float zoom;

    /**
     * Конструктор класса motion blur op.
     */
    public MotionBlurOp() {
        this(0f, 0f, 0f, 0f);
    }

    /**
     * Конструктор класса motion blur op.
     *
     * @param distance the distance
     * @param angle the angle
     * @param rotation the rotation
     * @param zoom the zoom
     */
    public MotionBlurOp(float distance, float angle, float rotation, float zoom) {
        this(distance, angle, rotation, zoom, 0.5f, 0.5f);
    }

    /**
     * Конструктор класса motion blur op.
     *
     * @param distance the distance
     * @param angle the angle
     * @param rotation the rotation
     * @param zoom the zoom
     * @param alignX the align x
     * @param alignY the align y
     */
    public MotionBlurOp(float distance, float angle, float rotation, float zoom, float alignX, float alignY) {
        this.distance = distance;
        this.angle = angle;
        this.rotation = rotation;
        this.zoom = zoom;
        this.alignX = alignX;
        this.alignY = alignY;
    }

    /**
     * Установить angle.
     *
     * @param angle новое значение angle
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * Получить angle.
     *
     * @return angle
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Установить distance.
     *
     * @param distance новое значение distance
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    /**
     * Получить distance.
     *
     * @return distance
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Установить rotation.
     *
     * @param rotation новое значение rotation
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     * Получить rotation.
     *
     * @return rotation
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * Установить zoom.
     *
     * @param zoom новое значение zoom
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    /**
     * Получить zoom.
     *
     * @return zoom
     */
    public float getZoom() {
        return zoom;
    }

    /**
     * Установить align x.
     *
     * @param alignX новое значение align x
     */
    public void setAlignX(float alignX) {
        this.alignX = alignX;
    }

    /**
     * Получить align x.
     *
     * @return align x
     */
    public float getAlignX() {
        return alignX;
    }

    /**
     * Установить align y.
     *
     * @param alignY новое значение align y
     */
    public void setAlignY(float alignY) {
        this.alignY = alignY;
    }

    /**
     * Получить align y.
     *
     * @return align y
     */
    public float getAlignY() {
        return alignY;
    }

    /**
     * Установить align.
     *
     * @param align новое значение align
     */
    public void setAlign(Point2D align) {
        this.alignX = (float) align.getX();
        this.alignY = (float) align.getY();
    }

    /**
     * Получить align.
     *
     * @return align
     */
    public Point2D getAlign() {
        return new Point2D.Float(alignX, alignY);
    }

    /**
     * Log2.
     *
     * @param n the n
     * @return int
     */
    private int log2(int n) {
        int m = 1;
        int log2n = 0;

        while (m < n) {
            m *= 2;
            log2n++;
        }
        return log2n;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }
        BufferedImage tsrc = src;
        float cx = (float) src.getWidth() * alignX;
        float cy = (float) src.getHeight() * alignY;
        float imageRadius = (float) Math.sqrt(cx * cx + cy * cy);
        float translateX = (float) (distance * Math.cos(angle));
        float translateY = (float) (distance * -Math.sin(angle));
        float scale = zoom;
        float rotate = rotation;
        float maxDistance = distance + Math.abs(rotation * imageRadius) + zoom * imageRadius;
        int steps = log2((int) maxDistance);

        translateX /= maxDistance;
        translateY /= maxDistance;
        scale /= maxDistance;
        rotate /= maxDistance;

        if (steps == 0) {
            Graphics2D g = dst.createGraphics();
            g.drawRenderedImage(src, null);
            g.dispose();
            return dst;
        }

        BufferedImage tmp = createCompatibleDestImage(src, null);
        for (int i = 0; i < steps; i++) {
            Graphics2D g = tmp.createGraphics();
            g.drawImage(tsrc, null, null);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

            g.translate(cx + translateX, cy + translateY);
            g.scale(1.0001 + scale, 1.0001 + scale); // The .0001 works round a bug on Windows where drawImage throws an ArrayIndexOutofBoundException
            if (rotation != 0) {
                g.rotate(rotate);
            }
            g.translate(-cx, -cy);

            g.drawImage(dst, null, null);
            g.dispose();
            BufferedImage ti = dst;
            dst = tmp;
            tmp = ti;
            tsrc = dst;

            translateX *= 2;
            translateY *= 2;
            scale *= 2;
            rotate *= 2;
        }
        return dst;
    }
}
