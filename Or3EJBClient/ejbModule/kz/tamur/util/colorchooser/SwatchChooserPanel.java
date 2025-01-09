package kz.tamur.util.colorchooser;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

import kz.tamur.rt.MainFrame;


class SwatchChooserPanel extends AbstractColorChooserPanel {

    SwatchPanel swatchPanel;
    MouseListener mainSwatchListener;
    MouseListener recentSwatchListener;

    public Color selColor;
    protected boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private OrColorChooser orColorChooser;
    
    public SwatchChooserPanel(OrColorChooser orColorChooser) {
        super();
        this.orColorChooser = orColorChooser;
    }

    public String getDisplayName() {
        return UIManager.getString("ColorChooser.swatchesNameText");
    }

    public Icon getSmallDisplayIcon() {
        return null;
    }

    public Icon getLargeDisplayIcon() {
        return null;
    }

    public void installChooserPanel(JColorChooser enclosingChooser) {
        super.installChooserPanel(enclosingChooser);
    }

    protected void buildChooser() {

        JPanel superHolder = new JPanel(new BorderLayout());

        swatchPanel = new MainSwatchPanel();
        swatchPanel.getAccessibleContext().setAccessibleName(getDisplayName());
        mainSwatchListener = new MainSwatchListener();
        swatchPanel.addMouseListener(mainSwatchListener);
        superHolder.add(swatchPanel, BorderLayout.CENTER);
        add(superHolder);
        setOpaque(isOpaque);
    }

    public void uninstallChooserPanel(JColorChooser enclosingChooser) {
        super.uninstallChooserPanel(enclosingChooser);
        swatchPanel.removeMouseListener(mainSwatchListener);
        swatchPanel = null;
        mainSwatchListener = null;
        recentSwatchListener = null;
        removeAll();  // strip out all the sub-components
    }

    public void updateChooser() {

    }


    class MainSwatchListener extends MouseAdapter implements Serializable {
        public void mousePressed(MouseEvent e) {
            Color color = swatchPanel.getColorForLocation(e.getX(), e.getY());
            selColor = color;
            orColorChooser.setCurrentColor(selColor);
        }
    }

}


class SwatchPanel extends JPanel {

    protected Color[] colors;
    protected Dimension swatchSize;
    protected Dimension numSwatches;
    protected Dimension gap;

    public SwatchPanel() {
        initValues();
        initColors();
        setToolTipText(""); // register for events
        setOpaque(true);
        setBackground(Color.white);
        setRequestFocusEnabled(false);
    }

    protected void initValues() {

    }

    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        for (int row = 0; row < numSwatches.height; row++) {
            for (int column = 0; column < numSwatches.width; column++) {

                g.setColor(getColorForCell(column, row));
                int x = column * (swatchSize.width + gap.width);
                int y = row * (swatchSize.height + gap.height);
                g.fillRect(x, y, swatchSize.width, swatchSize.height);
                g.setColor(Color.black);
                g.drawLine(x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
                g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.width - 1);
            }
        }
    }

    public Dimension getPreferredSize() {
        int x = numSwatches.width * (swatchSize.width + gap.width) - 1;
        int y = numSwatches.height * (swatchSize.height + gap.height) - 1;
        return new Dimension(x, y);
    }

    protected void initColors() {


    }

    public String getToolTipText(MouseEvent e) {
        Color color = getColorForLocation(e.getX(), e.getY());
        StringBuilder sb = new StringBuilder();
        return sb.append(color.getRed()).append(", ").append(color.getGreen()).append(", ").append(color.getBlue()).toString();
    }

    public Color getColorForLocation(int x, int y) {
        int column = x / (swatchSize.width + gap.width);
        int row = y / (swatchSize.height + gap.height);
        return getColorForCell(column, row);
    }

    private Color getColorForCell(int column, int row) {
        return colors[(row * numSwatches.width) + column];
    }


}


