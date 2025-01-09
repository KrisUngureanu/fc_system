package kz.tamur.comps.models;

import java.awt.Color;
import java.awt.Font;

public class PropertyUtil {

    private static Font defaultFont = new Font("Dialog", Font.PLAIN, 11);
    private static Font defaultComponentFont = new Font("Dialog", Font.PLAIN, 12);
    private static Font defaultTableTitleFont = new Font("Tahoma", Font.BOLD, 12);
    private static Color lightSysColor = new Color(216, 221, 231);

    public static Font getDefaultComponentFont() {
        return defaultComponentFont;
    }
    
    public static Font getDefaultFont() {
        return defaultFont;
    }
    
    public static Font getDefaultTableTitleFont() {
        return defaultTableTitleFont;
    }

    public static Color getLightSysColor() {
        return lightSysColor;
    }
}
