package kz.tamur.guidesigner;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Utils;
import kz.tamur.util.Funcs;

import javax.swing.*;

import com.cifs.or2.util.CursorToolkit;
import static kz.tamur.rt.Utils.getImageIconFull;
import static kz.tamur.rt.Utils.getImageIconJpg;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 20.05.2004
 * Time: 16:12:27
 * To change this template use File | Settings | File Templates.
 */
public class Splash extends JWindow {
    public static final int DESIGNER = 0;
    public static final int RUNTIME = 1;
    public static final int SERVICES = 2;
    JLabel lb = null;
    public Splash(int type) {
        switch (type) {
        case DESIGNER:
        case SERVICES:
            lb = new JLabel(getImageIconJpg("splash"));
            break;
        case RUNTIME:
            String path = Funcs.normalizeInput(System.getProperty("splash"));
            if (path != null && !path.isEmpty()) {
                // задать регулярное выражение с шаблонами возможных расширений картинок
                final Matcher M = Pattern.compile("\\.JPG$|\\.JPEG$|\\.GIF$|\\.PNG$").matcher(path.toUpperCase(Constants.OK));
                lb = (M.find()) ? new JLabel(getImageIconFull(path)) : new JLabel(getImageIconJpg(path));
            }else{
                lb = new JLabel();
            }
            break;
        default:
            lb = new JLabel();
            break;
        }
        getContentPane().add(lb);
        pack();
        CursorToolkit.startWaitCursor(lb);
    }
    
    
    @Override
    public void setVisible(boolean b) {
        if (b) {
            setLocation(Utils.getCenterLocationPoint(getSize()));  
        }
        super.setVisible(b);
    }
}
