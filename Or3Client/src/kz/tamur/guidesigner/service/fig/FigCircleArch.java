package kz.tamur.guidesigner.service.fig;

import org.tigris.gef.presentation.FigCircle;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ValeT
 * Date: 18.04.2006
 * Time: 17:59:39
 * To change this template use File | Settings | File Templates.
 */
public class FigCircleArch extends FigCircle {
    private int startAngle;
    private int archAngle;
    private Color archColor;
    public FigCircleArch(int i, int i1, int i2, int i3) {
        super(i, i1, i2, i3);
    }

    public FigCircleArch(int i, int i1, int i2, int i3, Color color, Color color1) {
        super(i, i1, i2, i3, color, color1);
    }

    public FigCircleArch(int i, int i1, int i2, int i3, boolean b) {
        super(i, i1, i2, i3, b);
    }

    public FigCircleArch(int i, int i1, int i2, int i3, boolean b, Color color, Color color1) {
        super(i, i1, i2, i3, b, color, color1);
    }
    public void setArchAngle(int startAngle,int archAngle,Color archColor){
        this.startAngle=startAngle;
        this.archAngle=archAngle;
        this.archColor=archColor;
    }
    public void paint(Graphics g ){
        super.paint(g);
        if(archColor!=null)
            g.setColor(archColor);
        g.fillArc(_x, _y, _w, _h,startAngle,archAngle);

    }
}
