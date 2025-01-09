package kz.tamur.comps.ui.ext.utils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class DragUtils {
    /**
     * Retrieves Images list from Transferable
     */

    public static Image getImportedImage(Transferable t) {
        if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                Object data = t.getTransferData(DataFlavor.imageFlavor);
                if (data instanceof Image) {
                    return (Image) data;
                }
            } catch (UnsupportedFlavorException e) {
                //
            } catch (IOException e) {
                //
            }
        }
        return null;
    }

    /**
     * Retrieves Files list from Transferable
     */

    public static List<File> getImportedFiles(Transferable t) {
        // From URL
        try {
            if (DragUtils.hasURIListFlavor(t.getTransferDataFlavors())) {
                // File link
                String url = (String) t.getTransferData(DragUtils.getUriListDataFlavor());
                final File file = new File(new URL(url).getPath());

                // Returning file
                return Arrays.asList(file);
            }
        } catch (Throwable e) {
            //
        }

        // From files list (Linux/MacOS)
        try {
            if (DragUtils.hasURIListFlavor(t.getTransferDataFlavors())) {
                // Parsing incoming files
                return DragUtils.textURIListToFileList((String) t.getTransferData(DragUtils.getUriListDataFlavor()));
            }
        } catch (Throwable e) {
            //
        }

        // From files list (Windows)
        try {
            // Getting files list
            return (AbstractList) t.getTransferData(DataFlavor.javaFileListFlavor);
        } catch (Throwable e) {
            //
        }

        return null;
    }

    /**
     * Retrieves URI list from data
     */

    public static List<File> textURIListToFileList(String data) {
        List<File> list = new ArrayList<File>(1);
        for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
            String s = st.nextToken();
            if (s.startsWith("#")) {
                // the line is a comment (as per the RFC 2483)
                continue;
            }
            try {
                URI uri = new URI(s);
                File file = new File(uri);
                list.add(file);
            } catch (Throwable e) {
                //
            }
        }
        return list;
    }

    /**
     * Checks if there is files URI list DataFlavor
     */

    public static boolean hasURIListFlavor(DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (getUriListDataFlavor().equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Files URI list flavor
     */

    private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";
    private static DataFlavor uriListFlavor = null;

    public static DataFlavor getUriListDataFlavor() {
        if (uriListFlavor == null) {
            try {
                return uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
            } catch (Throwable e) {
                return null;
            }
        } else {
            return uriListFlavor;
        }
    }
}
