package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.comps.OrGuiComponent;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: �������������
 * Date: 17.02.2005
 * Time: 18:31:49
 * To change this template use File | Settings | File Templates.
 */
public interface OrImageComponent extends OrGuiComponent {
    int getWidth();

    int getHeight();

    void setHorizontalAlignment(int center);

    void setVerticalAlignment(int center);

    void setIcon(ImageIcon img);

    void setFile(Object val);
}
