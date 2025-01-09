package kz.tamur.util;

import java.awt.*;


/**
 * User: vital
 * Date: 25.01.2005
 * Time: 11:59:38
 */
public interface Or3FrameTemplate {
    public void processClose() throws Exception;
    public void restoreFrame();
    public void maximizeFrame();
    public int getCurrentState();
    public void setCurrentState(int currentState);
    public Point getLastLocation();
}
