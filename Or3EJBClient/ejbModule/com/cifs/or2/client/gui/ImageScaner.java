package com.cifs.or2.client.gui;

class ImageScaner {
    public boolean scan(String fileName) {
        return scanPicture(getCString(fileName));
    }

    private byte[] getCString(String s) {
        return (s + '\0').getBytes();
    }

    static {
        //System.loadLibrary("ScanerSupport");
    }

    private native boolean scanPicture(byte[] file);
}