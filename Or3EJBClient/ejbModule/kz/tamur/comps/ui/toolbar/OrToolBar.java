package kz.tamur.comps.ui.toolbar;

import javax.swing.JToolBar;

public class OrToolBar extends JToolBar {
    private boolean isWindowResize = false;
    private String positionInSplitPane = "";
    private boolean isForceFloat = true;
    

    public OrToolBar() {
        super();
    }

    public OrToolBar(int orientation) {
        super(orientation);
    }

    public OrToolBar(String name) {
        super(name);
    }

    public OrToolBar(String name, int orientation) {
        super(name, orientation);
    }

    public OrToolBar(int orientation, boolean isWindowResize) {
        super(orientation);
        this.isWindowResize = isWindowResize;
    }
    
    public OrToolBar(int orientation, String positionInSplitPane, boolean isWindowResize) {
        super(orientation);
        this.positionInSplitPane = positionInSplitPane;
        this.isWindowResize = isWindowResize;
    }

    /**
     * @return the isWindowResize
     */
    public boolean isWindowResize() {
        return isWindowResize;
    }

    /**
     * @param isWindowResize
     *            the isWindowResize to set
     */
    public void setWindowResize(boolean isWindowResize) {
        this.isWindowResize = isWindowResize;
    }

    /**
     * @return the positionInSplitPane
     */
    public String getPositionInSplitPane() {
        return positionInSplitPane;
    }

    /**
     * @param positionInSplitPane the positionInSplitPane to set
     */
    public void setPositionInSplitPane(String positionInSplitPane) {
        this.positionInSplitPane = positionInSplitPane;
    }

    /**
     * @return the isForceFloat
     */
    public boolean isForceFloat() {
        return isForceFloat;
    }

    /**
     * @param isForceFloat the isForceFloat to set
     */
    public void setForceFloat(boolean isForceFloat) {
        this.isForceFloat = isForceFloat;
    }
    
    public OrToolBarUI getUI() {
        return (OrToolBarUI)super.getUI();
    }
}
