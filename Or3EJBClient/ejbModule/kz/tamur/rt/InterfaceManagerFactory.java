package kz.tamur.rt;


public class InterfaceManagerFactory {
    private InterfaceManager mgr_;
    private static InterfaceManagerFactory inst_;

    public static InterfaceManagerFactory instance() {
        if (inst_ == null)
            inst_ = new InterfaceManagerFactory();
        return inst_;
    }

    public void register(InterfaceManager mgr) {
        mgr_ = mgr;
    }

    public InterfaceManager getManager() {
        return mgr_;
    }

    private InterfaceManagerFactory() {
    };
}