package kz.tamur.comps.ui.ext.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebUtils {
    /**
     * Normalizes link address
     */

    public static String normalizeUrl(String url) {
        try {
            return new URI(url).normalize().toASCIIString();
        } catch (URISyntaxException e) {
            return url;
        }
    }

    /**
     * Returns URL query parameters
     */

    public static Map<String, List<String>> getUrlParameters(String url) {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        String[] urlParts = url.split("\\?");
        if (urlParts.length > 1) {
            String query = urlParts[1];
            for (String param : query.split("&")) {
                String pair[] = param.split("=");
                String key = decodeUrl(pair[0]);
                String value = "";
                if (pair.length > 1) {
                    value = decodeUrl(pair[1]);
                }
                List<String> values = params.get(key);
                if (values == null) {
                    values = new ArrayList<String>();
                    params.put(key, values);
                }
                values.add(value);
            }
        }
        return params;
    }

    /**
     * Encodes URL
     */

    public static String encodeUrl(String url) {
        try {
            URL u = new URL(url);
            URI uri = new URI(u.getProtocol(), u.getHost(), u.getPath(), u.getQuery(), null);
            return uri.toASCIIString();
        } catch (Throwable e) {
            return url;
        }
    }

    /**
     * Decodes URL
     */

    public static String decodeUrl(String url) {
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    /**
     * Shares link to Twitter
     */

    public static void shareOnTwitter(final String address) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    browseSite("http://twitter.com/intent/tweet?text=" + address);
                } catch (Throwable ex) {
                    //
                }
            }
        }).start();
    }

    /**
     * Shares link to VKontakte
     */

    public static void shareOnVk(final String address) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    browseSite("http://vkontakte.ru/share.php?url=" + address);
                } catch (Throwable ex) {
                    //
                }
            }
        }).start();
    }

    /**
     * Shares link to Facebook
     */

    public static void shareOnFb(final String address) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    browseSite("http://www.facebook.com/sharer.php?u=" + address);
                } catch (Throwable ex) {
                    //
                }
            }
        }).start();
    }

    /**
     * Opens site in default browser
     */

    public static void browseSiteSafely(String address) {
        try {
            browseSite(address);
        } catch (Throwable e) {
            //
        }
    }

    public static void browseSite(String address) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(address));
    }

    /**
     * Opens file in system file browser
     */

    public static void openFileSafely(File file) {
        try {
            openFile(file);
        } catch (Throwable e) {
            //
        }
    }

    public static void openFile(File file) throws IOException {
        Desktop.getDesktop().open(file);
    }

    /**
     * Creates new email in default system mail application
     */

    public static void writeEmailSafely(String email) {
        try {
            writeEmail(email);
        } catch (Throwable e) {
            //
        }
    }

    public static void writeEmail(String email) throws URISyntaxException, IOException {
        writeEmail(email, null, null);
    }

    public static void writeEmailSafely(String email, String subject, String body) {
        try {
            writeEmail(email, subject, body);
        } catch (Throwable e) {
            //
        }
    }

    public static void writeEmail(String email, String subject, String body) throws URISyntaxException, IOException {
        URI uri;
        if (!email.startsWith("mailto:")) {
            email = "mailto:" + email;
        }
        if (subject != null && body != null) {
            subject = subject.replaceAll(" ", "%20");
            body = body.replaceAll(" ", "%20");
            uri = new URI(email + "?subject=" + subject + "&body=" + body);
        } else {
            uri = new URI(email);
        }
        Desktop.getDesktop().mail(uri);
    }
}
