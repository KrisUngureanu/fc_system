package kz.tamur.util.colorchooser;

import java.awt.image.*;

abstract class SyntheticImage implements ImageProducer {
    private SyntheticImageGenerator root;
    protected int width = 10, height = 100;
    static final ColorModel cm = ColorModel.getRGBdefault();
    public static final int pixMask = 0xFF;
    private Thread runner;

    protected SyntheticImage() {
    }

    protected SyntheticImage(int w, int h) {
        width = w;
        height = h;
    }

    protected void computeRow(int y, int[] row) {
        int p = 255 - 255 * y / (height - 1);
        p = (pixMask << 24) | (p << 16) | (p << 8) | p;
        for (int i = row.length; --i >= 0;) row[i] = p;
    }

    public synchronized void addConsumer(ImageConsumer ic) {
        for (SyntheticImageGenerator ics = root; ics != null; ics = ics.next)
            if (ics.ic == ic) return;
        root = new SyntheticImageGenerator(ic, root, this);
    }

    public synchronized boolean isConsumer(ImageConsumer ic) {
        for (SyntheticImageGenerator ics = root; ics != null; ics = ics.next)
            if (ics.ic == ic) return true;
        return false;
    }

    public synchronized void removeConsumer(ImageConsumer ic) {
        SyntheticImageGenerator prev = null;
        for (SyntheticImageGenerator ics = root; ics != null; ics = ics.next)
            if (ics.ic == ic) {
                ics.useful = false;
                if (prev != null)
                    prev.next = ics.next;
                else
                    root = ics.next;
                return;
            }
    }

    public synchronized void startProduction(ImageConsumer ic) {
        addConsumer(ic);
        for (SyntheticImageGenerator ics = root; ics != null; ics = ics.next)
            if (ics.useful && !ics.isAlive())
                ics.start();
    }

    protected boolean isStatic() {
        return true;
    }

    public void nextFrame(int param) {
    }//Override if !isStatic

    public void requestTopDownLeftRightResend(ImageConsumer ic) {
    }
}

class SyntheticImageGenerator extends Thread {
    ImageConsumer ic;
    boolean useful;
    SyntheticImageGenerator next;
    SyntheticImage parent;

    SyntheticImageGenerator(ImageConsumer ic, SyntheticImageGenerator next,
                            SyntheticImage parent) {
        this.ic = ic;
        this.next = next;
        this.parent = parent;
        useful = true;
        setDaemon(true);
        //	System.out.println (Thread.getCurrent() + " is making a generator");
    }

    public void run() {
        ImageConsumer ic = this.ic;
        int w = parent.width;
        int h = parent.height;
        int hints = ImageConsumer.SINGLEPASS | ImageConsumer.COMPLETESCANLINES | ImageConsumer.TOPDOWNLEFTRIGHT;
        if (parent.isStatic())
            hints |= ImageConsumer.SINGLEFRAME;
        ic.setHints(hints);
        ic.setDimensions(w, h);
        ic.setProperties(null);
        ic.setColorModel(SyntheticImage.cm);

        if (useful) {
            int[] row = new int[w];
            doPrivileged(new Runnable() {
                public void run() {
                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                }
            });

            do {

                //  System.out.println("doing");
                for (int y = 0; y < h && useful; y++) {
                    parent.computeRow(y, row);
                    ic.setPixels(0, y, w, 1, SyntheticImage.cm, row, 0, w);
                }
                ic.imageComplete(parent.isStatic() ? ImageConsumer.STATICIMAGEDONE
                        : ImageConsumer.SINGLEFRAMEDONE);
            } while (!parent.isStatic() && useful);
        }
    }

    private final static void doPrivileged(final Runnable doRun) {

        java.security.AccessController.doPrivileged(
                new java.security.PrivilegedAction() {
                    public Object run() {
                        doRun.run();
                        return null;
                    }
                }
        );


    }
}
