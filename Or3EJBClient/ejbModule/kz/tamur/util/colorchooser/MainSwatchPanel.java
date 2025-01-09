package kz.tamur.util.colorchooser;

import java.awt.*;

/**
 * User: Vital
 * Date: 08.02.2005
 * Time: 19:14:50
 */
public class MainSwatchPanel extends SwatchPanel {


    protected void initValues() {
        swatchSize = new Dimension(15, 15);//UIManager.getDimension("ColorChooser.swatchesSwatchSize");
        numSwatches = new Dimension(31, 10);
        gap = new Dimension(1, 1);
    }

    protected void initColors() {
        int[] rawValues = initRawValues();
        int numColors = rawValues.length / 3;

        colors = new Color[numColors];
        for (int i = 0; i < numColors; i++) {
            colors[i] = new Color(rawValues[(i * 3)], rawValues[(i * 3) + 1], rawValues[(i * 3) + 2]);
        }
    }

    private int[] initRawValues() {

        int[] rawValues = {255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           255, 255, 255,
                           204, 255, 255,
                           204, 204, 255,
                           204, 204, 255,
                           204, 204, 255,
                           204, 204, 255,
                           204, 204, 255,
                           204, 204, 255,
                           204, 204, 255,
                           204, 204, 255,
                           204, 204, 255,
                           255, 204, 255,
                           255, 204, 204,
                           255, 204, 204,
                           255, 204, 204,
                           255, 204, 204,
                           255, 204, 204,
                           255, 204, 204,
                           255, 204, 204,
                           255, 204, 204,
                           255, 204, 204,
                           255, 255, 204,
                           204, 255, 204,
                           204, 255, 204,
                           204, 255, 204,
                           204, 255, 204,
                           204, 255, 204,
                           204, 255, 204,
                           204, 255, 204,
                           204, 255, 204,
                           204, 255, 204,
                           204, 255, 255,
                           153, 255, 255,
                           153, 204, 255,
                           153, 153, 255,
                           153, 153, 255,
                           153, 153, 255,
                           153, 153, 255,
                           153, 153, 255,
                           153, 153, 255,
                           153, 153, 255,
                           204, 153, 255,
                           255, 153, 255,
                           255, 153, 204,
                           255, 153, 153,
                           255, 153, 153,
                           255, 153, 153,
                           255, 153, 153,
                           255, 153, 153,
                           255, 153, 153,
                           255, 153, 153,
                           255, 204, 153,
                           255, 255, 153,
                           204, 255, 153,
                           153, 255, 153,
                           153, 255, 153,
                           153, 255, 153,
                           153, 255, 153,
                           153, 255, 153,
                           153, 255, 153,
                           153, 255, 153,
                           153, 255, 204,
                           153, 255, 255,
                           102, 255, 255,
                           102, 204, 255,
                           102, 153, 255,
                           102, 102, 255,
                           102, 102, 255,
                           102, 102, 255,
                           102, 102, 255,
                           102, 102, 255,
                           153, 102, 255,
                           204, 102, 255,
                           255, 102, 255,
                           255, 102, 204,
                           255, 102, 153,
                           255, 102, 102,
                           255, 102, 102,
                           255, 102, 102,
                           255, 102, 102,
                           255, 102, 102,
                           255, 153, 102,
                           255, 204, 102,
                           255, 255, 102,
                           204, 255, 102,
                           153, 255, 102,
                           102, 255, 102,
                           102, 255, 102,
                           102, 255, 102,
                           102, 255, 102,
                           102, 255, 102,
                           102, 255, 153,
                           102, 255, 204,
                           102, 255, 255,
                           51, 255, 255,
                           51, 204, 255,
                           51, 153, 255,
                           51, 102, 255,
                           51, 51, 255,
                           51, 51, 255,
                           51, 51, 255,
                           102, 51, 255,
                           153, 51, 255,
                           204, 51, 255,
                           255, 51, 255,
                           255, 51, 204,
                           255, 51, 153,
                           255, 51, 102,
                           255, 51, 51,
                           255, 51, 51,
                           255, 51, 51,
                           255, 102, 51,
                           255, 153, 51,
                           255, 204, 51,
                           255, 255, 51,
                           204, 255, 51,
                           153, 244, 51,
                           102, 255, 51,
                           51, 255, 51,
                           51, 255, 51,
                           51, 255, 51,
                           51, 255, 102,
                           51, 255, 153,
                           51, 255, 204,
                           51, 255, 255,
                           0, 255, 255,
                           0, 204, 255,
                           0, 153, 255,
                           0, 102, 255,
                           0, 51, 255,
                           0, 0, 255,
                           51, 0, 255,
                           102, 0, 255,
                           153, 0, 255,
                           204, 0, 255,
                           255, 0, 255,
                           255, 0, 204,
                           255, 0, 153,
                           255, 0, 102,
                           255, 0, 51,
                           255, 0, 0,
                           255, 51, 0,
                           255, 102, 0,
                           255, 153, 0,
                           255, 204, 0,
                           255, 255, 0,
                           204, 255, 0,
                           153, 255, 0,
                           102, 255, 0,
                           51, 255, 0,
                           0, 255, 0,
                           0, 255, 51,
                           0, 255, 102,
                           0, 255, 153,
                           0, 255, 204,
                           0, 255, 255,
                           0, 204, 204,
                           0, 204, 204,
                           0, 153, 204,
                           0, 102, 204,
                           0, 51, 204,
                           0, 0, 204,
                           51, 0, 204,
                           102, 0, 204,
                           153, 0, 204,
                           204, 0, 204,
                           204, 0, 204,
                           204, 0, 204,
                           204, 0, 153,
                           204, 0, 102,
                           204, 0, 51,
                           204, 0, 0,
                           204, 51, 0,
                           204, 102, 0,
                           204, 153, 0,
                           204, 204, 0,
                           204, 204, 0,
                           204, 204, 0,
                           153, 204, 0,
                           102, 204, 0,
                           51, 204, 0,
                           0, 204, 0,
                           0, 204, 51,
                           0, 204, 102,
                           0, 204, 153,
                           0, 204, 204,
                           0, 204, 204,
                           0, 153, 153,
                           0, 153, 153,
                           0, 153, 153,
                           0, 102, 153,
                           0, 51, 153,
                           0, 0, 153,
                           51, 0, 153,
                           102, 0, 153,
                           153, 0, 153,
                           153, 0, 153,
                           153, 0, 153,
                           153, 0, 153,
                           153, 0, 153,
                           153, 0, 102,
                           153, 0, 51,
                           153, 0, 0,
                           153, 51, 0,
                           153, 102, 0,
                           153, 153, 0,
                           153, 153, 0,
                           153, 153, 0,
                           153, 153, 0,
                           153, 153, 0,
                           102, 153, 0,
                           51, 153, 0,
                           0, 153, 0,
                           0, 153, 51,
                           0, 153, 102,
                           0, 153, 153,
                           0, 153, 153,
                           0, 153, 153,
                           0, 102, 102,
                           0, 102, 102,
                           0, 102, 102,
                           0, 102, 102,
                           0, 51, 102,
                           0, 0, 102,
                           51, 0, 102,
                           102, 0, 102,
                           102, 0, 102,
                           102, 0, 102,
                           102, 0, 102,
                           102, 0, 102,
                           102, 0, 102,
                           102, 0, 102,
                           102, 0, 51,
                           102, 0, 0,
                           102, 51, 0,
                           102, 102, 0,
                           102, 102, 0,
                           102, 102, 0,
                           102, 102, 0,
                           102, 102, 0,
                           102, 102, 0,
                           102, 102, 0,
                           51, 102, 0,
                           0, 102, 0,
                           0, 102, 51,
                           0, 102, 102,
                           0, 102, 102,
                           0, 102, 102,
                           0, 102, 102,
                           0, 51, 51,
                           0, 51, 51,
                           0, 51, 51,
                           0, 51, 51,
                           0, 51, 51,
                           0, 0, 51,
                           51, 0, 51,
                           51, 0, 51,
                           51, 0, 51,
                           51, 0, 51,
                           51, 0, 51,
                           51, 0, 51,
                           51, 0, 51,
                           51, 0, 51,
                           51, 0, 51,
                           51, 0, 0,
                           51, 51, 0,
                           51, 51, 0,
                           51, 51, 0,
                           51, 51, 0,
                           51, 51, 0,
                           51, 51, 0,
                           51, 51, 0,
                           51, 51, 0,
                           51, 51, 0,
                           0, 51, 0,
                           0, 51, 51,
                           0, 51, 51,
                           0, 51, 51,
                           0, 51, 51,
                           0, 51, 51};
        return rawValues;
    }
}